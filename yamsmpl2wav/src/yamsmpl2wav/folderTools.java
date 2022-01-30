package yamsmpl2wav;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class folderTools {
	
	public static String getDiskName(String diskFilePath) throws IOException {
		
		String diskName;
		
		File binaryFile = new File(diskFilePath);
		
		try (FileInputStream f = new FileInputStream(binaryFile)){
			byte[] bytesAll = f.readAllBytes();
			String diskByteName = new String (bytesAll);
			diskName = diskByteName;
		}
		
		return diskName;
	}
	
	public static String[] getSampleFolderNames(File cerosFile, String[] sampleFolderList) throws IOException {
		
		int numberOfFolders = sampleFolderList.length - 2; //Only folders, the -2 are the files
		
		String[] sampleFolderNames = new String[numberOfFolders]; //counts the number of sample folders
		byte [] byteNames = new byte[(int) cerosFile.length() - 1]; //-1 is the starting byte
		byte [] byteName = new byte[16];
		
		byteNames = hexTools.getBytes(cerosFile, byteNames.length, "1"); //the offset 1 is the starting byte
		
		int offset = 0;
		int folderNumber = 0;
		
		for (int i = 0; i < (numberOfFolders) * 2; i++) {
			
			if (i % 2 == 0) continue; //if the "folder name" is pair, then do the next code
			
			try {
				for (int k = 0; k < 16; k++) {
					byteName[k] = byteNames[k + offset];
				}
				String extractedName = new String (hexTools.byteArrayToString(byteName)); //converts the bytes into string
				sampleFolderNames[folderNumber] = extractedName; //insert the extracted name into the name string array
				folderNumber++; //sums folder number
				offset += 32; //sums offset
			} catch (ArrayIndexOutOfBoundsException e){
				System.out.println("oops! out of bound exception!" + e);
				return sampleFolderNames;
			}
		}
		
		return sampleFolderNames;
	}
	
	public static String getCorrectString (String unmodified) {
		
		String modified = (unmodified.replace("/","_")).trim();
		
		return modified;
	}
	
public static String getCorrectFileString (String unmodified) {
		
		String modified = (unmodified.replace("/","_")).replaceAll("[\\\\/:*?\"<>|]", "-").trim();
		
		return modified;
	}
}
