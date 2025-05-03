package com.collective.projectcore.utils;

public class UtilMethods {

    public static String sortStringUppercase(String string) {
        StringBuilder newstr = new StringBuilder();
        StringBuilder upper = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            if (Character.isUpperCase(string.charAt(i))) {
                upper.append(string.charAt(i));
            }
            else {
                newstr.append(string.charAt(i));
            }
        }
        return upper+ newstr.toString();
    }
}
