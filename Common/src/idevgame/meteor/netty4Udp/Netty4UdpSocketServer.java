package idevgame.meteor.netty4Udp;

import idevgame.meteor.net.IHandlerNetty4Udp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

/**
 * netty4启动
 *
 * @author moon
 * 2015�?11�?12�?
 */
public final class Netty4UdpSocketServer {

	private static final Logger logger = LoggerFactory.getLogger(Netty4UdpSocketServer.class);

	private final IHandlerNetty4Udp handler;
	private final int port;
	private String[] ips = null;

	public Netty4UdpSocketServer(IHandlerNetty4Udp handler, String[] _ips,int port) {
		this.handler = handler;
		this.ips = _ips;
		this.port = port;
		
//		if(this.ips == null){
//			this.ips = IpUtil.getAllLocalHostIPv4();
//		}
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
		logger.info("Netty4UdpSocketServer startUdp---Normal, port = " + port);
		
		int RCVBUF = 900*1024;
		int SNDBUF = 900*1024;

		final NioEventLoopGroup group = new NioEventLoopGroup();
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(group);
		bootstrap.channel(NioDatagramChannel.class);
//		bootstrap.option(ChannelOption.SO_BROADCAST, true);
		bootstrap.option(ChannelOption.SO_RCVBUF,RCVBUF);
		bootstrap.option(ChannelOption.SO_SNDBUF,SNDBUF);
		
//		bootstrap.option(ChannelOption.RCVBUF_ALLOCATOR,new FixedRecvByteBufAllocator(512));//是否�?要设置接收buff上限(数据包最大长�?),默认�?2048
		bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);//是否使用内存�?
		
		bootstrap.handler(new Netty4UdpSocketHandler(handler));

		
		logger.info("bootstrap=" +bootstrap.toString());
		

		for (String ip : ips) {
			logger.info("Upd绑定ip=" + ip + ",port=" + port);
			ChannelFuture f = bootstrap.bind(ip,port).sync();
		}
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				group.shutdownGracefully();
			}
		});

		logger.info("Netty4UdpSocketServer ok,bind at :" + port + ",RCVBUF=" + RCVBUF);
	}
	

	private void startEpoll() throws InterruptedException {
		logger.info("Netty4UdpSocketServer startUdp---Epoll, port = " + port);
		

		int RCVBUF = 900*1024;
		int SNDBUF = 900*1024;
		
		Bootstrap bootstrap = new Bootstrap();
		final EventLoopGroup group = new EpollEventLoopGroup();
		bootstrap.group(group)
				.channel(EpollDatagramChannel.class)
				.option(EpollChannelOption.SO_REUSEPORT, true)
				.option(ChannelOption.SO_RCVBUF,RCVBUF)
				.option(ChannelOption.SO_SNDBUF,SNDBUF);
		
		bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
		
		final Netty4UdpSocketHandler h = new Netty4UdpSocketHandler(handler);
		bootstrap.handler(h);
		
		logger.info("bootstrap=" +bootstrap.toString());
		int workerThreads = Math.max(1, Runtime.getRuntime().availableProcessors()/2);
		for (String ip : ips) {
			
			for (int i = 0; i < workerThreads; ++i) {
				logger.info("      Epoll bind:" + (i + 1) + "/" + workerThreads);
				ChannelFuture future = bootstrap.bind(ip,port).await();
				if (!future.isSuccess()) {
					logger.error(String.format("绑定失败: bind on port = %d.", port),future.cause());
				}else{
//					logger.info("成功绑定: bind on port = " + port);
				}
			}
		}

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				group.shutdownGracefully();
			}
		});
		
		logger.info("Netty4UdpSocketServer ok,bind at :" + port + ",RCVBUF=" + RCVBUF);
	}

}
