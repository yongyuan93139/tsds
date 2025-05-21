package com.example.inspectioncarparts.util;

import java.util.Collection;
import java.util.Map;

public final class CollectionUtils {
    private CollectionUtils() {}

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    public static boolean contains(Collection<?> collection, Object item) {
        if (isEmpty(collection)) {
            return false;
        }
        return collection.contains(item);
    }
}