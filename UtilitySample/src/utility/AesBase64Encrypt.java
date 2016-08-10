package utility;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AesBase64Encrypt {
	public static void main(String args[]) throws Exception {
		byte[] key, iv;
		key = new byte[] { 0x12, 0x54, 0x20, -0x78, 0x32, 0x52, 0x40, -0x78, 0x12, 0x54, 0x27, -0x78, -0x12, 0x54, 0x29, -0x78 };
		iv = new byte[] { -0x11, 0x14, 0x20, -0x38, 0x72, 0x62, 0x40, 0x78, -0x15, 0x42, 0x29, 0x51, -0x28, 0x44, 0x71, -0x71 };
		setKey(key, iv);
		
		System.out.println(enc("test01"));
		System.out.println(enc("Media Server"));
		System.out.println(enc("Control Server"));
		
		
		System.out.println(dec("RYwyTwFXtgXZC6BJV8a5eA=="));
		System.out.println(dec("+y+HOzaVc1kYhzwyIYF6lw=="));
		
		
	}
	
	// Mapping table from 6-bit nibbles to Base64 characters.
	private static final char[] map1 = new char[64];
	static {
		int i = 0;
		for (char c = 'A' ; c <= 'Z' ; c++)
			map1[i++] = c;
		for (char c = 'a' ; c <= 'z' ; c++)
			map1[i++] = c;
		for (char c = '0' ; c <= '9' ; c++)
			map1[i++] = c;
		map1[i++] = '+';
		map1[i++] = '/';
	}

	// Mapping table from Base64 characters to 6-bit nibbles.
	private static final byte[] map2 = new byte[128];
	static {
		for (int i = 0 ; i < map2.length ; i++)
			map2[i] = -1;
		for (int i = 0 ; i < 64 ; i++)
			map2[map1[i]] = (byte)i;
	}

	private static SecretKeySpec keySpec = null;
	private static IvParameterSpec initalVector = null;

	/**
	 * enc(), dec()시에 사용하는 key spec을 설정.
	 * 내부에 저장해 두고 enc(), dec()시에 사용하도록 한다.
	 * @param key aes 암호화용 key. 16 byte 배열.
	 * @param iv cbc를 위한 initial vector. 16 byte 배열.
	 */
	public static void setKey(byte[] key, byte[] iv) {
		keySpec = new SecretKeySpec(key, "AES");
		initalVector = new IvParameterSpec(iv);
	}

	/**
	 * userId, deviceId(UTF-8)를 암호화 해서 base64로 encoding한 후 반환한다.
	 * @param decoded userId 또는 deviceId 등의 문자열. UTF-8로 byte[]로 변환한 후 encrypt를 수행한다.
	 * @return encrypt된 결과(byte[])를 base64로 encoding하여 반환.
	 * @throws Exception 
	 */
	public static String enc(String decoded) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, keySpec, initalVector);

		byte[] encrypted = cipher.doFinal(decoded.getBytes("UTF-8"));
		return base64Encode(encrypted);
	}

	/**
	 * base64로 encoding된 문자열을 byte[]로 decdong한 후 그 결과 값을 복호화하면 
	 * UTF-8로 encoding된 문자열이 생성되며 그 문자열을 반환한다.
	 * @param base64로 encoding된 암호화된 문자열. 
	 * @return decrypt된 결과 문자열(UTF-8) 반환.
	 * @throws Exception 
	 */
	public static String dec(String encoded) throws Exception {
		byte[] encrypted = base64Decode(encoded);

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, keySpec, initalVector);

		byte[] decrypted = cipher.doFinal(encrypted);
		return new String(decrypted, "UTF-8");
	}

	private static String base64Encode(byte[] plain) {
		int oDataLen = (plain.length * 4 + 2) / 3; // output length without padding
		int oLen = ((plain.length + 2) / 3) * 4; // output length including padding
		StringBuffer sb = new StringBuffer(oLen);

		int ip = 0;
		int iEnd = plain.length;
		while (ip < iEnd) {
			int i0 = plain[ip++] & 0xff;
			int i1 = ip < iEnd ? plain[ip++] & 0xff : 0;
			int i2 = ip < iEnd ? plain[ip++] & 0xff : 0;

			int o0 = i0 >>> 2;
			int o1 = ((i0 & 3) << 4) | (i1 >>> 4);
			int o2 = ((i1 & 0xf) << 2) | (i2 >>> 6);
			int o3 = i2 & 0x3F;

			sb.append(map1[o0]);
			sb.append(map1[o1]);
			sb.append(sb.length() < oDataLen ? map1[o2] : '=');
			sb.append(sb.length() < oDataLen ? map1[o3] : '=');
		}

		return sb.toString();
	}

	private static byte[] base64Decode(String base64) {
		int iLen = base64.length();

		if (iLen % 4 != 0)
			throw new IllegalArgumentException("Length of Base64 encoded input string is not a multiple of 4.");

		while (iLen > 0 && base64.charAt(iLen - 1) == '=')
			iLen--;

		int oLen = (iLen * 3) / 4;
		byte[] out = new byte[oLen];

		int ip = 0;
		int iEnd = 0 + iLen;
		int op = 0;
		while (ip < iEnd) {
			int i0 = base64.charAt(ip++);
			int i1 = base64.charAt(ip++);
			int i2 = ip < iEnd ? base64.charAt(ip++) : 'A';
			int i3 = ip < iEnd ? base64.charAt(ip++) : 'A';
			if (i0 > 127 || i1 > 127 || i2 > 127 || i3 > 127)
				throw new IllegalArgumentException("Illegal character in Base64 encoded data.");

			int b0 = map2[i0];
			int b1 = map2[i1];
			int b2 = map2[i2];
			int b3 = map2[i3];
			if (b0 < 0 || b1 < 0 || b2 < 0 || b3 < 0)
				throw new IllegalArgumentException("Illegal character in Base64 encoded data.");

			int o0 = (b0 << 2) | (b1 >>> 4);
			int o1 = ((b1 & 0xf) << 4) | (b2 >>> 2);
			int o2 = ((b2 & 3) << 6) | b3;
			out[op++] = (byte)o0;
			if (op < oLen)
				out[op++] = (byte)o1;
			if (op < oLen)
				out[op++] = (byte)o2;
		}

		return out;
	}
}
