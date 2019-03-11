package cc.blynk.server.db;


import AppNameUtil.BLYNK;
import DateTimeUtils.UTC_CALENDAR;
import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.dao.UserKey;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.Profile;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.db.model.Purchase;
import cc.blynk.server.db.model.Redeem;
import cc.blynk.utils.AppNameUtil;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import org.junit.Assert;
import org.junit.Test;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 19.02.16.
 */
public class DBManagerTest {
    private static DBManager dbManager;

    private static BlockingIOProcessor blockingIOProcessor;

    private static final Calendar UTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

    @Test
    public void test() throws Exception {
        Assert.assertNotNull(DBManagerTest.dbManager.getConnection());
    }

    @Test
    public void testDbVersion() throws Exception {
        int dbVersion = DBManagerTest.dbManager.userDBDao.getDBVersion();
        Assert.assertTrue((dbVersion >= 90500));
    }

    @Test
    public void testUpsertForDifferentApps() throws Exception {
        ArrayList<User> users = new ArrayList<>();
        users.add(new User("test1@gmail.com", "pass", "testapp2", "local", "127.0.0.1", false, false));
        users.add(new User("test1@gmail.com", "pass", "testapp1", "local", "127.0.0.1", false, false));
        DBManagerTest.dbManager.userDBDao.save(users);
        ConcurrentMap<UserKey, User> dbUsers = DBManagerTest.dbManager.userDBDao.getAllUsers("local");
        Assert.assertEquals(2, dbUsers.size());
    }

    @Test
    public void testUpsertAndSelect() throws Exception {
        ArrayList<User> users = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            users.add(new User((("test" + i) + "@gmail.com"), "pass", AppNameUtil.BLYNK, "local", "127.0.0.1", false, false));
        }
        // dbManager.saveUsers(users);
        DBManagerTest.dbManager.userDBDao.save(users);
        ConcurrentMap<UserKey, User> dbUsers = DBManagerTest.dbManager.userDBDao.getAllUsers("local");
        System.out.println(("Records : " + (dbUsers.size())));
    }

    @Test
    public void testUpsertUser() throws Exception {
        ArrayList<User> users = new ArrayList<>();
        User user = new User("test@gmail.com", "pass", AppNameUtil.BLYNK, "local", "127.0.0.1", false, false);
        user.name = "123";
        user.lastModifiedTs = 0;
        user.lastLoggedAt = 1;
        user.lastLoggedIP = "127.0.0.1";
        users.add(user);
        user = new User("test@gmail.com", "pass", AppNameUtil.BLYNK, "local", "127.0.0.1", false, false);
        user.lastModifiedTs = 0;
        user.lastLoggedAt = 1;
        user.lastLoggedIP = "127.0.0.1";
        user.name = "123";
        users.add(user);
        user = new User("test2@gmail.com", "pass", AppNameUtil.BLYNK, "local", "127.0.0.1", false, false);
        user.lastModifiedTs = 0;
        user.lastLoggedAt = 1;
        user.lastLoggedIP = "127.0.0.1";
        user.name = "123";
        users.add(user);
        DBManagerTest.dbManager.userDBDao.save(users);
        try (Connection connection = DBManagerTest.dbManager.getConnection();Statement statement = connection.createStatement();ResultSet rs = statement.executeQuery("select * from users where email = 'test@gmail.com'")) {
            while (rs.next()) {
                Assert.assertEquals("test@gmail.com", rs.getString("email"));
                Assert.assertEquals(BLYNK, rs.getString("appName"));
                Assert.assertEquals("local", rs.getString("region"));
                Assert.assertEquals("123", rs.getString("name"));
                Assert.assertEquals("pass", rs.getString("pass"));
                Assert.assertEquals(0, rs.getTimestamp("last_modified", UTC_CALENDAR).getTime());
                Assert.assertEquals(1, rs.getTimestamp("last_logged", UTC_CALENDAR).getTime());
                Assert.assertEquals("127.0.0.1", rs.getString("last_logged_ip"));
                Assert.assertFalse(rs.getBoolean("is_facebook_user"));
                Assert.assertFalse(rs.getBoolean("is_super_admin"));
                Assert.assertEquals(2000, rs.getInt("energy"));
                Assert.assertEquals("{}", rs.getString("json"));
            } 
            connection.commit();
        }
    }

    @Test
    public void testUpsertUserFieldUpdated() throws Exception {
        ArrayList<User> users = new ArrayList<>();
        User user = new User("test@gmail.com", "pass", AppNameUtil.BLYNK, "local", "127.0.0.1", false, false);
        user.lastModifiedTs = 0;
        user.lastLoggedAt = 1;
        user.lastLoggedIP = "127.0.0.1";
        users.add(user);
        DBManagerTest.dbManager.userDBDao.save(users);
        users = new ArrayList();
        user = new User("test@gmail.com", "pass2", AppNameUtil.BLYNK, "local2", "127.0.0.1", true, true);
        user.name = "1234";
        user.lastModifiedTs = 1;
        user.lastLoggedAt = 2;
        user.lastLoggedIP = "127.0.0.2";
        user.energy = 1000;
        user.profile = new Profile();
        DashBoard dash = new DashBoard();
        dash.id = 1;
        dash.name = "123";
        user.profile.dashBoards = new DashBoard[]{ dash };
        users.add(user);
        DBManagerTest.dbManager.userDBDao.save(users);
        ConcurrentMap<UserKey, User> persistent = DBManagerTest.dbManager.userDBDao.getAllUsers("local2");
        user = persistent.get(new UserKey("test@gmail.com", AppNameUtil.BLYNK));
        Assert.assertEquals("test@gmail.com", user.email);
        Assert.assertEquals(BLYNK, user.appName);
        Assert.assertEquals("local2", user.region);
        Assert.assertEquals("pass2", user.pass);
        Assert.assertEquals("1234", user.name);
        Assert.assertEquals("127.0.0.1", user.ip);
        Assert.assertEquals(1, user.lastModifiedTs);
        Assert.assertEquals(2, user.lastLoggedAt);
        Assert.assertEquals("127.0.0.2", user.lastLoggedIP);
        Assert.assertTrue(user.isFacebookUser);
        Assert.assertTrue(user.isSuperAdmin);
        Assert.assertEquals(1000, user.energy);
        Assert.assertEquals("{\"dashBoards\":[{\"id\":1,\"parentId\":-1,\"isPreview\":false,\"name\":\"123\",\"createdAt\":0,\"updatedAt\":0,\"theme\":\"Blynk\",\"keepScreenOn\":false,\"isAppConnectedOn\":false,\"isNotificationsOff\":false,\"isShared\":false,\"isActive\":false,\"widgetBackgroundOn\":false,\"color\":-1,\"isDefaultColor\":true}]}", user.profile.toString());
    }

    @Test
    public void testInsertAndGetUser() throws Exception {
        ArrayList<User> users = new ArrayList<>();
        User user = new User("test@gmail.com", "pass", AppNameUtil.BLYNK, "local", "127.0.0.1", true, true);
        user.lastModifiedTs = 0;
        user.lastLoggedAt = 1;
        user.lastLoggedIP = "127.0.0.1";
        user.profile = new Profile();
        DashBoard dash = new DashBoard();
        dash.id = 1;
        dash.name = "123";
        user.profile.dashBoards = new DashBoard[]{ dash };
        users.add(user);
        DBManagerTest.dbManager.userDBDao.save(users);
        ConcurrentMap<UserKey, User> dbUsers = DBManagerTest.dbManager.userDBDao.getAllUsers("local");
        Assert.assertNotNull(dbUsers);
        Assert.assertEquals(1, dbUsers.size());
        User dbUser = dbUsers.get(new UserKey(user.email, user.appName));
        Assert.assertEquals("test@gmail.com", dbUser.email);
        Assert.assertEquals(BLYNK, dbUser.appName);
        Assert.assertEquals("local", dbUser.region);
        Assert.assertEquals("pass", dbUser.pass);
        Assert.assertEquals(0, dbUser.lastModifiedTs);
        Assert.assertEquals(1, dbUser.lastLoggedAt);
        Assert.assertEquals("127.0.0.1", dbUser.lastLoggedIP);
        Assert.assertEquals("{\"dashBoards\":[{\"id\":1,\"parentId\":-1,\"isPreview\":false,\"name\":\"123\",\"createdAt\":0,\"updatedAt\":0,\"theme\":\"Blynk\",\"keepScreenOn\":false,\"isAppConnectedOn\":false,\"isNotificationsOff\":false,\"isShared\":false,\"isActive\":false,\"widgetBackgroundOn\":false,\"color\":-1,\"isDefaultColor\":true}]}", dbUser.profile.toString());
        Assert.assertTrue(dbUser.isFacebookUser);
        Assert.assertTrue(dbUser.isSuperAdmin);
        Assert.assertEquals(2000, dbUser.energy);
        Assert.assertEquals("{\"dashBoards\":[{\"id\":1,\"parentId\":-1,\"isPreview\":false,\"name\":\"123\",\"createdAt\":0,\"updatedAt\":0,\"theme\":\"Blynk\",\"keepScreenOn\":false,\"isAppConnectedOn\":false,\"isNotificationsOff\":false,\"isShared\":false,\"isActive\":false,\"widgetBackgroundOn\":false,\"color\":-1,\"isDefaultColor\":true}]}", dbUser.profile.toString());
    }

    @Test
    public void testInsertGetDeleteUser() throws Exception {
        ArrayList<User> users = new ArrayList<>();
        User user = new User("test@gmail.com", "pass", AppNameUtil.BLYNK, "local", "127.0.0.1", true, true);
        user.lastModifiedTs = 0;
        user.lastLoggedAt = 1;
        user.lastLoggedIP = "127.0.0.1";
        user.profile = new Profile();
        DashBoard dash = new DashBoard();
        dash.id = 1;
        dash.name = "123";
        user.profile.dashBoards = new DashBoard[]{ dash };
        users.add(user);
        DBManagerTest.dbManager.userDBDao.save(users);
        Map<UserKey, User> dbUsers = DBManagerTest.dbManager.userDBDao.getAllUsers("local");
        Assert.assertNotNull(dbUsers);
        Assert.assertEquals(1, dbUsers.size());
        User dbUser = dbUsers.get(new UserKey(user.email, user.appName));
        Assert.assertEquals("test@gmail.com", dbUser.email);
        Assert.assertEquals(BLYNK, dbUser.appName);
        Assert.assertEquals("local", dbUser.region);
        Assert.assertEquals("pass", dbUser.pass);
        Assert.assertEquals(0, dbUser.lastModifiedTs);
        Assert.assertEquals(1, dbUser.lastLoggedAt);
        Assert.assertEquals("127.0.0.1", dbUser.lastLoggedIP);
        Assert.assertEquals("{\"dashBoards\":[{\"id\":1,\"parentId\":-1,\"isPreview\":false,\"name\":\"123\",\"createdAt\":0,\"updatedAt\":0,\"theme\":\"Blynk\",\"keepScreenOn\":false,\"isAppConnectedOn\":false,\"isNotificationsOff\":false,\"isShared\":false,\"isActive\":false,\"widgetBackgroundOn\":false,\"color\":-1,\"isDefaultColor\":true}]}", dbUser.profile.toString());
        Assert.assertTrue(dbUser.isFacebookUser);
        Assert.assertTrue(dbUser.isSuperAdmin);
        Assert.assertEquals(2000, dbUser.energy);
        Assert.assertEquals("{\"dashBoards\":[{\"id\":1,\"parentId\":-1,\"isPreview\":false,\"name\":\"123\",\"createdAt\":0,\"updatedAt\":0,\"theme\":\"Blynk\",\"keepScreenOn\":false,\"isAppConnectedOn\":false,\"isNotificationsOff\":false,\"isShared\":false,\"isActive\":false,\"widgetBackgroundOn\":false,\"color\":-1,\"isDefaultColor\":true}]}", dbUser.profile.toString());
        Assert.assertTrue(DBManagerTest.dbManager.userDBDao.deleteUser(new UserKey(user.email, user.appName)));
        dbUsers = DBManagerTest.dbManager.userDBDao.getAllUsers("local");
        Assert.assertNotNull(dbUsers);
        Assert.assertEquals(0, dbUsers.size());
    }

    @Test
    public void testRedeem() throws Exception {
        Assert.assertNull(DBManagerTest.dbManager.selectRedeemByToken("123"));
        String token = UUID.randomUUID().toString().replace("-", "");
        DBManagerTest.dbManager.executeSQL((("insert into redeem (token) values('" + token) + "')"));
        Assert.assertNotNull(DBManagerTest.dbManager.selectRedeemByToken(token));
        Assert.assertNull(DBManagerTest.dbManager.selectRedeemByToken("123"));
    }

    @Test
    public void testPurchase() throws Exception {
        DBManagerTest.dbManager.insertPurchase(new Purchase("test@gmail.com", 1000, 1.0, "123456"));
        try (Connection connection = DBManagerTest.dbManager.getConnection();Statement statement = connection.createStatement();ResultSet rs = statement.executeQuery("select * from purchase")) {
            while (rs.next()) {
                Assert.assertEquals("test@gmail.com", rs.getString("email"));
                Assert.assertEquals(1000, rs.getInt("reward"));
                Assert.assertEquals("123456", rs.getString("transactionId"));
                Assert.assertEquals(0.99, rs.getDouble("price"), 0.1);
                Assert.assertNotNull(rs.getDate("ts"));
            } 
            connection.commit();
        }
    }

    @Test
    public void testOptimisticLockingRedeem() throws Exception {
        String token = UUID.randomUUID().toString().replace("-", "");
        DBManagerTest.dbManager.executeSQL((("insert into redeem (token) values('" + token) + "')"));
        Redeem redeem = DBManagerTest.dbManager.selectRedeemByToken(token);
        Assert.assertNotNull(redeem);
        Assert.assertEquals(redeem.token, token);
        Assert.assertFalse(redeem.isRedeemed);
        Assert.assertEquals(1, redeem.version);
        Assert.assertNull(redeem.ts);
        Assert.assertTrue(DBManagerTest.dbManager.updateRedeem("user@user.com", token));
        Assert.assertFalse(DBManagerTest.dbManager.updateRedeem("user@user.com", token));
        redeem = DBManagerTest.dbManager.selectRedeemByToken(token);
        Assert.assertNotNull(redeem);
        Assert.assertEquals(redeem.token, token);
        Assert.assertTrue(redeem.isRedeemed);
        Assert.assertEquals(2, redeem.version);
        Assert.assertEquals("user@user.com", redeem.email);
        Assert.assertNotNull(redeem.ts);
    }

    @Test
    public void getUserIpNotExists() {
        String userIp = DBManagerTest.dbManager.userDBDao.getUserServerIp("test@gmail.com", BLYNK);
        Assert.assertNull(userIp);
    }

    @Test
    public void getUserIp() {
        ArrayList<User> users = new ArrayList<>();
        User user = new User("test@gmail.com", "pass", AppNameUtil.BLYNK, "local", "127.0.0.1", false, false);
        user.lastModifiedTs = 0;
        user.lastLoggedAt = 1;
        user.lastLoggedIP = "127.0.0.1";
        users.add(user);
        DBManagerTest.dbManager.userDBDao.save(users);
        String userIp = DBManagerTest.dbManager.userDBDao.getUserServerIp("test@gmail.com", BLYNK);
        Assert.assertEquals("127.0.0.1", userIp);
    }
}

