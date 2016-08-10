package utility;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;


public class SocketClient {
	
	public static void main(String[] args) {
		threadTest(10, "127.0.0.1", 8000);
	}
	
	public static void threadTest(int threadCount, String host, int port) {
		for (int inx = 0; inx < threadCount; inx++) {
			ConnectThread thread = new ConnectThread(host, port);
			System.out.println("===================================");
			System.out.println("Thread " + inx + " '" + thread.getName() + "' start!");
			thread.start();
			System.out.println("===================================");
		}
	}
}


class ConnectThread extends Thread {
	
	private String host;
	private int port;
	
	public ConnectThread(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	@Override
	public void run() {
		Socket socket = null;
		DataInputStream dis = null;
		
		try {
			socket = new Socket(host, port);
			socket.setSoTimeout(1000 * 15); // 타임아웃 15초로 설정
			
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			
			dos.write("hihihi".getBytes());
			dos.flush();
			
			// 소켓의 입력스트림을 얻는다.
			dis = new DataInputStream(socket.getInputStream()); // 기본형 단위로 처리하는 보조스트림
			
			// 소켓으로 부터 받은 데이터를 출력한다.
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];
			int read;
			
			while (true) {
				read = dis.read(buffer, 0, bufferSize);
				bos.write(buffer, 0, read);
				if (read < bufferSize)
					break;
			}
			System.out.println("=========================================");
			System.out.println("서버로부터 받은 메시지" + this.getName() + ": " + new String(bos.toByteArray(), "UTF-8"));
			System.out.println("=========================================");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (dis != null && socket != null) {
					System.out.println("연결을 종료합니다.");
					// 스트림과 소켓을 닫는다.
					dis.close();
					socket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}

