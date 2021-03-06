package org.epochx.gr.model.epox;

import org.epochx.core.Evolver;
import org.epochx.gr.op.crossover.WhighamCrossover;
import org.epochx.gr.op.init.RampedHalfAndHalfInitialiser;
import org.epochx.gr.op.mutation.WhighamMutation;
import org.epochx.life.*;
import org.epochx.op.selection.*;
import org.epochx.stats.*;
import org.epochx.test.*;
import org.junit.*;

public class MultiplexerTest extends ModelTest {

	private void setupModel(final Multiplexer model) {
		Evolver evolver = getEvolver();
		
		model.setNoRuns(100);
		model.setPopulationSize(500);
		model.setNoGenerations(51);
		model.setCrossoverProbability(0.9);
		model.setMutationProbability(0.1);
		model.setReproductionProbability(0.0);

		model.setCrossover(new WhighamCrossover(evolver));
		model.setMutation(new WhighamMutation(evolver));

		model.setMaxDepth(16);
		model.setMaxInitialDepth(5);
		model.setInitialiser(new RampedHalfAndHalfInitialiser(evolver, 1, false));
		model.setPoolSelector(null);
		model.setProgramSelector(new FitnessProportionateSelector(evolver));
		model.setNoElites(0);

		model.setTerminationFitness(0.0);
		
		//setupRunPrinting(evolver.getStats(model));
		//setupGenPrinting(evolver.getStats(model));
	}

	/**
	 * Tests 6-bit multiplexer with standard setup.
	 * 
	 * Expecting success rate between % and %.
	 */
	@Test
	public void testMultiplexer6Bit() {
		final int LOWER_SUCCESS = 37;
		final int UPPER_SUCCESS = 47;

		final Multiplexer model = new Multiplexer(getEvolver(), 6);
		setupModel(model);

		final int noSuccess = getNoSuccesses(model, false, false);
		assertBetween("Unexpected success rate for Multiplexer 6-bit", LOWER_SUCCESS, UPPER_SUCCESS, noSuccess);
	}

	/**
	 * Tests 11-bit multiplexer with standard setup except:
	 * - Population size of 4000
	 * - Program selector of FitnessProportionateSelector with over-selection
	 * 
	 * Expecting success rate between % and %.
	 */
	@Test
	public void testMultiplexer11Bit() {
		final int LOWER_SUCCESS = 35;
		final int UPPER_SUCCESS = 45;

		Evolver evolver = getEvolver();
		final Multiplexer model = new Multiplexer(evolver, 11);
		setupModel(model);
		model.setPopulationSize(4000);
		model.setProgramSelector(new FitnessProportionateSelector(evolver, true));

		final int noSuccess = getNoSuccesses(model, false, false);
		assertBetween("Unexpected success rate for Multiplexer 11-bit", LOWER_SUCCESS, UPPER_SUCCESS, noSuccess);
	}
}
