package cc.blynk.server.workers;


import GraphGranularityType.HOURLY;
import PinType.ANALOG;
import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.dao.ReportingDiskDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.reporting.average.AggregationKey;
import cc.blynk.server.core.reporting.average.AggregationValue;
import cc.blynk.server.core.reporting.average.AverageAggregatorProcessor;
import cc.blynk.utils.AppNameUtil;
import cc.blynk.utils.properties.ServerProperties;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 11.08.15.
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class ReportingWorkerTest {
    private static final Logger log = LogManager.getLogger(ReportingWorkerTest.class);

    private final String reportingFolder = Paths.get(System.getProperty("java.io.tmpdir"), "data").toString();

    @Mock
    public AverageAggregatorProcessor averageAggregator;

    public ReportingDiskDao reportingDaoMock;

    @Mock
    public ServerProperties properties;

    private BlockingIOProcessor blockingIOProcessor;

    @Test
    public void testFailure() {
        User user = new User();
        user.email = "test";
        user.appName = AppNameUtil.BLYNK;
        ReportingWorker reportingWorker = new ReportingWorker(reportingDaoMock, reportingFolder, new cc.blynk.server.db.ReportingDBManager(blockingIOProcessor, true));
        ConcurrentHashMap<AggregationKey, AggregationValue> map = new ConcurrentHashMap<>();
        long ts = (getTS()) / (AverageAggregatorProcessor.HOUR);
        AggregationKey aggregationKey = new AggregationKey("ddd\u0000+123@gmail.com", AppNameUtil.BLYNK, 1, 0, PinType.ANALOG, ((short) (1)), ts);
        AggregationValue aggregationValue = new AggregationValue();
        aggregationValue.update(100);
        map.put(aggregationKey, aggregationValue);
        Mockito.when(averageAggregator.getMinute()).thenReturn(map);
        reportingWorker.run();
        Assert.assertTrue(map.isEmpty());
    }

    @Test
    public void testStore() {
        User user = new User();
        user.email = "test";
        user.appName = AppNameUtil.BLYNK;
        ReportingWorker reportingWorker = new ReportingWorker(reportingDaoMock, reportingFolder, new cc.blynk.server.db.ReportingDBManager(blockingIOProcessor, true));
        ConcurrentHashMap<AggregationKey, AggregationValue> map = new ConcurrentHashMap<>();
        long ts = (getTS()) / (AverageAggregatorProcessor.HOUR);
        AggregationKey aggregationKey = new AggregationKey("test", AppNameUtil.BLYNK, 1, 0, PinType.ANALOG, ((short) (1)), ts);
        AggregationValue aggregationValue = new AggregationValue();
        aggregationValue.update(100);
        AggregationKey aggregationKey2 = new AggregationKey("test", AppNameUtil.BLYNK, 1, 0, PinType.ANALOG, ((short) (1)), (ts - 1));
        AggregationValue aggregationValue2 = new AggregationValue();
        aggregationValue2.update(150.54);
        AggregationKey aggregationKey3 = new AggregationKey("test2", AppNameUtil.BLYNK, 2, 0, PinType.ANALOG, ((short) (2)), ts);
        AggregationValue aggregationValue3 = new AggregationValue();
        aggregationValue3.update(200);
        map.put(aggregationKey, aggregationValue);
        map.put(aggregationKey2, aggregationValue2);
        map.put(aggregationKey3, aggregationValue3);
        Mockito.when(averageAggregator.getMinute()).thenReturn(new ConcurrentHashMap());
        Mockito.when(averageAggregator.getHourly()).thenReturn(map);
        Mockito.when(averageAggregator.getDaily()).thenReturn(new ConcurrentHashMap());
        reportingWorker.run();
        Assert.assertTrue(Files.exists(Paths.get(reportingFolder, "test", generateFilename(1, 0, ANALOG, ((short) (1)), HOURLY))));
        Assert.assertTrue(Files.exists(Paths.get(reportingFolder, "test2", generateFilename(2, 0, ANALOG, ((short) (2)), HOURLY))));
        Assert.assertTrue(map.isEmpty());
        ByteBuffer data = reportingDaoMock.getByteBufferFromDisk(user, 1, 0, ANALOG, ((short) (1)), 2, HOURLY, 0);
        Assert.assertNotNull(data);
        Assert.assertEquals(32, data.capacity());
        Assert.assertEquals(150.54, data.getDouble(), 0.001);
        Assert.assertEquals(((ts - 1) * (AverageAggregatorProcessor.HOUR)), data.getLong());
        Assert.assertEquals(100.0, data.getDouble(), 0.001);
        Assert.assertEquals((ts * (AverageAggregatorProcessor.HOUR)), data.getLong());
        User user2 = new User();
        user2.email = "test2";
        user2.appName = AppNameUtil.BLYNK;
        data = reportingDaoMock.getByteBufferFromDisk(user2, 2, 0, ANALOG, ((short) (2)), 1, HOURLY, 0);
        Assert.assertNotNull(data);
        Assert.assertEquals(16, data.capacity());
        Assert.assertEquals(200.0, data.getDouble(), 0.001);
        Assert.assertEquals((ts * (AverageAggregatorProcessor.HOUR)), data.getLong());
    }

    @Test
    public void testStore2() {
        ReportingWorker reportingWorker = new ReportingWorker(reportingDaoMock, reportingFolder, new cc.blynk.server.db.ReportingDBManager(blockingIOProcessor, true));
        ConcurrentHashMap<AggregationKey, AggregationValue> map = new ConcurrentHashMap<>();
        long ts = (getTS()) / (AverageAggregatorProcessor.HOUR);
        AggregationKey aggregationKey = new AggregationKey("test", AppNameUtil.BLYNK, 1, 0, PinType.ANALOG, ((short) (1)), ts);
        AggregationValue aggregationValue = new AggregationValue();
        aggregationValue.update(100);
        AggregationKey aggregationKey2 = new AggregationKey("test", AppNameUtil.BLYNK, 1, 0, PinType.ANALOG, ((short) (1)), (ts - 1));
        AggregationValue aggregationValue2 = new AggregationValue();
        aggregationValue2.update(150.54);
        AggregationKey aggregationKey3 = new AggregationKey("test", AppNameUtil.BLYNK, 1, 0, PinType.ANALOG, ((short) (1)), (ts - 2));
        AggregationValue aggregationValue3 = new AggregationValue();
        aggregationValue3.update(200);
        map.put(aggregationKey, aggregationValue);
        map.put(aggregationKey2, aggregationValue2);
        map.put(aggregationKey3, aggregationValue3);
        Mockito.when(averageAggregator.getMinute()).thenReturn(new ConcurrentHashMap());
        Mockito.when(averageAggregator.getHourly()).thenReturn(map);
        Mockito.when(averageAggregator.getDaily()).thenReturn(new ConcurrentHashMap());
        reportingWorker.run();
        Assert.assertTrue(Files.exists(Paths.get(reportingFolder, "test", generateFilename(1, 0, ANALOG, ((short) (1)), HOURLY))));
        Assert.assertTrue(map.isEmpty());
        User user = new User();
        user.email = "test";
        user.appName = AppNameUtil.BLYNK;
        // take less
        ByteBuffer data = reportingDaoMock.getByteBufferFromDisk(user, 1, 0, ANALOG, ((short) (1)), 1, HOURLY, 0);
        Assert.assertNotNull(data);
        Assert.assertEquals(16, data.capacity());
        Assert.assertEquals(100.0, data.getDouble(), 0.001);
        Assert.assertEquals((ts * (AverageAggregatorProcessor.HOUR)), data.getLong());
        // take more
        data = reportingDaoMock.getByteBufferFromDisk(user, 1, 0, ANALOG, ((short) (1)), 24, HOURLY, 0);
        Assert.assertNotNull(data);
        Assert.assertEquals(48, data.capacity());
        Assert.assertEquals(200.0, data.getDouble(), 0.001);
        Assert.assertEquals(((ts - 2) * (AverageAggregatorProcessor.HOUR)), data.getLong());
        Assert.assertEquals(150.54, data.getDouble(), 0.001);
        Assert.assertEquals(((ts - 1) * (AverageAggregatorProcessor.HOUR)), data.getLong());
        Assert.assertEquals(100.0, data.getDouble(), 0.001);
        Assert.assertEquals((ts * (AverageAggregatorProcessor.HOUR)), data.getLong());
    }

    @Test
    public void testDeleteCommand() {
        ReportingWorker reportingWorker = new ReportingWorker(reportingDaoMock, reportingFolder, new cc.blynk.server.db.ReportingDBManager(blockingIOProcessor, true));
        ConcurrentHashMap<AggregationKey, AggregationValue> map = new ConcurrentHashMap<>();
        long ts = (getTS()) / (AverageAggregatorProcessor.HOUR);
        AggregationKey aggregationKey = new AggregationKey("test", AppNameUtil.BLYNK, 1, 0, PinType.ANALOG, ((short) (1)), ts);
        AggregationValue aggregationValue = new AggregationValue();
        aggregationValue.update(100);
        AggregationKey aggregationKey2 = new AggregationKey("test", AppNameUtil.BLYNK, 1, 0, PinType.ANALOG, ((short) (1)), (ts - 1));
        AggregationValue aggregationValue2 = new AggregationValue();
        aggregationValue2.update(150.54);
        AggregationKey aggregationKey3 = new AggregationKey("test2", AppNameUtil.BLYNK, 2, 0, PinType.ANALOG, ((short) (2)), ts);
        AggregationValue aggregationValue3 = new AggregationValue();
        aggregationValue3.update(200);
        map.put(aggregationKey, aggregationValue);
        map.put(aggregationKey2, aggregationValue2);
        map.put(aggregationKey3, aggregationValue3);
        Mockito.when(averageAggregator.getMinute()).thenReturn(new ConcurrentHashMap());
        Mockito.when(averageAggregator.getHourly()).thenReturn(map);
        Mockito.when(averageAggregator.getDaily()).thenReturn(new ConcurrentHashMap());
        Mockito.when(properties.getProperty("data.folder")).thenReturn(System.getProperty("java.io.tmpdir"));
        reportingWorker.run();
        Assert.assertTrue(Files.exists(Paths.get(reportingFolder, "test", generateFilename(1, 0, ANALOG, ((short) (1)), HOURLY))));
        Assert.assertTrue(Files.exists(Paths.get(reportingFolder, "test2", generateFilename(2, 0, ANALOG, ((short) (2)), HOURLY))));
        User user = new User();
        user.email = "test";
        user.appName = AppNameUtil.BLYNK;
        new ReportingDiskDao(reportingFolder, true).delete(user, 1, 0, ANALOG, ((short) (1)));
        Assert.assertFalse(Files.exists(Paths.get(reportingFolder, "test", generateFilename(1, 0, ANALOG, ((short) (1)), HOURLY))));
    }
}

