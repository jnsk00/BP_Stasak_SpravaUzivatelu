package Playwright;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PlaywrightTest {
    private static Playwright playwright;
    private static Browser browser;
    private BrowserContext context;
    private Page page;
    private static String testUsername;
    private final String baseUrl = "http://localhost:8080";

    @BeforeAll
    static void setupClass() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
        testUsername = "testUser_" + UUID.randomUUID();
    }

    @BeforeEach
    void setup() {
        context = browser.newContext();
        page = context.newPage();
        page.navigate(baseUrl + "/index.html");
        page.waitForSelector("#username");
    }

    @AfterEach
    void tearDown() {
        if (page != null) page.close();
        if (context != null) context.close();
    }

    @AfterAll
    static void closePlaywright() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    private void loginAs(String username, String password) {
        page.locator("#loginUsername").fill(username);
        page.locator("#loginPassword").fill(password);
        page.locator("#loginButton").click();
    }

    @Test
    @Order(1)
    void testRegisterUser() {
        page.onceDialog(dialog -> {
            assertEquals("Uživatel zaregistrován!", dialog.message());
            dialog.accept();
        });

        page.locator("#username").fill(testUsername);
        page.locator("#password").fill("testPass123");
        page.locator(".register-btn").click();

        page.waitForSelector("#userList");
    }

    @Test
    @Order(2)
    void testLogin() {
        loginAs(testUsername, "testPass123");
        page.waitForURL(url -> url.contains("user-panel.html"));
        assertTrue(page.url().contains("user-panel.html"));
    }

    @Test
    @Order(3)
    void testUpdateUser() {
        loginAs(testUsername, "testPass123");
        page.waitForURL("**/user-panel.html");

        page.onceDialog(dialog -> {
            assertEquals("Údaje byly úspěšně aktualizovány.", dialog.message());
            dialog.accept();
        });

        page.locator("#newUsername").fill("updatedUser");
        page.locator("#newPassword").fill("newPass123");
        page.locator("button:has-text('Uložit změny')").click();
    }

    @Test
    @Order(4)
    void testDeleteUser() {
        loginAs("admin", "admin123");
        page.waitForURL("**/admin-panel.html");

        page.waitForSelector("#userList");
        Locator rows = page.locator("#userList tr");

        boolean userFoundAndDeleted = false;
        int rowCount = rows.count();

        for (int i = 0; i < rowCount; i++) {
            Locator row = rows.nth(i);
            String username = row.locator("td:nth-child(1)").innerText();

            if (username.equals("updatedUser")) {
                userFoundAndDeleted = true;

                page.onceDialog(Dialog::accept);
                row.locator("button:has-text('Smazat')").click();

                page.waitForSelector("#userList");
                break;
            }
        }

        assertTrue(userFoundAndDeleted, "Uživatel 'updatedUser' nebyl nalezen k odstranění.");

        boolean userStillExists = false;
        int updatedCount = rows.count();
        for (int i = 0; i < updatedCount; i++) {
            String username = rows.nth(i).locator("td:nth-child(1)").innerText();
            if (username.equals("updatedUser")) {
                userStillExists = true;
                break;
            }
        }

        assertFalse(userStillExists, "Uživatel 'updatedUser' stále existuje po smazání.");
    }
}
