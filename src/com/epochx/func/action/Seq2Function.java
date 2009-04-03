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
package com.epochx.func.action;

import com.epochx.action.*;
import com.epochx.core.representation.*;

/**
 * 
 */
public class Seq2Function extends FunctionNode<Action> {

	public Seq2Function() {
		this(null, null);
	}
	
	public Seq2Function(Node<Action> action1, Node<Action> action2) {
		super(action1, action2);
	}
	
	@Override
	public Action evaluate() {
		((Action) getChild(0).evaluate()).execute();
		((Action) getChild(1).evaluate()).execute();
		
		return Action.DO_NOTHING;
	}
	
	@Override
	public String getFunctionName() {
		return "SEQ2";
	}
	
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) && (obj instanceof Seq2Function);
	}
}