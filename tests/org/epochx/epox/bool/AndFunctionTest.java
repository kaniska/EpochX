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
package org.epochx.epox.bool;

import org.epochx.epox.*;
import org.epochx.gp.representation.AbstractNodeTestCase;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 
 */
public class AndFunctionTest extends AbstractNodeTestCase {

	@Override
	public Node getNode() {
		return new AndFunction();
	}

	@Test
	public void testEvaluateTT() {
		final AndFunction node = (AndFunction) getNode();
		final Node[] children = new Node[]{new Literal(true),
				new Literal(true)};
		node.setChildren(children);
		final boolean result = node.evaluate();

		assertTrue("AND of true and true is not true", result);
	}

	@Test
	public void testEvaluateTF() {
		final AndFunction node = (AndFunction) getNode();
		final Node[] children = new Node[]{new Literal(true),
				new Literal(false)};
		node.setChildren(children);
		final boolean result = node.evaluate();

		assertFalse("AND of true and false is not false", result);
	}

	@Test
	public void testEvaluateFT() {
		final AndFunction node = (AndFunction) getNode();
		final Node[] children = new Node[]{new Literal(false),
				new Literal(true)};
		node.setChildren(children);
		final boolean result = node.evaluate();

		assertFalse("AND of false and true is not false", result);
	}

	@Test
	public void testEvaluateFF() {
		final AndFunction node = (AndFunction) getNode();
		final Node[] children = new Node[]{new Literal(false),
				new Literal(false)};
		node.setChildren(children);
		final boolean result = node.evaluate();

		assertFalse("AND of false and false is not false", result);
	}

}
