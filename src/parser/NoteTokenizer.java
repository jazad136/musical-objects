package parser;

import static music.objects.MusicalObjects.Pitch.UNK;
import static music.objects.MusicalObjects.Sound.UNKS;

import java.util.StringTokenizer;
import java.util.regex.Pattern;

import music.objects.IllegalMarking;
import music.objects.MusicalObjects;
import music.objects.IllegalMarking.Reason;
import music.objects.MusicalObjects.Count;
import music.objects.MusicalObjects.Interval;
import music.objects.MusicalObjects.Pitch;
import music.objects.MusicalObjects.Sound;
import music.objects.Token;
import music.objects.Token.InstrumentToken;
import music.objects.Token.IntervalToken;
import music.objects.Token.MeasureToken;
import music.objects.Token.MeterToken;
import music.objects.Token.NoteToken;
import music.objects.Token.RepeatToken;
import music.objects.Token.RestToken;
import music.objects.Token.ScaleToken;
import music.objects.Token.StrikeToken;
import music.objects.Token.VolumeToken;
import music.objects.TokenLine;

/**
 * This class is responsible for turning streams of input strings into tokens. 
 */
public class NoteTokenizer {
	
	public static boolean foundMeasureSep = false;
	public static boolean foundRepeatSep = false;
	/**
	 * Construct a tokenizer that parses notes in a music file. 
	 */
	public NoteTokenizer()
	{
	}
	
	/**
	 * Weed out the tokens that belong to the line specified.
	 * Read lineString, and take in information related to the line number and stanza number.  
	 * @param lineString
	 * @return
	 */
	public static TokenLine allTokensFromLine(String lineString, int lineNum, int stanzaNum)
	{
		int measureNumber = 1;
		TokenLine toReturn = new TokenLine();
		char[] charArray = lineString.toCharArray();
		int[] run = exhaustNextToken(lineString, 0);
		for(int i = 0; i < charArray.length && run[1] != -1; i++) {
			Token newToken = new Token();
			newToken.tokenLiteral = lineString.substring(run[0], run[1]);
			newToken.line = lineNum;
			newToken.character = run[0]+1;
			newToken.stanza = stanzaNum;
			newToken.measureNumber = measureNumber;
			if(foundMeasureSep) {
				toReturn.add(new Token.MeasureToken(newToken));
				foundMeasureSep = false;
				measureNumber++;
			}
//			else if(foundRepeatSep) { 
//				toReturn.add(new Token.RepeatToken(newToken));
//				foundRepeatSep = false;
//			}
			else {
				 // only found a measuresep
				toReturn.add(newToken);
			}	
			i = run[1];
			run = exhaustNextToken(lineString, i);			
		}
		return toReturn;
	}
	
	/**
	 * Recognizes the following separator characters
	 * '\t'	OR U+0009	HORIZONTAL TABULATION<br>
	 * '\n'	OR U+000A	NEW LINE<br>
	 * '\f' OR U+000C	FORM FEED<br>
	 * '\r'	OR U+000D	CARRIAGE RETURN<br>
	 * '&nbsp;&nbsp;&nbsp;'	OR U+0020	SPACE
	 */
//	static boolean isSeparator(char c) {
//		boolean isControl = (c <= 0x0020);
//		boolean isWhite = Pattern.matches("[\\s]", ""+c); 
////	            (((((1L << 0x0009) | // HORIZONTAL TABULATION
////	            (1L << 0x000A) | // NEW LINE
////	            (1L << 0x000C) | // FORM FEED
////	            (1L << 0x000D) | // CARRIAGE RETURN
////	            (1L << 0x0020)) >> c) & 1L) != 0); // SPACE
//		boolean isExtra = c == ',';
//		boolean isMeasureSep = c == '|';
//		foundMeasureSep = isMeasureSep;
//		return isControl || isWhite || isExtra || isMeasureSep;
//	}
	static boolean isSeparator(char c) {
		boolean isControl = (c <= 0x0020);
		boolean isWhite = Pattern.matches("[\\s]", ""+c); 
//	            (((((1L << 0x0009) | // HORIZONTAL TABULATION
//	            (1L << 0x000A) | // NEW LINE
//	            (1L << 0x000C) | // FORM FEED
//	            (1L << 0x000D) | // CARRIAGE RETURN
//	            (1L << 0x0020)) >> c) & 1L) != 0); // SPACE
		boolean isExtra = c == ',';
		boolean isMeasureSep = c == '|';
		boolean isRepeatSep = c == ':';
		foundMeasureSep = isMeasureSep;
//		foundRepeatSep = isRepeatSep;
//		return isControl || isWhite || isExtra || isMeasureSep || isRepeatSep;
		return isControl || isWhite || isExtra || isMeasureSep;
	}
	/**
	 * Read the input to find the start and end of a token, if any example
	 * found after startAt within the provided input.
	 * If the string contains nothing but whitespace, it is a trivial string, 
	 *   and -1 is returned in the second array position. 
	 * Else, the start of the next token is returned in the first array position. 
	 * The end of the next token + 1 is returned in the second array position. 
	 */
	public static int[] exhaustNextToken(String input, int startAt)
	{
		int[] tokenRun = new int[2];
		char[] charArray = input.toCharArray();		
		while(startAt < charArray.length 
		&& isSeparator(charArray[startAt]) 
		&& !(foundMeasureSep)
//		&& !(foundRepeatSep)
		)
			startAt++;
		
		if(startAt >= charArray.length) {
			tokenRun[1] = -1;
			return tokenRun;
		}
//		if(foundMeasureSep || foundRepeatSep) {
		if(foundMeasureSep) { 
			tokenRun[0] = startAt;
			tokenRun[1] = startAt+1;
			return tokenRun;
		}
		
//		int end;
		tokenRun[0] = startAt;
		tokenRun[1] = -1;
//		boolean trivial = true;
		int cPos;
		for(cPos = startAt; cPos < charArray.length; cPos++) {
			if(isSeparator(charArray[cPos])) break;
//			else trivial = false;
			tokenRun[1] = cPos+1;
		}
//		if(trivial)
//			tokenRun[1] = -1;
//		else 
//			tokenRun[1] = cPos;
		
		
		return tokenRun;
	}
	
	public static TokenLine deepParseTokens(TokenLine raw)
	{
		TokenLine toReturn = new TokenLine();
		for(Token t : raw) {
			if(t instanceof MeasureToken) {
				toReturn.add(t);
			}
			else
				toReturn.add(token(t));
		}
		return toReturn;
	}
	
//	public static List<MeasureToken> measureTokens(MeasureToken t) { 
//		System.out.println(t.tokenLiteral);
//		List<MeasureToken> allTks = new LinkedList<>();
//		
//		
//		int barPt = t.tokenLiteral.indexOf('|');
//		int slshPt = t.tokenLiteral.indexOf('/');
//		
//		String left, right;
//		
//		
//		if(barPt != -1) { 
//			left = t.tokenLiteral.substring(0, barPt);
//			right = t.tokenLiteral.substring(barPt+1);
//		}
//		else if(slshPt != -1) {
//			if(slshPt == 0) 
//				throw new IllegalMarking(t, Reason.PITCH);
//			left = t.tokenLiteral.substring(0, slshPt);
//			right = t.tokenLiteral.substring(slshPt+1);
//		}
//		else {
//			left = t.tokenLiteral;
//		}
//		
//		char[] mtks = left.toCharArray();
//		MeasureToken next = firstNext(t, mtks[0]);
//		if(next instanceof NumberToken) { 
//			// numbers don't come first. 
//			throw new IllegalMarking(t, Reason.PITCH);
//		}
//		
//		// split into left and right
//		if(next instanceof BarToken) {
//			if(mtks.length == 1) {
//				allTks.add(next);
//				return allTks;
//			}
//			next = firstNext(t, mtks[1]);
//			
//			if(next instanceof NumberToken) {
//				boolean allDigits = true;
//				int i;
//				for(i = 1; i < mtks.length && allDigits; i++) 
//					allDigits = Character.isDigit(mtks[i]);
//				if(i == mtks.length) {
//					allTks.add(new RepeatDesignatorToken(t, t.tokenLiteral.substring(1), true));
//					return allTks;
//				}
//				next = firstNext(t, mtks[i]);
//				if(next instanceof ColonToken) { 
//					allTks.add(new RepeatDesignatorToken(t, t.tokenLiteral.substring(1,i), true));
//					return allTks;
//				}
//				throw new IllegalMarking(t, Reason.MEASURE);
//			}
//			if(next instanceof LetterToken) { 
//				
//			}
//		}
//		if(next instanceof ColonToken) {
//			if(mtks.length == 1)  
//				throw new IllegalMarking(next, Reason.MEASURE);
//			next = firstNext(t, mtks[1]);
//			if(next instanceof NumberToken) {
//				boolean allDigits = true;
//				for(int i = 2; i < mtks.length && allDigits; i++) 
//					allDigits = Character.isDigit(mtks[i]);
//				
//			}
//		}
//	}
	
	
//	public static MeasureToken firstNext(MeasureToken t, char nextChar) { 
//		if(nextChar == '|')
//			return new BarToken(t);
//		else if(nextChar == ':') 
//			return new ColonToken(t);
//		else if(Character.isDigit(nextChar))
//			return new NumberToken(t);
//		else if(Character.isLetter(nextChar))
//			return new NameToken(t);
//		else
//			return t;
//	}
	public static boolean isVolumeString(String input)
	{
		return Pattern.matches("[vV][0-9][0-9]?[0-9]?[0-9]?", input);
	}
	
	/**
	 * Returns true if input represents a string relevant to repeating measures.<br>
	 * Repeat strings may begin with an underscore, a character, or a number;
	 * and end in a colon (:). 
	 * @param input
	 * @return
	 */
	public static boolean isRepeatRelevantString(String input){
		return Pattern.matches("^([_0-9a-zA-Z])*:[0-9]*", input);
	}
	/**
	 * Returns true if input represents a measure marker.<br>
	 * Measure markers begin with an underscore or character (not a number),
	 * and end in a colon (:)
	 * @param input
	 * @return
	 */
//	public static boolean isMeasureMarkerString(String input)
//	{
//		return Pattern.matches("^([_a-zA-Z])([0-9a-zA-Z])*:", input);
//	}
	public static boolean isRepeatString(String input) { return input.equals(":"); }
	
	public static boolean specialTokenString(String tokenLiteral)
	{
		String target;

		if(tokenLiteral.contains("/")) // not a meter.
			return false;
		if(isVolumeString(tokenLiteral)) 
			return true;
		if(isRepeatRelevantString(tokenLiteral))
			return true;
		
		if(tokenLiteral.contains(".")) 
			target = tokenLiteral.substring(0, tokenLiteral.indexOf('.'));
		else 
			target = tokenLiteral;
		
		
		// special tokens are neither beats, nor sounds, nor counts, and have either uppercase or lowercase letters, not both.
		if(Token.stringToPitch(target) == UNK) // not a pitch
			if(!target.equalsIgnoreCase("r")) // not a rest.
				if(Token.stringToSound(target) == UNKS) // doesn't make a sound. 
					if(!tokenLiteral.contains(".")) // not a count.
//						if(tokenLiteral.matches("[A-Z]*") ^ tokenLiteral.matches("[a-z]-*")) // not mixed case
							return true;
					
		return false;
	}
	public static Token parseSpecialToken(Token token)
	{
		// Instruments are of the form <iname>(<space><iname>)*<space><selector_integer>
		// Volume tokens of the form 'v'<selector_integer>
		
//		Pattern p = Pattern.compile("[1-9]");
//		p.matcher(token.tokenLiteral);
		
		// instrument
		StringTokenizer st = new StringTokenizer(token.tokenLiteral);
		String oldDelims = " \t\n\r\f";
		int count = st.countTokens();
		if(count > 2)  {
			String firstName = st.nextToken();
			String lastName = st.nextToken(oldDelims + "v");
			if(st.hasMoreTokens()) {
				String volume = st.nextToken();
				return new Token.InstrumentToken(token, firstName, lastName, volume);
			}
			else	
				return new Token.InstrumentToken(token, firstName, lastName);
		}
		else if(isVolumeString(token.tokenLiteral)){
			String volume = st.nextToken(oldDelims + "v");
			return new Token.VolumeToken(token, volume);
		}
		else if(isRepeatString(token.tokenLiteral)) {
			return new Token.RepeatToken(token);
		}
//		else if(isRepeatRelevantString(token.tokenLiteral)) {
//			String reference = "";
//			String repCount = "";
//			if(!token.tokenLiteral.startsWith(":")) {
//				reference = st.nextToken(":");
//				repCount = st.nextToken(":" + oldDelims);
//			}
//			else {
				// skip the colon and get the repcount. 
//				repCount = st.nextToken(oldDelims).substring(1);
//				reference = "";
//			}
//			return new Token.RepeatToken(token, reference, repCount);
//		}
		else
			return new Token.InstrumentToken(token, token.tokenLiteral);
		
	}
	
	/**
	 * Parses a note token into a component of the program.
	 * It's all about what it starts with and what it ends with.
	 * If a token contains no dot, it has one part, and the whole string is the left literal. 
	 * If a token has a dot it has two left and right literals. 
	 * 
	 * If a token's left side then is an R, the whole token is a rest type 
	 * If not, and the left and right side are digit numbers, and the literal has a slash, 
	 *    the whole token is a meter type.
	 * If none of these hold, the whole token is not a type at all, throw an error. 
	 */
	public static Token token(Token token)
	{		
		int dotPt = token.tokenLiteral.indexOf('.');
		int slshPt = token.tokenLiteral.indexOf('/');
		
		// If a token contains no dot, it has one part, and the whole string is the left literal. 
		// If a token has a dot it has two left and right literals.
		String left, right;
		left = token.tokenLiteral;
		boolean noRight = false;
		boolean hasSlash = false;
		// separation
		if(dotPt == 0)
			throw new IllegalMarking(token, Reason.PITCH);
		if(dotPt != -1) {
			left = token.tokenLiteral.substring(0, dotPt);
			right = token.tokenLiteral.substring(dotPt+1);
		}
		else if(slshPt != -1){
			left = token.tokenLiteral.substring(0, slshPt);
			right = token.tokenLiteral.substring(slshPt+1);
			hasSlash = true;
		}
		else {
			noRight = true;
			right = "";
		}
		

		if(left.equalsIgnoreCase("r")) 		
			// settle on rest. 
			return new Token.RestToken(token, right);
		// digit facts
		boolean allDigits = true;
		if(noRight) allDigits = false;
		if(allDigits)
			for(char c : left.toCharArray()) 
				if(!Character.isDigit(c)) {
					allDigits = false;
					break;
				}
		if(allDigits)
			for(char c : right.toCharArray()) {
				if(!Character.isDigit(c) && c != ':') {
					allDigits = false;
					break;
				}
			}
		
		if(hasSlash) {
		if(allDigits) {
			
			int colPt = right.indexOf(":");
			if(colPt == -1)
				return new Token.MeterToken(token, left, right);
			else {
				String getsTheBeat = right.substring(0, colPt);
				String duration = right.substring(colPt+1);
				return new Token.MeterToken(token, left, getsTheBeat, duration);
			}
		}
		}
		if(hasSlash)
			throw new IllegalMarking(token, IllegalMarking.Reason.COUNT);
		
		/*If not, and the left hand side parses to a note type, and uses a dot and not a slash, 
		 * the whole token is a note type,  
		 */
		if(left.equalsIgnoreCase("3s")) {
			int x = 1;
		}
		ScaleToken tokenKey = ScaleToken.stringToScale(left);
		if(tokenKey.key != Pitch.UNK) 
			return tokenKey;
		
		Pitch notePitch = Token.stringToPitch(left);
		if(notePitch != Pitch.UNK) {
			return new Token.NoteToken(token, left, right);
		}
		Sound noteSound = Token.stringToSound(left);
		if(noteSound != Sound.UNKS)
			return new Token.StrikeToken(token, left, right);
		
		Interval simpleInt = Token.stringToInterval(left);
		if(simpleInt != Interval.UNKI) 
			return new Token.IntervalToken(token, left, right);
		
		if(specialTokenString(token.tokenLiteral)) 
			return parseSpecialToken(token);
		
		// If neither, the whole token is not a type at all, throw an error.
		throw new IllegalMarking(token, IllegalMarking.Reason.NOTE);
	}
	
	public String getAbbrev(Count c)
	{
		if(c == Count.QTR)
			return "q";
		return "";
	}
	public static class Patterns
	{
		public static Pattern volumeM = Pattern.compile("[vV][0-9][0-9]?[0-9]?[0-9]?");
	}
}
