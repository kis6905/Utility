package utility;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class HttpClient {
	
	public static void main (String[] agrs) {
//		String url = "https://203.235.203.220:9484/device/create";
//		String url = "https://203.235.203.220:9484/device/verify";
		String url = "https://203.235.203.220:9484/device/auth";
		run(url, "iskwon");
	}
	
	static final TrustManager tm = new X509TrustManager() { // 접속시 인증 확인해주는 Manager
		public void checkClientTrusted(X509Certificate[] chain, String authType) {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	};
	
	static final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() { 
		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};
	
	@SuppressWarnings("unchecked")
	public static void run(String address, String userId) {
		
		SSLContext sslCtx = null;
		try {
			sslCtx = SSLContext.getInstance("TLS");
			sslCtx.init(null, new TrustManager[] { tm }, new java.security.SecureRandom()); // TrustManager를 사용하도록 초기화
			HttpsURLConnection.setDefaultSSLSocketFactory(sslCtx.getSocketFactory());

			URL url = new URL(address);
			HttpURLConnection conn;
			if (url.getProtocol().toLowerCase().equals("https")) {
				HttpsURLConnection https = (HttpsURLConnection)url.openConnection(); 
				https.setHostnameVerifier(DO_NOT_VERIFY); 
				conn = https; 
			}
			else {
				conn = (HttpURLConnection)url.openConnection();
			}

			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			
//			String bodyStr = "userId=" + userId;
			
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("serialKey", "RSM012G2V5GQ40OHVJQ1DQMFE");
			jsonObj.put("macAddress", "AA:AA:AA:AA:AA:AA");
//			jsonObj.put("manufacturerCode", "RSM");
//			jsonObj.put("deviceModelCode", "01");
			jsonObj.put("typeCode", 1);
			
			OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
			writer.write(jsonObj.toString());
//			writer.write(bodyStr);
			writer.flush();

			
			// 응답이 json일 때
			int responseCode = conn.getResponseCode();
			if (responseCode == 200) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
				String line = "";
				StringBuffer resJsonBuffer = new StringBuffer();
				while ((line = reader.readLine()) != null)
					resJsonBuffer.append(line);
				
				writer.close();
				reader.close();
				
				JSONObject resJson = (JSONObject) new JSONParser().parse(resJsonBuffer.toString());
				System.out.println(resJson);
			}
			else {
				System.out.println("Error!! (" + responseCode + ")");
			}
			
			
			/*
			 *  파일 받기
			// 받은 파일 쓰기
			InputStream is = conn.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			
			String fileName = "logo.png";
			FileOutputStream fw = new FileOutputStream(fileName);
			byte[] buff = new byte[conn.getContentLength()];
			int len = -1;
			
			while ((len = bis.read(buff)) != -1) {
				fw.write(buff, 0, len);
			}
			bis.close();
			is.close();
			fw.close();
			
			System.out.println("파일 다운로드 완료");
			*/

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
