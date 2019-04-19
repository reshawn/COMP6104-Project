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

				int packetNum = 1;
				int frameNum = 1;
				for (int i=0; i<frameCount-1;i++){
					packetCounter++;
					long start1;
					long end1;
					long elapsed_time;
					record.write("Sending packets of frames : Packet "+packetNum+", Frame "+frameNum);
					record.newLine();
					frameNum++;
					if(packets.get(i).end_of_packet_byte.equalsIgnoreCase("Y")){
						packetNum++;
						frameNum= 1;
					}
						if (packetCounter%5==0){
							int error = flipBits(Integer.parseInt(packets.get(i).error_detection,16));
							String flipError =Integer.toHexString(error);

							System.out.println(packets.get(i).error_detection+" flipped- "+flipError);
							record.write(packets.get(i).error_detection+" flipped- "+flipError);
							record.newLine();//records the flipped error-detection field of the frame
							//sendPacket = new Frame(packets.get(i).sequence_number,flipError, packets.get(i).payload, packets.get(i).type,packets.get(i).end_of_packet_byte);
							sendPacket = packets.get(i);
							//System.out.println(sendPacket.error_detection);
							//sendPacket.error_detection = flipError;
							flag = 1; // if flag is 1 error detection recorded in client.log
						}else
							sendPacket = packets.get(i);



					start1 =System.currentTimeMillis(); //start time
						out.println(sendPacket); // it sent up to the fifth frame with the error, but the server does not receive it :(

							if(flag==1){ //error detection recorded in client.log
								record.write("Frame sent with flipped Error-Detection value");
								record.newLine();
								record.newLine();
								flag = 0;
							}else{
								record.write("Frame sent successfully.");
								record.newLine();
							}

						String serverAck = in.readLine();
						record.write("ACK recieved");
						record.newLine();	//logs the receiving of an ack
						ackError++;
							if(ackError == 8){ //pulls the errorAck from the Server and logs the receipt of the error ACK
								record.write("ACK" + serverAck + "recieved in error");
								record.newLine();
								record.newLine();
								ackError = 0;
							}
							delay_count++;

							if(delay_count==50){
								TimeUnit.MILLISECONDS.sleep(250);
								delay_count=0;
							}// a delay occurs every 50 ACKS

					end1 = System.currentTimeMillis();	//end time
					String[] ackParts = serverAck.split(" ");  //ack[4] is the ack type, either error or ACK
					System.out.println(ackParts[4]);
						if(ackParts[4].equalsIgnoreCase("ERROR")){
							out.println(packets.get(i));
						}
						else{
							//continue;
						}
					elapsed_time = end1-start1; //calculates the elapsed time to gauge if the time limit was exceeded

						if(elapsed_time>200){			//checks to see if the elapsed time is > 2 then the frame is resent
							out.println(packets.get(i));
							record.write("Time Limit exceeded,Frame resent.");
							record.newLine();
						}

			} // sends frames to server, end of for loop

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
		return (Math.abs(~n + 1));
	}// end of flipBits function


}//end of Client Class