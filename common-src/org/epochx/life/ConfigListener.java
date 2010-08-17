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

/**
 * Provides the interface to be implemented by objects that wish to listen for
 * configure events. Configure events are specifically for triggering the
 * configuration of components with parameters from the model, and are called
 * at the start of execution, prior to the start of each run and before the
 * start of each generation. The event may also be fired manually with a call to
 * the <code>LifeCycleManager</code>'s <code>fireConfigureEvent()</code> method.
 * To listen for configure events during execution of a model, instances of
 * <code>ConfigListener</code> must be added to the model's
 * <code>LifeCycleManager</code> which is retrievable through a call to the
 * model's <code>getLifeCycleManager()</code> method.
 * 
 * <p>
 * It is typical to listen to events using an anonymous class which often makes
 * the <code>abstract</code> <code>ConfigAdapter</code> class more convenient to
 * implement.
 * 
 * @see ConfigAdapter
 */
public interface ConfigListener {

	/**
	 * This event is automatically fired at the recommended times for loading
	 * model parameters - that is at the start of model execution, prior to run
	 * start and before generation start. It may also be triggered manually,
	 * potentially by the model if it updates its parameters at an unorthadox
	 * time in the life cycle.
	 */
	void onConfigure();

}
