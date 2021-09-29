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

import music.objects.MusicalObjects.Beat;

/**
 * Source code for the musical pair class.<br>
 * The musical pair is used by the encoder to together control the starting and stopping 
 * of a note being played. The constructor requires two objects, one to represent what happens
 * at the start of executing some musical action, and a second object for after a duration
 * of time after the start of the action. 
 * 
 * Currently these objects are only instantiated as two events that run on a Timer object.
 * The reasoning behind why we don't just use this class to instantiate TimerTask objects 
 * less indirectly ... has been lost to time. 
 */
public class MusicalPair<F, S> {
	/** The first element in this musical pair, to be acted upon first */
	public final F first;
	/** The second element in this musical pair, to be acted upon second */
	public final S second;
	
	/** The musical beat associated with this musical pair object.<br>
	  * This is useful in case additional information about the beat backing the pair is needed*/
	public Beat beat;
	
	/** The calculated duration of the execution of this encoded musical pair object.*/
	public long baseBeatDurationMs;
	
	/** The calculated time after start of song when this musical pair's actions were last executed 
	 *  Currently this variable is not read in this project. 
	 *  It set by the NoteSynchronizer class in this project.*/
	@SuppressWarnings("unused")
	private long time; 
	/** Constructor for the MusicalPair object. 
	 *  @param firstType - the object representing the first action to be used to play sound
	 *  @param secondType - the object representing the second action to be used to play sound
	 *  @param beat - the beat to reference for more information about this action
	 *  @param baseBeatDurationMs - the amount of time in milliseconds to execute this action
	 * */
	public MusicalPair(F firstType, S secondType, Beat beat, long baseBeatDurationMs)
	{
		this.first = firstType;
		this.second = secondType;
		this.beat = beat;
		this.baseBeatDurationMs = baseBeatDurationMs;
	}
	
	/** Sets the time after start of song when this musical pair's actions were last executed */
	public void setTime(long newTime) {  this.time = newTime; }
}
