package org.epochx.gx.representation;

import java.util.*;

import org.epochx.gx.op.init.*;
import org.epochx.tools.random.*;

public class Block implements Cloneable {

	private List<Statement> statements;
	
	public Block(List<Statement> statements) {
		this.statements = statements;
	}
	
	public void insertStatement(double probability, RandomNumberGenerator rng, VariableHandler vars, int maxNoStatements) {
		// The number of insert points at this level.
		int noInsertPoints = statements.size() + 1;
		
		for (int i=0; i<noInsertPoints; i++) {
			if (getNoStatements() >= maxNoStatements) {
				break;
			}
			
			double rnd = rng.nextDouble();
			
			if (rnd < probability) {
				int maxNestedStatements = maxNoStatements - getNoStatements() - 1;
				
				// Generate new statement.
				Statement newStatement = ProgramGenerator.getStatement(rng, vars, 0, maxNestedStatements);
				
				// Insert at ith point.
				statements.add(i, newStatement);
				
				// The number of insert points has just increased.
				noInsertPoints++;
				i++;
			} else if (i < statements.size()) {
				Statement s = statements.get(i);
				if (s.hasBlock()) {
					/*
					 * Max allowed at next level is amount unused at this level, plus the amount that are already inside the block.
					 */
					int maxNestedStatements = (maxNoStatements - getNoStatements()) + (s.getNoStatements() - 1);
					
					// Step into the block.
					s.insertStatement(probability, rng, vars, maxNestedStatements);
				} else {
					// Apply the statement.
					s.apply(vars);
				}
			}
		}
	}
	
	public List<Statement> getStatements() {
		return statements;
	}
	
	public int getNoStatements() {
		List<Statement> topStatements = getStatements();
		int noStatements = 0;
		for (Statement s: topStatements) {
			noStatements += s.getNoStatements();
		}
		
		return noStatements;
	}
	
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(" {\n");
		
		for (Statement s: statements) {
			buffer.append(s.toString());
			buffer.append('\n');
		}
		
		buffer.append("}");

		return buffer.toString();
	}
	
	@Override
	public Block clone() {
		Block clone = null;
		try {
			clone = (Block) super.clone();
		} catch (CloneNotSupportedException e) {
			assert false;
		}
		
		clone.statements = new ArrayList<Statement>();
		for (Statement s: this.statements) {
			clone.statements.add((Statement) s.clone());
		}
		
		return clone;
	}
	
	public void modifyExpression(double probability, RandomNumberGenerator rng, VariableHandler vars) {
		for (Statement s: statements) {
			s.modifyExpression(probability, rng, vars);
			s.apply(vars);
		}
	}
	
	public void evaluate(VariableHandler vars) {
		// Evaluate each statement.
		for (Statement s: statements) {
			s.evaluate(vars);
		}
	}

	public Statement deleteStatement(int deletePosition) {
		int current = 0;
		for (int i=0; i<statements.size(); i++) {
			if (current == deletePosition) {
				Statement toDelete = statements.get(i);
				if (toDelete instanceof Declaration) {
					// Only allow declaration to be removed if that variable is not referenced anywhere else.
					Variable v = ((Declaration) toDelete).getVariable();
					if (countVariableUses(v) != 1) {
						return null;
					}
				}
				return statements.remove(i);
			}
			
			// Does index lie within this statement.
			Statement s = statements.get(i);
			int noStatements = s.getNoStatements();
			if (deletePosition < (current + noStatements)) {
				// Position is inside this statement.
				return s.deleteStatement(deletePosition-current-1);
			}
				
			current += noStatements;
		}
		
		return null;
	}
	
	public void addStatement(Statement s) {
		statements.add(s);
	}
	
	public void removeStatement(Statement s) {
		statements.remove(s);
	}

	public Statement getStatement(int index) {
		int i = 0;
		for (Statement s: statements) {
			if (i == index) {
				return s;
			} else {
				int noStatements = s.getNoStatements();
				if (index < i+noStatements) {
					return s.getStatement(index-i);
				}
				i += s.getNoStatements();
			}
		}
		return null;
	}
	
	public int countVariableUses(Variable v) {
		String blockStr = toString();
		String varName = v.getVariableName();
		int lastIndex = 0;
		int count = 0;

		while (lastIndex != -1) {
			lastIndex = blockStr.indexOf(varName, lastIndex+1);

			if (lastIndex != -1) {
				count++;
			}
		}
		
		return count;
	}
	
	/**
	 * Returns a list of those variables that get declared in this statement or
	 * any nested statements.
	 * 
	 * @return
	 */
	public Set<Variable> getDeclaredVariables() {
		Set<Variable> variables = new HashSet<Variable>();
		for (Statement s: statements) {
			variables.addAll(s.getDeclaredVariables());
		}
		
		return variables;
	}
	
	/**
	 * Returns a list of those variables that are used in expressions within 
	 * this statement or any nested statements. It does not include those 
	 * variables that are declared but unused in expressions.
	 * 
	 * @return
	 */
	public Set<Variable> getUsedVariables() {
		Set<Variable> variables = new HashSet<Variable>();
		for (Statement s: statements) {
			variables.addAll(s.getUsedVariables());
		}
		
		return variables;
	}

	/**
	 * Returns the declaration statement for the given variable if one exists
	 * else returns null.
	 * @param v
	 * @return
	 */

	public Declaration getDeclaration(Variable v) {
		for (Statement s: statements) {
			if (s instanceof Declaration) {
				Declaration d = (Declaration) s;
				if (d.getVariable() == v) {
					return d;
				}
			} else if (s.hasBlock()) {
				Declaration d = s.getDeclaration(v);
				
				if (d != null) {
					return d;
				}
			}
		}

		return null;
	}

	public int getNoInsertPoints() {
		int noInsertPoints = 1;
		
		for (Statement s: statements) {
			noInsertPoints++;
			noInsertPoints += s.getNoInsertPoints();
		}
		
		return noInsertPoints;
	}

	public void insertStatements(List<Statement> swapStatements, int insertPoint) {
		int point = 0;
		int i = 0;
		for (Statement s: statements) {
			if (point == insertPoint) {
				statements.addAll(i, swapStatements);
				return;
			} else if (point+s.getNoInsertPoints() >= insertPoint) {
				s.insertStatements(insertPoint-point-1, swapStatements);
				return;
			}
			i++;
			point += s.getNoInsertPoints() + 1;
		}
		
		// If we get to here, then we just add the statements at the end.
		statements.addAll(swapStatements);
	}
}
