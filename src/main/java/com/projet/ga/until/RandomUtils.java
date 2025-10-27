package com.projet.ga.util;

import java.util.Random;

public class RandomUtils {
    public static double uniform(Random r, double lo, double hi){
        return lo + r.nextDouble()*(hi-lo);
    }
}
