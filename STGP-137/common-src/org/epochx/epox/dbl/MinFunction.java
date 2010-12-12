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
 * The latest version is available from: http:/www.epochx.org
 */
package org.epochx.epox.dbl;

import org.epochx.epox.*;

/**
 * A <code>MinFunction</code> which performs the simple comparison function
 * of determining which of 2 numbers is smaller, as per the boolean less-than
 * function.
 */
public class MinFunction extends Node {

	/**
	 * Construct a MinFunction with no children.
	 */
	public MinFunction(int n) {
		this((Node) null);
		
		setChildren(new Node[n]);
	}

	/**
	 * Construct a MinFunction with two child. When evaluated, the children will
	 * both be evaluated with the smaller of the two returned as the result.
	 * 
	 * @param child1 The first child node for comparison.
	 * @param child2 The second child node for comparison.
	 */
	public MinFunction(final Node ... children) {
		super(children);
	}

	/**
	 * Evaluating a <code>MinFunction</code> involves evaluating the children
	 * then comparing and returning which ever is the smaller of the 2 evaluated
	 * children.
	 */
	@Override
	public Object evaluate() {
		int arity = getArity();
		Class<?> returnType = getReturnType();
		
		if (returnType == Double.class) {
			double min = Double.MAX_VALUE;
			for (int i=0; i<arity; i++) {
				double value = NodeUtils.asDouble(getChild(i).evaluate());
				min = Math.min(value, min);
			}
			return min;
		} else if (returnType == Integer.class) {
			int min = Integer.MAX_VALUE;
			for (int i=0; i<arity; i++) {
				int value = NodeUtils.asInteger(getChild(i).evaluate());
				min = Math.min(value, min);
			}
			return min;
		} else if (returnType == Long.class) {
			long min = Long.MAX_VALUE;
			for (int i=0; i<arity; i++) {
				long value = NodeUtils.asLong(getChild(i).evaluate());
				min = Math.min(value, min);
			}
			return min;
		} else {
			return null;
		}
	}

	/**
	 * Get the unique name that identifies this function.
	 * 
	 * @return the unique name for the MinFunction which is MIN.
	 */
	@Override
	public String getIdentifier() {
		return "MIN";
	}
	
	@Override
	public Class<?> getReturnType(Class<?> ... inputTypes) {
		return NodeUtils.getWidestNumericalClass(inputTypes);
	}
}
