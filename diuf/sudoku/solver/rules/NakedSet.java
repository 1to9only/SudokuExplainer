/*
 * Project: Sudoku Explainer
 * Copyright (C) 2006-2007 Nicolas Juillerat
 * Available under the terms of the Lesser General Public License (LGPL)
 */
package diuf.sudoku.solver.rules;

import java.util.*;

import diuf.sudoku.*;
import diuf.sudoku.solver.*;
import diuf.sudoku.tools.*;


/**
 * Implementation of the naked sets solving techniques
 * (Naked Pair, Naked Triplet, Naked Quad).
 */
public class NakedSet implements IndirectHintProducer {

    private int degree;

    public NakedSet(int degree) {
//a     assert degree > 1 && degree <= 4;
        this.degree = degree;
    }

    public void getHints(Grid grid, HintsAccumulator accu) throws InterruptedException {
      if ( !grid.isLatinSquare() ) {
        getHints(grid, Grid.Block.class, accu);
      }
        getHints(grid, Grid.Column.class, accu);
        getHints(grid, Grid.Row.class, accu);
      if ( grid.isDiagonals() ) {
       if ( grid.isXDiagonal() ) {
        getHints(grid, Grid.Diagonal.class, accu);
       }
       if ( grid.isXAntiDiagonal() ) {
        getHints(grid, Grid.AntiDiagonal.class, accu);
       }
      }
      if ( grid.isDisjointGroups() ) {
        getHints(grid, Grid.DisjointGroup.class, accu);
      }
      if ( grid.isWindoku() ) {
        getHints(grid, Grid.Windoku.class, accu);
      }
      if ( grid.isCustom() ) {
        getHints(grid, Grid.Custom.class, accu);
      }
    }

    /**
     * For each regions of the given type, check if a n-tuple of values have
     * a common n-tuple of potential positions, and no other potential position.
     */
    private <T extends Grid.Region> void getHints(Grid grid, Class<T> regionType,
            HintsAccumulator accu) throws InterruptedException {
        Grid.Region[] regions = grid.getRegions(regionType);
        // Iterate on parts
        for (Grid.Region region : regions) {
            if (region.getEmptyCellCount() >= degree * 2) {
                Permutations perm = new Permutations(degree, 9);
                // Iterate on tuples of positions
                while (perm.hasNext()) {
                    int[] indexes = perm.nextBitNums();
//a                 assert indexes.length == degree;

                    // Build the cell tuple
                    Cell[] cells = new Cell[degree];
                    for (int i = 0; i < cells.length; i++)
                        cells[i] = region.getCell(indexes[i]);

                    // Build potential values for each position of the tuple
                    BitSet[] potentialValues = new BitSet[degree];
                    for (int i = 0; i < degree; i++)
                        potentialValues[i] = cells[i].getPotentialValues();

                    // Look for a common tuple of potential values, with same degree
                    BitSet commonPotentialValues =
                        CommonTuples.searchCommonTuple(potentialValues, degree);
                    if (commonPotentialValues != null) {
                        // Potential hint found
                        IndirectHint hint = createValueUniquenessHint(region, cells, commonPotentialValues);
                        if (hint.isWorth())
                            accu.add(hint);
                    }
                }
            }
        }
    }

    private IndirectHint createValueUniquenessHint(Grid.Region region, Cell[] cells,
            BitSet commonPotentialValues) {
        // Build value list
        int[] values = new int[degree];
        int dstIndex = 0;
        for (int value = 1; value <= 9; value++) {
            if (commonPotentialValues.get(value))
                values[dstIndex++] = value;
        }
        // Build concerned cell potentials
        Map<Cell,BitSet> cellPValues = new LinkedHashMap<Cell,BitSet>();
        for (Cell cell : cells) {
            BitSet potentials = new BitSet(9);
            potentials.or(commonPotentialValues);
            potentials.and(cell.getPotentialValues());
            cellPValues.put(cell, potentials);
        }
        // Build removable potentials
        Map<Cell,BitSet> cellRemovePValues = new HashMap<Cell,BitSet>();
        for (int i = 0; i < 9; i++) {
            Cell otherCell = region.getCell(i);
            if (!Arrays.asList(cells).contains(otherCell)) {
                // Get removable potentials
                BitSet removablePotentials = new BitSet(9);
                for (int value = 1; value <= 9; value++) {
                    if (commonPotentialValues.get(value) && otherCell.hasPotentialValue(value))
                        removablePotentials.set(value);
                }
                if (!removablePotentials.isEmpty())
                    cellRemovePValues.put(otherCell, removablePotentials);
            }
        }
        return new NakedSetHint(this, cells, values, cellPValues, cellRemovePValues, region);
    }

    @Override
    public String toString() {
        if (degree == 2)
            return "Naked Pairs";
        else if (degree == 3)
            return "Naked Triplets";
        else if (degree == 4)
            return "Naked Quads";
        else if (degree == 5)
            return "Naked Quintuplets";
        else if (degree == 6)
            return "Naked Sextuplets";
        else if (degree == 7)
            return "Naked Septuplets";
        else if (degree == 8)
            return "Naked Octuplets";
        return "Naked Sets " + degree;
    }

}
