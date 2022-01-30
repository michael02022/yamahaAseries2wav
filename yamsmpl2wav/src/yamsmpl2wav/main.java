package yamsmpl2wav;


import java.io.File;
import java.io.IOException;


public class main {
	
	public static void samplesRendering (String rootPath, String diskName, String sampleFolderName, String folderYamahaPath) throws NumberFormatException, IOException { //folderYamahaPath is the folder that includes the yamaha samples
		
		File yamahaFile1 = null;
		File yamahaFile2 = null;
		File checkFile = null;
		
		String[] sampleFilesList;
		String[] stereoChecker = new String[2];
		
		File f = new File(folderYamahaPath);
		
		sampleFilesList = f.list();
		
		//STEREO FILE
		
				for (int i = 1; i < sampleFilesList.length; i++) {
					//for loop for stereoChecker		
					try {
					//first revision
					checkFile = new File (folderYamahaPath + "\\" + sampleFilesList[i]); //selects the file with the fake id file (k)
					
					stereoChecker[0] = YamahaSmplFile.getSideChannel(checkFile); //store the channel information in the stereo Array with the selected file
					
					yamahaFile1 = new File (folderYamahaPath + "\\" + sampleFilesList[i]);
					int sizeYamahaFile1 = (int) YamahaSmplFile.getSizeFile(yamahaFile1); //gets the size of the file
					
					i++;
					//second revision
					checkFile = new File (folderYamahaPath + "\\" + sampleFilesList[i]); //selects the file with the fake id file (k)
					
					stereoChecker[1] = YamahaSmplFile.getSideChannel(checkFile); //store the channel information in the stereo Array with the selected file
					
					yamahaFile2 = new File (folderYamahaPath + "\\" + sampleFilesList[i]);
					int sizeYamahaFile2 = (int) YamahaSmplFile.getSizeFile(yamahaFile2);
					
					i++;
						
					//now you can verify with a comparison of L and R AND the files are the same size
						
						if (stereoChecker[0].equals("L") && (stereoChecker[1].equals("R") && (sizeYamahaFile1 == sizeYamahaFile2))) { //If the 2 samples are L and R and same size
							//write both samples into 1 short (stereo)
							short[] stereoSamples = new short[YamahaSmplFile.getSizeOfSample(yamahaFile1) * 2]; //size of the stereo sample
								
							short[] sampleL = YamahaSmplFile.getSamples(yamahaFile1);
							short[] sampleR = YamahaSmplFile.getSamples(yamahaFile2);
							
							stereoSamples = YamahaSmplFile.stereoShortIntoShort(sampleL, sampleR);
							
							YamahaSmplFile.writeWavFile(yamahaFile1, 2, stereoSamples, rootPath, FolderTools.getCorrectFileString(diskName), sampleFolderName);
							
							i++; //sums i since the next sample is part of the first one
						} else {
							//write only the first sample as short (mono) and delete the next sample
							
							YamahaSmplFile.writeWavFile(yamahaFile1, 1, YamahaSmplFile.getSamples(yamahaFile1), rootPath, FolderTools.getCorrectFileString(diskName), sampleFolderName);
							
						}
					} catch (ArrayIndexOutOfBoundsException e){
						//writes the last file of the folder
						YamahaSmplFile.writeWavFile(yamahaFile1, 1, YamahaSmplFile.getSamples(yamahaFile1), rootPath, FolderTools.getCorrectFileString(diskName), sampleFolderName);
						
						break;
					}
					
				i = i - 2; //restores i
				yamahaFile1 = null;
				yamahaFile2 = null;
				}
	}
	
	
	public static void main(String[] args) throws NumberFormatException, IOException {
		
		if (args.length == 0) {
			System.out.println("How to use this tool? simple!\n"
					+ "write: 'folder_in_path' 'folder_out_path'\n"
					+ "(both with quotation marks)\n"
					+ "'folder_in_path' being the root Yamaha A series library files path.\n"
					+ "'folder_out_path' being the empty folder where you want to save the converted files.\n");
			System.exit(0);
		}
		
		final String folderINPath = args[0];
		
		final String folderOUTPath = args[1];
		
		String[] diskList;
		String[] sampleFolderList;
		String[] sampleFolderNamesList;
				
		File root = new File(folderINPath); //root yamaha folder 
		
		diskList = root.list();
		
		for (int i = 0; i < diskList.length; i++) { //diskList
			
			File disk = new File(folderINPath + "\\" + diskList[i]); //selects the disk folder
			sampleFolderList = disk.list(); //list the files inside of disk folder
			
			for (int j = 1; j < sampleFolderList.length - 1; j++) { //skips 0000 and last Fxxx file
				
				//Selects 0000 file
				String diskName = FolderTools.getDiskName(folderINPath + "\\" +  FolderTools.getCorrectString(diskList[i]) + "\\" + sampleFolderList[sampleFolderList.length - 1]); //uses the last file, being Fxxx (name of disk folder)
				//----------------
				
				//Creates disk folder in folderOutPath
				new File(folderOUTPath + "\\" + FolderTools.getCorrectFileString(diskName)).mkdirs();
				//-----------------
				
				//Selects sample folder
				File sampleFolder = new File(folderINPath + "\\" + diskList[i] + "\\" + sampleFolderList[j] + "\\" + "SMPL"); 
				//---------------------
				
				//Get sample folder names
				File cerosFile = new File(folderINPath + "\\" +  diskList[i] + "\\" + "0000");
				sampleFolderNamesList = FolderTools.getSampleFolderNames(cerosFile, sampleFolderList);
				//-----------------------
				
				//Creates disk folder in folderOutPath
				System.out.println(folderOUTPath + "\\" + FolderTools.getCorrectFileString(diskName) + "\\" + FolderTools.getCorrectFileString(sampleFolderNamesList[j - 1])); //the -1 is the j = 1 which it should be 0 to make the array work
				
				new File(folderOUTPath + "\\" + FolderTools.getCorrectFileString(diskName) + "\\" + FolderTools.getCorrectFileString(sampleFolderNamesList[j - 1])).mkdirs();
				//-----------------
				
				samplesRendering(folderOUTPath, FolderTools.getCorrectFileString(diskName), FolderTools.getCorrectFileString(sampleFolderNamesList[j - 1]), sampleFolder.getPath());
				
				}
			}
			
		System.out.println("Task done.");
		System.exit(0);
		}
	}
