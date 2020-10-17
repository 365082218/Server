package idevgame.meteor.netty4;

import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;

/**
 * 
 *
 * @author moon
 * 2015å¹?11æœ?10æ—?
 */
public class Netty4Utils {

	public static String getIp(ChannelHandlerContext ctx){

		InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();

		return address.getAddress().getHostAddress();
	}
	
	public static int getPort(ChannelHandlerContext ctx){
		InetSocketAddress addr = (InetSocketAddress)ctx.channel().remoteAddress();
		int port = addr.getPort();
		return port;
	}
}
