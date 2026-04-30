import javax.swing.*;
import java.awt.*;
import java.util.Optional;

/**
 * Side panel that displays the next piece preview, score, level, and
 * lines-cleared counter.
 * <p>
 * Uses Java 8 {@link Optional} to handle the (nullable) next piece safely.
 */
public class SidePanel extends JPanel {

    private static final int CELL_SIZE    = 28;
    private static final int PREVIEW_SIZE = 4 * CELL_SIZE + 20;

    // Java 8: Optional wraps the nullable next-piece reference
    private Optional<Tetromino> nextPiece = Optional.empty();
    private GameState gameState;

    // ---- Colours & fonts --------------------------------------------------
    private static final Color BG_COLOR       = new Color(18,  18,  30);
    private static final Color PANEL_COLOR    = new Color(30,  30,  50);
    private static final Color BORDER_COLOR   = new Color(80,  80, 120);
    private static final Color LABEL_COLOR    = new Color(160, 160, 200);
    private static final Color VALUE_COLOR    = new Color(240, 240, 255);
    private static final Font  LABEL_FONT     = new Font("Monospaced", Font.PLAIN, 13);
    private static final Font  VALUE_FONT     = new Font("Monospaced", Font.BOLD,  18);
    private static final Font  TITLE_FONT     = new Font("Monospaced", Font.BOLD,  22);

    public SidePanel(GameState gameState) {
        this.gameState = gameState;
        setPreferredSize(new Dimension(160, Board.ROWS * CELL_SIZE));
        setBackground(BG_COLOR);
    }

    // ---- Public API -------------------------------------------------------

    public void setNextPiece(Tetromino piece) {
        this.nextPiece = Optional.ofNullable(piece);
        repaint();
    }

    // ---- Rendering --------------------------------------------------------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int panelW = getWidth();
        int y      = 20;

        // ── Title ──
        g2.setFont(TITLE_FONT);
        g2.setColor(new Color(120, 200, 255));
        String title = "TETRIS";
        int titleX = (panelW - g2.getFontMetrics().stringWidth(title)) / 2;
        g2.drawString(title, titleX, y + 20);
        y += 50;

        // ── Next piece box ──
        drawSection(g2, "NEXT", y, panelW);
        y += 25;
        drawNextPiece(g2, y, panelW);
        y += PREVIEW_SIZE + 15;

        // ── Score ──
        drawStat(g2, "SCORE", String.valueOf(gameState.getScore()), y, panelW);
        y += 60;

        // ── Level ──
        drawStat(g2, "LEVEL", String.valueOf(gameState.getLevel()), y, panelW);
        y += 60;

        // ── Lines ──
        drawStat(g2, "LINES", String.valueOf(gameState.getLinesCleared()), y, panelW);
        y += 80;

        // ── Controls hint ──
        drawControls(g2, y, panelW);
    }

    private void drawSection(Graphics2D g, String label, int y, int panelW) {
        g.setFont(LABEL_FONT);
        g.setColor(LABEL_COLOR);
        int lx = (panelW - g.getFontMetrics().stringWidth(label)) / 2;
        g.drawString(label, lx, y);
    }

    private void drawNextPiece(Graphics2D g, int startY, int panelW) {
        int boxX = (panelW - PREVIEW_SIZE) / 2;
        int boxY = startY;

        // Background box
        g.setColor(PANEL_COLOR);
        g.fillRoundRect(boxX, boxY, PREVIEW_SIZE, PREVIEW_SIZE, 10, 10);
        g.setColor(BORDER_COLOR);
        g.drawRoundRect(boxX, boxY, PREVIEW_SIZE, PREVIEW_SIZE, 10, 10);

        // Java 8: Optional.ifPresent — render only when a piece exists
        nextPiece.ifPresent(piece -> {
            boolean[][] cells = piece.getCells(0);
            Color color       = piece.getColor();
            int   offsetX     = boxX + 10;
            int   offsetY     = boxY + 10;

            for (int r = 0; r < 4; r++) {
                for (int c = 0; c < 4; c++) {
                    if (cells[r][c]) {
                        drawCell(g, offsetX + c * CELL_SIZE, offsetY + r * CELL_SIZE,
                                 CELL_SIZE - 1, color);
                    }
                }
            }
        });
    }

    private void drawStat(Graphics2D g, String label, String value, int y, int panelW) {
        // Label
        g.setFont(LABEL_FONT);
        g.setColor(LABEL_COLOR);
        int lx = (panelW - g.getFontMetrics().stringWidth(label)) / 2;
        g.drawString(label, lx, y);

        // Value
        g.setFont(VALUE_FONT);
        g.setColor(VALUE_COLOR);
        int vx = (panelW - g.getFontMetrics().stringWidth(value)) / 2;
        g.drawString(value, vx, y + 22);
    }

    private void drawControls(Graphics2D g, int y, int panelW) {
        g.setFont(new Font("Monospaced", Font.PLAIN, 10));
        g.setColor(new Color(100, 100, 140));
        String[] lines = {"← → Move", "↑ Rotate", "↓ Soft drop",
                          "Space Hard drop", "P  Pause"};
        for (String line : lines) {
            int lx = (panelW - g.getFontMetrics().stringWidth(line)) / 2;
            g.drawString(line, lx, y);
            y += 14;
        }
    }

    private void drawCell(Graphics2D g, int x, int y, int size, Color color) {
        g.setColor(color);
        g.fillRect(x, y, size, size);
        // Highlight
        g.setColor(color.brighter().brighter());
        g.fillRect(x, y, size, 3);
        g.fillRect(x, y, 3, size);
        // Shadow
        g.setColor(color.darker().darker());
        g.fillRect(x, y + size - 3, size, 3);
        g.fillRect(x + size - 3, y, 3, size);
    }
}
