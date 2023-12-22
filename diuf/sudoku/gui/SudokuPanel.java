/*
 * Project: Sudoku Explainer
 * Copyright (C) 2006-2007 Nicolas Juillerat
 * Available under the terms of the Lesser General Public License (LGPL)
 */
package diuf.sudoku.gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import javax.swing.*;

import diuf.sudoku.*;

/**
 * The panel representing the sudoku grid. Includes the legend.
 * <p>
 * All user actions are redirected to the {@link diuf.sudoku.gui.SudokuExplainer}
 * class.
 * @see diuf.sudoku.gui.SudokuFrame
 * @see diuf.sudoku.gui.SudokuExplainer
 */
public class SudokuPanel extends JPanel {

    private static final long serialVersionUID = 3709127163156966626L;

    private int CELL_OUTER_SIZE = 45;
    private int CELL_INNER_SIZE = 39;
    private int GRID_GAP_SIZE = 2;
    private int LEGEND_GAP_SIZE = 42;
    private int CELL_PAD = (CELL_OUTER_SIZE - CELL_INNER_SIZE) / 2;
    private int GRID_SIZE = CELL_OUTER_SIZE * 9;
    private String FONT_NAME = Settings.getInstance().getFontName();
    private int FONT_SIZE_SMALL = 12;
    private int FONT_SIZE_BIG = 36;
    private int FONT_SIZE_LEGEND = 24;

    private Grid grid;
    private Cell focusedCell = null;
    private Cell selectedCell = null;
    private int focusedCandidate = 0;
    private Map<Cell,BitSet> redPotentials;
    private Map<Cell,BitSet> greenPotentials;
    private Map<Cell,BitSet> bluePotentials;
    private Collection<Cell> greenCells;
    private Collection<Cell> redCells;
    private Grid.Region[] blueRegions;
    private Collection<Link> links;

    private SudokuFrame parent;
    private SudokuExplainer engine;

    private Font smallFont;
    private Font bigFont;
    private Font legendFont;

    private Color backgroundColor = new Color(248, 248, 248, 127);      // background
    private Color altBackgroundColor = new Color(164, 164, 164, 127);   // background

    private Color candidateMaskColor = new Color(0, 255, 255, 127);     // candidate mask
    private Color potentialMaskColor = new Color(0, 255, 0, 127);       // potential mask

    private Color DG1 =  new Color(208, 238, 227);
    private Color DG2 =  new Color(237, 222, 194);
    private Color DG3 =  new Color(227, 221, 232);
    private Color DG4 =  new Color(209, 211, 237);
    private Color DG5 =  new Color(241, 241, 201);
    private Color DG6 =  new Color(242, 216, 210);
    private Color DG7 =  new Color(242, 210, 237);
    private Color DG8 =  new Color(208, 232, 238);
    private Color DG9 =  new Color(210, 240, 202);
    private Color[] DG_Colors = {DG1, DG2, DG3, DG4, DG5, DG6, DG7, DG8, DG9};
    private Color customColor = new Color(244, 138, 138);   // h:0   s:200 l:180
    private Color Pastel01 = new Color( 119, 221, 119);     // Pastel Green            #77dd77
    private Color Pastel02 = new Color( 137, 207, 240);     // Baby Blue               #89cff0
    private Color Pastel03 = new Color( 153, 197, 196);     // Pastel Turquoise        #99c5c4
    private Color Pastel04 = new Color( 154, 222, 219);     // Blue Green Pastel       #9adedb
    private Color Pastel05 = new Color( 170, 148, 153);     // Persian Pastel          #aa9499
    private Color Pastel06 = new Color( 170, 240, 209);     // Magic Mint              #aaf0d1
    private Color Pastel07 = new Color( 178, 251, 165);     // Light Pastel Green      #b2fba5
    private Color Pastel08 = new Color( 179, 158, 181);     // Pastel Purple           #b39eb5
    private Color Pastel09 = new Color( 189, 176, 208);     // Pastel Lilac            #bdb0d0
    private Color Pastel10 = new Color( 190, 231, 165);     // Pastel Pea              #bee7a5
    private Color Pastel11 = new Color( 190, 253, 115);     // Light Lime              #befd73
    private Color Pastel12 = new Color( 193, 198, 252);     // Light Periwinkle        #c1c6fc
    private Color Pastel13 = new Color( 198, 164, 164);     // Pale Mauve              #c6a4a4
    private Color Pastel14 = new Color( 200, 255, 176);     // Light Light Green       #c8ffb0
    private Color Pastel15 = new Color( 203, 153, 201);     // Pastel Violet           #cb99c9
    private Color Pastel16 = new Color( 206, 240, 204);     // Pastel Mint             #cef0cc
    private Color Pastel17 = new Color( 207, 207, 196);     // Pastel Grey             #cfcfc4
    private Color Pastel18 = new Color( 214, 255, 254);     // Pale Blue               #d6fffe
    private Color Pastel19 = new Color( 216, 161, 196);     // Pastel Lavender         #d8a1c4
    private Color Pastel20 = new Color( 222, 165, 164);     // Pastel Pink             #dea5a4
    private Color Pastel21 = new Color( 222, 236, 225);     // Pastel Smirk            #deece1
    private Color Pastel22 = new Color( 223, 216, 225);     // Pastel Day              #dfd8e1
    private Color Pastel23 = new Color( 229, 217, 211);     // Pastel Parchment        #e5d9d3
    private Color Pastel24 = new Color( 233, 209, 191);     // Pastel Rose Tan         #e9d1bf
    private Color Pastel25 = new Color( 244, 154, 194);     // Pastel Magenta          #f49ac2
    private Color Pastel26 = new Color( 244, 191, 255);     // Electric Lavender       #f4bfff
    private Color Pastel27 = new Color( 253, 253, 150);     // Pastel Yellow           #fdfd96
    private Color Pastel28 = new Color( 255, 105,  97);     // Pastel Red              #ff6961
    private Color Pastel29 = new Color( 255, 150,  79);     // Pastel Orange           #ff964f
    private Color Pastel30 = new Color( 255, 152, 153);     // American Pink           #ff9899
    private Color Pastel31 = new Color( 255, 183, 206);     // Baby Pink               #ffb7ce
    private Color Pastel32 = new Color( 202, 155, 247);     // Baby Purple             #ca9bf7
    private Color Pastel33 = new Color( 131, 105,  83);     // Pastel Brown            #836953
    private Color[] Pastel_Colors = { Pastel01, Pastel02, Pastel03, Pastel04, Pastel05, Pastel06, Pastel07, Pastel08, Pastel09, Pastel10, Pastel11, Pastel12, Pastel13, Pastel14, Pastel15, Pastel16, Pastel17, Pastel18, Pastel19, Pastel20, Pastel21, Pastel22, Pastel23, Pastel24, Pastel25, Pastel26, Pastel27, Pastel28, Pastel29, Pastel30, Pastel31, Pastel32, Pastel33};
    private int[] Pastel_Index = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32};

    private String DOTA = "."+Settings.getInstance().getsA();

    public SudokuPanel(SudokuFrame parent) {
        super();
        this.parent = parent;
        if (GRID_SIZE > 800 && getToolkit().getScreenSize().height <= 1080)
            rescale();
        initialize();
        super.setOpaque(false);
        smallFont = new Font(FONT_NAME, Font.BOLD, FONT_SIZE_SMALL);
        bigFont = new Font(FONT_NAME, Font.BOLD, FONT_SIZE_BIG);
        legendFont = new Font(FONT_NAME, Font.BOLD, FONT_SIZE_LEGEND);
        newColors(0);
    }

    public void newColors(int paint) {
        Random random = new Random( System.currentTimeMillis());
        for (int i = 0; i < 33; i++) {
            int p1 = random.nextInt(32);
            int p2 = random.nextInt(32);
            int temp = Pastel_Index[p1];
            Pastel_Index[p1] = Pastel_Index[p2];
            Pastel_Index[p2] = temp;
        }
        if ( paint == 1 ) {
            repaint();
        }
    }

    private void rescale() {
        int scale = (GRID_SIZE/100+1)*100;
        CELL_OUTER_SIZE = rescale(CELL_OUTER_SIZE,scale);
        CELL_INNER_SIZE = rescale(CELL_INNER_SIZE,scale);
    //  GRID_GAP_SIZE = rescale(GRID_GAP_SIZE,scale);
        LEGEND_GAP_SIZE = rescale(LEGEND_GAP_SIZE,scale);
        CELL_PAD = (CELL_OUTER_SIZE - CELL_INNER_SIZE) / 2;
        GRID_SIZE = CELL_OUTER_SIZE * 9;
    //  FONT_SIZE_SMALL = rescale(FONT_SIZE_SMALL,scale);
    //  FONT_SIZE_BIG = rescale(FONT_SIZE_BIG,scale);
    //  FONT_SIZE_LEGEND = rescale(FONT_SIZE_LEGEND,scale);
    }

    private int rescale(int value, int scale) {
        return value * 800 / scale;
    }

    private void initialize() {
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                setFocusedCell(null);
            }
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                // Workaround mouseClicked won't be fired while moving mouse
                // http://stackoverflow.com/questions/3382330/mouselistener-for-jpanel-missing-mouseclicked-events
                if (!SudokuPanel.this.contains(e.getPoint()))
                    return;
                Cell target = getCellAt(e.getX(), e.getY());
                if (target == selectedCell && target != null) {
                    int value = getCandidateAt(e.getX(), e.getY());
                    if (e.getButton() == MouseEvent.BUTTON2 || e.getButton() == MouseEvent.BUTTON3
                            || (e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0) {
                        if (value != 0) { // Check that the cell is empty
                            // Toggle a candidate
                            engine.candidateMouTyped(target, value);
                            repaintCell(target);
                        }
                    } else {
                        if (target.getValue() == 0) { // if unsolved cell and
                          if ( target.hasPotentialValue( value) ) { // has potential candidate
                            // Set the cell's value
                            engine.cellValueMouTyped(target, value);
                            repaint();
                          }
                        } else {
                            // Clear the cell's value
                            engine.cellValueMouTyped(target, 0);
                            repaint();
                        }
                    }
                } else {
                    setFocusedCandidate(0);
                    setSelectedCell(target);
                    if (e.getButton() == MouseEvent.BUTTON2 || e.getButton() == MouseEvent.BUTTON3
                            || (e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0)
                        setFocusedCandidate(getCandidateAt(e.getX(), e.getY()));
                }
                SudokuPanel.super.requestFocusInWindow();
            }
        });

        this.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                setFocusedCell(getCellAt(e.getX(), e.getY()));
                if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0)
                    setFocusedCandidate(getCandidateAt(e.getX(), e.getY()));
                else
                    setFocusedCandidate(0);
            }
        });
        this.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                int code = e.getKeyCode();
                if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_RIGHT ||
                        code == KeyEvent.VK_UP || code == KeyEvent.VK_DOWN) {
                    setFocusedCell(null);
                    int x = 4;
                    int y = 4;
                    if (selectedCell != null) {
                        x = selectedCell.getX();
                        y = selectedCell.getY();
                    }
                    if (code == KeyEvent.VK_LEFT)
                        x = (x + 8) % 9;
                    else if (code == KeyEvent.VK_RIGHT)
                        x = (x + 1) % 9;
                    else if (code == KeyEvent.VK_UP)
                        y = (y + 8) % 9;
                    else if (code == KeyEvent.VK_DOWN)
                        y = (y + 1) % 9;
                    setFocusedCandidate(0);
                    if (selectedCell == null) {
                        // Select the central cell
                        setSelectedCell(grid.getCell(4, 4));
                        x = 4;
                        y = 4;
                    } else {
                        setSelectedCell(grid.getCell(x, y));
                    }
                } else if (code == KeyEvent.VK_DELETE || code == KeyEvent.VK_BACK_SPACE) {
                    if (selectedCell != null) {
                        engine.cellValueKbdTyped(selectedCell, 0);
                        repaint();
                    }
                } else if (code >= KeyEvent.VK_1 && code <= KeyEvent.VK_9) {
                    if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0) {
                        int value = (code - KeyEvent.VK_0);
                        if (selectedCell != null) {
                            engine.candidateKbdTyped(selectedCell, value);
                            repaintCell(selectedCell);
                        }
                    }
                } else if (code == KeyEvent.VK_ESCAPE) {
                    setSelectedCell(null);
                    engine.clearHints();
                    repaint();
                }
            }
            @Override
            public void keyTyped(java.awt.event.KeyEvent e) {
                boolean isProcessed = false;
                if (selectedCell != null) {
                    char ch = e.getKeyChar();
                    if (ch >= '1' && ch <= '9') {
                        int value = ch - '0';
                        engine.cellValueKbdTyped(selectedCell, value);
                        repaint();
                        isProcessed = true;
                    } else if (ch >= 'A' && ch <= 'I') {
                        int value = ch - 'A'+1;
                        engine.cellValueKbdTyped(selectedCell, value);
                        repaint();
                        isProcessed = true;
                    } else if (ch >= 'a' && ch <= 'i') {
                        int value = ch - 'a'+1;
                        engine.cellValueKbdTyped(selectedCell, value);
                        repaint();
                        isProcessed = true;
                    } else if (ch == ' ' || ch == '0') {
                        engine.cellValueKbdTyped(selectedCell, 0);
                        selectedCell.setValue(0);
                        repaint();
                        isProcessed = true;
                    } else if (ch == '\r' || ch == '\n') {
                        setSelectedCell(null);
                        parent.getBtnApplyHintAndGet().requestFocusInWindow();
                        repaint();
                        isProcessed = true;
                    }
                }
                if (!isProcessed && e.getComponent() != SudokuPanel.this.parent) {
                    e.setSource(SudokuPanel.this.parent);
                    dispatchEvent(e);
                }
            }
        });
    }

    void setEngine(SudokuExplainer explainer) {
        this.engine = explainer;
    }

    private Cell getCellAt(int x, int y) {
        int cx = (x - LEGEND_GAP_SIZE) / CELL_OUTER_SIZE;
        int cy = (y - GRID_GAP_SIZE) / CELL_OUTER_SIZE;
        if (x < LEGEND_GAP_SIZE || cx < 0 || cx >= 9 || cy < 0 || cy >= 9)
            return null;
        return grid.getCell(cx, cy);
    }

    private int getCandidateAt(int x, int y) {
        // Get cell's corner
        int cx = (x - LEGEND_GAP_SIZE) / CELL_OUTER_SIZE;
        int cy = (y - GRID_GAP_SIZE) / CELL_OUTER_SIZE;
        if (x < LEGEND_GAP_SIZE || cx < 0 || cx >= 9 || cy < 0 || cy >= 9)
            return 0;
        Cell cell = grid.getCell(cx, cy);
        if (!cell.equals(this.selectedCell))
            return 0;
        if (this.selectedCell != null && this.selectedCell.getValue() != 0)
            return 0;
        // Substract cell's corner
        x = x - cx * CELL_OUTER_SIZE - LEGEND_GAP_SIZE;
        y = y - cy * CELL_OUTER_SIZE - GRID_GAP_SIZE;
        // Get candidate
        int px = (x - CELL_PAD) / (CELL_INNER_SIZE / 3);
        int py = (y - CELL_PAD) / (CELL_INNER_SIZE / 3);
        if (px < 0 || px >= 3 || py < 0 || py >= 3)
            return 0;
        return py * 3 + px + 1;
    }

    public Grid getSudokuGrid() {
        return grid;
    }

    public void setSudokuGrid(Grid sudokuGrid) {
        this.grid = sudokuGrid;
        this.selectedCell = sudokuGrid.getCell(4, 4);
    }

    public Collection<Cell> getGreenCells() {
        return greenCells;
    }

    public void setGreenCells(Collection<Cell> greenCells) {
        this.greenCells = greenCells;
    }

    public Collection<Cell> getRedCells() {
        return redCells;
    }

    public void setRedCells(Collection<Cell> redCells) {
        this.redCells = redCells;
    }

    public Map<Cell, BitSet> getGreenPotentials() {
        return greenPotentials;
    }

    public void setGreenPotentials(Map<Cell, BitSet> greenPotentials) {
        this.greenPotentials = greenPotentials;
    }

    public Map<Cell, BitSet> getRedPotentials() {
        return redPotentials;
    }

    public void setRedPotentials(Map<Cell, BitSet> redPotentials) {
        this.redPotentials = redPotentials;
    }

    public Map<Cell, BitSet> getBluePotentials() {
        return bluePotentials;
    }

    public void setBluePotentials(Map<Cell, BitSet> bluePotentials) {
        this.bluePotentials = bluePotentials;
    }

    public void setBlueRegions(Grid.Region... regions) {
        this.blueRegions = regions;
    }

    public void setLinks(Collection<Link> links) {
        this.links = links;
    }

    public void clearSelection() {
        this.focusedCandidate = 0;
        this.selectedCell = null;
        this.focusedCell = null;
    }

    public void clearFocus() {
        this.focusedCandidate = 0;
        this.focusedCell = null;
    }

    private void repaintCell(Cell cell) {
        if (cell == null)
            return;
        repaint(cell.getX() * CELL_OUTER_SIZE + LEGEND_GAP_SIZE,
                cell.getY() * CELL_OUTER_SIZE + GRID_GAP_SIZE,
                CELL_OUTER_SIZE, CELL_OUTER_SIZE);
    }

    private void setFocusedCell(Cell cell) {
        repaintCell(this.focusedCell);
        this.focusedCell = cell;
        repaintCell(this.focusedCell);
    }

    private void setSelectedCell(Cell cell) {
        repaintCell(this.selectedCell);
        this.selectedCell = cell;
        boolean showing = Settings.getInstance().isShowingCandidateMasks();
        if ( showing == false ) {
            repaintCell(this.selectedCell);
        }
        else {
            repaint();
        }
    }

    private void setFocusedCandidate(int value) {
        if (this.selectedCell == null)
            return;
        this.focusedCandidate = value;
        repaintCell(this.selectedCell);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(GRID_SIZE + LEGEND_GAP_SIZE + GRID_GAP_SIZE,
                GRID_SIZE + LEGEND_GAP_SIZE + GRID_GAP_SIZE);
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    private void drawStringCentered(Graphics g, String s, int x, int y) {
        Rectangle2D rect = g.getFontMetrics().getStringBounds(s, g);
        double px = x - rect.getWidth() / 2;
        double py = y - rect.getHeight() / 2 - rect.getY();
        g.drawString(s, (int)(px + 0.5), (int)(py + 0.5));
    }

    private void drawStringCentered3D(Graphics g, String s, int x, int y) {
        Color color = g.getColor();
        g.setColor(Color.black);
        drawStringCentered(g, s, x, y + 1);
        g.setColor(Color.yellow);
        drawStringCentered(g, s, x - 1, y);
        g.setColor(color);
        drawStringCentered(g, s, x, y);
    }

    private boolean initPotentialColor(Graphics g, Cell cell, int value) {
        boolean isHighlighted = false;
        boolean isRed = false;
        Color col = Color.gray;
        if (bluePotentials != null) {
            BitSet blueValues = bluePotentials.get(cell);
            if (blueValues != null && blueValues.get(value)) {
                col = Color.blue;
                isHighlighted = true;
            }
        }
        if (redPotentials != null) {
            BitSet redValues = redPotentials.get(cell);
            if (redValues != null && redValues.get(value)) {
                col = Color.red;
                isHighlighted = true;
                isRed = true;
            }
        }
        if (greenPotentials != null) {
            BitSet greenValues = greenPotentials.get(cell);
            if (greenValues != null && greenValues.get(value)) {
                if (isRed) {
                    col = new Color(224, 128, 0);
                } else {
                    col = new Color(0, 224, 0);
                    isHighlighted = true;
                }
            }
        }
        if (cell == selectedCell)
            col = new Color(
                    (col.getRed() + Color.orange.getRed()) / 2,
                    (col.getGreen() + Color.orange.getGreen()) / 2,
                    (col.getBlue() + Color.orange.getBlue()) / 2);
        if (cell == selectedCell && value == focusedCandidate)
            col = Color.black;
        g.setColor(col);
        return isHighlighted;
    }

    private boolean init2PotentialColor(Graphics g, Cell cell, int value) {
        boolean isHighlighted = false;
        boolean isRed = false;
        Color col = Color.gray;
        if (bluePotentials != null) {
            BitSet blueValues = bluePotentials.get(cell);
            if (blueValues != null && blueValues.get(value)) {
                col = Color.blue;
                isHighlighted = true;
            }
        }
        if (redPotentials != null) {
            BitSet redValues = redPotentials.get(cell);
            if (redValues != null && redValues.get(value)) {
                col = Color.red;
                isHighlighted = true;
                isRed = true;
            }
        }
        if (greenPotentials != null) {
            BitSet greenValues = greenPotentials.get(cell);
            if (greenValues != null && greenValues.get(value)) {
                if (isRed) {
                    col = new Color(224, 128, 0);
                } else {
                    col = new Color(0, 224, 0);
                    isHighlighted = true;
                }
            }
        }
    //  if (cell == selectedCell)
    //      col = new Color(
    //              (col.getRed() + Color.orange.getRed()) / 2,
    //              (col.getGreen() + Color.orange.getGreen()) / 2,
    //              (col.getBlue() + Color.orange.getBlue()) / 2);
    //  if (cell == selectedCell && value == focusedCandidate)
    //      col = Color.black;
        g.setColor(col);
        return isHighlighted;
    }

    private void initFillColor(Graphics g, Cell cell) {
        Color col = Color.white;
        if ( grid.isDisjointGroups()) {
            Grid.DisjointGroup disjointgroup = grid.getDisjointGroupAt(cell.getX(),cell.getY());
            int dgi = disjointgroup.getDisjointGroupNum();
            col = Pastel_Colors[ Pastel_Index[ dgi+1]];
        }
        if (!grid.isDisjointGroups() && grid.isWindoku() ) {
            Grid.Windoku windoku = grid.getWindokuAt(cell.getX(),cell.getY());
            int wdi = windoku.getWindokuNum();
            if ( wdi < (3-1) * (3-1) ) {
            //  col = altBackgroundColor;
                col = Pastel_Colors[ Pastel_Index[ 0]];
            }
        }
        if (grid.isCustom() && grid.getCustomAt(cell.getX(),cell.getY())!=null) {
          if ( Settings.getInstance().getCustomConnect() == 1 ) {
            Grid.Custom custom = grid.getCustomAt(cell.getX(),cell.getY());
            col = Pastel_Colors[ Pastel_Index[ custom.getCustomNum()+1]];
          }
          if ( Settings.getInstance().getCustomConnect() == 0 ) {
            col = Pastel_Colors[ Pastel_Index[ 1]];
          }
        }
        if (redCells != null && redCells.contains(cell))
            col = Color.red;
        else if (greenCells != null && greenCells.contains(cell))
            col = new Color(192, 255, 255);
        else if (cell == selectedCell)
            col = Color.orange;
        else if (cell == focusedCell)
            col = Color.yellow;
        else {
            boolean showing = Settings.getInstance().isShowingCandidateMasks();
            if ( showing == true ) {
                // Selected candidates color
                int value = -1;
                if (null != selectedCell) {
                    value = selectedCell.getValue();
                }
                if ( value > 0 ) {
                    if ( value == cell.getValue()) {
                        col = candidateMaskColor;
                    }
                    else
                    if ( cell.hasPotentialValue(value)) {
                        col = potentialMaskColor;
                    }
                }
            }
        }
        g.setColor(col);
    }

    private void init2FillColor(Graphics g, Cell cell) {
        Color col = Color.white;
        if ( grid.isDisjointGroups()) {
            Grid.DisjointGroup disjointgroup = grid.getDisjointGroupAt(cell.getX(),cell.getY());
            int dgi = disjointgroup.getDisjointGroupNum();
            col = Pastel_Colors[ Pastel_Index[ dgi+1]];
        }
        if (!grid.isDisjointGroups() && grid.isWindoku() ) {
            Grid.Windoku windoku = grid.getWindokuAt(cell.getX(),cell.getY());
            int wdi = windoku.getWindokuNum();
            if ( wdi < (3-1) * (3-1) ) {
            //  col = altBackgroundColor;
                col = Pastel_Colors[ Pastel_Index[ 0]];
            }
        }
        if (grid.isCustom() && grid.getCustomAt(cell.getX(),cell.getY())!=null) {
          if ( Settings.getInstance().getCustomConnect() == 1 ) {
            Grid.Custom custom = grid.getCustomAt(cell.getX(),cell.getY());
            col = Pastel_Colors[ Pastel_Index[ custom.getCustomNum()+1]];
          }
          if ( Settings.getInstance().getCustomConnect() == 0 ) {
            col = Pastel_Colors[ Pastel_Index[ 1]];
          }
        }
        if (redCells != null && redCells.contains(cell))
            col = Color.red;
        else if (greenCells != null && greenCells.contains(cell))
            col = new Color(192, 255, 255);
    //  else if (cell == selectedCell)
    //      col = Color.orange;
    //  else if (cell == focusedCell)
    //      col = Color.yellow;
        else {
            boolean showing = Settings.getInstance().isShowingCandidateMasks();
            if ( showing == true ) {
                // Selected candidates color
                int value = -1;
                if (null != selectedCell) {
                    value = selectedCell.getValue();
                }
                if ( value > 0 ) {
                    if ( value == cell.getValue()) {
                        col = candidateMaskColor;
                    }
                    else
                    if ( cell.hasPotentialValue(value)) {
                        col = potentialMaskColor;
                    }
                }
            }
        }
        g.setColor(col);
    }

    private void initValueColor(Graphics g, Cell cell) {
        Color col = cell.isGiven() ? Color.black : Color.blue;
    //  if (cell == selectedCell)
    //      col = new Color(
    //              (col.getRed() + Color.orange.getRed()) / 2,
    //              (col.getGreen() + Color.orange.getGreen()) / 2,
    //              (col.getBlue() + Color.orange.getBlue()) / 2);
        g.setColor(col);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        initGraphics(g2);
        paintLegend(g);
        AffineTransform oldTransform = g2.getTransform();
        AffineTransform translate =
            AffineTransform.getTranslateInstance(LEGEND_GAP_SIZE, GRID_GAP_SIZE);
        g2.transform(translate);
        g.clearRect(0, 0, GRID_SIZE, GRID_SIZE);
        paintSelectionAndFocus(g);
        paintGrid(g, 0);
        paintHighlightedRegions(g, 0);
        paintCellsValues(g);
        paintLinks(g, 0);
        paintCellsPotentials(g);
        paintCursor();
        g2.setTransform(oldTransform);
    }

    public void saveAsImage(File file) {
      try {
        BufferedImage bi = new BufferedImage(GRID_SIZE+GRID_GAP_SIZE*2, GRID_SIZE+GRID_GAP_SIZE*2, BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.createGraphics();
        Graphics2D g2 = (Graphics2D)g;
        initGraphics(g2);
        g.setColor(Color.white);
        g.fillRect(0, 0, GRID_SIZE+GRID_GAP_SIZE*2, GRID_SIZE+GRID_GAP_SIZE*2);
        paint2SelectionAndFocus(g, GRID_GAP_SIZE);
        paintGrid(g, GRID_GAP_SIZE);
        paintHighlightedRegions(g, GRID_GAP_SIZE);
        paint2CellsValues(g, GRID_GAP_SIZE);
        paintLinks(g, GRID_GAP_SIZE);
        paint2CellsPotentials(g, GRID_GAP_SIZE);
        ImageIO.write(bi, "PNG", file);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    /**
     * Set the given rectangle to match the rectangle occupied by a cell.
     * Only valid if the grpahics context is translated so that the
     * origin matches the upper left corner of the grid.
     * @param x the horizontal coordinate of the cell within the grid (0..8)
     * @param y the vertical coordinate of the cell within the grid (0..8)
     * @param target set to the rectangle occupied by the cell, in pixels
     */
    private void readCellRectangle(int x, int y, Rectangle target) {
        target.x = x * CELL_OUTER_SIZE;
        target.y = y * CELL_OUTER_SIZE;
        target.width = CELL_OUTER_SIZE;
        target.height = CELL_OUTER_SIZE;
    }

    private void initGraphics(Graphics2D g2) {
        if (Settings.getInstance().isAntialiasing()) {
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        } else {
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
            g2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
            g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
            g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
            g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
        }
    }

    private void paintLegend(Graphics g) {
        g.setFont(legendFont);
        g.setColor(Color.gray); // R=128, G=128, B=128
        Settings settings = Settings.getInstance();
        for (int i = 0; i < 9; i++) {
            String xLegend;
            if (settings.isRCNotation())
                xLegend = "C" + (i + 1);
            else
                xLegend = Character.toString((char)('A' + i));
            String yLegend;
            if (settings.isRCNotation())
                yLegend = "R" + (i + 1);
            else
                yLegend = Integer.toString(i + 1);
          if (engine.isValueAllSolved(grid, i+1)) {
            g.setColor(Color.lightGray); // R=192, G=192, B=192
            drawStringCentered(g, yLegend,
                    LEGEND_GAP_SIZE / 2, CELL_OUTER_SIZE * i + GRID_GAP_SIZE + CELL_OUTER_SIZE / 2);
            g.setColor(Color.darkGray); // R=64, G=64, B=64
            drawStringCentered(g, yLegend,
                    LEGEND_GAP_SIZE/2+1, CELL_OUTER_SIZE * i + GRID_GAP_SIZE + CELL_OUTER_SIZE/2+1);
          } else {
            g.setColor(Color.gray); // R=128, G=128, B=128
            drawStringCentered(g, yLegend,
                    LEGEND_GAP_SIZE / 2, CELL_OUTER_SIZE * i + GRID_GAP_SIZE + CELL_OUTER_SIZE / 2);
          }
            g.setColor(Color.gray); // R=128, G=128, B=128
            drawStringCentered(g, xLegend,
                    LEGEND_GAP_SIZE + i * CELL_OUTER_SIZE + CELL_OUTER_SIZE / 2,
                    CELL_OUTER_SIZE * 9 + GRID_GAP_SIZE + LEGEND_GAP_SIZE / 2);
        }
    }

    private void paintSelectionAndFocus(Graphics g) {
        Rectangle clip = g.getClipBounds();
        Rectangle cellRect = new Rectangle();
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                readCellRectangle(x, y, cellRect);
                if (clip.intersects(cellRect)) {
                    Cell cell = grid.getCell(x, y);
                    initFillColor(g, cell);
                    g.fillRect(x * CELL_OUTER_SIZE, y * CELL_OUTER_SIZE, CELL_OUTER_SIZE, CELL_OUTER_SIZE);
                }
            }
        }
    }

    private void paint2SelectionAndFocus(Graphics g, int adj) {
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                    Cell cell = grid.getCell(x, y);
                    init2FillColor(g, cell);
                    g.fillRect(x * CELL_OUTER_SIZE+adj, y * CELL_OUTER_SIZE+adj, CELL_OUTER_SIZE, CELL_OUTER_SIZE);
            }
        }
    }

    private void paintGrid(Graphics g, int adj) {
            int lineWidth, offset;
        for (int i = 0; i <= 9; i++) {
            if (i % 9 == 0 || ( grid.isRC33() && i % 3 == 0 && !grid.isLatinSquare())
                           || (!grid.isRC33() && i % 3 == 0 && !grid.isLatinSquare())) {
                lineWidth = 4;
                g.setColor(Color.black);
            } else {
                lineWidth = 2;
                g.setColor(Color.blue.darker());
            offset = lineWidth / 2;
            // vertical lines
            g.fillRect(i * CELL_OUTER_SIZE - offset+adj, 0 - offset+adj, lineWidth, GRID_SIZE + lineWidth);
            }
            if (i % 9 == 0 || ( grid.isRC33() && i % 3 == 0 && !grid.isLatinSquare())
                           || (!grid.isRC33() && i % 3 == 0 && !grid.isLatinSquare())) {
                lineWidth = 4;
                g.setColor(Color.black);
            } else {
                lineWidth = 2;
                g.setColor(Color.blue.darker());
            offset = lineWidth / 2;
            // horizontal lines
            g.fillRect(0 - offset+adj, i * CELL_OUTER_SIZE - offset+adj, GRID_SIZE + lineWidth, lineWidth);
            }
        }
        for (int i = 0; i <= 9; i++) {
            if (i % 9 == 0 || ( grid.isRC33() && i % 3 == 0 && !grid.isLatinSquare())
                           || (!grid.isRC33() && i % 3 == 0 && !grid.isLatinSquare())) {
                lineWidth = 4;
                g.setColor(Color.black);
            offset = lineWidth / 2;
            // vertical lines
            g.fillRect(i * CELL_OUTER_SIZE - offset+adj, 0 - offset+adj, lineWidth, GRID_SIZE + lineWidth);
            } else {
                lineWidth = 2;
                g.setColor(Color.blue.darker());
            }
            if (i % 9 == 0 || ( grid.isRC33() && i % 3 == 0 && !grid.isLatinSquare())
                           || (!grid.isRC33() && i % 3 == 0 && !grid.isLatinSquare())) {
                lineWidth = 4;
                g.setColor(Color.black);
            offset = lineWidth / 2;
            // horizontal lines
            g.fillRect(0 - offset+adj, i * CELL_OUTER_SIZE - offset+adj, GRID_SIZE + lineWidth, lineWidth);
            } else {
                lineWidth = 2;
                g.setColor(Color.blue.darker());
            }
        }
        if (grid.isDiagonals()) {
            g.setColor(Color.black);
          if (grid.isXAntiDiagonal()) {
            g.drawLine( CELL_OUTER_SIZE / 6+adj, CELL_OUTER_SIZE / 6+adj, 9 * CELL_OUTER_SIZE - CELL_OUTER_SIZE / 6+adj, 9 * CELL_OUTER_SIZE - CELL_OUTER_SIZE / 6+adj); }
          if (grid.isXDiagonal()) {
            g.drawLine( CELL_OUTER_SIZE / 6+adj, 9 * CELL_OUTER_SIZE - CELL_OUTER_SIZE / 6+adj, 9 * CELL_OUTER_SIZE - CELL_OUTER_SIZE / 6+adj, CELL_OUTER_SIZE / 6+adj); }
        }
        if (grid.isWindoku()) {
            g.setColor(Color.black);
                lineWidth = 4;
                offset = lineWidth / 2;
            for (int i = 0; i < (3-1) * (3-1); i++) {
                Grid.Windoku wd = grid.getWindoku(i);
                Cell cl = wd.getCell(0);
                int cx = cl.getX();
                int cy = cl.getY();
                // verticals
                g.fillRect( cx * CELL_OUTER_SIZE - offset+adj, cy * CELL_OUTER_SIZE - offset+adj, lineWidth, GRID_SIZE / 3 + lineWidth);
                g.fillRect( ( cx + 3) * CELL_OUTER_SIZE - offset+adj, cy * CELL_OUTER_SIZE - offset+adj, lineWidth, GRID_SIZE / 3 + lineWidth);
                // horizontals
                g.fillRect( cx * CELL_OUTER_SIZE - offset+adj, cy * CELL_OUTER_SIZE - offset+adj, GRID_SIZE / 3 + lineWidth, lineWidth);
                g.fillRect( cx * CELL_OUTER_SIZE - offset+adj, ( cy + 3) * CELL_OUTER_SIZE - offset+adj, GRID_SIZE / 3 + lineWidth, lineWidth);
            }
        }
        if ( grid.isLatinSquare() && grid.isCustom() ) {
            g.setColor(Color.black);
            lineWidth = 4;
            offset = lineWidth / 2;
            // vertical lines
            for (int r = 0; r < 9; r++) {
                for (int c = 0; c < 8; c++ ) {
                    if ( grid.getCustomNumAt( c, r) != grid.getCustomNumAt( c+1, r) ) {
                        g.fillRect((c+1) * CELL_OUTER_SIZE - offset+adj, r * CELL_OUTER_SIZE - offset+adj, lineWidth, CELL_OUTER_SIZE + lineWidth);
                    }
                }
            }
            // horizontal lines
            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 9; c++ ) {
                    if ( grid.getCustomNumAt( c, r) != grid.getCustomNumAt( c, r+1) ) {
                        g.fillRect(c * CELL_OUTER_SIZE - offset+adj, (r+1) * CELL_OUTER_SIZE - offset+adj, CELL_OUTER_SIZE + lineWidth, lineWidth);
                    }
                }
            }
        }
    }

    private void paintHighlightedRegions(Graphics g, int adj) {
        if (blueRegions != null) {
            Color[] colors = new Color[] {new Color(0, 0, 192), new Color(0, 128, 0)};
            for (int rev = 0; rev < 2; rev++) {
                for (int i = 0; i < blueRegions.length; i++) {
                    int index = (rev == 0 ? i : blueRegions.length - 1 - i);
                    Grid.Region region = blueRegions[index];
                        int x, y, w, h; // coordinates, width, height (in cells)
                    if (region != null) {
                      if (region instanceof Grid.Row || region instanceof Grid.Column || region instanceof Grid.Block) {
                        if (region instanceof Grid.Row) {
                            Grid.Row row = (Grid.Row)region;
                            x = 0;
                            y = row.getRowNum();
                            w = 9;
                            h = 1;
                        } else if (region instanceof Grid.Column) {
                            Grid.Column column = (Grid.Column)region;
                            x = column.getColumnNum();
                            y = 0;
                            w = 1;
                            h = 9;
                        } else {
                            Grid.Block square = (Grid.Block)region;
                          if ( grid.isRC33() ) {
                            x = square.getHIndex() * 3;
                            y = square.getVIndex() * 3;
                            w = 3;
                            h = 3;
                          } else {
                            x = square.getHIndex() * 3;
                            y = square.getVIndex() * 3;
                            w = 3;
                            h = 3;
                          }
                        }
                        g.setColor(colors[index % 2]);
                        for (int s = -2 + rev; s <= 2; s+= 2) {
                            g.drawRect(x * CELL_OUTER_SIZE + s+adj, y * CELL_OUTER_SIZE + s+adj,
                                    w * CELL_OUTER_SIZE - s * 2, h * CELL_OUTER_SIZE - s * 2);
                        }
                        if (rev == 0) {
                            Color base = colors[index % 2];
                            g.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), 12));
                            g.fillRect(x * CELL_OUTER_SIZE + 3+adj, y * CELL_OUTER_SIZE + 3+adj,
                                    w * CELL_OUTER_SIZE - 6, h * CELL_OUTER_SIZE - 6);
                        }
                      } else
                      if (region instanceof Grid.Windoku) {
                        Grid.Windoku windoku = (Grid.Windoku)region;
                        int wdi = windoku.getWindokuNum();
                        int js = 0, jend = 1, jinc = 1;
                        w = windoku.getWindokuWCells();
                        h = windoku.getWindokuHCells();
                        if ( w != 1 && h == 1) { jend = 9; jinc = 3; }
                        if ( w == 1 && h != 1) { jend = 3; }
                        if ( w == 1 && h == 1) { jend = 9; }
                        for (int j = js; j < jend ; j+=jinc) {
                            Cell cell = windoku.getCell( j);
                            x = cell.getX();
                            y = cell.getY();
                            g.setColor(colors[index % 2]);
                            for (int s = -2 + rev; s <= 2; s+= 2) {
                                g.drawRect(x * CELL_OUTER_SIZE + s+adj, y * CELL_OUTER_SIZE + s+adj,
                                        w * CELL_OUTER_SIZE - s * 2, h * CELL_OUTER_SIZE - s * 2);
                            }
                            if (rev == 0) {
                                Color base = colors[index % 2];
                                g.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), 12));
                                g.fillRect(x * CELL_OUTER_SIZE + 3+adj, y * CELL_OUTER_SIZE + 3+adj,
                                        w * CELL_OUTER_SIZE - 6, h * CELL_OUTER_SIZE - 6);
                            }
                        }
                      } else
                      if (region instanceof Grid.Custom) {
                        Grid.Custom custom = (Grid.Custom)region;
                        int cti = custom.getCustomNum();
                        int js = 0, jend = 9, jinc = 1; w = 1; h = 1;
                        int lineWidth = 4;
                        int offset = lineWidth / 2;
                        for (int j = js; j < jend ; j+=jinc) {
                            Cell cell = custom.getCell( j);
                            x = cell.getX();
                            y = cell.getY();
                            g.setColor(colors[index % 2]);
                            if ( y==0 || (y!=0 && cti != custom.At(x,y-1)) )
                            {
                                g.fillRect(x * CELL_OUTER_SIZE - offset+adj, y * CELL_OUTER_SIZE - offset+adj, CELL_OUTER_SIZE + lineWidth, lineWidth);
                            }
                            if ( x==0 || (x!=0 && cti != custom.At(x-1,y)) )
                            {
                                g.fillRect(x * CELL_OUTER_SIZE - offset+adj, y * CELL_OUTER_SIZE - offset+adj, lineWidth, CELL_OUTER_SIZE + lineWidth);
                            }
                            if ( x==8 || (x!=8 && cti != custom.At(x+1,y)) )
                            {
                                g.fillRect((x+1) * CELL_OUTER_SIZE - offset+adj, y * CELL_OUTER_SIZE - offset+adj, lineWidth, CELL_OUTER_SIZE + lineWidth);
                            }
                            if ( y==8 || (y!=8 && cti != custom.At(x,y+1)) )
                            {
                                g.fillRect(x * CELL_OUTER_SIZE - offset+adj, (y+1) * CELL_OUTER_SIZE - offset+adj, CELL_OUTER_SIZE + lineWidth, lineWidth);
                            }
                            if (rev == 0) {
                                Color base = colors[index % 2];
                                g.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), 12));
                                g.fillRect(x * CELL_OUTER_SIZE + 3+adj, y * CELL_OUTER_SIZE + 3+adj,
                                        w * CELL_OUTER_SIZE - 6, h * CELL_OUTER_SIZE - 6);
                            }
                        }
                      } else {
                        for (int j = 0; j < 9 ; j++) {
                            Cell cell = region.getCell( j);
                            x = cell.getX();
                            y = cell.getY();
                            w = 1;
                            h = 1;
                            g.setColor(colors[index % 2]);
                          for (int s = -2 + rev; s <= 2; s+= 2) {
                            g.drawRect(x * CELL_OUTER_SIZE + s+adj, y * CELL_OUTER_SIZE + s+adj,
                                    w * CELL_OUTER_SIZE - s * 2, h * CELL_OUTER_SIZE - s * 2);
                          }
                          if (rev == 0) {
                            Color base = colors[index % 2];
                            g.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), 12));
                            g.fillRect(x * CELL_OUTER_SIZE + 3+adj, y * CELL_OUTER_SIZE + 3+adj,
                                    w * CELL_OUTER_SIZE - 6, h * CELL_OUTER_SIZE - 6);
                          }
                        }
                      }
                        index++;
                    }
                }
            }
        }
    }

    private void paintCellsPotentials(Graphics g) {
        Rectangle clip = g.getClipBounds();
        Rectangle cellRect = new Rectangle();
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                readCellRectangle(x, y, cellRect);
                if (clip.intersects(cellRect)) {
                    Cell cell = grid.getCell(x, y);
                    // Paint potentials
                    int index = 0;
                    g.setFont(smallFont);
                    for (int value = 1; value <= 9; value++) {
                        boolean paintIt = Settings.getInstance().isShowingCandidates();
                        if (cell == this.selectedCell && value == this.focusedCandidate) {
                            // Paint magenta selection
                            g.setColor(Color.magenta);
                            g.fillRect(
                                    x * CELL_OUTER_SIZE + CELL_PAD + (index % 3) * (CELL_INNER_SIZE / 3),
                                    y * CELL_OUTER_SIZE + CELL_PAD + (index / 3) * (CELL_INNER_SIZE / 3),
                                    CELL_INNER_SIZE / 3, CELL_INNER_SIZE / 3);
                            paintIt = true;
                        }
                        if (cell.hasPotentialValue(value)) {
                            int cx = x * CELL_OUTER_SIZE + CELL_PAD
                            + (index % 3) * (CELL_INNER_SIZE / 3) + CELL_INNER_SIZE / 6;
                            int cy = y * CELL_OUTER_SIZE + CELL_PAD
                            + (index / 3) * (CELL_INNER_SIZE / 3) + CELL_INNER_SIZE / 6;
                            boolean isHighlighted = initPotentialColor(g, cell, value);
                          if ( Settings.getInstance().isNumbers() ) {
                            if (isHighlighted)
                                drawStringCentered3D(g, "" + value, cx, cy);
                            else if (paintIt)
                                drawStringCentered(g, "" + value, cx, cy);
                          } else {
                            if (isHighlighted)
                                drawStringCentered3D(g, DOTA.substring(value,value+1), cx, cy);
                            else if (paintIt)
                                drawStringCentered(g, DOTA.substring(value,value+1), cx, cy);
                          }
                        }
                        index++;
                    }
                }
            }
        }
    }

    private void paint2CellsPotentials(Graphics g, int adj) {
        boolean paintIt = Settings.getInstance().isShowingCandidates();
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                    Cell cell = grid.getCell(x, y);
                    // Paint potentials
                    int index = 0;
                    g.setFont(smallFont);
                    for (int value = 1; value <= 9; value++) {
                        if (cell.hasPotentialValue(value)) {
                            int cx = x * CELL_OUTER_SIZE + CELL_PAD
                            + (index % 3) * (CELL_INNER_SIZE / 3) + CELL_INNER_SIZE / 6;
                            int cy = y * CELL_OUTER_SIZE + CELL_PAD
                            + (index / 3) * (CELL_INNER_SIZE / 3) + CELL_INNER_SIZE / 6;
                            boolean isHighlighted = init2PotentialColor(g, cell, value);
                          if ( Settings.getInstance().isNumbers() ) {
                            if (isHighlighted)
                                drawStringCentered3D(g, "" + value, cx+adj, cy+adj);
                            else if (paintIt)
                                drawStringCentered(g, "" + value, cx+adj, cy+adj);
                          } else {
                            if (isHighlighted)
                                drawStringCentered3D(g, DOTA.substring(value,value+1), cx+adj, cy+adj);
                            else if (paintIt)
                                drawStringCentered(g, DOTA.substring(value,value+1), cx+adj, cy+adj);
                          }
                        }
                        index++;
                    }
            }
        }
    }

    private void paintCellsValues(Graphics g) {
        Rectangle clip = g.getClipBounds();
        Rectangle cellRect = new Rectangle();
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                readCellRectangle(x, y, cellRect);
                if (clip.intersects(cellRect)) {
                    Cell cell = grid.getCell(x, y);
                    // Paint cell value
                    if (cell.getValue() != 0) {
                        g.setFont(bigFont);
                        int cx = x * CELL_OUTER_SIZE + CELL_PAD + CELL_INNER_SIZE / 2;
                        int cy = y * CELL_OUTER_SIZE + CELL_PAD + CELL_INNER_SIZE / 2;
                        initValueColor(g, cell);
                      if ( Settings.getInstance().isNumbers() ) {
                        drawStringCentered(g, "" + cell.getValue(), cx, cy);
                      } else {
                        int value = cell.getValue();
                        drawStringCentered(g, DOTA.substring(value,value+1), cx, cy);
                      }
                    }
                }
            }
        }
    }

    private void paint2CellsValues(Graphics g, int adj) {
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                    Cell cell = grid.getCell(x, y);
                    // Paint cell value
                    if (cell.getValue() != 0) {
                        g.setFont(bigFont);
                        int cx = x * CELL_OUTER_SIZE + CELL_PAD + CELL_INNER_SIZE / 2;
                        int cy = y * CELL_OUTER_SIZE + CELL_PAD + CELL_INNER_SIZE / 2;
                        initValueColor(g, cell);
                      if ( Settings.getInstance().isNumbers() ) {
                        drawStringCentered(g, "" + cell.getValue(), cx+adj, cy+adj);
                      } else {
                        int value = cell.getValue();
                        drawStringCentered(g, DOTA.substring(value,value+1), cx+adj, cy+adj);
                      }
                    }
            }
        }
    }

    private class Line {

        public final int sx;
        public final int sy;
        public final int ex;
        public final int ey;

        public Line(int sx, int sy, int ex, int ey) {
            this.sx = sx;
            this.sy = sy;
            this.ex = ex;
            this.ey = ey;
        }

        private int distanceUnscaled(int px, int py) {
            // Vectorial product, without normalization by length
            return (px - sx) * (ey - sy) - (py - sy) * (ex - sx);
        }

        private boolean intervalOverlaps(int s1, int e1, int s2, int e2) {
            if (s1 > e1) {
                // Swap
                s1 = s1 ^ e1;
                e1 = s1 ^ e1;
                s1 = s1 ^ e1;
            }
            if (s2 > e2) {
                // Swap
                s2 = s2 ^ e2;
                e2 = s2 ^ e2;
                s2 = s2 ^ e2;
            }
            return s1 < e2 && e1 > s2;
        }

        public boolean overlaps(Line other) {
            if (distanceUnscaled(other.sx, other.sy) == 0 &&
                    distanceUnscaled(other.ex, other.ey) == 0) {
                // Both lines are on the same right
                return intervalOverlaps(this.sx, this.ex, other.sx, other.ex)
                || intervalOverlaps(this.sy, this.ey, other.sy, other.ey);
            }
            return false;
        }

    }

    private void paintLinks(Graphics g, int adj) {
        g.setColor(Color.red);
        if (links != null) {
            Collection<Line> paintedLines = new ArrayList<Line>();
            for (Link link : links) {
                double sx = link.getSrcCell().getX() * CELL_OUTER_SIZE + CELL_PAD + CELL_INNER_SIZE / 6;
                double sy = link.getSrcCell().getY() * CELL_OUTER_SIZE + CELL_PAD + CELL_INNER_SIZE / 6;
                int srcValue = link.getSrcValue();
                if (srcValue > 0) {
                    sx += ((srcValue - 1) % 3) * (CELL_INNER_SIZE / 3);
                    sy += ((srcValue - 1) / 3) * (CELL_INNER_SIZE / 3);
                } else {
                    sx += CELL_INNER_SIZE / 3;
                    sy += CELL_INNER_SIZE / 3;
                }
                double ex = link.getDstCell().getX() * CELL_OUTER_SIZE + CELL_PAD + CELL_INNER_SIZE / 6;
                double ey = link.getDstCell().getY() * CELL_OUTER_SIZE + CELL_PAD + CELL_INNER_SIZE / 6;
                int dstValue = link.getDstValue();
                if (dstValue > 0) {
                    ex += ((dstValue - 1) % 3) * (CELL_INNER_SIZE / 3);
                    ey += ((dstValue - 1) / 3) * (CELL_INNER_SIZE / 3);
                } else {
                    ex += CELL_INNER_SIZE / 3;
                    ey += CELL_INNER_SIZE / 3;
                }
                // Get unity vector
                double length = Math.sqrt((ex - sx) * (ex - sx) + (ey - sy) * (ey - sy));
                double ux = (ex - sx) / length;
                double uy = (ey - sy) / length;
                // Build line object
                Line line = new Line((int)sx+adj, (int)sy+adj, (int)ex+adj, (int)ey+adj);
                // Count number of overlapping lines
                int countOverlap = 0;
                for (Line other : paintedLines) {
                    if (line.overlaps(other))
                        countOverlap++;
                }
                // Move the line perpendicularly to go away from overlapping lines
                double mx = (uy * ((countOverlap + 1) / 2) * 3.0);
                double my = (ux * ((countOverlap + 1) / 2) * 3.0);
                if (countOverlap % 2 == 0)
                    mx = -mx;
                else
                    my = -my;
                if (length >= CELL_INNER_SIZE / 2) {
                    // Truncate end points
                    if (srcValue > 0) {
                        sx += ux * CELL_INNER_SIZE / 6;
                        sy += uy * CELL_INNER_SIZE / 6;
                    }
                    if (dstValue > 0) {
                        ex -= ux * CELL_INNER_SIZE / 6;
                        ey -= uy * CELL_INNER_SIZE / 6;
                    }
                    if (dstValue > 0) {
                        // Draw arrow
                        double lx = ex - ux * 5 + uy * 2;
                        double ly = ey - uy * 5 - ux * 2;
                        double rx = ex - ux * 5 - uy * 2;
                        double ry = ey - uy * 5 + ux * 2;
                        g.fillPolygon(new int[] {(int)(ex + mx)+adj, (int)(rx + mx)+adj, (int)(lx + mx)+adj},
                                      new int[] {(int)(ey + my)+adj, (int)(ry + my)+adj, (int)(ly + my)+adj}, 3);
                    }
                    paintedLines.add(line);
                }
                // Draw the line
                if (dstValue == 0 && srcValue == 0) {
                    Color oldColor = g.getColor();
                    //g.setColor(Color.magenta);
                    g.drawLine((int)(sx + mx)+adj, (int)(sy + my)+adj, (int)(ex + mx)+adj, (int)(ey + my)+adj);
                    g.setColor(oldColor);
                } else {
                    g.drawLine((int)(sx + mx)+adj, (int)(sy + my)+adj, (int)(ex + mx)+adj, (int)(ey + my)+adj);
                }
            }
        }
    }

    private void paintCursor() {
        /*
    if (selectedCell == focusedCell)
      this.setCursor(new Cursor(Cursor.TEXT_CURSOR));
    else
      this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
         */
    }

}
