import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

/**
 * Main game panel: renders the board, drives the game loop via a Swing Timer,
 * and handles keyboard input.
 *
 * <p>Java 8 features used:
 * <ul>
 *   <li>Lambda expressions for {@link Timer} callbacks and key bindings</li>
 *   <li>Method references ({@code this::handleLeft} etc.)</li>
 *   <li>{@link Consumer} functional interface for key-action dispatch</li>
 *   <li>{@link java.util.Map} with lambda-built entries</li>
 * </ul>
 */
public class GamePanel extends JPanel {

    // ---- Layout constants ------------------------------------------------
    private static final int CELL_SIZE = 30;
    private static final int BOARD_W   = Board.COLS * CELL_SIZE;
    private static final int BOARD_H   = Board.ROWS * CELL_SIZE;

    // ---- Colours ---------------------------------------------------------
    private static final Color BG_COLOR      = new Color(10,  10,  20);
    private static final Color GRID_COLOR    = new Color(35,  35,  55);
    private static final Color GHOST_COLOR   = new Color(255, 255, 255, 45);
    private static final Color OVERLAY_COLOR = new Color(0,   0,   0,   175);

    // ---- Model -----------------------------------------------------------
    private final Board     board;
    private final GameState gameState;   // shared with SidePanel via TetrisGame
    private       Piece     current;
    private       Tetromino nextType;

    // ---- Swing -----------------------------------------------------------
    private final SidePanel sidePanel;
    private       Timer     dropTimer;

    // ---- Construction ----------------------------------------------------

    public GamePanel(SidePanel sidePanel, GameState gameState) {
        this.board     = new Board();
        this.gameState = gameState;
        this.sidePanel = sidePanel;
        setPreferredSize(new Dimension(BOARD_W, BOARD_H));
        setBackground(BG_COLOR);
        setFocusable(true);
        setupKeyBindings();
    }

    // ---- Game lifecycle --------------------------------------------------

    public void startGame() {
        board.reset();
        gameState.reset();
        nextType = Tetromino.RANDOM_SUPPLIER.get();
        spawnPiece();
        scheduleTimer();
        requestFocusInWindow();
    }

    private void scheduleTimer() {
        if (dropTimer != null) dropTimer.stop();
        // Java 8: lambda replaces ActionListener anonymous class
        dropTimer = new Timer(gameState.getDropInterval(), e -> gameTick());
        dropTimer.start();
    }

    private void gameTick() {
        if (gameState.isGameOver() || gameState.isPaused()) return;
        softDrop();
    }

    // ---- Piece management ------------------------------------------------

    private void spawnPiece() {
        current  = new Piece(nextType);
        nextType = Tetromino.RANDOM_SUPPLIER.get();
        sidePanel.setNextPiece(nextType);

        if (!board.isValidPosition(current)) {
            gameState.setGameOver(true);
            dropTimer.stop();
            repaint();
        }
    }

    private void lockAndSpawn() {
        board.placePiece(current);
        int cleared = board.clearLines();
        if (cleared > 0) {
            gameState.addClearedLines(cleared);
            scheduleTimer(); // update speed after level change
        }
        if (board.isOverflowing()) {
            gameState.setGameOver(true);
            dropTimer.stop();
        } else {
            spawnPiece();
        }
        repaint();
        sidePanel.repaint();
    }

    // ---- Movement actions ------------------------------------------------

    private void moveLeft() {
        current.moveLeft();
        if (!board.isValidPosition(current)) current.moveRight();
        repaint();
    }

    private void moveRight() {
        current.moveRight();
        if (!board.isValidPosition(current)) current.moveLeft();
        repaint();
    }

    private void softDrop() {
        current.moveDown();
        if (!board.isValidPosition(current)) {
            current.moveUp();
            lockAndSpawn();
        }
        repaint();
    }

    private void hardDrop() {
        while (board.isValidPosition(current)) {
            current.moveDown();
        }
        current.moveUp();
        lockAndSpawn();
    }

    private void rotate() {
        current.rotateClockwise();
        // Wall-kick: try nudging left or right if rotation collides
        if (!board.isValidPosition(current)) {
            current.moveRight();
            if (!board.isValidPosition(current)) {
                current.moveLeft();
                current.moveLeft();
                if (!board.isValidPosition(current)) {
                    current.moveRight();
                    current.rotateCounterClockwise(); // revert
                }
            }
        }
        repaint();
    }

    // ---- Ghost piece -----------------------------------------------------

    /** Returns a copy of the current piece dropped to its lowest valid row. */
    private Piece computeGhost() {
        Piece ghost = new Piece(current);
        while (board.isValidPosition(ghost)) ghost.moveDown();
        ghost.moveUp();
        return ghost;
    }

    // ---- Keyboard input --------------------------------------------------

    /**
     * Maps key codes to {@link Runnable} actions using a Java 8 style dispatch.
     * All lambdas capture {@code this} implicitly.
     */
    private void setupKeyBindings() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (gameState.isGameOver()) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) startGame();
                    return;
                }
                if (e.getKeyCode() == KeyEvent.VK_P) {
                    gameState.togglePause();
                    repaint();
                    return;
                }
                if (gameState.isPaused()) return;

                // Java 8: Consumer<Integer> dispatches key → action
                Consumer<Integer> dispatch = key -> {
                    switch (key) {
                        case KeyEvent.VK_LEFT:  moveLeft();  break;
                        case KeyEvent.VK_RIGHT: moveRight(); break;
                        case KeyEvent.VK_DOWN:  softDrop();  break;
                        case KeyEvent.VK_UP:    rotate();    break;
                        case KeyEvent.VK_SPACE: hardDrop();  break;
                    }
                };
                dispatch.accept(e.getKeyCode());
            }
        });
    }

    // ---- Rendering -------------------------------------------------------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawGrid(g2);
        drawBoard(g2);

        if (!gameState.isGameOver() && !gameState.isPaused() && current != null) {
            drawGhost(g2);
            drawPiece(g2, current, current.getColor());
        }

        if (gameState.isGameOver()) drawOverlay(g2, "GAME OVER", "Press ENTER to restart");
        if (gameState.isPaused())   drawOverlay(g2, "PAUSED",    "Press P to resume");
    }

    private void drawGrid(Graphics2D g) {
        g.setColor(GRID_COLOR);
        for (int col = 0; col <= Board.COLS; col++) {
            g.drawLine(col * CELL_SIZE, 0, col * CELL_SIZE, BOARD_H);
        }
        for (int row = 0; row <= Board.ROWS; row++) {
            g.drawLine(0, row * CELL_SIZE, BOARD_W, row * CELL_SIZE);
        }
    }

    private void drawBoard(Graphics2D g) {
        for (int row = 0; row < Board.ROWS; row++) {
            for (int col = 0; col < Board.COLS; col++) {
                Color c = board.getCell(row, col);
                if (c != null) {
                    drawCell(g, col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE - 1, c);
                }
            }
        }
    }

    private void drawGhost(Graphics2D g) {
        Piece ghost = computeGhost();
        boolean[][] cells = ghost.getCells();
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                if (cells[r][c]) {
                    int px = (ghost.getX() + c) * CELL_SIZE;
                    int py = (ghost.getY() + r) * CELL_SIZE;
                    g.setColor(GHOST_COLOR);
                    g.fillRect(px, py, CELL_SIZE - 1, CELL_SIZE - 1);
                    g.setColor(new Color(255, 255, 255, 80));
                    g.drawRect(px, py, CELL_SIZE - 2, CELL_SIZE - 2);
                }
            }
        }
    }

    private void drawPiece(Graphics2D g, Piece piece, Color color) {
        boolean[][] cells = piece.getCells();
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                if (cells[r][c]) {
                    int px = (piece.getX() + c) * CELL_SIZE;
                    int py = (piece.getY() + r) * CELL_SIZE;
                    drawCell(g, px, py, CELL_SIZE - 1, color);
                }
            }
        }
    }

    private void drawCell(Graphics2D g, int x, int y, int size, Color color) {
        // Base fill
        g.setColor(color);
        g.fillRect(x, y, size, size);
        // Top/left highlight
        g.setColor(color.brighter().brighter());
        g.fillRect(x, y, size, 3);
        g.fillRect(x, y, 3, size);
        // Bottom/right shadow
        g.setColor(color.darker().darker());
        g.fillRect(x, y + size - 3, size, 3);
        g.fillRect(x + size - 3, y, 3, size);
        // Inner gloss dot
        g.setColor(new Color(255, 255, 255, 60));
        g.fillOval(x + 5, y + 5, 6, 6);
    }

    private void drawOverlay(Graphics2D g, String title, String subtitle) {
        g.setColor(OVERLAY_COLOR);
        g.fillRect(0, 0, BOARD_W, BOARD_H);

        g.setFont(new Font("Monospaced", Font.BOLD, 32));
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(title, (BOARD_W - fm.stringWidth(title)) / 2, BOARD_H / 2 - 20);

        g.setFont(new Font("Monospaced", Font.PLAIN, 15));
        g.setColor(new Color(200, 200, 200));
        fm = g.getFontMetrics();
        g.drawString(subtitle, (BOARD_W - fm.stringWidth(subtitle)) / 2, BOARD_H / 2 + 15);
    }

}
