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
package org.epochx.gp.op.init;

import java.util.*;

import org.epochx.epox.*;
import org.epochx.gp.model.GPModel;
import org.epochx.gp.representation.GPCandidateProgram;
import org.epochx.op.ConfigOperator;
import org.epochx.representation.CandidateProgram;
import org.epochx.tools.random.RandomNumberGenerator;
import org.epochx.tools.util.TypeUtils;

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
 * <li>program return type</li>
 * <li>random number generator</li>
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
 * @see GrowInitialiser
 * @see RampedHalfAndHalfInitialiser
 */
public class FullInitialiser extends ConfigOperator<GPModel> implements GPInitialiser {

	private RandomNumberGenerator rng;

	// The language to construct the trees from.
	private List<Node> terminals;
	private List<Node> functions;
	private List<Node> syntax;

	// Each generated program's return type.
	private Class<?> returnType;
	
	// The size of the populations to construct.
	private int popSize;

	// The depth of every program tree to generate.
	private int depth;

	// Whether programs must be unique in generated populations.
	private boolean acceptDuplicates;
	
	// Return types that are valid at each depth.
	private Class<?>[][] validDepthTypes;
	

	/**
	 * Constructs a <code>FullInitialiser</code> with all the necessary
	 * parameters given.
	 */
	public FullInitialiser(final RandomNumberGenerator rng, 
			final List<Node> syntax, final Class<?> returnType, final int popSize,
			final int depth, final boolean acceptDuplicates) {
		this(null, acceptDuplicates);
		
		this.rng = rng;
		this.syntax = syntax;
		this.returnType = returnType;
		this.popSize = popSize;
		this.depth = depth;

		updateSyntax();
	}

	/**
	 * Constructs a <code>FullInitialiser</code> with the necessary parameters
	 * loaded from the given model. The parameters are reloaded on configure
	 * events. Duplicate programs are allowed in the populations that are
	 * constructed.
	 * 
	 * @param model the <code>GPModel</code> instance from which the necessary
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
	 * @param model the <code>GPModel</code> instance from which the necessary
	 *        parameters should be loaded.
	 * @param acceptDuplicates whether duplicates should be allowed in the
	 *        populations that are generated.
	 */
	public FullInitialiser(final GPModel model, final boolean acceptDuplicates) {
		super(model);
		
		terminals = new ArrayList<Node>();
		functions = new ArrayList<Node>();
		
		this.acceptDuplicates = acceptDuplicates;
	}

	/**
	 * Configures this operator with parameters from the model.
	 */
	@Override
	public void onConfigure() {
		rng = getModel().getRNG();
		depth = getModel().getMaxInitialDepth();
		popSize = getModel().getPopulationSize();
		
		// Only update the syntax if it has changed.
		final List<Node> newSyntax = getModel().getSyntax();
		if (!newSyntax.equals(syntax)) {
			syntax = newSyntax;
			
			// Update the terminal and function sets.
			updateSyntax();
			
			// Types possibilities table needs updating.
			validDepthTypes = null;
		}
		
		// Update return type.
		final Class<?> newReturnType = getModel().getReturnType();
		if (newReturnType != returnType) {
			returnType = newReturnType;
			
			// Types possibilities table needs updating.
			validDepthTypes = null;
		}
	}

	/*
	 * Updates the terminals and functions lists from the syntax.
	 */
	private void updateSyntax() {
		terminals.clear();
		functions.clear();

		if (syntax != null) {
			for (final Node n: syntax) {
				if (n.getArity() == 0) {
					terminals.add(n);
				} else {
					functions.add(n);
				}
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
	 * depth attribute.
	 * 
	 * @return A <code>List</code> of newly generated
	 *         <code>GPCandidateProgram</code> instances with full node trees.
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
			GPCandidateProgram candidate;

			do {
				// Build a new full node tree.
				candidate = getInitialProgram();
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
	 * @return a new <code>GPCandidateProgram</code> instance.
	 */
	public GPCandidateProgram getInitialProgram() {
		final Node root = getFullNodeTree();

		return new GPCandidateProgram(root, getModel());
	}

	/**
	 * Builds a full node tree down to the given depth. As the node tree will be
	 * full the maximum and minimum depths of the returned node tree should be
	 * equal to the depth argument. The nodes that form the tree will be
	 * randomly selected from the nodes provided as the syntax attribute.
	 * 
	 * @return The root node of a randomly generated full node tree of the
	 *         requested depth.
	 */
	public Node getFullNodeTree() {
		if (rng == null) {
			throw new IllegalStateException("No random number generator has been set");
		} else if (returnType == null) {
			throw new IllegalStateException("No return type has been set");
		} else if (depth < 0) {
			throw new IllegalStateException("Depth must be 0 or greater");
		} else if (terminals.isEmpty()) {
			throw new IllegalStateException("Syntax must include nodes with arity of 0");
		} else if ((depth > 0) && functions.isEmpty()) {
			throw new IllegalStateException("Syntax must include nodes with arity of >=1 if a depth >0 is used");
		}
		
		// Update the types possibilities table if needed.
		if (validDepthTypes == null) {
			updateValidTypes();
		}
		
		if (!TypeUtils.containsSub(validDepthTypes[depth], returnType)) {
			throw new IllegalStateException("Syntax is not able to produce full trees with the given return type.");
		}
		
		return getNodeTree(returnType, 0);
	}
	
	/*
	 * Helper method for the getFullNodeTree method. Recursively fills the
	 * children of a node, to construct a full tree down to a depth of
	 * maxDepth.
	 */
	private Node getNodeTree(Class<?> requiredType, int currentDepth) {
		// Choose a node with correct type and obtainable arg types.
		List<Node> validNodes = getValidNodes(depth-currentDepth, requiredType);
		
		if (validNodes.isEmpty()) {
			throw new IllegalStateException("Syntax is unable to create full node trees of given depth.");
		}
		final int randomIndex = rng.nextInt(validNodes.size());
		Node root = validNodes.get(randomIndex).newInstance();
		int arity = root.getArity();
		
		if (arity > 0) {
			// Construct a list of the arg sets that produce the right return type.
			// TODO We can surely cut down the amount of times we're calling this?!
			Class<?>[][] argTypeSets = getPossibleArgTypes(arity, validDepthTypes[depth-currentDepth]);
			List<Class<?>[]> validArgTypeSets = new ArrayList<Class<?>[]>();
			for (Class<?>[] argTypes: argTypeSets) {
				Class<?> type = root.getReturnType(argTypes);
				if (type != null && requiredType.isAssignableFrom(type)) {
					validArgTypeSets.add(argTypes);
				}
			}
			
			// Randomly select from the valid arg sets.
			if (validArgTypeSets.isEmpty()) {
				throw new IllegalStateException("Syntax is unable to create full node trees of given depth.");
			}
			Class<?>[] argTypes = validArgTypeSets.get(rng.nextInt(validArgTypeSets.size()));
			
			for (int i = 0; i < arity; i++) {
				root.setChild(i, getNodeTree(argTypes[i], currentDepth+1));
			}
		}
		
		return root;
	}
	
	private List<Node> getValidNodes(int remainingDepth, Class<?> requiredType) {
		List<Node> validNodes = new ArrayList<Node>();
		if (remainingDepth == 0) {
			for (Node n: terminals) {
				if (n.getReturnType().isAssignableFrom(requiredType)) {
					validNodes.add(n);
				}
			}
		} else {
			for (Node n: functions) {
				Class<?>[][] argTypeSets = getPossibleArgTypes(n.getArity(), validDepthTypes[remainingDepth-1]);
				
				for (Class<?>[] argTypes: argTypeSets) {
					Class<?> type = n.getReturnType(argTypes);
					if (type != null && requiredType.isAssignableFrom(type)) {
						validNodes.add(n);
						break;
					}
				}
			}
		}
		return validNodes;
	}
	
	/*
	 * Generates the "type possibilities table" from the syntax and return 
	 * type, as described by Montana.
	 */
	private void updateValidTypes() {
		validDepthTypes = new Class<?>[depth+1][];
		
		// Trees of depth 0 must be single terminal element.
		Set<Class<?>> types = new HashSet<Class<?>>();
		for (Node n: terminals) {
			types.add(n.getReturnType());
		}
		validDepthTypes[0] = types.toArray(new Class<?>[types.size()]);
		
		// Handle depths above 1.
		for (int i=1; i<=depth; i++) {
			types = new HashSet<Class<?>>();
			for (Node n: functions) {
				Class<?>[][] argTypeSets = getPossibleArgTypes(n.getArity(), validDepthTypes[i-1]);
			
				// Test each possible set of arguments.
				for (Class<?>[] argTypes: argTypeSets) {
					Class<?> returnType = n.getReturnType(argTypes);
					if (returnType != null) {
						types.add(returnType);
					}
				}
			}
			validDepthTypes[i] = types.toArray(new Class<?>[types.size()]);
		}
	}
	
	/*
	 * TODO We actually only need to do this once at each depth for a particular arity.
	 */
	private Class<?>[][] getPossibleArgTypes(int arity, Class<?>[] availableTypes) {
		int noTypes = availableTypes.length;
		int noCombinations = (int) Math.pow(noTypes, arity);
		Class<?>[][] possibleTypes = new Class<?>[noCombinations][arity];
		
		for (int i=0; i<arity; i++) {
			final int period = (int) Math.pow(noTypes, i);

			for (int j = 0; j < noCombinations; j++) {
				int group = (int) j / period;
				possibleTypes[j][i] = availableTypes[group % noTypes];
			}
		}
		
		return possibleTypes;
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
	 * Returns a <code>List</code> of the <code>Nodes</code> that form the
	 * syntax of new program generated with this initialiser, or
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
		
		updateSyntax();
		
		// Types possibilities table needs updating.
		validDepthTypes = null;
	}
	
	/**
	 * Gets the return type of the program trees that are generated with this 
	 * initialiser. For a program to have a specific return type means that its
	 * root node should return an instance of the given type.
	 * 
	 * @return the return type of the programs being generated.
	 */
	public Class<?> getReturnType() {
		return returnType;
	}

	/**
	 * Sets the return type of the program trees that are generated with this
	 * initialiser. For a program to have a specific return type means that is
	 * root node will return an instance of the given type.
	 * 
	 * @param returnType the return type that generated programs should have.
	 */
	public void setReturnType(final Class<?> returnType) {
		this.returnType = returnType;
		
		// Types possibilities table needs updating.
		validDepthTypes = null;
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
	 * Returns the depth that every program generated by this initialiser should
	 * have.
	 * 
	 * @return the depth of the program trees constructed.
	 */
	public int getDepth() {
		return depth;
	}

	/**
	 * Sets the depth that the program trees this initialiser constructs should
	 * be.
	 * 
	 * @param depth the depth of all new program trees.
	 */
	public void setDepth(final int depth) {
		if (depth > this.depth) {
			// Types possibilities table needs updating.
			//TODO Actually the whole table doesn't need updating, just extending to new depth.
			validDepthTypes = null;
		}
		
		this.depth = depth;
	}
}
