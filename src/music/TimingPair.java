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

import java.util.TimerTask;

import javax.sound.midi.*;

import music.objects.MusicalObjects;
import music.objects.MusicalObjects.Beat;

public class TimingPair extends MusicalPair<TimerTask, TimerTask> {
	
//	public final Beat beat;
//	public long time;
	
	public TimingPair(final Beat inputBeat, final MidiChannel synth, int volume, long beatTimeMs)
	{
		super(new TimerTask(){public void run(){
			synth.noteOn(inputBeat.midiNumber, volume);
			
		}}, new TimerTask(){public void run(){
			synth.noteOff(inputBeat.midiNumber);
		}}, inputBeat, beatTimeMs);
//		this.beat = inputBeat;
	}
	
	public TimingPair(final Beat inputBeat, final MidiChannel synth, long beatTimeMs)
	{
		super(new TimerTask(){public void run(){
			synth.noteOn(inputBeat.midiNumber, inputBeat.volume);
		}}, new TimerTask(){public void run(){
			synth.noteOff(inputBeat.midiNumber);
		}}, inputBeat, beatTimeMs);
//		this.beat = inputBeat;
	}
	public static class CarryPair extends MusicalPair<TimerTask, TimerTask>
	{
		public CarryPair(final Beat inputBeat, final MidiChannel synth, int volume, long beatTimeMs)
		{
			super(new TimerTask(){public void run()
			{
				synth.noteOn(inputBeat.midiNumber, volume);
			}} , new TimerTask(){public void run(){
				// nothing happens. 
			}},inputBeat, beatTimeMs);
		}
	}
}
