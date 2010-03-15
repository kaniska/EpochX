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
package org.epochx.model;

import org.epochx.op.*;
import org.epochx.op.selection.*;
import org.epochx.stats.StatsEngine;
import org.epochx.tools.random.*;

/**
 * Default implementation of a <code>Model</code>. This class provides sensible
 * defaults where possible and provides convenient setter methods to allow 
 * overwriting the defaults without having to override the accessor methods.
 * It would be more typical to extend the abstract model specific to the 
 * representation being used.
 */
public abstract class AbstractModel implements Model {

	// Run parameters.
	private int noRuns;
	private int noGenerations;
	private int populationSize;
	private int poolSize;
	private int noElites;

	private double terminationFitness;
	private double crossoverProbability;
	private double mutationProbability;
	
	// Components.
	private PoolSelector poolSelector;
	private ProgramSelector programSelector;
	private RandomNumberGenerator randomNumberGenerator;
	
	// Caching.
	private boolean cacheFitness;
	
	// Stats.
	private StatsEngine statsEngine;
	
	/**
	 * Construct the model with defaults.
	 */
	public AbstractModel() {
		// Control parameters.
		noRuns = 1;
		noGenerations = 50;
		populationSize = 100;
		poolSize = 50;
		noElites = 10;
		terminationFitness = 0.0;
		crossoverProbability = 0.9;
		mutationProbability = 0.1;
		
		// Components.
		programSelector = new RandomSelector();
		poolSelector = new TournamentSelector(7);
		randomNumberGenerator = new MersenneTwisterFast();
		
		// Caching.
		cacheFitness = true;
		
		// Stats.
		statsEngine = new StatsEngine();
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p>Defaults to true in AbstractModel.
	 * 
	 * @return {@inheritDoc}
	 */
	@Override
	public boolean cacheFitness() {
		return cacheFitness;
	}
	
	/**
	 * Overwrites the default setting of whether to cache the fitness values.
	 * 
	 * @param cacheFitness whether fitnesses should be cached or not.
	 */
	public void setCacheFitness(boolean cacheFitness) {
		this.cacheFitness = cacheFitness;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p>Defaults to 1 in AbstractModel.
	 * 
	 * @return {@inheritDoc}
	 */
	@Override
	public int getNoRuns() {
		return noRuns;
	}

	/**
	 * Overwrites the default number of runs.
	 * 
	 * @param noRuns the new number of runs to execute with this model.
	 */
	public void setNoRuns(int noRuns) {
		this.noRuns = noRuns;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>Defaults to 50 in AbstractModel.
	 * 
	 * @return {@inheritDoc}
	 */
	@Override
	public int getNoGenerations() {
		return noGenerations;
	}

	/**
	 * Overwrites the default number of generations.
	 * 
	 * @param noGenerations the new number of generations to use within a run.
	 */
	public void setNoGenerations(int noGenerations) {
		this.noGenerations = noGenerations;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p>Defaults to 100 in AbstractModel.
	 * 
	 * @return {@inheritDoc}
	 */
	@Override
	public int getPopulationSize() {
		return populationSize;
	}

	/**
	 * Overwrites the default population size of CandidatePrograms.
	 * 
	 * @param populationSize the new number of CandidatePrograms each generation 
	 * 						 should contain.
	 */
	public void setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>Defaults to 50 in AbstractModel.
	 * 
	 * @return {@inheritDoc}
	 */
	@Override
	public int getPoolSize() {
		return poolSize;
	}

	/**
	 * Overwrites the default pool size value.
	 * 
	 * @param poolSize the new size of the mating pool to use.
	 */
	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p>Defaults to 10 in AbstractModel.
	 * 
	 * @return {@inheritDoc}
	 */
	@Override
	public int getNoElites() {
		return noElites;
	}

	/**
	 * Overwrites the default number of elites to copy from one generation to
	 * the next.
	 * 
	 * @param noElites the new number of elites to copy across from one 
	 * 				   population to the next.
	 */
	public void setNoElites(int noElites) {
		this.noElites = noElites;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>Defaults to 0.9 in AbstractModel to represent a 90% chance.
	 * 
	 * @return {@inheritDoc}
	 */
	@Override
	public double getCrossoverProbability() {
		return crossoverProbability;
	}

	/**
	 * Overwrites the default Crossover probability.
	 * 
	 * @param crossoverProbability the new Crossover probability to use.
	 */
	public void setCrossoverProbability(double crossoverProbability) {
		this.crossoverProbability = crossoverProbability;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>Defaults to 0.1 in AbstractModel to represent a 10% chance.
	 * 
	 * @return {@inheritDoc}
	 */
	@Override
	public double getMutationProbability() {
		return mutationProbability;
	}

	/**
	 * Overwrites the default mutation probability.
	 * 
	 * @param mutationProbability the new mutation probability to use.
	 */
	public void setMutationProbability(double mutationProbability) {
		this.mutationProbability = mutationProbability;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>Automatically calculates the reproduction probability based upon the 
	 * Crossover and mutation probabilities as all three together must add up 
	 * to 100%. Defaults to 0% in AbstractModel.
	 * 
	 * @return {@inheritDoc}
	 */
	@Override
	public double getReproductionProbability() {
		return 1.0 - (getCrossoverProbability() + getMutationProbability());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>Defaults to 0.0 in AbstractModel.
	 * 
	 * @return {@inheritDoc}
	 */
	@Override
	public double getTerminationFitness() {
		return terminationFitness;
	}
	
	/**
	 * Overwrites the default fitness for run termination.
	 * 
	 * @param terminationFitness the new fitness below which a run will be 
	 * 							 terminated.
	 */
	public void setTerminationFitness(double terminationFitness) {
		this.terminationFitness = terminationFitness;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p>Defaults to {@link RandomSelector} in AbstractModel.
	 * 
	 * @return {@inheritDoc}
	 */
	@Override
	public ProgramSelector getProgramSelector() {
		return programSelector;
	}

	/**
	 * Overwrites the default parent selector used to select parents to undergo
	 * a genetic operator from either a pool or the previous population.
	 * 
	 * @param ProgramSelector the new ProgramSelector to be used when selecting 
	 * 						 parents for a genetic operator.
	 */
	public void setProgramSelector(ProgramSelector programSelector) {
		this.programSelector = programSelector;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>Defaults to {@link TournamentSelector} with a tournament size of 7 
	 * in AbstractModel.
	 * 
	 * @return {@inheritDoc}
	 */
	@Override
	public PoolSelector getPoolSelector() {
		return poolSelector;
	}

	/**
	 * Overwrites the default pool selector used to generate a mating pool.
	 * 
	 * @param PoolSelector the new PoolSelector to be used when building a 
	 * 						breeding pool.
	 */
	public void setPoolSelector(PoolSelector poolSelector) {
		this.poolSelector = poolSelector;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p>Defaults to {@link MersenneTwisterFast} in AbstractModel.
	 * 
	 * @return {@inheritDoc}
	 */
	@Override
	public RandomNumberGenerator getRNG() {
		return randomNumberGenerator;
	}
	
	/**
	 * Overwrites the default random number generator used to generate random 
	 * numbers to control behaviour throughout a run.
	 * 
	 * @param rng the random number generator to be used any time random 
	 * 				behaviour is required.
	 */
	public void setRNG(RandomNumberGenerator rng) {
		this.randomNumberGenerator = rng;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p>Defaults to {@link StatsEngine} in AbstractModel.
	 * 
	 * @return {@inheritDoc}
	 */
	@Override
	public StatsEngine getStatsEngine() {
		return statsEngine;
	}
	
	/**
	 * Sets the stats engine to use.
	 * 
	 * @param statsEngine the stats engine to use in providing statistics.
	 */
	public void setStatsEngine(StatsEngine statsEngine) {
		this.statsEngine = statsEngine;
	}
}
