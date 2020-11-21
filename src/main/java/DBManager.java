import com.mongodb.ConnectionString;
import com.mongodb.DocumentToDBRefTransformer;
import com.mongodb.client.*;
import com.mongodb.MongoClientSettings;
import org.bson.Document;


public class DBManager {

    Config cfg = new Config();
    String dbusername;
    String dbhost;
    String dbname;
    String dbcollection;
    String dbpwd;

    MongoDatabase database;

    DBManager(){
        dbusername = cfg.getProperty("dbusername");
        dbhost = cfg.getProperty("dbhost");
        dbname = cfg.getProperty("dbname");
        dbcollection = cfg.getProperty("dbcollection");
        dbpwd = cfg.getProperty("dbpwd");
        getInstance();
    }

    public void getInstance(){
        ConnectionString connString = new ConnectionString(
                "mongodb+srv://"+dbusername+":"+dbpwd+"@"+dbhost+".pufct.mongodb.net/"+dbcollection+"?retryWrites=true&w=majority"
        );
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connString)
                .retryWrites(true)
                .build();
        MongoClient mongoClient = MongoClients.create(settings);
        database = mongoClient.getDatabase(dbname);
    }

}
