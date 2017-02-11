package com.concordia.mcga.models;

import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Building {
    private LatLng centerCoordinate;
    private String shortName;
    private String name;
    private List<LatLng> edgeCoordinateList;
    private final static int strokeColor = Color.YELLOW;
    private final static int strokeWidth = 2;
    private final static int fillColor = 0x996d171f;

    public Building(LatLng coordinates, String name, String shortName) {
        this.centerCoordinate = coordinates;
        this.name = name;
        this.shortName = shortName;
        edgeCoordinateList = new ArrayList<>();
    }

    public LatLng getCenterCoordinates(){
        return centerCoordinate;
    }

    public String getName(){
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public void addEdgeCoordinate(LatLng ... edgeCoordinates){
        edgeCoordinateList = Arrays.asList(edgeCoordinates);
    }

    public PolygonOptions getPolygonOverlayOptions(){

        if(edgeCoordinateList.isEmpty()){
            return null;
        }

        PolygonOptions polygonOptions = new PolygonOptions();

        for (LatLng edgeCoordinate : edgeCoordinateList) {
            polygonOptions.add(edgeCoordinate);
        }

        polygonOptions.strokeColor(strokeColor)
                    .strokeWidth(strokeWidth)
                    .fillColor(fillColor);

        return polygonOptions;
    }

}
