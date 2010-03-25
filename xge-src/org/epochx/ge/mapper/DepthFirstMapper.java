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
package org.epochx.ge.mapper;

import org.epochx.ge.codon.CodonGenerator;
import org.epochx.ge.model.GEAbstractModel;
import org.epochx.ge.representation.GECandidateProgram;
import org.epochx.life.*;
import org.epochx.tools.grammar.*;

/**
 * A depth first mapper is the classic form of GE mapper. It maps a 
 * GECandidateProgram's chromosome to a String by using each codon in order to 
 * choose between productions when constructing a derivation tree in 
 * depth-first order.
 * 
 * <p>It provides facility for wrapping and extending of chromosomes.
 */
public class DepthFirstMapper implements Mapper {
	/*
	 * TODO Write a non-recursive mapper like breadth first mapper and make a 
	 * secondary contructor with a boolean option.
	 */
	// The controlling model.
	private GEAbstractModel model;
	
	// Wrapping and extending are mutually exclusive, they cannot both be true.
	private boolean wrapping;
	private boolean extending;
	
	private boolean removingUnusedCodons;
	
	private int maxWraps;
	private int noMappedCodons;
	private int noWraps;
	
	private Grammar grammar;
	private int maxProgramDepth;
	private int maxChromosomeLength;
	private CodonGenerator codonGenerator;
	
	/**
	 * Constructs an instance of DepthFirstMapper.
	 * 
	 * @param model the controlling model providing configuration details such 
	 * 				as the Grammar.
	 */
	public DepthFirstMapper(GEAbstractModel model) {
		this.model = model;
		
		// Default to extending.
		wrapping = true;
		extending = false;
		
		// Max wraps limit.
		maxWraps = 3;
		
		// Default to removing unused codons.
		removingUnusedCodons = true;
		
		noMappedCodons = -1;
		noWraps = 0;
		
		// Configure parameters from the model.
		model.getLifeCycleManager().addConfigListener(new ConfigAdapter() {
			@Override
			public void onConfigure() {
				configure();
			}
		});
	}
	
	/*
	 * Configure component with parameters from the model.
	 */
	private void configure() {
		grammar = model.getGrammar();
		maxProgramDepth = model.getMaxProgramDepth();
		maxChromosomeLength = model.getMaxChromosomeLength();
		codonGenerator = model.getCodonGenerator();
	}
	
	/**
	 * Perform the mapping operation. Mapping starts at the starting symbol of 
	 * the grammar and gradually constructs a program source by stepping down 
	 * the grammar's tree and each time there are multiple productions to 
	 * choose from, the next codon from the <code>GECandidateProgram's</code> 
	 * chromosome is used. The codon simply undergoes modulo by the number of 
	 * production choices, the result is the index of the production to be 
	 * used.
	 * 
	 * @param program the GECandidateProgram to be mapped to its source.
	 * @return a source string equivalent to the specified GECandidateProgram's 
	 * chromosome after mapping using the model's grammar. Null is returned if 
	 * a valid string could not be generated.
	 */
	@Override
	public NonTerminalSymbol map(GECandidateProgram program) {
		// The root node/symbol of the grammar tree.
		GrammarRule grammarTree = grammar.getStartRule();
		// The root node/symbol of the parse tree.
		NonTerminalSymbol parseTree = new NonTerminalSymbol(grammarTree);
		
		noMappedCodons = map(grammar.getStartRule(), parseTree, 0, program, 0);
		
		if (noMappedCodons == -1) {
			return null;
		} else if (noWraps > 0) {
			// If we had to wrap, then all the codons were used - some more 
			// than once.
			noMappedCodons = program.getNoCodons();
		}
		
		// Before returning, remove all the unused codons.
		if (removingUnusedCodons) {
			program.removeCodons(noMappedCodons, program.getNoCodons());
		}
		
		return parseTree;
	}
	
	/*
	 * Recursive helper method to resolve mapping from codons to source.
	 */
	private int map(GrammarRule grammarRule, NonTerminalSymbol parseNode, int depth, GECandidateProgram program, int currentCodon) {
		// We add one to the depth because the current depth does not include terminals.
		if (depth+1 > maxProgramDepth-1) {
			return -1;
		}
		
		// Assume it must be a non-terminal symbol.
		int noProductions = grammarRule.getNoProductions();

		// If only one choice, no need to use codon.
		GrammarProduction next;
		if (noProductions == 1) {
			next = grammarRule.getProduction(0);
		} else {
			if (currentCodon == program.getNoCodons()) {
				if (extending) {
					// If there are no more codons we simply add one.
					if (program.getNoCodons() < maxChromosomeLength) {
						int newCodon = codonGenerator.getCodon();
						program.appendCodon(newCodon);
					} else {
						return -1;
					}
				} else if (wrapping) {
					// Start again at first codon.
					noWraps++;
					// Check that we're allowed this many wraps.
					if (noWraps > maxWraps) {
						return -1;
					}
					currentCodon = 0;
				} else {
					// We're not wrapping or extending so the chromosome is invalid.
					return -1;
				}
			}
			
			// Pick a production using the next codon.
			int codon = program.getCodon(currentCodon++);
			next = grammarRule.getProduction(codon % noProductions);
		}
		
		// Map each symbol of the production and add to source.
		for (GrammarNode s: next.getGrammarNodes()) {
			if (s instanceof GrammarLiteral) {
				parseNode.addChild(new TerminalSymbol((GrammarLiteral) s));
			} else {
				GrammarRule nextGrammarRule = (GrammarRule) s;
				NonTerminalSymbol nextParseNode = new NonTerminalSymbol(nextGrammarRule);
				parseNode.addChild(nextParseNode);
				
				currentCodon = map(nextGrammarRule, nextParseNode, depth+1, program, currentCodon);
				if (currentCodon == -1) {
					return -1;
				}
			}
		}
		
		return currentCodon;
	}

	/**
	 * Returns whether the wrapping operation is in use. If wrapping is being 
	 * used then if there are insufficient codons during mapping then they will 
	 * be wrapped, starting back at the first codon.
	 * 
	 * @return true if codons will undergo wrapping, false otherwise.
	 */
	public boolean isWrapping() {
		return wrapping;
	}

	/**
	 * Specifies whether the wrapping operation should be used. If wrapping is 
	 * turned on then when insufficient codons are available during mapping, 
	 * the chromosome will be wrapped, starting again with the first codon.
	 * 
	 * <p>Wrapping and extending are mutually exclusive. If one is set to be 
	 * used then the other will automatically be turned off.
	 * 
	 * @param wrapping whether wrapping should be used during mapping.
	 */
	public void setWrapping(boolean wrapping) {
		this.wrapping = wrapping;
		
		extending = !wrapping;
	}

	/**
	 * Specifies the maximum number of times a programs chromosome can be 
	 * wrapped before the program is classified as invalid. This limit only 
	 * applies when wrapping is turned on.
	 * 
	 * @param maxWraps the number times a programs chromosome is allowed to wrap
	 * during mapping.
	 */
	public void setMaxWraps(int maxWraps) {
		this.maxWraps = maxWraps;
	}
	
	/**
	 * Returns the maximum number of times a program's chromosome will be 
	 * allowed to wrap before the program is classified as invalid. Defaults to
	 * 3.
	 * 
	 * @return the maximum number of times a programs chromosome is allowed to 
	 * wrap by the mapping process.
	 */
	public int getMaxWraps() {
		return maxWraps;
	}
	
	/**
	 * Returns whether the extension operation is in use. If extending is being 
	 * used then if there are insufficient codons during mapping then new 
	 * codons will be generated using the models CodonGenerator and appended to 
	 * the end of the program's chromosome. Extension will only occur up to the
	 * maximum chromosome length parameter specified in the model, after which 
	 * programs will be considered illegal and the mapper will return null.
	 * 
	 * @return true if codons will undergo extension, false otherwise.
	 */
	public boolean isExtending() {
		return extending;
	}

	/**
	 * Specifies whether the extension operation should be used. If extension 
	 * is turned on then when insufficient codons are available during mapping, 
	 * the chromosome will be extended with a newly generated codon using the 
	 * model's CodonGenerator. If the chromosome has already met the model's 
	 * specified maximum length then the mapping operation will be abandoned 
	 * and null will be returned to signal an invalid individual. 
	 * 
	 * @param extending whether extension should be used during mapping.
	 */
	public void setExtending(boolean extending) {
		this.extending = extending;
		
		wrapping = !extending;
	}
	
	/**
	 * Returns whether unused codons at the end of a chromosome are removed 
	 * after mapping is complete.
	 * 
	 * @return true if unused codons are currently being removed, false 
	 * otherwise.
	 */
	public boolean isRemovingUnusedCodons() {
		return removingUnusedCodons;
	}
	
	/**
	 * Specifies whether unused codons at the end of a chromosome should be 
	 * removed or not after the mapping operation is completed.
	 * 
	 * @param remove whether unused codons should be removed or not.
	 */
	public void setRemovingUnusedCodons(boolean remove) {
		removingUnusedCodons = remove;
	}

	/**
	 * Returns the number of codons that were used in mapping the last 
	 * GECandidateProgram to be mapped, or -1 if no CandidatePrograms have yet 
	 * been mapped. The number of mapped codons constitutes the active portion 
	 * of a chromosome and refers to the first n codons of the program.
	 * 
	 * <p>Note that if mapping was used then some codons may have been used 
	 * multiple times.
	 * 
	 * @return the number of codons used during the last mapping process.
	 */
	@Override
	public int getNoMappedCodons() {
		return noMappedCodons;
	}

}
