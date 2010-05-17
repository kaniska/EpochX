/*  
 *  Copyright 2007-2008 Lawrence Beadle
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

package regression;

import java.util.ArrayList;

import core.*;

/**
 * The Syntax6bit class conatins the basic syntax for the boolean programs
 * @author Lawrence Beadle
 */
public class SyntaxReg implements Syntax {
    
    private ArrayList<ArrayList<String>> syntax = new ArrayList<ArrayList<String>>();
    private ArrayList<String> part = new ArrayList<String>();
    private ArrayList<ArrayList<String>> terminals = new ArrayList<ArrayList<String>>();
    private ArrayList<String> eStart = new ArrayList<String>();
    private ArrayList<String> funcs = new ArrayList<String>();
    private ArrayList<String> terms = new ArrayList<String>();
    
    /**
     * The Syntax6bit constructor defines all the syntax for the 6 bit multiplexer problem
     */
    public SyntaxReg() {
        
        // load functions & structure ------------------------------------------
        
        // load IF
        part = new ArrayList<String>();
        part.add("(");
        part.add("ADD");
        part.add("*");
        part.add("*");
        part.add(")");
        syntax.add(part);
        
        // load AND
        part = new ArrayList<String>();
        part.add("(");
        part.add("SUB");
        part.add("*");
        part.add("*");
        part.add(")");
        syntax.add(part);
        
        // load OR
        part = new ArrayList<String>();
        part.add("(");
        part.add("MUL");
        part.add("*");
        part.add("*");
        part.add(")");
        syntax.add(part);
        
        // load NOT
        part = new ArrayList<String>();
        part.add("(");
        part.add("DIV");
        part.add("*");
        part.add("*");
        part.add(")");
        syntax.add(part);
        
        // load terminals ------------------------------------------------------
        part = new ArrayList<String>();
        part.add("x");
        syntax.add(part);
        terminals.add(part);
        
        part = new ArrayList<String>();
        part.add("-5");
        syntax.add(part);
        terminals.add(part);
        
        part = new ArrayList<String>();
        part.add("-4");
        syntax.add(part);
        terminals.add(part);
        
        part = new ArrayList<String>();
        part.add("-3");
        syntax.add(part);
        terminals.add(part);
        
        part = new ArrayList<String>();
        part.add("-2");
        syntax.add(part);
        terminals.add(part);
        
        part = new ArrayList<String>();
        part.add("-1");
        syntax.add(part);
        terminals.add(part);
        
        part = new ArrayList<String>();
        part.add("0");
        syntax.add(part);
        terminals.add(part);
        
        part = new ArrayList<String>();
        part.add("5");
        syntax.add(part);
        terminals.add(part);
        
        part = new ArrayList<String>();
        part.add("4");
        syntax.add(part);
        terminals.add(part);
        
        part = new ArrayList<String>();
        part.add("3");
        syntax.add(part);
        terminals.add(part);
        
        part = new ArrayList<String>();
        part.add("2");
        syntax.add(part);
        terminals.add(part);
        
        part = new ArrayList<String>();
        part.add("1");
        syntax.add(part);
        terminals.add(part);
        
        // sort out expression starting ----------------------------------------     
        eStart.add("x");
        eStart.add("-5");
        eStart.add("-4");
        eStart.add("-3");
        eStart.add("-2");
        eStart.add("-1");
        eStart.add("5");
        eStart.add("4");
        eStart.add("3");
        eStart.add("2");
        eStart.add("1");
        eStart.add("0");
        eStart.add("(");
        
        // make a list of fucntions --------------------------------------------
        funcs.add("ADD");
        funcs.add("MUL");
        funcs.add("SUB");
        funcs.add("DIV");
        
        // make a list of terminals --------------------------------------------        
        terms.add("x");
        terms.add("-5");
        terms.add("-4");
        terms.add("-3");
        terms.add("-2");
        terms.add("-1");
        terms.add("5");
        terms.add("4");
        terms.add("3");
        terms.add("2");
        terms.add("1");
        terms.add("0");
        
    }
    
    /**
     * Calls a syntax list to be returned
     * @return An ArrayList<String> representing all the terminals and function within the GP
     */
    public ArrayList<ArrayList<String>> getSyntax() {       
        return syntax;
    }
    
    /**
     * Returns a list of terminals
     * @return A list of terminals
     */
    public ArrayList<ArrayList<String>> getTerms() {        
        return terminals;
    }
    
    /**
     * Returns a list of items that can start an expression
     * @return A List of items that can start an expression - either a terminal or a '('
     */
    public ArrayList<String> getEStart() {        
        return eStart;
    }
    
    /**
     * Returns a list of functions
     * @return A list of Functions
     */
    public ArrayList<String> getFunctions() {        
        return funcs;
    }
    
    /**
     * Retrns a list of terminals
     * @return A list of terminals
     */
    public ArrayList<String> getTerminals() {        
        return terms;
    }
    
}