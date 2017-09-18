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

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;

public class PuzzleBoardView extends View {
    public static final int NUM_SHUFFLE_STEPS = 40;
    private Activity activity;
    private PuzzleBoard puzzleBoard;
    private ArrayList<PuzzleBoard> animation;
    private Random random = new Random();

    public PuzzleBoardView(Context context) {
        super(context);
        activity = (Activity) context;
        animation = null;
    }

    public void initialize(Bitmap imageBitmap, View parent) {
        int width = getWidth();
        puzzleBoard = new PuzzleBoard(imageBitmap, width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (puzzleBoard != null) {
            if (animation != null && animation.size() > 0) {
                puzzleBoard = animation.remove(0);
                puzzleBoard.draw(canvas);
                if (animation.size() == 0) {
                    animation = null;
                    puzzleBoard.reset();
                    Toast toast = Toast.makeText(activity, "Solved! ", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    this.postInvalidateDelayed(500);
                }
            } else {
                puzzleBoard.draw(canvas);
            }
        }
    }

    public void shuffle() {
        if (animation == null && puzzleBoard != null) {
            // Do something. Then:
            Random r=new Random();
            for(int i=0;i<NUM_SHUFFLE_STEPS;i++){
                ArrayList<PuzzleBoard> neighbours= puzzleBoard.neighbours();
                puzzleBoard= neighbours.get(r.nextInt(neighbours.size()));
                invalidate();
            }
            puzzleBoard.reset();
            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (animation == null && puzzleBoard != null) {
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (puzzleBoard.click(event.getX(), event.getY())) {
                        invalidate();
                        if (puzzleBoard.resolved()) {
                            Toast toast = Toast.makeText(activity, "Congratulations!", Toast.LENGTH_LONG);
                            toast.show();
                        }
                        return true;
                    }
            }
        }
        return super.onTouchEvent(event);
    }

    public void solve() {
        puzzleBoard.reset();
        Comparator<PuzzleBoard>comparator= new PuzzleBoardComparator();
        PriorityQueue<PuzzleBoard> priorityQueue= new PriorityQueue<PuzzleBoard>(comparator);
        priorityQueue.add(puzzleBoard);
        int count=0;
        PuzzleBoard leastPriorityBoard=priorityQueue.peek();
        while(!priorityQueue.isEmpty()){
            leastPriorityBoard=priorityQueue.remove();
            if(leastPriorityBoard.resolved()){
                ArrayList<PuzzleBoard> neighbours = leastPriorityBoard.neighbours();
                for (PuzzleBoard i:neighbours){
                    priorityQueue.add(i);
                }
                count++;
                if(count>100000){
                    Toast toast=Toast.makeText(activity,"I give up!",Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
            }else break;
        }
        ArrayList<PuzzleBoard> solutionSteps=new ArrayList<>();
        solutionSteps.add(leastPriorityBoard);
        PuzzleBoard backtrack = leastPriorityBoard.getPreviousBoard();
        while (backtrack!=null){
            solutionSteps.add(backtrack);
            backtrack=backtrack.getPreviousBoard();
        }
        Collections.reverse(solutionSteps);
        animation=solutionSteps;
        puzzleBoard = leastPriorityBoard;
        invalidate();

    }
}
