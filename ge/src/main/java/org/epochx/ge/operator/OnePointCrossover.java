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

import static org.epochx.Config.Template.TEMPLATE;
import static org.epochx.RandomSequence.RANDOM_SEQUENCE;

import java.util.List;

import org.epochx.AbstractOperator;
import org.epochx.Config;
import org.epochx.Individual;
import org.epochx.RandomSequence;
import org.epochx.Config.ConfigKey;
import org.epochx.event.ConfigEvent;
import org.epochx.event.EventManager;
import org.epochx.event.Listener;
import org.epochx.event.OperatorEvent.EndOperator;
import org.epochx.ge.Chromosome;
import org.epochx.ge.Codon;
import org.epochx.ge.GEIndividual;

/**
 * This class implements a one-point crossover on two <tt>GEIndividual</tt>s.
 * 
 * <p>
 * The operation is performed on the individual's chromosomes. A random codon
 * position is chosen in both parents' chromosomes and all the codons from that point
 * onwards are exchanged.
 * 
 * @see FixedPointCrossover
 * 
 * @since 2.0
 */
public class OnePointCrossover extends AbstractOperator implements Listener<ConfigEvent> {

	/**
	 * The key for setting and retrieving the probability of this operator being applied
	 */
	public static final ConfigKey<Double> PROBABILITY = new ConfigKey<Double>();
	
	// Configuration settings
	private RandomSequence random;
	private Double probability;

	/**
	 * Constructs a <tt>OnePointCrossover</tt> with control parameters
	 * automatically loaded from the config
	 */
	public OnePointCrossover() {
		this(true);
	}

	/**
	 * Constructs a <tt>OnePointCrossover</tt> with control parameters initially
	 * loaded from the config. If the <tt>autoConfig</tt> argument is set to
	 * <tt>true</tt> then the configuration will be automatically updated when
	 * the config is modified.
	 * 
	 * @param autoConfig whether this operator should automatically update its
	 *        configuration settings from the config
	 */
	public OnePointCrossover(boolean autoConfig) {
		setup();

		if (autoConfig) {
			EventManager.getInstance().add(ConfigEvent.class, this);
		}
	}
	
	/**
	 * Sets up this operator with the appropriate configuration settings.
	 * This method is called whenever a <tt>ConfigEvent</tt> occurs for a
	 * change in any of the following configuration parameters:
	 * <ul>
	 * <li>{@link RandomSequence#RANDOM_SEQUENCE}
	 * <li>{@link #PROBABILITY}
	 * </ul>
	 */
	protected void setup() {
		random = Config.getInstance().get(RANDOM_SEQUENCE);
		probability = Config.getInstance().get(PROBABILITY);
	}

	/**
	 * Receives configuration events and triggers this operator to configure its
	 * parameters if the <tt>ConfigEvent</tt> is for one of its required
	 * parameters.
	 * 
	 * @param event {@inheritDoc}
	 */
	@Override
	public void onEvent(ConfigEvent event) {
		if (event.isKindOf(TEMPLATE, RANDOM_SEQUENCE, PROBABILITY)) {
			setup();
		}
	}

	/**
	 * Performs a one-point crossover operation on the specified parent individuals.
	 * 
	 * <p>
	 * The operation is performed on the programs' chromosomes. A random codon
	 * position is chosen in each of the parent individual's chromosomes and all the 
	 * codons from those points onwards are exchanged.
	 * 
	 * @param event the <tt>EndOperator</tt> event to be filled with information
	 *        about this operation
	 * @param parents an array of two individuals to undergo one-point
	 *        crossover. Both individuals must be instances of <tt>GEIndividual</tt>.
	 * @return an array containing two <tt>GEIndividual</tt>s that are the
	 *         result of the crossover
	 */
	@Override
	public GEIndividual[] perform(EndOperator event, Individual ... parents) {
		GEIndividual parent1 = (GEIndividual) parents[0];
		GEIndividual parent2 = (GEIndividual) parents[1];

		Chromosome parent1Codons = parent1.getChromosome();
		Chromosome parent2Codons = parent2.getChromosome();
		int parent1Length = parent1Codons.length();
		int parent2Length = parent2Codons.length();
		
		// Choose crossover points
		int crossoverPoint1 = random.nextInt(parent1Length);
		int crossoverPoint2 = random.nextInt(parent2Length);

		// Add crossover points to the event
		((OnePointCrossoverEndEvent) event).setCrossoverPoint1(crossoverPoint1);
		((OnePointCrossoverEndEvent) event).setCrossoverPoint2(crossoverPoint2);

		// Make copies of the parents' chromosomes.
		Chromosome child1Codons = parent1Codons.clone();
		Chromosome child2Codons = parent2Codons.clone();

		List<Codon> codonsExchanged1 = child1Codons.removeCodons(crossoverPoint1, parent1Length);
		List<Codon> codonsExchanged2 = child2Codons.removeCodons(crossoverPoint2, parent2Length);

		// Add codon portions into the event
		((OnePointCrossoverEndEvent) event).setExchangedCodons1(codonsExchanged1);
		((OnePointCrossoverEndEvent) event).setExchangedCodons2(codonsExchanged2);

		// Swap over the endings at the crossover points.
		child1Codons.appendCodons(codonsExchanged2);
		child2Codons.appendCodons(codonsExchanged1);

		return new GEIndividual[]{new GEIndividual(child1Codons), new GEIndividual(child2Codons)};
	}
	
	/**
	 * Returns a <tt>OnePointCrossoverEndEvent</tt> with the operator and 
	 * parents set
	 */
	@Override
	protected OnePointCrossoverEndEvent getEndEvent(Individual ... parents) {
		return new OnePointCrossoverEndEvent(this, parents);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * One-point crossover operates on 2 individuals.
	 * 
	 * @return {@inheritDoc}
	 */
	@Override
	public int inputSize() {
		return 2;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double probability() {
		return probability;
	}

	/**
	 * Sets the probability of this operator being selected. If automatic configuration is
	 * enabled then any value set here will be overwritten by the {@link #PROBABILITY} 
	 * configuration setting on the next config event.
	 * 
	 * @param probability the new probability to set
	 */
	public void setProbability(double probability) {
		this.probability = probability;
	}
	
	/**
	 * Returns the random number sequence in use
	 * 
	 * @return the currently set random sequence
	 */
	public RandomSequence getRandomSequence() {
		return random;
	}

	/**
	 * Sets the random number sequence to use. If automatic configuration is
	 * enabled then any value set here will be overwritten by the
	 * {@link RandomSequence#RANDOM_SEQUENCE} configuration setting on the next
	 * config event.
	 * 
	 * @param random the random number generator to set
	 */
	public void setRandomSequence(final RandomSequence random) {
		this.random = random;
	}
}
