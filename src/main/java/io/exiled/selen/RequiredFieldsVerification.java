package io.exiled.selen;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Selenium test of required fields on defined URL.
 *
 * @author Vassili Moskaljov
 * @version 1.0
 */
public class RequiredFieldsVerification {
    private static final String URL = "https://docs.google.com/forms/d/e/1FAIpQLScVG7idLWR8sxNQygSnLuhehUNVFti0FnVviWCSjDh-JNhsMA/viewform?usp=sf_link";
    private WebDriver driver;
    private WebElement submitButton;
    private WebElement nameField;
    private WebElement emailField;
    private WebElement addressField;
    private ExtentReports report;
    private ExtentTest test;

    @BeforeTest
    protected void openBrowser() {
        System.setProperty("webdriver.gecko.driver", "src/main/resources/Drivers/geckodriver.exe");
        LocalDateTime timeStamp = LocalDateTime.now();
        String timeString = getTimeString(timeStamp, 10, 16, ":");
        String dateString = getTimeString(timeStamp, 5, 10, "-");

        report = new ExtentReports("src/main/resources/Reports/Report" + dateString + timeString + ".html", false);
        test = report.startTest("Verifying Browser Opened");
        test.log(LogStatus.INFO, "OpenBrowser test is initiated.");

        driver = new FirefoxDriver();
        driver.get(URL);
        submitButton = driver.findElement(By.xpath("//*[@id=\"mG61Hd\"]/div[2]/div/div[3]/div[1]/div/div/span"));

        if (submitButton == null) {
            throw new RuntimeException("Submit Button is set incorrectly");
        }

        test.log(LogStatus.INFO, "Submit button is verified");
        System.out.println("Submit button is verified");
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        nameField = driver.findElement(By.xpath("//*[@id=\"mG61Hd\"]/div[2]/div/div[2]/div[2]/div/div/div[2]/div/div[1]/div/div[1]/input"));
        emailField = driver.findElement(By.xpath("//*[@id=\"mG61Hd\"]/div[2]/div/div[2]/div[3]/div/div/div[2]/div/div[1]/div/div[1]/input"));
        addressField = driver.findElement(By.xpath("//*[@id=\"mG61Hd\"]/div[2]/div/div[2]/div[4]/div/div/div[2]/div/div[1]/div[2]/textarea"));
        test.log(LogStatus.PASS, "Browser is open and window is maximized");
        test.log(LogStatus.PASS, "String url is open in Firefox browser");

        report.endTest(test);
    }

    @Test
    public void verifyPageLoad() {
        test = report.startTest("Verify correct page loaded");

        String Actual = "Contact information";
        String Expected = driver.getTitle();

        test.log(LogStatus.INFO, "Verifying correct page.");
        if (Expected.equals(Actual)) {
            test.log(LogStatus.PASS, "Correct page is opened successfully");
            System.out.println("Correct page is opened successfully");
        } else {
            test.log(LogStatus.FAIL, "Correct page is failed to open");
            System.out.println("Correct page is failed to open");
        }

        report.endTest(test);
    }

    @Test
    public void verifyRequiredFieldName() {
        List<String> namesInput = new ArrayList<>();
        namesInput.add(" ");
        namesInput.add("-");
        namesInput.add("Shy");

        test = report.startTest("Verify Name field");
        test.log(LogStatus.INFO, "Initiating Name verification");

        boolean result = checkInputs(namesInput, nameField, "i25");
        if (result) {
            test.log(LogStatus.PASS, "Empty String was not accepted to the Name field");
            System.out.println("Empty String was not accepted to the Name field");
        } else {
            test.log(LogStatus.FAIL, "Field Name accepts any type and length of data that might be irrelevant");
            System.err.println("Field Name accepts any type and length of data that might be irrelevant");
        }

        report.endTest(test);
    }

    @Test
    public void verifyRequiredFieldEmail() {
        List<String> emailsInput = new ArrayList<>();
        emailsInput.add(" ");
        emailsInput.add("@");
        emailsInput.add(" @.ru");
        emailsInput.add(".x");
        emailsInput.add("empty");
        emailsInput.add(" @ .  ");

        test = report.startTest("Verify Email field");
        test.log(LogStatus.INFO, "Initiating Email verification");
        boolean result = checkInputs(emailsInput, emailField, "i29");
        if (result) {
            test.log(LogStatus.PASS, "Email verification is completed successfully");
            System.out.println("Email verification is completed successfully");
        } else {
            test.log(LogStatus.FAIL, "Email verification is completed unsuccessfully");
            System.err.println("Email verification is completed unsuccessfully");
        }

        report.endTest(test);
    }

    @Test
    public void verifyRequiredFieldAddress() {
        List<String> inputData = new ArrayList<>();
        inputData.add(" ");
        inputData.add("1");
        inputData.add("xx");
        inputData.add("short");

        test = report.startTest("Verify Address field");
        test.log(LogStatus.INFO, "Initiating Address verification");
        boolean result = checkInputs(inputData, addressField, "i33");

        if (result) {
            test.log(LogStatus.PASS, "Address verification is completed successfully");
            System.out.println("Address verification is completed successfully");
        } else {
            test.log(LogStatus.FAIL, "Address verification is completed unsuccessfully, field accepts any amount and type of data including empty String.Inserted data might be irrelevant.");
            System.err.println("Address verification is completed unsuccessfully, field accepts any amount and type of data including empty String.Inserted data might be irrelevant.");
        }

        report.endTest(test);
    }

    @Test
    public void submitOtherFields() {
        test = report.startTest("Verify other fields");
        test.log(LogStatus.INFO, "verifying non required fields");

        for (int i = 5; i < 20; i += 3) {
            WebElement element = driver.findElement(By.id("i" + i));
            element.click();
            submitButton.click();
            if (submissionCheck()) {
                test.log(LogStatus.ERROR, "Form submitted without all required fields.");
                System.err.println("Form submitted without all required fields.");
            } else {
                test.log(LogStatus.PASS, "Current dot didn't submit form without required fields");
                System.out.println("Current dot didn't submit form without required fields");
            }
            if (element.isSelected()) {
                test.log(LogStatus.INFO, "Current dot worked");
            }
        }
        driver.findElement(By.xpath("/html/body/div/div[2]/form/div[2]/div/div[2]/div[1]/div/div/div[2]/div[1]/div/span/div/div[6]/div/span/div/div/div[1]/input")).sendKeys("Custom field");

        test.log(LogStatus.PASS, "Radio buttons didnt submit without required fields");

        driver.findElement(By.xpath("/html/body/div/div[2]/form/div[2]/div/div[2]/div[5]/div/div/div[2]/div/div[1]/div/div[1]/input")).sendKeys("Not a number");
        submitButton.click();
        if (submissionCheck()) {
            test.log(LogStatus.FAIL, "Field Phone number submit form without required fields");
            System.err.println("Field Phone number submit form without required fields");
        } else {
            test.log(LogStatus.PASS, "Field Phone number didn't submit form without required fields.");
            System.out.println("Field Phone number didn't submit form without required fields.");
        }

        driver.findElement(By.xpath("/html/body/div/div[2]/form/div[2]/div/div[2]/div[6]/div/div/div[2]/div/div[1]/div[2]/textarea")).sendKeys("This is custom ");
        submitButton.click();
        if (submissionCheck()) {
            test.log(LogStatus.FAIL, "Field Comment submit form without required fields");
            System.err.println("Field Comment submit form without required fields");
        } else {
            test.log(LogStatus.PASS, "Field Comment didn't submit form without required fields.");
            System.out.println("Field Comment didn't submit form without required fields.");
        }

        report.endTest(test);
    }

    @AfterMethod
    public void AfterMethods() {
        report.flush();
    }

    @AfterClass
    public void submitCompleteForm() {
        test = report.startTest("Form submit");
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        nameField.sendKeys("Custom Name");
        emailField.sendKeys("cutom@email.com");
        addressField.sendKeys("Custom address");
        test.log(LogStatus.INFO, "All fields filled");
        submitButton.click();
        if (submissionCheck()) {
            test.log(LogStatus.FAIL, "Form submission has failed");
            System.err.println("Form submission has failed");
        } else {
            test.log(LogStatus.PASS, "Form successfully submitted");
            System.out.println("Form successfully submitted");
        }
        report.endTest(test);
        report.flush();
        report.close();
    }

    private static String getTimeString(LocalDateTime timeStamp, int startIndex, int endIndex, String replacer) {
        return timeStamp.toString().substring(startIndex, endIndex).replace(replacer, "_");
    }

    private boolean checkInputs(List<String> inputs, WebElement inputField, String errorId) {
        boolean result = true;

        for (String input : inputs) {
            inputField.sendKeys(input);
            submitButton.click();
            if (!driver.findElement(By.id(errorId)).isDisplayed()) {
                result = false;
                System.err.println("Input validation failed with input: " + input);
                test.log(LogStatus.ERROR, "Input validation failed with input: " + input);
            }
            inputField.clear();
        }

        return result;
    }

    protected boolean submissionCheck() {
        return driver.getCurrentUrl().equals(URL);
    }
}