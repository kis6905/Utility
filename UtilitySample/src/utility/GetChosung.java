package utility;

import java.text.ParseException;

public class GetChosung {
	public static void main(String[] args) throws ParseException {
		String inputStr = "가까나다따라마바빠사싸아자짜차카타파 ab c d & - .)(%gg g)하하";
		
		StringBuilder sb = new StringBuilder();
		for (int inx = 0 ; inx < inputStr.length() ; inx++) {
			char character = inputStr.charAt(inx);
			int characterVal = (int)character;

//			if (Character.isWhitespace(character)) {
//				// space 삭제.
//			}
			if (characterVal >= 44032 && characterVal <= 55203) { // 한글인 경우만 초성 추출해 추가
				sb.append(getChosung(characterVal));
			}
			else { // 한글이 아니면 그대로 추가 (영어, 특수문자, 공백)
				sb.append(character);
			}
		}
		
		System.out.println("결과 : " + sb.toString());
	}
	
	private static char getChosung(int inputChar) {
		char result = ' ';
		int first = (inputChar - 44032) / (21 * 28);
		switch (first) {
		case 0:
			result = 'ㄱ';
			break;
		case 1:
			result = 'ㄲ';
			break;
		case 2:
			result = 'ㄴ';
			break;
		case 3:
			result = 'ㄷ';
			break;
		case 4:
			result = 'ㄸ';
			break;
		case 5:
			result = 'ㄹ';
			break;
		case 6:
			result = 'ㅁ';
			break;
		case 7:
			result = 'ㅂ';
			break;
		case 8:
			result = 'ㅃ';
			break;
		case 9:
			result = 'ㅅ';
			break;
		case 10:
			result = 'ㅆ';
			break;
		case 11:
			result = 'ㅇ';
			break;
		case 12:
			result = 'ㅈ';
			break;
		case 13:
			result = 'ㅉ';
			break;
		case 14:
			result = 'ㅊ';
			break;
		case 15:
			result = 'ㅋ';
			break;
		case 16:
			result = 'ㅌ';
			break;
		case 17:
			result = 'ㅍ';
			break;
		case 18:
			result = 'ㅎ';
			break;
		}
		
		return result;
	}
	
	
}
