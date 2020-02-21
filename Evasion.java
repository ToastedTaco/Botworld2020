/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package brain;

import actor.Block;
import actor.BotBrain;
import actor.Business;
import actor.GameObject;
import actor.SubwayStation;
import grid.Location;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;

/**
 *
 * @author 20lambi
 */
public class Evasion extends BotBrain {

    private Location prevLoc = getLocation();
    private Location initialLoc = new Location(0, 0);
    private Color myColor;
    private ArrayList<Integer> prevMoves = new ArrayList();

    @Override
    public int chooseAction() {

        Business[][] businessGrid = getBusinessArray();
        Business businessOnThisSpace = businessGrid[getRow()][getCol()];
        Location prevLocA = getLocation(); //saving previous location, so bot will not move on a previous location
       
        int direction = evadeDir(); //calls method to find best move based on whether or not there is a "Block" and is the chepest move
        if (!businessOnThisSpace.getColor().equals(getColor())) {
            if (this.getScore() >= businessOnThisSpace.getCostToBuy()) {
                return 2000;
            }
        } else if (!(prevLoc.equals(initialLoc)) && (businessOnThisSpace.getColor().equals(myColor))) {
            return 3000;
        } 
        prevLoc = prevLocA;
      
        prevMoves.add(direction);
        return direction;

    }

    public Location getLocation() {
        return new Location(getRow(), getCol());
    }

    //Finds best random path based on Blocked Locations and cheapest cells
    public int evadeDir() {
        Business[][] businessArray = this.getBusinessArray();
        GameObject[][] objectArray = this.getArena();
        ArrayList<Location> locs = new ArrayList();
        //Creates an arraylist of all adjacent locations
        for (int d = 0; d < 360; d = d + 90) {
            if (inBounds(getLocation().getAdjacentLocation(d)) == true) {
                locs.add(getLocation().getAdjacentLocation(d));
            }
        }
        //Prints out adjacent location
        //Creates an arraylist of the costs of adjacent locations
        /*
        ArrayList<Integer> costs = new ArrayList();
        for (int c = 0; c < 4; c++) {
            costs.add(nextN(locs.get(c), businessArray));
        }
         */
        //Makes direction equal to the ideal next move as found in the nextMov method
        int direction = nextMov(locs, objectArray, businessArray, prevLoc);
        return direction;
    }

    //Finds cost of a location if it is valid
    public int nextN(Location next, Business[][] businessArray) {
        int costN = -1;

        costN = businessArray[next.getRow()][next.getCol()].getChargeAmount();

        return costN;
    }

    //Finds whether a space is blocked or has a subwaystation
    public boolean isBlock(Location next, GameObject[][] objectArray, Business[][] businessArray) {
        boolean isBlock = true;
        if ((!(objectArray[next.getRow()][next.getCol()] instanceof Block)) && (!(businessArray[next.getRow()][next.getCol()] instanceof SubwayStation))) {
            isBlock = false;
        }
        return isBlock;

    }

    //Determines next move based on whether a cell is blocked or contains a subwaystation and if it is the cheapest move, additionally, this method makes sure that the bot doesn't move on a previous location
    public int nextMov(ArrayList<Location> locs, GameObject[][] objectArray, Business[][] businessArray, Location prevLoc) {
        int direction = 0;
        ArrayList<Location> validLocs = new ArrayList();
        for (int c = 0; c < locs.size(); c++) {
            if (((isBlock(locs.get(c), objectArray, businessArray)) == false)) {
                validLocs.add(locs.get(c));
            }
        }
        ArrayList<Integer> costsValidLocs = new ArrayList();
        for (int c = 0; c < validLocs.size(); c++) {
            costsValidLocs.add(nextN(validLocs.get(c), businessArray));
        }
        ArrayList<Location> cheapestMoves = new ArrayList();
        for (int c = 0; c < validLocs.size(); c++) {
            if ((nextN(validLocs.get(c), businessArray) == Collections.min(costsValidLocs))) {
                cheapestMoves.add(validLocs.get(c));
            }
        }

        for (int c = 0; c < cheapestMoves.size(); c++) {
            if ((isBlock(cheapestMoves.get(c), objectArray, businessArray) == true)) {
                cheapestMoves.remove(c);
            }
        
        }

        if (cheapestMoves.size() > 1) {
            Random r = new Random();
            int validLocNum = r.nextInt(cheapestMoves.size());
            direction = getLocation().getDirectionToward(cheapestMoves.get(validLocNum));
        } else if (cheapestMoves.size() == 1) {
            direction = getLocation().getDirectionToward(cheapestMoves.get(0));
        } else {
            direction = (int) (Math.random() * 4) * 90;
        }
        return direction;
    }

    public boolean inBounds(Location next) {
        boolean inBounds = false;
        if ((next.getRow() >= 0 && next.getRow() <= 20) && (next.getCol() >= 0 && next.getCol() <= 20)) {
            inBounds = true;

        }
      
        return inBounds;
    }

}
