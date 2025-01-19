import com.fasterxml.jackson.databind.ObjectMapper;
import model.Item;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import service.CheckData;
import yandexMarket.MainPage;
import yandexMarket.ProductPage;
import yandexMarket.SearchResultPage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class YandexMarketTest {

    private WebDriver driver;
    private MainPage mainPage;

    /**
     * Инициализируем драйвер
     */
    @BeforeClass
    public void setUpClass() {
        driver = new ChromeDriver();
    }

    /**
     * Перед каждой новой итерацией заново создаем главную страницу и закрываем все лишние вкладки
     */
    @BeforeMethod
    public void makeMainPage() {
        mainPage = new MainPage(driver);
    }

    /**
     * Получение тестовых данных
     * @return возвращает Массив из данных
     * @throws IOException - в случае если тестового файла нет
     */
    @DataProvider(name = "searchData")
    public Map<String, String>[] searchDataProvider() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String>[] testData = objectMapper.readValue(
                new File("src/test/resources/test-data.json"),
                Map[].class
        );

        return testData;
    }

    /**
     * Главный сценарий тестирования <br>
     * 1) Сделать глобальный запрос<br>
     * 2) Получить данные из сделанного запроса<br>
     * 3) Проверить кол-во правильно найденных результатов, если их больше половины, то тест пройден<br>
     * 4) Отсортировать по дешевизне<br>
     * 5) Кликнуть на первый элемент<br>
     * 6) Получить информацию о магазине и цене<br>
     * @param testData
     */
    @Test(description = "Поиск товаров Яндекс Маркете, сортировка и проверка результатов"
            ,dataProvider = "searchData")
    public void searchTest(Map<String, String> testData) {
        SoftAssert softAssert = new SoftAssert();

        SearchResultPage searchResultPage = mainPage.search(testData.get("searchQuery"));
        List<Item> items = searchResultPage.getItems();

        softAssert.assertTrue(CheckData.checkItems(items, testData.get("searchQuery")) > 0.5
                , "Количество правильно найденных элементов меньше трети");


        searchResultPage.clickSort(SearchResultPage.SortType.CHEAPER);
        ProductPage productPage = searchResultPage.clickFirst();

        System.out.println(productPage.takeInfoFromPage());

        softAssert.assertAll();

    }

    /**
     * Закрыть драйвер
     */
    @AfterClass
    public void tearDownClass() {
        driver.quit();
    }

}