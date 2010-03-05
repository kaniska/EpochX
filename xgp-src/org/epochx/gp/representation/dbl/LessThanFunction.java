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
 *//* 
 * Copyright 2007-2010 Tom Castle & Lawrence Beadle
 * Licensed under GNU General Public License
 * 
 * This file is part of EpochX: genetic programming software for research
 * 
 * EpochX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * EpochX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with EpochX. If not, see <http://www.gnu.org/licenses/>.
 * 
 * The latest version is available from: http:/www.epochx.org
 */
package org.epochx.gp.representation.dbl;

import org.epochx.gp.representation.DoubleNode;

/**
 * The less than function is a numerically valued logical function. It 
 * performs the standard logic function "Less Than", where true or false
 * is return depending on if the first argument is smaller than the second 
 * argument or not, respectively. However, to satisfy the closure principle 
 * this implementation returns +1 for true or -1 for false.
 */
public class LessThanFunction extends DoubleNode {

	/**
	 * Construct a LessThanFunction with no children.
	 */
	public LessThanFunction() {
		this(null, null);
	}
	
	/**
	 * Construct a LessThanFunction with two children. When evaluated, both 
	 * children will be evaluated. Then if the numerical result of evaluating 
	 * the first child is smaller than the result of the second, then the 
	 * function will return +1.0 else, -1.0 will be the result.
	 * @param child1 The first child which is being tested if it is less than
	 * the second child.
	 * @param child2 The second child which the first child is being tested 
	 * against.
	 */
	public LessThanFunction(DoubleNode child1, DoubleNode child2) {
		super(child1, child2);
	}

	/**
	 * Evaluating a <code>LessThanFunction</code> involves evaluating both 
	 * children first then performing the logical less than operation. If 
	 * the first child is less in value than the second child then this 
	 * method will return +1.0 else it will return -1.0. 
	 */
	@Override
	public Double evaluate() {
		double value1 = (Double) getChild(0).evaluate();
		double value2 = (Double) getChild(1).evaluate();

		return (value1 > value2) ? +1.0 : -1.0;
	}
	
	/**
	 * Get the unique name that identifies this function.
	 * @return the unique name for the LessThanFunction which is LT.
	 */
	@Override
	public String getIdentifier() {
		return "LT";
	}

}
