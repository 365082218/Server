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
	//��ͻ��˵Ľ���
	//ȡ�����з����б�.
	@CMD(id = MeteorMsg.MsgType.GetRoomReq_VALUE)
	public PackCodec.Pack OnGetRoomListReq(ChannelHandlerContext ctx) throws Exception {
		Player player = GameSvr.Instance.FindPlayer(ctx);
		if (player == null){
			//δ��֤�������ȡ�����б�.
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
	
	//��������
	@CMD(id = MeteorMsg.MsgType.CreateRoomReq_VALUE)
	public PackCodec.Pack OnCreateRoomReq(ChannelHandlerContext ctx, CreateRoomReq req) throws Exception {
		Player player = GameSvr.Instance.FindPlayer(ctx);
		PackCodec.Pack pack = null;
		Room room = null;
		if (player != null){
			player.OnPlayerAlive();
			CreateRoomRsp.Builder build = CreateRoomRsp.newBuilder();
			//������ڷ�����
			if (player.room != null){
				build.setResult(EResult.PlayerInRoom);
				build.setLevelId(0);
				build.setRoomId(0);
				build.setPort(0);
				build.setPlayerId(0);
			} else {
				//�������̫�࣬�������޷��е�
				if (GameSvr.Instance.Rooms.size() >= GameSvr.MaxRooms){
					build.setResult(EResult.RoomMaxed);
					build.setLevelId(0);
					build.setRoomId(0);
					build.setPort(0);
					build.setPlayerId(0);
				}
				else{//������������
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
	
	//���뷿��
	@CMD(id = MeteorMsg.MsgType.JoinRoomReq_VALUE)
	public PackCodec.Pack OnJoinRoomReq(ChannelHandlerContext ctx, JoinRoomReq req) throws Exception {
		PackCodec.Pack pack = null;//new PackCodec.Pack(ProtocolCode.LOGIN_SERVER_LOGIN, builder.build().toByteArray());
		Pack pak = null;
		Player p = GameSvr.Instance.FindPlayer(ctx);
		//1.��δ������������֤
		if (p == null){
			ProtocolVerifyRsp.Builder build = ProtocolVerifyRsp.newBuilder();
			build.setResult(0);
			build.setPlayer(0);
			build.setMessage("�ͻ��˻�δ��֤");
			pak = new Pack(MeteorMsg.MsgType.ProtocolVerify_VALUE, build.build().toByteArray());
			return pak;
		}
		
		p.OnPlayerAlive();
		//2.�Ѿ�����������
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
		
		//3.���䲻����
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

		//4.������������
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
		
		//5.���벻ƥ��
		if (!r.password.equals("") && r.ownerIdx != p.playerIdx)//������������.
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
		
		//6.���ǰ汾��ƥ��
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
		
		//7.����ֹ����˷���
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
		
		//8.ȱ�ٽ�ɫģ��
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

		//9.�ɹ����뷿��
		p.NickName = req.getNick();//�ǳ��ǽ��뷿��ʱ���õ�
		r.OnPlayerEnter(p);//��������ҹ㲥��ĳ�˽��뷿�����Ϣ
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
//		System.out.println("�ͻ����������������֤:" + req.getVersion());
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
			build.setMessage("�ͻ����������Э��汾��ƥ��");
			pak = new Pack(MeteorMsg.MsgType.ProtocolVerify_VALUE, build.build().toByteArray());
			ctx.channel().writeAndFlush(pak);
			ctx.close();
//			System.out.println("�ͻ����������Э��汾��ƥ��:" + req.getVersion());
			return null;
		}
		return pak;
	}
	
	//�ͻ���������볡��,�ŵ�ָ���б����һ֡�����������.
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
	
	//�ͻ���������Ӧ��
	@CMD(id = MeteorMsg.MsgType.AliveUpdate_VALUE)
	public PackCodec.Pack OnHeartBeat(ChannelHandlerContext ctx) throws Exception {
		GameSvr.Instance.OnPlayerHeartBeat(ctx);
		return null;
	}
	//������ϢTcp
	@CMD(id = MeteorMsg.MsgType.ChatInRoomReq_VALUE)
	public PackCodec.Pack OnPlayerChat(ChannelHandlerContext ctx, ChatMsg req) throws Exception {
		Player p = GameSvr.Instance.FindPlayer(ctx);
		Pack pack = new Pack(MeteorMsg.MsgType.ChatInRoomRsp_VALUE, req.toByteArray());
		if (p != null && p.room != null){
			p.room.BoardCastMsgEx(pack, null);
		}
		return null;
	}
	
	//����Tcp��-����ǽ��ã�����TCP�����Ӱ���������������ܻᵼ������
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
