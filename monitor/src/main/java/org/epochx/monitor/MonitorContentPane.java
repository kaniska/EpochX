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
package org.epochx.monitor;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * A <code>MonitorContentPane</code> defines the content pane of a
 * <code>Monitor</code> frame.
 */
public class MonitorContentPane extends JPanel {

	/**
	 * Generated serial UID.
	 */
	private static final long serialVersionUID = 5738368515703552671L;

	/*
	 * The border & gap widths constants.
	 */
	private final static int BORDERWIDTH = 10, GAPWIDTH = 5;

	/**
	 * The <code>GridLayout</code>.
	 */
	private final GridLayout gridLayout;

	/**
	 * 
	 * Constructs a <code>MonitorContentPane</code>.
	 * 
	 * @param rows the number of rows in the <code>GridLayout</code>.
	 * @param cols the number of cols in the <code>GridLayout</code>.
	 */
	protected MonitorContentPane(int rows, int cols) {
		super();
		this.gridLayout = new GridLayout(rows, cols, GAPWIDTH, GAPWIDTH);
		this.setLayout(gridLayout);
		this.setBorder(new EmptyBorder(BORDERWIDTH, BORDERWIDTH, BORDERWIDTH, BORDERWIDTH));
	}

}
