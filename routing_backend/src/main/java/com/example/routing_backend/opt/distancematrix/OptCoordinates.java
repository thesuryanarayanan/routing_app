package com.example.routing_backend.opt.distancematrix;

import java.math.BigDecimal;
import java.util.Locale;

public record OptCoordinates(BigDecimal lat, BigDecimal lon) {

    public String key() {
        return String.format(Locale.ENGLISH, "%s,%s", lat, lon);
    }
}
