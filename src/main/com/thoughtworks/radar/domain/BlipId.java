package com.thoughtworks.radar;

import java.util.Objects;
import java.util.Optional;

public class BlipId implements Comparable<BlipId> {
    // was an int initially but became string on later radars
    private Optional<Integer> intId;
    private Optional<String> stringId;

    private BlipId(int i) {
        intId = Optional.of(i);
        stringId = Optional.empty();
    }

    private BlipId(String text) {
        stringId = Optional.of(text);
        intId =Optional.empty();
    }

    @Override
    public String toString() {
        if (intId.isPresent()) {
            return intId.get().toString();
        } else {
            return stringId.get();
        }
    }

    public static BlipId parse(String raw) {
        try {
            int intId = Integer.parseInt(raw);
            return BlipId.from(intId);
        }
        catch (NumberFormatException failedToParse) {
            return BlipId.from(raw);
        }
    }

    public static int compare(BlipId idA, BlipId idB) {
        return idA.compareTo(idB);
    }

    public static BlipId from(int i) {
        return new BlipId(i);
    }

    public static BlipId from(String text) {
        return new BlipId(text);
    }

    @Override
    public int compareTo(BlipId other) {
        if (intId.isPresent()) {
            Integer id = intId.get();
            if (other.intId.isPresent()) {
                return Integer.compare(id, other.intId.get());
            }
            if (other.stringId.isPresent()) {
                return id.toString().compareTo(other.stringId.get());
            }
        }
        if (stringId.isPresent()) {
            String id = stringId.get();
            if (other.stringId.isPresent()) {
                return id.compareTo(other.stringId.get());
            }
            if (other.intId.isPresent()) {
                return id.compareTo(other.intId.get().toString());
            }
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlipId blipId = (BlipId) o;
        return (this.compareTo(blipId)==0);
    }

    @Override
    public int hashCode() {
        if (intId.isPresent()) {
            return Objects.hash(intId.get());
        }
        if (stringId.isPresent()) {
            return Objects.hash(stringId.get());
        }
        return Objects.hash(intId,stringId);
    }
}
