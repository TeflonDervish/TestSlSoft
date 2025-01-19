import model.Item;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import service.CheckData;
import yandexMarket.MainPage;
import yandexMarket.ProductPage;
import yandexMarket.SearchResultPage;

import java.util.List;

/**
 * Тупо Main
 */
public class Main {

    public static void main(String[] args) {

        String query = "POCO X6 PRO";

        WebDriver driver = new ChromeDriver();
        MainPage mainPage = new MainPage(driver);
        SearchResultPage searchResultPage = mainPage.search(query);

        List<Item> items = searchResultPage.getItems();
        items.forEach(System.out::println);

        System.out.println(CheckData.checkItems(items, query));

        searchResultPage.clickSort(SearchResultPage.SortType.CHEAPER);
        ProductPage productPage = searchResultPage.clickFirst();

        System.out.println(productPage.takeInfoFromPage());

        driver.get("https://market.yandex.ru/");


    }
}
