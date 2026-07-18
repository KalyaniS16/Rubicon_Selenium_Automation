package org.rubicon.tests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rubicon.automation.pages.DashboardPage;
import org.rubicon.automation.pages.LoginPage;
import org.rubicon.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class DashboardPageTest extends BaseTest {

    private static final Logger LOG = LogManager.getLogger(DashboardPageTest.class);
    DashboardPage dashboardPage;

    @Test
    public void loginAndNavigateToDashboard() throws IOException {
        LOG.info("Setting up: Logging in and navigating to dashboard");

        // Read login credentials from LoginData.json (first entry)
        List<HashMap<String, String>> testData = getJsonDataToMap(
                System.getProperty("user.dir") + "\\src\\main\\resources\\testdata\\LoginData.json"
        );

        if (testData != null && !testData.isEmpty()) {
            HashMap<String, String> credentials = testData.get(0);
            String username = credentials.get("username");
            String password = credentials.get("password");

            // Perform login with credentials from JSON file (NOT hard-coded)
            loginPage.login(username, password);
            LOG.info("Logged in with credentials from LoginData.json");
        } else {
            throw new RuntimeException("No credentials found in LoginData.json");
        }

        // Initialize dashboard page and wait for it to load
        dashboardPage = new DashboardPage(driver);
        dashboardPage.waitForDashboardPage();
        LOG.info("Setup complete: User is now on dashboard page");
    }
}
