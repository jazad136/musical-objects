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
package music;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TimerTask;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.Patch;
import javax.sound.midi.Synthesizer;

import music.objects.MusicalObjects;
import music.objects.MusicalObjects.Beat;
import music.objects.MusicalObjects.Rest;

/**
 * This class keeps a representation of a recorded list of notes. 
 * @author jsaddle
 *
 */
public class MusicSequence extends ArrayList<MusicalPair<TimerTask, TimerTask>>{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public long beatTimeMs;
	int lastChannelNum;
	
	// copy constructor
	public MusicSequence(MusicSequence older)
	{
		this(older.beatTimeMs, older);
	}
	
	public MusicSequence(long beatTimeMs)
	{
		this(beatTimeMs, new ArrayList<>());
	}
	
	public MusicSequence(long beatTimeMs, Collection<MusicalPair<TimerTask, TimerTask>> allBeats)
	{
		super(allBeats);
		this.beatTimeMs = beatTimeMs;
		lastChannelNum = -1;
	}
//	public MusicSequence(long beatTimeMs, Collection<TimingPair> allBeats)
//	{
//		super(allBeats);
//		this.beatTimeMs = beatTimeMs;
//		nextChannel = 0;
//	}
	
//	public static List<TimingPair> generateTonesMidi(Beat[] notes, int volume, boolean addHarmonic, String midiInstrument) throws MidiUnavailableException
//	{
//		Synthesizer synth = MidiSystem.getSynthesizer();
//	    synth.open();
//	    MidiChannel[] channels = synth.getChannels();
//	    MidiChannel pianoChannel = channels[0];
//	    List<TimingPair> tp = new ArrayList<TimingPair>();
//		for(int i = 0; i < notes.length; i++) {
//			Beat nextB = notes[i];
//			TimingPair t = new TimingPair(nextB, pianoChannel, volume);
//			tp.add(t);
//		}
//		return tp;
//	}
	
	public static int findInstrument(Synthesizer synth, String instrumentName) {
		Instrument[] allInstruments = synth.getLoadedInstruments();
		for(int i = 0; i < allInstruments.length; i++) {
        	String eName = allInstruments[i].getName();
        	boolean matches = eName.toLowerCase().contains(instrumentName.toLowerCase());
        	if(matches)
        		return i;
        }
		return -1;
	}
	/**
	 * Return true if the new channel was successfully added
	 * @return
	 */
	public static Patch selectNext2(Instrument[] allInstruments, Beat nextB, Patch defaultPatch) {
		String bPatch = nextB.playsWith.patch.toLowerCase();
		int bSelection = nextB.playsWith.selection;
		if(bSelection == MusicalObjects.Instrument.SELECTION_UNCONFIRMED)
			bSelection = 1;
		int count = 0;
		
		for(javax.sound.midi.Instrument e : allInstruments) 
			
			if(e.getName().toLowerCase().contains(bPatch)) {
				count++;
        		if(count == bSelection)  
        			return e.getPatch();
        	}
		return defaultPatch;
	}	
	
	public static Patch selectNext(Instrument[] allInstruments, Beat nextB, Patch defaultPatch) {
		String bPatch = nextB.playsWith.patch.toLowerCase();
		int bSelection = nextB.playsWith.selection;
		if(bSelection == MusicalObjects.Instrument.SELECTION_UNCONFIRMED)
			bSelection = 1;
		int count = 0;
		for(javax.sound.midi.Instrument e : allInstruments) 
			if(e.getName().toLowerCase().contains(bPatch)) {
				count++;
        		if(count == bSelection)  
        			return e.getPatch();
        	}
		return defaultPatch;
	}	
	
	public static Patch findDefaultPatch(Instrument[] loadedInstruments) {
		for(javax.sound.midi.Instrument e : loadedInstruments) 
	    	if(e.getName().toLowerCase().contains("piano")) 
	    		return e.getPatch();
		return null;
	}
	
	public List<MusicalPair<TimerTask, TimerTask>> encodeBeats(Beat[] notes, Synthesizer synth, int defaultChannelNum) {
		MidiChannel[] channels = synth.getChannels();
		return encodeBeats(channels, synth, defaultChannelNum, 9, notes);
	}
	public List<MusicalPair<TimerTask, TimerTask>> encodeBeats(MidiChannel[] channels, 
			Synthesizer synth, int defaultChannelIdx, int drumChannelIdx, Beat[] notes) {
		
		List<MusicalPair<TimerTask, TimerTask>> tp = new ArrayList<>();
		if(notes.length == 0)
			return tp;
	    javax.sound.midi.Instrument[] allInstruments = synth.getLoadedInstruments();
	    Patch defaultP = findDefaultPatch(allInstruments);
	    MidiChannel drumChannel = channels[drumChannelIdx];
	    MidiChannel outputChannel = channels[defaultChannelIdx];
	    
	    Patch instrumentPatch = selectNext(allInstruments, notes[0], defaultP);
		outputChannel.programChange(instrumentPatch.getBank(), instrumentPatch.getProgram());
		Beat theBeat;
	    int channelIdx = defaultChannelIdx;
	    for(int i = 0; i < notes.length; i++) {
	    	theBeat = notes[i];
			if(i > 0 && !notes[i-1].playsWith.patch.equalsIgnoreCase(theBeat.playsWith.patch)) {
				Patch nextPatch = selectNext(allInstruments, theBeat, defaultP);
				if(instrumentPatch != nextPatch) { 
					channelIdx++;
					while(channelIdx < channels.length && channels[channelIdx] == null) { 
						outputChannel = channels[channelIdx];
						channelIdx++;
					}
					if(channelIdx > channels.length) 
						throw new RuntimeException("Midi Channel Overflow: Out of MIDI Channels.");
					
					outputChannel.programChange(instrumentPatch.getBank(), instrumentPatch.getProgram());
					instrumentPatch = nextPatch;
				}
			}
			MusicalPair<TimerTask, TimerTask> t = null;
			MidiChannel nextMidiChannel = theBeat.isSpecialSound ? drumChannel : outputChannel;
			t = new TimingPair(theBeat, nextMidiChannel, theBeat.playSpeedMs);
			
			tp.add(t);
		}
	    lastChannelNum = channelIdx;
		return tp;
	}

	public int getLastChannelNum() { return lastChannelNum; }
}
