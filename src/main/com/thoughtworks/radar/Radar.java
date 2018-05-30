package com.thoughtworks.radar;

import java.util.*;

public class Radar {
    private SortedMap<Integer,Blip> blips;

    public Radar() {
        this.blips = new TreeMap();
    }

    public List<Blip> getBlips() {
        List<Blip> result = new LinkedList<>();
        result.addAll(blips.values());
        return result;
    }

    public void add(Parser.RawBlip rawBlip) {
        int id = rawBlip.getId();

        Blip blip;
        if (!blips.containsKey(id)) {
            blip = new Blip(id, rawBlip.getName());
            blips.put(id, blip);
        } else {
            blip = blips.get(id);
        }

        blip.addHistory(new BlipHistory(rawBlip.getDate(), rawBlip.getRing()));
    }
}
