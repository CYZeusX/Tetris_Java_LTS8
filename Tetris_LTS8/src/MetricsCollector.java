import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.time.Duration;
import java.time.Instant;

public class MetricsCollector {
    private Instant startTime;
    private Instant endTime;

    public void start() {
        this.startTime = Instant.now();
    }

    public void stop() {
        this.endTime = Instant.now();
    }

    public String getCsvMetrics() {
        long durationMs = Duration.between(startTime, endTime).toMillis();
        Runtime runtime = Runtime.getRuntime();
        long usedMemoryKb = (runtime.totalMemory() - runtime.freeMemory()) / 1024;
        long maxMemoryKb = runtime.maxMemory() / 1024;
        
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        double cpuLoad = osBean.getSystemLoadAverage();

        return String.format("%d,%d,%d,%.2f", durationMs, usedMemoryKb, maxMemoryKb, cpuLoad);
    }
}