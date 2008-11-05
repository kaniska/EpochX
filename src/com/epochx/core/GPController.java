/*  
 *  Copyright 2007-2008 Lawrence Beadle & Tom Castle
 *  Licensed under GNU General Public License
 * 
 *  This file is part of Epoch X - (The Genetic Programming Analysis Software)
 *
 *  Epoch X is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Epoch X is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Epoch X.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.epochx.core;

import java.util.*;

import com.epochx.core.crossover.*;
import com.epochx.core.initialisation.*;
import com.epochx.core.representation.*;

/**
 * 
 */
public class GPController {
	
	private GPConfig config;
	
	public GPController(GPConfig config) {
		this.config = config;
	}
	
	public GPRun[] run() {
		GPRun[] runs = new GPRun[config.getNoRuns()];
		
		for (GPRun r: runs) {
			r = new GPRun();
			r.run(config);
		}
		
		return runs;
	}
	
	public static void main(String[] args) {
		GPConfig config = new GPConfig();
		config.setMaxDepth(6);
		config.setPopulationSize(25);
		config.setNoRuns(1);
		
		// Define functions.
		List<FunctionNode<?>> functions = new ArrayList<FunctionNode<?>>();
		functions.add(new IfFunction(null, null, null));
		functions.add(new AndFunction(null, null));
		functions.add(new OrFunction(null, null));
		functions.add(new NotFunction(null));
		config.setFunctions(functions);
		
		// Define terminals.
		List<TerminalNode<?>> terminals = new ArrayList<TerminalNode<?>>();
		terminals.add(new TerminalNode<String>("D3"));
		terminals.add(new TerminalNode<String>("D2"));
		terminals.add(new TerminalNode<String>("D1"));
		terminals.add(new TerminalNode<String>("D0"));
		terminals.add(new TerminalNode<String>("A1"));
		terminals.add(new TerminalNode<String>("A0"));
		config.setTerminals(terminals);
		
		// Define initialiser.
		Initialiser init = new GrowInitialiser(config, null);
		config.setInitialiser(init);
		
		// Define crossover.
		Crossover crossover = new UniformPointCrossover(config);
		config.setCrossover(crossover);
		
		// Run.
		GPController controller = new GPController(config);
		GPRun[] runs = controller.run();
	}
}
