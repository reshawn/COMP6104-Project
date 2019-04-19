//import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

public class Client {
	private static ArrayList<Frame> packets = new ArrayList<Frame>(); // this arraylist stores all the frames that were read in from the file
	private static int packetCount = 0;
	private static int frameCount = 0;
	public static String typee = "data";
	private static int packetCounter = 0;
	public static void main(String[] args)
			throws IOException {
			//Timer timer = new Timer(); 	///////////////TIMERRRRRRRRR

		try{
			readPackets();
		}catch(Exception e){System.out.println(e);}

		InetAddress addr = InetAddress.getByName("localhost");


		//System.out.println("addr = " + addr);

		Socket socket = new Socket(addr, 7020);

		try {
//
			//System.out.println("socket = " + socket);

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
			for (int i=0; i<packetCount-1;i++){
				packetCounter++;
//				Frame ACK;
				long start1;
				long end1;
				long elapsed_time;
				if (packetCounter%5==0){
					int error = flipBits(Integer.parseInt(packets.get(i).error_detection,16));
					String flipError = Integer.toHexString(error);
					Frame ACK = new Frame(packets.get(i).sequence_number,flipError, packets.get(i).payload, "ACK");
					//Frame ACK = new Frame(packets.get(i).sequence_number, "1", packets.get(i).payload, "ACK");
					start1 = System.nanoTime();
					out.println(ACK);
					end1 = System.nanoTime();
					elapsed_time = end1-start1;
					if(elapsed_time>2){			//checks to see if the elapsed time is > 2 then the frame is resent
						out.println(packets.get(i));
					}
				}
				else{
					start1 = System.nanoTime();	//starts a timer in nano-seconds yo
					out.println(packets.get(i));
					end1 = System.nanoTime();		//records the time it ends yo

					elapsed_time = end1-start1;	//elapsed time
					double seconds = (double)elapsed_time;	//changed to seconds
					if(elapsed_time>2){			//checks to see if the elapsed time is > 2 then the frame is resent
						out.println(packets.get(i));
					}
				}
				String serverAck = in.readLine();
				//System.out.println(in.readLine());
				String[] ackParts = serverAck.split(" ");  //ack[4] is the ack type, either error or ACK
				System.out.println(ackParts[4]);
				if(ackParts[4].equalsIgnoreCase("ERROR")){
					out.println(packets.get(i));
				}
				else{
					continue;
				}


			} // sends frames to server

			out.println("END");
		}
		catch (Exception e)
		{

		} finally {
			System.out.println("closing...");
			socket.close();
		}
	}

	public static void readPackets() throws Exception{
		try {

			Scanner scanner = new Scanner(new File("raw.out.txt"));
			System.out.println("Loading packets from raw.out file");
			String line = scanner.nextLine();
			int count = 0;
			packetCount = Integer.parseInt(line);
			System.out.println("Reading packets from file");
			while (scanner.hasNextLine()) {
				count++;
				line = scanner.nextLine();
				String[] parts = line.split(" ");
				int len = Integer.parseInt(parts[0]);
				//System.out.println("Packet number: "+count+" Len: "+len);
				//System.out.println("Packet: "+frame);
				char c = parts[1].charAt(0);
				int ccount = 1;
				String l = new String();
				while (ccount<(len*2)){
					if ((ccount%120)>0){
						l+= c;
						c = parts[1].charAt(ccount);
						ccount++;
					}
					else {
						String checksum = xorHex(l);
						Frame frame = new Frame(Integer.toString(count), checksum, l, typee, "N");
						count++;
						packets.add(frame);
						l = new String();
						l+=c;
						c = parts[1].charAt(ccount);
						ccount++;
					}
				}
				String checksum = xorHex(l);
				Frame frame = new Frame(Integer.toString(count), checksum, l, typee, "Y");
				// System.out.println(checksum);
				packets.add(frame);

			}
			frameCount = count;
			System.out.println("fc:"+frameCount);
			System.out.println("Finished reading packets from file.");
		} catch (Exception e) {
			// System.out.println(e.printStackTrace());
		}
	}

	public static String xorHex(String frame){
		int iter = 0;
		int length = frame.length();
		char[] result = new char[length];
		// System.out.println(frame +" " + length);
		result[0] = frame.charAt(0);
		for(int i =0; i<length-1; i++){
			// System.out.println(Integer.parseInt(Character.toString(result[i]),16) + "  , " + Integer.parseInt(Character.toString(frame.charAt(i+1)),16) + " , " + toHex(Integer.parseInt(Character.toString(result[i]),16) ^ Integer.parseInt(Character.toString(frame.charAt(i+1)),16)) );
			char r = toHex(Integer.parseInt(Character.toString(result[i]),16) ^ Integer.parseInt(Character.toString(frame.charAt(i+1)),16 ));
			result[i+1] = r;
		}
		return new String(""+result[length-2]+result[length-1]);
	}

	private static char toHex(int nibble) {
		if (nibble < 0 || nibble > 15) {
			throw new IllegalArgumentException();
		}
		return "0123456789ABCDEF".charAt(nibble);
	}

	private static int flipBits(int n){
		return ~n;
	}
}