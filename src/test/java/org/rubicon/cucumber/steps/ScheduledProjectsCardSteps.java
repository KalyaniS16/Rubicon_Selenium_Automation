package org.rubicon.cucumber.steps;

import io.cucumber.java.After;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.rubicon.automation.pages.module.dashboard.projectsCount.ScheduledProjectsCard;
import org.rubicon.base.BaseTest;
import org.testng.Assert;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class ScheduledProjectsCardSteps extends BaseTest {

    private static final String TEST_DATA_PATH =
            System.getProperty("user.dir") + "\\src\\main\\resources\\testdata\\ScheduledProjectsCardData.json";

    private ScheduledProjectsCard scheduledProjectsCard;
    private int dashboardCount;
    private int pageCount;

    private ScheduledProjectsCard card() {
        if (scheduledProjectsCard == null) {
            scheduledProjectsCard = new ScheduledProjectsCard(driver);
        }
        return scheduledProjectsCard;
    }

    @Then("the Scheduled Projects card is visible")
    public void theScheduledProjectsCardIsVisible() {
        Assert.assertTrue(card().isCardVisible(), "Scheduled Projects card is not visible");
    }

    @When("the user reads the Scheduled Projects count from the dashboard")
    public void theUserReadsTheScheduledProjectsCountFromTheDashboard() {
        dashboardCount = card().getDashboardScheduledProjectsCardCount();
        Assert.assertTrue(dashboardCount >= 0, "Dashboard count should be >= 0");
    }

    @When("the user opens the Scheduled Projects card")
    public void theUserOpensTheScheduledProjectsCard() {
        card().clickScheduledProjectsCard();
    }

    @Then("the Scheduled Projects detail page is displayed")
    public void theScheduledProjectsDetailPageIsDisplayed() {
        Assert.assertTrue(driver.getCurrentUrl().contains("dashboardcardview"),
                "Scheduled Projects detail page did not open");
    }

    @Then("the Scheduled Projects detail page count is valid")
    public void theScheduledProjectsDetailPageCountIsValid() {
        pageCount = card().getInnerCardScheduledProjectsTitleCount();
        Assert.assertTrue(pageCount >= 0, "Page count should be >= 0");
    }

    @Then("the dashboard and detail page Scheduled Projects counts match")
    public void theDashboardAndDetailPageScheduledProjectsCountsMatch() {
        Assert.assertEquals(dashboardCount, pageCount,
                "Dashboard count and page count should match");
    }

    @When("the user searches for a Scheduled Project")
    public void theUserSearchesForAScheduledProject() throws IOException {
        card().searchProject(loadSearchProjectName());
        Assert.assertTrue(driver.getCurrentUrl().contains("dashboardcardview"),
                "Still on detail page after search");
    }

    @When("the user refreshes the Scheduled Projects detail page")
    public void theUserRefreshesTheScheduledProjectsDetailPage() {
        card().pageRefresh();
        Assert.assertTrue(card().getInnerCardScheduledProjectsTitleCount() >= 0,
                "Count should be valid after refresh");
    }

    @When("the user exports Scheduled Projects")
    public void theUserExportsScheduledProjects() {
        card().exportScheduledProjects();
    }

    @After("@scheduled-projects")
    public void returnToDashboardAfterScheduledProjects() {
        if (driver != null) {
            navigateToDashboard();
        }
    }

    private String loadSearchProjectName() throws IOException {
        List<HashMap<String, String>> data = getJsonDataToMap(TEST_DATA_PATH);
        if (data == null || data.isEmpty()) {
            throw new RuntimeException("No test data found in ScheduledProjectsCardData.json");
        }
        return data.get(0).get("searchProjectName");
    }
}
