import java.awt.Color;

/**
 * Represents the currently active falling piece.
 * Encapsulates type, grid position, and rotation state.
 */
public class Piece {

    private final Tetromino type;
    private int x;        // column offset on the board
    private int y;        // row offset on the board
    private int rotation; // 0-3

    // ---- Construction -----------------------------------------------------

    public Piece(Tetromino type) {
        this.type     = type;
        this.rotation = 0;
        this.x        = Board.COLS / 2 - 2;  // centre horizontally
        this.y        = 0;
    }

    /** Deep-copy constructor — useful for ghost-piece rendering. */
    public Piece(Piece other) {
        this.type     = other.type;
        this.x        = other.x;
        this.y        = other.y;
        this.rotation = other.rotation;
    }

    // ---- Mutation (returns this for fluent chaining) ----------------------

    public Piece moveLeft()  { x--; return this; }
    public Piece moveRight() { x++; return this; }
    public Piece moveDown()  { y++; return this; }
    public Piece moveUp()    { y--; return this; }  // rollback

    public Piece rotateClockwise()        { rotation = (rotation + 1) % 4; return this; }
    public Piece rotateCounterClockwise() { rotation = (rotation + 3) % 4; return this; }

    // ---- Accessors --------------------------------------------------------

    public Tetromino getType()     { return type; }
    public int       getX()        { return x; }
    public int       getY()        { return y; }
    public int       getRotation() { return rotation; }
    public Color     getColor()    { return type.getColor(); }

    /** 4×4 boolean grid for the current rotation. */
    public boolean[][] getCells() {
        return type.getCells(rotation);
    }
}
