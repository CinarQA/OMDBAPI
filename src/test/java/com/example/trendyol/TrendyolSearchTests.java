package com.example.trendyol;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TrendyolSearchTests {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeAll
    static void setupDriverManager() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        driver.get("https://www.trendyol.com/");
        handleInitialPopups();
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Search for a product and verify results are displayed")
    void searchForProduct_displaysResults() {
        performSearch("ayakkabı");

        By productCardsLocator = By.cssSelector("div.p-card-wrppr");
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(productCardsLocator, 0));

        List<WebElement> productCards = driver.findElements(productCardsLocator);
        Assertions.assertFalse(productCards.isEmpty(), "Expected search results to contain products");
    }

    @Test
    @Order(2)
    @DisplayName("Open the first product details page and add it to the cart")
    void addProductToCartFromDetailsPage() {
        performSearch("ayakkabı");

        By productCardsLocator = By.cssSelector("div.p-card-wrppr");
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(productCardsLocator, 0));
        WebElement firstProduct = driver.findElements(productCardsLocator).get(0);
        firstProduct.click();

        switchToProductTab();

        By addToBasketButtonLocator = By.cssSelector("button.add-to-basket");
        wait.until(ExpectedConditions.elementToBeClickable(addToBasketButtonLocator)).click();

        By basketSuccessLocator = By.cssSelector("div.checkout-success");
        wait.until(ExpectedConditions.visibilityOfElementLocated(basketSuccessLocator));
        WebElement successToast = driver.findElement(basketSuccessLocator);
        Assertions.assertTrue(successToast.isDisplayed(), "Expected success toast after adding to basket");
    }

    private void performSearch(String query) {
        By searchBoxLocator = By.cssSelector("input[data-testid='suggestion']");
        wait.until(ExpectedConditions.elementToBeClickable(searchBoxLocator)).sendKeys(query + Keys.ENTER);
    }

    private void handleInitialPopups() {
        dismissElementIfPresent(By.cssSelector("button#onetrust-accept-btn-handler"));
        dismissElementIfPresent(By.cssSelector("div.fancybox-inner button.close"));
    }

    private void dismissElementIfPresent(By locator) {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            WebElement element = driver.findElement(locator);
            if (element.isDisplayed()) {
                element.click();
            }
        } catch (TimeoutException ignored) {
            // Element not present, safe to continue
        }
    }

    private void switchToProductTab() {
        String originalHandle = driver.getWindowHandle();
        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(originalHandle)) {
                driver.switchTo().window(handle);
                return;
            }
        }
    }
}
