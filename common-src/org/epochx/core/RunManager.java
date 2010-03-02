package org.epochx.core;

import java.util.List;

import org.epochx.life.LifeCycleManager;
import org.epochx.model.Model;
import org.epochx.representation.CandidateProgram;
import org.epochx.stats.GenerationStats;

public class RunManager {
	
	// The model describing the problem to be evolved.
	private Model model;
	
	// The manager that keeps track of life cycle events and their listeners.
	private LifeCycleManager lifeCycle;
	
	// Core components.
	private GenerationManager generation;
	private InitialisationManager initialisation;
	
	// The best program found so far during the run.
	private CandidateProgram bestProgram;
	
	// The fitness of the best program found so far during the run.
	private double bestFitness;
	
	// The run start time in nano-seconds for measuring program length.
	private long runStartTime;
	
	// The run end time in nano-seconds for measuring program length.
	private long runEndTime;
	
	// Gather generation statistics.
	private GenerationStats genStats;
	
	/*
	 * Private constructor. The static factory method run(GPModel) should be 
	 * used to create objects of GPRun and simultaneously execute them.
	 */
	private RunManager(Model model) {
		this.model = model;
		
		// Setup life cycle manager.
		lifeCycle = Controller.getLifeCycleManager();
		
		// Initialise the run.
		runStartTime = System.nanoTime();
		runEndTime = -1;
		bestProgram = null;
		bestFitness = Double.POSITIVE_INFINITY;
		
		// Setup core components.
		generation = new GenerationManager(model);
		initialisation = new InitialisationManager(model);
		
		// Create the statistics monitor.
		genStats = new GenerationStats();
		
		// Setup the listener for generation statistics.
		genStats.addGenerationStatListener(model.getGenerationStatListener());
	}
	
	/**
	 * Construct a new GPRun object and execute it. The GPModel passed in is
	 * used to provide the control parameters for the run.
	 * @param <TYPE> the type of <code>GPCandidateProgram</code> to be evolved.
	 * @param model  the model which will control the run with the parameters 
	 * 				 and fitness function to use.
	 * @return 		 The GPRun object that was executed, containing retrievable 
	 * 				 details about the run.
	 * @see GPModel
	 */
	public static RunManager run(Model model) {
		// Create the GPRun object.
		RunManager runner = new RunManager(model);
		
		// Run it.
		runner.run();
		
		return runner;
	}
	
	/*
	 * This is the private method which actually does the work in this class. 
	 * It is also the workhorse of the whole API as it creates the initial 
	 * population, initiates the genetic operators and performs any elitism 
	 * or pool selection in use.
	 */
	private void run() {
		// Tell our generation stats to record the start time.
		genStats.setStartTime();
		
		// Tell life cycle listener that a run is starting.
		lifeCycle.onRunStart();
		
		// Perform initialisation.
		List<CandidateProgram> pop = initialisation.initialise();
		
		// Generate generation stats for the inital population.
		genStats.addGen(pop, 0);
		
		// Keep track of the best program and fitness.
		updateBestProgram(pop);
		
		// Will be set to true when program of termination fitness achieved.
		boolean fitnessSuccess = false;
		
		// Execute each generation.
		for (int gen=1; gen<=model.getNoGenerations(); gen++) {			
			pop = generation.generation(pop);
			
			// Generate stats for the current population.
			genStats.addGen(pop, gen);
			
			// Keep track of the best program and fitness.
			updateBestProgram(pop);
			
			// We might be finished?
			if (bestFitness <= model.getTerminationFitness()) {
				fitnessSuccess = true;
				break;
			}
		}
		
		if (fitnessSuccess) {
			lifeCycle.onFitnessTermination();
		} else {
			lifeCycle.onGenerationTermination();
		}
		
		runEndTime = System.nanoTime();
	}

	/* 
	 * Update new best program.
	 * Note this forces us to evaluate all programs, which we might
	 * not have done if using TournamentSelection. 
	 */
	private void updateBestProgram(List<CandidateProgram> pop) {
		for (CandidateProgram program: pop) {
			double fitness = program.getFitness();
			if (fitness < bestFitness) {
				bestFitness = fitness;
				bestProgram = program;
			}
		}
	}
	
	/**
	 * Retrieve the GPCandidateProgram with the best fitness found during the 
	 * run. This GPCandidateProgram may have been found in any of the generations.
	 * 
	 * @return the GPCandidateProgram with the best fitness score found.
	 */
	public CandidateProgram getBestProgram() {
		return bestProgram;
	}

	/**
	 * Retrieve the fitness score of the GPCandidateProgram returned by 
	 * getBestProgram(). A lower fitness score is considered better than a 
	 * higher fitness score.
	 * 
	 * @return the fitness score of the best GPCandidateProgram found during 
	 * 		   execution.
	 */
	public double getBestFitness() {
		return bestFitness;
	}
	
	/**
	 * Retrieve the time in nano-seconds of the run. This will be either the 
	 * length of the whole run if it's finished or the time up until now if 
	 * the run is not finished.
	 * 
	 * @return The run time in nano-seconds.
	 */
	public long getRunTime() {
		if (runEndTime == -1) {
			return System.nanoTime() - runStartTime;
		} else {
			return runEndTime - runStartTime;
		}
	}
}