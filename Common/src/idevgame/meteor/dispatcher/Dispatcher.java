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
 *协议转发器
 */
public class Dispatcher {

	private static Logger logger = LoggerFactory.getLogger(Dispatcher.class);

	public static class Commander {
		private final Object o;
		private final Method method;
		private final Method protobufParser;
		//第1个参数永远是ChannelHandlerContext
		//如果协议无参数，则paramNum为0，表明这个协议无参数，只有协议号，转发给对应的无参协议处理函数即可
		//如果协议有参数，则按照协议处理类型，转发给不同的处理
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

		String err = null;
		for (Class cls : classes) {
			try {
				Object o = cls.newInstance();
				Method[] methods = cls.getDeclaredMethods();

				for (Method method : methods) {
					CMD cmd = method.getAnnotation(CMD.class);
					if(cmd != null) {
						if(newCommanders.get(cmd.id()) != null){
							err = "协议处理重复cmd.id = "+cmd.id();
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
	 *执行协议处理函数
	 */
	public PackCodec.Pack invoke(ChannelHandlerContext ctx, int cmd, byte[] bytes) throws Exception {
		Commander commander = commanders.get(cmd);
		if(commander != null) {
			long begin = System.currentTimeMillis();
			GeneratedMessageV3 params = null;
			PackCodec.Pack res = null;
			if (commander.protobufParser != null)
			{
				params = (GeneratedMessageV3)commander.protobufParser.invoke(null, bytes);
				res = (PackCodec.Pack) commander.method.invoke(commander.o, ctx, params);
			}
			else
			{
				res = (PackCodec.Pack) commander.method.invoke(commander.o, ctx);
			}
			long used = System.currentTimeMillis() - begin;

			logger.debug("协议id:[{}]执行耗时:{}", cmd, used);

			if (used > 1000) {
				logger.error("协议耗时超过1秒[{}]!!!{}", cmd, used);
			}

			return res;
		}
		return null;
	}
	
	public PackCodec.Pack invoke(long channelId, int cmd, byte[] bytes) throws Exception {
		Commander commander = commanders.get(cmd);
		if(commander != null) {
			long begin = System.currentTimeMillis();
			
			GeneratedMessageV3 params = (GeneratedMessageV3)commander.protobufParser.invoke(null, bytes);
			
			logger.debug("鏀跺埌鍗忚[{}], data={}", cmd, TextFormat.shortDebugString(params));
			
			PackCodec.Pack res = (PackCodec.Pack) commander.method.invoke(commander.o, channelId, params);
			
			long used = System.currentTimeMillis() - begin;
			
			logger.debug("鍗忚[{}]澶勭悊瀹屾垚锛岃�楁椂{}ms", cmd, used);
			
			// 鍗忚澶勭悊瓒呰繃1绉�
			if (used > 1000) {
				logger.error("鍗忚[{}]澶勭悊鎱�!!!鑰楁椂{}ms", cmd, used);
			}
			
			return res;
		}
		return null;
	}
	
	/**
	 * 鍗忚璋冪敤:fightserver鐢�
	 */
	public PackCodec.Pack invoke(long channelId, long playerId ,int cmd, byte[] bytes) throws Exception {
		Commander commander = commanders.get(cmd);
		if(commander != null) {
			long begin = System.currentTimeMillis();
			
			GeneratedMessage params = (GeneratedMessage)commander.protobufParser.invoke(null, bytes);
			
			logger.debug("鏀跺埌鍗忚[{}], data={}", cmd, TextFormat.shortDebugString(params));
			
			PackCodec.Pack res = (PackCodec.Pack) commander.method.invoke(commander.o, channelId,playerId, params);
			
			long used = System.currentTimeMillis() - begin;
			
			logger.debug("鍗忚[{}]澶勭悊瀹屾垚锛岃�楁椂{}ms", cmd, used);
			
			// 鍗忚澶勭悊瓒呰繃1绉�
			if (used > 1000) {
				logger.error("鍗忚[{}]澶勭悊鎱�!!!鑰楁椂{}ms", cmd, used);
			}
			
			return res;
		}
		return null;
	}
	
	

	public Commander getCommander(int cmd){
		return commanders.get(cmd);
	}
}
