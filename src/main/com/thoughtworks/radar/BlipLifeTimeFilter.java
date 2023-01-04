package com.thoughtworks.radar;

import com.thoughtworks.radar.domain.BlipLifetime;

public interface BlipLifeTimeFilter {
    boolean filter(BlipLifetime item);
}
