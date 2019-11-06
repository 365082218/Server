package idevgame.meteor.gamecenter;

import java.util.ArrayList;
import java.util.List;

import idevgame.meteor.proto.MeteorMsgs.RoomInfo;
import io.netty.channel.ChannelHandlerContext;

public class GameSvrInfo
{
	public static int Unique = 0;
	public GameSvrInfo(String n, ChannelHandlerContext ctx, String i, int p)
	{
		name = n;
		context = ctx;
		ip = i;
		port = p;
		index = Unique++;
	}
	ChannelHandlerContext context;
	public String name;
	//服务器相关信息.
	public String ip;
	public int port;
	public int index;
	public List<RoomInfo> RoomInLobby = new ArrayList<RoomInfo>();
}