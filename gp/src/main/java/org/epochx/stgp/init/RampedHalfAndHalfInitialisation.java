/*
 * Copyright 2007-2011 Tom Castle & Lawrence Beadle
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
package org.epochx.stgp.init;

import static org.epochx.Population.SIZE;
import static org.epochx.RandomSequence.RANDOM_SEQUENCE;
import static org.epochx.stgp.STGPIndividual.*;
import static org.epochx.stgp.init.RampedHalfAndHalfInitialisation.Method.*;

import java.math.BigInteger;
import java.util.*;

import org.epochx.*;
import org.epochx.Config.ConfigKey;
import org.epochx.epox.Node;
import org.epochx.event.*;
import org.epochx.stgp.STGPIndividual;

/**
 * Initialisation method which produces <tt>STGPIndividual</tt>s with program
 * trees constructed using a combination of grow and full initialisation
 * procedures. Program trees are constructed randomly from the nodes in the
 * syntax, with each node's data-type constraints enforced. The depth setting
 * used is gradually ramped up to the maximum depth parameter setting.
 * 
 * <p>
 * See the {@link #setup()} method documentation for a list of configuration
 * parameters used to control this operator.
 * 
 * @see FullInitialisation
 * @see GrowInitialisation
 */
public class RampedHalfAndHalfInitialisation implements STGPInitialisation, Listener<ConfigEvent> {

	/**
	 * The key for setting and retrieving the smallest maximum depth setting
	 * from which the ramping will begin
	 */
	public static final ConfigKey<Integer> RAMPING_START_DEPTH = new ConfigKey<Integer>();

	// Configuration settings
	private Node[] syntax; // TODO We don't really need to store this
	private RandomSequence random;
	private Class<?> returnType;
	private int populationSize;
	private boolean allowDuplicates;

	// The two halves
	private final GrowInitialisation grow;
	private final FullInitialisation full;

	// The depth limits of each program tree to generate.
	private int endDepth;
	private int startDepth;

	/**
	 * Initialisation method labels
	 */
	public enum Method {
		GROW, FULL;
	}

	/**
	 * Constructs a <tt>RampedHalfAndHalfInitialisation</tt> with control
	 * parameters automatically loaded from the config
	 */
	public RampedHalfAndHalfInitialisation() {
		this(true);
	}

	/**
	 * Constructs a <tt>RampedHalfAndHalfInitialisation</tt> with control
	 * parameters initially loaded from the config. If the <tt>autoConfig</tt>
	 * argument is set to <tt>true</tt> then the configuration will be
	 * automatically updated when the config is modified.
	 * 
	 * @param autoConfig whether this operator should automatically update its
	 *        configuration settings from the config
	 */
	public RampedHalfAndHalfInitialisation(boolean autoConfig) {
		grow = new GrowInitialisation(false);
		full = new FullInitialisation(false);

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
	 * <li>{@link Population#SIZE}
	 * <li>{@link STGPIndividual#SYNTAX}
	 * <li>{@link STGPIndividual#RETURN_TYPE}
	 * <li>{@link STGPIndividual#MAXIMUM_DEPTH}
	 * <li>{@link STGPInitialisation#MAXIMUM_INITIAL_DEPTH}
	 * <li>{@link InitialisationMethod#ALLOW_DUPLICATES} (default: <tt>true</tt>)
	 * <li>{@link #RAMPING_START_DEPTH}
	 * </ul>
	 */
	protected void setup() {
		random = Config.getInstance().get(RANDOM_SEQUENCE);
		populationSize = Config.getInstance().get(SIZE);
		syntax = Config.getInstance().get(SYNTAX);
		returnType = Config.getInstance().get(RETURN_TYPE);
		allowDuplicates = Config.getInstance().get(ALLOW_DUPLICATES, true);

		grow.setRandomSequence(random);
		full.setRandomSequence(random);

		Integer maxDepth = Config.getInstance().get(MAXIMUM_DEPTH);
		Integer maxInitialDepth = Config.getInstance().get(MAXIMUM_INITIAL_DEPTH);
		Integer startDepth = Config.getInstance().get(RAMPING_START_DEPTH);

		if (maxInitialDepth != null && (maxDepth == null || maxInitialDepth < maxDepth)) {
			endDepth = maxInitialDepth;
		}
		if (startDepth != null) {
			this.startDepth = startDepth;
		} else {
			this.startDepth = 0;
		}
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
		if (event.isKindOf(RANDOM_SEQUENCE, SIZE, SYNTAX, RETURN_TYPE, MAXIMUM_INITIAL_DEPTH, MAXIMUM_DEPTH, ALLOW_DUPLICATES, RAMPING_START_DEPTH)) {
			setup();
		}

		// These could be expensive so only do them when we really have to
		if (event.isKindOf(RETURN_TYPE)) {
			grow.setReturnType(returnType);
			full.setReturnType(returnType);
		}
		if (event.isKindOf(SYNTAX)) {
			grow.setSyntax(syntax);
			full.setSyntax(syntax);
		}
	}

	/**
	 * Creates a population of new <tt>STGPIndividuals</tt> from the
	 * <tt>Node</tt>s provided by the {@link STGPIndividual#SYNTAX} config
	 * parameter. Each individual is created with either a grow or a full
	 * initialisation method, which is alternated. The size of the population
	 * will be equal to the {@link Population#SIZE} config parameter. If the
	 * {@link InitialisationMethod#ALLOW_DUPLICATES} config parameter is set to
	 * <tt>false</tt> then the individuals in the population will be unique
	 * according to their <tt>equals</tt> methods. By default, duplicates are
	 * allowed.
	 * 
	 * <p>
	 * The use of grow and full initialisation procedures are alternated, but
	 * the population may not be made up of programs that alternate between
	 * being full and grown. If duplicates are disabled, then any duplicates
	 * that occur will be discarded but the use of grow/full will continue to
	 * alternate regardless of the programs discarded.
	 * 
	 * @return a population of <code>STGPIndividual</code> objects
	 */
	@Override
	public Population createPopulation() {
		EventManager.getInstance().fire(new InitialisationEvent.StartInitialisation());

		if (endDepth < startDepth) {
			throw new IllegalStateException("End depth must be greater than the start depth.");
		}

		Population population = new Population();

		Method[] method = new Method[populationSize];
		int[] programsPerDepth = programsPerDepth();

		int popIndex = 0;
		boolean growNext = true;
		for (int depth = startDepth; depth <= endDepth; depth++) {
			int noPrograms = programsPerDepth[depth - startDepth];

			for (int i = 0; i < noPrograms; i++) {
				STGPIndividual program;

				do {
					method[popIndex] = growNext ? GROW : FULL;
					if (growNext) {
						grow.setMaximumDepth(depth);
						program = grow.createIndividual();
					} else {
						full.setDepth(depth);
						program = full.createIndividual();
					}
					/*
					 * The effect is that if its a duplicate then will use other
					 * method next - this is deliberate because full may have
					 * less possible programs for a given depth.
					 */
					growNext = !growNext;
				} while (!allowDuplicates && population.contains(program));

				population.add(program);
				popIndex++;
			}
		}

		EventManager.getInstance().fire(new RampedHalfAndHalfEndEvent(population, method));

		return population;
	}

	/**
	 * Calculates the number of programs that should be generated at each depth
	 * level. Each depth level between start depth and end depth is (inclusive),
	 * is allocated an equal number of the total population size of programs. If
	 * the population size does not divide evenly then all remainders are
	 * allocated to the end depth level. If insufficient trees are possible at
	 * one depth level, then that depth's remaining allocation is granted to the
	 * next deepest level.
	 * 
	 * <p>
	 * The sum of the resultant array's elements is guarenteed to equal the
	 * population size, unless duplicates are disabled and it is impossible to
	 * create sufficient programs between the start and end depth settings. In
	 * this case an <tt>IllegalStateException</tt> will be thrown.
	 * 
	 * @return an array of the number of individuals at each depth, where the
	 *         element at index 0 refers to programs with the start depth and
	 *         the
	 *         element at index length-1 refers to programs with the end depth
	 */
	protected int[] programsPerDepth() {
		int noDepths = endDepth - startDepth + 1;
		int[] noPrograms = new int[noDepths];

		int programsPerDepth = populationSize / noDepths;
		Arrays.fill(noPrograms, programsPerDepth);

		// Add remainders, missed out by rounding, to largest depth
		int remainder = populationSize % noDepths;
		noPrograms[noPrograms.length - 1] += remainder;

		if (!allowDuplicates) {
			int cumulative = 0;
			for (int i = startDepth; i <= endDepth; i++) {
				int target = noPrograms[i - startDepth];
				BigInteger targetBI = BigInteger.valueOf(target);
				if (!grow.sufficientVarieties(i, returnType, targetBI)) {
					final BigInteger noPossibleBI = grow.varieties(i, returnType);

					// Must fit into an int because target was an int.
					int noPossible = noPossibleBI.intValue();

					// Exclude lower depths because will already be in pop.
					noPossible -= cumulative;

					// Update cumulative.
					cumulative += noPossible;
					int shortfall = target - noPossible;

					// Move the shortfall to the next depth if there is one.
					if (i + 1 <= endDepth) {
						noPrograms[i + 1 - startDepth] += shortfall;
						noPrograms[i - startDepth] -= shortfall;
					} else {
						throw new IllegalStateException(
								"Impossible to create sufficient programs inside depth parameters");
					}
				} else {
					// Assume that if we can create enough programs at a depth,
					// then we can at greater depths too.
					break;
				}
			}
		}

		return noPrograms;
	}

	/**
	 * Constructs a new <tt>STGPIndividual</tt> instance with a program tree
	 * composed of nodes provided by the {@link STGPIndividual#SYNTAX} config
	 * parameter. A grow or a full initialisation method is used, select at
	 * random
	 * 
	 * @return a new individual
	 */
	@Override
	public STGPIndividual createIndividual() {
		if (random.nextBoolean()) {
			return grow.createIndividual();
		} else {
			return full.createIndividual();
		}
	}

	/**
	 * Returns whether or not duplicates are currently allowed in generated
	 * populations
	 * 
	 * @return <tt>true</tt> if duplicates are currently allowed in populations
	 *         generated by the <tt>createPopulation</tt> method and
	 *         <tt>false</tt> otherwise
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
	public void setRandomSequence(RandomSequence random) {
		this.random = random;

		grow.setRandomSequence(random);
		full.setRandomSequence(random);
	}

	/**
	 * Returns the array of nodes in the available syntax. Program trees are
	 * generated using the nodes in this array.
	 * 
	 * @return an array of the nodes in the syntax
	 */
	public Node[] getSyntax() {
		return syntax;
	}

	/**
	 * Sets the array of nodes to generate program trees from. If automatic
	 * configuration is enabled then any value set here will be overwritten by
	 * the {@link STGPIndividual#SYNTAX} configuration setting on the next
	 * config event.
	 * 
	 * @param syntax an array of nodes to generate new program trees from
	 */
	public void setSyntax(Node[] syntax) {
		this.syntax = syntax;

		grow.setSyntax(syntax);
		full.setSyntax(syntax);
	}

	/**
	 * Returns the required data-type of the return for program trees
	 * generated with this initialisation method
	 * 
	 * @return the return type of the program trees generated
	 */
	public Class<?> getReturnType() {
		return returnType;
	}

	/**
	 * Sets the required data-type of the program trees generated. If
	 * automatic configuration is enabled then any value set here will be
	 * overwritten by the {@link STGPIndividual#RETURN_TYPE} configuration
	 * setting on the next config event.
	 * 
	 * @param returnType the data-type of the generated programs
	 */
	public void setReturnType(Class<?> returnType) {
		this.returnType = returnType;

		grow.setReturnType(returnType);
		full.setReturnType(returnType);
	}

	/**
	 * Returns the number of individuals to be generated in a population created
	 * by the <tt>createPopulation</tt> method
	 * 
	 * @return the size of the populations generated
	 */
	public int getPopulationSize() {
		return populationSize;
	}

	/**
	 * Sets the number of individuals to be generated in a population created
	 * by the <tt>createPopulation</tt> method. If automatic configuration is
	 * enabled then any value set here will be overwritten by the
	 * {@link Population#SIZE} configuration setting on the next config event.
	 * 
	 * @param size the size of the populations generated
	 */
	public void setPopulationSize(int size) {
		this.populationSize = size;
	}

	/**
	 * Returns the depth that the maximum depth is ramped up to
	 * 
	 * @return the maximum setting the depth will be ramped to
	 */
	public int getEndDepth() {
		return endDepth;
	}

	/**
	 * Sets the depth that the maximum depth will be ramped up to when a
	 * population is created with the <tt>createPopulation</tt> method. If
	 * automatic configuration is enabled, then any
	 * value set here will be overwritten by the
	 * {@link STGPInitialisation#MAXIMUM_INITIAL_DEPTH} configuration setting on
	 * the next config event, or the {@link STGPIndividual#MAXIMUM_DEPTH}
	 * setting if no initial maximum depth is set.
	 * 
	 * @param endDepth the maximum setting to ramp the depth to
	 */
	public void setEndDepth(int endDepth) {
		this.endDepth = endDepth;
	}

	/**
	 * Returns the depth that the maximum depth is ramped up from
	 * 
	 * @return the initial maximum depth setting before being ramped
	 */
	public int getStartDepth() {
		return startDepth;
	}

	/**
	 * Sets the depth that the maximum depth will be ramped up from when a
	 * population is created with the <tt>createPopulation</tt> method. If
	 * automatic configuration is enabled, then any
	 * value set here will be overwritten by the
	 * {@link RampedHalfAndHalfInitialisation#RAMPING_START_DEPTH} configuration
	 * setting on the next config event.
	 * 
	 * @param startDepth the initial maximum depth setting before being ramped
	 */
	public void setStartDepth(int startDepth) {
		this.startDepth = startDepth;
	}
}
