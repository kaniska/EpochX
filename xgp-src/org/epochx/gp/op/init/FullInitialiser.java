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
package org.epochx.gp.op.init;

import java.util.*;

import org.epochx.gp.model.GPModel;
import org.epochx.gp.representation.*;
import org.epochx.life.ConfigAdapter;
import org.epochx.representation.CandidateProgram;
import org.epochx.tools.random.RandomNumberGenerator;

/**
 * Initialisation implementation which produces full program trees down to a
 * specified depth.
 * 
 * <p>
 * If a model is provided then the following parameters are loaded upon every
 * configure event:
 * 
 * <ul>
 * <li>population size</li>
 * <li>maximum initial program depth</li>
 * <li>syntax</li>
 * <li>random number generator</li>
 * </ul>
 * 
 * @see GrowInitialiser
 * @see RampedHalfAndHalfInitialiser
 */
public class FullInitialiser implements GPInitialiser {

	// The controlling model.
	private final GPModel model;

	private RandomNumberGenerator rng;

	// The language to construct the trees from.
	private final List<Node> terminals;
	private final List<Node> functions;
	private List<Node> syntax;

	// The size of the populations to construct.
	private int popSize;

	// The depth of every program tree to generate.
	private int initialDepth;

	// Whether programs must be unique in generated populations.
	private boolean acceptDuplicates;

	/**
	 * Constructs a <code>FullInitialiser</code> with the necessary parameters
	 * loaded from the given model. The parameters are reloaded on configure
	 * events. Duplicate programs are allowed in the populations that are
	 * constructed.
	 * 
	 * @param model the <code>Model</code> instance from which the necessary
	 *        parameters should be loaded.
	 */
	public FullInitialiser(final GPModel model) {
		this(model, true);
	}

	/**
	 * Constructs a <code>FullInitialiser</code> with the necessary parameters
	 * loaded from the given model. The parameters are reloaded on configure
	 * events.
	 * 
	 * @param model the <code>Model</code> instance from which the necessary
	 *        parameters should be loaded.
	 * @param acceptDuplicates whether duplicates should be allowed in the
	 *        populations that are generated.
	 */
	public FullInitialiser(final GPModel model, final boolean acceptDuplicates) {
		this.model = model;
		this.acceptDuplicates = acceptDuplicates;

		terminals = new ArrayList<Node>();
		functions = new ArrayList<Node>();

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
		rng = model.getRNG();
		initialDepth = model.getMaxInitialDepth();
		popSize = model.getPopulationSize();

		// Perhaps we could check whether the syntax has changed first?
		syntax = model.getSyntax();
		terminals.clear();
		functions.clear();

		for (final Node n: syntax) {
			if (n.getArity() == 0) {
				terminals.add(n);
			} else {
				functions.add(n);
			}
		}
	}

	/**
	 * Generates a population of new <code>CandidatePrograms</code> constructed
	 * from the <code>Nodes</code> in the syntax attribute. The size of the
	 * population will be equal to the population size attribute. All programs
	 * in the population are only guarenteed to be unique (as defined by the
	 * <code>equals</code> method on <code>GPCandidateProgram</code>) if the
	 * <code>isDuplicatesEnabled</code> method returns <code>true</code>. Each
	 * program will have a full node tree with a depth equal to the
	 * depth
	 * attribute.
	 * 
	 * @return A <code>List</code> of newly generated
	 *         <code>GPCandidateProgram</code> instances with full node trees.
	 */
	@Override
	public List<CandidateProgram> getInitialPopulation() {
		// Create population list to be populated.
		final List<CandidateProgram> firstGen = new ArrayList<CandidateProgram>(
				popSize);

		// Create and add new programs to the population.
		for (int i = 0; i < popSize; i++) {
			GPCandidateProgram candidate;

			do {
				// Build a new full node tree.
				candidate = getInitialProgram(initialDepth);
			} while (!acceptDuplicates && firstGen.contains(candidate));

			// Must be unique - add to the new population.
			firstGen.add(candidate);
		}

		return firstGen;
	}
	
	/**
	 * Constructs a new full node tree and returns it within a 
	 * <code>GPCandidateProgram</code>. The nodes that form the tree will be 
	 * randomly selected from the nodes provided as the syntax attribute.
	 * 
	 * @param maxDepth The maximum depth of the node tree to be grown, where
	 *        the depth is the number of nodes from the root.
	 * @return a new <code>GPCandidateProgram</code> instance.
	 */
	public GPCandidateProgram getInitialProgram(final int maxDepth) {
		Node root = buildFullNodeTree(maxDepth);

		return new GPCandidateProgram(root, model);
	}

	/**
	 * Builds a full node tree down to the given depth. As the node tree will be
	 * full the maximum and minimum depths of the returned node tree should be
	 * equal to the depth argument. The nodes that form the tree will be
	 * randomly selected from the nodes provided as the syntax attribute.
	 * 
	 * @param depth The depth of the full node tree, where the
	 *        depth is the number of nodes from the root.
	 * @return The root node of a randomly generated full node tree of the
	 *         requested depth.
	 */
	public Node buildFullNodeTree(final int depth) {
		Node root;
		if (depth == 0) {
			// Randomly choose a terminal node as our root.
			final int randomIndex = rng.nextInt(terminals.size());
			root = terminals.get(randomIndex).clone();
		} else {
			// Randomly choose a root function node.
			final int randomIndex = rng.nextInt(functions.size());
			root = functions.get(randomIndex).clone();

			// Populate the root node with full children of depth-1.
			fillChildren(root, 0, depth);
		}

		return root;
	}

	/*
	 * Helper method for the buildFullNodeTree method. Recursively fills the
	 * children of a node, to construct a full tree down to a depth of
	 * maxDepth.
	 */
	private void fillChildren(final Node currentNode, final int currentDepth,
			final int maxDepth) {
		final int arity = currentNode.getArity();

		if (currentDepth < maxDepth - 1) {
			// Not near the maximum depth yet, fill children with
			// functions.
			for (int i = 0; i < arity; i++) {
				final int randomIndex = rng.nextInt(functions.size());
				final Node child = functions.get(randomIndex).clone();

				currentNode.setChild(i, child);
				fillChildren(child, (currentDepth + 1), maxDepth);
			}
		} else {
			// At maximum depth-1, fill children with terminals.
			for (int i = 0; i < arity; i++) {
				final int randomIndex = rng.nextInt(terminals.size());
				final Node child = terminals.get(randomIndex).clone();

				currentNode.setChild(i, child);
			}
		}
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
	public void setDuplicatesEnabled(boolean acceptDuplicates) {
		this.acceptDuplicates = acceptDuplicates;
	}
}
