/*
 * Project: Sudoku Explainer
 * Copyright (C) 2006-2007 Nicolas Juillerat
 * Available under the terms of the Lesser General Public License (LGPL)
 */
package diuf.sudoku;

import java.util.*;
//port java.util.prefs.*;

import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.UIManager;

/**
 * Global settings of the application.
 * Implemented using the singleton pattern.
 */
public class Settings {

    public final static int VERSION  = 2022;
    public final static int REVISION = 3;
    public final static int SUBREV   = 24;

    private static Settings instance = null;

    private static String jsonFilename = "Sudoku9Explainer.json";

    private boolean isRCNotation = true;
    private boolean isAntialiasing = true;
    private boolean isShowingCandidates = true;
    private boolean isShowingCandidateMasks = false;
    private String lookAndFeelClassName = null;
    private int iPuzzleFormat = 2;
    private int iGridSize = 9;
    private int iSaveFormat = 0;

    private String s0 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private String s1 = "123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private String sA = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private EnumSet<SolvingTechnique> techniques;

    private boolean isVertical = false;             // generate dialog
    private boolean isHorizontal = false;
    private boolean isDiagonal = false;
    private boolean isAntiDiagonal = false;
    private boolean isBiDiagonal = true;
    private boolean isOrthogonal = true;
    private boolean isRotational180 = true;
    private boolean isRotational90 = true;
    private boolean isNone = true;
    private boolean isFull = true;

    private boolean isEasy = false;
    private boolean isMedium = false;
    private boolean isHard = false;
    private boolean isFiendish = true;
    private boolean isDiabolical = false;

    private boolean isExact = false;

    private boolean isRC33 = true;                  // 3Rx3C or 3Rx3C
//  private boolean isRC33 = false;                 // 3Rx3C or 3Rx3C
    private boolean isLatinSquare = false;          // latin square
//  private boolean isLatinSquare = true;           // latin square
    private boolean isDiagonals = false;            // diagonals
    private boolean isXDiagonal = true;             // diagonal(/)
    private boolean isXAntiDiagonal = true;         // antidiagonal(\)
    private boolean isDisjointGroups = false;       // disjointgroups
    private boolean isWindoku = false;              // windoku
    private boolean isWindowsClosed = false;        // windowsclosed
    private boolean isWindowsOpen = false;          // windowsopen

    private boolean isCustom = false;
    private String custom = null;       // custom variant regions
    private int count = 9;              // custom regions
    private int customconnect = 0;      // custom connections

    private boolean isNumbers = true;               // is Numbers (else Alphas)

    private String FontName = "Verdana";

    private int apply = 23;             // apply button: 23= singles, 28= basics

    private int isChanged = 0;          // =1 if a setting changed

    private int LoadError = 0;          // =1 if settings load error, a save is done

    private boolean noSaves = false;    // =true no saves done, is set from command line

    private String methods = null;      // techniques, 1=enabled, 0=disabled

    private Settings() {
        init();
        load();
    }

    public static Settings getInstance() {
        if (instance == null)
            instance = new Settings();
        return instance;
    }

    public void setNoSaves() {      // call from command line utils, no saves done
        noSaves = true;
        init();                     // enable all solving techniques
        isRC33 = true;              // 3Rx3C or 3Rx3C
        isLatinSquare = false;      // set to vanilla sudoku
        isDiagonals = false;
        isXDiagonal = true;
        isXAntiDiagonal = true;
        isDisjointGroups = false;
        isWindoku = false;
        isWindowsClosed = false;
        isWindowsOpen = false;
        isCustom = false;
    }

    public void setRCNotation(boolean isRCNotation) {
      if ( this.isRCNotation != isRCNotation ) {
        this.isRCNotation = isRCNotation;
        save();
      }
    }

    public boolean isRCNotation() {
        return isRCNotation;
    }

    public void setAntialiasing(boolean isAntialiasing) {
      if ( this.isAntialiasing != isAntialiasing ) {
        this.isAntialiasing = isAntialiasing;
        save();
      }
    }

    public boolean isAntialiasing() {
        return this.isAntialiasing;
    }

    public void setShowingCandidates(boolean value) {
      if ( this.isShowingCandidates != value ) {
        this.isShowingCandidates = value;
        save();
      }
    }

    public boolean isShowingCandidates() {
        return this.isShowingCandidates;
    }

    public void setShowingCandidateMasks(boolean value) {
      if ( this.isShowingCandidateMasks != value ) {
        this.isShowingCandidateMasks = value;
        save();
      }
    }

    public boolean isShowingCandidateMasks() {
        return this.isShowingCandidateMasks;
    }

    public String getLookAndFeelClassName() {
        return lookAndFeelClassName;
    }

    public void setLookAndFeelClassName(String lookAndFeelClassName) {
      if ( !(this.lookAndFeelClassName.equals(lookAndFeelClassName)) ) {
        this.lookAndFeelClassName = lookAndFeelClassName;
        save();
      }
    }

    public String getFontName() {
        return FontName;
    }

//  public void setPuzzleFormat(int format) {
//    if ( this.iPuzzleFormat != format ) {
//      this.iPuzzleFormat = format;
//      save();
//    }
//  }

    public int getPuzzleFormat() {
        return iPuzzleFormat;
    }

    public int getGridSize() {
        return iGridSize;
    }

    public int getSaveFormat() {
        return iSaveFormat;
    }

    public String gets0() {
        return s0;
    }
    public String gets1() {
        return s1;
    }
    public String getsA() {
        return sA;
    }

    public EnumSet<SolvingTechnique> getTechniques() {
        return EnumSet.copyOf(this.techniques);
    }

    public void setTechniques(EnumSet<SolvingTechnique> techniques) {
        this.techniques = techniques;
        packmethods();
    }

    public boolean isUsingAllTechniques() {
        EnumSet<SolvingTechnique> all = EnumSet.allOf(SolvingTechnique.class);
        return this.techniques.equals(all);
    }

    public boolean isUsingOneOf(SolvingTechnique... solvingTechniques) {
        for (SolvingTechnique st : solvingTechniques) {
            if (this.techniques.contains(st))
                return true;
        }
        return false;
    }

    public boolean isusingAll(SolvingTechnique... solvingTechniques) {
        for (SolvingTechnique st : solvingTechniques) {
            if (!this.techniques.contains(st))
                return false;
        }
        return true;
    }

    public boolean isUsingAllButMaybeNot(SolvingTechnique... solvingTechniques) {
        List<SolvingTechnique> list = Arrays.asList(solvingTechniques);
        for (SolvingTechnique st : EnumSet.allOf(SolvingTechnique.class)) {
            if (!this.techniques.contains(st) && !list.contains(st))
                return false;
        }
        return true;
    }

    private void packmethods() {
        methods = "";
        for (SolvingTechnique st : EnumSet.allOf(SolvingTechnique.class)) {
            if (this.techniques.contains(st)) {
                methods += "1";
            } else {
                methods += "0";
            }
        }
        save();
    }

    public void unpackmethods() {
      if ( methods != null ) {
        if (EnumSet.allOf(SolvingTechnique.class).size() == methods.length()) {
            int index = 0;
            for (SolvingTechnique st : EnumSet.allOf(SolvingTechnique.class)) {
                char c = methods.charAt(index++);
                if (c == '1' && !this.techniques.contains(st)) {
                    techniques.add(st);
                }
                if (c == '0' && this.techniques.contains(st)) {
                    techniques.remove(st);
                }
            }
        }
      }
    }

    // generate dialog

    public void setVertical(boolean isVertical) {
      if ( this.isVertical != isVertical ) {
        this.isVertical = isVertical;
        save();
      }
    }
    public boolean isVertical() {
        return isVertical;
    }

    public void setHorizontal(boolean isHorizontal) {
      if ( this.isHorizontal != isHorizontal ) {
        this.isHorizontal = isHorizontal;
        save();
      }
    }
    public boolean isHorizontal() {
        return isHorizontal;
    }

    public void setDiagonal(boolean isDiagonal) {
      if ( this.isDiagonal != isDiagonal ) {
        this.isDiagonal = isDiagonal;
        save();
      }
    }
    public boolean isDiagonal() {
        return isDiagonal;
    }

    public void setAntiDiagonal(boolean isAntiDiagonal) {
      if ( this.isAntiDiagonal != isAntiDiagonal ) {
        this.isAntiDiagonal = isAntiDiagonal;
        save();
      }
    }
    public boolean isAntiDiagonal() {
        return isAntiDiagonal;
    }

    public void setBiDiagonal(boolean isBiDiagonal) {
      if ( this.isBiDiagonal != isBiDiagonal ) {
        this.isBiDiagonal = isBiDiagonal;
        save();
      }
    }
    public boolean isBiDiagonal() {
        return isBiDiagonal;
    }

    public void setOrthogonal(boolean isOrthogonal) {
      if ( this.isOrthogonal != isOrthogonal ) {
        this.isOrthogonal = isOrthogonal;
        save();
      }
    }
    public boolean isOrthogonal() {
        return isOrthogonal;
    }

    public void setRotational180(boolean isRotational180) {
      if ( this.isRotational180 != isRotational180 ) {
        this.isRotational180 = isRotational180;
        save();
      }
    }
    public boolean isRotational180() {
        return isRotational180;
    }

    public void setRotational90(boolean isRotational90) {
      if ( this.isRotational90 != isRotational90 ) {
        this.isRotational90 = isRotational90;
        save();
      }
    }
    public boolean isRotational90() {
        return isRotational90;
    }

    public void setNone(boolean isNone) {
      if ( this.isNone != isNone ) {
        this.isNone = isNone;
        save();
      }
    }
    public boolean isNone() {
        return isNone;
    }

    public void setFull(boolean isFull) {
      if ( this.isFull != isFull ) {
        this.isFull = isFull;
        save();
      }
    }
    public boolean isFull() {
        return isFull;
    }

    public void setEasy(boolean isEasy) {
      if ( this.isEasy != isEasy ) {
        this.isEasy = isEasy;
        isChanged = 1;
      }
    }
    public boolean isEasy() {
        return isEasy;
    }

    public void setMedium(boolean isMedium) {
      if ( this.isMedium != isMedium ) {
        this.isMedium = isMedium;
        isChanged = 1;
      }
    }
    public boolean isMedium() {
        return isMedium;
    }

    public void setHard(boolean isHard) {
      if ( this.isHard != isHard ) {
        this.isHard = isHard;
        isChanged = 1;
      }
    }
    public boolean isHard() {
        return isHard;
    }

    public void setFiendish(boolean isFiendish) {
      if ( this.isFiendish != isFiendish ) {
        this.isFiendish = isFiendish;
        isChanged = 1;
      }
    }
    public boolean isFiendish() {
        return isFiendish;
    }

    public void setDiabolical(boolean isDiabolical) {
      if ( this.isDiabolical != isDiabolical ) {
        this.isDiabolical = isDiabolical;
        isChanged = 1;
      }
    }
    public boolean isDiabolical() {
        return isDiabolical;
    }

    public void setExact(boolean isExact) {
      if ( this.isExact != isExact ) {
        this.isExact = isExact;
        save();
      }
    }
    public boolean isExact() {
        return isExact;
    }

    // variants

    public void setRC33(boolean isRC33) {
      if ( this.isRC33 != isRC33 ) {
        this.isRC33 = isRC33;
        isChanged = 1;
      }
    }
    public boolean isRC33() {
        return isRC33;
    }

    public void setLatinSquare(boolean isLatinSquare) {
      if ( this.isLatinSquare != isLatinSquare ) {
        this.isLatinSquare = isLatinSquare;
        isChanged = 1;
      }
    }
    public boolean isLatinSquare() {
        return isLatinSquare;
    }

    public void setDiagonals(boolean isDiagonals) {
      if ( this.isDiagonals != isDiagonals ) {
        this.isDiagonals = isDiagonals;
        isChanged = 1;
      }
    }
    public boolean isDiagonals() {
        return isDiagonals;
    }

    public void setXDiagonal(boolean isXDiagonal) {
      if ( this.isXDiagonal != isXDiagonal ) {
        this.isXDiagonal = isXDiagonal;
        isChanged = 1;
      }
    }
    public boolean isXDiagonal() {
        return isXDiagonal;
    }

    public void setXAntiDiagonal(boolean isXAntiDiagonal) {
      if ( this.isXAntiDiagonal != isXAntiDiagonal ) {
        this.isXAntiDiagonal = isXAntiDiagonal;
        isChanged = 1;
      }
    }
    public boolean isXAntiDiagonal() {
        return isXAntiDiagonal;
    }

    public void setDisjointGroups(boolean isDisjointGroups) {
      if ( this.isDisjointGroups != isDisjointGroups ) {
        this.isDisjointGroups = isDisjointGroups;
        isChanged = 1;
      }
    }
    public boolean isDisjointGroups() {
        return isDisjointGroups;
    }

    public void setWindoku(boolean isWindoku) {
      if ( this.isWindoku != isWindoku ) {
        this.isWindoku = isWindoku;
        isChanged = 1;
      }
    }
    public boolean isWindoku() {
        return isWindoku;
    }

    public void setWindowsClosed(boolean isWindowsClosed) {
      if ( this.isWindowsClosed != isWindowsClosed ) {
        this.isWindowsClosed = isWindowsClosed;
        isChanged = 1;
      }
    }
    public boolean isWindowsClosed() {
        return isWindowsClosed;
    }

    public void setWindowsOpen(boolean isWindowsOpen) {
      if ( this.isWindowsOpen != isWindowsOpen ) {
        this.isWindowsOpen = isWindowsOpen;
        isChanged = 1;
      }
    }
    public boolean isWindowsOpen() {
        return isWindowsOpen;
    }

    public void setCustom(boolean isCustom) {
      if ( this.isCustom != isCustom ) {
        this.isCustom = isCustom;
        save();
      }
    }
    public boolean isCustom() {
        return isCustom;
    }

    public void isCustomConnected() {
      this.customconnect = 0;
      for (int y=0; y<8; y++ ) {
        for (int x=0; x<8; x++ ) {
          int index = y*9+x;
          char ch = this.custom.charAt(index);
          if ( ch != '.' && ch != '0' ) {
            char cri = this.custom.charAt(index+1);
            if ( cri != '.' && cri != '0' && cri != ch ) {
              this.customconnect = 1;
            }
            char cbe = this.custom.charAt(index+9);
            if ( cbe != '.' && cbe != '0' && cbe != ch ) {
              this.customconnect = 1;
            }
          }
        }
      }
    }

    public void setCustom(String custom) {
      if ( this.custom == null || !(this.custom.equals(custom)) ) {
        this.custom = custom.replace( "0", ".");
       if ( this.isCustom != true ) {
        this.isCustom = true;
       }
        isCustomConnected();
        save();
      }
    }

    public String getCustom() {
        return custom;
    }

    public void setCount(int count) {
      if ( this.count != count ) {
        this.count = count;
        save();
      }
    }

    public int getCount() {
        return count;
    }

    public int getCustomConnect() {
        return customconnect;
    }

    public void setNumbers(boolean isNumbers) {
      if ( this.isNumbers != isNumbers ) {
        this.isNumbers = isNumbers;
        save();
      }
    }
    public boolean isNumbers() {
        return isNumbers;
    }

    public void setApply(int apply) {
      if ( this.apply != apply ) {
        this.apply = apply;
        save();
      }
    }

    public int getApply() {
        return apply;
    }

//  Load / Save

    private void init() {
        techniques = EnumSet.allOf(SolvingTechnique.class);
    }

    @SuppressWarnings("unchecked")
    public void load() {
        File file = new File(jsonFilename);
        if (file.exists() && file.length() == 0) {
        //  System.err.println(jsonFilename+" is corrupted, deleting...");
            file.delete();
        }
        LoadError = 0;
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(jsonFilename)) {
            Object obj = jsonParser.parse(reader);
            JSONArray jSettings = (JSONArray)obj;
            jSettings.forEach( Item -> {
                JSONObject stgObject = (JSONObject)Item;
                JSONObject stgDetails = (JSONObject)stgObject.get("Settings");
                String s = "";

                try {
                    s = (String)stgDetails.get("isRCNotation");
                    isRCNotation = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isAntialiasing");
                    isAntialiasing = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isShowingCandidates");
                    isShowingCandidates = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isShowingCandidateMasks");
                    isShowingCandidateMasks = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }

                try {
                    lookAndFeelClassName = (String)stgDetails.get("lookAndFeelClassName");
                }
                catch (NullPointerException e) { LoadError = 1;
                    lookAndFeelClassName = UIManager.getSystemLookAndFeelClassName();
                }
//              try {
//                  s = (String)stgDetails.get("iPuzzleFormat");
//                  iPuzzleFormat = s.charAt(0) - '0';
//              }
//              catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("iSaveFormat");
                    iSaveFormat = s.charAt(0) - '0';
                }
                catch (NullPointerException e) { LoadError = 1; }

                try {
                    s0 = (String)stgDetails.get("s0");
                    if ( s0 == null ) {
                        s0 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                        LoadError = 1;
                    }
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s1 = (String)stgDetails.get("s1");
                    if ( s1 == null ) {
                        s1 = "123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                        LoadError = 1;
                    }
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    sA = (String)stgDetails.get("sA");
                    if ( sA == null ) {
                        sA = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                        LoadError = 1;
                    }
                }
                catch (NullPointerException e) { LoadError = 1; }

                //generate dialog

                try {
                    s = (String)stgDetails.get("isVertical");
                    isVertical = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isHorizontal");
                    isHorizontal = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isDiagonal");
                    isDiagonal = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isAntiDiagonal");
                    isAntiDiagonal = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isBiDiagonal");
                    isBiDiagonal = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isOrthogonal");
                    isOrthogonal = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isRotational180");
                    isRotational180 = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isRotational90");
                    isRotational90 = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isNone");
                    isNone = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isFull");
                    isFull = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isEasy");
                    isEasy = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isMedium");
                    isMedium = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isHard");
                    isHard = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isFiendish");
                    isFiendish = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isDiabolical");
                    isDiabolical = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isExact");
                    isExact = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }

                // variants

                try {
                    s = (String)stgDetails.get("isRC33");
                    isRC33 = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isLatinSquare");
                    isLatinSquare = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isDiagonals");
                    isDiagonals = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isXDiagonal");
                    isXDiagonal = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isXAntiDiagonal");
                    isXAntiDiagonal = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isDisjointGroups");
                    isDisjointGroups = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isWindoku");
                    isWindoku = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isWindowsClosed");
                    isWindowsClosed = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isWindowsOpen");
                    isWindowsOpen = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isCustom");
                    isCustom = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    custom = (String)stgDetails.get("custom");
                    isCustomConnected();
                }
                catch (NullPointerException e) { ; }
                try {
                    s = (String)stgDetails.get("count");
                  if ( s.length() == 1 ) {
                    count = s.charAt(0) - '0';
                  }
                  if ( s.length() == 2 ) {
                    count = (s.charAt(0)-'0')*10 + s.charAt(1)-'0';
                  }
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("isNumbers");
                    isNumbers = s.equals("true")?true:false;
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    FontName = (String)stgDetails.get("FontName");
                }
                catch (NullPointerException e) { LoadError = 1; }
                try {
                    s = (String)stgDetails.get("apply");
                  if ( s.length() == 2 ) {
                    apply = (s.charAt(0)-'0')*10 + s.charAt(1)-'0';
                  }
                    if ( apply != 23 && apply != 28 ) {
                        apply = 23;
                    }
                }
                catch (NullPointerException e) { LoadError = 1; }

                try {
                    methods = (String)stgDetails.get("techniques");
                    if ( methods.length() == techniques.size() ) {
                        unpackmethods();
                    } else {
                        methods = null;     // causes reset!
                        LoadError = 1;      // forced update!
                    }
                }
                catch (NullPointerException e) { ; }
            });
            if ( LoadError == 1 ) {
                save();
            }
        } catch (FileNotFoundException e) {
        //  create new json file
            lookAndFeelClassName = UIManager.getSystemLookAndFeelClassName();
            save();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void saveChanged() {
        if ( isChanged == 1 ) {
            isChanged = 0;
            save();
        }
    }

    @SuppressWarnings("unchecked")
    public void save() {
      if ( !noSaves ) {
        JSONObject stgDetails = new JSONObject();
        stgDetails.put("isRCNotation", isRCNotation?"true":"false");
        stgDetails.put("isAntialiasing", isAntialiasing?"true":"false");
        stgDetails.put("isShowingCandidates", isShowingCandidates?"true":"false");
        stgDetails.put("isShowingCandidateMasks", isShowingCandidateMasks?"true":"false");
        stgDetails.put("lookAndFeelClassName", lookAndFeelClassName);
        stgDetails.put("iPuzzleFormat", ""+iPuzzleFormat);
        stgDetails.put("iGridSize", ""+iGridSize);
        stgDetails.put("iSaveFormat", ""+iSaveFormat);

        stgDetails.put("s0", s0);   // 0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ
        stgDetails.put("s1", s1);   // 123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ
        stgDetails.put("sA", sA);   // ABCDEFGHIJKLMNOPQRSTUVWXYZ

        // generate dialog

        stgDetails.put("isVertical", isVertical?"true":"false");
        stgDetails.put("isHorizontal", isHorizontal?"true":"false");
        stgDetails.put("isDiagonal", isDiagonal?"true":"false");
        stgDetails.put("isAntiDiagonal", isAntiDiagonal?"true":"false");
        stgDetails.put("isBiDiagonal", isBiDiagonal?"true":"false");
        stgDetails.put("isOrthogonal", isOrthogonal?"true":"false");
        stgDetails.put("isRotational180", isRotational180?"true":"false");
        stgDetails.put("isRotational90", isRotational90?"true":"false");
        stgDetails.put("isNone", isNone?"true":"false");
        stgDetails.put("isFull", isFull?"true":"false");
        stgDetails.put("isEasy", isEasy?"true":"false");
        stgDetails.put("isMedium", isMedium?"true":"false");
        stgDetails.put("isHard", isHard?"true":"false");
        stgDetails.put("isFiendish", isFiendish?"true":"false");
        stgDetails.put("isDiabolical", isDiabolical?"true":"false");
        stgDetails.put("isExact", isExact?"true":"false");

        // variants

        stgDetails.put("isRC33", isRC33?"true":"false");
        stgDetails.put("isLatinSquare", isLatinSquare?"true":"false");
        stgDetails.put("isDiagonals", isDiagonals?"true":"false");
        stgDetails.put("isXDiagonal", isXDiagonal?"true":"false");
        stgDetails.put("isXAntiDiagonal", isXAntiDiagonal?"true":"false");
        stgDetails.put("isDisjointGroups", isDisjointGroups?"true":"false");
        stgDetails.put("isWindoku", isWindoku?"true":"false");
        stgDetails.put("isWindowsClosed", isWindowsClosed?"true":"false");
        stgDetails.put("isWindowsOpen", isWindowsOpen?"true":"false");
        stgDetails.put("isCustom", isCustom?"true":"false");
        if ( custom != null ) {
            stgDetails.put("custom", custom);
        }
        stgDetails.put("count", ""+count);
        stgDetails.put("isNumbers", isNumbers?"true":"false");
        stgDetails.put("FontName", FontName);
        stgDetails.put("apply", ""+apply);

        if ( methods != null ) {
            stgDetails.put("techniques", methods);
        }

        JSONObject stgObject = new JSONObject();
        stgObject.put("Settings", stgDetails);

        JSONArray jSettings = new JSONArray();
        jSettings.add(stgObject);

        try (FileWriter file = new FileWriter(jsonFilename)) {
            file.write(jSettings.toJSONString());
            file.flush();

            if ( isChanged == 1 ) {
                isChanged = 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
      }
    }

}
