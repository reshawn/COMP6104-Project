import java.net.*;
import java.io.*;
import java.util.*;

public class Client {
	private static ArrayList<Frame> packets = new ArrayList<Frame>();
	private static int packetCount = 0;
	public static String typee = "data";

	public static void main(String[] args)
			throws IOException {
		try {readPackets();}catch(Exception e){System.out.println(e);}

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
			// socket.close();
		}
	}

	public static void readPackets() throws Exception{
		try {

			Scanner scanner = new Scanner(new File("raw.out.txt"));
			System.out.println("Loading packets from raw.out file");

			String line = scanner.nextLine();
			int count = 0;
			packetCount = Integer.parseInt(line);
			while (scanner.hasNextLine()) {
				count++;
				line = scanner.nextLine();
				String[] parts = line.split(" ");
				Frame frame = new Frame(Integer.toString(count), "???", parts[1], typee);
				int len = Integer.parseInt(parts[0]);
				System.out.println("Packet number: "+count+" Len: "+len);
				System.out.println("Packet: "+frame);
				packets.add(frame);
			}
		} catch (Exception e) {
			// System.out.println(e.printStackTrace());
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