package org.rubicon.tests.modules.dashboard.projectsCountTest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rubicon.automation.pages.module.dashboard.projectsCount.ScheduledEmployeesCard;
import org.rubicon.base.BaseTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class ScheduledEmployeesCardTest extends BaseTest {

    private static final Logger LOG = LogManager.getLogger(ScheduledEmployeesCardTest.class);
    private static final String TEST_DATA_PATH =
            System.getProperty("user.dir") + "/src/main/resources/testdata/ScheduledEmployeesCardData.json";

    private ScheduledEmployeesCard scheduledEmployeesCard;
    private HashMap<String, String> testData;
    private int dashboardCount;
    private int pageCount;

    @BeforeClass(alwaysRun = true)
    public void setUp() throws IOException {
        ensureLoggedInOnDashboard();
        scheduledEmployeesCard = new ScheduledEmployeesCard(driver);
        testData = loadTestData();
    }

    private HashMap<String, String> loadTestData() throws IOException {
        List<HashMap<String, String>> data = getJsonDataToMap(TEST_DATA_PATH);
        if (data == null || data.isEmpty()) {
            throw new RuntimeException("No test data found in ScheduledEmployeesCardData.json");
        }
        return data.get(0);
    }

    @AfterClass(alwaysRun = true)
    public void returnToDashboard() {
        navigateToDashboard();
        LOG.info("Returned to dashboard after Scheduled Employees card tests");
    }

    @Test(priority = 1, groups = {"regression", "dashboard-cards"},
            description = "Verify Scheduled Employees card is visible on dashboard")
    public void isCardVisible() {
        assertTrue(scheduledEmployeesCard.isCardVisible(), "Scheduled Employees card is not visible");
    }

    @Test(priority = 2, dependsOnMethods = "isCardVisible", groups = {"regression", "dashboard-cards"},
            description = "Capture Scheduled Employees count from dashboard card")
    public void verifyDashboardCount() {
        dashboardCount = scheduledEmployeesCard.getDashboardScheduledEmployeesCardCount();
        assertTrue(dashboardCount >= 0, "Dashboard count should be >= 0");
        LOG.info("Dashboard count: {}", dashboardCount);
    }

    @Test(priority = 3, dependsOnMethods = "verifyDashboardCount", groups = {"regression", "dashboard-cards"},
            description = "Open Scheduled Employees card detail page")
    public void clickCard() {
        scheduledEmployeesCard.clickScheduledEmployeesCard();
        assertTrue(driver.getCurrentUrl().contains("dashboardcardview"), "Detail page did not open");
    }

    @Test(priority = 4, dependsOnMethods = "clickCard", groups = {"regression", "dashboard-cards"},
            description = "Capture Scheduled Employees count from detail page")
    public void verifyPageCount() {
        pageCount = scheduledEmployeesCard.getInnerCardScheduledEmployeesTitleCount();
        assertTrue(pageCount >= 0, "Page count should be >= 0");
        LOG.info("Page count: {}", pageCount);
    }

    @Test(priority = 5, dependsOnMethods = "verifyPageCount", groups = {"regression", "dashboard-cards"},
            description = "Verify dashboard and detail page counts match")
    public void verifyCountsMatch() {
        assertEquals(dashboardCount, pageCount, "Dashboard count and page count should match");
    }

    @Test(priority = 6, dependsOnMethods = "verifyCountsMatch", groups = {"regression", "dashboard-cards"},
            description = "Search for an employee on Scheduled Employees detail page")
    public void searchForEmployee() {
        scheduledEmployeesCard.searchEmployees(testData.get("searchEmployeeName"));
        assertTrue(driver.getCurrentUrl().contains("dashboardcardview"), "Still on detail page after search");
    }

    @Test(priority = 7, dependsOnMethods = "searchForEmployee", groups = {"regression", "dashboard-cards"},
            description = "Refresh Scheduled Employees detail page")
    public void refreshPage() {
        scheduledEmployeesCard.pageRefresh();
        assertTrue(scheduledEmployeesCard.getInnerCardScheduledEmployeesTitleCount() >= 0,
                "Count should be valid after refresh");
    }

    @Test(priority = 8, dependsOnMethods = "refreshPage", groups = {"regression", "dashboard-cards"},
            description = "Export Scheduled Employees from detail page")
    public void exportEmployees() {
        scheduledEmployeesCard.exportScheduledEmployees();
        assertTrue(driver.getCurrentUrl().contains("dashboardcardview"), "Still on detail page after export");
    }
}
