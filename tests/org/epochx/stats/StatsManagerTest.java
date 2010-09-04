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
package org.epochx.stats;

import junit.framework.TestCase;

import org.epochx.core.Model;
import org.epochx.gp.model.GPModelDummy;

/**
 * 
 */
public class StatsManagerTest extends TestCase {

	private StatsManager statsManager;
	private Model model;

	@Override
	protected void setUp() throws Exception {
		model = new GPModelDummy();
		statsManager = new StatsManager(model);
	}

	/**
	 * Test that if a run statistic is not found then null is returned.
	 */
	public void testRunStatNotFound() {
		final String field = "runtest";
		assertNull("null not returned for missing run stat",
				statsManager.getStat(field));
	}

	/**
	 * Tests that a run statistic that is added to a stats manager is
	 * remembered.
	 */
	public void testAddRunStat() {
		final String field = "runtest";
		final String value = "value";
		statsManager.addData(field, value);
		assertSame("stats manager not storing run stats", value,
				statsManager.getStat(field));
	}
}
