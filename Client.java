import java.net.*;
import java.io.*;

public class Client {
		
	public static void main(String[] args) 
	throws IOException {

		InetAddress addr = InetAddress.getByName("localhost");


		System.out.println("addr = " + addr);

		Socket socket = new Socket(addr, 7020);

		try {
			System.out.println("socket = " + socket);
			BufferedReader in =
				new BufferedReader(
						new InputStreamReader(
								socket.getInputStream()));


			PrintWriter out =
				new PrintWriter(
						new BufferedWriter(
								new OutputStreamWriter(
										socket.getOutputStream())),true);

			
	       
			
			String TextToCode = "Infornation Technology";
			out.println(TextToCode);
			out.println("END");
		}
		catch (Exception e)
		{

		} finally {
			System.out.println("closing...");
			socket.close();
		}
	}

//	public String xorHex(String a, String b) {
//		// TODO: Validation
//		char[] chars = new char[a.length()];
//		for (int i = 0; i < chars.length; i++) {
//			chars[i] = toHex(fromHex(a.charAt(i)) ^ fromHex(b.charAt(i)));
//		}
//		return new String(chars);
//	}
//
//	private static int fromHex(char c) {
//		if (c >= '0' && c <= '9') {
//			return c - '0';
//		}
//		if (c >= 'A' && c <= 'F') {
//			return c - 'A' + 10;
//		}
//		if (c >= 'a' && c <= 'f') {
//			return c - 'a' + 10;
//		}
//		throw new IllegalArgumentException();
//	}
//
//	private char toHex(int nybble) {
//		if (nybble < 0 || nybble > 15) {
//			throw new IllegalArgumentException();
//		}
//		return "0123456789ABCDEF".charAt(nybble);
//	}
} 

