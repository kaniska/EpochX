/*  
 *  Copyright 2007-2008 Lawrence Beadle & Tom Castle
 *  Licensed under GNU General Public License
 * 
 *  This file is part of EpochX: genetic programming for research
 *
 *  EpochX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  EpochX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with EpochX.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.epochx.representation.ant;

import org.epochx.representation.*;

/**
 * A <code>FunctionNode</code> which provides the facility to sequence four 
 * instructions - which may be other functions or actions.
 */
public class Seq4Function extends VoidNode {
	
	/**
	 * Construct a Seq4Function with no children.
	 */
	public Seq4Function() {
		this(null, null, null, null);
	}
	
	/**
	 * Construct a Seq4Function with two children. When evaluated, the three 
	 * children will be evaluated in order. As such they will have been
	 * executed in sequence.
	 * @param child1 The first child node to be executed first in sequence.
	 * @param child2 The second child node to be executed second in sequence.
	 * @param child3 The third child node to be executed third in sequence.
	 */
	public Seq4Function(VoidNode child1, VoidNode child2, VoidNode child3, VoidNode child4) {
		super(child1, child2, child3, child4);
	}
	
	/**
	 * Evaluating a <code>Seq4Function</code> involves evaluating each of the
	 * children in sequence - the first child node, followed by the second 
	 * child node, followed by the third child node.
	 * 
	 * <p>Each of the children will thus have been evaluated (triggering 
	 * execution of actions at the <code>TerminalNodes</code>) and then this 
	 * method which must return an Action, returns Action.DO_NOTHING which any  
	 * functions higher up in the program tree will execute, but with no 
	 * effect.</p>
	 */
	@Override
	public Void evaluate() {
		getChild(0).evaluate();
		getChild(1).evaluate();
		getChild(2).evaluate();
		getChild(3).evaluate();
		
		return null;
	}
	
	/**
	 * Get the unique name that identifies this function.
	 * @return the unique name for the Seq4Function which is SEQ4.
	 */
	@Override
	public String getIdentifier() {
		return "SEQ4";
	}
}
