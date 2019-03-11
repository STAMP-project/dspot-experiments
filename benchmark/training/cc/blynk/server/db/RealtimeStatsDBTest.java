package cc.blynk.server.db;


import GraphGranularityType.DAILY;
import GraphGranularityType.HOURLY;
import GraphGranularityType.MINUTE;
import PinType.VIRTUAL;
import ReportingDBDao.insertMinute;
import ReportingDBDao.selectMinute;
import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.dao.UserDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.widgets.ui.reporting.ReportScheduler;
import cc.blynk.server.core.reporting.average.AggregationKey;
import cc.blynk.server.core.reporting.average.AggregationValue;
import cc.blynk.server.core.reporting.average.AverageAggregatorProcessor;
import cc.blynk.server.core.stats.GlobalStats;
import cc.blynk.server.core.stats.model.CommandStat;
import cc.blynk.server.core.stats.model.HttpStat;
import cc.blynk.server.core.stats.model.Stat;
import cc.blynk.server.db.dao.ReportingDBDao;
import cc.blynk.utils.AppNameUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Instant;
import java.util.Calendar;
import java.util.Collections;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.Assert;
import org.junit.Test;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 19.02.16.
 */
public class RealtimeStatsDBTest {
    private static ReportingDBManager reportingDBManager;

    private static BlockingIOProcessor blockingIOProcessor;

    private static final Calendar UTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

    @Test
    public void testRealTimeStatsInsertWroks() throws Exception {
        String region = "ua";
        long now = System.currentTimeMillis();
        SessionDao sessionDao = new SessionDao();
        UserDao userDao = new UserDao(new ConcurrentHashMap(), "test", "127.0.0.1");
        BlockingIOProcessor blockingIOProcessor = new BlockingIOProcessor(6, 1000);
        Stat stat = new Stat(sessionDao, userDao, blockingIOProcessor, new GlobalStats(), new ReportScheduler(1, "http://localhost/", null, null, Collections.emptyMap()), false);
        int i;
        final HttpStat hs = stat.http;
        i = 0;
        hs.isHardwareConnected = i++;
        hs.isAppConnected = i++;
        hs.getPinData = i++;
        hs.updatePinData = i++;
        hs.email = i++;
        hs.notify = i++;
        hs.getProject = i++;
        hs.qr = i++;
        hs.getHistoryPinData = i++;
        hs.total = i;
        final CommandStat cs = stat.commands;
        i = 0;
        cs.response = i++;
        cs.register = i++;
        cs.login = i++;
        cs.loadProfile = i++;
        cs.appSync = i++;
        cs.sharing = i++;
        cs.getToken = i++;
        cs.ping = i++;
        cs.activate = i++;
        cs.deactivate = i++;
        cs.refreshToken = i++;
        cs.getGraphData = i++;
        cs.exportGraphData = i++;
        cs.setWidgetProperty = i++;
        cs.bridge = i++;
        cs.hardware = i++;
        cs.getSharedDash = i++;
        cs.getShareToken = i++;
        cs.refreshShareToken = i++;
        cs.shareLogin = i++;
        cs.createProject = i++;
        cs.updateProject = i++;
        cs.deleteProject = i++;
        cs.hardwareSync = i++;
        cs.internal = i++;
        cs.sms = i++;
        cs.tweet = i++;
        cs.email = i++;
        cs.push = i++;
        cs.addPushToken = i++;
        cs.createWidget = i++;
        cs.updateWidget = i++;
        cs.deleteWidget = i++;
        cs.createDevice = i++;
        cs.updateDevice = i++;
        cs.deleteDevice = i++;
        cs.getDevices = i++;
        cs.createTag = i++;
        cs.updateTag = i++;
        cs.deleteTag = i++;
        cs.getTags = i++;
        cs.addEnergy = i++;
        cs.getEnergy = i++;
        cs.getServer = i++;
        cs.connectRedirect = i++;
        cs.webSockets = i++;
        cs.eventor = i++;
        cs.webhooks = i++;
        cs.appTotal = i++;
        cs.mqttTotal = i;
        RealtimeStatsDBTest.reportingDBManager.reportingDBDao.insertStat(region, stat);
        try (Connection connection = RealtimeStatsDBTest.reportingDBManager.getConnection();Statement statement = connection.createStatement();ResultSet rs = statement.executeQuery("select * from reporting_app_stat_minute")) {
            while (rs.next()) {
                Assert.assertEquals(region, rs.getString("region"));
                Assert.assertEquals(((now / (AverageAggregatorProcessor.MINUTE)) * (AverageAggregatorProcessor.MINUTE)), rs.getTimestamp("ts", RealtimeStatsDBTest.UTC).getTime());
                Assert.assertEquals(0, rs.getInt("minute_rate"));
                Assert.assertEquals(0, rs.getInt("registrations"));
                Assert.assertEquals(0, rs.getInt("active"));
                Assert.assertEquals(0, rs.getInt("active_week"));
                Assert.assertEquals(0, rs.getInt("active_month"));
                Assert.assertEquals(0, rs.getInt("connected"));
                Assert.assertEquals(0, rs.getInt("online_apps"));
                Assert.assertEquals(0, rs.getInt("total_online_apps"));
                Assert.assertEquals(0, rs.getInt("online_hards"));
                Assert.assertEquals(0, rs.getInt("total_online_hards"));
            } 
            connection.commit();
        }
        try (Connection connection = RealtimeStatsDBTest.reportingDBManager.getConnection();Statement statement = connection.createStatement();ResultSet rs = statement.executeQuery("select * from reporting_http_command_stat_minute")) {
            i = 0;
            while (rs.next()) {
                Assert.assertEquals(region, rs.getString("region"));
                Assert.assertEquals(((now / (AverageAggregatorProcessor.MINUTE)) * (AverageAggregatorProcessor.MINUTE)), rs.getTimestamp("ts", RealtimeStatsDBTest.UTC).getTime());
                Assert.assertEquals((i++), rs.getInt("is_hardware_connected"));
                Assert.assertEquals((i++), rs.getInt("is_app_connected"));
                Assert.assertEquals((i++), rs.getInt("get_pin_data"));
                Assert.assertEquals((i++), rs.getInt("update_pin"));
                Assert.assertEquals((i++), rs.getInt("email"));
                Assert.assertEquals((i++), rs.getInt("push"));
                Assert.assertEquals((i++), rs.getInt("get_project"));
                Assert.assertEquals((i++), rs.getInt("qr"));
                Assert.assertEquals((i++), rs.getInt("get_history_pin_data"));
                Assert.assertEquals((i++), rs.getInt("total"));
            } 
            connection.commit();
        }
        try (Connection connection = RealtimeStatsDBTest.reportingDBManager.getConnection();Statement statement = connection.createStatement();ResultSet rs = statement.executeQuery("select * from reporting_app_command_stat_minute")) {
            i = 0;
            while (rs.next()) {
                Assert.assertEquals(region, rs.getString("region"));
                Assert.assertEquals(((now / (AverageAggregatorProcessor.MINUTE)) * (AverageAggregatorProcessor.MINUTE)), rs.getTimestamp("ts", RealtimeStatsDBTest.UTC).getTime());
                Assert.assertEquals((i++), rs.getInt("response"));
                Assert.assertEquals((i++), rs.getInt("register"));
                Assert.assertEquals((i++), rs.getInt("login"));
                Assert.assertEquals((i++), rs.getInt("load_profile"));
                Assert.assertEquals((i++), rs.getInt("app_sync"));
                Assert.assertEquals((i++), rs.getInt("sharing"));
                Assert.assertEquals((i++), rs.getInt("get_token"));
                Assert.assertEquals((i++), rs.getInt("ping"));
                Assert.assertEquals((i++), rs.getInt("activate"));
                Assert.assertEquals((i++), rs.getInt("deactivate"));
                Assert.assertEquals((i++), rs.getInt("refresh_token"));
                Assert.assertEquals((i++), rs.getInt("get_graph_data"));
                Assert.assertEquals((i++), rs.getInt("export_graph_data"));
                Assert.assertEquals((i++), rs.getInt("set_widget_property"));
                Assert.assertEquals((i++), rs.getInt("bridge"));
                Assert.assertEquals((i++), rs.getInt("hardware"));
                Assert.assertEquals((i++), rs.getInt("get_share_dash"));
                Assert.assertEquals((i++), rs.getInt("get_share_token"));
                Assert.assertEquals((i++), rs.getInt("refresh_share_token"));
                Assert.assertEquals((i++), rs.getInt("share_login"));
                Assert.assertEquals((i++), rs.getInt("create_project"));
                Assert.assertEquals((i++), rs.getInt("update_project"));
                Assert.assertEquals((i++), rs.getInt("delete_project"));
                Assert.assertEquals((i++), rs.getInt("hardware_sync"));
                Assert.assertEquals((i++), rs.getInt("internal"));
                Assert.assertEquals((i++), rs.getInt("sms"));
                Assert.assertEquals((i++), rs.getInt("tweet"));
                Assert.assertEquals((i++), rs.getInt("email"));
                Assert.assertEquals((i++), rs.getInt("push"));
                Assert.assertEquals((i++), rs.getInt("add_push_token"));
                Assert.assertEquals((i++), rs.getInt("create_widget"));
                Assert.assertEquals((i++), rs.getInt("update_widget"));
                Assert.assertEquals((i++), rs.getInt("delete_widget"));
                Assert.assertEquals((i++), rs.getInt("create_device"));
                Assert.assertEquals((i++), rs.getInt("update_device"));
                Assert.assertEquals((i++), rs.getInt("delete_device"));
                Assert.assertEquals((i++), rs.getInt("get_devices"));
                Assert.assertEquals((i++), rs.getInt("create_tag"));
                Assert.assertEquals((i++), rs.getInt("update_tag"));
                Assert.assertEquals((i++), rs.getInt("delete_tag"));
                Assert.assertEquals((i++), rs.getInt("get_tags"));
                Assert.assertEquals((i++), rs.getInt("add_energy"));
                Assert.assertEquals((i++), rs.getInt("get_energy"));
                Assert.assertEquals((i++), rs.getInt("get_server"));
                Assert.assertEquals((i++), rs.getInt("connect_redirect"));
                Assert.assertEquals((i++), rs.getInt("web_sockets"));
                Assert.assertEquals((i++), rs.getInt("eventor"));
                Assert.assertEquals((i++), rs.getInt("webhooks"));
                Assert.assertEquals((i++), rs.getInt("appTotal"));
                Assert.assertEquals(i, rs.getInt("hardTotal"));
            } 
            connection.commit();
        }
    }

    @Test
    public void testManyConnections() throws Exception {
        User user = new User();
        user.email = "test@test.com";
        user.appName = AppNameUtil.BLYNK;
        Map<AggregationKey, AggregationValue> map = new ConcurrentHashMap<>();
        AggregationValue value = new AggregationValue();
        value.update(1);
        long ts = System.currentTimeMillis();
        for (int i = 0; i < 60; i++) {
            map.put(new AggregationKey(user.email, user.appName, i, 0, PinType.ANALOG, ((short) (i)), ts), value);
            RealtimeStatsDBTest.reportingDBManager.insertReporting(map, MINUTE);
            RealtimeStatsDBTest.reportingDBManager.insertReporting(map, HOURLY);
            RealtimeStatsDBTest.reportingDBManager.insertReporting(map, DAILY);
            map.clear();
        }
        while ((RealtimeStatsDBTest.blockingIOProcessor.messagingExecutor.getActiveCount()) > 0) {
            Thread.sleep(100);
        } 
    }

    @Test
    public void cleanOutdatedRecords() {
        RealtimeStatsDBTest.reportingDBManager.reportingDBDao.cleanOldReportingRecords(Instant.now());
    }

    @Test
    public void testDeleteWorksAsExpected() throws Exception {
        long minute;
        try (Connection connection = RealtimeStatsDBTest.reportingDBManager.getConnection();PreparedStatement ps = connection.prepareStatement(insertMinute)) {
            minute = ((System.currentTimeMillis()) / (AverageAggregatorProcessor.MINUTE)) * (AverageAggregatorProcessor.MINUTE);
            for (int i = 0; i < 370; i++) {
                ReportingDBDao.prepareReportingInsert(ps, "test1111@gmail.com", 1, 0, ((short) (0)), VIRTUAL, minute, ((double) (i)));
                ps.addBatch();
                minute += AverageAggregatorProcessor.MINUTE;
            }
            ps.executeBatch();
            connection.commit();
        }
    }

    @Test
    public void testInsert1000RecordsAndSelect() throws Exception {
        int a = 0;
        String userName = "test@gmail.com";
        long start = System.currentTimeMillis();
        long minute = (start / (AverageAggregatorProcessor.MINUTE)) * (AverageAggregatorProcessor.MINUTE);
        long startMinute = minute;
        try (Connection connection = RealtimeStatsDBTest.reportingDBManager.getConnection();PreparedStatement ps = connection.prepareStatement(insertMinute)) {
            for (int i = 0; i < 1000; i++) {
                ReportingDBDao.prepareReportingInsert(ps, userName, 1, 2, ((short) (0)), VIRTUAL, minute, ((double) (i)));
                ps.addBatch();
                minute += AverageAggregatorProcessor.MINUTE;
                a++;
            }
            ps.executeBatch();
            connection.commit();
        }
        System.out.println(((("Finished : " + ((System.currentTimeMillis()) - start)) + " millis. Executed : ") + a));
        try (Connection connection = RealtimeStatsDBTest.reportingDBManager.getConnection();Statement statement = connection.createStatement();ResultSet rs = statement.executeQuery("select * from reporting_average_minute order by ts ASC")) {
            int i = 0;
            while (rs.next()) {
                Assert.assertEquals(userName, rs.getString("email"));
                Assert.assertEquals(1, rs.getInt("project_id"));
                Assert.assertEquals(2, rs.getInt("device_id"));
                Assert.assertEquals(0, rs.getByte("pin"));
                Assert.assertEquals(VIRTUAL, PinType.values()[rs.getInt("pin_type")]);
                Assert.assertEquals(startMinute, rs.getTimestamp("ts", RealtimeStatsDBTest.UTC).getTime());
                Assert.assertEquals(((double) (i)), rs.getDouble("value"), 1.0E-4);
                startMinute += AverageAggregatorProcessor.MINUTE;
                i++;
            } 
            connection.commit();
        }
    }

    @Test
    public void testSelect() throws Exception {
        long ts = 1455924480000L;
        try (Connection connection = RealtimeStatsDBTest.reportingDBManager.getConnection();PreparedStatement ps = connection.prepareStatement(selectMinute)) {
            ReportingDBDao.prepareReportingSelect(ps, ts, 2);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                System.out.println((((rs.getLong("ts")) + " ") + (rs.getDouble("value"))));
            } 
            rs.close();
        }
    }
}

