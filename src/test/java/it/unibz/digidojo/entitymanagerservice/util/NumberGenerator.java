package it.unibz.digidojo.entitymanagerservice.util;

import java.util.concurrent.ThreadLocalRandom;

public class NumberGenerator {
    public static Long randomPositiveLong() {
        return ThreadLocalRandom.current().nextLong(1, 1000);
    }
}
