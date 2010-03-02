/*  
 *  Copyright 2007-2008 Lawrence Beadle & Tom Castle
 *  Licensed under GNU General Public License
 * 
 *  This file is part of EpochX: genetic programming for research
 *
 *  EpochX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  EpochX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with EpochX.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.epochx.core;

import org.epochx.core.Controller;
import org.epochx.life.LifeCycleManager;
import org.epochx.model.Model;
import org.epochx.stats.*;

/**
 * The Controller class provides the method for executing multiple GP runs 
 * and so can be considered the key class for starting using EpochX. Even when 
 * only executing one GP run, this method should be used rather than working  
 * with <code>GPRun</code> directly.
 * 
 * <p>To evolve a solution with EpochX the <code>Controller.run(GPModel)</code> 
 * method should be called, passing in a GPModel which defines the control 
 * parameters.
 */
public class Controller {

	// Singleton controller.
	private static Controller controller;
	
	// The manager that keeps track of life cycle events and their listeners.
	private LifeCycleManager lifeCycle;
	
	/*
	 * Private constructor. The only methods are the static run() and 
	 * getLifeCycleManager().
	 */
	private Controller() {
		// Setup life cycle manager.
		lifeCycle = new LifeCycleManager();
	}
	
	/**
	 * Executes one or more GP runs. The number of runs is set within the model 
	 * provided as an argument. This GPModel also supplies all the other 
	 * control parameters for the runs - each run is executed using this same 
	 * model object so will have identical parameters for comparison unless 
	 * changed during execution, for example, in another thread. (Although note 
	 * that unless stated elsewhere, this version of EpochX is not considered 
	 * thread safe).
	 * 
	 * @param <TYPE> The return type of the <code>CandidatePrograms</code> 
	 * 				 generated.
	 * @param model  The GPModel which defines the control parameters for the 
	 * 				 runs.
	 * @return An array of the GPRun objects which were executed on the model.
	 * 		   The details of each run can be retrieved from this object.
	 * @see GPRun
	 * @see GPModel
	 */
	public static RunManager[] run(Model model) {
		if (controller == null) {
			controller = new Controller();
		}
		
		// Stash each GPRun object for retrieval of run details.
		RunManager[] runs = new RunManager[model.getNoRuns()];
		
		// Keep track of the run stats.
		RunStats runStats = new RunStats();
		
		// Setup the listener for run statistics.
		runStats.addRunStatListener(model.getRunStatListener());
		
		for (int i=0; i<model.getNoRuns(); i++) {
			runs[i] = RunManager.run(model);
			
			// Generate stats for this run.
			runStats.addRun(runs[i], i+1);
		}

		return runs;
	}
	
	/**
	 * Returns the controller's life cycle manager. Only one life cycle manager
	 * is available in a Controller. The life cycle manager handles all life 
	 * cycle events that occur during a call to run() and informs the necessary 
	 * listeners.
	 * 
	 * @param <TYPE> the data-type of the program's being evolved.
	 * @return the life cycle manager for the controller.
	 */
	public static LifeCycleManager getLifeCycleManager() {
		if (controller == null) {
			controller = new Controller();
		}
		
		return controller.lifeCycle;
	}
}