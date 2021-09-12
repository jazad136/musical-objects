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

public class MusicalPair<F, S> 
{
	public final F first;
	public final S second;
	public Beat beat;
	public long time;
	public long baseBeatDurationMs;
	public MusicalPair(F firstType, S secondType, Beat beat, long baseBeatDurationMs)
	{
		this.first = firstType;
		this.second = secondType;
		this.beat = beat;
		this.baseBeatDurationMs = baseBeatDurationMs;
	}
	public void setTime(long time) { this.time = time; }
}
