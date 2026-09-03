package org.rubicon.tests.modules.dashboard.projectsCountTest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rubicon.automation.pages.module.dashboard.projectsCount.InactiveProjectsCard;
import org.rubicon.base.BaseTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class InactiveProjectsCardTest extends BaseTest {

    private static final Logger LOG = LogManager.getLogger(InactiveProjectsCardTest.class);
    private static final String TEST_DATA_PATH =
            System.getProperty("user.dir") + "\\src\\main\\resources\\testdata\\InactiveProjectsCardData.json";

    private InactiveProjectsCard inactiveProjectsCard;
    private HashMap<String, String> testData;
    private int dashboardCount;
    private int pageCount;

    @BeforeClass(alwaysRun = true)
    public void setUp() throws IOException {
        ensureLoggedInOnDashboard();
        inactiveProjectsCard = new InactiveProjectsCard(driver);
        testData = loadTestData();
    }

    private HashMap<String, String> loadTestData() throws IOException {
        List<HashMap<String, String>> data = getJsonDataToMap(TEST_DATA_PATH);
        if (data == null || data.isEmpty()) {
            throw new RuntimeException("No test data found in InactiveProjectsCardData.json");
        }
        return data.get(0);
    }

    @AfterClass(alwaysRun = true)
    public void returnToDashboard() {
        navigateToDashboard();
        LOG.info("Returned to dashboard after Inactive Projects card tests");
    }

    @Test(priority = 1, groups = {"regression", "dashboard-cards"},
            description = "Verify Inactive Projects card is visible on dashboard")
    public void isCardVisible() {
        assertTrue(inactiveProjectsCard.isCardVisible(), "Inactive Projects card is not visible");
    }

    @Test(priority = 2, dependsOnMethods = "isCardVisible", groups = {"regression", "dashboard-cards"},
            description = "Capture Inactive Projects count from dashboard card")
    public void verifyDashboardCount() {
        dashboardCount = inactiveProjectsCard.getDashboardInactiveProjectsCardCount();
        assertTrue(dashboardCount >= 0, "Dashboard count should be >= 0");
        LOG.info("Dashboard count: {}", dashboardCount);
    }

    @Test(priority = 3, dependsOnMethods = "verifyDashboardCount", groups = {"regression", "dashboard-cards"},
            description = "Open Inactive Projects card detail page")
    public void clickCard() {
        inactiveProjectsCard.clickInactiveProjectsCard();
        assertTrue(driver.getCurrentUrl().contains("dashboardcardview"), "Detail page did not open");
    }

    @Test(priority = 4, dependsOnMethods = "clickCard", groups = {"regression", "dashboard-cards"},
            description = "Capture Inactive Projects count from detail page")
    public void verifyPageCount() {
        pageCount = inactiveProjectsCard.getInnerCardInactiveProjectsTitleCount();
        assertTrue(pageCount >= 0, "Page count should be >= 0");
        LOG.info("Page count: {}", pageCount);
    }

    @Test(priority = 5, dependsOnMethods = "verifyPageCount", groups = {"regression", "dashboard-cards"},
            description = "Verify dashboard and detail page counts match")
    public void verifyCountsMatch() {
        assertEquals(dashboardCount, pageCount, "Dashboard count and page count should match");
    }

    @Test(priority = 6, dependsOnMethods = "verifyCountsMatch", groups = {"regression", "dashboard-cards"},
            description = "Export Inactive Projects from detail page")
    public void exportProjects() {
        inactiveProjectsCard.exportInactiveProjects();
        assertTrue(driver.getCurrentUrl().contains("dashboardcardview"), "Still on detail page after export");
    }

    @Test(priority = 7, dependsOnMethods = "exportProjects", groups = {"regression", "dashboard-cards"},
            description = "Search for a project on Inactive Projects detail page")
    public void searchForProject() {
        inactiveProjectsCard.searchProject(testData.get("searchProjectName"));
        assertTrue(driver.getCurrentUrl().contains("dashboardcardview"), "Still on detail page after search");
    }

    @Test(priority = 8, dependsOnMethods = "searchForProject", groups = {"regression", "dashboard-cards"},
            description = "Select a project to archive")
    public void selectProjectToArchive() {
        inactiveProjectsCard.selectProjectToArchive();
        assertTrue(driver.getCurrentUrl().contains("dashboardcardview"), "Still on detail page after selecting project");
    }

    @Test(priority = 9, dependsOnMethods = "selectProjectToArchive", groups = {"regression", "dashboard-cards"},
            description = "Archive the selected project")
    public void archiveSelectedProject() {
        inactiveProjectsCard.archiveProject();
        assertTrue(driver.getCurrentUrl().contains("dashboardcardview"), "Still on detail page after archive");
    }

    @Test(priority = 10, groups = {"regression", "dashboard-cards"},
            description = "Refresh Inactive Projects detail page")
    public void refreshPage() {
        inactiveProjectsCard.pageRefresh();
        assertTrue(inactiveProjectsCard.getInnerCardInactiveProjectsTitleCount() >= 0,
                "Count should be valid after refresh");
    }

    @Test(priority = 11, dependsOnMethods = "refreshPage", groups = {"regression", "dashboard-cards"},
            description = "Open filter panel on Inactive Projects detail page")
    public void filterProjects() {
        inactiveProjectsCard.filter();
        assertTrue(driver.getCurrentUrl().contains("dashboardcardview"), "Still on detail page after opening filter");
    }

    @Test(priority = 12, dependsOnMethods = "filterProjects", groups = {"regression", "dashboard-cards"},
            description = "Apply Project ID filter")
    public void applyFilter() {
        inactiveProjectsCard.applyProjectIDFilter(testData.get("filterProjectId"));
        assertTrue(driver.getCurrentUrl().contains("dashboardcardview"), "Still on detail page after applying filter");
    }

    @Test(priority = 13, dependsOnMethods = "applyFilter", groups = {"regression", "dashboard-cards"},
            description = "Clear applied filter")
    public void clearFilter() {
        inactiveProjectsCard.filter();
        inactiveProjectsCard.clearFilter();
        assertTrue(driver.getCurrentUrl().contains("dashboardcardview"), "Still on detail page after clearing filter");
    }

    @Test(priority = 14, dependsOnMethods = "clearFilter", groups = {"regression", "dashboard-cards"},
            description = "Apply Project Status filter")
    public void applyProjectStatusFilter() {
        inactiveProjectsCard.applyProjectStatusFilter();
        assertTrue(driver.getCurrentUrl().contains("dashboardcardview"), "Still on detail page after status filter");
    }
}
