import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.inc;
import static com.mongodb.client.model.Updates.set;


public class APISerlvet extends HttpServlet {


    String collectionName = "buildings";
    String collectionId = "5fba4b55ecc508467e34225d";

    String urlParamPattern = "(\\w+[-]?\\w+|\\d+)\\/*";
    Pattern compiledPattern = Pattern.compile(urlParamPattern);


    // READ
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        DBManager dbManager = new DBManager();
        resp.setContentType("application/json");
//        Document bson = dbManager.database.getCollection("buildings").find().first();
        Document bson = dbManager.database.getCollection(collectionName).find().first();


        // Regex Pattern to match URL Parameters

        String requestUrl = req.getRequestURI();

        Matcher m = compiledPattern.matcher(requestUrl);
        final List<String> matches = new ArrayList<>();

        while (m.find()) {
            matches.add(m.group(1));
        }

        // /capacity/buidlings
        // /capacity/buildings/<building>
        // /capacity/buildings/<building>/<room>
        PrintWriter out = resp.getWriter();

        if(matches.size()>0){

            if(matches.size() >= 2){
                bson = (Document) bson.get(matches.get(1));
            }
            if (matches.size() >= 3){
                bson = (Document) bson.get(matches.get(2));
            }
            try {
                int respValue = (Integer) bson.get(matches.get(0));
                out.println("{\n\""+matches.get(0)+"\" : "+respValue+"\n}");
            } catch (ClassCastException e){
                double respValue = (double) bson.get(matches.get(0));
                out.println("{\n\""+matches.get(0)+"\" : "+respValue+"\n}\r\t");

            }

        } else{
            out.println(bson.toJson());
        }

        out.close();

    }

    // CREATE
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        super.doPost(req, resp);
    }

    // UPDATE
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Get DB Connection
        DBManager dbManager = new DBManager();
        MongoCollection collection = dbManager.database.getCollection(collectionName);

        boolean update = false;

        // Regex Pattern to match URL Parameters
//        String urlParamPattern = "(\\w+[-]?\\w+|\\d+)\\/*";
//        Pattern r = Pattern.compile(urlParamPattern);

        String requestUrl = req.getRequestURI();

        Matcher m = compiledPattern.matcher(requestUrl);
        final List<String> matches = new ArrayList<>();

        while (m.find()) {
            matches.add(m.group(1));
        }

        String query = String.format("buildings.%s.%s.timetable.%s.booked", matches.get(2), matches.get(3), matches.get(4));
        PrintWriter out = resp.getWriter();

        // books first available spot
        if (matches.get(0).equals("book")) {
            // book room
            UpdateResult us = collection.updateOne(eq(query, false), set(query, true));
            if (us.getModifiedCount() > 0) {
                String queryTotalVisitors = String.format("buildings.%s.total_visitors", matches.get(1));

                collection.updateOne(eq("_id", new ObjectId("5fba4b55ecc508467e34225d")),
                        inc(queryTotalVisitors, 1));
                out.println("Succesfully Booked Room!");
                update = true;
            } else {
                out.println("Room already booked");
            }


        } else if (matches.get(0).equals("unbook")) {
            // unbook room
            UpdateResult us = collection.updateOne(eq(query, true), set(query, false));
            if (us.getModifiedCount() > 0) {
                String queryInc = String.format("buildings.%s.total_visitors", matches.get(1));
                collection.updateOne(eq("_id", new ObjectId(collectionId)), inc(queryInc, -1));
                out.println("Succesfully unbooked Room!");
                update = true;
            } else {
                out.println("Room is not booked");
            }

        }


        if (update) {

            Document bson = (Document) collection.find().first();
            Document buildings = (Document) bson.get("buildings");
            Document building = (Document) buildings.get(matches.get(1));
            int capacity = building.getInteger("capacity");
            int total_visitors = building.getInteger("total_visitors");
            String queryBooking = String.format("buildings.%s.booking", matches.get(1));
            double newBooking = (double) total_visitors / (double) capacity;
            collection.updateOne(eq("_id", new ObjectId(collectionId)), set(queryBooking, newBooking));
            out.println("Booking at " + matches.get(1) + " is now at " + newBooking * 100 + "%");
        }

        // /book/<building>/<room>/<integer>
        // books given spot (<integer>)


        out.close();
    }

    // DELETE
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        super.doDelete(req, resp);
    }
}
