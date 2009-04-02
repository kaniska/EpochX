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
package com.epochx.core.initialisation;

import java.util.*;
import com.epochx.core.GPModel;
import com.epochx.core.representation.*;
import com.epochx.semantics.*;
import net.sf.javabdd.*;

/**
 * @author lb212
 *
 */
public class HybridBooleanSemanticallyDrivenInitialiser<TYPE> implements Initialiser<TYPE> {

	private GPModel<TYPE> model;
	private BooleanSemanticModule semMod;
	
	public HybridBooleanSemanticallyDrivenInitialiser(GPModel<TYPE> model, SemanticModule semMod) {
		this.model = model;
		this.semMod = (BooleanSemanticModule) semMod;
	}
	
	/* (non-Javadoc)
	 * @see com.epochx.core.initialisation.Initialiser#getInitialPopulation()
	 */
	@Override
	public List<CandidateProgram<TYPE>> getInitialPopulation() {
		return generatePopulation();
	}
	
	private List<CandidateProgram<TYPE>> generatePopulation() {
		// initialise BDD stuff
        semMod.start();
        List<BDD> storage = new ArrayList<BDD>();
        FullInitialiser<TYPE> f = new FullInitialiser<TYPE>(model);
        List<CandidateProgram<TYPE>> firstPass = f.getInitialPopulation();
        
        // generate a full population to start with
        for(CandidateProgram<TYPE> c: firstPass) {
        	BooleanRepresentation b = semMod.codeToBehaviour(c);
        	if(!b.isConstant()) {
        		storage.add(b.getBDD());
        	}
        }

        // create random number generator
        Random random = new Random();
        int noOfFunctions = model.getFunctions().size();
        // mash together rest to make full pop
        while(storage.size()<model.getPopulationSize()) {
            int cFunc = random.nextInt(noOfFunctions);
            BDD result = null;
            if(cFunc==0) {
                result = storage.get(random.nextInt(storage.size())).ite(storage.get(random.nextInt(storage.size())), storage.get(random.nextInt(storage.size())));
            } else if(cFunc==1) {
                result = storage.get(random.nextInt(storage.size())).and(storage.get(random.nextInt(storage.size())));
            } else if(cFunc==2) {
                result = storage.get(random.nextInt(storage.size())).or(storage.get(random.nextInt(storage.size())));
            } else if(cFunc==3) {
                result = storage.get(random.nextInt(storage.size())).not();
            }
            // check unique
            if(!storage.contains(result) && !(result.nodeCount()<1)) {
                storage.add(result);
            }
        }
        
        // translate back and add to first gen
        List<CandidateProgram<TYPE>> firstGen = new ArrayList<CandidateProgram<TYPE>>();
        for(BDD toProg: storage) {
            firstGen.add(semMod.behaviourToCode(new BooleanRepresentation(toProg)));
        }
        
        // clear up BDD stuff
        semMod.stop();
        
        return firstGen;
	}	
}