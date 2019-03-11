package com.mongodb.hadoop.bookstore;


import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.hadoop.BSONFileInputFormat;
import com.mongodb.hadoop.mapred.MongoOutputFormat;
import com.mongodb.hadoop.mapred.output.MongoOutputCommitter;
import com.mongodb.hadoop.testutils.BaseHadoopTest;
import com.mongodb.hadoop.testutils.MapReduceJob;
import com.mongodb.hadoop.util.MongoClientURIBuilder;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class BookstoreTest extends BaseHadoopTest {
    public static final URI INVENTORY_BSON;

    private static final File JAR_PATH;

    static {
        try {
            File home = new File(BaseHadoopTest.PROJECT_HOME, "core");
            URL resource = BookstoreTest.class.getResource("/bookstore-dump/inventory.bson");
            INVENTORY_BSON = resource.toURI();
            JAR_PATH = BaseHadoopTest.findProjectJar(home, true);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void tagsIndex() throws IllegalAccessException, URISyntaxException, UnknownHostException {
        MongoClientURI uri = BaseHadoopTest.authCheck(new MongoClientURIBuilder().collection("mongo_hadoop", "bookstore_tags")).build();
        MongoClient mongoClient = new MongoClient(uri);
        DBCollection collection = mongoClient.getDB(uri.getDatabase()).getCollection(uri.getCollection());
        MapReduceJob job = new MapReduceJob(BookstoreConfig.class.getName()).jar(BookstoreTest.JAR_PATH).inputUris(BookstoreTest.INVENTORY_BSON).outputUri(uri).param("mapred.input.dir", BookstoreTest.INVENTORY_BSON.toString());
        if (!(BaseHadoopTest.HADOOP_VERSION.startsWith("1."))) {
            job.inputFormat(BSONFileInputFormat.class);
        } else {
            job.mapredInputFormat(BSONFileInputFormat.class);
            job.mapredOutputFormat(MongoOutputFormat.class);
            job.outputCommitter(MongoOutputCommitter.class);
        }
        job.execute(false);
        DBObject object = collection.findOne(new BasicDBObject("_id", "history"));
        Assert.assertNotNull(object);
        List books = ((List) (object.get("books")));
        Assert.assertEquals("Should find only 8 books", books.size(), 8);
    }
}

