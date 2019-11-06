package idevgame.meteor.gameserver.handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import idevgame.meteor.dispatcher.CMD;
import idevgame.meteor.net.PackCodec;
import idevgame.meteor.proto.MeteorMsgs.*;
import io.netty.channel.ChannelHandlerContext;

public class GameSvrMsgsHandler  {
	private Logger logger = LoggerFactory.getLogger(getClass());
	//��ͻ��˵Ľ���
	//ȡ�����з����б�.
	@CMD(id = MeteorMsg.MsgType.GetRoomReq_VALUE)
	public PackCodec.Pack OnGetRoomListReq(ChannelHandlerContext ctx) throws Exception {
		PackCodec.Pack pack = null;//new PackCodec.Pack(ProtocolCode.LOGIN_SERVER_LOGIN, builder.build().toByteArray());
		return pack;
	}
	
	//��������
	@CMD(id = MeteorMsg.MsgType.CreateRoomReq_VALUE)
	public PackCodec.Pack OnGetRoomListReq(ChannelHandlerContext ctx, CreateRoomReq req) throws Exception {
		PackCodec.Pack pack = null;//new PackCodec.Pack(ProtocolCode.LOGIN_SERVER_LOGIN, builder.build().toByteArray());
		return pack;
	}
	
	//���뷿��
	@CMD(id = MeteorMsg.MsgType.JoinRoomReq_VALUE)
	public PackCodec.Pack OnGetReq(ChannelHandlerContext ctx, JoinRoomReq req) throws Exception {
		PackCodec.Pack pack = null;//new PackCodec.Pack(ProtocolCode.LOGIN_SERVER_LOGIN, builder.build().toByteArray());
		return pack;
	}
	
	//�����ķ��Ľ���.
	//ע�᷵�ؽ��.
	@CMD(id = MeteorMsg.MsgType.RegisterSvrRsp_VALUE)
	public PackCodec.Pack OnRegisterServerRsp(ChannelHandlerContext ctx, RegisterSvrRsp rsp) throws Exception {
		return null;
	}
}
