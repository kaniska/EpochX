/*
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
 * The latest version is available from: http://www.epochx.org
 */
package org.epochx.epox.math;

import org.epochx.epox.Node;
import org.epochx.tools.util.*;

/**
 * A <code>FunctionNode</code> which performs the mathematical function of
 * factorial, which is normally expressed with an exclamation mark !
 * 
 * For example:
 * 5! = 5 x 4 x 3 x 2 x 1 = FACTORIAL 5
 * 
 */
public class FactorialFunction extends Node {

	/**
	 * Construct a FactorialFunction with no children.
	 */
	public FactorialFunction() {
		this(null);
	}

	/**
	 * Construct a FactorialFunction with one child. When evaluated, the child
	 * will
	 * be first evaluated, with the result undergoing the factorial function.
	 * 
	 * @param child The child which factorial will be performed on.
	 */
	public FactorialFunction(final Node child) {
		super(child);
	}

	/**
	 * Evaluating a <code>FactorialFunction</code> involves evaluating the child
	 * first then performing factorial on the result. Factorial requires a
	 * natural
	 * number, in order to achieve this the evaluated child may undergo
	 * rounding.
	 */
	@Override
	public Object evaluate() {
		Object c = getChild(0).evaluate();
		
		long cint = Math.abs(NumericUtils.asLong(c));
		
		long factorial = 1;
		for (long i = 1; i <= cint; i++) {
			factorial = factorial * i;
		}
		
		if (c instanceof Long) {
			return factorial;
		} else {
			return (int) factorial;
		}
	}

	/**
	 * Get the unique name that identifies this function.
	 * 
	 * @return the unique name for the FactorialFunction which is FACTORIAL.
	 */
	@Override
	public String getIdentifier() {
		return "FACTORIAL";
	}
	
	@Override
	public Class<?> getReturnType(Class<?> ... inputTypes) {
		if (inputTypes.length == 1 && TypeUtils.isIntegerType(inputTypes[0])) {
			return TypeUtils.getNumericType(inputTypes[0]);
		}
		
		return null;
	}
}
