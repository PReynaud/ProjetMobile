package com.epul.ProjetMobile.service;

import com.epul.ProjetMobile.business.Place;

import java.util.List;

/**
 * Created by Pierre on 24/12/2015.
 */
public interface PlacesServiceDelegate {
    void placeMarkers(List<Place> listOfPlaces);

    void enableSearch();
}
