package flatMain;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Path;

public class UnitTest {
	public static void main(String[] arg) throws FileNotFoundException {
		
		
		String currentDir;
		currentDir = System.getProperty("user.dir");
		String infoDir;
		infoDir = currentDir + "\\common\\";
		
		//System.out.println(infoDir);
		
		//Path p1 = Paths.get(System.getProperty("user.dir"));
		
		/*
		//INPUTS - string, desired format?
		//OUTPUTS - table formatted output to terminal
		String s[] = {"i", "am", "not", "throwing", "away", "my", "shot", "i'm"};
		
		String s1 = strArray2String(s);
		
		String s2 = "I'm,just,like,my,country\n";
		
		tableOut(s, "%-16s");
		*/
		
		//Export to CSV
		//String outputFile = "test1";
		//csvOut(outputFile, s1, s2);
		
	}
	
	//INPUTS - string, desired format?
	//OUTPUTS - table formatted output to terminal
	public static void tableOut (String s[], String format) {
		String formatDummy = format;
		for(int i = 0; i < s.length; i++) {
			s[i] = s[i].toUpperCase();
			if (i != (s.length - 1)) {
				format = format.concat(formatDummy);
			}
		}
		
		System.out.format(format + "\n", s);
	}
	
	public static void csvOut (String outputFileName, String... s1) throws IOException{
		FileWriter pw = new FileWriter(new File(outputFileName + ".csv"), true);
		for (int i=0; i < s1.length; i++) {
			pw.write(s1[i]);
			/*if (i == (s1.length-1)) {
				pw.write("\n");
			}*/
		}
		pw.close();
	}
	
	public static String strArray2String (String[] s) {
		StringBuilder s1Build = new StringBuilder();
		for (int i=0; i < s.length; i++) {
			s1Build.append(s[i]);
			if (i != (s.length-1)) {
				s1Build.append(",");
			} else {
				s1Build.append("\n");
			}
		}
		String s1 = s1Build.toString();
		return s1;
	}
	
	public static String getWorkingDir (String configDir) {
		String currentDir;
		currentDir = System.getProperty("user.dir");
		configDir = currentDir + "//" + configDir + "//";
		return configDir;
	}
}
