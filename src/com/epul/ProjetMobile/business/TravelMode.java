package com.epul.ProjetMobile.business;

import com.epul.ProjetMobile.service.PlacesService;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @Author Dimitri on 09/01/2016.
 * @Version 1.0
 */
public enum TravelMode {
    WALKING("WALKING"), BUS("BUS"), TRAM("TRAM"), SUBWAY("SUBWAY");

    public final String mode;

    TravelMode(String mode) {
        this.mode = mode;
    }

    public static TravelMode getByName(String name) {
        if (name != null) {
            for (TravelMode travelMode : TravelMode.values()) {
                if (name.equals(travelMode.mode)) {
                    return travelMode;
                }
            }
            Logger.getLogger(PlacesService.class.getName()).log(Level.WARNING,
                    null, String.format("No match for %s", name));
        }
        return null;
    }
}
