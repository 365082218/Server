package idevgame.meteor.gameserver;

import java.util.List;

import idevgame.meteor.net.PackCodec.Pack;
import idevgame.meteor.proto.MeteorMsgs.MeteorMsg;
enum RoomState
{
	Run,//比赛进行中
	Wait,//轮次结束准备下一个轮次比赛中，30S内若无玩家进入则关闭
	WaitClose,//等待关闭，30S
}

public class Room {
	static final int syncDelta = 62;//62毫秒同步一次，1秒约同步16次
	//房间序号，名称，密码，规则，地图ID
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
		mode = mo;//是正常或者录像
	}
	
	final int protectTime = 30000;
	RoomState state;
	int mode;
	boolean gamestart;//房间是否开始游戏-房主建立房间首个玩家进入后-开始计时
	int index;
	KcpS kcpServer;
	List<Player> players;
	String name;
	String password;
	int rule;
	int version;
	Record rec;//重播指令记录
	int group1;
	int group2;
	int maxPlayer;
	int maxHp;
	int count;//当前玩家总人数 队伍A+队伍B
	int turnTime;//单轮次时长.
	int mapIdx;//地图序号.
	int ownerIdx;//房主.
	//单次逻辑帧间隔时长
	int delta;
	long totalTime;
	long currentTick;
	int turnIndex;//多少轮了
	boolean hasPassword;
	//等待关闭计时器
	int waitClose;
	//单轮次时间到-换人和武器时间.
	void NewTurn()
	{
		currentTick = System.currentTimeMillis();
		totalTime = turnTime;
		delta = 0;
		gamestart = true;
		turnIndex++;
		state = RoomState.Wait;
		
		//广播告知所有玩家-开始下一轮
//		KcpBoardCastMsg(MeteorMsg.MsgType.NewTurn);
	}
	
	//TCP房间广播数据包.带消息体
	void BoardCastMsgEx(int message, Pack pak)
	{
		for (int i = 0; i < players.size(); i++)
		{
			Player p = players.get(i);
			p.SendPacket(pak);
		}
	}
	
	//TCP房间广播数据包.带消息号
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
	
	//等待30S关闭此房间，若无人进入
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
		//该轮次结束.
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
	
	
	
	//房间所有玩家离开.
	void OnPlayerAllLeaved()
	{
		gamestart = false;
		waitClose = 0;
	}
	
	//关闭房间-
	void Close()
	{
		GameSvr.Instance.eventBus.Fire(CommonEvent.RoomClosed);
	}
	
	//检查是否需要关闭房间
	boolean CheckClose()
	{
		if (waitClose >= protectTime)
		{
			Close();
			return true;
		}
		return false;
	}
	
	//初始化Kcp服务器
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
	
	//房间下一个逻辑帧内为指定玩家添加操作指令.
	void KcpMessage(Player p, int message)
	{
		//FrameCommand
	}
	
	//下一个逻辑帧内UDP广播消息
	void KcpBoardCastMsg(int message)
	{
		
	}
}
