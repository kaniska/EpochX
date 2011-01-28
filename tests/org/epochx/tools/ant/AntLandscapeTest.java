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
package org.epochx.tools.ant;

import static org.junit.Assert.*;

import java.awt.*;

import org.junit.*;

/**
 * 
 */
public class AntLandscapeTest {

	private AntLandscape landscape;

	@Before
	public void setUp() {
		landscape = new AntLandscape(new Dimension(32, 32));
	}

	/**
	 * Test the isValidLocation method.
	 */
	@Test
	public void testIsValidLocation() {
		assertFalse("location at excessive x point not invalid", landscape.isValidLocation(new Point(
				landscape.getWidth(), landscape.getHeight() - 1)));
		assertFalse("location at excessive y point not invalid", landscape.isValidLocation(new Point(
				landscape.getWidth() - 1, landscape.getHeight())));
		assertFalse("location at negative x point not invalid", landscape.isValidLocation(new Point(-1, 0)));
		assertFalse("location at negative y point not invalid", landscape.isValidLocation(new Point(0, -1)));

		assertTrue("location at 0,0 point not valid", landscape.isValidLocation(new Point(0, 0)));
		assertTrue("location at valid point not indicated valid", landscape.isValidLocation(new Point(
				landscape.getWidth() - 1, landscape.getHeight() - 1)));
	}

	/**
	 * Test the getNextLocation method.
	 */
	@Test
	public void testGetNextLocation() {
		Point location = new Point(0, 0);
		Orientation orientation = Orientation.EAST;
		assertEquals("next location from (0,0):EAST is not 1,0.", new Point(1, 0), landscape.getNextLocation(location, orientation));

		location = new Point(0, 0);
		orientation = Orientation.SOUTH;
		assertEquals("next location from (0,0):SOUTH is not 0,1.", new Point(0, 1), landscape.getNextLocation(location, orientation));

		location = new Point(0, 0);
		orientation = Orientation.WEST;
		assertEquals("next location from (0,0):WEST is not 31,0.", new Point(31, 0), landscape.getNextLocation(location, orientation));

		location = new Point(0, 0);
		orientation = Orientation.NORTH;
		assertEquals("next location from (0,0):NORTH is not 0,31.", new Point(0, 31), landscape.getNextLocation(location, orientation));
	}

}
