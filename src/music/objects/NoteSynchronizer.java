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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import music.MusicSequence;
import music.MusicalPair;
import music.objects.MusicalObjects.Count;
import music.objects.MusicalObjects.Instrument;
import music.objects.MusicalObjects.Meter;
import music.objects.MusicalObjects.Pitch;

public class NoteSynchronizer {
	/** List of MusicSequence objects containing MusicalObjects score sheet 
	 * elements that will be added to the music player threads */
	private ArrayList<MusicSequence> basis;
	public static Count defaultCount = Count.QTR;
	
	/** The default volume instruments in MusicalObjects are played at */
	public static final int defaultVolume = 100;
	/** The delay to wait, in milliseconds, to start music */
	public static final int delayToStartMusic = 3000;
	/** The default number of meter beats in a measure */
	public static final int defaultBeatsPerMeasure = 4;
	/** The default instrument patch to play songs in */
	public static final String defaultInstrumentPatch = "piano";
	/** The deafult key that intervals are played in */
	public static final Pitch defaultKey = Pitch.C4;
	/** The default MusicalObjects instrument object used to play songs */
	public static final Instrument defaultInstrument = new Instrument("piano");
	static {
		defaultInstrument.volume = defaultVolume;
	}
	/** The default meter object used to define timing */
	public static final Meter defaultMeter = new Meter(defaultBeatsPerMeasure, defaultCount);
	/** the semaphores used to signal the end of the song, and that help to end the program */
	public final Semaphore scheduleWait, doneWait;
	/** The schedule of times registered when adding songs to the track threads */
	public long[][][] schedule;
	
	/** Constructor for the NoteSynchronizer class. 
	 *  Sets up the semaphores to be used to keep track of completion of various end
	 *  tasks for music player, as well as the list to hold new music sequences
	 *  containing musical objects score sheet elements to be played */
	public NoteSynchronizer()
	{
		scheduleWait = new Semaphore(0, true);
		doneWait = new Semaphore(0, true);
		basis = new ArrayList<>();
	}
	
	public NoteSynchronizer(Count getsTheBeat) { this(); }
	
	/** Adds a new list of music sequence objects to this note synchronizer to be played later
	 * via a call to startMusic()
	 */
	public void addMusicSequence(MusicSequence mseq) { basis.add(mseq); }
	
	/** 
	 * Starts playing musical objects score sheet music. 
	 * Creates a copy of the basis containing the sequences that contain content,
	 * then using this copy, schedule as many threads as needed to play all tracks
	 * using the schedule() method. 
	 */
	public void startMusic()
	{
		// create a copy of the basis containing useful sequences to play.
		ArrayList<MusicSequence> toPlay = new ArrayList<MusicSequence>(basis);
		Iterator<MusicSequence> sIt = toPlay.iterator();
		  // remove unnecessary sequences
		while(sIt.hasNext())
			if(sIt.next().isEmpty())
				sIt.remove();
		
		// schedule the music to play via little threads that each do their part.
		long startTime = delayToStartMusic;
		int sequences = toPlay.size();
		final boolean[] done = new boolean[sequences];
		int longestIndex = isLongestOf(toPlay);
		schedule = new long[sequences][][];
		
		for(int i = 0; i < sequences; i++) {
			MusicSequence mseq = toPlay.get(i);
			final int seqNo = i;
			schedule[seqNo] = new long[mseq.size()][2];
			Thread t = new Thread(){
				public void run()
				{
					if(seqNo == longestIndex) 
						done[seqNo] = schedule(mseq, startTime, seqNo, schedule[seqNo], true);
					else
						done[seqNo] = schedule(mseq, startTime, seqNo, schedule[seqNo], false);
					if(checkDone(done)) 
						scheduleWait.release();
				}
			};
			t.start();
		}
		try{
			scheduleWait.acquire();
			doneWait.acquire();
			Thread.sleep(1000);
		} 
		catch(InterruptedException e) { }
		
		
	}
	
	/**
	 * Determine via exhaustive calculation which index of sequences is "longest", 
	 * or contains counted beats adding up to the longest duration.
	 * 
	 * Returns the index of the sequence with the longest duration. 
	 */
	public int isLongestOf(List<MusicSequence> sequences)
	{
		long finalTime = Long.MIN_VALUE;
		int longestSong = -1;
		for(int i = 0; i < sequences.size(); i++) {
			MusicSequence nextMs = sequences.get(i);
			if(nextMs.size()==0)
				continue;
			MusicalPair<TimerTask, TimerTask> last = nextMs.get(nextMs.size()-1);
			
			if(last.second.scheduledExecutionTime() > finalTime) {
				finalTime = last.second.scheduledExecutionTime();
				longestSong = i;
			}
		}
		return longestSong;
	}
	
	/**
	 * Return true if the process of scheduling for all music sequences in the basis is complete. 
	 * Return false if for one of the music sequences, the schedule method has not completed. 
	 */
	public boolean checkDone(boolean[] finishArray) {
		for(boolean done : finishArray) 
			if(!done)
				return false;
		return true;
	}
	/** 
	 * Attempt to create as many timer tasks as necessary to simulate the track of beats provided
	 * in music sequence "times", noting the intended start of the sequence as "startTime",
	 * the number of the sequences "timerNo", and the journal of all times logged by the 
	 * synchronizer "schedule". Each time an event is to be played, a sequence of execution times
	 * for start and stop is logged into the schedule for bookkeeping purposes and for re-use in future versions
	 * of this project. 
	 */
	public boolean schedule(MusicSequence times, long startTime, int timerNo, long[][] schedule, boolean isLongest) {
		if(times.isEmpty())
			throw new RuntimeException("No notes provided to scheduler.");
		Timer musicalDing = new Timer("MusicalObjectsSongRunner" + timerNo);
		long nowDelay = startTime;
		// let there be a slight delay between every note off signal, and note on signal.
		long slightDelay = (int)Math.ceil(MusicalObjects.durationOf(Count.TRIPLET_HTETH, Count.QTR, Count.QTR.timeUnits));
		
		MusicalPair<TimerTask, TimerTask> timeNow = times.get(0);
		musicalDing.schedule(timeNow.first, startTime);
		MusicalPair<TimerTask, TimerTask> next;
		long nextDuration = 0;
		for(int i = 1; i < times.size(); i++) {
			
			next = times.get(i);
			nextDuration = (long)Math.rint(MusicalObjects.durationOf(timeNow.beat, Count.QTR, timeNow.baseBeatDurationMs));
			// figure out where the next delay point is
			schedule[i-1][0] = nowDelay;
			nowDelay += nextDuration;
			schedule[i-1][1] = nowDelay;
			// schedule an event to end the last event, and the event to start the next event.
			musicalDing.schedule(timeNow.second, nowDelay-slightDelay);
			timeNow = next;
			musicalDing.schedule(timeNow.first, nowDelay);
			
			timeNow.setTime(nowDelay - (int)MusicalObjects.durationOf(timeNow.beat, Count.QTR, Count.QTR.timeUnits));
		}
		
		if(nextDuration > 0)
			musicalDing.schedule(timeNow.second, nowDelay, nextDuration);
		if(isLongest) {
			musicalDing.schedule(new TimerTask(){
				public void run() { 
					doneWait.release(9); 
					musicalDing.cancel();}}, 
			nowDelay + 1000);
		}
		else {
			musicalDing.schedule(new TimerTask(){
				public void run() {musicalDing.cancel();}}, 
			nowDelay + 1000);
		}
		return true;
	}
}
