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
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

import music.TimingPair;
import music.objects.IllegalMarking;
import music.objects.MusicalObjects.Beat;
import music.objects.MusicalObjects.Measure;
import music.objects.Token;
import music.objects.TokenLine;


/**
 * This class is responsible for parsing input strings into music producing token lines,
 * and for breaking up those token lines into metered measures. 
 * 
 * one first calls liner measurer's constructor to create the song from a file.
 * @author Jonathan A. Saddler
 *
 */
public class LinerMeasurer {
	
	public static List<String> weedComments(List<String> allLines)
	{
		LinkedList<String> copiedLines = new LinkedList<String>();
		Iterator<String> lines = allLines.iterator();
		while(lines.hasNext()) {
			String next = lines.next().trim();
			if(!isCommentLine(next))
				copiedLines.add(next);
		}
		return copiedLines;
	}
	
	
	 
	
	public static List<String> weedInitialComments(List<String> allLines)
	{
		LinkedList<String> copiedLines = new LinkedList<String>();
		Iterator<String> lines = allLines.iterator();
		while(lines.hasNext()) {
			String next = lines.next().trim();
			if(isCommentLine(next)) 
				lines.remove();
			break;
		}
//		while(lines.hasNext()) {
//			copiedLines.add(lines.next().trim());
//		}
		return copiedLines;
	}
	
	public static boolean isCommentLine(String line)
	{
		return line.startsWith("//");
	}
	
	public TokenLine[] song;
	boolean[] commentedSongLines;
	static int extraLines;
	
	public LinerMeasurer(File songFile) throws IOException
	{
		extraLines = 0;
		
		List<String> remaining = Files.readAllLines(songFile.toPath());
//		remaining = new ArrayList<String>(weedInitialComments(remaining));
		
		remaining = new ArrayList<String>(weedComments(remaining));
		int firstBreak = getNextBreak(remaining);
		song = new TokenLine[firstBreak];
		remaining = initialSong(remaining, song);
		parseAndJoinLines(remaining, song);	
	}
	public static class New
	{
		public static TokenLine[] parseNewFile(File songFile) throws IOException
		{
			List<String> remaining = Files.readAllLines(songFile.toPath());
			return createLines(remaining);
		}
		/**
		 * Returns 
		 * A) the index of the first empty line found in the list found between 0 and list.size() exclusive, 
		 * or list.size() if there was no line break found in the list.<br>
		 * B) the number of comment lines found in the lines found. 
		 * @param strings
		 * @return
		 */
		public static int[] getInitialNextBreak(List<String> strings)
		{
			int i, uselessLines;
			boolean breakOnNextEmpty = false;
			for(i = 0, uselessLines = 0; i < strings.size(); i++) {
				String next = strings.get(i).trim();
				if(next.isEmpty())
					if(breakOnNextEmpty) 
						break;
					else
						uselessLines++;
				else {
					if(isCommentLine(next))
						uselessLines++;
					breakOnNextEmpty = true;
				}
			}
			return new int[]{i, uselessLines};
		}
		
		public static TokenLine[] createLines(String... input)
		{
			return createLines(Arrays.asList(input));
		}
		public static TokenLine[] createLines(List<String> input)
		{
			// first line. 
			int[] breaks = getInitialNextBreak(input);
			int stanzaBreak = breaks[0];
			int nothingLines = breaks[1];
			// the song has the number of lines
			// equal to the number of lines before the first break minus the lines that don't get parsed.
			int songLines = stanzaBreak - nothingLines;
			TokenLine[] song = new TokenLine[songLines];
			int assignStanza;
			
			// loop through each stanza
			int totalLine = 1;
			for(assignStanza = 1; !input.isEmpty(); assignStanza++) {
				// loop through each line in the stanza
				for(int line = 0, assignLine = 0; line < stanzaBreak; line++, totalLine++) {
					String nextL = input.get(line).trim();
					if(!nextL.isEmpty() && !isCommentLine(nextL)) {
						// if it's not a comment or empty line, parse it as music
						if(assignLine > songLines) 
							// if we have gone over the  max amount of lines in the stanza throw an exception
							throw new IllegalMarking(assignLine + " lines specified in stanza " + assignStanza + " at line " + totalLine + ".\n"
									+ "This song can only have " + songLines + " lines per stanza.");
						// assign the line
						song[assignLine++] = NoteTokenizer.allTokensFromLine(nextL, totalLine, assignStanza);
					}
				}
				// get the next stanza
				input = input.subList(stanzaBreak, input.size());
				int s = getNextStanza(input);
				if(s == -1)
					break;
				input = input.subList(s, input.size());
				totalLine += s;
				stanzaBreak = getNextBreak(input);
			}
			return song;
		}
	}
	
	
	/**
	 * If there are lines remaining after the initial stanza, join those lines to the ends
	 * of the initial stanza.
	 * @param remainingLines
	 * @param finalLines
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
	 * Get the list of lines that represents the first line of the song.
	 * If any of these lines are commented
	 * @param gottenLines
	 * @param song
	 * @return
	 */
	public List<String> initialSong(List<String> gottenLines, TokenLine[] song)
	{
		
		int firstBreak = getNextBreak(gottenLines);
		commentedSongLines = new boolean[firstBreak];
		for(int i = 0; i < firstBreak; i++) {
			String line = gottenLines.get(i);
			song[i] = NoteTokenizer.allTokensFromLine(line, i+1, 1);
			if(isCommentLine(line)) 
				commentedSongLines[i] = true;
		}
		
		return gottenLines.subList(firstBreak, gottenLines.size());
	}
	/**
	 * Returns the index of the first empty line found in the list found between 0 and list.size() exclusive, 
	 * or list.size() if there was no line break found in the list.
	 */
	public static int getNextBreak(List<String> strings)
	{
		if(strings.isEmpty())
			return -1;
		
		for(int i = 0; i < strings.size(); i++) {
			String next = strings.get(i);
			if(next.trim().isEmpty()) 
				return i;
		}
		return strings.size();
	}
	public static int getNextStanza(List<String> strings)
	{
		if(strings.isEmpty())
			return -1;
		
		for(int i = 0; i < strings.size(); i++) {
			String next = strings.get(i);
			if(!next.trim().isEmpty() && isCommentLine(next))
				return i;
		}
		return -1;
	}
	
	/**
	 * Returns the index of the first empty line found in the list found between 0 and list.size() exclusive, 
	 * or list.size() if there was no line break found in the list between the indices of startAt and list.size().  
	 * If nonTrivial is specified, then the first set of empty lines in the list are not considered. 
	 */
	public static int getNextBreak(List<String> strings, int startAt, boolean nonTrivial)
	{
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
	
	public static List<TimingPair> generateTonesMidi(Beat[] notes, Synthesizer synth, int volume, boolean addHarmonic) 
			throws MidiUnavailableException
	{
	    MidiChannel[] channels = synth.getChannels();
	    MidiChannel pianoChannel = channels[0];
	    List<TimingPair> tp = new ArrayList<TimingPair>();
		for(int i = 0; i < notes.length; i++) {
			Beat nextB = notes[i];
			TimingPair t = new TimingPair(nextB, pianoChannel, volume);
			tp.add(t);
		}
		return tp;
	}
}
