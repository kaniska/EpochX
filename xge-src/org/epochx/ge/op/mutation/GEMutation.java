/*  
 *  Copyright 2009 Tom Castle
 *  Licensed under GNU General Public License
 * 
 *  This file is part of XGE: grammatical evolution for research
 *
 *  XGE is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  XGE is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with XGE.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.epochx.ge.op.mutation;

import org.epochx.ge.representation.GECandidateProgram;
import org.epochx.op.Mutation;
import org.epochx.representation.CandidateProgram;

/**
 * This interface defines the structure which specific mutation operators can
 * implement to provide different methods of mutating a <code>GECandidateProgram</code>. 
 * GPMutation instances are used by the core GRMutation class to perform a single 
 * mutation operation.
 * 
 * @see org.epochx.ge.model.GRMutation
 */
public interface GEMutation extends Mutation {

	/**
	 * Implementations should perform some form of alteration to the genetic 
	 * material of the given GECandidateProgram, returning the resultant program.
	 * 
	 * @param program The GECandidateProgram selected to undergo this mutation 
	 * 				  operation.
	 * @return A GECandidateProgram that was the result of altering the provided 
	 * GECandidateProgram.
	 */
	@Override
	public GECandidateProgram mutate(CandidateProgram program);
		
}