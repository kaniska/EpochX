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

import java.util.*;

import org.apache.commons.lang.*;
import org.apache.commons.lang.math.NumberUtils;
import org.epochx.representation.*;

import static org.epochx.stats.StatsManager.ExpiryEvent.*;

/**
 * Provides constants to be used as keys to request statistics from the
 * StatsManager.
 */
public class StatField {
	
	/**
	 * Returns an <code>Integer</code> which is the sequential identifier of the
	 * last run in the series of runs. The first run will be run number 0.
	 */
	public static final Stat RUN_NUMBER = new AbstractStat(RUN) {};

	/**
	 * Returns a <code>Long</code> which is the length of time in nanoseconds
	 * that the last run took to complete.
	 */
	public static final Stat RUN_TIME = new AbstractStat(RUN) {};
	
	/**
	 * Returns a <code>Double</code> which is the lowest fitness score achieved
	 * by a <code>CandidateProgram</code> in the last run. Note that
	 * standardised fitness is used, so the best program has the minimum
	 * fitness.
	 */
	public static final Stat RUN_FITNESS_MIN = new AbstractStat(RUN){};

	/**
	 * Returns the <code>CandidateProgram</code> which obtained the fitness
	 * score in the RUN_FITNESS_MIN field.
	 */
	public static final Stat RUN_FITTEST_PROGRAM = new AbstractStat(RUN){};

	/**
	 * Returns an <code>Integer</code> which is the number of times the last
	 * initialisation operation was reverted.
	 */
	public static final Stat INIT_REVERSIONS = new AbstractStat(INITIALISATION){};

	/**
	 * Returns an <code>Integer</code> which is the sequential identifier of the
	 * last generation where generation 0 is the initialisation phase.
	 */
	public static final Stat GEN_NUMBER = new AbstractStat(GENERATION) {};
	
	/**
	 * Returns a <code>double[]</code> which contains the fitnesses of all the
	 * <code>CandidateProgram</code>s in the population at the end of the
	 * previous generation. The fitnesses are arranged in the same order as the
	 * <code>List&lt;CandidateProgram&gt;</code> returned for the 
	 * <code>GEN_POP</code> field.
	 */
	public static final Stat GEN_FITNESSES = new AbstractStat(GENERATION) {
		public Object getStatValue() {
			double[] fitnesses = null;

			// Request the population from the stats manager.
			final List<CandidateProgram> pop = (List<CandidateProgram>) StatsManager.getInstance().getStat(GEN_POP);

			// Get the fitnesses of each program.
			if (pop != null) {
				fitnesses = new double[pop.size()];

				for (int i = 0; i < fitnesses.length; i++) {
					fitnesses[i] = pop.get(i).getFitness();
				}
			}

			return fitnesses;
		};
	};
	
	/**
	 * Returns a <code>Double</code> which is the lowest fitness score of a
	 * <code>CandidateProgram</code> in the last generation. Note that
	 * standardised fitness is used, so the best program has the minimum
	 * fitness.
	 */
	public static final Stat GEN_FITNESS_MIN = new AbstractStat(GENERATION) {
		public Object getStatValue() {
			Double minFitness = null;

			// Request the population fitnesses from the stats manager.
			final double[] fitnesses = (double[]) StatsManager.getInstance().getStat(GEN_FITNESSES);

			// Calculate the minimum fitness value.
			if (fitnesses != null) {
				minFitness = NumberUtils.min(fitnesses);
			}

			return minFitness;
		}
	};

	/**
	 * Returns a <code>Double</code> which is the highest fitness score of a
	 * <code>CandidateProgram</code> in the last generation. Note that
	 * standardised fitness is used, so the best program has the minimum
	 * fitness.
	 */
	public static final Stat GEN_FITNESS_MAX = new AbstractStat(GENERATION) {
		public Object getStatValue() {
			Double maxFitness = null;

			// Request the population fitnesses from the stats manager.
			final double[] fitnesses = (double[]) StatsManager.getInstance().getStat(GEN_FITNESSES);

			// Calculate the maximum fitness value.
			if (fitnesses != null) {
				maxFitness = NumberUtils.max(fitnesses);
			}

			return maxFitness;
		};		
	};

	/**
	 * Returns a <code>Double</code> which is the average fitness score of all
	 * the <code>CandidateProgram</code>s in the population at the end of the
	 * previous generation.
	 */
	public static final Stat GEN_FITNESS_AVE = new AbstractStat(GENERATION) {
		public Object getStatValue() {
			Double aveFitness = null;

			// Request the population fitnesses from the stats manager.
			final double[] fitnesses = (double[]) StatsManager.getInstance().getStat(GEN_FITNESSES);

			// Calculate the average fitness value.
			if (fitnesses != null) {
				aveFitness = StatsUtils.ave(fitnesses);
			}

			return aveFitness;
		};
	};

	/**
	 * Returns a <code>Double</code> which is the standard deviation of the
	 * fitness scores of all the <code>CandidateProgram</code>s in the
	 * population at the end of the previous generation.
	 */
	public static final Stat GEN_FITNESS_STDEV = new AbstractStat(GENERATION) {
		public Object getStatValue() {
			Double stdevFitness = null;

			// Request the population fitnesses from the stats manager.
			final double[] fitnesses = (double[]) StatsManager.getInstance()
					.getStat(GEN_FITNESSES);
			final double averageFitness = (Double) StatsManager.getInstance()
					.getStat(GEN_FITNESS_AVE);

			// Calculate the standard deviation of the fitness values.
			if (fitnesses != null) {
				stdevFitness = StatsUtils.stdev(fitnesses, averageFitness);
			}

			return stdevFitness;
		};
	};

	/**
	 * Returns a <code>Double</code> which is the median value of all the
	 * fitness scores from the population of <code>CandidateProgram</code>s at
	 * the end of the previous generation.
	 */
	public static final Stat GEN_FITNESS_MEDIAN = new AbstractStat(GENERATION) {
		public Object getStatValue() {
			Double medianFitness = null;

			// Request the population fitnesses from the stats manager.
			final double[] fitnesses = (double[]) StatsManager.getInstance()
					.getStat(GEN_FITNESSES);

			// Calculate the median of the fitness values.
			if (fitnesses != null) {
				medianFitness = StatsUtils.median(fitnesses);
			}

			return medianFitness;
		};
	};

	/**
	 * Returns a <code>Double</code> which is the 95% confidence interval either
	 * side of the fitness mean for the population of
	 * <code>CandidateProgram</code>s at the end of the previous generation.
	 */
	public static final Stat GEN_FITNESS_CI95 = new AbstractStat(GENERATION) {
		public Object getStatValue() {
			Double ci95Fitness = null;

			// Request the population fitnesses from the stats manager.
			final double[] fitnesses = (double[]) StatsManager.getInstance().getStat(GEN_FITNESSES);
			final double stdev = (Double) StatsManager.getInstance().getStat(GEN_FITNESS_STDEV);

			// Calculate the 95% confidence interval from the mean of the fitness
			// values.
			if (fitnesses != null) {
				ci95Fitness = StatsUtils.ci95(fitnesses, stdev);
			}

			return ci95Fitness;
		};
	};

	/**
	 * Returns the <code>CandidateProgram</code> which obtained the fitness
	 * score in the <code>GEN_FITNESS_MIN</code> field. If more than one program
	 * obtained the same fitness then the one returned will be chosen 
	 * arbitrarily. See the similarly named <code>GEN_FITTEST_PROGRAMS</code> 
	 * field to obtain all the programs.
	 */
	public static final Stat GEN_FITTEST_PROGRAM = new AbstractStat(GENERATION) {
		public Object getStatValue() {
			CandidateProgram bestProgram = null;

			// Request the population fitnesses from the stats manager.
			final double[] fitnesses = (double[]) StatsManager.getInstance().getStat(GEN_FITNESSES);
			final List<CandidateProgram> pop = (List<CandidateProgram>) StatsManager.getInstance().getStat(GEN_POP);

			// Retrieve the program with the minimum fitness value.
			if (fitnesses != null) {
				final int bestProgramIndex = StatsUtils.minIndex(fitnesses);

				bestProgram = pop.get(bestProgramIndex);
			}

			return bestProgram;
		};
	};
	
	/**
	 * Returns a <code>List&lt;CandidateProgram&gt;</code> of all those programs
	 * which obtained the fitness score in the <code>GEN_FITNESS_MIN</code> 
	 * field. See the similarly named <code>GEN_FITTEST_PROGRAM</code> field for
	 * returning just one <code>CandidateProgram</code>.
	 */
	public static final Stat GEN_FITTEST_PROGRAMS = new AbstractStat(GENERATION) {
		public Object getStatValue() {
			List<CandidateProgram> bestPrograms = new ArrayList<CandidateProgram>();

			// Request the population fitnesses from the stats manager.
			final Double minFitness = (Double) StatsManager.getInstance().getStat(GEN_FITNESS_MIN);
			final double[] fitnesses = (double[]) StatsManager.getInstance().getStat(GEN_FITNESSES);
			final List<CandidateProgram> pop = (List<CandidateProgram>) StatsManager.getInstance().getStat(GEN_POP);

			// Retrieve all the programs with the minimum fitness value.
			if (minFitness != null && fitnesses != null) {
				for (int i=0; i<fitnesses.length; i++) {
					double fitness = fitnesses[i];
					if (fitness == minFitness.doubleValue()) {
						bestPrograms.add(pop.get(i));
					}
				}
			}

			return bestPrograms;
		};
	};

	/**
	 * Returns an <code>Integer</code> which is the number of times the previous
	 * generation was reverted.
	 */
	public static final Stat GEN_REVERSIONS = new AbstractStat(GENERATION) {};

	/**
	 * Returns a <code>List&lt;CandidateProgram&gt;</code> which is the
	 * population of <code>CandidateProgram</code>s at the end of the previous
	 * generation.
	 */
	public static final Stat GEN_POP = new AbstractStat(GENERATION) {};
	
	/**
	 * Returns a <code>List&lt;CandidateProgram&gt;</code> which is the
	 * population of <code>CandidateProgram</code>s at the end of the previous
	 * generation. The programs are sorted according to fitness ascending from 
	 * lowest fitness score to highest.
	 */
	public static final Stat GEN_POP_SORTED = new AbstractStat(GENERATION) {
		public Object getStatValue() {
			List<CandidateProgram> sortedPop = null;
			
			// Request the population from the stats manager.
			final List<CandidateProgram> pop = (List<CandidateProgram>) StatsManager.getInstance().getStat(GEN_POP);
			
			if (pop != null) {
				// Create a copy of the population to be sorted.
				sortedPop = new ArrayList<CandidateProgram>(pop);
			
				// Sort the population.
				Collections.sort(sortedPop);
			}

			return sortedPop;
		};
	};
	
	/**
	 * Returns a <code>List&lt;CandidateProgram&gt;</code> which is the
	 * population of <code>CandidateProgram</code>s at the end of the previous
	 * generation. The programs are sorted according to fitness descending from 
	 * highest fitness score to lowest.
	 */
	public static final Stat GEN_POP_SORTED_DESC = new AbstractStat(GENERATION) {
		public Object getStatValue() {
			List<CandidateProgram> sortedPop = null;
			
			// Request the population from the stats manager.
			final List<CandidateProgram> pop = (List<CandidateProgram>) StatsManager.getInstance().getStat(GEN_POP);
			
			if (pop != null) {
				// Create a copy of the population to be sorted.
				sortedPop = new ArrayList<CandidateProgram>(pop);
				
				// Sort the population.
				Collections.sort(sortedPop, Collections.reverseOrder());
			}

			return sortedPop;
		};
	};
	
	/**
	 * Returns a <code>double[]</code> which contains the fitnesses of all the
	 * <code>CandidateProgram</code>s in the population at the end of the
	 * previous generation. The fitnesses are sorted in ascending order from 
	 * lowest fitness score to highest. The order matches the  
	 * the <code>List&lt;CandidateProgram&gt;</code> returned for the 
	 * <code>GEN_POP_SORTED</code> field.
	 */
	public static final Stat GEN_FITNESSES_SORTED = new AbstractStat(GENERATION) {
		public Object getStatValue() {
			double[] sortedFitnesses = null;
			
			// Request the population fitnesses from the stats manager.
			final double[] fitnesses = (double[]) StatsManager.getInstance().getStat(GEN_FITNESSES);
			
			if (fitnesses != null) {
				// Create a copy of the fitnesses.
				sortedFitnesses = fitnesses.clone();
				
				// Sort the fitnesses.
				Arrays.sort(sortedFitnesses);
			}

			return sortedFitnesses;
		};
	};
	
	/**
	 * Returns a <code>double[]</code> which contains the fitnesses of all the
	 * <code>CandidateProgram</code>s in the population at the end of the
	 * previous generation. The fitnesses are sorted in descending order from 
	 * highest fitness score to lowest. The order matches the 
	 * <code>List&lt;CandidateProgram&gt;</code> returned for the 
	 * <code>GEN_POP_SORTED_DESC</code> field.
	 */
	public static final Stat GEN_FITNESSES_SORTED_DESC = new AbstractStat(GENERATION) {
		public Object getStatValue() {
			double[] sortedFitnessesDesc = null;
			
			// Request the ascending sorted fitnesses from the stats manager.
			final double[] sortedFitnesses = (double[]) StatsManager.getInstance().getStat(GEN_FITNESSES_SORTED);

			if (sortedFitnesses != null) {
				// Create a copy of the sorted fitnesses.
				sortedFitnessesDesc = sortedFitnesses.clone();
				
				// Reverse the order of the sorted fitnesses.
				ArrayUtils.reverse(sortedFitnessesDesc);
			}

			return sortedFitnessesDesc;
		};
	};

	/**
	 * Returns a <code>Long</code> which is the length of time in nanoseconds
	 * that the last generation took to complete.
	 */
	public static final Stat GEN_TIME = new AbstractStat(GENERATION) {};

	/**
	 * Returns a <code>CandidateProgram</code> which is a copy of the program
	 * which underwent mutation as it was <b>before</b> the mutation operation
	 * was applied.
	 */
	public static final Stat MUT_PARENT = new AbstractStat(MUTATION) {};

	/**
	 * Returns a <code>CandidateProgram</code> which is the program that is the
	 * result of the last mutation operation.
	 */
	public static final Stat MUT_CHILD = new AbstractStat(MUTATION) {};

	/**
	 * Returns a <code>Long</code> which is the length of time in nanoseconds
	 * that the last mutation operation took to complete.
	 */
	public static final Stat MUT_TIME = new AbstractStat(MUTATION) {};

	/**
	 * Returns an <code>Integer</code> which is the number of times the last
	 * mutation operation was reverted.
	 */
	public static final Stat MUT_REVERSIONS = new AbstractStat(MUTATION) {};

	/**
	 * Returns a <code>Double</code> which is the fitness score of the program
	 * selected for mutation prior to the mutation operation being performed.
	 */
	public static final Stat MUT_PARENT_FITNESS = new AbstractStat(MUTATION) {
		public Object getStatValue() {
			Double fitness = null;
			
			// Request the parent from the stats manager.
			final CandidateProgram parent = (CandidateProgram) StatsManager.getInstance().getStat(MUT_PARENT);

			if (parent != null) {
				// Get the program's fitness.
				fitness = parent.getFitness();
			}

			return fitness;
		};
	};
	
	/**
	 * Returns a <code>Double</code> which is the fitness score of the program
	 * selected for mutation after the mutation operation has been performed.
	 */
	public static final Stat MUT_CHILD_FITNESS = new AbstractStat(MUTATION) {
		public Object getStatValue() {
			Double fitness = null;
			
			// Request the child from the stats manager.
			final CandidateProgram child = (CandidateProgram) StatsManager.getInstance().getStat(MUT_CHILD);

			if (child != null) {
				// Get the program's fitness.
				fitness = child.getFitness();
			}

			return fitness;
		};
	};
	
	/**
	 * Returns a <code>Double</code> which is the amount by which the fitness of
	 * the mutated program has changed from before to after the operation. If 
	 * the fitness has decreased then the returned value will be negative, if 
	 * it has increased then it will be positive and it will be zero if the 
	 * fitness is unchanged.
	 */
	public static final Stat MUT_FITNESS_CHANGE = new AbstractStat(MUTATION) {
		public Object getStatValue() {
			Double fitnessChange = null;
			
			// Request the fitnesses from the stats manager.
			final Double fitnessBefore = (Double) StatsManager.getInstance().getStat(MUT_PARENT_FITNESS);
			final Double fitnessAfter = (Double) StatsManager.getInstance().getStat(MUT_CHILD_FITNESS);
			
			if (fitnessBefore != null && fitnessAfter != null) {
				// Calculate the fitness change.
				fitnessChange = fitnessAfter.doubleValue() - fitnessBefore.doubleValue();
			}

			return fitnessChange;
		};		
	};
	
	/**
	 * Returns a <code>CandidateProgram[]</code> which contains a copy of the
	 * programs which underwent crossover as they were <b>before</b> the
	 * crossover operation was applied.
	 **/
	public static final Stat XO_PARENTS = new AbstractStat(CROSSOVER) {};

	/**
	 * Returns a <code>CandidateProgram[]</code> which contains the programs
	 * that are the result of the last crossover operation.
	 **/
	public static final Stat XO_CHILDREN = new AbstractStat(CROSSOVER) {};

	/**
	 * Returns a <code>Long</code> which is the length of time in nanoseconds
	 * that the last crossover operation took to complete.
	 */
	public static final Stat XO_TIME = new AbstractStat(CROSSOVER) {};

	/**
	 * Returns an <code>Integer</code> which is the number of times the last
	 * crossover operation was reverted.
	 */
	public static final Stat XO_REVERSIONS = new AbstractStat(CROSSOVER) {};
	
	/**
	 * Returns a <code>double[]</code> which is an array of the fitnesses of the
	 * programs selected for crossover prior to the crossover operation being 
	 * performed. The order of the fitness scores is the same as for the 
	 * <code>CandidateProgram[]</code> returned for the <code>XO_PARENTS</code>
	 * field.
	 */
	public static final Stat XO_PARENT_FITNESSES = new AbstractStat(CROSSOVER) {
		public Object getStatValue() {
			double[] fitnesses = null;
			
			// Request the parents from the stats manager.
			final CandidateProgram[] parents = (CandidateProgram[]) StatsManager.getInstance().getStat(XO_PARENTS);
			
			if (parents != null) {
				fitnesses = new double[parents.length];
				
				for (int i=0; i<parents.length; i++) {
					// Calculate the fitness change.
					fitnesses[i] = parents[i].getFitness();
				}
			}

			return fitnesses;
		};
	};
	
	/**
	 * Returns a <code>double[]</code> which is an array of the fitnes scores 
	 * of the programs selected for crossover after the crossover operation has
	 * been performed. The order of the fitness scores is the same as for the 
	 * <code>CandidateProgram[]</code> returned for the <code>XO_CHILDREN</code>
	 * field.
	 */
	public static final Stat XO_CHILD_FITNESSES = new AbstractStat(CROSSOVER) {
		public Object getStatValue() {
			double[] fitnesses = null;
			
			// Request the children from the stats manager.
			final CandidateProgram[] children = (CandidateProgram[]) StatsManager.getInstance().getStat(XO_CHILDREN);
			
			if (children != null) {
				fitnesses = new double[children.length];
				
				for (int i=0; i<children.length; i++) {
					// Calculate the fitness change.
					fitnesses[i] = children[i].getFitness();
				}
			}

			return fitnesses;
		};
	};
	
	/**
	 * Returns a <code>Double</code> which is the average value of the fitnesses
	 * of the parents selected for crossover prior to the crossover operation
	 * being performed.
	 */
	public static final Stat XO_PARENTS_FITNESS_AVE = new AbstractStat(CROSSOVER) {
		public Object getStatValue() {
			Double aveFitness = null;

			// Request the population fitnesses from the stats manager.
			final double[] fitnesses = (double[]) StatsManager.getInstance().getStat(XO_PARENT_FITNESSES);

			// Calculate the average fitness value.
			if (fitnesses != null) {
				aveFitness = StatsUtils.ave(fitnesses);
			}

			return aveFitness;
		};
	};
	
	/**
	 * Returns a <code>Double</code> which is the average value of the fitnesses
	 * of the programs selected for crossover after the crossover operation has
	 * been performed.
	 */
	public static final Stat XO_CHILDREN_FITNESS_AVE = new AbstractStat(CROSSOVER) {
		public Object getStatValue() {
			Double aveFitness = null;

			// Request the population fitnesses from the stats manager.
			final double[] fitnesses = (double[]) StatsManager.getInstance().getStat(XO_CHILD_FITNESSES);

			// Calculate the average fitness value.
			if (fitnesses != null) {
				aveFitness = StatsUtils.ave(fitnesses);
			}

			return aveFitness;
		};
	};

	/**
	 * Returns a <code>Double</code> which is the amount by which the average 
	 * fitnesses of the crossed over programs has changed from before to after 
	 * the operation. If the fitness has decreased then the returned value will 
	 * be negative, if it has increased then it will be positive and it will be
	 * zero if the fitness is unchanged.
	 */
	public static final Stat XO_FITNESS_AVE_CHANGE = new AbstractStat(CROSSOVER) {
		public Object getStatValue() {
			Double fitnessChange = null;
			
			// Request the parent from the stats manager.
			final Double aveFitnessBefore = (Double) StatsManager.getInstance().getStat(XO_PARENTS_FITNESS_AVE);
			final Double aveFitnessAfter = (Double) StatsManager.getInstance().getStat(XO_CHILDREN_FITNESS_AVE);
			
			if (aveFitnessBefore != null && aveFitnessAfter != null) {
				// Calculate the fitness change.
				fitnessChange = aveFitnessAfter.doubleValue() - aveFitnessBefore.doubleValue();
			}

			return fitnessChange;
		};
	};
	
	/**
	 * Returns an <code>Integer</code> which is the number of times the last
	 * pool selection operation was reverted.
	 */
	public static final Stat POOL_REVERSIONS = new AbstractStat(POOL_SELECTION) {};

	/**
	 * Returns a <code>double[]</code> which contains the fitnesses of all the
	 * <code>CandidateProgram</code>s in the pool that was last constructed. 
	 * The fitnesses are arranged in the same order as the
	 *  <code>CandidateProgram[]</code> returned for the 
	 *  <code>POOL_PROGRAMS</code> field.
	 */
	public static final Stat POOL_FITNESSES = new AbstractStat(POOL_SELECTION) {
		public Object getStatValue() {
			double[] fitnesses = null;

			// Request the pool from the stats manager.
			final List<CandidateProgram> pop = (List<CandidateProgram>) StatsManager.getInstance().getStat(POOL_PROGRAMS);

			// Get the fitnesses of each program.
			if (pop != null) {
				fitnesses = new double[pop.size()];

				for (int i = 0; i < fitnesses.length; i++) {
					fitnesses[i] = pop.get(i).getFitness();
				}
			}

			return fitnesses;
		};
	};

	/**
	 * Returns a <code>Double</code> which is the lowest fitness score of a
	 * <code>CandidateProgram</code> in the last pool. Note that
	 * standardised fitness is used, so the best program has the minimum
	 * fitness.
	 */
	public static final Stat POOL_FITNESS_MIN = new AbstractStat(POOL_SELECTION) {
		public Object getStatValue() {
			Double minFitness = null;

			// Request the pool fitnesses from the stats manager.
			final double[] fitnesses = (double[]) StatsManager.getInstance().getStat(POOL_FITNESSES);

			// Calculate the minimum fitness value.
			if (fitnesses != null) {
				minFitness = NumberUtils.min(fitnesses);
			}

			return minFitness;
		};
	};

	/**
	 * Returns a <code>Double</code> which is the highest fitness score of a
	 * <code>CandidateProgram</code> in the last pool. Note that
	 * standardised fitness is used, so the best program has the minimum
	 * fitness.
	 */
	public static final Stat POOL_FITNESS_MAX = new AbstractStat(POOL_SELECTION) {
		public Object getStatValue() {
			Double maxFitness = null;

			// Request the pool fitnesses from the stats manager.
			final double[] fitnesses = (double[]) StatsManager.getInstance().getStat(POOL_FITNESSES);

			// Calculate the maximum fitness value.
			if (fitnesses != null) {
				maxFitness = NumberUtils.max(fitnesses);
			}

			return maxFitness;
		};
	};

	/**
	 * Returns a <code>Double</code> which is the average fitness score of all
	 * the <code>CandidateProgram</code>s in the last breeding pool that was 
	 * selected.
	 */
	public static final Stat POOL_FITNESS_AVE = new AbstractStat(POOL_SELECTION) {
		public Object getStatValue() {
			Double aveFitness = null;

			// Request the pool fitnesses from the stats manager.
			final double[] fitnesses = (double[]) StatsManager.getInstance()
					.getStat(POOL_FITNESSES);

			// Calculate the average fitness value.
			if (fitnesses != null) {
				aveFitness = StatsUtils.ave(fitnesses);
			}

			return aveFitness;
		};
	};

	/**
	 * Returns a <code>Double</code> which is the standard deviation of the
	 * fitness scores of all the <code>CandidateProgram</code>s in the
	 * last breeding pool that was selected.
	 */
	public static final Stat POOL_FITNESS_STDEV = new AbstractStat(POOL_SELECTION) {
		public Object getStatValue() {
			Double stdevFitness = null;

			// Request the pool fitnesses from the stats manager.
			final double[] fitnesses = (double[]) StatsManager.getInstance()
					.getStat(POOL_FITNESSES);
			final double averageFitness = (Double) StatsManager.getInstance()
					.getStat(POOL_FITNESS_AVE);

			// Calculate the standard deviation of the fitness values.
			if (fitnesses != null) {
				stdevFitness = StatsUtils.stdev(fitnesses, averageFitness);
			}

			return stdevFitness;
		};
	};

	/**
	 * Returns a <code>Double</code> which is the median value of all the
	 * fitness scores from the pool of <code>CandidateProgram</code>s that was 
	 * last selected.
	 */
	public static final Stat POOL_FITNESS_MEDIAN = new AbstractStat(POOL_SELECTION) {
		public Object getStatValue() {
			Double medianFitness = null;

			// Request the pool fitnesses from the stats manager.
			final double[] fitnesses = (double[]) StatsManager.getInstance()
					.getStat(POOL_FITNESSES);

			// Calculate the median of the fitness values.
			if (fitnesses != null) {
				medianFitness = StatsUtils.median(fitnesses);
			}

			return medianFitness;
		};
	};

	/**
	 * Returns a <code>Double</code> which is the 95% confidence interval either
	 * side of the fitness mean for the pool of <code>CandidateProgram</code>s 
	 * that was last selected.
	 */
	public static final Stat POOL_FITNESS_CI95 = new AbstractStat(POOL_SELECTION) {
		public Object getStatValue() {
			Double ci95Fitness = null;

			// Request the pool fitnesses from the stats manager.
			final double[] fitnesses = (double[]) StatsManager.getInstance().getStat(POOL_FITNESSES);
			final double stdev = (Double) StatsManager.getInstance().getStat(POOL_FITNESS_STDEV);

			// Calculate the 95% confidence interval from the mean of the fitness
			// values.
			if (fitnesses != null) {
				ci95Fitness = StatsUtils.ci95(fitnesses, stdev);
			}

			return ci95Fitness;
		};	
	};

	/**
	 * Returns the <code>CandidateProgram</code> which obtained the fitness
	 * score in the <code>POOL_FITNESS_MIN</code> field. If more than one 
	 * program obtained the same fitness then the one returned will be chosen 
	 * arbitrarily. See the similarly named <code>POOL_FITTEST_PROGRAMS</code> 
	 * field to obtain all the programs.
	 */
	public static final Stat POOL_FITTEST_PROGRAM = new AbstractStat(POOL_SELECTION) {
		public Object getStatValue() {
			CandidateProgram bestProgram = null;

			// Request the pool fitnesses from the stats manager.
			final double[] fitnesses = (double[]) StatsManager.getInstance().getStat(POOL_FITNESSES);
			final List<CandidateProgram> pool = (List<CandidateProgram>) StatsManager.getInstance().getStat(POOL_PROGRAMS);

			// Retrieve the program with the minimum fitness value.
			if (fitnesses != null) {
				final int bestProgramIndex = StatsUtils.minIndex(fitnesses);

				bestProgram = pool.get(bestProgramIndex);
			}

			return bestProgram;
		};
	};
	
	/**
	 * Returns a <code>List&lt;CandidateProgram&gt;</code> of all those programs
	 * which obtained the fitness score in the <code>POOL_FITNESS_MIN</code> 
	 * field. See the similarly named <code>POOL_FITTEST_PROGRAM</code> field 
	 * for returning just one <code>CandidateProgram</code>.
	 */
	public static final Stat POOL_FITTEST_PROGRAMS = new AbstractStat(POOL_SELECTION) {
		public Object getStatValue() {
			List<CandidateProgram> bestPrograms = new ArrayList<CandidateProgram>();

			// Request the pool fitnesses from the stats manager.
			final Double minFitness = (Double) StatsManager.getInstance().getStat(POOL_FITNESS_MIN);
			final double[] fitnesses = (double[]) StatsManager.getInstance().getStat(POOL_FITNESSES);
			final List<CandidateProgram> pool = (List<CandidateProgram>) StatsManager.getInstance().getStat(POOL_PROGRAMS);

			// Retrieve all the programs with the minimum fitness value.
			if (minFitness != null && fitnesses != null) {
				for (int i=0; i<fitnesses.length; i++) {
					double fitness = fitnesses[i];
					if (fitness == minFitness.doubleValue()) {
						bestPrograms.add(pool.get(i));
					}
				}
			}

			return bestPrograms;
		};
	};

	/**
	 * Returns a <code>List&lt;CandidateProgram&gt;</code> which is the
	 * pool of <code>CandidateProgram</code>s that was last selected.
	 */
	public static final Stat POOL_PROGRAMS = new AbstractStat(POOL_SELECTION) {};
	
	/**
	 * Returns an <code>Integer</code> which is the number of programs that were
	 * selected to form the breeding pool.
	 */
	public static final Stat POOL_SIZE = new AbstractStat(POOL_SELECTION) {
		public Object getStatValue() {
			Integer poolSize = null;
			
			// Request the pool from the stats manager.
			final List<CandidateProgram> pool = (List<CandidateProgram>) StatsManager.getInstance().getStat(XO_PARENTS_FITNESS_AVE);
			
			if (pool != null) {
				// Calculate the fitness change.
				poolSize = pool.size();
			}

			return poolSize;
		};
	};

	/**
	 * Returns a <code>Long</code> which is the length of time in nanoseconds
	 * that the last pool selection took to complete.
	 */
	public static final Stat POOL_TIME = new AbstractStat(POOL_SELECTION) {};
	
	/**
	 * Returns an <code>Integer</code> which is the number of times the last
	 * reproduction operation was reverted.
	 */
	public static final Stat REP_REVERSIONS = new AbstractStat(REPRODUCTION) {};
	
	/**
	 * Returns a <code>Long</code> which is the length of time in nanoseconds
	 * that the last reproduction operation took to complete.
	 */
	public static final Stat REP_TIME = new AbstractStat(REPRODUCTION) {};
	
	/**
	 * Returns a <code>List&lt;CandidateProgram&gt;</code> which contains all 
	 * the programs that were selected for elitism.
	 */
	public static final Stat ELITE_PROGRAMS = new AbstractStat(ELITISM) {};
	
	/**
	 * Returns an <code>Integer</code> which is the number of programs that were
	 * selected for elitism.
	 */
	public static final Stat ELITE_SIZE = new AbstractStat(ELITISM) {
		public Object getStatValue() {
			Integer eliteSize = null;
			
			// Request the pool from the stats manager.
			final List<CandidateProgram> elites = (List<CandidateProgram>) StatsManager.getInstance().getStat(ELITE_PROGRAMS);
			
			if (elites != null) {
				// Calculate the fitness change.
				eliteSize = elites.size();
			}

			return eliteSize;
		};
	};
	
	/**
	 * Returns a <code>double[]</code> which contains the fitnesses of all the
	 * <code>CandidateProgram</code>s selected for elitism. The fitnesses are 
	 * arranged in the same order as the <code>CandidateProgram[]</code> 
	 * returned for the <code>ELITE_PROGRAMS</code> field.
	 */
	public static final Stat ELITE_FITNESSES = new AbstractStat(ELITISM) {
		public Object getStatValue() {
			double[] fitnesses = null;

			// Request the elites from the stats manager.
			final List<CandidateProgram> elites = (List<CandidateProgram>) StatsManager.getInstance().getStat(ELITE_PROGRAMS);

			// Get the fitnesses of each program.
			if (elites != null) {
				fitnesses = new double[elites.size()];

				for (int i = 0; i < fitnesses.length; i++) {
					fitnesses[i] = elites.get(i).getFitness();
				}
			}

			return fitnesses;
		};
	};
	
	/**
	 * Returns a <code>Double</code> which is the minimum fitness value of the 
	 * programs selected for elitism. With standardised fitness this will always
	 * be the same as the value returned from <code>GEN_FITNESS_MIN</code> from
	 * the previous generation.
	 */
	public static final Stat ELITE_FITNESS_MIN = new AbstractStat(ELITISM) {
		public Object getStatValue() {
			Double minFitness = null;

			// Request the elite fitnesses from the stats manager.
			final double[] fitnesses = (double[]) StatsManager.getInstance().getStat(ELITE_FITNESSES);

			// Calculate the minimum fitness value.
			if (fitnesses != null) {
				minFitness = NumberUtils.min(fitnesses);
			}

			return minFitness;
		};
	};
	
	/**
	 * Returns a <code>Double</code> which is the maximum fitness score of the 
	 * programs selected for elitism.
	 */
	public static final Stat ELITE_FITNESS_MAX = new AbstractStat(ELITISM) {
		public Object getStatValue() {
			Double maxFitness = null;

			// Request the elite fitnesses from the stats manager.
			final double[] fitnesses = (double[]) StatsManager.getInstance().getStat(ELITE_FITNESSES);

			// Calculate the maximum fitness value.
			if (fitnesses != null) {
				maxFitness = NumberUtils.max(fitnesses);
			}

			return maxFitness;
		};
	};
	
	/**
	 * Returns a <code>Double</code> which is the average fitness of all the 
	 * programs selected for elitism.
	 */
	public static final Stat ELITE_FITNESS_AVE = new AbstractStat(ELITISM) {
		public Object getStatValue() {
			Double aveFitness = null;

			// Request the elite fitnesses from the stats manager.
			final double[] fitnesses = (double[]) StatsManager.getInstance()
					.getStat(ELITE_FITNESSES);

			// Calculate the average fitness value.
			if (fitnesses != null) {
				aveFitness = StatsUtils.ave(fitnesses);
			}

			return aveFitness;
		};
	};
	
	/**
	 * Returns a <code>Double</code> which is the standard deviation of all the
	 * fitnesses of the programs selected for elitism.
	 */
	public static final Stat ELITE_FITNESS_STDEV = new AbstractStat(ELITISM) {
		public Object getStatValue() {
			Double stdevFitness = null;

			// Request the elite fitnesses from the stats manager.
			final double[] fitnesses = (double[]) StatsManager.getInstance()
					.getStat(ELITE_FITNESSES);
			final double averageFitness = (Double) StatsManager.getInstance()
					.getStat(ELITE_FITNESS_AVE);

			// Calculate the standard deviation of the fitness values.
			if (fitnesses != null) {
				stdevFitness = StatsUtils.stdev(fitnesses, averageFitness);
			}

			return stdevFitness;
		};
	};
	
	/**
	 * Returns a <code>Double</code> which is the median fitness score of all 
	 * the programs selected for elitism.
	 */
	public static final Stat ELITE_FITNESS_MEDIAN = new AbstractStat(ELITISM) {
		public Object getStatValue() {
			Double medianFitness = null;

			// Request the elite fitnesses from the stats manager.
			final double[] fitnesses = (double[]) StatsManager.getInstance()
					.getStat(ELITE_FITNESSES);

			// Calculate the median of the fitness values.
			if (fitnesses != null) {
				medianFitness = StatsUtils.median(fitnesses);
			}

			return medianFitness;
		};
	};
	
	/**
	 * Returns a <code>Double</code> which is the 95% confidence interval either
	 * side of the fitness mean for the programs selected for elitism.
	 */
	public static final Stat ELITE_FITNESS_CI95 = new AbstractStat(ELITISM) {
		public Object getStatValue() {
			Double ci95Fitness = null;

			// Request the elite fitnesses from the stats manager.
			final double[] fitnesses = (double[]) StatsManager.getInstance().getStat(ELITE_FITNESSES);
			final double stdev = (Double) StatsManager.getInstance().getStat(ELITE_FITNESS_STDEV);

			// Calculate the 95% confidence interval from the mean of the fitness
			// values.
			if (fitnesses != null) {
				ci95Fitness = StatsUtils.ci95(fitnesses, stdev);
			}

			return ci95Fitness;
		};
	};
	
	/**
	 * Returns a <code>Long</code> which is the length of time in nanoseconds 
	 * that the last elitism operation took to complete.
	 */
	public static final Stat ELITE_TIME = new AbstractStat(ELITISM) {};

}
