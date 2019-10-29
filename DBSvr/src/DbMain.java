
import java.net.InetSocketAddress;
import java.util.Vector;
import io.netty.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.channel.nio.NioEventLoopGroup;
public class DbMain {
	public static void main(String[] ags) throws Exception {
		new Server().start();
	}
}

class ServerHandler extends ChannelInboundHandlerAdapter
{
	private static final int maxconn = 50;
	private int connetcNum = 0;
	private Vector<ChannelHandlerContext> contexts = new Vector<>(2);
	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // TODO Auto-generated method stub
        String in = (String) msg;
        //System.out.println(getTime() + " �ͻ���" + ctx.channel().remoteAddress() + ":" + in);
        //��ֻ��һ������ʱ������֪ͨ
        if (contexts.size() < 2) {
            ctx.writeAndFlush("�Է�������");
            return;
        }
        //��ȡ��һ��channelhandlercontxt���±�
        int currentIndex = contexts.indexOf(ctx);
        int anotherIndex = Math.abs(currentIndex - 1);
        //����һ���ͻ���ת����Ϣ
        contexts.get(anotherIndex).writeAndFlush(in);
    }
}

class Server
{
	public Server()
	{
		
	}
	
	public void start() throws InterruptedException {
        ServerHandler sHandler = new ServerHandler();
        InetSocketAddress localSocket = new InetSocketAddress("127.0.0.1", 9990);
        ServerBootstrap b = new ServerBootstrap();
        b.group(new NioEventLoopGroup())
        .localAddress(localSocket)
        .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        //���������������
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new StringEncoder());
                        ch.pipeline().addLast(sHandler);
                    }

                });
        final ChannelFuture f = b.bind().sync();
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture f) throws Exception {
                // TODO Auto-generated method stub
                if (f.isSuccess()) {
                    System.out.println("�����������ɹ�");
                } else {
                    System.out.println("����������ʧ��");
                    f.cause().printStackTrace();
                }
            }
        });
    }
}