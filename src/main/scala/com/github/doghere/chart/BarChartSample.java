//package com.github.doghere.chart;
//
//import javafx.application.*;
//import javafx.collections.*;
//import javafx.embed.swing.SwingFXUtils;
//import javafx.scene.*;
//import javafx.scene.chart.*;
//import javafx.scene.image.Image;
//import javafx.scene.layout.Region;
//import javafx.scene.layout.StackPane;
//import javafx.stage.Stage;
//
//import javax.imageio.ImageIO;
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.*;
//import java.util.logging.*;
//
//public class BarChartSample extends Application {
//    private static final Logger logger = Logger.getLogger(BarChartSample.class.getName());
//
//    private static final String IMAGE_TYPE = "png";
//    private static final String IMAGE_FILENAME = "image." + IMAGE_TYPE;
//    private static final String WORKING_DIR = System.getProperty("user.dir");
//    private static final String IMAGE_PATH = new File(WORKING_DIR, IMAGE_FILENAME).getPath();
//
//    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
//    private final Random random = new Random();
//
//    private final int CHART_SIZE = 400;
//
//    @Override
//    public void start(Stage stage) throws IOException {
//        Chart chart = createLine();
//        Image  image = snapshot(createChart(),chart);
//        exportPng(SwingFXUtils.fromFXImage(image, null), IMAGE_PATH);
//
//        Platform.exit();
//    }
//
//    private Chart createChart() {
//        // create a chart.
//        final PieChart chart = new PieChart();
//        ObservableList<PieChart.Data> pieChartData =
//                FXCollections.observableArrayList(
//                        new PieChart.Data("Grapefruit", random.nextInt(30)),
//                        new PieChart.Data("Oranges", random.nextInt(30)),
//                        new PieChart.Data("Plums", random.nextInt(30)),
//                        new PieChart.Data("Pears", random.nextInt(30)),
//                        new PieChart.Data("Apples", random.nextInt(30))
//                );
//        chart.setData(pieChartData);
//        chart.setTitle("Imported Fruits - " + dateFormat.format(new Date()));
//
//        // It is important for snapshots that the chart is not animated
//        // otherwise we could get a snapshot of the chart before the
//        // data display has been animated in.
//        chart.setAnimated(false);
//
//        chart.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
//        chart.setPrefSize(CHART_SIZE, CHART_SIZE);
//        chart.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
//        chart.setAnimated(false);
//
//        return chart;
//    }
//
//    private Chart createLine(){
//        //defining the axes
//        final NumberAxis xAxis = new NumberAxis();
//        final NumberAxis yAxis = new NumberAxis();
//        xAxis.setLabel("Number of Month");
//
//        final LineChart<Number,Number> lineChart =
//                new LineChart<Number,Number>(xAxis,yAxis);
//
//        lineChart.setTitle("Stock Monitoring, 2010");
//        //defining a series
//        XYChart.Series series = new XYChart.Series();
//        series.setName("My portfolio");
//        //populating the series with data
//        series.getData().add(new XYChart.Data(1, 23));
//        series.getData().add(new XYChart.Data(2, 14));
//        series.getData().add(new XYChart.Data(3, 15));
//        series.getData().add(new XYChart.Data(4, 24));
//        series.getData().add(new XYChart.Data(5, 34));
//        series.getData().add(new XYChart.Data(6, 36));
//        series.getData().add(new XYChart.Data(7, 22));
//        series.getData().add(new XYChart.Data(8, 45));
//        series.getData().add(new XYChart.Data(9, 43));
//        series.getData().add(new XYChart.Data(10, 17));
//        series.getData().add(new XYChart.Data(11, 29));
//        series.getData().add(new XYChart.Data(12, 25));
////        Scene scene  = new Scene(lineChart,800,600);
//
//        lineChart.getData().add(series);
//        lineChart.setAnimated(false);
//
//        return lineChart;
//    }
//
//
//    private StackPane layerCharts(final javafx.scene.chart.Chart ... charts) {
////        for (int i = 1; i < charts.length; i++) {
////            configureOverlayChart(charts[i]);
////        }
//
//        StackPane stackpane = new StackPane();
//        stackpane.getChildren().addAll(charts);
//
//        return stackpane;
//    }
//
//    private void configureOverlayChart(final XYChart<String, Number> chart) {
//        chart.setAlternativeRowFillVisible(false);
//        chart.setAlternativeColumnFillVisible(false);
//        chart.setHorizontalGridLinesVisible(false);
//        chart.setVerticalGridLinesVisible(false);
//        chart.getXAxis().setVisible(false);
//        chart.getYAxis().setVisible(false);
//
//        chart.getStylesheets().addAll(getClass().getResource("overlay-chart.css").toExternalForm());
//    }
//
//    private Image snapshot(final Chart sourceNode,final Chart b) {
//        // Note: if the source node is not in a scene, css styles will not
//        // be applied during a snapshot which may result in incorrect rendering.
//
//        //防止截图后无数据
//        sourceNode.setAnimated(false);
//        sourceNode.applyCss();
//        sourceNode.layout();
//        b.setAnimated(false);
//
//
//        final Scene snapshotScene = new Scene(layerCharts(sourceNode,b));
//
//        return sourceNode.snapshot(
//                new SnapshotParameters(),
//                null
//        );
//    }
//
//    private void exportPng(BufferedImage image, String filename) {
//        try {
//            ImageIO.write(image, IMAGE_TYPE, new File(filename));
//
//            logger.log(Level.INFO, "Wrote image to: " + filename);
//        } catch (IOException ex) {
//            logger.log(Level.SEVERE, null, ex);
//        }
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}