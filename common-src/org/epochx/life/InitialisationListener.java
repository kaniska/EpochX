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
package org.epochx.life;

import java.util.List;

import org.epochx.representation.CandidateProgram;


/**
 * Defines the initialisation events which may be raised.
 */
public interface InitialisationListener {

	/**
	 * Event raised before the initialisation operation starts.
	 */
	public void onInitialisationStart();
	
	/**
	 * Event raised after a population has been initialised. The initial 
	 * population may be modified and returned. This event is revertable by 
	 * returning null which will trigger the discarding of the population and 
	 * a reattempt at initialisation with this event being raised again after. 
	 * If the initialisation should be accepted then the population should be 
	 * returned as it is.
	 * 
	 * @param pop the newly initialised population.
	 * @return the population of CandidatePrograms to continue with as the 
	 * newly initialised population, or null if initialisation should be rerun.
	 */
	public List<CandidateProgram> onInitialisation(List<CandidateProgram> pop);
	
	/**
	 * Event raised after the initialisation operation has ended and been accepted.
	 */
	public void onInitialisationEnd();
}
