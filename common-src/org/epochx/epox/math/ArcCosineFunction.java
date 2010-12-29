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
 * A <code>FunctionNode</code> which performs the inverse trigonometric function
 * of
 * arccosine.
 */
public class ArcCosineFunction extends Node {

	/**
	 * Construct an ArcCosineFunction with no children.
	 */
	public ArcCosineFunction() {
		this(null);
	}

	/**
	 * Construct an ArcCosineFunction with one child. When evaluated, the child
	 * will be evaluated with arccosine performed on the result.
	 * 
	 * @param child The child which arccosine will be performed on.
	 */
	public ArcCosineFunction(final Node child) {
		super(child);
	}

	/**
	 * Evaluating an <code>ArcCosineFunction</code> involves calculating the
	 * arccosine of the child once it's been evaluated.
	 */
	@Override
	public Double evaluate() {
		Object c = getChild(0).evaluate();
		
		return Math.acos(NumericUtils.asDouble(c));
	}

	/**
	 * Get the unique name that identifies this function.
	 * 
	 * @return the unique name for the ArcCosineFunction which is ACOS.
	 */
	@Override
	public String getIdentifier() {
		return "ACOS";
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
