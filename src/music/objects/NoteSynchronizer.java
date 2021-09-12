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
	//TODO: Add Java 8 JRE to workspace. 
	private ArrayList<MusicSequence> basis;
	public static Count defaultCount = Count.QTR;
	
	public static final int defaultVolume = 100;
	public static final int delayToStartMusic = 3000;
	public static final int defaultMeterDuration = 600;
	public static final int defaultBeatsPerMeasure = 4;
	public static final String defaultInstrumentPatch = "piano";
	public static final Pitch defaultKey = Pitch.C4;
	public static final Instrument defaultInstrument = new Instrument("piano");
	
	public static final Meter defaultMeter = new Meter(defaultBeatsPerMeasure, defaultCount);
	static {
		defaultInstrument.volume = defaultVolume;
		defaultInstrument.patch = defaultInstrumentPatch;
	}
	public final Semaphore scheduleWait, doneWait;
	public long[][][] schedule;
	
	public NoteSynchronizer()
	{
		scheduleWait = new Semaphore(0, true);
		doneWait = new Semaphore(0, true);
		basis = new ArrayList<>();
	}
	
	public NoteSynchronizer(Count getsTheBeat) { this(); }
	
	
	public void addMusicSequence(MusicSequence mseq) { basis.add(mseq); }
	public void startMusic()
	{
		// create a copy of the basis containing useful sequences to play.
		ArrayList<MusicSequence> toPlay = new ArrayList<MusicSequence>(basis);
		Iterator<MusicSequence> sIt = toPlay.iterator();
		  // remove unnecessary sequences
		while(sIt.hasNext())
			if(sIt.next().isEmpty())
				sIt.remove();
		
//		Calendar nowCal = Calendar.getInstance();
//		long now = nowCal.getTimeInMillis();
		
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
	public boolean checkDone(boolean[] finishArray)
	{
		for(boolean done : finishArray) 
			if(!done)
				return false;
		return true;
	}
	public boolean schedule(MusicSequence times, long startTime, int timerNo, long[][] schedule, boolean isLongest)
	{
		if(times.isEmpty())
			throw new RuntimeException("No notes provided to scheduler.");
		Timer musicalDing = new Timer("MusicalObjectsSongRunner" + timerNo);
		long nowDelay = startTime;
		// let there be a slight delay between every note off signal, and note on signal.
		long slightDelay = (int)Math.ceil(MusicalObjects.durationOf(Count.TRIPLET_HTETH, Count.QTR, times.beatTimeMs));
		
		MusicalPair<TimerTask, TimerTask> timeNow = times.get(0);
		musicalDing.schedule(timeNow.first, startTime);
		MusicalPair<TimerTask, TimerTask> next;
		long nextDuration = 0;
		for(int i = 1; i < times.size(); i++) {
			
			next = times.get(i);
//			long slightDelay = (int)Math.ceil(MusicalObjects.durationOf(Count.TRIPLET_HTETH, Count.QTR, next.beat.count.timeUnits));
//			nextDuration = (long)Math.rint(MusicalObjects.durationOf(timeNow.beat, Count.QTR, next.baseBeatDurationMs));
			nextDuration = (long)Math.rint(MusicalObjects.durationOf(timeNow.beat, Count.QTR, timeNow.baseBeatDurationMs));
			// figure out where the next delay point is
			schedule[i-1][0] = nowDelay;
			nowDelay += nextDuration;
			schedule[i-1][1] = nowDelay;
			// schedule an event to end the last event, and the event to start the next event.
			musicalDing.schedule(timeNow.second, nowDelay-slightDelay);
			timeNow = next;
			musicalDing.schedule(timeNow.first, nowDelay);
			
//			timeNow.time = nowDelay - (int)MusicalObjects.durationOf(timeNow.beat, times.getsTheBeat, times.beatTimeMs);
			timeNow.setTime(nowDelay - (int)MusicalObjects.durationOf(timeNow.beat, Count.QTR, times.beatTimeMs));
//			timeNow.setTime(nowDelay - (int)MusicalObjects.durationOf(timeNow.beat, Count.QTR, timeNow.baseBeatDurationMs));
		}
		
		if(nextDuration > 0)
			musicalDing.schedule(timeNow.second, nowDelay, nextDuration);
		if(isLongest) {
			musicalDing.schedule(new TimerTask(){
				public void run() { 
					doneWait.release(9); 
//					doneWait.release(); 
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
