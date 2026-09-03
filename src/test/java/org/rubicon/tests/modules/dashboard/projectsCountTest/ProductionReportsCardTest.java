package org.rubicon.tests.modules.dashboard.projectsCountTest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rubicon.automation.pages.module.dashboard.projectsCount.ProductionReportsCard;
import org.rubicon.base.BaseTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class ProductionReportsCardTest extends BaseTest {

    private static final Logger LOG = LogManager.getLogger(ProductionReportsCardTest.class);

    private ProductionReportsCard productionReportsCard;
    private int dashboardCount;
    private int pageCount;

    @BeforeClass(alwaysRun = true)
    public void setUp() throws IOException {
        ensureLoggedInOnDashboard();
        productionReportsCard = new ProductionReportsCard(driver);
    }

    /**
     * Final class in dashboardCards.xml — leave browser open on dashboard after suite.
     */
    @AfterClass(alwaysRun = true)
    public void returnToDashboard() {
        navigateToDashboard();
        assertTrue(driver.getCurrentUrl().contains("#/dashboard")
                        && !driver.getCurrentUrl().contains("dashboardcardview"),
                "Suite should end on the dashboard page");
        LOG.info("Suite complete — browser remains open on dashboard");
    }

    @Test(priority = 1, groups = {"regression", "dashboard-cards"},
            description = "Verify Production Reports card is visible on dashboard")
    public void isCardVisible() {
        assertTrue(productionReportsCard.isCardVisible(), "Production Reports card is not visible");
    }

    @Test(priority = 2, dependsOnMethods = "isCardVisible", groups = {"regression", "dashboard-cards"},
            description = "Capture Production Reports count from dashboard card")
    public void verifyDashboardCount() {
        dashboardCount = productionReportsCard.getDashboardProductionReportsCardCount();
        assertTrue(dashboardCount >= 0, "Dashboard count should be >= 0");
        LOG.info("Dashboard count: {}", dashboardCount);
    }

    @Test(priority = 3, dependsOnMethods = "verifyDashboardCount", groups = {"regression", "dashboard-cards"},
            description = "Open Production Reports card detail page")
    public void clickCard() {
        productionReportsCard.clickProductionReportsCard();
        assertTrue(driver.getCurrentUrl().contains("dashboardcardview"), "Detail page did not open");
    }

    @Test(priority = 4, dependsOnMethods = "clickCard", groups = {"regression", "dashboard-cards"},
            description = "Capture Production Reports count from detail page")
    public void verifyPageCount() {
        pageCount = productionReportsCard.getInnerCardProductionReportsTitleCount();
        assertTrue(pageCount >= 0, "Page count should be >= 0");
        LOG.info("Page count: {}", pageCount);
    }

    @Test(priority = 5, dependsOnMethods = "verifyPageCount", groups = {"regression", "dashboard-cards"},
            description = "Verify dashboard and detail page counts match")
    public void verifyCountsMatch() {
        assertEquals(dashboardCount, pageCount, "Dashboard count and page count should match");
    }

    @Test(priority = 6, dependsOnMethods = "verifyCountsMatch", groups = {"regression", "dashboard-cards"},
            description = "Export Production Reports from detail page")
    public void exportReports() {
        productionReportsCard.exportProductionReports();
        assertTrue(driver.getCurrentUrl().contains("dashboardcardview"), "Still on detail page after export");
    }
}
