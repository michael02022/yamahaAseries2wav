package yamsmpl2wav;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class hexTools {
	
	public static int byteArrayToInt(byte[] byteArray)
    {
        String hex = "";
  
        // Iterating through each byte in the array
        for (byte i : byteArray) {
            hex += String.format("%02X", i);
        }
        
        return Integer.parseInt(hex,16);
    }
	
	public static String byteArrayToString(byte[] byteArray)
    {
        String s = new String (byteArray,StandardCharsets.UTF_8);
        return s;
    }
    
	//General Purpose File to ByteArray
    public static byte[] getBytes(File binaryFile, int sizeInBytes, String stringOffset) throws FileNotFoundException, IOException {
		try (FileInputStream f = new FileInputStream(binaryFile)){
			
			int offset = Integer.parseInt(stringOffset,16);
			byte[] bytesAll = f.readAllBytes();
			byte[] byteChunk = new byte[sizeInBytes];
			
			for (int i = 0; i < sizeInBytes; i++) {
				byteChunk[i] = bytesAll[offset];
				offset++;
			}
			return byteChunk;
		}
	}
    
    public static short[] getShortFromBytes(byte[] byteArray) throws FileNotFoundException, IOException {
    	
    	short[] samples = new short[byteArray.length / 2];
    	
    	int j = 0;
    	for (int i = 0; i < byteArray.length / 2; i++) {
    		samples[i] = (short) ((byteArray[1 + j] & 0xff) | (byteArray[j] << 8));
    		j+=2;
    	}
    	
    	return samples;
    }
    
}
