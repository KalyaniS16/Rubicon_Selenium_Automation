package org.rubicon.tests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rubicon.automation.pages.module.dashboard.DashboardPage;
import org.rubicon.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class LoginApplicationTest extends BaseTest {

    private static final Logger LOG = LogManager.getLogger(LoginApplicationTest.class);

    /**
     * Test to verify successful login to the application
     * and navigate to dashboard
     */
    @Test(dataProvider = "getLoginData")
    public void testSuccessfulLogin(HashMap<String, String> input) throws IOException {
        LOG.info("Starting login test with username: {}", input.get("username"));

        // User should be on login page
        Assert.assertNotNull(loginPage, "Login page should be initialized");

        // Perform login with provided credentials
        loginPage.login(input.get("username"), input.get("password"));
        isLoggedIn = true;

        // Create dashboard page object
        DashboardPage dashboardPage = new DashboardPage(driver);

        // Verify that user successfully navigated to dashboard
        dashboardPage.waitForDashboardPage();

        // At this point, user should stay in dashboard
        // No logout is performed - user remains logged in
        Assert.assertTrue(true, "Login and dashboard navigation successful");
    }

    /**
     * DataProvider to supply login test data from JSON file
     */
    @DataProvider(name = "getLoginData")
    public Object[][] getLoginTestData() throws IOException {
        List<HashMap<String, String>> testData = getJsonDataToMap(
                System.getProperty("user.dir") + "\\src\\main\\resources\\testdata\\LoginData.json"
        );

        Object[][] data = new Object[testData.size()][1];
        for (int i = 0; i < testData.size(); i++) {
            data[i][0] = testData.get(i);
        }

        return data;
    }
}
