package idevgame.meteor.net;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;

public interface IHandlerNetty4Udp {
	
	void onReceive(ChannelHandlerContext ctx,DatagramPacket msg,byte[] bytes);
	
}
