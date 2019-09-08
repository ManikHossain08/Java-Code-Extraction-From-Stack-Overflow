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

	static final String MacOSXPathToWrite = "/Users/manikhossain/EclipseCreatedSOJavaFile/";
	static final String MacOSXPathToReadFile = "/Users/manikhossain/EclipseCreatedSOJavaFile/QueryResults copy.csv";

	static String[] codeTokens = { "class", "package", "public", "private", "public static", "private static",
			"public class", "private boolean", "public void", "private void", "import", "public interface",
			"private interface", "abstract class", "public boolean", "void" };
	static String[] codeReverseTokens = { "String.class" };
	static String[] removeUnwantedCode = { "<?xml version", "<application", "apply plugin", "compileSdkVersion",
			"<fileSets>", "<artifactId>", "<properties>", "pom.xml", "<plugins>", "using namespace std;", "<Switch",
			"</style>", "org.junit.Test", "Runtime Environment", "fatal error", "<dependency>", "<div>", "<bean",
			"<!--", "git repo", "java.lang", "</property>", "Caused by:", "internal error occurred", "FATAL EXCEPTION",
			"ComparisonFailure", "SDK_INT", "Unknown Source", "</mapping>", "mapping error", "error", "hibernate",
			"failed", "dependencies", "Unexpected", "unexpected", "No active", "ERROR", "html", "module", "</option>",
			"docker", "vector" };

	static String MacOSXFullPathToWrite = "";
	static String CombineAllCode = "";
	static boolean isStartwithSlashAndContainsTokens = false;
	static String postIDAsFileName = "";
	static String bodyOfCode = "";

	public static void main(String[] args) throws IOException {

		String filename = MacOSXPathToReadFile;
		File file = new File(filename);
		try {
			Scanner sc = new Scanner(file);
			while (sc.hasNext()) {
				String data = sc.nextLine();
				data = filterSOCodeFromDB(data);
				if (!data.toLowerCase().contentEquals("body")) {
					if (data.startsWith("\"") && !data.contentEquals("\"")) { // indicate starting of each row value
						data = data.substring(1); // remove first char because at the beginning " this char comes
						if (data.contains("<postid>")) {
							postIDAsFileName = data.split("<postid>")[0];
							bodyOfCode = data.split("<postid>")[1];
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
			System.out.println("Code Extraction done succesfully.");
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
		data = data.replace("&amp;&amp;", "&&");
		if (data.contains("here") || data.contains("java.lang")) {
			isStartwithSlashAndContainsTokens = true;
		}

		if (data.contains("//")) {
			if (data.split("//").length > 1 || data.startsWith("//")) {
				String[] splitComments = data.split("//");
				codeWrangglingFunctionORBlocks(splitComments[1], 0);
				if (isStartwithSlashAndContainsTokens) {
					if (!data.startsWith("//"))
						isStartwithSlashAndContainsTokens = false;
					data = splitComments[0];
					
				}
				if (splitComments[1].strip().contentEquals("}")) {
					data = splitComments[1];
				}
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
				String contentOfFile = codeWrangglingFunctionORBlocks(departedCode[0], 1);
				int noOfLines = countLines(contentOfFile);
				int noOfWords = countWordsUsingStringTokenizer(contentOfFile);
				try {
					if (noOfWords > 3 && noOfLines > 5) {
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

		for (String matchingToken : codeTokens) {
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
				fileContent = fileContent + "}";
			}
		}
		return fileContent;
	}

	private static void createJavaFileUsingFileClass(String fileName, String allCode) throws IOException {
		MacOSXFullPathToWrite = MacOSXPathToWrite + fileName + ".java";
		File file = new File(MacOSXFullPathToWrite);
		boolean isUnwanted = false;
		file.delete();
		for (String removeUnwantedCode1 : removeUnwantedCode) {
			if (allCode.toString().contains(removeUnwantedCode1)) {
				isUnwanted = true;
			}
		}
		// Create the file if the code is only in java and not unwanted code
		if (!isUnwanted) {
			file.createNewFile();
			// Write Content
			FileWriter writer = new FileWriter(file);
			writer.write(allCode);
			writer.close();
		}

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
