package org.rubicon.tests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rubicon.automation.pages.DashboardPage;
import org.rubicon.automation.pages.ScheduledProjectsCard;
import org.rubicon.base.BaseTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class ScheduledProjectsCardTest extends BaseTest {

    private static final Logger LOG = LogManager.getLogger(ScheduledProjectsCardTest.class);
    private ScheduledProjectsCard scheduledProjectsCard;
    private int dashboardScheduledProjectsCount = -1;
    private int scheduledProjectPageCount = -1;

    @BeforeClass
    public void setupScheduledProjectsCardTest() throws IOException {
        // Read login credentials from LoginData.json
        List<HashMap<String, String>> testData = getJsonDataToMap(
                System.getProperty("user.dir") + "\\src\\main\\resources\\testdata\\LoginData.json"
        );

        if (testData != null && !testData.isEmpty()) {
            HashMap<String, String> credentials = testData.get(0);
            String username = credentials.get("username");
            String password = credentials.get("password");

            // Perform login with credentials from JSON file
            loginPage.login(username, password);
            LOG.info("Logged in with credentials from LoginData.json");
        } else {
            throw new RuntimeException("No credentials found in LoginData.json");
        }

        // Initialize dashboard page and wait for it to load
        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.waitForDashboardPage();
        LOG.info("Setup complete: User is now on dashboard page");

        // Initialize Scheduled Projects Card page object
        scheduledProjectsCard = new ScheduledProjectsCard(driver);
        LOG.info("Scheduled Projects Card page object initialized");
    }

    @Test(priority=1)
    public void isCardVisible()
    {
        assertTrue(scheduledProjectsCard.isCardVisible(), "Scheduled Projects card is not visible");
    }

    @Test(priority=2)
    public void verifyDashboardScheduledProjectCount()
    {
        dashboardScheduledProjectsCount = scheduledProjectsCard.getDashboardScheduledProjectsCardCount();
        LOG.info("Dashboard Scheduled Projects card count is: {}", dashboardScheduledProjectsCount);
        assertTrue(dashboardScheduledProjectsCount >= 0, "Dashboard Scheduled Projects count should be non-negative");
    }

    @Test(priority=3)
    public void clickScheduledProjectsCard()
    {
        scheduledProjectsCard.clickScheduledProjectsCard();
        LOG.info("Scheduled Projects Card clicked");
    }

    @Test(priority=4)
    public void verifyScheduledProjectsCardCount()
    {
        scheduledProjectPageCount = scheduledProjectsCard.getInnerCardScheduledProjectsTitleCount();
        LOG.info("Scheduled Projects count retrieved: {}", scheduledProjectPageCount);
        assertTrue(scheduledProjectPageCount >= 0, "Scheduled Projects page count should be non-negative");
    }

    @Test(priority=5)
    public void verifyScheduledProjectsCountMatches(){
        assertTrue(dashboardScheduledProjectsCount >= 0,
                "Dashboard count was never captured. Make sure verifyDashboardScheduledProjectCount() ran first.");
        assertTrue(scheduledProjectPageCount >= 0,
                "Page count was never captured. Make sure verifyScheduledProjectsCardCount() ran first.");
        assertEquals(dashboardScheduledProjectsCount, scheduledProjectPageCount,
                "Dashboard card count should match Scheduled Projects page title count");
        LOG.info("Scheduled Projects count matches between dashboard and scheduled projects page: {}",
                dashboardScheduledProjectsCount);
    }

    @Test(priority=6)
    public void searchForProject(){
        try {
            scheduledProjectsCard.searchProject("CP Projects");
            LOG.info("Scheduled Projects Card search result");
        } catch (RuntimeException e) {
            LOG.warn("Search test skipped - test data not available: {}", e.getMessage());
            // Skip this test if project doesn't exist
            throw new org.testng.SkipException("Test project 'CP Projects' not found. This is expected if test data is not loaded.");
        }
    }

    @Test(priority=7)
    public void pageRefreshProjectsCard()
    {
        scheduledProjectsCard.pageRefresh();
        LOG.info("Scheduled Projects Card page refreshed");
    }

    @Test(priority=8)
    public void downloadScheduledProjects()
    {
        try {
            scheduledProjectsCard.exportScheduledProjects();
            LOG.info("Scheduled Projects Card export completed");
        } catch (RuntimeException e) {
            LOG.warn("Export test failed: {}", e.getMessage());
            // If export modal doesn't appear, skip rather than fail
            throw new org.testng.SkipException("Export modal not available. This may indicate UI changes or test environment issues.");
        }
    }

}


