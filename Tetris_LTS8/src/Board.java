import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Board {
    public static final int WIDTH = 10;
    public static final int HEIGHT = 20;
    private char[][] grid;

    public Board() {
        grid = new char[HEIGHT][WIDTH];
        for (char[] row : grid) Arrays.fill(row, '.');
    }

    public boolean isValidPosition(char[][] shape, int x, int y) {
        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[0].length; c++) {
                if (shape[r][c] != 0) {
                    int boardX = x + c;
                    int boardY = y + r;
                    if (boardX < 0 || boardX >= WIDTH || boardY >= HEIGHT) return false;
                    if (boardY >= 0 && grid[boardY][boardX] != '.') return false;
                }
            }
        }
        return true;
    }

    public void placePiece(char[][] shape, int x, int y) {
        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[0].length; c++) {
                if (shape[r][c] != 0 && y + r >= 0) {
                    grid[y + r][x + c] = shape[r][c];
                }
            }
        }
    }

    public int clearLines() {
        List<char[]> validRows = Arrays.stream(grid)
                .filter(row -> IntStream.range(0, row.length).anyMatch(i -> row[i] == '.'))
                .collect(Collectors.toList());

        int linesCleared = HEIGHT - validRows.size();

        char[][] newGrid = new char[HEIGHT][WIDTH];
        for (char[] row : newGrid) Arrays.fill(row, '.');
        
        int offset = linesCleared;
        for (int i = 0; i < validRows.size(); i++) {
            System.arraycopy(validRows.get(i), 0, newGrid[i + offset], 0, WIDTH);
        }
        
        this.grid = newGrid;
        return linesCleared;
    }

    public void render(char[][] activeShape, int activeX, int activeY) {
        char[][] renderGrid = new char[HEIGHT][WIDTH];
        for (int i = 0; i < HEIGHT; i++) {
            System.arraycopy(grid[i], 0, renderGrid[i], 0, WIDTH);
        }

        if (activeShape != null) {
            for (int r = 0; r < activeShape.length; r++) {
                for (int c = 0; c < activeShape[0].length; c++) {
                    if (activeShape[r][c] != 0 && activeY + r >= 0) {
                        renderGrid[activeY + r][activeX + c] = activeShape[r][c];
                    }
                }
            }
        }

        System.out.println("----------");
        Arrays.stream(renderGrid)
                .map(String::new)
                .forEach(System.out::println);
        System.out.println("----------");
    }
}