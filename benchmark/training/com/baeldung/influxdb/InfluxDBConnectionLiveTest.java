package com.baeldung.influxdb;


import java.util.List;
import java.util.concurrent.TimeUnit;
import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.InfluxDB;
import org.junit.Assert;
import org.junit.Test;


@Slf4j
public class InfluxDBConnectionLiveTest {
    @Test
    public void whenCorrectInfoDatabaseConnects() {
        InfluxDB connection = connectDatabase();
        Assert.assertTrue(pingServer(connection));
    }

    @Test
    public void whenDatabaseCreatedDatabaseChecksOk() {
        InfluxDB connection = connectDatabase();
        // Create "baeldung" and check for it
        connection.createDatabase("baeldung");
        Assert.assertTrue(connection.databaseExists("baeldung"));
        // Verify that nonsense databases are not there
        Assert.assertFalse(connection.databaseExists("foobar"));
        // Drop "baeldung" and check again
        connection.deleteDatabase("baeldung");
        Assert.assertFalse(connection.databaseExists("baeldung"));
    }

    @Test
    public void whenPointsWrittenPointsExists() throws Exception {
        InfluxDB connection = connectDatabase();
        String dbName = "baeldung";
        connection.createDatabase(dbName);
        // Need a retention policy before we can proceed
        connection.createRetentionPolicy("defaultPolicy", "baeldung", "30d", 1, true);
        // Since we are doing a batch thread, we need to set this as a default
        connection.setRetentionPolicy("defaultPolicy");
        // Enable batch mode
        connection.enableBatch(10, 10, TimeUnit.MILLISECONDS);
        for (int i = 0; i < 10; i++) {
            Point point = Point.measurement("memory").time(System.currentTimeMillis(), TimeUnit.MILLISECONDS).addField("name", "server1").addField("free", 4743656L).addField("used", 1015096L).addField("buffer", 1010467L).build();
            connection.write(dbName, "defaultPolicy", point);
            Thread.sleep(2);
        }
        // Unfortunately, the sleep inside the loop doesn't always add enough time to insure
        // that Influx's batch thread flushes all of the writes and this sometimes fails without
        // another brief pause.
        Thread.sleep(10);
        List<com.baeldung.influxdb.MemoryPoint> memoryPointList = getPoints(connection, "Select * from memory", "baeldung");
        TestCase.assertEquals(10, memoryPointList.size());
        // Turn off batch and clean up
        connection.disableBatch();
        connection.deleteDatabase("baeldung");
        connection.close();
    }

    @Test
    public void whenBatchWrittenBatchExists() {
        InfluxDB connection = connectDatabase();
        String dbName = "baeldung";
        connection.createDatabase(dbName);
        // Need a retention policy before we can proceed
        // Since we are doing batches, we need not set it
        connection.createRetentionPolicy("defaultPolicy", "baeldung", "30d", 1, true);
        BatchPoints batchPoints = BatchPoints.database(dbName).retentionPolicy("defaultPolicy").build();
        Point point1 = Point.measurement("memory").time(System.currentTimeMillis(), TimeUnit.MILLISECONDS).addField("free", 4743656L).addField("used", 1015096L).addField("buffer", 1010467L).build();
        Point point2 = Point.measurement("memory").time(((System.currentTimeMillis()) - 100), TimeUnit.MILLISECONDS).addField("free", 4743696L).addField("used", 1016096L).addField("buffer", 1008467L).build();
        batchPoints.point(point1);
        batchPoints.point(point2);
        connection.write(batchPoints);
        List<MemoryPoint> memoryPointList = getPoints(connection, "Select * from memory", "baeldung");
        TestCase.assertEquals(2, memoryPointList.size());
        Assert.assertTrue((4743696L == (memoryPointList.get(0).getFree())));
        memoryPointList = getPoints(connection, "Select * from memory order by time desc", "baeldung");
        TestCase.assertEquals(2, memoryPointList.size());
        Assert.assertTrue((4743656L == (memoryPointList.get(0).getFree())));
        // Clean up database
        connection.deleteDatabase("baeldung");
        connection.close();
    }
}

