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
import actor.JobSite;
import actor.SubwayStation;
import grid.Location;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 *
 * @author 20lambi
 */
public class SmallBusiness extends BotBrain {

    private ArrayList<Location> JobsiteLoc = new ArrayList();
    private Location prevLoc = getLocation();

    @Override
    public int chooseAction() {
        Business[][] businessGrid = getBusinessArray();
        getJobsites(businessGrid);
        Location prevLocA = getLocation(); //saving previous location, so bot will not move on a previous location
        int direction = evadeDir(); //calls method to find best move based on whether or not there is a "Block" and is the chepest move
        if (this.getScore() < 180000) {
            if (this.getBusinessArray()[this.getRow()][this.getCol()].getColor().equals(this.getColor())) {
                if (this.getBusinessArray()[this.getRow()][this.getCol()].getChargeAmount() < 350) {
                    return 3060;
                } else {
                    return direction;
                }
            }
            return 2000;

        }

        prevLoc = prevLocA;
        return direction;

    }

    public void getJobsites(Business[][] businessGrid) {
        for (int r = 0; r <= 20; r++) {
            for (int c = 0; c <= 20; c++) {
                if (businessGrid[r][c] instanceof JobSite) {
                    if ((r != 0) && (c != 0) || (r != 20) && (c != 0) || (r != 20) && (c != 0) || (r != 20) && (c != 20)) {
                        JobsiteLoc.add(new Location(r, c));
                    }
                }
            }
        }
    }

    public int getClosest(Location currentLoc) {
        ArrayList<Integer> distances = new ArrayList();
        int goTo = 0;
        for (int c = 0; c < JobsiteLoc.size(); c++) {
            distances.add(currentLoc.distanceTo(JobsiteLoc.get(c)));
        }
        Collections.sort(distances);
        for (int c = 0; c < JobsiteLoc.size(); c++) {
            if (distances.get(0) == currentLoc.distanceTo(JobsiteLoc.get(c))) {
                goTo = c;
            }

        }
        return goTo;
    }
    
    //Finds location 
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
            costs.add(costN(locs.get(c), businessArray));
        }
         */
        //Makes direction equal to the ideal next move as found in the nextMov method
        int direction = nextMov(locs, objectArray, businessArray, prevLoc);
        return direction;
    }

    //Finds cost of a location if it is valid
    public int costN(Location next, Business[][] businessArray) {
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
            costsValidLocs.add(costN(validLocs.get(c), businessArray));
        }
        ArrayList<Location> cheapestMoves = new ArrayList();
        for (int c = 0; c < validLocs.size(); c++) {
            if ((costN(validLocs.get(c), businessArray) == Collections.min(costsValidLocs))) {
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

    //Checks to see if a given location is within the competition bounds
    public boolean inBounds(Location next) {
        boolean inBounds = false;
        if ((next.getRow() >= 0 && next.getRow() <= 20) && (next.getCol() >= 0 && next.getCol() <= 20)) {
            inBounds = true;

        }

        return inBounds;
    }
}
