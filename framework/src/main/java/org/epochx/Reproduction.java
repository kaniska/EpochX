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
 * This class implements the reproduction (copy) operator.
 */
public class Reproduction extends AbstractOperator {

	/**
	 * The property key under which the reproduction probability is stored.
	 */
	public static final ConfigKey<Double> PROBABILITY = new ConfigKey<Double>();

	/**
	 * The default reproduction probability.
	 */
	public static final double DEFAULT_PROBABILITY = 0.90;

	@Override
	public int inputSize() {
		return 1;
	}

	@Override
	public Individual[] perform(Individual ... individuals) {
		return individuals;
	}

	@Override
	public double probability() {
		return Config.getInstance().get(PROBABILITY, DEFAULT_PROBABILITY);
	}

}