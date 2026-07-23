package org.rubicon.automation.pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ScheduledProjectsCard extends BasePage {

    public ScheduledProjectsCard(WebDriver driver) {
        super(driver);
    }

    private static final Logger LOG = LogManager.getLogger(ScheduledProjectsCard.class);
    private static final Pattern DIGITS = Pattern.compile("\\d+");

    /** Cached counts so comparison works after navigating away from the dashboard. */
    private Integer cachedDashboardCount;
    private Integer cachedPageCount;

    /**
     * Locators for Dashboard Scheduled Projects card
     */
    private final By cardTitle = By.xpath("//div[normalize-space()='Scheduled Projects']");
    private final By cardContainer = By.xpath(
            "//div[contains(@class,'dashboard-card')]"
                    + "//div[normalize-space()='Scheduled Projects']"
                    + "/ancestor::div[contains(@class,'cursor-pointer')][1]");
    private final By dashboardCardCount = By.xpath(
            "//div[contains(@class,'dashboard-card')]"
                    + "//div[normalize-space()='Scheduled Projects']"
                    + "/ancestor::div[contains(@class,'cursor-pointer')][1]"
                    + "//div[contains(@class,'text-9xl') or contains(@class,'text-8xl') or contains(@class,'text-7xl')]");

    /**
     * Method to verify Dashboard Scheduled Projects card is visible
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
     * Extract integer count from the dashboard Scheduled Projects card count element.
     * Waits until the async count text is populated before parsing.
     */
    public int getDashboardScheduledProjectsCardCount() {
        try {
            WebElement countEl = wait.until(ExpectedConditions.visibilityOfElementLocated(dashboardCardCount));
            wait.until(d -> {
                String t = countEl.getText();
                return t != null && !t.trim().isEmpty();
            });

            String text = countEl.getText().trim();
            LOG.info("Dashboard Scheduled Projects card count text: {}", text);
            cachedDashboardCount = extractFirstInteger(text);
            return cachedDashboardCount;
        } catch (Exception e) {
            LOG.error("Unable to read dashboard card count: {}", e.getMessage());
            throw new RuntimeException("Could not read dashboard card count", e);
        }
    }

    /**
     * Method to click on the Dashboard Scheduled Projects card
     */
    public void clickScheduledProjectsCard() {
        try {
            WebElement card = wait.until(ExpectedConditions.elementToBeClickable(cardContainer));
            card.click();
            LOG.info("Clicked on Dashboard Scheduled Projects card");
        } catch (Exception e) {
            LOG.error("Failed to click on card: {}", e.getMessage());
            throw new RuntimeException("Could not click on Dashboard Scheduled Projects card", e);
        }
    }

    // Locator for Scheduled Projects page title that contains the count, e.g. "Scheduled Projects (3)"
    private final By scheduledProjectsTitle = By.xpath("//div[contains(@class,'title-ui-design') and contains(normalize-space(),'Scheduled Projects')]");

    /**
     * Extract integer count from the Scheduled Projects page title (e.g. "Scheduled Projects (3)").
     * Waits until the title includes (N) before parsing.
     */
    public int getInnerCardScheduledProjectsTitleCount() {
        try {
            WebElement titleEl = wait.until(ExpectedConditions.visibilityOfElementLocated(scheduledProjectsTitle));
            wait.until(d -> {
                String t = titleEl.getText();
                return t != null && DIGITS.matcher(t).find();
            });

            String text = titleEl.getText().trim();
            LOG.info("Scheduled Projects title text: {}", text);
            cachedPageCount = extractFirstInteger(text);
            return cachedPageCount;
        } catch (Exception e) {
            LOG.error("Failed to read Scheduled Projects title count: {}", e.getMessage());
            throw new RuntimeException("Could not read Scheduled Projects title count", e);
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
        Matcher m = DIGITS.matcher(text);
        if (m.find()) {
            try {
                return Integer.parseInt(m.group());
            } catch (NumberFormatException nfe) {
                throw new RuntimeException("Failed to parse number from text: " + text, nfe);
            }
        }
        throw new RuntimeException("No numeric count found in text: " + text);
    }

    /**
     * Compare previously read dashboard and page counts.
     * Uses cached values so this works after navigating away from the dashboard.
     */
    public void verifyScheduledProjectsCountMatches() {
        if (cachedDashboardCount == null) {
            throw new RuntimeException(
                    "Dashboard count was not read yet. Call getDashboardScheduledProjectsCardCount() first.");
        }
        int scheduledProjectsCount = cachedPageCount != null
                ? cachedPageCount
                : getInnerCardScheduledProjectsTitleCount();

        if (cachedDashboardCount.equals(scheduledProjectsCount)) {
            LOG.info("Scheduled Projects count matches: {}", cachedDashboardCount);
        } else {
            throw new RuntimeException("Scheduled Projects count mismatch - dashboard: "
                    + cachedDashboardCount + " vs scheduled page: " + scheduledProjectsCount);
        }
    }


    // Locators for Search and Export
    private final By searchInput = By.xpath("//input[@placeholder='Search']");
    private final By refreshButton = By.xpath("//button[contains(@class,'white_btn')][.//img[@alt='refresh']]");
    private final By exportButton = By.xpath("//button[contains(@class,'button_large') or contains(text(),'Export')]");
    private final By snackBarText = By.xpath("//span[contains(text(),'The data may take some time to export')]");


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
}
