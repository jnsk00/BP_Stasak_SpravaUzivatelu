package Performance;

import org.junit.platform.launcher.*;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

public class SeleniumVsPlaywrightPerformanceTest {

    private static final int ITERATIONS = 5;
    private static final String RESULT_FILE = "target/performance-results/selenium_vs_playwright.txt";

    public static void main(String[] args) throws IOException {
        new File("target/performance-results").mkdirs();
        FileWriter writer = new FileWriter(RESULT_FILE);

        long totalSeleniumTime = 0;
        long totalPlaywrightTime = 0;

        for (int i = 1; i <= ITERATIONS; i++) {
            writer.write("----- Iterace " + i + " -----\n");

            // === Selenium ===
            long startSelenium = System.nanoTime();
            runJUnitTests(Selenium.SeleniumTest.class);
            long endSelenium = System.nanoTime();
            long durationSelenium = (endSelenium - startSelenium) / 1_000_000;
            totalSeleniumTime += durationSelenium;
            writer.write("Selenium: " + durationSelenium + " ms\n");

            // === Playwright ===
            long startPlaywright = System.nanoTime();
            runJUnitTests(Playwright.PlaywrightTest.class);
            long endPlaywright = System.nanoTime();
            long durationPlaywright = (endPlaywright - startPlaywright) / 1_000_000;
            totalPlaywrightTime += durationPlaywright;
            writer.write("Playwright: " + durationPlaywright + " ms\n\n");
        }

        writer.write("===== Průměrné časy =====\n");
        writer.write("Selenium avg: " + (totalSeleniumTime / ITERATIONS) + " ms\n");
        writer.write("Playwright avg: " + (totalPlaywrightTime / ITERATIONS) + " ms\n");
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
