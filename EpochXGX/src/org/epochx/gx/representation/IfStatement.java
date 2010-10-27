package org.epochx.gx.representation;

import java.util.*;

import org.epochx.gx.op.init.*;
import org.epochx.gx.tools.util.*;
import org.epochx.tools.random.*;

public class IfStatement extends BlockStatement {

	private Expression condition;
	
	private Block ifCode;
	
	private Block elseCode;
	
	public IfStatement(Expression condition, Block ifCode, Block elseCode) {
		this.condition = condition;
		this.ifCode = ifCode;
		this.elseCode = elseCode;
	}
	
	public IfStatement(Expression condition, Block ifCode) {
		this(condition, ifCode, null);
	}
	
	public Block getIfCode() {
		return ifCode;
	}
	
	@Override
	public void apply(VariableHandler vars) {
		// Variable scope will hide any new variables here so do nothing.
	}

	@Override
	public void evaluate(VariableHandler vars) {
		Boolean result = (Boolean) condition.evaluate(vars);
		if (result) {
			ifCode.evaluate(vars);
		} else if (elseCode != null){
			elseCode.evaluate(vars);
		}
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		
		buffer.append("if (");
		buffer.append(condition.toString());
		buffer.append(")");
		buffer.append(ifCode.toString());
		if (elseCode != null) {
			buffer.append(" else ");
			buffer.append(elseCode.toString());
		}
		
		return buffer.toString();
	}
	
	@Override
	public IfStatement clone() {
		IfStatement clone = (IfStatement) super.clone();
		
		clone.condition = this.condition.clone();
		clone.ifCode = this.ifCode.clone();
		
		if (this.elseCode != null) {
			clone.elseCode = this.elseCode.clone();
		}
			
		return clone;
	}

	@Override
	public void modifyExpression(double probability, RandomNumberGenerator rng, VariableHandler vars) {
		double rand = rng.nextDouble();
		
		if (rand < probability) {
			condition = ProgramGenerator.getExpression(rng, vars, condition.getDataType(), 0);
		} else {
			condition.modifyExpression(probability, rng, vars, 0);
		}
		
		// Record the number of active variables.
		int noActive = vars.getNoActiveVariables();
		
		ifCode.modifyExpression(probability, rng, vars);
		
		// Remove any variables declared within.
		vars.setNoActiveVariables(noActive);
		
		if (elseCode != null) {
			elseCode.modifyExpression(probability, rng, vars);
			
			// Remove any variables declared within.
			vars.setNoActiveVariables(noActive);
		}
	}

	@Override
	public int getNoStatements() {
		int noStatements = 1 + ifCode.getNoStatements();
		
		if (elseCode != null) {
			noStatements += elseCode.getNoStatements();
		}
		
		return noStatements;
	}
	
	@Override
	public void insertStatement(double probability, RandomNumberGenerator rng, VariableHandler vars, int maxNoStatements, int loopDepth) {
		// Record the number of active variables.
		int noActive = vars.getNoActiveVariables();
		
		ifCode.insertStatement(probability, rng, vars, maxNoStatements, loopDepth);
		
		// Remove any variables declared within.
		vars.setNoActiveVariables(noActive);
	}
	
	@Override
	public Statement deleteStatement(int deletePosition) {
		return ifCode.deleteStatement(deletePosition);
	}

	public Expression getCondition() {
		return condition;
	}
	
	@Override
	public Statement getStatement(int index) {
		if (index == 0) {
			return this;
		} else {
			return ifCode.getStatement(index-1);
		}
	}

	@Override
	public Set<Variable> getDeclaredVariables() {
		return ifCode.getDeclaredVariables();
	}

	@Override
	public Set<Variable> getUsedVariables() {
		Set<Variable> variables = ifCode.getUsedVariables();

		variables.addAll(condition.getUsedVariables());
		
		return variables;
	}
	
	@Override
	public Declaration getDeclaration(Variable v) {
		return ifCode.getDeclaration(v);
	}
	
	public int getNoInsertPoints() {
		return ifCode.getNoInsertPoints();
	}
	
	public void insertStatements(int insertPoint, List<Statement> swapStatements) {
		ifCode.insertStatements(swapStatements, insertPoint);
	}

	@Override
	public void copyVariables(VariableHandler vars, Map<Variable, Variable> variableCopies) {
		condition = VariableUtils.copyVariable(condition, variableCopies, vars);
		
		ifCode.copyVariables(vars, variableCopies);
	}
	

	@Override
	public int getDepth() {
		return 1 + ifCode.getDepth();
	}
	
	@Override
	public int getLoopDepth() {
		return ifCode.getLoopDepth();
	}

	@Override
	public int getDepthOfInsertPoint(int insertPoint) {
		return ifCode.getDepthOfInsertPoint(insertPoint);
	}
	
	@Override
	public int getLoopDepthOfInsertPoint(int insertPoint) {
		return ifCode.getLoopDepthOfInsertPoint(insertPoint);
	}

	@Override
	public void deleteStatement(Statement toDelete) {
		ifCode.deleteStatement(toDelete);
	}
}
