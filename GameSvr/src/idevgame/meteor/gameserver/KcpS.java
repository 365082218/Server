package idevgame.meteor.gameserver;
import io.netty.buffer.ByteBuf;
import java.nio.charset.Charset;
import idevgame.meteor.gameserver.jkcp.KcpOnUdp;
import idevgame.meteor.gameserver.jkcp.KcpServer;

/**
 * @author beykery
 */
public class KcpS extends KcpServer {

    public KcpS(int port, int workerSize) {
        super(port, workerSize);
    }

    @Override
    public void handleReceive(ByteBuf bb, KcpOnUdp kcp) {
    	int playerId = kcp.getKcp().getConv();
    	Player player = GameSvr.Instance.FindPlayer(playerId);
    	if (player != null)
    		player.onReceive(kcp, bb);
    }

    @Override
    public void handleException(Throwable ex, KcpOnUdp kcp) {
        System.out.println(ex);
    }

    @Override
    public void handleClose(KcpOnUdp kcp, int errorCode) {
//        System.out.println("closed" + kcp);
//        System.out.println("waitSnd:" + kcp.getKcp().waitSnd());
//        System.out.println("conv:" + kcp.getKcp().getConv());
        int playerId = kcp.getKcp().getConv();
    	Player player = GameSvr.Instance.FindPlayer(playerId);
    	if (player != null)
    		player.onKcpClosed(kcp, errorCode);
    }
}
