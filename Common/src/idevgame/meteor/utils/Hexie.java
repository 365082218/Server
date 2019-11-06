package idevgame.meteor.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Hexie
 *
 * @author moon
 * @version 2.0 - 2014-06-13
 */
public class Hexie {

	private static class HexieNode {
		private boolean isEnd = false;
		private Map<Character, HexieNode> next = new HashMap<>();
	}

	private HexieNode head = new HexieNode();

	public void init(String[] strings) {
		head.next.clear();

		for (String s : strings) {
//			s = s.toLowerCase();
			char[] ss = s.toCharArray();

			HexieNode node = head;
			for (int i = 0; i < ss.length; ++i) {
				char ch = ss[i];
				HexieNode nextNode = node.next.get(ch);
				if (nextNode == null) {
					nextNode = new HexieNode();
					node.next.put(ch, nextNode);
				}
				if (i == ss.length - 1) {
					nextNode.isEnd = true;
				}
				node = nextNode;
			}
		}
	}

	private int hasShieldWord(String s, int index, HexieNode node){
		if (node.isEnd) {
			return index;
		}
		if (index >= s.length()) {
			return -1;
		}
		HexieNode nextNode = node.next.get(s.charAt(index));
		if (nextNode == null) {
			return -1;
		}
		return hasShieldWord(s, index+1, nextNode);
	}

	public boolean hasShieldWord(String s){
//		s = s.toLowerCase();

		for (int i = 0; i < s.length(); i++) {
			if(hasShieldWord(s, i, head) != -1) {
				return true;
			}
		}

		return false;
	}

	public String replaceShieldWord(String s) {
//		s = s.toLowerCase();

		char[] chs = s.toCharArray();

		for (int i = 0; i < s.length(); i++) {
			int to = hasShieldWord(s, i, head);

			if(to != -1) {
				for (int j = i; j < to; j++) {
					chs[j] = '*';
				}
				i = to;
			}
		}

		return new String(chs);
	}

	public static void main(String[] args) {
		Hexie hexie = new Hexie();

		hexie.init(new String[]{
				"fuck", "閹存垶鎼�", "p"
		});

		String[] test = {
				"fuck you","!!!fuck you","閺堝鐦洪悽锟�","ps","xx閹存垶鎼穢xxfuckxxx","3p"
		};

		for (int i = 0; i < 10000; i++) {
			for (String s : test) {
				hexie.hasShieldWord(s);
				hexie.replaceShieldWord(s);
			}
		}

		for (String s : test) {
			long begin = System.nanoTime();

			System.out.println("濠ф劒瑕�:"+s);
			System.out.println("濡拷閺屻儳绮ㄩ弸锟�:"+hexie.hasShieldWord(s));
			System.out.println("鏉╁洦鎶ら弫鍫熺亯:"+hexie.replaceShieldWord(s));

			System.out.println("閼版妞�:" + (System.nanoTime() - begin) + "ns");
			System.out.println("----------------");
		}
	}
}
