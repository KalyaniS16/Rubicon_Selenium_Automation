package org.rubicon.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.rubicon.automation.reports.ReportManager;
import org.rubicon.base.BaseTest;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.IOException;

public class TestListener extends BaseTest implements ITestListener, ISuiteListener {

    private static final Logger LOG = LogManager.getLogger(TestListener.class);

    ExtentTest test;
    ExtentReports extent = ReportManager.getReportObject();
    WebDriver driver;
    ThreadLocal<ExtentTest> extentTest = new ThreadLocal<ExtentTest>();   //ThreadSafe

    @Override
    //	Invoked each time before a test will be invoked.
    public void onTestStart(ITestResult result) {
        test = extent.createTest(result.getMethod().getMethodName());     //when ErrorValidationClass method (LoginErrorValidation) is under execution, it will get only methodname and set the entry in extent report.
        extentTest.set(test);   //this will set the test object into ThreadLocal
    }

    //	On test pass
    @Override
    public void onTestSuccess(ITestResult result) {
        extentTest.get().log(Status.PASS, "Test is passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testCaseName = result.getMethod().getMethodName();
        ExtentTest current = extentTest.get();
        current.fail(result.getThrowable());

        try {
            // driver is static on BaseTest; get(null) works for static fields
            driver = (WebDriver) result.getTestClass().getRealClass().getField("driver").get(null);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            LOG.error("Could not read driver for screenshot of {}", testCaseName, e);
            return;
        }

        if (driver == null) {
            LOG.warn("No active driver, skipping screenshot for {}", testCaseName);
            return;
        }

        try {
            current.addScreenCaptureFromPath(getScreenShot(testCaseName, driver), testCaseName);
        } catch (IOException | RuntimeException e) {
            LOG.error("Could not capture screenshot for {}", testCaseName, e);
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        if (extentTest.get() != null) {
            extentTest.get().skip(result.getThrowable() != null
                    ? result.getThrowable()
                    : new Exception("Test skipped"));
        }
        LOG.warn("Test skipped: {}", result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
    }

    @Override
    public void onStart(ITestContext context) {
    }

    @Override
    public void onFinish(ITestContext context) {
        // Flush after each <test> so a mid-suite crash still leaves a report file.
        ReportManager.flush();
    }

    @Override
    public void onStart(ISuite suite) {
    }

    @Override
    public void onFinish(ISuite suite) {
        ReportManager.flush();
    }

}
