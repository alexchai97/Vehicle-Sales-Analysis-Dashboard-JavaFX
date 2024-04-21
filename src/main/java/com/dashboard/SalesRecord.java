package com.dashboard;

import java.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class SalesRecord {
    public int Year;
    public int QTR;
    public String Region;
    public String Vehicle;
    public int Quantity;

    public SalesRecord(int Year, int QTR, String Region, String Vehicle, int Quantity) {
        this.Year = Year;
        this.QTR = QTR;
        this.Region = Region;
        this.Vehicle = Vehicle;
        this.Quantity = Quantity;
    }
    
    @Override
    public String toString() {
        return String.format("SalesRecord [Year=%d, QTR=%d, Region=%s, Vehicle=%s, Quantity=%d]",
                             Year, QTR, Region, Vehicle, Quantity);
    }

    // Define a print method to print the SalesRecord object using the toString method
    public void print() {
        System.out.println(this.toString());
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("Year", Year);
        map.put("QTR", QTR);
        map.put("Region", Region);
        map.put("Vehicle", Vehicle);
        map.put("Quantity", Quantity);
        return map;
    }

    public SimpleIntegerProperty getYearProperty() {
        return new SimpleIntegerProperty(Year);
    }

    public SimpleIntegerProperty getQTRProperty() {
        return new SimpleIntegerProperty(QTR);
    }

    public SimpleStringProperty getRegionProperty() {
        return new SimpleStringProperty(Region);
    }

    public SimpleStringProperty getVehicleProperty() {
        return new SimpleStringProperty(Vehicle);
    }

    public SimpleIntegerProperty getQuantityProperty() {
        return new SimpleIntegerProperty(Quantity);
    }
    
}
