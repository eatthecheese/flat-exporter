package flatMain;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

public class ReadFile {
	private String path;
	
	public ReadFile(String file_path) {
		path = file_path;
	}
	
	// get contents in string array format
	public String[] OpenFile () throws IOException {
		FileReader fr = new FileReader(path);
		BufferedReader textReader = new BufferedReader(fr);
		
		int numLines = readLines();
		String[] textData = new String[numLines];
		
		for (int i = 0; i < numLines; i++) {
			textData[i] = textReader.readLine();
		}
		
		textReader.close();
		return textData;
	}
	
	// gets desired Input columns in string array format
	public String[] OpenFileCols () throws IOException {
		FileReader fr = new FileReader(path);
		BufferedReader textReader = new BufferedReader(fr);
		
		int numLines = readLines();
		String[] textData = new String[numLines];
		
		
		for (int i = 0; i < numLines; i++) {
			textData[i] = textReader.readLine().split("=")[0].trim();
		}
		
		textReader.close();
		return textData;
	}
	
	// gets desired Input field values in string array format
	public String[] OpenFileValues () throws IOException {
		FileReader fr = new FileReader(path);
		BufferedReader textReader = new BufferedReader(fr);
		
		int numLines = readLines();
		String[] textData = new String[numLines];
		
		
		for (int i = 0; i < numLines; i++) {
			textData[i] = textReader.readLine().split("=")[1].trim();
		}
		
		textReader.close();
		return textData;
	}
	
	// get numlines of text file
	int readLines() throws IOException {
		FileReader fr = new FileReader(path);
		BufferedReader bf = new BufferedReader(fr);
		
		String aLine = null;
		int numLines = 0;
		
		while ((aLine = bf.readLine()) != null) {
			numLines++;
		}
		bf.close();
		
		return numLines;
	}
}
