import java.util.Arrays;
import java.util.Random;

public enum Tetromino {
    I(new char[][]{{'I', 'I', 'I', 'I'}}),
    J(new char[][]{{'J', 'J', 'J'}, { 0 ,  0 , 'J'}}),
    L(new char[][]{{'L', 'L', 'L'}, {'L',  0 ,  0 }}),
    O(new char[][]{{'O', 'O'}, {'O', 'O'}}),
    S(new char[][]{{ 0 , 'S', 'S'}, {'S', 'S',  0 }}),
    T(new char[][]{{'T', 'T', 'T'}, { 0 , 'T',  0 }}),
    Z(new char[][]{{'Z', 'Z',  0 }, { 0 , 'Z', 'Z'}});

    private final char[][] shape;

    Tetromino(char[][] shape) {
        this.shape = shape;
    }

    public char[][] getShape() {
        return shape;
    }

    public char[][] rotateRight(char[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        char[][] rotated = new char[cols][rows];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                rotated[c][rows - 1 - r] = matrix[r][c];
            }
        }
        return rotated;
    }

    public static Tetromino getRandomPiece() {
        Random random = new Random();
        return values()[random.nextInt(values().length)];
    }
}