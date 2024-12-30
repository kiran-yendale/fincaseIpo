package in.fincase.serviceimpl;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import in.fincase.dto.LinkinTimeDTO;
import in.fincase.entity.FamilyMembersEntity;
import in.fincase.entity.UserEntity;
import in.fincase.repository.FamilyMEmberRepository;
import in.fincase.repository.UserRepository;
import in.fincase.service.LinkiTimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class LinkinTimeServiceImpl implements LinkiTimeService {

    private static final Logger logger = LoggerFactory.getLogger(LinkinTimeServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FamilyMEmberRepository familyMembersRepo;

    @Override
    public List<LinkinTimeDTO> getIpoStatus(String ipoName) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("Finding user by email: {}", email);

        Optional<UserEntity> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            logger.error("User not found for email: {}", email);
            throw new RuntimeException("User not found");
        }

        Long userId = user.get().getId();
        logger.info("Retrieved user ID: {}", userId);

        List<FamilyMembersEntity> familyList = familyMembersRepo.findByUserId(userId);
        List<String> panNumbers = familyList.stream()
                .map(FamilyMembersEntity::getPanNumber)
                .collect(Collectors.toList());
        logger.info("Found family members with PAN numbers: {}", panNumbers);

        // Set up ChromeDriver
        System.setProperty("webdriver.chrome.driver", "D:\\softwares\\chromeDriver\\chromedriver-win64\\chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        options.setBinary("D:\\softwares\\ChromeBrowser\\chrome-win64\\chrome.exe");
        options.addArguments("--disable-dev-shm-usage", "--no-sandbox", "--remote-allow-origins=*");

        WebDriver driver = new ChromeDriver(options);
        logger.info("WebDriver initialized.");

        List<LinkinTimeDTO> results = new ArrayList<>();

        try {
            driver.get("https://linkintime.co.in/Initial_Offer/public-issues.html");
            WebElement ipoNameDropDown = driver.findElement(By.id("ddlCompany"));
            Select selectIpo = new Select(ipoNameDropDown);
            selectIpo.selectByVisibleText("Senores Pharmaceuticals Limited - IPO");
            logger.info("Navigated to the IPO page and selected IPO name.");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            for (String pan : panNumbers) {
                try {
                    WebElement radioButton = driver.findElement(By.id("349"));
                    if (!radioButton.isSelected()) {
                        radioButton.click();
                    }

                    WebElement panNumberBox = driver.findElement(By.id("txtStat"));
                    panNumberBox.clear();
                    panNumberBox.sendKeys(pan);

                    WebElement submitButton = driver.findElement(By.id("btnsearc"));
                    if (submitButton.isEnabled()) {
                        submitButton.click();
                    }

                    Thread.sleep(2000);
                    if (isPopupPresent(driver)) {  // Corrected method call
                        WebElement popupButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".showcss.ui-button")));
                        popupButton.click();
                        logger.info("Popup handled for missing record.");
                    } 

                    WebElement table = wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("table")));
                    List<WebElement> rows = table.findElements(By.tagName("tr"));

                    String applicantName = "", securitiesApplied = "", securitiesAllotted = "";
                    for (WebElement row : rows) {
                        List<WebElement> cols = row.findElements(By.tagName("td"));
                        if (cols.size() > 0) {
                            switch (cols.get(0).getText()) {
                                case "Sole / 1st Applicant":
                                    applicantName = cols.get(1).getText();
                                    break;
                                case "Securities applied":
                                    securitiesApplied = cols.get(1).getText();
                                    break;
                                case "Securities Allotted":
                                    securitiesAllotted = cols.get(1).getText();
                                    break;
                            }
                        }
                    }

                    results.add(new LinkinTimeDTO(applicantName, securitiesApplied, securitiesAllotted));
                } catch (Exception e) {
                    logger.warn("No results for PAN: {}", pan);
                    logger.error("Error processing PAN: {}", pan, e);
                }
            }
        } finally {
            driver.quit();
            logger.info("WebDriver session closed.");
        }

        return results;
    }

    // Method to check if a popup is present
    private boolean isPopupPresent(WebDriver driver) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement popupButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".showcss.ui-button")));
            return popupButton != null;
        } catch (Exception e) {
            return false; 
        }
    }
}
