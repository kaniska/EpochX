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
package org.epochx.tools.eval;

import javax.script.*;


/**
 * A <code>RubyInterpreter</code> provides the facility to evaluate individual 
 * Ruby expressions and execute multi-line Ruby statements.
 * 
 * <p>
 * <code>RubyInterpreter</code> extends from the <code>ScriptingInterpreter
 * </code>, adding ruby specific enhancements, including optimized performance.
 */
public class RubyInterpreter extends ScriptingInterpreter {
	
	/*
	 * Constructs a RubyInterpreter.
	 */
	public RubyInterpreter() {
		super("ruby");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object eval(final String expression, final String[] argNames, final Object[] argValues) {
		final String code = getEvalCode(expression, argNames);

		Object result = null;
		
		Invocable invocableEngine = (Invocable) getEngine();
		try {
			getEngine().eval(code);
		    result = invocableEngine.invokeFunction("expr", argValues);
		} catch (ScriptException ex) {
			ex.printStackTrace();
		} catch (NoSuchMethodException ex) {
		    ex.printStackTrace();
		}

		return result;
	}
	
	private String getEvalCode(final String expression, final String[] argNames) {
		final StringBuffer code = new StringBuffer();

        code.append("def expr(");
        for (int i=0; i<argNames.length; i++) {
        	if (i > 0) {
        		code.append(',');
        	}
			code.append(argNames[i]);
		}
        code.append(")\n");
        
        code.append("return ");
        code.append(expression);
        code.append(";\n");
        code.append("end\n");
        
        return code.toString();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] eval(final String expression, final String[] argNames, final Object[][] argValues) {
		//TODO Might be able to speed this up further by compiling then using invokeMethod.
		final Object[] results = new Object[argValues.length];

		final String code = getEvalCode(expression, argNames);
		
        
		Invocable invocableEngine = (Invocable) getEngine();
		try {
			getEngine().eval(code);
			
			// Evaluate each argument set.
	        for (int i=0; i<results.length; i++) {
	        	results[i] = invocableEngine.invokeFunction("expr", argValues[i]);
	        }
		} catch (ScriptException ex) {
			ex.printStackTrace();
		} catch (NoSuchMethodException ex) {
		    ex.printStackTrace();
		}
			        
		return results;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void exec(final String program, final String[] argNames, final Object[] argValues) {		
		final String code = getExecCode(program, argNames);

		Invocable invocableEngine = (Invocable) getEngine();
		try {
			getEngine().eval(code);
		    invocableEngine.invokeFunction("expr", argValues);
		} catch (ScriptException ex) {
			ex.printStackTrace();
		} catch (NoSuchMethodException ex) {
		    ex.printStackTrace();
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void exec(final String program, final String[] argNames, final Object[][] argValues) {
		//TODO Might be able to speed this up further by compiling then using invokeMethod.
		final String code = getExecCode(program, argNames);
		
		Invocable invocableEngine = (Invocable) getEngine();
		try {
			getEngine().eval(code);
			
			// Evaluate each argument set.
	        for (int i=0; i<argValues.length; i++) {
	        	invocableEngine.invokeFunction("expr", argValues[i]);
	        }
		} catch (ScriptException ex) {
			ex.printStackTrace();
		} catch (NoSuchMethodException ex) {
		    ex.printStackTrace();
		}
	}
	
	private String getExecCode(final String program, final String[] argNames) {
		final StringBuffer code = new StringBuffer();

        //code.append("class Evaluation\n");
        code.append("def expr(");
        for (int i=0; i<argNames.length; i++) {
        	if (i > 0) {
        		code.append(',');
        	}
			code.append(argNames[i]);
		}
        code.append(")\n");
        
        code.append(program);
        code.append("\n");
        code.append("end\n");
        
        return code.toString();
	}
}
