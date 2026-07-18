package org.rubicon.automation.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LoginPage extends BasePage {

    WebDriver driver;

    public LoginPage(WebDriver driver) {
        super(driver);
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void loginPage(WebDriver driver) {
//		Initialization of Driver
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    //	WebElement userEmail = driver.findElement(By.id("userEmail"));
    //	WebElement userPassword = driver.findElement(By.id("userPassword"));
    @FindBy(name = "username")
    WebElement username;

    @FindBy(name="password")
    WebElement password;

    @FindBy(css = "button[type='submit']")
    WebElement submit;

    @FindBy(xpath = "//div[normalize-space()='Username and/or Password is invalid']")
    WebElement errorMessage;

    @FindBy(xpath = "//button[normalize-space()='OK']")
    WebElement okButton;

    public String getErrorMessage() {
        return errorMessage.getText();
    }

    public boolean isErrorMessageDisplayed() {
        return errorMessage.isDisplayed();
    }

    public void goTo() {
        driver.get("https://stage.rubiconcontractors.net/#/auth/login");
    }

    /**
     * Method to enter username
     */
    public void enterUsername(String userNameValue) {
        this.wait.until(ExpectedConditions.visibilityOf(username));
        username.clear();
        username.sendKeys(userNameValue);
    }

    /**
     * Method to enter password
     */
    public void enterPassword(String passwordValue) {
        this.wait.until(ExpectedConditions.visibilityOf(password));
        password.clear();
        password.sendKeys(passwordValue);
    }

    /**
     * Method to click submit button
     */
    public void clickSubmit() {
        this.wait.until(ExpectedConditions.elementToBeClickable(submit));
        submit.click();
    }

    /**
     * Complete login flow - enter credentials and submit
     */
    public void login(String userNameValue, String passwordValue) {
        enterUsername(userNameValue);
        enterPassword(passwordValue);
        clickSubmit();
    }

    /**
     * Method to click OK button on error message
     */
    public void clickOkButton() {
        this.wait.until(ExpectedConditions.elementToBeClickable(okButton));
        okButton.click();
    }
}
