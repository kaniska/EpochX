/*
 * Copyright 2007-2013
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
package org.epochx.ge.operator;

import java.util.List;

import org.epochx.core.*;
import org.epochx.ge.GEIndividual;
import org.epochx.ge.model.GEModel;
import org.epochx.life.ConfigListener;
import org.epochx.representation.CandidateProgram;
import org.epochx.stats.*;
import org.epochx.stats.Stats.ExpiryEvent;
import org.epochx.tools.random.RandomNumberGenerator;

/**
 * This class implements a one point crossover on two
 * <code>CandidatePrograms</code>.
 * 
 * <p>
 * The operation is performed on the programs' chromosomes. A random codon
 * position is chosen in both parent programs and all the codons from that point
 * onwards are exchanged.
 */
public class OnePointCrossover implements GECrossover, ConfigListener {

	/**
	 * Requests an <code>Integer</code> which is the point chosen in the first
	 * parent's chromosomes for the one point crossover operation.
	 */
	public static final Stat XO_POINT1 = new AbstractStat(ExpiryEvent.CROSSOVER) {};

	/**
	 * Requests an <code>Integer</code> which is the point chosen in the second
	 * parent's chromosomes for the one point crossover operation.
	 */
	public static final Stat XO_POINT2 = new AbstractStat(ExpiryEvent.CROSSOVER) {};

	/**
	 * Requests a <code>List&lt;Integer&gt;</code> which is the portion of the
	 * first parent program which is being exchanged into the second parent.
	 */
	public static final Stat XO_CODONS1 = new AbstractStat(ExpiryEvent.CROSSOVER) {};

	/**
	 * Requests a <code>List&lt;Integer&gt;</code> which is the portion of the
	 * second parent program which is being exchanged into the first parent.
	 */
	public static final Stat XO_CODONS2 = new AbstractStat(ExpiryEvent.CROSSOVER) {};

	private Evolver evolver;
	
	private Stats stats;
	
	// The random number generator in use.
	private RandomNumberGenerator rng;

	/**
	 * Constructs an instance of <code>FixedPointCrossover</code> with the only
	 * necessary parameter given.
	 * 
	 * @param rng a <code>RandomNumberGenerator</code> used to lead
	 *        non-deterministic behaviour.
	 */
	public OnePointCrossover(final RandomNumberGenerator rng) {
		this((Evolver) null);

		this.rng = rng;
	}

	/**
	 * Constructs a <code>OnePointCrossover</code>.
	 * 
	 * @param model
	 */
	public OnePointCrossover(final Evolver evolver) {
		this.evolver = evolver;
	}

	/*
	 * Configure component with parameters from the model.
	 */
	@Override
	public void configure(Model model) {
		if (model instanceof GEModel) {
			stats = evolver.getStats(model);
			rng = model.getRNG();
		}
	}

	/**
	 * Performs a one point crossover operation on the specified parent
	 * programs.
	 * 
	 * <p>
	 * The operation is performed on the programs' chromosomes. A random codon
	 * position is chosen in <b>both</b> parent programs and all the codons from
	 * that point onwards are exchanged.
	 * 
	 * @param p1 {@inheritDoc}
	 * @param p2 {@inheritDoc}
	 * @return {@inheritDoc}
	 */
	@Override
	public GEIndividual[] crossover(final CandidateProgram p1, final CandidateProgram p2) {
		final GEIndividual parent1 = (GEIndividual) p1;
		final GEIndividual parent2 = (GEIndividual) p2;

		// Choose crossover points.
		final int crossoverPoint1 = rng.nextInt(parent1.getNoCodons());
		final int crossoverPoint2 = rng.nextInt(parent2.getNoCodons());

		// Add crossover points to the stats manager.
		stats.addData(XO_POINT1, crossoverPoint1);
		stats.addData(XO_POINT2, crossoverPoint2);

		// Make copies of the parents.
		final GEIndividual child1 = (GEIndividual) parent1.clone();
		final GEIndividual child2 = (GEIndividual) parent2.clone();

		final List<Integer> part1 = child1.removeCodons(crossoverPoint1, child1.getNoCodons());
		final List<Integer> part2 = child2.removeCodons(crossoverPoint2, child2.getNoCodons());

		// Add codon portions into the stats manager.
		stats.addData(XO_CODONS1, part1);
		stats.addData(XO_CODONS2, part2);

		// Swap over the endings at the crossover points.
		child2.appendCodons(part1);
		child1.appendCodons(part2);

		return new GEIndividual[]{child1, child2};
	}

	/**
	 * Returns the random number generator that this crossover is using or
	 * <code>null</code> if none has been set.
	 * 
	 * @return the rng the currently set random number generator.
	 */
	public RandomNumberGenerator getRNG() {
		return rng;
	}

	/**
	 * Sets the random number generator to use. If a model has been set then
	 * this parameter will be overwritten with the random number generator from
	 * that model on the next configure event.
	 * 
	 * @param rng the random number generator to set.
	 */
	public void setRNG(final RandomNumberGenerator rng) {
		this.rng = rng;
	}
}
