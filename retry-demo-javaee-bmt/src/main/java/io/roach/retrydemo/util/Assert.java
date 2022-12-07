package io.roach.retrydemo.util;

public abstract class Assert {
    private Assert() {
    }

    public static void isTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }

    public static void isFalse(boolean condition, String message) {
        if (condition) {
            throw new IllegalStateException(message);
        }
    }

    public static void notNull(Object obj, String message) {
        if (obj == null) {
            throw new IllegalStateException(message);
        }
    }
}
