import java.util.*;
import java.io.*;

public class Frame implements java.io.Serializable {
	public byte sequence_number;
	public byte error_detection;


    public Frame(byte seq, byte error) {
		sequence_number = seq;
		error_detection = error;
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