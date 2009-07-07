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
package com.epochx.func.dbl;

import com.epochx.core.representation.*;

/**
 * The CoefficientPowerFunction is equivalent to a <code>PowerFunction</code> 
 * combined with a <code>MultiplyFunction</code>. It allows a succinct way of 
 * representing a variable with an exponent and a coefficient.
 * 
 * An example:
 *    3x^2, which is equivalent to 3*(x^2)
 *    CVP 3 x 2, which is equivalent to MUL(POW x 2)
 */
public class CoefficientPowerFunction extends FunctionNode<Double> {

	/**
	 * Construct an CoefficientPowerFunction with no children.
	 */
	public CoefficientPowerFunction() {
		this(null, null, null);
	}
	
	/**
	 * Construct a CoefficientPowerFunction with three children. When evaluated, 
	 * all children will first be evaluated. Then the second child will be raised 
	 * to the power of the third child, and multiplied by the first.
	 * @param coefficient will be multiplied by the result of the term raised to 
	 * the exponent.
	 * @param term will be raised to the power of the exponent and multiplied by 
	 * the coefficient.
	 * @param exponent the power the term will be raised to.
	 */
	public CoefficientPowerFunction(Node<Double> coefficient, Node<Double> term, Node<Double> exponent) {
		super(coefficient, term, exponent);
	}

	/**
	 * Evaluating a <code>CoefficientPowerFunction</code> is performed by 
	 * evaluating the children and then calculating the result of the second 
	 * child raised to the power of the third, then multiplied by the first
	 * child.
	 */
	@Override
	public Double evaluate() {
		double c1 = ((Double) getChild(0).evaluate()).doubleValue();
		double c2 = ((Double) getChild(1).evaluate()).doubleValue();
		double c3 = ((Double) getChild(2).evaluate()).doubleValue();
		
		return c1 * (Math.pow(c2, c3));
	}
	
	/**
	 * Get the unique name that identifies this function.
	 * @return the unique name for the CoefficientPowerFunction which is CVP.
	 */
	@Override
	public String getFunctionName() {
		return "CVP";
	}
}
