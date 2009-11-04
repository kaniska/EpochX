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
 * A <code>FunctionNode</code> which performs the inverse trigonometric function of 
 * arctangent.
 */
public class ArcTangentFunction extends FunctionNode<Double> {

	/**
	 * Construct an ArcTangentFunction with no children.
	 */
	public ArcTangentFunction() {
		this(null);
	}
	
	/**
	 * Construct an ArcTangentFunction with one child. When evaluated, the child
	 * will be evaluated with arctangent performed on the result.
	 * @param child The child which arctangent will be performed on.
	 */
	public ArcTangentFunction(Node<Double> child) {
		super(child);
	}
	
	/**
	 * Evaluating an <code>ArcTangentFunction</code> involves calculating the 
	 * arctangent of the child once it's been evaluated.
	 */
	@Override
	public Double evaluate() {
		double c = ((Double) getChild(0).evaluate()).doubleValue();
		
		return Math.atan(c);
	}
	
	/**
	 * Get the unique name that identifies this function.
	 * @return the unique name for the ArcTangentFunction which is ATAN.
	 */
	@Override
	public String getFunctionName() {
		return "ATAN";
	}
}
