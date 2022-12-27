package com.thoughtworks.radar;

import com.thoughtworks.radar.domain.Blip;

public interface BlipFilter {
    boolean filter(Blip item);
}
