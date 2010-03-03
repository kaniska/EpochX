package org.epochx.ge.model.java.mux;

import org.apache.commons.lang.ArrayUtils;
import org.epochx.ge.model.GEAbstractModel;
import org.epochx.ge.representation.GECandidateProgram;
import org.epochx.representation.CandidateProgram;
import org.epochx.tools.eval.JavaEvaluator;
import org.epochx.tools.grammar.Grammar;
import org.epochx.tools.util.BoolUtils;


public class Multiplexer20Bit extends GEAbstractModel {
	
	public static final String GRAMMAR_STRING = 
		"<prog> ::= <expr>\n" +
		"<expr> ::= <expr> <op> <expr> " +
				"| ( <expr> <op> <expr> ) " +
				"| <var> " +
				"| <pre-op> ( <var> ) " +
				"| ( <expr> ) ? <expr> : <expr>\n" +
		"<pre-op> ::= !\n" +
		"<op> ::= \"||\" | &&\n" +
		"<var> ::= a0 | a1 | a2 | a3 | d0 | d1 | d2 | d3 | d4 | d5 | d6 | d7 | d8 | d9 | d10 | d11 | d12 | d13 | d14 | d15\n";
	
	private Grammar grammar;
	
	private boolean[][] inputs;
	
	private JavaEvaluator evaluator;
	
	public Multiplexer20Bit() {
		grammar = new Grammar(GRAMMAR_STRING);
		evaluator = new JavaEvaluator();
		
		inputs = BoolUtils.generateBoolSequences(20);
	}

	@Override
	public double getFitness(CandidateProgram p) {
		GECandidateProgram program = (GECandidateProgram) p;
		
        double score = 0;
        
        String[] argNames = {"a0", "a1", "a2", "a3", "d0", "d1", "d2", "d3", "d4", "d5", "d6", "d7", "d8", "d9", "d10", "d11", "d12", "d13", "d14", "d15"};
        
        // Execute on all possible inputs.
        for (boolean[] vars: inputs) {
        	// Convert to object array.
        	Boolean[] objVars = ArrayUtils.toObject(vars);
        	
        	Boolean result = (Boolean) evaluator.eval(program.getSourceCode(), argNames, objVars);
        	
            if (result != null && result == chooseResult(vars)) {
                score++;
            }
        }
        
        return 1048576 - score;
	}
	
    private boolean chooseResult(boolean[] input) {
    	// scoring solution
        String locator = "";
        if(input[0]==true) {
            locator = locator + "1";
        } else {
            locator = locator + "0";
        }
        if(input[1]==true) {
            locator = locator + "1";
        } else {
            locator = locator + "0";
        }
        if(input[2]==true) {
            locator = locator + "1";
        } else {
            locator = locator + "0";
        }
        if(input[3]==true) {
            locator = locator + "1";
        } else {
            locator = locator + "0";
        }
        
        int location = (15 - Integer.parseInt(locator, 2)) + 4;        
        return input[location];
    }
    
	@Override
	public Grammar getGrammar() {
		return grammar;
	}
}
