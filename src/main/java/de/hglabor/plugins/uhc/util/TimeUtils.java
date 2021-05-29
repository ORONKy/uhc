package de.hglabor.plugins.uhc.util;

import java.util.concurrent.TimeUnit;

public final class TimeUtils {
    private TimeUtils() {}

    public static int getDiffInSeconds(long futureTimeStamp) {
        return (int) TimeUnit.MILLISECONDS.toSeconds(futureTimeStamp - System.currentTimeMillis());
    }

    public static int getDiffInSeconds(long firstTimeStamp, long secondTimeStamp) {
        return (int) TimeUnit.MILLISECONDS.toSeconds(firstTimeStamp - secondTimeStamp);
    }
}
