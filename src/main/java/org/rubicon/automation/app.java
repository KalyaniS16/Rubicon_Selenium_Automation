package org.rubicon.automation;

/**
 * Main application class for Rubicon Contractors Selenium Automation Framework
 *
 * This class serves as the entry point for the automation framework.
 * It provides version information and basic framework details.
 *
 * Framework Details:
 * - Framework Type: Selenium with TestNG
 * - Application: Rubicon Contractors
 * - URL: https://stage.rubiconcontractors.net
 * - Reporting: Extent Reports
 * - Logging: Log4j
 *
 * @author Kalyani Sajanpawar
 * @version 1.0
 */
public class app {

    public static final String APPLICATION_NAME = "Rubicon Contractors";
    public static final String APPLICATION_URL = "https://stage.rubiconcontractors.net";
    public static final String LOGIN_PAGE_PATH = "#/auth/login";
    public static final String DASHBOARD_PAGE_PATH = "#/dashboard";
    public static final String FRAMEWORK_VERSION = "1.0";

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("Rubicon Contractors Selenium Automation");
        System.out.println("========================================");
        System.out.println("Application: " + APPLICATION_NAME);
        System.out.println("URL: " + APPLICATION_URL);
        System.out.println("Version: " + FRAMEWORK_VERSION);
        System.out.println("Framework: Selenium + TestNG + Cucumber");
        System.out.println("Reporting: Extent Reports");
        System.out.println("Logging: Log4j");
        System.out.println("========================================");
        System.out.println("Run tests using Maven: mvn test");
        System.out.println("========================================");
    }
}
