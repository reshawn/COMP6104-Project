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
        while (true) {  
          String str = in.readLine();
          if (str.equals("END")){
            sendToNetwork.close();
            log.close();
		        break;
          } 
          String[] parts = str.split(" ");
          Frame frame = new Frame(parts[1], parts[2], parts[3], parts[4]);
          String checksum = xorHex(parts[3]);
          if (checksum.equals(parts[2])){
            //send to network layer
            Frame ACK = new Frame(parts[1], parts[1], "", "ACK");
            out.println(ACK);
            sendToNetwork.write(frame.toString()); sendToNetwork.newLine();
            log.write("Frame received."); log.newLine();
          }
          else {
            //log frame received in error here
            log.write("Frame received in error."); log.newLine();
          }

          packets.add(frame);
          
          out.println(str);
        }

       
      } finally {
        System.out.println("closing...");
        socket.close();
      }
    } finally {
      s.close();
    }
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
  
} 