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
package com.epochx.example.artificialant;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

import com.epochx.action.*;
import com.epochx.ant.*;
import com.epochx.core.*;
import com.epochx.op.crossover.*;
import com.epochx.op.initialisation.*;
import com.epochx.op.selection.*;
import com.epochx.representation.*;
import com.epochx.representation.action.*;
import com.epochx.stats.*;
import com.epochx.util.FileManip;

/**
 * 
 */
public class ArtificialAntSantaFe extends GPAbstractModel<Action> {

	private List<String> inputs = new ArrayList<String>();;
	private HashMap<String, TerminalNode<Action>> variables = new HashMap<String, TerminalNode<Action>>();
	private Dimension dimension = new Dimension(32, 32);	
	private ArrayList<Point> foodLocations = new ArrayList<Point>();
	private AntLandscape antLandscape = new AntLandscape(dimension, null);
	private Ant ant = new Ant(600, antLandscape);
	private int noOfFoodPellets;
	private int fitAssessed = 0;
	
	public ArtificialAntSantaFe() {		
		inputs = FileManip.loadInput(new File("inputsantafe.txt"));
		// create list of food locations
		foodLocations = new ArrayList<Point>();
		for(String i: inputs) {
			if(!i.equalsIgnoreCase("DC")) {
				String[] parts = i.split(":");
				int x = Integer.parseInt(parts[0]);
				int y = Integer.parseInt(parts[1]);
				Point p = new Point(x, y);
				foodLocations.add(p);
			}
		}
		
		noOfFoodPellets = foodLocations.size();
		
		configure();
	}
	
	public void configure() {
		// Define variables.
		variables.put("MOVE", new TerminalNode<Action>(new AntMoveAction(ant)));
		variables.put("TURN-LEFT", new TerminalNode<Action>(new AntTurnLeftAction(ant)));
		variables.put("TURN-RIGHT", new TerminalNode<Action>(new AntTurnRightAction(ant)));
		
		setGenStatFields(new GenerationStatField[]{GenerationStatField.FITNESS_MIN, GenerationStatField.FITNESS_AVE, GenerationStatField.LENGTH_AVE, GenerationStatField.RUN_TIME});
		setRunStatFields(new RunStatField[]{RunStatField.BEST_FITNESS, RunStatField.BEST_PROGRAM, RunStatField.RUN_TIME});
		
		setPopulationSize(500);
		setNoGenerations(10);
		setCrossoverProbability(0.9);
		setMutationProbability(0.0);
		setNoRuns(1);
		setPoolSize(50);
		setNoElites(50);
		setInitialMaxDepth(6);
		setMaxDepth(5);
		setPoolSelector(new TournamentSelector<Action>(7));
		setProgramSelector(new RandomSelector<Action>());
		setCrossover(new UniformPointCrossover<Action>());
		setInitialiser(new RampedHalfAndHalfInitialiser<Action>(this));
	}
	
	@Override
	public List<FunctionNode<Action>> getFunctions() {
		// Define functions.
		List<FunctionNode<Action>> functions = new ArrayList<FunctionNode<Action>>();
		functions.add(new IfFoodAheadFunction(ant, antLandscape));
		functions.add(new Seq2Function());
		functions.add(new Seq3Function());
		return functions;
	}

	@Override
	public List<TerminalNode<Action>> getTerminals() {		
		// Define terminals.
		List<TerminalNode<Action>> terminals = new ArrayList<TerminalNode<Action>>();
		terminals.add(variables.get("MOVE"));
		terminals.add(variables.get("TURN-LEFT"));
		terminals.add(variables.get("TURN-RIGHT"));		
		return terminals;
	}
	
	/* (non-Javadoc)
	 * @see com.epochx.core.GPModel#getFitness(com.epochx.core.representation.CandidateProgram)
	 */
	@Override
	public double getFitness(CandidateProgram<Action> program) {
		fitAssessed++;
		//System.out.println(program.getRootNode());
		//System.out.println(fitAssessed);
		// refresh food list before evaluation
		// hard copy foodLocations
		ArrayList<Point> fl = new ArrayList<Point>();
		for(Point p: foodLocations) {
			fl.add(p);
		}
		//System.out.println(fl);
		antLandscape.setFoodLocations(fl);
		ant.reset(600, antLandscape);
		//System.out.println("A1 = " + ant);
		// run the ant
		while(ant.getMoves()<ant.getMaxMoves()) {
			program.evaluate();
		}
		//System.out.println("A2 = " + ant);
		// calculate score
		double score = (double) (noOfFoodPellets - ant.getFoodEaten());
		//System.out.println(score);
		return score;
	}
	
	public Ant getAnt() {
		return ant;
	}
	
	public AntLandscape getAntLandScape() {
		return antLandscape;
	}
	
	public static void main(String[] args) {
		GPController.run(new ArtificialAntSantaFe());
	}
}
