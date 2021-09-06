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
import java.util.Arrays;
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
    private WebElement customField;
    private WebElement phoneNumberField;
    private WebElement commentsField;
    private ExtentReports report;
    private ExtentTest test;

    @BeforeTest
    protected void initialize() {
        System.setProperty("webdriver.gecko.driver", "src/main/resources/Drivers/geckodriver.exe");
        LocalDateTime timeStamp = LocalDateTime.now();
        String timeString = getTimeString(timeStamp, 10, 16, ":");
        String dateString = getTimeString(timeStamp, 5, 10, "-");

        report = new ExtentReports("src/main/resources/Reports/Report" + dateString + timeString + ".html", false);
        test = report.startTest("Verifying Browser Opened");
        test.log(LogStatus.INFO, "OpenBrowser test is initiated.");

        driver = new FirefoxDriver();
        driver.get(URL);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().window().maximize();

        submitButton = driver.findElement(By.xpath("//*[@id=\"mG61Hd\"]/div[2]/div/div[3]/div[1]/div/div/span"));
        verifyField(submitButton == null, "Submit Button");

        nameField = driver.findElement(By.xpath("//*[@id=\"mG61Hd\"]/div[2]/div/div[2]/div[2]/div/div/div[2]/div/div[1]/div/div[1]/input"));
        verifyField(nameField == null, "Name");

        emailField = driver.findElement(By.xpath("//*[@id=\"mG61Hd\"]/div[2]/div/div[2]/div[3]/div/div/div[2]/div/div[1]/div/div[1]/input"));
        verifyField(emailField == null, "Email");

        addressField = driver.findElement(By.xpath("//*[@id=\"mG61Hd\"]/div[2]/div/div[2]/div[4]/div/div/div[2]/div/div[1]/div[2]/textarea"));
        verifyField(addressField == null, "Address");

        customField = driver.findElement(By.xpath("/html/body/div/div[2]/form/div[2]/div/div[2]/div[1]/div/div/div[2]/div[1]/div/span/div/div[6]/div/span/div/div/div[1]/input"));
        verifyField(customField == null, "Custom");

        phoneNumberField = driver.findElement(By.xpath("/html/body/div/div[2]/form/div[2]/div/div[2]/div[5]/div/div/div[2]/div/div[1]/div/div[1]/input"));
        verifyField(phoneNumberField == null, "Phone Number");

        commentsField = driver.findElement(By.xpath("/html/body/div/div[2]/form/div[2]/div/div[2]/div[6]/div/div/div[2]/div/div[1]/div[2]/textarea"));
        verifyField(commentsField == null, "Comment field");

        test.log(LogStatus.PASS, "Browser is open and window is maximized");
        test.log(LogStatus.PASS, "String url is open in Firefox browser");

        report.endTest(test);
    }

    @Test
    public void verifyPageLoad() {
        test = report.startTest("Verify correct page loaded");

        String actual = "Contact information";
        String expected = driver.getTitle();

        test.log(LogStatus.INFO, "Verifying correct page.");
        logData(expected.equals(actual), "Correct page is opened successfully.", "Correct page is failed to open.");

        report.endTest(test);
    }

    @Test
    public void verifyRequiredFieldName() {
        List<String> namesInput = Arrays.asList(" ", "-", "Shy");

        test = report.startTest("Verify Name field.");
        test.log(LogStatus.INFO, "Initiating Name field verification.");

        boolean result = checkInputs(namesInput, nameField, "i25");
        logData(result, "Empty String was not accepted to the Name field.",
                "Field Name accepts any type and length of data that might be irrelevant.");

        report.endTest(test);
    }

    @Test
    public void verifyRequiredFieldEmail() {
        List<String> emailsInput = Arrays.asList(" ", "@", " @.ru", ".x", "empty", " @ .  ");

        test = report.startTest("Verify Email field");
        test.log(LogStatus.INFO, "Initiating Email field verification");

        boolean result = checkInputs(emailsInput, emailField, "i29");
        logData(result, "Email verification is completed successfully.",
                "Email verification is completed unsuccessfully.");

        report.endTest(test);
    }

    @Test
    public void verifyRequiredFieldAddress() {
        List<String> inputData = Arrays.asList(" ", "1", "xx", "short");

        test = report.startTest("Verify Address field");
        test.log(LogStatus.INFO, "Initiating Address field verification");

        boolean result = checkInputs(inputData, addressField, "i33");
        logData(result, "Address verification is completed successfully",
                "Address verification is completed unsuccessfully, field accepts any amount and type of data" +
                        " including empty String.Inserted data might be irrelevant.");

        report.endTest(test);
    }

    @Test
    public void submitOtherFields() {
        test = report.startTest("Verify other fields.");
        test.log(LogStatus.INFO, "verifying non required fields.");

        for (int i = 5; i < 20; i += 3) {
            WebElement element = driver.findElement(By.id("i" + i));
            element.click();
            submitButton.click();
            logData(submissionCheck(), "Current radio button didn't submit form without required fields.",
                    "Form submitted without all required fields.");

            if (element.isSelected()) {
                test.log(LogStatus.INFO, "Current radio button worked.");
            }
        }
        customField.sendKeys("Custom field.");
        test.log(LogStatus.PASS, "Radio buttons didnt submit without required fields.");

        phoneNumberField.sendKeys("Not a number.");
        submitButton.click();
        logData(submissionCheck(), "Field Phone number didn't submit form without required fields.",
                "Field Phone number submit form without required fields.");

        commentsField.sendKeys("This is comments field input.");
        submitButton.click();
        logData(submissionCheck(), "Field Comment didn't submit form without required fields.",
                "Field Comment submit form without required fields.");

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
        logData(submissionCheck(), "Form successfully submitted", "Form submission has failed");

        report.endTest(test);
        report.flush();
        report.close();
    }

    /**
     * Method will create String for report name forming from current date and time.
     *
     * @param timeStamp  current date and time.
     * @param startIndex specify starting index for forming file name string.
     * @param endIndex   specify ending index for forming file name string.
     * @param replacer   specify element to be replaced by specific symbol.
     * @return String which will be used for report file name.
     */
    private static String getTimeString(LocalDateTime timeStamp, int startIndex, int endIndex, String replacer) {
        return timeStamp.toString().substring(startIndex, endIndex).replace(replacer, "_");
    }

    /**
     * Method makes inputs one by one from List and checks if form accepts the input.
     *
     * @param errorId    ID of error that would appear if input is not accepted.
     * @param inputs     List of inputs to insert into field for validation.
     * @param inputField specific field that will be used for inputs and validation.
     * @return will return true if validation has passed.
     */
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

    /**
     * Method that will write data to test logs and console.
     *
     * @param condition   this will specify the state of current test block.
     * @param passMessage message that will be added if condition is true.
     * @param failMessage message that will be added if condition is false.
     */
    private void logData(Boolean condition, String passMessage, String failMessage) {
        if (condition) {
            test.log(LogStatus.PASS, passMessage);
            System.out.println(passMessage);
        } else {
            test.log(LogStatus.FAIL, failMessage);
            System.err.println(failMessage);
        }
    }

    /**
     * Method verifies fields initialization at the beginning of the test.
     *
     * @param condition is the state of the current reference, is it null.
     * @param name      of the field that being verified.
     */
    private void verifyField(Boolean condition, String name) {
        if (condition) {
            throw new RuntimeException(name + " field is set incorrectly.");
        }
        test.log(LogStatus.INFO, name + " field is set correctly.");
        System.out.println(name + " field is set correctly.");
    }

    /**
     * Method check if form was submitted successfully.
     */
    private boolean submissionCheck() {
        return !driver.getCurrentUrl().equals(URL);
    }
}