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
package com.epochx.func.bool;

import com.epochx.core.representation.*;

/**
 * 
 */
public class NandFunction extends FunctionNode<Boolean> {

	public NandFunction() {
		this(null, null);
	}
	
	public NandFunction(Node<Boolean> child1, Node<Boolean> child2) {
		super(child1, child2);
	}
	
	@Override
	public Boolean evaluate() {
		boolean c1 = ((Boolean) getChild(0).evaluate()).booleanValue();
		boolean c2 = ((Boolean) getChild(1).evaluate()).booleanValue();
		
		return !(c1 && c2);
	}
	
	@Override
	public String getFunctionName() {
		return "NAND";
	}
	
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) && (obj instanceof NandFunction);
	}
	
}