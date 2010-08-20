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
 * The latest version is available from: http:/www.epochx.org
 */
package org.epochx.gp.op.mutation;

import java.util.List;

import org.epochx.gp.model.GPModel;
import org.epochx.gp.representation.*;
import org.epochx.life.ConfigAdapter;
import org.epochx.representation.CandidateProgram;
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
 */
public class PointMutation implements GPMutation {

	// The controlling model.
	private final GPModel model;

	private List<Node> syntax;

	private RandomNumberGenerator rng;

	// The probability that each node has of being mutated.
	private final double pointProbability;

	/**
	 * Construct a point mutation with a default point probability of 0.01. It
	 * is
	 * generally recommended that the PointMutation(GPModel, double) constructor
	 * is used instead.
	 * 
	 * @param model The current controlling model. Parameters such as full
	 *        syntax will be obtained from this.
	 */
	public PointMutation(final GPModel model) {
		this(model, 0.01);
	}

	/**
	 * Construct a point mutation with user specified point probability.
	 * 
	 * @param pointProbability The probability each node in a selected program
	 *        has of undergoing a mutation. 1.0 would result in all nodes being
	 *        changed, and 0.0 would mean no nodes were changed. A typical value
	 *        would be 0.01.
	 */
	public PointMutation(final GPModel model, final double pointProbability) {
		this.model = model;
		this.pointProbability = pointProbability;

		// Configure parameters from the model.
		model.getLifeCycleManager().addConfigListener(new ConfigAdapter() {

			@Override
			public void onConfigure() {
				configure();
			}
		});
	}

	/*
	 * Configure component with parameters from the model.
	 */
	private void configure() {
		syntax = model.getSyntax();
		rng = model.getRNG();
	}

	/**
	 * Perform point mutation on the given GPCandidateProgram. Each node in the
	 * program tree is considered in turn, with each having the given
	 * probability of actually being exchanged. Given that a node is chosen
	 * then a new function or terminal node of the same arity is used to
	 * replace it.
	 * 
	 * @param program The GPCandidateProgram selected to undergo this mutation
	 *        operation.
	 * @return A GPCandidateProgram that was the result of a point mutation on
	 *         the provided GPCandidateProgram.
	 */
	@Override
	public GPCandidateProgram mutate(final CandidateProgram p) {
		final GPCandidateProgram program = (GPCandidateProgram) p;

		// Iterate over each node in the program tree.
		final int length = program.getProgramLength();
		for (int i = 0; i < length; i++) {
			// Only change pointProbability of the time.
			if (rng.nextDouble() < pointProbability) {
				// Get the arity of the ith node of the program.
				final Node node = program.getNthNode(i);
				final int arity = node.getArity();

				// Find a different function/terminal with same arity - start
				// from random position.
				final int rand = rng.nextInt(syntax.size());
				for (int j = 0; j < syntax.size(); j++) {
					final int index = (j + rand) % syntax.size();
					Node n = syntax.get(index);

					if ((n.getArity() == arity) && !nodesEqual(node, n)) {
						n = n.clone();

						// Attach the old node's children.
						for (int k = 0; k < arity; k++) {
							n.setChild(k, node.getChild(k));
						}
						// Then set the new node back into the program.
						program.setNthNode(i, n);

						// No need to keep looking.
						break;
					}
				}
				// If none were found then we fall out the bottom and consider
				// the next node.
			}
		}

		return program;
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

}
