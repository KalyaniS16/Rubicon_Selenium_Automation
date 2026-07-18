package org.rubicon.automation.utilities;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class WaitUtils {

    public static WebDriver driver;
    public static WebDriverWait wait;

    public static void waitForUrlContains(WebDriver driver, String urlPart) {
        new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.urlContains(urlPart));
    }
}
