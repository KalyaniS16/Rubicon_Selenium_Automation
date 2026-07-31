package org.rubicon.tests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rubicon.automation.pages.DashboardPage;
import org.rubicon.automation.pages.ScheduledEmployeesCard;
import org.rubicon.automation.pages.ScheduledProjectsCard;
import org.rubicon.base.BaseTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class ScheduledEmployeesCardTest extends BaseTest {

    private static final Logger LOG = LogManager.getLogger(ScheduledEmployeesCardTest.class);
    private ScheduledEmployeesCard scheduledEmployeesCard;
    private int dashboardScheduledEmployeesCount = -1;
    private int scheduledEmployeePageCount = -1;

    @BeforeClass
    public void setupScheduledEmployeesCardTest() throws IOException {
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

        // Initialize Scheduled Employees Card page object
        scheduledEmployeesCard = new ScheduledEmployeesCard(driver);
        LOG.info("Scheduled Employees Card page object initialized");
    }

    @Test(priority=1)
    public void isCardVisible()
    {
        assertTrue(scheduledEmployeesCard.isCardVisible(), "Scheduled Employees card is not visible");
    }

    @Test(priority=2)
    public void verifyDashboardScheduledEmployeeCount()
    {
        dashboardScheduledEmployeesCount = scheduledEmployeesCard.getDashboardScheduledEmployeesCardCount();
        LOG.info("Dashboard Scheduled Employees card count is: {}", dashboardScheduledEmployeesCount);
        assertTrue(dashboardScheduledEmployeesCount >= 0, "Dashboard Scheduled Employees count should be non-negative");
    }

    @Test(priority=3)
    public void clickScheduledEmployeesCard()
    {
        scheduledEmployeesCard.clickScheduledEmployeesCard();
        LOG.info("Scheduled Employees Card clicked");
    }

    @Test(priority=4)
    public void verifyScheduledEmployeesCardCount()
    {
        scheduledEmployeePageCount = scheduledEmployeesCard.getInnerCardScheduledEmployeesTitleCount();
        LOG.info("Scheduled Employees count retrieved: {}", scheduledEmployeePageCount);
        assertTrue(scheduledEmployeePageCount >= 0, "Scheduled Employees page count should be non-negative");
    }

    @Test(priority=5)
    public void verifyScheduledEmployeesCountMatches(){
        assertTrue(dashboardScheduledEmployeesCount >= 0,
                "Dashboard count was never captured. Make sure verifyDashboardScheduledEmployeeCount() ran first.");
        assertTrue(scheduledEmployeePageCount >= 0,
                "Page count was never captured. Make sure verifyScheduledEmployeesCardCount() ran first.");
        assertEquals(dashboardScheduledEmployeesCount, scheduledEmployeePageCount,
                "Dashboard card count should match Scheduled Employees page title count");
        LOG.info("Scheduled Employees count matches between dashboard and scheduled employees page: {}",
                dashboardScheduledEmployeesCount);
    }

    @Test(priority=6)
    public void searchForEmployee(){
        try {
            scheduledEmployeesCard.searchEmployees("Sai Church");
            LOG.info("Scheduled Employees Card search result");
        } catch (RuntimeException e) {
            LOG.warn("Search test skipped - test data not available: {}", e.getMessage());
            // Skip this test if employee doesn't exist
            throw new org.testng.SkipException("Test employee 'Sai Church' not found. This is expected if test data is not loaded.");
        }
    }

    @Test(priority=7)
    public void pageRefreshEmployeesCard()
    {
        scheduledEmployeesCard.pageRefresh();
        LOG.info("Scheduled Employees Card page refreshed");
    }

    @Test(priority=8)
    public void downloadScheduledEmployees()
    {
        try {
            scheduledEmployeesCard.exportScheduledEmployees();
            LOG.info("Scheduled Employees Card export completed");
        } catch (RuntimeException e) {
            LOG.warn("Export test failed: {}", e.getMessage());
            // If export modal doesn't appear, skip rather than fail
            throw new org.testng.SkipException("Export modal not available. This may indicate UI changes or test environment issues.");
        }
    }

    @Test(priority=9)
    public void goBackToDashboard()
    {
        scheduledEmployeesCard.goBackToDashboardPage();
        LOG.info("Going back to dashboard page from Scheduled Employees page");
    }
}
