/*
 * Project: Sudoku Explainer
 * Copyright (C) 2023 1to9only
 * Available under the terms of the Lesser General Public License (LGPL)
 */
package diuf.sudoku.solver;

import java.util.ArrayList;
import java.util.List;

/**
 * Hints accumulator, that accumulates hints of the same rating.
 */
public class SmallestHintAccumulator implements HintsAccumulator {

    private List<Hint> result = new ArrayList<Hint>();

    private double lastDifficulty = 0.0;

    public SmallestHintAccumulator() {
        super();
    }

    public void add( Hint hint) throws InterruptedException {
        double newDifficulty = ((Rule)hint).getDifficulty();
        if ( lastDifficulty == 0.0 ) {
            lastDifficulty = newDifficulty;
        }
        if ( lastDifficulty != newDifficulty ) {
            throw new InterruptedException();
        }
        if ( !result.contains( hint) ) {
            result.add( hint);
        }
    }

    public List<Hint> getHints() {
        return result;
    }

    public boolean hasHints()
    {
        return !result.isEmpty();
    }

    public void clear()
    {
        result.clear();
        lastDifficulty = 0.0;
    }

}
