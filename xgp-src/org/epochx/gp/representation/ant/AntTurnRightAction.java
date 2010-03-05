/*  
 *  Copyright 2007-2010 Tom Castle & Lawrence Beadle
 *  Licensed under GNU General Public License
 * 
 *  This file is part of EpochX: genetic programming software for research
 *
 *  EpochX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  EpochX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with EpochX. If not, see <http://www.gnu.org/licenses/>.
 * 
 *  The latest version is available from: http:/www.epochx.org
 */
package org.epochx.gp.representation.ant;

import org.epochx.gp.representation.VoidNode;
import org.epochx.tools.ant.Ant;

/**
 * This class defines an action which when executed will trigger the ant 
 * to turn right from its current orientation in its ant landscape.
 */
public class AntTurnRightAction extends VoidNode {

	private Ant ant;
	
	/**
	 * Constructs an AntTurnRightAction, supplying an ant that the action can be 
	 * performed on.
	 * @param ant the Ant that will be turned right upon execution.
	 */
	public AntTurnRightAction(Ant ant) {
		super();
		
		this.ant = ant;
	}
	
	@Override
	public String getIdentifier() {
		return "TURN-RIGHT";
	}

	@Override
	public Void evaluate() {
		ant.turnRight();
		
		return null;
	}
}
