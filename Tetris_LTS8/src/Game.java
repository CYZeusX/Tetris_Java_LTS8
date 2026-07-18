import java.util.Optional;
import java.util.Random;
import java.util.Scanner;

public class Game {
    private final Board board;
    private final boolean headless;
    private Optional<char[][]> currentPiece;
    private Tetromino currentTetromino;
    private int x, y;
    private boolean isGameOver;
    private final Random random;

    public Game(boolean headless) {
        this.board = new Board();
        this.headless = headless;
        this.currentPiece = Optional.empty();
        this.random = new Random();
    }

    public void play() {
        Scanner scanner = headless ? null : new Scanner(System.in);
        spawnPiece();

        while (!isGameOver) {
            if (!headless) {
                board.render(currentPiece.orElse(null), x, y);
                System.out.print("Enter move (W=Rotate, A=Left, S=Down, D=Right): ");
                String input = scanner.hasNextLine() ? scanner.nextLine().toUpperCase() : "";
                processInput(input);
            } else {
                simulateHeadlessMove();
            }
        }

        if (!headless) {
            board.render(null, 0, 0);
            System.out.println("Game Over!");
        }
    }

    private void spawnPiece() {
        currentTetromino = Tetromino.getRandomPiece();
        currentPiece = Optional.of(currentTetromino.getShape());
        x = Board.WIDTH / 2 - currentPiece.get()[0].length / 2;
        y = 0;

        if (!board.isValidPosition(currentPiece.get(), x, y)) {
            isGameOver = true;
        }
    }

    private void processInput(String input) {
        if (!currentPiece.isPresent()) return;

        char[][] shape = currentPiece.get();
        if (input.contains("W")) {
            char[][] rotated = currentTetromino.rotateRight(shape);
            if (board.isValidPosition(rotated, x, y)) currentPiece = Optional.of(rotated);
        }
        if (input.contains("A") && board.isValidPosition(shape, x - 1, y)) x--;
        if (input.contains("D") && board.isValidPosition(shape, x + 1, y)) x++;
        
        // S acts as soft drop (moves down 1 unit) or gravity occurs natively
        if (board.isValidPosition(currentPiece.get(), x, y + 1)) {
            y++;
        } else {
            lockPiece();
        }
    }

    private void simulateHeadlessMove() {
        int moveType = random.nextInt(4);
        if (moveType == 0) processInput("W");
        else if (moveType == 1) processInput("A");
        else if (moveType == 2) processInput("D");
        
        while (board.isValidPosition(currentPiece.get(), x, y + 1)) {
            y++;
        }
        lockPiece();
    }

    private void lockPiece() {
        currentPiece.ifPresent(shape -> {
            board.placePiece(shape, x, y);
            board.clearLines();
            spawnPiece();
        });
    }
}