/*
 * Copyright 2007-2013
 * Licensed under GNU Lesser General Public License
 * 
 * This file is part of EpochX
 * 
 * EpochX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * EpochX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with EpochX. If not, see <http://www.gnu.org/licenses/>.
 * 
 * The latest version is available from: http://www.epochx.org
 */

package org.epochx;

import org.epochx.Config.ConfigKey;

/**
 * <code>Initialiser</code> components are responsible for creating individuals,
 * delegating the creation to an {@link InitialisationMethod} instance.
 */
public class Initialiser extends ProxyComponent<InitialisationMethod> {

	/**
	 * The key for setting and retrieving the <code>InitialisationMethod</code>
	 * used
	 * by this component.
	 */
	public static final ConfigKey<InitialisationMethod> METHOD = new ConfigKey<InitialisationMethod>();

	/**
	 * Constructs a <code>Initialiser</code>.
	 */
	public Initialiser() {
		super(METHOD);
	}

	/**
	 * Delegates the initialisation of the population to the
	 * <code>InitialisationMethod</code> object.
	 */
	@Override
	public Population process(Population population) {
		if (handler == null) {
			throw new IllegalStateException("The initialisation method has not been set.");
		}

		return handler.createPopulation();
	}

}