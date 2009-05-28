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
 * A <code>FunctionNode</code> which performs the common (base 10) logarithm.
 * @see LogFunction
 */
public class Log10Function extends FunctionNode<Double> {
	
	/**
	 * Construct a Log10Function with no children.
	 */
	public Log10Function() {
		this(null);
	}
	
	/**
	 * Construct a Log10Function with one child. When evaluated, the logarithm 
	 * of the evaluated child will be calculated.
	 * @param child The child of which the base 10 logarithm will be calculated.
	 */
	public Log10Function(Node<Double> child) {
		super(child);
	}

	/**
	 * Evaluating a <code>Log10Function</code> involves evaluating the child 
	 * then calculating it's base 10 logarithm.
	 */
	@Override
	public Double evaluate() {
		// TODO Could this bit not be done in superclass somehow, with a call to super.evaluate() required here?
		double c = ((Double) getChild(0).evaluate()).doubleValue();
		
		// TODO Need to check that this and others don't throw up any nasty divide by 0 like issues to protect against.
		return Math.log10(c);
	}
	
	/**
	 * Get the unique name that identifies this function.
	 * @return the unique name for the Log10Function which is LOG-10.
	 */
	@Override
	public String getFunctionName() {
		return "LOG-10";
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Could this instanceof business not be done higher up too? i.e. are they of the same type?
		return super.equals(obj) && (obj instanceof Log10Function);
	}
}