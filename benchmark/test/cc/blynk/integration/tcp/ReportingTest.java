package cc.blynk.integration.tcp;


import FileUtils.CSV_DIR;
import Format.ISO_SIMPLE.pattern;
import GraphGranularityType.DAILY;
import GraphGranularityType.HOURLY;
import GraphGranularityType.MINUTE;
import PinType.DIGITAL;
import PinType.VIRTUAL;
import ReportResult.NO_DATA;
import cc.blynk.integration.BaseTest;
import cc.blynk.integration.CounterBase;
import cc.blynk.integration.Holder;
import cc.blynk.integration.TestUtil;
import cc.blynk.integration.model.tcp.ClientPair;
import cc.blynk.server.core.dao.ReportingDiskDao;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.device.BoardType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.widgets.outputs.graph.GraphGranularityType;
import cc.blynk.server.core.model.widgets.ui.reporting.Format;
import cc.blynk.server.core.model.widgets.ui.reporting.Report;
import cc.blynk.server.core.model.widgets.ui.reporting.ReportingWidget;
import cc.blynk.server.core.model.widgets.ui.reporting.source.ReportDataStream;
import cc.blynk.server.core.model.widgets.ui.reporting.source.ReportSource;
import cc.blynk.server.core.model.widgets.ui.reporting.type.DayOfMonth;
import cc.blynk.server.core.model.widgets.ui.reporting.type.OneTimeReport;
import cc.blynk.server.core.model.widgets.ui.reporting.type.ReportDurationType;
import cc.blynk.server.servers.BaseServer;
import cc.blynk.utils.AppNameUtil;
import cc.blynk.utils.FileUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.asynchttpclient.Response;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/2/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class ReportingTest extends BaseTest {
    private BaseServer appServer;

    private BaseServer hardwareServer;

    private ClientPair clientPair;

    @Test
    public void testDeleteAllDeviceData() throws Exception {
        Device device1 = new Device(2, "My Device2", BoardType.ESP8266);
        clientPair.appClient.createDevice(1, device1);
        String tempDir = holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        Path pinReportingDataPath10 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, DIGITAL, ((short) (8)), MINUTE));
        Path pinReportingDataPath11 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, DIGITAL, ((short) (8)), HOURLY));
        Path pinReportingDataPath12 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, DIGITAL, ((short) (8)), DAILY));
        Path pinReportingDataPath20 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 2, DIGITAL, ((short) (8)), MINUTE));
        FileUtils.write(pinReportingDataPath10, 1.11, 1111111);
        FileUtils.write(pinReportingDataPath11, 1.11, 1111111);
        FileUtils.write(pinReportingDataPath12, 1.11, 1111111);
        FileUtils.write(pinReportingDataPath20, 1.22, 2222222);
        clientPair.appClient.send("deleteDeviceData 1-*");
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        Assert.assertTrue(Files.notExists(pinReportingDataPath10));
        Assert.assertTrue(Files.notExists(pinReportingDataPath11));
        Assert.assertTrue(Files.notExists(pinReportingDataPath12));
        Assert.assertTrue(Files.notExists(pinReportingDataPath20));
    }

    @Test
    public void testDeleteDeviceDataFor1Device() throws Exception {
        Device device1 = new Device(2, "My Device2", BoardType.ESP8266);
        clientPair.appClient.createDevice(1, device1);
        String tempDir = holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        Path pinReportingDataPath10 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, DIGITAL, ((short) (8)), MINUTE));
        Path pinReportingDataPath11 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, DIGITAL, ((short) (8)), HOURLY));
        Path pinReportingDataPath12 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, DIGITAL, ((short) (8)), DAILY));
        Path pinReportingDataPath20 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 2, DIGITAL, ((short) (8)), MINUTE));
        FileUtils.write(pinReportingDataPath10, 1.11, 1111111);
        FileUtils.write(pinReportingDataPath11, 1.11, 1111111);
        FileUtils.write(pinReportingDataPath12, 1.11, 1111111);
        FileUtils.write(pinReportingDataPath20, 1.22, 2222222);
        clientPair.appClient.deleteDeviceData(1, 2);
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        Assert.assertTrue(Files.exists(pinReportingDataPath10));
        Assert.assertTrue(Files.exists(pinReportingDataPath11));
        Assert.assertTrue(Files.exists(pinReportingDataPath12));
        Assert.assertTrue(Files.notExists(pinReportingDataPath20));
    }

    @Test
    public void testDeleteDeviceDataForSpecificPin() throws Exception {
        Device device1 = new Device(2, "My Device2", BoardType.ESP8266);
        clientPair.appClient.createDevice(1, device1);
        String tempDir = holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        Path pinReportingDataPath10 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, DIGITAL, ((short) (8)), MINUTE));
        Path pinReportingDataPath11 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, DIGITAL, ((short) (8)), HOURLY));
        Path pinReportingDataPath12 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, DIGITAL, ((short) (8)), DAILY));
        Path pinReportingDataPath13 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, VIRTUAL, ((short) (9)), DAILY));
        Path pinReportingDataPath20 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 2, DIGITAL, ((short) (8)), MINUTE));
        FileUtils.write(pinReportingDataPath10, 1.11, 1111111);
        FileUtils.write(pinReportingDataPath11, 1.11, 1111111);
        FileUtils.write(pinReportingDataPath12, 1.11, 1111111);
        FileUtils.write(pinReportingDataPath13, 1.11, 1111111);
        FileUtils.write(pinReportingDataPath20, 1.22, 2222222);
        clientPair.appClient.deleteDeviceData(1, 2, "d8");
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        Assert.assertTrue(Files.exists(pinReportingDataPath10));
        Assert.assertTrue(Files.exists(pinReportingDataPath11));
        Assert.assertTrue(Files.exists(pinReportingDataPath12));
        Assert.assertTrue(Files.exists(pinReportingDataPath13));
        Assert.assertTrue(Files.notExists(pinReportingDataPath20));
        clientPair.appClient.deleteDeviceData(1, 0, "d8", "v9");
        clientPair.appClient.verifyResult(TestUtil.ok(3));
        Assert.assertTrue(Files.notExists(pinReportingDataPath10));
        Assert.assertTrue(Files.notExists(pinReportingDataPath11));
        Assert.assertTrue(Files.notExists(pinReportingDataPath12));
        Assert.assertTrue(Files.notExists(pinReportingDataPath13));
        Assert.assertTrue(Files.notExists(pinReportingDataPath20));
    }

    @Test
    public void createReportCRUD() throws Exception {
        ReportDataStream reportDataStream = new ReportDataStream(((short) (1)), PinType.VIRTUAL, "Temperature", true);
        ReportSource reportSource = new cc.blynk.server.core.model.widgets.ui.reporting.source.TileTemplateReportSource(new ReportDataStream[]{ reportDataStream }, 1, new int[]{ 0 });
        ReportingWidget reportingWidget = new ReportingWidget();
        reportingWidget.height = 1;
        reportingWidget.width = 1;
        reportingWidget.reportSources = new ReportSource[]{ reportSource };
        clientPair.appClient.send("getEnergy");
        clientPair.appClient.verifyResult(produce(1, GET_ENERGY, "7500"));
        clientPair.appClient.createWidget(1, reportingWidget);
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        clientPair.appClient.send("getEnergy");
        clientPair.appClient.verifyResult(produce(3, GET_ENERGY, "7500"));
        Report report = new Report(1, "My One Time Report", new ReportSource[]{ reportSource }, new OneTimeReport(TimeUnit.DAYS.toMillis(1)), "test@gmail.com", GraphGranularityType.MINUTE, true, CSV_FILE_PER_DEVICE, null, ZoneId.of("UTC"), 0, 0, null);
        clientPair.appClient.createReport(1, report);
        report = clientPair.appClient.parseReportFromResponse(4);
        Assert.assertNotNull(report);
        clientPair.appClient.send("getEnergy");
        clientPair.appClient.verifyResult(produce(5, GET_ENERGY, "4600"));
        report = new Report(1, "Updated", new ReportSource[]{ reportSource }, new OneTimeReport(TimeUnit.DAYS.toMillis(1)), "test@gmail.com", GraphGranularityType.MINUTE, true, CSV_FILE_PER_DEVICE, null, ZoneId.of("UTC"), 0, 0, null);
        clientPair.appClient.updateReport(1, report);
        report = clientPair.appClient.parseReportFromResponse(6);
        Assert.assertNotNull(report);
        Assert.assertEquals("Updated", report.name);
        clientPair.appClient.deleteReport(1, report.id);
        clientPair.appClient.verifyResult(TestUtil.ok(7));
        clientPair.appClient.send("getEnergy");
        clientPair.appClient.verifyResult(produce(8, GET_ENERGY, "7500"));
    }

    @Test
    public void testDailyReportIsTriggered() throws Exception {
        ReportDataStream reportDataStream = new ReportDataStream(((short) (1)), PinType.VIRTUAL, "Temperature", true);
        ReportSource reportSource = new cc.blynk.server.core.model.widgets.ui.reporting.source.TileTemplateReportSource(new ReportDataStream[]{ reportDataStream }, 1, new int[]{ 0 });
        ReportingWidget reportingWidget = new ReportingWidget();
        reportingWidget.height = 1;
        reportingWidget.width = 1;
        reportingWidget.reportSources = new ReportSource[]{ reportSource };
        clientPair.appClient.createWidget(1, reportingWidget);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.send(("addEnergy " + (("100000" + "\u0000") + "1370-3990-1414-55681")));
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        // a bit upfront
        long now = (System.currentTimeMillis()) + 1500;
        Report report = new Report(1, "DailyReport", new ReportSource[]{ reportSource }, new cc.blynk.server.core.model.widgets.ui.reporting.type.DailyReport(now, ReportDurationType.INFINITE, 0, 0), "test@gmail.com", GraphGranularityType.MINUTE, true, CSV_FILE_PER_DEVICE, null, ZoneId.of("UTC"), 0, 0, null);
        clientPair.appClient.createReport(1, report);
        report = clientPair.appClient.parseReportFromResponse(3);
        Assert.assertNotNull(report);
        Assert.assertEquals(now, report.nextReportAt, 3000);
        Report report2 = new Report(2, "DailyReport2", new ReportSource[]{ reportSource }, new cc.blynk.server.core.model.widgets.ui.reporting.type.DailyReport(now, ReportDurationType.INFINITE, now, now), "test@gmail.com", GraphGranularityType.MINUTE, true, CSV_FILE_PER_DEVICE, null, ZoneId.of("UTC"), 0, 0, null);
        clientPair.appClient.createReport(1, report2);
        report = clientPair.appClient.parseReportFromResponse(4);
        Assert.assertNotNull(report);
        Assert.assertEquals(now, report.nextReportAt, 3000);
        // expecting now is ignored as duration is INFINITE
        Report report3 = new Report(3, "DailyReport3", new ReportSource[]{ reportSource }, new cc.blynk.server.core.model.widgets.ui.reporting.type.DailyReport(now, ReportDurationType.INFINITE, (now + 86400000), (now + 86400000)), "test@gmail.com", GraphGranularityType.MINUTE, true, CSV_FILE_PER_DEVICE, null, ZoneId.of("UTC"), 0, 0, null);
        clientPair.appClient.createReport(1, report3);
        report = clientPair.appClient.parseReportFromResponse(5);
        Assert.assertNotNull(report);
        Assert.assertEquals(System.currentTimeMillis(), report.nextReportAt, 3000);
        // now date is greater than end date, such reports are not accepted.
        Report report4 = new Report(4, "DailyReport4", new ReportSource[]{ reportSource }, new cc.blynk.server.core.model.widgets.ui.reporting.type.DailyReport(now, ReportDurationType.INFINITE, (now + 86400000), now), "test@gmail.com", GraphGranularityType.MINUTE, true, CSV_FILE_PER_DEVICE, null, ZoneId.of("UTC"), 0, 0, null);
        clientPair.appClient.createReport(1, report4);
        clientPair.appClient.verifyResult(TestUtil.illegalCommand(6));
        // trigger date is tomorrow
        Report report5 = new Report(5, "DailyReport5", new ReportSource[]{ reportSource }, new cc.blynk.server.core.model.widgets.ui.reporting.type.DailyReport(now, ReportDurationType.CUSTOM, (now + 86400000), (now + 86400000)), "test@gmail.com", GraphGranularityType.MINUTE, true, CSV_FILE_PER_DEVICE, null, ZoneId.of("UTC"), 0, 0, null);
        clientPair.appClient.createReport(1, report5);
        report = clientPair.appClient.parseReportFromResponse(7);
        Assert.assertNotNull(report);
        Assert.assertEquals((now + 86400000), report.nextReportAt, 3000);
        // report wit the same id is not allowed
        Report report6 = new Report(5, "DailyReport6", new ReportSource[]{ reportSource }, new cc.blynk.server.core.model.widgets.ui.reporting.type.DailyReport(now, ReportDurationType.CUSTOM, (now + 86400000), (now + 86400000)), "test@gmail.com", GraphGranularityType.MINUTE, true, CSV_FILE_PER_DEVICE, null, ZoneId.of("UTC"), 0, 0, null);
        clientPair.appClient.createReport(1, report6);
        clientPair.appClient.verifyResult(TestUtil.illegalCommand(8));
        int tries = 0;
        while (((holder.reportScheduler.getCompletedTaskCount()) < 3) && (tries < 20)) {
            TestUtil.sleep(100);
            tries++;
        } 
        Mockito.verify(holder.mailWrapper, Mockito.never()).sendReportEmail(ArgumentMatchers.eq("test@gmail.com"), ArgumentMatchers.eq("DailyReport"), ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(holder.mailWrapper, Mockito.never()).sendReportEmail(ArgumentMatchers.eq("test@gmail.com"), ArgumentMatchers.eq("DailyReport2"), ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(holder.mailWrapper, Mockito.never()).sendReportEmail(ArgumentMatchers.eq("test@gmail.com"), ArgumentMatchers.eq("DailyReport3"), ArgumentMatchers.any(), ArgumentMatchers.any());
        Assert.assertEquals(3, holder.reportScheduler.getCompletedTaskCount());
        Assert.assertEquals(7, holder.reportScheduler.getTaskCount());
    }

    @Test
    public void testReportIdRemovedFromScheduler() throws Exception {
        ReportDataStream reportDataStream = new ReportDataStream(((short) (1)), PinType.VIRTUAL, "Temperature", true);
        ReportSource reportSource = new cc.blynk.server.core.model.widgets.ui.reporting.source.TileTemplateReportSource(new ReportDataStream[]{ reportDataStream }, 1, new int[]{ 0 });
        ReportingWidget reportingWidget = new ReportingWidget();
        reportingWidget.height = 1;
        reportingWidget.width = 1;
        reportingWidget.reportSources = new ReportSource[]{ reportSource };
        clientPair.appClient.createWidget(1, reportingWidget);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.send(("addEnergy " + (("100000" + "\u0000") + "1370-3990-1414-55681")));
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        // a bit upfront
        long now = (System.currentTimeMillis()) + 1000;
        Report report = new Report(1, "DailyReport", new ReportSource[]{ reportSource }, new cc.blynk.server.core.model.widgets.ui.reporting.type.DailyReport(now, ReportDurationType.INFINITE, 0, 0), "test@gmail.com", GraphGranularityType.MINUTE, true, CSV_FILE_PER_DEVICE, null, ZoneId.of("UTC"), 0, 0, null);
        clientPair.appClient.createReport(1, report);
        report = clientPair.appClient.parseReportFromResponse(3);
        Assert.assertNotNull(report);
        Assert.assertEquals(System.currentTimeMillis(), report.nextReportAt, 3000);
        Report report2 = new Report(2, "DailyReport2", new ReportSource[]{ reportSource }, new cc.blynk.server.core.model.widgets.ui.reporting.type.DailyReport(now, ReportDurationType.INFINITE, now, now), "test@gmail.com", GraphGranularityType.MINUTE, true, CSV_FILE_PER_DEVICE, null, ZoneId.of("UTC"), 0, 0, null);
        clientPair.appClient.createReport(1, report2);
        report2 = clientPair.appClient.parseReportFromResponse(4);
        Assert.assertNotNull(report2);
        Assert.assertEquals(System.currentTimeMillis(), report2.nextReportAt, 3000);
        int tries = 0;
        while (((holder.reportScheduler.getCompletedTaskCount()) < 2) && (tries < 20)) {
            TestUtil.sleep(100);
            tries++;
        } 
        Mockito.verify(holder.mailWrapper, Mockito.never()).sendReportEmail(ArgumentMatchers.eq("test@gmail.com"), ArgumentMatchers.eq("DailyReport"), ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(holder.mailWrapper, Mockito.never()).sendReportEmail(ArgumentMatchers.eq("test@gmail.com"), ArgumentMatchers.eq("DailyReport2"), ArgumentMatchers.any(), ArgumentMatchers.any());
        Assert.assertEquals(2, holder.reportScheduler.getCompletedTaskCount());
        Assert.assertEquals(4, holder.reportScheduler.getTaskCount());
        clientPair.appClient.send("loadProfileGzipped 1");
        DashBoard dashBoard = clientPair.appClient.parseDash(5);
        Assert.assertNotNull(dashBoard);
        reportingWidget = dashBoard.getReportingWidget();
        Assert.assertNotNull(reportingWidget);
        Assert.assertEquals(NO_DATA, reportingWidget.reports[0].lastRunResult);
        Assert.assertEquals(NO_DATA, reportingWidget.reports[1].lastRunResult);
        clientPair.appClient.deleteReport(1, 1);
        clientPair.appClient.verifyResult(TestUtil.ok(6));
        Assert.assertEquals(3, holder.reportScheduler.getTaskCount());
        clientPair.appClient.deleteReport(1, 2);
        clientPair.appClient.verifyResult(TestUtil.ok(7));
        Assert.assertEquals(2, holder.reportScheduler.getCompletedTaskCount());
        Assert.assertEquals(2, holder.reportScheduler.getTaskCount());
        Assert.assertEquals(0, holder.reportScheduler.map.size());
    }

    @Test
    public void testReportIdRemovedFromSchedulerWhenDashIsRemoved() throws Exception {
        ReportDataStream reportDataStream = new ReportDataStream(((short) (1)), PinType.VIRTUAL, "Temperature", true);
        ReportSource reportSource = new cc.blynk.server.core.model.widgets.ui.reporting.source.TileTemplateReportSource(new ReportDataStream[]{ reportDataStream }, 1, new int[]{ 0 });
        ReportingWidget reportingWidget = new ReportingWidget();
        reportingWidget.height = 1;
        reportingWidget.width = 1;
        reportingWidget.reportSources = new ReportSource[]{ reportSource };
        clientPair.appClient.createWidget(1, reportingWidget);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.send(("addEnergy " + (("100000" + "\u0000") + "1370-3990-1414-55681")));
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        // a bit upfront
        long now = (System.currentTimeMillis()) + 1000;
        Report report = new Report(1, "DailyReport", new ReportSource[]{ reportSource }, new cc.blynk.server.core.model.widgets.ui.reporting.type.DailyReport(now, ReportDurationType.INFINITE, 0, 0), "test@gmail.com", GraphGranularityType.MINUTE, true, CSV_FILE_PER_DEVICE, null, ZoneId.of("UTC"), 0, 0, null);
        clientPair.appClient.createReport(1, report);
        report = clientPair.appClient.parseReportFromResponse(3);
        Assert.assertNotNull(report);
        Assert.assertEquals(System.currentTimeMillis(), report.nextReportAt, 3000);
        Report report2 = new Report(2, "DailyReport2", new ReportSource[]{ reportSource }, new cc.blynk.server.core.model.widgets.ui.reporting.type.DailyReport(now, ReportDurationType.INFINITE, now, now), "test@gmail.com", GraphGranularityType.MINUTE, true, CSV_FILE_PER_DEVICE, null, ZoneId.of("UTC"), 0, 0, null);
        clientPair.appClient.createReport(1, report2);
        report2 = clientPair.appClient.parseReportFromResponse(4);
        Assert.assertNotNull(report2);
        Assert.assertEquals(System.currentTimeMillis(), report2.nextReportAt, 3000);
        int tries = 0;
        while (((holder.reportScheduler.getCompletedTaskCount()) < 2) && (tries < 20)) {
            TestUtil.sleep(100);
            tries++;
        } 
        Mockito.verify(holder.mailWrapper, Mockito.never()).sendReportEmail(ArgumentMatchers.eq("test@gmail.com"), ArgumentMatchers.eq("DailyReport"), ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(holder.mailWrapper, Mockito.never()).sendReportEmail(ArgumentMatchers.eq("test@gmail.com"), ArgumentMatchers.eq("DailyReport2"), ArgumentMatchers.any(), ArgumentMatchers.any());
        Assert.assertEquals(2, holder.reportScheduler.getCompletedTaskCount());
        Assert.assertEquals(4, holder.reportScheduler.getTaskCount());
        clientPair.appClient.deleteDash(1);
        clientPair.appClient.verifyResult(TestUtil.ok(5));
        Assert.assertEquals(2, holder.reportScheduler.getCompletedTaskCount());
        Assert.assertEquals(2, holder.reportScheduler.getTaskCount());
        Assert.assertEquals(0, holder.reportScheduler.map.size());
    }

    @Test
    public void testDailyReportWithSinglePointIsTriggeredAndNullName() throws Exception {
        String tempDir = holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        Path pinReportingDataPath10 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, VIRTUAL, ((short) (1)), MINUTE));
        long pointNow = System.currentTimeMillis();
        FileUtils.write(pinReportingDataPath10, 1.11, pointNow);
        ReportDataStream reportDataStream = new ReportDataStream(((short) (1)), PinType.VIRTUAL, "Temperature", true);
        ReportSource reportSource = new cc.blynk.server.core.model.widgets.ui.reporting.source.TileTemplateReportSource(new ReportDataStream[]{ reportDataStream }, 1, new int[]{ 0 });
        ReportingWidget reportingWidget = new ReportingWidget();
        reportingWidget.height = 1;
        reportingWidget.width = 1;
        reportingWidget.reportSources = new ReportSource[]{ reportSource };
        clientPair.appClient.createWidget(1, reportingWidget);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        // a bit upfront
        long now = (System.currentTimeMillis()) + 1000;
        LocalTime localTime = ofInstant(Instant.ofEpochMilli(now), ZoneId.of("UTC"));
        localTime = LocalTime.of(localTime.getHour(), localTime.getMinute());
        Report report = new Report(1, null, new ReportSource[]{ reportSource }, new cc.blynk.server.core.model.widgets.ui.reporting.type.DailyReport(now, ReportDurationType.INFINITE, 0, 0), "test@gmail.com", GraphGranularityType.MINUTE, true, CSV_FILE_PER_DEVICE_PER_PIN, Format.ISO_SIMPLE, ZoneId.of("UTC"), 0, 0, null);
        clientPair.appClient.createReport(1, report);
        report = clientPair.appClient.parseReportFromResponse(2);
        Assert.assertNotNull(report);
        Assert.assertEquals(System.currentTimeMillis(), report.nextReportAt, 5000);
        String date = LocalDate.now(report.tzName).toString();
        String filename = (((((CounterBase.getUserName()) + "_Blynk_") + (report.id)) + "_") + date) + ".zip";
        String downloadUrl = "http://127.0.0.1:18080/" + filename;
        Mockito.verify(holder.mailWrapper, Mockito.timeout(3000)).sendReportEmail(ArgumentMatchers.eq("test@gmail.com"), ArgumentMatchers.eq("Your daily Report is ready"), ArgumentMatchers.eq(downloadUrl), ArgumentMatchers.eq(("Report name: Report<br>Period: Daily, at " + localTime)));
        TestUtil.sleep(200);
        Assert.assertEquals(1, holder.reportScheduler.getCompletedTaskCount());
        Assert.assertEquals(2, holder.reportScheduler.getTaskCount());
        Path result = Paths.get(CSV_DIR, ((((((((CounterBase.getUserName()) + "_") + (AppNameUtil.BLYNK)) + "_") + (report.id)) + "_") + date) + ".zip"));
        Assert.assertTrue(Files.exists(result));
        String resultCsvString = readStringFromFirstZipEntry(result);
        String[] split = resultCsvString.split("[,\n]");
        String nowFormatted = DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.of("UTC")).format(Instant.ofEpochMilli(pointNow));
        Assert.assertEquals(nowFormatted, split[0]);
        Assert.assertEquals(1.11, Double.parseDouble(split[1]), 1.0E-4);
        AsyncHttpClient httpclient = new org.asynchttpclient.DefaultAsyncHttpClient(new DefaultAsyncHttpClientConfig.Builder().setUserAgent(null).setKeepAlive(true).build());
        Future<Response> f = httpclient.prepareGet(downloadUrl).execute();
        Response response = f.get();
        Assert.assertEquals(200, response.getStatusCode());
        Assert.assertEquals("application/zip", response.getContentType());
        httpclient.close();
    }

    @Test
    public void testDailyReportWith24PointsCorrectlyFetched() throws Exception {
        String tempDir = holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        Path pinReportingDataPath10 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, VIRTUAL, ((short) (1)), HOURLY));
        long pointNow = System.currentTimeMillis();
        long pointNowTruncated = (pointNow / (HOURLY.period)) * (HOURLY.period);
        pointNowTruncated -= TimeUnit.DAYS.toMillis(1);
        for (int i = 0; i < 24; i++) {
            FileUtils.write(pinReportingDataPath10, i, (pointNowTruncated + (TimeUnit.HOURS.toMillis(i))));
        }
        ReportDataStream reportDataStream = new ReportDataStream(((short) (1)), PinType.VIRTUAL, "Temperature", true);
        ReportSource reportSource = new cc.blynk.server.core.model.widgets.ui.reporting.source.TileTemplateReportSource(new ReportDataStream[]{ reportDataStream }, 1, new int[]{ 0 });
        ReportingWidget reportingWidget = new ReportingWidget();
        reportingWidget.height = 1;
        reportingWidget.width = 1;
        reportingWidget.reportSources = new ReportSource[]{ reportSource };
        clientPair.appClient.createWidget(1, reportingWidget);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        // a bit upfront
        long now = pointNow + 1000;
        LocalTime localTime = ofInstant(Instant.ofEpochMilli(now), ZoneId.of("UTC"));
        localTime = LocalTime.of(localTime.getHour(), localTime.getMinute());
        Report report = new Report(1, "DailyReport", new ReportSource[]{ reportSource }, new cc.blynk.server.core.model.widgets.ui.reporting.type.DailyReport(now, ReportDurationType.INFINITE, 0, 0), "test@gmail.com", GraphGranularityType.HOURLY, true, CSV_FILE_PER_DEVICE_PER_PIN, Format.ISO_SIMPLE, ZoneId.of("UTC"), 0, 0, null);
        clientPair.appClient.createReport(1, report);
        report = clientPair.appClient.parseReportFromResponse(2);
        Assert.assertNotNull(report);
        Assert.assertEquals(System.currentTimeMillis(), report.nextReportAt, 3500);
        String date = LocalDate.now(report.tzName).toString();
        String filename = (((((CounterBase.getUserName()) + "_Blynk_") + (report.id)) + "_") + date) + ".zip";
        String downloadUrl = "http://127.0.0.1:18080/" + filename;
        Mockito.verify(holder.mailWrapper, Mockito.timeout(3000)).sendReportEmail(ArgumentMatchers.eq("test@gmail.com"), ArgumentMatchers.eq("Your daily DailyReport is ready"), ArgumentMatchers.eq(downloadUrl), ArgumentMatchers.eq(("Report name: DailyReport<br>Period: Daily, at " + localTime)));
        TestUtil.sleep(200);
        Assert.assertEquals(1, holder.reportScheduler.getCompletedTaskCount());
        Assert.assertEquals(2, holder.reportScheduler.getTaskCount());
        Path result = Paths.get(CSV_DIR, ((((((((CounterBase.getUserName()) + "_") + (AppNameUtil.BLYNK)) + "_") + (report.id)) + "_") + date) + ".zip"));
        Assert.assertTrue(Files.exists(result));
        String resultCsvString = readStringFromFirstZipEntry(result);
        String[] split = resultCsvString.split("\n");
        Assert.assertEquals(24, split.length);
    }

    @Test
    public void testFinalFileNameCSVPerDevicePerPin() throws Exception {
        Device device1 = new Device(2, "My Device2 with big name", BoardType.ESP8266);
        clientPair.appClient.createDevice(1, device1);
        String tempDir = holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        Path pinReportingDataPath10 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, VIRTUAL, ((short) (1)), MINUTE));
        long pointNow = System.currentTimeMillis();
        FileUtils.write(pinReportingDataPath10, 1.11, pointNow);
        Path pinReportingDataPath20 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 2, VIRTUAL, ((short) (1)), MINUTE));
        FileUtils.write(pinReportingDataPath20, 1.11, pointNow);
        ReportDataStream reportDataStream = new ReportDataStream(((short) (1)), PinType.VIRTUAL, "Temperature,yes", true);
        ReportSource reportSource = new cc.blynk.server.core.model.widgets.ui.reporting.source.TileTemplateReportSource(new ReportDataStream[]{ reportDataStream }, 1, new int[]{ 0, 2 });
        ReportingWidget reportingWidget = new ReportingWidget();
        reportingWidget.height = 1;
        reportingWidget.width = 1;
        reportingWidget.reportSources = new ReportSource[]{ reportSource };
        clientPair.appClient.createWidget(1, reportingWidget);
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        // a bit upfront
        long now = (System.currentTimeMillis()) + 1000;
        LocalTime localTime = ofInstant(Instant.ofEpochMilli(now), ZoneId.of("UTC"));
        localTime = LocalTime.of(localTime.getHour(), localTime.getMinute());
        Report report = new Report(1, "DailyReport", new ReportSource[]{ reportSource }, new cc.blynk.server.core.model.widgets.ui.reporting.type.DailyReport(now, ReportDurationType.INFINITE, 0, 0), "test@gmail.com", GraphGranularityType.MINUTE, true, CSV_FILE_PER_DEVICE_PER_PIN, Format.ISO_SIMPLE, ZoneId.of("UTC"), 0, 0, null);
        clientPair.appClient.createReport(1, report);
        report = clientPair.appClient.parseReportFromResponse(3);
        Assert.assertNotNull(report);
        Assert.assertEquals(System.currentTimeMillis(), report.nextReportAt, 3000);
        String date = LocalDate.now(report.tzName).toString();
        String filename = (((((CounterBase.getUserName()) + "_Blynk_") + (report.id)) + "_") + date) + ".zip";
        String downloadUrl = "http://127.0.0.1:18080/" + filename;
        Mockito.verify(holder.mailWrapper, Mockito.timeout(3000)).sendReportEmail(ArgumentMatchers.eq("test@gmail.com"), ArgumentMatchers.eq("Your daily DailyReport is ready"), ArgumentMatchers.eq(downloadUrl), ArgumentMatchers.eq(("Report name: DailyReport<br>Period: Daily, at " + localTime)));
        TestUtil.sleep(200);
        Assert.assertEquals(1, holder.reportScheduler.getCompletedTaskCount());
        Assert.assertEquals(2, holder.reportScheduler.getTaskCount());
        Path result = Paths.get(CSV_DIR, ((((((((CounterBase.getUserName()) + "_") + (AppNameUtil.BLYNK)) + "_") + (report.id)) + "_") + date) + ".zip"));
        Assert.assertTrue(Files.exists(result));
        ZipFile zipFile = new ZipFile(result.toString());
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        Assert.assertTrue(entries.hasMoreElements());
        ZipEntry entry = entries.nextElement();
        Assert.assertNotNull(entry);
        Assert.assertEquals("MyDevice_0_Temperatureyes.csv", entry.getName());
        ZipEntry entry2 = entries.nextElement();
        Assert.assertNotNull(entry2);
        Assert.assertEquals("MyDevice2withbig_2_Temperatureyes.csv", entry2.getName());
    }

    @Test
    public void testFinalFileNameCSVPerDevice() throws Exception {
        Device device1 = new Device(2, "My Device2 with big name", BoardType.ESP8266);
        clientPair.appClient.createDevice(1, device1);
        String tempDir = holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        Path pinReportingDataPath10 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, VIRTUAL, ((short) (1)), MINUTE));
        long pointNow = System.currentTimeMillis();
        FileUtils.write(pinReportingDataPath10, 1.11, pointNow);
        Path pinReportingDataPath20 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 2, VIRTUAL, ((short) (1)), MINUTE));
        FileUtils.write(pinReportingDataPath20, 1.11, pointNow);
        ReportDataStream reportDataStream = new ReportDataStream(((short) (1)), PinType.VIRTUAL, null, true);
        ReportSource reportSource = new cc.blynk.server.core.model.widgets.ui.reporting.source.TileTemplateReportSource(new ReportDataStream[]{ reportDataStream }, 1, new int[]{ 0, 2 });
        ReportingWidget reportingWidget = new ReportingWidget();
        reportingWidget.height = 1;
        reportingWidget.width = 1;
        reportingWidget.reportSources = new ReportSource[]{ reportSource };
        clientPair.appClient.createWidget(1, reportingWidget);
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        // a bit upfront
        long now = (System.currentTimeMillis()) + 1000;
        LocalTime localTime = ofInstant(Instant.ofEpochMilli(now), ZoneId.of("UTC"));
        localTime = LocalTime.of(localTime.getHour(), localTime.getMinute());
        Report report = new Report(1, "DailyReport", new ReportSource[]{ reportSource }, new cc.blynk.server.core.model.widgets.ui.reporting.type.DailyReport(now, ReportDurationType.INFINITE, 0, 0), "test@gmail.com", GraphGranularityType.MINUTE, true, CSV_FILE_PER_DEVICE, Format.ISO_SIMPLE, ZoneId.of("UTC"), 0, 0, null);
        clientPair.appClient.createReport(1, report);
        report = clientPair.appClient.parseReportFromResponse(3);
        Assert.assertNotNull(report);
        Assert.assertEquals(System.currentTimeMillis(), report.nextReportAt, 3000);
        String date = LocalDate.now(report.tzName).toString();
        String filename = (((((CounterBase.getUserName()) + "_Blynk_") + (report.id)) + "_") + date) + ".zip";
        String downloadUrl = "http://127.0.0.1:18080/" + filename;
        Mockito.verify(holder.mailWrapper, Mockito.timeout(3000)).sendReportEmail(ArgumentMatchers.eq("test@gmail.com"), ArgumentMatchers.eq("Your daily DailyReport is ready"), ArgumentMatchers.eq(downloadUrl), ArgumentMatchers.eq(("Report name: DailyReport<br>Period: Daily, at " + localTime)));
        TestUtil.sleep(200);
        Assert.assertEquals(1, holder.reportScheduler.getCompletedTaskCount());
        Assert.assertEquals(2, holder.reportScheduler.getTaskCount());
        Path result = Paths.get(CSV_DIR, ((((((((CounterBase.getUserName()) + "_") + (AppNameUtil.BLYNK)) + "_") + (report.id)) + "_") + date) + ".zip"));
        Assert.assertTrue(Files.exists(result));
        ZipFile zipFile = new ZipFile(result.toString());
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        Assert.assertTrue(entries.hasMoreElements());
        ZipEntry entry = entries.nextElement();
        Assert.assertNotNull(entry);
        Assert.assertEquals("MyDevice_0.csv", entry.getName());
        ZipEntry entry2 = entries.nextElement();
        Assert.assertNotNull(entry2);
        Assert.assertEquals("MyDevice2withbig_2.csv", entry2.getName());
        String resultCsvString = readStringFromFirstZipEntry(result);
        Assert.assertNotNull(resultCsvString);
        String nowFormatted = DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.of("UTC")).format(Instant.ofEpochMilli(pointNow));
        Assert.assertEquals(resultCsvString, (nowFormatted + ",v1,1.11\n"));
    }

    @Test
    public void testFinalFileNameCSVPerDeviceUtf8() throws Exception {
        Device device1 = new Device(2, "My Device2 with big name", BoardType.ESP8266);
        clientPair.appClient.createDevice(1, device1);
        String tempDir = holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        Path pinReportingDataPath10 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, VIRTUAL, ((short) (1)), MINUTE));
        long pointNow = System.currentTimeMillis();
        FileUtils.write(pinReportingDataPath10, 1.11, pointNow);
        Path pinReportingDataPath20 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 2, VIRTUAL, ((short) (1)), MINUTE));
        FileUtils.write(pinReportingDataPath20, 1.11, pointNow);
        ReportDataStream reportDataStream = new ReportDataStream(((short) (1)), PinType.VIRTUAL, "??? ?????????", true);
        ReportSource reportSource = new cc.blynk.server.core.model.widgets.ui.reporting.source.TileTemplateReportSource(new ReportDataStream[]{ reportDataStream }, 1, new int[]{ 0, 2 });
        ReportingWidget reportingWidget = new ReportingWidget();
        reportingWidget.height = 1;
        reportingWidget.width = 1;
        reportingWidget.reportSources = new ReportSource[]{ reportSource };
        clientPair.appClient.createWidget(1, reportingWidget);
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        // a bit upfront
        long now = (System.currentTimeMillis()) + 1000;
        LocalTime localTime = ofInstant(Instant.ofEpochMilli(now), ZoneId.of("UTC"));
        localTime = LocalTime.of(localTime.getHour(), localTime.getMinute());
        Report report = new Report(1, "DailyReport", new ReportSource[]{ reportSource }, new cc.blynk.server.core.model.widgets.ui.reporting.type.DailyReport(now, ReportDurationType.INFINITE, 0, 0), "test@gmail.com", GraphGranularityType.MINUTE, true, CSV_FILE_PER_DEVICE, Format.ISO_SIMPLE, ZoneId.of("UTC"), 0, 0, null);
        clientPair.appClient.createReport(1, report);
        report = clientPair.appClient.parseReportFromResponse(3);
        Assert.assertNotNull(report);
        Assert.assertEquals(System.currentTimeMillis(), report.nextReportAt, 3000);
        String date = LocalDate.now(report.tzName).toString();
        String filename = (((((CounterBase.getUserName()) + "_Blynk_") + (report.id)) + "_") + date) + ".zip";
        String downloadUrl = "http://127.0.0.1:18080/" + filename;
        Mockito.verify(holder.mailWrapper, Mockito.timeout(3000)).sendReportEmail(ArgumentMatchers.eq("test@gmail.com"), ArgumentMatchers.eq("Your daily DailyReport is ready"), ArgumentMatchers.eq(downloadUrl), ArgumentMatchers.eq(("Report name: DailyReport<br>Period: Daily, at " + localTime)));
        TestUtil.sleep(200);
        Assert.assertEquals(1, holder.reportScheduler.getCompletedTaskCount());
        Assert.assertEquals(2, holder.reportScheduler.getTaskCount());
        Path result = Paths.get(CSV_DIR, ((((((((CounterBase.getUserName()) + "_") + (AppNameUtil.BLYNK)) + "_") + (report.id)) + "_") + date) + ".zip"));
        Assert.assertTrue(Files.exists(result));
        ZipFile zipFile = new ZipFile(result.toString());
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        Assert.assertTrue(entries.hasMoreElements());
        ZipEntry entry = entries.nextElement();
        Assert.assertNotNull(entry);
        Assert.assertEquals("MyDevice_0.csv", entry.getName());
        ZipEntry entry2 = entries.nextElement();
        Assert.assertNotNull(entry2);
        Assert.assertEquals("MyDevice2withbig_2.csv", entry2.getName());
        String resultCsvString = readStringFromFirstZipEntry(result);
        Assert.assertNotNull(resultCsvString);
        String nowFormatted = DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.of("UTC")).format(Instant.ofEpochMilli(pointNow));
        Assert.assertEquals((nowFormatted + ",\u041c\u0456\u0439 \u0434\u0430\u0442\u0430\u0441\u0442\u0440\u0456\u043c,1.11\n"), resultCsvString);
    }

    @Test
    public void testFinalFileNameCSVPerDevice2() throws Exception {
        Device device1 = new Device(2, "My Device2 with big name", BoardType.ESP8266);
        clientPair.appClient.createDevice(1, device1);
        String tempDir = holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        Path pinReportingDataPath10 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, VIRTUAL, ((short) (1)), MINUTE));
        Path pinReportingDataPath20 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, VIRTUAL, ((short) (2)), MINUTE));
        long pointNow = System.currentTimeMillis();
        FileUtils.write(pinReportingDataPath10, 1.11, pointNow);
        FileUtils.write(pinReportingDataPath20, 1.12, pointNow);
        Path pinReportingDataPath12 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 2, VIRTUAL, ((short) (1)), MINUTE));
        Path pinReportingDataPath22 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 2, VIRTUAL, ((short) (2)), MINUTE));
        FileUtils.write(pinReportingDataPath12, 1.13, pointNow);
        FileUtils.write(pinReportingDataPath22, 1.14, pointNow);
        ReportDataStream reportDataStream = new ReportDataStream(((short) (1)), PinType.VIRTUAL, null, true);
        ReportDataStream reportDataStream2 = new ReportDataStream(((short) (2)), PinType.VIRTUAL, null, true);
        ReportSource reportSource = new cc.blynk.server.core.model.widgets.ui.reporting.source.TileTemplateReportSource(new ReportDataStream[]{ reportDataStream, reportDataStream2 }, 1, new int[]{ 0, 2 });
        ReportingWidget reportingWidget = new ReportingWidget();
        reportingWidget.height = 1;
        reportingWidget.width = 1;
        reportingWidget.reportSources = new ReportSource[]{ reportSource };
        clientPair.appClient.createWidget(1, reportingWidget);
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        // a bit upfront
        long now = (System.currentTimeMillis()) + 1000;
        LocalTime localTime = ofInstant(Instant.ofEpochMilli(now), ZoneId.of("UTC"));
        localTime = LocalTime.of(localTime.getHour(), localTime.getMinute());
        Report report = new Report(1, "DailyReport", new ReportSource[]{ reportSource }, new cc.blynk.server.core.model.widgets.ui.reporting.type.DailyReport(now, ReportDurationType.INFINITE, 0, 0), "test@gmail.com", GraphGranularityType.MINUTE, true, CSV_FILE_PER_DEVICE, Format.ISO_SIMPLE, ZoneId.of("UTC"), 0, 0, null);
        clientPair.appClient.createReport(1, report);
        report = clientPair.appClient.parseReportFromResponse(3);
        Assert.assertNotNull(report);
        Assert.assertEquals(System.currentTimeMillis(), report.nextReportAt, 3000);
        String date = LocalDate.now(report.tzName).toString();
        String filename = (((((CounterBase.getUserName()) + "_Blynk_") + (report.id)) + "_") + date) + ".zip";
        String downloadUrl = "http://127.0.0.1:18080/" + filename;
        Mockito.verify(holder.mailWrapper, Mockito.timeout(3000)).sendReportEmail(ArgumentMatchers.eq("test@gmail.com"), ArgumentMatchers.eq("Your daily DailyReport is ready"), ArgumentMatchers.eq(downloadUrl), ArgumentMatchers.eq(("Report name: DailyReport<br>Period: Daily, at " + localTime)));
        TestUtil.sleep(200);
        Assert.assertEquals(1, holder.reportScheduler.getCompletedTaskCount());
        Assert.assertEquals(2, holder.reportScheduler.getTaskCount());
        Path result = Paths.get(CSV_DIR, ((((((((CounterBase.getUserName()) + "_") + (AppNameUtil.BLYNK)) + "_") + (report.id)) + "_") + date) + ".zip"));
        Assert.assertTrue(Files.exists(result));
        ZipFile zipFile = new ZipFile(result.toString());
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        Assert.assertTrue(entries.hasMoreElements());
        ZipEntry entry = entries.nextElement();
        Assert.assertNotNull(entry);
        Assert.assertEquals("MyDevice_0.csv", entry.getName());
        ZipEntry entry2 = entries.nextElement();
        Assert.assertNotNull(entry2);
        Assert.assertEquals("MyDevice2withbig_2.csv", entry2.getName());
        String nowFormatted = DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.of("UTC")).format(Instant.ofEpochMilli(pointNow));
        String resultCsvString = readStringFromZipEntry(zipFile, entry);
        Assert.assertNotNull(resultCsvString);
        Assert.assertEquals(resultCsvString, (((nowFormatted + ",v1,1.11\n") + nowFormatted) + ",v2,1.12\n"));
        String resultCsvString2 = readStringFromZipEntry(zipFile, entry2);
        Assert.assertNotNull(resultCsvString2);
        Assert.assertEquals(resultCsvString2, (((nowFormatted + ",v1,1.13\n") + nowFormatted) + ",v2,1.14\n"));
    }

    @Test
    public void testFinalFileNameCSVPerDeviceUnicode() throws Exception {
        Device device1 = new Device(2, "??? ??????", BoardType.ESP8266);
        clientPair.appClient.createDevice(1, device1);
        String tempDir = holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        long pointNow = System.currentTimeMillis();
        Path pinReportingDataPath12 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 2, VIRTUAL, ((short) (1)), MINUTE));
        Path pinReportingDataPath22 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 2, VIRTUAL, ((short) (2)), MINUTE));
        FileUtils.write(pinReportingDataPath12, 1.13, pointNow);
        FileUtils.write(pinReportingDataPath22, 1.14, pointNow);
        ReportDataStream reportDataStream = new ReportDataStream(((short) (1)), PinType.VIRTUAL, null, true);
        ReportDataStream reportDataStream2 = new ReportDataStream(((short) (2)), PinType.VIRTUAL, null, true);
        ReportSource reportSource = new cc.blynk.server.core.model.widgets.ui.reporting.source.TileTemplateReportSource(new ReportDataStream[]{ reportDataStream, reportDataStream2 }, 1, new int[]{ 2 });
        ReportingWidget reportingWidget = new ReportingWidget();
        reportingWidget.height = 1;
        reportingWidget.width = 1;
        reportingWidget.reportSources = new ReportSource[]{ reportSource };
        clientPair.appClient.createWidget(1, reportingWidget);
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        // a bit upfront
        long now = (System.currentTimeMillis()) + 1000;
        LocalTime localTime = ofInstant(Instant.ofEpochMilli(now), ZoneId.of("UTC"));
        localTime = LocalTime.of(localTime.getHour(), localTime.getMinute());
        Report report = new Report(1, "DailyReport", new ReportSource[]{ reportSource }, new cc.blynk.server.core.model.widgets.ui.reporting.type.DailyReport(now, ReportDurationType.INFINITE, 0, 0), "test@gmail.com", GraphGranularityType.MINUTE, true, CSV_FILE_PER_DEVICE, Format.ISO_SIMPLE, ZoneId.of("UTC"), 0, 0, null);
        clientPair.appClient.createReport(1, report);
        report = clientPair.appClient.parseReportFromResponse(3);
        Assert.assertNotNull(report);
        Assert.assertEquals(System.currentTimeMillis(), report.nextReportAt, 3000);
        String date = LocalDate.now(report.tzName).toString();
        String filename = (((((CounterBase.getUserName()) + "_Blynk_") + (report.id)) + "_") + date) + ".zip";
        String downloadUrl = "http://127.0.0.1:18080/" + filename;
        Mockito.verify(holder.mailWrapper, Mockito.timeout(3000)).sendReportEmail(ArgumentMatchers.eq("test@gmail.com"), ArgumentMatchers.eq("Your daily DailyReport is ready"), ArgumentMatchers.eq(downloadUrl), ArgumentMatchers.eq(("Report name: DailyReport<br>Period: Daily, at " + localTime)));
        TestUtil.sleep(200);
        Assert.assertEquals(1, holder.reportScheduler.getCompletedTaskCount());
        Assert.assertEquals(2, holder.reportScheduler.getTaskCount());
        Path result = Paths.get(CSV_DIR, ((((((((CounterBase.getUserName()) + "_") + (AppNameUtil.BLYNK)) + "_") + (report.id)) + "_") + date) + ".zip"));
        Assert.assertTrue(Files.exists(result));
        ZipFile zipFile = new ZipFile(result.toString());
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        Assert.assertTrue(entries.hasMoreElements());
        ZipEntry entry = entries.nextElement();
        Assert.assertNotNull(entry);
        Assert.assertEquals("?????????_2.csv", entry.getName());
        String nowFormatted = DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.of("UTC")).format(Instant.ofEpochMilli(pointNow));
        String resultCsvString = readStringFromZipEntry(zipFile, entry);
        Assert.assertNotNull(resultCsvString);
        Assert.assertEquals((((nowFormatted + ",v1,1.13\n") + nowFormatted) + ",v2,1.14\n"), resultCsvString);
    }

    @Test
    public void testFinalFileNameCSVPerDevice2DataStreamWithName() throws Exception {
        Device device1 = new Device(2, "My Device2 with big name", BoardType.ESP8266);
        clientPair.appClient.createDevice(1, device1);
        String tempDir = holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        Path pinReportingDataPath10 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, VIRTUAL, ((short) (1)), MINUTE));
        Path pinReportingDataPath20 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, VIRTUAL, ((short) (2)), MINUTE));
        long pointNow = System.currentTimeMillis();
        FileUtils.write(pinReportingDataPath10, 1.11, pointNow);
        FileUtils.write(pinReportingDataPath20, 1.12, pointNow);
        Path pinReportingDataPath12 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 2, VIRTUAL, ((short) (1)), MINUTE));
        Path pinReportingDataPath22 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 2, VIRTUAL, ((short) (2)), MINUTE));
        FileUtils.write(pinReportingDataPath12, 1.13, pointNow);
        FileUtils.write(pinReportingDataPath22, 1.14, pointNow);
        ReportDataStream reportDataStream = new ReportDataStream(((short) (1)), PinType.VIRTUAL, "Temperature", true);
        ReportDataStream reportDataStream2 = new ReportDataStream(((short) (2)), PinType.VIRTUAL, "Humidity", true);
        ReportSource reportSource = new cc.blynk.server.core.model.widgets.ui.reporting.source.TileTemplateReportSource(new ReportDataStream[]{ reportDataStream, reportDataStream2 }, 1, new int[]{ 0, 2 });
        ReportingWidget reportingWidget = new ReportingWidget();
        reportingWidget.height = 1;
        reportingWidget.width = 1;
        reportingWidget.reportSources = new ReportSource[]{ reportSource };
        clientPair.appClient.createWidget(1, reportingWidget);
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        // a bit upfront
        long now = (System.currentTimeMillis()) + 1000;
        LocalTime localTime = ofInstant(Instant.ofEpochMilli(now), ZoneId.of("UTC"));
        localTime = LocalTime.of(localTime.getHour(), localTime.getMinute());
        Report report = new Report(1, "DailyReport", new ReportSource[]{ reportSource }, new cc.blynk.server.core.model.widgets.ui.reporting.type.DailyReport(now, ReportDurationType.INFINITE, 0, 0), "test@gmail.com", GraphGranularityType.MINUTE, true, CSV_FILE_PER_DEVICE, Format.ISO_SIMPLE, ZoneId.of("UTC"), 0, 0, null);
        clientPair.appClient.createReport(1, report);
        report = clientPair.appClient.parseReportFromResponse(3);
        Assert.assertNotNull(report);
        Assert.assertEquals(System.currentTimeMillis(), report.nextReportAt, 2000);
        String date = LocalDate.now(report.tzName).toString();
        String filename = (((((CounterBase.getUserName()) + "_Blynk_") + (report.id)) + "_") + date) + ".zip";
        String downloadUrl = "http://127.0.0.1:18080/" + filename;
        Mockito.verify(holder.mailWrapper, Mockito.timeout(3000)).sendReportEmail(ArgumentMatchers.eq("test@gmail.com"), ArgumentMatchers.eq("Your daily DailyReport is ready"), ArgumentMatchers.eq(downloadUrl), ArgumentMatchers.eq(("Report name: DailyReport<br>Period: Daily, at " + localTime)));
        TestUtil.sleep(200);
        Assert.assertEquals(1, holder.reportScheduler.getCompletedTaskCount());
        Assert.assertEquals(2, holder.reportScheduler.getTaskCount());
        Path result = Paths.get(CSV_DIR, ((((((((CounterBase.getUserName()) + "_") + (AppNameUtil.BLYNK)) + "_") + (report.id)) + "_") + date) + ".zip"));
        Assert.assertTrue(Files.exists(result));
        ZipFile zipFile = new ZipFile(result.toString());
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        Assert.assertTrue(entries.hasMoreElements());
        ZipEntry entry = entries.nextElement();
        Assert.assertNotNull(entry);
        Assert.assertEquals("MyDevice_0.csv", entry.getName());
        ZipEntry entry2 = entries.nextElement();
        Assert.assertNotNull(entry2);
        Assert.assertEquals("MyDevice2withbig_2.csv", entry2.getName());
        String nowFormatted = DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.of("UTC")).format(Instant.ofEpochMilli(pointNow));
        String resultCsvString = readStringFromZipEntry(zipFile, entry);
        Assert.assertNotNull(resultCsvString);
        Assert.assertEquals(resultCsvString, (((nowFormatted + ",Temperature,1.11\n") + nowFormatted) + ",Humidity,1.12\n"));
        String resultCsvString2 = readStringFromZipEntry(zipFile, entry2);
        Assert.assertNotNull(resultCsvString2);
        Assert.assertEquals(resultCsvString2, (((nowFormatted + ",Temperature,1.13\n") + nowFormatted) + ",Humidity,1.14\n"));
    }

    @Test
    public void testFinalFileNameCSVMerged2DataStreamWithName() throws Exception {
        Device device1 = new Device(2, "My Device2 with big name", BoardType.ESP8266);
        clientPair.appClient.createDevice(1, device1);
        String tempDir = holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        Path pinReportingDataPath10 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, VIRTUAL, ((short) (1)), MINUTE));
        Path pinReportingDataPath20 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, VIRTUAL, ((short) (2)), MINUTE));
        long pointNow = System.currentTimeMillis();
        FileUtils.write(pinReportingDataPath10, 1.11, pointNow);
        FileUtils.write(pinReportingDataPath20, 1.12, pointNow);
        Path pinReportingDataPath12 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 2, VIRTUAL, ((short) (1)), MINUTE));
        Path pinReportingDataPath22 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 2, VIRTUAL, ((short) (2)), MINUTE));
        FileUtils.write(pinReportingDataPath12, 1.13, pointNow);
        FileUtils.write(pinReportingDataPath22, 1.14, pointNow);
        ReportDataStream reportDataStream = new ReportDataStream(((short) (1)), PinType.VIRTUAL, "Temperature", true);
        ReportDataStream reportDataStream2 = new ReportDataStream(((short) (2)), PinType.VIRTUAL, "Humidity", true);
        ReportSource reportSource = new cc.blynk.server.core.model.widgets.ui.reporting.source.TileTemplateReportSource(new ReportDataStream[]{ reportDataStream, reportDataStream2 }, 1, new int[]{ 0, 2 });
        ReportingWidget reportingWidget = new ReportingWidget();
        reportingWidget.height = 1;
        reportingWidget.width = 1;
        reportingWidget.reportSources = new ReportSource[]{ reportSource };
        clientPair.appClient.createWidget(1, reportingWidget);
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        // a bit upfront
        long now = (System.currentTimeMillis()) + 1000;
        LocalTime localTime = ofInstant(Instant.ofEpochMilli(now), ZoneId.of("UTC"));
        localTime = LocalTime.of(localTime.getHour(), localTime.getMinute());
        Report report = new Report(1, "DailyReport", new ReportSource[]{ reportSource }, new cc.blynk.server.core.model.widgets.ui.reporting.type.DailyReport(now, ReportDurationType.INFINITE, 0, 0), "test@gmail.com", GraphGranularityType.MINUTE, true, MERGED_CSV, Format.ISO_SIMPLE, ZoneId.of("UTC"), 0, 0, null);
        clientPair.appClient.createReport(1, report);
        report = clientPair.appClient.parseReportFromResponse(3);
        Assert.assertNotNull(report);
        Assert.assertEquals(System.currentTimeMillis(), report.nextReportAt, 2000);
        String date = LocalDate.now(report.tzName).toString();
        String filename = (((((CounterBase.getUserName()) + "_Blynk_") + (report.id)) + "_") + date) + ".zip";
        String downloadUrl = "http://127.0.0.1:18080/" + filename;
        Mockito.verify(holder.mailWrapper, Mockito.timeout(3000)).sendReportEmail(ArgumentMatchers.eq("test@gmail.com"), ArgumentMatchers.eq("Your daily DailyReport is ready"), ArgumentMatchers.eq(downloadUrl), ArgumentMatchers.eq(("Report name: DailyReport<br>Period: Daily, at " + localTime)));
        TestUtil.sleep(200);
        Assert.assertEquals(1, holder.reportScheduler.getCompletedTaskCount());
        Assert.assertEquals(2, holder.reportScheduler.getTaskCount());
        Path result = Paths.get(CSV_DIR, ((((((((CounterBase.getUserName()) + "_") + (AppNameUtil.BLYNK)) + "_") + (report.id)) + "_") + date) + ".zip"));
        Assert.assertTrue(Files.exists(result));
        ZipFile zipFile = new ZipFile(result.toString());
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        Assert.assertTrue(entries.hasMoreElements());
        ZipEntry entry = entries.nextElement();
        Assert.assertNotNull(entry);
        Assert.assertEquals("DailyReport.csv", entry.getName());
        String nowFormatted = DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.of("UTC")).format(Instant.ofEpochMilli(pointNow));
        String resultCsvString = readStringFromZipEntry(zipFile, entry);
        Assert.assertNotNull(resultCsvString);
        Assert.assertEquals(resultCsvString, (((((((nowFormatted + ",Temperature,My Device,1.11\n") + nowFormatted) + ",Humidity,My Device,1.12\n") + nowFormatted) + ",Temperature,My Device2 with big name,1.13\n") + nowFormatted) + ",Humidity,My Device2 with big name,1.14\n"));
    }

    @Test
    public void testFinalFileNameCSVMerged2DataStreamWithNameCorrectEscaping() throws Exception {
        Device device1 = new Device(2, "My Device2 with \"big\" name", BoardType.ESP8266);
        clientPair.appClient.createDevice(1, device1);
        String tempDir = holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        Path pinReportingDataPath10 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, VIRTUAL, ((short) (1)), MINUTE));
        Path pinReportingDataPath20 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, VIRTUAL, ((short) (2)), MINUTE));
        long pointNow = System.currentTimeMillis();
        FileUtils.write(pinReportingDataPath10, 1.11, pointNow);
        FileUtils.write(pinReportingDataPath20, 1.12, pointNow);
        Path pinReportingDataPath12 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 2, VIRTUAL, ((short) (1)), MINUTE));
        Path pinReportingDataPath22 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 2, VIRTUAL, ((short) (2)), MINUTE));
        FileUtils.write(pinReportingDataPath12, 1.13, pointNow);
        FileUtils.write(pinReportingDataPath22, 1.14, pointNow);
        ReportDataStream reportDataStream = new ReportDataStream(((short) (1)), PinType.VIRTUAL, "Temperature", true);
        ReportDataStream reportDataStream2 = new ReportDataStream(((short) (2)), PinType.VIRTUAL, "Humidity", true);
        ReportSource reportSource = new cc.blynk.server.core.model.widgets.ui.reporting.source.TileTemplateReportSource(new ReportDataStream[]{ reportDataStream, reportDataStream2 }, 1, new int[]{ 0, 2 });
        ReportingWidget reportingWidget = new ReportingWidget();
        reportingWidget.height = 1;
        reportingWidget.width = 1;
        reportingWidget.reportSources = new ReportSource[]{ reportSource };
        clientPair.appClient.createWidget(1, reportingWidget);
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        // a bit upfront
        long now = (System.currentTimeMillis()) + 1000;
        LocalTime localTime = ofInstant(Instant.ofEpochMilli(now), ZoneId.of("UTC"));
        localTime = LocalTime.of(localTime.getHour(), localTime.getMinute());
        Report report = new Report(1, "DailyReport", new ReportSource[]{ reportSource }, new cc.blynk.server.core.model.widgets.ui.reporting.type.DailyReport(now, ReportDurationType.INFINITE, 0, 0), "test@gmail.com", GraphGranularityType.MINUTE, true, MERGED_CSV, Format.ISO_SIMPLE, ZoneId.of("UTC"), 0, 0, null);
        clientPair.appClient.createReport(1, report);
        report = clientPair.appClient.parseReportFromResponse(3);
        Assert.assertNotNull(report);
        Assert.assertEquals(System.currentTimeMillis(), report.nextReportAt, 2000);
        String date = LocalDate.now(report.tzName).toString();
        String filename = (((((CounterBase.getUserName()) + "_Blynk_") + (report.id)) + "_") + date) + ".zip";
        String downloadUrl = "http://127.0.0.1:18080/" + filename;
        Mockito.verify(holder.mailWrapper, Mockito.timeout(3000)).sendReportEmail(ArgumentMatchers.eq("test@gmail.com"), ArgumentMatchers.eq("Your daily DailyReport is ready"), ArgumentMatchers.eq(downloadUrl), ArgumentMatchers.eq(("Report name: DailyReport<br>Period: Daily, at " + localTime)));
        TestUtil.sleep(200);
        Assert.assertEquals(1, holder.reportScheduler.getCompletedTaskCount());
        Assert.assertEquals(2, holder.reportScheduler.getTaskCount());
        Path result = Paths.get(CSV_DIR, ((((((((CounterBase.getUserName()) + "_") + (AppNameUtil.BLYNK)) + "_") + (report.id)) + "_") + date) + ".zip"));
        Assert.assertTrue(Files.exists(result));
        ZipFile zipFile = new ZipFile(result.toString());
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        Assert.assertTrue(entries.hasMoreElements());
        ZipEntry entry = entries.nextElement();
        Assert.assertNotNull(entry);
        Assert.assertEquals("DailyReport.csv", entry.getName());
        String nowFormatted = DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.of("UTC")).format(Instant.ofEpochMilli(pointNow));
        String resultCsvString = readStringFromZipEntry(zipFile, entry);
        Assert.assertNotNull(resultCsvString);
        Assert.assertEquals(resultCsvString, (((((((nowFormatted + ",Temperature,My Device,1.11\n") + nowFormatted) + ",Humidity,My Device,1.12\n") + nowFormatted) + ",Temperature,\"My Device2 with \"\"big\"\" name\",1.13\n") + nowFormatted) + ",Humidity,\"My Device2 with \"\"big\"\" name\",1.14\n"));
    }

    @Test
    public void testFinalFileNameCSVMerged2DataStreamWithNameCorrectEscaping2() throws Exception {
        Device device1 = new Device(2, "My Device2 with, big name", BoardType.ESP8266);
        clientPair.appClient.createDevice(1, device1);
        String tempDir = holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        Path pinReportingDataPath10 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, VIRTUAL, ((short) (1)), MINUTE));
        Path pinReportingDataPath20 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, VIRTUAL, ((short) (2)), MINUTE));
        long pointNow = System.currentTimeMillis();
        FileUtils.write(pinReportingDataPath10, 1.11, pointNow);
        FileUtils.write(pinReportingDataPath20, 1.12, pointNow);
        Path pinReportingDataPath12 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 2, VIRTUAL, ((short) (1)), MINUTE));
        Path pinReportingDataPath22 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 2, VIRTUAL, ((short) (2)), MINUTE));
        FileUtils.write(pinReportingDataPath12, 1.13, pointNow);
        FileUtils.write(pinReportingDataPath22, 1.14, pointNow);
        ReportDataStream reportDataStream = new ReportDataStream(((short) (1)), PinType.VIRTUAL, "Temperature", true);
        ReportDataStream reportDataStream2 = new ReportDataStream(((short) (2)), PinType.VIRTUAL, "Humidity", true);
        ReportSource reportSource = new cc.blynk.server.core.model.widgets.ui.reporting.source.TileTemplateReportSource(new ReportDataStream[]{ reportDataStream, reportDataStream2 }, 1, new int[]{ 0, 2 });
        ReportingWidget reportingWidget = new ReportingWidget();
        reportingWidget.height = 1;
        reportingWidget.width = 1;
        reportingWidget.reportSources = new ReportSource[]{ reportSource };
        clientPair.appClient.createWidget(1, reportingWidget);
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        // a bit upfront
        long now = (System.currentTimeMillis()) + 1000;
        LocalTime localTime = ofInstant(Instant.ofEpochMilli(now), ZoneId.of("UTC"));
        localTime = LocalTime.of(localTime.getHour(), localTime.getMinute());
        Report report = new Report(1, "DailyReport", new ReportSource[]{ reportSource }, new cc.blynk.server.core.model.widgets.ui.reporting.type.DailyReport(now, ReportDurationType.INFINITE, 0, 0), "test@gmail.com", GraphGranularityType.MINUTE, true, MERGED_CSV, Format.ISO_SIMPLE, ZoneId.of("UTC"), 0, 0, null);
        clientPair.appClient.createReport(1, report);
        report = clientPair.appClient.parseReportFromResponse(3);
        Assert.assertNotNull(report);
        Assert.assertEquals(System.currentTimeMillis(), report.nextReportAt, 5000);
        String date = LocalDate.now(report.tzName).toString();
        String filename = (((((CounterBase.getUserName()) + "_Blynk_") + (report.id)) + "_") + date) + ".zip";
        String downloadUrl = "http://127.0.0.1:18080/" + filename;
        Mockito.verify(holder.mailWrapper, Mockito.timeout(3000)).sendReportEmail(ArgumentMatchers.eq("test@gmail.com"), ArgumentMatchers.eq("Your daily DailyReport is ready"), ArgumentMatchers.eq(downloadUrl), ArgumentMatchers.eq(("Report name: DailyReport<br>Period: Daily, at " + localTime)));
        TestUtil.sleep(200);
        Assert.assertEquals(1, holder.reportScheduler.getCompletedTaskCount());
        Assert.assertEquals(2, holder.reportScheduler.getTaskCount());
        Path result = Paths.get(CSV_DIR, ((((((((CounterBase.getUserName()) + "_") + (AppNameUtil.BLYNK)) + "_") + (report.id)) + "_") + date) + ".zip"));
        Assert.assertTrue(Files.exists(result));
        ZipFile zipFile = new ZipFile(result.toString());
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        Assert.assertTrue(entries.hasMoreElements());
        ZipEntry entry = entries.nextElement();
        Assert.assertNotNull(entry);
        Assert.assertEquals("DailyReport.csv", entry.getName());
        String nowFormatted = DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.of("UTC")).format(Instant.ofEpochMilli(pointNow));
        String resultCsvString = readStringFromZipEntry(zipFile, entry);
        Assert.assertNotNull(resultCsvString);
        Assert.assertEquals(resultCsvString, (((((((nowFormatted + ",Temperature,My Device,1.11\n") + nowFormatted) + ",Humidity,My Device,1.12\n") + nowFormatted) + ",Temperature,\"My Device2 with, big name\",1.13\n") + nowFormatted) + ",Humidity,\"My Device2 with, big name\",1.14\n"));
    }

    @Test
    public void testDailyReportWithSinglePointIsTriggeredAndExpired() throws Exception {
        String tempDir = holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        Path pinReportingDataPath10 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, VIRTUAL, ((short) (1)), MINUTE));
        long pointNow = System.currentTimeMillis();
        FileUtils.write(pinReportingDataPath10, 1.11, pointNow);
        ReportDataStream reportDataStream = new ReportDataStream(((short) (1)), PinType.VIRTUAL, null, true);
        ReportSource reportSource = new cc.blynk.server.core.model.widgets.ui.reporting.source.TileTemplateReportSource(new ReportDataStream[]{ reportDataStream }, 1, new int[]{ 0 });
        ReportingWidget reportingWidget = new ReportingWidget();
        reportingWidget.id = 222222;
        reportingWidget.height = 1;
        reportingWidget.width = 1;
        reportingWidget.reportSources = new ReportSource[]{ reportSource };
        clientPair.appClient.createWidget(1, reportingWidget);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        // a bit upfront
        long now = (System.currentTimeMillis()) + 1500;
        Report report = new Report(1, "DailyReport", new ReportSource[]{ reportSource }, new cc.blynk.server.core.model.widgets.ui.reporting.type.DailyReport(now, ReportDurationType.CUSTOM, now, now), "test@gmail.com", GraphGranularityType.MINUTE, true, CSV_FILE_PER_DEVICE_PER_PIN, Format.ISO_SIMPLE, ZoneId.of("UTC"), 0, 0, null);
        clientPair.appClient.createReport(1, report);
        report = clientPair.appClient.parseReportFromResponse(2);
        Assert.assertNotNull(report);
        Assert.assertEquals(System.currentTimeMillis(), report.nextReportAt, 3000);
        String date = LocalDate.now(report.tzName).toString();
        String filename = (((((CounterBase.getUserName()) + "_Blynk_") + (report.id)) + "_") + date) + ".zip";
        Mockito.verify(holder.mailWrapper, Mockito.timeout(3000)).sendReportEmail(ArgumentMatchers.eq("test@gmail.com"), ArgumentMatchers.eq("Your daily DailyReport is ready"), ArgumentMatchers.eq(("http://127.0.0.1:18080/" + filename)), ArgumentMatchers.any());
        TestUtil.sleep(200);
        Assert.assertEquals(1, holder.reportScheduler.getCompletedTaskCount());
        Assert.assertEquals(1, holder.reportScheduler.getTaskCount());
        Path result = Paths.get(CSV_DIR, ((((((((CounterBase.getUserName()) + "_") + (AppNameUtil.BLYNK)) + "_") + (report.id)) + "_") + date) + ".zip"));
        Assert.assertTrue(Files.exists(result));
        String resultCsvString = readStringFromFirstZipEntry(result);
        String[] split = resultCsvString.split("[,\n]");
        String nowFormatted = DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.of("UTC")).format(Instant.ofEpochMilli(pointNow));
        Assert.assertEquals(nowFormatted, split[0]);
        Assert.assertEquals(1.11, Double.parseDouble(split[1]), 1.0E-4);
        clientPair.appClient.getWidget(1, 222222);
        ReportingWidget reportingWidget2 = ((ReportingWidget) (JsonParser.parseWidget(clientPair.appClient.getBody(3), 0)));
        Assert.assertNotNull(reportingWidget2);
        Assert.assertEquals(EXPIRED, reportingWidget2.reports[0].lastRunResult);
    }

    @Test
    public void testOneTimeReportIsTriggered() throws Exception {
        String tempDir = holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        Path pinReportingDataPath10 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, VIRTUAL, ((short) (1)), MINUTE));
        long now = System.currentTimeMillis();
        FileUtils.write(pinReportingDataPath10, 1.11, now);
        ReportDataStream reportDataStream = new ReportDataStream(((short) (1)), PinType.VIRTUAL, "Temperature", true);
        ReportSource reportSource = new cc.blynk.server.core.model.widgets.ui.reporting.source.TileTemplateReportSource(new ReportDataStream[]{ reportDataStream }, 1, new int[]{ 0 });
        ReportingWidget reportingWidget = new ReportingWidget();
        reportingWidget.height = 1;
        reportingWidget.width = 1;
        reportingWidget.reportSources = new ReportSource[]{ reportSource };
        clientPair.appClient.createWidget(1, reportingWidget);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        Report report = new Report(1, "OneTime Report", new ReportSource[]{ reportSource }, new OneTimeReport(TimeUnit.DAYS.toMillis(1)), "test@gmail.com", GraphGranularityType.MINUTE, true, CSV_FILE_PER_DEVICE_PER_PIN, Format.ISO_SIMPLE, ZoneId.of("UTC"), 0, 0, null);
        clientPair.appClient.createReport(1, report);
        report = clientPair.appClient.parseReportFromResponse(2);
        Assert.assertNotNull(report);
        Assert.assertEquals(0, report.nextReportAt);
        Assert.assertEquals(0, report.lastReportAt);
        Mockito.verify(holder.mailWrapper, Mockito.never()).sendReportEmail(ArgumentMatchers.eq("test@gmail.com"), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        clientPair.appClient.exportReport(1, 1);
        report = clientPair.appClient.parseReportFromResponse(3);
        Assert.assertNotNull(report);
        Assert.assertEquals(0, report.nextReportAt);
        Assert.assertEquals(System.currentTimeMillis(), report.lastReportAt, 2000);
        Assert.assertEquals(OK, report.lastRunResult);
        String date = LocalDate.now(report.tzName).toString();
        String filename = (((((CounterBase.getUserName()) + "_Blynk_") + (report.id)) + "_") + date) + ".zip";
        Mockito.verify(holder.mailWrapper, Mockito.timeout(3000)).sendReportEmail(ArgumentMatchers.eq("test@gmail.com"), ArgumentMatchers.eq("Your one time OneTime Report is ready"), ArgumentMatchers.eq(("http://127.0.0.1:18080/" + filename)), ArgumentMatchers.eq("Report name: OneTime Report<br>Period: One time"));
        TestUtil.sleep(200);
        Assert.assertEquals(1, holder.reportScheduler.getCompletedTaskCount());
        Assert.assertEquals(1, holder.reportScheduler.getTaskCount());
        Assert.assertEquals(0, holder.reportScheduler.map.size());
        Assert.assertEquals(0, holder.reportScheduler.getActiveCount());
        Path result = Paths.get(CSV_DIR, ((((((((CounterBase.getUserName()) + "_") + (AppNameUtil.BLYNK)) + "_") + (report.id)) + "_") + date) + ".zip"));
        Assert.assertTrue(Files.exists(result));
        String resultCsvString = readStringFromFirstZipEntry(result);
        Assert.assertNotNull(resultCsvString);
        String[] split = resultCsvString.split("[,\n]");
        String nowFormatted = DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.of("UTC")).format(Instant.ofEpochMilli(now));
        Assert.assertEquals(nowFormatted, split[0]);
        Assert.assertEquals(1.11, Double.parseDouble(split[1]), 1.0E-4);
    }

    @Test
    public void testOneTimeReportWithWrongSources() throws Exception {
        String tempDir = holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        Path pinReportingDataPath10 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, VIRTUAL, ((short) (1)), MINUTE));
        long now = System.currentTimeMillis();
        FileUtils.write(pinReportingDataPath10, 1.11, now);
        ReportDataStream reportDataStream = new ReportDataStream(((short) (1)), PinType.VIRTUAL, "Temperature", true);
        ReportSource reportSource = new cc.blynk.server.core.model.widgets.ui.reporting.source.TileTemplateReportSource(new ReportDataStream[]{ reportDataStream }, 1, new int[]{ 0 });
        ReportSource reportSource2 = new cc.blynk.server.core.model.widgets.ui.reporting.source.TileTemplateReportSource(new ReportDataStream[]{ reportDataStream }, 1, new int[]{ 0 });
        ReportingWidget reportingWidget = new ReportingWidget();
        reportingWidget.height = 1;
        reportingWidget.width = 1;
        reportingWidget.reportSources = new ReportSource[]{ reportSource };
        clientPair.appClient.createWidget(1, reportingWidget);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        Report report = new Report(1, "OneTime Report", new ReportSource[]{ reportSource, reportSource2 }, new OneTimeReport(TimeUnit.DAYS.toMillis(1)), "test@gmail.com", GraphGranularityType.MINUTE, true, CSV_FILE_PER_DEVICE_PER_PIN, Format.ISO_SIMPLE, ZoneId.of("UTC"), 0, 0, null);
        clientPair.appClient.createReport(1, report);
        report = clientPair.appClient.parseReportFromResponse(2);
        Assert.assertNotNull(report);
        Assert.assertEquals(0, report.nextReportAt);
        Assert.assertEquals(0, report.lastReportAt);
        Mockito.verify(holder.mailWrapper, Mockito.never()).sendReportEmail(ArgumentMatchers.eq("test@gmail.com"), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        clientPair.appClient.exportReport(1, 1);
        report = clientPair.appClient.parseReportFromResponse(3);
        Assert.assertNotNull(report);
        Assert.assertEquals(0, report.nextReportAt);
        Assert.assertEquals(System.currentTimeMillis(), report.lastReportAt, 2000);
        Assert.assertEquals(OK, report.lastRunResult);
        String date = LocalDate.now(report.tzName).toString();
        String filename = (((((CounterBase.getUserName()) + "_Blynk_") + (report.id)) + "_") + date) + ".zip";
        Mockito.verify(holder.mailWrapper, Mockito.timeout(3000)).sendReportEmail(ArgumentMatchers.eq("test@gmail.com"), ArgumentMatchers.eq("Your one time OneTime Report is ready"), ArgumentMatchers.eq(("http://127.0.0.1:18080/" + filename)), ArgumentMatchers.eq("Report name: OneTime Report<br>Period: One time"));
        TestUtil.sleep(200);
        Assert.assertEquals(1, holder.reportScheduler.getCompletedTaskCount());
        Assert.assertEquals(1, holder.reportScheduler.getTaskCount());
        Assert.assertEquals(0, holder.reportScheduler.map.size());
        Assert.assertEquals(0, holder.reportScheduler.getActiveCount());
        Path result = Paths.get(CSV_DIR, ((((((((CounterBase.getUserName()) + "_") + (AppNameUtil.BLYNK)) + "_") + (report.id)) + "_") + date) + ".zip"));
        Assert.assertTrue(Files.exists(result));
        String resultCsvString = readStringFromFirstZipEntry(result);
        Assert.assertNotNull(resultCsvString);
        String[] split = resultCsvString.split("[,\n]");
        String nowFormatted = DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.of("UTC")).format(Instant.ofEpochMilli(now));
        Assert.assertEquals(nowFormatted, split[0]);
        Assert.assertEquals(1.11, Double.parseDouble(split[1]), 1.0E-4);
    }

    @Test
    public void testMultipleReceiversFroOneTimeReport() throws Exception {
        String tempDir = holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        Path pinReportingDataPath10 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, VIRTUAL, ((short) (1)), MINUTE));
        long now = System.currentTimeMillis();
        FileUtils.write(pinReportingDataPath10, 1.11, now);
        ReportDataStream reportDataStream = new ReportDataStream(((short) (1)), PinType.VIRTUAL, "Temperature", true);
        ReportSource reportSource = new cc.blynk.server.core.model.widgets.ui.reporting.source.TileTemplateReportSource(new ReportDataStream[]{ reportDataStream }, 1, new int[]{ 0 });
        ReportingWidget reportingWidget = new ReportingWidget();
        reportingWidget.height = 1;
        reportingWidget.width = 1;
        reportingWidget.reportSources = new ReportSource[]{ reportSource };
        clientPair.appClient.createWidget(1, reportingWidget);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        Report report = new Report(1, "OneTime Report", new ReportSource[]{ reportSource }, new OneTimeReport(TimeUnit.DAYS.toMillis(1)), "test@gmail.com,test2@gmail.com", GraphGranularityType.MINUTE, true, CSV_FILE_PER_DEVICE_PER_PIN, Format.ISO_SIMPLE, ZoneId.of("UTC"), 0, 0, null);
        clientPair.appClient.createReport(1, report);
        report = clientPair.appClient.parseReportFromResponse(2);
        Assert.assertNotNull(report);
        Assert.assertEquals(0, report.nextReportAt);
        Assert.assertEquals(0, report.lastReportAt);
        Mockito.verify(holder.mailWrapper, Mockito.never()).sendReportEmail(ArgumentMatchers.eq("test@gmail.com,test2@gmail.com"), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        clientPair.appClient.exportReport(1, 1);
        report = clientPair.appClient.parseReportFromResponse(3);
        Assert.assertNotNull(report);
        Assert.assertEquals(0, report.nextReportAt);
        Assert.assertEquals(System.currentTimeMillis(), report.lastReportAt, 2000);
        Assert.assertEquals(OK, report.lastRunResult);
        String date = LocalDate.now(report.tzName).toString();
        String filename = (((((CounterBase.getUserName()) + "_Blynk_") + (report.id)) + "_") + date) + ".zip";
        Mockito.verify(holder.mailWrapper, Mockito.timeout(3000)).sendReportEmail(ArgumentMatchers.eq("test@gmail.com,test2@gmail.com"), ArgumentMatchers.eq("Your one time OneTime Report is ready"), ArgumentMatchers.eq(("http://127.0.0.1:18080/" + filename)), ArgumentMatchers.eq("Report name: OneTime Report<br>Period: One time"));
        TestUtil.sleep(200);
        Assert.assertEquals(1, holder.reportScheduler.getCompletedTaskCount());
        Assert.assertEquals(1, holder.reportScheduler.getTaskCount());
        Assert.assertEquals(0, holder.reportScheduler.map.size());
        Assert.assertEquals(0, holder.reportScheduler.getActiveCount());
        Path result = Paths.get(CSV_DIR, ((((((((CounterBase.getUserName()) + "_") + (AppNameUtil.BLYNK)) + "_") + (report.id)) + "_") + date) + ".zip"));
        Assert.assertTrue(Files.exists(result));
        String resultCsvString = readStringFromFirstZipEntry(result);
        Assert.assertNotNull(resultCsvString);
        String[] split = resultCsvString.split("[,\n]");
        String nowFormatted = DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.of("UTC")).format(Instant.ofEpochMilli(now));
        Assert.assertEquals(nowFormatted, split[0]);
        Assert.assertEquals(1.11, Double.parseDouble(split[1]), 1.0E-4);
    }

    @Test
    public void testStreamsAreCorrectlyFiltered() throws Exception {
        String tempDir = holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        Path pinReportingDataPath11 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, VIRTUAL, ((short) (1)), MINUTE));
        Path pinReportingDataPath12 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, VIRTUAL, ((short) (2)), MINUTE));
        long now = System.currentTimeMillis();
        FileUtils.write(pinReportingDataPath11, 1.11, now);
        FileUtils.write(pinReportingDataPath12, 1.12, now);
        ReportDataStream reportDataStream = new ReportDataStream(((short) (1)), PinType.VIRTUAL, "Temperature", true);
        ReportDataStream reportDataStream2 = new ReportDataStream(((short) (2)), PinType.VIRTUAL, "Temperature2", true);
        ReportSource reportSource = new cc.blynk.server.core.model.widgets.ui.reporting.source.TileTemplateReportSource(new ReportDataStream[]{ reportDataStream, reportDataStream2 }, 1, new int[]{ 0 });
        ReportingWidget reportingWidget = new ReportingWidget();
        reportingWidget.height = 1;
        reportingWidget.width = 1;
        reportingWidget.reportSources = new ReportSource[]{ reportSource };
        clientPair.appClient.createWidget(1, reportingWidget);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        reportDataStream = new ReportDataStream(((short) (1)), PinType.VIRTUAL, "Temperature", false);
        Report report = new Report(1, "OneTime Report", new ReportSource[]{ new cc.blynk.server.core.model.widgets.ui.reporting.source.TileTemplateReportSource(new ReportDataStream[]{ reportDataStream, reportDataStream2 }, 1, new int[]{ 0 }) }, new OneTimeReport(TimeUnit.DAYS.toMillis(1)), "test@gmail.com", GraphGranularityType.MINUTE, true, CSV_FILE_PER_DEVICE_PER_PIN, Format.ISO_SIMPLE, ZoneId.of("UTC"), 0, 0, null);
        clientPair.appClient.createReport(1, report);
        report = clientPair.appClient.parseReportFromResponse(2);
        Assert.assertNotNull(report);
        Assert.assertEquals(0, report.nextReportAt);
        Assert.assertEquals(0, report.lastReportAt);
        Mockito.verify(holder.mailWrapper, Mockito.never()).sendReportEmail(ArgumentMatchers.eq("test@gmail.com"), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        clientPair.appClient.exportReport(1, 1);
        report = clientPair.appClient.parseReportFromResponse(3);
        Assert.assertNotNull(report);
        Assert.assertEquals(0, report.nextReportAt);
        Assert.assertEquals(System.currentTimeMillis(), report.lastReportAt, 2000);
        Assert.assertEquals(OK, report.lastRunResult);
        String date = LocalDate.now(report.tzName).toString();
        String filename = (((((CounterBase.getUserName()) + "_Blynk_") + (report.id)) + "_") + date) + ".zip";
        Mockito.verify(holder.mailWrapper, Mockito.timeout(3000)).sendReportEmail(ArgumentMatchers.eq("test@gmail.com"), ArgumentMatchers.eq("Your one time OneTime Report is ready"), ArgumentMatchers.eq(("http://127.0.0.1:18080/" + filename)), ArgumentMatchers.eq("Report name: OneTime Report<br>Period: One time"));
        TestUtil.sleep(200);
        Assert.assertEquals(1, holder.reportScheduler.getCompletedTaskCount());
        Assert.assertEquals(1, holder.reportScheduler.getTaskCount());
        Assert.assertEquals(0, holder.reportScheduler.map.size());
        Assert.assertEquals(0, holder.reportScheduler.getActiveCount());
        Path result = Paths.get(CSV_DIR, ((((((((CounterBase.getUserName()) + "_") + (AppNameUtil.BLYNK)) + "_") + (report.id)) + "_") + date) + ".zip"));
        Assert.assertTrue(Files.exists(result));
        String resultCsvString = readStringFromFirstZipEntry(result);
        Assert.assertNotNull(resultCsvString);
        String[] split = resultCsvString.split("[,\n]");
        String nowFormatted = DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.of("UTC")).format(Instant.ofEpochMilli(now));
        Assert.assertEquals(2, split.length);
        Assert.assertEquals(nowFormatted, split[0]);
        Assert.assertEquals(1.12, Double.parseDouble(split[1]), 1.0E-4);
    }

    @Test
    public void testExportIsLimited() throws Exception {
        String tempDir = holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        Path pinReportingDataPath10 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, VIRTUAL, ((short) (1)), MINUTE));
        long now = System.currentTimeMillis();
        FileUtils.write(pinReportingDataPath10, 1.11, now);
        ReportDataStream reportDataStream = new ReportDataStream(((short) (1)), PinType.VIRTUAL, "Temperature", true);
        ReportSource reportSource = new cc.blynk.server.core.model.widgets.ui.reporting.source.TileTemplateReportSource(new ReportDataStream[]{ reportDataStream }, 1, new int[]{ 0 });
        ReportingWidget reportingWidget = new ReportingWidget();
        reportingWidget.height = 1;
        reportingWidget.width = 1;
        reportingWidget.reportSources = new ReportSource[]{ reportSource };
        clientPair.appClient.createWidget(1, reportingWidget);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        Report report = new Report(1, "OneTime Report", new ReportSource[]{ reportSource }, new OneTimeReport(TimeUnit.DAYS.toMillis(1)), "test@gmail.com", GraphGranularityType.MINUTE, true, CSV_FILE_PER_DEVICE_PER_PIN, Format.ISO_SIMPLE, ZoneId.of("UTC"), 0, 0, null);
        clientPair.appClient.createReport(1, report);
        report = clientPair.appClient.parseReportFromResponse(2);
        Assert.assertNotNull(report);
        Assert.assertEquals(0, report.nextReportAt);
        Assert.assertEquals(0, report.lastReportAt);
        Mockito.verify(holder.mailWrapper, Mockito.never()).sendReportEmail(ArgumentMatchers.eq("test@gmail.com"), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        clientPair.appClient.exportReport(1, 1);
        report = clientPair.appClient.parseReportFromResponse(3);
        Assert.assertNotNull(report);
        Assert.assertEquals(0, report.nextReportAt);
        Assert.assertEquals(System.currentTimeMillis(), report.lastReportAt, 2000);
        Assert.assertEquals(OK, report.lastRunResult);
        clientPair.appClient.exportReport(1, 1);
        clientPair.appClient.verifyResult(new cc.blynk.server.core.protocol.model.messages.ResponseMessage(4, QUOTA_LIMIT));
    }

    @Test
    public void testOneTimeReportIsTriggeredWithAnotherFormat() throws Exception {
        String tempDir = holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        Path pinReportingDataPath10 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, VIRTUAL, ((short) (1)), MINUTE));
        long now = System.currentTimeMillis();
        FileUtils.write(pinReportingDataPath10, 1.11, now);
        ReportDataStream reportDataStream = new ReportDataStream(((short) (1)), PinType.VIRTUAL, "Temperature", true);
        ReportSource reportSource = new cc.blynk.server.core.model.widgets.ui.reporting.source.TileTemplateReportSource(new ReportDataStream[]{ reportDataStream }, 1, new int[]{ 0 });
        ReportingWidget reportingWidget = new ReportingWidget();
        reportingWidget.height = 1;
        reportingWidget.width = 1;
        reportingWidget.reportSources = new ReportSource[]{ reportSource };
        clientPair.appClient.createWidget(1, reportingWidget);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        Report report = new Report(1, "OneTime Report", new ReportSource[]{ reportSource }, new OneTimeReport(TimeUnit.DAYS.toMillis(1)), "test@gmail.com", GraphGranularityType.MINUTE, true, CSV_FILE_PER_DEVICE_PER_PIN, Format.TS, ZoneId.of("UTC"), 0, 0, null);
        clientPair.appClient.createReport(1, report);
        report = clientPair.appClient.parseReportFromResponse(2);
        Assert.assertNotNull(report);
        Assert.assertEquals(0, report.nextReportAt);
        Assert.assertEquals(0, report.lastReportAt);
        Mockito.verify(holder.mailWrapper, Mockito.never()).sendReportEmail(ArgumentMatchers.eq("test@gmail.com"), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        clientPair.appClient.exportReport(1, 1);
        report = clientPair.appClient.parseReportFromResponse(3);
        Assert.assertNotNull(report);
        Assert.assertEquals(0, report.nextReportAt);
        Assert.assertEquals(System.currentTimeMillis(), report.lastReportAt, 2000);
        Assert.assertEquals(OK, report.lastRunResult);
        String date = LocalDate.now(report.tzName).toString();
        String filename = (((((CounterBase.getUserName()) + "_Blynk_") + (report.id)) + "_") + date) + ".zip";
        Mockito.verify(holder.mailWrapper, Mockito.timeout(3000)).sendReportEmail(ArgumentMatchers.eq("test@gmail.com"), ArgumentMatchers.eq("Your one time OneTime Report is ready"), ArgumentMatchers.eq(("http://127.0.0.1:18080/" + filename)), ArgumentMatchers.eq("Report name: OneTime Report<br>Period: One time"));
        TestUtil.sleep(200);
        Assert.assertEquals(1, holder.reportScheduler.getCompletedTaskCount());
        Assert.assertEquals(1, holder.reportScheduler.getTaskCount());
        Assert.assertEquals(0, holder.reportScheduler.map.size());
        Assert.assertEquals(0, holder.reportScheduler.getActiveCount());
        Path result = Paths.get(CSV_DIR, ((((((((CounterBase.getUserName()) + "_") + (AppNameUtil.BLYNK)) + "_") + (report.id)) + "_") + date) + ".zip"));
        Assert.assertTrue(Files.exists(result));
        String resultCsvString = readStringFromFirstZipEntry(result);
        Assert.assertNotNull(resultCsvString);
        String[] split = resultCsvString.split("[,\n]");
        Assert.assertEquals(now, Long.parseLong(split[0]), 2000);
        Assert.assertEquals(1.11, Double.parseDouble(split[1]), 1.0E-4);
    }

    @Test
    public void testOneTimeReportIsTriggeredWithCustomJson() throws Exception {
        ReportDataStream reportDataStream = new ReportDataStream(((short) (1)), PinType.VIRTUAL, "Temperature", true);
        ReportSource reportSource = new cc.blynk.server.core.model.widgets.ui.reporting.source.TileTemplateReportSource(new ReportDataStream[]{ reportDataStream }, 1, new int[]{ 0 });
        ReportingWidget reportingWidget = new ReportingWidget();
        reportingWidget.height = 1;
        reportingWidget.width = 1;
        reportingWidget.reportSources = new ReportSource[]{ reportSource };
        clientPair.appClient.createWidget(1, reportingWidget);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.createReport(1, "{\"id\":12838,\"name\":\"Report\",\"reportSources\":[{\"type\":\"TILE_TEMPLATE\",\"templateId\":11844,\"deviceIds\":[0],\"reportDataStreams\":[{\"pin\":1,\"pinType\":\"VIRTUAL\",\"label\":\"Temperature\",\"isSelected\":true},{\"pin\":0,\"pinType\":\"VIRTUAL\",\"label\":\"Humidity\",\"isSelected\":true},{\"pin\":2,\"pinType\":\"VIRTUAL\",\"label\":\"Heat\",\"isSelected\":true}]}],\"reportType\":{\"type\":\"ONE_TIME\",\"rangeMillis\":86400000},\"recipients\":\"alexkipar@gmail.com\",\"granularityType\":\"HOURLY\",\"isActive\":true,\"reportOutput\":\"CSV_FILE_PER_DEVICE_PER_PIN\",\"tzName\":\"Europe/Kiev\",\"nextReportAt\":0,\"lastReportAt\":1528309698795,\"lastRunResult\":\"ERROR\"}");
        Report report = clientPair.appClient.parseReportFromResponse(2);
        Assert.assertNotNull(report);
        clientPair.appClient.exportReport(1, 12838);
        report = clientPair.appClient.parseReportFromResponse(3);
        Assert.assertNotNull(report);
        Assert.assertEquals(0, report.nextReportAt);
        Assert.assertEquals(System.currentTimeMillis(), report.lastReportAt, 2000);
        Assert.assertEquals(NO_DATA, report.lastRunResult);
    }

    @Test
    public void testOneTimeReportIsTriggeredAndNoData() throws Exception {
        ReportDataStream reportDataStream = new ReportDataStream(((short) (1)), PinType.VIRTUAL, "Temperature", true);
        ReportSource reportSource = new cc.blynk.server.core.model.widgets.ui.reporting.source.TileTemplateReportSource(new ReportDataStream[]{ reportDataStream }, 1, new int[]{ 0 });
        ReportingWidget reportingWidget = new ReportingWidget();
        reportingWidget.height = 1;
        reportingWidget.width = 1;
        reportingWidget.reportSources = new ReportSource[]{ reportSource };
        clientPair.appClient.createWidget(1, reportingWidget);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        Report report = new Report(1, "OneTime Report", new ReportSource[]{ reportSource }, new OneTimeReport(TimeUnit.DAYS.toMillis(1)), "test@gmail.com", GraphGranularityType.MINUTE, true, CSV_FILE_PER_DEVICE_PER_PIN, null, ZoneId.of("UTC"), 0, 0, null);
        clientPair.appClient.createReport(1, report);
        report = clientPair.appClient.parseReportFromResponse(2);
        Assert.assertNotNull(report);
        Assert.assertEquals(0, report.nextReportAt);
        Assert.assertEquals(0, report.lastReportAt);
        clientPair.appClient.exportReport(1, 1);
        report = clientPair.appClient.parseReportFromResponse(3);
        Assert.assertNotNull(report);
        Assert.assertEquals(0, report.nextReportAt);
        Assert.assertEquals(System.currentTimeMillis(), report.lastReportAt, 2000);
        Assert.assertEquals(NO_DATA, report.lastRunResult);
        Mockito.verify(holder.mailWrapper, Mockito.never()).sendReportEmail(ArgumentMatchers.eq("test@gmail.com"), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        TestUtil.sleep(200);
        Assert.assertEquals(1, holder.reportScheduler.getCompletedTaskCount());
        Assert.assertEquals(1, holder.reportScheduler.getTaskCount());
        Assert.assertEquals(0, holder.reportScheduler.map.size());
        Assert.assertEquals(0, holder.reportScheduler.getActiveCount());
    }

    @Test
    public void testOneTimeReportIsTriggeredAndNoData2() throws Exception {
        String tempDir = holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        Path pinReportingDataPath10 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, VIRTUAL, ((short) (1)), MINUTE));
        FileUtils.write(pinReportingDataPath10, 1.11, 111111);
        ReportDataStream reportDataStream = new ReportDataStream(((short) (1)), PinType.VIRTUAL, "Temperature", true);
        ReportSource reportSource = new cc.blynk.server.core.model.widgets.ui.reporting.source.TileTemplateReportSource(new ReportDataStream[]{ reportDataStream }, 1, new int[]{ 0 });
        ReportingWidget reportingWidget = new ReportingWidget();
        reportingWidget.height = 1;
        reportingWidget.width = 1;
        reportingWidget.reportSources = new ReportSource[]{ reportSource };
        clientPair.appClient.createWidget(1, reportingWidget);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        Report report = new Report(1, "OneTime Report", new ReportSource[]{ reportSource }, new OneTimeReport(TimeUnit.DAYS.toMillis(1)), "test@gmail.com", GraphGranularityType.MINUTE, true, CSV_FILE_PER_DEVICE_PER_PIN, null, ZoneId.of("UTC"), 0, 0, null);
        clientPair.appClient.createReport(1, report);
        report = clientPair.appClient.parseReportFromResponse(2);
        Assert.assertNotNull(report);
        Assert.assertEquals(0, report.nextReportAt);
        Assert.assertEquals(0, report.lastReportAt);
        clientPair.appClient.exportReport(1, 1);
        report = clientPair.appClient.parseReportFromResponse(3);
        Assert.assertNotNull(report);
        Assert.assertEquals(0, report.nextReportAt);
        Assert.assertEquals(System.currentTimeMillis(), report.lastReportAt, 2000);
        Assert.assertEquals(NO_DATA, report.lastRunResult);
        Mockito.verify(holder.mailWrapper, Mockito.never()).sendReportEmail(ArgumentMatchers.eq("test@gmail.com"), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        TestUtil.sleep(200);
        Assert.assertEquals(1, holder.reportScheduler.getCompletedTaskCount());
        Assert.assertEquals(1, holder.reportScheduler.getTaskCount());
        Assert.assertEquals(0, holder.reportScheduler.map.size());
        Assert.assertEquals(0, holder.reportScheduler.getActiveCount());
    }

    @Test
    public void testExpiredReportIsNotAddedToTheProject() throws Exception {
        ReportDataStream reportDataStream = new ReportDataStream(((short) (1)), PinType.VIRTUAL, "Temperature", true);
        ReportSource reportSource = new cc.blynk.server.core.model.widgets.ui.reporting.source.TileTemplateReportSource(new ReportDataStream[]{ reportDataStream }, 1, new int[]{ 0 });
        ReportingWidget reportingWidget = new ReportingWidget();
        reportingWidget.id = 222222;
        reportingWidget.height = 1;
        reportingWidget.width = 1;
        reportingWidget.reportSources = new ReportSource[]{ reportSource };
        clientPair.appClient.createWidget(1, reportingWidget);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        long now = (System.currentTimeMillis()) + 1500;
        Report report = new Report(1, "MonthlyReport", new ReportSource[]{ reportSource }, new cc.blynk.server.core.model.widgets.ui.reporting.type.MonthlyReport(now, ReportDurationType.CUSTOM, now, now, DayOfMonth.FIRST), "test@gmail.com", GraphGranularityType.MINUTE, true, CSV_FILE_PER_DEVICE_PER_PIN, null, ZoneId.of("UTC"), 0, 0, null);
        clientPair.appClient.createReport(1, report);
        clientPair.appClient.verifyResult(TestUtil.illegalCommandBody(2));
        clientPair.appClient.getWidget(1, 222222);
        ReportingWidget reportingWidget2 = ((ReportingWidget) (JsonParser.parseWidget(clientPair.appClient.getBody(3), 0)));
        Assert.assertNotNull(reportingWidget2);
        Assert.assertEquals(0, reportingWidget2.reports.length);
        Mockito.verify(holder.mailWrapper, Mockito.never()).sendReportEmail(ArgumentMatchers.eq("test@gmail.com"), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        TestUtil.sleep(200);
        Assert.assertEquals(0, holder.reportScheduler.getCompletedTaskCount());
        Assert.assertEquals(0, holder.reportScheduler.getTaskCount());
        Assert.assertEquals(0, holder.reportScheduler.map.size());
        Assert.assertEquals(0, holder.reportScheduler.getActiveCount());
    }

    @Test
    public void testExpiredReportIsNotAddedToTheProject2() throws Exception {
        ReportDataStream reportDataStream = new ReportDataStream(((short) (1)), PinType.VIRTUAL, "Temperature", true);
        ReportSource reportSource = new cc.blynk.server.core.model.widgets.ui.reporting.source.TileTemplateReportSource(new ReportDataStream[]{ reportDataStream }, 1, new int[]{ 0 });
        ReportingWidget reportingWidget = new ReportingWidget();
        reportingWidget.id = 222222;
        reportingWidget.height = 1;
        reportingWidget.width = 1;
        reportingWidget.reportSources = new ReportSource[]{ reportSource };
        clientPair.appClient.createWidget(1, reportingWidget);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        long now = (System.currentTimeMillis()) + 1500;
        Report report = new Report(1, "MonthlyReport", new ReportSource[]{ reportSource }, new cc.blynk.server.core.model.widgets.ui.reporting.type.MonthlyReport(now, ReportDurationType.CUSTOM, now, (now + (30L * 86400000)), DayOfMonth.FIRST), "test@gmail.com", GraphGranularityType.MINUTE, true, CSV_FILE_PER_DEVICE_PER_PIN, null, ZoneId.of("UTC"), 0, 0, null);
        clientPair.appClient.createReport(1, report);
        report = clientPair.appClient.parseReportFromResponse(2);
        Assert.assertNotNull(report);
        report = new Report(1, "MonthlyReport2", new ReportSource[]{ reportSource }, new cc.blynk.server.core.model.widgets.ui.reporting.type.MonthlyReport(now, ReportDurationType.CUSTOM, now, now, DayOfMonth.FIRST), "test@gmail.com", GraphGranularityType.MINUTE, true, CSV_FILE_PER_DEVICE_PER_PIN, null, ZoneId.of("UTC"), 0, 0, null);
        clientPair.appClient.updateReport(1, report);
        clientPair.appClient.verifyResult(TestUtil.illegalCommandBody(3));
        clientPair.appClient.getWidget(1, 222222);
        ReportingWidget reportingWidget2 = ((ReportingWidget) (JsonParser.parseWidget(clientPair.appClient.getBody(4), 0)));
        Assert.assertNotNull(reportingWidget2);
        Assert.assertEquals(1, reportingWidget2.reports.length);
        Assert.assertEquals("MonthlyReport", reportingWidget2.reports[0].name);
    }

    @Test
    public void testDailyReportWithSinglePointIsTriggeredAndOneRecordIsFiltered() throws Exception {
        String tempDir = holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        Path pinReportingDataPath10 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, VIRTUAL, ((short) (1)), MINUTE));
        long pointNow = System.currentTimeMillis();
        FileUtils.write(pinReportingDataPath10, 1.12, (pointNow - (TimeUnit.HOURS.toMillis(25))));
        FileUtils.write(pinReportingDataPath10, 1.11, pointNow);
        ReportDataStream reportDataStream = new ReportDataStream(((short) (1)), PinType.VIRTUAL, null, true);
        ReportSource reportSource = new cc.blynk.server.core.model.widgets.ui.reporting.source.TileTemplateReportSource(new ReportDataStream[]{ reportDataStream }, 1, new int[]{ 0 });
        ReportingWidget reportingWidget = new ReportingWidget();
        reportingWidget.id = 222222;
        reportingWidget.height = 1;
        reportingWidget.width = 1;
        reportingWidget.reportSources = new ReportSource[]{ reportSource };
        clientPair.appClient.createWidget(1, reportingWidget);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        // a bit upfront
        long now = (System.currentTimeMillis()) + 1500;
        Report report = new Report(1, "DailyReport", new ReportSource[]{ reportSource }, new cc.blynk.server.core.model.widgets.ui.reporting.type.DailyReport(now, ReportDurationType.INFINITE, 0, 0), "test@gmail.com", GraphGranularityType.MINUTE, true, CSV_FILE_PER_DEVICE_PER_PIN, Format.ISO_SIMPLE, ZoneId.of("UTC"), 0, 0, null);
        clientPair.appClient.createReport(1, report);
        report = clientPair.appClient.parseReportFromResponse(2);
        Assert.assertNotNull(report);
        Assert.assertEquals(System.currentTimeMillis(), report.nextReportAt, 3000);
        String date = LocalDate.now(report.tzName).toString();
        String filename = (((((CounterBase.getUserName()) + "_Blynk_") + (report.id)) + "_") + date) + ".zip";
        Mockito.verify(holder.mailWrapper, Mockito.timeout(3000)).sendReportEmail(ArgumentMatchers.eq("test@gmail.com"), ArgumentMatchers.eq("Your daily DailyReport is ready"), ArgumentMatchers.eq(("http://127.0.0.1:18080/" + filename)), ArgumentMatchers.any());
        TestUtil.sleep(200);
        Assert.assertEquals(1, holder.reportScheduler.getCompletedTaskCount());
        Assert.assertEquals(2, holder.reportScheduler.getTaskCount());
        Path result = Paths.get(CSV_DIR, ((((((((CounterBase.getUserName()) + "_") + (AppNameUtil.BLYNK)) + "_") + (report.id)) + "_") + date) + ".zip"));
        Assert.assertTrue(Files.exists(result));
        String resultCsvString = readStringFromFirstZipEntry(result);
        String[] split = resultCsvString.split("[,\n]");
        String nowFormatted = DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.of("UTC")).format(Instant.ofEpochMilli(pointNow));
        Assert.assertEquals(nowFormatted, split[0]);
        Assert.assertEquals(1.11, Double.parseDouble(split[1]), 1.0E-4);
        clientPair.appClient.getWidget(1, 222222);
        ReportingWidget reportingWidget2 = ((ReportingWidget) (JsonParser.parseWidget(clientPair.appClient.getBody(3), 0)));
        Assert.assertNotNull(reportingWidget2);
        Assert.assertEquals(OK, reportingWidget2.reports[0].lastRunResult);
    }
}

