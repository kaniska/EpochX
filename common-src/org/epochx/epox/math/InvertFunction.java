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
 * A <code>FunctionNode</code> which performs the multiplicative inverse (or
 * reciprocal), that is the inverse of x is 1/x.
 */
public class InvertFunction extends Node {

	/**
	 * Construct an InvertFunction with no children.
	 */
	public InvertFunction() {
		this(null);
	}

	/**
	 * Construct an InvertFunction with one child. When evaluated, the child
	 * will be evaluated before the inversion operation is performed.
	 * 
	 * @param child The child which the reciprocal will be found for.
	 */
	public InvertFunction(final Node child) {
		super(child);
	}

	/**
	 * Evaluating an <code>InvertFunction</code> involves calculating the
	 * result of 1 divided by the result of evaluating the child. The
	 * exception to this is where the child evaluates to 0.0. In this case
	 * there is no finite reciprocal and the result will be 1.0.
	 */
	@Override
	public Double evaluate() {
		double c = NumericUtils.asDouble(getChild(0).evaluate());
		
		if (c == 0) {
			return 1.0;
		} else {
			return 1 / c;
		}
	}

	/**
	 * Get the unique name that identifies this function.
	 * 
	 * @return the unique name for the InvertFunction which is INV.
	 */
	@Override
	public String getIdentifier() {
		return "INV";
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
