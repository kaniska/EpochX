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
package org.epochx.life;

import org.epochx.core.MutationManager;
import org.epochx.representation.CandidateProgram;

/**
 * Provides the interface to be implemented by objects that wish to handle run
 * events. See the {@link MutationManager}'s class documentation for details of
 * when each mutation event will be fired. To listen for mutation events during
 * execution of a model, instances of <code>MutationListener</code> must be
 * added to the model's <code>LifeCycleManager</code> which is retrievable
 * through a call to the model's <code>getLifeCycleManager()</code> method.
 * 
 * <p>
 * It is typical to listen to events using an anonymous class which often makes
 * the <code>abstract</code> <code>MutationAdapter</code> class more convenient
 * to implement.
 * 
 * @see MutationAdapter
 * @see MutationManager
 */
public interface MutationListener {

	/**
	 * Event fired before the mutation operation starts.
	 */
	void onMutationStart();

	/**
	 * Event fired after the selection and mutation operation has occurred.
	 * The child may be modified and returned. This event is revertable by
	 * returning <code>null</code> which will trigger the discarding of the
	 * parent and mutant child, the reselection of a new parent, a new mutation
	 * attempt and this event being raised again. If the mutation should be
	 * accepted then the child should be returned as it is.
	 * 
	 * @param parent the program that was selected to undergo mutation.
	 * @param child the resultant program from the parent undergoing mutation.
	 * @return a GPCandidateProgram that should be considered the result of a
	 *         mutation operation, or null if the mutation should be reverted.
	 */
	CandidateProgram onMutation(CandidateProgram parent, CandidateProgram child);

	/**
	 * Event fired after the mutation operation has ended.
	 */
	void onMutationEnd();
}