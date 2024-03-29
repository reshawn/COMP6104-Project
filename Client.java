import java.net.*;
import java.io.*;
import java.util.*;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;


public class Client {
	private static ArrayList<Frame> packets = new ArrayList<Frame>(); // this arraylist stores all the frames that were read in from the file
	private static int packetCount = 0;
	private static int frameCount = 0;
	public static String typee = "data";
	private static int packetCounter = 0;
	private static int delay_count = 0;
	private static int flag = 0;
	private static Frame sendPacket;
	private static int ackError = 0;

	public static void main(String[] args)
			throws IOException {

				/*Creates client.log file*/
				File client_output = new File("client.log");
        		BufferedWriter record = new BufferedWriter(new FileWriter(client_output));
        			if (!client_output.exists()) {
          				client_output.createNewFile();
        			}

				try{
					readPackets();
				}catch(Exception e){System.out.println(e);}
				InetAddress addr = InetAddress.getByName("localhost");

				Socket socket = new Socket(addr, 7020);

				try {
					BufferedReader in =
						new BufferedReader(
							new InputStreamReader(
									socket.getInputStream()));


				PrintWriter out =
					new PrintWriter(
							new BufferedWriter(
									new OutputStreamWriter(
											socket.getOutputStream())),true);

			System.out.println("Sending packets to server.");
			int packetNum = 1;
			int frameNum = 1;
			for (int i=0; i<frameCount-1;i++){
				packetCounter++;
//				Frame ACK;
				record.write("Sending packets of frames : Packet "+packetNum+", Frame "+frameNum);
				record.newLine();
				frameNum++;
				if(packets.get(i).end_of_packet_byte.equalsIgnoreCase("Y")){
					packetNum++;
					frameNum= 1;
				}
				Frame sendPacket = packets.get(i);
				if (packetCounter%5==0){
					int error = flipBits(Integer.parseInt(packets.get(i).error_detection,16));
					String flipError =Integer.toHexString(error);
					// System.out.println(packets.get(i).error_detection+" flipped- "+flipError);
					
					record.write(packets.get(i).error_detection+" flipped- "+flipError);
					record.newLine();//records the flipped error-detection field of the frame
					
					// System.out.println(sendPacket);
					sendPacket = new Frame(packets.get(i).sequence_number,flipError, packets.get(i).payload, packets.get(i).type,packets.get(i).end_of_packet_byte);
					// System.out.println(packets.get(i).end_of_packet_byte);
					// System.out.println(sendPacket);
				}
				// System.out.println(sendPacket);
				out.println(sendPacket);
				
				socket.setSoTimeout(200);
				String serverAck = " ";
				while(serverAck.equals(" ")){
					try {
						serverAck = in.readLine();
						// System.out.println(serverAck);
					}
					catch(SocketTimeoutException e){
						// System.out.println("resending");
						out.println(packets.get(i));
					}
				}
				
				socket.setSoTimeout(0);
				// System.out.println("sendPacket");
				String[] ackParts = serverAck.split(" ");  //ack[4] is the ack type, either error or ACK
				// System.out.println(ackParts[4]);
				String chs = packets.get(i).sequence_number;
				
				while(!ackParts[2].equals(chs)){
					out.println(packets.get(i));
					serverAck = in.readLine();
					// System.out.println(serverAck);
					ackParts = serverAck.split(" ");
				}

				//System.out.println(in.readLine());

			} // sends frames to server

			out.println("END");
		}//end of try/catch

		catch (Exception e)
		{

		} finally {
			System.out.println("closing...");
			socket.close();
		}
	}//end of main










/*Functions*/

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
					else{
						String checksum = xorHex(l);
						Frame frame = new Frame(Integer.toString(count), checksum, l, typee, "N");
						count++;
						packets.add(frame);
						l = new String();
						l+=c;
						c = parts[1].charAt(ccount);
						ccount++;
					}
				}//end of 2nd while loop

					String checksum = xorHex(l);
					Frame frame = new Frame(Integer.toString(count), checksum, l, typee, "Y");
					// System.out.println(checksum);
					packets.add(frame);

			}//end of 1st while loop
				frameCount = count;
				System.out.println("fc:"+frameCount);
				System.out.println("Finished reading packets from file.");
		}catch (Exception e){
			// System.out.println(e.printStackTrace());
		}

	}//end of readPackets function




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
			}//end of for loop
				return new String(""+result[length-2]+result[length-1]);

	}//end of xorHex function




	private static char toHex(int nibble) {
		if (nibble < 0 || nibble > 15) {
			throw new IllegalArgumentException();
		}
		return "0123456789ABCDEF".charAt(nibble);

	}//end of toHex function




	private static int flipBits(int n){
		bin = bin.replaceAll("0", "x");
		bin = bin.replaceAll("1", "0");
		bin = bin.replaceAll("x", "1");
		int dec = Integer.parseInt(bin,2);
		return dec;
	}
}
