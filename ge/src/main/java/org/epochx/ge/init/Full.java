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
import static org.epochx.RandomSequence.RANDOM_SEQUENCE;
import static org.epochx.ge.Codon.MAXIMUM_VALUE;
import static org.epochx.ge.Codon.MINIMUM_VALUE;
import static org.epochx.ge.GEIndividual.MAXIMUM_DEPTH;
import static org.epochx.grammar.Grammar.GRAMMAR;
import static org.epochx.ge.CodonFactory.CODON_FACTORY;

import java.util.ArrayList;
import java.util.List;

import org.epochx.Config;
import org.epochx.InitialisationMethod;
import org.epochx.Population;
import org.epochx.RandomSequence;
import org.epochx.event.ConfigEvent;
import org.epochx.event.EventManager;
import org.epochx.event.InitialisationEvent;
import org.epochx.event.Listener;
import org.epochx.ge.Chromosome;
import org.epochx.ge.Codon;
import org.epochx.ge.CodonFactory;
import org.epochx.ge.GEIndividual;
import org.epochx.grammar.Grammar;
import org.epochx.grammar.GrammarNode;
import org.epochx.grammar.GrammarProduction;
import org.epochx.grammar.GrammarRule;

/**
 * Initialisation method which produces <code>GEIndividual</code>s with chromosomes
 * that map to full parse trees of a specified depth. Since the initialisation
 * is tied to the parse tree, an internal mapping is used which is equivalent
 * to a depth-first mapping.
 * 
 * <p>
 * See the {@link #setup()} method documentation for a list of configuration
 * parameters used to control this operator.
 * 
 * @see FixedLength
 * @see Grow
 * @see RampedHalfAndHalf
 * 
 * @since 2.0
 */
public class Full implements GEInitialisation, Listener<ConfigEvent> {

	// Configuration settings
	private RandomSequence random;
	private Grammar grammar;
	private Integer populationSize;
	private Integer depth;
	private Long maxCodonValue;
	private Long minCodonValue;
	private Boolean allowDuplicates;
	private CodonFactory codonFactory;

	/**
	 * Constructs a <code>FullInitialisation</code> with control parameters
	 * automatically loaded from the config
	 */
	public Full() {
		this(true);
	}

	/**
	 * Constructs a <code>FullInitialisation</code> with control parameters
	 * initially loaded from the config. If the <code>autoConfig</code> argument is
	 * set to <code>true</code> then the configuration will be automatically updated
	 * when the config is modified.
	 * 
	 * @param autoConfig whether this operator should automatically update its
	 *        configuration settings from the config
	 */
	public Full(boolean autoConfig) {
		// Default config values
		allowDuplicates = true;
		maxCodonValue = Long.MAX_VALUE;
		minCodonValue = 0L;
		
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
	 * <li>{@link RandomSequence#RANDOM_SEQUENCE}
	 * <li>{@link Population#SIZE}
	 * <li>{@link InitialisationMethod#ALLOW_DUPLICATES} (default: <code>true</code>)
	 * <li>{@link Grammar#GRAMMAR}
	 * <li>{@link Codon#MAXIMUM_VALUE} (default: <code>Long.MAXIMUM_VALUE</code>)
	 * <li>{@link Codon#MINIMUM_VALUE} (default: <code>0</code>)
	 * <li>{@link GEIndividual#MAXIMUM_DEPTH}
	 * <li>{@link CodonFactory#CODON_FACTORY}
	 * </ul>
	 */
	protected void setup() {
		random = Config.getInstance().get(RANDOM_SEQUENCE);
		populationSize = Config.getInstance().get(SIZE);
		allowDuplicates = Config.getInstance().get(ALLOW_DUPLICATES, allowDuplicates);
		grammar = Config.getInstance().get(GRAMMAR);
		maxCodonValue = Config.getInstance().get(MAXIMUM_VALUE, maxCodonValue);
		minCodonValue = Config.getInstance().get(MINIMUM_VALUE, minCodonValue);
		depth = Config.getInstance().get(MAXIMUM_DEPTH);
		codonFactory = Config.getInstance().get(CodonFactory.CODON_FACTORY);
	}

	/**
	 * Receives configuration events and triggers this operator to reconfigure
	 * if the <code>ConfigEvent</code> is for one of its required parameters
	 * 
	 * @param event {@inheritDoc}
	 */
	@Override
	public void onEvent(ConfigEvent event) {
		if (event.isKindOf(TEMPLATE, RANDOM_SEQUENCE, SIZE, ALLOW_DUPLICATES, GRAMMAR, MAXIMUM_VALUE, MINIMUM_VALUE, MAXIMUM_DEPTH, CODON_FACTORY)) {
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
	 * Constructs a new <code>GEIndividual</code> with a sequence of
	 * codons that map to a full derivation tree on the currently set grammar
	 * 
	 * @return a new <code>GEIndividual</code> instance
	 */
	@Override
	public GEIndividual createIndividual() {
		if (grammar == null) {
			throw new IllegalStateException("no grammar has been set");
		}

		GrammarRule start = grammar.getStartRule();

		// Determine the minimum depth possible for a valid program
		int minDepth = start.getMinDepth();
		if (minDepth > depth) {
			throw new IllegalStateException("no possible programs within given max depth parameter for this grammar");
		}

		Chromosome codons = new Chromosome();

		// Fill in the list of codons with reference to the grammar
		fillCodons(codons, start, 0, depth);

		return new GEIndividual(codons);
	}

	/*
	 * Constructs a full parse tree by making appropriate production choices
	 * and then filling in a randomly selected codon that matches the production
	 * choice.
	 */
	private void fillCodons(Chromosome codons, GrammarNode rule, int currentDepth, int maxDepth) {
		if (rule instanceof GrammarRule) {
			GrammarRule nt = (GrammarRule) rule;

			// Check that there is more than one production
			int productionIndex = 0;
			int noProductions = nt.getNoProductions();
			if (noProductions > 1) {
				List<Integer> validProductions = validProductions(nt.getProductions(), maxDepth - currentDepth - 1);

				// Choose a production randomly
				int chosenProduction = random.nextInt(validProductions.size());
				productionIndex = validProductions.get(chosenProduction);

				// Scale the production index up to get our new codon
				long codonValue = scaleUp(productionIndex, noProductions);

				codons.appendCodon(codonFactory.codon(codonValue));
			}

			// Drop down the tree at this production
			GrammarProduction p = nt.getProduction(productionIndex);

			List<GrammarNode> symbols = p.getGrammarNodes();
			for (GrammarNode s: symbols) {
				fillCodons(codons, s, currentDepth + 1, maxDepth);
			}
		}
	}

	/*
	 * Helper method for fillCodons that determines which of a set of
	 * productions are valid choices to meet the depth constraints. It returns
	 * a list of indices for the valid productions.
	 */
	private List<Integer> validProductions(List<GrammarProduction> grammarProductions, int maxDepth) {
		List<Integer> validRecursive = new ArrayList<Integer>();
		List<Integer> validAll = new ArrayList<Integer>();

		for (int i = 0; i < grammarProductions.size(); i++) {
			GrammarProduction p = grammarProductions.get(i);

			if (p.getMinDepth() <= maxDepth) {
				validAll.add(i);

				if (p.isRecursive()) {
					validRecursive.add(i);
				}
			}
		}

		return validRecursive.isEmpty() ? validAll : validRecursive;
	}

	/*
	 * Scales a production choice up to a random number inside the codon size
	 * limits, while maintaining the modulo of the number
	 */
	private long scaleUp(int productionIndex, int noProductions) {
		//TODO This is the same as the one used in GrowInitialisation - should be moved to utilities class
		long range = maxCodonValue - minCodonValue;
		long value = random.nextLong(range - noProductions);
		value += minCodonValue;

		// Comparing separate index count saves us %ing large values
		long index = value % noProductions;
		while ((index % noProductions) != productionIndex) {
			value++;
			index++;
		}

		return value;
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
	}

	/**
	 * Returns the grammar that the <code>GEIndividual</code>s will satisfy with
	 * full program trees
	 * 
	 * @return the currently set grammar
	 */
	public Grammar getGrammar() {
		return grammar;
	}

	/**
	 * Sets the grammar to be satisfied by the full parse trees of the new
	 * <code>GEIndividual</code>s. If automatic configuration is enabled then any
	 * value set here will be overwritten by the {@link Grammar#GRAMMAR}
	 * configuration setting on the next config event.
	 * 
	 * @param grammar the grammar to set
	 */
	public void setGrammar(Grammar grammar) {
		this.grammar = grammar;
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
	 * enabled then any value set here will be overwritten by the
	 * {@link Population#SIZE} configuration setting on the next config event.
	 * 
	 * @param size the size of the populations generated
	 */
	public void setPopulationSize(int size) {
		this.populationSize = size;
	}

	/**
	 * Returns the depth of the parse trees generated with this initialisation
	 * method
	 * 
	 * @return the depth of the parse trees constructed
	 */
	public int getDepth() {
		return depth;
	}

	/**
	 * Sets the depth of the parse trees created by the
	 * <code>createIndividual</code> method. If automatic configuration is enabled
	 * then any value set here will be overwritten by the
	 * {@link GEIndividual#MAXIMUM_DEPTH} configuration setting on
	 * the next config event.
	 * 
	 * @param depth the depth of all parse trees generated
	 */
	public void setDepth(int depth) {
		this.depth = depth;
	}

	/**
	 * Returns the maximum value that codons chosen by this initialiser are
	 * allowed to have
	 * 
	 * @return the maximum value of codons selected by this initialiser
	 */
	public long getMaximimCodonValue() {
		return maxCodonValue;
	}

	/**
	 * Sets the maximum value that codons chosen by this initialiser are
	 * allowed to have. If automatic configuration is enabled
	 * then any value set here will be overwritten by the
	 * {@link Codon#MAXIMUM_VALUE} configuration setting on the next config
	 * event.
	 * 
	 * @param maxCodonValue the maximum value for codons selected by this
	 *        initialiser
	 */
	public void setMaximumCodonValue(long maxCodonValue) {
		this.maxCodonValue = maxCodonValue;
	}
	
	/**
	 * Returns the maximum value that codons chosen by this initialiser are
	 * allowed to have
	 * 
	 * @return the maximum value of codons selected by this initialiser
	 */
	public long getMinimumCodonValue() {
		return minCodonValue;
	}

	/**
	 * Sets the maximum value that codons chosen by this initialiser are
	 * allowed to have. If automatic configuration is enabled
	 * then any value set here will be overwritten by the
	 * {@link Codon#MAXIMUM_VALUE} configuration setting on the next config
	 * event.
	 * 
	 * @param minCodonValue the maximum value for codons selected by this
	 *        initialiser
	 */
	public void setMinimumCodonValue(long minCodonValue) {
		this.minCodonValue = minCodonValue;
	}
	
	/**
	 * Returns the <code>CodonFactory</code> currently in use
	 * 
	 * @return the codon factory being used to generate new codons
	 */
	public CodonFactory getCodonFactory() {
		return codonFactory;
	}

	/**
	 * Sets the <code>CodonFactory</code> this initialiser will use to generate
	 * new codon instances. If automatic configuration is enabled
	 * then any value set here will be overwritten by the
	 * {@link CodonFactory#CODON_FACTORY} configuration setting on the next config
	 * event.
	 * 
	 * @param codonFactory the codon factory to use to generate new codons
	 */
	public void setCodonFactory(CodonFactory codonFactory) {
		this.codonFactory = codonFactory;
	}
}
