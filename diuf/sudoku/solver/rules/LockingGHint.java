/*
 * Project: Sudoku Explainer
 * Copyright (C) 2006-2007 Nicolas Juillerat
 * Available under the terms of the Lesser General Public License (LGPL)
 */
package diuf.sudoku.solver.rules;

import java.util.*;

import diuf.sudoku.*;
import diuf.sudoku.solver.*;
import diuf.sudoku.solver.rules.chaining.*;
import diuf.sudoku.tools.*;

/**
 * Generalized Intersection for Variants
 */
public class LockingGHint extends IndirectHint implements Rule, HasParentPotentialHint {

    private final Cell[] cells;
    private final int value;
    private final Map<Cell, BitSet> highlightPotentials;
    private final Grid.Region[] regions;

    public LockingGHint(IndirectHintProducer rule, Cell[] cells,
            int value, Map<Cell, BitSet> highlightPotentials,
            Map<Cell, BitSet> removePotentials, Grid.Region... regions) {
        super(rule, removePotentials);
        this.cells = cells;
        this.value = value;
        this.highlightPotentials = highlightPotentials;
        this.regions = regions;
    }

    @Override
    public int getViewCount() {
        return 1;
    }

    @Override
    public Cell[] getSelectedCells() {
        return cells;
    }

    @Override
    public Map<Cell, BitSet> getGreenPotentials(int viewNum) {
        return highlightPotentials;
    }

    @Override
    public Map<Cell, BitSet> getRedPotentials(int viewNum) {
        return super.getRemovablePotentials();
    }

    @Override
    public Collection<Link> getLinks(int viewNum) {
        return null;
    }

    @Override
    public Grid.Region[] getRegions() {
        return this.regions;
    }

    public double getDifficulty() {
        return 2.9; // Generalized Intersection
    }

    public String getName() {
        return "Generalized Intersection";
    }

    public Collection<Potential> getRuleParents(Grid initialGrid, Grid currentGrid) {
        Collection<Potential> result = new ArrayList<Potential>();
        // Add any potential of first region that are not in second region
        for (int i = 0; i < 1; i+= 2) {
            for (int pos1 = 0; pos1 < 9; pos1++) {
                Cell cell = regions[i].getCell(pos1);
                Cell initCell = initialGrid.getCell(cell.getX(), cell.getY());
                if (initCell.hasPotentialValue(value) && !cell.hasPotentialValue(value)) {
                    result.add(new Potential(cell, value, false));
                }
            }
        }
        return result;
    }

    public String getClueHtml(boolean isBig) {
        if (isBig) {
            return "Look for a " + getName() +
            " on the value <b>" + value + "<b>";
        } else {
            return "Look for a " + getName();
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getName());
        builder.append(": ");
        builder.append(Cell.toFullString(this.cells));
        builder.append(": ");
        builder.append(value);
        builder.append(" in ");
        builder.append(regions[0].toString());
        return builder.toString();
    }

    public String toString2() {
        StringBuilder builder = new StringBuilder();
        builder.append(getName());
        builder.append(": ");
        builder.append(Cell.toFullString(this.cells));
        builder.append(": ");
        builder.append(value);
        builder.append(" in ");
        builder.append(regions[0].toString());
        return builder.toString();
    }

    @Override
    public String toHtml() {
        String result = HtmlLoader.loadHtml(this, "LockingGHint.html");
        String ruleName = getName();
        String valueName = Integer.toString(value);
        String firstRegion = regions[0].toString();
        return HtmlLoader.format(result, ruleName, valueName, firstRegion);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LockingGHint))
            return false;
        LockingGHint other = (LockingGHint)o;
        if (this.value != other.value)
            return false;
        if (this.cells.length != other.cells.length)
            return false;
        return Arrays.asList(this.cells).containsAll(Arrays.asList(other.cells));
    }

    @Override
    public int hashCode() {
        int result = 0;
        for (Cell cell : cells)
            result ^= cell.hashCode();
        result ^= value;
        return result;
    }

}
