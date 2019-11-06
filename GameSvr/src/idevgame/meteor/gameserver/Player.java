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
	//��ɫ���ڷ�����Ϣ
	Room rom;
	//udp���
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
	}
	
	void OnReceiveMsg(Pack pak)
	{
		try
		{
			//ָ֡��.
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
	int weapon1;//��������
	int weapon2;//��������
	int model;//ģ��
	int camp;//��Ӫ
}
