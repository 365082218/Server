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
import idevgame.meteor.proto.MeteorMsgs.MeteorMsg;
import idevgame.meteor.proto.MeteorMsgs.RegisterSvrReq;
import idevgame.meteor.proto.MeteorMsgs.RoomInfo;
import idevgame.meteor.proto.MeteorMsgs.RoomInfo.MeteorVersion;
import idevgame.meteor.proto.MeteorMsgs.RoomInfo.RoomPattern;
import idevgame.meteor.proto.MeteorMsgs.RoomInfo.RoomRule;
import idevgame.meteor.proto.MeteorMsgs.SyncSvrState;
import idevgame.meteor.utils.ClassPathScanner;
import idevgame.meteor.utils.PropertiesUtil;
import idevgame.meteor.utils.Session;
import io.netty.channel.ChannelHandlerContext;
import idevgame.meteor.dispatcher.Dispatcher;
import idevgame.meteor.dispatcher.Event;
import idevgame.meteor.dispatcher.EventBus;
import idevgame.meteor.dispatcher.EventDispatcher;
import idevgame.meteor.net.PackCodec;
import idevgame.meteor.net.PackCodec.Pack;
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
	//public static 
	private final String prop = "center.properties";
	private Logger logger = LoggerFactory.getLogger(GameSvr.class);
	public PropertiesUtil center = new PropertiesUtil(prop);
	//��Ϸ������
	public Netty4SocketClient center_server;
	public int center_port;
	public final List<Room> Rooms = new ArrayList<Room>();
	public final ConcurrentMap<Integer, Player> players = new ConcurrentHashMap<Integer, Player>();
	public final ConcurrentMap<Integer, Session> users = new ConcurrentHashMap<Integer, Session>();
	public EventBus eventBus;
	public static GameSvr Instance = new GameSvr();
//	public static DBHelper db0;
	public static void main(String[] ags) throws Exception {
//		db0 = new DBHelper(prop);
		//ɨ�账��Э�����-��ʼ���ַ���
		Instance.Init();
		Instance.InitKcpServer();
		GameSvr.Instance.eventBus.Fire(CommonEvent.RoomClosed);
	}
	
	void Init() throws Exception
	{
		//��ʼ���¼�����
		EventDispatcher eventDispatcher = new EventDispatcher();
		Set<Class> classesAll = ClassPathScanner.scan("idevgame.meteor.gameserver", false, true, false, null);
		eventDispatcher.load(classesAll);
		eventBus = new EventBus(eventDispatcher);
		eventBus.AddListener(this);
		//��ʼ��Э�鴦��
		Dispatcher dispatcher = new Dispatcher();
		Set<Class> classes = ClassPathScanner.scan("idevgame.meteor.gameserver.handler", false, true, false, null);
		dispatcher.load(classes);
		//�ͻ���handler
		NetHandler handler = new NetHandler(dispatcher);
		int port = Integer.parseInt(center.getProperty("center.port"));
		//�������ķ�
		center_server = new Netty4SocketClient(handler, port, "gamesvr->gamecenter");
		try
		{
			center_server.connect();
		}
		catch (Exception exp)
		{
			exp.printStackTrace();
		}
		//������Ϸ��
		port = Integer.parseInt(center.getProperty("gamesvr.port"));
		Netty4SocketServer  game_server = new Netty4SocketServer(new GameSvrNetHandler(dispatcher), port, 60);
		game_server.start();
		
		//�����ķ�ע��,��֪���ķ��������������IP�Ͷ˿�.
		RegisterSvrReq.Builder builder = RegisterSvrReq.newBuilder();
		builder.setServerName("���Է�");
		builder.setIp("127.0.0.1");
		builder.setPort(port);
		RegisterSvrReq req = builder.build();
		Pack pak = new PackCodec.Pack(MeteorMsg.MsgType.RegisterSvrReq_VALUE, req.toByteArray());
		center_server.Write(pak);
		
		Runnable runnable = new Runnable() {  
            public void run() {
            	//��ʱ�����ķ�����ͬ����ǰ���з����״̬
        		SyncState();
            }
        };  
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();  
        // �ڶ�������Ϊ�״�ִ�е���ʱʱ�䣬����������Ϊ��ʱִ�еļ��ʱ��  
        service.scheduleAtFixedRate(runnable, 30, 30, TimeUnit.SECONDS);
	}
	
	public void InitKcpServer()
	{
		KcpS s = new KcpS(2222, 1);
        s.noDelay(1, 10, 2, 1);
        s.setMinRto(10);
        s.wndSize(64, 64);
        s.setTimeout(10 * 1000);
        s.setMtu(512);
        s.start();
	}
	
	@Event(id = CommonEvent.RoomClosed)  
	public void OnRoomClosed()
	{
		System.out.println("OnRoomClosed");
	}
	
	//����Ϸ����״̬ͬ�������ķ���
	void SyncState()
	{
		synchronized(Rooms)
    	{
			if (Rooms.size() == 0)
				return;
			SyncSvrState.Builder builder = SyncSvrState.newBuilder();
			RoomInfo.Builder roomBuild = RoomInfo.newBuilder();
			for (int i = 0; i < Rooms.size(); i++)
			{
				Room r = Rooms.get(i);
				roomBuild.setGroup1(r.group1);
				roomBuild.setGroup2(r.group2);
				roomBuild.setHpMax(r.maxHp);
				roomBuild.setLevelIdx(r.mapIdx);
				roomBuild.setMaxPlayer(r.maxPlayer);
				roomBuild.setPassword(r.password != "" ? 1 : 0);
				roomBuild.setPattern(RoomPattern.forNumber(r.mode));
				roomBuild.setPlayerCount(r.count);
				roomBuild.setRoomId(r.index);
				roomBuild.setRoomName(r.name);
				roomBuild.setRule(RoomRule.forNumber(r.rule));
				roomBuild.setVersion(MeteorVersion.forNumber(r.version));
				RoomInfo room = roomBuild.build();
				builder.addRoomInLobby(room);
			}

			SyncSvrState req = builder.build();
			Pack pak = new PackCodec.Pack(MeteorMsg.MsgType.SyncSvrState_VALUE, req.toByteArray());
			center_server.Write(pak);
    	}
	}
	
	public Player FindPlayer(int playerId)
	{
		Player p = players.get(playerId);
		if (p == null)
			p = new Player();
		return p;
	}
	//�ͻ������ӽ���
	public void OnPlayerConnect(ChannelHandlerContext ctx)
	{
		Session s = new Session(ctx);
		users.put(s.SessionId, s);
	}
	
	//�ͻ��˶Ͽ�����
	public void OnPlayerDisConnect(ChannelHandlerContext ctx)
	{
		int delete = -1;
		Set<Integer> c = users.keySet();
		for (Integer i: c)
		{
			Session s = users.get(i);
			if (s.context == ctx)
			{
				delete = i;
				break;
			}
		}
		if (delete != -1)
		{
			//�������ҽ�ɫ�����Ѵ��ڣ������뿪ս��.
			if (players.containsKey(delete))
			{
				Player p = players.get(delete);
				if (p.rom != null)
				{
					p.rom.OnPlayerLeaved(p);
				}
			}
			users.remove(delete);
		}
	}
}