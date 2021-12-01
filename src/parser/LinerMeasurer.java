/**
 *   Copyright Jonathan A. Saddler 2021. 
 *
 *   Musical Objects is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, version 3 of the License only.
 *   
 *   Musical Objects is distributed in the hope that it will be useful,
 *   but without any warranty; without even the implied warranty of
 *   merchantability or fitness for a particular purpose.  See the
 *   GNU General Public License for more details. 
 *   
 *   You should have received a copy of the GNU General Public License
 *   along with Musical Objects. If not, see https://www.gnu.org/licenses.
 *   A copy of this license can be found at the top directory or "root-level" directory 
 *   of this project where downloaded from GitHub. 
 */

package parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import music.objects.IllegalMarking;
import music.objects.TokenLine;

/**
 * The LinerMeasurer class is responsible for parsing input strings into music producing token lines,
 * and for breaking up those token lines into metered measures. 
 * 
 * To create a song from a file, use liner measurer's constructor to build a TokenLine array
 * where each token line represents tracks of the song to be played. 
 * @author Jonathan A. Saddler, Ph. D. 
 */
public class LinerMeasurer {
	
	
	public TokenLine[] song;
	boolean[] commentedSongLines;
	static int extraLines;
	
	/**
	 * Instantiate a new LinerMeasurer object. The song file passed in is
	 * translated into a list of Strings, each string containing one line of the
	 * input file songFile. Comments are weeded out, the initial song is built, and the
	 * rest of the song is appended to lines of the initial song. 
	 * Upon reaching each line, each String is broken into tokens using NoteTokenizer, 
	 * and tokens are attached to one cell of a TokenLine array depending 
	 * on the line of its stanza. The outcome is a song array containing tokenlines that
	 * can be encoded into a playable song. 
	 */
	public LinerMeasurer(File songFile) throws IOException {
		extraLines = 0;
		List<String> remaining = Files.readAllLines(songFile.toPath());
//		song = initialSong(remaining);
		remaining = new ArrayList<String>(weedComments(remaining));
		int firstBreak = getNextBreak(remaining, 0, true);
		song = new TokenLine[firstBreak];
		remaining = initialSong(remaining, song);
		parseAndJoinLines(remaining, song);	
	}
	
	/**
	 * Get the lines that constitute the first stanza of the song, the first
	 * lines found in the input, up until the first line break. 
	 * <br>
	 * Known problem: if comments appear on the last line or between the first
	 * and last line, this method fails to recognize the number of lines in the song
	 * properly. 
	 */
	public List<String> initialSong(List<String> gottenLines, TokenLine[] song) {
		int firstBreak = getNextBreak(gottenLines, 0, true);
		

		for(int i = 0, j = 0; i < firstBreak; i++) {
			String line = gottenLines.get(i);
			song[j] = NoteTokenizer.allTokensFromLine(line, i+1, 1);
			j++;
		}
		
		return gottenLines.subList(firstBreak, gottenLines.size());
	}
	
	public TokenLine[] initialSong(List<String> gottenLines) {
		int firstBreak = getNextBreak(gottenLines, 0, true);	
		ArrayList<TokenLine> song = new ArrayList<>();
		for(int i = 0, j = 0; i < firstBreak; i++) {
			String line = gottenLines.get(i);
			if(line.trim().isEmpty() || isCommentLine(line))
				continue;
			song.add(NoteTokenizer.allTokensFromLine(line, i+1, 1));
			gottenLines.remove(i);
			j++;
		}
		return song.toArray(new TokenLine[0]);
	}
	
	/**
	 * Returns the index of the first empty line found in the list found between 0 and list.size() exclusive, 
	 * or list.size() if there was no line break found in the list.
	 * Line breaks consist of an empty or whitespace-only string. 
	 */
	private static int getNextBreak(List<String> strings) {
		if(strings.isEmpty())
			return -1;
		
		for(int i = 0; i < strings.size(); i++) {
			String next = strings.get(i);
			if(next.trim().isEmpty()) 
				return i;
		}
//		for(int i = 0; i < strings.size(); i++) 
//			if(strings.get(i).isBlank()) 
//				return i;
		
		return strings.size();
	}
	
	/** Remove any line that matches the criteria for being a comment
	 * from the input list of strings, and return the lines remaining
	 * after comments are removed.
	 */
	private static List<String> weedComments(List<String> allLines) {
		LinkedList<String> copiedLines = new LinkedList<String>();
		Iterator<String> lines = allLines.iterator();
		while(lines.hasNext()) {
			String next = lines.next().trim();
			if(!isCommentLine(next))
				copiedLines.add(next);
		}
		return copiedLines;
	}
	
	/** Return true if line starts with '//' and false otherwise */
	private static boolean isCommentLine(String line) { return line.startsWith("//"); }
	
	
	/**
	 * For each string in remaining lines, find the line in finalLines 
	 * it pertains to, and then join the elements of new lines to the end of it. 
	 * The end goal of this method is to join together the full track remaining in the song 
	 * in lines below the first segment of a track that appears in a song atop the file.
	 * 
	 *  If there are too many lines in a stanza in the input, this will
	 *  be reported as an error. (this method has a known bug, pertaining to the line
	 *  of this error not being as accurate as it could be.) 
	 */
	public void parseAndJoinLines(List<String> remainingLines, TokenLine[] finalLines)
	{	
		for(int i = 0; i < finalLines.length; i++) 
			finalLines[i] = NoteTokenizer.deepParseTokens(finalLines[i]);
		int nextBreak = getNextBreak(remainingLines);
		
		int lineIndex = finalLines.length + 1;
		int stanzaIndex = 2;
		
		while(!remainingLines.isEmpty()) {
			nextBreak = getNextBreak(remainingLines, 0, true);
			int l = 0;
			for(int i = 0; i < nextBreak; i++) {
				String nextRaw = remainingLines.get(i);
				if(nextRaw.trim().isEmpty())
					continue;
//				if(commentedSongLines[i]) {
//					continue;
//				}
				if(l == finalLines.length)
					throw new IllegalMarking("Too many lines specified in stanza at line " + (lineIndex + extraLines + 1) + ".\n"
							+ "This song can only have " + finalLines.length + " lines per stanza.");
				TokenLine nextLine = NoteTokenizer.allTokensFromLine(nextRaw, lineIndex++, stanzaIndex);
				
				nextLine = NoteTokenizer.deepParseTokens(nextLine);
				finalLines[l].addAll(nextLine);
				l++;
			}
			remainingLines = remainingLines.subList(nextBreak, remainingLines.size());
			stanzaIndex++;
		}
	}
	
	
	
	
	/**
	 * Returns the index of the first empty line found in the list found between 0 and list.size() exclusive, 
	 * or list.size() if there was no line break found in the list between the indices of startAt 
	 * and list.size().  
	 * 
	 * If nonTrivial is specified, then the first set of empty or comment lines in the input are not
	 * considered, and a break will be searched for after the first non-comment non-empty line 
	 * is found in the input. 
	 */
	private static int getNextBreak(List<String> strings, int startAt, boolean nonTrivial) {
		if(startAt >= strings.size())
			return strings.size();
		else if(startAt < 0)
			startAt = 0;
		boolean breakOnNext = !nonTrivial; // if nonTrivial is specified, then 
										   // we don't want to break on the next empty line we find
		int i;
		for(i = startAt; i < strings.size(); i++) {
			String next = strings.get(i).trim();
			if(next.isEmpty() || isCommentLine(next)) {
				extraLines++;
				if(breakOnNext)
					break;
			}
			else
				breakOnNext = true;
		}		
		return i;
	}
}
