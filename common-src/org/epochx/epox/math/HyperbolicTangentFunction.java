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
 * A <code>FunctionNode</code> which performs the hyperbolic trigonometric
 * function of hyperbolic tangent.
 */
public class HyperbolicTangentFunction extends Node {

	/**
	 * Construct a HyperbolicTangentFunction with no children.
	 */
	public HyperbolicTangentFunction() {
		this(null);
	}

	/**
	 * Construct a HyperbolicTangentFunction with one child. When evaluated, the
	 * child will first be evaluated with hyperbolic tangent performed on the
	 * result.
	 * 
	 * @param child The child which hyperbolic tangent will be performed on.
	 */
	public HyperbolicTangentFunction(final Node child) {
		super(child);
	}

	/**
	 * Evaluating an <code>HyperbolicTangentFunction</code> involves calculating
	 * the
	 * hyperbolic tangent of the child once it has also been evaluated.
	 */
	@Override
	public Double evaluate() {
		Object c = getChild(0).evaluate();

		return Math.tanh(NumericUtils.asDouble(c));
	}

	/**
	 * Get the unique name that identifies this function.
	 * 
	 * @return the unique name for the HyperbolicTangentFunction which is TANH.
	 */
	@Override
	public String getIdentifier() {
		return "TANH";
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
