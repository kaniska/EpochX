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
 * A <code>FunctionNode</code> which performs the natural (base e) logarithm.
 * @see Log10Function
 */
public class LogFunction extends FunctionNode<Double> {
	
	/**
	 * Construct a LogFunction with no children.
	 */
	public LogFunction() {
		this(null);
	}
	
	/**
	 * Construct a LogFunction with one child. When evaluated, the logarithm 
	 * of the evaluated child will be calculated.
	 * @param child The child of which the base e logarithm will be calculated.
	 */
	public LogFunction(Node<Double> child) {
		super(child);
	}

	/**
	 * Evaluating a <code>LogFunction</code> involves evaluating the child 
	 * then calculating it's base e logarithm.
	 */
	@Override
	public Double evaluate() {
		double c = ((Double) getChild(0).evaluate()).doubleValue();
		
		return Math.log(c);
	}
	
	/**
	 * Get the unique name that identifies this function.
	 * @return the unique name for the LogFunction which is LN.
	 */
	@Override
	public String getFunctionName() {
		return "LN";
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) && (obj instanceof LogFunction);
	}
}