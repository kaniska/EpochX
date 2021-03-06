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
package org.epochx.ge.init;

import static org.epochx.Config.Template.TEMPLATE;
import static org.epochx.Population.SIZE;
import static org.epochx.ge.Chromosome.MAXIMUM_LENGTH;

import org.epochx.*;
import org.epochx.event.*;
import org.epochx.ge.*;

/**
 * Initialisation method which produces <code>GEIndividual</code>s with fixed length
 * chromosomes. 
 * 
 * <p>
 * See the {@link #setup()} method documentation for a list of configuration
 * parameters used to control this operator.
 * 
 * @see Full
 * @see Grow
 * @see RampedHalfAndHalf
 * 
 * @since 2.0
 */
public class FixedLength implements GEInitialisation, Listener<ConfigEvent> {

	// Configuration settings
	private Integer populationSize;
	private Integer chromosomeLength;
	private Boolean allowDuplicates;

	/**
	 * Constructs a <code>FixedLengthInitialisation</code> with control parameters
	 * automatically loaded from the config
	 */
	public FixedLength() {
		this(true);
	}

	/**
	 * Constructs a <code>FixedLengthInitialisation</code> with control parameters
	 * initially loaded from the config. If the <code>autoConfig</code> argument is
	 * set to <code>true</code> then the configuration will be automatically updated
	 * when the config is modified.
	 * 
	 * @param autoConfig whether this operator should automatically update its
	 *        configuration settings from the config
	 */
	public FixedLength(boolean autoConfig) {
		// Default config values
		allowDuplicates = true;
		
		setup();

		if (autoConfig) {
			EventManager.getInstance().add(ConfigEvent.class, this);
		}
	}

	/**
	 * Sets up this operator with the appropriate configuration settings.
	 * This method is called whenever a <code>ConfigEvent</code> occurs for a
	 * change in any of the following configuration parameters:
	 * <ul>
	 * <li>{@link Population#SIZE}
	 * <li>{@link GenericChromosome#MAXIMUM_LENGTH}
	 * <li>{@link InitialisationMethod#ALLOW_DUPLICATES} (default: <code>true</code>)
	 * </ul>
	 */
	protected void setup() {
		populationSize = Config.getInstance().get(SIZE);
		allowDuplicates = Config.getInstance().get(ALLOW_DUPLICATES, allowDuplicates);
		chromosomeLength = Config.getInstance().get(MAXIMUM_LENGTH);
	}

	/**
	 * Receives configuration events and triggers this operator to reconfigure
	 * if the <code>ConfigEvent</code> is for one of its required parameters
	 * 
	 * @param event {@inheritDoc}
	 */
	@Override
	public void onEvent(ConfigEvent event) {
		if (event.isKindOf(TEMPLATE, SIZE, ALLOW_DUPLICATES, MAXIMUM_LENGTH)) {
			setup();
		}
	}

	/**
	 * Creates a population of new <code>GEIndividuals</code>. Each individual is 
	 * created by a call to the<code>createIndividual</code> method. The size of the
	 * population will be equal to the {@link Population#SIZE} config parameter.
	 * If the {@link InitialisationMethod#ALLOW_DUPLICATES} config parameter is 
	 * set to <code>false</code> then the individuals in the population will be 
	 * unique according to their <code>equals</code> methods. By default, duplicates
	 * are allowed.
	 * 
	 * @return a population of <code>GEIndividual</code> objects
	 */
	@Override
	public Population createPopulation() {
		EventManager.getInstance().fire(new InitialisationEvent.StartInitialisation());

		Population population = new Population();

		for (int i = 0; i < populationSize; i++) {
			GEIndividual individual;

			do {
				individual = createIndividual();
			} while (!allowDuplicates && population.contains(individual));

			population.add(individual);
		}

		EventManager.getInstance().fire(new InitialisationEvent.EndInitialisation(population));

		return population;
	}

	/**
	 * Constructs a new <code>GEIndividual</code> instance with a fixed length 
	 * chromosome, as determined by the {@link Chromosome#MAXIMUM_LENGTH}
	 * config parameter.
	 * 
	 * @return a new individual with a fixed length chromosome
	 */
	@Override
	public GEIndividual createIndividual() {
		if (chromosomeLength < 1) {
			throw new IllegalStateException("chromosome length must be 1 or greater");
		}

		Chromosome chromosome = new Chromosome();
		for (int i = 0; i < chromosomeLength; i++) {
			chromosome.extend();
		}

		return new GEIndividual(chromosome);
	}

	/**
	 * Returns the length of chromosomes that all individuals are generated with
	 * 
	 * @return the length of generated chromosomes
	 */
	public int getChromosomeLength() {
		return chromosomeLength;
	}

	/**
	 * Sets the length of chromosomes for individuals. If automatic 
	 * configuration is enabled then any value set here will be overwritten by 
	 * the {@link GenericChromosome#MAXIMUM_LENGTH} configuration setting on the next 
	 * config event.
	 * 
	 * @param chromosomeLength the fixed length to use for chromosomes
	 */
	public void setChromosomeLength(int chromosomeLength) {
		this.chromosomeLength = chromosomeLength;
	}

	/**
	 * Returns whether or not duplicates are currently allowed in generated
	 * populations
	 * 
	 * @return <code>true</code> if duplicates are currently allowed in populations
	 *         generated by the <code>createPopulation</code> method and
	 *         <code>false</code> otherwise
	 */
	public boolean isDuplicatesEnabled() {
		return allowDuplicates;
	}

	/**
	 * Sets whether duplicates should be allowed in populations that are
	 * generated. If automatic configuration is enabled then any value set here
	 * will be overwritten by the {@link InitialisationMethod#ALLOW_DUPLICATES}
	 * configuration setting on the next config event.
	 * 
	 * @param allowDuplicates whether duplicates should be allowed in
	 *        populations that are generated
	 */
	public void setDuplicatesEnabled(boolean allowDuplicates) {
		this.allowDuplicates = allowDuplicates;
	}

	/**
	 * Returns the number of individuals to be generated in a population created
	 * by the <code>createPopulation</code> method
	 * 
	 * @return the size of the populations generated
	 */
	public int getPopulationSize() {
		return populationSize;
	}

	/**
	 * Sets the number of individuals to be generated in a population created
	 * by the <code>createPopulation</code> method. If automatic configuration is
	 * enabled thenAny value set here will be overwritten by the
	 * {@link Population#SIZE} configuration setting on the next config event.
	 * 
	 * @param size the size of the populations generated
	 */
	public void setPopulationSize(int size) {
		this.populationSize = size;
	}
}
