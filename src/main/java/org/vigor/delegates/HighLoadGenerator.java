package org.vigor.delegates;

import java.util.Random;

public class HighLoadGenerator {

    private static final int ARRAY_SIZE = 1024 * 1024 * 20;
    private static final int COUNT = 10;

    public static void loadCpu() {
        Random random = new Random();

        for (int i = 0; i < COUNT; i++) {
            int[] arr = new int[ARRAY_SIZE];
            for (int j=0; j < ARRAY_SIZE; j++) {
                arr[j] = random.nextInt();
            }
        }
    }
}
