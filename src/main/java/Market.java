import java.util.List;


/**
 * Класс, который описывает основные методы работы с разными системами для онлайн покупок
 */
public abstract class Market {

    /**
     * Метод предназначенный для глобального поиска по странице
     *
     * @param query - здесь вводится поисковой запрос
     * @return - возвращает список найденных элементов на странице поиска
     */
    public abstract List<? extends Item> search(String query);

    /**
     * Метод необходим, чтобы использовать сортировку по странице
     *
     * @param type - позволяет выбрать тип сортировки
     */
    public abstract void clickSort(String type);

    /**
     * Метод, который кликнет по первого элементу после выполнения поиска
     */
    public abstract void clickFirst();

    /**
     * Переключается на новое окно, закрывая предыдущее
     */
    public abstract void switchWindow();

    /**
     * Метод, позволяет получить информацию с карточки товара на странице
     *
     * @return Возвращает информацию в виде текста
     */
    public abstract String takeInfoFromPage();

    /**
     * Реализует закрытие окна
     */
    public abstract void quit();

    /**
     * Метод, который проверяет насколько поисковой запрос
     * выдал информацию в соответствии с введенным запросом
     *
     * @param items - список, который получается после поиска информации по странице
     * @param query - запрос для глобального поиска
     * @return - возвращает значение от 0 до 1, чем выше значение, тем больше совпадений по названию было
     */
    public static double checkItems(List<? extends Item> items, String query) {
        double result = 0;

        for (Item item : items)
            result += item.getName().toLowerCase().contains(query.toLowerCase()) ? 1 : 0;

        return result / items.size();
    }
}
