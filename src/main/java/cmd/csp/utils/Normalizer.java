package cmd.csp.utils;

import java.util.regex.Pattern;

import smile.nlp.normalizer.SimpleNormalizer;

public class Normalizer {
	public boolean onWhitespaces = true;
	public boolean onFormatChars = true;
	public boolean onQuotesDouble = true;
	public boolean onQuotesSingle = true;
	public boolean onDash = true;
	public boolean onTrim = true;
	public boolean onUnicode = true;
	public boolean onUpper = false;
	public boolean onLower = false;
	public boolean onCRLF = false;
	public boolean onDeleteNum = false;
	public boolean onNormalizeNum = false;
	public boolean onSpecialChar = false;
	
	public Normalizer() {
		// TODO Auto-generated constructor stub
	}
    private static final Pattern WHITESPACE = Pattern.compile("(?U)\\s+");
    private static final Pattern CONTROL_FORMAT_CHARS = Pattern.compile("[\\p{Cc}\\p{Cf}]");
    private static final Pattern DOUBLE_QUOTES = Pattern.compile("[\\u02BA\\u201C\\u201D\\u201E\\u201F\\u2033\\u2036\\u275D\\u275E\\u301D\\u301E\\u301F\\uFF02]");
    private static final Pattern SINGLE_QUOTES = Pattern.compile("[\\u0060\\u02BB\\u02BC\\u02BD\\u2018\\u2019\\u201A\\u201B\\u275B\\u275C]");
    private static final Pattern DASH = Pattern.compile("[\\u2012\\u2013\\u2014\\u2015\\u2053]");


     public String Process(String input) {
    	String text = input;
    	if(onTrim) text = text.trim();
        if(onLower) text = text.toLowerCase();
        if(onUpper) text = text.toUpperCase();
        if(onCRLF) text = text.replaceAll("(\r\n|\n\r|\r|\n)", " ");
        if(onSpecialChar) text = removeSpecialChar(text);

        if(onDeleteNum && !onNormalizeNum) text = deleteNum(text);
        if(onNormalizeNum) text = normalizeNum(text);

        
    	if(onUnicode) {
            if (!java.text.Normalizer.isNormalized(text, java.text.Normalizer.Form.NFKC)) {
                text = java.text.Normalizer.normalize(text, java.text.Normalizer.Form.NFKC);
            }
    	}

        if(onWhitespaces) text = WHITESPACE.matcher(text).replaceAll(" ");
        if(onFormatChars) text = CONTROL_FORMAT_CHARS.matcher(text).replaceAll("");
        if(onQuotesDouble) text = DOUBLE_QUOTES.matcher(text).replaceAll("\"");
        if(onQuotesSingle) text = SINGLE_QUOTES.matcher(text).replaceAll("'");
        if(onDash) text = DASH.matcher(text).replaceAll("--");

        return text;
    }
     private String removeSpecialChar(String inputText) {
    	 String currentText = inputText;
    	 String replacement=" ";
    	 currentText = currentText.replace("@", replacement);
    	 currentText = currentText.replace("€", replacement);
    	 currentText = currentText.replace("$", replacement);
    	 currentText = currentText.replace("%", replacement);
    	 currentText = currentText.replace("&", replacement);
     	 currentText = currentText.replace("*", replacement);
    	 currentText = currentText.replace("#", replacement);
    	 currentText = currentText.replace("_", replacement);
     	 currentText = currentText.replace("{", replacement);
    	 currentText = currentText.replace("}", replacement);
    	 currentText = currentText.replace("[", replacement);
    	 currentText = currentText.replace("]", replacement);
    	 currentText = currentText.replace("  ", " ");
    	 return currentText.trim();
     }
     private String normalizeNum(String inputText) {
    	 String searchText = inputText;
    	 String strP="";
    	 String replacement="";
    	 
    	 strP = "/";
    	 replacement="X#";
       	 searchText = searchText.replace("0" +strP, replacement);
    	 searchText = searchText.replace("1" +strP, replacement);
    	 searchText = searchText.replace("2" +strP, replacement);
    	 searchText = searchText.replace("3" +strP, replacement);
    	 searchText = searchText.replace("4" +strP, replacement);
    	 searchText = searchText.replace("5" +strP, replacement);
    	 searchText = searchText.replace("6" +strP, replacement);
    	 searchText = searchText.replace("7" +strP, replacement);
    	 searchText = searchText.replace("8" +strP, replacement);
    	 searchText = searchText.replace("9" +strP, replacement);

    	 strP = "-";
       	 replacement="X#";
       	 searchText = searchText.replace("0" +strP, replacement);
    	 searchText = searchText.replace("1" +strP, replacement);
    	 searchText = searchText.replace("2" +strP, replacement);
    	 searchText = searchText.replace("3" +strP, replacement);
    	 searchText = searchText.replace("4" +strP, replacement);
    	 searchText = searchText.replace("5" +strP, replacement);
    	 searchText = searchText.replace("6" +strP, replacement);
    	 searchText = searchText.replace("7" +strP, replacement);
    	 searchText = searchText.replace("8" +strP, replacement);
    	 searchText = searchText.replace("9" +strP, replacement);

    	 strP = ".";
       	 replacement="XN";
       	 searchText = searchText.replace("0" +strP, replacement);
    	 searchText = searchText.replace("1" +strP, replacement);
    	 searchText = searchText.replace("2" +strP, replacement);
    	 searchText = searchText.replace("3" +strP, replacement);
    	 searchText = searchText.replace("4" +strP, replacement);
    	 searchText = searchText.replace("5" +strP, replacement);
    	 searchText = searchText.replace("6" +strP, replacement);
    	 searchText = searchText.replace("7" +strP, replacement);
    	 searchText = searchText.replace("8" +strP, replacement);
    	 searchText = searchText.replace("9" +strP, replacement);
    	 
    	 strP = ",";
       	 replacement="XN";
    	 searchText = searchText.replace("0" +strP, replacement);
    	 searchText = searchText.replace("1" +strP, replacement);
    	 searchText = searchText.replace("2" +strP, replacement);
    	 searchText = searchText.replace("3" +strP, replacement);
    	 searchText = searchText.replace("4" +strP, replacement);
    	 searchText = searchText.replace("5" +strP, replacement);
    	 searchText = searchText.replace("6" +strP, replacement);
    	 searchText = searchText.replace("7" +strP, replacement);
    	 searchText = searchText.replace("8" +strP, replacement);
    	 searchText = searchText.replace("9" +strP, replacement);
    	 
       	 replacement="X";
    	 searchText = searchText.replace("0", replacement);
    	 searchText = searchText.replace("1", replacement);
    	 searchText = searchText.replace("2", replacement);
    	 searchText = searchText.replace("3", replacement);
    	 searchText = searchText.replace("4", replacement);
    	 searchText = searchText.replace("5", replacement);
    	 searchText = searchText.replace("6", replacement);
    	 searchText = searchText.replace("7", replacement);
    	 searchText = searchText.replace("8", replacement);
    	 searchText = searchText.replace("9", replacement);
    	 return searchText;
     }
     private String deleteNum(String inputText) {
    	 String searchText = inputText;
    	 String strP="";
    	 String replacement="";
    	 
    	 strP = "/";
       	 searchText = searchText.replace("0" +strP, replacement);
    	 searchText = searchText.replace("1" +strP, replacement);
    	 searchText = searchText.replace("2" +strP, replacement);
    	 searchText = searchText.replace("3" +strP, replacement);
    	 searchText = searchText.replace("4" +strP, replacement);
    	 searchText = searchText.replace("5" +strP, replacement);
    	 searchText = searchText.replace("6" +strP, replacement);
    	 searchText = searchText.replace("7" +strP, replacement);
    	 searchText = searchText.replace("8" +strP, replacement);
    	 searchText = searchText.replace("9" +strP, replacement);

    	 strP = "-";
       	 searchText = searchText.replace("0" +strP, replacement);
    	 searchText = searchText.replace("1" +strP, replacement);
    	 searchText = searchText.replace("2" +strP, replacement);
    	 searchText = searchText.replace("3" +strP, replacement);
    	 searchText = searchText.replace("4" +strP, replacement);
    	 searchText = searchText.replace("5" +strP, replacement);
    	 searchText = searchText.replace("6" +strP, replacement);
    	 searchText = searchText.replace("7" +strP, replacement);
    	 searchText = searchText.replace("8" +strP, replacement);
    	 searchText = searchText.replace("9" +strP, replacement);

    	 strP = ".";
    	 searchText = searchText.replace("0" +strP, replacement);
    	 searchText = searchText.replace("1" +strP, replacement);
    	 searchText = searchText.replace("2" +strP, replacement);
    	 searchText = searchText.replace("3" +strP, replacement);
    	 searchText = searchText.replace("4" +strP, replacement);
    	 searchText = searchText.replace("5" +strP, replacement);
    	 searchText = searchText.replace("6" +strP, replacement);
    	 searchText = searchText.replace("7" +strP, replacement);
    	 searchText = searchText.replace("8" +strP, replacement);
    	 searchText = searchText.replace("9" +strP, replacement);
    	 
    	 strP = ",";
    	 searchText = searchText.replace("0" +strP, replacement);
    	 searchText = searchText.replace("1" +strP, replacement);
    	 searchText = searchText.replace("2" +strP, replacement);
    	 searchText = searchText.replace("3" +strP, replacement);
    	 searchText = searchText.replace("4" +strP, replacement);
    	 searchText = searchText.replace("5" +strP, replacement);
    	 searchText = searchText.replace("6" +strP, replacement);
    	 searchText = searchText.replace("7" +strP, replacement);
    	 searchText = searchText.replace("8" +strP, replacement);
    	 searchText = searchText.replace("9" +strP, replacement);
    	 
    	 searchText = searchText.replace("0", replacement);
    	 searchText = searchText.replace("1", replacement);
    	 searchText = searchText.replace("2", replacement);
    	 searchText = searchText.replace("3", replacement);
    	 searchText = searchText.replace("4", replacement);
    	 searchText = searchText.replace("5", replacement);
    	 searchText = searchText.replace("6", replacement);
    	 searchText = searchText.replace("7", replacement);
    	 searchText = searchText.replace("8", replacement);
    	 searchText = searchText.replace("9", replacement);
    	 return searchText;
     }
}
