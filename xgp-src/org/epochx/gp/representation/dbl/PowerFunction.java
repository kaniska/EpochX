/*  
 *  Copyright 2007-2008 Lawrence Beadle & Tom Castle
 *  Licensed under GNU General Public License
 * 
 *  This file is part of EpochX: genetic programming for research
 *
 *  EpochX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  EpochX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with EpochX.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.epochx.gp.representation.dbl;

import org.epochx.gp.representation.DoubleNode;

/**
 * A <code>FunctionNode</code> which performs the mathematical operation of 
 * exponentiation.
 */
public class PowerFunction extends DoubleNode {

	/**
	 * Construct a PowerFunction with no children.
	 */
	public PowerFunction() {
		this(null, null);
	}
	
	/**
	 * Construct a PowerFunction with 2 children. When evaluated, the evaluation 
	 * of the first child is raised to the power of the evaluation of the second.
	 * @param base The first child node - the base.
	 * @param exponent The second child node - the exponent.
	 */
	public PowerFunction(DoubleNode base, DoubleNode exponent) {
		super(base, exponent);
	}

	/**
	 * Evaluating a <code>PowerFunction</code> involves raising the first child 
	 * to the power of the second, after both children are evaluated.
	 */
	@Override
	public Double evaluate() {
		double c1 = ((Double) getChild(0).evaluate()).doubleValue();
		double c2 = ((Double) getChild(1).evaluate()).doubleValue();
		
		return Math.pow(c1, c2);
	}
	
	/**
	 * Get the unique name that identifies this function.
	 * @return the unique name for the PowerFunction which is POW.
	 */
	@Override
	public String getIdentifier() {
		return "POW";
	}
}