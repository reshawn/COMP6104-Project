import java.io.*;
import java.net.*;
import java.util.*;

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

        while (true) {  
          String str = in.readLine();
          if (str.equals("END")) 
		      break;
          String[] parts = str.split(" ");
          Frame frame = new Frame(parts[1], parts[2], parts[3], parts[4]);
          packets.add(frame);
          System.out.println("Echoing: " + str);
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
  
} 