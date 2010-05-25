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
package org.epochx.ge.model.ruby;

import org.apache.commons.lang.ArrayUtils;
import org.epochx.ge.model.GEModel;
import org.epochx.ge.representation.GECandidateProgram;
import org.epochx.representation.CandidateProgram;
import org.epochx.tools.eval.RubyInterpreter;
import org.epochx.tools.util.BoolUtils;


public abstract class Multiplexer extends GEModel {

	private boolean[][] inputs;
	
	private int noInputs;
	
	private RubyInterpreter evaluator;
	
	private String[] argNames;
	
	public Multiplexer(int noInputBits, int noAddressBits) {
		evaluator = new RubyInterpreter();
		
		inputs = BoolUtils.generateBoolSequences(noInputBits);
		noInputs = (int) Math.pow(2, noInputBits);
		argNames = new String[noInputBits];
		for (int i=0; i<noAddressBits; i++) {
			argNames[i] = "a" + i;
		}
		for (int i=0; i<(noInputBits-noAddressBits); i++) {
			argNames[i+noAddressBits] = "d" + i;
		}
	}
	
	@Override
	public double getFitness(CandidateProgram p) {
		GECandidateProgram program = (GECandidateProgram) p;
		
        double score = 0;

		// Convert to object array.
		Boolean[][] objInputs = new Boolean[inputs.length][];
    	for (int i=0; i<objInputs.length; i++) {
    		objInputs[i] = ArrayUtils.toObject(inputs[i]);
    	}
    	
		Object[] results = evaluator.eval(program.getSourceCode(), argNames, objInputs);
        for (int i=0; i<results.length; i++) {
	    	boolean[] vars = inputs[i];
	
	        if (results[i] != null && ((Boolean) results[i] == chooseResult(vars))) {
	            score++;
	        } else if (!program.isValid()) {
	        	score = 0;
	        	break;
	        }
	    }
        
        return noInputs - score;
	}

	protected abstract boolean chooseResult(boolean[] inputs);

}
