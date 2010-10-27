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
package org.epochx.op.selection;

import java.util.*;

import org.epochx.core.Model;
import org.epochx.life.*;
import org.epochx.op.*;
import org.epochx.representation.CandidateProgram;
import org.epochx.tools.random.RandomNumberGenerator;


/**
 * A random selector is a program and pool selector which provides no selection 
 * pressure. No reference is made to the programs' fitness scores.
 */
public class RandomSelector implements ProgramSelector, PoolSelector {
	
	// The controlling model.
	private final Model model;
	
	// Random number generator.
	private RandomNumberGenerator rng;
	
	// The current pool from which programs should be chosen.
	private List<CandidateProgram> pool;
	
	/**
	 * Constructs an instance of <code>LinearRankSelector</code>.
	 * 
	 * @param model the Model which defines the run parameters such as the 
	 * 				random number generator to use.
	 */
	public RandomSelector(final Model model) {
		this.model = model;
		
		// Configure parameters from the model.
		model.getLifeCycleManager().addConfigListener(new ConfigAdapter() {
			@Override
			public void onConfigure() {
				configure();
			}
		});
	}
	
	/*
	 * Configure component with parameters from the model.
	 */
	private void configure() {
		rng = model.getRNG();
	}
	
	/**
	 * Sets the population from which individual programs will be randomly 
	 * selected.
	 * 
	 * @param pool the population of candidate programs from which programs 
	 * 			  should be selected.
	 */
	@Override
	public void setSelectionPool(final List<CandidateProgram> pool) {
		this.pool = pool;
	}
	
	/**
	 * Randomly chooses and returns a program from the population with no bias.
	 * 
	 * @return a randomly selected program.
	 */
	@Override
	public CandidateProgram getProgram() {
		if (pool == null || pool.isEmpty()) {
			throw new IllegalStateException("selection pool cannot be " +
					"null and must contain 1 or more CandidatePrograms");
		} else if (rng == null) {
			throw new IllegalStateException("random number generator not set");
		}
		
		return pool.get(rng.nextInt(pool.size()));
	}

	/**
	 * Randomly chooses programs from the given population up to a total of 
	 * <code>poolSize</code> and returns them as a list. The generated pool may 
	 * contain duplicate programs, and as such the pool size is allowed to be
	 * greater than the population size.
	 * 
	 * @param pop the population of CandidatePrograms from which the programs 
	 * 			  in the pool should be chosen. Must not be null, nor empty.
	 * @param poolSize the number of programs that should be selected from the 
	 * 			 	   population to form the pool. Must be 1 or greater.
	 * @return the randomly selected pool of candidate programs.
	 */
	@Override
	public List<CandidateProgram> getPool(final List<CandidateProgram> pop, 
			final int poolSize) {
		if (poolSize < 1) {
			throw new IllegalArgumentException("poolSize must be greater than 0");
		} else if ((pop == null) || (pop.isEmpty())) {
			throw new IllegalArgumentException("population to select pool from must not be null nor empty");
		} else if (rng == null) {
			throw new IllegalStateException("random number generator not set");
		}
		
		// Construct our pool.
		final List<CandidateProgram> pool = new ArrayList<CandidateProgram>(poolSize);
		for (int i=0; i<poolSize; i++) {
			pool.add(pop.get(rng.nextInt(pop.size())));
		}
		
		return pool;
	}
}