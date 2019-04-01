import java.net.*;
import java.io.*;
import java.util.*;

public class Client {
	private static ArrayList<String> packets = new ArrayList<String>();
	private static int packetCount = 0;
		
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
				int len = Integer.parseInt(parts[0]);
				System.out.println("Packet number: "+count+" Len: "+len);
				System.out.println("Packet: "+parts[1]);
				packets.add(parts[1]);
			}
		} catch (Exception e) {
		// System.out.println(e.printStackTrace());
		}
	}

} 

