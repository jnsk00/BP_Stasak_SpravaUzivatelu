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
        if (page != null) {
            page.close();
        }
        if (context != null) {
            context.close();
        }
    }

    @AfterAll
    static void closePlaywright() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    @Test
    @Order(1)
    void testRegisterUser() {
        page.locator("#username").fill(testUsername);
        page.locator("#password").fill("testPass123");
        page.locator(".register-btn").click();

        page.onDialog(dialog -> {
            assertEquals("Uživatel zaregistrován!", dialog.message());
            dialog.accept();
        });

        page.waitForTimeout(500); // Malé zpoždění pro vizuální testování
    }

    @Test
    @Order(2)
    void testLogin() {
        page.locator("#loginUsername").fill(testUsername);
        page.locator("#loginPassword").fill("testPass123");
        page.locator("#loginButton").click();

        page.waitForURL(url -> url.contains("user-panel.html"));
        assertTrue(page.url().contains("user-panel.html"), "Nejsme na přihlašovací stránce!");
    }

    @Test
    @Order(3)
    void testUpdateUser() {
        testLogin();

        page.locator("#newUsername").fill("updatedUser");
        page.locator("#newPassword").fill("newPass123");
        page.locator("button:has-text('Uložit změny')").click();

        // Čekání na alert
        page.onDialog(dialog -> {
            System.out.println("✅ Alert text: " + dialog.message());
            dialog.accept();
        });

        page.waitForTimeout(500);
    }

    @Test
    @Order(4)
    void testDeleteUser() {
        page.locator("#loginUsername").fill("admin");
        page.locator("#loginPassword").fill("admin123");
        page.locator("#loginButton").click();

        page.waitForURL(url -> url.contains("/admin-panel"));
        page.waitForSelector("#userList");

        Locator rows = page.locator("#userList tr");
        boolean userFound = false;
        int rowCount = rows.count();

        for (int i = 0; i < rowCount; i++) {
            Locator row = rows.nth(i);
            String username = row.locator("td:nth-child(1)").innerText();

            if (username.equals("updatedUser")) {
                userFound = true;
                row.locator("button:has-text('Smazat')").click();

                page.onDialog(Dialog::accept); // Potvrzení alertu

                page.waitForTimeout(1000);
                break;
            }
        }

        // Ověření, že uživatel neexistuje
        boolean userStillExists = page.locator("#userList tr").all()
                .stream().anyMatch(row -> row.locator("td:nth-child(1)").innerText().equals("updatedUser"));

        assertFalse(userStillExists, "Uživatel 'updatedUser' stále existuje, i když měl být smazán!");
    }
}