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
package com.epochx.core.selection;

import java.util.*;

import com.epochx.core.representation.*;

/**
 * 
 */
public class RandomParentSelector<TYPE> implements ParentSelector<TYPE>, PouleSelector<TYPE> {

	@Override
	public CandidateProgram<TYPE> getParent(List<CandidateProgram<TYPE>> pop) {		
		return pop.get((int) Math.floor(Math.random()*pop.size()));
	}

	@Override
	public List<CandidateProgram<TYPE>> getPoule(List<CandidateProgram<TYPE>> pop, int pouleSize) {
		// If pouleSize is 0 or less then we use the whole population.
		if (pouleSize <= 0) {
			return pop;
		}
		
		List<CandidateProgram<TYPE>> poule = new ArrayList<CandidateProgram<TYPE>>(pouleSize);
		
		for (int i=0; i<pouleSize; i++) {
			poule.add(getParent(pop));
		}
		
		return poule;
	}

}
