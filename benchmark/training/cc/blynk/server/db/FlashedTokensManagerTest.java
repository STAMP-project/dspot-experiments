package cc.blynk.server.db;


import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.db.model.FlashedToken;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 19.02.16.
 */
public class FlashedTokensManagerTest {
    private static DBManager dbManager;

    private static BlockingIOProcessor blockingIOProcessor;

    @Test
    public void test() throws Exception {
        Assert.assertNotNull(FlashedTokensManagerTest.dbManager.getConnection());
    }

    @Test
    public void testNoToken() throws Exception {
        Assert.assertNull(FlashedTokensManagerTest.dbManager.selectFlashedToken("123"));
    }

    @Test
    public void testInsertAndSelect() throws Exception {
        FlashedToken[] list = new FlashedToken[1];
        String token = UUID.randomUUID().toString().replace("-", "");
        FlashedToken flashedToken = new FlashedToken("test@blynk.cc", token, "appname", 1, 0);
        list[0] = flashedToken;
        FlashedTokensManagerTest.dbManager.insertFlashedTokens(list);
        FlashedToken selected = FlashedTokensManagerTest.dbManager.selectFlashedToken(token);
        Assert.assertEquals(flashedToken, selected);
    }

    @Test
    public void testInsertToken() throws Exception {
        FlashedToken[] list = new FlashedToken[1];
        String token = UUID.randomUUID().toString().replace("-", "");
        FlashedToken flashedToken = new FlashedToken("test@blynk.cc", token, "appname", 1, 0);
        list[0] = flashedToken;
        FlashedTokensManagerTest.dbManager.insertFlashedTokens(list);
        try (Connection connection = FlashedTokensManagerTest.dbManager.getConnection();Statement statement = connection.createStatement();ResultSet rs = statement.executeQuery("select * from flashed_tokens")) {
            while (rs.next()) {
                Assert.assertEquals(flashedToken.token, rs.getString("token"));
                Assert.assertEquals(flashedToken.appId, rs.getString("app_name"));
                Assert.assertEquals(flashedToken.deviceId, rs.getInt("device_id"));
                Assert.assertFalse(rs.getBoolean("is_activated"));
                Assert.assertNull(rs.getDate("ts"));
            } 
            connection.commit();
        }
    }

    @Test
    public void testInsertAndActivate() throws Exception {
        FlashedToken[] list = new FlashedToken[1];
        String token = UUID.randomUUID().toString().replace("-", "");
        FlashedToken flashedToken = new FlashedToken("test@blynk.cc", token, "appname", 1, 0);
        list[0] = flashedToken;
        FlashedTokensManagerTest.dbManager.insertFlashedTokens(list);
        FlashedTokensManagerTest.dbManager.activateFlashedToken(flashedToken.token);
        try (Connection connection = FlashedTokensManagerTest.dbManager.getConnection();Statement statement = connection.createStatement();ResultSet rs = statement.executeQuery("select * from flashed_tokens")) {
            while (rs.next()) {
                Assert.assertEquals(flashedToken.token, rs.getString("token"));
                Assert.assertEquals(flashedToken.appId, rs.getString("app_name"));
                Assert.assertEquals(flashedToken.deviceId, rs.getInt("device_id"));
                Assert.assertTrue(rs.getBoolean("is_activated"));
                Assert.assertNotNull(rs.getDate("ts"));
            } 
            connection.commit();
        }
    }
}

