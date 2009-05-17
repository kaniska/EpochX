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

package com.epochx.ant;

import static com.epochx.ant.Orientation.*;

import java.awt.*;

/**
 * An Ant represents an artificial ant which exists within and can move about 
 * and perform other actions on an <code>AntLandscape</code>.
 */
public class Ant {
    
	// The compass direction the ant is currently facing to travel.
    private Orientation orientation;
    
    // How many time steps have passed for the ant.
    //TODO This should be renamed because it's not number of moves it's number of timesteps.
    private int moves;
    
    // The maximum number of time steps that this ant is allowed to make.
    //TODO This should probably be renamed at the same time as the 'moves' field.
    private int maxMoves;
    
    // Location of the ant within its landscape.
    private int xLocation;
    private int yLocation;
    
    // How many food pellets this ant has eaten.
    private int foodEaten;
    
    // The landscape that this ant exists within.
    private AntLandscape landscape;
    
    /**
     * Constructs an Ant object on the given landscape.
     * @param timeSteps the maximum number of time steps the ant is allowed to 
     * move. Turning, moving and skipping all count as one time step.
     * @param landscape the landscape the ant will move through.
     */
    public Ant(int timeSteps, AntLandscape landscape) {
        // Initialise the ant.
    	reset(timeSteps, landscape);
    }
    
    /**
     * Resets the ant, setting it up with a new number of time steps allocated 
     * and a new <code>AntLandscape</code>.
     * @param timeSteps the maximum number of time steps the ant is allowed to 
     * move. Turning, moving and skipping all count as one time step.
     * @param landscape the landscape the ant will move through.
     */
    public void reset(int timeSteps, AntLandscape landscape) {
        this.maxMoves = timeSteps;
        this.landscape = landscape;
    	
        // Initialise the ant.
    	reset();
    }
    
    /**
     * Reset the ant using the same number of time steps and on the same 
     * <code>AntLandscape</code>.
     */
    public void reset() {
    	orientation = EAST;
        moves = 0;
        xLocation = 0;
        yLocation = 0;
        foodEaten = 0;
    }
    
    /**
     * Turn the ant to the left relative to its current orientation. For 
     * example, if calling getOrientation() returns EAST before calling this 
     * method it will return NORTH after calling this method. If the ant 
     * has reached its maximum number of allowed time steps then this method 
     * will do nothing.
     */
    public void turnLeft() {
        // Don't allow the turn it the ant has reached its maximum.
    	if(moves >= maxMoves) {
            return;
        }
        
    	// Set the ant's new orientation.
        if(orientation == EAST) {
            orientation = NORTH;
        } else if(orientation == NORTH) {
            orientation = WEST;
        } else if(orientation == WEST) {
            orientation = SOUTH;
        } else if(orientation == SOUTH) {
            orientation = EAST;
        }
        
        moves++;
    }
    
    /**
     * Turn the ant to the right relative to its current orientation. For 
     * example, if calling getOrientation() returns EAST before calling this 
     * method it will return SOUTH after calling this method. If the ant 
     * has reached its maximum number of allowed time steps then this method 
     * will do nothing.
     */
    public void turnRight() {
    	// Don't allow the turn it the ant has reached its maximum.
    	if(moves >= maxMoves) {
            return;
        }
    	
    	// Set the ant's new orientation.
        if(orientation == EAST) {
            orientation = SOUTH;
        } else if(orientation == SOUTH) {
            orientation = WEST;
        } else if(orientation == WEST) {
            orientation = NORTH;
        } else if(orientation == NORTH) {
            orientation = EAST;
        }
        
        moves++;
    }
    
    /**
     * Moves the ant's position one place in the direction it is currently 
     * facing based upon its orientation. If the ant's new location on the 
     * landscape contains a food pellet then the ant will attempt to eat it. 
     * If the ant has reached its maximum number of allowed time steps then 
     * this method will do nothing.
     */
    public void move() {
    	// Don't allow the move it the ant has reached its maximum.
        if(moves>=maxMoves) {
            return;
        }
        
        // Update the ant's location according to its orientation.
        if(orientation == EAST) {
            if(xLocation<31) {
                xLocation++;
            } else {
                xLocation = 0;
            }
        } else if(orientation == NORTH) {
            if(yLocation>0) {
                yLocation--;
            } else {
                yLocation = 31;
            }
        } else if(orientation == WEST) {
            if(xLocation>0) {
                xLocation--;
            } else {
                xLocation = 31;
            }
        } else if(orientation == SOUTH) {
            if(yLocation<31) {
                yLocation++;
            } else {
                yLocation = 0;
            }
        }
        //TODO If we update the number of moves BEFORE eating then we might reach the limit here and not beable to eat. Is that right? Because eating doesn't take a move. I'm not sure eatFood should test for max moves at all.
        moves++;
        
        // If the new location has food then eat it.
        if(landscape.isFoodLocation(getLocation())) {
        	this.eatFood();
        	landscape.removeFoodLocation(getLocation());
        }
    }
    
    /**
     * Increments the number of food pellets the ant has eaten.
     */
    public void eatFood() {
    	// Don't allow the ant to eat if it has reached its maximum moves.
    	if(moves>=maxMoves) {
            return;
        }
    	
        foodEaten++;
    }
    
    /**
     * Returns the current location of Ant as a point in the x/y-coordinate 
     * system that represents the landscape.
     * @return the current location of the ant as a <code>Point</code>.
     */
    public Point getLocation() {
    	return new Point(xLocation, yLocation);
    }
    
    /**
     * Sets the location of the ant on its landscape using an x/y co-ordinate 
     * system.
     * @param x the x axis location of the ant.
     * @param y the Y axis location of the ant.
     */
    public void setLocation(int x, int y) {
        xLocation = x;
        yLocation = y;
    }
    
    /**
     * Gets the number of time steps the ant has completed. Turning, moving and 
     * skipping all count as one time step.
     * @return The number of time steps the ant has gone through.
     */
    public int getMoves() {
        return moves;
    }
    
    /**
     * Returns the number of food pellets the ant has eaten.
     * @return The number of food pellets the ant has eaten.
     */
    public int getFoodEaten() {
        return foodEaten;
    }
    
    /**
     * Returns the direction the Ant is currently facing and travelling.
     * @return a compass orientation indicating the direction the ant is 
     * currently facing.
     */
    public Orientation getOrientation() {
        return orientation;
    }
    
    /**
     * Sets the orientation the Ant is to facing. This is the direction the 
     * ant will proceed in if the move method is called next.
     * @param orientation The new orientation the ant is to face.
     */
    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }
    
    /**
     * Skipping will cause the ant to fill one timestep without moving in its 
     * ant landscape. This is required for the skip algorithm to prevent the 
     * ant falling into dead ends.
     */
    public void skip() {
        moves++;
    }
    
    /**
     * Returns the maximum number of time steps the ant is allowed to 
     * move. Turning, moving and skipping all count as one time step.
     * @return the maximum number of time steps the ant is allowed to 
     * move
     */
    public int getMaxMoves() {
    	return maxMoves;
    }
    
    @Override
    public String toString() {
    	return xLocation + ":" + yLocation + " Facing " + orientation + " --- moves = " + moves;
    }
}
