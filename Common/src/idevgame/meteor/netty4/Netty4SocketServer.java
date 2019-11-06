package idevgame.meteor.netty4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import idevgame.meteor.net.IHandlerNetty4;

import java.nio.ByteOrder;
import java.util.concurrent.TimeUnit;

public final class Netty4SocketServer {

	private static final Logger logger = LoggerFactory
			.getLogger(Netty4SocketServer.class);

	private final IHandlerNetty4 handler;
	private final int port;
	private final int timeout;

	public Netty4SocketServer(IHandlerNetty4 handler, int port, int timeout) {
		this.handler = handler;
		this.port = port;
		this.timeout = timeout;
	}
	
	public void start() throws InterruptedException{
		String OSName = System.getProperty("os.name").toLowerCase();
		String osVersion = System.getProperty("os.version"); 
		System.out.println("OSName=" + OSName + ",osVersion=" + osVersion);
		if(OSName.indexOf("linux") != -1){
			startEpoll();
		}else{
			startNormal();
		}
	}

	private void startNormal() throws InterruptedException {
		logger.info("Netty4SocketServer start---Normal, port = " + port);
		
		final NioEventLoopGroup bossGroup = new NioEventLoopGroup();
		final NioEventLoopGroup workerGroup = new NioEventLoopGroup();

		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(bossGroup, workerGroup);
		bootstrap.channel(NioServerSocketChannel.class);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		bootstrap.option(ChannelOption.SO_BACKLOG, 128);
		
		bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
		bootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
		
		bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ChannelPipeline cp = ch.pipeline();
				
				cp.addLast(new Netty4Decoder());
				cp.addLast(new LengthFieldPrepender(ByteOrder.BIG_ENDIAN,4,0,true));
				cp.addLast(new Netty4Encoder());
				
				cp.addLast("handler", new Netty4SocketHandler(handler));
				
				if (timeout > 0) {
					cp.addLast("idleTimeoutHandler", new IdleStateHandler(0, 0, timeout, TimeUnit.SECONDS));
					cp.addLast("heartbeatHandler", new NettyHeartbeat());
				}
			}
		});
		
		logger.info("bootstrap=" +bootstrap.toString());
		
		ChannelFuture f = bootstrap.bind(port).sync();
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				bossGroup.shutdownGracefully();
				workerGroup.shutdownGracefully();
			}
		});
		
		logger.info("Netty4SocketServer ok,bind at :" + port);
	}
	
	private void startEpoll() throws InterruptedException {
		logger.info("Netty4SocketServer start---Epoll, port = " + port);
		final EventLoopGroup bossGroup = new EpollEventLoopGroup();
		final EventLoopGroup workerGroup = new EpollEventLoopGroup();
		ServerBootstrap bootstrap = new ServerBootstrap();

		bootstrap.group(bossGroup, workerGroup)
				.channel(EpollServerSocketChannel.class)
				.option(ChannelOption.SO_REUSEADDR, true)
				.option(EpollChannelOption.SO_REUSEPORT, true)
				.childOption(ChannelOption.SO_KEEPALIVE, true);

		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		bootstrap.option(ChannelOption.SO_BACKLOG, 128);
		
		bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
		bootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

		final Netty4SocketHandler h = new Netty4SocketHandler(handler);
		bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ChannelPipeline cp = ch.pipeline();

				cp.addLast(new Netty4Decoder());
				cp.addLast(new LengthFieldPrepender(4));
				cp.addLast(new Netty4Encoder());

				cp.addLast("handler",h);

				if (timeout > 0) {
					cp.addLast("idleTimeoutHandler", new IdleStateHandler(0, 0,timeout, TimeUnit.SECONDS));
					cp.addLast("heartbeatHandler", new NettyHeartbeat());
				}
			}
		});
		
		logger.info("bootstrap=" +bootstrap.toString());

		int workerThreads = Math.max(1, Runtime.getRuntime().availableProcessors());
		for (int i = 0; i < workerThreads; ++i) {
			System.out.println("Epoll bind:" + (i + 1) + "/" + workerThreads);
			ChannelFuture future = bootstrap.bind(port).await();
			if (!future.isSuccess()) {
				logger.error(String.format("bind on port = %d.", port),future.cause());
			}else{
				logger.info("成功绑定: bind on port = " + port);
			}
		}
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				workerGroup.shutdownGracefully();
				bossGroup.shutdownGracefully();
			}
		});
		
		logger.info("Netty4SocketServer ok,bind at :" + port);
	}

}
