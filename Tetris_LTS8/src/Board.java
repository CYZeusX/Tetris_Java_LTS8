import java.awt.Color;
import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * The 10×20 Tetris board.
 * <p>
 * Uses Java 8 Stream API and lambda expressions for collision detection
 * and line-clearing logic.
 */
public class Board {

    public static final int COLS = 10;
    public static final int ROWS = 20;

    // null = empty cell, non-null = locked piece colour
    private final Color[][] grid = new Color[ROWS][COLS];

    // ---- Collision detection ----------------------------------------------

    /**
     * Returns true when every filled cell of the piece maps to an in-bounds,
     * empty grid cell.  Uses nested IntStream + allMatch (Java 8 streams).
     */
    public boolean isValidPosition(Piece piece) {
        boolean[][] cells = piece.getCells();
        return IntStream.range(0, 4).allMatch(row ->
            IntStream.range(0, 4).allMatch(col -> {
                if (!cells[row][col]) return true;       // empty cell — always fine
                int gx = piece.getX() + col;
                int gy = piece.getY() + row;
                return gx >= 0 && gx < COLS             // within horizontal bounds
                    && gy >= 0 && gy < ROWS              // within vertical bounds
                    && grid[gy][gx] == null;             // cell is empty
            })
        );
    }

    // ---- Piece locking ----------------------------------------------------

    /** Stamps the piece's colour into the grid. */
    public void placePiece(Piece piece) {
        boolean[][] cells = piece.getCells();
        IntStream.range(0, 4).forEach(row ->
            IntStream.range(0, 4).forEach(col -> {
                if (cells[row][col]) {
                    int gx = piece.getX() + col;
                    int gy = piece.getY() + row;
                    if (gy >= 0 && gy < ROWS && gx >= 0 && gx < COLS) {
                        grid[gy][gx] = piece.getColor();
                    }
                }
            })
        );
    }

    // ---- Line clearing ----------------------------------------------------

    /**
     * Scans the board from bottom to top, removes full rows, and shifts the
     * remaining content down.  Returns the number of lines cleared.
     *
     * <p>Uses {@code Arrays.stream} and {@code allMatch} (Java 8 features)
     * to test row completeness.</p>
     */
    public int clearLines() {
        int cleared = 0;
        for (int row = ROWS - 1; row >= 0; row--) {
            if (isRowFull(row)) {
                removeRow(row);
                cleared++;
                row++;            // re-examine the same index after shift
            }
        }
        return cleared;
    }

    private boolean isRowFull(int row) {
        // Arrays.stream + allMatch — Java 8 idiom
        return Arrays.stream(grid[row]).allMatch(c -> c != null);
    }

    private void removeRow(int row) {
        // Shift everything above this row down by one
        for (int r = row; r > 0; r--) {
            grid[r] = Arrays.copyOf(grid[r - 1], COLS);
        }
        grid[0] = new Color[COLS];  // blank top row
    }

    // ---- Board inspection -------------------------------------------------

    public Color getCell(int row, int col) {
        return grid[row][col];
    }

    /** True if any cell in row 0 is occupied — game-over condition. */
    public boolean isOverflowing() {
        return Arrays.stream(grid[0]).anyMatch(c -> c != null);
    }

    public void reset() {
        IntStream.range(0, ROWS).forEach(r -> Arrays.fill(grid[r], null));
    }
}
