import java.awt.Color;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Represents the seven Tetromino piece types.
 * Uses Java 8 EnumMap, lambdas, and method references.
 */
public enum Tetromino {

    // ---- Piece definitions -----------------------------------------------
    I, O, T, S, Z, J, L;

    // Shapes[pieceOrdinal][rotation][row] = int[4] bitmask per row
    // Each shape fits in a 4x4 bounding box; 1 = filled cell
    private static final int[][][] SHAPES_I = {
        {0b0000, 0b1111, 0b0000, 0b0000},   // rot 0 ─ horizontal
        {0b0010, 0b0010, 0b0010, 0b0010},   // rot 1 │ vertical
        {0b0000, 0b0000, 0b1111, 0b0000},   // rot 2 ─
        {0b0100, 0b0100, 0b0100, 0b0100}    // rot 3 │
    };
    private static final int[][][] SHAPES_O = {
        {0b0110, 0b0110, 0b0000, 0b0000},
        {0b0110, 0b0110, 0b0000, 0b0000},
        {0b0110, 0b0110, 0b0000, 0b0000},
        {0b0110, 0b0110, 0b0000, 0b0000}
    };
    private static final int[][][] SHAPES_T = {
        {0b0100, 0b1110, 0b0000, 0b0000},
        {0b0100, 0b0110, 0b0100, 0b0000},
        {0b0000, 0b1110, 0b0100, 0b0000},
        {0b0100, 0b1100, 0b0100, 0b0000}
    };
    private static final int[][][] SHAPES_S = {
        {0b0110, 0b1100, 0b0000, 0b0000},
        {0b1000, 0b1100, 0b0100, 0b0000},
        {0b0000, 0b0110, 0b1100, 0b0000},
        {0b1000, 0b1100, 0b0100, 0b0000}
    };
    private static final int[][][] SHAPES_Z = {
        {0b1100, 0b0110, 0b0000, 0b0000},
        {0b0100, 0b1100, 0b1000, 0b0000},
        {0b0000, 0b1100, 0b0110, 0b0000},
        {0b0100, 0b1100, 0b1000, 0b0000}
    };
    private static final int[][][] SHAPES_J = {
        {0b1000, 0b1110, 0b0000, 0b0000},
        {0b1100, 0b1000, 0b1000, 0b0000},
        {0b0000, 0b1110, 0b0010, 0b0000},
        {0b0100, 0b0100, 0b1100, 0b0000}
    };
    private static final int[][][] SHAPES_L = {
        {0b0010, 0b1110, 0b0000, 0b0000},
        {0b1000, 0b1000, 0b1100, 0b0000},
        {0b0000, 0b1110, 0b1000, 0b0000},
        {0b1100, 0b0100, 0b0100, 0b0000}
    };

    // Lookup table: ordinal → shape array (4 rotations × 4 rows)
    private static final int[][][][] ALL_SHAPES = {
        SHAPES_I, SHAPES_O, SHAPES_T, SHAPES_S, SHAPES_Z, SHAPES_J, SHAPES_L
    };

    // Colors stored in an EnumMap (Java 8 idiomatic enum usage)
    private static final Map<Tetromino, Color> COLORS = new EnumMap<>(Tetromino.class);

    static {
        COLORS.put(I, new Color(0,   240, 240));
        COLORS.put(O, new Color(240, 240,   0));
        COLORS.put(T, new Color(160,   0, 240));
        COLORS.put(S, new Color(0,   240,   0));
        COLORS.put(Z, new Color(240,   0,   0));
        COLORS.put(J, new Color(0,     0, 240));
        COLORS.put(L, new Color(240, 160,   0));
    }

    private static final Random RANDOM = new Random();

    // ---- Public API -------------------------------------------------------

    /** Returns the 4-row bitmask for this piece at the given rotation. */
    public int[] getRows(int rotation) {
        return ALL_SHAPES[ordinal()][rotation % 4];
    }

    /** Returns a boolean[4][4] grid: true = occupied cell. */
    public boolean[][] getCells(int rotation) {
        int[] rows = getRows(rotation);
        boolean[][] cells = new boolean[4][4];
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                // Bit 3 is the leftmost column (col 0)
                cells[r][c] = ((rows[r] >> (3 - c)) & 1) == 1;
            }
        }
        return cells;
    }

    /** Color associated with this piece type. */
    public Color getColor() {
        return COLORS.get(this);
    }

    /**
     * A Java 8 {@link Supplier} that yields a random Tetromino.
     * Usage:  Tetromino.RANDOM_SUPPLIER.get()
     */
    public static final Supplier<Tetromino> RANDOM_SUPPLIER =
        () -> values()[RANDOM.nextInt(values().length)];
}
