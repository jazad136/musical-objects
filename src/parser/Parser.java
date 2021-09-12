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

import java.util.ArrayList;

import music.objects.MusicalObjects.Beat;
import music.objects.MusicalObjects.Instrument;
import music.objects.MusicalObjects.Meter;
import music.objects.MusicalObjects.Note;
import music.objects.MusicalObjects.Rest;
import music.objects.MusicalObjects.Strike;
import music.objects.MusicalObjects.Volume;
import music.objects.NoteSynchronizer;
import music.objects.Token;
import music.objects.Token.BeatToken;
import music.objects.Token.InstrumentToken;
import music.objects.Token.IntervalToken;
import music.objects.Token.MeasureToken;
import music.objects.Token.MeterToken;
import music.objects.Token.NoteToken;
import music.objects.Token.RestToken;
import music.objects.Token.ScaleToken;
import music.objects.Token.StrikeToken;
import music.objects.Token.VolumeToken;
import music.objects.TokenLine;


/** 
 * This class is responsible for helping interpret the content found in Musical Objects
 * tokens as playable song parts, with memory values that can be interpreted by an encoder.
 * 
 * Tokens from a list are processed in order from start to finish, and readded
 * to a list of "Beat" objects
 */
public class Parser {
	public static enum ParserHelperType {BEAT, INSTRUMENT, VOLUME};
	
	public Parser() { }
	public static Beat[] beatTokensToBeats(TokenLine parsedTokens) {
		ArrayList<Beat> toReturn = new ArrayList<Beat>();
		for(Token input : parsedTokens) {
			if(input instanceof MeasureToken) 
				continue;
			else if(input instanceof ScaleToken) 
				continue;
			else if(input instanceof InstrumentToken)
				continue;
			else if(input instanceof VolumeToken)
				continue;
			else {
				Beat inputBeat = toBeat(input);
				toReturn.add(inputBeat);
			}
		}
		return toReturn.toArray(new Beat[0]);
	}
	
	public static Instrument toInstrument(Token input)
	{
		InstrumentToken iT = (InstrumentToken)input;
		if(iT.volumeSetting == Instrument.SELECTION_UNCONFIRMED)
			return new Instrument(iT.patch, iT.patchSelection, iT.homePitch, NoteSynchronizer.defaultVolume);
		else
			return new Instrument(iT.patch, iT.patchSelection, iT.homePitch, iT.volumeSetting);
	}
	public static Meter toMeter(Token input)
	{
		MeterToken mt = (MeterToken)input;	
		return new Meter(mt.beatsPerMeasure, mt.getsTheBeat, mt.getsTheBeatDuration);
	}
	
	
	public static int toVolumeSetting(Token input) {
		return ((VolumeToken)input).volumeSetting;
	}
	public static Volume toVolume(Token input) { 
		int newV = ((VolumeToken)(input)).volumeSetting;
		return new Volume(newV);
	}
	
	public static Beat toBeat(Token input)
	{
		BeatToken tData = (BeatToken)input;
		Beat toReturn = null;
		
		if(input instanceof NoteToken) {
			NoteToken noteT = (NoteToken)input;
			toReturn = new Note(noteT.count, noteT.pitch, noteT.instrument.volume, noteT.playSpeed);
		}
		else if(input instanceof RestToken) {
			RestToken restT = (RestToken)input;
			toReturn = new Rest(restT.count, restT.playSpeed);
		}
		else if(input instanceof IntervalToken) {
			IntervalToken intvT = (IntervalToken)input;
			toReturn = new Note(intvT.count, intvT.pitch, intvT.instrument.volume, intvT.playSpeed);
		}
		else {
			StrikeToken soundT = (StrikeToken)input;
			toReturn = new Strike(soundT.count, soundT.sound, soundT.instrument.volume, soundT.playSpeed);
		}
		toReturn.playsWith = tData.instrument;
		return toReturn;
	}
	
}
