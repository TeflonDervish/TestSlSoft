import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class YandexMarket extends Market{

    private final WebDriver webDriver;
    private final Wait<WebDriver> webDriverWait;
    private final Actions actions;

    public YandexMarket(WebDriver webDriver) {
        this(webDriver, 10);
    }

    public YandexMarket(WebDriver webDriver, int duration) {
        this.webDriver = webDriver;
        webDriverWait =
                new FluentWait<>(webDriver)
                        .withTimeout(Duration.ofSeconds(duration))
                        .pollingEvery(Duration.ofMillis(300))
                        .ignoring(ElementClickInterceptedException.class)
                        .ignoring(StaleElementReferenceException.class);
        actions = new Actions(webDriver);

        webDriver.get("https://market.yandex.ru/");
        actions.click().perform();
    }

    public List<Item> search(String query) {
        WebElement search = webDriver.findElement(By.xpath("//input[@data-auto='search-input']"));
        search.sendKeys(query, Keys.ENTER);

        webDriverWait.until(
                ExpectedConditions.numberOfElementsToBeMoreThan(
                        By.xpath("//div[@data-zone-name='productSnippet']"),
                        15));
        List<WebElement> products = webDriver.findElements(By.xpath("//div[@data-zone-name='productSnippet']"));

        List<Item> items = new ArrayList<>();

        for (WebElement webElement : products) {
            try {
                items.add(
                        new Item(
                                webElement.findElement(By.xpath(".//span[@data-auto='snippet-title']")).getText(),
                                webElement.findElement(By.xpath(".//span[@data-auto='snippet-price-current']//span")).getText()
                        )
                );
            } catch (Exception e) {
                items.add(null);
            }
        }

        return items;

    }

    public void clickSort(String type) {
        webDriverWait.until(
                d -> {
                    webDriver.findElement(By.xpath("//button[@data-autotest-id='" + type + "']"))
                            .click();
                    return true;
                });
    }

    public void clickFirst() {
        webDriverWait.until(
                d -> {
                    WebElement product = webDriver.findElement(By.xpath("//div[@data-zone-name='productSnippet']"));
                    product.click();
                    return true;
                });
        switchWindow();
    }

    public void switchWindow() {
        String currentWindow = webDriver.getWindowHandle();
        Set<String> windows = webDriver.getWindowHandles();
        String windowToSwitch = null;

        for (String window: windows) {
            if (!window.equals(currentWindow)) {
                windowToSwitch = window;
                break;
            }
        }

        if (windowToSwitch == null) System.out.println("Открыто только одно окно");
        else webDriver.switchTo().window(windowToSwitch);
    }

    public String takeInfoFromPage() {
        return "Название магазина: " +
                webDriver.findElement(By.xpath("//div[@data-baobab-name='shopItem']//span")).getText() +
                "\nЦена товара: " +
                webDriver.findElement(By.xpath("//h3[@data-auto='snippet-price-current']")).getText()
                        .replaceAll("\\D", "");
    }

    public static void main(String[] args) {

        String query = "POCO X6 PRO";

        WebDriver driver = new ChromeDriver();
        YandexMarket yandexMarket = new YandexMarket(driver);
        List<Item> items = yandexMarket.search(query);
        items.forEach(System.out::println);
        System.out.println(YandexMarket.checkItems(items, query));
        yandexMarket.clickSort("aprice");
        yandexMarket.clickFirst();
        System.out.println(yandexMarket.takeInfoFromPage());


    }
}


