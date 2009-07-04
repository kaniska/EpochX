/*  
 *  Copyright 2007-2008 Lawrence Beadle & Tom Castle
 *  Licensed under GNU General Public License
 * 
 *  This file is part of Epoch X - (The Genetic Programming Analysis Software)
 *
 *  Epoch X is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Epoch X is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Epoch X.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.epochx.action;

import com.epochx.ant.*;

/**
 * This class defines an action which when executed will trigger the ant 
 * to turn left from its current orientation in its ant landscape.
 */
public class AntTurnLeftAction extends AntAction {

	/**
	 * Constructs an AntTurnLeftAction, supplying an ant that the action can be 
	 * performed on.
	 * @param ant the Ant that will be turned left upon execution.
	 */
	public AntTurnLeftAction(Ant ant) {
		super(ant);
	}

	/**
	 * Execute this action, which will trigger the ant to turn left from its 
	 * current orientation in its ant landscape.
	 */
	@Override
	public void execute() {
		getAnt().turnLeft();
	}
	
	/**
	 * String representation of this action which identifies the action type.
	 * @return string representation of this action which identifies the action 
	 * type.
	 */
	@Override
	public String toString() {
		return "TURN-LEFT";
	}
}