package org.rubicon.base;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
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
    public WebDriver driver;
    public LoginPage loginPage;

    @Test
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

    @BeforeClass(alwaysRun = false)
    public LoginPage launchApplication() throws IOException {
        driver = intitalizeDriver();
        loginPage = new LoginPage(driver);
        loginPage.goTo();
        return loginPage;
    }

//    @AfterClass(alwaysRun = false)
//    public void closeApp() {
//        driver.quit();
//    }
//
}