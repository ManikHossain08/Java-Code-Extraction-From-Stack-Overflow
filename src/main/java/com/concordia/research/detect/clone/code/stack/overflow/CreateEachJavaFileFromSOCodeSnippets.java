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
	static final String MacOSXPathToWriteUnWantedCode = "/Users/manikhossain/EclipseCreatedSOJavaFile/UnWantedCode/";
	static final String MacOSXPathFilteredErroticFile = "/Users/manikhossain/Q_NiCadLogs.csv";

	static String[] codeTokensForFunctions = { "class", "package", "public", "private", "public static",
			"private static", "public class", "private boolean", "public void", "private void", "import",
			"public interface", "private interface", "abstract class", "public boolean", "void" };
	static String[] codeReverseTokens = { "String.class" };
	static String[] removeUnwantedCode = { "<application", "apply plugin", "compileSdkVersion", "<fileSets>",
			"<artifactId>", "<properties>", "pom.xml", "<plugins>", "using namespace std;", "<Switch", "</style>",
			"Runtime Environment", "fatal error", "<dependency>", "<div>", "<bean", "<!--", "git repo", "</property>",
			"Caused by:", "FATAL EXCEPTION", "ComparisonFailure", "Unknown Source", "</mapping>", "dependencies",
			"Unexpected", "unexpected", "No active", "</html>", "module", "</option>", "docker", "vector", "version:",
			"src/main", "warning", "<?php", "</project>", "fatal", "xhtml", "404", "</div>", "<form:", "C#",
			"statusCode", "</activity>", "java.lang", "</script>", "<tr>", "<td>", "TABLE", "<table>", "<TextView",
			"<servlet-mapping>", "Breakpoint", "<Label>", "SDK_INT", "using", "scala", "<script>", "html", "ajax",
			"console.log", "RelativeLayout", "</button>", "</View>", "<LinearLayout", "<FrameLayout", "</Form>",
			"<Button", "selector", "undefined" };

	// unexpected, using

	static String[] wantedCode = { "tableselection", "+xml", "editable", "select *", "SELECT *", "Editable",
			"java.lang.reflect", "Hashtable"

	};

	static String MacOSXFullPathToWrite = "";
	static String CombineAllCode = "";
	static boolean isStartwithSlashAndContainsTokens = false;
	static boolean isBlocks = false;
	static String postIDAsFileName = "";
	static String bodyOfCode = "";
	static int NoOfFileExtracted = 0;
	static int NoOfEroticFile = 0;
	static int fromEachFile = 0;
	static int smallCOde = 0;
	static int GoodCode = 0;
	static int totalCode = 0;
	static int totalQA = 1;
	static int totalGoodQA = 0;
	static int totalLeftQA = 0;
	static String fileLogYearly = "";
	static String CSVbuilder = null;
	static String CSVbuildergood = null;
	static String CSVbuildergood_2 = null;
	static String CSVbuildergood_3 = null;
	static String CSVbuilder_2 = null;
	static String CSVbuilder_3 = null;
	static String CSVbuilder_4 = null;
	static final String MacOSXPathToReadFile = "/Users/manikhossain/EclipseCreatedSOJavaFile/Acpt2018/";

	public static void main(String[] args) throws IOException {

		String filename = MacOSXPathToReadFile;
		FileWriter csvWriterLeft = new FileWriter("/Users/manikhossain/LeftQuestions2008.csv");
		FileWriter csvWriterLeft_2 = new FileWriter("/Users/manikhossain/LeftQuestions2008_2.csv");
		FileWriter csvWriterLeft_3 = new FileWriter("/Users/manikhossain/LeftQuestions2008_3.csv");
		FileWriter csvWriterLeft_4 = new FileWriter("/Users/manikhossain/LeftQuestions2008_4.csv");
		FileWriter csvWriterGood = new FileWriter("/Users/manikhossain/GoodQuestions2008.csv");
		FileWriter csvWriterGood_2 = new FileWriter("/Users/manikhossain/GoodQuestions2008_2.csv");
		FileWriter csvWriterGood_3 = new FileWriter("/Users/manikhossain/GoodQuestions2008_3.csv");
		// File file = new File(filename);
		File directory1 = new File(MacOSXPathToWriteBlocks);
		File directory2 = new File(MacOSXPathToWriteFunctions);
		File directory3 = new File(MacOSXPathToWriteUnWantedCode);

		if (!directory1.exists()) {
			directory1.mkdir();
		}
		if (!directory2.exists()) {
			directory2.mkdir();
		}
		if (!directory3.exists()) {
			directory3.mkdir();
		}

		File[] files = new File(filename).listFiles();
		// If this pathname does not denote a directory, then listFiles() returns null.

		for (File singlefile : files) {
			if (singlefile.isFile()) {
				System.out.println("Started Reading from file: " + singlefile.getName());
				getAllFilesFromCSVFolders(singlefile, csvWriterLeft, csvWriterGood, csvWriterGood_2, csvWriterGood_3,
						csvWriterLeft_2, csvWriterLeft_3, csvWriterLeft_4);
				System.out.println("Ended Reading from file: " + singlefile.getName());
			}
		}
		csvWriterLeft.close();
		csvWriterGood.close();
		csvWriterGood_2.close();
		csvWriterGood_3.close();
		csvWriterLeft_2.close();
		csvWriterLeft_3.close();
		csvWriterLeft_4.close();
		System.out.println("Code Extraction done succesfully. " + NoOfFileExtracted
				+ " java files have been extracted from this csv file.");
		System.out.println("UnWanted code Extraction done succesfully: " + NoOfEroticFile
				+ " UnWanted codes/files have been extracted from this csv file.");
		System.out.println(
				"Total good code extraction after removing unwanted code: " + (NoOfFileExtracted - NoOfEroticFile));
		fileLogYearly = "Code Extraction done succesfully. " + NoOfFileExtracted
				+ " java files have been extracted from this csv file." + "UnWanted code Extraction done succesfully: "
				+ NoOfEroticFile + " UnWanted codes/files have been extracted from this csv file."
				+ "Total good code extraction after removing unwanted code: " + (NoOfFileExtracted - NoOfEroticFile)
				+ "Number of small code: " + smallCOde + "\n" + "Number of total code: " + totalCode;

		System.out.println("Number of small code: " + smallCOde);

		System.out.println("Number of total code: " + totalCode);
		System.out.println("Number of total Good Code: " + GoodCode);
		//System.out.println("Number of total Q or A: " + totalQA); 
		

		//System.out.println("Number of total LeftQA: " + totalLeftQA);
		//System.out.println("Number of total Good QA: " + totalGoodQA);

		File file = new File("/Users/manikhossain/EclipseCreatedSOJavaFile/" + "fileLogYearly.txt");
		file.createNewFile();
		// Write Content
		FileWriter writer = new FileWriter(file);
		writer.write(fileLogYearly);
		writer.close();

	}

	private static void getAllFilesFromCSVFolders(File file, FileWriter csvWriterLeft, FileWriter csvWriterGood,
			FileWriter csvWriterGood_2, FileWriter csvWriterGood_3, FileWriter csvWriterLeft_2,
			FileWriter csvWriterLeft_3, FileWriter csvWriterLeft_4) throws IOException {
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
							bodyOfCode = data;
							// continue;
						}
						if (!isStartwithSlashAndContainsTokens) {
							// add only code not comments with tokens till end
							CombineAllCode = CombineAllCode + bodyOfCode;
						}
					} else if (data.contentEquals("\"")) { // indicate last line of each row value that means each
															// questions or answers
						extractHtmlCodeTags(postIDAsFileName, CombineAllCode, csvWriterLeft, csvWriterGood,
								csvWriterGood_2, csvWriterGood_3, csvWriterLeft_2, csvWriterLeft_3, csvWriterLeft_4);
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
			if (data.split("//")[1].strip().contains("{")) {
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
	private static void extractHtmlCodeTags(String fileName, String stringToSearch, FileWriter csvWriterLeft,
			FileWriter csvWriterGood, FileWriter csvWriterGood_2, FileWriter csvWriterGood_3,
			FileWriter csvWriterLeft_2, FileWriter csvWriterLeft_3, FileWriter csvWriterLeft_4) throws IOException {
		int counter = 1;
		// boolean isAnswerUsed = false;
		totalQA += 1;
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
						// isAnswerUsed = true;
						counter += 1;
						GoodCode = GoodCode + 1;
						// break;
					} else {
						smallCOde = smallCOde + 1;
						// isAnswerUsed = false;
					}

					totalCode += 1;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
//		if (!isAnswerUsed) {
//			if (totalLeftQA < 65534) {
//				CSVbuilder = fileName + "," + counter;
//				csvWriterLeft.write("\n");
//				csvWriterLeft.write(CSVbuilder);
//			} else if (totalLeftQA >= 65534 && totalLeftQA < 131068) {
//				CSVbuilder_2 = fileName + "," + counter;
//				csvWriterLeft_2.write("\n");
//				csvWriterLeft_2.write(CSVbuilder_2);
//			} else if (totalLeftQA >= 131068 && totalLeftQA < 196604) {
//				CSVbuilder_3 = fileName + "," + counter;
//				csvWriterLeft_3.write("\n");
//				csvWriterLeft_3.write(CSVbuilder_3);
//			} else if (totalLeftQA >= 196604 && totalLeftQA < 262138) {
//				CSVbuilder_4 = fileName + "," + counter;
//				csvWriterLeft_4.write("\n");
//				csvWriterLeft_4.write(CSVbuilder_4);
//			}
//			totalLeftQA += 1;
//		}
//		if (isAnswerUsed) {
//		
//			if (totalGoodQA < 65534) {
//				CSVbuildergood = fileName + "," + counter;
//				csvWriterGood.write("\n");
//				csvWriterGood.write(CSVbuildergood);
//			} else if (totalGoodQA >= 65534 && totalGoodQA < 131068) {
//				CSVbuildergood_2 = fileName + "," + counter;
//				csvWriterGood_2.write("\n");
//				csvWriterGood_2.write(CSVbuildergood_2);
//			} else if (totalGoodQA >= 131068 && totalGoodQA < 196604) {
//				CSVbuildergood_3 = fileName + "," + counter;
//				csvWriterGood_3.write("\n");
//				csvWriterGood_3.write(CSVbuildergood_3);
//			}
//			
//			totalGoodQA += 1;
//		}
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
				fileContent = fileContent + "\n" + "}";
			}
		}

		return fileContent;
	}

	private static void createJavaFileUsingFileClass(String fileName, String allCode) throws IOException {
		boolean isUnwanted = false;
		String reasonOfUnwanted = "";

		if (isBlocks) {
			MacOSXFullPathToWrite = MacOSXPathToWriteBlocks + fileName + ".java";
		} else {
			MacOSXFullPathToWrite = MacOSXPathToWriteFunctions + fileName + ".java";

		}

		if (isUnwanted) {
			MacOSXFullPathToWrite = MacOSXPathToWriteUnWantedCode + fileName + ".java";
			allCode = allCode + "\n" + "//" + reasonOfUnwanted;
			NoOfEroticFile += 1;
		}
		File file = new File(MacOSXFullPathToWrite);
		file.delete();

		// Create the file if the code is only in java and not unwanted code
		if (true) {
			// if (!isBlocks)CleanMultipleclassInSameFile(allCode, fileName);
//			allCode = removeUnbalancedCurlyBrackets(allCode);
//			allCode = removeUnbalancedCurlyBrackets(allCode);
//			allCode = removeUnbalancedCurlyBrackets(allCode);
//			allCode = removeUnbalancedCurlyBrackets(allCode);
//			allCode = removeUnbalancedCurlyBrackets(allCode);

//			file.createNewFile();
//			FileWriter writer = new FileWriter(file);
//			writer.write(allCode);
//			writer.close();
			NoOfFileExtracted += 1;

		}
		isBlocks = false;

	}

	private static boolean IstheFileErrotic(String fileName) throws FileNotFoundException {
		boolean IstheFileErrotic = false;
		File file = new File(MacOSXPathFilteredErroticFile);
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
		} else if (!allCode.startsWith("package") && allCode.contains("package")) {
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
		} else if (!allCode.startsWith("public class") && allCode.contains("public class")) {

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
