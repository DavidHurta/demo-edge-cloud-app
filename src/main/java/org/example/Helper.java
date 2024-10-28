package org.example;

public class Helper {
    public static double safeDivision(double a, double b) {
        if (b == 0) {
            return 0;
        }
        return a / b;
    }
}
