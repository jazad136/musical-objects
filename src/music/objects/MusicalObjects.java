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
	}

	/** 
	 * A pitch stores information about the exact midi number that some 
	 * pitch on a MusicalObjects score sheet expresses, as well as
	 * the frequency value of the note being expressed. If necessary, 
	 * accidental aliases are also provided to help express opposing accidentals
	 * that mean the same thing. <br>
	 * <br>
	 * Accidental pitches are identified by their "sharp"
	 * alternatives, but accidental alternatives of pitches that contain "flats" 
	 * are also recognized in MusicalObjects score sheets each pitch. 
	 */
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
	
		/** The the frequency that identifies the sound of the note when played aloud */
		public final double noteValue;
		/** The number of this note as specified in MIDI standards*/
		public final int midiNumber;
		/** For accidentals, the string or set of strings that help identify this note
		 * using the literal one half step higher */
		public final String[] downAccidentalAliases;
		public final String nicknameAlias;
		/** Constructor for the pitch, that sets the noteValue, the midi note number, 
		 *  any aliases (for accidental notes), and nickname aliases for the note itself. 
		 */
		private Pitch(double noteValue, int midiNoteNumber, String downAccidentalAliases, String nicknameAliases)
		{
			this.noteValue = noteValue; 
			this.midiNumber = midiNoteNumber;
			this.downAccidentalAliases = downAccidentalAliases.split("/");
			this.nicknameAlias = nicknameAliases;
		}
		/** Constructor for the pitch, that sets the noteValue, the midi note Number, 
		 *  any aliases (for accidental notes). 
		 */
		private Pitch(double noteValue, int midiNoteNumber, String downAccidentalAliases)
		{
			this.noteValue = noteValue; 
			this.midiNumber = midiNoteNumber;
			this.downAccidentalAliases = downAccidentalAliases.split("/");
			this.nicknameAlias = "";
		}
		/** Constructor for the pitch, that sets the noteValue and the midi note number
		 */
		private Pitch(double noteValue, int midiNoteNumber)
		{
			this.noteValue = noteValue; 
			this.midiNumber = midiNoteNumber;
			this.downAccidentalAliases = new String[0];
			this.nicknameAlias = "";
		}
		/**
		 * Returns a String representation of this note. If it has a nickname it is printed.
		 * Otherwise, its enumerated constant is printed instead. 
		 */
		public String toString() {
			return nicknameAlias.isEmpty() ? name() : nicknameAlias;
		}
		
		/**
		 * Transposes this pitch to a new pitch. Pitches are organized in this enumeration
		 * by half steps. The integer parameter half steps is used to find the necessary
		 * pitch out of the ones supported in Musical Objects score sheets. If too extreme
		 * a value is provided, Pitch.UNK is returned. 
		 */
		public Pitch transpose(int halfSteps) {
			if(ordinal() + halfSteps > Pitch.values().length 
			|| ordinal() + halfSteps < 0)
				return UNK;
			return values()[ordinal()+halfSteps];
		}
	}
	
	/** 
	 * A sound stores information about the midi number some 
	 * sound token on a MusicalObjects score sheet will express within the drum kit
	 * NoteSynchronizer will choose to use to play drum sounds.  
	 * 
	 * The frequency that all sounds are played on is the C4 frequency on the keyboard. 
	 */
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
		
		/** Constructor that gives a negative one value to the midi number setting. */
		private Sound()
		{
			this.midiNumber = -1;
		}
		/** Constructor that sets the midi number argument to the integer specified. */
		private Sound(int midiNumber)
		{
			this.midiNumber = midiNumber;
		}
	}
	/**
	 * Empty interface for the staff component, that is extended by all tokens on a 
	 * Musical Objects score sheet. 
	 */
	public abstract interface StaffComponent
	{
		
	}
	
	/** 
	 * Class representing the object helping to store data about the volume token. 
	 */
	public static class Volume implements StaffComponent
	{
		public int volume;
		public Volume(int setting) {
			this.volume = setting;
		}
	}
	/** 
	 * A Musical Objects instrument stores information about instrument tokens
	 * provided via musical objects score sheets, helping to dictate whether they were or 
	 * were not found on the system.  
	 */
	public static class Instrument implements StaffComponent
	{
		/** The main pitch that this instrument will be used to play if one is specified */
		public Pitch homePitch;
		/** The system name of the instrument specified that will be used to play instances of sound */
		public String patch;
		/** If there are multiple MIDI Instrument objects with the same name, the selection of
		 * which one to play. Otherwise, this is set to -1 for unconfirmed*/
		public int selection;
		/** The volume at which to play this instrument. If no volume is specified, has a setting of -1 for unconfirmed */
		public int volume; 
		/** A constant representing that a selection has not been confirmed */
		public static final int SELECTION_UNCONFIRMED = -1;
		
		/** Constructor for an instrument object, specifying only the patch to be played. */
		public Instrument(String patch)
		{
			this.patch = patch;
			this.selection = SELECTION_UNCONFIRMED;
			this.volume = SELECTION_UNCONFIRMED;
			this.homePitch = Pitch.UNK;
		}
		
		/** 
		 * Constructor for an instrument object, specifying the patch and numerical 
		 * selection for similarly named patches. 
		 */
		public Instrument(String patch, int selection)
		{
			this(patch);
			this.selection = selection;
		}
		
		/**
		 * Contsructor for an instrument object, specifying the patch and 
		 * base pitch to be played using this instrument (reserved for use
		 * to play strikes).
		 */
		public Instrument(String patch, Pitch basePitch)
		{
			this(patch);
			this.homePitch = basePitch;
		}
		
		/**
		 * Constructor for an instrument object, specifying the patch, 
		 * the patch selection, and the
		 * base pitch to be played using this instrument
		 */
		public Instrument(String patch, int selection, Pitch basePitch)
		{
			this(patch, selection);
			this.homePitch = basePitch;
		}
		
		/** Constructor for an instrument object that sets the patch, the
		 * base pitch and the volume that the instrument is to be played at.*/ 
		public Instrument(String patch, Pitch basePitch, int volumeSetting)
		{
			this(patch, basePitch);
			this.volume = volumeSetting;
		}
		/** Constructor for an instrument object, specifying the patch, patch selection
		 * for similarly named patches, the homePitch to play the instrument, and the 
		 * volume setting to play the instrument at. 
		 */
		public Instrument(String patch, int selection, Pitch basePitch, int volumeSetting)
		{
			this(patch, selection, basePitch);
			this.volume = volumeSetting;
		}
		/** Returns this instrument object as a string, Consisting of the String \"Instrument\"
		 * followed by the patch, and if set, the patch selection, volume, and home pitch.*/
		public String toString()
		{
			return "Instrument " + patch + (selection != SELECTION_UNCONFIRMED ? " "+selection : " ") 
					+ " at " + (volume != SELECTION_UNCONFIRMED ? "volume " + volume : "normal volume")
					+ ((homePitch != Pitch.UNK) ? "(home pitch " + homePitch + ")" : "");
		}
		
		/**
		 * Returns a copy of this instrument object, with all instance variables 
		 * carried over from this object. 
		 */
		public Instrument copy()
		{
			Instrument toReturn = new Instrument(patch);
			toReturn.homePitch = homePitch;
			toReturn.volume = volume;
			toReturn.patch = patch;
			toReturn.selection = selection;
			return toReturn;
		}
	}
	
	/** 
	 * Reserved class representing the measure marker component of a musical objects
	 * score sheet. This class will be used to carry information about 
	 * measure tokens. 
	 */
	public static class MeasureMarker implements StaffComponent
	{
		
	}
	/**
	 * A meter is an object storing information about the time signature of a 
	 * staff line, as well as how long notes in a staff without a count durate. 
	 * beatsMerPeasure will be used in future iterations of MusicalObjects.
	 */
	public static class Meter implements StaffComponent
	{
		/** An integer describing how many beats should exist between measure markers in this meter */
		public int beatsPerMeasure;
		/** A count object describing the count object representing the default beat of the meter */
		public Count getsTheBeat;
		/** A long integer denoting the number of milliseconds each getsTheBeat beat should last. */
		public long getsTheBeatDurationMs;
		/** A constant representing that a duration is undefined */
		public static final long DURATION_UNDEFINED = -1;
		/** Constructor for the Meter class, which sets the beats per measure and the 
		 *  count that gets the beat. */
		public Meter(int beatsPerMeasure, Count getsTheBeat)
		{
			this.beatsPerMeasure = beatsPerMeasure;
			this.getsTheBeat = getsTheBeat;
			this.getsTheBeatDurationMs = DURATION_UNDEFINED;
		}
		
		/** Constructor for the meter class. Sets the beats per measure and the 
		 *  count that gets the beat, as well as that beat's intended millisecond duration.
		 */
		public Meter(int beatsPerMeasure, Count getsTheBeat, long getsTheBeatDurationMs)
		{
			this.beatsPerMeasure = beatsPerMeasure;
			this.getsTheBeat = getsTheBeat;
			this.getsTheBeatDurationMs = getsTheBeatDurationMs;
		}
	}
	/**
	 * A Beat stores information about a musical event. In musical objects, a pitch sound that 
	 * results from playing an instrument, a duration of not making sound taken in the middle 
	 * of pieces, and a strike on the drum kit, are all represented by the information 
	 * recorded in the beat object. 
	 *
	 * An instance of beat must return a string representation of its type. 
	 * It must also return some representation of<br>
	 * a pitch value at which to play the sound,<br> 
	 * a count value representing the duration of the beat<br>
	 * some representation of the midi number used to play the sound,<br> 
	 * and a number representing the volume at which the sound is to be played. <br>
	 * 
	 * Other variables in the beat object also exist to help encode other valuable information.
	 */
	public abstract static class Beat implements StaffComponent
	{
		/** A long representing how fast this beat should be played in milliseconds */
		public long playSpeedMs;
		/** The count assigned to this beat*/
		public Count count;
		/** The value of the pitch assigned to this beat */
		public double pitchVal;
		/** The midiNumber of the pitch to be played */
		public int midiNumber;
		/** Helps determine whether this beat falls underneath special criteria */
		public boolean isSpecialSound;
		/** The volume this beat should be played at*/
		public int volume;
		/** The instrument used to play this beat */
		public Instrument playsWith;
		/** The state of whether this beat is a "carrying" beat 
		 * To be used in future iterations of this project */
		
		
		/** Constructor for the beat that instantiates basic
		 *  values for the volume and instrument for this beat */
		public Beat()
		{
			volume = Instrument.SELECTION_UNCONFIRMED;
			this.playsWith = new Instrument("");
			
		}
		
		/** Return the first part of this beat's count */
		public String firstPartOfCount()
		{
			switch(count) {
			case UNKC 	: return "";
			default		:
			}
			return count.alias.toLowerCase();
		}
		
		/** Return the string representation of this beat's descriptive type */
		public abstract String type();
		
		/** Set the millisecond playstring from a number representing beats per minute */
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
		
		/** Set how long this beat should durate in milliseconds */
		public void setPlaySpeedMs(long playSpeed) { this.playSpeedMs = playSpeed; }
	}
	
	/** A Musical Objects Rest indicates that a break
	 * should occur of a certain duration in the score. 
	 */
	public static class Rest extends Beat {
		
		/** Constructor for the rest that sets the count to an unset value */
		public Rest() { this(Count.UNKC); }
		
		/** Constructor for the rest that sets the count to a specific value */
		public Rest(Count count) {
			this.count = count;
			pitchVal = 0;
			midiNumber = 0;
			volume = 0;
		}
		/** Constructor for the rest that sets the count to a specific value
		 * as well as the hard duration for the rest in the same constructor
		 */
		public Rest(Count count, long playSpeedMs)
		{
			this(count);
			this.playSpeedMs = playSpeedMs;
		}

		/** Return the string representation of this beat's descriptive type.
		 * For the Rest, returns the string "rest" */
		public static String getType(){return "rest";}
		
		/** An instance member that returns the string representation of this 
		 * beat's descriptive type. For the Rest, returns the string "rest" */ 
		public String type() {return getType();}
		
		/** Return a string representation of this rest object. 
		 *  Consists of the string "r" followed by a shortened string version 
		 *  of the count object */
		public String toString()
		{
			return "r" + (count != Count.UNKC ? "." + firstPartOfCount() : "");
		}
	}
	
	/** 
	 * A Musical Objects note indicates that an instrument should be played. 
	 */
	public static class Note extends Beat {
		/** The pitch object holding the midi number used to play the note. */
		public Pitch pitch;
		
		/** Constructor for the note, which sets only the pitch to play and 
		 * the count for the duration of the note. 
		 * @param count
		 * @param pitch
		 */
		public Note(Count count, Pitch pitch) {
			this.count = count;
			this.pitch = pitch;
			pitchVal = pitch.noteValue;
			midiNumber = pitch.midiNumber;
		}
		
		/** Constructor for the note, which sets the count for the duration of the note,
		 * the pitch to play, and the volume at which to play the note. 
		 */
		public Note(Count count, Pitch pitch, int volumeSeting)
		{
			this.count = count;
			this.pitch = pitch;
			this.volume = volumeSeting;
			pitchVal = pitch.noteValue;
			midiNumber = pitch.midiNumber;
		}
		/** 
		 * Constructor for the note, which sets the count for the duration of the note,
		 * the pitch to play, the volume at which to play the note, and the 
		 * length of time this note should be played for in milliseconds.
		 */
		public Note(Count count, Pitch pitch, int volumeSetting, long playSpeedMs)
		{
			this(count, pitch, volumeSetting);
			this.playSpeedMs = playSpeedMs;
		}
		/** Copy constructor for the note, that copies the count, the pitch, the old
		 * volume, and the playspeed to this new object.
		 */
		public Note(Note oldNote)
		{
			this(oldNote.count, oldNote.pitch, oldNote.volume, oldNote.playSpeedMs);
		}
		/** An instance member that returns the string representation of this 
		 * beat's descriptive type. For the Note , returns the string "note" */ 
		public String type(){return "note";}
		
		/** Returns a string representation of this note, the name of the pitch,
		 * followed by the short name for the duration count of the note.
		 */
		public String toString()
		{
			return pitch.name() + "." + count.name();
		}
		
		/** Return the string representation of this beat's descriptive type.
		 * For the Note, returns the string "note" */
		public static String getType() {return "note";}
	}

	
	/**
	 * A Musical Objects strike indicates an onomatopoeia or in other words, 
	 * a "sound-sounding"-word, that should be played on the MIDI drum kit.
	 * The Sound chosen selects the midi number that should be played.
	 */
	public static class Strike extends Beat {
		
		/** The sound object holding the midi number used to play the strike. */
		public Sound sound;
		
		/** Constructor for the strike which sets only the strike to play and
		 * the count for the duration of the strike.
		 */
		public Strike(Count count, Sound sound)
		{
			this.isSpecialSound = true;
			this.count = count;
			this.sound = sound;
			pitchVal = Pitch.C4.noteValue;
			midiNumber = sound.midiNumber;
		}
		/** Constructor for the strike, which sets the count duration
		 * of the strike, the sound to play, and the volume loudness at which to 
		 * play the strike.
		 */
		public Strike(Count count, Sound sound, int volumeSetting)
		{
			this(count, sound);
			this.volume = volumeSetting;
		}
		/**
		 * Constructor for the strike, which sets the count duration of the strike,
		 * the sound to play, the volume at which the strike should be played, and
		 * the speed of the score this strike is being played for, in milliseconds.
		 */
		public Strike(Count count, Sound sound, int volumeSetting, long playSpeedMs)
		{
			this(count, sound, volumeSetting);
			this.playSpeedMs = playSpeedMs;
		}
		
		/** An instance member that returns the string representation of this 
		 *  beat's descriptive type. For the Strike, returns the string "strike" */ 
		@Override
		public String type() { return getType(); }
		
		/** Return the string representation of this beat's descriptive type.
		 * For the Note, returns the string "strike" */
		public static String getType(){ return "strike"; }
		
		/** Returns a string representation of this note, the name of this 
		 *  pitch, in all capital letters. 
		 */
		public String toString() { return sound.name(); }
	}
	
	
	/**
	 * Report on the scaled duration of the count, given the provided information about the 
	 * score. The second count, getsTheBeat, is used to determine the standard count
	 * of the score, for which to help determine the length of the count in question.
	 * The getsTheBeatValue is divided or multiplied by a "scale factor" and
	 * returned from this function. 
	 * 
	 * This scale factor is calculated to be greater than 1,
	 * and the value returns from this function is set to grow, if getsTheBeat's relative
	 * duration is greater than count's duration. The scale factor is less than one,
	 * and the value returned is set to shrink if getTheBeat's relative duration is less
	 * than the count's duration.
	 */
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
	
	/** 
	 * Report on the scaled duration of the count, given the provided information about the 
	 * score. The second count, getsTheBeat, is used to determine the standard count
	 * of the score, for which to help determine the length of the count of the beat provided.
	 * The getsTheBeatValue is divided or multiplied by a "scale factor" and
	 * returned from this function. 
	 * 
	 * This scale factor is calculated to be greater than 1,
	 * and the value returns from this function is set to grow, if getsTheBeat's relative
	 * duration is greater than count's duration. The scale factor is less than one,
	 * and the value returned is set to shrink if getTheBeat's relative duration is less
	 * than the count's duration.
	 */
	public static float durationOf(Beat beat, Count getsTheBeat, float getsTheBeatValue)
	{
		return durationOf(beat.count, getsTheBeat, getsTheBeatValue);
	}
}
