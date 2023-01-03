package com.thoughtworks.radar.domain;

import java.util.Objects;

public class UniqueBlipId implements Comparable<UniqueBlipId> {
    // was an int initially but became string on later radars
    private final int id;

    private UniqueBlipId(int i) {
        id = i;
    }

    @Override
    public String toString() {
        return Integer.toString(id);
    }

    public static UniqueBlipId parse(String raw) {
        try {
            int intId = Integer.parseInt(raw);
            return UniqueBlipId.from(intId);
        }
        catch (NumberFormatException failedToParse) {
            throw new RuntimeException("All blip ids are now numeric");
        }
    }

    public static int compare(UniqueBlipId idA, UniqueBlipId idB) {
        return idA.compareTo(idB);
    }

    public static UniqueBlipId from(int i) {
        return new UniqueBlipId(i);
    }

    public static UniqueBlipId from(String text) {
        return parse(text);
    }

    @Override
    public int compareTo(UniqueBlipId other) {
        return Integer.compare(id, other.id);

//        if (intId.isPresent()) {
//            Integer id = intId.get();
//            if (other.intId.isPresent()) {
//                return Integer.compare(id, other.intId.get());
//            }
//            if (other.stringId.isPresent()) {
//                return id.toString().compareTo(other.stringId.get());
//            }
//        }
//        if (stringId.isPresent()) {
//            String id = stringId.get();
//            if (other.stringId.isPresent()) {
//                return id.compareTo(other.stringId.get());
//            }
//            if (other.intId.isPresent()) {
//                return id.compareTo(other.intId.get().toString());
//            }
//        }
//        return 0;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UniqueBlipId that = (UniqueBlipId) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
