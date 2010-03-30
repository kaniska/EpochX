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
package org.epochx.core;

import java.util.*;

import junit.framework.TestCase;

import org.epochx.gp.model.*;
import org.epochx.gp.representation.*;
import org.epochx.life.CrossoverListener;
import org.epochx.representation.CandidateProgram;

/**
 * 
 */
public class CrossoverManagerTest extends TestCase {

	private GPModel model;
	private CrossoverManager crossoverManager;
	
	private int count;
	
	@Override
	protected void setUp() throws Exception {
		model = new GPModelDummy();
		crossoverManager = new CrossoverManager(model);
	}
	
	/**
	 * Tests that an exception is thrown if the crossover is null but crossover
	 * probability is not null.
	 */
	public void testCrossoverNotSet() {
		model.setCrossover(null);
		model.setCrossoverProbability(0.1);
		
		try {
			crossoverManager.crossover();
			fail("illegal state exception not thrown for a model with crossover enabled but null operator");
		} catch(IllegalStateException e) {}
	}
	
	/**
	 * Tests that an exception is thrown if the program selector is null when
	 * attempting crossover.
	 */
	public void testProgramSelectorNotSet() {
		// Create a model with a null program selector.
		model.setProgramSelector(null);
		crossoverManager = new CrossoverManager(model);
		try {
			crossoverManager.crossover();
			fail("illegal state exception not thrown for a model with null a program selector");
		} catch(IllegalStateException e) {}
	}
	
	/**
	 * Tests that the crossover events are all triggered and in the correct 
	 * order.
	 */
	public void testCrossoverEventsOrder() {
		// We add the chars '1', '2', '3' to builder to check order of calls.
		final StringBuilder verify = new StringBuilder();
		
		List<CandidateProgram> pop = new ArrayList<CandidateProgram>();
		pop.add(new GPCandidateProgram(new BooleanLiteral(false), (GPModel) model));
		pop.add(new GPCandidateProgram(new BooleanLiteral(false), (GPModel) model));
		model.getProgramSelector().setSelectionPool(pop);
		
		// Listen for the crossver.
		model.getLifeCycleManager().addCrossoverListener(new CrossoverListener() {
			@Override
			public void onCrossoverStart() {
				verify.append('1');
			}
			@Override
			public CandidateProgram[] onCrossover(CandidateProgram[] parents, CandidateProgram[] children) {
				verify.append('2');
				return children;
			}
			@Override
			public void onCrossoverEnd() {
				verify.append('3');
			}
		});
		model.getLifeCycleManager().fireConfigureEvent();
		crossoverManager.crossover();
		
		assertEquals("crossover events were not called in the correct order", "123", verify.toString());
	}
	
	/**
	 * Tests that returning null to the crossover event will revert the crossover.
	 */
	public void testGenerationEventRevert() {
		// We add the chars '1', '2', '3' to builder to check order of calls.
		final StringBuilder verify = new StringBuilder();
		
		List<CandidateProgram> pop = new ArrayList<CandidateProgram>();
		pop.add(new GPCandidateProgram(new BooleanLiteral(false), (GPModel) model));
		pop.add(new GPCandidateProgram(new BooleanLiteral(false), (GPModel) model));
		model.getProgramSelector().setSelectionPool(pop);
		
		count = 0;
		
		// Listen for the generation.
		model.getLifeCycleManager().addCrossoverListener(new CrossoverListener() {
			@Override
			public void onCrossoverStart() {
				verify.append('1');
			}
			@Override
			public CandidateProgram[] onCrossover(CandidateProgram[] parents, CandidateProgram[] children) {
				verify.append('2');
				// Revert 3 times before confirming.
				if (count == 3) {
					return children;
				} else {
					count++;
				}
				return null;
			}
			@Override
			public void onCrossoverEnd() {
				verify.append('3');
			}
		});
		
		model.getLifeCycleManager().fireConfigureEvent();
		crossoverManager.crossover();
		
		assertEquals("crossover operation was not correctly reverted", "122223", verify.toString());
	}
}
