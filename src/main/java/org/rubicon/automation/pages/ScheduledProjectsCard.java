package org.rubicon.automation.pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ScheduledProjectsCard extends BasePage {

    public ScheduledProjectsCard(WebDriver driver) {
        super(driver);
    }

    private static final Logger LOG = LogManager.getLogger(ScheduledProjectsCard.class);

    /**
     * Locators for Scheduled Projects card
     */
    private final By cardTitle = By.xpath("//div[normalize-space()='Scheduled Projects']");
    // Find the specific count element within a container that has "Scheduled Projects" text nearby
    private final By dashboardCardCountContainer = By.xpath("//div[contains(normalize-space(),'Scheduled Projects') and contains(@class,'text-base') or contains(@class,'text-sm')]/preceding-sibling::div[contains(@class,'text-9xl') or contains(@class,'text-8xl') or contains(@class,'text-7xl')]");
    private final By cardContainer = By.xpath("//div[contains(normalize-space(),'Scheduled Projects')]/ancestor::div[contains(@class,'cursor-pointer') or contains(@class,'dashboard-card')][1]");

    /**
     * Method to verify Scheduled Projects card is visible
     */
    public boolean isCardVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(cardTitle));
            LOG.info("Scheduled Projects card is visible");
            return true;
        } catch (Exception e) {
            LOG.warn("Scheduled Projects card is not visible: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Method to click on the Scheduled Projects card
     */
    public void clickCard() {
        try {
            WebElement card = wait.until(ExpectedConditions.elementToBeClickable(cardContainer));
            card.click();
            LOG.info("Clicked on Scheduled Projects card");
        } catch (Exception e) {
            LOG.error("Failed to click on card: {}", e.getMessage());
            throw new RuntimeException("Could not click on Scheduled Projects card", e);
        }
    }

    // Locators for Search and Export
    private final By searchInput = By.xpath("//input[@placeholder='Search']");
    private final By refreshButton = By.xpath("//button[contains(@class,'white_btn')][.//img[@alt='refresh']]");
    private final By exportButton = By.xpath("//button[contains(@class,'button_large') or contains(text(),'Export')]");
    private final By snackBarText = By.xpath("//span[contains(text(),'The data may take some time to export')]");

    // Locator for Scheduled Projects page title that contains the count, e.g. "Scheduled Projects (4)"
    private final By scheduledProjectsTitle = By.xpath("//div[contains(@class,'title-ui-design') and contains(normalize-space(),'Scheduled Projects')]");

    // Search for the Project
    public void searchProject(String projectName) {
        try {
            WebElement search = wait.until(ExpectedConditions.elementToBeClickable(searchInput));
            search.clear();
            search.sendKeys(projectName);
            LOG.info("Searching for project: {}", projectName);
            Thread.sleep(2000);
        } catch (Exception e) {
            LOG.error("Error during project search: {}", e.getMessage());
            throw new RuntimeException("Could not search for project: " + projectName, e);
        }
    }

    // Page Refresh
    public void pageRefresh() {
        try {
            WebElement refresh = wait.until(ExpectedConditions.elementToBeClickable(refreshButton));
            refresh.click();
            LOG.info("Page refreshed and project list reloaded");
            // Wait for page to reload
            Thread.sleep(2000);
        } catch (Exception e) {
            LOG.error("Error during page refresh: {}", e.getMessage());
            throw new RuntimeException("Could not refresh page", e);
        }
    }

    // Export the ScheduledProjects
    public void exportScheduledProjects() {
        try {
            WebElement export = wait.until(ExpectedConditions.elementToBeClickable(exportButton));
            export.click();
            LOG.info("Export button clicked");
            Thread.sleep(1500);

            try {
                WebElement snackBar = wait.until(ExpectedConditions.visibilityOfElementLocated(snackBarText));
                String actualText = snackBar.getText();
                LOG.info("Snackbar text: {}", actualText);

                if (!actualText.contains("The data may take some time to export")) {
                    throw new AssertionError("Expected success message not found. Actual: " + actualText);
                }
            } catch (Exception e) {
                LOG.warn("Snackbar message not found, but export may have been initiated");
            }
        } catch (Exception e) {
            LOG.error("Error during export: {}", e.getMessage());
            throw new RuntimeException("Could not export scheduled projects", e);
        }
    }

    /**
     * Extract integer count from the dashboard card count element.
     */
    public int getDashboardCardCount() {
        try {
            // Locate the dashboard card container first
            WebElement container = wait.until(ExpectedConditions.visibilityOfElementLocated(cardContainer));

            // Look for a large-number element inside the card (classes like text-9xl / text-8xl / text-7xl)
            List<WebElement> largeTextDivs = container.findElements(By.xpath(".//*[contains(@class,'text-9xl') or contains(@class,'text-8xl') or contains(@class,'text-7xl')]") );

            for (WebElement div : largeTextDivs) {
                String text = div.getText().trim();
                if (!text.isEmpty()) {
                    try {
                        return extractFirstInteger(text);
                    } catch (RuntimeException pe) {
                        // continue if this element doesn't contain a parsable number
                        LOG.debug("Found element but not a number: {}", text);
                    }
                }
            }

            // Fallback: try the more specific locator
            WebElement countEl = wait.until(ExpectedConditions.visibilityOfElementLocated(dashboardCardCountContainer));
            String text = countEl.getText().trim();
            return extractFirstInteger(text);
        } catch (Exception e) {
            LOG.error("Unable to read dashboard card count: {}", e.getMessage());
            throw new RuntimeException("Could not read dashboard card count", e);
        }
    }


    /**
     * Extract integer count from the Scheduled Projects page title (e.g. "Scheduled Projects (4)").
     */
    public int getScheduledProjectsTitleCount() {
        try {
            WebElement titleEl = wait.until(ExpectedConditions.visibilityOfElementLocated(scheduledProjectsTitle));
            String text = titleEl.getText().trim();
            LOG.info("Scheduled Projects title text: {}", text);
            return extractFirstInteger(text);
        } catch (Exception e) {
            LOG.error("Failed to read Scheduled Projects title count: {}", e.getMessage());
            throw new RuntimeException("Could not read Scheduled Projects title count", e);
        }
    }

    /**
     * Compare the dashboard card count with the Scheduled Projects page count.
     * Returns true when they match, false otherwise.
     */
    public boolean verifyScheduledProjectsCountMatches() {
        int dashboardCount = getDashboardCardCount();
        int scheduledCount = getScheduledProjectsTitleCount();
        if (dashboardCount == scheduledCount) {
            LOG.info("Scheduled Projects count matches: {}", dashboardCount);
            return true;
        } else {
            LOG.error("Scheduled Projects count mismatch - dashboard: {} vs scheduled page: {}", dashboardCount, scheduledCount);
            return false;
        }
    }

    /**
     * Extract the first integer found in the given text.
     * This is a small, focused helper to keep the caller code readable.
     * If no integer is present, this method throws a RuntimeException so callers fail-fast.
     */
    private static int extractFirstInteger(String text) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Text to parse is null or empty");
        }
        Matcher m = Pattern.compile("\\d+").matcher(text);
        if (m.find()) {
            try {
                return Integer.parseInt(m.group());
            } catch (NumberFormatException nfe) {
                throw new RuntimeException("Failed to parse number from text: " + text, nfe);
            }
        }
        throw new RuntimeException("No numeric count found in text: " + text);
    }

}
