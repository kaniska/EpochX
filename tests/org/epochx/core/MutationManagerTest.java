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
package org.epochx.core;

import static org.junit.Assert.*;

import java.util.*;

import org.epochx.epox.Literal;
import org.epochx.gp.model.*;
import org.epochx.gp.representation.GPCandidateProgram;
import org.epochx.life.*;
import org.epochx.representation.CandidateProgram;
import org.junit.*;

/**
 * 
 */
public class MutationManagerTest {

	private GPModel model;
	private MutationManager mutationManager;

	private int count;

	@Before
	public void setUp() {
		model = new GPModelDummy();
		mutationManager = new MutationManager(model);
	}

	/**
	 * Tests that an exception is thrown if the crossover is null but crossover
	 * probability is not null.
	 */
	@Test
	public void testMutatorNotSet() {
		model.setMutation(null);
		model.setMutationProbability(0.1);

		evolver.getLife().fireConfigureEvent();

		try {
			mutationManager.mutate();
			fail("illegal state exception not thrown for a model with mutation enabled but null operator");
		} catch (final IllegalStateException e) {
		}
	}

	/**
	 * Tests that an exception is thrown if the program selector is null when
	 * attempting mutation.
	 */
	@Test
	public void testProgramSelectorNotSet() {
		// Create a model with a null program selector.
		model.setProgramSelector(null);

		evolver.getLife().fireConfigureEvent();

		try {
			mutationManager.mutate();
			fail("illegal state exception not thrown when performing mutation with a null program selector");
		} catch (final IllegalStateException e) {
		}
	}

	/**
	 * Tests that the mutation events are all triggered and in the correct
	 * order.
	 */
	@Test
	public void testMutationEventsOrder() {
		// We add the chars '1', '2', '3' to builder to check order of calls.
		final StringBuilder verify = new StringBuilder();

		final List<CandidateProgram> pop = new ArrayList<CandidateProgram>();
		pop.add(new GPCandidateProgram(new Literal(false), model));
		model.getProgramSelector().setSelectionPool(pop);
		model.getSyntax().add(new Literal(false));

		// Listen for the crossver.
		evolver.getLife().addMutationListener(new MutationListener() {

			@Override
			public void onMutationStart() {
				verify.append('1');
			}

			@Override
			public void onMutationEnd() {
				verify.append('3');
			}
		});
		evolver.getLife().addHook(new AbstractHook() {

			@Override
			public CandidateProgram mutationHook(final CandidateProgram parent, final CandidateProgram child) {
				verify.append('2');
				return child;
			}
		});

		evolver.getLife().fireConfigureEvent();
		mutationManager.mutate();

		assertEquals("mutation events were not called in the correct order", "123", verify.toString());
	}

	/**
	 * Tests that returning null to the crossover event will revert the
	 * crossover.
	 */
	@Test
	public void testGenerationEventRevert() {
		// We add the chars '1', '2', '3' to builder to check order of calls.
		final StringBuilder verify = new StringBuilder();

		final List<CandidateProgram> pop = new ArrayList<CandidateProgram>();
		pop.add(new GPCandidateProgram(new Literal(false), model));
		pop.add(new GPCandidateProgram(new Literal(false), model));
		model.getProgramSelector().setSelectionPool(pop);
		model.getSyntax().add(new Literal(false));

		count = 0;

		// Listen for the generation.
		evolver.getLife().addHook(new AbstractHook() {

			@Override
			public CandidateProgram mutationHook(final CandidateProgram parent, final CandidateProgram child) {
				verify.append('2');
				// Revert 3 times before confirming.
				if (count == 3) {
					return child;
				} else {
					count++;
				}
				return null;
			}
		});

		evolver.getLife().fireConfigureEvent();
		mutationManager.mutate();

		assertEquals("mutation operation was not correctly reverted", "2222", verify.toString());
	}
}
