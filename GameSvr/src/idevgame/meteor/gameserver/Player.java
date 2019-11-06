package idevgame.meteor.gameserver;

import idevgame.meteor.net.PackCodec.Pack;
import idevgame.meteor.proto.MeteorMsgs.GameFrames;
import idevgame.meteor.proto.MeteorMsgs.MeteorMsg;
import idevgame.meteor.utils.Session;
import io.netty.buffer.ByteBuf;

public class Player {
	boolean dirty;
	String Id;
	String Name;
	int playerIdx;
	Session getSession()
	{
		return GameSvr.Instance.users.get(playerIdx);
	}
	//角色所在房间信息
	Room rom;
	//udp相关
	KcpS server;
	int kcpReceivedBytes;
//	byte kcpBuffer[DATA_BUFSIZE];
//	void KcpRelease();
//	void KcpUpdate();
//	void InitKcp(KcpServer kcps);
	public void onReceive(ByteBuf in)
	{
		int leftBytes = in.readableBytes();
		while (leftBytes >= 4)
		{
			int packetLength = in.readInt();
			System.out.println("packet length:" + packetLength);
			if (leftBytes >= packetLength)
			{
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
	}
	
	void OnReceiveMsg(Pack pak)
	{
		try
		{
			//帧指令.
			if (pak.cmd == MeteorMsg.MsgType.SyncCommand_VALUE)
			{
				System.out.println("MeteorMsg.MsgType.SyncCommand_VALUE");
				GameFrames f = GameFrames.parseFrom(pak.data);
				System.out.print(f);
			}
		}
		catch(Exception exp)
		{
			System.out.println("error");
		}
	}
//	void ExtractPacket();
//	void OnEnterLevel(byte buff[], int size);
	void SendPacket(Pack pak)
	{
		getSession().context.writeAndFlush(pak);
	}
	boolean dead;
	int rebornTick;
	int weapon1;//主手武器
	int weapon2;//副手武器
	int model;//模型
	int camp;//阵营
}
