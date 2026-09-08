package org.rubicon.cucumber.hooks;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.rubicon.automation.reports.ReportManager;
import org.rubicon.base.BaseTest;

import java.io.File;
import java.io.IOException;

public class CucumberHooks extends BaseTest {

    @Before
    public void beforeScenario(Scenario scenario) throws IOException {
        launchApplication();
        ReportManager.startTest(scenario.getName());
    }

    @After
    public void afterScenario(Scenario scenario) {
        ExtentTest test = ReportManager.getTest();
        if (test == null) {
            return;
        }

        if (scenario.isFailed() && driver != null) {
            try {
                String screenshotPath = getScreenShot(sanitize(scenario.getName()), driver);
                test.fail(scenario.getName() + " failed")
                        .addScreenCaptureFromPath(screenshotPath, scenario.getName());
            } catch (IOException | RuntimeException e) {
                test.fail(scenario.getName() + " failed; screenshot could not be captured");
            }
        } else if (scenario.isFailed()) {
            test.fail(scenario.getName() + " failed");
        } else {
            test.log(Status.PASS, "Scenario passed");
        }

        ReportManager.endTest();
    }

    @AfterAll
    public static void afterAllScenarios() {
        ReportManager.flush();
        File report = ReportManager.getReportFile();
        if (report.exists()) {
            String url = "file:///" + report.getAbsolutePath().replace("\\", "/").replace(" ", "%20");
            System.out.println();
            System.out.println("========================================================");
            System.out.println(" Extent Report: " + url);
            System.out.println(" Cucumber HTML: file:///" +
                    new File(System.getProperty("user.dir"), "reports/cucumber.html")
                            .getAbsolutePath().replace("\\", "/"));
            System.out.println("========================================================");
            System.out.println();
        }
    }

    private String sanitize(String name) {
        return name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
