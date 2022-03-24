# SudokuExplainer (SE)

## 1.2.1
Nicolas Juillerat's SudokuExplainer

## 1.2.1.3
gsf's (Glenn Fowler) serate modifications from: http://gsf.cococlyde.org/download/sudoku/serate.tgz used to rate sudoku puzzles for the Patterns Game: http://forum.enjoysudoku.com/patterns-game-t6290.html

The Java binary can be downloaded from: http://gsf.cococlyde.org/download/sudoku/SudokuExplainer.jar

## Usage - GUI
```
java.exe -jar SudokuExplainer.jar
```

## Usage - serate
```
java.exe -cp SudokuExplainer.jar diuf.sudoku.test.serate --input=puzzles.txt --output=output.txt
```
The default FORMAT is "%g ED=%r/%p/%d", so this does not have to be specified on the command line.

#### Comparison
Comparison of time taken to rate all sudokus posted in [Patterns Game 415](http://forum.enjoysudoku.com/post302459.html#p302459) (87 sudokus, including more's):
```
                        Version     Size            Rating Time         Commit

SudokuExplainer.jar   - 1.2.1.3   - 304,777 bytes - 11 m  2 s

SudokuExplainer.jar   - 2022.3.xx - 278,955 bytes -  4 m  8 s   028c7c2ebd43afe5781b18cfd1f2d7b4ba03acb7
```

