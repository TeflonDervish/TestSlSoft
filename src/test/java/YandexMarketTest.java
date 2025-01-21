import ch.qos.logback.core.util.FileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Attachment;
import io.qameta.allure.Description;
import io.qameta.allure.Flaky;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import model.Item;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
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
    private SearchResultPage searchResultPage;
    private ProductPage productPage;

    private SoftAssert softAssert;

    private String testDataFile;

    /**
     * Инициализируем драйвер<br>
     * Получаем тестовый файл
     */
    @BeforeClass
    @Parameters({"testData"})
    public void setUpClass(@Optional("") String testData) {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        testDataFile = testData;
    }

    /**
     * Перед каждой новой итерацией заново создаем главную страницу и закрываем все лишние вкладки
     */
    @BeforeMethod
    public void prepareTest() {
        mainPage = new MainPage(driver, 30);
        softAssert = new SoftAssert();
    }

    /**
     * Получение тестовых данных
     *
     * @return возвращает Массив из данных
     * @throws IOException в случае если тестового файла нет
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
     * 3) Проверка есть ли искомый товар с списке<br>
     * 4) Отсортировать по дешевизне<br>
     * 5) Кликнуть на первый элемент<br>
     * 6) Получить информацию о магазине и цене<br>
     *
     * @param testData Тестовые данные
     */
    @Test(dataProvider = "searchData")
    @Description("Поиск товаров Яндекс Маркете, сортировка и проверка результатов")
    public void searchTest(Map<String, String> testData) {
        log(testData.get("searchQuery"));

        // 1) Глобальный запрос по странице
        searchResultPage = makeGlobalSearch(testData.get("searchQuery"));

        // 2) Получение данных из сделанных запросов
        List<Item> items = getItems();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            result.append(items.get(i).getName()).append("\n\t").append(items.get(i).getPrice()).append("\n");
        }
        log(result.toString());

        // 3) Проверка кол-ва правильно найденных результатов, если их больше половины, то тест пройден
        checkData(items, testData.get("searchQuery"));

        // 4) Нажать на кнопку с сортировкой подешевле
        clickSort(SearchResultPage.SortType.CHEAPER);

        // 5) Кликнуть на первый найденный элемент на странице
        productPage = clickFirst();

        // 6) Вывод в консоль информацию о цене и продавце
        Item item = takeInfoFromPage();
        log("\n\tНазвание магазина: " + item.getShopName() +
                "\n\tЦена товара: " + item.getPrice());
        saveScreenshot();

    }

    @Step("1) Поиска {query}")
    public SearchResultPage makeGlobalSearch(String query) {
        return mainPage.search(query);
    }

    @Step("2) Получение данных из сделанных запросов")
    public List<Item> getItems() {
        return searchResultPage.getItems();
    }

    @Step("3) Проверка есть ли искомый товар с списке")
    public void checkData(List<Item> items, String query) {
        softAssert.assertTrue(CheckData.checkItems(items, query));
    }

    @Step("4) Нажать на кнопку с сортировкой подешевле")
    public void clickSort(SearchResultPage.SortType sortType) {
        searchResultPage.clickSort(sortType);
    }

    @Step("5) Кликнуть на первый найденный элемент на странице")
    public ProductPage clickFirst() {
        return searchResultPage.clickFirst();
    }

    @Step("6) Вывод в консоль информацию о цене и продавце")
    public Item takeInfoFromPage() {
        return productPage.takeInfoFromPage();
    }

    /**
     * Метод для вывода логов в отчет
     * @param log текстовое сообщение
     * @return возвращает текст
     */
    @Attachment(value = "Информация во время выполнения", type = "text/plain")
    public String log(String log) {
        return log;
    }

    /**
     * Метод для добавления скриншота в отчет
     * @return возвращает скриншот страницы
     */
    @Attachment(value = "Страница с товаром", type = "image/png")
    public byte[] saveScreenshot() {
        try {
            return FileUtils.readFileToByteArray(
                    ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE)
            );
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    /**
     * Вывести все ошибки
     */
    @AfterMethod
    public void afterTest() {
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