package yandexMarket;

import lombok.Data;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import java.time.Duration;
import java.util.Set;

/**
 * Класс для работы с главной страницой YandexMarket
 */
@Data
public class MainPage {

    private final WebDriver webDriver;
    private final Wait<WebDriver> webDriverWait;
    private final Actions actions;

    private final By searchInput = By.xpath("//input[@data-auto='search-input']");

    /**
     * Конструктор без явного указания времени ожидания
     *
     * @param webDriver - driver, для выполнения действий
     */
    public MainPage(WebDriver webDriver) {
        this(webDriver, 10);
    }

    /**
     * Конструктор с явным указанием времени ожидания
     *
     * @param webDriver - driver, для выполнения действий
     * @param duration  - время ожидания
     */
    public MainPage(WebDriver webDriver, int duration) {
        this.webDriver = webDriver;
        // Создания кастомизированного ожидания
        webDriverWait =
                new FluentWait<>(webDriver)
                        .withTimeout(Duration.ofSeconds(duration))
                        .pollingEvery(Duration.ofMillis(300))
                        .ignoring(ElementClickInterceptedException.class)
                        .ignoring(StaleElementReferenceException.class);
        actions = new Actions(webDriver);

        //Обычное ожидание
        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(duration));

        // Открытие браузера
        webDriver.get("https://market.yandex.ru/");

        // Предназначен для закрытия всплывающего окна, с просьбой войти в аккаунт
        actions.click().perform();

        // Закрываем все лишние открывшиеся окна
        closeAllWindowsExceptTheCurrent();
    }

    /**
     * Метод, для выполнения глобального поиска по строке
     *
     * @param query - строка поиска
     * @return - возвращает страницу с результатами поиска
     */
    public SearchResultPage search(String query) {
        webDriver.findElement(searchInput)
                .sendKeys(query, Keys.ENTER);
        return new SearchResultPage(webDriver, webDriverWait);
    }


    /**
     * Закрывает все окна кроме текущего
     */
    public void closeAllWindowsExceptTheCurrent() {
        // Получение текущего окна
        String currentWindow = webDriver.getWindowHandle();

        // Получение всех открытых окон
        Set<String> windows = webDriver.getWindowHandles();

        // Перебор отрытых окон, в поисках лишних окон
        for (String window : windows) {
            if (!window.equals(currentWindow)) {
                webDriver.switchTo().window(window);
                webDriver.close();
                webDriver.switchTo().window(currentWindow);
            }
        }
    }

}
