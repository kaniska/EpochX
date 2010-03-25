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

import static org.epochx.stats.StatField.*;

import java.util.*;

import org.epochx.life.*;
import org.epochx.op.ProgramSelector;
import org.epochx.representation.CandidateProgram;
import org.epochx.stats.StatsManager;
import org.epochx.tools.random.RandomNumberGenerator;

/**
 * This component is responsible for carrying out single generations of an 
 * evolutionary run. A generation receives a population of 
 * <code>CandidatePrograms</code>, performs genetic operations (usually with 
 * some selection pressure) on those programs and returns a new population of 
 * <code>CandidatePrograms</code>.
 * 
 * <p>
 * An instance of <code>GenerationManager</code> is tied to a 
 * <code>Model</code>.
 * 
 * <p>
 * Use of a generation will generate the following events:
 * 
 * <p>
 * <table border="1">
 *     <tr>
 *         <th>Event</th>
 *         <th>Revert</th>
 *         <th>Modify</th>
 *         <th>Raised when?</th>
 *     </tr>
 *     <tr>
 *         <td>onGenerationStart</td>
 *         <td>no</td>
 *         <td>no</td>
 *         <td>Before the generation is started.
 *         </td>
 *     </tr>
 *     <tr>
 *         <td>onGeneration</td>
 *         <td><strong>yes</strong></td>
 *         <td><strong>yes</strong></td>
 *         <td>Immediately after the generation is completed, giving the listener 
 *         the opportunity to request a revert which will cause the generation 
 *         to be performed again starting from the previous population. This 
 *         will result in this event being raised again.
 *         </td>
 *     </tr>
 *     <tr>
 *         <td>onGenerationEnd</td>
 *         <td>no</td>
 *         <td>no</td>
 *         <td>After the generation has been completed.
 *         </td>
 *     </tr>
 * </table>
 */
public class GenerationManager {

	// The controlling model.
	private Model model;
	
	// Core components.
	private final ElitismManager elitism;
	private final PoolSelectionManager poolSelection;
	private final CrossoverManager crossover;
	private final MutationManager mutation;
	private final ReproductionManager reproduction;
	
	private RandomNumberGenerator rng;

	// Operator probabilities.
	private double mutationProbability;
	private double crossoverProbability;
	
	private int popSize;
	private ProgramSelector programSelector;
	
	// Count of generation reversions.
	private int reversions;
	
	/**
	 * Constructs a generation manager for carrying out generations. The given 
	 * <code>Model</code> defines how the components will be configured for the
	 * generation.
	 * 
	 * @param model a model which will provide the control parameters for the 
	 * generation.
	 */
	public GenerationManager(Model model) {
		this.model = model;
		
		// Setup core components.
		elitism = new ElitismManager(model);
		poolSelection = new PoolSelectionManager(model);
		crossover = new CrossoverManager(model);
		mutation = new MutationManager(model);
		reproduction = new ReproductionManager(model);
		
		reversions = 0;
		
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
		programSelector = model.getProgramSelector();
		popSize = model.getPopulationSize();
		mutationProbability = model.getMutationProbability();
		crossoverProbability = model.getCrossoverProbability();
	}
	
	/**
	 * Performs one generation of an evolutionary run. The method receives the 
	 * previous population and then performs one generation and returns the 
	 * resultant population. 
	 * 
	 * <p>
	 * A generation consists of the following sequence of events:
	 * 
	 * <ol>
	 *   <li>Select the elites and put them into the next population.</li>
	 *   <li>Select a breeding pool of programs.</li>
	 *   <li>Randomly choose an operator based upon probablities from the model:
	 *   	<ul>
	 *   		<li>Crossover - pass control to crossover component.</li>
	 *   		<li>Mutation - pass control to mutation component.</li>
	 *   		<li>Reproduction - pass control to reproduction component.</li>
	 *   	</ul>
	 *   </li>
	 *   <li>Insert the result of the operator into the next population.</li>
	 *   <li>Start back at 3. until the next population is full.</li>
	 *   <li>Return the new population.</li>
	 * </ol>
	 * 
	 * <p>
	 * The necessary events trigger life cycle events.
	 * 
	 * @param previousPop the previous population which will undergo 
	 * 				      manipulation to create the next population.
	 * @return the population derived from performing genetic operations on the
	 * 					  previous population.
	 */
	public List<CandidateProgram> generation(final int generationNumber, 
					final List<CandidateProgram> previousPop) {
		// Initialise all variables.
		reversions = 0;
		
		// Inform all listeners that a generation is starting.
		model.getLifeCycleManager().onConfigure();
		model.getLifeCycleManager().onGenerationStart();
		
		// Record the generation start time.
		final long startTime = System.nanoTime();
		
		// Record the generation number in the stats data.
		model.getStatsManager().addGenerationData(GEN_NUMBER, generationNumber);
		
		// Create next population to fill.
		List<CandidateProgram> pop = new ArrayList<CandidateProgram>(popSize);

		do {
			// Perform elitism.
			pop.addAll(elitism.elitism(previousPop));
			
			// Construct a breeding pool.
			final List<CandidateProgram> pool = poolSelection.getPool(previousPop);
			
			// Give parent selector a pool of programs to choose from.
			programSelector.setSelectionPool(pool);
			
			// Fill the population by performing genetic operations.
			while(pop.size() < popSize) {
				// Randomly choose a genetic operator.
				final double random = rng.nextDouble();
				
				if (random < crossoverProbability) {
					// Perform crossover.
					final CandidateProgram[] children = crossover.crossover();
					for (final CandidateProgram c: children) {
						if (pop.size() < popSize) {
							pop.add(c);
						}
					}
				} else if (random < crossoverProbability+mutationProbability) {
					// Perform mutation.
					pop.add(mutation.mutate());
				} else {
					// Perform reproduction.
					pop.add(reproduction.reproduce());
				}
			}
			
			// Request confirmation of generation.
			pop = model.getLifeCycleManager().onGeneration(pop);
			
			// If reverted, increment reversions count.
			if (pop == null) {
				reversions++;
			}
		} while(pop == null);
		
		// Store the stats data from the generation.
		model.getStatsManager().addGenerationData(GEN_REVERSIONS, reversions);
		model.getStatsManager().addGenerationData(GEN_POPULATION, pop);
		model.getStatsManager().addGenerationData(GEN_TIME, (System.nanoTime() - startTime));
		
		// Tell everyone the generation has ended.
		model.getLifeCycleManager().onGenerationEnd();
		
		return pop;
	}
}
