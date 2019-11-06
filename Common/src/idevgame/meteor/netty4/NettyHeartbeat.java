package idevgame.meteor.netty4;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class NettyHeartbeat extends ChannelDuplexHandler {

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.ALL_IDLE) {
            	System.out.println("###ChannelHandlerContext,ip=" + Netty4Utils.getIp(ctx));
    			ctx.channel().close();
            } else {
            	System.out.println("###IdleStateEvent,e.getState()=" + e.state());
            }
        }
    }

}
