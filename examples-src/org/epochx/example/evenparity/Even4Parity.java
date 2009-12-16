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
package org.epochx.example.evenparity;

import java.util.*;

import org.epochx.core.*;
import org.epochx.op.crossover.UniformPointCrossover;
import org.epochx.op.initialisation.RampedHalfAndHalfInitialiser;
import org.epochx.op.selection.*;
import org.epochx.representation.*;
import org.epochx.representation.bool.*;
import org.epochx.stats.*;
import org.epochx.tools.util.BoolUtils;


/**
 * 
 */
public class Even4Parity extends GPAbstractModel {

	private boolean[][] inputs;
	
	private HashMap<String, BooleanVariable> variables = new HashMap<String, BooleanVariable>();
	
	public Even4Parity() {
		inputs = BoolUtils.generateBoolSequences(4);
		
		configure();
	}
	
	public void configure() {
		// Define variables.
		variables.put("D3", new BooleanVariable("D3"));
		variables.put("D2", new BooleanVariable("D2"));
		variables.put("D1", new BooleanVariable("D1"));
		variables.put("D0", new BooleanVariable("D0"));
		
		setGenStatFields(new GenerationStatField[]{GenerationStatField.FITNESS_MIN, GenerationStatField.FITNESS_AVE, GenerationStatField.LENGTH_AVE, GenerationStatField.RUN_TIME});
		setRunStatFields(new RunStatField[]{RunStatField.BEST_FITNESS, RunStatField.BEST_PROGRAM, RunStatField.RUN_TIME});
		
		setPopulationSize(500);
		setNoGenerations(50);
		setCrossoverProbability(0.8);
		setMutationProbability(0.1);
		setNoRuns(100);
		setPoolSize(50);
		setNoElites(50);
		setInitialMaxDepth(6);
		setMaxProgramDepth(17);
		setPoolSelector(new TournamentSelector(this, 7));
		setProgramSelector(new RandomSelector(this));
		setCrossover(new UniformPointCrossover(this));
		setInitialiser(new RampedHalfAndHalfInitialiser(this));
	}
	
	@Override
	public List<Node> getFunctions() {
		// Define functions.
		List<Node> functions = new ArrayList<Node>();
		functions.add(new XorFunction());
		functions.add(new AndFunction());
		functions.add(new OrFunction());
		functions.add(new NotFunction());
		return functions;
	}

	@Override
	public List<Node> getTerminals() {		
		// Define terminals.
		List<Node> terminals = new ArrayList<Node>();
		terminals.add(variables.get("D3"));
		terminals.add(variables.get("D2"));
		terminals.add(variables.get("D1"));
		terminals.add(variables.get("D0"));
		
		return terminals;
	}
	
	@Override
	public double getFitness(CandidateProgram p) {
        GPCandidateProgram program = (GPCandidateProgram) p;
		
		double score = 0;
        
        // Execute on all possible inputs.
        for (boolean[] in: inputs) {
        	
        	// Set the variables.
        	variables.get("D0").setValue(in[0]);
        	variables.get("D1").setValue(in[1]);
        	variables.get("D2").setValue(in[2]);
        	variables.get("D3").setValue(in[3]);
        	
            if ((Boolean) program.evaluate() == chooseResult(in)) {
                score++;
            }
        }
        
        return 16 - score;
	}
	
    private boolean chooseResult(boolean[] input) {
        // scoring solution
        int eCount = 0;
        for(int i = 0; i<input.length; i++) {
            if(input[i]==true) {
                eCount++;
            }
        }
        if(eCount%2==0) {
            return true;
        } else {
            return false;
        }
    }
	
	public static void main(String[] args) {
		Controller.run(new Even4Parity());
	}
}
