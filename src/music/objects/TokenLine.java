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
import java.util.List;
import music.objects.Token.RepeatToken;

public class TokenLine extends ArrayList<Token>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TokenLine()
	{
		super();
	}
	
	public TokenLine(List<Token> input)
	{
		super(input);
	}
	
	
	public void append(List<Token> moreTokens)
	{
		addAll(moreTokens);
	}
	
	public TokenLine copy() { 
		TokenLine newTokens = new TokenLine(); 
		for(Token t : this)  
			newTokens.add(t);
		
		return newTokens;
	}
	public String toString()
	{
		if(this.isEmpty())
			return "> <";
		String toReturn = "< ";
		int max = size();
		int count = 0;
		for(Token t : this) {
			if(t instanceof RepeatToken) 
				toReturn += t + " ";
			else 
				toReturn += t.tokenLiteral;
			count++;
			toReturn += (count == max) ? "" : " ";
		}
		
		toReturn += " >";
		return toReturn;
	}
}