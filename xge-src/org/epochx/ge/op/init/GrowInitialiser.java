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
 * The latest version is available from: http://www.epochx.org
 */
package org.epochx.ge.op.init;

import java.util.*;

import org.epochx.ge.model.GEModel;
import org.epochx.ge.representation.GECandidateProgram;
import org.epochx.op.ConfigOperator;
import org.epochx.representation.CandidateProgram;
import org.epochx.tools.grammar.*;
import org.epochx.tools.random.RandomNumberGenerator;

/**
 * Initialisation implementation which constructs each program's chromosome
 * while ensuring that the parse tree it maps to is under the initial maximum
 * depth limit. Each program generated with this initialiser will have a parse
 * tree of a different depth. The depth of the parse tree is determined by a
 * depth first mapping. Each codon is generated by randomly choosing a value up
 * to the maximum codon value, which will provide the necessary production
 * choice to construct the parse tree within the depth limitation.
 * 
 * <p>
 * If a model is provided then the following parameters are loaded upon every
 * configure event:
 * 
 * <ul>
 * <li>population size</li>
 * <li>random number generator</li>
 * <li>grammar</li>
 * <li>maximum initial depth</li>
 * <li>maximum codon value</li>
 * </ul>
 * 
 * <p>
 * If the <code>getModel</code> method returns <code>null</code> then no model
 * is set and whatever static parameters have been set as parameters to the
 * constructor or using the standard accessor methods will be used. If any
 * compulsory parameters remain unset when the initialiser is requested to
 * generate new programs, then an <code>IllegalStateException</code> will be
 * thrown.
 * 
 * @see FixedLengthInitialiser
 * @see FullInitialiser
 * @see RampedHalfAndHalfInitialiser
 */
public class GrowInitialiser extends ConfigOperator<GEModel> implements GEInitialiser {

	private RandomNumberGenerator rng;

	// The grammar each program's parse tree must satisfy.
	private Grammar grammar;

	// The size of the populations to construct.
	private int popSize;

	// The depth of every program tree to generate.
	private int maxDepth;

	// The maximum value a codon may have.
	private int maxCodonValue;

	// Whether programs must be unique in generated populations.
	private boolean acceptDuplicates;

	/**
	 * Constructs a <code>GrowInitialiser</code> with all the necessary
	 * parameters given.
	 */
	public GrowInitialiser(final RandomNumberGenerator rng,
			final Grammar grammar, final int popSize, final int maxDepth,
			final int maxCodonValue, final boolean acceptDuplicates) {
		this(null, acceptDuplicates);
		
		this.rng = rng;
		this.grammar = grammar;
		this.popSize = popSize;
		this.maxDepth = maxDepth;
		this.maxCodonValue = maxCodonValue;
	}

	/**
	 * Constructs a <code>GrowInitialiser</code> with the necessary parameters
	 * loaded from the given model. The parameters are reloaded on configure
	 * events. Duplicate programs are allowed in the populations that are
	 * constructed.
	 * 
	 * @param model the <code>GEModel</code> instance from which the necessary
	 *        parameters should be loaded.
	 */
	public GrowInitialiser(final GEModel model) {
		this(model, true);
	}

	/**
	 * Constructs a <code>GrowInitialiser</code> with the necessary parameters
	 * loaded from the given model. The parameters are reloaded on configure
	 * events.
	 * 
	 * @param model the <code>GEModel</code> instance from which the necessary
	 *        parameters should be loaded.
	 * @param acceptDuplicates whether duplicates should be allowed in the
	 *        populations that are generated.
	 */
	public GrowInitialiser(final GEModel model, final boolean acceptDuplicates) {
		super(model);
		
		this.acceptDuplicates = acceptDuplicates;
	}

	/*
	 * Configure component with parameters from the model.
	 */
	@Override
	public void onConfigure() {
		rng = getModel().getRNG();
		grammar = getModel().getGrammar();
		popSize = getModel().getPopulationSize();
		maxDepth = getModel().getMaxInitialDepth();
		maxCodonValue = getModel().getMaxCodonSize();
	}

	/**
	 * Generates a population of new <code>GECandidatePrograms</code>
	 * constructed with a random sequence of codons such that they map to a
	 * parse tree of a depth at most equal to the max depth property. The size
	 * of the population will be equal to the population size attribute. All
	 * programs in the population are only guarenteed to be unique (as defined
	 * by the <code>equals</code> method on <code>GECandidateProgram</code>) if
	 * the <code>isDuplicatesEnabled</code> method returns <code>true</code>.
	 * 
	 * @return A <code>List</code> of newly generated
	 *         <code>GECandidateProgram</code> instances.
	 */
	@Override
	public List<CandidateProgram> getInitialPopulation() {
		if (popSize < 1) {
			throw new IllegalStateException(
					"Population size must be 1 or greater");
		}

		// Create population list to be populated.
		final List<CandidateProgram> firstGen = new ArrayList<CandidateProgram>(
				popSize);

		// Create and add new programs to the population.
		for (int i = 0; i < popSize; i++) {
			GECandidateProgram candidate;

			do {
				// Create a new program down to the models initial max depth.
				candidate = getInitialProgram();
			} while (!acceptDuplicates && firstGen.contains(candidate));

			// Add to the new population.
			firstGen.add(candidate);
		}

		return firstGen;
	}

	/**
	 * Constructs a new <code>GECandidateProgram</code> with a sequence of
	 * codons that map to a derivation tree using the currently set grammar
	 * which has a depth at most equal to the max depth property.
	 * 
	 * @return a new <code>GECandidateProgram</code> instance.
	 */
	public GECandidateProgram getInitialProgram() {
		if (rng == null) {
			throw new IllegalStateException(
					"No random number generator has been set");
		} else if (grammar == null) {
			throw new IllegalStateException("No grammar has been set");
		}

		final GrammarRule start = grammar.getStartRule();

		// Determine the minimum depth possible for a valid program.
		int minDepth = start.getMinDepth();
		if (minDepth > maxDepth) {
			throw new IllegalStateException(
					"No possible programs within given max depth parameter for this grammar.");
		}

		// Empty list of codons to be filled.
		final List<Integer> codons = new ArrayList<Integer>();

		// Fill in the list of codons with reference to the grammar.
		fillCodons(codons, start, 0, maxDepth);

		return new GECandidateProgram(codons, getModel());
	}

	/*
	 * Constructs a full parse tree by making appropriate production choices
	 * and then filling in a randomly selected codon that matches the production
	 * choice.
	 */
	private void fillCodons(final List<Integer> codons, final GrammarNode rule,
			final int depth, final int maxDepth) {
		if (rule instanceof GrammarRule) {
			final GrammarRule nt = (GrammarRule) rule;

			// Check if theres more than one production.
			int productionIndex = 0;
			final int noProductions = nt.getNoProductions();
			if (noProductions > 1) {
				final List<Integer> validProductions = getValidProductionIndexes(
						nt.getProductions(), maxDepth - depth - 1);

				// Choose a production randomly.
				final int chosenProduction = rng.nextInt(validProductions
						.size());
				productionIndex = validProductions.get(chosenProduction);

				// Scale the production index up to get our new codon.
				final int codon = convertToCodon(productionIndex, noProductions);

				codons.add(codon);
			}

			// Drop down the tree at this production.
			final GrammarProduction p = nt.getProduction(productionIndex);

			final List<GrammarNode> symbols = p.getGrammarNodes();
			for (final GrammarNode s: symbols) {
				fillCodons(codons, s, depth + 1, maxDepth);
			}
		}
	}

	/*
	 * Helper method for fillCodons which determines which of a set of
	 * productions are valid choices to meet the depth constraints.
	 */
	private List<Integer> getValidProductionIndexes(
			final List<GrammarProduction> grammarProductions, final int maxDepth) {
		final List<Integer> valid = new ArrayList<Integer>();

		for (int i = 0; i < grammarProductions.size(); i++) {
			final GrammarProduction p = grammarProductions.get(i);

			if (p.getMinDepth() <= maxDepth) {
				valid.add(i);
			}
		}

		// If there were any valid recursive productions, return them, otherwise
		// use the others.
		return valid;
	}

	/*
	 * Converts a production choice from a number of productions to a codon by
	 * scaling the production index up to a random number inside the model's
	 * max codon size limit, while maintaining the modulo of the number.
	 */
	private int convertToCodon(final int productionIndex,
			final int noProductions) {
		if (maxCodonValue < 3) {
			throw new IllegalStateException("maximum codon value cannot be less than 3");
		}
		
		int codon = rng.nextInt(maxCodonValue - noProductions);

		// Increment codon until it is valid index.
		int currentIndex = codon % noProductions;
		// Comparing separate index count saves us %ing large ints.
		while ((currentIndex % noProductions) != productionIndex) {
			codon++;
			currentIndex++;
		}

		return codon;
	}

	/**
	 * Returns whether or not duplicates are currently accepted or rejected from
	 * generated populations.
	 * 
	 * @return <code>true</code> if duplicates are currently accepted in any
	 *         populations generated by the <code>getInitialPopulation</code>
	 *         method and <code>false</code> otherwise
	 */
	public boolean isDuplicatesEnabled() {
		return acceptDuplicates;
	}

	/**
	 * Sets whether duplicates should be allowed in the populations that are
	 * generated, or if they should be discarded.
	 * 
	 * @param acceptDuplicates whether duplicates should be accepted in the
	 *        populations that are constructed.
	 */
	public void setDuplicatesEnabled(final boolean acceptDuplicates) {
		this.acceptDuplicates = acceptDuplicates;
	}

	/**
	 * Returns the random number generator that this initialiser is using or
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

	/**
	 * Returns the grammar that this initialiser is generating programs to
	 * satisfy. It is using this grammar that the initialiser will map the
	 * program's codons to determine the parse tree depth.
	 * 
	 * @return the grammar that generated programs are being constructed for.
	 */
	public Grammar getGrammar() {
		return grammar;
	}

	/**
	 * Sets the grammar that should be used to determine which codons to use to
	 * create a program parse tree within the set depth limitations.
	 * 
	 * @param grammar the <code>Grammar</code> that generated programs should be
	 *        constructed for.
	 */
	public void setGrammar(final Grammar grammar) {
		this.grammar = grammar;
	}

	/**
	 * Returns the size of the populations that this initialiser constructs or
	 * <code>-1</code> if none has been set.
	 * 
	 * @return the size of the populations that this initialiser will generate.
	 */
	public int getPopSize() {
		return popSize;
	}

	/**
	 * Sets the size of the populations that this initialiser should construct
	 * on calls to the <code>getInitialPopulation</code> method.
	 * 
	 * @param popSize the size of the populations that should be created by this
	 *        initialiser.
	 */
	public void setPopSize(final int popSize) {
		this.popSize = popSize;
	}

	/**
	 * Returns the maximum depth that any of the parse trees mapped from the
	 * codons of the programs generated by this initialiser should have.
	 * 
	 * @return the maximum depth of the parse trees constructed.
	 */
	public int getMaxDepth() {
		return maxDepth;
	}

	/**
	 * Sets the maximum depth that the chromosomes of the programs this
	 * initialiser constructs should map to.
	 * 
	 * @param maxDepth the maximum depth that the parse trees of new programs
	 *        should have.
	 */
	public void setMaxDepth(final int maxDepth) {
		this.maxDepth = maxDepth;
	}

	/**
	 * Returns the maximum value that codons chosen by this initialiser are
	 * allowed to have.
	 * 
	 * @return the maxCodonValue the maximum integer value that codons selected
	 *         by this initialiser may take.
	 */
	public int getMaxCodonValue() {
		return maxCodonValue;
	}

	/**
	 * Sets the maximum value that codons chosen by this initialiser should
	 * take.
	 * 
	 * @param maxCodonValue the maximum integer value that codons selected by
	 *        this initialiser should take.
	 */
	public void setMaxCodonValue(int maxCodonValue) {
		this.maxCodonValue = maxCodonValue;
	}
}
