package org.rubicon.tests.modules.dashboard.projectsCountTest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rubicon.automation.pages.module.dashboard.projectsCount.ScheduledProjectsCard;
import org.rubicon.base.BaseTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class ScheduledProjectsCardTest extends BaseTest {

    private static final Logger LOG = LogManager.getLogger(ScheduledProjectsCardTest.class);
    private static final String TEST_DATA_PATH =
            System.getProperty("user.dir") + "/src/main/resources/testdata/ScheduledProjectsCardData.json";

    private ScheduledProjectsCard scheduledProjectsCard;
    private HashMap<String, String> testData;
    private int dashboardCount;
    private int pageCount;

    @BeforeClass(alwaysRun = true)
    public void setUp() throws IOException {
        ensureLoggedInOnDashboard();
        scheduledProjectsCard = new ScheduledProjectsCard(driver);
        testData = loadTestData();
    }

    private HashMap<String, String> loadTestData() throws IOException {
        List<HashMap<String, String>> data = getJsonDataToMap(TEST_DATA_PATH);
        if (data == null || data.isEmpty()) {
            throw new RuntimeException("No test data found in ScheduledProjectsCardData.json");
        }
        return data.get(0);
    }

    @AfterClass(alwaysRun = true)
    public void returnToDashboard() {
        navigateToDashboard();
        LOG.info("Returned to dashboard after Scheduled Projects card tests");
    }

    @Test(priority = 1, groups = {"regression", "dashboard-cards"},
            description = "Verify Scheduled Projects card is visible on dashboard")
    public void isCardVisible() {
        assertTrue(scheduledProjectsCard.isCardVisible(), "Scheduled Projects card is not visible");
    }

    @Test(priority = 2, dependsOnMethods = "isCardVisible", groups = {"regression", "dashboard-cards"},
            description = "Capture Scheduled Projects count from dashboard card")
    public void verifyDashboardCount() {
        dashboardCount = scheduledProjectsCard.getDashboardScheduledProjectsCardCount();
        assertTrue(dashboardCount >= 0, "Dashboard count should be >= 0");
        LOG.info("Dashboard count: {}", dashboardCount);
    }

    @Test(priority = 3, dependsOnMethods = "verifyDashboardCount", groups = {"regression", "dashboard-cards"},
            description = "Open Scheduled Projects card detail page")
    public void clickCard() {
        scheduledProjectsCard.clickScheduledProjectsCard();
        assertTrue(driver.getCurrentUrl().contains("dashboardcardview"), "Detail page did not open");
    }

    @Test(priority = 4, dependsOnMethods = "clickCard", groups = {"regression", "dashboard-cards"},
            description = "Capture Scheduled Projects count from detail page")
    public void verifyPageCount() {
        pageCount = scheduledProjectsCard.getInnerCardScheduledProjectsTitleCount();
        assertTrue(pageCount >= 0, "Page count should be >= 0");
        LOG.info("Page count: {}", pageCount);
    }

    @Test(priority = 5, dependsOnMethods = "verifyPageCount", groups = {"regression", "dashboard-cards"},
            description = "Verify dashboard and detail page counts match")
    public void verifyCountsMatch() {
        assertEquals(dashboardCount, pageCount, "Dashboard count and page count should match");
    }

    @Test(priority = 6, dependsOnMethods = "verifyCountsMatch", groups = {"regression", "dashboard-cards"},
            description = "Search for a project on Scheduled Projects detail page")
    public void searchForProject() {
        scheduledProjectsCard.searchProject(testData.get("searchProjectName"));
        assertTrue(driver.getCurrentUrl().contains("dashboardcardview"), "Still on detail page after search");
    }

    @Test(priority = 7, dependsOnMethods = "searchForProject", groups = {"regression", "dashboard-cards"},
            description = "Refresh Scheduled Projects detail page")
    public void refreshPage() {
        scheduledProjectsCard.pageRefresh();
        assertTrue(scheduledProjectsCard.getInnerCardScheduledProjectsTitleCount() >= 0,
                "Count should be valid after refresh");
    }

    @Test(priority = 8, dependsOnMethods = "refreshPage", groups = {"regression", "dashboard-cards"},
            description = "Export Scheduled Projects from detail page")
    public void exportProjects() {
        scheduledProjectsCard.exportScheduledProjects();
        assertTrue(driver.getCurrentUrl().contains("dashboardcardview"), "Still on detail page after export");
    }
}
