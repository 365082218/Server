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
	static int syncDelta = 66;//66����ͬ��һ�Σ�1��Լͬ��16��
	//������ţ����ƣ����룬���򣬵�ͼID
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
		pattern = _pattern;//����������¼��
		players = new ArrayList<Player>();
		commands = new ArrayList<GameFrame>();
		models = m;
		blackList = new ArrayList<Skick>();
	}
	
	public boolean start;//�Ƿ�ʼ��֡������һ����ң�����ս���󣬿�ʼ��֡�����ֻ�ǽ����䣬�ǲ���֡��
	public RoomState state;
	public RoomPattern pattern;
	public int index;
	public List<Player> players;
	public List<GameFrame>  commands;//�±����֡���
	public String name;
	public String password;
	public RoomRule rule;
	public MeteorVersion version;
	Record rec;//�ز�ָ���¼
	public List<Integer> models;
	public int group1;
	public int group2;
	public int groupPlayerMax;
	public int maxPlayer;
	public int maxHp;
	public int playerNum;//��ǰ��������� ����A+����B
	int turnTime;//���ִ�ʱ��.
	public int mapIdx;//��ͼ���.
	public int ownerIdx;//����.
	//�����߼�֡���ʱ��
	long delta;
	int totalTime;
	long currentTick;
	int turn;//��������
	int frame;//����֡
	boolean hasPassword;
	public List<Skick> blackList;//������
	
	//�յ�ĳ����ҵ�ָ֡��-һ��ʱ���ڵ�״̬ͬ��/ָ��.
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
					//���õ�20��ģ�ͺͷ�����װ�˵�ģ���⣬�������л���.
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
	
	//���ִ�ʱ�䵽-���˺�����ʱ��.
	void NewTurn()
	{
		currentTick = System.currentTimeMillis();
		totalTime = turnTime;
		delta = 0;
		turn++;
		state = RoomState.Wait;
		start = false;
		//ȫ����ң���ͬ��״̬�л���δͬ��
		synchronized(players)
		{
			for (int i = 0; i < players.size(); i++){
				Player p = players.get(i);
				p.NewTurn();
			}
		}
		//�㲥��֪�������-��һ�ֿ�ʼ������ѡ����Ӫ��ɫ��,��������˵ȴ���һ����ҽ��������������֡
		//ͬʱ�ͻ�����Ҫ���ó���״̬��ɾ�����н�ɫ����Ҫ�ٸ���UDP��
		this.KcpBoardCastMsg(MeteorMsg.Command.NewTurn_VALUE);
		group1 = 0;
		group2 = 0;
		//���½���
	}
	
	//TCP����㲥���ݰ�.����Ϣ��
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
	
	//TCP����㲥���ݰ�.����Ϣ��
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
		//���ִν���.
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
	//�رշ���-
	void Close()
	{
		state = RoomState.Closing;
		GameSvr.Instance.eventBus.Fire(CommonEvent.RoomClosed, this);
	}
	
	//���뷿�䣬��δ���볡��
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
		//Ҫȷ�������Ӫ�Ƿ��ܼ�������
		if (rule == RoomRule.KillTarget || rule == RoomRule.Endless)
		{
			if ((p.camp == 1 && group1 >= groupPlayerMax) ||(p.camp == 2 && group2 >= groupPlayerMax))
			{
				//ѡ�����Ӫ��������.
				OnEnterLevelRsp.Builder rsp = OnEnterLevelRsp.newBuilder();
				rsp.setReason(EResult.CampMax);
				rsp.setResult(0);
				Pack pack = new Pack(MeteorMsg.MsgType.EnterLevelRsp_VALUE, rsp.build().toByteArray());
				p.TcpSendPacket(pack);
				return;
			}
		}
		
		//����쵽����ʱ�䣬��ʾ������Ϸ��������
		if (totalTime <= 30 * 1000){
			OnEnterLevelRsp.Builder rsp = OnEnterLevelRsp.newBuilder();
			rsp.setReason(EResult.Timeup);
			rsp.setResult(3 + (totalTime / 1000));
			Pack pack = new Pack(MeteorMsg.MsgType.EnterLevelRsp_VALUE, rsp.build().toByteArray());
			p.TcpSendPacket(pack);
			return;
		}
		
		if (!start){
			start = true;//һ�����˽���ս��(Tcp����)�Ϳ�ʼ��
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
		//�㲥��֪������ɫ���˽���ؿ���
		PlayerEvent.Builder pb = PlayerEvent.newBuilder();
		pb.setPlayerId(p.playerIdx);
		pb.setName(p.NickName);
		pb.setModel(p.model);
		pb.setWeapon(p.weapon1);
		pb.setCamp(p.camp);
		pb.setSpawnIndex(p.spawn);
		//���ó�����
		Pack pak = new Pack(MeteorMsg.MsgType.OnPlayerEnterLevel_VALUE, pb.build().toByteArray());
		this.BoardCastMsgEx(pak, null);
		//���߸���ң�֮ǰ�ڳ����ڵĽ�ɫ��Ϣ-һ�������������������.
		OnEnterLevelRsp.Builder rsp = OnEnterLevelRsp.newBuilder();
		rsp.setReason(EResult.Succeed);
		rsp.setResult(1);
		//ֻͬ���Ѿ���ս���ڵĽ�ɫ,��ѡ�˽���Ĳ�ͬ��
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
		
		//��ս��ǰ����������
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
	
	//�뿪������ͬʱ�뿪����
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
				//����һ������뿪�¼����ŵ�ָ֡���б���.��һ֡�㲥��ȥ.
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
	
	//��һ�������kcp���ӵ������������л�״̬Ϊ��ʼ
	void ChangeState(RoomState newstate)
	{
		state = newstate;
	}
	
	void KcpMessage(Player p, int message)
	{
		Pack pak = new Pack(message, null);
		p.KcpSend(pak);
	}

	
	//��һ���߼�֡��UDP�㲥��Ϣ
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
	
	
	//��һ���߼�֡��UDP
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
