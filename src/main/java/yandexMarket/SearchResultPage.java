package yandexMarket;

import lombok.Data;
import lombok.Getter;
import model.Item;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;

import java.util.List;
import java.util.Set;


@Data
public class SearchResultPage {

    private static final By productCard = By.xpath("//div[@data-zone-name='productSnippet']");
    private static final By productCardName = By.xpath(".//span[@data-auto='snippet-title']");
    private static final By productCardPrice = By.xpath(".//span[@data-auto='snippet-price-current']//span");

    /**
     * Enum для выбора типа сортировки
     */
    @Getter
    public enum SortType {
        POPULAR(By.xpath("//button[@data-zone-name='sort' and @data-autotest-id='drop']")),
        CHEAPER(By.xpath("//button[@data-zone-name='sort' and @data-autotest-id='aprice']")),
        MORE_EXPENSIVE(By.xpath("//button[@data-zone-name='sort' and @data-autotest-id='dprice']")),
        RATING(By.xpath("//button[@data-zone-name='sort' and @data-autotest-id='rating']"));

        private final By sortType;

        SortType(By xpath) {
            this.sortType = xpath;
        }

    }

    private final WebDriver webDriver;
    private final Wait<WebDriver> webDriverWait;
    private final Actions actions;

    /**
     * Конструктор для создания экземпляра другими страницами
     *
     * @param webDriver     - Драйвер браузера
     * @param webDriverWait - Явное ожидание
     */
    public SearchResultPage(WebDriver webDriver, Wait<WebDriver> webDriverWait) {
        this.webDriver = webDriver;
        this.webDriverWait = webDriverWait;
        this.actions = new Actions(webDriver);
    }

    /**
     * Метод для получения товаров со страницы
     *
     * @return - Список элементов типа Item
     */
    public List<Item> getItems() {
        webDriverWait.until(
                ExpectedConditions.numberOfElementsToBeMoreThan(
                        productCard,
                        15
                )
        );

        return webDriver.findElements(productCard)
                .stream().map(
                        webElement -> new Item(
                                webElement.findElement(productCardName).getText(),
                                webElement.findElement(productCardPrice).getText()
                        )
                ).toList();


    }

    /**
     * Метод необходим, чтобы использовать сортировку по странице
     *
     * @param sortType - выбор типа сортировки
     */
    public void clickSort(SortType sortType) {
        webDriverWait.until(
                d -> {
                    webDriver.findElement(sortType.getSortType())
                            .click();
                    return true;
                }
        );
    }

    /**
     * Переключается на новое окно, закрывая предыдущее
     */
    public void switchWindow() {
        // Получение текущего окна
        String currentWindow = webDriver.getWindowHandle();

        // Получение всех открытых окон
        Set<String> windows = webDriver.getWindowHandles();
        String windowToSwitch = null;

        // Перебор отрытых окон, в поисках того на которое можно переключиться
        for (String window : windows) {
            if (!window.equals(currentWindow)) {
                windowToSwitch = window;
                break;
            }
        }

        if (windowToSwitch == null) System.out.println("Открыто только одно окно");
        else {
            webDriver.close();
            webDriver.switchTo().window(windowToSwitch);
        }
    }


    /**
     * Метод, который кликнет по первого элементу на странице
     */
    public ProductPage clickFirst() {
        webDriverWait.until(
                d -> {
                    webDriver.findElement(productCard)
                            .click();
                    return true;
                }
        );

        switchWindow();
        return new ProductPage(webDriver, webDriverWait);

    }


}
