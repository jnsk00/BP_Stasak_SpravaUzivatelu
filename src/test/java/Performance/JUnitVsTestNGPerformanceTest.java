package Performance;

import org.junit.platform.launcher.*;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.testng.TestNG;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

public class JUnitVsTestNGPerformanceTest {

    private static final int ITERATIONS = 5;
    private static final String RESULT_FILE = "target/performance-results/junit_vs_testng.txt";

    public static void main(String[] args) throws IOException {
        new File("target/performance-results").mkdirs();
        FileWriter writer = new FileWriter(RESULT_FILE);
        long junitTotal = 0;
        long testngTotal = 0;

        for (int i = 1; i <= ITERATIONS; i++) {
            writer.write("----- Iterace " + i + " -----\n");

            // === JUnit (přímo) ===
            long junitStart = System.nanoTime();
            runJUnitTests(JUnit.GUIControllerTest.class);
            long junitEnd = System.nanoTime();
            long junitDuration = (junitEnd - junitStart) / 1_000_000;
            junitTotal += junitDuration;
            writer.write("JUnit čas: " + junitDuration + " ms\n");

            // === TestNG ===
            long testngStart = System.nanoTime();
            TestNG testng = new TestNG();
            testng.setTestClasses(new Class[]{testng.GUIControllerTestNGTest.class});
            testng.setVerbose(0);
            testng.run();
            long testngEnd = System.nanoTime();
            long testngDuration = (testngEnd - testngStart) / 1_000_000;
            testngTotal += testngDuration;
            writer.write("TestNG čas: " + testngDuration + " ms\n\n");
        }

        writer.write("===== Průměrné časy =====\n");
        writer.write("JUnit avg: " + (junitTotal / ITERATIONS) + " ms\n");
        writer.write("TestNG avg: " + (testngTotal / ITERATIONS) + " ms\n");
        writer.close();

        System.out.println("✅ Výsledky uloženy do " + RESULT_FILE);
        System.exit(0);
    }

    private static void runJUnitTests(Class<?> testClass) {
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectClass(testClass))
                .build();

        Launcher launcher = LauncherFactory.create();
        launcher.execute(request);
    }
}
