package com.mb.prestartcheck;

import java.util.ArrayList;


public class Checklist {
    ArrayList<Section> sections = new ArrayList<Section>();
    User user;
    ArrayList<Response> responses = new ArrayList<Response>();

    private long id;
    private String label;

    public long getId() {
        return id;
    }

    public  String getLabel() { return label;}

    public Checklist(long id,  String label)
    {
        this.id = id;
        this.label = label;
    }
}
