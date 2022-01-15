/**
 *  Copyright (C) 2021 Jonathan Saddler <1129384+jazad136@users.noreply.github.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 or (at your option)
 *  version 3 of the License.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package testing;

import static music.objects.MusicalObjects.Pitch.A4;
import static music.objects.MusicalObjects.Pitch.B4;
import static music.objects.MusicalObjects.Pitch.D3;
import static music.objects.MusicalObjects.Pitch.D4;
import static music.objects.MusicalObjects.Pitch.F3;
import static music.objects.MusicalObjects.Pitch.G3;

import java.util.Arrays;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Patch;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;

import music.objects.MusicalObjects.Beat;
import music.objects.MusicalObjects.Count;
import music.objects.MusicalObjects.Interval;
import music.objects.MusicalObjects.Note;
import music.objects.MusicalObjects.Pitch;
import music.objects.MusicalObjects.Rest;
import music.objects.Token.ScaleToken.Scale;

public class CanonTest {

	private static final int standardVol = 80;
	private static final int standardDur = 300;
	
	public static void main(String[] args) {
		try (Synthesizer synth = MidiSystem.getSynthesizer()) {
			synth.open();
			SelectedStandardInstruments ssi = new SelectedStandardInstruments(synth);
			boolean doubleCanon = hasDoubleCanonOption(args);
			canonTest(synth, ssi.strings, doubleCanon); 			
		} catch(MidiUnavailableException me) { 
			System.err.println("Midi system is unavailable.");
		}
		catch(InterruptedException ie) { 
			System.err.println("Interrupted.");
		}
	}
	
	public static boolean hasDoubleCanonOption(String[] args) { 
		if(args.length > 0) 
			if(args[0].equals("-double"))
				return true;
		
		return false;
	}
	public static void canonTest(InstrumentSet set) { 
		try (Synthesizer synth = MidiSystem.getSynthesizer()) {
			synth.open();
			canonTest(synth, set);
		} catch(MidiUnavailableException me) { 
			System.err.println("Midi system is unavailable.");
		}
		catch(InterruptedException ie) { 
			System.err.println("Interrupted.");
		}
	}
	public static class SelectedStandardInstruments { 
		// Check https://www.midi.org/specifications-old/item/gm-level-1-sound-set
		public InstrumentSet pianos
			, chromPercs
			, guitars
			, basses
			, strings
			, ensembles;
		public SelectedStandardInstruments(Synthesizer synth) {
			synth.loadAllInstruments(synth.getDefaultSoundbank());
			Instrument[] availInstruments = synth.getLoadedInstruments();
			pianos = new InstrumentSet(availInstruments, 0, 7);
			chromPercs = new InstrumentSet(availInstruments, 8, 15);
			InstrumentSet g2 = new InstrumentSet(availInstruments, 24, 31);
			InstrumentSet g1 = new InstrumentSet(availInstruments, 15, 15);
			guitars = new InstrumentSet(g1, g2);
			basses = new InstrumentSet(availInstruments, 32, 39);
			InstrumentSet s1 = new InstrumentSet(availInstruments, 41-1, 43-1);
			// contrabassoon (44) and timpani (48) aren't strings, 
			// contrabassoon (44) is a wind
			// timpani (48) is a drum. 
			
			InstrumentSet s2 = new InstrumentSet(availInstruments, 45-1, 47-1);
			InstrumentSet s3_ensembles = new InstrumentSet(availInstruments, 49-1, 56-1);
//			strings = new InstrumentSet(availInstruments, 40, 47);
			strings = new InstrumentSet(s1, s2);
			strings = new InstrumentSet(strings, s3_ensembles);
//			ensembles = new InstrumentSet(availInstruments, 49-1, 56-1);
		}
	}
	public static class InstrumentSet {
		Patch[] chosen;
		Instrument[] availInstruments; 
		public int setSize;
		public InstrumentSet(Instrument[] availInstruments, int first, int last) {
			this.setSize = last - first + 1;
			this.chosen = new Patch[this.setSize];
			this.availInstruments = new Instrument[this.setSize];
			int count = 0;
			for(int preset = first; preset <= last; preset++, count++) {
				this.availInstruments[count] = availInstruments[preset];
				this.chosen[count] = availInstruments[preset].getPatch();
			}
		}
		public InstrumentSet(InstrumentSet first, InstrumentSet second) { 
			this.setSize = first.setSize + second.setSize;
			this.chosen = Arrays.copyOf(first.chosen, this.setSize);
			this.availInstruments = Arrays.copyOf(first.availInstruments, this.setSize);
			int count = first.setSize;
			
			for(int preset = 0; preset < second.setSize; preset++, count++) { 
				this.availInstruments[count] = second.availInstruments[preset];
				this.chosen[count] = second.chosen[preset];
			}
		}
	}
	
	public static class Intervals { 
		static Pitch p1 = D4;
		static Pitch p2 = A4;
		static Pitch p3 = B4;
		static Pitch p4 = F3;
		static Pitch p5 = G3;
		static Pitch p6 = D3;
		static Pitch p7 = G3;
		static Pitch p8 = F3;
		Pitch[] keyProgression;
		Interval[] intProgression;
		final int totalChords;
		public Intervals() { 
			keyProgression = new Pitch[] {p1, p2, p3, p4, p5, p6, p7, p8};
			intProgression = new Interval[] { Interval.P1, Interval.M3, Interval.P5 };
			totalChords = keyProgression.length;
		}
		public Beat[] getNotes(Scale major, int progressionStep) { 
			Pitch play1 = major.toPitch(keyProgression[progressionStep], Interval.P1);
	    	Pitch play2 = major.toPitch(keyProgression[progressionStep], Interval.M3);
	    	Pitch play3 = major.toPitch(keyProgression[progressionStep], Interval.P5);
	    	return new Beat[] {
	    		 new Rest(Count.HALF)
	    		 ,new Note(Count.HALF, play1) 
	    		 ,new Note(Count.HALF, play2)
	    		 ,new Note(Count.HALF, play3)
	    	};
		}
	}
	public static Beat beat(Pitch pitch, Count length) { 
		if(pitch == null) 
			return new Rest(length);
		return new Note(length, pitch);
	}
	
	public static void canonTest(Synthesizer synth, InstrumentSet set, 
			boolean doubleCanon, int...volDur ) throws InterruptedException {
		int volume = standardVol;
		if(volDur.length > 0) { 
			volume = volDur[0];
		}
		int duration = standardDur;
		if(volDur.length > 1) { 
			duration = volDur[1];
		}
    	MidiChannel[] ac = synth.getChannels();
    	int instC = 0;
    	System.out.println("start.");
    	
    	Scale major = Scale.MAJOR;
    	Intervals intv = new Intervals();
    	
    	
//    	plays
//    	Beat[][] matrix = {
//    		
//    	}
    	Instrument s;
    	for(int p = 0; p < set.chosen.length; p++) {
    		Patch patch = set.chosen[p];
    		int step = p % intv.totalChords;
    		System.out.println(set.availInstruments[p]);
    		System.out.println("Step: " + (step+1));
    		ac[instC].programChange(patch.getBank(), patch.getProgram());
    		Beat[] plays = intv.getNotes(major, 0);
    		for(int i = 1; i <= 9; i++) {
    			if(i != 9) { 
    				Thread.sleep( duration );
    			}
    			if(i == 1) { 
    				// do nothing
    			}
    			if(i == 2) { 
    				ac[instC].noteOn(plays[1].midiNumber, volume);
    			}
    			if(i == 3) { 
    				ac[instC].noteOn(plays[2].midiNumber, volume);
    			}
    			if(i == 4) { 
    				ac[instC].noteOff(plays[1].midiNumber);
    				ac[instC].noteOn(plays[3].midiNumber, volume);
    			}
    			if(i == 5) { 
    				ac[instC].noteOff(plays[2].midiNumber);
    			}
    			if(doubleCanon) { 
	    			if(i == 6) { 
	    				ac[instC].noteOff(plays[3].midiNumber);
	    				ac[instC].noteOn(plays[1].midiNumber, volume);
	    			}
	    			if(i == 7) { 
	    				ac[instC].noteOff(plays[1].midiNumber);
	    				ac[instC].noteOn(plays[2].midiNumber, volume);
	    			}
	    			if(i == 8) {     				
	    				ac[instC].noteOff(plays[2].midiNumber);
	    				ac[instC].noteOn(plays[3].midiNumber, volume);
	    			}
	    			if(i == 9) { 
	    				ac[instC].noteOff(plays[3].midiNumber);
	    			}
    			}
    			else {
    				if(i == 6) { 
    					ac[instC].noteOff(plays[3].midiNumber);
					}
					if(i == 7) { 
						// do nothing
					}
					if(i == 8) {     				
						// do nothing
					}
					if(i == 9) { 
						// do nothing
					}
    			}
    		}
    	}
        System.out.println("stop.");
	    
	}
	public static void canonTest(Synthesizer synth, InstrumentSet set, int...volDur ) throws InterruptedException
	{
		int volume = standardVol;
		if(volDur.length > 0) { 
			volume = volDur[0];
		}
		int duration = standardDur;
		if(volDur.length > 1) { 
			duration = volDur[1];
		}
    	MidiChannel[] ac = synth.getChannels();
    	int instC = 0;
    	System.out.println("start.");
    	
    	Scale major = Scale.MAJOR;
    	Intervals intv = new Intervals();
    	
    	
//    	plays
//    	Beat[][] matrix = {
//    		
//    	}
    	Instrument s;
    	for(int p = 0; p < set.chosen.length; p++) {
    		Patch patch = set.chosen[p];
    		int step = p % intv.totalChords;
    		System.out.println(set.availInstruments[p]);
    		System.out.println("Step: " + (step+1));
    		ac[instC].programChange(patch.getBank(), patch.getProgram());
    		Beat[] plays = intv.getNotes(major, 0);
    		for(int i = 1; i <= 9; i++) {
    			if(i != 9) { 
    				Thread.sleep( duration );
    			}
    			if(i == 1) { 
    				// do nothing
    			}
    			if(i == 2) { 
    				ac[instC].noteOn(plays[1].midiNumber, volume);
    			}
    			if(i == 3) { 
    				ac[instC].noteOn(plays[2].midiNumber, volume);
    			}
    			if(i == 4) { 
    				ac[instC].noteOff(plays[1].midiNumber);
    				ac[instC].noteOn(plays[3].midiNumber, volume);
    			}
    			if(i == 5) { 
    				ac[instC].noteOff(plays[2].midiNumber);
    			}
    			if(i == 6) { 
				ac[instC].noteOff(plays[3].midiNumber);
				}
				if(i == 7) { 
					// do nothing
				}
				if(i == 8) {     				
					// do nothing
				}
				if(i == 9) { 
					// do nothing
				}
    		}
    	}
        System.out.println("stop.");
	    
	}
	
	
	
	public static void repeatThenTriple(int volume, int duration, InstrumentSet set)
	{
	    try (Synthesizer synth = MidiSystem.getSynthesizer();) {
	    	synth.open();
	    	Soundbank defSounds = synth.getDefaultSoundbank();
	    	synth.loadAllInstruments(defSounds);
	    	Instrument[] ai = synth.getLoadedInstruments();
	    	int stdVol = volume;
	    	MidiChannel[] ac = synth.getChannels();
	    	int instC = 0;
	    	System.out.println("start.");
	    	
	    	Instrument s;
	    	for(int p = 0; p < set.chosen.length; p++) {
	    		Patch patch = set.chosen[p];
	    		System.out.println(set.availInstruments[p]);
	    		ac[instC].programChange(patch.getBank(), patch.getProgram());
	    		
	    		for(int i = 0; i < 5; i++) {
	    			volume = stdVol + i*30;
		    		ac[instC].noteOn(Pitch.C4.midiNumber, volume); // C note
		    		if(i == 4) {
		    			ac[instC].noteOn(Pitch.E4.midiNumber, volume);
		    			ac[instC].noteOn(Pitch.G4.midiNumber, volume);
		    		}
		    		Thread.sleep( duration );
		    		ac[instC].noteOff(Pitch.C4.midiNumber);
		    		if(i == 4) {
		    			ac[instC].noteOff(Pitch.E4.midiNumber);
			    		ac[instC].noteOff(Pitch.G4.midiNumber);	
		    		}
		    		Thread.sleep( duration/3 );
	    		}
	    	}
	        System.out.println("stop.");
	    } catch(MidiUnavailableException | InterruptedException e) {
	    	
	    	
	    }
	}
}
