package Performance;

import org.junit.platform.launcher.*;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

public class MockitoVsJMockitPerformanceTest {

    private static final int ITERATIONS = 5;
    private static final String RESULT_FILE = "target/performance-results/mockito_vs_jmockit_clean.txt";

    public static void main(String[] args) throws IOException {
        new File("target/performance-results").mkdirs();
        FileWriter writer = new FileWriter(RESULT_FILE);

        long totalMockito = 0;
        long totalJMockit = 0;

        for (int i = 1; i <= ITERATIONS; i++) {
            writer.write("----- Iterace " + i + " -----\n");

            // === Mockito ===
            long startMockito = System.nanoTime();
            runJUnitTests(Mockito.UserControllerMockitoTest.class);
            long endMockito = System.nanoTime();
            long mockitoTime = (endMockito - startMockito) / 1_000_000;
            totalMockito += mockitoTime;
            writer.write("Mockito čas: " + mockitoTime + " ms\n");

            // === JMockit ===
            long startJMockit = System.nanoTime();
            runJUnitTests(JMockit.UserControllerJMockitTest.class);
            long endJMockit = System.nanoTime();
            long jmockitTime = (endJMockit - startJMockit) / 1_000_000;
            totalJMockit += jmockitTime;
            writer.write("JMockit čas: " + jmockitTime + " ms\n\n");
        }

        writer.write("===== Průměrné časy =====\n");
        writer.write("Mockito avg: " + (totalMockito / ITERATIONS) + " ms\n");
        writer.write("JMockit avg: " + (totalJMockit / ITERATIONS) + " ms\n");
        writer.close();

        System.out.println("✅ Hotovo – výsledky uloženy do " + RESULT_FILE);
    }

    private static void runJUnitTests(Class<?> testClass) {
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectClass(testClass))
                .build();

        Launcher launcher = LauncherFactory.create();
        launcher.execute(request);
    }
}
