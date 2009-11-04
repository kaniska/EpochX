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
package org.epochx.representation.dbl;

import org.epochx.representation.*;

/**
 * A <code>FunctionNode</code> which performs the mathematical sign function  
 * that extracts the sign of a number.
 */
public class SignumFunction extends FunctionNode<Double> {

	/**
	 * Construct a SignumFunction with no children.
	 */
	public SignumFunction() {
		this(null);
	}
	
	/**
	 * Construct a SignumFunction with one child. When evaluated, the child
	 * will be evaluated with signum performed on the result.
	 * @param child The child which signum will be performed on.
	 */
	public SignumFunction(Node<Double> child) {
		super(child);
	}

	/**
	 * Evaluating a <code>SignumFunction</code> involves evaluating the child  
	 * then the result will be zero if it resolves to zero, 1.0 if greater than
	 * zero and -1.0 if less than zero.
	 */
	@Override
	public Double evaluate() {
		double c = ((Double) getChild(0).evaluate()).doubleValue();
		
		return Math.signum(c);
	}
	
	/**
	 * Get the unique name that identifies this function.
	 * @return the unique name for the SignumFunction which is SGN.
	 */
	@Override
	public String getFunctionName() {
		return "SGN";
	}
}
