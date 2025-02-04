package tuto1;

import org.apache.spark.sql.*;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.SaveMode;
import java.util.Arrays;

public class HotelsData {
    public static void main(String[] args) throws InterruptedException {
        SparkSession spark = SparkSession.builder()
                .appName("FilteredHotelData")
                .master("local[*]")
                .config("spark.mongodb.write.connection.uri", "mongodb://localhost:27017")
                .config("spark.mongodb.read.connection.uri", "mongodb://localhost:27017")
                .config("spark.mongodb.output.uri", "mongodb://localhost:2717")
                .getOrCreate();

        System.out.println("Spark Session Initialized");

        Dataset<Row> hotelsDF = spark.read().option("header", "true")
                .csv("C:\\Users\\ThinkPad\\Desktop\\GI2\\javaprojet\\test\\src\\main\\resources\\com\\example\\test\\Data\\hotels.csv");
        System.out.println("Hotels CSV file loaded successfully.");

        Dataset<Row> citiesDF = spark.read().option("header", "true")
                .csv("C:\\Users\\ThinkPad\\Desktop\\GI2\\javaprojet\\test\\src\\main\\resources\\com\\example\\test\\Data\\data_citiesheader.csv");
        System.out.println("Cities CSV file loaded successfully.");

        hotelsDF = hotelsDF.toDF(Arrays.stream(hotelsDF.columns())
                .map(String::trim)
                .toArray(String[]::new));

        citiesDF = citiesDF.toDF(Arrays.stream(citiesDF.columns())
                .map(String::trim)
                .toArray(String[]::new));

        for (String colName : hotelsDF.columns()) {
            hotelsDF = hotelsDF.withColumn(colName, functions.regexp_replace(hotelsDF.col(colName), "<.*?>", ""));
            hotelsDF = hotelsDF.withColumn(colName, functions.trim(hotelsDF.col(colName)));
        }

        String[] countries = {"Morocco", "Portugal", "Spain", "Paraguay", "Argentina", "Uruguay"};
        Dataset<Row> filteredCitiesDF = citiesDF.filter(functions.col("Country").isin((Object[]) countries));

        Dataset<Row> filteredHotelsDF = hotelsDF.join(filteredCitiesDF, hotelsDF.col("cityName").equalTo(filteredCitiesDF.col("City")))
                .filter(functions.col("countyName").isin((Object[]) countries));

        filteredHotelsDF = filteredHotelsDF.na().drop();

        String database = "SmartCity";
        String hotelsCollection = "HotelsCollection";
        String citiesCollection = "CitiesCollection";

        try {
            filteredHotelsDF.write()
                    .format("mongodb")
                    .option("uri", "mongodb://localhost:27017")
                    .option("database", database)
                    .option("collection", hotelsCollection)
                    .mode(SaveMode.Overwrite)
                    .save();
            System.out.println("Filtered Hotels Data saved to MongoDB collection: " + hotelsCollection);

            filteredCitiesDF.write()
                    .format("mongodb")
                    .option("uri", "mongodb://localhost:27017")
                    .option("database", database)
                    .option("collection", citiesCollection)
                    .mode(SaveMode.Overwrite)
                    .save();
            System.out.println("Filtered Cities Data saved to MongoDB collection: " + citiesCollection);
        } catch (Exception e) {
            System.err.println("Error occurred while writing to MongoDB: " + e.getMessage());
        }
        System.out.println("Spark UI will remain active...");
        while (true) {
            Thread.sleep(10000);
        }
    }
}