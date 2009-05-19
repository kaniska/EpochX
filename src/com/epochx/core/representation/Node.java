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
package com.epochx.core.representation;

import java.util.*;

import com.epochx.core.*;

/**
 * Subclasses of <code>Node</code> should ensure they call the superclass 
 * constructor with all child Nodes so information such as the arity of the
 * Node can be maintained. Concrete subclasses must also implement evaluate().
 * 
 * A Node is a vertex in a tree structure which represents a program. A Node 
 * can be thought of as an expression in a computer programming language.
 * Evaluating a Node will involve evaluating any children and potentially 
 * performing some operation and/or returning a value. A program
 * is maintained by the <code>CandidateProgram</code> class.
 *  
 * @see FunctionNode
 * @see TerminalNode
 */
public abstract class Node<TYPE> implements Cloneable {
	
	private Node<TYPE>[] children;
	
	/**
	 * Node constructor
	 * @param children children of this node
	 */
	public Node(Node<TYPE> ... children) {
		this.children = children;
	}
	
	/**
	 * Performs some operation and/or returns a value associated 
	 * with this Node.
	 * @return The result of evaluating the candidate program
	 */
	public abstract TYPE evaluate();
	
	/**
	 * Returns the children of the node
	 * @return The children of the node
	 */
	public Node<TYPE>[] getChildren() {
		return children;
	}

	/**
	 * Sets the children of the node
	 * @param children The new children to be set
	 */
	public void setChildren(Node<TYPE>[] children) {
		this.children = children;
	}
	
	/**
	 * Returns a specific child by index
	 * @param index The index (representing arity) of the child to be returned
	 * @return The desired child node
	 */
	public Node<TYPE> getChild(int index) {
		return children[index];
	}
	
	/**
	 * Retrieves the nth node from the tree when considering this node to be 
	 * the root - that is the 0th node. The tree is traversed in pre-order.
	 * @param n The index of the node to be returned
	 * @return The desired node
	 */
	public Node<?> getNthNode(int n) {
		return getNthNode(n, 0);
	}
	
	/*
	 * Recursive helper for the public getNthNode(int).
	 */
	private Node<?> getNthNode(int n, int current) {
		if (n == current)
			return this;
		
		Node<?> node = null;
		for (Node<?> child: children) {
			int childLength = GPProgramAnalyser.getProgramLength(child);
			
			// Only look at the subtree if it contains the right range of nodes.
			if (n <= childLength + current) {
				node = child.getNthNode(n, current+1);
				if (node != null) break;
			}
			
			current += childLength;
		}
		
		return node;
	}
	
	/**
	 * Set the nth node in the tree when considering this Node to be the root
	 * node - that is, the 0th node. The tree is traversed in pre-order.
	 * @param newNode The new node to be inserted
	 * @param n The index at which to place the new node
	 */
	public void setNthNode(Node<TYPE> newNode, int n) {		
		setNthNode(newNode, n, 0);
	}
	
	/*
	 * Recursive helper for the public setNthNode(Node, int).
	 */
	private void setNthNode(Node<TYPE> newNode, int n, int current) {
		int arity = getArity();
		for (int i=0; i<arity; i++) {
			if (current+1 == n) {
				setChild(newNode, i);
				break;
			}
			
			Node<TYPE> child = getChild(i);
			int childLength = GPProgramAnalyser.getProgramLength(child);
			
			// Only look at the subtree if it contains the right range of nodes.
			if (n <= childLength + current) {
				child.setNthNode(newNode, n, current+1);
			}
			
			current += childLength;
		}
	}
	
	/**
	 * Retrieves the nodes from the tree at depth d when considering this node to be 
	 * the root - that is depth 0.
	 * @param depth The depth to retrieve nodes at
	 * @return The nodes at the desired depth
	 */
	public List<Node<?>> getNodesAtDepth(int d) {
		List<Node<?>> nodes = new ArrayList<Node<?>>();
		fillNodesAtDepth(nodes, d, 0);
		return nodes;
	}
	
	private void fillNodesAtDepth(List<Node<?>> nodes, int d, int current){
		if (d == current) {
			nodes.add(this);
		} else {
			for (Node<?> child: children) {
				// Only look at the subtree if it contains the right range of nodes.
				child.fillNodesAtDepth(nodes, d, current+1);
			}
		}
	}
	
	/**
	 * Sets a child node
	 * @param child The new child node
	 * @param index The index (representing arity) to set
	 */
	public void setChild(Node<TYPE> child, int index) {
		children[index] = child;
	}
	
	/**
	 * Returns the number of children this Node has. Only immediate children 
	 * of this Node are counted, not ALL children below this Node.
	 * 
	 * @return	the number of child Nodes.
	 */
	public int getArity() {
		return children.length;
	}

	@Override
	public Object clone() {
		try {
			Node<TYPE> clone = (Node<TYPE>) super.clone();
			
			clone.children = this.children.clone();
			for (int i=0; i<children.length; i++) {
				clone.children[i] = this.children[i];
				if (clone.children[i] != null)
					clone.children[i] = (Node<TYPE>) clone.children[i].clone();
			}
			
			return clone;
		} catch (CloneNotSupportedException e) {
			// This shouldn't ever happen - if it does then everythings going to 
			// blow up anyway.
		}
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		boolean equal = true;
		
		if (obj instanceof Node) {
			Node<TYPE> n = (Node<TYPE>) obj;
			
			if (n.getArity() != this.getArity()) {
				equal = false;
			}
			
			for(int i=0; i<n.getArity() && equal; i++) {
				Node<?> thatChild = n.getChild(i);
				Node<?> thisChild = this.getChild(i);
				
				if ((thisChild != null) ^ (thatChild != null)) {
					equal = false;
				} else {
					equal = ((thisChild == null) && (thatChild == null) || thisChild.equals(thatChild));
				}
			}
		} else {
			equal = false;
		}
		return equal;
	}	
	
	@Override
	public int hashCode() {
		int result = 17;
		for (Node<?> child: children) {
			result = 37 * result + child.hashCode();
		}
		return result;
	}
	
	public int getNoTerminals() {
		if (this instanceof TerminalNode) {
			return 1;
		} else {
			int result = 0;
			for (int i=0; i<getArity(); i++) {
				result += getChild(i).getNoTerminals();
			}
			return result;
		}
	}
	
	public int getNoDistinctTerminals() {
		// Get a list of terminals.
		List<TerminalNode<TYPE>> terminals = getTerminalNodes();
		
		// Remove duplicates.
		HashSet terminalHash = new HashSet(terminals);
		
		// The number left is how many distinct terminals.
		return terminalHash.size();
	}
	
	public List<TerminalNode<TYPE>> getTerminalNodes() {
		// Alternatively we could use an array, which is quicker/more efficient in this situation?
		List<TerminalNode<TYPE>> terminals = new ArrayList<TerminalNode<TYPE>>();
		
		if (this instanceof TerminalNode) {
			terminals.add((TerminalNode<TYPE>) this);
		} else {
			for (int i=0; i<getArity(); i++) {
				terminals.addAll(getChild(i).getTerminalNodes());
			}
		}
		return terminals;
	}
}
