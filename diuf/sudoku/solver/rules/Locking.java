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
 * Implementation of Pointing and Claiming solving techniques.
 */
public class Locking implements IndirectHintProducer {

    private final boolean isDirectMode;

    public Locking(boolean isDirectMode) {
        this.isDirectMode = isDirectMode;
    }

    public void getHints(Grid grid, HintsAccumulator accu) throws InterruptedException {
       if ( !grid.isLatinSquare() ) {
        getHints(grid, Grid.Block.class, Grid.Column.class, accu);
        getHints(grid, Grid.Block.class, Grid.Row.class, accu);
        getHints(grid, Grid.Column.class, Grid.Block.class, accu);
        getHints(grid, Grid.Row.class, Grid.Block.class, accu);
       }

      if ( grid.isDiagonals() ) {
       if ( !grid.isLatinSquare() ) {
        if ( grid.isXDiagonal() )
        getHints(grid, Grid.Block.class, Grid.Diagonal.class, accu);
        if ( grid.isXAntiDiagonal() )
        getHints(grid, Grid.Block.class, Grid.AntiDiagonal.class, accu); }
        if ( grid.isXDiagonal() )
        getHints(grid, Grid.Column.class, Grid.Diagonal.class, accu);
        if ( grid.isXAntiDiagonal() )
        getHints(grid, Grid.Column.class, Grid.AntiDiagonal.class, accu);
        if ( grid.isXDiagonal() )
        getHints(grid, Grid.Row.class, Grid.Diagonal.class, accu);
        if ( grid.isXAntiDiagonal() )
        getHints(grid, Grid.Row.class, Grid.AntiDiagonal.class, accu);
       if ( !grid.isLatinSquare() ) {
        if ( grid.isXDiagonal() )
        getHints(grid, Grid.Diagonal.class, Grid.Block.class, accu);
        if ( grid.isXAntiDiagonal() )
        getHints(grid, Grid.AntiDiagonal.class, Grid.Block.class, accu); }
        if ( grid.isXDiagonal() )
        getHints(grid, Grid.Diagonal.class, Grid.Column.class, accu);
        if ( grid.isXAntiDiagonal() )
        getHints(grid, Grid.AntiDiagonal.class, Grid.Column.class, accu);
        if ( grid.isXDiagonal() )
        getHints(grid, Grid.Diagonal.class, Grid.Row.class, accu);
        if ( grid.isXAntiDiagonal() )
        getHints(grid, Grid.AntiDiagonal.class, Grid.Row.class, accu);
      }

      if ( grid.isDisjointGroups() ) {
       if ( !grid.isLatinSquare() ) {
        getHints(grid, Grid.Block.class, Grid.DisjointGroup.class, accu); }
        getHints(grid, Grid.Column.class, Grid.DisjointGroup.class, accu);
        getHints(grid, Grid.Row.class, Grid.DisjointGroup.class, accu);
       if ( !grid.isLatinSquare() ) {
        getHints(grid, Grid.DisjointGroup.class, Grid.Block.class, accu); }
        getHints(grid, Grid.DisjointGroup.class, Grid.Column.class, accu);
        getHints(grid, Grid.DisjointGroup.class, Grid.Row.class, accu);
       if ( grid.isDiagonals() ) {
        if ( grid.isXDiagonal() )
        getHints(grid, Grid.Diagonal.class, Grid.DisjointGroup.class, accu);
        if ( grid.isXAntiDiagonal() )
        getHints(grid, Grid.AntiDiagonal.class, Grid.DisjointGroup.class, accu);
        if ( grid.isXDiagonal() )
        getHints(grid, Grid.DisjointGroup.class, Grid.Diagonal.class, accu);
        if ( grid.isXAntiDiagonal() )
        getHints(grid, Grid.DisjointGroup.class, Grid.AntiDiagonal.class, accu);
       }
      }

      if ( grid.isWindoku() ) {
       if ( !grid.isLatinSquare() ) {
        getHints(grid, Grid.Block.class, Grid.Windoku.class, accu); }
        getHints(grid, Grid.Column.class, Grid.Windoku.class, accu);
        getHints(grid, Grid.Row.class, Grid.Windoku.class, accu);
       if ( !grid.isLatinSquare() ) {
        getHints(grid, Grid.Windoku.class, Grid.Block.class, accu); }
        getHints(grid, Grid.Windoku.class, Grid.Column.class, accu);
        getHints(grid, Grid.Windoku.class, Grid.Row.class, accu);
       if ( grid.isDiagonals() ) {
        if ( grid.isXDiagonal() )
        getHints(grid, Grid.Diagonal.class, Grid.Windoku.class, accu);
        if ( grid.isXAntiDiagonal() )
        getHints(grid, Grid.AntiDiagonal.class, Grid.Windoku.class, accu);
        if ( grid.isXDiagonal() )
        getHints(grid, Grid.Windoku.class, Grid.Diagonal.class, accu);
        if ( grid.isXAntiDiagonal() )
        getHints(grid, Grid.Windoku.class, Grid.AntiDiagonal.class, accu);
       }
       if ( grid.isDisjointGroups() ) {
        getHints(grid, Grid.DisjointGroup.class, Grid.Windoku.class, accu);
        getHints(grid, Grid.Windoku.class, Grid.DisjointGroup.class, accu);
       }
      }

      if ( grid.isCustom() ) {
       if ( !grid.isLatinSquare() ) {
        getHints(grid, Grid.Block.class, Grid.Custom.class, accu); }
        getHints(grid, Grid.Column.class, Grid.Custom.class, accu);
        getHints(grid, Grid.Row.class, Grid.Custom.class, accu);
       if ( !grid.isLatinSquare() ) {
        getHints(grid, Grid.Custom.class, Grid.Block.class, accu); }
        getHints(grid, Grid.Custom.class, Grid.Column.class, accu);
        getHints(grid, Grid.Custom.class, Grid.Row.class, accu);
       if ( grid.isDiagonals() ) {
        if ( grid.isXDiagonal() )
        getHints(grid, Grid.Diagonal.class, Grid.Custom.class, accu);
        if ( grid.isXAntiDiagonal() )
        getHints(grid, Grid.AntiDiagonal.class, Grid.Custom.class, accu);
        if ( grid.isXDiagonal() )
        getHints(grid, Grid.Custom.class, Grid.Diagonal.class, accu);
        if ( grid.isXAntiDiagonal() )
        getHints(grid, Grid.Custom.class, Grid.AntiDiagonal.class, accu);
       }
       if ( grid.isWindoku() ) {
        getHints(grid, Grid.Windoku.class, Grid.Custom.class, accu);
        getHints(grid, Grid.Custom.class, Grid.Windoku.class, accu);
       }
      }

        // generalized intersection

      if ( !isDirectMode ) {
       if ( !grid.isLatinSquare() ) {
        getHintsG(grid, Grid.Block.class, accu); }
        getHintsG(grid, Grid.Column.class, accu);
        getHintsG(grid, Grid.Row.class, accu);
       if ( grid.isDiagonals() ) {
        if ( grid.isXDiagonal() )
        getHintsG(grid, Grid.Diagonal.class, accu);
        if ( grid.isXAntiDiagonal() )
        getHintsG(grid, Grid.AntiDiagonal.class, accu);
       }
       if ( grid.isDisjointGroups() ) {
        getHintsG(grid, Grid.DisjointGroup.class, accu);
       }
       if ( grid.isWindoku() ) {
        getHintsG(grid, Grid.Windoku.class, accu);
       }
       if ( grid.isCustom() ) {
        getHintsG(grid, Grid.Custom.class, accu);
       }
      }
    }

    /**
     * Given two part types, iterate on pairs of parts of both types that
     * are crossing. For each such pair (p1, p2), check if all the potential
     * positions of a value in p1 are also in p2.
     * <p>
     * Note: at least one of the two part type must be a
     * {@link Grid.Block 3x3 square}.
     * @param regionType1 the first part type
     * @param regionType2 the second part type
     */
    private <S extends Grid.Region, T extends Grid.Region> void getHints(
            Grid grid, Class<S> regionType1, Class<T> regionType2,
            HintsAccumulator accu) throws InterruptedException {
//a     assert (regionType1 == Grid.Block.class) != (regionType2 == Grid.Block.class);
        int i1max = grid.getRegionMax(regionType1);
        int i2max = grid.getRegionMax(regionType2);
        // Iterate on pairs of parts
        for (int i1 = 0; i1 < i1max; i1++) {
            for (int i2 = 0; i2 < i2max; i2++) {
                Grid.Region region1 = grid.getRegions(regionType1)[i1];
                Grid.Region region2 = grid.getRegions(regionType2)[i2];
              if ( region1 != null && region2 != null ) {
                if (region1.crosses(region2)) {
                    Set<Cell> region2Cells = region2.getCellSet();
                    // Iterate on values
                    for (int value = 1; value <= 9; value++) {
                        boolean isInCommonSet = true;
                        // Get the potential positions of the value in part1
                        BitSet potentialPositions = region1.getPotentialPositions(value);
                        // Note: if cardinality == 1, this is Hidden Single in part1
                        if (potentialPositions.cardinality() > 1) {
                            // Test if all potential positions are also in part2
                            for (int i = 0; i < 9; i++) {
                                if (potentialPositions.get(i)) {
                                    Cell cell = region1.getCell(i);
                                    if (!region2Cells.contains(cell))
                                        isInCommonSet = false;
                                }
                            }
                            if (isInCommonSet) {
                                if (isDirectMode) {
                                    lookForFollowingHiddenSingles(grid, regionType1, accu, i1,
                                            region1, region2, value);
                                } else {
                                    // Potential solution found
                                    IndirectHint hint = createLockingHint(region1, region2, null, value);
                                    if (hint.isWorth())
                                        accu.add(hint);
                                }
                            }
                        }
                    } // for each value
                } // if parts are crossing
              }
            }
        }
    }

    /** Generalized Intersection
     * Given one part type, iterate on values that number 2-4,
     * check for possible eliminations in the other regions.
     * <p>
     * Note: at least one variant must be enabled.
     * @param regionType1 the first part type
     */
    private <S extends Grid.Region> void getHintsG(
            Grid grid, Class<S> regionType1,
            HintsAccumulator accu) throws InterruptedException {
        int i1max = grid.getRegionMax(regionType1);
        // Iterate on pairs of parts
        for (int i1 = 0; i1 < i1max; i1++) {
            Grid.Region region1 = grid.getRegions(regionType1)[i1];
            if ( region1 != null ) {
                Set<Cell> region1Cells = region1.getCellSet();
                for (int value = 1; value <= 9; value++) {
                    // Get the potential positions of the value in part1
                    BitSet potentialPositions = region1.getPotentialPositions(value);
                    int potentialCardinality = potentialPositions.cardinality();
                    if (potentialCardinality > 1 && potentialCardinality < 8) {

                        Cell[] regionCells = new Cell[potentialCardinality]; // highlighted potentials
                        List<Cell> result = new ArrayList<Cell>();           // generalized intersection cells
                        for (int i = 0, pos = 0; i < 9; i++) {
                            if (potentialPositions.get(i)) {
                                Cell cell = region1.getCell(i);
                                regionCells[pos++] = cell;
                                if ( result.isEmpty() ) {
                                    result.addAll(cell.getHouseCells());
                                }
                                else{
                                    result.retainAll(cell.getHouseCells());
                                }
                            }
                        }
                        result.removeAll(region1Cells);
                        if ( !result.isEmpty() ) {
                            int havePossibleSolution = 0;
                            for ( Cell cell : result ) {
                                if ( cell.hasPotentialValue(value) ) {
                                    // Possible solution found
                                    havePossibleSolution = 1;
                                }
                            }
                          if ( havePossibleSolution == 1 ) {
                            // Build highlighted potentials
                            Map<Cell,BitSet> cellPotentials = new HashMap<Cell,BitSet>();
                            for (int i = 0; i< potentialCardinality; i++) {
                                cellPotentials.put(regionCells[i], SingletonBitSet.create(value));
                            }
                            // Build removable potentials
                            Map<Cell,BitSet> cellRemovablePotentials = new HashMap<Cell,BitSet>();
                            for ( Cell cell : result ) {
                                if ( cell.hasPotentialValue(value) ) {
                                    // Possible solution found
                                    cellRemovablePotentials.put(cell, SingletonBitSet.create(value));
                                }
                            }

                            // Build hint
                            IndirectHint hint = new LockingGHint(this, regionCells, value,
                                cellPotentials, cellRemovablePotentials, region1, null);
                            if (hint.isWorth()) {
                                accu.add(hint);
                            }
                          }
                        } // !Empty
                    } // cardinality
                } // for each value
            } // region1
        }
    }

    private <S extends Grid.Region> void lookForFollowingHiddenSingles(Grid grid,
            Class<S> regionType1, HintsAccumulator accu, int i1,
            Grid.Region region1, Grid.Region region2, int value) throws InterruptedException {
        int i3max = grid.getRegionMax(regionType1);
        // Look if the pointing / claiming induce a hidden single
        for(int i3 = 0; i3 < i3max; i3++) {
            if (i3 != i1) {
                Grid.Region region3 = grid.getRegions(regionType1)[i3];
              if ( region3 != null ) {
                if (region3.crosses(region2)) {
                    // Region <> region1 but crosses region2
                    Set<Cell> region2Cells = region2.getCellSet();
                    BitSet potentialPositions3 = region3.getPotentialPositions(value);
                    if (potentialPositions3.cardinality() > 1) {
                        int nbRemainInRegion3 = 0;
                        Cell hcell = null;
                        for (int i = 0; i < 9; i++) {
                            if (potentialPositions3.get(i)) {
                                Cell cell = region3.getCell(i);
                                if (!region2Cells.contains(cell)) { // This position is not removed
                                    nbRemainInRegion3++;
                                    hcell = cell;
                                }
                            }
                        }
                        if (nbRemainInRegion3 == 1) {
                            IndirectHint hint = createLockingHint(region1, region2, hcell, value);
                            if (hint.isWorth())
                                accu.add(hint);
                        }
                    }
                }
              }
            }
        }
    }

    private IndirectHint createLockingHint(Grid.Region p1, Grid.Region p2, Cell hcell, int value) {
        // Build highlighted potentials
        Map<Cell,BitSet> cellPotentials = new HashMap<Cell,BitSet>();
        for (int i = 0; i < 9; i++) {
            Cell cell = p1.getCell(i);
            if (cell.hasPotentialValue(value))
                cellPotentials.put(cell, SingletonBitSet.create(value));
        }
        // Build removable potentials
        Map<Cell,BitSet> cellRemovablePotentials = new HashMap<Cell,BitSet>();
        List<Cell> highlightedCells = new ArrayList<Cell>();
        Set<Cell> p1Cells = p1.getCellSet();
        for (int i = 0; i < 9; i++) {
            Cell cell = p2.getCell(i);
            if (!p1Cells.contains(cell)) {
                if (cell.hasPotentialValue(value))
                    cellRemovablePotentials.put(cell, SingletonBitSet.create(value));
            } else if (cell.hasPotentialValue(value))
                highlightedCells.add(cell);
        }
        // Build list of cells
        Cell[] cells = new Cell[highlightedCells.size()];
        highlightedCells.toArray(cells);
        // Build hint
        if (isDirectMode)
            return new DirectLockingHint(this, cells, hcell, value, cellPotentials,
                    cellRemovablePotentials, p1, p2);
        else
            return new LockingHint(this, cells, value, cellPotentials,
                    cellRemovablePotentials, p1, p2);
    }

    @Override
    public String toString() {
        if (isDirectMode)
            return "Direct Intersections";
        else
            return "Intersections";
    }

}
