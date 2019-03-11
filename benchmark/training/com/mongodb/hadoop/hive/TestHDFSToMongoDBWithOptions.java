package com.mongodb.hadoop.hive;


import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class TestHDFSToMongoDBWithOptions extends HiveTest {
    @Test
    @SuppressWarnings("unchecked")
    public void testMongoMapping() {
        DBObject doc = getCollection(HiveTest.MONGO_COLLECTION).findOne();
        String[] propsSplit = HiveTest.SERDE_PROPERTIES.split("=");
        int propsSplitLen = propsSplit.length;
        Assert.assertEquals((propsSplitLen % 2), 0);
        // now read in the 'mongo.columns.mapping' mapping
        String colsMap = null;
        for (int i = 0; (i < (propsSplit.length)) && (colsMap == null); i++) {
            final String entry = propsSplit[i];
            if ((entry.toLowerCase().equals("'mongo.columns.mapping'")) && ((i - 1) < propsSplitLen)) {
                colsMap = propsSplit[(i + 1)];
            }
        }
        Assert.assertNotNull(colsMap);
        // first remove '' around colsMap
        colsMap = colsMap.substring(1, ((colsMap.length()) - 1));
        Set<String> docKeys = doc.keySet();
        for (String s : ((Map<String, String>) (JSON.parse(colsMap))).values()) {
            Assert.assertTrue(docKeys.contains(s));
        }
    }

    @Test
    public void testCountSameTable() {
        Results hiveData = getAllDataFromTable(HiveTest.HDFS_BACKED_TABLE);
        Results mongoData = getAllDataFromTable(HiveTest.MONGO_BACKED_TABLE);
        Assert.assertNotEquals(hiveData.size(), 0);
        Assert.assertNotEquals(mongoData.size(), 0);
        Assert.assertEquals(hiveData, mongoData);
    }
}

