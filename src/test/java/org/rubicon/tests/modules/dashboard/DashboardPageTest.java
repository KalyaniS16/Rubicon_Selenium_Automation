package org.rubicon.tests.modules.dashboard;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rubicon.automation.pages.module.dashboard.DashboardPage;
import org.rubicon.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

public class DashboardPageTest extends BaseTest {

    private static final Logger LOG = LogManager.getLogger(DashboardPageTest.class);
    private DashboardPage dashboardPage;

    @BeforeClass(alwaysRun = true)
    public void setUp() throws IOException {
        ensureLoggedInOnDashboard();
        dashboardPage = new DashboardPage(driver);
    }

    @Test(priority = 1, groups = {"smoke", "dashboard"},
            description = "Verify user lands on the dashboard page after login")
    public void verifyDashboardIsLoaded() {
        dashboardPage.waitForDashboardPage();
        Assert.assertTrue(driver.getCurrentUrl().contains("#/dashboard"),
                "User should be on the dashboard page");
        LOG.info("Dashboard page verified successfully");
    }
}
