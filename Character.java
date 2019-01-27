public class Character {
	private static final int BITS_PER_BYTE = 8;
	private int[] bits;
	
	public enum ByteType {
		Unknown,
		Ascii,
		Continuation,
		Start2,
		Start3,
		Start4
	}
	
	public Character (int val) {
		this(String.format("%s%s", "00000000".substring(0, BITS_PER_BYTE - Integer.toString(val, 2).length()), Integer.toString(val, 2)));
		if(val >= Math.pow(2, BITS_PER_BYTE) || val < 0)
			throw new IllegalArgumentException("Cannot initialize character with integer value of " + val);
	}
	
	public Character(Character c) {
		bits = new int[BITS_PER_BYTE];
		for(int i = 0; i < BITS_PER_BYTE; i++) {
			bits[i] = c.bits[i];
		}
	}
	
	public Character(String s){
		bits = new int[BITS_PER_BYTE];
		char ch;
		if(s.length() != BITS_PER_BYTE) {
			throw new IllegalArgumentException("Cannot convert " + s + " to a character: not " + BITS_PER_BYTE + " characters.");
		}
		for(int i = 0; i < BITS_PER_BYTE; i++) {
			ch = s.charAt(i);
			if(ch != '0' && ch != '1' && ch != 'x')
				throw new IllegalArgumentException("Cannot convert " + s + " to a character: " + ch + " not a valid character.");
			setBit(i, ch);
		}
	}
	
	public static Character xor(Character left, Character right) {
		if(left == null || right == null)
			throw new IllegalArgumentException("Character in xor is null");
		int leftVal, rightVal;
		String result = "";
		for(int i = 0; i < BITS_PER_BYTE; i++) {
			leftVal = left.getBit(i);
			rightVal = right.getBit(i);
			if(leftVal == 2 || rightVal == 2)
				result += 'x';
			else
				result += leftVal ^ rightVal;
		}
		return new Character(result);
	}
	
	public String toString() {
		String retStr = "";
		for(int i = 0; i < BITS_PER_BYTE; i++) {
			char c = (char)(bits[i] == 2 ? 'x' : '0' + (char) bits[i]);
			retStr += c;
		}
		return retStr;
	}
	
	public boolean equals(Object o) {
		if(o == null || !(o instanceof Character))
			return false;
		
		Character c = (Character) o;
		for(int i = 0; i < BITS_PER_BYTE; i++)
				if(this.getBit(i) != c.getBit(i))
					return false;
		return true;
	}

	private int getBit(int index) {
		return bits[index];
	}
	
	private void setBit(int index, char val) {
		switch(val){
			case '0':
				bits[index] = 0;
				break;
			case '1':
				bits[index] = 1;
				break;
			case 'x':
				bits[index] = 2;
				break;
			default:
				throw new IllegalArgumentException("Unhandled character " + bits[index]);
		}
	}
	
	public boolean isControlCharacter() {
		int bit = getBit(0);
		if(bit == 2)
			throw new RuntimeException("Cannot determine if byte is control character; first bit of byte is 'x'.");
		return bit == 1;
	}
	
	public ByteType getUnambiguousByteType() {
		if(getBit(0) == 0)
			return ByteType.Ascii;
		if(getBit(0) == 1 && getBit(1) == 0)
			return ByteType.Continuation;
		if(getBit(0) == 1 && getBit(1) == 1 && getBit(2) == 0)
			return ByteType.Start2;
		if(getBit(0) == 1 && getBit(1) == 1 && getBit(2) == 1 && getBit(3) == 0)
			return ByteType.Start3;
		if(getBit(0) == 1 && getBit(1) == 1 && getBit(2) == 1 && getBit(3) == 1 && getBit(4) == 0 )
			return ByteType.Start4;
		return ByteType.Unknown;
	}
	
	
	
	//// TESTS ////
	
	public static void testChar() {
		boolean caught = false;
		Character t = new Character(25);
		if(!t.toString().equals("00011001"))
			System.out.println("Error creating character with value 25. Expected 00011001 but got " + t.toString());
		t = new Character(111);
		if(!t.toString().equals("01101111"))
			System.out.println("Error creating character with value 111. Expected 01101111 but got " + t.toString());
		t.setBit(4, 'x');
		Character t1 = new Character("0110x111");
		if(!t.equals(t1))
			System.out.println("Characters t != t1");
		if(!t1.equals(t))
			System.out.println("Characters t1 != t");
			
		t = new Character("0x010111");
		if(t.getBit(1) != 2)
			System.out.println("Error creating character 0x010111");
		
		
		try{
			new Character(-5);
		} catch(Exception e) {
			caught = true;
		}
		if(!caught)
			System.out.println("constructing character -5 did not cause error");
		caught = false;
		try{
			new Character(256);
		} catch(Exception e) {
			caught = true;
		}
		if(!caught)
			System.out.println("constructing character 256 did not cause error");
		caught = false;
		try{
			new Character("0x01");
		} catch(Exception e) {
			caught = true;
		}
		if(!caught)
			System.out.println("constructing character '0x01' did not cause error");
		caught = false;
		
		System.out.println("testChar() finished");
	}
	
	public static void testXor() {
		Character t1 = new Character("0x0011x1");
		Character t2 = new Character("x0xx01x1");
		Character r1 = new Character("xxxx10x0");
		Character t3 = new Character("10101010");
		Character t4 = new Character("11110000");
		Character r2 = new Character("01011010");
		if(!Character.xor(t1, t2).equals(r1))
			System.out.println(t1 + " xor " + t2 + " = " + r1 + "  actual: " + t1 + " xor " + t2 + " = " + Character.xor(t1, t2));
		if(!Character.xor(t3, t4).equals(r2))
			System.out.println(t3 + " xor " + t4 + " = " + r2 + "  actual: " + t3 + " xor " + t4 + " = " + Character.xor(t3, t4));
		
		System.out.println("testXor() finished");
	}
}