package idevgame.meteor.gameserver;

import java.lang.reflect.InvocationTargetException;
import java.nio.channels.ClosedChannelException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import idevgame.meteor.dispatcher.Dispatcher;
import idevgame.meteor.net.IHandlerNetty4;
import idevgame.meteor.net.PackCodec.Pack;
import io.netty.channel.ChannelHandlerContext;

public class NetHandler implements IHandlerNetty4 {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private Dispatcher dispatcher;
	public NetHandler(Dispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	@Override
	public void onConnect(ChannelHandlerContext ctx) {
		System.out.println("game svr connecte gamecenter success");
	}

	@Override
	public void onReceive(ChannelHandlerContext ctx, Pack req) {
		Pack rsp = null;
		try {
			rsp = dispatcher.invoke(ctx, req.cmd, req.data);
		} 
		catch (InvocationTargetException e) {
			logger.error("协议处理出现异常", e);
		}
		catch (Exception e2) {
			logger.error("协议处理出现异常", e2);
		}
		
		if (rsp != null) {
			ctx.channel().writeAndFlush(rsp);
		}
	}

	@Override
	public void onClose(ChannelHandlerContext ctx) {
		System.out.println("game svr disconnect gamecenter");
	}

	@Override
	public void onException(ChannelHandlerContext ctx, Throwable e) {
		if (!(e instanceof ClosedChannelException)) {
			e.printStackTrace();
		}
	}
}
