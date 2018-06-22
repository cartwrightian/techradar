package com.thoughtworks.radar;

import java.util.ArrayList;

public class Quartiles {

    private ArrayList<Long> results;

    public Quartiles() {
        results = new ArrayList<>(4);
    }

    public void add(int quater, long halflife) {
        results.add(quater-1,halflife);
    }

    public long get(int quarter) {
        return results.get(quarter-1);
    }
}
