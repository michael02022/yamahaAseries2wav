package yamsmpl2wav;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.hackyourlife.s550.riff.DataChunk;
//import org.hackyourlife.s550.riff.InstrumentChunk;
import org.hackyourlife.s550.riff.RiffWave;
import org.hackyourlife.s550.riff.SampleChunk;
import org.hackyourlife.s550.riff.SampleChunk.SampleLoop;
import org.hackyourlife.s550.riff.WaveFormatChunk;

public class yamahaSmplFile {
	
	public static short[] getSamples (File yamahaSmplFile) throws NumberFormatException, IOException {
		
		int fileSizeInBytes = (int) yamahaSmplFile.length();
		int sizeOfBytes = fileSizeInBytes - Integer.parseInt("200", 16);
		
		byte [] byteSamples = new byte[sizeOfBytes];
		
		byteSamples = hexTools.getBytes(yamahaSmplFile, sizeOfBytes, "200");
		
		short[] samples = hexTools.getShortFromBytes(byteSamples);
		
		return samples;
	}
	
	public static long getSizeFile (File yamahaSmplFile) {
		long fileSizeInBytes = yamahaSmplFile.length();
		
		return fileSizeInBytes;
	}
	
	public static int getSizeOfSample (File yamahaSmplFile) throws NumberFormatException, IOException {
		int fileSizeInBytes = (int) yamahaSmplFile.length();
		int sizeOfBytes = fileSizeInBytes - Integer.parseInt("200",16);
		
		return sizeOfBytes;
	}
	
	public static String getName(File yamahaSmplFile) throws NumberFormatException, IOException {
		
		String fileName = new String (hexTools.getBytes(yamahaSmplFile, 16, "32"));
		
		return fileName;
	}
	
	public static String getNameStereo(File yamahaSmplFile) throws NumberFormatException, IOException {
		
		String fileName = new String (hexTools.getBytes(yamahaSmplFile, 16, "32"));
		
		fileName = fileName.substring(0, 14);
		
		return fileName + "-S";
	}
	
	public static int getSampleRate (File yamahaSmplFile) throws NumberFormatException, IOException {
		
		int sampleRate = hexTools.byteArrayToInt(hexTools.getBytes(yamahaSmplFile, 2, "28"));
		
		return sampleRate;
	}
	
	public static int getMidiNote (File yamahaSmplFile) throws NumberFormatException, IOException {
		
		int midiNote = hexTools.byteArrayToInt(hexTools.getBytes(yamahaSmplFile, 1, "7E"));
		
		return midiNote;
	}
	
	public static int getStartLoop (File yamahaSmplFile) throws NumberFormatException, IOException {
		
		int startLoop = hexTools.byteArrayToInt(hexTools.getBytes(yamahaSmplFile, 4, "96"));
		
		return startLoop + 1; //fix most of loop issues, some samples needs +2 instead, other don't need any sum, probably some byte not documented
	}
	
	public static int getEndLoop (File yamahaSmplFile) throws NumberFormatException, IOException {
		
		int endLoop = hexTools.byteArrayToInt(hexTools.getBytes(yamahaSmplFile, 4, "9A")) + getStartLoop(yamahaSmplFile);
		
		return endLoop;
	}
	
	public static String getSideChannel (File yamahaSmplFile) throws NumberFormatException, IOException {
		
		String sideChannel = new String (hexTools.getBytes(yamahaSmplFile, 1, "41"));
		
		return sideChannel;
	}
	
	public static short[] stereoShortIntoShort (short[] L, short[] R) {
		
		short[] stereoSamples = new short[L.length * 2];
		
		for(int k = 0; k < L.length; k++) {
			stereoSamples[2 * k] = L[k];
			stereoSamples[2 * k + 1] = R[k];
		}
		
		return stereoSamples;
	}
	
	public static void writeWavFile (File smplFile, int channels, short[] samples, String outPath, String diskName, String sampleFolderName) throws NumberFormatException, IOException {
		int currentSampleRate = getSampleRate(smplFile);
		int currentMidiNote = getMidiNote(smplFile);
		int currentStartLoop = getStartLoop(smplFile);
		int currentEndLoop = getEndLoop(smplFile);
		String nameFile = "";
		
		RiffWave wavFileOut = new RiffWave();
		wavFileOut.set(new WaveFormatChunk());
		wavFileOut.set(new DataChunk());
		wavFileOut.setSampleRate(currentSampleRate);
		wavFileOut.setSampleFormat(WaveFormatChunk.WAVE_FORMAT_PCM);
		wavFileOut.setChannels(channels);
		wavFileOut.setBitsPerSample(16);
		wavFileOut.set16bitSamples(samples);
		
		SampleChunk smpl = new SampleChunk();
		smpl.setMidiUnityNote(currentMidiNote);
		smpl.setMidiPitchFraction(0);
		smpl.addSampleLoop(new SampleLoop(0, 0, currentStartLoop, currentEndLoop, 0, 0));
		wavFileOut.set(smpl);
		
		if (channels == 2) {
			nameFile = getNameStereo(smplFile);
		} else {
			nameFile = getName(smplFile);
		}
		
		try(BufferedOutputStream wav = new BufferedOutputStream(new FileOutputStream(outPath + "\\" + diskName + "\\" + sampleFolderName + "\\" + folderTools.getCorrectFileString(nameFile) + ".wav"))) {
			wavFileOut.write(wav);
			System.out.println(outPath + "\\" + diskName + "\\" + folderTools.getCorrectFileString(sampleFolderName) + "\\" + folderTools.getCorrectFileString(nameFile) + ".wav");
		}
	}
}
