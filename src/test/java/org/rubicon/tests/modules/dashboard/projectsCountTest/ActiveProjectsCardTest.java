package org.rubicon.tests.modules.dashboard.projectsCountTest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rubicon.automation.pages.module.dashboard.projectsCount.ActiveProjectsCard;
import org.rubicon.base.BaseTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class ActiveProjectsCardTest extends BaseTest {

    private static final Logger LOG = LogManager.getLogger(ActiveProjectsCardTest.class);

    private ActiveProjectsCard activeProjectsCard;
    private int dashboardCount;
    private int pageCount;

    @BeforeClass(alwaysRun = true)
    public void setUp() throws IOException {
        ensureLoggedInOnDashboard();
        activeProjectsCard = new ActiveProjectsCard(driver);
    }

    @AfterClass(alwaysRun = true)
    public void returnToDashboard() {
        navigateToDashboard();
        LOG.info("Returned to dashboard after Active Projects card tests");
    }

    @Test(priority = 1, groups = {"regression", "dashboard-cards"},
            description = "Verify Active Projects card is visible on dashboard")
    public void isCardVisible() {
        assertTrue(activeProjectsCard.isCardVisible(), "Active Projects card is not visible");
    }

    @Test(priority = 2, dependsOnMethods = "isCardVisible", groups = {"regression", "dashboard-cards"},
            description = "Capture Active Projects count from dashboard card")
    public void verifyDashboardCount() {
        dashboardCount = activeProjectsCard.getDashboardActiveProjectsCardCount();
        assertTrue(dashboardCount >= 0, "Dashboard count should be >= 0");
        LOG.info("Dashboard count: {}", dashboardCount);
    }

    @Test(priority = 3, dependsOnMethods = "verifyDashboardCount", groups = {"regression", "dashboard-cards"},
            description = "Open Active Projects card detail page")
    public void clickCard() {
        activeProjectsCard.clickActiveProjectsCard();
        assertTrue(driver.getCurrentUrl().contains("dashboardcardview"), "Detail page did not open");
    }

    @Test(priority = 4, dependsOnMethods = "clickCard", groups = {"regression", "dashboard-cards"},
            description = "Capture Active Projects count from detail page")
    public void verifyPageCount() {
        pageCount = activeProjectsCard.getInnerCardActiveProjectsTitleCount();
        assertTrue(pageCount >= 0, "Page count should be >= 0");
        LOG.info("Page count: {}", pageCount);
    }

    @Test(priority = 5, dependsOnMethods = "verifyPageCount", groups = {"regression", "dashboard-cards"},
            description = "Verify dashboard and detail page counts match")
    public void verifyCountsMatch() {
        assertEquals(dashboardCount, pageCount, "Dashboard count and page count should match");
    }

    @Test(priority = 6, dependsOnMethods = "verifyCountsMatch", groups = {"regression", "dashboard-cards"},
            description = "Export Active Projects from detail page")
    public void exportProjects() {
        activeProjectsCard.exportActiveProjects();
        assertTrue(driver.getCurrentUrl().contains("dashboardcardview"), "Still on detail page after export");
    }
}
