package org.rubicon.automation.reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;

public class ReportManager {

    private static ExtentReports extent;

    public static synchronized ExtentReports getReportObject() {
        if (extent == null) {
            String reportDir = System.getProperty("user.dir") + File.separator + "reports";
            new File(reportDir).mkdirs();
            String path = reportDir + File.separator + "index.html";

            ExtentSparkReporter reporter = new ExtentSparkReporter(path);
            reporter.config().setReportName("Rubicon Test Automation Report");
            reporter.config().setDocumentTitle("Rubicon Automation Report");
            reporter.config().setTheme(Theme.STANDARD);

            extent = new ExtentReports();
            extent.attachReporter(reporter);
            extent.setSystemInfo("Tester", "Kalyani Sajanpawar");
            extent.setSystemInfo("Project", "Rubicon Contractors");
            extent.setSystemInfo("Test", "Dashboard cards");
            extent.setSystemInfo("Framework", "Selenium + TestNG");
        }
        return extent;
    }

    public static void flush() {
        if (extent != null) {
            extent.flush();
        }
    }
}
