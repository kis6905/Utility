package utility;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

public class WebsocketClient {
	
	public static void main(String[] args) {
		connectWebsocket();
	}
	
	private static WebSocketClient mWebSocketClient = null;
	
	public static void connectWebsocket() {
		URI uri;
		try {
			uri = new URI("ws://10.1.2.58:8080/info");
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return;
		}
		
		/*
		 * Draft_17 = Sec-WebSocket-Version: 13 (aka RFC-6455)
		 * Draft_10 = Sec-WebSocket-Version: 8
		 * Draft_76 = Sec-WebSocket-Version (unspecified, pre-versioning, Hixie-76)
		 * Draft_75 = Sec-WebSocket-Version (unspecified, pre-versioning, Hixie-75)
		 */
		mWebSocketClient = new WebSocketClient(uri, new Draft_17()) {
			@Override
			public void onOpen(ServerHandshake serverHandshake) {
				System.out.println("Opened");
				mWebSocketClient.send("{\"requestKind\": \"registration\", \"deviceId\": \"abc123\"}");
			}

			@Override
			public void onMessage(String s) {
				System.out.println("onMessage " + s);
			}

			@Override
			public void onClose(int i, String s, boolean b) {
				System.out.println("Closed " + s);
			}

			@Override
			public void onError(Exception e) {
				System.out.println("Error " + e.getMessage());
			}
		};
		mWebSocketClient.connect();
	}
}
