package Selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SeleniumTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private String testUsername;
    private String baseUrl = "http://localhost:8080";

    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setup() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("registerUsername")));
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.get(baseUrl + "/index.html");
        testUsername = "testUser_" + UUID.randomUUID();
        driver.manage().deleteAllCookies();
        driver.navigate().refresh();

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
        driver.findElement(By.id("registerUsername")).sendKeys(testUsername);
        driver.findElement(By.id("registerPassword")).sendKeys("testPass123");
        driver.findElement(By.id("registerButton")).click();

        Alert alert = driver.switchTo().alert();
        String alertText = alert.getText();
        alert.accept();

        assertEquals("Uživatel zaregistrován!", alertText, "Nečekaný alert!");
    }



    private void handleAlert(String expectedText) {
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            String alertText = alert.getText();
            System.out.println("Zobrazený alert: " + alertText);

            // Pokud alert neodpovídá očekávání, test selže
            assertEquals(expectedText, alertText, "Nečekaný alert!");
            alert.accept();
        } catch (TimeoutException e) {
            Assertions.fail("Očekávaný alert '" + expectedText + "' se nezobrazil!");
        }
    }


    @Test
    @Order(2)
    void testLogin() {
        testRegisterUser(); // Registrujeme nového uživatele

        driver.findElement(By.id("loginUsername")).sendKeys(testUsername);
        driver.findElement(By.id("loginPassword")).sendKeys("testPass123");
        driver.findElement(By.id("loginButton")).click();

        assertTrue(driver.getCurrentUrl().contains("user-panel.html"), "Nejsme na přihlašovací stránce!");
    }

    @Test
    @Order(3)
    void testUpdateUser() {
        testLogin();

        driver.findElement(By.id("newUsername")).sendKeys("updatedUser");
        driver.findElement(By.id("newPassword")).sendKeys("newPass123");
        driver.findElement(By.xpath("//button[text()='Uložit změny']")).click();

        Alert alert = driver.switchTo().alert();
        String alertText = alert.getText();
        alert.accept();

        assertEquals("Údaje byly úspěšně aktualizovány.", alertText, "Chyba při aktualizaci!");
    }


    @Test
    @Order(3)
    void testDeleteUser() {
        testLogin();
        driver.findElement(By.id("deleteUserButton")).click();

        Alert alert = driver.switchTo().alert();
        assertEquals("Uživatel byl smazán.", alert.getText());
        alert.accept();
    }
}
