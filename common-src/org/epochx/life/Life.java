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

import java.util.List;

import org.epochx.ref.ListenerList;
import org.epochx.representation.CandidateProgram;

/**
 * The Life Cycle system in EpochX is managed by an instance of this class.
 * Users of this class are either event listeners or event providers.
 * 
 * <h4>Event listeners</h4>
 * 
 * <p>
 * Objects that are interested in listening for life cycle events should use the
 * set of <code>addXxxListener(XxxListener)</code> methods. In most
 * circumstances it is beneficial to use an anonymous implementation of one of
 * the <code>abstract</code> <code>XxxAdapter</code> classes.
 * 
 * <p>
 * For example, to perform an action at the end of each generation:
 * 
 * <pre>
 * Life.getLife().addGenerationListener(new GenerationAdapter() {
 * 		public void onGenerationEnd() {
 * 			... do something ...
 * 		}
 * });
 * </pre>
 * 
 * <h4>Event providers</h4>
 * 
 * <p>
 * For the most part, events are generated by the EpochX framework itself.
 * Events are raised with a call to one of the <code>fireXxxEvent</code>
 * methods. This will trigger a distribution of the event to all appropriate
 * listeners.
 */
public class Life {
	
	private static Life instance;

	// The life cycle listeners.
	private final ListenerList<ConfigListener> configListeners;
	private final ListenerList<RunListener> runListeners;
	private final ListenerList<InitialisationListener> initialisationListeners;
	private final ListenerList<ElitismListener> elitismListeners;
	private final ListenerList<PoolSelectionListener> poolSelectionListeners;
	private final ListenerList<CrossoverListener> crossoverListeners;
	private final ListenerList<MutationListener> mutationListeners;
	private final ListenerList<ReproductionListener> reproductionListeners;
	private final ListenerList<GenerationListener> generationListeners;
	
	// Hooks.
	private final ListenerList<Hook> hooks;

	/*
	 * Construct a new life cycle manager.
	 */
	private Life() {
		// Initialise listener lists.
		configListeners = new ListenerList<ConfigListener>();
		runListeners = new ListenerList<RunListener>();
		initialisationListeners = new ListenerList<InitialisationListener>();
		elitismListeners = new ListenerList<ElitismListener>();
		poolSelectionListeners = new ListenerList<PoolSelectionListener>();
		crossoverListeners = new ListenerList<CrossoverListener>();
		mutationListeners = new ListenerList<MutationListener>();
		reproductionListeners = new ListenerList<ReproductionListener>();
		generationListeners = new ListenerList<GenerationListener>();
		
		hooks = new ListenerList<Hook>();
	}
	
	public static Life get() {
		if (instance == null) {
			instance = new Life();
		}
		
		return instance;
	}
	
	/**
	 * Adds a <code>Hook</code> to the life cycle.
	 * 
	 * @param hook the <code>Hook</code> to be added.
	 */
	public void addHook(final Hook hook) {
		hooks.add(hook);
	}

	public void addHook(final Hook hook, final boolean strong) {
		hooks.add(hook, strong);
	}
	
	public void insertHook(final int index, final Hook hook) {
		hooks.add(index, hook);
	}
	
	public void insertHook(final int index, final Hook hook, final boolean strong) {
		hooks.add(index, hook, strong);
	}
	
	/**
	 * Removes a <code>Hook</code> from the life cycle.
	 * 
	 * @param hook the <code>Hook</code> to be removed.
	 */
	public void removeHook(final InitialisationListener hook) {
		hooks.remove(hook);
	}
	
	/**
	 * Adds a <code>ConfigListener</code> to the life cycle. 
	 * 
	 * @param listener the <code>ConfigListener</code> to be added.
	 */
	public void addConfigListener(final ConfigListener listener) {
		addConfigListener(listener, true);
	}
	
	/**
	 * It does not make sense to use this method with an anonymous inner class 
	 * as the listener since it will contain an implicit strong reference to the
	 * owner, and as such neither will ever be garbage collected. 
	 * 
	 * @param listener
	 * @param owner
	 */
	public void addConfigListener(final ConfigListener listener, boolean strong) {
		configListeners.add(listener, strong);
	}

	/**
	 * Removes a <code>ConfigListener</code> from the life cycle.
	 * 
	 * @param listener the <code>ConfigListener</code> to be removed.
	 */
	public void removeConfigListener(final ConfigListener listener) {
		configListeners.remove(listener);
	}

	/**
	 * Adds a <code>RunListener</code> to the life cycle.
	 * 
	 * @param listener the <code>RunListener</code> to be added.
	 */
	public void addRunListener(final RunListener listener) {
		runListeners.add(listener);
	}
	
	public void addRunListener(final RunListener listener, boolean strong) {
		runListeners.add(listener, strong);
	}

	/**
	 * Removes a <code>RunListener</code> from the life cycle.
	 * 
	 * @param listener the <code>RunListener</code> to be removed.
	 */
	public void removeRunListener(final RunListener listener) {
		runListeners.remove(listener);
	}

	/**
	 * Adds an <code>InitialisationListener</code> to the life cycle.
	 * 
	 * @param listener the <code>InitialisationListener</code> to be added.
	 */
	public void addInitialisationListener(final InitialisationListener listener) {
		initialisationListeners.add(listener);
	}

	public void addInitialisationListener(final InitialisationListener listener, boolean strong) {
		initialisationListeners.add(listener, strong);
	}
	
	/**
	 * Removes a <code>InitialisationListener</code> from the life cycle.
	 * 
	 * @param listener the <code>InitialisationListener</code> to be removed.
	 */
	public void removeInitialisationListener(
			final InitialisationListener listener) {
		initialisationListeners.remove(listener);
	}

	/**
	 * Adds an <code>ElitismListener</code> to the life cycle.
	 * 
	 * @param listener the <code>ElitismListener</code> to be added.
	 */
	public void addElitismListener(final ElitismListener listener) {
		elitismListeners.add(listener);
	}

	public void addElitismListener(final ElitismListener listener, boolean strong) {
		elitismListeners.add(listener, strong);
	}
	
	/**
	 * Removes a <code>ElitismListener</code> from the life cycle.
	 * 
	 * @param listener the <code>ElitismListener</code> to be removed.
	 */
	public void removeElitismListener(final ElitismListener listener) {
		elitismListeners.remove(listener);
	}

	/**
	 * Adds a <code>PoolSelectionListener</code> to the life cycle.
	 * 
	 * @param listener the <code>PoolSelectionListener</code> to be added.
	 */
	public void addPoolSelectionListener(final PoolSelectionListener listener) {
		poolSelectionListeners.add(listener);
	}
	
	public void addPoolSelectionListener(final PoolSelectionListener listener, boolean strong) {
		poolSelectionListeners.add(listener, strong);
	}

	/**
	 * Removes a <code>PoolSelectionListener</code> from the life cycle.
	 * 
	 * @param listener the <code>PoolSelectionListener</code> to be removed.
	 */
	public void removePoolSelectionListener(final PoolSelectionListener listener) {
		poolSelectionListeners.remove(listener);
	}

	/**
	 * Adds a <code>CrossoverListener</code> to the life cycle.
	 * 
	 * @param listener the <code>CrossoverListener</code> to be added.
	 */
	public void addCrossoverListener(final CrossoverListener listener) {
		crossoverListeners.add(listener);
	}
	
	public void addCrossoverListener(final CrossoverListener listener, boolean strong) {
		crossoverListeners.add(listener, strong);
	}

	/**
	 * Removes a <code>CrossoverListener</code> from the life cycle.
	 * 
	 * @param listener the <code>CrossoverListener</code> to be removed.
	 */
	public void removeCrossoverListener(final CrossoverListener listener) {
		crossoverListeners.remove(listener);
	}

	/**
	 * Adds a <code>MutationListener</code> to the life cycle.
	 * 
	 * @param listener the <code>MutationListener</code> to be added.
	 */
	public void addMutationListener(final MutationListener listener) {
		mutationListeners.add(listener);
	}
	
	public void addMutationListener(final MutationListener listener, boolean strong) {
		mutationListeners.add(listener, strong);
	}

	/**
	 * Removes a <code>MutationListener</code> from the life cycle.
	 * 
	 * @param listener the <code>MutationListener</code> to be removed.
	 */
	public void removeMutationListener(final MutationListener listener) {
		mutationListeners.remove(listener);
	}

	/**
	 * Adds a <code>ReproductionListener</code> to the life cycle.
	 * 
	 * @param listener the <code>ReproductionListener</code> to be added.
	 */
	public void addReproductionListener(final ReproductionListener listener) {
		reproductionListeners.add(listener);
	}
	
	public void addReproductionListener(final ReproductionListener listener, boolean strong) {
		reproductionListeners.add(listener, strong);
	}

	/**
	 * Removes a <code>ReproductionListener</code> from the life cycle.
	 * 
	 * @param listener the <code>ReproductionListener</code> to be removed.
	 */
	public void removeReproductionListener(final ReproductionListener listener) {
		reproductionListeners.remove(listener);
	}

	/**
	 * Adds a <code>GenerationListener</code> to the life cycle.
	 * 
	 * @param listener the <code>GenerationListener</code> to be added.
	 */
	public void addGenerationListener(final GenerationListener listener) {
		generationListeners.add(listener);
	}
	
	public void addGenerationListener(final GenerationListener listener, boolean strong) {
		generationListeners.add(listener, strong);
	}

	/**
	 * Removes a <code>GenerationListener</code> from the life cycle.
	 * 
	 * @param listener the <code>GenerationListener</code> to be removed.
	 */
	public void removeGenerationListener(final GenerationListener listener) {
		generationListeners.remove(listener);
	}

	/**
	 * Notifies all listeners that have registered interest for notification on
	 * this event type.
	 */
	public void fireConfigureEvent() {
		for (final ConfigListener listener: configListeners) {
			if (listener != null) {
				listener.onConfigure();
			}
		}
	}

	/**
	 * Notifies all listeners that have registered interest for notification on
	 * this event type.
	 */
	public void fireRunStartEvent() {
		for (final RunListener listener: runListeners) {
			listener.onRunStart();
		}
	}

	/**
	 * Notifies all listeners that have registered interest for notification on
	 * this event type.
	 */
	public void fireSuccessEvent() {
		for (final RunListener listener: runListeners) {
			listener.onSuccess();
		}
	}

	/**
	 * Notifies all listeners that have registered interest for notification on
	 * this event type.
	 */
	public void fireRunEndEvent() {
		for (final RunListener listener: runListeners) {
			listener.onRunEnd();
		}
	}

	/**
	 * Notifies all listeners that have registered interest for notification on
	 * this event type.
	 */
	public void fireInitialisationStartEvent() {
		for (final InitialisationListener listener: initialisationListeners) {
			listener.onInitialisationStart();
		}
	}

	/**
	 * Notifies all listeners that have registered interest for notification on
	 * this event type.
	 */
	public void fireInitialisationEndEvent() {
		for (final InitialisationListener listener: initialisationListeners) {
			listener.onInitialisationEnd();
		}
	}

	/**
	 * Notifies all listeners that have registered interest for notification on
	 * this event type.
	 */
	public void fireElitismStartEvent() {
		for (final ElitismListener listener: elitismListeners) {
			listener.onElitismStart();
		}
	}

	/**
	 * Notifies all listeners that have registered interest for notification on
	 * this event type.
	 */
	public void fireElitismEndEvent() {
		for (final ElitismListener listener: elitismListeners) {
			listener.onElitismEnd();
		}
	}

	/**
	 * Notifies all listeners that have registered interest for notification on
	 * this event type.
	 */
	public void firePoolSelectionStartEvent() {
		for (final PoolSelectionListener listener: poolSelectionListeners) {
			listener.onPoolSelectionStart();
		}
	}

	/**
	 * Notifies all listeners that have registered interest for notification on
	 * this event type.
	 */
	public void firePoolSelectionEndEvent() {
		for (final PoolSelectionListener listener: poolSelectionListeners) {
			listener.onPoolSelectionEnd();
		}
	}

	/**
	 * Notifies all listeners that have registered interest for notification on
	 * this event type.
	 */
	public void fireCrossoverStartEvent() {
		for (final CrossoverListener listener: crossoverListeners) {
			listener.onCrossoverStart();
		}
	}

	/**
	 * Notifies all listeners that have registered interest for notification on
	 * this event type.
	 */
	public void fireCrossoverEndEvent() {
		for (final CrossoverListener listener: crossoverListeners) {
			listener.onCrossoverEnd();
		}
	}

	/**
	 * Notifies all listeners that have registered interest for notification on
	 * this event type.
	 */
	public void fireMutationStartEvent() {
		for (final MutationListener listener: mutationListeners) {
			listener.onMutationStart();
		}
	}

	/**
	 * Notifies all listeners that have registered interest for notification on
	 * this event type.
	 */
	public void fireMutationEndEvent() {
		for (final MutationListener listener: mutationListeners) {
			listener.onMutationEnd();
		}
	}

	/**
	 * Notifies all listeners that have registered interest for notification on
	 * this event type.
	 */
	public void fireReproductionStartEvent() {
		for (final ReproductionListener listener: reproductionListeners) {
			listener.onReproductionStart();
		}
	}

	/**
	 * Notifies all listeners that have registered interest for notification on
	 * this event type.
	 */
	public void fireReproductionEndEvent() {
		for (final ReproductionListener listener: reproductionListeners) {
			listener.onReproductionEnd();
		}
	}

	/**
	 * Notifies all listeners that have registered interest for notification on
	 * this event type.
	 */
	public void fireGenerationStartEvent() {
		for (final GenerationListener listener: generationListeners) {
			listener.onGenerationStart();
		}
	}

	/**
	 * Notifies all listeners that have registered interest for notification on
	 * this event type.
	 */
	public void fireGenerationEndEvent() {
		for (final GenerationListener listener: generationListeners) {
			listener.onGenerationEnd();
		}
	}

	/**
	 * Runs all initialisation hooks that have been registered. If multiple 
	 * hooks are used then the ordering is significant. The population 
	 * received as a parameter is fed to the first hook method. Each hook is 
	 * then chained so that the value returned from one hook is fed into the 
	 * next. The value returned by the final hook is returned from this method.
	 * The exception to this is if any of the hooks return <code>null</code>, 
	 * then no further hooks will be used and this method immediately returns 
	 * with <code>null</code>.
	 * 
	 * @param pop a population of <code>CandidatePrograms</code>.
	 * @return the response from the hooks which may be the given pop, a
	 *         different or modified list of <code>CandidatePrograms</code> or
	 *         null.
	 */
	public List<CandidateProgram> runInitialisationHooks(List<CandidateProgram> pop) {
		for (final Hook h: hooks) {
			pop = h.initialisationHook(pop);

			if (pop == null) {
				break;
			}
		}

		return pop;
	}
	
	/**
	 * Runs all elitism hooks that have been registered. If multiple 
	 * hooks are used then the ordering is significant. The population of elites
	 * received as a parameter is fed to the first hook method. Each hook is 
	 * then chained so that the value returned from one hook is fed into the 
	 * next. The value returned by the final hook is returned from this method.
	 * No hooks may return a <code>null</code> value.
	 * 
	 * @param elites a population of <code>CandidatePrograms</code>.
	 * @return the response from the hooks which may be the given list of 
	 * 		   elites, a different or modified list of 
	 * 		   <code>CandidatePrograms</code> or null.
	 */
	public List<CandidateProgram> runElitismHooks(List<CandidateProgram> elites) {
		for (final Hook h: hooks) {
			elites = h.elitismHook(elites);

			if (elites == null) {
				throw new NullPointerException("an elitism hook returned elites as null");
			}
		}

		assert (elites != null);

		return elites;
	}
	
	/**
	 * Runs all pool selection hooks that have been registered. If multiple 
	 * hooks are used then the ordering is significant. The breeding pool 
	 * received as a parameter is fed to the first hook method. Each hook is 
	 * then chained so that the value returned from one hook is fed into the 
	 * next. The value returned by the final hook is returned from this method.
	 * The exception to this is if any of the hooks return <code>null</code>, 
	 * then no further hooks will be used and this method immediately returns 
	 * with <code>null</code>.
	 * 
	 * @param pool a population of <code>CandidatePrograms</code>.
	 * @return the response from the hooks which may be the given pool, a
	 *         different or modified list of <code>CandidatePrograms</code> or
	 *         null.
	 */
	public List<CandidateProgram> runPoolSelectionHooks(List<CandidateProgram> pool) {
		for (final Hook h: hooks) {
			pool = h.poolSelectionHook(pool);

			if (pool == null) {
				break;
			}
		}

		return pool;
	}
	
	/**
	 * Runs all crossover hooks that have been registered. If multiple 
	 * hooks are used then the ordering is significant. The parents and children 
	 * received as a parameter are fed to the first hook method. Each hook is 
	 * then chained so that the value returned from one hook is fed in as the 
	 * children to the next. The value returned by the final hook is then 
	 * returned from this method. The exception to this is if any of the hooks 
	 * return <code>null</code>, then no further hooks will be used and this 
	 * method immediately returns with <code>null</code>.
	 * 
	 * @param parents an array of <code>CandidatePrograms</code> which underwent
	 *        crossover.
	 * @param children an array of <code>CandidatePrograms</code> which are the
	 *        result of a crossover operation on the parents.
	 * @return the response from the hooks which may be the given children,
	 *         or potentially an array of entirely different
	 *         <code>CandidatePrograms</code>. <code>null</code> is also a valid
	 *         return value.
	 */
	public CandidateProgram[] runCrossoverHooks(final CandidateProgram[] parents, CandidateProgram[] children) {
		for (final Hook h: hooks) {
			children = h.crossoverHook(parents, children);

			if (children == null) {
				break;
			}
		}

		return children;
	}

	/**
	 * Runs all mutation hooks that have been registered. If multiple 
	 * hooks are used then the ordering is significant. The parent and child 
	 * received as a parameter are fed to the first hook method. Each hook is 
	 * then chained so that the value returned from one hook is fed in as the 
	 * child to the next. The value returned by the final hook is then 
	 * returned from this method. The exception to this is if any of the hooks 
	 * return <code>null</code>, then no further hooks will be used and this 
	 * method immediately returns with <code>null</code>.
	 * 
	 * @param parent a <code>CandidateProgram</code> which underwent mutation.
	 * @param child a <code>CandidateProgram</code> which is the result of a
	 *        mutation operation on the parent.
	 * @return the response from the hooks which may be the given child,
	 *         or potentially an entirely different
	 *         <code>CandidateProgram</code>. <code>null</code> is also a valid
	 *         return value.
	 */
	public CandidateProgram runMutationHooks(final CandidateProgram parent, CandidateProgram child) {
		for (final Hook h: hooks) {
			child = h.mutationHook(parent, child);

			if (child == null) {
				break;
			}
		}

		return child;
	}
	
	/**
	 * Runs all reproduction hooks that have been registered. If multiple 
	 * hooks are used then the ordering is significant. The program 
	 * received as a parameter is fed to the first hook method. Each hook is 
	 * then chained so that the value returned from one hook is fed in to the 
	 * next. The value returned by the final hook is then returned from this 
	 * method. The exception to this is if any of the hooks return 
	 * <code>null</code>, then no further hooks will be used and this method 
	 * immediately returns with <code>null</code>.
	 * 
	 * @param program a <code>CandidateProgram</code> which was selected for
	 *        reproduction.
	 * @return the response from the hooks which may be the given program,
	 *         or potentially an entirely different
	 *         <code>CandidateProgram</code>. <code>null</code> is also a valid
	 *         return value.
	 */
	public CandidateProgram runReproductionHooks(CandidateProgram program) {
		for (final Hook h: hooks) {
			program = h.reproductionHook(program);

			if (program == null) {
				break;
			}
		}

		return program;
	}

	/**
	 * Runs all generation hooks that have been registered. If multiple 
	 * hooks are used then the ordering is significant. The population 
	 * received as a parameter is fed to the first hook method. Each hook is 
	 * then chained so that the value returned from one hook is fed in to the 
	 * next. The value returned by the final hook is then returned from this 
	 * method. The exception to this is if any of the hooks return 
	 * <code>null</code>, then no further hooks will be used and this method 
	 * immediately returns with <code>null</code>.
	 * 
	 * @param pop a list of <code>CandidatePrograms</code> which were the result
	 *        of an evolutionary generation.
	 * @return the response from the hooks which may be the given pop,
	 *         or potentially a list of entirely different
	 *         <code>CandidatePrograms</code>. <code>null</code> is also a valid
	 *         return value.
	 */
	public List<CandidateProgram> runGenerationHooks(List<CandidateProgram> pop) {
		for (final Hook h: hooks) {
			pop = h.generationHook(pop);

			if (pop == null) {
				break;
			}
		}

		return pop;
	}
	
	/**
	 * Clears all listeners for all types of events.
	 */
	public void clearListeners() {
		configListeners.clear();
		runListeners.clear();
		initialisationListeners.clear();
		elitismListeners.clear();
		poolSelectionListeners.clear();
		crossoverListeners.clear();
		mutationListeners.clear();
		reproductionListeners.clear();
		generationListeners.clear();
	}
	
	/**
	 * Clears all hooks.
	 */
	public void clearHooks() {
		hooks.clear();
	}
}
