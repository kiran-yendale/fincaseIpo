package in.fincase.serviceimpl;

import org.openqa.selenium.By;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class IPOResultScraper {
 
public Map<String, String> checkIpoAllotment(String ipoName, String pan) {
    // Set ChromeDriver path
    System.setProperty("webdriver.chrome.driver", "D:\\softwares\\chromeDriver\\chromedriver-win64\\chromedriver.exe");

    // Configure ChromeOptions for stability
    ChromeOptions options = new ChromeOptions();
    options.setBinary("D:\\softwares\\ChromeBrowser\\chrome-win64\\chrome.exe");
    options.addArguments("--disable-dev-shm-usage"); // Resolve shared memory issues
    options.addArguments("--no-sandbox");           // Avoid sandboxing issues
    options.addArguments("--remote-allow-origins=*"); // Handle remote origin policy

    // Initialize WebDriver
    WebDriver driver = new ChromeDriver(options);
    	System.out.println("Driver set up completed");
    
    Map<String, String> extractedData = new HashMap<>();
    try {
        // Open the website
        driver.get("https://maashitla.com/allotment-status/public-issues");

        // Locate the dropdown element and select IPO
        WebElement dropdownElement = driver.findElement(By.id("txtCompany"));
        Select dropdown = new Select(dropdownElement);
        dropdown.selectByVisibleText(ipoName);

        // Select the radio button for PAN
        WebElement radioButton = driver.findElement(By.id("pan"));
        if (!radioButton.isSelected()) {
            radioButton.click();
        }

        // Enter the PAN number
        WebElement panNumberBox = driver.findElement(By.id("txtSearch"));
        panNumberBox.sendKeys(pan);

        // Click the submit button
        WebElement submitButton = driver.findElement(By.id("btnSearch"));
        if (submitButton.isEnabled()) {
            submitButton.click();
        }

        Thread.sleep(2000);
        // Locate the result div and extract the text
        WebElement resultDiv = driver.findElement(By.cssSelector(".contact-form-success"));
        String resultText = resultDiv.getText();

        
        // Parse the data
        extractedData = parseResultText(resultText);

    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        // Quit the driver
        driver.quit();
    }
   
    return extractedData;
}

private Map<String, String> parseResultText(String text) {
    Map<String, String> dataMap = new HashMap<>();

    // Split text by newline or `<br>` to process each line
    String[] lines = text.split("\\n|<br>");
    for (String line : lines) {
        if (line.contains(":")) {
            String[] parts = line.split(":", 2);
            String key = parts[0].trim();
            String value = parts[1].trim();
            dataMap.put(key, value);
        }
    }
    return dataMap;
    
}
}


