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
package org.epochx.gp.model;

import java.util.List;

import org.epochx.core.Model;
import org.epochx.gp.op.crossover.*;
import org.epochx.gp.op.init.*;
import org.epochx.gp.op.mutation.*;
import org.epochx.gp.representation.Node;


/**
 * GPModel is a partial implementation of GPModel which provides 
 * sensible defaults for many of the necessary control parameters. It also 
 * provides a simple way of setting many values so an extending class isn't 
 * required to override all methods they wish to alter, and can instead use 
 * a simple setter method call. 
 * 
 * <p>Those methods that it isn't possible to provide a <em>sensible</em> 
 * default for, for example getFitness(GPCandidateProgram), getTerminals() and
 * getFunctions(), are not implemented to force the extending class to 
 * consider their implementation.
 * 
 * @see GPModel
 */
public abstract class GPModel extends Model {

	private int maxInitialDepth;
	private int maxProgramDepth;
	
	private List<Node> syntax;
	
	/**
	 * Construct a GPModel with a set of sensible defaults. See the appropriate
	 * accessor method for information of each default value.
	 */
	public GPModel() {
		// Initialise default parameter values.
		maxInitialDepth = 6;
		maxProgramDepth = 12;
		
		// Initialise components.
		setInitialiser(new FullInitialiser(this));
		setCrossover(new UniformPointCrossover(this));
		setMutation(new SubtreeMutation(this));
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * This implementation checks that this model is in a runnable state for 
	 * performing an XGP run before executing. A model is in a runnable state if
	 * all compulsory control parameters and operators have been set, for 
	 * example, syntax must not be an empty list. If it is not in a runnable 
	 * state then an <code>IllegalStateException</code> is thrown.
	 */
	@Override
	public void run() {
		// Validate that the model is in a runnable state.
		if (!isInRunnableState()) {
			throw new IllegalStateException("model not in runnable state - one or more compulsory control parameters unset");
		}
		
		super.run();
	}
	
	/**
	 * Tests whether the model is sufficiently setup to be executed. For a model
	 * to be in a runnable state it must have all compulsory control parameters 
	 * and operators set.
	 * 
	 * @return true if this model is in a runnable state, false otherwise.
	 */
	public boolean isInRunnableState() {
		/*
		 * We assume all parameters with a default are still set because their 
		 * own validation should have caught any attempt to unset them.
		 */
		boolean runnable = true;		
		
		if (getSyntax().isEmpty()) {
			runnable = false;
		}

		return runnable;
	}
	
	/**
	 * Retrieves the maximum depth of CandidatePrograms allowed in the 
	 * population after initialisation. The exact way in which the 
	 * implementation ensures this depth is kept to may vary.
	 * 
	 * <p>Defaults to 6.
	 * 
	 * @return the maximum depth of CandidatePrograms to be allowed in the 
	 * 		   population after initialisation.
	 */
	public int getInitialMaxDepth() {
		return maxInitialDepth;
	}

	/**
	 * Overwrites the default max program tree depth allowed after 
	 * initialisation is performed.
	 * 
	 * <p>Max depth of -1 is allowed to indicate no limit.
	 * 
	 * @param maxInitialDepth the new max program tree depth to use.
	 */
	public void setInitialMaxDepth(int maxInitialDepth) {
		//TODO The name of this needs to be made consistent with those from XGR and XGE.
		if (maxInitialDepth >= -1) {
			this.maxInitialDepth = maxInitialDepth;
		} else {
			throw new IllegalArgumentException("maxInitialDepth must be -1 or greater");
		}
	}

	/**
	 * Retrieves the maximum depth of CandidatePrograms allowed in the 
	 * population after undergoing genetic operators. The exact way in which 
	 * CandidatePrograms deeper than this limit are dealt with may vary, but 
	 * they will not be allowed to remain into the next generation unaltered.
	 * 
	 * <p>Defaults to 12.
	 * 
	 * @return the maximum depth of CandidatePrograms to be allowed in the 
	 * 		   population after genetic operators.
	 */
	public int getMaxProgramDepth() {
		return maxProgramDepth;
	}

	/**
	 * Overwrites the default max program tree depth allowed after genetic 
	 * operators are performed.
	 * 
	 * <p>Max depth of -1 is allowed to indicate no limit.
	 * 
	 * @param maxDepth the new max program tree depth to use.
	 */
	public void setMaxProgramDepth(int maxDepth) {
		if (maxDepth >= -1) {
			this.maxProgramDepth = maxDepth;
		} else {
			throw new IllegalArgumentException("maxProgramDepth must be -1 or greater");
		}
	}
	
	/**
	 * Retrieves the full set of syntax, that is terminals AND function nodes.
	 * 
	 * @return the full syntax for use in building node trees.
	 */
	public List<Node> getSyntax() {
		return syntax;
	}
	
	/**
	 * Sets the syntax to use, that is a complete set of the terminals and 
	 * function nodes.
	 * 
	 * @param syntax a list of the functions and terminals.
	 */
	public void setSyntax(List<Node> syntax) {
		if (syntax != null) {
			this.syntax = syntax;
		} else {
			throw new IllegalArgumentException("syntax must not be null");
		}
	}
}
