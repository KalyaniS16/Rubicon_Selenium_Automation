package org.rubicon.automation.reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ReportManager {

    public static ExtentReports getReportObject() {        //as we keep this method as static, we can access it without creating object of that class
//		ExtentSparkReporter expects the path where reporter should be created
        String path = 	System.getProperty("user.dir")+"\\src\\main\\java\\org\\rubicon\\automation\\reports\\ReportManager.java\\index.html";
        ExtentSparkReporter reporter = new ExtentSparkReporter(path);
        reporter.config().setReportName("Rubicon Test Automation Report");
        reporter.config().setDocumentTitle("Rubicon Automation Report");
        reporter.config().setTheme(Theme.STANDARD);

//		ExtentReports responsible to driver all your reporting execution
        ExtentReports extent = new ExtentReports();
        extent.attachReporter(reporter);          //helps to attach all the reports to main class ExtentReports
        extent.setSystemInfo("Tester", "Kalyani Sajanpawar");
        extent.setSystemInfo("Project", "Rubicon Contractors");
        extent.setSystemInfo("Test", "Dashboard cards");
        extent.setSystemInfo("Framework", "Selenium + TestNG");
        return extent;
    }

    public static void logInfo(String s) {
        ExtentTest test = ReportManager.getReportObject().createTest(s);
        test.info(s);
    }
}
