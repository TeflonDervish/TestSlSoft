package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс для сохранения информации о товаре
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    private String name; // Название продукта
    private Integer price; // Цена продукта
    private String shopName; // Название магазина

    public Item(String name, String price) {
        this(
                name,
                Integer.parseInt(price.replaceAll("\\D", ""))
        );
    }

    public Item(String name, Integer price) {
        this.name = name;
        this.price = price;
    }

    public Item(String name, String price, String shopName) {
        this(name, price);
        this.shopName = shopName;
    }

    public void setPrice(String price) {
        this.price = Integer.parseInt(price.replaceAll("\\D", ""));
    }

}
