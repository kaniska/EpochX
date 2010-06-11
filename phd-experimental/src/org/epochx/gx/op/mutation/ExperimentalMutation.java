package org.epochx.gx.op.mutation;

import org.epochx.gx.model.*;
import org.epochx.gx.representation.*;
import org.epochx.life.*;
import org.epochx.representation.*;
import org.epochx.tools.random.*;

public class ExperimentalMutation implements GXMutation {

	// The controlling model.
	private GXModel model;
	
	private RandomNumberGenerator rng;
	
	public ExperimentalMutation(final GXModel model) {
		this.model = model;
		
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
		rng = model.getRNG();
	}
	
	@Override
	public GXCandidateProgram mutate(final CandidateProgram p) {
		GXCandidateProgram program = (GXCandidateProgram) p;
		
		double random = rng.nextDouble();
		
		if (random < 0.2) {
			// Insert statement.
			insertStatement(program.getAST());
		} else if (random < 0.4) {
			// Delete statement.
			
		} else {
			// Modify expression.
			
		}
		
		return null;
	}
	
	private void insertStatement(AST ast) {
		
	}
	
	private void deleteStatement() {
		
	}
	
	private void modifyExpression() {
		
	}

}
