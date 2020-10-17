package idevgame.meteor.gameserver.handler;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import idevgame.meteor.dispatcher.CMD;
import idevgame.meteor.gameserver.EResult;
import idevgame.meteor.gameserver.GameSvr;
import idevgame.meteor.gameserver.Player;
import idevgame.meteor.gameserver.Room;
import idevgame.meteor.gameserver.RoomState;
import idevgame.meteor.gameserver.Skick;
import idevgame.meteor.net.PackCodec;
import idevgame.meteor.net.PackCodec.Pack;
import idevgame.meteor.proto.MeteorMsgs.*;
import io.netty.channel.ChannelHandlerContext;
import idevgame.meteor.proto.MeteorMsgs.AudioChatMsg;
import idevgame.meteor.proto.MeteorMsgs.RoomInfo;
import idevgame.meteor.proto.MeteorMsgs.RoomInfo.MeteorVersion;
import idevgame.meteor.proto.MeteorMsgs.RoomInfo.RoomPattern;
import idevgame.meteor.proto.MeteorMsgs.RoomInfo.RoomRule;
public class GameSvrMsgsHandler  {
	private Logger logger = LoggerFactory.getLogger(getClass());
	//与客户端的交互
	//取得所有房间列表.
	@CMD(id = MeteorMsg.MsgType.GetRoomReq_VALUE)
	public PackCodec.Pack OnGetRoomListReq(ChannelHandlerContext ctx) throws Exception {
		Player player = GameSvr.Instance.FindPlayer(ctx);
		if (player == null){
			//未验证的玩家拉取房间列表.
			player.context.close();
			return null;
		}
		GetRoomRsp.Builder builder = GetRoomRsp.newBuilder();
		synchronized(GameSvr.Instance.Rooms)
		{
			RoomInfo.Builder roomBuild = RoomInfo.newBuilder();
			for (int i = 0; i < GameSvr.Instance.Rooms.size(); i++)
			{
				Room r = GameSvr.Instance.Rooms.get(i);
				if (r.state == RoomState.Closing)
					continue;
				roomBuild.setGroup1(r.group1);
				roomBuild.setGroup2(r.group2);
				roomBuild.setHpMax(r.maxHp);
				roomBuild.setLevelIdx(r.mapIdx);
				roomBuild.setMaxPlayer(r.maxPlayer);
				roomBuild.setPassword(r.password != "" ? 1 : 0);
				roomBuild.setPattern(r.pattern);
				roomBuild.setPlayerCount(r.playerNum);
				roomBuild.setRoomId(r.index);
				roomBuild.setRoomName(r.name);
				roomBuild.setRule(r.rule);
				roomBuild.setVersion(r.version);
				roomBuild.addAllModels(r.models);
				roomBuild.setOwner(r.ownerIdx);
				RoomInfo room = roomBuild.build();
				builder.addRooms(room);
			}
		}
		GetRoomRsp rsp = builder.build();
		PackCodec.Pack pack = new PackCodec.Pack(MeteorMsg.MsgType.GetRoomRsp_VALUE, rsp.toByteArray());
		return pack;
	}
	
	//创建房间
	@CMD(id = MeteorMsg.MsgType.CreateRoomReq_VALUE)
	public PackCodec.Pack OnCreateRoomReq(ChannelHandlerContext ctx, CreateRoomReq req) throws Exception {
		Player player = GameSvr.Instance.FindPlayer(ctx);
		PackCodec.Pack pack = null;
		Room room = null;
		if (player != null){
			player.OnPlayerAlive();
			CreateRoomRsp.Builder build = CreateRoomRsp.newBuilder();
			//玩家正在房间内
			if (player.room != null){
				build.setResult(EResult.PlayerInRoom);
				build.setLevelId(0);
				build.setRoomId(0);
				build.setPort(0);
				build.setPlayerId(0);
			} else {
				//如果房间太多，服务器无法承担
				if (GameSvr.Instance.Rooms.size() >= GameSvr.MaxRooms){
					build.setResult(EResult.RoomMaxed);
					build.setLevelId(0);
					build.setRoomId(0);
					build.setPort(0);
					build.setPlayerId(0);
				}
				else{//正常创建房间
					room = GameSvr.Instance.CreateRoom(req.getRoomName(), req.getSecret(), req.getRule(), req.getLevelIdx(), player.playerIdx, req.getRoundTime(), req.getMaxPlayer(), req.getHpMax(), req.getVersion(), req.getPattern(), req.getModelsList());
					build.setResult(EResult.Succeed);
					build.setLevelId(req.getLevelIdx());
					build.setRoomId(room.index);
					build.setPort(GameSvr.KcpPort);
					build.setPlayerId(player.playerIdx);
				}
			}
			CreateRoomRsp rsp = build.build();
			pack = new PackCodec.Pack(MeteorMsg.MsgType.CreateRoomRsp_VALUE, rsp.toByteArray());
			return pack;
		}
		return null;
	}
	
	//进入房间
	@CMD(id = MeteorMsg.MsgType.JoinRoomReq_VALUE)
	public PackCodec.Pack OnJoinRoomReq(ChannelHandlerContext ctx, JoinRoomReq req) throws Exception {
		PackCodec.Pack pack = null;//new PackCodec.Pack(ProtocolCode.LOGIN_SERVER_LOGIN, builder.build().toByteArray());
		Pack pak = null;
		Player p = GameSvr.Instance.FindPlayer(ctx);
		//1.还未经过服务器验证
		if (p == null){
			ProtocolVerifyRsp.Builder build = ProtocolVerifyRsp.newBuilder();
			build.setResult(0);
			build.setPlayer(0);
			build.setMessage("客户端还未验证");
			pak = new Pack(MeteorMsg.MsgType.ProtocolVerify_VALUE, build.build().toByteArray());
			return pak;
		}
		
		p.OnPlayerAlive();
		//2.已经在其他房间
		if (p.room != null){
			JoinRoomRsp.Builder build = JoinRoomRsp.newBuilder();
			build.setResult(0);
			build.setReason(EResult.PlayerInRoom);
			build.setLevelIdx(0);
			build.setPort(0);
			build.setRoomId(req.getRoomId());
			pak = new Pack(MeteorMsg.MsgType.JoinRoomRsp_VALUE, build.build().toByteArray());
			return pak;
		}
		
		//3.房间不存在
		int id = req.getRoomId();
		Room r = GameSvr.Instance.FindRoom(id);
		if (r == null){
			JoinRoomRsp.Builder build = JoinRoomRsp.newBuilder();
			build.setResult(0);
			build.setReason(EResult.RoomInvalid);
			build.setLevelIdx(0);
			build.setPort(0);
			build.setRoomId(req.getRoomId());
			pak = new Pack(MeteorMsg.MsgType.JoinRoomRsp_VALUE, build.build().toByteArray());
			return pak;
		}

		//4.房间人数已满
		if (r.playerNum == r.maxPlayer){
			JoinRoomRsp.Builder build = JoinRoomRsp.newBuilder();
			build.setResult(0);
			build.setReason(EResult.PlayerMax);
			build.setLevelIdx(0);
			build.setPort(0);
			build.setRoomId(req.getRoomId());
			pak = new Pack(MeteorMsg.MsgType.JoinRoomRsp_VALUE, build.build().toByteArray());
			return pak;
		}
		
		//5.密码不匹配
		if (!r.password.equals("") && r.ownerIdx != p.playerIdx)//房主不用密码.
		{
			if (!r.password.equals(req.getPassword())){
				JoinRoomRsp.Builder build = JoinRoomRsp.newBuilder();
				build.setResult(0);
				build.setReason(EResult.PasswordError);
				build.setLevelIdx(0);
				build.setPort(0);
				build.setRoomId(req.getRoomId());
				pak = new Pack(MeteorMsg.MsgType.JoinRoomRsp_VALUE, build.build().toByteArray());
				return pak;
			}
		}
		
		//6.流星版本不匹配
		if (r.version != req.getVersion())
		{
			JoinRoomRsp.Builder build = JoinRoomRsp.newBuilder();
			build.setResult(0);
			build.setReason(EResult.VersionInvalid);
			build.setLevelIdx(0);
			build.setPort(0);
			build.setRoomId(req.getRoomId());
			pak = new Pack(MeteorMsg.MsgType.JoinRoomRsp_VALUE, build.build().toByteArray());
			return pak;
		}
		
		//7.被禁止进入此房间
		InetSocketAddress addr = (InetSocketAddress)p.context.channel().remoteAddress();
		String ip = addr.getAddress().getHostAddress();
		int port = addr.getPort();
		for (int i = 0; i < r.blackList.size(); i++){
			Skick b = r.blackList.get(i);
			if (b.ip == ip && b.port == port){
				JoinRoomRsp.Builder build = JoinRoomRsp.newBuilder();
				build.setResult(0);
				build.setReason(EResult.Skicked);
				build.setLevelIdx(0);
				build.setPort(0);
				build.setRoomId(req.getRoomId());
				pak = new Pack(MeteorMsg.MsgType.JoinRoomRsp_VALUE, build.build().toByteArray());
				return pak;
			}
		}
		
		//8.缺少角色模型
		List<Integer> models = req.getModelsList();
		for (int i = 0; i < r.models.size(); i++){
			int model = r.models.get(i);
			if (!models.contains(model)){
				JoinRoomRsp.Builder build = JoinRoomRsp.newBuilder();
				build.setResult(model);
				build.setReason(EResult.ModelMiss);
				build.setLevelIdx(0);
				build.setPort(0);
				build.setRoomId(req.getRoomId());
				pak = new Pack(MeteorMsg.MsgType.JoinRoomRsp_VALUE, build.build().toByteArray());
				return pak;
			}
		}

		//9.成功进入房间
		p.NickName = req.getNick();//昵称是进入房间时设置的
		r.OnPlayerEnter(p);//给其他玩家广播，某人进入房间的消息
		JoinRoomRsp.Builder build = JoinRoomRsp.newBuilder();
		build.setResult(1);
		build.setReason(EResult.Succeed);
		build.setLevelIdx(r.mapIdx);
		build.setPort(GameSvr.KcpPort);
		build.setRoomId(r.index);
		pak = new Pack(MeteorMsg.MsgType.JoinRoomRsp_VALUE, build.build().toByteArray());
		return pak;
	}
	
	
	@CMD(id = MeteorMsg.MsgType.ProtocolVerify_VALUE)
	public PackCodec.Pack OnProtocolVerify(ChannelHandlerContext ctx, ProtocolVerifyReq req) throws Exception {
		Pack pak = null;
//		System.out.println("客户端请求与服务器验证:" + req.getVersion());
		ProtocolVerifyRsp.Builder build = ProtocolVerifyRsp.newBuilder();
		if (req.getVersion() == GameSvr.ProtocolVersion)
		{
			Player p = GameSvr.Instance.OnPlayerVerifyed(ctx);
			build.setResult(1);
			build.setPlayer(p.playerIdx);
			build.setMessage("Verify Successful");
			pak = new Pack(MeteorMsg.MsgType.ProtocolVerify_VALUE, build.build().toByteArray());
		} else{
			build.setResult(0);
			build.setPlayer(0);
			build.setMessage("客户端与服务器协议版本不匹配");
			pak = new Pack(MeteorMsg.MsgType.ProtocolVerify_VALUE, build.build().toByteArray());
			ctx.channel().writeAndFlush(pak);
			ctx.close();
//			System.out.println("客户端与服务器协议版本不匹配:" + req.getVersion());
			return null;
		}
		return pak;
	}
	
	//客户端请求进入场景,放到指令列表里，下一帧发给所有玩家.
	@CMD(id = MeteorMsg.MsgType.EnterLevelReq_VALUE)
	public PackCodec.Pack RequestEnterLevel(ChannelHandlerContext ctx, PlayerEvent req) throws Exception {
		Player p = GameSvr.Instance.FindPlayer(ctx);
		if (p != null){
			p.OnPlayerAlive();
			if (p.room != null){
				p.weapon1 = req.getWeapon();
				p.weapon2 = 0;
				p.camp = req.getCamp();
				p.model = req.getModel();
				p.room.OnPlayerEnterLevel(p);
			}
		}
		return null;
	}
	
	//客户端心跳包应答
	@CMD(id = MeteorMsg.MsgType.AliveUpdate_VALUE)
	public PackCodec.Pack OnHeartBeat(ChannelHandlerContext ctx) throws Exception {
		GameSvr.Instance.OnPlayerHeartBeat(ctx);
		return null;
	}
	//聊天信息Tcp
	@CMD(id = MeteorMsg.MsgType.ChatInRoomReq_VALUE)
	public PackCodec.Pack OnPlayerChat(ChannelHandlerContext ctx, ChatMsg req) throws Exception {
		Player p = GameSvr.Instance.FindPlayer(ctx);
		Pack pack = new Pack(MeteorMsg.MsgType.ChatInRoomRsp_VALUE, req.toByteArray());
		if (p != null && p.room != null){
			p.room.BoardCastMsgEx(pack, null);
		}
		return null;
	}
	
	//语音Tcp包-最好是禁用，否则TCP传输会影响网卡流量，可能会导致问题
	@CMD(id = MeteorMsg.MsgType.AudioChat_VALUE)
	public PackCodec.Pack OnPlayerAudioChat(ChannelHandlerContext ctx, AudioChatMsg req) throws Exception {
		Player p = GameSvr.Instance.FindPlayer(ctx);
		Pack pack = new Pack(MeteorMsg.MsgType.AudioChat_VALUE, req.toByteArray());
		if (p != null && p.room != null){
			p.room.BoardCastMsgEx(pack, p);
		}
		return null;
	}
}
