package com.mb.prestartcheck.fins;

import java.util.Random;

public class FINSUtils {

    public static int getRandomInt(int bound)
    {
        Random random = new Random();
        return random.nextInt(bound);
    }

    public  static byte getByte(int value)
    {
        return (byte) (0xFF & value);
    }

    public  static int[] get2DigitBCDFromByte(byte b)
    {
        int[] ret = {0, 0};

        int num1 = 0;
        if ((b & (byte)1) > 0 ) ret[0] += 1;
        if ((b & (byte)2) > 0 ) ret[0] += 2;
        if ((b & (byte)4) > 0 ) ret[0] += 4;
        if ((b & (byte)8) > 0 ) ret[0] += 8;

        int num2 = 0;
        if ((b & (byte)16) > 0 ) ret[1] += 1;
        if ((b & (byte)32) > 0 ) ret[1] += 2;
        if ((b & (byte)64) > 0 ) ret[1] += 4;
        if ((b & (byte)128) > 0 ) ret[1] += 8;


        return ret;
    }
}
