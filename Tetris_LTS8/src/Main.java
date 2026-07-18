import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        boolean headless = Arrays.asList(args).contains("--headless");

        MetricsCollector metrics = new MetricsCollector();
        metrics.start();

        Game game = new Game(headless);
        game.play();

        metrics.stop();

        if (headless) {
            System.out.println(metrics.getCsvMetrics());
        } else {
            System.out.println("Execution Metrics (CSV):");
            System.out.println("Duration(ms),UsedMem(KB),MaxMem(KB),CPULoad");
            System.out.println(metrics.getCsvMetrics());
        }
    }
}