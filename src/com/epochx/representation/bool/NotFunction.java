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
package com.epochx.representation.bool;

import com.epochx.representation.*;

/**
 * A <code>FunctionNode</code> which performs logical negation.
 */
public class NotFunction extends FunctionNode<Boolean> {
	
	/**
	 * Construct a NotFunction with no children.
	 */
	public NotFunction() {
		this(null);
	}
	
	/**
	 * Construct a NotFunction with one children. When evaluated, if the child 
	 * evaluates to true, the result will be false. If false, the result will be
	 * true.
	 * @param child The child node which will be evaluated and negated.
	 */
	public NotFunction(Node<Boolean> child) {
		super(child);
	}
	
	/**
	 * Evaluating a <code>NotFunction</code> involves evaluating the child, then 
	 * negating the result. If the child evaluates to true, the result will be false, 
	 * otherwise it will be true.
	 */
	@Override
	public Boolean evaluate() {
		return !((Boolean) getChild(0).evaluate()).booleanValue();
	}
	
	/**
	 * Get the unique name that identifies this function.
	 * @return the unique name for the NotFunction which is NOT.
	 */
	@Override
	public String getFunctionName() {
		return "NOT";
	}
}
