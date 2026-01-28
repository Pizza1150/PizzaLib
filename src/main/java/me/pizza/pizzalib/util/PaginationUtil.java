package me.pizza.pizzalib.util;

import java.util.List;

public class PaginationUtil {

    private PaginationUtil() {
    }

    public static int getMaxPage(int size, int perPage) {
        if (size <= 0) return 0;
        return (size - 1) / perPage;
    }

    public static <T> List<T> getItemsForPage(List<T> items, int page, int perPage) {
        if (items.isEmpty()) return List.of();

        int fromIndex = page * perPage;
        if (fromIndex >= items.size()) return List.of();

        int toIndex = Math.min(fromIndex + perPage, items.size());

        return items.subList(fromIndex, toIndex);
    }
}
