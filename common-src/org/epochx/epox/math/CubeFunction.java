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
package org.epochx.epox.math;

import org.epochx.epox.Node;
import org.epochx.tools.util.*;

/**
 * A <code>FunctionNode</code> which performs the arithmetic function of cube,
 * that is - raising to the third power. It is equivalent to the
 * <code>PowerFunction</code> where the second child is the double literal 3.0.
 */
public class CubeFunction extends Node {

	/**
	 * Construct a CubeFunction with no children.
	 */
	public CubeFunction() {
		this(null);
	}

	/**
	 * Construct a CubeFunction with one child. When evaluated, the child will
	 * be evaluated with the result then raised to the power of 3.
	 * 
	 * @param child The child which will be cubed.
	 */
	public CubeFunction(final Node child) {
		super(child);
	}

	/**
	 * Evaluating a <code>CubeFunction</code> involves evaluating the child
	 * then raising the result to the power of 3.
	 */
	@Override
	public Object evaluate() {
		Object c = getChild(0).evaluate();
		
		double result = Math.pow(NumericUtils.asDouble(c), 3);

		if (c instanceof Long) {
			return (long) result;
		} else if (c instanceof Integer) {
			return (int) result;
		} else if (c instanceof Double) {
			return result;
		} else if (c instanceof Float) {
			return (float) result;
		} else {
			return null;
		}
	}

	/**
	 * Get the unique name that identifies this function.
	 * 
	 * @return the unique name for the CubeFunction which is CUBE.
	 */
	@Override
	public String getIdentifier() {
		return "CUBE";
	}
	
	@Override
	public Class<?> getReturnType(Class<?> ... inputTypes) {
		if (inputTypes.length == 1 && TypeUtils.isNumericType(inputTypes[0])) {
			return inputTypes[0];
		} else {
			return null;
		}
	}
}
