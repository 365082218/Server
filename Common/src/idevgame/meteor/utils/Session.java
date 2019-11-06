package idevgame.meteor.utils;

import io.netty.channel.ChannelHandlerContext;

public class Session {
	private static int Unique = 0;
	public int SessionId;
	public ChannelHandlerContext context;
	public Session(ChannelHandlerContext ctx)
	{
		context = ctx;
		SessionId = Unique++;
	}
	
}
