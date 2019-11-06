package idevgame.meteor.utils;
import java.io.File;  
import java.io.IOException;  
import java.util.ArrayList;  

import javax.xml.parsers.DocumentBuilder;  
import javax.xml.parsers.DocumentBuilderFactory;  
import javax.xml.parsers.ParserConfigurationException;  

import org.w3c.dom.Document;  
import org.w3c.dom.Element;  
import org.w3c.dom.Node;  
import org.w3c.dom.NodeList;  
import org.xml.sax.SAXException; 

/**
 * 鐟欙絾鐎絏ML瀹搞儱鍙跨猾锟�
 * @author lc
 *
 */
public class XMLUtils {
	
	/**
	 * 娴肩姴鍙唜ml閺傚洣娆㈤惃鍕熅瀵板嫸绱濇潻鏂挎礀XML閺傚洦銆傞惃鍕壌閼哄倻鍋lement
	 * 
	 * @param path
	 *            xml閺傚洣娆㈤惃鍕熅瀵帮拷
	 * @return element
	 */
	public static Element getElementMyXML(String path) {
		return getElementMyXML(new File(path));
	}
	
	/**
	 * 娴肩姴鍙唜ml閺傚洣娆㈤惃鍕熅瀵板嫸绱濇潻鏂挎礀XML閺傚洦銆傞惃鍕壌閼哄倻鍋lement
	 * 
	 * @param path
	 *            xml閺傚洣娆㈤惃鍕熅瀵帮拷
	 * @return element
	 */
	public static Element getElementMyXML(File file) {
		Element element = null;
		if(!file.exists()){//閺傚洣娆㈡稉宥呯摠閸︼拷
			return null;
		}
		
		// documentBuilder娑撶儤濞婄挒鈥茬瑝閼崇晫娲块幒銉ョ杽娓氬瀵�(鐏忓摣ML閺傚洣娆㈡潪顒佸床娑撶瘚OM閺傚洣娆�)
		DocumentBuilder db = null;
		DocumentBuilderFactory dbf = null;

		try {
			// 瀵版鍩孌OM鐟欙絾鐎介崳銊ф畱瀹搞儱宸剁�圭偘绶�
			// 瀵版鍩宩avax.xml.parsers.DocumentBuilderFactory閿涙稓琚惃鍕杽娓氬姘ㄩ弰顖涘灉娴狀剝顩﹂惃鍕掗弸鎰珤瀹搞儱宸�
			dbf = DocumentBuilderFactory.newInstance();
			// 娴犲顶OM瀹搞儱宸堕懢宄扮繁DOM鐟欙絾鐎介崳锟�
			// 闁俺绻僯avax.xml.parsers.DocumentBuilderFactory鐎圭偘绶ラ惃鍕饯閹焦鏌熷▔鏄籩wDocumentBuilder閿涘牞绱氬妤�鍩孌OM鐟欙絾鐎介崳锟�
			db = dbf.newDocumentBuilder();
			// 瀵版鍩屾稉锟芥稉鐙M楠炴儼绻戦崶鐐电舶document鐎电钖�
			Document dt = db.parse(file);
			// 瀵版鍩孹ML閺傚洦銆傞惃鍕壌閼哄倻鍋�
			// 閸︹�昈M娑擃厼褰ч張澶嬬壌閼哄倻鍋ｉ弰顖欑娑撶尦rg.w3c.dom.Element鐎电钖勯妴锟�
			element = dt.getDocumentElement();
			// System.out.println("Element瀹歌尙绮￠懢宄板絿");
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return element;
	}

	/**
	 * 娴肩姴鍙嗛悥鎯板Ν閻愮atherNode閿涘矁绻戦崶鐐村閺堝鐡欓懞鍌滃仯childNode閺�鎯у弳ArrayList娑擄拷
	 * 
	 * @param element
	 * @return ArrayList
	 */
	public static ArrayList<Node> getAllChildNodesMyXML(NodeList fatherNode) {
		ArrayList<Node> al = new ArrayList<Node>();
		// 闁秴宸婚幍锟介張澶庡Ν閻愶拷
		for (int i = 0; i < fatherNode.getLength(); i++) {
			Node childNode = fatherNode.item(i);
			// 婵″倹鐏夌�涙劘濡悙閫涜礋缁岄缚鐑︽潻锟�
			if ("#text".equals(childNode.getNodeName())) {
				continue;
			}
			// 鐎涙劘濡悙瑙勬箒閸愬懎顔愰敍灞藉閸忋儱鍩宎rraylist娑擄拷
			al.add(childNode);
		}
		return al;
	}

	/**
	 * 娴肩姴鍙嗛悥鎯板Ν閻愮atherNode閿涘苯鎷扮�涙顑佹稉鐬me閿涘矁绻戦崶鐐茬摍閼哄倻鍋ｉ崥宥呯摟缁涘绨琻ame閻ㄥ垻hildNode閺�鎯у弳ArrayList娑擄拷
	 * 
	 * @param element
	 * @param name
	 * @return ArrayList
	 */
	public static ArrayList<Node> getChildNodesMyXML(NodeList fatherNode, String name) {
		ArrayList<Node> al = new ArrayList<Node>();
		// 闁秴宸婚幍锟介張澶庡Ν閻愶拷
		for (int i = 0; i < fatherNode.getLength(); i++) {
			Node childNode = fatherNode.item(i);
			// System.out.println(fatherNode.getLength());
			// System.out.println(childNode.getNodeName());
			// 婵″倹鐏夌�涙劘濡悙鍦畱閸氬秴鐡х粵澶夌艾name
			if (name.equals(childNode.getNodeName())) {
				// 閸旂姴鍙嗛崚鐧畆raylist娑擄拷
				al.add(childNode);
			}
		}
		return al;
	}

	/**
	 * 娴肩姴鍙嗛悥鎯板Ν閻愮atherNode閿涘苯鎷扮�涙顑佹稉鐬me閿涘矁绻戦崶鐐茬摍閼哄倻鍋ｇ仦鐐达拷顪tributes缁涘绨琻ame閻ㄥ垻hildNode閺�鎯у弳ArrayList娑擄拷
	 * 
	 * @param fatherNode
	 * @param attributes
	 * @param name
	 * @return
	 */
	public static ArrayList<Node> getChildNodesMyXMLAttributes(NodeList fatherNode,
			String attributes, String name) {
		ArrayList<Node> al = new ArrayList<Node>();
		// 闁秴宸婚幍锟介張澶庡Ν閻愶拷
		for (int i = 0; i < fatherNode.getLength(); i++) {
			Node childNode = fatherNode.item(i);
			System.out.println(childNode.getNodeName());
			try {
				// 婵″倹鐏夌�涙劘濡悙鍦畱鐏炵偞锟筋湩ttributes缁涘绨琻ame閿涘苯鍨崝鐘插弳閸掔櫘rraylist娑擄拷
				if (name.equals(childNode.getAttributes()
						.getNamedItem(attributes).getNodeValue())) {
					al.add(childNode);
				}
			} catch (NullPointerException e) {
				// System.out.println("鐠囥儴濡悙瑙勭梾閺堝鐫橀幀锟�");
			}
		}
		return al;
	}
	
	public static void main(String[] args) {  
        //婢圭増妲慩ML閸︽澘娼冮敍灞肩瑝婵夘偄鍟撶捄顖氱窞閻ㄥ嫯鐦芥妯款吇娑撳搫浼愮粙瀣瀮娴犺泛銇欓弽鍦窗瑜版洑绗�  
        String path = "/E:/pan/work/workSpace/good_Eclipse44/world2-server/game-server/bin/ai/ai_1002.xml";
        //閼惧嘲绶盓lement鐎电钖�  
        Element element = getElementMyXML(path);
        
        NodeList fatherNode = element.getChildNodes();
        
        ArrayList<Node> arrayList = getAllChildNodesMyXML(fatherNode);
        for (int i = 0; i < arrayList.size(); i++) {
        	ArrayList<Node> arrayList2 = getAllChildNodesMyXML(arrayList.get(i).getChildNodes());
        	for (int j = 0; j < arrayList2.size(); j++) {
        		System.out.println(arrayList2.get(j).getNodeName());
				System.out.println(arrayList2.get(j).getAttributes().getNamedItem("random").getNodeValue());
			}
		}
		// 闁秴宸婚幍锟介張澶庡Ν閻愶拷
//		for (int i = 0; i < fatherNode.getLength(); i++) {
//			
//			Node childNode = fatherNode.item(i);
//			
//			// 婵″倹鐏夌�涙劘濡悙閫涜礋缁岄缚鐑︽潻锟�
//			if ("#text".equals(childNode.getNodeName())) {
//				continue;
//			}
//			
//			System.out.println(childNode.getNodeName());
//			System.out.println("-------------");
//			NodeList fatherNode2 =childNode.getChildNodes();
//			for (int j = 0; j < fatherNode2.getLength(); j++) {
//				Node childNode2 = fatherNode2.item(j);
//				// 婵″倹鐏夌�涙劘濡悙閫涜礋缁岄缚鐑︽潻锟�
//				if ("#text".equals(childNode2.getNodeName())) {
//					continue;
//				}
//				
//				System.out.println(childNode2.getNodeName());
//				
//				NodeList fatherNode3 =childNode2.getChildNodes();
//				for (int k = 0; k < fatherNode3.getLength(); k++) {
//					Node childNode3 = fatherNode3.item(k);
//					// 婵″倹鐏夌�涙劘濡悙閫涜礋缁岄缚鐑︽潻锟�
//					if ("#text".equals(childNode3.getNodeName())) {
//						continue;
//					}
//					System.out.println(childNode3.getNodeName());
//					System.out.println(childNode3.getAttributes().getNamedItem("precent_min_hp").getNodeValue());
//				}
//				
//			}
//			
//			System.out.println("閼哄倻鍋ｉ柆宥呭坊缂佹挻娼�");
//
//		}
        
        
        
        
        //閼惧嘲绶眛ype缁涘绨琌racle閻ㄥ嫬鐡欓懞鍌滃仯閺�鎯ф躬al娑擄拷  
//        ArrayList<Node> al = myXML.getChildNodesMyXMLAttributes(element.getChildNodes(), "type", "1");  
//        ArrayList<Node> al2 = null;  
          
//        NodeList list = element.getElementsByTagName("state");
//        System.out.println(list.getLength());
//        for (int i = 0; i < list.getLength(); i++) {
//			System.out.println(list.item(i).getNodeName());
//			System.out.println(list.item(i).getAttributes().getNamedItem("priority").getNodeValue());
//		}
//        System.out.println("缁楊兛绔寸痪褑濡悙锟�" + al.size());  
//        for(int i =0;i<al.size();i++){  
//            System.out.println("缁楊兛绔寸痪褑濡悙鐟版倳鐎涙ぞ璐熼敍锟�"+al.get(i).getNodeName());  
//            System.out.println("缁楊兛绔寸痪褑濡悙鐟扮潣閹満ype娑撶尨绱�"+al.get(i).getAttributes().getNamedItem("type").getNodeValue());  
//            System.out.println("缁楊兛绔寸痪褑濡悙鍦畱鐎涙劘濡悙閫涜礋"+al.get(i).getTextContent());  
//        }  
//          
//        //閸欐牕绶辩粭顑跨癌缁狙冪摍閼哄倻鍋ｉ崥宥呯摟娑撶皫ser閻ㄥ嫯濡悙锟�  
//        al2 = myXML.getChildNodesMyXML(al.get(0).getChildNodes(),"user");  
//          
//        System.out.println("缁楊兛绨╃痪褑濡悙锟�");  
//        for(int i =0;i<al.size();i++){  
//            System.out.println("缁楊兛绨╃痪褑濡悙鐟版倳鐎涙ぞ璐熼敍锟�"+al2.get(i).getNodeName());  
//            System.out.println("缁楊兛绨╃痪褑濡悙鍦畱鐎涙劘濡悙閫涜礋"+al2.get(i).getTextContent());  
//        }  
          
        //System.out.println(al);  
        //System.out.println(al2);  
  
    }  
	

}






