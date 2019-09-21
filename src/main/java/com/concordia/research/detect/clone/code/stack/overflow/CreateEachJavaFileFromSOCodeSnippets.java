package com.concordia.research.detect.clone.code.stack.overflow;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * Author: Manik Hossain Version: 1.0.0
 */
public class CreateEachJavaFileFromSOCodeSnippets {

	static final String MacOSXPathToWriteBlocks = "/Users/manikhossain/EclipseCreatedSOJavaFile/Blocks/";
	static final String MacOSXPathToWriteFunctions = "/Users/manikhossain/EclipseCreatedSOJavaFile/Functions/";
	static final String MacOSXPathToReadFile = "/Users/manikhossain/EclipseCreatedSOJavaFile/Q_2018.csv";
	static final String MacOSXPathLoggedErroticFile = "/Users/manikhossain/Q_NiCadLogs.csv";

	static String[] codeTokensForFunctions = { "class", "package", "public", "private", "public static",
			"private static", "public class", "private boolean", "public void", "private void", "import",
			"public interface", "private interface", "abstract class", "public boolean", "void" };
	static String[] codeReverseTokens = { "String.class" };
	static String[] removeUnwantedCode = { "xml", "<application", "apply plugin", "compileSdkVersion", "<fileSets>",
			"<artifactId>", "<properties>", "pom.xml", "<plugins>", "using namespace std;", "<Switch", "</style>",
			"org.junit", "Runtime Environment", "fatal error", "<dependency>", "<div>", "<bean", "<!--", "git repo",
			"</property>", "Caused by:", "FATAL EXCEPTION", "ComparisonFailure", "Unknown Source", "</mapping>",
			"hibernate", "dependencies", "Unexpected", "unexpected", "No active", "ERROR", "</html>", "module",
			"</option>", "docker", "vector", "version:", "src/main", "warning", "<?php", "</project>", "\"data\"",
			"FAILS", "failed", "fails", "fatal", "xhtml", "404", "</div>", "<form:", "C#", "Button", "print",
			"statusCode", "</activity>", "etc", "->", "java.lang", "select", "SELECT", "sql", "SQL", "</script>",
			"<tr>", "<td>", "spring", "error", "TABLE", "table", "<TextView", "<servlet-mapping>", "jar",
			"Breakpoint","<Label>","VERSION","SDK_INT","using","scala","<script>","html" };

	static String MacOSXFullPathToWrite = "";
	static String CombineAllCode = "";
	static boolean isStartwithSlashAndContainsTokens = false;
	static boolean isBlocks = false;
	static String postIDAsFileName = "";
	static String bodyOfCode = "";
	static int NoOfFileExtracted = 0;

	public static void main(String[] args) throws IOException {

		String filename = MacOSXPathToReadFile;
		//File file = new File(filename);
		File directory1 = new File(MacOSXPathToWriteBlocks);
		File directory2 = new File(MacOSXPathToWriteFunctions);
	
		if (!directory1.exists()) {
			directory1.mkdir();
		}
		if (!directory2.exists()) {
			directory2.mkdir();
		}
		
		
		File[] files = new File(filename).listFiles();
		//If this pathname does not denote a directory, then listFiles() returns null. 

		for (File singlefile : files) {
		    if (singlefile.isFile()) {
		    	getAllFilesFromCSVFolders(singlefile);
		    }
		}
	}

	private static void getAllFilesFromCSVFolders(File file) throws IOException {
		try {
			Scanner sc = new Scanner(file);
			while (sc.hasNext()) {
				String data = sc.nextLine();
				data = filterSOCodeFromDB(data);
				if (!data.toLowerCase().contentEquals("body")) {
					if (data.startsWith("\"") && !data.contentEquals("\"")) { // indicate starting of each row value
						data = data.substring(1); // remove first char because at the beginning " this char comes
						if (data.contains("<postid>") && data.split("<postid>").length > 1) {
							postIDAsFileName = data.split("<postid>")[0];
							bodyOfCode = data.split("<postid>")[1];
						} else {
							postIDAsFileName = "FoundProblemInData";
							bodyOfCode = "FoundProblemInData";
							continue;
						}
						if (!isStartwithSlashAndContainsTokens) {
							// add only code not comments with tokens till end
							CombineAllCode = CombineAllCode + bodyOfCode;
						}
					} else if (data.contentEquals("\"")) { // indicate last line of each row value
						extractHtmlCodeTags(postIDAsFileName, CombineAllCode);
						CombineAllCode = "";
					} else {
						if (!isStartwithSlashAndContainsTokens) {
							CombineAllCode = CombineAllCode + "\n";
							CombineAllCode = CombineAllCode + data;
						}
					}
				}

			}
			sc.close();
			System.out.println("Code Extraction done succesfully. " + NoOfFileExtracted
					+ " java files have been extracted from this csv file.");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	// replace garbage code from SO database [here single lines of code will come]
	private static String filterSOCodeFromDB(String data) {
		isStartwithSlashAndContainsTokens = false;
		data = data.replace("\"\"", "\"");
		data = data.replace("&lt;", "<");
		data = data.replace("&gt;", ">");
		data = data.replace("...", "");
		data = data.replace("....", "");
		data = data.replace("..", "");
		data = data.replace("&amp;&amp;", "&&");
		data = data.replace("&amp;", "&"); 
		data = data.replace("`", "");
		if (data.contains("here") || data.strip().contentEquals(".") || data.startsWith(".")) {
			isStartwithSlashAndContainsTokens = true;
		}
		if (data.contains("//") && data.split("//").length > 1) {

			if (data.split("//")[1].strip().contains("}")) {
				data = data.replace("}", "//remove left bracket by manual conding");
			}
			if(data.split("//")[1].strip().contains("{")) {
				data = data.replace("{", "//remove right bracket by manual conding");
			}
			String[] splitComments = data.split("//");
			codeWrangglingFunctionORBlocks(splitComments[1], 0);
			if (isStartwithSlashAndContainsTokens) {
				if (!data.startsWith("//"))
					isStartwithSlashAndContainsTokens = false;
				data = splitComments[0];
			}
			if (data.startsWith(".")) {
				isStartwithSlashAndContainsTokens = true;
			}
		}

		return data;
	}

	// extract only code portion from whole HTML code using split function.
	private static void extractHtmlCodeTags(String fileName, String stringToSearch) throws IOException {
		int counter = 1;
		String[] splitCOde = stringToSearch.split("<code>");
		for (String partByPartCode : splitCOde) {
			String[] departedCode = partByPartCode.split("</code>");
			if (departedCode.length > 1) {
				int noOfLines = countLines(departedCode[0]);
				int noOfWords = countWordsUsingStringTokenizer(departedCode[0]);
				try {
					if (noOfWords > 3 && noOfLines > 9) {
						String contentOfFile = codeWrangglingFunctionORBlocks(departedCode[0], 1);
						createJavaFileUsingFileClass(fileName + "_" + Integer.toString(counter), contentOfFile);
						counter += 1;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/*
	 * Adding {} for blocks of code because without brackets parser could not parse
	 * the file, ischeckCode = 0 means checking statement as a comment that start
	 * with "//" ischeckCode = 1 that means the whole code to check block of code
	 */
	private static String codeWrangglingFunctionORBlocks(String data, int ischeckCode) {
		boolean iscontainTokens = false;

		if (ischeckCode == 1)
			data = removeUnbalancedCurlyBrackets(data);

		for (String matchingToken : codeTokensForFunctions) {
			if (data.toString().contains(matchingToken)) {
				iscontainTokens = true;
				if (ischeckCode == 0)
					isStartwithSlashAndContainsTokens = true;
			}
		}
		for (String matchingReverseToken : codeReverseTokens) {
			if (data.toString().contains(matchingReverseToken)) {
				if (iscontainTokens && ischeckCode == 1)
					iscontainTokens = false;
			}
		}
		if (!iscontainTokens && ischeckCode == 1) {
			{
				data = "{" + "\n" + data + "\n" + "}";
				iscontainTokens = false;
				isBlocks = true;
			}
		}
		return data;
	}

	private static String removeUnbalancedCurlyBrackets(String fileContent) {
		fileContent = fileContent.trim();
		long countLeftBrackets = fileContent.chars().filter(ch -> ch == '{').count();
		long countRightBrackets = fileContent.codePoints().filter(ch -> ch == '}').count();
		if (countLeftBrackets != countRightBrackets) {
			if (fileContent != null && fileContent.length() > 0 && countLeftBrackets < countRightBrackets
					&& fileContent.charAt(fileContent.length() - 1) == '}') {
				fileContent = fileContent.substring(0, fileContent.length() - 1);
			} else if (countLeftBrackets > countRightBrackets) {
				fileContent = fileContent + "\n"+ "}";
			}
		}

		return fileContent;
	}

	private static void createJavaFileUsingFileClass(String fileName, String allCode) throws IOException {
		boolean isUnwanted = false;
		
		if (isBlocks) {
			MacOSXFullPathToWrite = MacOSXPathToWriteBlocks + fileName + ".java";
		} else {
			MacOSXFullPathToWrite = MacOSXPathToWriteFunctions + fileName + ".java";
			
		}
		File file = new File(MacOSXFullPathToWrite);

		file.delete();
//		for (String removeUnwantedCode1 : removeUnwantedCode) {
//			if (allCode.toString().contains(removeUnwantedCode1)) {
//				isUnwanted = true;
//				break;
//			}
//		}
		
//		if(allCode.startsWith("class")) {
//			isUnwanted = true;
//		}
		
//		if (!isBlocks && !isUnwanted) isUnwanted = IstheFileErrotic(fileName);
		
		
		// Create the file if the code is only in java and not unwanted code
		if (!isUnwanted) {
			//if (!isBlocks)CleanMultipleclassInSameFile(allCode, fileName);
			allCode = removeUnbalancedCurlyBrackets(allCode);
			allCode = removeUnbalancedCurlyBrackets(allCode);
			allCode = removeUnbalancedCurlyBrackets(allCode);
			allCode = removeUnbalancedCurlyBrackets(allCode);
			allCode = removeUnbalancedCurlyBrackets(allCode);

			file.createNewFile();
			// Write Content
			FileWriter writer = new FileWriter(file);
			writer.write(allCode);
			writer.close();
			NoOfFileExtracted += 1;
			//isBlocks = false;
		}
		isBlocks = false;

	}

	private static boolean IstheFileErrotic(String fileName) throws FileNotFoundException {
		boolean IstheFileErrotic = false;
		File file = new File(MacOSXPathLoggedErroticFile);
		Scanner sc2 = new Scanner(file);
		while (sc2.hasNext()) {
			String data = sc2.nextLine();
			if ((!data.contentEquals("") || !data.isEmpty()) && data.contains(fileName)) {
				IstheFileErrotic = true;
				System.out.println("gurbageCode Found: " + fileName);
				break;
			}
		}
		sc2.close();
		return IstheFileErrotic;
	}

	private static String CleanMultipleclassInSameFile(String allCode, String fileName) {
		String gurbageCode = "";
		if (!allCode.startsWith("package") && allCode.contains("package")) {
			gurbageCode = allCode.split("package")[0].strip();
			if (!gurbageCode.isEmpty()) {
				if (!gurbageCode.contains("package") && !gurbageCode.contains("import") 
						&& !gurbageCode.startsWith("@")) { 
					allCode = allCode.replace(gurbageCode, "");
					System.out.println("public class: " + fileName);
					System.out.println("gurbageCode: " + gurbageCode);
					gurbageCode = "";
				}
			}
		}
		else if (!allCode.startsWith("package") && allCode.contains("package")) {
			gurbageCode = allCode.split("import")[0].strip();
			if (!gurbageCode.isEmpty()) {
				if (!gurbageCode.contains("package") && !gurbageCode.contains("import") 
						&& !gurbageCode.startsWith("@")) { 
					allCode = allCode.replace(gurbageCode, "");
					System.out.println("public class: " + fileName);
					System.out.println("gurbageCode: " + gurbageCode);
					gurbageCode = "";
				}
			}
		}else if(!allCode.startsWith("public class") && allCode.contains("public class")) {
			
			gurbageCode = allCode.split("public class")[0].strip();
			if (!gurbageCode.isEmpty()) {
				if (!gurbageCode.contains("package") && !gurbageCode.contains("import") 
						&& !gurbageCode.startsWith("@")) { 
					allCode = allCode.replace(gurbageCode, "");
					System.out.println("public class: " + fileName);
					System.out.println("gurbageCode: " + gurbageCode);
					gurbageCode = "";
				}
			}
			
		}
		return allCode;
	}

	private static int countLines(String str) {
		String[] lines = str.split("\r\n|\r|\n");
		return lines.length;
	}

	private static int countWordsUsingStringTokenizer(String sentence) {
		if (sentence == null || sentence.isEmpty()) {
			return 0;
		}
		StringTokenizer tokens = new StringTokenizer(sentence);
		return tokens.countTokens();
	}

}
