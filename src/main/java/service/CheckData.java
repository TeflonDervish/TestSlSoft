package service;

import model.Item;

import java.util.List;

/**
 * Класс предназначенный для проверки и валидации данных полученных от браузера
 */
public class CheckData {

    /**
     * Метод проверяет, удовлетворяют ли элементы в списке введеному запросу
     *
     * @param items - список найденных элементов
     * @param query - запрос глобального посика
     * @return - возвращает истину если найден хоть один товар с таким названием
     */
    public static boolean checkItems(List<Item> items, String query) {
        return items.stream().anyMatch(x -> x.getName().toLowerCase().contains(query.toLowerCase()));
    }
}
