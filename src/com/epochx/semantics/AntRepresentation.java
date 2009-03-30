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
package com.epochx.semantics;

import java.util.ArrayList;

/**
 * @author lb212
 *
 */
public class AntRepresentation implements Representation {
	
	private ArrayList<String> antRepresentation;
	
	/**
	 * Constructor for antRepresentation
	 * @param antRepresentation the new move sequence for this ant representation
	 */
	public AntRepresentation(ArrayList<String> antRepresentation) {
		this.antRepresentation = antRepresentation;
	}
	
	/**
	 * Returns the ants move sequence
	 * @return The ants move sequence
	 */
	public ArrayList<String> getAntRepresentation() {
		return antRepresentation;
	}

	/* (non-Javadoc)
	 * @see com.epochx.semantics.Representation#equals(com.epochx.semantics.Representation)
	 */
	@Override
	public boolean equals(Representation anotherBehaviour) {
		if(anotherBehaviour instanceof AntRepresentation) {
			AntRepresentation antRep = (AntRepresentation) anotherBehaviour;
			if(antRep.getAntRepresentation().equals(this.antRepresentation)) {
				return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.epochx.semantics.Representation#isConstant()
	 */
	@Override
	public boolean isConstant() {
		if(!antRepresentation.contains("M")) {
			return true;
		} else {
			return false;
		}
	}

}
