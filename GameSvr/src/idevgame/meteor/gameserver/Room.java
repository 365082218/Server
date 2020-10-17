package idevgame.meteor.gameserver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import idevgame.meteor.dispatcher.EventDispatcher;
import idevgame.meteor.gameserver.jkcp.KcpServer;
import idevgame.meteor.net.PackCodec;
import idevgame.meteor.net.PackCodec.Pack;
import idevgame.meteor.proto.MeteorMsgs.FrameCommand;
import idevgame.meteor.proto.MeteorMsgs.GameFrame;
import idevgame.meteor.proto.MeteorMsgs.MeteorMsg;
import idevgame.meteor.proto.MeteorMsgs.MeteorMsg.Command;
import idevgame.meteor.proto.MeteorMsgs.OnEnterLevelRsp;
import idevgame.meteor.proto.MeteorMsgs.OnPlayerJoinRoom;
import idevgame.meteor.proto.MeteorMsgs.PlayerEvent;
import idevgame.meteor.proto.MeteorMsgs.PlayerSync;
import idevgame.meteor.proto.MeteorMsgs.RoomInfo.MeteorVersion;
import idevgame.meteor.proto.MeteorMsgs.RoomInfo.RoomPattern;
import idevgame.meteor.proto.MeteorMsgs.RoomInfo.RoomRule;
import idevgame.meteor.proto.MeteorMsgs.SyncMsg;


public class Room {
	private static Logger logger = LoggerFactory.getLogger(Room.class);
	static int uniqueId = 0;
	static int syncDelta = 66;//66毫秒同步一次，1秒约同步16次
	//房间序号，名称，密码，规则，地图ID
	public Room(String n, String pwd, RoomRule _rule, int map, int owner, int time, int maxP, int maxH, MeteorVersion ver, RoomPattern _pattern, List<Integer> m)
	{
		index = uniqueId++;
		if (uniqueId == Integer.MAX_VALUE)
			uniqueId = 0;
		name = n;
		password = pwd;
		rule = _rule;
		version = ver;
		group1 = 0;
		group2 = 0;
		maxPlayer = maxP;
		maxHp = maxH;
		playerNum = 0;
		turnTime = time * 60 * 1000;
//		turnTime = 50 * 1000;
		mapIdx = map;
		ownerIdx = owner;
		delta = 0;
		totalTime = turnTime;
		currentTick = 0;
		turn = 0;
		frame = 0;
		hasPassword = pwd != "";
		groupPlayerMax = (_rule == RoomRule.KillTarget || _rule == RoomRule.Endless) ? maxPlayer / 2 : maxPlayer;
		state = RoomState.Wait;
		pattern = _pattern;//是正常或者录像
		players = new ArrayList<Player>();
		commands = new ArrayList<GameFrame>();
		models = m;
		blackList = new ArrayList<Skick>();
	}
	
	public boolean start;//是否开始跑帧，当第一个玩家，进入战场后，开始跑帧，如果只是进房间，是不跑帧的
	public RoomState state;
	public RoomPattern pattern;
	public int index;
	public List<Player> players;
	public List<GameFrame>  commands;//下标就是帧序号
	public String name;
	public String password;
	public RoomRule rule;
	public MeteorVersion version;
	Record rec;//重播指令记录
	public List<Integer> models;
	public int group1;
	public int group2;
	public int groupPlayerMax;
	public int maxPlayer;
	public int maxHp;
	public int playerNum;//当前玩家总人数 队伍A+队伍B
	int turnTime;//单轮次时长.
	public int mapIdx;//地图序号.
	public int ownerIdx;//房主.
	//单次逻辑帧间隔时长
	long delta;
	int totalTime;
	long currentTick;
	int turn;//多少轮了
	int frame;//多少帧
	boolean hasPassword;
	public List<Skick> blackList;//黑名单
	
	//收到某个玩家的帧指令-一段时间内的状态同步/指令.
	public void OnReveiceInput(Player player, GameFrame frame) throws Exception
	{
		List<FrameCommand> fcmd = frame.getCommandsList();
		synchronized(player)
		{
			for (int i = 0; i < fcmd.size(); i++){
				FrameCommand f = fcmd.get(i);
				Command cmd = f.getCommand();
				ByteString bs = f.getData();
				if (cmd == Command.ClientSync)
				{
					PlayerSync sync = PlayerSync.parseFrom(bs);
					player.pos = sync.getPosition();
					player.action = sync.getAction();
					player.rotate = sync.getRotation();
					player.ang = sync.getAng();
					player.hp = sync.getHp();
					List<Integer> ls = sync.getBuffList();
					player.buffs.clear();
					for (int j = 0; j < ls.size(); j++)
						player.buffs.add(ls.get(j));
					int model = sync.getModel();
					//内置的20个模型和房主安装了的模型外，都不许切换了.
					if (this.models.contains(model) || model <= 20)
						player.model = model;
					player.weapon1 = sync.getWeapon();
					player.weapon2 = sync.getWeapon1();
				}
			}
			player.synced = true;
			player.dirty = true;
		}
	}
	
	//单轮次时间到-换人和武器时间.
	void NewTurn()
	{
		currentTick = System.currentTimeMillis();
		totalTime = turnTime;
		delta = 0;
		turn++;
		state = RoomState.Wait;
		start = false;
		//全部玩家，把同步状态切换会未同步
		synchronized(players)
		{
			for (int i = 0; i < players.size(); i++){
				Player p = players.get(i);
				p.NewTurn();
			}
		}
		//广播告知所有玩家-下一轮开始，可以选择阵营角色了,表明服务端等待第一个玩家进入驱动服务端跑帧
		//同时客户端需要重置场景状态，删除所有角色，不要再更新UDP包
		this.KcpBoardCastMsg(MeteorMsg.Command.NewTurn_VALUE);
		group1 = 0;
		group2 = 0;
		//重新进入
	}
	
	//TCP房间广播数据包.带消息体
	public void BoardCastMsgEx(Pack pak, Player exclude)
	{
		for (int i = 0; i < players.size(); i++)
		{
			Player p = players.get(i);
			if (p == exclude)
				continue;
			p.TcpSendPacket(pak);
		}
	}
	
	//TCP房间广播数据包.带消息号
	public void BoardCastMsg(int message)
	{
		Pack pak = new Pack(message, null);
		for (int i = 0; i < players.size(); i++)
		{
			Player p = players.get(i);
			p.TcpSendPacket(pak);
		}
	}

	
	void Update(long millsecond)
	{
		switch (state)
		{
			case Run:
				Fight(millsecond);
				break;
			case Wait:
			case Closing:
				break;
		default:
			break;
		}
		
	}
	
	void Fight(long deltaTime)
	{
		delta += deltaTime;
		totalTime -= deltaTime;
		currentTick = GameSvr.Instance.GlobalTime;
		//该轮次结束.
		if (totalTime <= 0)
		{
			NewTurn();
			return;
		}
		
		if (delta > syncDelta)
		{
			delta = 0;
			GameFrame frame = Sync(null);
			Pack pack = new Pack(MeteorMsg.Command.ServerSync_VALUE, frame.toByteArray());
			this.KcpBoardCastMsgEx(pack, null);
		}
	}
	
	GameFrame Sync(Player exclude){
		GameFrame.Builder cmdbuild = GameFrame.newBuilder();
		cmdbuild.setTime(totalTime);
		synchronized(players)
		{
			for (int i = 0; i < players.size(); i++)
			{
				Player p = players.get(i);
				synchronized(p)
				{
					if (p == exclude)
						continue;
					if (p.state != PlayerState.InLevel)
						continue;
					if (!p.synced)
						continue;
					p.dirty = false;
					FrameCommand.Builder fb = FrameCommand.newBuilder();
					fb.setCommand(MeteorMsg.Command.ServerSync);
					fb.setPlayerId(p.playerIdx);
					PlayerSync.Builder pb = PlayerSync.newBuilder();
					pb.setPlayerId(p.playerIdx);
					pb.setCamp(p.camp);
					pb.setName(p.NickName);
					pb.setAng(p.ang);
					pb.setAction(p.action);
					pb.setHp(p.hp);
					pb.setModel(p.model);
					pb.setPosition(p.pos);
					pb.setRotation(p.rotate);
					pb.setWeapon(p.weapon1);
					pb.setWeapon1(p.weapon2);
					for (int j = 0; j < p.buffs.size(); j++)
						pb.addBuff(p.buffs.get(j));
					fb.setData(pb.build().toByteString());
					cmdbuild.addCommands(fb.build());
				}
			}
			return cmdbuild.build();
		}
	}
	//关闭房间-
	void Close()
	{
		state = RoomState.Closing;
		GameSvr.Instance.eventBus.Fire(CommonEvent.RoomClosed, this);
	}
	
	//进入房间，还未进入场景
	public void OnPlayerEnter(Player p)
	{
		synchronized (players)
		{
			if (players.size() != 0)
			{
				OnPlayerJoinRoom.Builder builder = OnPlayerJoinRoom.newBuilder();
				builder.setNick(p.NickName);
				builder.setPlayer(p.playerIdx);
				Pack pak = new Pack(MeteorMsg.MsgType.OnPlayerJoinRoom_VALUE, builder.build().toByteArray());
				BoardCastMsgEx(pak, null);
			}
			players.add(p);
			p.state = PlayerState.InRoom;
			p.room = this;
			playerNum = players.size();
		}
	}
	
	public void OnPlayerEnterLevel(Player p){
		//要确定这个阵营是否还能继续加人
		if (rule == RoomRule.KillTarget || rule == RoomRule.Endless)
		{
			if ((p.camp == 1 && group1 >= groupPlayerMax) ||(p.camp == 2 && group2 >= groupPlayerMax))
			{
				//选择的阵营人数已满.
				OnEnterLevelRsp.Builder rsp = OnEnterLevelRsp.newBuilder();
				rsp.setReason(EResult.CampMax);
				rsp.setResult(0);
				Pack pack = new Pack(MeteorMsg.MsgType.EnterLevelRsp_VALUE, rsp.build().toByteArray());
				p.TcpSendPacket(pack);
				return;
			}
		}
		
		//如果快到结算时间，提示此轮游戏即将结束
		if (totalTime <= 30 * 1000){
			OnEnterLevelRsp.Builder rsp = OnEnterLevelRsp.newBuilder();
			rsp.setReason(EResult.Timeup);
			rsp.setResult(3 + (totalTime / 1000));
			Pack pack = new Pack(MeteorMsg.MsgType.EnterLevelRsp_VALUE, rsp.build().toByteArray());
			p.TcpSendPacket(pack);
			return;
		}
		
		if (!start){
			start = true;//一旦有人进入战场(Tcp请求)就开始跑
			this.ChangeState(RoomState.Run);
		}
		
		if (rule == RoomRule.KillTarget || rule == RoomRule.Endless){
			if (p.camp == 1)
				group1++;
			if (p.camp == 2)
				group2++;
		}
		
		p.state = PlayerState.InLevel;
		GetSpawnIndex(p);
		//广播告知其他角色有人进入关卡了
		PlayerEvent.Builder pb = PlayerEvent.newBuilder();
		pb.setPlayerId(p.playerIdx);
		pb.setName(p.NickName);
		pb.setModel(p.model);
		pb.setWeapon(p.weapon1);
		pb.setCamp(p.camp);
		pb.setSpawnIndex(p.spawn);
		//设置出生点
		Pack pak = new Pack(MeteorMsg.MsgType.OnPlayerEnterLevel_VALUE, pb.build().toByteArray());
		this.BoardCastMsgEx(pak, null);
		//告诉该玩家，之前在场景内的角色信息-一定不包括进入的这个玩家.
		OnEnterLevelRsp.Builder rsp = OnEnterLevelRsp.newBuilder();
		rsp.setReason(EResult.Succeed);
		rsp.setResult(1);
		//只同步已经在战场内的角色,在选人界面的不同步
		for (int i = 0; i < players.size(); i++){
			Player n = players.get(i);
			if (!n.synced)
				continue;
			PlayerSync.Builder nb = PlayerSync.newBuilder();
			nb.setHp(n.hp);
			nb.setAng(n.ang);
			nb.setModel(n.model);
			nb.setPlayerId(n.playerIdx);
			nb.setCamp(n.camp);
			nb.setName(n.NickName);
			nb.setWeapon(n.weapon1);
			nb.setWeapon1(n.weapon2);
			nb.setAction(n.action);
			nb.setPosition(n.pos);
			nb.setRotation(n.rotate);
			nb.addAllBuff(n.buffs);
			nb.setSpwanIndex(n.spawn);
			rsp.addPlayers(nb.build());
		}
		Pack pack = new Pack(MeteorMsg.MsgType.EnterLevelRsp_VALUE, rsp.build().toByteArray());
		p.TcpSendPacket(pack);
		
		//进战斗前调整好速率
		SyncMsg.Builder syncmb = SyncMsg.newBuilder();
		syncmb.setSyncrate(1000 / Room.syncDelta);
		Pack syncRate = new Pack(MeteorMsg.MsgType.SyncRate_VALUE, syncmb.build().toByteArray());
		p.TcpSendPacket(syncRate);
	}
	
	int spawnIndex = 0;
	int spawnIndexA = 0;
	int spawnIndexB = 0;
	public void GetSpawnIndex(Player player){
		if (rule == RoomRule.Endless || rule == RoomRule.KillTarget){
			if (player.camp == 0){
				player.spawn = spawnIndexA++;
				if (spawnIndexA >= 8)
					spawnIndexA = 0;
			} else if (player.camp == 1){
				player.spawn = spawnIndexB++;
				if (spawnIndexB >= 8)
					spawnIndexB = 0;
			}
		} else {
			player.spawn = spawnIndex;
			spawnIndex ++;
			if (spawnIndex >= 16){
				spawnIndex = 0;
			}
		}
	}
	
	//离开场景，同时离开房间
	public void OnPlayerLeaved(Player p)
	{
		synchronized (players)
		{
			if (rule == RoomRule.KillTarget || rule == RoomRule.Endless){
				if (p.camp == 1)
					group1--;
				if (p.camp == 2)
					group2--;
				if (group1 < 0)
					group1 = 0;
				if (group2 < 0)
					group2 = 0;
			}
			p.Reset();
			players.remove(p);
			playerNum = players.size();
			if (playerNum == 0)
			{
				Close();
			}
			else
			{
				//创建一个玩家离开事件，放到帧指令列表内.下一帧广播出去.
				PlayerEvent.Builder build = PlayerEvent.newBuilder();
				build.setPlayerId(p.playerIdx);
				build.setName(p.NickName);
				build.setCamp(p.camp);
				build.setModel(p.model);
				build.setWeapon(p.weapon1);
				Pack pak = new Pack(MeteorMsg.MsgType.OnPlayerLeaveLevel_VALUE, build.build().toByteArray());
				this.BoardCastMsgEx(pak, null);
			}
		}
	}
	
	//第一个玩家用kcp链接到服务器，则切换状态为开始
	void ChangeState(RoomState newstate)
	{
		state = newstate;
	}
	
	void KcpMessage(Player p, int message)
	{
		Pack pak = new Pack(message, null);
		p.KcpSend(pak);
	}

	
	//下一个逻辑帧内UDP广播消息
	void KcpBoardCastMsg(int message)
	{
		synchronized(this)
		{
			Pack pak = new Pack(message, null);
			for (int i = 0; i < players.size(); i++){
				Player p = players.get(i);
				p.KcpSend(pak);
			}
		}
	}
	
	
	//下一个逻辑帧内UDP
	void KcpBoardCastMsgEx(Pack pak, Player except){
		synchronized(this)
		{
			for (int i = 0; i < players.size(); i++){
				Player p = players.get(i);
				if (p == except)
					continue;
				p.KcpSend(pak);
			}
		}
	}
}
