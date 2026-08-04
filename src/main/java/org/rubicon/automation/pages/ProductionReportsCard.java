package org.rubicon.automation.pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProductionReportsCard extends BasePage {
    public ProductionReportsCard(WebDriver driver) {
        super(driver);
    }
    private static final Logger LOG = LogManager.getLogger(ProductionReportsCard.class);
    private static final Pattern DIGITS = Pattern.compile("\\d+");

    /** Cached counts so comparison works after navigating away from the dashboard. */
    private Integer cachedDashboardCount;
    private Integer cachedPageCount;

    /**
     * Locators for Dashboard Production Reports Card
     */
    private final By cardTitle = By.xpath("//div[normalize-space()='Production Reports']");
    private final By cardContainer = By.xpath(
            "//div[contains(@class,'dashboard-card')]"
                    + "//div[normalize-space()='Production Reports']"
                    + "/ancestor::div[contains(@class,'cursor-pointer')][1]");
    private final By dashboardCardCount = By.xpath(
            "//div[contains(@class,'dashboard-card')]"
                    + "//div[normalize-space()='Production Reports']"
                    + "/ancestor::div[contains(@class,'cursor-pointer')][1]"
                    + "//div[contains(@class,'text-9xl') or contains(@class,'text-8xl') or contains(@class,'text-7xl')]");

    /**
     * Method to verify Dashboard Production Reports card is visible
     */
    public boolean isCardVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(cardTitle));
            LOG.info("Production Reports card is visible");
            return true;
        } catch (Exception e) {
            LOG.warn("Production Reports card is not visible: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extract integer count from the dashboard Production Reports card count element.
     * Waits until the async count text is populated before parsing.
     */
    public int getDashboardProductionReportsCardCount() {
        try {
            WebElement countEl = wait.until(ExpectedConditions.visibilityOfElementLocated(dashboardCardCount));
            wait.until(d -> {
                String t = countEl.getText();
                return t != null && !t.trim().isEmpty();
            });

            String text = countEl.getText().trim();
            LOG.info("Dashboard Production Reports card count text: {}", text);
            cachedDashboardCount = extractFirstInteger(text);
            return cachedDashboardCount;
        } catch (Exception e) {
            LOG.error("Unable to read dashboard card count: {}", e.getMessage());
            throw new RuntimeException("Could not read dashboard card count", e);
        }
    }

    /**
     * Method to click on the Dashboard Production Reports card
     */
    public void clickProductionReportsCard() {
        try {
            WebElement card = wait.until(ExpectedConditions.elementToBeClickable(cardContainer));
            card.click();
            LOG.info("Clicked on Dashboard Production Reports card");
        } catch (Exception e) {
            LOG.error("Failed to click on card: {}", e.getMessage());
            throw new RuntimeException("Could not click on Dashboard Production Reports card", e);
        }
    }

    // Locator for Production Reports page title that contains the count, e.g. "Production Reports (3)"
    private final By productionReportsTitle = By.xpath("//div[contains(@class,'title-ui-design') and contains(normalize-space(),'Production Reports')]");

    /**
     * Extract integer count from the Production Reports page title (e.g. "Production Reports (3)").
     * Waits until the title includes (N) before parsing.
     */
    public int getInnerCardProductionReportsTitleCount() {
        try {
            WebElement titleEl = wait.until(ExpectedConditions.visibilityOfElementLocated(productionReportsTitle));
            wait.until(d -> {
                String t = titleEl.getText();
                return t != null && DIGITS.matcher(t).find();
            });

            String text = titleEl.getText().trim();
            LOG.info("Production Reports title text: {}", text);
            cachedPageCount = extractFirstInteger(text);
            return cachedPageCount;
        } catch (Exception e) {
            LOG.error("Failed to read Production Reports title count: {}", e.getMessage());
            throw new RuntimeException("Could not read Production Reports title count", e);
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
        // Remove thousand separators first so "6,066" and "6066" both parse as 6066.
        // Matching comma-formatted numbers with \\d{1,3} alone incorrectly yields 606 from 6066.
        String normalized = text.replace(",", "");
        Matcher m = DIGITS.matcher(normalized);
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
    public void verifyProductionReportsCountMatches() {
        if (cachedDashboardCount == null) {
            throw new RuntimeException(
                    "Dashboard count was not read yet. Call getDashboardProductionReportsCardCount() first.");
        }
        int productionReportsCount = cachedPageCount != null
                ? cachedPageCount
                : getInnerCardProductionReportsTitleCount();

        if (cachedDashboardCount.equals(productionReportsCount)) {
            LOG.info("Production Reports count matches: {}", cachedDashboardCount);
        } else {
            throw new RuntimeException("Production Reports count mismatch - dashboard: "
                    + cachedDashboardCount + " vs production reports page: " + productionReportsCount);
        }
    }

    // Locators for Export

    private final By exportButton = By.xpath("//button[contains(@class,'button_large') or contains(text(),'Export')]");
    private final By snackBarText = By.xpath("//span[contains(text(),'The data may take some time to export')]");
    private final By backButton = By.xpath("//button[contains(@class, 'back-button-top')]");

    // Export the ProductionReports
    public void exportProductionReports() {
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
            throw new RuntimeException("Could not export production reports", e);
        }
    }

    //Go back to Dashboard Page
    public void goBackToDashboardPage() {
        try {
            WebElement back = wait.until(ExpectedConditions.elementToBeClickable(backButton));
            back.click();
            LOG.info("Going back to dashboard page");
        } catch (Exception e) {
            LOG.error("Error going back to dashboard page: {}", e.getMessage());
            throw new RuntimeException("Could not go back to dashboard page", e);
        }
    }
}
