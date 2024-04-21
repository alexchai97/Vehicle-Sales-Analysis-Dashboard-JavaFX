package com.dashboard;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class SalesReader {
    public ArrayList<SalesRecord> salesData = new ArrayList<>();

    // To store unique set of values for the data
    public Set<String> YearSet = new LinkedHashSet<>();
    public Set<String> QuarterSet = new LinkedHashSet<>();
    public Set<String> RegionSet = new LinkedHashSet<>();
    public Set<String> VehicleSet = new LinkedHashSet<>();

    public void readData(String link) throws Exception {
        // Check if the link is a local file or a URL
        File file = new File(link);
        StringBuilder jsonData = new StringBuilder();

        // If the link is a local file
        if (file.exists() && !file.isDirectory()) {
            // Read data from the local file
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonData.append(line);
                }
            }
        } else {
            URL jsonLink = new URL(link);

            HttpURLConnection connection = (HttpURLConnection) jsonLink.openConnection();
            connection.setRequestMethod("GET");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonData.append(line);
                }
            }
        }

        Gson gson = new Gson();
        TypeToken<ArrayList<SalesRecord>> token = new TypeToken<ArrayList<SalesRecord>>() {
        };
        ArrayList<SalesRecord> data = gson.fromJson(jsonData.toString(), token.getType());

        salesData.addAll(data);
        
        saveDataToLocal("data.json");
        initSets();
    }

    private void initSets() {
        for (SalesRecord record : salesData) {
            YearSet.add("" + record.Year);
            QuarterSet.add("Q" + record.QTR);
            RegionSet.add(record.Region);
            VehicleSet.add(record.Vehicle);
        }

    }

    public void saveDataToLocal(String filePath) {
        Gson gson = new Gson();

        // Convert the salesData list to a JSON string
        String jsonString = gson.toJson(salesData);

        try (FileWriter writer = new FileWriter(filePath)) {
            // Write the JSON string to the specified file
            writer.write(jsonString);
            System.out.println("Sales data saved to " + filePath);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

}
