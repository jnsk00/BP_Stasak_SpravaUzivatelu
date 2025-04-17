package Performance;

public class AllPerformanceTestsRunner {

    public static void main(String[] args) throws Exception {
        System.out.println("📈 Spouštím výkonnostní testy...\n");

        // 1. Mockito vs JMockit
        System.out.println("▶️ Mockito vs JMockit");
        MockitoVsJMockitPerformanceTest.main(null);
        System.out.println();

        // 2. Selenium vs Playwright
        System.out.println("▶️ Selenium vs Playwright");
        SeleniumVsPlaywrightPerformanceTest.main(null);
        System.out.println();

        // 3. JUnit vs TestNG
        System.out.println("▶️ JUnit vs TestNG");
        JUnitVsTestNGPerformanceTest.main(null);
        System.out.println();

        System.out.println("✅ Všechny výkonnostní testy dokončeny.");
    }
}
