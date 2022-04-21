package com.mb.prestartcheck;

import java.util.Comparator;

public class ComparerResponse implements Comparator<Response> {
    @Override
    public int compare(Response o1, Response o2) {
        if (o1 == null && o2 != null ) return -1;

        if (o1 != null && o2 == null ) return 1;

        if (o1 == null && o2 == null) return 0;

        if (!o1.getClass().isInstance(Response.class) && o2.getClass().isInstance(Response.class)) return -1;

        if (o1.getClass().isInstance(Response.class) && !o2.getClass().isInstance(Response.class)) return 1;

        Response r1 = (Response)o1;
        Response r2 = (Response)o2;

        return (int)(r1.getSection().getId() - r2.getSection().getId() +
                r1.getQuestion().getId() - r2.getQuestion().getId());

    }
}
