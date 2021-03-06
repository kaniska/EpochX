package org.epochx.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.epochx.BranchedBreeder;
import org.epochx.Config;
import org.epochx.Config.Template;
import org.epochx.Evolver;
import org.epochx.FitnessEvaluator;
import org.epochx.GenerationalStrategy;
import org.epochx.GenerationalTemplate;
import org.epochx.Initialiser;
import org.epochx.MaximumGenerations;
import org.epochx.Operator;
import org.epochx.Population;
import org.epochx.Reproduction;
import org.epochx.TerminationCriteria;
import org.epochx.TerminationFitness;
import org.epochx.event.EventManager;
import org.epochx.event.GenerationEvent.EndGeneration;
import org.epochx.event.Listener;
import org.epochx.event.RunEvent.EndRun;
import org.epochx.event.stat.AbstractStat;
import org.epochx.event.stat.GenerationAverageDoubleFitness;
import org.epochx.event.stat.GenerationBestFitness;
import org.epochx.event.stat.GenerationBestIndividuals;
import org.epochx.event.stat.GenerationFitnessDiversity;
import org.epochx.event.stat.GenerationNumber;
import org.epochx.event.stat.GenerationWorstFitness;
import org.epochx.monitor.Monitor;
import org.epochx.monitor.chart.Chart;
import org.epochx.monitor.chart.ChartTrace;
import org.epochx.monitor.table.Table;
import org.epochx.refactoring.PopulationNeutrality;
import org.epochx.refactoring.Problem;
import org.epochx.refactoring.initialisation.RampedHalfAndHalf;
import org.epochx.refactoring.initialisation.TreeFactory;
import org.epochx.refactoring.operator.Mutation;
import org.epochx.refactoring.operator.NeutralAwareMutation;
import org.epochx.refactoring.problem.EvenParity;
import org.epochx.refactoring.representation.CoverageFitness;
import org.epochx.selection.TournamentSelector;

public class MonitorFrameTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Config config = Config.getInstance();
		config.set(Template.KEY, new GenerationalTemplate());

		// the problem instance

		// Problem problem = new Multiplexer(2);
		// SexticPolynomial polynomial = new SexticPolynomial();
		Problem problem = new EvenParity(5);
		config.set(Initialiser.METHOD, new RampedHalfAndHalf(problem.getFunctionSet(), problem.getTerminalSet()));
		// config.set(Initialiser.METHOD, new
		// FullMethod(problem.getFunctionSet(), problem.getTerminalSet()));
		config.set(FitnessEvaluator.FUNCTION, problem);

		// some parameters

		config.set(Population.SIZE, 100);
		// config.set(Crossover.PROBABILITY, 0.9);
		// config.set(Reproduction.PROBABILITY, 0.1);
		config.set(BranchedBreeder.ELITISM, 0);
		config.set(MaximumGenerations.MAXIMUM_GENERATIONS,100);
		config.set(TreeFactory.MAX_DEPTH, 17);
		config.set(TreeFactory.INITIAL_DEPTH, 6);
		config.set(TournamentSelector.TOURNAMENT_SIZE, 4);
		config.set(NeutralAwareMutation.NEUTRAL_MOVES_ENABLED, true);

		// genetic operators

		config.set(Reproduction.PROBABILITY, 0.10);
		config.set(Mutation.PROBABILITY, 0.90);

		List<Operator> operators = new ArrayList<Operator>();
		operators.add(new Reproduction());
		operators.add(new Mutation());

		// operators.add(new StrictInflateMutation());
		// operators.add(new StrictDeflateMutation());
		// operators.add(new PointTerminalMutation());

		config.set(BranchedBreeder.OPERATORS, operators);

		// termination criteria

		List<TerminationCriteria> criteria = new ArrayList<TerminationCriteria>();
		criteria.add(new MaximumGenerations());
		criteria.add(new TerminationFitness(new CoverageFitness(0)));
		config.set(GenerationalStrategy.TERMINATION_CRITERIA, criteria);

		AbstractStat.register(PopulationNeutrality.class);

		// our stats monitor

		Monitor monitor = new Monitor("Monitor Frame", 1, 2);
		
		final Table table1 = new Table("Fitnesses Table");
		table1.addStat(GenerationNumber.class);
		table1.addStat(GenerationBestFitness.class);
		table1.addStat(GenerationWorstFitness.class);
		table1.addStat(GenerationAverageDoubleFitness.class);
		table1.addListener(EndGeneration.class);

		monitor.add(table1, 1, 1);
		
		Table table2 = new Table("Fitness Diversity Table");
		table2.addStat(GenerationNumber.class);
		table2.addStat(GenerationFitnessDiversity.class);
		table2.addListener(EndGeneration.class);

		monitor.add(table2, 1, 2);
		
		Table table3 = new Table("Best Individual Table");
		table3.addStat(GenerationNumber.class);
		table3.addStat(GenerationBestIndividuals.class);
		table3.addListener(EndGeneration.class);

		monitor.add(table3, 1, 1);
		
		Chart chart1 = new Chart("Fitnesses Chart");
		
		ChartTrace trace1 = new ChartTrace();
		trace1.setXStat(GenerationNumber.class);
		trace1.setYStat(GenerationBestFitness.class);
		chart1.addTrace(trace1);
		
		ChartTrace trace2 = new ChartTrace();
		trace2.setXStat(GenerationNumber.class);
		trace2.setYStat(GenerationWorstFitness.class);
		chart1.addTrace(trace2);
		
		ChartTrace trace3 = new ChartTrace();
		trace3.setXStat(GenerationNumber.class);
		trace3.setYStat(GenerationAverageDoubleFitness.class);
		chart1.addTrace(trace3);
	
		/*
		ChartTrace traceTab[] = new ChartTrace[1000];
		
		for(int i = 0; i<1000; i++) {
			traceTab[i] = new ChartTrace(chart1);
			traceTab[i].setXStat(GenerationNumber.class);
			traceTab[i].setYStat(GenerationFitnessDiversity.class);
		}
		//*/
				
		chart1.addListener(EndGeneration.class);
		
		monitor.add(chart1, 1, 2);
		
		Chart chart2 = new Chart("Fitness Diversity Chart");
		
		ChartTrace trace4 = new ChartTrace();
		trace4.setXStat(GenerationNumber.class);
		trace4.setYStat(GenerationFitnessDiversity.class);
		chart2.addTrace(trace4);
		
		chart2.addListener(EndGeneration.class);
		
		monitor.add(chart2, 1, 2);
		
		// we are ready to go!

		Evolver evolver = new Evolver();
		@SuppressWarnings("unused")
		Population population = evolver.run();
	}
}
