import java.util.*;
import java.io.*;

public class Frame implements java.io.Serializable {
	public byte sequence_number;
	public byte error_detection;
	public String startFlag;
	public String endFlag;


    public Frame(byte seq, byte error) {
		this.sequence_number = seq;
		this.error_detection = error;
		this.startFlag = "7E";
		this.endFlag = "7E";
    }

    public byte getSequence_num(){
    	return sequence_number;
    }

    public byte getError_det(){
    	return error_detection;
    }


	public String toString(){
		return new String(sequence_number + " " + error_detection);
	}



}// end of class