package idevgame.meteor.gamecenter.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import idevgame.meteor.dispatcher.CMD;
import idevgame.meteor.gamecenter.GameCenter;
import idevgame.meteor.gamecenter.GameSvrInfo;
import idevgame.meteor.net.PackCodec;
import idevgame.meteor.net.PackCodec.Pack;
import idevgame.meteor.proto.MeteorMsgs.*;
import io.netty.channel.ChannelHandlerContext;

public class CenterMsgsHandler  {
	private Logger logger = LoggerFactory.getLogger(getClass());

	//游戏服务关闭.
//	@CMD(id = MeteorMsg.MsgType.UnRegisterSvr_VALUE)
//	public PackCodec.Pack UnRegisterServer(ChannelHandlerContext ctx) throws Exception {
//		//关掉链接，会自动清理掉服务器
//		ctx.disconnect();
//		return null;
//	}
//	
//	//游戏服注册到中心服
//	@CMD(id = MeteorMsg.MsgType.RegisterSvrReq_VALUE)
//	public PackCodec.Pack RegisterServer(ChannelHandlerContext ctx, RegisterSvrReq req) throws Exception {
//		System.out.println("RegisterSvrReq message");
//		String server = req.getServerName();
//		//处理重名
//		while (true)
//		{
//			boolean hasRepeat = false;
//			for (int i = 0; i < GameCenter.server.size(); i++)
//			{
//				GameSvrInfo s = GameCenter.server.get(i);
//				if (s.name == server)
//				{
//					server += "_";
//					hasRepeat = true;
//					break;
//				}
//			}
//			if (!hasRepeat)
//				break;
//		}
//		
//		GameSvrInfo insert = new GameSvrInfo(server, ctx, req.getIp(), req.getPort());
//		synchronized(GameCenter.server)
//		{
//			GameCenter.server.add(insert);
//		}
//		RegisterSvrRsp.Builder builder = RegisterSvrRsp.newBuilder();
//		builder.setResult(1);
//		builder.setReason(0);
//		builder.setServerId(insert.index);
//		builder.setServerName(server);
//		return new PackCodec.Pack(MeteorMsg.MsgType.RegisterSvrRsp_VALUE, builder.build().toByteArray());
//	}
//	
//	Pack pakSvrList;
//	//获取所有游戏服信息
//	@CMD(id = MeteorMsg.MsgType.GetSvrReq_VALUE)
//	public PackCodec.Pack GetSvrList(ChannelHandlerContext ctx) throws Exception {
//		if (pakSvrList != null)
//			return pakSvrList;
//		synchronized (GameCenter.Instance.server)
//		{
//			pakSvrList = UpdateSvrList();
//			return pakSvrList;
//		}
//	}
//	
//	//游戏服务刷新状态-房间和负载.
//	@CMD(id = MeteorMsg.MsgType.SyncSvrState_VALUE)
//	public PackCodec.Pack RefreshSvrState(ChannelHandlerContext ctx, SyncSvrState state) throws Exception {
//		//同时更新所有服务器信息列表
//		synchronized (GameCenter.Instance.server)
//		{
//			GameSvrInfo svr = GameCenter.Instance.FindGameSvr(ctx);
//			if (svr != null)
//			{
//				svr.RoomInLobby.clear();
//				for (int i = 0; i < state.getRoomInLobbyCount(); i++)
//				{
//					RoomInfo r = state.getRoomInLobby(i);
//					svr.RoomInLobby.add(r);
//				}
//			}
//			pakSvrList = UpdateSvrList();
//		}
//		return null;
//	}
//	
//	Pack UpdateSvrList()
//	{
//		synchronized (GameCenter.Instance.server)
//		{
//			if (GameCenter.Instance.server.size() == 0)
//				return null;
//			SvrList.Builder builder = SvrList.newBuilder();
//			for (int i = 0; i < GameCenter.Instance.server.size(); i++)
//			{
//				GameSvrInfo svr = GameCenter.Instance.server.get(i); 
//				SvrInfo.Builder svrBuilder = builder.addGameSvrListBuilder();
//				
//				svrBuilder.setIdx(svr.index);
//				svrBuilder.setIp(svr.ip);
//				svrBuilder.setName(svr.name);
//				svrBuilder.setPort(svr.port);
//				
//				for (int j = 0; j < svr.RoomInLobby.size(); j++)
//				{
//					RoomInfo r = svr.RoomInLobby.get(j);
//					svrBuilder.addRoomInLobby(j, r);
//				}
//			}
//			return new PackCodec.Pack(MeteorMsg.MsgType.GetSvrRsp_VALUE, builder.build().toByteArray());
//		}
//	}
}
