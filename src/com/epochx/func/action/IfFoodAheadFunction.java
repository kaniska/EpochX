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
package com.epochx.func.action;

import java.awt.*;

import com.epochx.action.*;
import com.epochx.ant.*;
import com.epochx.core.representation.*;

/**
 * 
 */
public class IfFoodAheadFunction extends FunctionNode<Action> {
	
	private Ant ant;
	private AntLandscape landscape;
	
	public IfFoodAheadFunction(Ant ant, AntLandscape landscape) {
		this(ant, landscape, null, null);
	}
	
	public IfFoodAheadFunction(Ant ant, AntLandscape landscape, Node<Action> child1, Node<Action> child2) {
		super(child1, child2);
		
		this.ant = ant;
		this.landscape = landscape;
	}
	
	@Override
	public Action evaluate() {

		// Find out the location we want to check.
		Point ahead = landscape.getNextLocation(ant.getLocation(), ant.getOrientation());
		
		if (landscape.isFoodLocation(ahead)) {
			((Action) getChild(0).evaluate()).execute();
		} else {
			((Action) getChild(1).evaluate()).execute();
		}
		
		return Action.DO_NOTHING;
	}
	
	@Override
	public String getFunctionName() {
		return "IF-FOOD-AHEAD";
	}

	@Override
	public boolean equals(Object obj) {
		//TODO This should probably check it has the same ant and landscape too.
		return super.equals(obj) && (obj instanceof IfFoodAheadFunction);
	}
}