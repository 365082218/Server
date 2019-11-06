package idevgame.meteor.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * ScriptsUtils - 閼存碍婀板銉ュ徔缁拷
 * 
 * <p>閺嶇厧绱℃稉绨�avascript,閺�顖涘瘮閼存碍婀扮紓鎾崇摠</p>
 * @author moon
 *
 */
public class ScriptsUtils {
	private static ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");
	private static Map<String, CompiledScript> scripts = new ConcurrentHashMap<String, CompiledScript>();
	
	/**鐎涙ê鍋嶉懘姘拱
	 * 
	 * @throws javax.script.ScriptException
	 */
	public static void put(String name, String src) throws ScriptException {
		Compilable compilable = (Compilable) engine;
		CompiledScript script = compilable.compile(src);
		scripts.put(name, script);
	}
	
	/**
	 * 濞夈劍鍓伴崣鍌涙殶缁鐎烽崣顖欎簰閺勭棷ublic class or public static class
	 * 
	 * @param name 閼存碍婀伴崥锟�
	 * @param params 閸欏倹鏆�
	 * @return 閼存碍婀伴幍褑顢戠紒鎾寸亯
	 * @throws javax.script.ScriptException
	 */
	public static Object eval(String name, Map<String, Object> params) throws ScriptException {
		CompiledScript script = scripts.get(name);
		if(script == null) {
			throw new ScriptException("no such methor!");
		}
		
		Bindings b = engine.createBindings();
		if(params!=null){
			b.putAll(params);
		}
		return script.eval(b);
	}
	
}
