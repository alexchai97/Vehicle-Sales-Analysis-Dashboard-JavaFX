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

    // Aggregate data function
    public static HashMap<String, Integer> aggregateData(
            ArrayList<SalesRecord> salesData, String delim,
            int aggregateBy) {

        // Set the default delimiter if not provided
        if (delim == null) {
            delim = " ";
        }

        // Convert aggregateBy to a binary string
        String binaryString = Integer.toBinaryString(aggregateBy);

        // Pad the binary string with leading zeros to ensure it is 4 bits long
        binaryString = String.format("%4s", binaryString).replace(' ', '0');

        // Map to store the aggregated quantities
        HashMap<String, Integer> qtyMap = new HashMap<>();

        // Iterate through sales data
        for (SalesRecord record : salesData) {
            StringBuilder keyBuilder = new StringBuilder();

            String[] keys = new String[] {
                    "" + record.Year, // Convert Year to String
                    "Q" + record.QTR, // Combine 'Q' with the quarter
                    record.Region, // Add the Region directly
                    record.Vehicle // Add the Vehicle directly
            };

            // List to store the key parts
            List<String> keyParts = new ArrayList<>();

            // Iterate over the binary string and the keys array
            for (int i = 0; i < binaryString.length(); i++) {
                // If the binary string has a '1' at this position, add the corresponding key
                // part
                if (binaryString.charAt(i) == '1') {
                    keyParts.add(keys[i]);
                }
            }

            // Join the key parts with a space to form the final key
            String key = String.join(delim, keyParts);

            // If the key is empty, set it to "Total"
            if (key.isEmpty()) {
                key = "Total Sales";
            }

            // Get the current quantity for the generated key, defaulting to 0
            int currentQty = qtyMap.getOrDefault(key, 0);

            // Add the record's quantity to the current quantity
            currentQty += record.Quantity;

            // Update the map with the new quantity
            qtyMap.put(key, currentQty);
        }

        return qtyMap;
    }
}
