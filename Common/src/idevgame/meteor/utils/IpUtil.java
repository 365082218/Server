package idevgame.meteor.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import com.alibaba.fastjson.JSON;

public class IpUtil {
	
	static String[] localIp = {//鐏烇拷閸╃喓缍塱p閻ㄥ嫯鎹ｆ慨锟�
			"10.",
			"192.168.",
			"172.16.",
			"172.17.",
			"172.18.",
			"172.19.",
			"172.20.",
			"172.21.",
			"172.22.",
			"172.23.",
			"172.24.",
			"172.25.",
			"172.26.",
			"172.27.",
			"172.28.",
			"172.29.",
			"172.30.",
			"172.31."
	};
	
	/**
	 * 
	 * @param dest_url
	 *            -- 閸︽澘娼�
	 * @param commString
	 *            -- 閸欏倹鏆�
	 * @param requestMode
	 *            -- 鐠囬攱鐪伴弬鐟扮础 POST or GET
	 * @return
	 */
	private static String connectURL(String dest_url, String commString,
			String requestMode) {
		String rec_string = "";

		URL url = null;
		HttpURLConnection urlconn = null;
		try {
			url = new URL(dest_url);
			urlconn = (HttpURLConnection) url.openConnection();
			urlconn.setRequestProperty("content-type", "text/plain");
			urlconn.setRequestMethod(requestMode);
			urlconn.setDoInput(true);
			if (requestMode.equalsIgnoreCase("POST")) {
				urlconn.setDoOutput(true);
				OutputStream out = urlconn.getOutputStream();
				out.write(commString.getBytes("UTF8"));
				out.flush();
				out.close();
			}
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
			return "";
		}
		return rec_string;
	}
	
	/**
	 * 閺勵垰鎯佺仦锟介崺鐔虹秹ip
	 * @param ip
	 * @return
	 */
	public static boolean isLocalIp(String ip) {
		//閸愬懐缍塈P閺勵垯浜掓稉瀣桨閸戠姳閲滃▓闈涚磻婢跺娈�: 
		//10.x.x.x
		//192.168.x.x
		//172.16.x.x閼凤拷172.31.x.x
		for (String ipStart : localIp) {
			if(ip.startsWith(ipStart)){//閸愬懐缍塱p娑撳秵顥呴弻锟�
//				System.out.println("閸愬懐缍塱p娑撳秵顥呴弻锟�:" + pa.ip);
				return true;
			}
		}
		
		return false;
	}

	/**
	 * 閺嶈宓乮p閼惧嘲褰囨穱鈩冧紖
	 * @param ip
	 * @param key:country/region/city...
	 * @return
	 */
	public static String getIpInfo(String ip,String key) {
		try {
			if(isLocalIp(ip)){
				return "鐏烇拷閸╃喓缍塱p:" + ip;
			}
			
			String rec = connectURL(
					"http://ip.taobao.com/service/getIpInfo.php?ip=" + ip,"", "GET");
			
			HashMap<?, ?> fromJson = JSON.parseObject(rec, HashMap.class);
			
//		System.out.println("fromJson=" + fromJson.toString());
			//fromJson={code=0, data={"area":"閸楀骸宕�","area_id":"800000","city":"閹姴绐炵敮锟�","city_id":"441300","country":"娑擃厼娴�","country_id":"CN","county":"","county_id":"-1","ip":"183.61.71.118","isp":"閻㈠吀淇�","isp_id":"100017","region":"楠炲じ绗㈤惇锟�","region_id":"440000"}}
			
			com.alibaba.fastjson.JSONObject dataMap = (com.alibaba.fastjson.JSONObject) fromJson.get("data");

			return dataMap.get(key).toString();
		} catch (Exception e) {
			e.printStackTrace();
			
			return "閺冪姵纭剁拠鍡楀焼閻ㄥ埇p:" + ip;
		}
	}
	
	/**
	 * getLocalHostName
	 * @return
	 */
	public static String getLocalHostName() {
		String hostName;
		try {
			InetAddress addr = InetAddress.getLocalHost();
			hostName = addr.getHostName();
		} catch (Exception ex) {
			hostName = "";
		}
		return hostName;
	}

	/**
	 * 閼惧嘲褰囬張顒�婀撮幍锟介張濉眕,閸栧懏瀚径姘辩秹閸楋拷
	 * @return
	 */
	public static String[] getAllLocalHostIPv4() {
		System.out.println("瀵拷婵骞忛崣鏉媝閸︽澘娼�:");
		List<String> ipList = new ArrayList<>();
		try {
			Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
		    while (netInterfaces.hasMoreElements()) {
		    	NetworkInterface ni = netInterfaces.nextElement();//缂冩垹绮堕幒銉ュ經
		    	if(ni.isLoopback() || ni.isPointToPoint() || !ni.isUp() || ni.isVirtual()){
		    		System.out.println("閺冪姵鏅ョ純鎴濆幢:" + ni.getDisplayName() 
		    				+ ",isLoopback()=" + ni.isLoopback()
		    				+ ",isPointToPoint()=" + ni.isPointToPoint()
		    				+ ",isUp()=" + ni.isUp()
		    				+ ",isVirtual()=" + ni.isVirtual()
		    				);
		    		continue;
		    	}
//		        System.out.println("DisplayName:" + ni.getDisplayName());
//		        System.out.println("Name:" + ni.getName());  
		        Enumeration<InetAddress> ips = ni.getInetAddresses();
		        while (ips.hasMoreElements()) {
		        	InetAddress ip = ips.nextElement();
		        	if(isIpv6(ip)){
		        		continue;
		        	}
		        	ipList.add(ip.getHostAddress());
					System.out.println("ip=" + ip);
		        }  
		    }
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		String[] ret = null;
		if(ipList.size() > 0){
			ret = new String[ipList.size()];
			for (int i = 0; i < ret.length; i++) {
				ret[i] = ipList.get(i);
			}
		}else{
			System.out.println("閼惧嘲褰噄p閸︽澘娼冩径杈Е!");
		}
		
		return ret;
	}

	/**
	 * 閸掋倖鏌囬弰顖欑瑝閺勭棤pv6
	 * @param ip
	 * @return
	 */
	private static boolean isIpv6(InetAddress ip) {
		return (ip instanceof Inet6Address);
	}

	public static void main(String[] args) {
//		System.out.println("ip=" + getIpInfo("121.14.199.236","city"));
		String[] ips = getAllLocalHostIPv4();
		if(ips != null){
			for (String ip : ips) {
				System.out.println("ip=" + ip);
			}
		}
	}

}
