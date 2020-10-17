package idevgame.meteor.dispatcher;


import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.TextFormat;

import idevgame.meteor.net.PackCodec;
import io.netty.channel.ChannelHandlerContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;

/**
 *鍗忚杞彂鍣�
 */
public class Dispatcher {

	private static Logger logger = LoggerFactory.getLogger(Dispatcher.class);

	public static class Commander {
		private final Object o;
		private final Method method;
		private final Method protobufParser;
		//绗�1涓弬鏁版案杩滄槸ChannelHandlerContext
		//濡傛灉鍗忚鏃犲弬鏁帮紝鍒檖aramNum涓�0锛岃〃鏄庤繖涓崗璁棤鍙傛暟锛屽彧鏈夊崗璁彿锛岃浆鍙戠粰瀵瑰簲鐨勬棤鍙傚崗璁鐞嗗嚱鏁板嵆鍙�
		//濡傛灉鍗忚鏈夊弬鏁帮紝鍒欐寜鐓у崗璁鐞嗙被鍨嬶紝杞彂缁欎笉鍚岀殑澶勭悊
		public Commander(Object o, Method method, boolean extraParam) throws NoSuchMethodException {
			this.o = o;
			this.method = method;
			if (extraParam)
			{
				Class paramType = method.getParameterTypes()[1];
				this.protobufParser = paramType.getMethod("parseFrom",byte[].class);
			}
			else
			{
				this.protobufParser = null;
			}
		}
	}

	private Map<Integer, Commander> commanders = new HashMap<>();
	public synchronized void load(Collection<Class> classes){

		Map<Integer, Commander> newCommanders = new HashMap<>();
		if (classes.size() == 0){
			System.out.println("classload error");
		} else {
			System.out.println("classload ok");
		}
		String err = null;
		for (Class cls : classes) {
			try {
				Object o = cls.newInstance();
				Method[] methods = cls.getDeclaredMethods();

				for (Method method : methods) {
					CMD cmd = method.getAnnotation(CMD.class);
					if(cmd != null) {
						if(newCommanders.get(cmd.id()) != null){
							err = "鍗忚澶勭悊閲嶅cmd.id = "+cmd.id();
							logger.error(err);
						}
						newCommanders.put(cmd.id(), new Commander(o, method, method.getParameterCount() == 2));
					}
				}
			} catch (Exception e) {
				logger.error("["+cls+"]!!!",e);
			}
		}
		if(err != null){
			throw new RuntimeException(err);
		}

		commanders = newCommanders;
	}
	
	/**
	 *处理消息
	 */
	public PackCodec.Pack invoke(ChannelHandlerContext ctx, int cmd, byte[] bytes) throws Exception {
		Commander commander = commanders.get(cmd);
		if(commander != null) {
//			System.out.println("dispatcher invoke command");
			long begin = System.currentTimeMillis();
			GeneratedMessageV3 params = null;
			PackCodec.Pack res = null;
			if (commander.protobufParser != null)
			{
//				System.out.println("commander.protobufParser != null");
				params = (GeneratedMessageV3)commander.protobufParser.invoke(null, bytes);
				res = (PackCodec.Pack) commander.method.invoke(commander.o, ctx, params);
			}
			else
			{
//				System.out.println("commander.protobufParser == null");
				res = (PackCodec.Pack) commander.method.invoke(commander.o, ctx);
			}
			long used = System.currentTimeMillis() - begin;

//			logger.debug("处理消息id:[{}]消耗时间:{}", cmd, used);

			if (used > 1000) {
				logger.error("处理消息耗时超过1S{}]!!!{}", cmd, used);
			}

			return res;
		}
		else
		{
//			System.out.println("command为空");
		}
		return null;
	}
	
	public PackCodec.Pack invoke(long channelId, int cmd, byte[] bytes) throws Exception {
		Commander commander = commanders.get(cmd);
		if(commander != null) {
			long begin = System.currentTimeMillis();
			
			GeneratedMessageV3 params = (GeneratedMessageV3)commander.protobufParser.invoke(null, bytes);
			
			logger.debug("閺�璺哄煂閸楀繗顔匸{}], data={}", cmd, TextFormat.shortDebugString(params));
			
			PackCodec.Pack res = (PackCodec.Pack) commander.method.invoke(commander.o, channelId, params);
			
			long used = System.currentTimeMillis() - begin;
			
			logger.debug("閸楀繗顔匸{}]婢跺嫮鎮婄�瑰本鍨氶敍宀冿拷妤佹{}ms", cmd, used);
			
			// 閸楀繗顔呮径鍕倞鐡掑懓绻�1缁夛拷
			if (used > 1000) {
				logger.error("閸楀繗顔匸{}]婢跺嫮鎮婇幈锟�!!!閼版妞倇}ms", cmd, used);
			}
			
			return res;
		}
		return null;
	}
	
	/**
	 * 閸楀繗顔呯拫鍐暏:fightserver閻拷
	 */
	public PackCodec.Pack invoke(long channelId, long playerId ,int cmd, byte[] bytes) throws Exception {
		Commander commander = commanders.get(cmd);
		if(commander != null) {
			long begin = System.currentTimeMillis();
			
			GeneratedMessage params = (GeneratedMessage)commander.protobufParser.invoke(null, bytes);
			
			logger.debug("閺�璺哄煂閸楀繗顔匸{}], data={}", cmd, TextFormat.shortDebugString(params));
			
			PackCodec.Pack res = (PackCodec.Pack) commander.method.invoke(commander.o, channelId,playerId, params);
			
			long used = System.currentTimeMillis() - begin;
			
			logger.debug("閸楀繗顔匸{}]婢跺嫮鎮婄�瑰本鍨氶敍宀冿拷妤佹{}ms", cmd, used);
			
			// 閸楀繗顔呮径鍕倞鐡掑懓绻�1缁夛拷
			if (used > 1000) {
				logger.error("閸楀繗顔匸{}]婢跺嫮鎮婇幈锟�!!!閼版妞倇}ms", cmd, used);
			}
			
			return res;
		}
		return null;
	}
	
	

	public Commander getCommander(int cmd){
		return commanders.get(cmd);
	}
}
