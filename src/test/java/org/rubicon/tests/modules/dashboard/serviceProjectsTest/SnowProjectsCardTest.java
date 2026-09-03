package org.rubicon.tests.modules.dashboard.serviceProjectsTest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rubicon.automation.pages.module.dashboard.serviceProjects.SnowProjectsCard;
import org.rubicon.base.BaseTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertTrue;

public class SnowProjectsCardTest extends BaseTest {
    private static final Logger LOG = LogManager.getLogger(SnowProjectsCardTest.class);

    private SnowProjectsCard snowProjectsCard;

    @BeforeClass(alwaysRun = true)
    public void setUp() throws IOException {
        ensureLoggedInOnDashboard();
        snowProjectsCard = new SnowProjectsCard(driver);
    }

//    @AfterClass(alwaysRun = true)
//    public void returnToDashboard() {
//        navigateToDashboard();
//        LOG.info("Returned to dashboard after Snow Projects card tests");
//    }

    @Test(priority = 1)
    public void isCardVisible() {
        assertTrue(snowProjectsCard.isCardVisible(), "Snow Projects card is not visible");
    }

    @Test(priority = 2, dependsOnMethods = "isCardVisible")
    public void clickInnerPage() {
        snowProjectsCard.clickSnowProjectInnerPage();
        LOG.info("Inner page clicked");
    }

    @Test(priority = 3, dependsOnMethods = "clickInnerPage")
    public void VerifyTheCalendarIsClickable() {
        snowProjectsCard.clickOnCalendar();
        LOG.info("The calendar is clickable");
    }

    @Test(priority = 4)
    public void verifyTheImportData() {
        snowProjectsCard.clickOnDownloadData();
        LOG.info("The download data is clickable");
    }

    @Test(priority = 5, dependsOnMethods = "verifyTheImportData")
    public void verifyTheFilterIsApplied() {
        snowProjectsCard.applyFilter();
        LOG.info("Filter applied successfully");
    }

    @Test(dependsOnMethods = "verifyTheFilterIsApplied")
    public void verifyTheAppliedFilterIsCleared() {
        snowProjectsCard.setClearFilter();
        LOG.info("Filter cleared successfully");
    }
}
