package es.dsrroma.garantator.utils;

import es.dsrroma.garantator.data.model.AbstractBaseModel;

public class MyStringUtils {

    public static String notEmpty(String s) {
        if (s == null) {
            return "";
        }
        return s;
    }

    public static boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public static boolean isNotEmpty(String s) {
        return s != null && !s.isEmpty();
    }

    public static boolean hasName(AbstractBaseModel model) {
        return model != null && isNotEmpty(model.getName());
    }
}
