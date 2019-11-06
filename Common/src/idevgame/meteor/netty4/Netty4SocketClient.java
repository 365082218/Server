package idevgame.meteor.netty4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import idevgame.meteor.net.IHandlerNetty4;
import idevgame.meteor.net.PackCodec.Pack;

import java.nio.ByteOrder;
import java.util.concurrent.TimeUnit;

public final class Netty4SocketClient {

	private static final Logger logger = LoggerFactory
			.getLogger(Netty4SocketClient.class);

	private final IHandlerNetty4 handler;
	private final int port;
	private Channel client;
	private String name;
	public Netty4SocketClient(IHandlerNetty4 handler, int port, String n) {
		this.handler = handler;
		this.port = port;
		this.name = n;
	}
	
	public void connect() throws InterruptedException{
		if (client != null)
		{
			System.out.println("connect already");
			return;
		}
		logger.info("Netty4SocketClient start---Normal, port = " + port);
		final EventLoopGroup workerGroup = new NioEventLoopGroup();
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(workerGroup);
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ChannelPipeline cp = ch.pipeline();
				cp.addLast(new Netty4Decoder());
				cp.addLast(new LengthFieldPrepender(ByteOrder.BIG_ENDIAN,4,0,true));
				cp.addLast(new Netty4Encoder());
				cp.addLast("handler", new Netty4SocketHandler(handler));
				cp.addLast("idleTimeoutHandler", new IdleStateHandler(0, 0, 60, TimeUnit.SECONDS));
				cp.addLast("heartbeatHandler", new NettyHeartbeat());
			}
		});
		
		ChannelFuture f = bootstrap.connect("127.0.0.1", port);
		client = f.channel();
	}

	public void Write(Pack pak)
	{
		client.writeAndFlush(pak);
	}
}
