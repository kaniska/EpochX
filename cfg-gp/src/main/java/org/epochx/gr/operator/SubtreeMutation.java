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
package org.epochx.gr.operator;

import static org.epochx.RandomSequence.RANDOM_SEQUENCE;

import java.util.List;

import org.epochx.AbstractOperator;
import org.epochx.Config;
import org.epochx.Individual;
import org.epochx.RandomSequence;
import org.epochx.Config.ConfigKey;
import org.epochx.Config.Template;
import org.epochx.event.ConfigEvent;
import org.epochx.event.EventManager;
import org.epochx.event.Listener;
import org.epochx.event.OperatorEvent.EndOperator;
import org.epochx.gr.GRIndividual;
import org.epochx.gr.init.GrowInitialiser;
import org.epochx.grammar.*;

/**
 * This class performs a subtree mutation on a <code>GRIndividual</code>, as described
 * by Whigham in his paper "Grammatically-based genetic programming".
 * 
 * <p>
 * A <code>NonTerminalSymbol</code> is randomly selected as the crossover point
 * in each individual's parse tree and the two subtrees rooted at those nodes are
 * exchanged.
 * 
 * @since 2.0
 */
public class SubtreeMutation extends AbstractOperator implements Listener<ConfigEvent> {

	/**
	 * The key for setting and retrieving the probability of this operator being applied
	 */
	public static final ConfigKey<Double> PROBABILITY = new ConfigKey<Double>();
	
	private final GrowInitialiser grower;
	
	// Configuration settings
	private RandomSequence random;
	private Double probability;

	/**
	 * Constructs a <code>SubtreeMutation</code> with control parameters
	 * automatically loaded from the config
	 */
	public SubtreeMutation() {
		this(true);
	}

	/**
	 * Constructs a <code>SubtreeMutation</code> with control parameters initially
	 * loaded from the config. If the <code>autoConfig</code> argument is set to
	 * <code>true</code> then the configuration will be automatically updated when
	 * the config is modified.
	 * 
	 * @param autoConfig whether this operator should automatically update its
	 *        configuration settings from the config
	 */
	public SubtreeMutation(boolean autoConfig) {
		grower = new GrowInitialiser(null);
		
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
	 * <li>{@link #PROBABILITY}
	 * </ul>
	 */
	protected void setup() {
		random = Config.getInstance().get(RANDOM_SEQUENCE);
		probability = Config.getInstance().get(PROBABILITY);

		grower.setRandomSequence(random);
	}

	/**
	 * Receives configuration events and triggers this operator to configure its
	 * parameters if the <code>ConfigEvent</code> is for one of its required
	 * parameters.
	 * 
	 * @param event {@inheritDoc}
	 */
	@Override
	public void onEvent(ConfigEvent event) {
		if (event.isKindOf(Template.TEMPLATE, RANDOM_SEQUENCE, PROBABILITY)) {
			setup();
		}
	}

	/**
	 * Performs a subtree mutation operation on the specified parent individual.
	 * 
	 * <p>
	 * A <code>NonTerminalSymbol</code> is randomly selected as the mutation point
	 * in the individual's parse tree and the subtree rooted at that point is exchanged
	 * with a new randomly generated subtree.
	 * 
	 * @param event the <code>EndOperator</code> event to be filled with information
	 *        about this operation
	 * @param parents an array containing one individual to undergo mutation. The 
	 * 		  individual must be an instance of <code>GRIndividual</code>.
	 * @return an array containing one <code>GRIndividual</code>s that is the
	 *         result of the mutation
	 */
	@Override
	public GRIndividual[] perform(EndOperator event, Individual ... parents) {
		GRIndividual mutatedProgram = (GRIndividual) program.clone();

		NonTerminalSymbol parseTree = mutatedProgram.getParseTree();

		// This is v.inefficient because we have to fly up and down the tree lots of times.
		List<Integer> nonTerminals = parseTree.getNonTerminalIndexes();

		// Choose a node to change.
		int point = nonTerminals.get(rng.nextInt(nonTerminals.size()));
		NonTerminalSymbol original = (NonTerminalSymbol) parseTree.getNthSymbol(point);
		int originalDepth = original.getDepth();

		// Add mutation into the end event
		((SubtreeMutationEndEvent) event).setMutationPoint(point);

		// Construct a new subtree from that node's grammar rule.
		//TODO We should allow any depth down to the maximum rather than just the original subtrees depth
		GrammarRule rule = original.getGrammarRule();
		NonTerminalSymbol subtree = grower.getGrownParseTree(originalDepth, rule);

		// Add subtree into the end event
		stats.addData(MUT_SUBTREE, subtree);

		// Replace node.
		if (point == 0) {
			mutatedProgram.setParseTree(subtree);
		} else {
			parseTree.setNthSymbol(point, subtree);
		}

		return mutatedProgram;
	}

	/**
	 * Returns a <code>SubtreeMutationEndEvent</code> with the operator and 
	 * parent set
	 * 
	 * @param parent the individual that was operated on
	 * @return operator end event
	 */
	@Override
	protected SubtreeMutationEndEvent getEndEvent(Individual ... parent) {
		return new SubtreeMutationEndEvent(this, parent);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Subtree mutation operates on one individual.
	 * 
	 * @return {@inheritDoc}
	 */
	@Override
	public int inputSize() {
		return 1;
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
	public void setRandomSequence(RandomSequence random) {
		this.random = random;
		
		grower.setRandomSequence(random);
	}
}
