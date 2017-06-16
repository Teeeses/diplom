package com.library.example.cifar10;

import com.library.example.cifar10.network.CNNdroid;

/**
 * Created by develop on 16.06.2017.
 */

public class Utils {

    public static long getAllocatedRAM(long value) {
        long allocatedRAM;
        if (value < CNNdroid.MAX_PARAM_SIZE) {
            allocatedRAM = value * 1024 * 1024;
        }
        else {
            allocatedRAM = CNNdroid.MAX_PARAM_SIZE;
        }
        return allocatedRAM;
    }
}
