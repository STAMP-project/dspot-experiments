package com.mongodb.hadoop.hive;


import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import java.util.List;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


public class TestHDFSToMongoDB extends HiveTest {
    @Test
    public void testSameDataHDFSAndMongoHiveTables() {
        Results hiveData = getAllDataFromTable(HiveTest.HDFS_BACKED_TABLE);
        Results mongoData = getAllDataFromTable(HiveTest.MONGO_BACKED_TABLE);
        Assert.assertNotEquals(hiveData.size(), 0);
        Assert.assertNotEquals(mongoData.size(), 0);
        Assert.assertEquals(hiveData, mongoData);
    }

    @Test
    public void testDeleteReflectData() {
        Results results = getAllDataFromTable(HiveTest.MONGO_BACKED_TABLE);
        int size = results.size();
        Assert.assertTrue((size > 0));
        List<String> t = results.get(new Random().nextInt(size));
        DBObject toDelete = new BasicDBObject();
        int i = 0;
        for (Results.Field field : results.getFields()) {
            // add more types as necessary
            if (field.getType().equals("int")) {
                toDelete.put(field.getName(), Integer.valueOf(t.get(i)));
            } else
                if (field.getType().equals("string")) {
                    toDelete.put(field.getName(), t.get(i));
                } else {
                    toDelete.put(field.getName(), t.get(i));
                }

            i++;
        }
        deleteFromCollection(toDelete);
        // get data from table now that the first row has been removed
        Results newResults = getAllDataFromTable(HiveTest.MONGO_BACKED_TABLE);
        // now make sure that 'toDelete' doesn't exist anymore
        for (List<String> newResult : newResults) {
            Assert.assertNotEquals(newResult, t);
        }
    }

    @Test
    public void testDropReflectData() {
        Assert.assertTrue(((getAllDataFromTable(HiveTest.MONGO_BACKED_TABLE).size()) > 0));
        getCollection(HiveTest.MONGO_COLLECTION).drop();
        Assert.assertEquals(0, getAllDataFromTable(HiveTest.MONGO_BACKED_TABLE).size());
    }

    @Test
    public void testJOINHDFSMongoDB() {
        Results mongoTblData = getAllDataFromTable(HiveTest.MONGO_BACKED_TABLE);
        Results hiveTblData = getAllDataFromTable(HiveTest.HDFS_BACKED_TABLE);
        Assert.assertNotEquals(hiveTblData.size(), 0);
        Assert.assertNotEquals(mongoTblData.size(), 0);
        Results joinedData = performTwoTableJOIN(HiveTest.MONGO_BACKED_TABLE, HiveTest.HDFS_BACKED_TABLE);
        Assert.assertEquals(((hiveTblData.size()) * (mongoTblData.size())), joinedData.size());
    }
}

