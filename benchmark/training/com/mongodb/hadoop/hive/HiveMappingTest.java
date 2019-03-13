package com.mongodb.hadoop.hive;


import MongoConfigUtil.INPUT_QUERY;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.MongoClientURI;
import com.mongodb.hadoop.util.MongoClientURIBuilder;
import com.mongodb.util.JSON;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;


public class HiveMappingTest extends HiveTest {
    private static final Log LOG = LogFactory.getLog(HiveMappingTest.class);

    @Test
    public void nestedColumns() throws SQLException {
        DBCollection collection = getCollection("hive_addresses");
        collection.drop();
        dropTable("hive_addresses");
        collection.insert(user(1, "Jim", "Beam", "Clermont", "KY"));
        collection.insert(user(2, "Don", "Draper", "New York", "NY"));
        collection.insert(user(3, "John", "Elway", "Denver", "CO"));
        MongoClientURI uri = authCheck(new MongoClientURIBuilder().collection("mongo_hadoop", collection.getName())).build();
        Map<String, String> map = new HashMap<String, String>() {
            {
                put("id", "_id");
                put("firstName", "firstName");
                put("lastName", "lastName");
                put("place.municipality", "address.city");
                put("place.region", "address.state");
            }
        };
        execute(String.format(("CREATE TABLE hive_addresses " + (((("(id INT, firstName STRING, lastName STRING, " + "place STRUCT<municipality:STRING, region:STRING>)\n") + "STORED BY \'%s\'\n") + "WITH SERDEPROPERTIES(\'mongo.columns.mapping\'=\'%s\')\n") + "TBLPROPERTIES ('mongo.uri'='%s')")), MongoStorageHandler.class.getName(), JSON.serialize(map), uri));
        // Alias inner fields to avoid retrieving entire struct as a String.
        Results execute = query("SELECT place.municipality AS city, place.region AS state, firstname from hive_addresses");
        Assert.assertEquals("KY", execute.getRow(0).get("state"));
        Assert.assertEquals("Don", execute.getRow(1).get("firstname"));
        Assert.assertEquals("Denver", execute.getRow(2).get("city"));
    }

    @Test
    public void nestedObjects() throws SQLException {
        DBCollection collection = getCollection("hive_addresses");
        collection.drop();
        dropTable("hive_addresses");
        collection.insert(user(1, "Jim", "Beam", "Clermont", "KY"));
        collection.insert(user(2, "Don", "Draper", "New York", "NY"));
        collection.insert(user(3, "John", "Elway", "Denver", "CO"));
        MongoClientURI uri = authCheck(new MongoClientURIBuilder().collection("mongo_hadoop", collection.getName())).build();
        HiveMappingTest.ColumnMapping map = new HiveMappingTest.ColumnMapping().map("id", "_id", "INT").map("firstName", "firstName", "STRING").map("lastName", "lastName", "STRING").map("city", "address.city", "STRING").map("state", "address.state", "STRING");
        // , lastName STRING
        execute(String.format(("CREATE TABLE hive_addresses (id INT, firstName STRING, lastName STRING, city STRING, state STRING)\n" + (("STORED BY \'%s\'\n" + "WITH SERDEPROPERTIES(\'mongo.columns.mapping\'=\'%s\')\n") + "TBLPROPERTIES ('mongo.uri'='%s')")), MongoStorageHandler.class.getName(), map.toSerDePropertiesString(), uri));
        Results execute = query("SELECT * from hive_addresses");
        Assert.assertEquals("KY", execute.getRow(0).get("state"));
        Assert.assertEquals("Don", execute.getRow(1).get("firstname"));
        Assert.assertEquals("Denver", execute.getRow(2).get("city"));
    }

    @Test
    public void queryBasedHiveTable() throws SQLException {
        String tableName = "filtered";
        DBCollection collection = getCollection(tableName);
        collection.drop();
        dropTable(tableName);
        int size = 1000;
        for (int i = 0; i < size; i++) {
            collection.insert(new BasicDBObject("_id", i).append("intField", (i % 10)).append("booleanField", ((i % 2) == 0)).append("stringField", ("" + ((i % 2) == 0))));
        }
        MongoClientURI uri = authCheck(new MongoClientURIBuilder().collection("mongo_hadoop", collection.getName())).build();
        HiveMappingTest.ColumnMapping map = new HiveMappingTest.ColumnMapping().map("id", "_id", "INT").map("ints", "intField", "INT").map("booleans", "booleanField", "BOOLEAN").map("strings", "stringField", "STRING");
        HiveMappingTest.HiveTableBuilder builder = new HiveMappingTest.HiveTableBuilder().mapping(map).name(tableName).uri(uri).tableProperty(INPUT_QUERY, "{_id : {\"$gte\" : 900 }}");
        execute(builder.toString());
        Assert.assertEquals(String.format("Should find %d items", size), collection.count(), size);
        Results execute = query(String.format("SELECT * from %s where id=1", tableName));
        Assert.assertTrue(((execute.size()) == 0));
        int expected = size - 900;
        Assert.assertEquals(String.format("Should find only %d items", expected), query(("SELECT count(*) as count from " + tableName)).iterator().next().get(0), ("" + expected));
    }

    private static class ColumnMapping {
        private final List<HiveMappingTest.ColumnMapping.Column> columns = new ArrayList<HiveMappingTest.ColumnMapping.Column>();

        public HiveMappingTest.ColumnMapping map(final String hiveColumn, final String mongoField, final String hiveType) {
            columns.add(new HiveMappingTest.ColumnMapping.Column(hiveColumn, mongoField, hiveType));
            return this;
        }

        public String toColumnDesc() {
            StringBuilder builder = new StringBuilder();
            for (HiveMappingTest.ColumnMapping.Column entry : columns) {
                if ((builder.length()) != 0) {
                    builder.append(", ");
                }
                builder.append(String.format("%s %s", entry.hiveColumn, entry.hiveType));
            }
            return builder.toString();
        }

        public String toSerDePropertiesString() {
            StringBuilder builder = new StringBuilder();
            for (HiveMappingTest.ColumnMapping.Column entry : columns) {
                if ((builder.length()) != 0) {
                    builder.append(", ");
                }
                builder.append(String.format("\"%s\" : \"%s\"", entry.hiveColumn, entry.mongoField));
            }
            return ("{ " + builder) + " }";
        }

        private static class Column {
            private final String hiveColumn;

            private final String mongoField;

            private final String hiveType;

            public Column(final String hiveColumn, final String mongoField, final String hiveType) {
                this.hiveColumn = hiveColumn;
                this.mongoField = mongoField;
                this.hiveType = hiveType;
            }
        }
    }

    private static class HiveTableBuilder {
        private String tableName;

        private HiveMappingTest.ColumnMapping mapping;

        private String storageHandler = MongoStorageHandler.class.getName();

        private MongoClientURI uri;

        private Map<String, String> tableProperties = new TreeMap<String, String>();

        public HiveMappingTest.HiveTableBuilder name(final String name) {
            tableName = name;
            return this;
        }

        public HiveMappingTest.HiveTableBuilder mapping(final HiveMappingTest.ColumnMapping mapping) {
            this.mapping = mapping;
            return this;
        }

        public HiveMappingTest.HiveTableBuilder storageHandler(final String storageHandler) {
            this.storageHandler = storageHandler;
            return this;
        }

        public HiveMappingTest.HiveTableBuilder uri(final MongoClientURI uri) {
            this.uri = uri;
            return this;
        }

        public HiveMappingTest.HiveTableBuilder tableProperty(final String key, final String value) {
            tableProperties.put(key, value);
            return this;
        }

        public String toString() {
            storageHandler = MongoStorageHandler.class.getName();
            StringBuilder props = new StringBuilder();
            tableProperties.put(MongoStorageHandler.MONGO_URI, uri.toString());
            for (Map.Entry<String, String> entry : tableProperties.entrySet()) {
                if ((props.length()) != 0) {
                    props.append(", \n\t\t");
                }
                props.append(String.format("'%s' = '%s'", entry.getKey(), entry.getValue()));
            }
            return String.format(("CREATE TABLE %s (%s)\n" + (("STORED BY \'%s\'\n" + "WITH SERDEPROPERTIES(\'%s\' = \'%s\')\n") + "TBLPROPERTIES (%s)")), tableName, mapping.toColumnDesc(), storageHandler, BSONSerDe.MONGO_COLS, mapping.toSerDePropertiesString(), props);
        }
    }
}

