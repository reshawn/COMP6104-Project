import java.util.*;
import java.io.*;

public class Frame implements java.io.Serializable {
	public String sequence_number;
	public String error_detection;
	public String payload;
	public String startFlag;
	public String endFlag;


    public Frame(String seq, String error, String payload) {
		this.sequence_number = seq;
		this.error_detection = error;
		this.payload = payload;
		this.startFlag = "7E";
		this.endFlag = "7E";
    }

    public String getSequence_num(){
    	return sequence_number;
    }

    public String getError_det(){
    	return error_detection;
    }


	public String toString(){
		return new String(sequence_number + " " + error_detection);
	}



}// end of class