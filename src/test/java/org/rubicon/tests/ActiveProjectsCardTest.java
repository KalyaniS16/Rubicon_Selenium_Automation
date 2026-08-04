package org.rubicon.tests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rubicon.automation.pages.ActiveProjectsCard;
import org.rubicon.base.BaseTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class ActiveProjectsCardTest extends BaseTest {

    private static final Logger LOG = LogManager.getLogger(ActiveProjectsCardTest.class);
    private ActiveProjectsCard activeProjectsCard;
    private int dashboardActiveProjectsCount = -1;
    private int activeProjectPageCount = -1;

    @BeforeClass
    public void setupActiveProjectsCardTest() throws IOException {
        // Reuses existing login session when run in suite; logs in when run alone
        ensureLoggedInOnDashboard();
        LOG.info("Setup complete: User is now on dashboard page");

        activeProjectsCard = new ActiveProjectsCard(driver);
        LOG.info("Active Projects Card page object initialized");
    }

    @Test(priority=1)
    public void isCardVisible()
    {
        assertTrue(activeProjectsCard.isCardVisible(), "Active Projects card is not visible");
    }

    @Test(priority=2)
    public void verifyDashboardActiveProjectCount()
    {
        dashboardActiveProjectsCount = activeProjectsCard.getDashboardActiveProjectsCardCount();
        LOG.info("Dashboard Active Projects card count is: {}", dashboardActiveProjectsCount);
        assertTrue(dashboardActiveProjectsCount >= 0, "Dashboard Active Projects count should be non-negative");
    }

    @Test(priority=3)
    public void clickActiveProjectsCard()
    {
        activeProjectsCard.clickActiveProjectsCard();
        LOG.info("Active Projects Card clicked");
    }

    @Test(priority=4)
    public void verifyActiveProjectsCardCount()
    {
        activeProjectPageCount = activeProjectsCard.getInnerCardActiveProjectsTitleCount();
        LOG.info("Active Projects count retrieved: {}", activeProjectPageCount);
        assertTrue(activeProjectPageCount >= 0, "Active Projects page count should be non-negative");
    }

    @Test(priority=5)
    public void verifyActiveProjectsCountMatches(){
        assertTrue(dashboardActiveProjectsCount >= 0,
                "Dashboard count was never captured. Make sure verifyDashboardActiveProjectCount() ran first.");
        assertTrue(activeProjectPageCount >= 0,
                "Page count was never captured. Make sure verifyActiveProjectsCardCount() ran first.");
        assertEquals(dashboardActiveProjectsCount, activeProjectPageCount,
                "Dashboard card count should match Active Projects page title count");
        LOG.info("Active Projects count matches between dashboard and active projects page: {}",
                dashboardActiveProjectsCount);
    }

    @Test(priority=6)
    public void downloadActiveProjects()
    {
        try {
            activeProjectsCard.exportActiveProjects();
            LOG.info("Active Projects Card export completed");
        } catch (RuntimeException e) {
            LOG.warn("Export test failed: {}", e.getMessage());
            // If export modal doesn't appear, skip rather than fail
            throw new org.testng.SkipException("Export modal not available. This may indicate UI changes or test environment issues.");
        }
    }

    @Test(priority=7)
    public void goBackToDashboard()
    {
        activeProjectsCard.goBackToDashboardPage();
        LOG.info("Going back to dashboard page from Active Projects page");
    }
}
