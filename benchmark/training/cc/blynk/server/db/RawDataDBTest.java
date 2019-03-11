package cc.blynk.server.db;


import NumberUtil.NO_RESULT;
import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.reporting.raw.RawDataProcessor;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import java.util.TimeZone;
import org.junit.Assert;
import org.junit.Test;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 19.02.16.
 */
// todo tests for large batches.
public class RawDataDBTest {
    private static ReportingDBManager reportingDBManager;

    private static BlockingIOProcessor blockingIOProcessor;

    private static final Calendar UTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

    private static User user;

    @Test
    public void testInsertStringAsRawData() throws Exception {
        RawDataProcessor rawDataProcessor = new RawDataProcessor(true);
        rawDataProcessor.collect(new cc.blynk.server.core.reporting.raw.BaseReportingKey(RawDataDBTest.user.email, RawDataDBTest.user.appName, 1, 2, PinType.VIRTUAL, ((short) (3))), 1111111111, "Lamp is ON", NO_RESULT);
        // invoking directly dao to avoid separate thread execution
        RawDataDBTest.reportingDBManager.reportingDBDao.insertRawData(rawDataProcessor.rawStorage);
        try (Connection connection = RawDataDBTest.reportingDBManager.getConnection();Statement statement = connection.createStatement();ResultSet rs = statement.executeQuery("select * from reporting_raw_data")) {
            while (rs.next()) {
                Assert.assertEquals("test@test.com", rs.getString("email"));
                Assert.assertEquals(1, rs.getInt("project_id"));
                Assert.assertEquals(2, rs.getInt("device_id"));
                Assert.assertEquals(3, rs.getByte("pin"));
                Assert.assertEquals("v", rs.getString("pinType"));
                Assert.assertEquals(1111111111, rs.getTimestamp("ts", RawDataDBTest.UTC).getTime());
                Assert.assertEquals("Lamp is ON", rs.getString("stringValue"));
                Assert.assertNull(rs.getString("doubleValue"));
            } 
            connection.commit();
        }
    }

    @Test
    public void testInsertDoubleAsRawData() throws Exception {
        RawDataProcessor rawDataProcessor = new RawDataProcessor(true);
        rawDataProcessor.collect(new cc.blynk.server.core.reporting.raw.BaseReportingKey(RawDataDBTest.user.email, RawDataDBTest.user.appName, 1, 2, PinType.VIRTUAL, ((short) (3))), 1111111111, "Lamp is ON", 1.33);
        // invoking directly dao to avoid separate thread execution
        RawDataDBTest.reportingDBManager.reportingDBDao.insertRawData(rawDataProcessor.rawStorage);
        try (Connection connection = RawDataDBTest.reportingDBManager.getConnection();Statement statement = connection.createStatement();ResultSet rs = statement.executeQuery("select * from reporting_raw_data")) {
            while (rs.next()) {
                Assert.assertEquals("test@test.com", rs.getString("email"));
                Assert.assertEquals(1, rs.getInt("project_id"));
                Assert.assertEquals(2, rs.getInt("device_id"));
                Assert.assertEquals(3, rs.getByte("pin"));
                Assert.assertEquals("v", rs.getString("pinType"));
                Assert.assertEquals(1111111111, rs.getTimestamp("ts", RawDataDBTest.UTC).getTime());
                Assert.assertNull(rs.getString("stringValue"));
                Assert.assertEquals(1.33, rs.getDouble("doubleValue"), 1.0E-7);
            } 
            connection.commit();
        }
    }
}

