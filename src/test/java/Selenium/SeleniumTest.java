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

    private void loginAs(String username, String password) {
        driver.findElement(By.id("loginUsername")).sendKeys(username);
        driver.findElement(By.id("loginPassword")).sendKeys(password);
        driver.findElement(By.id("loginButton")).click();
    }

    @Test
    @Order(1)
    void testRegisterUser() {
        driver.findElement(By.id("username")).sendKeys(testUsername);
        driver.findElement(By.id("password")).sendKeys("testPass123");
        driver.findElement(By.className("register-btn")).click();

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String alertText = alert.getText();
        alert.accept();

        assertEquals("Uživatel zaregistrován!", alertText);
    }

    @Test
    @Order(2)
    void testLogin() {
        loginAs(testUsername, "testPass123");
        boolean loginSuccess = wait.until(ExpectedConditions.urlContains("user-panel.html"));
        assertTrue(loginSuccess, "Nepovedlo se přihlášení – chybí redirect na uživatelský panel.");
    }

    @Test
    @Order(3)
    void testUpdateUser() {
        loginAs(testUsername, "testPass123");
        wait.until(ExpectedConditions.urlContains("user-panel.html"));

        driver.findElement(By.id("newUsername")).sendKeys("updatedUser");
        driver.findElement(By.id("newPassword")).sendKeys("newPass123");
        driver.findElement(By.xpath("//button[text()='Uložit změny']")).click();

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("Údaje byly úspěšně aktualizovány.", alert.getText());
        alert.accept();
    }

    @Test
    @Order(4)
    void testDeleteUser() {
        loginAs("admin", "admin123");
        wait.until(ExpectedConditions.urlContains("admin-panel"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userList")));

        List<WebElement> rows = driver.findElements(By.cssSelector("#userList tr"));
        boolean userFound = false;

        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (!cells.isEmpty() && cells.get(0).getText().equals("updatedUser")) {
                userFound = true;
                row.findElement(By.xpath(".//button[contains(text(),'Smazat')]")).click();

                Alert alert = wait.until(ExpectedConditions.alertIsPresent());
                alert.accept();
                wait.until(ExpectedConditions.stalenessOf(row));
                break;
            }
        }

        assertTrue(userFound, "Uživatel updatedUser nebyl nalezen v seznamu.");
        List<WebElement> updatedRows = driver.findElements(By.cssSelector("#userList tr"));
        boolean userStillExists = updatedRows.stream()
                .anyMatch(r -> r.findElements(By.tagName("td")).get(0).getText().equals("updatedUser"));

        assertFalse(userStillExists, "Uživatel 'updatedUser' stále existuje po pokusu o smazání.");
    }
}
