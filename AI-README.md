# Tetris — Java 8 Edition

A fully-featured Tetris game built with **Java 8 LTS** and **Java Swing**.

## Java 8 Features Used

| Feature | Where |
|---|---|
| Lambda expressions | `Timer`, `KeyAdapter`, `Stream` operations |
| Stream API | `Board.clearLines()`, `Board.isValidPosition()` — `IntStream`, `Arrays.stream`, `allMatch`, `anyMatch` |
| Optional / OptionalInt | `SidePanel.nextPiece`, `GameState.addClearedLines()` |
| EnumMap | `Tetromino.COLORS` — O(1) colour lookup by enum key |
| Functional interfaces | `Consumer<Integer>` key-dispatch in `GamePanel`, `Supplier<Tetromino>` random generator |
| Method references | `SwingUtilities::invokeLater`, `TetrisGame::new` |

## OOP Design

```
TetrisGame (JFrame)
├── GamePanel (JPanel) ── game loop, input, rendering
│   ├── Board            ── 10×20 grid model
│   ├── GameState        ── score / level / lines
│   └── Piece            ── active falling piece
│       └── Tetromino    ── enum: shape data + colour
└── SidePanel (JPanel)  ── next piece preview + HUD
```

## Build & Run

### Requirements
- JDK 8 or higher
- No external dependencies

### Compile
```bash
cd src
javac *.java
```

### Run
```bash
java TetrisGame
```

### One-liner (from the `src/` folder)
```bash
javac *.java && java TetrisGame
```

## Controls

| Key | Action |
|---|---|
| `←` / `→` | Move left / right |
| `↑` | Rotate clockwise (with wall-kick) |
| `↓` | Soft drop |
| `Space` | Hard drop |
| `P` | Pause / Resume |
| `Enter` | Restart (after game over) |

## Scoring

| Lines cleared | Points × level |
|---|---|
| 1 line  | 100 |
| 2 lines | 300 |
| 3 lines | 500 |
| 4 lines | 800 |

Speed increases every 10 lines (10 levels total, minimum 100 ms/tick).
