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

import org.epochx.core.*;
import org.epochx.life.*;
import org.epochx.op.*;
import org.epochx.representation.*;
import org.epochx.tools.random.*;

/**
 * Fitness proportionate selection chooses programs with a probability 
 * proportional to their fitness inverted. Fitnesses must be inverted prior to
 * selection since fitness is standardised. Inversion is performed by 
 * subtracting each fitness from the maximum fitness in the population, plus the
 * minimum fitness.
 * 
 * @see LinearRankSelector
 * @see RandomSelector
 * @see TournamentSelector
 */
public class FitnessProportionateSelector implements ProgramSelector, PoolSelector {

	// The controlling model.
	private final Model model;
	
	// Internal program selectors used by the 2 different tasks.
	private final ProgramFitnessProportionateSelector programSelection;
	private final ProgramFitnessProportionateSelector poolSelection;
	
	// Random number generator.
	private RandomNumberGenerator rng;
	
	/**
	 * Constructs an instance of <code>FitnessProportionateSelector</code>.
	 * 
	 * @param model the Model which defines the run parameters such as the 
	 * 				random number generator to use.
	 */
	public FitnessProportionateSelector(final Model model) {
		this.model = model;
		
		// Construct the internal program selectors.
		programSelection = new ProgramFitnessProportionateSelector();
		poolSelection = new ProgramFitnessProportionateSelector();
		
		// Configure parameters from the model.
		model.getLifeCycleManager().addConfigListener(new ConfigAdapter() {
			@Override
			public void onConfigure() {
				configure();
			}
		});
	}
	
	/*
	 * Configures component with parameters from the model.
	 */
	private void configure() {
		rng = model.getRNG();
	}
	
	/**
	 * Sets the population from which programs will be selected.
	 * 
	 * @param pool the population of candidate programs from which programs 
	 * 			  should be selected.
	 */
	@Override
	public void setSelectionPool(final List<CandidateProgram> pool) {
		programSelection.setSelectionPool(pool);
	}
	
	/**
	 * Selects a candidate program at random from the population according to 
	 * the probabilities which were assigned proportional to the inverse of the
	 * program's fitness.
	 * 
	 * @return a program selected from the current population based on fitness.
	 */
	@Override
	public CandidateProgram getProgram() {
		return programSelection.getProgram();
	}
	
	/**
	 * Constructs a pool of programs from the population, choosing each one 
	 * with the program selection element of FitnessProportionateSelector. The 
	 * size of the pool created will be equal to the poolSize argument. The 
	 * generated pool may contain duplicate programs, and as such the pool size 
	 * may be greater than the population size.
	 * 
	 * @param pool the population of CandidatePrograms from which the programs 
	 * 			  in the pool should be chosen.
	 * @param poolSize the number of programs that should be selected from the 
	 * 			 	   population to form the pool. The poolSize must be 1 or 
	 * 				   greater.
	 * @return the pool of candidate programs selected according to fitness 
	 * rank.
	 */
	@Override
	public List<CandidateProgram> getPool(
			final List<CandidateProgram> pop, final int poolSize) {
		if (poolSize < 1) {
			throw new IllegalArgumentException("poolSize must be greater than 0");
		} else if ((pop == null) || (pop.isEmpty())) {
			throw new IllegalArgumentException("population to select pool from must not be null nor empty");
		}
		
		// Use internal program selector to select poolSize programs.
		poolSelection.setSelectionPool(pop);
		final List<CandidateProgram> pool = new ArrayList<CandidateProgram>();
		
		for (int i=0; i<poolSize; i++) {
			pool.add(poolSelection.getProgram());
		}
		
		return pool;
	}
	
	/*
	 * This is a little strange, but we use an inner class here so we can 
	 * create 2 separate instances of it internally for the 2 tasks of pool
	 * selection and program selection which is necessary because they both 
	 * select from different pools. The original implementation of getPool 
	 * created an internal instance of FitnessProportionateSelector but it is 
	 * not advisable to create components between model configurations.
	 */
	private class ProgramFitnessProportionateSelector implements ProgramSelector {
		
		// The current population from which programs should be chosen.
		private List<CandidateProgram> pool;
		
		// Normalised fitnesses.
		private double[] fitnesses;
		
		@Override
		public void setSelectionPool(final List<CandidateProgram> pool) {
			if (pool == null || pool.isEmpty()) {
				throw new IllegalArgumentException("selection pool cannot be " +
						"null and must contain 1 or more CandidatePrograms");
			}
			this.pool = pool;
			
			fitnesses = new double[pool.size()];
			
			double maxFitness = Double.NEGATIVE_INFINITY;
			double minFitness = Double.POSITIVE_INFINITY;
			
			// Find the max and min fitnesses.
			for (int i=0; i<fitnesses.length; i++) {
				fitnesses[i] = pool.get(i).getFitness();
				if (fitnesses[i] > maxFitness) {
					maxFitness = fitnesses[i];
				}
				if (fitnesses[i] < minFitness) {
					minFitness = fitnesses[i];
				}
			}
			
			// Add the minimum fitness as an offset.
			maxFitness += minFitness;
			
			// Invert and sum all the fitnesses.
			double totalFitness = 0;
			for (int i=0; i<pool.size(); i++) {
				fitnesses[i] = maxFitness - fitnesses[i];
				totalFitness += fitnesses[i];
			}
			
			// Calculate cumulative normalised fitnesses.
			double sum = 0.0;
			for (int i=0; i<fitnesses.length; i++) {
				sum += (fitnesses[i] / totalFitness);
				fitnesses[i] = sum;
			}
			
			// Ensure the final probability is 1.0.
			fitnesses[fitnesses.length-1] = 1.0;
		}

		@Override
		public CandidateProgram getProgram() {
			double ran = rng.nextDouble();
			
			assert (ran >= 0.0 && ran <= 1.0);
			
			for (int i=0; i<fitnesses.length; i++) {
				if (ran <= fitnesses[i]) {
					return pool.get(i);
				}
			}
			
			// This shouldn't ever happen assuming the final probability is 1.0.
			assert false;
			
			return null;
		}
	}

}
