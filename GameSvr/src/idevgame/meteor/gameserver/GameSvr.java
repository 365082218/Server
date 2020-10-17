package idevgame.meteor.gameserver;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import idevgame.meteor.netty4.Netty4SocketClient;
import idevgame.meteor.netty4.Netty4SocketServer;
import idevgame.meteor.proto.MeteorMsgs.RoomInfo.MeteorVersion;
import idevgame.meteor.proto.MeteorMsgs.RoomInfo.RoomPattern;
import idevgame.meteor.proto.MeteorMsgs.RoomInfo.RoomRule;
import idevgame.meteor.utils.ClassPathScanner;
import idevgame.meteor.utils.PropertiesUtil;
import io.netty.channel.ChannelHandlerContext;
import idevgame.meteor.dispatcher.Dispatcher;
import idevgame.meteor.dispatcher.Event;
import idevgame.meteor.dispatcher.EventBus;
import idevgame.meteor.dispatcher.EventDispatcher;
import idevgame.meteor.gameserver.handler.GameSvrMsgsHandler;
class CommonEvent
{
	public CommonEvent()
	{
		
	}
	static final int RoomClosed = 1;
}

//处理客户端链接
//主动连接到其他服务器
public class GameSvr {
	public static int MaxRooms = 16;
	public static int TcpPort = 7200;
	public static int KcpPort = 7300;
	public static int SyncRate = 20;//1秒20次
	public static int ProtocolVersion = 20201013;
	public static int MaxPacketSize = 512 * 1024;//最大解开512K的消息报文，也就是说一场战斗的数据记录，只能比这个小
	//public static 
	private final String prop = "center.properties";
	private Logger logger = LoggerFactory.getLogger(GameSvr.class);
	public PropertiesUtil center = new PropertiesUtil(prop);
	//游戏服务器
	public Netty4SocketClient center_server;
	public int center_port;
	public final List<Room> Rooms = new ArrayList<Room>();
	public final List<Room> DeletedRooms = new ArrayList<Room>();
	public final ConcurrentMap<Integer, Player> players = new ConcurrentHashMap<Integer, Player>();
	public EventBus eventBus;
	public static GameSvr Instance = new GameSvr();
	public long GlobalTime = 0;
//	public static DBHelper db0;
	public static void main(String[] ags) throws Exception {
//		db0 = new DBHelper(prop);
		//扫描处理协议的类-初始化分发器
		Instance.Init();
		Instance.InitKcpServer();
		Instance.Loop();
	}
	
	void Loop() throws Exception{
		long current = System.currentTimeMillis();
		while (true){
			boolean update = DeletedRooms.size() != 0;
			for (int i = 0; i < DeletedRooms.size(); i++){
				Rooms.remove(DeletedRooms.get(i));
			}
			DeletedRooms.clear();
			if (update)
				BoardCastRoomChanged();
			long next = System.currentTimeMillis();
			long dt = next  - current;
			GlobalTime += dt;
			for (int i = 0; i < Rooms.size(); i++){
				Room r = Rooms.get(i);
				if (r.state == RoomState.Closing)
					continue;
				r.Update(dt);
			}
			current = next;
			Thread.sleep(1);
		}
	}
	void Init() throws Exception
	{
		//初始化事件中心
		EventDispatcher eventDispatcher = new EventDispatcher();
		Set<Class> classesAll = ClassPathScanner.scan("idevgame.meteor.gameserver", false, true, false, null);
		classesAll.clear();
		classesAll.add(this.getClass());
		eventDispatcher.load(classesAll);
		eventBus = new EventBus(eventDispatcher);
		eventBus.AddListener(this);
		//初始化协议处理
		Dispatcher dispatcher = new Dispatcher();
		Set<Class> classes = ClassPathScanner.scan("idevgame.meteor.gameserver.handler", false, true, false, null);
		classes.clear();
		classes.add(GameSvrMsgsHandler.class);
		dispatcher.load(classes);
		//客户端handler
//		NetHandler handler = new NetHandler(dispatcher);
//		int port = Integer.parseInt(center.getProperty("center.port"));
		//链接中心服
//		center_server = new Netty4SocketClient(handler, port, "gamesvr->gamecenter");
//		try
//		{
//			center_server.connect();
//		}
//		catch (Exception exp)
//		{
//			exp.printStackTrace();
//		}
		//启动游戏服
		TcpPort = Integer.parseInt(center.getProperty("gamesvr.tcpport"));
		MaxRooms = Integer.parseInt(center.getProperty("gamesvr.maxrooms"));
		SyncRate = Integer.parseInt(center.getProperty("gamesvr.syncrate"));
		Room.syncDelta = 1000 / SyncRate;
		if (Room.syncDelta < 10)
			Room.syncDelta = 16;//每秒60次同步最多
		if (Room.syncDelta > 100)
			Room.syncDelta = 100;//每秒10次同步最少
		Netty4SocketServer  game_server = new Netty4SocketServer(new GameSvrNetHandler(dispatcher), TcpPort, 60);
		game_server.start();
		//向中心服注册,告知中心服自身启动服务的IP和端口.
//		RegisterSvrReq.Builder builder = RegisterSvrReq.newBuilder();
//		builder.setServerName("测试服");
//		builder.setIp("127.0.0.1");
//		builder.setPort(port);
//		RegisterSvrReq req = builder.build();
//		Pack pak = new PackCodec.Pack(MeteorMsg.MsgType.RegisterSvrReq_VALUE, req.toByteArray());
//		center_server.Write(pak);
		
		Runnable runnable = new Runnable() {  
            public void run() {
            	//定时向中心服务器同步当前所有房间的状态
        		HeartBeat();
            }
        };  
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();  
        // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间  
        service.scheduleAtFixedRate(runnable, 30, 30, TimeUnit.SECONDS);
	}
	
	public void BoardCastRoomChanged(){
		
	}
	
	public void InitKcpServer()
	{
		KcpPort = Integer.parseInt(center.getProperty("gamesvr.kcpport"));
		KcpS s = new KcpS(KcpPort, 1);
        s.noDelay(1, 20, 2, 1);
        s.setMinRto(10);
        s.wndSize(128, 128);
        s.setTimeout(0);
        s.setMtu(1400);
        s.start();
	}
	
	@Event(id = CommonEvent.RoomClosed)  
	public void OnRoomClosed(Room closed)
	{
		DeletedRooms.add(closed);//下一次删除
	}
	
	//客户端TCP心跳，服务器做
	void HeartBeat()
	{
		synchronized(players)
    	{
			if (players.size() == 0)
				return;
			ConcurrentMap<Integer, Player> deleted = new ConcurrentHashMap<Integer, Player>(); 
			Set<Integer> c = players.keySet();
			long now = System.currentTimeMillis();
			for (Integer i: c)
			{
				Player p = players.get(i);
				if (!p.Alive(now))
					deleted.put(p.playerIdx, p);
			}
			Set<Integer> d = deleted.keySet();
			for (Integer i: d)
			{
				Player p = players.get(i);
				if (p.room != null){
					p.room.OnPlayerLeaved(p);
				}
				players.remove(i);
			}
			Set<Integer> h = players.keySet();
			for (Integer i: h)
			{
				Player p = players.get(i);
				p.HeartBeat();
			}
			
    	}
	}
	public Player FindPlayer(ChannelHandlerContext ctx){
		Set<Integer> c = players.keySet();
		for (Integer i: c)
		{
			Player p = players.get(i);
			if (p.context == ctx)
			{
				return p;
			}
		}
		return null;
	}
	
	public Player FindPlayer(int playerId)
	{
		synchronized(players)
		{
			Player p = players.get(playerId);
			return p;
		}
	}
	
	public Player OnPlayerVerifyed(ChannelHandlerContext ctx){
		Player p = new Player(ctx);
		players.put(p.playerIdx, p);
		return p;
	}
	//客户端链接进来
	public void OnPlayerConnect(ChannelHandlerContext ctx)
	{
		//等待用户验证
	}
	
	//客户端断开链接
	public void OnPlayerDisConnect(ChannelHandlerContext ctx)
	{
		int delete = -1;
		Set<Integer> c = players.keySet();
		for (Integer i: c)
		{
			Player p = players.get(i);
			if (p.context == ctx)
			{
				delete = i;
				break;
			}
		}
		if (delete != -1)
		{
			//如果该玩家角色对象已存在，对其离开战场.
			Player p = players.get(delete);
			if (p != null){
				if (p.room != null)
					p.room.OnPlayerLeaved(p);
				players.remove(p.playerIdx);
			}
		}
	}

	public Room FindRoom(int id){
		for (int i = 0; i < Rooms.size(); i++){
			Room r = Rooms.get(i);
			if (r.index == id)
				return r;
		}
		return null;
	}
	
	public void OnPlayerHeartBeat(ChannelHandlerContext ctx) {
		Player p = FindPlayer(ctx);
		if (p != null){
			p.OnPlayerAlive();
		}
	}
	
	//pattern:是否录像转播
	//version:流星版本
	public Room CreateRoom(String name, String pwd, RoomRule rule, int map, int owner, int time, int maxPlayer, int maxHp,  MeteorVersion meteor_version, RoomPattern pattern, List<Integer> models){
		Room created = new Room(name, pwd, rule, map, owner, time, maxPlayer, maxHp, meteor_version, pattern, models);
		Rooms.add(created);
		return created;
	}
}