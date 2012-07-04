/*
 * Copyright 2007-2011
 * Lawrence Beadle, Tom Castle and Fernando Otero
 * Licensed under GNU Lesser General Public License
 * 
 * This file is part of EpochX
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
 * The latest version is available from: http:/www.epochx.org
 */

package org.epochx.stgp.stat;

import org.epochx.event.GenerationEvent.EndGeneration;
import org.epochx.event.stat.AbstractStat;

/**
 * A stat that returns the maximum number of terminals in all the program 
 * trees in the population from the previous completed generation. All 
 * individuals in the population must be instances of <tt>STGPIndividual</tt>.
 * 
 * @see GenerationMinimumTerminals
 * @see GenerationMaximumNonTerminals
 */
public class GenerationMaximumTerminals extends AbstractStat<EndGeneration> {

	private int max;

	/**
	 * Constructs a <tt>GenerationMaximumTerminals</tt> stat and registers its
	 * dependencies
	 */
	public GenerationMaximumTerminals() {
		super(GenerationTerminals.class);
	}

	/**
	 * Triggers the generation of an updated value for this stat. Once this stat
	 * has been registered, this method will be called on each
	 * <tt>EndGeneration</tt> event.
	 * 
	 * @param event an object that encapsulates information about the event that
	 *        occurred
	 */
	@Override
	public void onEvent(EndGeneration event) {
		int[] terminals = AbstractStat.get(GenerationTerminals.class).getTerminals();
		max = -1;

		for (int t: terminals) {
			if (t > max) {
				max = t;
			}
		}
	}
	
	/**
	 * Returns the maximum number of terminals in the program trees in the 
	 * previous generation
	 *  
	 * @return the maximum number of terminals in the program trees
	 */
	public int getMaximum() {
		return max;
	}

	/**
	 * Returns a string representation of the value of this stat
	 * 
	 * @return a <tt>String</tt> that represents the value of this stat
	 */
	@Override
	public String toString() {
		return Integer.toString(max);
	}
}