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



import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Source code for the MusicalObjects Class
 * This class is responsible for holding enumerations of various objects
 * that must be enumerated for every grammar piece encountered in a MusicalObjects
 * score sheet. 
 * 
 * One can search this file for all constants
 * 
 * @author Jonathan A. Saddler
 *
 */
public class MusicalObjects {
	
	
	public static enum Count {
		TRIPLET_HTETH("tht", 2), 	DOUBLE_TRIPLET_HTETH("t2ht", 4), 	HTETH("ht", 6), 	DOTTED_HTETH("dht", 9), 
		TRIPLET_SFTH("tsf",4),     	DOUBLE_TRIPLET_SFTH("t2sf", 8), 	SFTH("sf", 12), 	DOTTED_SFTH("dsf",18), 
		TRIPLET_TSND("tt", 8),     	DOUBLE_TRIPLET_TSND("t2t", 16), 	TSND("t", 24), 		DOTTED_TSND("dt", 36), 
		TRIPLET_SXTH("ts", 16),     DOUBLE_TRIPLET_SXTH("t2s", 32), 	SXTH("s", 48), 		DOTTED_SXTH("ds", 72), 
		TRIPLET_EGTH("te", 32),  	DOUBLE_TRIPLET_EGTH("t2e", 64),     EGTH("e",96),		DOTTED_EGTH("de", 144),
		TRIPLET_QTR("tq", 64), 	    DOUBLE_TRIPLET_QTR("t2q", 128), 	QTR("q", 192), 		DOTTED_QTR("dq", 288),
		TRIPLET_HALF("th", 128),    DOUBLE_TRIPLET_HALF("t2h", 256), 	HALF("h", 384), 	DOTTED_HALF("dh", 576), 
		TRIPLET_WHOLE("tw", 256),   DOUBLE_TRIPLET_WHOLE("t2w", 512), 	WHOLE("w", 768), 	DOTTED_WHOLE("dw", 1152), 
		TRIPLET_DWHOLE("tw2", 512), DOUBLE_TRIPLET_DWHOLE("t2w2", 1024),DWHOLE("w2", 1536), DOTTED_DWHOLE("dw2",2304), 
		TRIPLET_LWHOLE("tw4", 1024),DOUBLE_TRIPLET_LWHOLE("t2w4", 2048),LWHOLE("w4", 3072), DOTTED_LWHOLE("dw4", 4608),
		UNKC;
		
		public final String alias;
		public final int timeUnits;
		
		private Count() {
			alias = "";
			timeUnits = -1;
		}
		private Count(String alias) {
			this.alias = alias;
			timeUnits = -1;
		}
		private Count(String alias, int lengthValue) {
			this.alias = alias;
			this.timeUnits = lengthValue;
		}		
		
		/**
		 * Returns the greatest count that is defeated by (has time units less than) the timeUnit specified
		 */
		public static Count greatestLowerBound(int timeUnit) {
			for(Count c : values()) 
				if(timeUnit < c.timeUnits) {
					if(c.ordinal() == 0) return values()[0];
					else 				 return values()[c.ordinal()-1];
				}
			return values()[values().length-1];
		}
	}
	
	
	public static enum KeyQuality {
		PERFECT, MAJOR, MINOR, AUGMENTED, DIMINISHED, UNKQ;	
	}
	
	public static enum Interval {
		// thanks goes out to https://musictheoryblog.blogspot.com/2007/01/intervals.html
		// for a list of intervals
		//thanks goes out to the Yamaha DGX-230/YPG-235 Owner's Manual, (pp. 69) for information
		// on different chords
		// make the variables as small as possible.
		 Z (0)
		,H (1)
		,W (2)	
//		, IM8(-10, "-"
		, IM7(-8, "-i7")
		, IM6(-6, "i6")
		, IM5(-7, "i5") // 
		, IM4(-5, "i4") // so 
		, IM3(-3, "i3") // la
//		, IM2
		, IM2(-1, "i2") // ti
		,P1(0, KeyQuality.MAJOR       , "1")          ,DIM2(0, KeyQuality.DIMINISHED , "1")
		
		,AUG1(1, KeyQuality.AUGMENTED , "1s", "2f")   ,MI2(1,KeyQuality.MINOR        , "1s" , "2f") 
		
		,M2 (2, KeyQuality.MAJOR      , "2")          ,DIM3(2, KeyQuality.DIMINISHED , "2")
		
		,AUG2(3, KeyQuality.AUGMENTED , "2s", "3f")   ,MI3(3,KeyQuality.MINOR        , "2s" , "3f") 
		
		,M3 (4, KeyQuality.MAJOR      , "3" , "4f")   ,DIM4(4, KeyQuality.DIMINISHED , "3"  , "4f")
		
		,AUG3(5, KeyQuality.AUGMENTED , "4" , "3s" )  ,P4 (5, KeyQuality.MAJOR       , "4" , "3s")  
		
		,AUG4 (6, KeyQuality.AUGMENTED, "4s", "5f")   ,DIM5(6, KeyQuality.DIMINISHED , "4s" , "5f")
		
		,P5 (7, KeyQuality.MAJOR      , "5") 		  ,DIM6(7, KeyQuality.DIMINISHED , "5")
		
		,MI6(8,KeyQuality.MINOR       , "5s", "6f")   ,AUG5(8, KeyQuality.AUGMENTED  , "5s" , "6f")
		
		,M6 (9, KeyQuality.MAJOR      , "6")          ,DIM7(9, KeyQuality.DIMINISHED , "6")
		
		,MI7(10,KeyQuality.MINOR      , "6s", "7f")   ,AUG6(10, KeyQuality.AUGMENTED , "6s" , "7f")
		
		,M7 (11, KeyQuality.MAJOR     , "7" , "8f")   ,DIM8(11, KeyQuality.DIMINISHED, "7", "8f")
		
		,P8 (12, KeyQuality.MAJOR     , "8")        ,AUG7(12, KeyQuality.AUGMENTED , "9/8")
		
		,MI9(13,KeyQuality.MINOR      , "9s", "2f")   ,AUG8(13, KeyQuality.AUGMENTED , "9s" , "2f")
		
		,M9  (14, KeyQuality.MAJOR    , "10")         ,DIM10(2, KeyQuality.DIMINISHED,"2")
		,MI10(15, KeyQuality.MINOR    , "10s" , "11f") ,AUG9(3, KeyQuality.AUGMENTED ,"2s" , "3f")
		,M10 (16, KeyQuality.MAJOR    , "11"  , "12f")  ,DIM11(4, KeyQuality.DIMINISHED,"3"  , "4f")
		,P11 (17, KeyQuality.MAJOR    , "12"  , "11s")  ,AUG10(5, KeyQuality.AUGMENTED ,"4"  , "3s" )
		,AUG11(18, KeyQuality.AUGMENTED, "12s", "13f"),DIM12(6, KeyQuality.DIMINISHED,"4s" , "5f")
		,P12 (19, KeyQuality.MAJOR    , "13") 		 ,DIM13(7, KeyQuality.DIMINISHED,"5")
		,MI13(20, KeyQuality.MINOR    , "13s" , "14f")  ,AUG12(8, KeyQuality.AUGMENTED, "5s" , "6f")
		,M13 (21, KeyQuality.MAJOR    , "14")          ,DIM14(9, KeyQuality.DIMINISHED,"6")
		,MI14(22, KeyQuality.MINOR    , "14s" , "15f")  ,AUG13(10, KeyQuality.AUGMENTED,"6s" , "7f")
		,M14 (23, KeyQuality.MAJOR    , "15"  , "16f")  ,DIM15(11, KeyQuality.DIMINISHED,"7", "8f")
		,P15 (24, KeyQuality.MAJOR    , "16")  ,AUG15(12, KeyQuality.AUGMENTED, "1h/8")
		,UNKI(0) 
		;
		
		final int halfSteps;
		public KeyQuality quality;
		public final String[] nicknameAliases;
		public final String[] accidentalAliases;
		private Interval(int jump) {
			this.halfSteps = jump;
			this.accidentalAliases = new String[0];
			this.quality = KeyQuality.MAJOR;
			nicknameAliases = new String[0];
		}
//		private Interval(int jump, KeyQuality q) {
//			this.nicknameAliases = new String[0];
//			this.accidentalAliases = new String[0];
//			this.quality = q;
//			this.halfSteps = jump;
//		}
		private Interval(int i, String nicknameAliases) {
			this.nicknameAliases = nicknameAliases.split("/");
			this.accidentalAliases = new String[0];
			this.quality = KeyQuality.MAJOR;
			this.halfSteps = i;
		}
		private Interval(int i, KeyQuality q, String nicknameAliases) {
			this.nicknameAliases = nicknameAliases.split("/");
			this.accidentalAliases = new String[0];
			this.quality = q;
			this.halfSteps = i;
		}
		private Interval(int i, KeyQuality q, String nicknameAliases, String downAccidentalAliases) {
			this.nicknameAliases = nicknameAliases.split("/");
			this.accidentalAliases = downAccidentalAliases.split("/");
			this.quality = q;
			this.halfSteps = i;
		} 
		
		
		// the idea: support mid range from [0 to 12]
		// support low range from [1l to 11l then 0]
		// support high range from [+0h to +11h then 0hh] 
		
		public Interval transpose(int newHalfSteps) {
			if(this == UNKI 
			|| halfSteps + newHalfSteps > 12
			|| ordinal() + newHalfSteps < -12) {
				return UNKI;
			}
			
			switch(halfSteps + newHalfSteps) {
			case -1: 
			case -2:
			case -3:
			case -4:
			case -5:
			case -6:
			case -7:
			case -8:
			case -9:
			case -10:
			case -11:
			case -12:
			case 0: return P1;
			case 1: return MI2;
			case 2: return M2;
			case 3: return MI3;
			case 4: return M3;
			case 5: return P4;
			case 6: return DIM5;
			case 7: return P5;
			case 8: return MI6;
			case 9: return M6;
			case 10: return MI7;
			default: return UNKI;
			}
		}
	}
	public static enum IntervalOld {
		// thanks goes out to https://musictheoryblog.blogspot.com/2007/01/intervals.html
		// for a list of intervals
		//thanks goes out to the Yamaha DGX-230/YPG-235 Owner's Manual, (pp. 69) for information
		// on different chords
		// make the variables as small as possible.
		Z(0, KeyQuality.MAJOR)
		,H (1, KeyQuality.MAJOR)
		,W  (2, KeyQuality.MAJOR)
		
		,P1(0, KeyQuality.MAJOR     ,"1")         ,D2(0, KeyQuality.DIMINISHED,"1")
		,A1(1, KeyQuality.AUGMENTED ,"1s" , "2f") ,Mi2(1,KeyQuality.MINOR     ,"1s" , "2f") 
		,M2 (2, KeyQuality.MAJOR    ,"2")         ,D3(2, KeyQuality.DIMINISHED,"2")
		,A2(3, KeyQuality.AUGMENTED ,"2s" , "3f") ,Mi3(3,KeyQuality.MINOR     ,"2s" , "3f") 
		,M3 (4, KeyQuality.MAJOR    ,"3" , "4f")  ,D4(4, KeyQuality.DIMINISHED,"3"  , "4f")
		,A3(5, KeyQuality.AUGMENTED ,"4"  , "3s" ),P4 (5, KeyQuality.MAJOR    ,"4" , "3s")  
		,A4 (6, KeyQuality.AUGMENTED, "4s", "5f") ,D5(6, KeyQuality.DIMINISHED,"4s" , "5f")
		,P5 (7, KeyQuality.MAJOR,    "5") 		  ,D6(7, KeyQuality.DIMINISHED,"5")
		,Mi6(8,KeyQuality.MINOR,     "5s" , "6f") ,A5(8, KeyQuality.AUGMENTED, "5s" , "6f")
		,M6 (9, KeyQuality.MAJOR,    "6")         ,D7(9, KeyQuality.DIMINISHED,"6")
		,Mi7(10,KeyQuality.MINOR,    "6s" , "7f") ,A6(10, KeyQuality.AUGMENTED,"6s" , "7f")
		,M7 (11, KeyQuality.MAJOR,   "7"  , "8f") ,D8(11, KeyQuality.DIMINISHED,"7", "8f")
		,P1h (12, KeyQuality.MAJOR,  "8")    	  ,A7(12, KeyQuality.AUGMENTED, "8")
		,A1h (13, KeyQuality.MAJOR,  "8s" , "9f") ,Mi2h(13, KeyQuality.AUGMENTED, "8s","9f")
		,M2h(14,KeyQuality.MAJOR,    "9")        ,D3h(14, KeyQuality.DIMINISHED ,"9")
		,A2h(15,KeyQuality.AUGMENTED,"10")       ,Mi3h(15, KeyQuality.MINOR,"10")
//		,Mi10(15,KeyQuality.MINOR     ,"2s" , "3f") ,A9(3, KeyQuality.AUGMENTED ,"2s" , "3f")
//		,M10 (16, KeyQuality.MAJOR    ,"3" , "4f")  ,D11(4, KeyQuality.DIMINISHED,"3"  , "4f")
//		,P11 (17, KeyQuality.MAJOR    ,"4" , "3s")  ,A10(5, KeyQuality.AUGMENTED ,"4"  , "3s" )
//		,A11 (18, KeyQuality.AUGMENTED, "4s", "5f") ,D12(6, KeyQuality.DIMINISHED,"4s" , "5f")
//		,P12 (19, KeyQuality.MAJOR,    "5") 		,D13(7, KeyQuality.DIMINISHED,"5")
//		,Mi13(20,KeyQuality.MINOR,     "5s" , "6f") ,A12(8, KeyQuality.AUGMENTED, "5s" , "6f")
//		,M13 (21, KeyQuality.MAJOR,    "6")         ,D14(9, KeyQuality.DIMINISHED,"6")
//		,Mi14(22,KeyQuality.MINOR,    "6s" , "7f")  ,A13(10, KeyQuality.AUGMENTED,"6s" , "7f")
//		,M14 (23, KeyQuality.MAJOR,   "7",   "8f")  ,D15(11, KeyQuality.DIMINISHED,"7", "8f")
//		,P15 (24, KeyQuality.MAJOR,   "1h/8")       ,A15(12, KeyQuality.AUGMENTED, "1h/8")
		,UNKI(0, KeyQuality.UNKQ) 
		;
		
		final int halfSteps;
		public KeyQuality quality;
		public final String[] nicknameAliases;
		public final String[] accidentalAliases;
		
		private IntervalOld(int jump, KeyQuality q) {
			this.nicknameAliases = new String[0];
			this.accidentalAliases = new String[0];
			this.quality = q;
			this.halfSteps = jump;
		}
		private IntervalOld(int i, KeyQuality q, String nicknameAliases) {
			this.nicknameAliases = nicknameAliases.split("/");
			this.accidentalAliases = new String[0];
			this.quality = q;
			this.halfSteps = i;
		}
		private IntervalOld(int i, KeyQuality q, String nicknameAliases, String downAccidentalAliases) {
			this.nicknameAliases = nicknameAliases.split("/");
			this.accidentalAliases = downAccidentalAliases.split("/");
			this.quality = q;
			this.halfSteps = i;
		} 
		
		
		// the idea: support mid range from [0 to 12]
		// support low range from [1l to 11l then 0]
		// support high range from [+0h to +11h then 0hh] 
		
//		public Interval transpose(int newHalfSteps) {
//			if(this == UNKI 
//			|| halfSteps + newHalfSteps > 12
//			|| ordinal() + newHalfSteps < -12) {
//				return UNKI;
//			}
//			
//			switch(halfSteps + newHalfSteps) {
//			case -1: 
//			case -2:
//			case -3:
//			case -4:
//			case -5:
//			case -6:
//			case -7:
//			case -8:
//			case -9:
//			case -10:
//			case -11:
//			case -12:
//				case 0: return P1;
//				case 1: return Mi2;
//				case 2: return M2;
//				case 3: return Mi3;
//				case 4: return M3;
//				case 5: return P4;
//				case 6: return D5;
//				case 7: return P5;
//				case 8: return Mi6;
//				case 9: return M6;
//				case 10: return Mi7;
//				default: return UNKI;
//			}
//		}
	}
	public static enum Pitch {
		A0(27.5,    21),		As0(29.1352, 22, "Bf0")
		,B0(30.8677, 23)
		,C1(32.7032, 24),		Cs1(34.6478, 25, "Df1")
		,D1(36.7081, 26),		Ds1(38.8909, 27, "Ef1")
		,E1(41.2034, 28)				
		,F1(43.6535, 29),		Fs1(46.2493, 30, "Gf1")
		,G1(48.9994, 31),		Gs1(51.9131, 32, "Af1")
		,A1(55,	     33),		As1(58.2705, 34, "Bf1")
		,B1(61.7354, 35)
		
		,C2(65.4064, 36), 		Cs2(69.2957, 37, "Df2")
		,D2(82.4069, 38), 		Ds2(87.3071, 39, "Ef2")
		,E2(82.4069, 40)
		,F2(87.3071, 41), 		Fs2(97.9989, 42, "Gf2")
		,G2(69.2957, 43),		Gs2(73.4162, 44, "Af2")
		,A2(110, 45), 			As2(116.541, 46, "Bf2")
		,B2(123.471, 47)
		
		
		,C3(130.813, 48, "", "Cl"), 		Cs3(138.591, 49, "Df3/Dfl", "Csl") 
		,D3(146.832, 50, "", "Dl"), 		Ds3(155.563, 51, "Ef3/Efl", "Dsl")
		,E3(164.814, 52, "", "El") 
		,F3(174.614, 53,  "", "Fl"), 		Fs3(184.997, 54, "Gf3/Gfl", "Fsl")
		,G3(195.998, 55, "", "Gl"), 		Gs3(207.652, 56, "Af3/Afl", "Gsl")
		,A3(220.000, 57, "", "Al"), 		As3(233.082, 58, "Bf3/Bfl", "Asl")
		,B3(246.492, 59, "", "Bl")
		
		,C4(261.626,  60, "", "C"), 		Cs4(277.183, 61, "Df4/Df", "Cs")
		,D4(293.665,  62, "", "D"), 		Ds4(311.127, 63, "Ef4/Ef", "Ds")
		,E4(329.628,  64, "", "E")
		,F4(349.228,  65, "", "F"), 		Fs4(369.994, 66, "Gf4/Gf", "Fs")
		,G4(391.995,  67, "", "G"), 		Gs4(415.305, 68, "Af4/Af", "Gs")
		,A4(440,      69, "", "A"), 		As4(466.164, 70, "Bf4/Bf", "As")
		,B4(493.883,  71, "", "B")
		
		,C5(523.251, 72, "", "Ch"), 	Cs5(554.365, 73, "Df5/Dfh", "Csh")
		,D5(587.330, 74, "", "Dh"),		Ds5(622.254, 75, "Ef5/Efh", "Dsh")
		,E5(659.255, 76, "", "Eh")
		,F5(698.456, 77, "", "Fh"),		Fs5(739.989, 78, "Gf5/Gfh", "Fsh")
		,G5(783.991, 79, "", "Gh"), 	Gs5(830.609, 80, "Af5/Afh", "Gsh")
		,A5(880, 	 81, "", "Ah"), 	As5(932.328, 82, "Bf5/Bfh", "Ash")
		,B5(987.767, 83, "", "Bh")
		
		,C6(1046.5, 84), 		Cs6(1108.73,  85, "Df6")
		,D6(1174.66, 86), 		Ds6(1244.51,  87, "Ef6")
		,E6(1318.51, 88)
		,F6(1396.91, 89), 		Fs6(1479.98,  90, "Gf6")
		,G6(1567.98, 91), 		Gs6(1661.22,  92, "Af6")
		,A6(1760, 93),			As6(1864.66,  94, "Bf6")
		
		,C7(2093, 	96) 		,Cs7(2217.46, 97, "Df7")
		,D7(2349.32,98)			,Ds7(2489.02, 99, "Ef7")
		,E7(2637.02,100)
		,F7(2793.83,101)		,Fs7(2959.96, 102,"Gf7")
		,G7(3135.96,103)		,Gs7(3322.44, 104,"Af7")
		,A7(3520,	105)		, As7(3729.31, 106, "Bf7")
		,B7(3951.07,107)		
		,C8(4186.01, 96, "CMAX")
		,UNK(0, 0);
		;
	
		public final double noteValue;
		public final int midiNumber;
		public final String[] downAccidentalAliases;
		public final String nicknameAlias;
		private Pitch(double noteValue, int midiNoteNumber, String downAccidentalAliases, String nicknameAliases)
		{
			this.noteValue = noteValue; 
			this.midiNumber = midiNoteNumber;
			this.downAccidentalAliases = downAccidentalAliases.split("/");
			this.nicknameAlias = nicknameAliases;
		}
		private Pitch(double noteValue, int midiNoteNumber, String downAccidentalAliases)
		{
			this.noteValue = noteValue; 
			this.midiNumber = midiNoteNumber;
			this.downAccidentalAliases = downAccidentalAliases.split("/");
			this.nicknameAlias = "";
		}
		private Pitch(double noteValue, int midiNoteNumber)
		{
			this.noteValue = noteValue; 
			this.midiNumber = midiNoteNumber;
			this.downAccidentalAliases = new String[0];
			this.nicknameAlias = "";
		}
		public String toString()
		{
			return nicknameAlias.isEmpty() ? name() : nicknameAlias;
		}
		public Pitch transpose(int halfSteps) {
			if(ordinal() + halfSteps > Pitch.values().length 
			|| ordinal() + halfSteps < 0)
				return UNK;
			return values()[ordinal()+halfSteps];
		}
	}
	
	public static enum Sound
	{
		TUM(35),
		PM(36),
		PUM(36),
		RIM(37),  
		BA(38), // rim shot
		SNARE(38),
		POP(38),
		CLAP(39),
		CLP(39),
		TI(42),  // closed hi hat
		BUM(43),
		TST(44), // pedal hi hat
		TS(46),
		BOM(47), // mid tom 1
		CRASH(49),
		BIM(50), // HI
		VIBE(58), // VIBRASLAP
		UNKS;
		
		
		public final int midiNumber;
		private Sound()
		{
			this.midiNumber = -1;
		}
		private Sound(int midiNumber)
		{
			this.midiNumber = midiNumber;
		}
	}
	
	public abstract interface StaffComponent
	{
		
	}
	
	public static class Volume implements StaffComponent
	{
		public int volume;
		public Volume(int setting)
		{
			this.volume = setting;
		}
	}
	public static class Instrument implements StaffComponent
	{
		public Pitch homePitch;
		public String patch;
		public int selection;
		public int volume; 
		public static final int SELECTION_UNCONFIRMED = -1;
		
		
		public Instrument(String patch)
		{
			this.patch = patch;
			this.selection = SELECTION_UNCONFIRMED;
			this.volume = SELECTION_UNCONFIRMED;
			this.homePitch = Pitch.UNK;
		}
		
		public Instrument(String patch, int selection)
		{
			this(patch);
			this.selection = selection;
		}
		
		public Instrument(String patch, Pitch basePitch)
		{
			this(patch);
			this.homePitch = basePitch;
		}
		
		public Instrument(String patch, int selection, Pitch basePitch)
		{
			this(patch, selection);
			this.homePitch = basePitch;
		}
		
		// set the volume. 
		public Instrument(String patch, Pitch basePitch, int volumeSetting)
		{
			this(patch, basePitch);
			this.volume = volumeSetting;
		}
		public Instrument(String patch, int selection, Pitch basePitch, int volumeSetting)
		{
			this(patch, selection, basePitch);
			this.volume = volumeSetting;
		}
		public String toString()
		{
			return "Instrument " + patch + (selection != SELECTION_UNCONFIRMED ? " "+selection : " ") 
					+ " at " + (volume != SELECTION_UNCONFIRMED ? "volume " + volume : "normal volume")
					+ ((homePitch != Pitch.UNK) ? "(home pitch " + homePitch + ")" : "");
		}
		
		public Instrument copy()
		{
			Instrument toReturn = new Instrument(patch);
			toReturn.homePitch = homePitch;
			toReturn.volume = volume;
			toReturn.patch = patch;
			toReturn.selection = SELECTION_UNCONFIRMED;
			return toReturn;
		}
	}
	public static class Repeat implements StaffComponent
	{
		public Repeat(Measure repeatMeasure, int currentMeasure)
		{
			
		}
	}
	public static class MeasureMarker implements StaffComponent
	{
		
	}
	
	public static class Key implements StaffComponent
	{
		public Pitch pitch;	
	}
	public static class Meter implements StaffComponent
	{
		public int beatsPerMeasure;
		public Count getsTheBeat;
		public long getsTheBeatDurationMs;
		public static final long DURATION_UNDEFINED = -1;
		public Meter(int beatsPerMeasure, Count getsTheBeat)
		{
			this.beatsPerMeasure = beatsPerMeasure;
			this.getsTheBeat = getsTheBeat;
			this.getsTheBeatDurationMs = DURATION_UNDEFINED;
		}
		
		public Meter(int beatsPerMeasure, Count getsTheBeat, long getsTheBeatDurationMs)
		{
			this.beatsPerMeasure = beatsPerMeasure;
			this.getsTheBeat = getsTheBeat;
			this.getsTheBeatDurationMs = getsTheBeatDurationMs;
		}
	}
	public abstract static class Beat implements StaffComponent
	{
		public long playSpeedMs;
		public Count count;
		public double pitchVal;
		public int midiNumber;
		public boolean isSpecialSound;
		public int volume;
		public Instrument playsWith;
		public int carry;
		public static final int NO_CARRY = 0;
		public static final int CARRY_NEXT = 1;
		public static final int CARRY_PREV = 2;
		public Beat()
		{
			volume = Instrument.SELECTION_UNCONFIRMED;
			this.playsWith = new Instrument("");
		}
		public String firstPartOfCount()
		{
			switch(count) {
			case UNKC 	: return "";
			default		:
			}
			return count.alias.toLowerCase();
		}
		public abstract String type();
		
		public void setPlayspeedFromBPMString(String bpmStr) {
			// say it's 4 what would happen? 
			// it should turn into 250
			// if it's 8 it should turn into 125
			// 16 would be 62.5
			double bpm = Double.parseDouble(bpmStr);
			double bps = bpm / 60;
			double ms = 1000/bps;
			this.playSpeedMs = Math.round(ms);
		}
		public void setPlaySpeedMs(long playSpeed) { this.playSpeedMs = playSpeed; }
	}
	
	public static class Rest extends Beat {
		
		public Rest()
		{
			this(Count.UNKC);
		}
		public Rest(Count count)
		{
			this.count = count;
			pitchVal = 0;
			midiNumber = 0;
			volume = 0;
		}
		public Rest(Count count, long playSpeedMs)
		{
			this(count);
			this.playSpeedMs = playSpeedMs;
		}
//		public Rest(Count count, int carryCode)
//		{
//			this(count);
//			this.carry = carryCode;
//		}
		public static String getType(){return "rest";}
		
		public String type() {return getType();}
		public String toString()
		{
			return "r" + (count != Count.UNKC ? "." + firstPartOfCount() : "");
		}
	}
	
	public static class Note extends Beat {
		public Pitch pitch;
		
//		public Note(Count count, Pitch pitch, int carryCode)
//		{
//			this(count, pitch);
//			this.carry = carryCode;
//		}
		public Note(Count count, Pitch pitch) {
			this.count = count;
			this.pitch = pitch;
			pitchVal = pitch.noteValue;
			midiNumber = pitch.midiNumber;
		}
		public Note(Count count, Pitch pitch, int volumeSeting)
		{
			this.count = count;
			this.pitch = pitch;
			this.volume = volumeSeting;
			pitchVal = pitch.noteValue;
			midiNumber = pitch.midiNumber;
		}
		public Note(Count count, Pitch pitch, int volumeSetting, long playSpeedMs)
		{
			this(count, pitch, volumeSetting);
			this.playSpeedMs = playSpeedMs;
		}
		public Note(Note oldNote)
		{
			this(oldNote.count, oldNote.pitch, oldNote.volume, oldNote.playSpeedMs);
		}
		public String type(){return "note";}
		
		public String toString()
		{
			return pitch.name() + "." + count.name();
		}
		public static String getType() {return "note";}
	}
	
	public static class Strike extends Beat {
		public Sound sound;
		
//		public Strike(Count count, Sound sound, int carryCode)
//		{
//			this(count, sound);
//		}
		
		public Strike(Count count, Sound sound)
		{
			this.isSpecialSound = true;
			this.count = count;
			this.sound = sound;
			pitchVal = Pitch.C4.noteValue;
			midiNumber = sound.midiNumber;
		}
		public Strike(Count count, Sound sound, int volumeSetting)
		{
			this(count, sound);
			this.volume = volumeSetting;
		}
		public Strike(Count count, Sound sound, int volumeSetting, long playSpeedMs)
		{
			this(count, sound, volumeSetting);
			this.playSpeedMs = playSpeedMs;
		}
		
		@Override
		public String type() { return getType(); }
		public static String getType(){ return "strike"; }
		public String toString() { return sound.name(); }
	}
	
	
	public static float durationOf(Count count, Count getsTheBeat, float getsTheBeatValue)
	{
		if(count.ordinal() < getsTheBeat.ordinal()) {
			switch(count) {
				case TRIPLET_HTETH: 
				case DOUBLE_TRIPLET_HTETH: 
				case HTETH: 
				case DOTTED_HTETH:
					if(count == Count.DOUBLE_TRIPLET_HTETH) getsTheBeatValue /= 3/2.0;
					else if(count == Count.TRIPLET_HTETH) 	getsTheBeatValue /= 3.0;
					else if(count == Count.DOTTED_HTETH) getsTheBeatValue *= 1.5;
					if(getsTheBeat == Count.HTETH) return getsTheBeatValue;
					else getsTheBeatValue /= 2;
					
				case TRIPLET_SFTH: 
				case DOUBLE_TRIPLET_SFTH:
				case SFTH: 
				case DOTTED_SFTH:
					if(count == Count.DOUBLE_TRIPLET_SFTH) getsTheBeatValue /= 3/2.0;
					else if(count == Count.TRIPLET_SFTH) getsTheBeatValue /= 3.0;
					else if(count == Count.DOTTED_SFTH) getsTheBeatValue *= 1.5;
					if(getsTheBeat == Count.SFTH) return getsTheBeatValue;
					else getsTheBeatValue /= 2;
					
				case TRIPLET_TSND: 
				case DOUBLE_TRIPLET_TSND:
				case TSND: 
				case DOTTED_TSND:
					if(count == Count.DOUBLE_TRIPLET_TSND) getsTheBeatValue /= 3/2.0;
					else if(count == Count.TRIPLET_TSND) getsTheBeatValue /= 3.0;
					else if(count == Count.DOTTED_SFTH) getsTheBeatValue *= 1.5;
					if(getsTheBeat == Count.SFTH) return getsTheBeatValue;
					else getsTheBeatValue /= 2;
					
				case TRIPLET_SXTH: 
				case DOUBLE_TRIPLET_SXTH:
				case SXTH: 
				case DOTTED_SXTH:
					if(count == Count.DOUBLE_TRIPLET_SXTH) getsTheBeatValue /= 3/2.0;
					else if(count == Count.TRIPLET_SXTH) getsTheBeatValue /= 3.0;
					else if(count == Count.DOTTED_SXTH) getsTheBeatValue *= 1.5;
					if(getsTheBeat == Count.SXTH) return getsTheBeatValue;
					else getsTheBeatValue /= 2;
				 
				case TRIPLET_EGTH: 
				case DOUBLE_TRIPLET_EGTH:
				case EGTH: 
				case DOTTED_EGTH: 
					if(count == Count.DOUBLE_TRIPLET_EGTH) getsTheBeatValue /= 3/2.0;
					else if(count == Count.TRIPLET_EGTH) getsTheBeatValue /= 3.0;
					else if(count == Count.DOTTED_EGTH) getsTheBeatValue *= 1.5;
					if(getsTheBeat == Count.EGTH) return getsTheBeatValue;
					else getsTheBeatValue /= 2;
				
				case TRIPLET_QTR:
				case DOUBLE_TRIPLET_QTR:
				case QTR: 
				case DOTTED_QTR:
					if(count == Count.TRIPLET_QTR) getsTheBeatValue /= 3.0;
					else if(count == Count.DOUBLE_TRIPLET_QTR) getsTheBeatValue /= 3/2.0;
					else if(count == Count.DOTTED_QTR) getsTheBeatValue *= 1.5;
					if(getsTheBeat == Count.QTR) return getsTheBeatValue;
					else getsTheBeatValue /= 2;
					
				case TRIPLET_HALF:
				case DOUBLE_TRIPLET_HALF:
				case HALF: 
				case DOTTED_HALF: 
					if(count == Count.TRIPLET_HALF) getsTheBeatValue /= 3.0;
					else if(count == Count.DOUBLE_TRIPLET_HALF) getsTheBeatValue /= 3/2.0;
					else if(count == Count.DOTTED_HALF) getsTheBeatValue *= 1.5;
					if(getsTheBeat == Count.HALF) return getsTheBeatValue;
					else getsTheBeatValue /= 2;
					
				case TRIPLET_WHOLE:
				case DOUBLE_TRIPLET_WHOLE:
				case WHOLE: 
				case DOTTED_WHOLE: 
					if(count == Count.TRIPLET_WHOLE) getsTheBeatValue /= 3.0;
					else if(count == Count.DOUBLE_TRIPLET_WHOLE) getsTheBeatValue /= 3/2.0;
					else if(count == Count.DOTTED_WHOLE) getsTheBeatValue *= 1.5;
					if(getsTheBeat == Count.WHOLE) return getsTheBeatValue;
					else getsTheBeatValue /= 2;
					
				case TRIPLET_DWHOLE: 
				case DOUBLE_TRIPLET_DWHOLE:
				case DWHOLE: 
				case DOTTED_DWHOLE: 
					if(count == Count.TRIPLET_DWHOLE) getsTheBeatValue /= 3.0;
					else if(count == Count.DOUBLE_TRIPLET_DWHOLE) getsTheBeatValue /= 3/2.0;
					else if(count == Count.DOTTED_DWHOLE) getsTheBeatValue *= 1.5;
					if(getsTheBeat == Count.DWHOLE) return getsTheBeatValue;
					else getsTheBeatValue /= 2;
				case TRIPLET_LWHOLE: 
				case LWHOLE: 
				case DOUBLE_TRIPLET_LWHOLE:
				case DOTTED_LWHOLE: 
					if(count == Count.TRIPLET_LWHOLE) getsTheBeatValue /= 3.0;
					else if(count == Count.DOUBLE_TRIPLET_LWHOLE) getsTheBeatValue /= 3/2.0;
					else if(count == Count.DOTTED_LWHOLE) getsTheBeatValue *= 1.5;
					if(getsTheBeat == Count.LWHOLE) return getsTheBeatValue;
					else getsTheBeatValue /= 2;
				case UNKC : return 0;
			}
		}
		else if(count.ordinal() > getsTheBeat.ordinal()) {
			switch(count) {
				case DOTTED_LWHOLE: 
				case LWHOLE: 
				case DOUBLE_TRIPLET_LWHOLE:
				case TRIPLET_LWHOLE:  
					if(count == Count.TRIPLET_LWHOLE) getsTheBeatValue /= 3.0;
					else if(count == Count.DOUBLE_TRIPLET_LWHOLE) getsTheBeatValue /= 3/2.0;
					else if(count == Count.DOTTED_LWHOLE) getsTheBeatValue *= 1.5;
					if(getsTheBeat == Count.LWHOLE) return getsTheBeatValue;
					else getsTheBeatValue *= 2;
					
				case DOTTED_DWHOLE: 
				case DWHOLE: 
				case DOUBLE_TRIPLET_DWHOLE:
				case TRIPLET_DWHOLE:
					
					if(count == Count.TRIPLET_DWHOLE) getsTheBeatValue /= 3.0;
					else if(count == Count.DOUBLE_TRIPLET_DWHOLE) getsTheBeatValue /= 3/2.0;
					else if(count == Count.DOTTED_DWHOLE) getsTheBeatValue *= 1.5;
					if(getsTheBeat == Count.DWHOLE) return getsTheBeatValue;
					else getsTheBeatValue *= 2;
					
				case DOTTED_WHOLE: 
				case WHOLE:
				case DOUBLE_TRIPLET_WHOLE:
				case TRIPLET_WHOLE:
					if(count == Count.TRIPLET_WHOLE) getsTheBeatValue /= 3.0;
					else if(count == Count.DOUBLE_TRIPLET_WHOLE) getsTheBeatValue /= 3/2.0;
					else if(count == Count.DOTTED_WHOLE) getsTheBeatValue *= 1.5;
					if(getsTheBeat == Count.WHOLE) return getsTheBeatValue;
					else getsTheBeatValue *= 2;
				
				case DOTTED_HALF: 
				case HALF: 
				case DOUBLE_TRIPLET_HALF:
				case TRIPLET_HALF:  
					
					if(count == Count.TRIPLET_HALF) getsTheBeatValue /= 3.0;
					else if(count == Count.DOUBLE_TRIPLET_HALF) getsTheBeatValue /= 3/2.0;
					else if(count == Count.DOTTED_HALF) getsTheBeatValue *= 1.5;
					if(getsTheBeat == Count.HALF) return getsTheBeatValue;
					else getsTheBeatValue *= 2;
				
				case DOTTED_QTR: 
				case QTR:
				case DOUBLE_TRIPLET_QTR:
				case TRIPLET_QTR:  
					if(count == Count.TRIPLET_QTR) getsTheBeatValue /= 3.0;
					else if(count == Count.DOUBLE_TRIPLET_QTR) getsTheBeatValue /= 3/2.0;
					else if(count == Count.DOTTED_QTR) getsTheBeatValue *= 1.5;
					if(getsTheBeat == Count.QTR) return getsTheBeatValue;
					else getsTheBeatValue *= 2;
				
				case DOTTED_EGTH: 
				case EGTH: 
				case DOUBLE_TRIPLET_EGTH:
				case TRIPLET_EGTH:   
					
					if(count == Count.TRIPLET_EGTH) getsTheBeatValue /= 3.0;
					else if(count == Count.DOUBLE_TRIPLET_EGTH) getsTheBeatValue /= 3/2.0;
					else if(count == Count.DOTTED_EGTH) getsTheBeatValue *= 1.5;
					if(getsTheBeat == Count.EGTH) return getsTheBeatValue;
					else getsTheBeatValue *= 2;
				
				case DOTTED_SXTH: 
				case SXTH: 
				case DOUBLE_TRIPLET_SXTH:
				case TRIPLET_SXTH:
					
					if(count == Count.DOUBLE_TRIPLET_SXTH) getsTheBeatValue /= 3/2.0;
					if(count == Count.TRIPLET_SXTH) getsTheBeatValue /= 3.0;
					else if(count == Count.DOTTED_SXTH) getsTheBeatValue *= 1.5;
					if(getsTheBeat == Count.SXTH) return getsTheBeatValue;
					else getsTheBeatValue *= 2;
				
				case DOTTED_TSND: 
				case TSND:
				case DOUBLE_TRIPLET_TSND:
				case TRIPLET_TSND:  
					if(count == Count.TRIPLET_TSND) getsTheBeatValue /= 3.0;
					else if(count == Count.DOUBLE_TRIPLET_TSND) getsTheBeatValue /= 3/2.0;
					else if(count == Count.DOTTED_SFTH) getsTheBeatValue *= 1.5;
					if(getsTheBeat == Count.SFTH) return getsTheBeatValue;
					else getsTheBeatValue *= 2;
				
				case DOTTED_SFTH: 
				case SFTH:
				case DOUBLE_TRIPLET_SFTH:
				case TRIPLET_SFTH:  
					
					if(count == Count.TRIPLET_SFTH) getsTheBeatValue /= 3.0;
					else if(count == Count.DOUBLE_TRIPLET_SFTH) getsTheBeatValue /= 3/2.0;
					else if(count == Count.DOTTED_SFTH) getsTheBeatValue *= 1.5;
					if(getsTheBeat == Count.SFTH) return getsTheBeatValue;
					else getsTheBeatValue *= 2;
					
				case DOTTED_HTETH: 
				case HTETH:
				case DOUBLE_TRIPLET_HTETH:
				case TRIPLET_HTETH: 

					if(count == Count.TRIPLET_HTETH) getsTheBeatValue /= 3.0;
					else if(count == Count.DOUBLE_TRIPLET_HTETH) getsTheBeatValue /= 3/2.0;
					else if(count == Count.DOTTED_HTETH) getsTheBeatValue *= 1.5;
					if(getsTheBeat == Count.HTETH) return getsTheBeatValue;
					else getsTheBeatValue *= 2;
				case UNKC: return 0;
			}
		}
		return getsTheBeatValue;
	}
	public static float durationOf(Beat beat, Count getsTheBeat, float getsTheBeatValue)
	{
		return durationOf(beat.count, getsTheBeat, getsTheBeatValue);
	}
	
	
	public static class BeatSet 
	{
		public final Note tt, t2t, t, dt;
		public final Note ts, t2s, s, ds;
		public final Note te, t2e, e, de;
		public final Note tq, t2q, q, dq;
		public final Note th, t2h, h, dh;
		public final Note tw, t2w, w, dw;
		
		
		public BeatSet(Pitch pitch)
		{
			t = new Note(Count.TSND, pitch);
				tt = new Note(Count.TRIPLET_TSND, pitch);
				t2t = new Note(Count.DOUBLE_TRIPLET_TSND, pitch);
				dt = new Note(Count.DOTTED_TSND, pitch);
			s = new Note(Count.SXTH, pitch);
				ts = new Note(Count.TRIPLET_SXTH, pitch);
				t2s = new Note(Count.DOUBLE_TRIPLET_SXTH, pitch);
				ds = new Note(Count.DOTTED_SXTH, pitch);
			e = new Note(Count.EGTH, pitch);
				te = new Note(Count.TRIPLET_EGTH, pitch);
				t2e = new Note(Count.DOUBLE_TRIPLET_EGTH, pitch);
				de = new Note(Count.DOTTED_EGTH, pitch);
			q = new Note(Count.QTR, pitch);
				tq = new Note(Count.TRIPLET_QTR, pitch);
				t2q = new Note(Count.DOUBLE_TRIPLET_QTR, pitch);
				dq = new Note(Count.DOTTED_QTR, pitch);
			h = new Note(Count.HALF, pitch);
				th = new Note(Count.TRIPLET_HALF, pitch);
				t2h = new Note(Count.DOUBLE_TRIPLET_HALF, pitch);
				dh = new Note(Count.DOTTED_HALF, pitch);
			w = new Note(Count.WHOLE, pitch);
				tw = new Note(Count.TRIPLET_WHOLE, pitch);
				t2w = new Note(Count.DOUBLE_TRIPLET_WHOLE, pitch);
				dw = new Note(Count.DOTTED_WHOLE, pitch);
		}
	}
	
	public static class RestSet
	{
		public Rest tt, t2t, t, dt;
		public Rest ts, t2s, s, ds;
		public Rest te, t2e, e, de;
		public Rest tq, t2q, q, dq;
		public Rest th, t2h, h, dh; 
		public Rest tw, t2w, w, dw;
		public Rest tw2, t2w2, w2, dw2;
		public RestSet()
		{
			t = new Rest(Count.TSND);
				dt = new Rest(Count.DOTTED_TSND);
				t2t = new Rest(Count.DOUBLE_TRIPLET_TSND);
				tt = new Rest(Count.TRIPLET_TSND);
			s = new Rest(Count.SXTH);
				ts = new Rest(Count.TRIPLET_SXTH);
				t2s = new Rest(Count.DOUBLE_TRIPLET_SXTH);
				ds = new Rest(Count.DOTTED_SXTH);
			e = new Rest(Count.EGTH);
				te = new Rest(Count.TRIPLET_EGTH);
				t2e = new Rest(Count.DOUBLE_TRIPLET_EGTH);
				de = new Rest(Count.DOTTED_EGTH);
			q = new Rest(Count.QTR);
				tq = new Rest(Count.TRIPLET_QTR);
				t2q = new Rest(Count.DOUBLE_TRIPLET_QTR);
				dq = new Rest(Count.DOTTED_QTR);
			h = new Rest(Count.HALF);
				th = new Rest(Count.TRIPLET_HALF);
				t2h = new Rest(Count.DOUBLE_TRIPLET_HALF);
				dh = new Rest(Count.DOTTED_HALF);
			w = new Rest(Count.WHOLE);
				tw = new Rest(Count.TRIPLET_WHOLE);
				t2w = new Rest(Count.DOUBLE_TRIPLET_WHOLE);
				dw = new Rest(Count.DOTTED_WHOLE);
			w2 = new Rest(Count.DWHOLE);
				tw2 = new Rest(Count.TRIPLET_DWHOLE);
				t2w2 = new Rest(Count.DOUBLE_TRIPLET_DWHOLE);
				dw2 = new Rest(Count.DOTTED_DWHOLE);
		}
	}
	
	public static class MeasureLimit { 
		public int limitStart;
		public MeasureLimit(int measureNumber) { 
			this.limitStart = measureNumber;
		}
	}
	public static class Measure  
	{
		public LinkedList<Beat> beats;
		public Measure reference;
		public Meter meter;
		
		
		public Measure(int beatsPerMeasure, Count getsTheBeat)
		{
			beats = new LinkedList<Beat>();
			meter = new Meter(beatsPerMeasure, getsTheBeat);
		}
		public Measure(Meter meter)
		{
			beats = new LinkedList<Beat>();
			this.meter = meter;
		}
		public Beat first()
		{
			return beats.getFirst();
		}
		public Beat end()
		{
			return beats.getLast();
		}
		public void setMeter(Meter newMeterSpeed)
		{
			this.meter = newMeterSpeed;
		}
		public Measure addBeat(Beat toAdd)
		{
			beats.add(toAdd);
			return this;
		}
		public Beat pop()
		{
			return beats.pollLast();
		}
		public Measure addAllBeats(Collection<Beat> toAdd)
		{
			beats.addAll(toAdd);
			return this;
		}
		
		public Measure addAllBeats(Beat[] toAdd)
		{
			beats.addAll(Arrays.asList(toAdd));
			return this;
		}
		
		public boolean isEmpty()
		{
			return beats.isEmpty();
		}
		public String toString()
		{
			if(beats.isEmpty())
				return "||";
			Iterator<Beat> beatIt = beats.iterator();
			String measureLine = "";
			boolean firstBeat = true;
			do {
				Beat next = beatIt.next();
				if(firstBeat) {
					if(next.carry == Note.CARRY_PREV) 
						measureLine += "-";
					else
						measureLine += " ";
				}
				else
					measureLine += "";
				firstBeat = false;
				if(next.type().equals(Rest.getType()))
					measureLine += String.format("r.%c", Character.toLowerCase(next.count.name().charAt(0)));
				else if(next.type().equals(Strike.getType()))
					measureLine += ((Strike)next).sound;
				else if(next.type().equals(Note.getType())) {
					char firstCharCount = Character.toLowerCase(next.count.name().charAt(0));
					String noteName = ((Note)next).pitch.toString();
					measureLine += String.format("%s.%c", noteName, firstCharCount);
				}
				else
					measureLine += next;
				if(next.carry == Note.CARRY_NEXT)
					measureLine += "-";
				else	
					measureLine += " ";
				
			} while(beatIt.hasNext());
			
				
			return "|" + measureLine + "|";
		}
	}
}
