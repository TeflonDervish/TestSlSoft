import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import model.Item;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
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

@Slf4j
public class YandexMarketTest {

    private WebDriver driver;
    private MainPage mainPage;

    private String testDataFile;

    /**
     * Инициализируем драйвер
     */
    @BeforeClass
    @Parameters({"testData"})
    public void setUpClass(String testData) {
        driver = new ChromeDriver();
        testDataFile = testData;
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

        return objectMapper.readValue(
                new File(testDataFile),
                Map[].class
        );
    }

    /**
     * Главный сценарий тестирования <br>
     * 1) Сделать глобальный запрос<br>
     * 2) Получить данные из сделанного запроса<br>
     * 3) Проверить кол-во правильно найденных результатов, если их больше половины, то тест пройден<br>
     * 4) Отсортировать по дешевизне<br>
     * 5) Кликнуть на первый элемент<br>
     * 6) Получить информацию о магазине и цене<br>
     * @param testData - Тестовые данные
     */
    @Test(description = "Поиск товаров Яндекс Маркете, сортировка и проверка результатов",
            dataProvider = "searchData")
    public void searchTest(Map<String, String> testData) {
        SoftAssert softAssert = new SoftAssert();

        System.out.println(testData.get("searchQuery"));

        // 1) Глобальный запрос по странице
        SearchResultPage searchResultPage = makeGlobalSearch(mainPage, testData.get("searchQuery"));

        // 2) Получение данных из сделанных запросов
        List<Item> items = getItems(searchResultPage);

        // 3) Проверка кол-ва правильно найденных результатов, если их больше половины, то тест пройден
        checkResult(softAssert, items, testData.get("searchQuery"));

        // 4) Нажать на кнопку с сортировкой подешевле
        clickSort(searchResultPage, SearchResultPage.SortType.CHEAPER);

        // 5) Кликнуть на первый найденный элемент на странице
        ProductPage productPage = clickFirst(searchResultPage);

        // 6) Вывод в консоль информацию о цене и продавце
        Item item = takeInfoFromPage(productPage);
        System.out.println(
                "\tНазвание магазина: " + item.getShopName() +
                        "\n\tЦена в магазине: " + item.getPrice()
        );

        softAssert.assertAll();
    }

    @Step
    public static SearchResultPage makeGlobalSearch(MainPage mainPage, String searchQuery) {
        return mainPage.search(searchQuery);
    }

    @Step
    public static List<Item> getItems(SearchResultPage searchResultPage){
        return searchResultPage.getItems();
    }

    @Step
    public static void checkResult(SoftAssert softAssert, List<Item> items, String searchQuery) {
        softAssert.assertTrue(CheckData.checkItems(items, searchQuery) > 0.5,
                "Количество правильно найденных элементов меньше половины");
    }

    @Step
    public static void clickSort(SearchResultPage searchResultPage, SearchResultPage.SortType sortType){
        searchResultPage.clickSort(sortType);
    }

    @Step
    public static ProductPage clickFirst(SearchResultPage searchResultPage) {
        return searchResultPage.clickFirst();
    }

    @Step
    public static Item takeInfoFromPage(ProductPage productPage) {
        return productPage.takeInfoFromPage();
    }

    /**
     * Закрыть драйвер
     */
    @AfterClass
    public void tearDownClass() {
        driver.quit();
    }

}