package service;

import model.Item;

import java.util.List;

/**
 * Класс предназначенный для проверки и валидации данных полученных от браузера
 */
public class CheckData {

    /**
     * Метод проверяет, удовлетворяют ли элементы в списке введеному запросу
     * @param items - список найденных элементов
     * @param query - запрос глобального посика
     * @return - возвращает число от 0 до 1, чем больше число, тем больше совпадений нашлось на странице
     */
    public static double checkItems(List<Item> items, String query) {
        double result = 0;

        for (Item item: items)
            result += item.getName().toLowerCase().contains(query.toLowerCase()) ? 1 : 0;

        return result / items.size();
    }
}
