import java.io.*;
import java.net.*;
import java.util.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class Server{  

  public static final int PORT = 7020;
  private static ArrayList<Frame> packets = new ArrayList<Frame>();
   
  public static void main(String[] args) throws IOException {

    ServerSocket s = new ServerSocket(PORT);
    System.out.println("Started: " + s);
       
    try {
      Socket socket = s.accept();
      try {
        System.out.println(
          "Connection accepted: "+ socket);
        BufferedReader in = 
          new BufferedReader(
            new InputStreamReader(
              socket.getInputStream()));

        PrintWriter out = 
          new PrintWriter(
            new BufferedWriter(
              new OutputStreamWriter(
                socket.getOutputStream())),true);
        File outputfile = new File("server.out");
        if (!outputfile.exists()) {
          outputfile.createNewFile();
        }
        BufferedWriter sendToNetwork = new BufferedWriter(new FileWriter(outputfile));
        File logfile = new File("server.log");
        if (!logfile.exists()) {
          logfile.createNewFile();
        }
        BufferedWriter log = new BufferedWriter(new FileWriter(logfile));
        int ackCount = 0;
        while (true) {  
          String str = in.readLine();
          if (str.equals("END")){
            sendToNetwork.close();
            log.close();
		        break;
          } 
          String[] parts = str.split(" ");
          Frame frame = unstuffPacket(str);
          String checksum = xorHex(parts[3]);
          if (checksum.equals(parts[2])){
            //send to network layer
            
            ackCount++;
            Frame ACK;
            if (ackCount%8==0){
              //force server error transmission
              int incorrectErrDet = flipBits( Integer.parseInt(parts[1]));
              ACK = new Frame(parts[1], ""+incorrectErrDet, "", "ERROR");
            }
            else {
              //send ack correctly
              ACK = new Frame(parts[1], parts[1], "", "ACK");
            }
            
            out.println(ACK);
            log.write("ACK sent."); log.newLine();
            sendToNetwork.write(frame.toString()); sendToNetwork.newLine();
            log.write("Packet sent to network layer."); log.newLine();
            
            if (isDuplicate(frame)){
              log.write("Duplicate frame received."); log.newLine();
            }
            else {
              log.write("Frame received."); log.newLine();
            }
          }
          else {
            //log frame received in error here
            log.write("Frame received in error."); log.newLine();
          }

          packets.add(frame);
          
          // out.println(str);
        }

       
      } finally {
        System.out.println("closing...");
        socket.close();
      }
    } finally {
      s.close();
    }
  }

  public static Frame unstuffPacket(String packet){
    String[] parts = packet.split(" ");
    String seq = parts[1];
    String errorCode = parts[2];
    String payload = parts[3];
    String type = parts[4];
    Frame frame = new Frame(seq, errorCode,payload, type );
    return frame;
  }


  private static boolean isDuplicate(Frame f){
    for(int i=0; i<packets.size();i++){
      if (f.getSequence_num().equals(packets.get(i).getSequence_num()))
        return true;
    }
    return false;
  }

  private static String xorHex(String frame){
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