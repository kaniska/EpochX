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
package org.epochx.example.ant;

import static org.epochx.gp.stats.GPStatField.*;

import org.epochx.gp.op.crossover.*;
import org.epochx.gp.op.init.FullInitialiser;
import org.epochx.gp.op.mutation.SubtreeMutation;
import org.epochx.life.*;
import org.epochx.op.selection.*;


/**
 * 
 */
public class SantaFeTrail extends org.epochx.gp.model.SantaFeTrail {
	
	public SantaFeTrail() {
		setPopulationSize(10000);
		setNoGenerations(100);
		setCrossoverProbability(0.9);
		setMutationProbability(0.1);
		setNoRuns(100);
		//setPoolSize(-1);
		setNoElites(50);
		setMaxInitialDepth(6);
		setMaxDepth(17);
		setPoolSelector(null);
		setProgramSelector(new TournamentSelector(this, 3));
		setCrossover(new KozaCrossover(this));
		setMutation(new SubtreeMutation(this));
		setInitialiser(new FullInitialiser(this));

		getLifeCycleManager().addGenerationListener(new GenerationAdapter() {
			@Override
			public void onGenerationEnd() {
				getStatsManager().printGenerationStats(GEN_NUMBER, GEN_FITNESS_MIN, GEN_FITNESS_AVE, GEN_DEPTH_AVE, GEN_DEPTH_MAX);
			}
		});
		
		getLifeCycleManager().addRunListener(new RunAdapter() {
			@Override
			public void onRunEnd() {
				getStatsManager().printRunStats(RUN_NUMBER, RUN_FITNESS_MIN, RUN_FITTEST_PROGRAM);
			}
		});
	}
	
	public static void main(String[] args) {
		new SantaFeTrail().run();
	}
}
