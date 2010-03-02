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
package org.epochx.example.mux;

import java.util.*;

import org.epochx.core.*;
import org.epochx.op.crossover.UniformPointCrossover;
import org.epochx.op.selection.*;
import org.epochx.representation.*;
import org.epochx.representation.bool.*;
import org.epochx.stats.*;
import org.epochx.tools.util.BoolUtils;


/**
 * 
 *
 */
public class Multiplexer11Bit extends GPAbstractModel {

	private boolean[][] inputs;
	
	private HashMap<String, BooleanVariable> variables = new HashMap<String, BooleanVariable>();
	
	public Multiplexer11Bit() {
		inputs = BoolUtils.generateBoolSequences(11);
		
		configure();
	}
	
	public void configure() {
		// Define variables.
		variables.put("D7", new BooleanVariable("D7"));
		variables.put("D6", new BooleanVariable("D6"));
		variables.put("D5", new BooleanVariable("D5"));
		variables.put("D4", new BooleanVariable("D4"));
		variables.put("D3", new BooleanVariable("D3"));
		variables.put("D2", new BooleanVariable("D2"));
		variables.put("D1", new BooleanVariable("D1"));
		variables.put("D0", new BooleanVariable("D0"));
		variables.put("A2", new BooleanVariable("A2"));
		variables.put("A1", new BooleanVariable("A1"));
		variables.put("A0", new BooleanVariable("A0"));
		
		setGenStatFields(new GenerationStatField[]{GenerationStatField.FITNESS_MIN, GenerationStatField.FITNESS_AVE, GenerationStatField.LENGTH_AVE, GenerationStatField.RUN_TIME});
		setRunStatFields(new RunStatField[]{RunStatField.BEST_FITNESS, RunStatField.BEST_PROGRAM, RunStatField.RUN_TIME});
		
		setPopulationSize(500);
		setNoGenerations(50);
		setCrossoverProbability(0.9);
		setMutationProbability(0.0);
		setNoRuns(1);
		setPoolSize(50);
		setNoElites(50);
		setMaxProgramDepth(6);
		setPoolSelector(new TournamentSelector(this, 7));
		setProgramSelector(new RandomSelector(this));
		setCrossover(new UniformPointCrossover(this));
	}
	
	@Override
	public List<Node> getFunctions() {
		// Define functions.
		List<Node> functions = new ArrayList<Node>();
		functions.add(new IfFunction());
		functions.add(new AndFunction());
		functions.add(new OrFunction());
		functions.add(new NotFunction());
		return functions;
	}

	@Override
	public List<Node> getTerminals() {		
		// Define terminals.
		List<Node> terminals = new ArrayList<Node>();
		terminals.add(variables.get("D7"));
		terminals.add(variables.get("D6"));
		terminals.add(variables.get("D5"));
		terminals.add(variables.get("D4"));
		terminals.add(variables.get("D3"));
		terminals.add(variables.get("D2"));
		terminals.add(variables.get("D1"));
		terminals.add(variables.get("D0"));
		terminals.add(variables.get("A2"));
		terminals.add(variables.get("A1"));
		terminals.add(variables.get("A0"));
		
		return terminals;
	}
	
	@Override
	public double getFitness(CandidateProgram p) {
		GPCandidateProgram program = (GPCandidateProgram) p;
		
        double score = 0;
        
        // Execute on all possible inputs.
        for (boolean[] in: inputs) {
        	// Set the variables.
        	variables.get("A0").setValue(in[0]);
        	variables.get("A1").setValue(in[1]);
        	variables.get("A2").setValue(in[2]);
        	variables.get("D0").setValue(in[3]);
        	variables.get("D1").setValue(in[4]);
        	variables.get("D2").setValue(in[5]);
        	variables.get("D3").setValue(in[6]);
        	variables.get("D4").setValue(in[7]);
        	variables.get("D5").setValue(in[8]);
        	variables.get("D6").setValue(in[9]);
        	variables.get("D7").setValue(in[10]);
        	
            if ((Boolean) program.evaluate() == chooseResult(in)) {
                score++;
            }
        }
        
        return 2048 - score;
	}
	
    private boolean chooseResult(boolean[] input) {
    	boolean result = false;
    	// scoring solution
        if(input[0] && input[1] && input[2]) {
            result = input[3];
        } else if(input[0] && input[1]& !input[2]) {
            result = input[4];            
        } else if(input[0] && !input[1] && input[2]) {
            result = input[5];
        } else if(input[0] && !input[1] && !input[2]) {
            result = input[6];
        } else if(!input[0] && input[1] && input[2]) {
            result = input[7];
        } else if(!input[0] && input[1] && !input[2]) {
            result = input[8];
        } else if(!input[0] && !input[1] && input[2]) {
            result = input[9];
        } else if(!input[0] && !input[1] && !input[2]) {
            result = input[10];
        }
        return result;
    }
	
	public static void main(String[] args) {
		Controller.run(new Multiplexer11Bit());
	}
}