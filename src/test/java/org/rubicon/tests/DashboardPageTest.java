package org.rubicon.tests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rubicon.automation.pages.DashboardPage;
import org.rubicon.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

public class DashboardPageTest extends BaseTest {

    private static final Logger LOG = LogManager.getLogger(DashboardPageTest.class);
    DashboardPage dashboardPage;

    @BeforeClass
    public void loginAndNavigateToDashboard() throws IOException {
        LOG.info("Setting up: Ensuring login and navigating to dashboard");
        ensureLoggedInOnDashboard();
        dashboardPage = new DashboardPage(driver);
        LOG.info("Setup complete: User is now on dashboard page");
    }

    @Test
    public void verifyDashboardIsLoaded() {
        dashboardPage.waitForDashboardPage();
        Assert.assertTrue(driver.getCurrentUrl().contains("#/dashboard"),
                "User should be on the dashboard page");
        LOG.info("Dashboard page verified successfully");
    }
}
