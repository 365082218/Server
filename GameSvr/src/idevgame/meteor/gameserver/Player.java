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
		//KCP不知道为何断开
		System.out.println("Player.onKcpClosed():" + this.NickName);
	}
	
	//进入选人界面
	public void NewTurn(){
		weapon1 = 0;//主手武器
		weapon2 = 0;//副手武器
		model = 0;//模型
		camp = 0;//阵营
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
		weapon1 = 0;//主手武器
		weapon2 = 0;//副手武器
		model = 0;//模型
		camp = 0;//阵营
		
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
	
	//收到KCP消息
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
				//已经读取了8字节，剩余身体长度
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
				//状态同步.
				GameFrame f = GameFrame.parseFrom(pak.data);
				//把数据同步到角色属性上
				if (room != null)
					room.OnReveiceInput(this, f);
			} else if (pak.cmd == MeteorMsg.Command.Drop_VALUE){
				if (room != null){
					room.KcpBoardCastMsgEx(pak, null);
				}
				//玩家扔出道具
			} else if (pak.cmd == MeteorMsg.Command.Kill_VALUE){
				//房主杀死
				if (room != null){
					if (room.ownerIdx == playerIdx){
						room.KcpBoardCastMsgEx(pak, null);
					}
				}
			} else if (pak.cmd == MeteorMsg.Command.AddNpc_VALUE){
				//添加机器人,暂不支持
			} else if (pak.cmd == MeteorMsg.Command.GetItem_VALUE){
				//拾取物品
				if (room != null){
					room.KcpBoardCastMsgEx(pak, this);
				}
			} else if (pak.cmd == MeteorMsg.Command.Kick_VALUE){
				//房主踢人
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
				//房主踢人且给房间设置黑名单
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
		//如果10S内有收到客户端消息，不要发心跳
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
	
	//收到任意TCP消息,都保活TCP链接
	public void OnPlayerAlive(){
		aliveTick = System.currentTimeMillis();
	}
	
	boolean dead;
	int rebornTick;
	public int weapon1;//主手武器
	public int weapon2;//副手武器
	public int model;//模型
	public int camp;//阵营 0-流星 1蝴蝶
	public int hp;//气血
	public int ang;//怒气
	public int spawn;//出生点
	public _Vector3 pos;
	public _Quaternion rotate;
	public int action;
	public ArrayList<Integer> buffs;
	public boolean dirty;//是否被客户端上传的数据覆盖
	public boolean synced;//是否同步过初次数据.
}
