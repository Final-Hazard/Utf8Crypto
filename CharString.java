import java.util.ArrayList;

public class CharString {
	private ArrayList<Character> string;
	
	public CharString() {
		string = new ArrayList<Character>(16);
	}
	
	public CharString(CharString copy) {
		string = new ArrayList<Character>(copy.getString().size());
		for(int i = 0; i < copy.getString().size(); i++) {
			string.set(i, new Character(copy.charAt(i)));
		}
	}
	
	private ArrayList<Character> getString() {
		return string;
	}
	
	public void setCharacter(int index, Character c){
		if(c == null)
			throw new IllegalArgumentException("Cannot add null character to CharString");
		string.set(index, c);
	}
	
	
	public Character charAt(int index) {
		return string.get(index);
	}
	
	
	public void addCharacter(Character c) {
		if(c == null)
			throw new IllegalArgumentException("Cannot add null character to CharString");
		string.add(c);
	}
	
	public boolean isUtf8Simple() {
		int count = 0;
		for(int i = 0; i < string.size(); i++) {
			if(charAt(i).isControlCharacter()){
				count++;
			} else {
				if(count == 1)
					return false;
				count = 0;
			}
		}
		if(count == 1)
			return false;
		return true;
	}
	
	/** requires that the string is unambiguously utf8 */
	public boolean isUtf8Complete() {
		int expectedUtf8s = 0;
		for(int i = 0; i < string.size(); i++) {
			switch(charAt(i).getUnambiguousByteType()) {
				case Unknown:
					throw new IllegalArgumentException("Unable to process unknown byte type, character " + i + " : " + charAt(i));
				case Ascii:
					if(expectedUtf8s != 0)
						return false;
					break;
				case Continuation:
					if(expectedUtf8s == 0)
						return false;
					expectedUtf8s--;
					break;
				case Start2:
					if(expectedUtf8s != 0)
						return false;
					expectedUtf8s = 1;
					break;
				case Start3:
					if(expectedUtf8s != 0)
						return false;
					expectedUtf8s = 2;
					break;
				case Start4:
					if(expectedUtf8s != 0)
						return false;
					expectedUtf8s = 3;
					break;
				default:
					throw new RuntimeException("Unhandled byteType: " + charAt(i).getUnambiguousByteType());
			}
		}
		if(expectedUtf8s != 0)
			return false;
		return true;
	}
	
	public String toString() {
		String retStr = "";
		for(int i = 0; i < string.size(); i++) {
			retStr += charAt(i) + " ";
		}
		return retStr;
	}
	
	
	//// TESTS ////
	
	public static void testIsUtf8Simple() {
		CharString t = new CharString();
		for(int j = 0; j < 8; j++) {
			t.addCharacter(new Character(((j % 4 == 0 || j % 4 == 1) ? '1' : '0') + "000xxx1"));
		}
		
		if(!t.isUtf8Simple())
			System.out.println("Expected utf8 string is not utf8: " + t);
		t.setCharacter(7, new Character("1xxx1xxx"));
		if(t.isUtf8Simple())
			System.out.println("Expected non-utf8 string is utf8: " + t);
		t.setCharacter(6, new Character("11111111"));
		if(!t.isUtf8Simple())
			System.out.println("Expected utf8 string is not utf8: " + t);
		
		System.out.println("testIsUtf8Simple() finished");
	}
	
	public static void testIsUtf8Complete() {
		CharString t = new CharString();
		t.addCharacter(new Character("10xxxxxx"));
		if(t.isUtf8Complete())
			System.out.println("Non complete utf8 marked as complete: " + t);
		t.setCharacter(0, new Character("0xxxxxxx"));
		if(!t.isUtf8Complete())
			System.out.println("Complete utf8 marked as non-complete: " + t);
		t.addCharacter(new Character("110xxxxx"));
		if(t.isUtf8Complete())
			System.out.println("Non complete utf8 marked as complete: " + t);
		t.addCharacter(new Character("10xxxxxx"));
		if(!t.isUtf8Complete())
			System.out.println("Complete utf8 marked as non-complete: " + t);
		t.addCharacter(new Character("10xxxxxx"));
		if(t.isUtf8Complete())
			System.out.println("Non complete utf8 marked as complete: " + t);
		t.setCharacter(1, new Character("11110xxx"));
		if(t.isUtf8Complete())
			System.out.println("Non complete utf8 marked as complete: " + t);
		t.addCharacter(new Character("00111111"));
		if(t.isUtf8Complete())
			System.out.println("Non complete utf8 marked as complete: " + t);
		t.setCharacter(4, new Character("10111111"));
		if(!t.isUtf8Complete())
			System.out.println("Complete utf8 marked as non-complete: " + t);
		t.addCharacter(new Character("110x0000"));
		if(t.isUtf8Complete())
			System.out.println("Non complete utf8 marked as complete: " + t);
		t.addCharacter(new Character("10xx0000"));
		if(!t.isUtf8Complete())
			System.out.println("Complete utf8 marked as non-complete: " + t);
		
		System.out.println("isUtf8Complete() finished");
	}
}