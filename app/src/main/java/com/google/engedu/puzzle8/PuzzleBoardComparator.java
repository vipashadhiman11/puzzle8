package com.google.engedu.puzzle8;

import java.util.Comparator;

/**
 * Created by DELL-PC on 6/3/2017.
 */

public class PuzzleBoardComparator implements Comparator<PuzzleBoard> {
    @Override
    public int compare(PuzzleBoard x, PuzzleBoard y) {
        return  x.priority() - y.priority();

    }
}
