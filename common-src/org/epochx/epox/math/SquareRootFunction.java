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
 * A <code>FunctionNode</code> which performs the mathematical function of
 * square
 * root.
 */
public class SquareRootFunction extends Node {

	/**
	 * Construct a SquareRootFunction with no children.
	 */
	public SquareRootFunction() {
		this(null);
	}

	/**
	 * Construct a SquareRootFunction with one child. When evaluated, the child
	 * will
	 * be first evaluated, with the result square-rooted.
	 * 
	 * @param child The child which cube root will be performed on.
	 */
	public SquareRootFunction(final Node child) {
		super(child);
	}

	/**
	 * Evaluating a <code>SquareRootFunction</code> involves evaluating the
	 * child
	 * first then performing square root on the result.
	 */
	@Override
	public Double evaluate() {
		Object c = getChild(0).evaluate();
		
		return Math.sqrt(NumericUtils.asDouble(c));
	}

	/**
	 * Get the unique name that identifies this function.
	 * 
	 * @return the unique name for the SquareRootFunction which is SQRT.
	 */
	@Override
	public String getIdentifier() {
		return "SQRT";
	}
	
	@Override
	public Class<?> getReturnType(Class<?> ... inputTypes) {
		if (inputTypes.length == 1 && TypeUtils.isNumericType(inputTypes[0])) {
			return Double.class;
		} else {
			return null;
		}
	}
}
