package org.epochx.gp.model;

import org.epochx.core.Evolver;
import org.epochx.gp.op.crossover.*;
import org.epochx.gp.op.init.RampedHalfAndHalfInitialiser;
import org.epochx.gp.op.mutation.*;
import org.epochx.life.*;
import org.epochx.op.selection.*;
import org.epochx.stats.*;
import org.epochx.test.*;
import org.junit.*;

public class EvenParityTest extends ModelTest {

	private void setupModel(final EvenParity model) {
		Evolver evolver = getEvolver();
		
		model.setNoRuns(100);
		model.setPopulationSize(4000);
		model.setNoGenerations(51);
		model.setCrossoverProbability(0.9);
		model.setMutationProbability(0.0);
		model.setReproductionProbability(0.1);

		model.setCrossover(new KozaCrossover(evolver));

		model.setMaxDepth(16);
		model.setMaxInitialDepth(5);
		model.setInitialiser(new RampedHalfAndHalfInitialiser(evolver, 1, false));
		model.setPoolSelector(null);
		model.setProgramSelector(new FitnessProportionateSelector(evolver, true));
		model.setNoElites(1);

		model.setTerminationFitness(0.0);
	}
	
	@Override
	public void setUp() {
		super.setUp();
	}

	/**
	 * Tests even 3 parity with standard setup.
	 * 
	 * Koza's success rate: 100% (p531).
	 * Expecting success rate between 99% and 100%.
	 */
	@Test
	public void testEven3Parity() {
		final int LOWER_SUCCESS = 99;
		final int UPPER_SUCCESS = 100;

		final EvenParity model = new EvenParity(getEvolver(), 3);
		setupModel(model);

		final int noSuccess = getNoSuccesses(model, false, true);
		assertBetween("Unexpected success rate for Even 3 Parity", LOWER_SUCCESS, UPPER_SUCCESS, noSuccess);
	}

	/**
	 * Tests even 4 parity with standard setup.
	 * 
	 * Koza's success rate: 45% (p531).
	 * Expecting success rate between 85% and 95%.
	 */
	@Test
	public void testEven4Parity() {
		final int LOWER_SUCCESS = 85;
		final int UPPER_SUCCESS = 95;

		final EvenParity model = new EvenParity(getEvolver(), 4);
		setupModel(model);

		final int noSuccess = getNoSuccesses(model, false, true);
		assertBetween("Unexpected success rate for Even 4 Parity", LOWER_SUCCESS, UPPER_SUCCESS, noSuccess);
	}

	/**
	 * Tests even 5 parity with standard setup.
	 * 
	 * Expecting success rate between 70% and 80%.
	 */
	@Test
	public void testEven5Parity() {
		final int LOWER_SUCCESS = 70;
		final int UPPER_SUCCESS = 80;

		final EvenParity model = new EvenParity(getEvolver(), 5);
		setupModel(model);

		final int noSuccess = getNoSuccesses(model, false, false);
		assertBetween("Unexpected success rate for Even 5 Parity", LOWER_SUCCESS, UPPER_SUCCESS, noSuccess);
	}

	/**
	 * Tests standard setup except:
	 * - Reproduction probability of 0.0
	 * - Mutation probability of 0.1
	 * - Mutation operator of SubtreeMutation
	 * 
	 * Expecting success rate between 85% and 95%.
	 */
	@Test
	public void testEven4ParitySubtreeMutation() {
		final int LOWER_SUCCESS = 85;
		final int UPPER_SUCCESS = 95;

		Evolver evolver = getEvolver();
		final EvenParity model = new EvenParity(evolver, 4);
		setupModel(model);
		model.setMutationProbability(0.1);
		model.setReproductionProbability(0.0);
		model.setMutation(new SubtreeMutation(evolver));

		final int noSuccess = getNoSuccesses(model, false, false);
		assertBetween("Unexpected success rate for Even 4 Parity with subtree mutation", LOWER_SUCCESS, UPPER_SUCCESS, noSuccess);
	}

	/**
	 * Tests standard setup except:
	 * - Reproduction probability of 0.0
	 * - Mutation probability of 0.1
	 * - Mutation operator of PointMutation
	 * 
	 * Expecting success rate between 90% and 100%.
	 */
	@Test
	public void testEven4ParityPointMutation() {
		final int LOWER_SUCCESS = 90;
		final int UPPER_SUCCESS = 100;

		Evolver evolver = getEvolver();
		final EvenParity model = new EvenParity(evolver, 4);
		setupModel(model);
		model.setMutationProbability(0.1);
		model.setReproductionProbability(0.0);
		model.setMutation(new PointMutation(evolver));

		final int noSuccess = getNoSuccesses(model, false, false);
		assertBetween("Unexpected success rate for Even 4 Parity with point mutation", LOWER_SUCCESS, UPPER_SUCCESS, noSuccess);
	}

	/**
	 * Tests standard setup except:
	 * - Crossover operator of SubtreeCrossover
	 * 
	 * Expecting success rate between 70% and 80%.
	 */
	@Test
	public void testEven4ParitySubtreeCrossover() {
		final int LOWER_SUCCESS = 70;
		final int UPPER_SUCCESS = 80;

		Evolver evolver = getEvolver();
		final EvenParity model = new EvenParity(evolver, 4);
		setupModel(model);
		model.setCrossover(new SubtreeCrossover(evolver));

		final int noSuccess = getNoSuccesses(model, false, false);
		assertBetween("Unexpected success rate for Even 4 Parity with subtree crossover", LOWER_SUCCESS, UPPER_SUCCESS, noSuccess);
	}

	/**
	 * Tests standard setup except:
	 * - Crossover operator of OnePointCrossover
	 * - Mutation operator of SubtreeMutation (because one-point crossover needs
	 * mutation).
	 * - Reproduction probability of 0.0
	 * - Mutation probability of 0.3
	 * - Crossover probability of 0.7
	 * - Program selector of TournamentSelector 7.
	 * 
	 * Expecting success rate between 10% and 20%.
	 */
	@Test
	public void testEven4ParityOnePointCrossover() {
		final int LOWER_SUCCESS = 10;
		final int UPPER_SUCCESS = 20;

		Evolver evolver = getEvolver();
		final EvenParity model = new EvenParity(evolver, 4);
		setupModel(model);
		model.setCrossover(new OnePointCrossover(evolver));
		model.setMutation(new SubtreeMutation(evolver));
		model.setProgramSelector(new TournamentSelector(evolver, 7));
		model.setMutationProbability(0.3);
		model.setReproductionProbability(0.0);
		model.setCrossoverProbability(0.7);

		final int noSuccess = getNoSuccesses(model, false, false);
		assertBetween("Unexpected success rate for Even 4 Parity with one-point crossover", LOWER_SUCCESS, UPPER_SUCCESS, noSuccess);
	}

	/**
	 * Tests standard setup except:
	 * - Program selector of LinearRankSelector
	 * 
	 * Expecting success rate between 0% and 0%.
	 */
	@Test
	public void testEven4ParityLinearRankSelection() {
		final int LOWER_SUCCESS = 0;
		final int UPPER_SUCCESS = 0;

		Evolver evolver = getEvolver();
		final EvenParity model = new EvenParity(evolver, 4);
		setupModel(model);
		model.setProgramSelector(new LinearRankSelector(evolver));

		final int noSuccess = getNoSuccesses(model, false, false);
		assertBetween("Unexpected success rate for Even 4 Parity with linear rank selector", LOWER_SUCCESS, UPPER_SUCCESS, noSuccess);
	}

	/**
	 * Tests standard setup except:
	 * - Program selector of TournamentSelector (size 7)
	 * 
	 * Expecting success rate between 90% and 100%.
	 */
	@Test
	public void testEven4ParityTournament7Selection() {
		final int LOWER_SUCCESS = 90;
		final int UPPER_SUCCESS = 100;

		Evolver evolver = getEvolver();
		final EvenParity model = new EvenParity(evolver, 4);
		setupModel(model);
		model.setProgramSelector(new TournamentSelector(evolver, 7));

		final int noSuccess = getNoSuccesses(model, false, false);
		assertBetween("Unexpected success rate for Even 4 Parity with tournament selector", LOWER_SUCCESS, UPPER_SUCCESS, noSuccess);
	}
}
