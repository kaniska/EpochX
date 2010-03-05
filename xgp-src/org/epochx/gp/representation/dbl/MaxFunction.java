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
package org.epochx.gp.representation.dbl;

import org.epochx.gp.representation.DoubleNode;

/**
 * A <code>FunctionNode</code> which performs the simple comparison function 
 * of determining which of 2 numbers is larger, as per the boolean greater-than
 * function.
 */
public class MaxFunction extends DoubleNode {
	
	/**
	 * Construct a MaxFunction with no children.
	 */
	public MaxFunction() {
		this(null, null);
	}
	
	/**
	 * Construct a MaxFunction with two child. When evaluated, the children will 
	 * both be evaluated with the larger of the two returned as the result.
	 * @param child1 The first child node for comparison.
	 * @param child2 The second child node for comparison.
	 */
	public MaxFunction(DoubleNode child1, DoubleNode child2) {
		super(child1, child2);
	}

	/**
	 * Evaluating a <code>MaxFunction</code> involves evaluating the children  
	 * then comparing and returning which ever is the larger of the 2 evaluated 
	 * children.
	 */
	@Override
	public Double evaluate() {
		double c1 = ((Double) getChild(0).evaluate()).doubleValue();
		double c2 = ((Double) getChild(1).evaluate()).doubleValue();
		
		if (c1 >= c2) {
			return c1;
		} else {
			return c2;
		}
	}
	
	/**
	 * Get the unique name that identifies this function.
	 * @return the unique name for the MaxFunction which is MAX.
	 */
	@Override
	public String getIdentifier() {
		return "MAX";
	}
}
