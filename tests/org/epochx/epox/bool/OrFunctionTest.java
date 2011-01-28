/*
 * Copyright 2007-2011 Tom Castle & Lawrence Beadle
 * Licensed under GNU Lesser General Public License
 * 
 * This file is part of EpochX: genetic programming software for research
 * 
 * EpochX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * EpochX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with EpochX. If not, see <http://www.gnu.org/licenses/>.
 * 
 * The latest version is available from: http://www.epochx.org
 */
package org.epochx.epox.bool;

import static org.junit.Assert.*;

import org.epochx.epox.*;
import org.epochx.tools.eval.*;
import org.junit.Test;

/**
 * Unit tests for {@link org.epochx.epox.bool.OrFunction}
 */
public class OrFunctionTest extends NodeTestCase {

	private OrFunction node;
	private MockNode child1;
	private MockNode child2;
	
	/**
	 * Part of test fixture for superclass.
	 */
	@Override
	public Node getNode() {
		return new OrFunction();
	}
	
	/**
	 * Sets up the test environment.
	 */
	@Override
	public void setUp() {
		child1 = new MockNode();
		child2 = new MockNode();
		child1.setGetIdentifier("child1");
		child2.setGetIdentifier("child2");
		
		node = new OrFunction(child1, child2);

		super.setUp();
	}
	
	/**
	 * Tests that {@link org.epochx.epox.bool.OrFunction#evaluate()} correctly
	 * evaluates inputs.
	 */
	@Test
	public void testEvaluate() {
		child1.setEvaluate(Boolean.TRUE);
		child2.setEvaluate(Boolean.TRUE);
		assertTrue("OR of true and true should be true", node.evaluate());
		
		child1.setEvaluate(Boolean.TRUE);
		child2.setEvaluate(Boolean.FALSE);
		assertTrue("OR of true and false should be true", node.evaluate());
		
		child1.setEvaluate(Boolean.FALSE);
		child2.setEvaluate(Boolean.TRUE);
		assertTrue("OR of false and true should be true", node.evaluate());
		
		child1.setEvaluate(Boolean.FALSE);
		child2.setEvaluate(Boolean.FALSE);
		assertFalse("OR of false and false should be false", node.evaluate());
	}
	
	/**
	 * Tests that {@link org.epochx.epox.bool.OrFunction#getReturnType(Class...)}
	 * returns a Boolean data-type for two Boolean inputs, and <code>null</code>
	 * for non-Boolean or incorrect number.
	 */
	@Test
	public void testGetReturnTypeNor() {
		Class<?> returnType = node.getReturnType(Boolean.class, Boolean.class);
		assertEquals("type should be boolean for 2 boolean inputs", Boolean.class, returnType);
		
		returnType = node.getReturnType(Integer.class, Boolean.class);
		assertNull("non-boolean inputs should be invalid", returnType);
		
		returnType = node.getReturnType(Boolean.class, Boolean.class, Boolean.class);
		assertNull("too many boolean inputs should be invalid", returnType);
	}
	
	/**
	 * Tests that this function can be parsed by the EpoxParser.
	 */
	@Test
	public void testEpoxParser() {
		EpoxParser parser = new EpoxParser();
		
		try {
			parser.declareVariable(new Variable("X", Boolean.class));
			Node n = parser.parse("OR(X, X)");
			assertSame("Parsing did not return an instance of the correct node", OrFunction.class, n.getClass());
		} catch (MalformedProgramException e) {
			fail("Malformed program exception thrown when parsing");
		}
	}
}
