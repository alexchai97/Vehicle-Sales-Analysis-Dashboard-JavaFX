package com.dashboard;

import java.util.*;

public class SalesAnalysis {
    public static Map<String, Float> calculateSummary(List<? extends Number> quantities) {
        // Initialize variables for sum, average, median, min, and max
        float sum = 0;
        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;
        float median;

        // Calculate sum, min, and max
        for (Number quantity : quantities) {
            float value = quantity.floatValue();
            sum += value;
            if (value < min) {
                min = value;
            }
            if (value > max) {
                max = value;
            }
        }

        // Calculate average
        float avg = sum / quantities.size();

        // Sort the list of quantities
        List<Number> sortedQuantities = new ArrayList<>(quantities);
        Collections.sort(sortedQuantities, Comparator.comparingDouble(Number::doubleValue));

        // Calculate median
        int size = sortedQuantities.size();
        if (size % 2 == 1) {
            // If the list has an odd length, the median is the middle element
            median = sortedQuantities.get(size / 2).floatValue();
        } else {
            // If the list has an even length, the median is the average of the two middle
            // elements
            median = (sortedQuantities.get(size / 2 - 1).floatValue() + sortedQuantities.get(size / 2).floatValue())
                    / 2;
        }

        // Create a map to store the summary statistics
        Map<String, Float> summary = new HashMap<>();
        summary.put("Sum", sum);
        summary.put("Average", avg);
        summary.put("Median", median);
        summary.put("Min", min);
        summary.put("Max", max);

        return summary;
    }

    public static HashMap<String, Integer> aggregateData(ArrayList<SalesRecord> salesData, String delim, boolean[] aggregateBy) {
        if (delim == null) {
            delim = " ";
        }
        
        HashMap<String, Integer> qtyMap = new HashMap<>();
    
        for (SalesRecord record : salesData) {
            // Initialize an array of keys based on the record's properties
            String[] keys = new String[] {
                String.valueOf(record.Year),
                "Q" + record.QTR,
                record.Region,
                record.Vehicle
            };
    
            List<String> keyParts = new ArrayList<>();
    
            // Build the key using the specified aggregation keys
            for (int i = 0; i < aggregateBy.length; i++) {
                if (aggregateBy[i]) {
                    keyParts.add(keys[i]);
                }
            }
    
            // Join key parts to form the key using the specified delimiter
            String key = String.join(delim, keyParts);
    
            if (key.isEmpty()) {
                key = "Total Sales";
            }
    
            // Aggregate quantity for the key
            qtyMap.put(key, qtyMap.getOrDefault(key, 0) + record.Quantity);
        }
    
        return qtyMap;
    }
    
}
