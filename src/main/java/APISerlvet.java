import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import org.bson.Document;



import com.mongodb.*;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.*;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.nio.file.Paths;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;






import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class APISerlvet extends HttpServlet {


    String collectionName = "buildings";


    // READ
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DBManager dbManager = new DBManager();
//        Document bson = dbManager.database.getCollection("buildings").find().first();
        Document bson = dbManager.database.getCollection(collectionName).find().first();

        resp.setContentType("application/json");
        resp.getOutputStream().print(bson.toJson());

    }

    // CREATE
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    // UPDATE
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // Get DB Connection
        DBManager dbManager = new DBManager();
        MongoCollection collection = dbManager.database.getCollection(collectionName);

        // Regex Pattern to match URL Parameters
        String urlParamPattern = "(\\w+[-]?\\w+|\\d+)\\/*";
        Pattern r = Pattern.compile(urlParamPattern);

        String requestUrl = req.getRequestURI();
        String action;
        String data;


            Matcher m = r.matcher(requestUrl);
            final List<String> matches = new ArrayList<>();

            while (m.find()) {
                matches.add(m.group(1));
            }

        String query = String.format("buildings.%s.%s.timetable.%s.booked", matches.get(1), matches.get(2), matches.get(3));


        // /book/<building>/<room>/firstavailable
        // /book/<building>/<room>/<integer>


        // books first available spot
        if(matches.get(0).equals("book")) {
            // book room
            collection.updateOne(eq(query, false), set(query, true));

        } else if (matches.get(0).equals("unbook")) {
            // unbook room
            collection.updateOne(eq(query, true), set(query,false));

        }


        // /book/<building>/<room>/<integer>
        // books given spot (<integer>)


    }

    // DELETE
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doDelete(req, resp);
    }
}
