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
package org.epochx.ge.operator;

import static org.epochx.Config.Template.TEMPLATE;
import static org.epochx.RandomSequence.RANDOM_SEQUENCE;
import static org.epochx.ge.CodonFactory.CODON_FACTORY;

import java.util.*;

import org.epochx.AbstractOperator;
import org.epochx.Config;
import org.epochx.Individual;
import org.epochx.RandomSequence;
import org.epochx.Config.ConfigKey;
import org.epochx.event.ConfigEvent;
import org.epochx.event.EventManager;
import org.epochx.event.Listener;
import org.epochx.event.OperatorEvent;
import org.epochx.event.OperatorEvent.EndOperator;
import org.epochx.ge.Chromosome;
import org.epochx.ge.CodonFactory;
import org.epochx.ge.GEIndividual;

/**
 * This class performs a simple point mutation on a <code>GEIndividual</code>.
 * 
 * <p>
 * Each codon in the program's chromosome is considered for mutation, with the
 * probability of that codon being mutated given by the {@link #POINT_PROBABILITY}
 * config key. If the codon does undergo mutation then a replacement codon is 
 * generated using the <code>CodonFactory</code>.
 * 
 * @since 2.0
 */
public class PointMutation extends AbstractOperator implements Listener<ConfigEvent> {

	/**
	 * The key for setting and retrieving the probability of this operator being applied
	 */
	public static final ConfigKey<Double> PROBABILITY = new ConfigKey<Double>();
	
	/**
	 * The key for setting and retrieving the probability each codon has of being mutated
	 */
	public static final ConfigKey<Double> POINT_PROBABILITY = new ConfigKey<Double>();

	// Configuration settings
	private CodonFactory codonFactory;
	private RandomSequence random;
	private Double probability;
	private Double pointProbability;

	/**
	 * Constructs a <code>PointMutation</code> with control parameters
	 * automatically loaded from the config
	 */
	public PointMutation() {
		this(true);
	}

	/**
	 * Constructs a <code>PointMutation</code> with control parameters initially
	 * loaded from the config. If the <code>autoConfig</code> argument is set to
	 * <code>true</code> then the configuration will be automatically updated when
	 * the config is modified.
	 * 
	 * @param autoConfig whether this operator should automatically update its
	 *        configuration settings from the config
	 */
	public PointMutation(boolean autoConfig) {
		// Default config values
		pointProbability = 0.01;
		
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
	 * <li>{@link #POINT_PROBABILITY} (default: 0.01)
	 * <li>{@link CodonFactory#CODON_FACTORY}
	 * </ul>
	 */
	protected void setup() {
		random = Config.getInstance().get(RANDOM_SEQUENCE);
		probability = Config.getInstance().get(PROBABILITY);
		pointProbability = Config.getInstance().get(POINT_PROBABILITY, pointProbability);
		codonFactory = Config.getInstance().get(CODON_FACTORY);
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
		if (event.isKindOf(TEMPLATE, RANDOM_SEQUENCE, PROBABILITY, POINT_PROBABILITY, CODON_FACTORY)) {
			setup();
		}
	}

	/**
	 * Performs point mutation on the given <code>GEIndividual</code>. Each codon in the
	 * individual's chromosome is considered in turn and is modified with some probability
	 * as specified by the point probability. Given that a codon is chosen then a new 
	 * codon is generated using the codon factory to replace it.
	 * 
	 * @param event the <code>EndOperator</code> event to be filled with information
	 *        about this operation
	 * @param parent an array of one individual to undergo point mutation. The individual 
	 * 		  must be an instance of <code>GEIndividual</code>.
	 * @return an array containing one <code>GEIndividual</code> that is the
	 *         result of the mutation
	 */
	@Override
	public GEIndividual[] perform(EndOperator event, Individual ... parent) {
		GEIndividual parent1 = (GEIndividual) parent[0];

		Chromosome codons = parent1.getChromosome().clone();
		int noCodons = codons.length();

		List<Integer> points = new ArrayList<Integer>();
		for (int i = 0; i < noCodons; i++) {
			// Perform a point mutation at the ith node, pointProbability% of
			// time.
			if (random.nextDouble() < pointProbability) {
				codons.setCodon(i, codonFactory.codon());
				points.add(i);
			}
		}

		// Add mutation points into the event
		((EndEvent) event).setMutationPoints(points);

		return new GEIndividual[]{new GEIndividual(codons)};
	}
	
	/**
	 * Returns a <code>PointMutationEndEvent</code> with the operator and 
	 * parent set
	 * 
	 * @param parent the individual that was operated on
	 * @return operator end event
	 */
	@Override
	protected EndEvent getEndEvent(Individual ... parent) {
		return new EndEvent(this, parent);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Point mutation operates on one individual.
	 * 
	 * @return {@inheritDoc}
	 */
	@Override
	public int inputSize() {
		return 1;
	}
	
	/**
	 * Returns the currently set probability of each codon undergoing mutation.
	 * 
	 * @return a value between <code>0.0</code> and <code>1.0</code> inclusive
	 *         which is the probability that a codon will undergo mutation.
	 */
	public double getPointProbability() {
		return pointProbability;
	}

	/**
	 * Sets the probability that each codon considered undergoes mutation.
	 * 
	 * @param pointProbability the probability each codon has of undergoing a mutation. 
	 * 		  <code>1.0</code> would result in all codons being changed, and <code>0.0</code> 
	 * 	      would mean no codons were changed.
	 */
	public void setPointProbability(double pointProbability) {
		this.pointProbability = pointProbability;
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
	 * Sets the <code>CodonFactory</code> this initialiser should use to generate
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
	
	/**
	 * An event fired at the end of a point mutation
	 * 
	 * @see PointMutation
	 * 
	 * @since 2.0
	 */
	public class EndEvent extends OperatorEvent.EndOperator {

		private List<Integer> mutationPoints;

		/**
		 * Constructs a <code>PointMutationEndEvent</code> with the details of the
		 * event
		 * 
		 * @param operator the operator that performed the mutation
		 * @param parents an array of two individuals that the operator was
		 *        performed on
		 */
		public EndEvent(PointMutation operator, Individual[] parents) {
			super(operator, parents);
		}

		/**
		 * Returns a list of the indexes which were mutated in the individual's 
		 * chromosome
		 * 
		 * @return a list of the mutation points
		 */
		public List<Integer> getMutationPoints() {
			return mutationPoints;
		}
		
		/**
		 * Sets a list of the mutation points
		 * 
		 * @param mutationPoints a list of the mutation points
		 */
		public void setMutationPoints(List<Integer> mutationPoints) {
			this.mutationPoints = mutationPoints;
		}
	}
}
