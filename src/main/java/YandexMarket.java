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

/**
 * Класс, который позволяет выполнять работу с платформой YandexMarket
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class YandexMarket extends Market {

    private final WebDriver webDriver;
    private final Wait<WebDriver> webDriverWait;
    private final Actions actions;

    /**
     * Конструктор без явного указания продолжительности ожидания
     *
     * @param webDriver - драйвер браузера
     */
    public YandexMarket(WebDriver webDriver) {
        this(webDriver, 10);
    }

    /**
     * Конструктор с явным указанием продолжительности ожидания
     *
     * @param webDriver - драйвер браузера
     * @param duration  - время ожидания действия
     */
    public YandexMarket(WebDriver webDriver, int duration) {
        this.webDriver = webDriver;
        // Создания кастомизированного ожидания
        webDriverWait =
                new FluentWait<>(webDriver)
                        .withTimeout(Duration.ofSeconds(duration))
                        .pollingEvery(Duration.ofMillis(300))
                        .ignoring(ElementClickInterceptedException.class)
                        .ignoring(StaleElementReferenceException.class);
        actions = new Actions(webDriver);

        // Открытие браузера
        webDriver.get("https://market.yandex.ru/");
        // Предназначен для закрытия всплывающего окна, с просьбой войти в аккаунт
        actions.click().perform();
    }

    /**
     * Метод делает глобальный поиск по сайту
     *
     * @param query - здесь вводится поисковой запрос
     * @return - возвращает список найденных элементов на странице поиска
     */
    public List<Item> search(String query) {
        // Выполнение глобального поиска
        WebElement search = webDriver.findElement(By.xpath("//input[@data-auto='search-input']"));
        search.sendKeys(query, Keys.ENTER);

        // Ожидание пока на странице появится 15 элементов
        webDriverWait.until(
                ExpectedConditions.numberOfElementsToBeMoreThan(
                        By.xpath("//div[@data-zone-name='productSnippet']"),
                        15));

        // Получение элементов на странице
        List<WebElement> products = webDriver.findElements(By.xpath("//div[@data-zone-name='productSnippet']"));

        // Заполнения Item по элементам списка products
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

    /**
     * Метод необходим, чтобы использовать сортировку по странице
     *
     * @param type - позволяет выбрать тип сортировки
     *             (drop - популярные,
     *             aprice - подешевле,
     *             dprice - подороже,
     *             rating - высокий рейтинг)
     */
    public void clickSort(String type) {
        webDriverWait.until(
                d -> {
                    webDriver.findElement(By.xpath("//button[" +
                                    "@data-zone-name='sort'" +
                                    " and " +
                                    "@data-autotest-id='" + type + "']"))
                            .click();
                    return true;
                });
    }

    /**
     * Метод, который кликнет по первого элементу после выполнения поиска
     */
    public void clickFirst() {
        webDriverWait.until(
                d -> {
                    webDriver.findElement(By.xpath("//div[@data-zone-name='productSnippet']"))
                            .click();
                    return true;
                });
        switchWindow();
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
     * Метод, позволяет получить информацию с карточки товара на странице
     *
     * @return Возвращает информацию в виде текста
     */
    public String takeInfoFromPage() {
        return "Название магазина: " +
                webDriver.findElement(By.xpath("//div[@data-baobab-name='shopItem']//span")).getText() +
                "\nЦена товара: " +
                webDriver.findElement(By.xpath("//h3[@data-auto='snippet-price-current']")).getText()
                        .replaceAll("\\D", "");
    }

    /**
     * Реализует закрытие окна
     */
    public void quit() {
        webDriver.quit();
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
        driver.quit();

    }
}


