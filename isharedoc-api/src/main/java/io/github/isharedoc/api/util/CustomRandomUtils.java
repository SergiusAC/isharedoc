package io.github.isharedoc.api.util;

import org.apache.commons.lang3.RandomStringUtils;

public class CustomRandomUtils {

    private static final RandomStringUtils RANDOM_STRING_UTILS = RandomStringUtils.secure();

    public static String randomNumericString(int length) {
        return RANDOM_STRING_UTILS.nextNumeric(length);
    }

}
