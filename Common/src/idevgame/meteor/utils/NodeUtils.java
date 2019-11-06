package idevgame.meteor.utils;

import org.w3c.dom.Node;

public class NodeUtils {

	/**
	 * 根据key获得node里面值，返回int
	 * @param node
	 * @param key
	 * @return int
	 */
	public static int getNamedItemResultInt(Node node, String key){
		Node value = node.getAttributes().getNamedItem(key);
		if(value == null){
			return 0;
		}
		return Integer.parseInt(value.getNodeValue());
	}
	
	/**
	 * 根据key获得node里面值，返回boolean
	 * @param node
	 * @param key
	 * @return boolean
	 */
	public static boolean getNamedItemResultBoolean(Node node, String key){
		Node value = node.getAttributes().getNamedItem(key);
		if(value == null){
			return false;
		}
		return Boolean.parseBoolean(value.getNodeValue());
	}
	
	/**
	 * 根据key获得node里面值，返回float
	 * @param node
	 * @param key
	 * @return float
	 */
	public static float getNamedItemResultFloat(Node node, String key){
		Node value = node.getAttributes().getNamedItem(key);
		if(value == null){
			return 0;
		}
		return Float.parseFloat(value.getNodeValue());
	}
}
