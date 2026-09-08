# Rubicon Contractors Selenium Automation Framework - Complete Guide

## Overview
This is a complete Selenium automation framework for the Rubicon Contractors application using TestNG, Page Object Model (POM), and comprehensive reporting.

## Framework Architecture

### Layers:
1. **Page Object Layer** - Contains page classes (BasePage, LoginPage, DashboardPage, ScheduledProjectsCard)
2. **Test Layer** - Contains test classes (LoginApplicationTest, DashboardPageTest)
3. **Utilities Layer** - Contains helper methods (WaitUtils, ReportManager)
4. **Base Test Layer** - Contains common test setup/teardown (BaseTest)

## Key Components

### 1. **BasePage.java** (Main Page Object Base Class)
- **Location**: `src/main/java/org/rubicon/automation/pages/BasePage.java`
- **Responsibilities**:
  - Initialize WebDriver
  - Set up explicit waits (20 seconds)
  - Read configuration from `global.properties`
  - Load test data from JSON files
  - Handle screenshots for failed tests

### 2. **LoginPage.java** (Login Page Object)
- **Location**: `src/main/java/org/rubicon/automation/pages/LoginPage.java`
- **Key Methods**:
  - `goTo()` - Navigate to login page
  - `enterUsername(String username)` - Enter username
  - `enterPassword(String password)` - Enter password
  - `clickSubmit()` - Click submit button
  - `login(String username, String password)` - Complete login flow
  - `isErrorMessageDisplayed()` - Check if error message is shown
  - `getErrorMessage()` - Get error message text
  - `clickOkButton()` - Click OK button on error

### 3. **DashboardPage.java** (Dashboard Page Object)
- **Location**: `src/main/java/org/rubicon/automation/pages/DashboardPage.java`
- **Key Methods**:
  - `waitForDashboardPage()` - Wait for dashboard to load
  - `getCardCount(String cardName)` - Get count from a dashboard card
  - `clickCard(String cardName)` - Click on a dashboard card
  - `goBackToDashboard()` - Navigate back to dashboard

### 4. **ScheduledProjectsCard.java** (Specific Card Page Object)
- **Location**: `src/main/java/org/rubicon/automation/pages/ScheduledProjectsCard.java`
- **Key Methods**:
  - `isCardVisible()` - Verify card is visible
  - `getProjectCount()` - Get project count
  - `clickCard()` - Click on the card

### 5. **BaseTest.java** (Base Test Class)
- **Location**: `src/test/java/org/rubicon/base/BaseTest.java`
- **Key Methods**:
  - `intitalizeDriver()` - Initialize WebDriver (Chrome, Firefox, Edge)
  - `getJsonDataToMap()` - Read JSON test data
  - `getScreenShot()` - Capture screenshot on failure
  - `@BeforeMethod launchApplication()` - Launch app before each test
  - `@AfterMethod closeApp()` - Close browser after each test

### 6. **LoginApplicationTest.java** (Login Test Class)
- **Location**: `src/test/java/org/rubicon/tests/LoginApplicationTest.java`
- **Test Methods**:
  - `testSuccessfulLogin()` - Test successful login with valid credentials
  - `testInvalidLoginCredentials()` - Test login with invalid credentials
  - Uses `@DataProvider` to supply test data from LoginData.json

### 7. **DashboardPageTest.java** (Dashboard Test Class)
- **Location**: `src/test/java/org/rubicon/tests/DashboardPageTest.java`
- **Test Methods**:
  - `testDashboardPageLoadsSuccessfully()` - Verify dashboard loads
  - `testScheduledProjectsCardVisible()` - Verify card visibility
  - `testClickDashboardCard()` - Test clicking cards and navigating back
  - `testUserStaysOnDashboard()` - **IMPORTANT**: Verify user stays on dashboard (no logout)

## Test Data

### LoginData.json
- **Location**: `src/main/resources/testdata/LoginData.json`
- **Format**: JSON array with username and password objects
```json
[
  {
    "username": "rubicon@stage",
    "password": "Welcome01"
  },
  {
    "username": "KS@Superadmin",
    "password": "Welcome01"
  }
]
```

## Configuration

### global.properties
- **Location**: `src/main/resources/config/global.properties`
- **Key Properties**:
  - `browser` - Browser to use (chrome, firefox, edge)
  - `environment` - Environment (stage, production)
  - `explicit.wait` - Explicit wait timeout (20 seconds)
  - `headless` - Run browser in headless mode

## Test Flow

### Login Test Flow:
1. Launch application → Navigate to login page
2. Enter username
3. Enter password
4. Click submit
5. Wait for dashboard to load
6. Verify successful login
7. Close browser

### Dashboard Test Flow:
1. Launch application → Navigate to login page
2. Enter credentials and login
3. Wait for dashboard to load
4. Verify dashboard elements
5. Interact with cards (click, get count)
6. **Verify user stays on dashboard (NO LOGOUT)**
7. Close browser

## How to Run Tests

### Run All Tests:
```bash
mvn test
```

### Run Specific Test Class:
```bash
mvn test -Dtest=LoginApplicationTest
mvn test -Dtest=DashboardPageTest
```

### Run Specific Test Method:
```bash
mvn test -Dtest=LoginApplicationTest#testSuccessfulLogin
mvn test -Dtest=DashboardPageTest#testUserStaysOnDashboard
```

### Run Tests with Specific Browser:
```bash
mvn test -Dbrowser=firefox
mvn test -Dbrowser=edge
```

### Run Tests in Headless Mode:
Update `global.properties` or set system property:
```bash
mvn test -Dheadless=true
```

## Key Features

### 1. **Page Object Model (POM)**
- Each page has its own class
- Locators are centralized
- Methods are reusable

### 2. **Data-Driven Testing**
- Tests can be run with multiple datasets
- Uses `@DataProvider` in TestNG
- Test data loaded from JSON files

### 3. **Explicit Waits**
- 20-second explicit wait for element visibility
- Custom `WaitUtils` class for URL-based waits
- Prevents flaky tests

### 4. **Comprehensive Logging**
- Log4j logging at every step
- Logs contain timestamp and log level
- Easy to debug test failures

### 5. **Screenshot Capture**
- Screenshots captured on test failure
- Stored in `reports/` directory
- Integrated with Extent Reports

### 6. **Extent Reports**
- HTML test reports with rich formatting
- Test status (PASS/FAIL/SKIP)
- Screenshots attached to failed tests
- System information (OS, browser, etc.)

### 7. **Test Retry Mechanism**
- `RetryTest.java` implements retry logic
- Failed tests can be automatically retried
- Configurable retry count

### 8. **Test Listeners**
- `TestListener.java` implements test hooks
- Captures screenshots on failure
- Logs test status to Extent Reports

## Important: User Stays on Dashboard

### This Framework Ensures:
✅ User logs in successfully
✅ User navigates to Dashboard
✅ User STAYS on Dashboard (no logout)
✅ User can interact with dashboard elements
✅ Test verifies user is still on dashboard after actions

### No Logout Scenario:
- Tests do NOT call logout functionality
- After test completion, driver is closed
- User session is NOT ended during tests
- `@AfterMethod` in BaseTest closes browser, not logs out

## Troubleshooting

### Issue: Login fails
- **Solution**: Verify credentials in LoginData.json are correct
- Verify application is accessible at the URL
- Check network connectivity

### Issue: Dashboard page doesn't load
- **Solution**: Check if login was successful
- Verify dashboard URL contains `#/dashboard`
- Check explicit wait timeout (20 seconds)

### Issue: NoSuchElementException
- **Solution**: Verify web element locators (XPath, CSS selectors)
- Update locators in page classes if UI changed
- Use browser developer tools to inspect elements

### Issue: Test takes too long
- **Solution**: Check implicit wait (10 seconds) in BasePage
- Reduce explicit wait timeout if needed
- Verify application performance

## Project Structure

```
Rubicon_Selenium_Automation/
├── src/
│   ├── main/
│   │   ├── java/org/rubicon/
│   │   │   └── automation/
│   │   │       ├── pages/
│   │   │       │   ├── BasePage.java
│   │   │       │   ├── LoginPage.java
│   │   │       │   ├── DashboardPage.java
│   │   │       │   └── ScheduledProjectsCard.java
│   │   │       ├── utilities/
│   │   │       │   └── WaitUtils.java
│   │   │       └── reports/
│   │   │           └── ReportManager.java
│   │   └── resources/
│   │       ├── config/
│   │       │   ├── global.properties
│   │       │   └── stage.properties
│   │       └── testdata/
│   │           └── LoginData.json
│   └── test/
│       └── java/org/rubicon/
│           ├── base/
│           │   ├── BaseTest.java
│           │   └── RetryTest.java
│           ├── listeners/
│           │   └── TestListener.java
│           └── tests/
│               ├── LoginApplicationTest.java
│               └── DashboardPageTest.java
├── pom.xml
└── FRAMEWORK_GUIDE.md
```

## Dependencies

- **Selenium**: 4.45.0
- **TestNG**: 7.12.0
- **WebDriverManager**: 6.3.4
- **Extent Reports**: 5.1.2
- **Log4j**: 2.26.1
- **Jackson**: For JSON parsing

## Best Practices Implemented

1. ✅ Page Object Model for maintainability
2. ✅ Explicit waits to prevent flakiness
3. ✅ Centralized locators
4. ✅ Data-driven testing
5. ✅ Comprehensive logging
6. ✅ Screenshot capture on failure
7. ✅ HTML reporting
8. ✅ Retry mechanism for failed tests
9. ✅ Listener pattern for test hooks
10. ✅ No logout - user stays on dashboard

## Next Steps

1. Update `LoginData.json` with actual test user credentials
2. Run tests using `mvn test`
3. Check Extent Reports in `reports/` folder
4. Add more test methods as needed
5. Extend to test other pages and functionalities

## Support

For any issues or questions:
1. Check logs in console output
2. Review Extent Reports HTML
3. Check screenshots in reports folder
4. Verify application URL and credentials
5. Check browser compatibility

---

**Framework Version**: 1.0
**Last Updated**: July 2026
**Author**: Automation Team

