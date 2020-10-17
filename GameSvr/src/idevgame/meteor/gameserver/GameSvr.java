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

//����ͻ�������
//�������ӵ�����������
public class GameSvr {
	public static int MaxRooms = 16;
	public static int TcpPort = 7200;
	public static int KcpPort = 7300;
	public static int SyncRate = 20;//1��20��
	public static int ProtocolVersion = 20201013;
	public static int MaxPacketSize = 512 * 1024;//���⿪512K����Ϣ���ģ�Ҳ����˵һ��ս�������ݼ�¼��ֻ�ܱ����С
	//public static 
	private final String prop = "center.properties";
	private Logger logger = LoggerFactory.getLogger(GameSvr.class);
	public PropertiesUtil center = new PropertiesUtil(prop);
	//��Ϸ������
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
		//ɨ�账��Э�����-��ʼ���ַ���
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
		//��ʼ���¼�����
		EventDispatcher eventDispatcher = new EventDispatcher();
		Set<Class> classesAll = ClassPathScanner.scan("idevgame.meteor.gameserver", false, true, false, null);
		classesAll.clear();
		classesAll.add(this.getClass());
		eventDispatcher.load(classesAll);
		eventBus = new EventBus(eventDispatcher);
		eventBus.AddListener(this);
		//��ʼ��Э�鴦��
		Dispatcher dispatcher = new Dispatcher();
		Set<Class> classes = ClassPathScanner.scan("idevgame.meteor.gameserver.handler", false, true, false, null);
		classes.clear();
		classes.add(GameSvrMsgsHandler.class);
		dispatcher.load(classes);
		//�ͻ���handler
//		NetHandler handler = new NetHandler(dispatcher);
//		int port = Integer.parseInt(center.getProperty("center.port"));
		//�������ķ�
//		center_server = new Netty4SocketClient(handler, port, "gamesvr->gamecenter");
//		try
//		{
//			center_server.connect();
//		}
//		catch (Exception exp)
//		{
//			exp.printStackTrace();
//		}
		//������Ϸ��
		TcpPort = Integer.parseInt(center.getProperty("gamesvr.tcpport"));
		MaxRooms = Integer.parseInt(center.getProperty("gamesvr.maxrooms"));
		SyncRate = Integer.parseInt(center.getProperty("gamesvr.syncrate"));
		Room.syncDelta = 1000 / SyncRate;
		if (Room.syncDelta < 10)
			Room.syncDelta = 16;//ÿ��60��ͬ�����
		if (Room.syncDelta > 100)
			Room.syncDelta = 100;//ÿ��10��ͬ������
		Netty4SocketServer  game_server = new Netty4SocketServer(new GameSvrNetHandler(dispatcher), TcpPort, 60);
		game_server.start();
		//�����ķ�ע��,��֪���ķ��������������IP�Ͷ˿�.
//		RegisterSvrReq.Builder builder = RegisterSvrReq.newBuilder();
//		builder.setServerName("���Է�");
//		builder.setIp("127.0.0.1");
//		builder.setPort(port);
//		RegisterSvrReq req = builder.build();
//		Pack pak = new PackCodec.Pack(MeteorMsg.MsgType.RegisterSvrReq_VALUE, req.toByteArray());
//		center_server.Write(pak);
		
		Runnable runnable = new Runnable() {  
            public void run() {
            	//��ʱ�����ķ�����ͬ����ǰ���з����״̬
        		HeartBeat();
            }
        };  
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();  
        // �ڶ�������Ϊ�״�ִ�е���ʱʱ�䣬����������Ϊ��ʱִ�еļ��ʱ��  
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
		DeletedRooms.add(closed);//��һ��ɾ��
	}
	
	//�ͻ���TCP��������������
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
	//�ͻ������ӽ���
	public void OnPlayerConnect(ChannelHandlerContext ctx)
	{
		//�ȴ��û���֤
	}
	
	//�ͻ��˶Ͽ�����
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
			//�������ҽ�ɫ�����Ѵ��ڣ������뿪ս��.
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
	
	//pattern:�Ƿ�¼��ת��
	//version:���ǰ汾
	public Room CreateRoom(String name, String pwd, RoomRule rule, int map, int owner, int time, int maxPlayer, int maxHp,  MeteorVersion meteor_version, RoomPattern pattern, List<Integer> models){
		Room created = new Room(name, pwd, rule, map, owner, time, maxPlayer, maxHp, meteor_version, pattern, models);
		Rooms.add(created);
		return created;
	}
}