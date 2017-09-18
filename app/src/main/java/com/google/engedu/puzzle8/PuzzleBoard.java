/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.puzzle8;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.Map;


public class PuzzleBoard {

    private static final int NUM_TILES = 3;
    private static final int[][] NEIGHBOUR_COORDS = {
            { -1, 0 },
            { 1, 0 },
            { 0, -1 },
            { 0, 1 }
    };
    int steps;

    private ArrayList<PuzzleTile> tiles;
    private PuzzleBoard previousBoard ;
    PuzzleBoard(Bitmap bitmap, int parentWidth) {
        previousBoard = null;
        //it will make partitions of the image
        tiles=new ArrayList<PuzzleTile>();
        int tileNumber=-1;
        bitmap = Bitmap.createScaledBitmap(bitmap,parentWidth,parentWidth,true);
        for(int row=0;row<NUM_TILES;row++){
            for(int col=0;col<NUM_TILES;col++){
                tileNumber++;
                if(!(row==NUM_TILES-1&&col==NUM_TILES-1)){
                    tiles.add(new PuzzleTile(Bitmap.createBitmap(bitmap,col*parentWidth/NUM_TILES,row*parentWidth/NUM_TILES,parentWidth/NUM_TILES,parentWidth/NUM_TILES),tileNumber));
                }
                else{
                    tiles.add(null);
                }
            }
        }
    }

    PuzzleBoard(PuzzleBoard otherBoard) {
        previousBoard=otherBoard;
        tiles = (ArrayList<PuzzleTile>) otherBoard.tiles.clone();
        steps= 0;
    }

    public void reset() {
        previousBoard=null;
        steps = 0;
        // Nothing for now but you may have things to reset once you implement the solver.
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        return tiles.equals(((PuzzleBoard) o).tiles);
    }

    public void draw(Canvas canvas) {
        if (tiles == null) {
            return;
        }
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                tile.draw(canvas, i % NUM_TILES, i / NUM_TILES);
            }
        }
    }

    public boolean click(float x, float y) {
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                if (tile.isClicked(x, y, i % NUM_TILES, i / NUM_TILES)) {
                    return tryMoving(i % NUM_TILES, i / NUM_TILES);
                }
            }
        }
        return false;
    }

    private boolean tryMoving(int tileX, int tileY) {
        for (int[] delta : NEIGHBOUR_COORDS) {
            int nullX = tileX + delta[0];
            int nullY = tileY + delta[1];
            if (nullX >= 0 && nullX < NUM_TILES && nullY >= 0 && nullY < NUM_TILES &&
                    tiles.get(XYtoIndex(nullX, nullY)) == null) {
                swapTiles(XYtoIndex(nullX, nullY), XYtoIndex(tileX, tileY));
                return true;
            }

        }
        return false;
    }

    public boolean resolved() {
        for (int i = 0; i < NUM_TILES * NUM_TILES - 1; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile == null || tile.getNumber() != i)
                return false;
        }
        return true;
    }

    private int XYtoIndex(int x, int y) {
        return x + y * NUM_TILES;
    }

    protected void swapTiles(int i, int j) {
        PuzzleTile temp = tiles.get(i);
        tiles.set(i, tiles.get(j));
        tiles.set(j, temp);
    }

    public ArrayList<PuzzleBoard> neighbours() {
        ArrayList<PuzzleBoard> possibleMoves = new ArrayList<>();
        int x=-1,y=-1;
        for(int j=0;j<NUM_TILES;j++){
            for (int i=0;i<NUM_TILES;i++){
                if(tiles.get(XYtoIndex(i,j))==null){
                    x=i;y=j;
                }
            }
        }

        PuzzleBoard copy;

        for(int[] tuple:NEIGHBOUR_COORDS){
            if(XYtoIndex(tuple[0]+x,tuple[1]+y)<NUM_TILES*NUM_TILES && XYtoIndex(tuple[0]+x,tuple[1]+y)>=0 && tuple[0]+x>=0 && tuple[0]+x<NUM_TILES && tuple[1]+y>=0 && tuple[1]+y<NUM_TILES){
                copy=new PuzzleBoard(this);
                copy.swapTiles(XYtoIndex(tuple[0]+x, tuple[1]+y),XYtoIndex(x,y));
                if(copy!=previousBoard) possibleMoves.add(copy);
            }
        }

        return possibleMoves;
    }

    public int priority() {
        int manhattanDistance =0;
        for(int j=0;j<NUM_TILES;j++){
            for (int i=0;i<NUM_TILES;i++){
                if(tiles.get(XYtoIndex(i,j)) != null){
                    int targetX = (tiles.get(XYtoIndex(i,j)).getNumber())/NUM_TILES;
                    int targetY = (tiles.get(XYtoIndex(i,j)).getNumber())/NUM_TILES;
                    manhattanDistance += Math.abs(i-targetX)+ Math.abs(j-targetY);
                }
            }
        }
        return manhattanDistance + steps;
    }

    public PuzzleBoard getPreviousBoard() {
        return previousBoard;
    }
}
