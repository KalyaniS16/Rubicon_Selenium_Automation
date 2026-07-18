package org.rubicon.automation.pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.rubicon.automation.utilities.WaitUtils;

public class DashboardPage extends BasePage {

    public static final String SCHEDULED_PROJECTS = "Scheduled Projects";

    private static final Logger LOG = LogManager.getLogger(DashboardPage.class);

    private final By pageTitle = By.xpath("//div[contains(@class,'title-ui-design') and contains(.,'Dashboard')]");
    private final By ScheduledProjects = By.xpath("//div[contains(@class,'dashboard-card') and //div[normalize-space()='Scheduled Projects']]");


    public DashboardPage(WebDriver driver) {
        super(driver);
    }
    /** Wait until dashboard URL is loaded */
    public void waitForDashboardPage() {
        // use the instance driver and wait inherited from BasePage
        WaitUtils.waitForUrlContains(this.driver, "#/dashboard");
        this.wait.until(ExpectedConditions.visibilityOfElementLocated(ScheduledProjects));
        LOG.info("Dashboard page loaded");
    }

    /** Reads the count shown on a dashboard card (example: 1,234 or 10/50). */
    public String getCardCount(String cardName) {
        waitForDashboardPage();
        WebElement countElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath(cardCountLocator(cardName))));
        wait.until(d -> !countElement.getText().trim().isEmpty());
        String count = countElement.getText().trim();
        LOG.info("Dashboard card '{}' count: {}", cardName, count);
        return count;
    }

    /** Clicks a dashboard card and waits for its detail page. */
    public void clickCard(String cardName) {
        waitForDashboardPage();
        WebElement card = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath(cardClickLocator(cardName))));
        card.click();
        WaitUtils.waitForUrlContains(driver, "dashboardcardview");
        LOG.info("Opened detail page for '{}'", cardName);
    }


    /** Browser back button, then wait for dashboard again. */
    public void goBackToDashboard() {
        driver.navigate().back();
        waitForDashboardPage();
        LOG.info("Returned to dashboard");
    }

    private String cardClickLocator(String cardName) {
        return "//div[contains(@class,'dashboard-card')]//div[normalize-space()='" + cardName
                + "']/ancestor::div[contains(@class,'cursor-pointer')][1]";
    }

    private String cardCountLocator(String cardName) {
        return cardClickLocator(cardName) + "//div[contains(@class,'text-9xl')]";
    }

}