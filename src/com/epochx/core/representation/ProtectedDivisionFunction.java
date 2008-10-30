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
package com.epochx.core.representation;

public class ProtectedDivisionFunction extends FunctionNode<Double> {
	
	public ProtectedDivisionFunction(Node<Double> child1, Node<Double> child2) {
		super(child1, child2);
	}

	@Override
	public Double evaluate() {
		double c1 = getChild(0).evaluate().doubleValue();
		double c2 = getChild(1).evaluate().doubleValue();
		
		if(c2==0) {
			return 0d;
		} else {
			return c1 / c2;
		}
	}

}