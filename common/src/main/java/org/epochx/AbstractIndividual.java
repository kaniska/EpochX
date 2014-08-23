/* 
 * Copyright 2007-2013
 * Licensed under GNU Lesser General Public License
 * 
 * This file is part of EpochX
 * 
 * EpochX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * EpochX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with EpochX. If not, see <http://www.gnu.org/licenses/>.
 * 
 * The latest version is available from: http:/www.epochx.org
 */
package org.epochx;

/**
 * 
 */
public abstract class AbstractIndividual implements Individual {

	private static final long serialVersionUID = -4321760091640776785L;
	
	private Fitness fitness;

	/**
	 * @param fitness the fitness to set
	 */
	public void setFitness(Fitness fitness) {
		this.fitness = fitness;
	}
	
	/* (non-Javadoc)
	 * @see org.epochx.Individual#getFitness()
	 */
	@Override
	public Fitness getFitness() {
		return fitness;
	}

	public AbstractIndividual clone() {
		try {
			AbstractIndividual clone = (AbstractIndividual) super.clone();
			clone.fitness = fitness;
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
	}
}
