package idevgame.meteor.gameserver;

import java.util.ArrayList;
import java.util.List;

import idevgame.meteor.gameserver.jkcp.KcpOnUdp;
import idevgame.meteor.net.PackCodec;
import idevgame.meteor.net.PackCodec.Pack;
import idevgame.meteor.netty4.Netty4Utils;
import idevgame.meteor.proto.MeteorMsgs.GameFrame;
import idevgame.meteor.proto.MeteorMsgs.GetRoomRsp;
import idevgame.meteor.proto.MeteorMsgs.MeteorMsg;
import idevgame.meteor.proto.MeteorMsgs.OperateMsg;
import idevgame.meteor.proto.MeteorMsgs._Quaternion;
import idevgame.meteor.proto.MeteorMsgs._Vector3;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
public class Player {
	private static int Unique = 0;
	public ChannelHandlerContext context;
	public Player(ChannelHandlerContext ctx){
		context = ctx;
		playerIdx = Unique++;
		if (Unique == Integer.MAX_VALUE)
			Unique = 0;
		aliveTick = System.currentTimeMillis();
		NickName = "";
		heartPak = new Pack(MeteorMsg.MsgType.AliveUpdate_VALUE, null);
		Reset();
	}
	
	public PlayerState state;
	public String NickName;
	KcpOnUdp kcpProxy;
	public int playerIdx;
	public Room room;
	public void onKcpClosed(KcpOnUdp kcpc, int errCode){
		kcpProxy = null;
		//KCP��֪��Ϊ�ζϿ�
		System.out.println("Player.onKcpClosed():" + this.NickName);
	}
	
	//����ѡ�˽���
	public void NewTurn(){
		weapon1 = 0;//��������
		weapon2 = 0;//��������
		model = 0;//ģ��
		camp = 0;//��Ӫ
		state = PlayerState.InLevel;
		buffs = new ArrayList<Integer>();
		hp = 100;
		ang = 0;
		_Vector3.Builder posbuild = _Vector3.newBuilder();
		posbuild.setX(0);
		posbuild.setY(0);
		posbuild.setZ(0);
		this.pos = posbuild.build();
		_Quaternion.Builder qb = _Quaternion.newBuilder();
		qb.setX(0);
		qb.setY(0);
		qb.setZ(0);
		qb.setW(0);
		this.rotate = qb.build();
		this.action = 0;
		synced = false;
	}
	
	public void Reset(){
		dirty = false;
		room = null;
		dead = false;
		synced = false;
		rebornTick = 0;
		weapon1 = 0;//��������
		weapon2 = 0;//��������
		model = 0;//ģ��
		camp = 0;//��Ӫ
		
		state = PlayerState.InLobby;
		buffs = new ArrayList<Integer>();
		hp = 100;
		ang = 0;
		_Vector3.Builder posbuild = _Vector3.newBuilder();
		posbuild.setX(0);
		posbuild.setY(0);
		posbuild.setZ(0);
		this.pos = posbuild.build();
		_Quaternion.Builder qb = _Quaternion.newBuilder();
		qb.setX(0);
		qb.setY(0);
		qb.setZ(0);
		qb.setW(0);
		this.rotate = qb.build();
		this.action = 0;
		if (kcpProxy != null){
			kcpProxy.close();
			kcpProxy = null;
		}
	}
	
	//�յ�KCP��Ϣ
	public void onReceive(KcpOnUdp kcpc, ByteBuf in)
	{
		kcpProxy = kcpc;
		int leftBytes = in.readableBytes();
		while (leftBytes >= 4)
		{
			int packetLength = in.getInt(0);
//			System.out.println("packet length:" + packetLength);
			if (leftBytes >= packetLength)
			{
				int length = in.readInt();
				int message = in.readInt();
				//�Ѿ���ȡ��8�ֽڣ�ʣ�����峤��
				int body = packetLength - 8;
				byte [] buff = new byte[body];
				if (body != 0)
					in.readBytes(buff);
				Pack p = new Pack(message, buff);
				OnReceiveMsg(p);
				leftBytes -= packetLength;
			}
			else
				break;
		}
		in.release();
	}
	
	void OnReceiveMsg(Pack pak)
	{
		try
		{
			if (pak.cmd == MeteorMsg.Command.ClientSync_VALUE)
			{
				//״̬ͬ��.
				GameFrame f = GameFrame.parseFrom(pak.data);
				//������ͬ������ɫ������
				if (room != null)
					room.OnReveiceInput(this, f);
			} else if (pak.cmd == MeteorMsg.Command.Drop_VALUE){
				if (room != null){
					room.KcpBoardCastMsgEx(pak, null);
				}
				//����ӳ�����
			} else if (pak.cmd == MeteorMsg.Command.Kill_VALUE){
				//����ɱ��
				if (room != null){
					if (room.ownerIdx == playerIdx){
						room.KcpBoardCastMsgEx(pak, null);
					}
				}
			} else if (pak.cmd == MeteorMsg.Command.AddNpc_VALUE){
				//��ӻ�����,�ݲ�֧��
			} else if (pak.cmd == MeteorMsg.Command.GetItem_VALUE){
				//ʰȡ��Ʒ
				if (room != null){
					room.KcpBoardCastMsgEx(pak, this);
				}
			} else if (pak.cmd == MeteorMsg.Command.Kick_VALUE){
				//��������
				if (room != null){
					if (room.ownerIdx == playerIdx){
						OperateMsg skick = OperateMsg.parseFrom(pak.data);
						int kicked = skick.getKillTarget();
						Player p = GameSvr.Instance.FindPlayer(kicked);
						if (p != null && p != this)
						{
							room.KcpBoardCastMsgEx(pak, null);
						}
					}
				}
			} else if (pak.cmd == MeteorMsg.Command.Skick_VALUE){
				//���������Ҹ��������ú�����
				if (room != null){
					if (room.ownerIdx == playerIdx){
						OperateMsg skick = OperateMsg.parseFrom(pak.data);
						int kicked = skick.getKillTarget();
						Player p = GameSvr.Instance.FindPlayer(kicked);
						if (p != null && p != this)
						{
							String ip = Netty4Utils.getIp(p.context);
							int port = Netty4Utils.getPort(p.context);
							Skick sk = new Skick(ip, port);
							room.blackList.add(sk);
							room.KcpBoardCastMsgEx(pak, null);
						}
					}
				}
			}
		}
		catch(Exception exp)
		{
			System.out.println("expection:" + exp.getMessage());
		}
	}
	
//	void ExtractPacket();
//	void OnEnterLevel(byte buff[], int size);
	
	public void TcpSendPacket(Pack pak)
	{
		synchronized(this)
		{
			context.channel().writeAndFlush(pak);
		}
	}
	
	public void KcpSend(Pack pak){
		synchronized(this)
		{
			if (this.kcpProxy != null)
			{
				ByteBuf b = PackCodec.encodeEx(pak.cmd, pak.data);
				this.kcpProxy.send(b);
			}
		}
	}
	
	long aliveTick = 0;
	Pack heartPak = null;
	public void HeartBeat(){
		//���10S�����յ��ͻ�����Ϣ����Ҫ������
		if ((aliveTick + 10000) > System.currentTimeMillis())
			return;
		synchronized(this)
		{
			context.channel().writeAndFlush(heartPak);
		}
	}
	
	public boolean Alive(long mills){
		return (aliveTick + 120000) > mills;
	}
	
	//�յ�����TCP��Ϣ,������TCP����
	public void OnPlayerAlive(){
		aliveTick = System.currentTimeMillis();
	}
	
	boolean dead;
	int rebornTick;
	public int weapon1;//��������
	public int weapon2;//��������
	public int model;//ģ��
	public int camp;//��Ӫ 0-���� 1����
	public int hp;//��Ѫ
	public int ang;//ŭ��
	public int spawn;//������
	public _Vector3 pos;
	public _Quaternion rotate;
	public int action;
	public ArrayList<Integer> buffs;
	public boolean dirty;//�Ƿ񱻿ͻ����ϴ������ݸ���
	public boolean synced;//�Ƿ�ͬ������������.
}
