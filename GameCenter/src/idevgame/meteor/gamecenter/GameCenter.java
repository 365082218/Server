package idevgame.meteor.gamecenter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import idevgame.meteor.netty4.Netty4SocketServer;
import idevgame.meteor.utils.ClassPathScanner;
import idevgame.meteor.utils.PropertiesUtil;
import io.netty.channel.ChannelHandlerContext;
import idevgame.meteor.dispatcher.Dispatcher;


//处理客户端链接
//主动连接到其他服务器
//早期版本不打算要中心服，先把游戏服做好，后期扩展规模再实现
public class GameCenter {
	//public static 
	private static final String prop = "center.properties";
	private static Logger logger = LoggerFactory.getLogger(GameCenter.class);
	public static PropertiesUtil center = new PropertiesUtil(prop);
	//游戏服务器
	public final static List<GameSvrInfo> server = new ArrayList<GameSvrInfo>();
	public static GameCenter Instance;
//	public static DBHelper db0;
	public static void main(String[] ags) throws Exception {
//		db0 = new DBHelper(prop);
		//扫描处理协议的类-初始化分发器
		Instance = new GameCenter();
		Instance.Init();
	}
	
	public void Init() throws Exception
	{
		Dispatcher dispatcher = new Dispatcher();
		Set<Class> classes = ClassPathScanner.scan("idevgame.meteor.gamecenter.handler", false, true, false, null);
		dispatcher.load(classes);
		Netty4SocketServer  center_server = new Netty4SocketServer(new GameCenterNetHandler(dispatcher), Integer.parseInt(center.getProperty("center.port")), 60);
		center_server.start();
	}
	
	public void OnChannelClose(ChannelHandlerContext ctx)
	{
		synchronized(server)
		{
			GameSvrInfo delete = null;
			for (int i = 0; i < server.size(); i++)
			{
				GameSvrInfo svr = server.get(i);
				if (svr.context == ctx)
				{
					delete = svr;
					break;
				}
			}
			if (delete != null)
				server.remove(delete);
		}
	}
	
	public GameSvrInfo FindGameSvr(ChannelHandlerContext ctx)
	{
		GameSvrInfo target = null;
		synchronized(server)
		{
			for (int i = 0; i < server.size(); i++)
			{
				GameSvrInfo svr = server.get(i);
				if (svr.context == ctx)
				{
					target = svr;
					break;
				}
			}
		}
		return target;
	}
}