import java.io.*;
import java.net.*;
import java.util.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class Server{  

  public static final int PORT = 7020;
  private static ArrayList<Frame> packets = new ArrayList<Frame>();
  private static String packet = "";
   
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
        System.out.println("Accepting packets from server.");
        while (true) {  
          String str = in.readLine();
          String[] parts = str.split(" ");
          Frame frame = unstuffPacket(str);
          String checksum = xorHex(parts[3]);
          if (checksum.equals(parts[2])){
            //send to network layer
            
            ackCount++;
            Frame ACK;
            if (ackCount%8==0){
              //force server error transmission
              String incorrectErrDet = Integer.toString(flipBits(Integer.parseInt(parts[1])));
              ACK = new Frame(parts[1], incorrectErrDet, "", "ACK", "");
            }
            else {
              //send ack correctly
              ACK = new Frame(parts[1], parts[1], "", "ACK", "");
            }
            
            out.println(ACK);
            log.write("ACK sent."); log.newLine();

            //reassemble packet
            
            // System.out.println("frame:"+frame.toString());
            if (isDuplicate(frame)){
              log.write("Duplicate frame received."); log.newLine();
            }
            else {
              packets.add(frame);
              log.write("Frame received."); log.newLine();
              if (frame.getEoP().equals("N")){
                packet+= frame.getPayload();
              }
              else {
                packet += frame.getPayload();
                // System.out.println("packet:"+packet);
                sendToNetwork.write(packet); sendToNetwork.newLine();
                log.write("Packet sent to network layer."); log.newLine();
                packet = "";
              }
            }
            
            
            
            
          }
          else {
            //log frame received in error here
            log.write("Frame received in error."); log.newLine();
          }

          
          
          // out.println(str);
        }

       
      } catch(Exception e){
        System.out.println("Client disconnected, terminating server.");
        
      }finally {
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
    String eopb = parts[5];
    Frame frame = new Frame(seq, errorCode,payload, type, eopb );
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
		String bin = Integer.toString(n,2);

		bin = bin.replaceAll("0", "x");
		bin = bin.replaceAll("1", "0");
		bin = bin.replaceAll("x", "1");
		int dec = Integer.parseInt(bin,2);
		return dec;
  }
  
} 