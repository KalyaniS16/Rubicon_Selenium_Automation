package org.rubicon.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.rubicon.automation.pages.module.dashboard.DashboardPage;
import org.rubicon.base.BaseTest;
import org.testng.Assert;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class LoginSteps extends BaseTest {

    @Given("the user is on the login page")
    public void theUserIsOnTheLoginPage() {
        Assert.assertNotNull(loginPage, "Login page should be initialized");
        loginPage.goTo();
        isLoggedIn = false;
    }

    @When("the user logs in with valid credentials")
    public void theUserLogsInWithValidCredentials() throws IOException {
        HashMap<String, String> credentials = loadLoginCredentials();
        loginPage.login(credentials.get("username"), credentials.get("password"));
        isLoggedIn = true;
    }

    @When("the user logs in with username {string} and password {string}")
    public void theUserLogsInWithUsernameAndPassword(String username, String password) {
        loginPage.login(username, password);
        isLoggedIn = true;
    }

    @Given("the user is logged in on the dashboard")
    public void theUserIsLoggedInOnTheDashboard() throws IOException {
        ensureLoggedInOnDashboard();
    }

    @Then("the dashboard page is displayed")
    public void theDashboardPageIsDisplayed() {
        new DashboardPage(driver).waitForDashboardPage();
        Assert.assertTrue(driver.getCurrentUrl().contains("#/dashboard"),
                "User should be on the dashboard page");
    }

    private HashMap<String, String> loadLoginCredentials() throws IOException {
        List<HashMap<String, String>> testData = getJsonDataToMap(
                System.getProperty("user.dir") + "\\src\\main\\resources\\testdata\\LoginData.json"
        );
        if (testData == null || testData.isEmpty()) {
            throw new RuntimeException("No credentials found in LoginData.json");
        }
        return testData.get(0);
    }
}
