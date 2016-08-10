package utility;

import java.io.*;
import java.security.*;
import java.security.cert.*;
import java.util.Properties;

import javax.net.ssl.*;

/**
 * @description	This class is for when the certification of a SSL server trying to connect is not verified.
 * 				It makes certification of the server when java.net.ssl.SSLHandshakeException occurs.
 * @how	1. Run this before executing Client. 
 * 		2. 'jssecacerts' file will be made in the class-path.
 * 		3. Copy the file and paste to %JRE-HOME%/lib/security/
 * */
public class Certification {
	public static String HOST;
	public static int PORT;
	
	private static Properties props;
//	private static File propsFile;
//	private static FileInputStream fs;
	
	public static void main(String[] args)	throws Exception {
		init();
		checkCert();
	}
	
	public static void init() {
//		propsFile = new File(System.getProperty("user.dir") + File.separator + "unit_tester.properties");
//		props = new Properties();

//		try {
//			fs = new FileInputStream(propsFile);
//			props.load(new BufferedInputStream(fs));
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		props = new Properties();
		InputStream in = props.getClass().getResourceAsStream("/unit_tester.properties"); 
		try {
			props.load(in);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		HOST = props.getProperty("ssl_server_host").trim();
		PORT = Integer.parseInt(props.getProperty("ssl_server_port").trim());
	}

    public static void checkCert() throws Exception {
		char[] passphrase = "changeit".toCharArray();
	
		File file = new File("jssecacerts");
		System.out.println(System.getProperty("java.home"));
	
		if (file.isFile() == false) {
		    char SEP = File.separatorChar;
		    File dir = new File(System.getProperty("java.home") + SEP
			    + "lib" + SEP + "security");
		    file = new File(dir, "jssecacerts");
		    if (file.isFile() == false) {
			file = new File(dir, "cacerts");
		    }
		}
		System.out.println("Loading KeyStore " + file + "...");
		InputStream in = new FileInputStream(file);
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		ks.load(in, passphrase);
		in.close();
	
		SSLContext context = SSLContext.getInstance("TLS");
		TrustManagerFactory tmf =
		    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(ks);
		X509TrustManager defaultTrustManager = (X509TrustManager)tmf.getTrustManagers()[0];
		SavingTrustManager tm = new SavingTrustManager(defaultTrustManager);
		context.init(null, new TrustManager[] {tm}, null);
		SSLSocketFactory factory = context.getSocketFactory();
		
		System.out.println("Opening connection to " + HOST + ":" + PORT + "...");
		SSLSocket socket = (SSLSocket)factory.createSocket(HOST, PORT);
		socket.setSoTimeout(10000);
		try {
		    System.out.println("Starting SSL handshake...");
		    socket.startHandshake();
		    socket.close();
		    System.out.println();
		    System.out.println("No errors, certificate is already trusted");
			return;
		} catch (SSLException e) {
		    System.out.println(e.getMessage());
		    //e.printStackTrace(System.out);
		}
	
		X509Certificate[] chain = tm.chain;
		if (chain == null) {
		    System.out.println("Could not obtain server certificate chain");
		    return;
		}
	
//		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	
		//System.out.println("Server sent " + chain.length + " certificate(s):");
		MessageDigest sha1 = MessageDigest.getInstance("SHA1");
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		for (int i = 0; i < chain.length; i++) {
		    X509Certificate cert = chain[i];
		    //System.out.println
		    //	(" " + (i + 1) + " Subject " + cert.getSubjectDN());
		    //System.out.println("   Issuer  " + cert.getIssuerDN());
		    sha1.update(cert.getEncoded());
		    //System.out.println("   sha1    " + toHexString(sha1.digest()));
		    md5.update(cert.getEncoded());
		    //System.out.println("   md5     " + toHexString(md5.digest()));
		    //System.out.println();
		}
	
		//System.out.println("Enter certificate to add to trusted keystore or 'q' to quit: [1]");
		//String line = reader.readLine().trim();
		//int k;
		//try {
		//    k = (line.length() == 0) ? 0 : Integer.parseInt(line) - 1;
		//} catch (NumberFormatException e) {
		//    System.out.println("KeyStore not changed");
		//    return;
		//}
		
		int k=0;
		X509Certificate cert = chain[k];
		String alias = HOST + "-" + (k + 1);
		ks.setCertificateEntry(alias, cert);
	
		OutputStream out = new FileOutputStream("jssecacerts");
		ks.store(out, passphrase);
		out.close();
	
		//System.out.println(cert);
		System.out.println("Added certificate to keystore 'jssecacerts' using alias '"+ alias + "'");
    }
    
    private static final char[] HEXDIGITS = "0123456789abcdef".toCharArray();
    
	@SuppressWarnings("unused")
	private static String toHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 3);
		for (int b : bytes) {
			b &= 0xff;
			sb.append(HEXDIGITS[b >> 4]);
			sb.append(HEXDIGITS[b & 15]);
			sb.append(' ');
		}
		return sb.toString();
	}

	private static class SavingTrustManager implements X509TrustManager {

		private final X509TrustManager tm;
		private X509Certificate[] chain;

		SavingTrustManager(X509TrustManager tm) {
			this.tm = tm;
		}

		public X509Certificate[] getAcceptedIssuers() {
			throw new UnsupportedOperationException();
		}

		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
			throw new UnsupportedOperationException();
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
			this.chain = chain;
			tm.checkServerTrusted(chain, authType);
		}
	}

}
