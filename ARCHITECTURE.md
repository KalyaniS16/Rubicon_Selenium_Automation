# Framework Architecture & Visual Guide

## Complete Framework Architecture

```
┌─────────────────────────────────────────────────────────────┐
│         RUBICON SELENIUM AUTOMATION FRAMEWORK                │
└─────────────────────────────────────────────────────────────┘

┌──────────────────────┐
│  TEST LAYER          │
├──────────────────────┤
│ LoginApplicationTest │ ──→ Tests login functionality
├──────────────────────┤
│ DashboardPageTest    │ ──→ Tests dashboard & user stays logged in
└──────────────────────┘
        ↓
┌──────────────────────┐
│  PAGE OBJECT LAYER   │
├──────────────────────┤
│ LoginPage            │ ──→ Login page interactions
├──────────────────────┤
│ DashboardPage        │ ──→ Dashboard interactions
├──────────────────────┤
│ ScheduledProjectsCard│ ──→ Card-specific interactions
├──────────────────────┤
│ BasePage (extends)   │ ──→ Base functionality
└──────────────────────┘
        ↓
┌──────────────────────┐
│  BASE LAYER          │
├──────────────────────┤
│ BaseTest             │ ──→ Test lifecycle (@Before/@After)
├──────────────────────┤
│ BasePage             │ ──→ Driver, waits, utilities
└──────────────────────┘
        ↓
┌──────────────────────┐
│  UTILITY LAYER       │
├──────────────────────┤
│ WaitUtils            │ ──→ Wait methods
├──────────────────────┤
│ ReportManager        │ ──→ Extent Reports generation
└──────────────────────┘
        ↓
┌──────────────────────┐
│  SELENIUM/DRIVERS    │
├──────────────────────┤
│ WebDriver            │ ──→ Chrome/Firefox/Edge
└──────────────────────┘
```

## Test Execution Flow Diagram

```
START TEST RUN
    ↓
INITIALIZE MAVEN
    ↓
COMPILE SOURCES
    ├─ LoginApplicationTest.java ✅
    ├─ DashboardPageTest.java ✅
    ├─ BaseTest.java ✅
    └─ Page classes ✅
    ↓
FOR EACH TEST METHOD:
    ↓
    ├─ @BeforeMethod (BaseTest.launchApplication())
    │  ├─ Initialize WebDriver
    │  ├─ Create LoginPage object
    │  └─ Navigate to login URL
    │  Browser shows: https://stage.rubiconcontractors.net/#/auth/login
    │  ↓
    │  ├─ TEST METHOD RUNS
    │  │
    │  │  Example: testSuccessfulLogin()
    │  │  ├─ Login with credentials
    │  │  ├─ Create DashboardPage object
    │  │  ├─ Wait for dashboard
    │  │  └─ Assert success
    │  │  Browser shows: https://stage.rubiconcontractors.net/#/dashboard
    │  │
    │  ├─ Example: testUserStaysOnDashboard()
    │  │  ├─ Verify on dashboard
    │  │  ├─ Simulate user activity
    │  │  ├─ Verify still on dashboard
    │  │  └─ Verify NO logout occurred
    │  │
    │  ├─ @AfterMethod (BaseTest.closeApp())
    │  │  └─ driver.quit() ← Close browser
    │  │
    │  └─ Listener captures:
    │     ├─ Test status (PASS/FAIL)
    │     ├─ Screenshots (on failure)
    │     └─ Logs
    ↓
GENERATE EXTENT REPORTS
    └─ reports/index.html ← View in browser
    ↓
END TEST RUN
```

## Class Inheritance Hierarchy

```
┌─────────────────────────────────────────────────────────┐
│                    BasePage                             │
│                                                         │
│  - WebDriver driver                                     │
│  - WebDriverWait wait                                   │
│  - initializeDriver()                                   │
│  - getJsonDataToMap()                                   │
│  - getScreenShot()                                      │
└─────────────────────────────────────────────────────────┘
                    ↑
        ┌───────────┼───────────┬───────────────┐
        │           │           │               │
┌───────────────┐ ┌──────────────────┐ ┌─────────────────────┐
│  LoginPage    │ │ DashboardPage    │ │ScheduledProjectsCard│
│               │ │                  │ │                     │
│ + login()     │ │ + waitFor..()    │ │ + isCardVisible()   │
│ + enter..()   │ │ + getCardCount() │ │ + getProjectCount() │
│ + clickSubmit │ │ + clickCard()    │ │ + clickCard()       │
│ + isError..() │ │ + goBackTo..()   │ │ + isCardClickable() │
└───────────────┘ └──────────────────┘ └─────────────────────┘

┌─────────────────────────────────────────────────────────┐
│                    BaseTest                             │
│                                                         │
│  - WebDriver driver                                     │
│  - LoginPage landingPage                                │
│  - @BeforeMethod launchApplication()                    │
│  - @AfterMethod closeApp()                              │
│  - intitalizeDriver()                                   │
│  - getJsonDataToMap()                                   │
└─────────────────────────────────────────────────────────┘
                    ↑
        ┌───────────┴───────────┐
        │                       │
┌─────────────────────────┐ ┌─────────────────────────┐
│ LoginApplicationTest    │ │ DashboardPageTest       │
│                         │ │                         │
│ + testSuccessfulLogin() │ │ + testDashboardLoads()  │
│ + testInvalidLogin()    │ │ + testCardVisible()     │
│ + getLoginData()        │ │ + testClickCard()       │
│                         │ │ + testUserStays...()    │
└─────────────────────────┘ └─────────────────────────┘
```

## Data Flow Diagram

```
┌────────────────────────┐
│  LoginData.json        │
│  - username            │
│  - password            │
└────────────────────────┘
         ↓
┌────────────────────────┐
│  @DataProvider         │
│  getLoginData()        │
│  Reads JSON file       │
│  Creates HashMap       │
└────────────────────────┘
         ↓
┌────────────────────────┐
│  @Test Method          │
│  receives HashMap      │
│  with credentials      │
└────────────────────────┘
         ↓
┌────────────────────────┐
│  LoginPage.login()     │
│  Uses credentials      │
└────────────────────────┘
         ↓
┌────────────────────────┐
│  Test Executes         │
│  with test data        │
└────────────────────────┘
```

## Component Interaction Diagram

```
TEST METHOD
    ↓
    └─→ LoginPage
         ├─ goTo()
         │  └─→ driver.get(URL)
         ├─ enterUsername()
         │  └─→ wait.until(elementVisible)
         │      WebElement.sendKeys()
         ├─ enterPassword()
         │  └─→ wait.until(elementVisible)
         │      WebElement.sendKeys()
         └─ clickSubmit()
            └─→ wait.until(elementClickable)
                WebElement.click()
    ↓
    └─→ DashboardPage
         ├─ waitForDashboardPage()
         │  └─→ WaitUtils.waitForUrlContains()
         │      wait.until(elementVisible)
         └─ getCardCount()
            └─→ wait.until(elementVisible)
                WebElement.getText()
```

## Test Sequence for Login Test

```
TEST: testSuccessfulLogin()

1. @BeforeMethod
   Browser opens
   Navigate to: https://stage.rubiconcontractors.net/#/auth/login
   ↓

2. Test executes
   LoginPage loginPage = new LoginPage(driver);
   ↓
   loginPage.login("rubicon@stage", "Welcome01");
   ├─ Find username field (wait up to 20 seconds)
   ├─ Clear and enter username
   ├─ Find password field (wait up to 20 seconds)
   ├─ Clear and enter password
   ├─ Find submit button (wait up to 20 seconds)
   └─ Click submit button
   ↓
   Page navigates to dashboard
   Browser shows: https://stage.rubiconcontractors.net/#/dashboard
   ↓
   DashboardPage dashboardPage = new DashboardPage(driver);
   dashboardPage.waitForDashboardPage();
   ├─ Wait for URL to contain "#/dashboard"
   └─ Wait for Scheduled Projects card to be visible
   ↓
   Assert.assertTrue(true) ✅
   ↓

3. @AfterMethod
   driver.quit()
   Browser closes
   ↓

4. Listener captures results
   Test: PASSED ✅
   Status: PASS
   Time: 5 seconds
   ↓

5. Report generation
   Add result to Extent Reports
   reports/index.html
```

## Test Sequence for Dashboard Stay Test

```
TEST: testUserStaysOnDashboard()

1. @BeforeMethod (DashboardPageTest override)
   Browser opens
   Navigate to login page
   Login with credentials
   Navigate to dashboard ✅
   ↓

2. Test executes
   dashboardPage.waitForDashboardPage();
   → Wait for dashboard to load ✅
   ↓
   String dashboardUrl = driver.getCurrentUrl();
   → dashboardUrl = "https://stage.rubiconcontractors.net/#/dashboard"
   ↓
   Thread.sleep(2000);
   → Simulate user doing activities ✅
   ↓
   String stillOnDashboard = driver.getCurrentUrl();
   → stillOnDashboard = "https://stage.rubiconcontractors.net/#/dashboard"
   ↓
   Assert.assertEquals(stillOnDashboard, dashboardUrl) ✅
   → URL has NOT changed, user is still on dashboard!
   ↓
   Assert.assertFalse(stillOnDashboard.contains("logout")) ✅
   → No logout in URL, logout didn't happen!
   ↓
   Assert.assertFalse(stillOnDashboard.contains("login")) ✅
   → Not on login page, user is still logged in!
   ↓

3. @AfterMethod
   driver.quit()
   Browser closes
   ↓

4. Result
   User was on dashboard when browser closed
   USER STAYED ON DASHBOARD (no logout) ✅✅✅
```

## Wait Mechanism (Explicit Waits)

```
┌─────────────────────────────────────────┐
│     WebDriverWait (20 seconds timeout)   │
│                                          │
│  Initialized in BasePage constructor:   │
│  new WebDriverWait(driver,               │
│      Duration.ofSeconds(20))             │
└─────────────────────────────────────────┘
            ↓
Used in every method:

wait.until(ExpectedConditions.visibilityOf(element))
├─ Checks every 500ms (default)
├─ Returns when element is visible
├─ Throws TimeoutException if > 20 seconds
└─ Prevents "Element not found" errors

wait.until(ExpectedConditions.elementToBeClickable(element))
├─ Checks every 500ms
├─ Returns when element is clickable
├─ Throws TimeoutException if > 20 seconds
└─ Prevents stale element errors
```

## Report Generation Flow

```
Test Executes
    ↓
Listener.onTestStart()
    └─ Create ExtentTest
    ↓
Test Method
    ├─ Logs actions
    └─ Captures screenshots
    ↓
Test Completes
    ├─ Success?
    │  └─ Listener.onTestSuccess()
    │     Log: "Test PASSED"
    │     Status: PASS (Green)
    │
    └─ Failure?
       └─ Listener.onTestFailure()
          Log: "Test FAILED"
          Attach: Screenshot
          Status: FAIL (Red)
    ↓
ExtentReports.flush()
    ↓
Generate: reports/index.html
    ├─ Test summary
    ├─ Pass/Fail count
    ├─ Test details
    ├─ Screenshots
    ├─ Execution time
    └─ System info
    ↓
Open in browser to view
```

## File Organization

```
src/
├── main/
│   ├── java/org/rubicon/automation/
│   │   ├── pages/
│   │   │   ├── BasePage.java ────────┐
│   │   │   ├── LoginPage.java        │ Page Objects
│   │   │   ├── DashboardPage.java    │
│   │   │   └── ScheduledProjectsCard │
│   │   │   ↑                         ↓
│   │   ├── utilities/
│   │   │   └── WaitUtils.java ───────┐
│   │   │       (Shared utilities)    │
│   │   ├── reports/                  │
│   │   │   └── ReportManager.java    │ Utilities
│   │   │       (Report generation)   │
│   │   └── app.java                  │
│   │       (Main class)              ↓
│   │
│   └── resources/
│       ├── config/
│       │   └── global.properties ────┐
│       │       (Browser, env, wait)  │
│       │                             │
│       └── testdata/                 │ Configuration
│           └── LoginData.json        │ & Test Data
│               (Credentials)         ↓
│
└── test/
    └── java/org/rubicon/
        ├── base/
        │   ├── BaseTest.java ────────┐
        │   │   (Test lifecycle)      │
        │   └── RetryTest.java        │ Test Base
        │       (Retry logic)         │
        │                             │
        ├── listeners/                │
        │   └── TestListener.java ────┐ Listeners
        │       (Capture results)     │
        │                             │
        └── tests/                    │
            ├── LoginApplicationTest  │ Test Classes
            └── DashboardPageTest     ↓
```

## Execution Timeline

```
00:00s → START
00:01s → Maven starts
00:02s → Compile sources
00:05s → Initialize test suite
        
        FOR LoginApplicationTest
00:06s → Launch browser
00:07s → Navigate to login page
00:08s → Enter credentials
00:09s → Submit form
00:10s → Wait for dashboard
00:11s → Assert success
00:12s → Close browser
        
        FOR DashboardPageTest  
00:13s → Launch browser
00:14s → Navigate to login
00:15s → Login
00:16s → Navigate to dashboard
00:17s → Run test (e.g., verify stay)
00:18s → Close browser
        
00:19s → Generate reports
00:20s → END
        
Total: ~20 seconds for full test suite
```

---

This visual guide shows how all components work together to create your complete automation framework!

