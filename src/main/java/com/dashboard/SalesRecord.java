package com.dashboard;

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
}
