package idevgame.meteor.utils;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.compress.utils.IOUtils;
import com.sun.net.httpserver.HttpExchange;

public class HttpUtils {
	
	private static final String URL_PARAM_CONNECT_FLAG = "&";
	/**
	 * 
	 * @param httpUr 鐠囬攱鐪�
	 * @param saveFile 娣囨繂鐡ㄩ弬鍥︽
	 * @throws IOException
	 */
	public static void httpDownload(String httpUrl, String saveFile)
			throws IOException {
		URL url = new URL(httpUrl);
		URLConnection conn = url.openConnection();
		try (InputStream inStream = conn.getInputStream();
				FileOutputStream fs = new FileOutputStream(saveFile);) {
			IOUtils.copy(inStream, fs);
		} 
	}
	
	
	/**
	 * 
	 * @param httpUrl
	 * @return
	 * @throws IOException
	 */
	public static InputStream httpDownload(String httpUrl) throws IOException {
		URL url = new URL(httpUrl);
		URLConnection conn = url.openConnection();
		InputStream inStream = conn.getInputStream();
		return inStream;
	}
	
	/**
	 * get 鐠囬攱鐪�
	 * @param httpUrl 鏉╃偞甯�
	 * @return
	 * @throws IOException 
	 */
	public static String httpPost(String httpUrl, String param) throws IOException {
		URL url = new URL(httpUrl);
		URLConnection conn = url.openConnection();
        // 閸欐垿锟戒赋OST鐠囬攱鐪拌箛鍛淬�忕拋鍓х枂婵″倷绗呮稉銈堫攽
        conn.setDoOutput(true);
        conn.setDoInput(true);
        // 閼惧嘲褰嘦RLConnection鐎电钖勭�电懓绨查惃鍕翻閸戠儤绁�
		try (OutputStream outputStream = conn.getOutputStream(); PrintWriter out = new PrintWriter(outputStream);) {
			// 閸欐垿锟戒浇顕Ч鍌氬棘閺侊拷
			out.print(param);
			// flush鏉堟挸鍤ù浣烘畱缂傛挸鍟�
			out.flush();
		}
		
		try (InputStream input = conn.getInputStream();) {
			byte[] datas = IOUtils.toByteArray(input);
			String string = new String(datas);
			return string;
		}
	}
	
	/**
	 * 
	 * @param strUrl
	 * @param map 閸欏倹鏆�
	 * @param decodeCharset 缂傛牜鐖渦tf8 
	 * @param timeOut 鐡掑懏妞�
	 * @return
	 * @throws IOException 
	 */
	public static String URLPost(String strUrl, Map<String, String> map,
			String decodeCharset, int timeOut) throws IOException {
		String content = "";
		content = getUrl(map);
		URL url = new URL(strUrl);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setDoInput(true);
		con.setDoOutput(true);
		con.setAllowUserInteraction(false);
		con.setUseCaches(false);
		con.setRequestMethod("POST");
		con.setConnectTimeout(timeOut);//  鐡掑懏妞傜拋鍓х枂
		con.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded;charset="
						+ decodeCharset);
		try (OutputStream outputStream = con.getOutputStream(); PrintWriter out = new PrintWriter(outputStream)) {
			out.write(new String(content.getBytes("UTF-8")));
			out.flush();
		}
		
		try (InputStream input = con.getInputStream();) {
			byte[] datas = IOUtils.toByteArray(input);
			String string = new String(datas);
			return string;
		} finally {
			con.disconnect();
		}
	}

	/**
	 * 閹疯壈顥婇崣鍌涙殶
	 * @param map
	 * @return
	 */
	public static String getUrl(Map<String, String> map) {
		if (null == map || map.keySet().size() == 0) {
			return ("");
		}
		StringBuffer url = new StringBuffer();
		Set<String> keys = map.keySet();
		for (Iterator<String> i = keys.iterator(); i.hasNext();) {
			String key = String.valueOf(i.next());
			if (map.containsKey(key)) {
				Object val = map.get(key);
				String str = val != null ? val.toString() : "";
				try {
					str = URLEncoder.encode(str, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				url.append(key).append("=").append(str).append(
						URL_PARAM_CONNECT_FLAG);
			}
		}
		String strURL = "";
		strURL = url.toString();
		if (URL_PARAM_CONNECT_FLAG.equals(""
				+ strURL.charAt(strURL.length() - 1))) {
			strURL = strURL.substring(0, strURL.length() - 1);
		}
		return (strURL);
	}
	
	
	/**
	 * GET METHOD
	 * 
	 * @param strUrl
	 *            String
	 * @param map
	 *            Map
	 * @throws IOException
	 * @return List
	 */
	public static List<String> URLGet(String strUrl, Map<String, String> map,
			int timeOut) throws IOException {
		String strtTotalURL = "";
		List<String> result = new ArrayList<String>();
		if (strtTotalURL.indexOf("?") == -1) {
			strtTotalURL = strUrl + "?" + getUrl(map);
		} else {
			strtTotalURL = strUrl + "&" + getUrl(map);
		}
		System.out.println("strtTotalURL:" + strtTotalURL);
		URL url = new URL(strtTotalURL);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setUseCaches(false);
		HttpURLConnection.setFollowRedirects(true);
		con.setConnectTimeout(timeOut);// 30缁夋帟绉�
		BufferedReader in = new BufferedReader(new InputStreamReader(con
				.getInputStream(), "utf8"));
		while (true) {
			String line = in.readLine();
			if (line == null) {
				break;
			} else {
				result.add(line);
				System.out.println(line);
			}
		}
		in.close();
		con.disconnect();
		return result;
	}
	
	
	public static void parseGetParameters(HttpExchange exchange,
			String decodeCharset) throws UnsupportedEncodingException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		URI requestedUri = exchange.getRequestURI();
		String query = requestedUri.getRawQuery();
//		logger.info("getData = " + query);
		parseQuery(query, parameters, decodeCharset);
		exchange.setAttribute("parameters", parameters);
	}

	public static void parsePostParameters(HttpExchange exchange,
			String decodeCharset) throws IOException {
		if ("post".equalsIgnoreCase(exchange.getRequestMethod())) {
			@SuppressWarnings("unchecked")
			Map<String, Object> parameters = (Map<String, Object>) exchange
					.getAttribute("parameters");
			InputStreamReader isr = new InputStreamReader(exchange
					.getRequestBody(), decodeCharset);
			BufferedReader br = new BufferedReader(isr);
			String query = br.readLine();
//			logger.info("postData = " + query);
			parseQuery(query, parameters, decodeCharset);
		}
	}
	
	
	public static void parseQuery(String query, Map<String, Object> parameters,
			String decodeCharset) throws UnsupportedEncodingException {
		if (query != null) {
			String pairs[] = query.split("[&]");
			for (String pair : pairs) {
				String param[] = pair.split("[=]");

				String key = null;
				String value = null;
				if (param.length > 0) {
					key = URLDecoder.decode(param[0], decodeCharset);
				}
				if (param.length > 1) {
					value = URLDecoder.decode(param[1], decodeCharset);
				}
				if (parameters.containsKey(key)) {
					Object obj = parameters.get(key);
					if (obj instanceof List<?>) {
						List<String> values = (List<String>) obj;
						values.add(value);
					} else if (obj instanceof String) {
						List<String> values = new ArrayList<String>();
						values.add((String) obj);
						values.add(value);
						parameters.put(key, values);
					}
				} else {
					parameters.put(key, value);
				}
			}
		}
	}
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws IOException {

		String param = "areaId=1&&info=ok";
		String string = HttpUtils.httpPost("http://127.0.0.1:8080/moon2res/info.do", param);
		
	}

}
