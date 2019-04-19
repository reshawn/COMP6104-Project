import java.util.*;
import java.io.*;

public class Frame implements java.io.Serializable {
	public String sequence_number;
	public String error_detection;
	public String payload;
	public String type;
	public String end_of_packet_byte;


	public Frame(String seq, String error, String payload, String typeronie, String eopb) {
		this.sequence_number = seq;
		this.error_detection = error;
		this.payload = payload;
		this.type = typeronie;
		this.end_of_packet_byte = eopb;
		//this.type = "data";
	}

	public String getSequence_num(){
		return sequence_number;
	}

	public String getError_det(){
		return error_detection;
	}

	public String getEoP(){
		return end_of_packet_byte;
	}

	public String getPayload(){
		return payload;
	}

	public String toString(){
		return new String(" "+sequence_number + " " + error_detection+" "+payload+" "+type+ " "+end_of_packet_byte);
	}



}// end of class