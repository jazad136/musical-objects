package music;

import java.util.TimerTask;

import javax.sound.midi.*;

import music.objects.MusicalObjects;
import music.objects.MusicalObjects.Beat;

/**
 * Source code for the TimingPair or "timing musical pair" class. A timing musical pair 
 * is a pair of commands designed to be run on a Java Timer, and play music via the synthesizer. 
 * The constructor's parameters are used to construct these tasks. When a timing pair
 * is added to a MusicSequence, the NoteSynchronizer takes these pairs and uses them
 * to play the beat specified at the volume specified. 
 * @author Jonathan A. Saddler Ph. D. 
 *
 */
public class TimingPair extends MusicalPair<TimerTask, TimerTask> {
	
	public TimingPair(final Beat inputBeat, final MidiChannel synth, int volume, long beatTimeMs)
	{
		super(new TimerTask(){public void run(){
			synth.noteOn(inputBeat.midiNumber, volume);
			
		}}, new TimerTask(){public void run(){
			synth.noteOff(inputBeat.midiNumber);
		}}, inputBeat, beatTimeMs);
	}
	
	public TimingPair(final Beat inputBeat, final MidiChannel synth, long beatTimeMs)
	{
		super(new TimerTask(){public void run(){
			synth.noteOn(inputBeat.midiNumber, inputBeat.volume);
		}}, new TimerTask(){public void run(){
			synth.noteOff(inputBeat.midiNumber);
		}}, inputBeat, beatTimeMs);
	}
}
