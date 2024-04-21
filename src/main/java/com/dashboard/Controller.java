package com.dashboard;

import java.io.*;
import java.util.*;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.Region;
import javafx.scene.chart.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.util.Callback;

import javafx.scene.Node;

public class Controller {
    @FXML
    private HBox hBoxYearChk;
    @FXML
    private HBox hBoxQtrChk;
    @FXML
    private HBox hBoxRegionChk;
    @FXML
    private HBox hBoxVehicleChk;
    @FXML
    private HBox hBoxYearBtn;
    @FXML
    private HBox hBoxQtrBtn;
    @FXML
    private HBox hBoxRegionBtn;
    @FXML
    private HBox hBoxVehicleBtn;
    @FXML
    private HBox hBoxAgg;

    @FXML
    private PieChart pieChart;

    @FXML
    private LineChart<String, Number> salesTrend;

    @FXML
    private LineChart<String, Number> growthTrend;

    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    private TableView<SalesRecord> tableSales;

    private SalesReader reader = new SalesReader();
    private ArrayList<SalesRecord> filteredSalesData = new ArrayList<>();
    private ArrayList<SalesRecord> filteredSalesDataAgg = new ArrayList<>();

    private Set<String> filters = new LinkedHashSet<>();

    private boolean ifQuarter = true;

    private TableColumn<SalesRecord, Integer> yearColumn = new TableColumn<>("Year");
    private TableColumn<SalesRecord, Integer> quarterColumn = new TableColumn<>("Quarter");
    private TableColumn<SalesRecord, String> regionColumn = new TableColumn<>("Region");
    private TableColumn<SalesRecord, String> vehicleColumn = new TableColumn<>("Vehicle");
    private TableColumn<SalesRecord, Integer> quantityColumn = new TableColumn<>("Quantity");

    private boolean[] aggregateKey = { true, true, true, true };

    public void initialize() {
        try {
            reader.readData("https://raw.githubusercontent.com/alexchai97/Vehicle-Sales-Analysis-Dashboard-JavaFX/main/files/data.json");

            initPresetsandFilters();
            configTable();
            load();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void load() {
        filterSalesData();
        loadTable();
        loadPieChart();
        loadBarChart();
        loadTimeline();
        loadGrowthTrend();
    }

    public void initPresetsandFilters() {
        filterPresetFromSet(reader.YearSet, hBoxYearChk, hBoxYearBtn, "year");
        filterPresetFromSet(reader.QuarterSet, hBoxQtrChk, hBoxQtrBtn, "qtr");
        filterPresetFromSet(reader.RegionSet, hBoxRegionChk, hBoxRegionBtn, "region");
        filterPresetFromSet(reader.VehicleSet, hBoxVehicleChk, hBoxVehicleBtn, "vehicle");

        createAggCheckBox("Year");
        createAggCheckBox("Quarter");
        createAggCheckBox("Region");
        createAggCheckBox("Vehicle");
    }

    public void filterPresetFromSet(Set<String> set, HBox filterBox, HBox presetBox, String type) {
        // Iterate through the set of strings
        for (String text : set) {
            filters.add(text);

            CheckBox filterCheckBox = new CheckBox(text);
            filterCheckBox.setSelected(true);
            ;
            filterBox.setMargin(filterCheckBox, new Insets(4, 4, 4, 4));
            filterBox.getChildren().add(filterCheckBox);
            filterCheckBox.setOnAction(event -> handleFilterEvent(filterCheckBox, text));

            Button presetButton = new Button(text);
            presetBox.setMargin(presetButton, new Insets(4, 4, 4, 4));
            presetBox.getChildren().add(presetButton);
            presetButton.setOnAction(event -> handlePresetEvent(presetButton, filterCheckBox, filterBox, text, type));
        }

        Button presetButton = new Button("All");
        presetBox.setMargin(presetButton, new Insets(4, 4, 4, 4));
        presetBox.getChildren().add(presetButton);
        presetButton.setStyle("-fx-background-color: #7BC342;");

        presetButton.setOnAction(event -> checkAllCheckboxes(filterBox));
    }

    private void handleFilterEvent(CheckBox filterCheckBox, String text) {
        load();
        return;
    }

    private void handlePresetEvent(Button presetButton, CheckBox filterCheckBox, HBox filterBox, String text,
            String buttonType) {
        switch (buttonType) {
            case "year":
                selectMatchingCheckboxAndUncheckOthers(filterBox, filterCheckBox, text);
                checkAllCheckboxes(hBoxQtrChk);
                break;
            case "qtr":
                selectMatchingCheckboxAndUncheckOthers(filterBox, filterCheckBox, text);
                checkAllCheckboxes(hBoxYearChk);
                break;
            case "region":
                selectMatchingCheckboxAndUncheckOthers(filterBox, filterCheckBox, text);
                checkAllCheckboxes(hBoxVehicleChk);
                break;
            case "vehicle":
                selectMatchingCheckboxAndUncheckOthers(filterBox, filterCheckBox, text);
                checkAllCheckboxes(hBoxRegionChk);
                break;
            default:
                System.err.println("Invalid button type in handlePresetEvent!");
                break;
        }

        load();
        return;
    }

    private void selectMatchingCheckboxAndUncheckOthers(HBox filterBox, CheckBox filterCheckBox, String text) {
        for (Node node : filterBox.getChildren()) {
            if (node instanceof CheckBox) {
                CheckBox cb = (CheckBox) node;
                if (cb.getText().equals(text)) {
                    cb.setSelected(true);
                } else {
                    cb.setSelected(false);
                }
            }
        }
    }

    private void checkAllCheckboxes(HBox box) {
        for (Node node : box.getChildren()) {
            if (node instanceof CheckBox) {
                CheckBox cb = (CheckBox) node;
                cb.setSelected(true);
            }
        }

        load();
        return;
    }

    private void filterSalesData() {
        updateFilters();

        filteredSalesData.clear();

        for (SalesRecord record : reader.salesData) {
            if (filters.contains("" + record.Year) && filters.contains("Q" + record.QTR)
                    && filters.contains(record.Region) && filters.contains(record.Vehicle)) {
                filteredSalesData.add(record);
            }
        }

        return;
    }

    private void updateFilters() {
        filters.clear();
        checkAndUpdateFilter(hBoxYearChk);
        checkAndUpdateFilter(hBoxQtrChk);
        checkAndUpdateFilter(hBoxRegionChk);
        checkAndUpdateFilter(hBoxVehicleChk);
    }

    private void checkAndUpdateFilter(HBox filterBox) {
        for (Node node : filterBox.getChildren()) {
            if (node instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) node;
                if (checkBox.isSelected()) {
                    filters.add(checkBox.getText());
                }
            }
        }
    }

    public void setQuarterly() {
        ifQuarter = true;

        aggregateKey[0] = true;
        aggregateKey[1] = true;
        aggregateKey[2] = true;
        aggregateKey[3] = true;

        setAggKey(aggregateKey);
        load();
    }

    public void setYearly() {
        ifQuarter = false;

        aggregateKey[0] = true;
        aggregateKey[1] = false;
        aggregateKey[2] = true;
        aggregateKey[3] = true;

        setAggKey(aggregateKey);
        load();
    }

    private void loadTable() {
        updateAgg();
        tableSales.getItems().clear();
        quarterColumn.setVisible(ifQuarter);

        tableSales.getItems().addAll(filteredSalesDataAgg);

        yearColumn.setVisible(aggregateKey[0]);
        quarterColumn.setVisible(aggregateKey[1]);
        regionColumn.setVisible(aggregateKey[2]);
        vehicleColumn.setVisible(aggregateKey[3]);

        tableSales.refresh();
    }

    private void configTable() {
        yearColumn.setCellValueFactory(cellData -> cellData.getValue().getYearProperty().asObject());
        quarterColumn.setCellValueFactory(cellData -> cellData.getValue().getQTRProperty().asObject());
        regionColumn.setCellValueFactory(cellData -> cellData.getValue().getRegionProperty());
        vehicleColumn.setCellValueFactory(cellData -> cellData.getValue().getVehicleProperty());
        quantityColumn.setCellValueFactory(cellData -> cellData.getValue().getQuantityProperty().asObject());

        tableSales.getColumns().addAll(yearColumn, quarterColumn, regionColumn, vehicleColumn, quantityColumn);
    }

    private CheckBox createAggCheckBox(String text) {
        CheckBox checkBox = new CheckBox(text);
        checkBox.setSelected(true);
        checkBox.setPadding(new Insets(4, 4, 4, 4));
        checkBox.setOnAction(event -> load());
        hBoxAgg.getChildren().add(checkBox);

        return checkBox;
    }

    public void updateAgg() {

        filteredSalesDataAgg.clear();

        if (aggregateKey == null || aggregateKey.length != hBoxAgg.getChildren().size()) {
            aggregateKey = new boolean[hBoxAgg.getChildren().size()];
        }

        for (int i = 0; i < hBoxAgg.getChildren().size(); i++) {
            Node node = hBoxAgg.getChildren().get(i);
            if (node instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) node;
                aggregateKey[i] = checkBox.isSelected();
            }
        }

        HashMap<String, Integer> yearQty = SalesAnalysis.aggregateData(filteredSalesData, "|", aggregateKey);

        TreeMap<String, Integer> sortedYearQty = new TreeMap<>(yearQty);

        for (Map.Entry<String, Integer> entry : sortedYearQty.entrySet()) {
            // Split key using the pipe delimiter (escape the pipe with double backslash)
            String[] keys = entry.getKey().split("\\|");
            int quantity = entry.getValue();

            String[] recordData = { "0", "Q0", "", "" };

            // Populate recordData based on aggregateKey
            int keyIndex = 0;
            for (int i = 0; i < aggregateKey.length; i++) {
                if (aggregateKey[i]) {
                    if (keyIndex < keys.length) {
                        recordData[i] = keys[keyIndex];
                        keyIndex++;
                    }
                }
            }

            // Parse values
            int year = Integer.parseInt(recordData[0]);
            int qtr = Integer.parseInt(recordData[1].substring(1));
            String region = recordData[2] != null ? recordData[2] : "";
            String vehicle = recordData[3] != null ? recordData[3] : "";

            // Add new record
            filteredSalesDataAgg.add(new SalesRecord(year, qtr, region, vehicle, quantity));
        }
    }

    public void setAggKey(boolean[] aggKey) {
        if (aggKey == null || aggKey.length != hBoxAgg.getChildren().size()) {
            throw new IllegalArgumentException(
                    "The provided aggKey array must have the same length as the number of checkboxes in hBoxAgg.");
        }

        this.aggregateKey = aggKey;

        for (int i = 0; i < hBoxAgg.getChildren().size(); i++) {
            Node node = hBoxAgg.getChildren().get(i);
            if (node instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) node;
                checkBox.setSelected(aggKey[i]);
            }
        }
    }

    public void loadPieChart() {

        boolean[] aggregateBy = new boolean[] { false, false, aggregateKey[2], aggregateKey[3] };

        // Build the chart title based on the aggregation keys
        List<String> titleAppend = new ArrayList<>();
        if (aggregateBy[2]) {
            titleAppend.add("Region");
        }
        if (aggregateBy[3]) {
            titleAppend.add("Vehicle");
        }
        String chartTitle = "Sales Percentage by " + String.join(", ", titleAppend)
                + (!aggregateBy[2] && !aggregateBy[3] ? "Total" : "");
        pieChart.setTitle(chartTitle);

        // Get the aggregated data
        HashMap<String, Integer> aggregatedData = SalesAnalysis.aggregateData(filteredSalesData, ", ", aggregateBy);

        Map<String, Float> summary = SalesAnalysis.calculateSummary(new ArrayList<>(aggregatedData.values()));
        float totalSales = summary.get("Sum");

        // Create PieChart data
        List<PieChart.Data> pieData = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : aggregatedData.entrySet()) {
            String region = entry.getKey();
            int regionSales = entry.getValue();

            double percentage = (double) regionSales / totalSales * 100;
            // Create PieChart.Data with region and percentage
            pieData.add(new PieChart.Data(region + " (" + String.format("%.2f%%", percentage) + ")", regionSales));
        }

        // Clear existing pie chart data
        pieChart.getData().clear();
        // Add all pie chart data
        pieChart.getData().addAll(pieData);

    }

    public void loadBarChart() {
        // Clear existing bar chart data
        barChart.getData().clear();

        boolean[] aggregateBy = { aggregateKey[0], aggregateKey[1], aggregateKey[2], aggregateKey[3] };
        aggregateBy[0] = false; // Year
        aggregateBy[1] = false; // Quarter

        // Check the aggregation keys and adjust them if necessary
        int trueCount = 0;
        for (boolean key : aggregateBy) {
            if (key)
                trueCount++;
        }

        // Set chart title
        List<String> titleAppend = new ArrayList<>();
        if (aggregateBy[0])
            titleAppend.add("Year");
        if (aggregateBy[1])
            titleAppend.add("Quarter");
        if (aggregateBy[2])
            titleAppend.add("Region");
        if (aggregateBy[3])
            titleAppend.add("Vehicle");

        barChart.setTitle("Sales Volume by " + String.join(", ", titleAppend) + (trueCount == 0 ? " Total Sales" : "")
                + (trueCount > 2 ? " (Max 2 Selections)" : ""));

        // Aggregate data
        HashMap<String, Integer> aggregatedData = SalesAnalysis.aggregateData(filteredSalesData, ", ", aggregateBy);

        // Handle the case when both Region and Vehicle are selected
        if (aggregateKey[2] && aggregateKey[3]) {
            HashMap<String, XYChart.Series<String, Number>> seriesMap = new HashMap<>();
            for (Map.Entry<String, Integer> entry : aggregatedData.entrySet()) {
                String[] keyParts = entry.getKey().split(", ");
                String vehicle = keyParts[0];
                String region = keyParts[1];
                int sales = entry.getValue();

                // Get or create the series for each vehicle
                XYChart.Series<String, Number> series = seriesMap.computeIfAbsent(vehicle, v -> new XYChart.Series<>());
                series.setName(vehicle);

                // Add sales data point (by region) to the series
                series.getData().add(new XYChart.Data<>(region, sales));
            }

            // Add all series to the bar chart
            seriesMap.values().forEach(barChart.getData()::add);
            return;
        }

        // Default scenario: Add all data points to a single series
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        // Set the name of the series for the legend
        series.setName("Sales Data");

        for (Map.Entry<String, Integer> entry : aggregatedData.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        barChart.getData().add(series);

    }

    public void loadTimeline() {
        // Clear existing data from the timeline chart
        salesTrend.getData().clear();

        // Determine the aggregation keys for the timeline
        boolean aggregateYear = aggregateKey[0]; // Year
        boolean aggregateQuarter = aggregateKey[1]; // Quarter
        boolean aggregateRegion = aggregateKey[2]; // Region
        boolean aggregateVehicle = aggregateKey[3]; // Vehicle

        // Build the chart title based on the chosen aggregation keys
        List<String> titleParts = new ArrayList<>();
        if (aggregateYear) {
            titleParts.add("Year");
        }
        if (aggregateQuarter) {
            titleParts.add("Quarter");
        }
        salesTrend.setTitle("Sales Trend by " + String.join(", ", titleParts));

        // Use a TreeMap for sorting the series by legend key
        Map<String, XYChart.Series<String, Number>> seriesMap = new TreeMap<>();

        // Iterate through the aggregated data and group data points by legend key
        for (SalesRecord record : filteredSalesDataAgg) {

            ArrayList<String> xAxisKeyParts = new ArrayList<>(), legendKeyParts = new ArrayList<>();
            if (aggregateYear) {
                xAxisKeyParts.add("" + record.Year);
            }

            if (aggregateQuarter) {
                xAxisKeyParts.add("Q" + record.QTR);
            }

            if (aggregateRegion) {
                legendKeyParts.add(record.Region);
            }

            if (aggregateVehicle) {
                legendKeyParts.add(record.Vehicle);
            }

            String legendKey = String.join(" ", legendKeyParts);
            String xAxisKey = String.join(" ", xAxisKeyParts);

            if (legendKeyParts.isEmpty()) {
                legendKey = "Total Sales";
            }

            // Retrieve or create a series for the legend key
            XYChart.Series<String, Number> series = seriesMap.getOrDefault(legendKey, new XYChart.Series<>());
            series.setName(legendKey);

            // Add the data point to the series
            XYChart.Data<String, Number> dataPoint = new XYChart.Data<>(xAxisKey, record.Quantity);
            series.getData().add(dataPoint);

            // Update the series map
            seriesMap.put(legendKey, series);
        }

        // Add all series to the timeline chart
        for (XYChart.Series<String, Number> series : seriesMap.values()) {
            salesTrend.getData().add(series);
        }
    }
    
    
    public void loadGrowthTrend() {
        // Clear existing data from the growth trend chart
        growthTrend.getData().clear();

        
    
        // Determine the aggregation keys for the timeline
        boolean aggregateYear = aggregateKey[0]; // Year
        boolean aggregateQuarter = aggregateKey[1]; // Quarter
        boolean aggregateRegion = aggregateKey[2]; // Region
        boolean aggregateVehicle = aggregateKey[3]; // Vehicle

        List<String> titleParts = new ArrayList<>();
        if (aggregateYear) {
            titleParts.add("Year");
        }
        if (aggregateQuarter) {
            titleParts.add("Quarter");
        }

        growthTrend.setTitle("Growth Trend by " + String.join(", ", titleParts));

        // Use a TreeMap for sorting the series by legend key
        Map<String, XYChart.Series<String, Number>> growthSeriesMap = new TreeMap<>();
    
        // Initialize a map to track previous quantities for each legend key
        Map<String, Integer> previousQuantities = new HashMap<>();
    
        // Iterate through the filtered sales data
        for (SalesRecord record : filteredSalesDataAgg) {
            // Determine the x-axis key
            ArrayList<String> xAxisKeyParts = new ArrayList<>();
            if (aggregateYear) {
                xAxisKeyParts.add("" + record.Year);
            }
            if (aggregateQuarter) {
                xAxisKeyParts.add("Q" + record.QTR);
            }
            String xAxisKey = String.join(" ", xAxisKeyParts);
    
            // Determine the legend key
            ArrayList<String> legendKeyParts = new ArrayList<>();
            if (aggregateRegion) {
                legendKeyParts.add(record.Region);
            }
            if (aggregateVehicle) {
                legendKeyParts.add(record.Vehicle);
            }
            String legendKey = String.join(" ", legendKeyParts);

            if (legendKeyParts.isEmpty()) {
                legendKey = "Total Growth";
            }
    
            // Retrieve or create a series for the growth trend chart - legendKey eg: America Exige / America / Exige
            //there will be legendKey for every combination of legendkey
            XYChart.Series<String, Number> growthSeries = growthSeriesMap.getOrDefault(legendKey, new XYChart.Series<>());

            //for new or existing, the name is set to legendkey, if existing, no changes
            growthSeries.setName(legendKey);
    
            // Calculate the percentage growth for the growth trend chart
            Integer previousQuantity = previousQuantities.get(legendKey);

            double percentageGrowth = 0;
            if (previousQuantity != null) {
                percentageGrowth = ((record.Quantity - previousQuantity) / (double) previousQuantity) * 100;
            }
    
            // Add the growth data point to the growth trend chart
            XYChart.Data<String, Number> growthDataPoint = new XYChart.Data<>(xAxisKey, percentageGrowth);
            growthSeries.getData().add(growthDataPoint);
    
            // Update the series map with the growth series
            growthSeriesMap.put(legendKey, growthSeries);
    
            // Update the previous quantity for growth calculation
            previousQuantities.put(legendKey, record.Quantity);

            // System.out.println(legendKey + " " + previousQuantity + " " + record.Quantity);
        }
    
        // Add all growth series to the growth trend chart
        for (XYChart.Series<String, Number> series : growthSeriesMap.values()) {
            growthTrend.getData().add(series);
        }
    }
    
    // public void loadGrowthTrend() {
    //     HashMap<String, Integer> aggregatedData = SalesAnalysis.aggregateData(filteredSalesData, ", ", aggregateKey);

    //     ArrayList<Map "2016 Q1" , "America Evora", Qty>

    //     Set<String> xAxis = new LinkedHashSet();
    //     Set<String> legends = new LinkedHashSet();
        
    //     ArrayList<String> arrYear = new ArrayList<>(reader.YearSet);
    //     arrQtr
    //     arrRegion
    //     arrQty

    //     nested for loop 


    //     aggregatedData.get()
    // }

}
