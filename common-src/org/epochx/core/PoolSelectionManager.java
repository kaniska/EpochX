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

import static org.epochx.stats.StatField.POOL_REVERSIONS;

import java.util.List;

import org.epochx.life.*;
import org.epochx.model.Model;
import org.epochx.op.PoolSelector;
import org.epochx.representation.CandidateProgram;
import org.epochx.stats.StatsManager;

/**
 * The task of pool selection is the selection of a subset of programs from a 
 * larger population based upon some preference (typically a fitness bias, 
 * although sometimes merely randomly). In the EpochX algorithm, it is from a 
 * pool such as this that programs will be selected to undergo the genetic 
 * operators.
 * 
 * <p>
 * The actual operation of selecting a pool of programs will be performed by an
 * implementation of <code>PoolSelector</code> which is obtained from the 
 * <code>Model</code>.
 * 
 * <p>
 * Use of the pool selection operation will generate the following events:
 * 
 * <table>
 *     <tr>
 *         <th>Event</th>
 *         <th>Revert</th>
 *         <th>Modify</th>
 *         <th>Raised when?</th>
 *     </tr>
 *     <tr>
 *         <td>onPoolSelectionStart</td>
 *         <td>no</td>
 *         <td>no</td>
 *         <td>Before the selection operation is attempted for the first time.
 *         </td>
 *     </tr>
 *     <tr>
 *         <td>onPoolSelection</td>
 *         <td><strong>yes</strong></td>
 *         <td><strong>yes</strong></td>
 *         <td>Immediately after a pool is selected, giving the listener the
 *         opportunity to request a revert which will cause a re-selection of 
 *         the pool and this event to be raised again.</td>
 *     </tr>
 *     <tr>
 *         <td>onPoolSelectionEnd</td>
 *         <td>no</td>
 *         <td>no</td>
 *         <td>After the selection operation has been completed and accepted.
 *         </td>
 *     </tr>
 * </table>
 * 
 * @see PoolSelector
 */
public class PoolSelectionManager {
	
	// The controlling model.
	private final Model model;
	
	// The pool selector to use to generate the breeding pool.
	private PoolSelector poolSelector;
	
	// The number of programs that make up a breeding pool.
	private int poolSize;
	
	// The number of times the pool selection was rejected.
	private int reversions;
	
	/**
	 * Constructs an instance of PoolSelectionManager which will setup the 
	 * breeding pool selection operation.
	 * 
	 * @param model the Model which defines the PoolSelector operator and any
	 * 				other control parameters.
	 * 
	 * @see PoolSelector
	 */
	public PoolSelectionManager(final Model model) {
		this.model = model;
		
		// Initialise parameters.
		initialise();
		
		// Re-initialise each generation.
		LifeCycleManager.getLifeCycleManager().addGenerationListener(new GenerationAdapter() {
			@Override
			public void onGenerationStart() {
				initialise();
			}
		});
	}
	
	/*
	 * Initialises PoolSelectionManager, in particular all parameters from the 
	 * model should be refreshed in case they've changed since the last call.
	 */
	private void initialise() {
		poolSize = model.getPoolSize();
		poolSelector = model.getPoolSelector();
	}
	
	/**
	 * Selects a pool of <code>CandidatePrograms</code> by calling the 
	 * <code>getPool</code> method on the PoolSelector retrieved from the model
	 * given at construction. The size of the program pool requested from the 
	 * pool selector will also be obtained from this model.
	 * 
	 * <p>
	 * The returned breeding pool may be a subset of the given population, but
	 * the size of the returned list may equally be larger than the given 
	 * population, for example, if the pool contains duplicates.
	 * 
	 * @param pop the population of programs from which the pool will be 
	 * 			  selected.
	 * @return a list of <code>CandidatePrograms</code> to be used as a 
	 * breeding pool.
	 */
	public List<CandidateProgram> getPool(List<CandidateProgram> pop) {
		// Inform all listeners that pool selection is starting.
		LifeCycleManager.getLifeCycleManager().onPoolSelectionStart();
		
		// Reset the number of reversions.
		reversions = 0;
		
		List<CandidateProgram> pool = null;
		do {
			// Perform pool selection.
			pool = poolSelector.getPool(pop, poolSize);
			
			// Allow life cycle listener to confirm or modify.
			pool = LifeCycleManager.getLifeCycleManager().onPoolSelection(pool);
			
			// If reverted then increment reversion counter.
			if (pool == null) {
				reversions++;
			}
		} while(pool == null);
		
		// Store the stats from the pool selection.
		StatsManager.getStatsManager().addGenerationData(POOL_REVERSIONS, reversions);
		
		// Inform all listeners that pool selection has ended.
		LifeCycleManager.getLifeCycleManager().onPoolSelectionEnd();
		
		return pool;
	}

}
