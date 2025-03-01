package yandexMarket;

import lombok.Data;
import model.Item;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;

/**
 * Класс для работы со страницей продукта
 */
@Data
public class ProductPage {

    private final WebDriver webDriver;
    private final Wait<WebDriver> webDriverWait;
    private final Actions actions;

    private static final By productPrice = By.xpath("//*[@data-auto='snippet-price-current' or @data-auto='price-block']");
    private static final By productShop = By.xpath("//*[@data-baobab-name='shopItem']//span");

    /**
     * Конструктор для создания экземпляра другими страницами
     *
     * @param webDriver     - Драйвер браузера
     * @param webDriverWait - Явное ожидание
     */
    public ProductPage(WebDriver webDriver, Wait<WebDriver> webDriverWait) {
        this.webDriver = webDriver;
        this.webDriverWait = webDriverWait;
        this.actions = new Actions(webDriver);
    }

    /**
     * Метод для получения информации о странице в Item
     *
     * @return - возвращает экземпляр Item
     */
    public Item takeInfoFromPage() {
        webDriverWait.until(ExpectedConditions.presenceOfElementLocated(productPrice));
        webDriverWait.until(ExpectedConditions.presenceOfElementLocated(productShop));
        return new Item(
                "",
                webDriver.findElement(productPrice).getText(),
                webDriver.findElement(productShop).getText()
        );
    }
}
