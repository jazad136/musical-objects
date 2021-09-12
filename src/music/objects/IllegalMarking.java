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
 *   along with Musical Objects. If not, see <https://www.gnu.org/licenses/>. A copy
 *   of this license can be found at the top directory or "root-level" directory 
 *   of this project where downloaded from GitHub. 
 */
package music.objects;



public class IllegalMarking extends IllegalArgumentException
{
	
	public enum Reason {NOTE, PITCH, COUNT, INTERVAL, METER_BEAT, DURATION, INSTRUMENT, SOUND, VOLUME_SETTING, MEASURE, SCALE, UNDEFINED};
	private Token targetToken;
	private final Reason reason;
	private boolean hasTargetToken;
	private String shortMessage;
	
	public IllegalMarking(Token token, Reason reason)
	{
		if(token != null) {
			hasTargetToken = true;
			targetToken = token;
		}
		this.reason = reason;
		shortMessage = "";
	}
	
	public IllegalMarking(Reason reason)
	{
		this.reason = reason;
		shortMessage = "";
		
	}
	
	public IllegalMarking(String shortMessage)
	{
		this.shortMessage = shortMessage;
		reason = Reason.UNDEFINED;
	}
	
	public String getMessage()
	{
		String message; 
		if(!shortMessage.isEmpty()) 
			message = shortMessage;
		else if(hasTargetToken) {
			String line = "" + targetToken.line;
			String pos = "" + targetToken.character;
			message = "\n\nAt line " + line + ", character " + pos + ",\n";
			if(reason == Reason.NOTE) 
				message += targetToken.tokenLiteral + " was found, but " 
						+ targetToken.tokenLiteral + " is not a valid note.";
			else if(reason == Reason.MEASURE) {
				message += targetToken.tokenLiteral + " was found, but "
						+ targetToken.tokenLiteral + " contains invalid content after the colon"
								+ "that cannot be parsed as an integer.";
			}
			else if(reason == Reason.SCALE) {
				message += targetToken.tokenLiteral + " was found, but " 
						+ targetToken.tokenLiteral + " is not a valid scale/key marking.";
			}
			else {
				message += "A sequence " + targetToken.tokenLiteral 
						+ " was found that did not use a valid "; 
				if(reason == Reason.COUNT)
					message += "count";
				else if(reason == Reason.PITCH) 
					message += "pitch";
				else if(reason == Reason.INSTRUMENT)
					message += "instrument name";
				else if(reason == Reason.SOUND)
					message += "sound type";
				else if(reason == Reason.VOLUME_SETTING)
					message += "volume setting or instrument number";
				
				else
					message += "meter beat-per-measure number";
			}
		}
		else
			message = "An illegal marking was found. Reason: " + reason;
		return message;
	}
}