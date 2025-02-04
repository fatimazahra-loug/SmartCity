package tuto1;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class GroundsData {
    public static void main(String[] args) {
        String connectionString = "mongodb://localhost:27017";
        try (var mongoClient = MongoClients.create(connectionString)) {
            MongoDatabase database = mongoClient.getDatabase("SmartCity");
            MongoCollection<Document> collection = database.getCollection("StadiumsCollection");
            String csvFile = "C:\\Users\\admin\\Desktop\\SmartCity\\SmartCity\\src\\main\\resources\\com\\example\\test\\Data\\world_cup_2030_stadiums.csv";
            try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                String line;
                String[] headers = br.readLine().split(",");
                List<Document> documents = new ArrayList<>();

                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",");
                    Document doc = new Document();
                    for (int i = 0; i < headers.length; i++) {
                        doc.append(headers[i], values[i]);
                    }
                    documents.add(doc);
                }

                collection.insertMany(documents);

                System.out.println("Data successfully stored in MongoDB!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}