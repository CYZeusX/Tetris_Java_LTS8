import java.util.OptionalInt;
import java.util.stream.IntStream;

/**
 * Immutable-style game-state manager: score, level, lines, and timing.
 * <p>
 * Demonstrates Java 8 {@link OptionalInt} and method chaining.
 */
public class GameState {

    // Scoring table indexed by lines cleared in one drop (0-4)
    private static final int[] LINE_SCORE_TABLE = {0, 100, 300, 500, 800};

    // Gravity speeds per level (milliseconds per tick), floors at 100 ms
    private static final int BASE_INTERVAL  = 1000;
    private static final int INTERVAL_STEP  =   90;
    private static final int MIN_INTERVAL   =  100;

    private int score;
    private int level;
    private int linesCleared;
    private boolean gameOver;
    private boolean paused;

    // ---- Construction / reset ---------------------------------------------

    public GameState() { reset(); }

    public void reset() {
        score        = 0;
        level        = 1;
        linesCleared = 0;
        gameOver     = false;
        paused       = false;
    }

    // ---- State transitions ------------------------------------------------

    /**
     * Awards points for {@code lines} cleared simultaneously.
     * Uses an {@link OptionalInt} look-up to guard the score table safely.
     */
    public void addClearedLines(int lines) {
        if (lines <= 0) return;
        linesCleared += lines;

        // Java 8: OptionalInt prevents out-of-bounds without a try/catch
        OptionalInt tableScore = IntStream.of(LINE_SCORE_TABLE)
            .skip(Math.min(lines, LINE_SCORE_TABLE.length - 1))
            .findFirst();
        score += tableScore.orElse(LINE_SCORE_TABLE[LINE_SCORE_TABLE.length - 1]) * level;

        level = linesCleared / 10 + 1;
    }

    /** Milliseconds between automatic downward ticks. */
    public int getDropInterval() {
        return Math.max(MIN_INTERVAL, BASE_INTERVAL - (level - 1) * INTERVAL_STEP);
    }

    // ---- Accessors / mutators --------------------------------------------

    public int     getScore()        { return score;        }
    public int     getLevel()        { return level;        }
    public int     getLinesCleared() { return linesCleared; }
    public boolean isGameOver()      { return gameOver;     }
    public boolean isPaused()        { return paused;       }

    public void setGameOver(boolean gameOver) { this.gameOver = gameOver; }
    public void togglePause()                 { paused = !paused;         }
}
