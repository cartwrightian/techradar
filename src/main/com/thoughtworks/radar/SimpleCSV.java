package com.thoughtworks.radar;

import java.util.LinkedList;
import java.util.List;

public class SimpleCSV  implements ToCSV {
    List<String> items;

    public SimpleCSV() {
        items = new LinkedList<>();
    }

    public SimpleCSV add(String item) {
        items.add(item);
        return this;
    }

    @Override
    public String toCSV() {
        StringBuilder output = new StringBuilder();
        items.forEach(item -> {
            if (output.length()>0) {
                output.append(',');
            }
            output.append(item);
        });
        return output.toString();
    }

    @Override
    public String getHeader() {
        // assume caller of class adds header
        return "";
    }
}
