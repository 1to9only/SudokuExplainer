/*
 * Project: Sudoku Explainer
 * Copyright (C) 2006-2007 Nicolas Juillerat
 * Available under the terms of the Lesser General Public License (LGPL)
 */
package diuf.sudoku.io;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.*;
import java.util.*;
import java.util.List;

import diuf.sudoku.*;

/**
 * Static methods to load and store Sudokus from and to
 * files or the clipboard.
 * <p>
 * The support for formats is minimal and quick&dirty.
 * Only plain text formats are supported when reading:
 * <ul>
 * <li>A single line of 81 characters (all characters not in the
 * '1' - '9' range is considered as an empty cell).
 * <li>9 lines of 9 characters.
 * <li>Other multi-lines formats, with more than one character per cell,
 * or more than one line per row, or even with a few characters between
 * blocks might be supported, but there is no warranty. If a given format
 * works, and is not one of the first two above, you should consider you are lucky.
 * </ul>
 * <p>
 * When writing, the following format is used:
 * <ul>
 * <li>9 lines of 9 characters
 * <li>empty cells are represented by a '.'
 * </ul>
 */
public class SudokuIO {

    private static final int RES_OK = 2;
    private static final int RES_WARN = 1;
    private static final int RES_ERROR = 0;

    private static final String ERROR_MSG = "Unreadable Sudoku format";
    private static final String WARNING_MSG = "Warning: the Sudoku format was not recognized.\nThe Sudoku may not have been read correctly";

    private static int loadFromReader(Grid grid, Reader reader) throws IOException {
        List<String> lines = new ArrayList<String>();
        LineNumberReader lineReader = new LineNumberReader(reader);
        String line = lineReader.readLine();
        while (line != null) {
            lines.add(line);
            line = lineReader.readLine();
        }
        if (lines.size() > 1) {
            String allLines = "";
            String[] arrLines = new String[lines.size()];
            lines.toArray(arrLines);
            for (int i = 0; i < arrLines.length; i++)
                allLines += arrLines[i] + " ";
            int result = loadFromSingleLine(grid, allLines);
            return result;
        } else
        if (lines.size() == 1) {
            int result = loadFromSingleLine(grid, lines.get(0));
            return result;
        }
        return RES_ERROR;
    }

//  private static int autoDetectFormat(String line) {
//      int cellnum = 0;
//      int cluenum = 0;
//      int linelen = line.length();
//      char ch = 0;
//      int pformat = 0;
//      int ispad = 0;
//      int grpcnt = 0;
//      int grpmax = 0;
//      int cluecount = 0;
//      while ( cellnum < 81 && cluenum < linelen ) {
//          ch = line.charAt(cluenum++);
//          if (ch > '9' && ch <= 'I') { cluecount++; ispad = 0; grpcnt++;
//              if ( pformat < 4 ) {
//                  pformat = 4;
//              }
//          }
//          if (ch > '8' && ch <= '9') { cluecount++; ispad = 0; grpcnt++;
//              if ( pformat == 3 || pformat < 2 ) {
//                  pformat = 2;
//              }
//          }
//          if (ch >= 'A' && ch <= '8') { cluecount++; ispad = 0; grpcnt++;
//              if ( pformat == 3 || pformat < 1 ) {
//                  pformat = 1;
//              }
//          }
//          if (ch >= '0' && ch <= '9') { cluecount++; ispad = 0; grpcnt++;
//              if ( pformat == 0 ) {
//                  pformat = 3;
//              }
//          }
// 
//          if (ch == '.' || ch == '0') { cluecount++; ispad = 0; grpcnt++; }
//          if ( ispad == 0 ) { ispad = 1; if ( grpcnt > grpmax ) { grpmax = grpcnt; } grpcnt = 0; }
//          if ( cluecount >= 81 ) { return pformat; }
//      }
//      return pformat;
//  }
// 
    private static int loadFromSingleLine(Grid grid, String line) {
        line += " "; // extra char
        int cellnum = 0;
        int cluenum = 0;
        int linelen = line.length();
        char ch = 0;
        int pformat = Settings.getInstance().getPuzzleFormat();
        int ispad = 0;
        int grpcnt = 0;
        int grpmax = 0;
        int cluecount = 0;
        while ( cluenum < linelen ) {
            ch = line.charAt(cluenum++);
            switch ( pformat ) {
            case 1:
                if (ch >= '0' && ch <= '8') { cluecount++; ispad = 0; grpcnt++; }
           else if (ch >= 'A' && ch <= '8') { cluecount++; ispad = 0; grpcnt++; }
           else if (ch == '.'             ) { cluecount++; ispad = 0; grpcnt++; }
           else if ( ispad == 0 ) { ispad = 1; if ( grpcnt > grpmax ) { grpmax = grpcnt; } grpcnt = 0; }
                break;
            case 2:
                if (ch >= '1' && ch <= '9') { cluecount++; ispad = 0; grpcnt++; }
           else if (ch >= 'A' && ch <= '9') { cluecount++; ispad = 0; grpcnt++; }
           else if (ch == '.' || ch == '0') { cluecount++; ispad = 0; grpcnt++; }
           else if ( ispad == 0 ) { ispad = 1; if ( grpcnt > grpmax ) { grpmax = grpcnt; } grpcnt = 0; }
                break;
            case 3:
                if (ch >= '0' && ch <= '9') { cluecount++; ispad = 0; grpcnt++; }
           else if (ch == '.'             ) { cluecount++; ispad = 0; grpcnt++; }
           else if ( ispad == 0 ) { ispad = 1; if ( grpcnt > grpmax ) { grpmax = grpcnt; } grpcnt = 0; }
                break;
            case 4:
            default:
                if (ch >= 'A' && ch <= 'I') { cluecount++; ispad = 0; grpcnt++; }
           else if (ch == '.' || ch == '0') { cluecount++; ispad = 0; grpcnt++; }
           else if ( ispad == 0 ) { ispad = 1; if ( grpcnt > grpmax ) { grpmax = grpcnt; } grpcnt = 0; }
                break;
            }
        }

        cellnum = 0;
        cluenum = 0;

        if ( cluecount >= 81 ) { // sudoku
            while ( cellnum < 81 && cluenum < linelen ) {
                ch = line.charAt(cluenum++);
                switch ( pformat ) {
                case 1:
                    if (ch >= '0' && ch <= '8') { int value = ch - '0'+1; grid.setCellValue(cellnum % 9, cellnum / 9, value); cellnum++; }
               else if (ch >= 'A' && ch <= '8') { int value = ch -'A'+11; grid.setCellValue(cellnum % 9, cellnum / 9, value); cellnum++; }
               else if (ch == '.'             ) { cellnum++; }
                    break;
                case 2:
                    if (ch >= '1' && ch <= '9') { int value = ch - '1'+1; grid.setCellValue(cellnum % 9, cellnum / 9, value); cellnum++; }
               else if (ch >= 'A' && ch <= '9') { int value = ch -'A'+10; grid.setCellValue(cellnum % 9, cellnum / 9, value); cellnum++; }
               else if (ch == '.' || ch == '0') { cellnum++; }
                    break;
                case 3:
                    char ch2 = line.charAt(cluenum);
//     if (ch == '2' && ch2>= '0' && ch2<= '9') { int value = ch2-'0'+20; grid.setCellValue(cellnum % 9, cellnum / 9, value); cellnum++; cluenum++; } else
//     if (ch == '1' && ch2>= '0' && ch2<= '9') { int value = ch2-'0'+10; grid.setCellValue(cellnum % 9, cellnum / 9, value); cellnum++; cluenum++; } else
                    if (ch >= '1' && ch <= '9') { int value = ch - '1'+1; grid.setCellValue(cellnum % 9, cellnum / 9, value); cellnum++; }
               else if (ch == '.'             ) { cellnum++; }
                    break;
                case 4:
                default:
                    if (ch >= 'A' && ch <= 'I') { int value = ch - 'A'+1; grid.setCellValue(cellnum % 9, cellnum / 9, value); cellnum++; }
               else if (ch == '.' || ch == '0') { cellnum++; }
                    break;
                }
            }
            return ( cellnum==81 ? RES_OK : RES_WARN);
        }
//      // try again!!
//    if ( Settings.getInstance().getGridSize() >= 10 ) {
//          cellnum = 0;
//          cluenum = 0;
//          linelen = line.length();
//           ch = 0;
//          pformat = autoDetectFormat( line);
//          ispad = 0;
//          grpcnt = 0;
//          grpmax = 0;
//          cluecount = 0;
//      while ( cluenum < linelen ) {
//          ch = line.charAt(cluenum++);
//          switch ( pformat ) {
//          case 1:
//              if (ch >= '0' && ch <= '8') { cluecount++; ispad = 0; grpcnt++; }
//         else if (ch >= 'A' && ch <= '8') { cluecount++; ispad = 0; grpcnt++; }
//         else if (ch == '.'             ) { cluecount++; ispad = 0; grpcnt++; }
//         else if ( ispad == 0 ) { ispad = 1; if ( grpcnt > grpmax ) { grpmax = grpcnt; } grpcnt = 0; }
//              break;
//          case 2:
//              if (ch >= '1' && ch <= '9') { cluecount++; ispad = 0; grpcnt++; }
//         else if (ch >= 'A' && ch <= '9') { cluecount++; ispad = 0; grpcnt++; }
//         else if (ch == '.' || ch == '0') { cluecount++; ispad = 0; grpcnt++; }
//         else if ( ispad == 0 ) { ispad = 1; if ( grpcnt > grpmax ) { grpmax = grpcnt; } grpcnt = 0; }
//              break;
//          case 3:
//              if (ch >= '0' && ch <= '9') { cluecount++; ispad = 0; grpcnt++; }
//         else if (ch == '.'             ) { cluecount++; ispad = 0; grpcnt++; }
//         else if ( ispad == 0 ) { ispad = 1; if ( grpcnt > grpmax ) { grpmax = grpcnt; } grpcnt = 0; }
//              break;
//          case 4:
//          default:
//              if (ch >= 'A' && ch <= 'I') { cluecount++; ispad = 0; grpcnt++; }
//         else if (ch == '.' || ch == '0') { cluecount++; ispad = 0; grpcnt++; }
//         else if ( ispad == 0 ) { ispad = 1; if ( grpcnt > grpmax ) { grpmax = grpcnt; } grpcnt = 0; }
//              break;
//          }
//      }
// 
//      cellnum = 0;
//      cluenum = 0;
// 
//      if ( cluecount >= 81 ) { // sudoku
//          while ( cellnum < 81 && cluenum < linelen ) {
//              ch = line.charAt(cluenum++);
//              switch ( pformat ) {
//              case 1:
//                  if (ch >= '0' && ch <= '8') { int value = ch - '0'+1; grid.setCellValue(cellnum % 9, cellnum / 9, value); cellnum++; }
//             else if (ch >= 'A' && ch <= '8') { int value = ch -'A'+11; grid.setCellValue(cellnum % 9, cellnum / 9, value); cellnum++; }
//             else if (ch == '.'             ) { cellnum++; }
//                  break;
//              case 2:
//                  if (ch >= '1' && ch <= '9') { int value = ch - '1'+1; grid.setCellValue(cellnum % 9, cellnum / 9, value); cellnum++; }
//             else if (ch >= 'A' && ch <= '9') { int value = ch -'A'+10; grid.setCellValue(cellnum % 9, cellnum / 9, value); cellnum++; }
//             else if (ch == '.' || ch == '0') { cellnum++; }
//                  break;
//              case 3:
//                  char ch2 = line.charAt(cluenum);
//     if (ch == '2' && ch2>= '0' && ch2<= '9') { int value = ch2-'0'+20; grid.setCellValue(cellnum % 9, cellnum / 9, value); cellnum++; cluenum++; } else
//     if (ch == '1' && ch2>= '0' && ch2<= '9') { int value = ch2-'0'+10; grid.setCellValue(cellnum % 9, cellnum / 9, value); cellnum++; cluenum++; } else
//                  if (ch >= '1' && ch <= '9') { int value = ch - '1'+1; grid.setCellValue(cellnum % 9, cellnum / 9, value); cellnum++; }
//             else if (ch == '.'             ) { cellnum++; }
//                  break;
//              case 4:
//              default:
//                  if (ch >= 'A' && ch <= 'I') { int value = ch - 'A'+1; grid.setCellValue(cellnum % 9, cellnum / 9, value); cellnum++; }
//             else if (ch == '.' || ch == '0') { cellnum++; }
//                  break;
//              }
//          }
//          return ( cellnum==81 ? RES_OK : RES_WARN);
//      }
//    }
        return RES_ERROR;
    }

    private static void saveToWriter(Grid grid, Writer writer) throws IOException {
        int pformat = Settings.getInstance().getPuzzleFormat();
        int gSize = Settings.getInstance().getGridSize();
        int sformat = Settings.getInstance().getSaveFormat();
        String text = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        if ( pformat == 5 ) {
            char[] characters = text.toCharArray();
            for (int i=0; i <81; i++) { int n1 = (int)( Math.random() * characters.length); int n2 = (int)( Math.random() * characters.length); char temp = characters[n1]; characters[n1] = characters[n2]; characters[n2] = temp; }
            text = new String( characters);
        }
      if ( sformat != 0 ) {
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                int value = grid.getCellValue(x, y);
                String ch = ".";
                switch ( pformat ) {
                case 1:
                    if (value > 0) ch = ".0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".substring(value,value+1);
                    break;
                case 2:
                    if (value > 0) ch = ".123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".substring(value,value+1);
                    break;
                case 3:
                    if (value > 0) ch = ""  + value;
//                  if (ch.length() == 1 && gSize > 9) ch = " " + ch;
                    ch = " "  + ch;
                    break;
                case 5:
                    ch = text.substring(value,value+1);
                    break;
                case 4:
                default:
                    if (value > 0) ch = ".ABCDEFGHIJKLMNOPQRSTUVWXYZ".substring(value,value+1);
                    break;
                }
                writer.write(ch);
            }
            writer.write("\r\n");
        }
      }
      if ( sformat == 0 ) {
        boolean isLatin = Settings.getInstance().isLatinSquare();
        int crd = 1; if (pformat == 3 && gSize >= 10) crd = 2;

        int y = 0, x = 0;
        if ( Settings.getInstance().isRC33() ) {
            y = 3; x = 3;
        }
        else {
            y = 3; x = 3;
        }
        String s = "";
        for (int i=0; i<x; i++ ) {
          if ( i == 0 || !isLatin ) {
            s = "+";
            for (int j=0; j<y; j++ ) {
                for (int k=0; k<x; k++ ) { s += "-";
                    for (int l=0; l<crd; l++ ) { s += "-";
                    }
                }
              if ( j+1 == y || !isLatin )
                s += "-+";
            }
            writer.write(s + "\r\n");
          }

            for (int j=0; j<y; j++ ) {
                s = "|";
                for (int k=0; k<y; k++ ) {
                    for (int l=0; l<x; l++ ) {
                        s += " ";
                //      int cnt = 0;
                        int c = (((i*y)+j)*9)+k*x+l;
                        Cell cell = grid.getCell(c % 9, c / 9);
                        int n = cell.getValue();

                String ch = ".";
                switch ( pformat ) {
                case 1:
                    if (n > 0) ch = ".0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".substring(n,n+1);
                    break;
                case 2:
                    if (n > 0) ch = ".123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".substring(n,n+1);
                    break;
                case 3:
                    if (n > 0) ch = ""  + n;
//                  if (ch.length() == 1 && gSize > 9) ch = " " + ch;
                    break;
                case 5:
                    ch = text.substring(n,n+1);
                    break;
                case 4:
                default:
                    if (n > 0) ch = ".ABCDEFGHIJKLMNOPQRSTUVWXYZ".substring(n,n+1);
                    break;
                }
                s += ch;

                    }
                  if ( k+1 == y || !isLatin )
                    s += " |";
                }
                writer.write(s + "\r\n");
            }
        }

        s = "+";
        for (int j=0; j<y; j++ ) {
            for (int k=0; k<x; k++ ) { s += "-";
                for (int l=0; l<crd; l++ ) { s += "-";
                }
            }
          if ( j+1 == y || !isLatin )
            s += "-+";
        }
        writer.write(s + "\r\n");
      }
    }

    private static void saveToWriter81(Grid grid, Writer writer) throws IOException {
        int pformat = Settings.getInstance().getPuzzleFormat();
        int gSize = Settings.getInstance().getGridSize();
        String text = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        if ( pformat == 5 ) {
            char[] characters = text.toCharArray();
            for (int i=0; i <81; i++) { int n1 = (int)( Math.random() * characters.length); int n2 = (int)( Math.random() * characters.length); char temp = characters[n1]; characters[n1] = characters[n2]; characters[n2] = temp; }
            text = new String( characters);
        }
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                int value = grid.getCellValue(x, y);
                String ch = ".";
                switch ( pformat ) {
                case 1:
                    if (value > 0) ch = ".0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".substring(value,value+1);
                    break;
                case 2:
                    if (value > 0) ch = ".123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".substring(value,value+1);
                    break;
                case 3:
                    if (value > 0) ch = ""  + value;
                  if ( !(y == 0 && x == 0) ) {
                    ch = " "  + ch;
                  }
                    break;
                case 5:
                    ch = text.substring(value,value+1);
                    break;
                case 4:
                default:
                    if (value > 0) ch = ".ABCDEFGHIJKLMNOPQRSTUVWXYZ".substring(value,value+1);
                    break;
                }
                writer.write(ch);
            }
        }
        writer.write("\r\n");
    }

    private static void savePencilMarksToWriter(Grid grid, Writer writer) throws IOException {
        boolean isLatin = Settings.getInstance().isLatinSquare();
        int crd = 1;
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                int n = grid.getCell(x, y).getPotentialValues().cardinality();
                if ( n > crd ) { crd = n; }
            }
        }

        int y = 0, x = 0;
        if ( Settings.getInstance().isRC33() ) {
            y = 3; x = 3;
        }
        else {
            y = 3; x = 3;
        }
        String s = "";
        for (int i=0; i<x; i++ ) {
          if ( i == 0 || !isLatin ) {
            s = "+";
            for (int j=0; j<y; j++ ) {
                for (int k=0; k<x; k++ ) { s += "-";
                    for (int l=0; l<crd; l++ ) { s += "-";
                    }
                }
              if ( j+1 == y || !isLatin )
                s += "-+";
            }
            writer.write(s + "\r\n");
          }

            for (int j=0; j<y; j++ ) {
                s = "|";
                for (int k=0; k<y; k++ ) {
                    for (int l=0; l<x; l++ ) {
                        s += " ";
                        int cnt = 0;
                        int c = (((i*y)+j)*9)+k*x+l;
                        Cell cell = grid.getCell(c % 9, c / 9);
                        int n = cell.getValue();
                        if ( n != 0 ) {
                            s += ".123456789".substring(n,n+1);
                            cnt += 1;
                        }
                        if ( n == 0 ) {
                            for (int pv=1; pv<=9; pv++ ) {
                                if ( cell.hasPotentialValue( pv) ) {
                                    s += ".123456789".substring(pv,pv+1);
                                    cnt += 1;
                                }
                            }
                        }
                        for (int pad=cnt; pad<crd; pad++ ) { s += " ";
                        }
                    }
                  if ( k+1 == y || !isLatin )
                    s += " |";
                }
                writer.write(s + "\r\n");
            }
        }

        s = "+";
        for (int j=0; j<y; j++ ) {
            for (int k=0; k<x; k++ ) { s += "-";
                for (int l=0; l<crd; l++ ) { s += "-";
                }
            }
          if ( j+1 == y || !isLatin )
            s += "-+";
        }
        writer.write(s + "\r\n");
    }

    /**
     * Test whether a Sudoku can be loaded from the current
     * content of the clipboard.
     * @return whether a Sudoku can be loaded from the current
     * content of the clipboard
     */
    public static boolean isClipboardLoadable() {
        Grid grid = new Grid();
        return (loadFromClipboard(grid) == null);
    }

    public static ErrorMessage loadFromClipboard(Grid grid) {
        Transferable content =
            Toolkit.getDefaultToolkit().getSystemClipboard().getContents(grid);
        if (content == null)
            return new ErrorMessage("The clipboard is empty");
        Reader reader = null;
        try {
            DataFlavor flavor = new DataFlavor(String.class, "Plain text");
            reader = flavor.getReaderForText(content);
            int result = loadFromReader(grid, reader);
            if (result == RES_OK) // success
                return null;
            if (result == RES_WARN) // warning
                return new ErrorMessage(WARNING_MSG, false, (Object[])(new String[0]));
            else // error
                return new ErrorMessage(ERROR_MSG, true, (Object[])(new String[0]));
        } catch (IOException ex) {
            return new ErrorMessage("Error while copying:\n{0}", ex);
        } catch (UnsupportedFlavorException ex) {
            return new ErrorMessage("Unsupported data type");
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch(Exception ex) {}
        }
    }

    public static void saveToClipboard81(Grid grid) {
        StringWriter writer = new StringWriter();
        try {
            saveToWriter81(grid, writer);
            StringSelection data = new StringSelection(writer.toString());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(data, data);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void saveToClipboard(Grid grid) {
        StringWriter writer = new StringWriter();
        try {
            saveToWriter(grid, writer);
            StringSelection data = new StringSelection(writer.toString());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(data, data);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void savePencilMarksToClipboard(Grid grid) {
        StringWriter writer = new StringWriter();
        try {
            savePencilMarksToWriter(grid, writer);
            StringSelection data = new StringSelection(writer.toString());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(data, data);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static ErrorMessage loadFromFile(Grid grid, File file) {
        Reader reader = null;
        try {
            FileReader freader = new FileReader(file);
            reader = new BufferedReader(freader);
            int result = loadFromReader(grid, reader);
            if (result == RES_OK)
                return null;
            else if (result == RES_WARN)
                return new ErrorMessage(WARNING_MSG, false, (Object[])(new String[0]));
            else
                return new ErrorMessage(ERROR_MSG, true, (Object[])(new String[0]));
        } catch (FileNotFoundException ex) {
            return new ErrorMessage("File not found: {0}", file);
        } catch (IOException ex) {
            return new ErrorMessage("Error while reading file {0}:\n{1}", file, ex);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static ErrorMessage saveToFile81(Grid grid, File file) {
        Writer writer = null;
        try {
            FileWriter fwriter = new FileWriter(file);
            writer = new BufferedWriter(fwriter);
            saveToWriter81(grid, writer);
            return null;
        } catch (IOException ex) {
            return new ErrorMessage("Error while writing file {0}:\n{1}", file, ex);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static ErrorMessage saveToFile(Grid grid, File file) {
        Writer writer = null;
        try {
            FileWriter fwriter = new FileWriter(file);
            writer = new BufferedWriter(fwriter);
            saveToWriter(grid, writer);
            return null;
        } catch (IOException ex) {
            return new ErrorMessage("Error while writing file {0}:\n{1}", file, ex);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static ErrorMessage savePencilMarksToFile(Grid grid, File file) {
        Writer writer = null;
        try {
            FileWriter fwriter = new FileWriter(file);
            writer = new BufferedWriter(fwriter);
            savePencilMarksToWriter(grid, writer);
            return null;
        } catch (IOException ex) {
            return new ErrorMessage("Error while writing file {0}:\n{1}", file, ex);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

}
