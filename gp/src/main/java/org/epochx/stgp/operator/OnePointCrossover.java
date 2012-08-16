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
package org.epochx.stgp.operator;

import static org.epochx.RandomSequence.RANDOM_SEQUENCE;

import java.util.*;

import org.epochx.*;
import org.epochx.Config.ConfigKey;
import org.epochx.epox.Node;
import org.epochx.event.*;
import org.epochx.event.OperatorEvent.EndOperator;
import org.epochx.stgp.STGPIndividual;

/**
 * A crossover operator for <tt>STGPIndividual</tt>s that exchanges subtrees
 * in two individuals. Random crossover points are chosen in both program trees
 * from those points which align in the two programs. Alignment is determined by
 * nodes in the same position having the same arity and data-type, or for the
 * strict form of the operator the nodes must be the same. The crossover points
 * are located in the same position in both program trees and the two subtrees
 * at these points are exchanged. It is recommended that a significant mutation
 * rate (Poli & Langdon use 30%) is used in combination with one-point crossover
 * since it encourages rapid convergence.
 * 
 * For more information about the one-point crossover operator see:<br />
 * R.Poli and W.B.Langdon, Genetic Programming with One-Point Crossover<br />
 * Soft Computing in Engineering Design and Manufacturing, pp. 180-189,
 * Springer-Verlag<br />
 * London, 23-27 June 1997.
 * 
 * @see KozaCrossover
 * @see SubtreeCrossover
 */
public class OnePointCrossover extends AbstractOperator implements Listener<ConfigEvent> {

	/**
	 * The key for setting and retrieving whether the strict form of one-point
	 * crossover should be used
	 */
	public static final ConfigKey<Double> STRICT = new ConfigKey<Double>();

	// Configuration settings
	private RandomSequence random;
	private boolean strict;

	private double probability;

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
	 * <li>{@link #STRICT}
	 * </ul>
	 */
	protected void setup() {
		random = Config.getInstance().get(RANDOM_SEQUENCE);
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
		if (event.isKindOf(RANDOM_SEQUENCE)) {
			setup();
		}
	}

	/**
	 * Performs a one-point crossover on the given individuals. Random crossover
	 * points are chosen in both program trees from those points which align in
	 * the two programs. Alignment is determined by nodes in the same position
	 * having the same arity and data-type, or for the strict form of the
	 * operator the nodes must be the same. The crossover points are located in
	 * the same position in both program trees and the two subtrees at these
	 * points are exchanged. See the paper referenced in the class documation
	 * for more details of the operation of one-point crossover.
	 * 
	 * @param event the <tt>EndOperator</tt> event to be filled with information
	 *        about this operation
	 * @param parents an array of two individuals to undergo one-point
	 *        crossover. Both individuals must be instances of
	 *        <tt>STGPIndividual</tt>.
	 * @return an array containing two <tt>STGPIndividual</tt>s that are the
	 *         result of the crossover
	 */
	@Override
	public STGPIndividual[] perform(EndOperator event, Individual ... parents) {
		STGPIndividual program1 = (STGPIndividual) parents[0];
		STGPIndividual program2 = (STGPIndividual) parents[1];

		// List the points that align
		List<Integer> points1 = new ArrayList<Integer>();
		List<Integer> points2 = new ArrayList<Integer>();
		alignedPoints(program1.getRoot(), program2.getRoot(), points1, points2, 0, 0);

		int randomIndex = random.nextInt(points1.size());
		int swapPoint1 = points1.get(randomIndex);
		int swapPoint2 = points2.get(randomIndex);

		Node subtree1 = program1.getNode(swapPoint1);
		Node subtree2 = program2.getNode(swapPoint2);

		// Clone so children and parents remain distinct for event
		STGPIndividual child1 = program1.clone();
		STGPIndividual child2 = program2.clone();

		child1.setNode(swapPoint1, subtree2);
		child2.setNode(swapPoint2, subtree1);

		((OnePointCrossoverEndEvent) event).setSubtrees(new Node[]{subtree1, subtree2});
		((OnePointCrossoverEndEvent) event).setCrossoverPoints(new int[]{swapPoint1, swapPoint2});

		return new STGPIndividual[]{child1, child2};
	}

	/**
	 * Returns a <tt>OnePointCrossoverEndEvent</tt> with the operator and 
	 * parents set
	 */
	@Override
	protected OnePointCrossoverEndEvent getEndEvent(Individual ... parents) {
		return new OnePointCrossoverEndEvent(this, parents);
	}

	/*
	 * Private helper method for apply method. Traverses the two program
	 * trees and identifies the swap points that can be swapped because they're
	 * connected to a part of the tree that aligns. Supports strict or
	 * non-strict.
	 */
	private void alignedPoints(Node root1, Node root2, List<Integer> points1, List<Integer> points2, int current1,
			int current2) {
		points1.add(current1);
		points2.add(current2);

		boolean valid = false;
		if (!strict) {
			valid = (root1.getArity() == root2.getArity()) && (root1.dataType() == root2.dataType());
		} else {
			valid = root1.getClass().equals(root2.getClass());
		}

		if (valid) {
			for (int i = 0; i < root1.getArity(); i++) {
				Node child1 = root1.getChild(i);
				Node child2 = root2.getChild(i);
				alignedPoints(child1, child2, points1, points2, current1 + 1, current2 + 1);

				current1 += child1.length();
				current2 += child2.length();
			}
		}
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
	 * Sets the probability of this operator being selected
	 * 
	 * @param probability the new probability to set
	 */
	public void setProbability(double probability) {
		this.probability = probability;
	}

	/**
	 * Returns whether strict one-point crossover is being used or not. If set
	 * to <tt>true</tt> then alignment of the parent programs takes into
	 * account not only the arity of the nodes, but also the node type.
	 * 
	 * @return <tt>true</tt> if strict one-point crossover is in use, and
	 *         <tt>false</tt> otherwise
	 */
	public boolean isStrict() {
		return strict;
	}

	/**
	 * Sets whether strict one-point crossover should be used or not. Strict
	 * one-point crossover causes alignment of the parent programs takes into
	 * account not only the arity of the nodes, but also the node type. If
	 * automatic configuration is enabled then any value set here will be
	 * overwritten by the {@link #STRICT} configuration setting on the next
	 * config event.
	 * 
	 * @param strict <tt>true</tt> if strict one-point crossover should be used
	 *        and <tt>false</tt> otherwise
	 */
	public void setStrict(final boolean strict) {
		this.strict = strict;
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