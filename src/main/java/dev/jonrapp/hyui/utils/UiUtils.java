package dev.jonrapp.hyui.utils;

public class UiUtils {

    public static String getArraySelector(String selector, int index){
        return selector + index;
    }

    public static String selectors(String... s) {
        return String.join(" ", s);
    }

}
