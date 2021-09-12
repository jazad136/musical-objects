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
package music.objects;

import static music.objects.MusicalObjects.Pitch.UNK;
import static music.objects.MusicalObjects.Count.UNKC;
import static music.objects.MusicalObjects.Sound.UNKS;

import java.util.Arrays;
import java.util.LinkedList;

import static music.objects.MusicalObjects.Interval.*;
import music.objects.MusicalObjects.*;
import music.objects.Token.ScaleToken.Scale;
import music.objects.IllegalMarking.Reason;


public class Token 
{
	public String tokenLiteral;
	public int line;
	public int stanza;
	public int character;
	public int measureNumber;
	public Token()
	{
		tokenLiteral = "";
		line = -1;
		stanza = -1;
		character = -1;
		measureNumber = -1;
	}
	
	public Token(String literal, int line, int stanza, int character)
	{
		this.tokenLiteral = literal;
		this.line = line;
		this.stanza = stanza; 
		this.character = character;
		this.measureNumber = -1;
	}
	
	public Token(Token oldToken) {
		this.tokenLiteral = oldToken.tokenLiteral;
		this.line = oldToken.line;
		this.stanza = oldToken.stanza; 
		this.character = oldToken.character;
		this.measureNumber = oldToken.measureNumber;
	}
	
	public void setBaseCountIfUnset(Meter newMeter) { }
	public void setBasePitchIfUnset(Scale currentScale, Pitch basePitch) { }
	public void setInstrumentIfUnset(Instrument instrument) { }
	
	
	public static class MeasureToken extends Token {
		int measureNumber;
		int jumpCount;
		boolean hasReference;
		
		public MeasureToken(Token rawInput) {
			super(rawInput);
			this.measureNumber = rawInput.measureNumber;
			this.hasReference = false;
		}
		
		public MeasureToken(Token token, int measureNumber) {
			this(token);
			this.measureNumber = measureNumber;
			this.hasReference = false;
		}
		
		public MeasureToken(Token token, int measureNumber, String jumpReference, int jumpCount) {
			this(token, measureNumber);
			if(jumpReference != null) {
				this.hasReference = true;
				this.jumpCount = jumpCount;
			}
		}
		public boolean isTerminal() { return false; }
		
	}
	public static class BarToken extends MeasureToken {
		public BarToken(Token input) { super(input); } 
		public String toString() { return "|"; }
	}
	public static class ColonToken extends MeasureToken { 
		public ColonToken(Token input) { super(input); } 
		public String toString() { return ":"; } 
	}
	public static class NumberToken extends MeasureToken {
		
		public NumberToken(Token input) { 
			super(input);
		}
		public int getValue() { return Integer.parseInt(tokenLiteral); }
		public String toString() { return tokenLiteral; }
	}
	public static class NameToken extends MeasureToken { 
		public NameToken(Token input) { 
			super(input);
		}
		public String getValue() { return tokenLiteral; }
		public String toString() { return tokenLiteral; }
	}
	
	
	public static class MeterToken extends Token {
		public int beatsPerMeasure;
		public Count getsTheBeat;
		public long getsTheBeatDuration;
		
		public MeterToken(Token token, String beatsPerMeasureString, String getsTheBeatString) {
			super(token);
			try {
				this.beatsPerMeasure = Integer.parseInt(beatsPerMeasureString);
			} catch(NumberFormatException e) {
				throw new IllegalMarking(token, IllegalMarking.Reason.METER_BEAT);
			}
			this.getsTheBeat = stringToBeatCount(getsTheBeatString);
			if(getsTheBeat == UNKC)
				throw new IllegalMarking(token, IllegalMarking.Reason.COUNT);
			
			getsTheBeatDuration = Meter.DURATION_UNDEFINED;
		}
		
		public MeterToken(Token token, String beatsPerMeasureString, String getsTheBeatString, String durationString) {
			this(token, beatsPerMeasureString, getsTheBeatString);
			try {
				this.getsTheBeatDuration = Long.parseLong(durationString);
			} catch(NumberFormatException e) {
				throw new IllegalMarking(token, IllegalMarking.Reason.DURATION);
			}
		}
	}
	
	
	public static class BeatToken extends Token {
		public Count count;
		public Instrument instrument;
		public long playSpeed;
		public BeatToken(Token token) {
			super(token);
			instrument = NoteSynchronizer.defaultInstrument;
		}
		
		
	}
	public static class StrikeToken extends BeatToken
	{
		public Sound sound;
		public StrikeToken(Token token, String soundString)
		{
			super(token);
			this.sound = stringToSound(soundString);
			if(sound == UNKS)
				throw new IllegalMarking(token, IllegalMarking.Reason.SOUND);
			this.count = UNKC;
		}
		
		/**
		 * countString can be empty.
		 * @param token
		 * @param soundString
		 * @param countString
		 */
		public StrikeToken(Token token, String soundString, String countString)
		{
			this(token, soundString);
			if(!countString.isEmpty()) {
				Count forcedAttempt = stringToCount(countString);
				if(forcedAttempt == UNKC)
					throw new IllegalMarking(token, IllegalMarking.Reason.COUNT);
				count = forcedAttempt;
			}
		}
		
		public void setBaseCountIfUnset(Meter newMeter) {
			if(count == UNKC) {
				this.count = newMeter.getsTheBeat;
			}
			this.playSpeed = newMeter.getsTheBeatDurationMs;
		}
		public void setInstrumentIfUnset(Instrument instrument) { 
			this.instrument = instrument; 
		}
	}
	
	public static class ScaleToken extends Token
	{	
		public Pitch key;
		public Scale scale;

		public ScaleToken() { 
			key = Pitch.UNK;
//			scale = Scale.UNKSC;
		}
		public ScaleToken(Token token, String keyString)
		{
			super(token);
			ScaleToken result = stringToScale(keyString);
			if(result.key == null) 
				throw new IllegalMarking(token, IllegalMarking.Reason.SCALE);
			this.key = result.key;
			this.scale = result.scale;
		}
		
		
		public static enum Scale3 {
			//                       0   1    2    3    4    5     6    7  major
			//                       0   2    4    5    7    9    11   12  duodective
	        //                          Wl - Wl - Hf - Wl - Wl -  Wl - Hf  jump
			 MAJOR("m",	 			P1,	M2,  M3,  P4,  P5,  M6,   M7,  P8)//interval
			 					   //   Wl   Hf   Wl   Wl   Hf    Wl   Wl
			                       //0   2    3    5    7    8    10   12 - half steps
			,MINOR("mi",            P1, M2,  MI3, P4,  P5,  MI6, MI7,  P8
			)
			;
			final String suffix;
			final String suffixAlias;
			final Interval[] steps;
			Scale3(String suffix, Interval... bases)
			{
				this.suffix = suffix;
				this.suffixAlias = "";
				this.steps = bases;
			}
//			public Pitch toPitch3(Pitch keyPitch, Interval intv) { 
//				
//				if(intv == P1) { 
//					keyPitch.transpose(steps[0].halfSteps);
//				}
//				// middle
//				if(intv == D2) 
//					keyPitch.transpose(steps[1].halfSteps-1);
//				if(intv == A1) 
//					keyPitch.transpose(steps[0].halfSteps+1);
//				
//				
//				if(intv == M2) { 
//					keyPitch.transpose(steps[1].halfSteps);
//				}
//				if(intv == Mi3) {
//					keyPitch.transpose(steps[2].halfSteps);
//				}
//				return keyPitch;
////				if(intv == P)
//			}
//			public Pitch toPitch2(Pitch keyPitch, Interval intv) { 
//				if(intv == P1) {
//					// do nothing
//					keyPitch.transpose(steps[0].halfSteps);
//				}
//				
//				if(intv == A1) {
//					keyPitch.transpose(steps[0].halfSteps + 1);
//				}
//				if(intv == Mi2) { 
//					keyPitch.transpose(steps[1].halfSteps - 1);
//				}
//				// M2
//				if(intv == M2) 
//					keyPitch.transpose(steps[1].halfSteps);
//				if(intv == D3)
//					keyPitch.transpose(steps[1].halfSteps);
//				// A2
//				if(intv == A2) 
//					keyPitch.transpose(steps[1].halfSteps+1);
//				if(intv == Mi3) 
//					keyPitch.transpose(steps[2].halfSteps-1);
//				// M3
//				if(intv == M3)
//					keyPitch.transpose(steps[2].halfSteps);
//				if(intv == D4)
//					keyPitch.transpose(steps[2].halfSteps);
//				
//				
//				return keyPitch;
//			}
			public Pitch toPitch(Pitch keyPitch, Interval intv)
			{
				int transpose = 0;
				int i;
				// after normalizing the interval to be between 1 and 8. 
				for(i = 0; i < steps.length; i++) {
					
					if(intv.halfSteps == steps[i].halfSteps) 
						break;
					else if(intv.halfSteps > steps[i].halfSteps) {
						transpose -= intv.halfSteps - steps[i].halfSteps;
						// here I need to be careful, and I need to designate "distance 
						// between two intervals" 
						
					}
					// here I need to be careful, and I need to designate what
					// is a whole and half step
					transpose += steps[i].halfSteps;
				}
				transpose += steps[i].halfSteps;
				return keyPitch.transpose(transpose);
			}
		}
		public static enum Scale2 { 
			//                    1  2  3  4  5  6  7  8  9  10 11 12
			//                    DO DI RE RI MI FA FI SO SI LA LI TI 
			 MAJOR("m"           ,W, Z, W, Z, H, W, Z, W, Z, W, Z, H)
			,MINOR("mi"          ,W, Z, H, W, Z, W, Z, H, W, Z, W, Z)
			,MELODIC_MINOR("mim")
			,OCTATONIC("o", Z,Z,H);
			
			final String suffix;
			final String suffixAlias;
			final Interval[] steps;
			Scale2(String suffix, Interval... steps)
			{
				this.suffix = suffix;
				this.suffixAlias = "";
				this.steps = steps;
			}
//			public int toTransposed(Interval intv) { 
//				int[] stepTypes = Arrays.asList(steps).stream().mapToInt(iv -> iv.halfSteps).toArray();
//				int tValue = 0;
//				for(int i = 0; i < stepTypes.length; i++) {
//					// W W H W W W H
//					switch(stepTypes[i]) { 
//					case 1:
//						switch(intv) { 
//						case A7: 
//						case M7: case D8: 
//						case Mi7: case A6: 
//						case M6: case D7: 
//						case Mi6: case A5: 
//						case P5: case D6: 
//						case A4: case D5: 
//						case P4: case A3: 
//						case M3: case D4:  
//						case Mi3: case A2: intv = intv.transpose(-1); 
//						case M2: case D3:  intv = intv.transpose(-1);
//						case Mi2: case A1: intv = intv.transpose(-1); 
//						case P1: case D2:  return 0; // P1 = 1
//						}
//					case 2:
//						tValue += intv.halfSteps;
//						switch(intv) { 
//						case A7: case P8: intv = intv.transpose(-2);  break;
//						case M7: case D8: intv = intv.transpose(-1);  break;
//						case Mi7: case A6: intv = intv.transpose(-1); break;
//						case M6: case D7: intv = intv.transpose(-2);  break;
//						case Mi6: case A5:intv = intv.transpose(-1);  break;
//						case P5: case D6: intv = intv.transpose(-2);  break;
//						case A4: case D5: intv = intv.transpose(-1);  break;
//						case P4: case A3: intv = intv.transpose(-2);  break;
//						case M3: case D4: intv = intv.transpose(-2);  break;
//						case Mi3: case A2:intv = intv.transpose(-1);  break;
//						case M2: case D3: intv = intv.transpose(-2);  break;
//						case Mi2:case A1: intv = intv.transpose(-1);  break;
//						case P1: case D2: break;
//						case H: 
//						
//						case UNKI: 
//						case W: 
//						case Z: 
//						default: 
//						}
//					}
//					
//					}
//				}
//			public Pitch toPitch3(Pitch keyPitch, Interval intv) {
//				int jump = 0;
//				int needIndices = 1;
//				switch(intv) {
//				case P1:
//				case D2: return keyPitch;
//				case Mi2: 
//				case A1: needIndices = 1;
//				}
//				int tpc = 0;
//				boolean zsOff = false;
//				for(int i = needIndices; i > 0; i--) {
//					if(steps[i] == Z && !zsOff) {
//						tpc++;
//					}
//					else {
//						zsOff = true;
//					}
//				}
//				return Pitch.UNK;
//				
//			}
		}
		public static enum Scale {
			//                       0   1    2    3    4    5    6    7  major
	        //                          Wl - Wl - Hf - Wl - Wl - Wl - Hf
			MAJOR("m",	 				W,   W,   H,   W,   W,   W,   H)
		    //                         -Wl - Hf - Wl - Wl - Hf - Wl - Wl  minor 
			,NATURAL_MINOR("mi","min",  W,   H,   W,   W,   H,   W,   W) 
			//                       0   1    2    3    4    5    6    7      
			//                         -Wl - Hf - Wl - Wl - Hf - A2 - Hf 
			,MELODIC_MINOR("mim",       M2, MI2, M2,   M2, MI2, AUG2, MI2) 
			// minor harmonic           
			,HARMONIC_MINOR("mih")      
			//                       0    1    2    3    4    5    6    7    8    9   10   11   12
	        //                          -Hf - Hf - Hf - Hf - Hf - Hf - Hf - Hf - Hf - Hf - Hf - Hf
			,CHROMATIC("chr", 		    MI2, MI2, MI2, MI2, MI2, MI2, MI2, MI2, MI2, MI2, MI2, MI2)
			,PENTATONIC_MAJOR("map",      M2,  M2, MI3,  M2, MI3)  
			//                            -2   -2   -3   -2   -3  
			// 						  0    1    2    3    4    5
			,PENTATONIC_MINOR("mip",      M3,  M2, M2,  MI3,  M2)
			,OCTATONIC("o",             H,W,H,W,H,W,H,H,W)
			,OCTATONIC_MINOR("mio",     W,H,W,H,W,H,W,H)
			
			
//			, UNKSC()
			;
			// Scale + key = 
			// Pitch + Scale + Key = Note
			// Key [+ Scale] + Interval = Note
			final String suffix;
			final String suffixAlias;
			final Interval[] steps;
			final Interval[] reversed;
			private Scale() {
				this.suffix = "";
				this.suffixAlias = "";
				this.steps = new Interval[0];
				this.reversed = new Interval[steps.length];
				for(int i = 0, j = steps.length-1; i < steps.length; i++, j--) 
					this.reversed[i] = steps[j];
			}

			private Scale(String suffix, Interval... steps)
			{
				this.suffix = suffix;
				this.suffixAlias = "";
				this.steps = steps;
				this.reversed = new Interval[steps.length];
				for(int i = 0, j = steps.length-1; i < steps.length; i++, j--) 
					this.reversed[i] = steps[j];
			}
			
			private Scale(String suffix, String suffixAlias, Interval... steps)
			{
				this.suffix = suffix;
				this.suffixAlias = suffixAlias;
				this.steps = steps;
				this.reversed = new Interval[steps.length];
				for(int i = 0, j = steps.length-1; i < steps.length; i++, j--) 
					this.reversed[i] = steps[j];
			}
			
			
//			public Pitch toPitch2(Pitch keyPitch, Interval intv) {
//				int tpc = transposeCount(intv);
//				return keyPitch.transpose(tpc);
//				
//			}
			
			public Pitch toPitch(Pitch keyPitch, Interval intv)
			{
				return keyPitch.transpose(intv.halfSteps);
			}
//			public int transposeCount(Interval intv) {
//				int step = 0;
//				int toReturn = 0;
//				
//				switch(intv) {
//				case AUG7: toReturn += steps[(step++)%steps.length].halfSteps;
//				case M7:
//				case DIM8: toReturn += steps[(step++)%steps.length].halfSteps;
//				case MI7:
//				case AUG6: toReturn += steps[(step++)%steps.length].halfSteps;
//				case M6:
//				case DIM7: toReturn += steps[(step++)%steps.length].halfSteps;
//				case MI6:
//				case AUG5: toReturn += steps[(step++)%steps.length].halfSteps;
//				case P5:
//				case DIM6: toReturn += steps[(step++)%steps.length].halfSteps;
//				case AUG4:
//				case DIM5: toReturn += steps[(step++)%steps.length].halfSteps;
//				case P4:
//				case AUG3: toReturn += steps[(step++)%steps.length].halfSteps;
//				case M3:
//				case DIM4: toReturn += steps[(step++)%steps.length].halfSteps;
//				case Mi3:
//				case A2: toReturn += steps[(step++)%steps.length].halfSteps;
//				case M2:
//				case D3: toReturn += steps[(step++)%steps.length].halfSteps;
//				case Mi2:
//				case A1: toReturn += steps[(step++)%steps.length].halfSteps;
//				case P1:
//				case D2:
//				case UNKI:
//					break;
//				default:
//					break; 
//				}
//				return toReturn;
//			}
		}
	}
	
	
	public static class IntervalToken extends BeatToken
	{
		public Interval interval;
		public Pitch pitch;
		public IntervalToken(Token token, String intervalString)
		{
			super(token);
			this.interval = stringToInterval(intervalString);
			if(interval == UNKI)
				throw new IllegalMarking(token, IllegalMarking.Reason.NOTE);
			this.count = UNKC;
		}
		
		/**
		 * Specifies the count string to be the string specified, and the
		 * interval symbol string to be the string specified.
		 * 
		 * countString can be empty.
		 * @param pitchString
		 * @param countString
		 */
		public IntervalToken(Token token, String intervalString, String countString)
		{
			this(token, intervalString);
			if(!countString.isEmpty()) {
				Count forcedAttempt = stringToCount(countString);
				if(forcedAttempt == UNKC)
					throw new IllegalMarking(token, IllegalMarking.Reason.COUNT);
				count = forcedAttempt;
			}
		}
		
		public void setBasePitchIfUnset(Scale currentScale, Pitch basePitch) { 
			this.pitch = currentScale.toPitch(basePitch, interval);
		}
		public void setInstrumentIfUnset(Instrument instrument) { this.instrument = instrument; }
		public void setBaseCountIfUnset(Meter newMeter) {
			if(count == UNKC) {
				this.count = newMeter.getsTheBeat;
			}
			this.playSpeed = newMeter.getsTheBeatDurationMs;
		}
	}
	/**
	 * A token that specifies a musical note. 
	 */
	public static class NoteToken extends BeatToken
	{
		public Pitch pitch;
		
		public NoteToken(Token token)
		{
			super(token);
			this.pitch = UNK;
			this.count = UNKC;
		}
		public NoteToken(Token token, String pitchString)
		{
			super(token);
			this.pitch = stringToPitch(pitchString);
			if(pitch == UNK)
				throw new IllegalMarking(token, IllegalMarking.Reason.NOTE);
			this.count = UNKC;
		}
		
		/**
		 * Specifies the count string to be the string specified, and the
		 * pitch string to be the string specified.
		 * 
		 * countString can be empty.
		 * @param pitchString
		 * @param countString
		 */
		public NoteToken(Token token, String pitchString, String countString)
		{
			this(token, pitchString);
			if(!countString.isEmpty()) {
				Count forcedAttempt = stringToCount(countString);
				if(forcedAttempt == UNKC)
					throw new IllegalMarking(token, IllegalMarking.Reason.COUNT);
				count = forcedAttempt;
			}
		}
		public void setBaseCountIfUnset(Meter newMeter) {
			if(count == UNKC) {
				this.count = newMeter.getsTheBeat;
			}
			this.playSpeed = newMeter.getsTheBeatDurationMs;
		}
		public void setInstrumentIfUnset(Instrument instrument) { this.instrument = instrument; }
	}
	
	/**
	 * Specifies a rest. 
	 * @author jsaddle
	 *
	 */
	public static class RestToken extends BeatToken 
	{
		/**
		 * Initializes the count string to be the string specified. 
		 * @param countString
		 */
		public RestToken(Token token, String countString)
		{
			super(token);
			if(countString.isEmpty())
				count = UNKC;
			else {
				Count forcedAttempt = stringToCount(countString);
				if(forcedAttempt == UNKC)
					throw new IllegalMarking(token, IllegalMarking.Reason.COUNT);
				count = forcedAttempt;
			}
		}
		
		/**
		 * If the base count is not yet set, set the count of this rest.
		 */
		public void setBaseCountIfUnset(Meter newMeter) {
			if(count == UNKC) {
				this.count = newMeter.getsTheBeat;
			}
			this.playSpeed = newMeter.getsTheBeatDurationMs;
		}
		
		public void setInstrumentIfUnset(Instrument instrument) { this.instrument = instrument; }
	}	
	
	
	public static class VolumeToken extends Token
	{
		public int volumeSetting;
		public VolumeToken(Token token, String volume) {
			super(token);
			int attempt;
			try {
				attempt = Integer.parseInt(volume);
				if(attempt < 0)
					throw new NumberFormatException();
			} catch(NumberFormatException e) {
				throw new IllegalMarking(Reason.VOLUME_SETTING);
			}
			volumeSetting = attempt;
		}
	}
//	public static class RepeatDesignatorToken extends MeasureToken {
//		public String reference;
//		public boolean isNumeric;
//		public RepeatDesignatorToken(Token token, String reference, boolean isNumeric) {
//			super(token);
//			this.reference = reference;
//			this.hasReference = true;
//			this.isNumeric = isNumeric;
//		}
//		public String toString() { 
//			return "MARKER" + (!reference.isEmpty() ? " " + reference : "");
//		}
//	}
	
	public static class RepeatToken extends Token {
		public RepeatToken(Token token) { super(token); }
		
		public String toString() { return ":"; }
	}
	
	public static class InstrumentToken extends Token
	{
		public Pitch homePitch;
		public final String patch;
		public final int patchSelection;
		public int volumeSetting;
		public InstrumentToken(Token token, String patch)
		{
			super(token);
			this.patch = patch;
			this.patchSelection = Instrument.SELECTION_UNCONFIRMED;
			this.volumeSetting = Instrument.SELECTION_UNCONFIRMED;
			this.homePitch = Pitch.UNK;
		}
		
		public InstrumentToken(Token token, String patch, String selectionString)
		{
			super(token);
			this.patch = patch;
			this.homePitch = Pitch.UNK;
			this.volumeSetting = Instrument.SELECTION_UNCONFIRMED;
			int sel = Instrument.SELECTION_UNCONFIRMED;
			
			try{
				this.patchSelection = Integer.parseInt(selectionString);
				if(sel < 1)
					throw new NumberFormatException();
			} catch(NumberFormatException e) {
				throw new IllegalMarking(Reason.INSTRUMENT);
			}
		}
		
		public InstrumentToken(Token token, String patch, String selectionString, String volume)
		{
			this(token, patch, selectionString);
			int attempt;
			try {
				attempt = Integer.parseInt(volume);
				if(attempt < 0)
					throw new NumberFormatException();
			} catch(NumberFormatException e) {
				throw new IllegalMarking(Reason.VOLUME_SETTING);
			}
			volumeSetting = attempt;
		}
	}
	
	public static Count stringToBeatCount(String input)
	{
		switch(input) {
		case "1":  return Count.WHOLE;
		case "2":  return Count.HALF;
		case "3":  return Count.DOTTED_HALF;
		case "4":  return Count.QTR;
		case "6":  return Count.DOTTED_QTR;
		case "8":  return Count.EGTH;
		case "12": return Count.DOTTED_EGTH;
		case "16": return Count.SXTH;
		case "24": return Count.DOTTED_SXTH;
		case "32": return Count.TSND;
		case "48": return Count.DOTTED_TSND;
		case "64": return Count.SFTH;
		case "96": return Count.DOTTED_SFTH;
		case "128":return Count.HTETH;
		}
		return Count.UNKC;
	}
	
	public static Interval stringToInterval(String input) {
		for(Interval i : Interval.values()) {
			if(i.name().equalsIgnoreCase(input)) 
				return i;
			if(i.nicknameAliases.length > 0) {
				for(int j = 0; j < i.nicknameAliases.length; j++) 
					if(i.nicknameAliases[j].equals(input)) 
						return i;
			}
			if(i.accidentalAliases.length > 0) {
				if(i.accidentalAliases[0].equalsIgnoreCase(input))
					return i;
				else if(i.accidentalAliases.length > 1 
						&& i.accidentalAliases[1].equalsIgnoreCase(input))
					return i;
			}
		}
		return Interval.UNKI;
	}
	
	public static KeyQuality qualityFromString(String input)
	{
		if(input.isEmpty())
			return KeyQuality.UNKQ;
		String compare;
		if(input.length() > 2) {
			compare = input.substring(0, 2);	
		}
		else 
			compare = input;
		
		if(Character.isDigit(compare.charAt(1)))  
			compare = input.substring(0, 1);
		
		compare = compare.toUpperCase();
		switch(compare) {
		case "P" : 
		case "M" :return KeyQuality.MAJOR;
		case "MI" : return KeyQuality.MINOR;
		case "AU" : return KeyQuality.AUGMENTED;
		case "DM" : return KeyQuality.DIMINISHED;
		default  : return KeyQuality.UNKQ;
		}
	}
	
	public static int jumpFromString(String input)
	{
		return -1;
	}
	
	public static ScaleToken stringToScale(String input) {
		
		ScaleToken toReturn = new ScaleToken();
		String scPart = "";
//		ScaleToken.Scale foundSc = ScaleToken.Scale.UNKSC;
		
		ScaleToken.Scale foundSc = null;
		int i;
		scaleLoop:
		for(i = input.length()-1; i > 0; i--) {
			scPart = input.charAt(i) + scPart;
			for(ScaleToken.Scale sc : ScaleToken.Scale.values()) {
				if(sc.suffix.equalsIgnoreCase(scPart)) { 
					foundSc = sc;
					break scaleLoop;
				}
				else if(sc.suffixAlias.equalsIgnoreCase(scPart)) {
					foundSc = sc;
					break scaleLoop;
				}
			}
		}
		if(foundSc == null)
			return toReturn;
		String baseStr = input.substring(0, i);
		Pitch foundPt = stringToPitch(baseStr);
		if(foundPt == UNK) 
			return toReturn;
		
		toReturn.key = foundPt;
		toReturn.scale = foundSc;
		return toReturn;
	}
	public static Count stringToCount(String input)
	{
		for(Count c : Count.values()) {
			if(c.alias.equalsIgnoreCase(input))
				return c;
		}
		return UNKC; // count part was not found. 
	}
	public static Sound stringToSound(String input)
	{
		for(Sound t : Sound.values()) 
			if(t.toString().equalsIgnoreCase(input))
				return t;
		return UNKS; // pitch part was not found.
	}
	
	public static Pitch stringToPitch(String input)
	{
		for(Pitch p : Pitch.values()) { 
			if(p.name().equalsIgnoreCase(input)
			|| p.nicknameAlias.equalsIgnoreCase(input)
			)
				return p;
			else if(p.downAccidentalAliases.length > 0) {
				if(p.downAccidentalAliases[0].equalsIgnoreCase(input))
					return p;
				else if(p.downAccidentalAliases.length > 1 
						&& p.downAccidentalAliases[1].equalsIgnoreCase(input))
					return p;
			}
		}
			
		return UNK; // pitch part was not found. 
	}
	
	
	
	public static boolean checkAllDigits(String inputString)
	{
		if(inputString.isEmpty())
			return false;
		for(char c : inputString.toCharArray()) 
			if(!Character.isDigit(c)) 
				return false;
		return true;
	}
	public boolean checkAllDigits(String inputString, char exception)
	{
		if(inputString.isEmpty())
			return false;
		for(char c : inputString.toCharArray()) 
			if(!Character.isDigit(c) && c != exception) 
				return false;
		return true;
	}
	public String toString()
	{
		
		String toReturn = tokenLiteral;
		
		if(line != -1 && character != -1)
			toReturn += " @ ln " + line + ", pos " + character;
		return toReturn;
	}
}
