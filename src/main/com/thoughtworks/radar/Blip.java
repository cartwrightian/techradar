package com.thoughtworks.radar;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Blip implements Comparable<Blip> {
    private final int id;
    private String name;
    private List<BlipHistory> historyList;

    public Blip(int id, String name) {
        this.id = id;
        this.name = name;
        historyList = new LinkedList<>();
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Blip blip = (Blip) o;
        return id == blip.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Blip other) {
        return Integer.compare(this.id, other.id);
    }

    public List<BlipHistory> getHistory() {
        return historyList;
    }

    public void addHistory(BlipHistory blipHistory) {
        historyList.add(blipHistory);
    }
}
