package idevgame.meteor.gameserver;

import java.util.List;

import idevgame.meteor.net.PackCodec.Pack;
import idevgame.meteor.proto.MeteorMsgs.MeteorMsg;
enum RoomState
{
	Run,//����������
	Wait,//�ִν���׼����һ���ִα����У�30S��������ҽ�����ر�
	WaitClose,//�ȴ��رգ�30S
}

public class Room {
	static final int syncDelta = 62;//62����ͬ��һ�Σ�1��Լͬ��16��
	//������ţ����ƣ����룬���򣬵�ͼID
	public Room(int idx, String n, String p, int r, int m, int owner, int t, int maxP, int maxH, int v, int mo)
	{
		index = idx;
		name = n;
		password = p;
		rule = r;
		version = v;
		group1 = 0;
		group2 = 0;
		maxPlayer = maxP;
		maxHp = maxH;
		count = 0;
		turnTime = t;
		mapIdx = m;
		ownerIdx = owner;
		gamestart = false;
		waitClose = 0;
		delta = 0;
		totalTime = turnTime;
		currentTick = 0;
		turnIndex = 0;
		hasPassword = p != "";
		state = RoomState.WaitClose;
		mode = mo;//����������¼��
	}
	
	final int protectTime = 30000;
	RoomState state;
	int mode;
	boolean gamestart;//�����Ƿ�ʼ��Ϸ-�������������׸���ҽ����-��ʼ��ʱ
	int index;
	KcpS kcpServer;
	List<Player> players;
	String name;
	String password;
	int rule;
	int version;
	Record rec;//�ز�ָ���¼
	int group1;
	int group2;
	int maxPlayer;
	int maxHp;
	int count;//��ǰ��������� ����A+����B
	int turnTime;//���ִ�ʱ��.
	int mapIdx;//��ͼ���.
	int ownerIdx;//����.
	//�����߼�֡���ʱ��
	int delta;
	long totalTime;
	long currentTick;
	int turnIndex;//��������
	boolean hasPassword;
	//�ȴ��رռ�ʱ��
	int waitClose;
	//���ִ�ʱ�䵽-���˺�����ʱ��.
	void NewTurn()
	{
		currentTick = System.currentTimeMillis();
		totalTime = turnTime;
		delta = 0;
		gamestart = true;
		turnIndex++;
		state = RoomState.Wait;
		
		//�㲥��֪�������-��ʼ��һ��
//		KcpBoardCastMsg(MeteorMsg.MsgType.NewTurn);
	}
	
	//TCP����㲥���ݰ�.����Ϣ��
	void BoardCastMsgEx(int message, Pack pak)
	{
		for (int i = 0; i < players.size(); i++)
		{
			Player p = players.get(i);
			p.SendPacket(pak);
		}
	}
	
	//TCP����㲥���ݰ�.����Ϣ��
	void BoardCastMsg(int message)
	{
		Pack pak = new Pack(message, null);
		for (int i = 0; i < players.size(); i++)
		{
			Player p = players.get(i);
			p.SendPacket(pak);
		}
	}

	
	void Update()
	{
		switch (state)
		{
			case WaitClose:
				Wait();
				break;
			case Run:
				Fight();
				break;
			case Wait:
				break;
		default:
			break;
		}
		
	}
	
	//�ȴ�30S�رմ˷��䣬�����˽���
	void Wait()
	{
		boolean closed = CheckClose();
		if (closed)
			Close();
	}
	
	void Fight()
	{
		long t = System.currentTimeMillis();
		int dt = (int)(t - currentTick);
		delta += dt;
		totalTime -= dt;
		currentTick = t;
		//���ִν���.
		if (totalTime <= 0)
		{
			NewTurn();
			return;
		}
		
		if (delta > syncDelta)
		{
//			if (kcpServer != NULL)
//				kcpServer.Update();
		}
	}
	
	
	
	//������������뿪.
	void OnPlayerAllLeaved()
	{
		gamestart = false;
		waitClose = 0;
	}
	
	//�رշ���-
	void Close()
	{
		GameSvr.Instance.eventBus.Fire(CommonEvent.RoomClosed);
	}
	
	//����Ƿ���Ҫ�رշ���
	boolean CheckClose()
	{
		if (waitClose >= protectTime)
		{
			Close();
			return true;
		}
		return false;
	}
	
	//��ʼ��Kcp������
	int InitKcpServer()
	{
		return 0;
	}
	
	void OnPlayerEnter(Player p)
	{
		synchronized (players)
		{
			players.add(p);
			count = players.size();
			if (count != 0 && state != RoomState.Run)
				ChangeState(RoomState.Run);
				
		}
	}
	
	void OnPlayerLeaved(Player p)
	{
		synchronized (players)
		{
			players.remove(p);
			count = players.size();
			if (count == 0)
				OnPlayerAllLeaved();
			else
			{
				KcpMessage(p, MeteorMsg.MsgType.OnPlayerQuit_VALUE);
			}
		}
	}
	
	void ChangeState(RoomState newstate)
	{
		state = newstate;
		switch (state)
		{
		case Run:
			break;
		}
	}
	
	//������һ���߼�֡��Ϊָ�������Ӳ���ָ��.
	void KcpMessage(Player p, int message)
	{
		//FrameCommand
	}
	
	//��һ���߼�֡��UDP�㲥��Ϣ
	void KcpBoardCastMsg(int message)
	{
		
	}
}
