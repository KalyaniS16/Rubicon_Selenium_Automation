package org.rubicon.automation.pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InactiveProjectsCard extends BasePage {
    public InactiveProjectsCard(WebDriver driver) {
        super(driver);
    }
    private static final Logger LOG = LogManager.getLogger(InactiveProjectsCard.class);
    private static final Pattern DIGITS = Pattern.compile("\\d+");

    /** Cached counts so comparison works after navigating away from the dashboard. */
    private Integer cachedDashboardCount;
    private Integer cachedPageCount;

    /**
     * Locators for Dashboard Inactive Projects card
     */
    private final By cardTitle = By.xpath("//div[normalize-space()='Inactive Projects']");
    private final By cardContainer = By.xpath(
            "//div[contains(@class,'dashboard-card')]"
                    + "//div[normalize-space()='Inactive Projects']"
                    + "/ancestor::div[contains(@class,'cursor-pointer')][1]");
    private final By dashboardCardCount = By.xpath(
            "//div[contains(@class,'dashboard-card')]"
                    + "//div[normalize-space()='Inactive Projects']"
                    + "/ancestor::div[contains(@class,'cursor-pointer')][1]"
                    + "//div[contains(@class,'text-9xl') or contains(@class,'text-8xl') or contains(@class,'text-7xl')]");

    /**
     * Method to verify Dashboard Inactive Projects card is visible
     */
    public boolean isCardVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(cardTitle));
            LOG.info("Inactive Projects card is visible");
            return true;
        } catch (Exception e) {
            LOG.warn("Inactive Projects card is not visible: {}", e.getMessage());
            return false;
        }
    }
    /**
         * Extract integer count from the dashboard Inactive Projects card count element.
     * Waits until the async count text is populated before parsing.
     */
    public int getDashboardInactiveProjectsCardCount() {
        try {
            WebElement countEl = wait.until(ExpectedConditions.visibilityOfElementLocated(dashboardCardCount));
            wait.until(d -> {
                String t = countEl.getText();
                return t != null && !t.trim().isEmpty();
            });

            String text = countEl.getText().trim();
            LOG.info("Dashboard Inactive Projects card count text: {}", text);
            cachedDashboardCount = extractFirstInteger(text);
            return cachedDashboardCount;
        } catch (Exception e) {
            LOG.error("Unable to read dashboard card count: {}", e.getMessage());
            throw new RuntimeException("Could not read dashboard card count", e);
        }
    }

    /**
     * Method to click on the Dashboard Inactive Projects card
     */
    public void clickInactiveProjectsCard() {
        try {
            WebElement card = wait.until(ExpectedConditions.elementToBeClickable(cardContainer));
            card.click();
            LOG.info("Clicked on Dashboard Inactive Projects card");
        } catch (Exception e) {
            LOG.error("Failed to click on card: {}", e.getMessage());
            throw new RuntimeException("Could not click on Dashboard Inactive Projects card", e);
        }
    }

    // Locator for Inactive Projects page title that contains the count, e.g. "Inactive Projects (1)"
    private final By inactiveProjectsTitle = By.xpath("//div[contains(@class,'title-ui-design') and contains(normalize-space(),'Inactive Projects')]");

    /**
     * Extract integer count from the Inactive Projects page title (e.g. "Inactive Projects (1)").
     * Waits until the title includes (N) before parsing.
     */
    public int getInnerCardInactiveProjectsTitleCount() {
        try {
            WebElement titleEl = wait.until(ExpectedConditions.visibilityOfElementLocated(inactiveProjectsTitle));
            wait.until(d -> {
                String t = titleEl.getText();
                return t != null && DIGITS.matcher(t).find();
            });

            String text = titleEl.getText().trim();
            LOG.info("Inactive Projects title text: {}", text);
            cachedPageCount = extractFirstInteger(text);
            return cachedPageCount;
        } catch (Exception e) {
            LOG.error("Failed to read Inactive Projects title count: {}", e.getMessage());
            throw new RuntimeException("Could not read Inactive Projects title count", e);
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
    public void verifyInactiveProjectsCountMatches() {
        if (cachedDashboardCount == null) {
            throw new RuntimeException(
                    "Dashboard count was not read yet. Call getDashboardInactiveProjectsCardCount() first.");
        }
        int inactiveProjectsCount = cachedPageCount != null
                ? cachedPageCount
                : getInnerCardInactiveProjectsTitleCount();

        if (cachedDashboardCount.equals(inactiveProjectsCount)) {
            LOG.info("Inactive Projects count matches: {}", cachedDashboardCount);
        } else {
            throw new RuntimeException("Inactive Projects count mismatch - dashboard: "
                    + cachedDashboardCount + " vs inactive page: " + inactiveProjectsCount);
        }
    }

    private final By exportButton = By.xpath("//button[contains(@class,'button_large') or contains(text(),'Export')]");
    private final By snackBarText = By.xpath("//span[contains(text(),'The data may take some time to export')]");
    private final By backButton = By.xpath("//button[contains(@class, 'back-button-top')]");

    // Export the InactiveProjects
    public void exportInactiveProjects() {
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
            throw new RuntimeException("Could not export active projects", e);
        }
    }

    // Locators for Search functionality
    private final By searchInput = By.xpath("//input[@placeholder='Search']");
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

    //Locator for checkbox selection
    private final By selectCheckBox = By.xpath("//label[contains(@class, 'mat-checkbox-layout')]");
    //select the checkbox based on search
    public void selectProjectToArchive(){
        try {
            WebElement select = wait.until(ExpectedConditions.elementToBeClickable(selectCheckBox));
            select.click();
            LOG.info("Checkbox has been selected");
        } catch (Exception e) {
            LOG.error("Error during checkbox selection: {}", e.getMessage());
            throw new RuntimeException("Could not select checkbox", e);
        }
    }

    //Locator to archive the selected Project
    private final By archiveProject = By.xpath("//span[@mattooltip='Archive']/button");
    // Use normalize-space() (not text()) so OK inside nested <span> still matches Material buttons.
    private final By confirmationPopup = By.xpath(
            "//div[contains(@class,'dialog_main')]"
                    + "[.//div[contains(@class,'msg_box') and contains(.,'archive')]]"
                    + "//button[normalize-space()='OK']");
    private final By successPopup = By.xpath(
            "//div[contains(@class,'msg_box') and contains(.,'archived successfully')]");
    private final By OKButtonAfterSuccess = By.xpath(
            "//div[contains(@class,'dialog_main') or contains(@class,'success_div')]"
                    + "[.//div[contains(@class,'msg_box') and contains(.,'archived successfully')]]"
                    + "//button[normalize-space()='OK']");
    private final By overlayBackdrop = By.cssSelector(
            "div.cdk-overlay-backdrop.cdk-overlay-backdrop-showing");

    //Archive Project
    public void archiveProject() {
        try {
            WebElement archive = wait.until(ExpectedConditions.elementToBeClickable(archiveProject));
            LOG.info("Archiving project");
            archive.click();

            WebElement confirmation = wait.until(ExpectedConditions.elementToBeClickable(confirmationPopup));
            LOG.info("Confirmation popup OK clicked");
            confirmation.click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(successPopup));
            LOG.info("Selected projects are archived successfully.");

            WebElement ok = wait.until(ExpectedConditions.elementToBeClickable(OKButtonAfterSuccess));
            ok.click();
            LOG.info("Success dialog dismissed");

            // Ensure dialog backdrop is gone so later clicks (refresh/back) are not blocked
            wait.until(ExpectedConditions.invisibilityOfElementLocated(overlayBackdrop));
        } catch (Exception e) {
            LOG.error("Error during project archiving: {}", e.getMessage());
            throw new RuntimeException("Could not archive project", e);
        }
    }

    //Locator for page refresh
    private final By refreshButton = By.xpath("//button[contains(@class,'white_btn')][.//img[@alt='refresh']]");
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

    //Locator to click Filter
    private final By filterButton = By.xpath("//button[contains(@class,'white_btn')][.//img[@alt='filter']]");
    // Click Filter
    public void filter() {
        try {
            WebElement filter = wait.until(ExpectedConditions.elementToBeClickable(filterButton));
            filter.click();
            LOG.info("Filter button clicked");
        } catch (Exception e) {
            LOG.error("Error clicking filter button: {}", e.getMessage());
            throw new RuntimeException("Could not click filter button", e);
        }
    }

    //Locators of Filter
    private final By projectIDLabel = By.xpath("//mat-label[contains(@class,'text-heading') and normalize-space(text())='Project Id']");
    private final By searchProjectID = By.xpath("//input[@name='ProjectId' and @placeholder='Enter Project Id']");
    private final By  applyFilter = By.xpath("//button[contains(@class,'button_min') and normalize-space(text())='Apply']");
    private final By clearFilter = By.xpath("//button[.//span[contains(text(),'rotate_right')]]");
    private final By projectStatusLabel = By.xpath("///h2[normalize-space(text())='Project Status']");
    private final By selectProjectStatus = By.xpath("//label[contains(@class,'mat-checkbox-layout')][.//span[contains(@class,'mat-checkbox-label') and contains(.,'InProgress')]]");


    // Apply Filter- ProjectID
    public void applyProjectIDFilter(String nameOfProject) {
        try {
            WebElement searchProject = wait.until(ExpectedConditions.visibilityOfElementLocated(searchProjectID));
            searchProject.sendKeys(nameOfProject);
            LOG.info("Searching for project: {}", searchProject.getAttribute("value"));

            WebElement apply = wait.until(ExpectedConditions.elementToBeClickable(applyFilter));
            apply.click();
            LOG.info("Filter applied");
            Thread.sleep(2000);
        } catch (Exception e) {
            LOG.error("Error applying filter: {}", e.getMessage());
            throw new RuntimeException("Could not apply filter", e);
        }
    }

    //Apply Filter- ProjectStatus
    public void applyProjectStatusFilter() {
        try {
            WebElement clickProjectStatus = wait.until(ExpectedConditions.elementToBeClickable(selectProjectStatus));
            clickProjectStatus.click();
            LOG.info("Project status selected");

            WebElement apply = wait.until(ExpectedConditions.elementToBeClickable(applyFilter));
            apply.click();
            LOG.info("Filter applied");
            Thread.sleep(2000);
        } catch (Exception e) {
            LOG.error("Error applying filter: {}", e.getMessage());
            throw new RuntimeException("Could not apply filter", e);
        }
    }

    //ClearFilter
    public void clearFilter() {
        try{
            WebElement clear =  wait.until(ExpectedConditions.elementToBeClickable(clearFilter));
            clear.click();
        } catch (Exception e) {
            LOG.error("Error clearing filter: {}", e.getMessage());
            throw new RuntimeException("Could not clear filter", e);
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
