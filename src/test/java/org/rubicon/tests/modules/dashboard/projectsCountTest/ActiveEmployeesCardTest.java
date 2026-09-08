package org.rubicon.tests.modules.dashboard.projectsCountTest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rubicon.automation.pages.module.dashboard.projectsCount.ActiveEmployeesCard;
import org.rubicon.base.BaseTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class ActiveEmployeesCardTest extends BaseTest {

    private static final Logger LOG = LogManager.getLogger(ActiveEmployeesCardTest.class);

    private ActiveEmployeesCard activeEmployeesCard;
    private int dashboardCount;
    private int pageCount;

    @BeforeClass(alwaysRun = true)
    public void setUp() throws IOException {
        ensureLoggedInOnDashboard();
        activeEmployeesCard = new ActiveEmployeesCard(driver);
    }

    @AfterClass(alwaysRun = true)
    public void returnToDashboard() {
        navigateToDashboard();
        LOG.info("Returned to dashboard after Active Employees card tests");
    }

    @Test(priority = 1, groups = {"regression", "dashboard-cards"},
            description = "Verify Active Employees card is visible on dashboard")
    public void isCardVisible() {
        assertTrue(activeEmployeesCard.isCardVisible(), "Active Employees card is not visible");
    }

    @Test(priority = 2, dependsOnMethods = "isCardVisible", groups = {"regression", "dashboard-cards"},
            description = "Capture Active Employees count from dashboard card")
    public void verifyDashboardCount() {
        dashboardCount = activeEmployeesCard.getDashboardActiveEmployeesCardCount();
        assertTrue(dashboardCount >= 0, "Dashboard count should be >= 0");
        LOG.info("Dashboard count: {}", dashboardCount);
    }

    @Test(priority = 3, dependsOnMethods = "verifyDashboardCount", groups = {"regression", "dashboard-cards"},
            description = "Open Active Employees card detail page")
    public void clickCard() {
        activeEmployeesCard.clickActiveEmployeesCard();
        assertTrue(driver.getCurrentUrl().contains("dashboardcardview"), "Detail page did not open");
    }

    @Test(priority = 4, dependsOnMethods = "clickCard", groups = {"regression", "dashboard-cards"},
            description = "Capture Active Employees count from detail page")
    public void verifyPageCount() {
        pageCount = activeEmployeesCard.getInnerCardActiveEmployeesTitleCount();
        assertTrue(pageCount >= 0, "Page count should be >= 0");
        LOG.info("Page count: {}", pageCount);
    }

    @Test(priority = 5, dependsOnMethods = "verifyPageCount", groups = {"regression", "dashboard-cards"},
            description = "Verify dashboard and detail page counts match")
    public void verifyCountsMatch() {
        assertEquals(dashboardCount, pageCount, "Dashboard count and page count should match");
    }

    @Test(priority = 6, dependsOnMethods = "verifyCountsMatch", groups = {"regression", "dashboard-cards"},
            description = "Export Active Employees from detail page")
    public void exportEmployees() {
        activeEmployeesCard.exportActiveEmployees();
        assertTrue(driver.getCurrentUrl().contains("dashboardcardview"), "Still on detail page after export");
    }
}
