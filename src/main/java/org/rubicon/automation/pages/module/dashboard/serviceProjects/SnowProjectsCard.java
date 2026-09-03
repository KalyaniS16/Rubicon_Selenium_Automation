package org.rubicon.automation.pages.module.dashboard.serviceProjects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.rubicon.automation.pages.BasePage;

import java.time.Duration;

public class SnowProjectsCard extends BasePage {

    private static final Logger LOG = LogManager.getLogger(SnowProjectsCard.class);

    public SnowProjectsCard(WebDriver driver) {
        super(driver);
    }

    // ----- Dashboard card -----
    private final By cardTitle = By.xpath("//div[normalize-space()='Service Projects']");
    private final By innerPage = By.xpath("//a[contains(@href, '#/dashboard/serviceproject/1')]");

    // ----- Inner page -----
    private final By calendarButton = By.xpath("//button[@aria-label='Open calendar']");
    private final By calendarDate = By.xpath(
            "//div[contains(@class,'mat-calendar-body-cell-content')][normalize-space()='1']");
    private final By downloadButton = By.xpath(
            "//button[contains(@class,'white_btn') and img[@alt='import']]");
    private final By downloadSnackBar = By.xpath(
            "//snack-bar-container[contains(@class,'success-snack')]"
                    + "[.//span[contains(text(),'data may take some time to export')]]");

    // ----- Filter panel -----
    private final By filterButton = By.xpath(
            "//button[contains(@class,'white_btn') and img[@alt='filter']]");
    private final By companyNameTitle = By.xpath(
            "//h2[contains(@class,'text-tinys') and normalize-space()='Company Name']");
    private final By selectOptions = By.xpath(
            "//h2[contains(@class,'text-tinys') and normalize-space()='Company Name']"
                    + "/following::div[contains(@class,'select-box')][1]"
                    + "//div[normalize-space()='Select Options']");
    // Company checkboxes only (skips "All"). [5] and [6] = 5th and 6th company in the list.
    private final By fifthCompanyOption = By.xpath(
            "(//div[contains(@class,'employee-checkbox-design')]"
                    + "//label[contains(@class,'mat-checkbox-layout')]"
                    + "[not(normalize-space(.)='All')])[5]");
    private final By sixthCompanyOption = By.xpath(
            "(//div[contains(@class,'employee-checkbox-design')]"
                    + "//label[contains(@class,'mat-checkbox-layout')]"
                    + "[not(normalize-space(.)='All')])[6]");
    private final By okButton = By.xpath("//button[@type='submit' and normalize-space()='OK']");
    private final By applyButton = By.xpath(
            "//button[contains(@class,'button_min') and normalize-space()='Apply']");
    private final By clearButton = By.xpath(
            "//button[contains(@class,'clear-btn') and normalize-space()='Clear']");

    public boolean isCardVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(cardTitle));
            LOG.info("Service projects card is visible");
            return true;
        } catch (Exception e) {
            LOG.warn("Service projects card is not visible: {}", e.getMessage());
            return false;
        }
    }

    public void clickSnowProjectInnerPage() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(innerPage)).click();
            Thread.sleep(2000);
            LOG.info("Clicked on Snow Project Inner Page");
        } catch (Exception e) {
            LOG.error("Failed to click on Snow Project Inner Page: {}", e.getMessage());
            throw new RuntimeException("Could not load Snow Project Inner Page", e);
        }
    }

    public void clickOnCalendar() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(calendarButton)).click();
            wait.until(ExpectedConditions.elementToBeClickable(calendarDate)).click();
            Thread.sleep(2000);
            LOG.info("Clicked on date required");
        } catch (Exception e) {
            LOG.error("Failed to click on calendar: {}", e.getMessage());
            throw new RuntimeException("Could not load calendar", e);
        }
    }

    public void clickOnDownloadData() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(downloadButton)).click();
            new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(ExpectedConditions.invisibilityOfElementLocated(downloadSnackBar));
            LOG.info("Download started; export snackbar dismissed");
        } catch (Exception e) {
            LOG.error("Failed to download the data: {}", e.getMessage());
            throw new RuntimeException("Could not download the data", e);
        }
    }

    /**
     * Opens filter → selects 5th & 6th company (not "All") → OK → Apply.
     */
    public void applyFilter() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(filterButton)).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(companyNameTitle));
            wait.until(ExpectedConditions.elementToBeClickable(selectOptions)).click();

            clickCompanyOption(fifthCompanyOption, 5);
            clickCompanyOption(sixthCompanyOption, 6);

            wait.until(ExpectedConditions.elementToBeClickable(okButton)).click();
            LOG.info("Clicked OK to confirm company selection");

            wait.until(ExpectedConditions.elementToBeClickable(applyButton)).click();
            Thread.sleep(2000);
            LOG.info("Filter applied successfully");
        } catch (Exception e) {
            LOG.error("Failed to apply filter: {}", e.getMessage());
            throw new RuntimeException("Could not apply filter", e);
        }
    }

    public void setClearFilter() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(clearButton)).click();
            LOG.info("Filter cleared");
        } catch (Exception e) {
            LOG.error("Failed to clear filter: {}", e.getMessage());
            throw new RuntimeException("Could not clear filter", e);
        }
    }

    /** Scrolls the option into view and clicks it. */
    private void clickCompanyOption(By optionLocator, int position) {
        WebElement option = wait.until(ExpectedConditions.presenceOfElementLocated(optionLocator));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'}); arguments[0].click();",
                option);
        LOG.info("Selected company option at position {}", position);
    }

    private final By quickView = By.xpath("//button[@id='snow-summary-expansion-btn']");
    //properties card:
    private final By propertiesLabel = By.xpath("//div[contains(@class,'isSnowSummary_divdashboard')]//span[@class='header-descript' and normalize-space()='Properties']");
    private final By scheduledProjects = By.xpath("//div[@class='innerText w-full']//span[contains(class,'text-tinys font-medium') and ]");



}
