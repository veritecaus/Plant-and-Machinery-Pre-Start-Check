package com.mb.prestartcheck;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class Lottery {

    static Lottery instance;
    Random random;

    public static Lottery getInstance() {
        if (instance == null) instance = new Lottery();
        Date now = new Date();
        instance.random = new Random(now.getTime());
        return instance;
    }

    public  int getNumber(int max)
    {
        return this.random.nextInt(max); //exclusive of max.
    }

    public ArrayList<Integer> getRandomSet(int max)
    {
        //Let n, nr be sets containing values from a linear sequence set to to k ( 1,2,3, .... k).
        //Select an element in n that is not in nr.
        ArrayList<Integer> n = new ArrayList<Integer>();
        ArrayList<Integer> nr = new ArrayList<Integer>();

        for(int i = 0; i < max; i++)
            n.add(i);

        for(int i = 0; i < max; i++)
        {
            int ridx = getNumber(n.size());
            nr.add(n.get(ridx));
            n.remove(ridx);
        }

        return nr;
    }


}
