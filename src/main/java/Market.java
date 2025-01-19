import java.util.List;

public abstract class Market {

    public abstract List<? extends Item> search(String query);

    public abstract void clickSort(String type);

    public abstract void clickFirst();

    public abstract void switchWindow();

    public abstract String takeInfoFromPage();

    public static double checkItems(List<Item> items, String query) {
        double result = 0;

        for (Item item: items)
            result += item.getName().toLowerCase().contains(query.toLowerCase()) ? 1 : 0;

        return result / items.size();
    }
}
