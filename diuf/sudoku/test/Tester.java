/*
 * Project: Sudoku Explainer
 * Copyright (C) 2006-2007 Nicolas Juillerat
 * Available under the terms of the Lesser General Public License (LGPL)
 */
package diuf.sudoku.test;

import java.io.*;
import java.text.*;
import java.util.*;

import diuf.sudoku.*;
import diuf.sudoku.solver.*;


public class Tester {

    /**
     * Analyze all the Sudokus of a given file, and store the results
     * in another given file. The content of the result file is also
     * printed on the console.
     * @param args the two file names
     */
    public static void main(String[] args) {
        if (args.length != 2)
            throw new IllegalArgumentException("Expected arguments: fileName log");
        String fileName = args[0];
        String logFile = args[1];
        LineNumberReader reader = null;
        PrintWriter writer = null;
        Settings.getInstance().setNoSaves();
        try {
            Reader reader0 = new FileReader(fileName);
            reader = new LineNumberReader(reader0);
            Writer writer0 = new FileWriter(logFile);
            BufferedWriter writer1 = new BufferedWriter(writer0);
            writer = new PrintWriter(writer1);
            DecimalFormat format = new DecimalFormat("#0.0");
            String line = reader.readLine();
            while (line != null) {
                line = line.trim();
                if (line.length() >= 81) {
                    writer.println("Analyzing Sudoku #" + reader.getLineNumber());
                    System.out.println("Analyzing Sudoku #" + reader.getLineNumber());
                    writer.println(line);
                    System.out.println(line);
                    Grid grid = new Grid();
                    for (int i = 0; i < 81; i++) {
                        char ch = line.charAt(i);
                        if (ch >= '1' && ch <= '9') {
                            int value = (ch - '0');
                            grid.setCellValue(i % 9, i / 9, value);
                        }
                        if (ch >= 'A' && ch <= 'I') {
                            int value = (ch - 'A'+1);
                            grid.setCellValue(i % 9, i / 9, value);
                        }
                    }
                    Solver solver = new Solver(grid);
                    solver.rebuildPotentialValues();
                    try {
                        Map<Rule,Integer> rules = solver.solve(null);
                        Map<String,Integer> ruleNames = solver.toNamedList(rules);
                        double difficulty = 0;
                        String hardestRule = "";
                        for (Rule rule : rules.keySet()) {
                            if (rule.getDifficulty() > difficulty) {
                                difficulty = rule.getDifficulty();
                                hardestRule = rule.getName();
                            }
                        }
                        for (String ruleName : ruleNames.keySet()) {
                            int count = ruleNames.get(ruleName);
                            double minRuleDifficulty = 20.0;
                            for (Rule rule : rules.keySet()) {
                                if (rule.getName().equals(ruleName)) {
                                    if ( minRuleDifficulty > rule.getDifficulty() ) {
                                        minRuleDifficulty = rule.getDifficulty();
                                    }
                                }
                            }
                            double maxRuleDifficulty = 0.0;
                            for (Rule rule : rules.keySet()) {
                                if (rule.getName().equals(ruleName)) {
                                    if ( maxRuleDifficulty < rule.getDifficulty() ) {
                                        maxRuleDifficulty = rule.getDifficulty();
                                    }
                                }
                            }
                          if ( minRuleDifficulty == maxRuleDifficulty ) {
                            writer.println(Integer.toString(count) + " x " + ruleName+" ("+format.format(minRuleDifficulty)+")"); }
                          if ( minRuleDifficulty != maxRuleDifficulty ) {
                            writer.println(Integer.toString(count) + " x " + ruleName+" ("+format.format(minRuleDifficulty)+"-"+format.format(maxRuleDifficulty)+")"); }

                          if ( minRuleDifficulty == maxRuleDifficulty ) {
                            System.out.println(Integer.toString(count) + " x " + ruleName+" ("+format.format(minRuleDifficulty)+")"); }
                          if ( minRuleDifficulty != maxRuleDifficulty ) {
                            System.out.println(Integer.toString(count) + " x " + ruleName+" ("+format.format(minRuleDifficulty)+"-"+format.format(maxRuleDifficulty)+")"); }
                        }
                    //  writer.println("Hardest technique: " + hardestRule);
                    //  System.out.println("Hardest technique: " + hardestRule);
                    //  writer.println("Difficulty: " + difficulty);
                    //  System.out.println("Difficulty: " + difficulty);
                    } catch (UnsupportedOperationException ex) {
                        writer.println("Failed !");
                        System.out.println("Failed !");
                    }
                    writer.println();
                    System.out.println();
                    writer.flush();
                }// else
                 // System.out.println("Skipping incomplete line: " + line);
                line = reader.readLine();
            }
            writer.close();
            reader.close();
        } catch(FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (reader != null)
                    reader.close();
                if (writer != null)
                    writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        System.out.print("Finished.");
    }

}
