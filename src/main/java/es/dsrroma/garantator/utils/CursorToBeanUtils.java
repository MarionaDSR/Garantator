package es.dsrroma.garantator.utils;
// Inspired on https://gist.github.com/john990/8396626

import android.database.Cursor;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CursorToBeanUtils {

    private static final String SET = "set";

    public static <T> T cursorToBean(Cursor cursor, Class<T> clazz) {
        List<T> beans = cursorToBeans(cursor, clazz);
        if (beans.size() == 1) {
            return beans.get(0);
        } else {
            throw new IllegalArgumentException("The cursor have more than one result");
        }
    }

    public static <T> List<T> cursorToBeans (Cursor cursor, Class<T> clazz) {
        List<T> beans = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            String[] columns = cursor.getColumnNames();
            while (cursor.moveToNext()) {
                try {
                    T bean = clazz.newInstance();
                    for (String column : columns) {
                        setValue(cursor, column, clazz, bean);
                    }
                    beans.add(bean);
                } catch (InstantiationException e) {
                    throw new IllegalArgumentException("Class " + clazz.getName() + " not instantiable", e);
                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException("Constructor method for " + clazz.getName() + " not accessible", e);
                }

            }
        }
        return beans;
    }

    private static <T> void setValue(Cursor cursor, String column, Class<T> clazz, T bean) {
        try {
            Field field = clazz.getField(column);
            Method setter = getSetter(column, field, clazz);
            Object value = getFieldValue(cursor, column, field);
            setter.invoke(bean, value);
        } catch(NoSuchFieldException e){
            throw new IllegalArgumentException("No field for column " + column + " in " + clazz.getName(), e);
        } catch(InvocationTargetException e){
            throw new IllegalArgumentException("setter method for " + column + " throw an exception", e);
        } catch(IllegalAccessException e){
            throw new IllegalArgumentException("setter method for " + column + " not accessible", e);
        }
    }

    private static <T> Object getFieldValue(Cursor cursor, String column, Field field) {
        int index = cursor.getColumnIndex(column);
        Class type = field.getType();
        if (type == String.class) {
            return cursor.getString(index);
        } else if (type == int.class || type == Integer.class) {
            return cursor.getInt(index);
        } else if (type == long.class || type == Long.class) {
            return cursor.getLong(index);
        } else if (type == Date.class) {
            return cursor.getString(index); // TODO parse to Date
        } else {
            throw new UnsupportedOperationException("Unable to get " + type + " elements.");
        }
    }

    private static Method getSetter(String column, Field field, Class clazz) {
        try {
            String setterName = getSetterName(column);
            return clazz.getMethod(setterName, new Class[]{field.getType()});
        } catch (NoSuchMethodException e) {
            Log.d("getSetter", e.getMessage());
            throw new IllegalArgumentException("No setter for " + field.getName() + " in " + clazz.getName());
        }
    }

    private static String getSetterName(String column) {
        return SET + column.substring(0, 1).toUpperCase() + column.substring(1);
    }
}
