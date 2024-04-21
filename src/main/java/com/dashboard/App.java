package com.dashboard;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.*;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("dashboard"), 1440, 991);
        stage.setTitle("Vehicle Sales Analytics Dashboard");
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    // public static void main(String[] args) {
    //     SalesReader reader = new SalesReader();

    //     try {
    //         reader.readData("http://localhost/cars.json");
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }

    //     // Aggregate data using the specified binary key (1100) - Year and Region
    //     HashMap<String, Integer> yearQty = SalesAnalysis.aggregateData(reader.salesData, " ", Integer.parseInt("1110", 2));

    //     // Create a TreeMap from the HashMap to sort the keys alphabetically
    //     TreeMap<String, Integer> sortedYearQty = new TreeMap<>(yearQty);

    //     for (Map.Entry<String, Integer> entry : sortedYearQty.entrySet()) {
    //         System.out.println(entry.getKey() + ": " + entry.getValue());
    //     }

    //     System.out.println();

    //     // Calculate summary statistics from the hash map's values
    //     Collection<Integer> values = yearQty.values();
    //     Map<String, Float> summary = SalesAnalysis.calculateSummary(new ArrayList<>(values));

    //     System.out.println("Summary Statistics:");
    //     for (Map.Entry<String, Float> entry : summary.entrySet()) {
    //         System.out.println(entry.getKey() + ": " + entry.getValue());
    //     }
        
    //     System.out.println();

    //     List<Float> quantities = new ArrayList<>();
    //     for (SalesRecord record : reader.salesData) {
    //         quantities.add((float) record.Quantity);
    //     }

    //     // Calculate summary statistics from the quantities
    //     summary = SalesAnalysis.calculateSummary(quantities);

    //     // Print the summary statistics
    //     System.out.println("Overall Summary Statistics:");
    //     for (Map.Entry<String, Float> entry : summary.entrySet()) {
    //         System.out.println(entry.getKey() + ": " + entry.getValue());
    //     }
    // }

    public static void main(String[] args) {
        launch();
    }
}
