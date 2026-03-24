package me.instrumentalityi.contentapi.paper.utils;

import java.util.Arrays;

public class ClassUtil {

    public static boolean isDirectChild(Class<?> parent, Class<?> child) {
        return child.getSuperclass() == parent ||
                Arrays.stream(child.getInterfaces()).anyMatch(i -> i == parent);
    }
}
