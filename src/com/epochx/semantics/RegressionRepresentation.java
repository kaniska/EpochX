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

import java.util.*;

import com.epochx.core.representation.Node;
import com.epochx.core.representation.TerminalNode;
import com.epochx.func.dbl.CoefficientExponentFunction;

/**
 * @author Lawrence Beadle & Tom Castle
 */
public class RegressionRepresentation implements Representation {
	
	private ArrayList<CoefficientExponentFunction> regressionRepresentation;
	
	/**
	 * Constructor for regression representation object
	 * @param regressionRepresentation list of the coefficients
	 */
	public RegressionRepresentation(ArrayList<CoefficientExponentFunction> regressionRepresentation) {
		this.regressionRepresentation = regressionRepresentation;
	}
	
	/**
	 * Constructor for repression representation object - will create blank representation
	 */
	public RegressionRepresentation() {
		this.regressionRepresentation = new ArrayList<CoefficientExponentFunction>();
	}
	
	/**
	 * Returns the regression representation (the formula coefficients)
	 * @return A list of the formula coefficients
	 */
	public ArrayList<CoefficientExponentFunction> getRegressionRepresentation() {
		return regressionRepresentation;
	}
	
	/**
	 * Simplifies any CVPs with same variable and power
	 */
	/*public void simplify() {
		for(int i = 0; i<regressionRepresentation.size()-1; i++) {
			CoefficientExponentFunction a = regressionRepresentation.get(i);
			for(int j = i+1; j<regressionRepresentation.size(); j++) {
				CoefficientExponentFunction b = regressionRepresentation.get(j);
				if(a.getChild(1).equals(b.getChild(1)) && a.getChild(2).equals(b.getChild(2))) {
					double coefficient1 = (Double) a.getChild(0).evaluate();
					double coefficient2 = (Double) b.getChild(0).evaluate();
					double newCoefficient = coefficient1 + coefficient2;
					a = new CoefficientExponentFunction(new TerminalNode<Double>(newCoefficient),(Node<Double>) a.getChild(1),(Node<Double>) a.getChild(2));
					regressionRepresentation.remove(j);
				}
			}
		}
	}*/
	
	public void simplify() {
		outer: for (int i=0; i<regressionRepresentation.size(); i++) {
			CoefficientExponentFunction cvp1 = regressionRepresentation.get(i);
			Node<Double> coefficient1 = (Node<Double>) cvp1.getChild(0);
			Node<Double> term1 = (Node<Double>) cvp1.getChild(1);
			Node<Double> exponent1 = (Node<Double>) cvp1.getChild(2);

			// Compare against every one AFTER it in the list.
			for (int j=i+1; j<regressionRepresentation.size(); j++) {
				CoefficientExponentFunction cvp2 = regressionRepresentation.get(j);
				Node<Double> coefficient2 = (Node<Double>) cvp2.getChild(0);
				Node<Double> term2 = (Node<Double>) cvp2.getChild(1);
				Node<Double> exponent2 = (Node<Double>) cvp2.getChild(2);
				
				if (term1.equals(term2) && exponent1.equals(exponent2)) {
					double newCoefficient = coefficient1.evaluate() + coefficient2.evaluate();
					
					// Update the second element with the new coefficient.
					cvp2.setChild(new TerminalNode<Double>(newCoefficient), 0);
					
					// Nullify the current one and then we'll skip to the next...
					regressionRepresentation.set(i, null);
					
					// Once we've done one merge go onto the next element - others will be caught later.
					continue outer;
				}
			}
		}
		
		// Add non-nulls to this new list.
		List<CoefficientExponentFunction> combinedCVPs = new ArrayList<CoefficientExponentFunction>();
		for (CoefficientExponentFunction cvp: regressionRepresentation) {
			if (cvp != null) {
				combinedCVPs.add(cvp);
			}
		}
		
		// Clear the old list.
		regressionRepresentation.clear();
		
		// Then throw the new ones back in to the old list.
		regressionRepresentation.addAll(combinedCVPs);
	}
	
	public void order() {
		Collections.sort(regressionRepresentation, new Comparator<CoefficientExponentFunction>(){
			@Override
			public int compare(CoefficientExponentFunction cvp1,
							   CoefficientExponentFunction cvp2) {
				double power1 = ((Node<Double>) cvp1.getChild(2)).evaluate();
				double power2 = ((Node<Double>) cvp2.getChild(2)).evaluate();
				
				return Double.compare(power1, power2);
			}
		});
	}
	
	public String toString() {
		String output = "";
		for(CoefficientExponentFunction c: regressionRepresentation) {
			output = output + c.toString() + " ";
		}
		return output;
	}

	/* (non-Javadoc)
	 * @see com.epochx.semantics.Representation#equals(com.epochx.semantics.Representation)
	 */
	@Override
	public boolean equals(Representation anotherBehaviour) {
		if(anotherBehaviour instanceof RegressionRepresentation) {
			RegressionRepresentation regRep = (RegressionRepresentation) anotherBehaviour;
			if(regRep.getRegressionRepresentation()==this.regressionRepresentation) {
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
		// if length = 1 there is only a constant in the x side of f(x)
		if(regressionRepresentation.size()==1) {
			if(regressionRepresentation.get(0).getChild(2).equals(new TerminalNode<Double>(0d))) {
				return true;
			}
			if(regressionRepresentation.get(0).getChild(2).equals(new TerminalNode<Double>(-0d))) {
				return true;
			}
			
		}
		return false;
	}

}
