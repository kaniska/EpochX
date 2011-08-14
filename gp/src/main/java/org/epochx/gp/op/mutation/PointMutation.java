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
package org.epochx.gp.op.mutation;

import java.util.*;

import org.epochx.core.*;
import org.epochx.epox.Node;
import org.epochx.gp.model.GPModel;
import org.epochx.gp.representation.GPCandidateProgram;
import org.epochx.life.ConfigListener;
import org.epochx.representation.CandidateProgram;
import org.epochx.stats.*;
import org.epochx.stats.Stats.ExpiryEvent;
import org.epochx.tools.random.RandomNumberGenerator;

/**
 * This class performs a simple point mutation on a
 * <code>GPCandidateProgram</code>.
 * 
 * <p>
 * Each node in the program tree is considered for mutation, with the
 * probability of that node being mutated given as an argument to the
 * PointMutation constructor. If the node does undergo mutation then a
 * replacement node is selected from the full syntax (function and terminal
 * sets), at random.
 * 
 * <p>
 * If a model is provided then the following parameters are loaded upon every
 * configure event:
 * 
 * <ul>
 * <li>random number generator</li>
 * <li>syntax</li>
 * </ul>
 * 
 * <p>
 * If the <code>getModel</code> method returns <code>null</code> then no model
 * is set and whatever static parameters have been set as parameters to the
 * constructor or using the standard accessor methods will be used. If any
 * compulsory parameters remain unset when the mutation is performed then an
 * <code>IllegalStateException</code> will be thrown.
 * 
 * @see SubtreeMutation
 */
public class PointMutation implements GPMutation, ConfigListener {

	/**
	 * Requests a <code>List&lt;Integer&gt;</code> which is a list of the points
	 * modified as a result of the point mutation operation.
	 */
	public static final Stat MUT_POINTS = new AbstractStat(ExpiryEvent.MUTATION) {};

	private Evolver evolver;
	
	private Stats stats;
	
	private List<Node> syntax;

	private RandomNumberGenerator rng;

	// The probability that each node has of being mutated.
	private final double pointProbability;

	/**
	 * Constructs a <code>PointMutation</code>.
	 * 
	 * @param rng
	 * @param syntax
	 */
	public PointMutation(final RandomNumberGenerator rng, final List<Node> syntax, final double pointProbability) {
		this(null, pointProbability);

		this.rng = rng;
		this.syntax = syntax;
	}

	/**
	 * Construct a point mutation with a default point probability of 0.01. It
	 * is generally recommended that the PointMutation(GPModel, double)
	 * constructor is used instead.
	 * 
	 * @param model The current controlling model. Parameters such as full
	 *        syntax will be obtained from this.
	 */
	public PointMutation(final Evolver evolver) {
		this(evolver, 0.01);
	}

	/**
	 * Construct a point mutation with user specified point probability.
	 * 
	 * @param pointProbability The probability each node in a selected program
	 *        has of undergoing a mutation. 1.0 would result in all nodes being
	 *        changed, and 0.0 would mean no nodes were changed. A typical value
	 *        would be 0.01.
	 */
	public PointMutation(final Evolver evolver, final double pointProbability) {
		this.evolver = evolver;
		this.pointProbability = pointProbability;
	}

	/**
	 * Configures this operator with parameters from the model.
	 */
	@Override
	public void configure(Model model) {
		if (model instanceof GPModel) {
			stats = evolver.getStats(model);
			syntax = ((GPModel) model).getSyntax();
			rng = ((GPModel) model).getRNG();
		}
	}

	/**
	 * Perform point mutation on the given GPCandidateProgram. Each node in the
	 * program tree is considered in turn, with each having the given
	 * probability of actually being exchanged. Given that a node is chosen
	 * then a new function or terminal node of the same arity is used to
	 * replace it.
	 * 
	 * @param p The GPCandidateProgram selected to undergo this mutation
	 *        operation.
	 * @return A GPCandidateProgram that was the result of a point mutation on
	 *         the provided GPCandidateProgram.
	 */
	@Override
	public GPCandidateProgram mutate(final CandidateProgram p) {
		final GPCandidateProgram program = (GPCandidateProgram) p;

		final List<Integer> points = new ArrayList<Integer>();

		// Iterate over each node in the program tree.
		final int length = program.getProgramLength();
		for (int i = 0; i < length; i++) {
			// Only change pointProbability of the time.
			if (rng.nextDouble() < pointProbability) {
				// Get the arity of the ith node of the program.
				final Node node = program.getNthNode(i);
				final int arity = node.getArity();

				// Find compatible replacements.
				final List<Node> replacements = getReplacements(node);
				if (!replacements.isEmpty()) {
					// Randomly choose a replacement.
					Node replacement = replacements.get(rng.nextInt(replacements.size()));
					replacement = replacement.newInstance();

					// Attach the old node's children.
					for (int k = 0; k < arity; k++) {
						replacement.setChild(k, node.getChild(k));
					}
					// Then set the new node back into the program.
					program.setNthNode(i, replacement);

					// Record the index of the node that we changed.
					points.add(i);
				}
				// If no replacements then we fall out the bottom and consider
				// the next node.
			}
		}

		// Add mutation points into the stats manager.
		stats.addData(MUT_POINTS, points);

		return program;
	}

	private List<Node> getReplacements(final Node n) {
		final int arity = n.getArity();

		// Get the return type.
		// TODO Ideally this would be the parent's required argument type
		// instead.
		final Class<?> returnType = n.getReturnType();

		// Get the data-type of children.
		final Class<?>[] argTypes = new Class<?>[arity];
		for (int i = 0; i < arity; i++) {
			argTypes[i] = n.getChild(i).getClass();
		}

		// Filter the syntax down to compatible replacements.
		final List<Node> replacements = new ArrayList<Node>();
		for (final Node replacement: syntax) {
			if ((replacement.getArity() == arity) && !nodesEqual(replacement, n)) {
				final Class<?> replacementReturn = replacement.getReturnType(argTypes);
				if ((replacementReturn != null) && returnType.isAssignableFrom(replacementReturn)) {
					replacements.add(replacement);
				}
			}
		}

		return replacements;
	}

	/*
	 * Helper function to check node equivalence. We cannot just use Node's
	 * equals() method because we don't want to compare children if it's a
	 * function node.
	 */
	private boolean nodesEqual(final Node nodeA, final Node nodeB) {
		boolean equal = false;

		// Check they're the same type first.
		if (nodeA.getClass().equals(nodeB.getClass())) {
			if (nodeA.getArity() > 0) {
				// They're both the same function type.
				equal = true;
			} else {
				equal = nodeA.equals(nodeB);
			}
		}

		return equal;
	}

	/**
	 * Returns the random number generator that this mutation is using or
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
	 * Returns a <code>List</code> of the <code>Nodes</code> that form the
	 * syntax of new program generated with this mutation, or
	 * an empty list if none have been set.
	 * 
	 * @return the types of <code>Node</code> that should be used in
	 *         constructing new programs.
	 */
	public List<Node> getSyntax() {
		return syntax;
	}

	/**
	 * Sets the <code>Nodes</code> that should be used to construct new
	 * programs. If a model has been set then this parameter will be overwritten
	 * with the syntax from that model on the next configure event.
	 * 
	 * @param syntax a <code>List</code> of the types of <code>Node</code> that
	 *        should be used in constructing new programs.
	 */
	public void setSyntax(final List<Node> syntax) {
		this.syntax = syntax;
	}
}
