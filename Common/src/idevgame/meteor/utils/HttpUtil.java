package idevgame.meteor.utils;

import java.io.BufferedReader;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
public class HttpUtil {
	public String connectURL(String dest_url, String commString) {
		String rec_string = "";

		URL url = null;
		HttpURLConnection urlconn = null;
		try {
			url = new URL(dest_url);
			urlconn = (HttpURLConnection) url.openConnection();
			urlconn.setRequestProperty("content-type", "text/plain");
			urlconn.setRequestMethod("POST");
			urlconn.setDoInput(true);
			urlconn.setDoOutput(true);
			OutputStream out = urlconn.getOutputStream();
			out.write(commString.getBytes("UTF8"));
			out.flush();
			out.close();

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					urlconn.getInputStream()));
			StringBuffer sb = new StringBuffer();
			int ch;
			while ((ch = rd.read()) > -1)
				sb.append((char) ch);
			rec_string = sb.toString();
			rd.close();
			urlconn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rec_string;
	}
	
	/**
	 * 闁哄牆顦顒勫极閹殿喗鐣盚TTP POST閻犲洭鏀遍惇锟�
	 * @param urlPath HTTP闁规亽鍎辫ぐ锟�
	 * @param obj 閻犲洭鏀遍惇浼村矗閸屾稒娈跺☉鎾剁槷SON閻庣數顢婇挅锟�
	 * @return
	 */
	  public static String loadJSON(String urlPath,String obj) {
		  String jsonData = null;
	        try {
	            //闁告帗绋戠紓鎾存交閻愭潙澶�
	            URL url = new URL(urlPath);
	            HttpURLConnection connection = (HttpURLConnection) url
	                    .openConnection();
	            connection.setDoOutput(true);
	            connection.setDoInput(true);
	            connection.setRequestMethod("POST");
	            connection.setUseCaches(false);
	            connection.setInstanceFollowRedirects(true);
	            connection.setRequestProperty("Content-Type",
	                    "application/x-www-form-urlencoded;charset=utf-8");
	            connection.connect();
	            //POST閻犲洭鏀遍惇锟�
	            DataOutputStream out = new DataOutputStream(
	                    connection.getOutputStream());
//	            out.writeBytes(obj.toString());
	            out.write(obj.getBytes("utf-8"));
	            out.flush();
	            out.close();
	            //閻犲洩顕цぐ鍥传瀹ュ懐瀹�
	            BufferedReader reader = new BufferedReader(new InputStreamReader(
	                    connection.getInputStream(),"utf-8"));
	            String lines;
	            StringBuffer sb = new StringBuffer("");
	            while ((lines = reader.readLine()) != null) {
//	                lines = new String(lines.getBytes(), "utf-8");
	                sb.append(lines);
	            }
	            jsonData = sb.toString();
	            reader.close();
	            // 闁哄偆鍘肩槐鎴炴交閻愭潙澶�
	            connection.disconnect();
	        } catch (MalformedURLException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        } catch (UnsupportedEncodingException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        return jsonData;
	    }

	public static void main(String[] args) {
		/**
		 * 濞寸姰鍎扮粭鍛圭�ｎ厾妲稿☉鎾跺劋椤掓粌顕ｈ箛鏇炵畾濠⒀冨剱P闁挎稑鏈崢褔鎮介敓锟�
		 */
		// String key = "029FE15D2751134CF165ADBCC4DB3722";
		// HttpUtil httpUtil = new HttpUtil();
		// String p =
		// "data={\"act\":1002,\"sendtime\":0,\"gameid\":10,\"channelid\":0,\"subchannelid\":0,\"msgcontent\":\"闁哄啠鏅欑粭鍌涚附閺傘倗纾奸柨娑虫嫹123\"}";
		//
		// String p2 =
		// "{\"act\":1002,\"sendtime\":0,\"gameid\":10,\"channelid\":0,\"subchannelid\":0,\"msgcontent\":\"闁哄啠鏅欑粭鍌涚附閺傘倗纾奸柨娑虫嫹123\"}";
		//
		// String sign = MD5Util.getMD5Str(p2 + key);
		// System.out.println("p=" + p);
		// System.out.println("sign=" + sign);
		// System.out.println("閻犲洭鏀遍惇浼存儍閸曨偄妫橀柡渚婃嫹=" + p + "sign=" + sign);
		// String r = httpUtil
		// .loadJSON("http://10.6.8.201:8080/worldonline2/w2p/input.cp",
		// p + "&sign=" + sign);
		// System.out.println("闁告繂绉寸花鏌ユ儍閸曨偄妫橀柡渚婃嫹=" + r);

//		HttpUtil httpUtil = new HttpUtil();
//		String sbf = MD5Util
//				.getMD5Str("{\"cmdId\":10001,\"data\":{\"serverid\":1,\"actorid\":1,\"accountid\":1,\"actorname\":\"婵炴潙顑堥惁顖滄喆閹烘洖顥忛柛姘〉",\"questiontype\":1,\"sendtime\":1425623610023,\"theme\":\"QQ:381405335\",\"content\":\"婵炴潙顑堥惁顖炲礃閸涱収鍟嘰"}}868ECEE5B052E25D0B7CA2E79FE4940F");
//		System.out
//				.println("{\"cmdId\":10001,\"data\":{\"serverid\":1,\"actorid\":1,\"accountid\":1,\"actorname\":\"婵炴潙顑堥惁顖滄喆閹烘洖顥忛柛姘〉",\"questiontype\":1,\"sendtime\":1425623610023,\"theme\":\"QQ:381405335\",\"content\":\"婵炴潙顑堥惁顖炲礃閸涱収鍟嘰"}}");
//		System.out.println("sig====" + sbf);
//		String obj = "{\"cmdId\":10001,\"data\":{\"serverid\":1,\"actorid\":1,\"accountid\":1,\"actorname\":\"婵炴潙顑堥惁顖滄喆閹烘洖顥忛柛姘〉",\"questiontype\":1,\"sendtime\":1425623610023,\"theme\":\"QQ:381405335\",\"content\":\"婵炴潙顑堥惁顖炲礃閸涱収鍟嘰"},\"sig\":\""
//				+ sbf + "\"}";
//		String url = "http://advertiser.inmobiapis.com/tpce/v1/events/download?trackingPartner=gude&propertyId=46a0703e9b844c9398bf9538a37c1603&impId=123";
//		System.out.println(httpUtil.loadJSON(url, ""));

//yzq
//		HttpUtil httpUtil = new HttpUtil();
//		String sbf = MD5Util
//				 .getMD5Str("{\"cmdId\":10001,\"data\":{\"serverid\":1,\"actorid\":1,\"accountid\":1,\"actorname\":\"婵炴潙顑堥惁顖滄喆閹烘洖顥忛柛姘〉",\"questiontype\":1,\"sendtime\":1425623610023,\"theme\":\"QQ:381405335\",\"content\":\"婵炴潙顑堥惁顖炲礃閸涱収鍟嘰"}}868ECEE5B052E25D0B7CA2E79FE4940F");
//		System.out
//				 .println("{\"cmdId\":10001,\"data\":{\"serverid\":1,\"actorid\":1,\"accountid\":1,\"actorname\":\"婵炴潙顑堥惁顖滄喆閹烘洖顥忛柛姘〉",\"questiontype\":1,\"sendtime\":1425623610023,\"theme\":\"QQ:381405335\",\"content\":\"婵炴潙顑堥惁顖炲礃閸涱収鍟嘰"}}");
//		System.out.println("sig====" + sbf);
//		String obj = "{\"cmdId\":10001,\"data\":{\"serverid\":1,\"actorid\":1,\"accountid\":1,\"actorname\":\"婵炴潙顑堥惁顖滄喆閹烘洖顥忛柛姘〉",\"questiontype\":1,\"sendtime\":1425623610023,\"theme\":\"QQ:381405335\",\"content\":\"婵炴潙顑堥惁顖炲礃閸涱収鍟嘰"},\"sig\":\""
//				 + sbf + "\"}";
//		String url = "http://10.6.8.201:8080/worldonline2/w2p/input.cp";
//		System.out.println(httpUtil.loadJSON(url, obj));
	}
	
	
	
	
}
