package Selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SeleniumTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private static String testUsername;
    private final String baseUrl = "http://localhost:8080";

    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
        testUsername = "testUser_" + UUID.randomUUID();
    }

    @BeforeEach
    void setup() {
        driver = new ChromeDriver();

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.get(baseUrl + "/index.html");

        driver.manage().deleteAllCookies();
        driver.navigate().refresh();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")));

    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @Order(1)
    void testRegisterUser() {
        driver.findElement(By.id("username")).sendKeys(testUsername);
        driver.findElement(By.id("password")).sendKeys("testPass123");
        driver.findElement(By.className("register-btn")).click();


        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            String alertText = alert.getText();
            alert.accept();
            assertEquals("Uživatel zaregistrován!", alertText, "Nečekaný alert!");
        } catch (TimeoutException | NoAlertPresentException e) {
            fail("❌ Alert se neobjevil po registraci! Pravděpodobně došlo k chybě.");
        }
    }

        @Test
    @Order(2)
    void testLogin() {

        driver.findElement(By.id("loginUsername")).sendKeys(testUsername);
        driver.findElement(By.id("loginPassword")).sendKeys("testPass123");
        driver.findElement(By.id("loginButton")).click();

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            boolean loginSuccess = wait.until(ExpectedConditions.urlContains("user-panel.html"));
            assertTrue(loginSuccess, "Nejsme na přihlašovací stránce!");

        }

    @Test
    @Order(3)
    void testUpdateUser() throws InterruptedException {
        testLogin();

        driver.findElement(By.id("newUsername")).sendKeys("updatedUser");
        driver.findElement(By.id("newPassword")).sendKeys("newPass123");
        driver.findElement(By.xpath("//button[text()='Uložit změny']")).click();

        Thread.sleep(500);

    // Poté čekání na alert
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            System.out.println("✅ Alert text: " + alert.getText());
            alert.accept();
        } catch (TimeoutException e) {
            System.out.println("ALERT SE NEZOBRAZIL!");
        }
    }


    @Test
    @Order(4)
    void testDeleteUser() {
        if (wait == null) {
            throw new IllegalStateException("WebDriverWait nebyl správně inicializován!");
        }

        driver.findElement(By.id("loginUsername")).sendKeys("admin");
        driver.findElement(By.id("loginPassword")).sendKeys("admin123");
        driver.findElement(By.id("loginButton")).click();


        wait.until(ExpectedConditions.urlContains("/admin-panel"));

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userList")));

        List<WebElement> rows = driver.findElements(By.cssSelector("#userList tr"));
        boolean userFound = false;

        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));

            if (!cells.isEmpty() && cells.get(0).getText().equals("updatedUser")) {
                userFound = true;


                WebElement deleteButton = row.findElement(By.xpath(".//button[contains(text(),'Smazat')]"));
                deleteButton.click();


                try {
                    Alert alert = wait.until(ExpectedConditions.alertIsPresent());
                    alert.accept(); // Potvrď smazání
                } catch (Exception e) {
                    System.out.println("Žádný alert k potvrzení.");
                }

                // ✅ Počkej na opětovné načtení seznamu uživatelů
                wait.until(ExpectedConditions.stalenessOf(row));
                break;
            }
        }


        List<WebElement> updatedRows = driver.findElements(By.cssSelector("#userList tr"));
        boolean userStillExists = updatedRows.stream()
                .anyMatch(r -> r.findElements(By.tagName("td")).get(0).getText().equals("updatedUser"));

        assertFalse(userStillExists, "Uživatel 'updatedUser' stále existuje, i když měl být smazán!");
    }
}
