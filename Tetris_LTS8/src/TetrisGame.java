import javax.swing.*;
import java.awt.*;

/**
 * Application entry point.
 * <p>
 * Uses Java 8 method reference ({@code TetrisGame::new}) and
 * {@link SwingUtilities#invokeLater} for safe EDT construction.
 */
public class TetrisGame extends JFrame {

    // ---- Construction ----------------------------------------------------

    public TetrisGame() {
        super("Tetris — Java 8 Edition");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setBackground(new Color(10, 10, 20));

        // Build panels — GameState is shared so both panels read the same data
        GameState   state     = new GameState();
        SidePanel   side      = new SidePanel(state);
        GamePanel   game      = new GamePanel(side, state);

        // Layout: game board left, side info right
        JPanel root = new JPanel(new BorderLayout(2, 0));
        root.setBackground(new Color(10, 10, 20));
        root.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        root.add(game, BorderLayout.CENTER);
        root.add(side, BorderLayout.EAST);

        add(root);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        // Start after the window is on screen
        game.startGame();
    }

    // ---- Entry point -----------------------------------------------------

    /**
     * {@code TetrisGame::new} is a Java 8 constructor method-reference
     * passed to {@link SwingUtilities#invokeLater}.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(TetrisGame::new);   // Java 8 method reference
    }
}
