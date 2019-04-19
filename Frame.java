import java.util.*;
import java.io.*;

public class Frame implements java.io.Serializable {
	public String sequence_number;
	public String error_detection;
	public String payload;
	public String type;


	public Frame(String seq, String error, String payload, String typeronie) {
		this.sequence_number = seq;
		this.error_detection = error;
		this.payload = payload;
		this.type = typeronie;
		//this.type = "data";
	}

	public String getSequence_num(){
		return sequence_number;
	}

	public String getError_det(){
		return error_detection;
	}


	public String toString(){
		return new String(" "+sequence_number + " " + error_detection+" "+payload+" "+type);
	}

	public String getPayload() {
		return payload;
	}
}// end of class