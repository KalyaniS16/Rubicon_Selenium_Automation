package org.rubicon.base;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.rubicon.automation.app;
import org.rubicon.automation.pages.DashboardPage;
import org.rubicon.automation.pages.LoginPage;
import org.testng.annotations.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class BaseTest {
    // Shared across test classes in the same JVM run (suite or single-class)
    public static WebDriver driver;
    public static LoginPage loginPage;
    protected static boolean isLoggedIn = false;

    public WebDriver intitalizeDriver() throws IOException {
//		understanding global properties so that if test needs to run on diff browsers, then it can run
//		for reading the golbal.properties file
//		using Properties class, we are able to parse global.properties file and extract all global parameter values
        Properties properties = new Properties();

//		getting project path using user.dir
//C:\Workspace\SmartRainLLP\Rubicon_Selenium_Automation\src\main\resources\config\global.properties
        FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + "\\src\\main\\resources\\config\\global.properties");
        properties.load(fis);          // to load global.data properties file

//      using ternary operation
        String browserName = System.getProperty("browser")!=null ? System.getProperty("browser") : properties.getProperty("browser");           //this getProperty method will help to read the system level variables
//		String browserName = properties.getProperty("browser");

        if (browserName.equalsIgnoreCase("chrome")) {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();
        }
//		else if (browserName.equalsIgnoreCase("firefox")) {
//			WebDriverManager.firefoxdriver().setup();
//			driver = new FirefoxDriver();
//		}
//		else if (browserName.equalsIgnoreCase("edge")) {
//			WebDriverManager.edgedriver().setup();
//			driver = new EdgeDriver();
//		}
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
        return driver;
    }

    public List<HashMap<String, String>> getJsonDataToMap(String filePath) throws IOException {
//		readFileToByteArray method will read the json file, It will scan the entire data of json file and convert into string variable
//		read json to string
        String jsonContent = FileUtils.readFileToString(new File(filePath), StandardCharsets.UTF_8);
        System.out.println("Loaded JSON content: " + jsonContent);

//		external utility which will convert String to hashmap
//		JACKSON DATBIND --> external dependency used to convert
        ObjectMapper objectMapper = new ObjectMapper();
        List<HashMap<String, String>> data = objectMapper.readValue(jsonContent, new TypeReference<List<HashMap<String,String>>>() {
        });
        return data;
//		{map,map}
    }

 //	TakeScreenShot for the failed tests
    public String getScreenShot(String testCaseName, WebDriver driver) throws IOException {
        TakesScreenshot ss = (TakesScreenshot)driver;
        File source = ss.getScreenshotAs(OutputType.FILE);
//		we are giving path of the file where screenshot needs to be dumped
        File file = new File(System.getProperty("user.dir")+"//reports//"+"testCaseName" +".png");
        FileUtils.copyFile(source, file);
        return System.getProperty("user.dir")+"//reports//" +"testCaseName" +".png";
    }

    /**
     * Opens the browser and navigates to the login page only if no session exists yet.
     * Safe for LoginApplicationTest (stays on login page) and for card tests (shared session).
     */
    @BeforeClass(alwaysRun = true)
    public void launchApplication() throws IOException {
        if (driver == null) {
            driver = intitalizeDriver();
            loginPage = new LoginPage(driver);
            loginPage.goTo();
        }
    }

    /**
     * Ensures a single login session and lands on the dashboard.
     * - Suite run: first caller logs in; later classes reuse the same browser/session.
     * - Single class run: creates session (via launchApplication) and logs in for that class.
     * Does not quit the browser.
     */
    protected void ensureLoggedInOnDashboard() throws IOException {
        if (driver == null) {
            launchApplication();
        }

        if (!isLoggedIn) {
            List<HashMap<String, String>> testData = getJsonDataToMap(
                    System.getProperty("user.dir") + "\\src\\main\\resources\\testdata\\LoginData.json"
            );

            if (testData == null || testData.isEmpty()) {
                throw new RuntimeException("No credentials found in LoginData.json");
            }

            HashMap<String, String> credentials = testData.get(0);
            loginPage.login(credentials.get("username"), credentials.get("password"));
            isLoggedIn = true;
        }

        navigateToDashboard();
    }

    protected void navigateToDashboard() {
        String currentUrl = driver.getCurrentUrl();
        // Card detail pages also contain "#/dashboard" in the URL (e.g. dashboardcardview)
        if (!currentUrl.contains("#/dashboard") || currentUrl.contains("dashboardcardview")) {
            driver.get(app.APPLICATION_URL + "/" + app.DASHBOARD_PAGE_PATH);
        }
        new DashboardPage(driver).waitForDashboardPage();
    }

//    @AfterClass(alwaysRun = false)
//    public void closeApp() {
//        driver.quit();
//    }
//
}
