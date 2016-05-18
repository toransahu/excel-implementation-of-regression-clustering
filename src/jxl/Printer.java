package jxl;

public class Printer {
	
	public static void error(String str) {
		int size=160;
	    int left = (size - str.length()) / 2;
	    int right = size - left - str.length();
	    String repeatedChar = " ";
	    StringBuffer buff = new StringBuffer();
	    for (int i = 0; i < left; i++) {
	        buff.append(repeatedChar);
	    }
	    buff.append(str);
	    for (int i = 0; i < right; i++) {
	        buff.append(repeatedChar);
	    }
	    // to see the end (and debug) if using spaces as repeatedChar
	    //buff.append("$");  
	    System.err.println(buff.toString());
	}
	
	public static void main(String str) {
		int size=160;
	    int left = (size - str.length()) / 2;
	    int right = size - left - str.length();
	    String repeatedChar = " ";
	    StringBuffer buff = new StringBuffer();
	    for (int i = 0; i < left; i++) {
	        buff.append(repeatedChar);
	    }
	    buff.append(str);
	    for (int i = 0; i < right; i++) {
	        buff.append(repeatedChar);
	    }
	    // to see the end (and debug) if using spaces as repeatedChar
	    //buff.append("$");  
	    System.out.println(buff.toString());
	}
	

}
