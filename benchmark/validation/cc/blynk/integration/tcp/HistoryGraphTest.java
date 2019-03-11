package cc.blynk.integration.tcp;


import FileUtils.CSV_DIR;
import GraphGranularityType.HOURLY;
import GraphGranularityType.MINUTE;
import GraphPeriod.DAY;
import GraphPeriod.LIVE;
import GraphPeriod.ONE_HOUR;
import GraphPeriod.THREE_MONTHS;
import GraphPeriod.THREE_MONTHS.granularityType;
import PinType.ANALOG;
import PinType.DIGITAL;
import PinType.VIRTUAL;
import cc.blynk.integration.BaseTest;
import cc.blynk.integration.CounterBase;
import cc.blynk.integration.Holder;
import cc.blynk.integration.SingleServerInstancePerTest;
import cc.blynk.integration.TestUtil;
import cc.blynk.integration.model.tcp.BaseTestAppClient;
import cc.blynk.integration.model.tcp.ClientPair;
import cc.blynk.integration.model.tcp.TestAppClient;
import cc.blynk.server.core.dao.ReportingDiskDao;
import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.device.BoardType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.device.Status;
import cc.blynk.server.core.model.device.Tag;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.outputs.graph.AggregationFunctionType;
import cc.blynk.server.core.model.widgets.outputs.graph.FontSize;
import cc.blynk.server.core.model.widgets.outputs.graph.GraphDataStream;
import cc.blynk.server.core.model.widgets.outputs.graph.GraphGranularityType;
import cc.blynk.server.core.model.widgets.outputs.graph.GraphPeriod;
import cc.blynk.server.core.model.widgets.outputs.graph.GraphType;
import cc.blynk.server.core.model.widgets.outputs.graph.Superchart;
import cc.blynk.server.core.model.widgets.ui.DeviceSelector;
import cc.blynk.server.core.model.widgets.ui.reporting.Report;
import cc.blynk.server.core.model.widgets.ui.reporting.ReportingWidget;
import cc.blynk.server.core.model.widgets.ui.reporting.source.ReportDataStream;
import cc.blynk.server.core.model.widgets.ui.reporting.source.ReportSource;
import cc.blynk.server.core.model.widgets.ui.reporting.type.OneTimeReport;
import cc.blynk.server.core.model.widgets.ui.tiles.DeviceTiles;
import cc.blynk.server.core.model.widgets.ui.tiles.TileTemplate;
import cc.blynk.server.core.model.widgets.ui.tiles.templates.PageTileTemplate;
import cc.blynk.server.core.protocol.model.messages.BinaryMessage;
import cc.blynk.server.core.protocol.model.messages.common.HardwareMessage;
import cc.blynk.server.workers.HistoryGraphUnusedPinDataCleanerWorker;
import cc.blynk.server.workers.ReportingTruncateWorker;
import cc.blynk.utils.FileUtils;
import cc.blynk.utils.ReportingUtil;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.ZoneId;
import java.util.concurrent.ThreadLocalRandom;
import java.util.zip.GZIPInputStream;
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
public class HistoryGraphTest extends SingleServerInstancePerTest {
    private static String blynkTempDir;

    @Test
    public void testTooManyDataForGraphWorkWithNewProtocol() throws Exception {
        TestAppClient appClient = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient.login(CounterBase.getUserName(), "1", "Android", "2.18.0");
        appClient.verifyResult(TestUtil.ok(1));
        String tempDir = HistoryGraphTest.holder.props.getProperty("data.folder");
        Superchart enhancedHistoryGraph = new Superchart();
        enhancedHistoryGraph.id = 432;
        enhancedHistoryGraph.width = 8;
        enhancedHistoryGraph.height = 4;
        DataStream dataStream1 = new DataStream(((short) (8)), PinType.DIGITAL);
        DataStream dataStream2 = new DataStream(((short) (9)), PinType.DIGITAL);
        DataStream dataStream3 = new DataStream(((short) (10)), PinType.DIGITAL);
        DataStream dataStream4 = new DataStream(((short) (11)), PinType.DIGITAL);
        GraphDataStream graphDataStream1 = new GraphDataStream(null, GraphType.LINE, 0, 0, dataStream1, AggregationFunctionType.MAX, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        GraphDataStream graphDataStream2 = new GraphDataStream(null, GraphType.LINE, 0, 0, dataStream2, AggregationFunctionType.MAX, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        GraphDataStream graphDataStream3 = new GraphDataStream(null, GraphType.LINE, 0, 0, dataStream3, AggregationFunctionType.MAX, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        GraphDataStream graphDataStream4 = new GraphDataStream(null, GraphType.LINE, 0, 0, dataStream4, AggregationFunctionType.MAX, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        enhancedHistoryGraph.dataStreams = new GraphDataStream[]{ graphDataStream1, graphDataStream2, graphDataStream3, graphDataStream4 };
        appClient.createWidget(1, enhancedHistoryGraph);
        appClient.verifyResult(TestUtil.ok(1));
        appClient.reset();
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        Path pinReportingDataPath1 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, DIGITAL, ((short) (8)), granularityType));
        Path pinReportingDataPath2 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, DIGITAL, ((short) (9)), granularityType));
        Path pinReportingDataPath3 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, DIGITAL, ((short) (10)), granularityType));
        Path pinReportingDataPath4 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, DIGITAL, ((short) (11)), granularityType));
        for (int i = 0; i < (THREE_MONTHS.numberOfPoints); i++) {
            long now = System.currentTimeMillis();
            FileUtils.write(pinReportingDataPath1, ThreadLocalRandom.current().nextDouble(), now);
            FileUtils.write(pinReportingDataPath2, ThreadLocalRandom.current().nextDouble(), now);
            FileUtils.write(pinReportingDataPath3, ThreadLocalRandom.current().nextDouble(), now);
            FileUtils.write(pinReportingDataPath4, ThreadLocalRandom.current().nextDouble(), now);
        }
        appClient.reset();
        appClient.getEnhancedGraphData(1, 432, THREE_MONTHS);
        BinaryMessage graphDataResponse = appClient.getBinaryBody();
        Assert.assertNotNull(graphDataResponse);
        byte[] decompressedGraphData = BaseTest.decompress(graphDataResponse.getBytes());
        Assert.assertNotNull(decompressedGraphData);
    }

    @Test
    public void testGetGraphDataForTagAndForEnhancedGraph1StreamWithoutData() throws Exception {
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        Tag tag0 = new Tag(100000, "Tag1", new int[]{ 0, 1 });
        clientPair.appClient.createTag(1, tag0);
        String createdTag = clientPair.appClient.getBody(2);
        Tag tag = JsonParser.parseTag(createdTag, 0);
        Assert.assertNotNull(tag);
        Assert.assertEquals(100000, tag.id);
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.createTag(2, tag)));
        String tempDir = HistoryGraphTest.holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        Path pinReportingDataPath = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, VIRTUAL, ((short) (88)), GraphPeriod.DAY.granularityType));
        FileUtils.write(pinReportingDataPath, 1.11, 1111111);
        FileUtils.write(pinReportingDataPath, 1.22, 2222222);
        Superchart enhancedHistoryGraph = new Superchart();
        enhancedHistoryGraph.id = 432;
        enhancedHistoryGraph.width = 8;
        enhancedHistoryGraph.height = 4;
        DataStream dataStream = new DataStream(((short) (88)), PinType.VIRTUAL);
        DataStream dataStream2 = new DataStream(((short) (89)), PinType.VIRTUAL);
        GraphDataStream graphDataStream = new GraphDataStream(null, GraphType.LINE, 0, 100000, dataStream, AggregationFunctionType.MAX, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        GraphDataStream graphDataStream2 = new GraphDataStream(null, GraphType.LINE, 0, 1, dataStream2, AggregationFunctionType.MAX, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        enhancedHistoryGraph.dataStreams = new GraphDataStream[]{ graphDataStream, graphDataStream2 };
        clientPair.appClient.createWidget(1, enhancedHistoryGraph);
        clientPair.appClient.verifyResult(TestUtil.ok(3));
        clientPair.appClient.reset();
        clientPair.appClient.getEnhancedGraphData(1, 432, DAY);
        BinaryMessage graphDataResponse = clientPair.appClient.getBinaryBody();
        Assert.assertNotNull(graphDataResponse);
        byte[] decompressedGraphData = BaseTest.decompress(graphDataResponse.getBytes());
        ByteBuffer bb = ByteBuffer.wrap(decompressedGraphData);
        Assert.assertEquals(1, bb.getInt());
        Assert.assertEquals(2, bb.getInt());
        Assert.assertEquals(1.11, bb.getDouble(), 0.1);
        Assert.assertEquals(1111111, bb.getLong());
        Assert.assertEquals(1.22, bb.getDouble(), 0.1);
        Assert.assertEquals(2222222, bb.getLong());
    }

    @Test
    public void testGetGraphDataForTagAndForEnhancedGraphMAX() throws Exception {
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        Tag tag0 = new Tag(100000, "Tag1", new int[]{ 0, 1 });
        clientPair.appClient.createTag(1, tag0);
        String createdTag = clientPair.appClient.getBody(2);
        Tag tag = JsonParser.parseTag(createdTag, 0);
        Assert.assertNotNull(tag);
        Assert.assertEquals(100000, tag.id);
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.createTag(2, tag)));
        String tempDir = HistoryGraphTest.holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        Path pinReportingDataPath = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, VIRTUAL, ((short) (88)), GraphPeriod.DAY.granularityType));
        FileUtils.write(pinReportingDataPath, 1.11, 1111111);
        FileUtils.write(pinReportingDataPath, 1.22, 2222222);
        Path pinReportingDataPath2 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 1, VIRTUAL, ((short) (88)), GraphPeriod.DAY.granularityType));
        FileUtils.write(pinReportingDataPath2, 1.112, 1111111);
        FileUtils.write(pinReportingDataPath2, 1.222, 2222222);
        Superchart enhancedHistoryGraph = new Superchart();
        enhancedHistoryGraph.id = 432;
        enhancedHistoryGraph.width = 8;
        enhancedHistoryGraph.height = 4;
        DataStream dataStream = new DataStream(((short) (88)), PinType.VIRTUAL);
        GraphDataStream graphDataStream = new GraphDataStream(null, GraphType.LINE, 0, 100000, dataStream, AggregationFunctionType.MAX, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        enhancedHistoryGraph.dataStreams = new GraphDataStream[]{ graphDataStream };
        clientPair.appClient.createWidget(1, enhancedHistoryGraph);
        clientPair.appClient.verifyResult(TestUtil.ok(3));
        clientPair.appClient.reset();
        clientPair.appClient.getEnhancedGraphData(1, 432, DAY);
        BinaryMessage graphDataResponse = clientPair.appClient.getBinaryBody();
        Assert.assertNotNull(graphDataResponse);
        byte[] decompressedGraphData = BaseTest.decompress(graphDataResponse.getBytes());
        ByteBuffer bb = ByteBuffer.wrap(decompressedGraphData);
        Assert.assertEquals(1, bb.getInt());
        Assert.assertEquals(2, bb.getInt());
        Assert.assertEquals(1.112, bb.getDouble(), 0.1);
        Assert.assertEquals(1111111, bb.getLong());
        Assert.assertEquals(1.222, bb.getDouble(), 0.1);
        Assert.assertEquals(2222222, bb.getLong());
    }

    @Test
    public void testGetGraphDataForTagAndForEnhancedGraphMIN() throws Exception {
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        Tag tag0 = new Tag(100000, "Tag1", new int[]{ 0, 1 });
        clientPair.appClient.createTag(1, tag0);
        String createdTag = clientPair.appClient.getBody(2);
        Tag tag = JsonParser.parseTag(createdTag, 0);
        Assert.assertNotNull(tag);
        Assert.assertEquals(100000, tag.id);
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.createTag(2, tag)));
        String tempDir = HistoryGraphTest.holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        Path pinReportingDataPath = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, VIRTUAL, ((short) (88)), GraphPeriod.DAY.granularityType));
        FileUtils.write(pinReportingDataPath, 1.11, 1111111);
        FileUtils.write(pinReportingDataPath, 1.22, 2222222);
        Path pinReportingDataPath2 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 1, VIRTUAL, ((short) (88)), GraphPeriod.DAY.granularityType));
        FileUtils.write(pinReportingDataPath2, 1.112, 1111111);
        FileUtils.write(pinReportingDataPath2, 1.222, 2222222);
        Superchart enhancedHistoryGraph = new Superchart();
        enhancedHistoryGraph.id = 432;
        enhancedHistoryGraph.width = 8;
        enhancedHistoryGraph.height = 4;
        DataStream dataStream = new DataStream(((short) (88)), PinType.VIRTUAL);
        GraphDataStream graphDataStream = new GraphDataStream(null, GraphType.LINE, 0, 100000, dataStream, AggregationFunctionType.MIN, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        enhancedHistoryGraph.dataStreams = new GraphDataStream[]{ graphDataStream };
        clientPair.appClient.createWidget(1, enhancedHistoryGraph);
        clientPair.appClient.verifyResult(TestUtil.ok(3));
        clientPair.appClient.reset();
        clientPair.appClient.getEnhancedGraphData(1, 432, DAY);
        BinaryMessage graphDataResponse = clientPair.appClient.getBinaryBody();
        Assert.assertNotNull(graphDataResponse);
        byte[] decompressedGraphData = BaseTest.decompress(graphDataResponse.getBytes());
        ByteBuffer bb = ByteBuffer.wrap(decompressedGraphData);
        Assert.assertEquals(1, bb.getInt());
        Assert.assertEquals(2, bb.getInt());
        Assert.assertEquals(1.11, bb.getDouble(), 0.1);
        Assert.assertEquals(1111111, bb.getLong());
        Assert.assertEquals(1.22, bb.getDouble(), 0.1);
        Assert.assertEquals(2222222, bb.getLong());
    }

    @Test
    public void testGetGraphDataForTagAndForEnhancedGraphSUM() throws Exception {
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        Tag tag0 = new Tag(100000, "Tag1", new int[]{ 0, 1 });
        clientPair.appClient.createTag(1, tag0);
        String createdTag = clientPair.appClient.getBody(2);
        Tag tag = JsonParser.parseTag(createdTag, 0);
        Assert.assertNotNull(tag);
        Assert.assertEquals(100000, tag.id);
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.createTag(2, tag)));
        String tempDir = HistoryGraphTest.holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        Path pinReportingDataPath = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, VIRTUAL, ((short) (88)), GraphPeriod.DAY.granularityType));
        FileUtils.write(pinReportingDataPath, 1.11, 1111111);
        FileUtils.write(pinReportingDataPath, 1.22, 2222222);
        Path pinReportingDataPath2 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 1, VIRTUAL, ((short) (88)), GraphPeriod.DAY.granularityType));
        FileUtils.write(pinReportingDataPath2, 1.112, 1111111);
        FileUtils.write(pinReportingDataPath2, 1.222, 2222222);
        Superchart enhancedHistoryGraph = new Superchart();
        enhancedHistoryGraph.id = 432;
        enhancedHistoryGraph.width = 8;
        enhancedHistoryGraph.height = 4;
        DataStream dataStream = new DataStream(((short) (88)), PinType.VIRTUAL);
        GraphDataStream graphDataStream = new GraphDataStream(null, GraphType.LINE, 0, 100000, dataStream, AggregationFunctionType.SUM, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        enhancedHistoryGraph.dataStreams = new GraphDataStream[]{ graphDataStream };
        clientPair.appClient.createWidget(1, enhancedHistoryGraph);
        clientPair.appClient.verifyResult(TestUtil.ok(3));
        clientPair.appClient.reset();
        clientPair.appClient.getEnhancedGraphData(1, 432, DAY);
        BinaryMessage graphDataResponse = clientPair.appClient.getBinaryBody();
        Assert.assertNotNull(graphDataResponse);
        byte[] decompressedGraphData = BaseTest.decompress(graphDataResponse.getBytes());
        ByteBuffer bb = ByteBuffer.wrap(decompressedGraphData);
        Assert.assertEquals(1, bb.getInt());
        Assert.assertEquals(2, bb.getInt());
        Assert.assertEquals(2.222, bb.getDouble(), 0.001);
        Assert.assertEquals(1111111, bb.getLong());
        Assert.assertEquals(2.442, bb.getDouble(), 0.001);
        Assert.assertEquals(2222222, bb.getLong());
    }

    @Test
    public void testGetGraphDataForTagAndForEnhancedGraphAVG() throws Exception {
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        Tag tag0 = new Tag(100000, "Tag1", new int[]{ 0, 1 });
        clientPair.appClient.createTag(1, tag0);
        String createdTag = clientPair.appClient.getBody(2);
        Tag tag = JsonParser.parseTag(createdTag, 0);
        Assert.assertNotNull(tag);
        Assert.assertEquals(100000, tag.id);
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.createTag(2, tag)));
        String tempDir = HistoryGraphTest.holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        Path pinReportingDataPath = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, VIRTUAL, ((short) (88)), GraphPeriod.DAY.granularityType));
        FileUtils.write(pinReportingDataPath, 1.11, 1111111);
        FileUtils.write(pinReportingDataPath, 1.22, 2222222);
        Path pinReportingDataPath2 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 1, VIRTUAL, ((short) (88)), GraphPeriod.DAY.granularityType));
        FileUtils.write(pinReportingDataPath2, 1.112, 1111111);
        FileUtils.write(pinReportingDataPath2, 1.222, 2222222);
        Superchart enhancedHistoryGraph = new Superchart();
        enhancedHistoryGraph.id = 432;
        enhancedHistoryGraph.width = 8;
        enhancedHistoryGraph.height = 4;
        DataStream dataStream = new DataStream(((short) (88)), PinType.VIRTUAL);
        GraphDataStream graphDataStream = new GraphDataStream(null, GraphType.LINE, 0, 100000, dataStream, AggregationFunctionType.AVG, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        enhancedHistoryGraph.dataStreams = new GraphDataStream[]{ graphDataStream };
        clientPair.appClient.createWidget(1, enhancedHistoryGraph);
        clientPair.appClient.verifyResult(TestUtil.ok(3));
        clientPair.appClient.reset();
        clientPair.appClient.getEnhancedGraphData(1, 432, DAY);
        BinaryMessage graphDataResponse = clientPair.appClient.getBinaryBody();
        Assert.assertNotNull(graphDataResponse);
        byte[] decompressedGraphData = BaseTest.decompress(graphDataResponse.getBytes());
        ByteBuffer bb = ByteBuffer.wrap(decompressedGraphData);
        Assert.assertEquals(1, bb.getInt());
        Assert.assertEquals(2, bb.getInt());
        Assert.assertEquals(1.111, bb.getDouble(), 0.001);
        Assert.assertEquals(1111111, bb.getLong());
        Assert.assertEquals(1.221, bb.getDouble(), 0.001);
        Assert.assertEquals(2222222, bb.getLong());
    }

    @Test
    public void testGetGraphDataForTagAndForEnhancedGraphMEDIAN() throws Exception {
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        Tag tag0 = new Tag(100000, "Tag1", new int[]{ 0, 1 });
        clientPair.appClient.createTag(1, tag0);
        String createdTag = clientPair.appClient.getBody(2);
        Tag tag = JsonParser.parseTag(createdTag, 0);
        Assert.assertNotNull(tag);
        Assert.assertEquals(100000, tag.id);
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.createTag(2, tag)));
        String tempDir = HistoryGraphTest.holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        Path pinReportingDataPath = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, VIRTUAL, ((short) (88)), GraphPeriod.DAY.granularityType));
        FileUtils.write(pinReportingDataPath, 1.11, 1111111);
        FileUtils.write(pinReportingDataPath, 1.22, 2222222);
        Path pinReportingDataPath2 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 1, VIRTUAL, ((short) (88)), GraphPeriod.DAY.granularityType));
        FileUtils.write(pinReportingDataPath2, 1.112, 1111111);
        FileUtils.write(pinReportingDataPath2, 1.222, 2222222);
        Superchart enhancedHistoryGraph = new Superchart();
        enhancedHistoryGraph.id = 432;
        enhancedHistoryGraph.width = 8;
        enhancedHistoryGraph.height = 4;
        DataStream dataStream = new DataStream(((short) (88)), PinType.VIRTUAL);
        GraphDataStream graphDataStream = new GraphDataStream(null, GraphType.LINE, 0, 100000, dataStream, AggregationFunctionType.MED, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        enhancedHistoryGraph.dataStreams = new GraphDataStream[]{ graphDataStream };
        clientPair.appClient.createWidget(1, enhancedHistoryGraph);
        clientPair.appClient.verifyResult(TestUtil.ok(3));
        clientPair.appClient.reset();
        clientPair.appClient.getEnhancedGraphData(1, 432, DAY);
        BinaryMessage graphDataResponse = clientPair.appClient.getBinaryBody();
        Assert.assertNotNull(graphDataResponse);
        byte[] decompressedGraphData = BaseTest.decompress(graphDataResponse.getBytes());
        ByteBuffer bb = ByteBuffer.wrap(decompressedGraphData);
        Assert.assertEquals(1, bb.getInt());
        Assert.assertEquals(2, bb.getInt());
        Assert.assertEquals(1.112, bb.getDouble(), 0.001);
        Assert.assertEquals(1111111, bb.getLong());
        Assert.assertEquals(1.222, bb.getDouble(), 0.001);
        Assert.assertEquals(2222222, bb.getLong());
    }

    @Test
    public void testGetGraphDataForTagAndForEnhancedGraphMEDIANFor3Devices() throws Exception {
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        Device device2 = new Device(2, "My Device", BoardType.ESP8266);
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        clientPair.appClient.createDevice(1, device2);
        device = clientPair.appClient.parseDevice(2);
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.createDevice(2, device)));
        Tag tag0 = new Tag(100000, "Tag1", new int[]{ 0, 1, 2 });
        clientPair.appClient.createTag(1, tag0);
        String createdTag = clientPair.appClient.getBody(3);
        Tag tag = JsonParser.parseTag(createdTag, 0);
        Assert.assertNotNull(tag);
        Assert.assertEquals(100000, tag.id);
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.createTag(3, tag)));
        String tempDir = HistoryGraphTest.holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        Path pinReportingDataPath = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, VIRTUAL, ((short) (88)), GraphPeriod.DAY.granularityType));
        FileUtils.write(pinReportingDataPath, 1.11, 1111111);
        FileUtils.write(pinReportingDataPath, 1.22, 2222222);
        Path pinReportingDataPath2 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 1, VIRTUAL, ((short) (88)), GraphPeriod.DAY.granularityType));
        FileUtils.write(pinReportingDataPath2, 1.112, 1111111);
        FileUtils.write(pinReportingDataPath2, 1.222, 2222222);
        Path pinReportingDataPath3 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 2, VIRTUAL, ((short) (88)), GraphPeriod.DAY.granularityType));
        FileUtils.write(pinReportingDataPath3, 1.113, 1111111);
        FileUtils.write(pinReportingDataPath3, 1.223, 2222222);
        Superchart enhancedHistoryGraph = new Superchart();
        enhancedHistoryGraph.id = 432;
        enhancedHistoryGraph.width = 8;
        enhancedHistoryGraph.height = 4;
        DataStream dataStream = new DataStream(((short) (88)), PinType.VIRTUAL);
        GraphDataStream graphDataStream = new GraphDataStream(null, GraphType.LINE, 0, 100000, dataStream, AggregationFunctionType.MED, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        enhancedHistoryGraph.dataStreams = new GraphDataStream[]{ graphDataStream };
        clientPair.appClient.createWidget(1, enhancedHistoryGraph);
        clientPair.appClient.verifyResult(TestUtil.ok(4));
        clientPair.appClient.reset();
        clientPair.appClient.getEnhancedGraphData(1, 432, DAY);
        BinaryMessage graphDataResponse = clientPair.appClient.getBinaryBody();
        Assert.assertNotNull(graphDataResponse);
        byte[] decompressedGraphData = BaseTest.decompress(graphDataResponse.getBytes());
        ByteBuffer bb = ByteBuffer.wrap(decompressedGraphData);
        Assert.assertEquals(1, bb.getInt());
        Assert.assertEquals(2, bb.getInt());
        Assert.assertEquals(1.112, bb.getDouble(), 0.001);
        Assert.assertEquals(1111111, bb.getLong());
        Assert.assertEquals(1.222, bb.getDouble(), 0.001);
        Assert.assertEquals(2222222, bb.getLong());
    }

    @Test
    public void testGetGraphDataForEnhancedGraphWithEmptyDataStream() throws Exception {
        String tempDir = HistoryGraphTest.holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        Path pinReportingDataPath = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, DIGITAL, ((short) (8)), HOURLY));
        FileUtils.write(pinReportingDataPath, 1.11, 1111111);
        FileUtils.write(pinReportingDataPath, 1.22, 2222222);
        Superchart enhancedHistoryGraph = new Superchart();
        enhancedHistoryGraph.id = 432;
        enhancedHistoryGraph.width = 8;
        enhancedHistoryGraph.height = 4;
        GraphDataStream graphDataStream = new GraphDataStream(null, GraphType.LINE, 0, 0, null, null, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        enhancedHistoryGraph.dataStreams = new GraphDataStream[]{ graphDataStream };
        clientPair.appClient.createWidget(1, enhancedHistoryGraph);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.getEnhancedGraphData(1, 432, DAY);
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new cc.blynk.server.core.protocol.model.messages.ResponseMessage(2, NO_DATA)));
    }

    @Test
    public void testGetGraphDataForEnhancedGraph() throws Exception {
        String tempDir = HistoryGraphTest.holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        Path pinReportingDataPath = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, DIGITAL, ((short) (8)), GraphPeriod.ONE_HOUR.granularityType));
        for (int point = 0; point < ((ONE_HOUR.numberOfPoints) + 1); point++) {
            FileUtils.write(pinReportingDataPath, ((double) (point)), (1111111 + point));
        }
        Superchart enhancedHistoryGraph = new Superchart();
        enhancedHistoryGraph.id = 432;
        enhancedHistoryGraph.width = 8;
        enhancedHistoryGraph.height = 4;
        DataStream dataStream = new DataStream(((short) (8)), PinType.DIGITAL);
        GraphDataStream graphDataStream = new GraphDataStream(null, GraphType.LINE, 0, 0, dataStream, null, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        enhancedHistoryGraph.dataStreams = new GraphDataStream[]{ graphDataStream };
        clientPair.appClient.createWidget(1, enhancedHistoryGraph);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.reset();
        clientPair.appClient.getEnhancedGraphData(1, 432, ONE_HOUR);
        BinaryMessage graphDataResponse = clientPair.appClient.getBinaryBody();
        Assert.assertNotNull(graphDataResponse);
        byte[] decompressedGraphData = BaseTest.decompress(graphDataResponse.getBytes());
        ByteBuffer bb = ByteBuffer.wrap(decompressedGraphData);
        Assert.assertEquals(1, bb.getInt());
        Assert.assertEquals(60, bb.getInt());
        for (int point = 1; point < ((ONE_HOUR.numberOfPoints) + 1); point++) {
            Assert.assertEquals(point, bb.getDouble(), 0.1);
            Assert.assertEquals((1111111 + point), bb.getLong());
        }
    }

    @Test
    public void testGetGraphDataForEnhancedGraphFor2Streams() throws Exception {
        String tempDir = HistoryGraphTest.holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        Path pinReportingDataPath = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, DIGITAL, ((short) (8)), GraphPeriod.DAY.granularityType));
        FileUtils.write(pinReportingDataPath, 1.11, 1111111);
        FileUtils.write(pinReportingDataPath, 1.22, 2222222);
        Superchart enhancedHistoryGraph = new Superchart();
        enhancedHistoryGraph.id = 432;
        enhancedHistoryGraph.width = 8;
        enhancedHistoryGraph.height = 4;
        DataStream dataStream = new DataStream(((short) (8)), PinType.DIGITAL);
        DataStream dataStream2 = new DataStream(((short) (9)), PinType.DIGITAL);
        GraphDataStream graphDataStream = new GraphDataStream(null, GraphType.LINE, 0, 0, dataStream, null, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        GraphDataStream graphDataStream2 = new GraphDataStream(null, GraphType.LINE, 0, 0, dataStream2, null, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        enhancedHistoryGraph.dataStreams = new GraphDataStream[]{ graphDataStream, graphDataStream2 };
        clientPair.appClient.createWidget(1, enhancedHistoryGraph);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.reset();
        clientPair.appClient.getEnhancedGraphData(1, 432, DAY);
        BinaryMessage graphDataResponse = clientPair.appClient.getBinaryBody();
        Assert.assertNotNull(graphDataResponse);
        byte[] decompressedGraphData = BaseTest.decompress(graphDataResponse.getBytes());
        ByteBuffer bb = ByteBuffer.wrap(decompressedGraphData);
        Assert.assertEquals(1, bb.getInt());
        Assert.assertEquals(2, bb.getInt());
        Assert.assertEquals(1.11, bb.getDouble(), 0.1);
        Assert.assertEquals(1111111, bb.getLong());
        Assert.assertEquals(1.22, bb.getDouble(), 0.1);
        Assert.assertEquals(2222222, bb.getLong());
        Assert.assertEquals(0, bb.getInt());
    }

    @Test
    public void testGetGraphDataForEnhancedGraphWithWrongDataStream() throws Exception {
        String tempDir = HistoryGraphTest.holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        Path pinReportingDataPath = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, DIGITAL, ((short) (8)), HOURLY));
        FileUtils.write(pinReportingDataPath, 1.11, 1111111);
        FileUtils.write(pinReportingDataPath, 1.22, 2222222);
        Superchart enhancedHistoryGraph = new Superchart();
        enhancedHistoryGraph.id = 432;
        enhancedHistoryGraph.width = 8;
        enhancedHistoryGraph.height = 4;
        DataStream dataStream = new DataStream(((short) (8)), null);
        GraphDataStream graphDataStream = new GraphDataStream(null, GraphType.LINE, 0, 0, dataStream, null, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        enhancedHistoryGraph.dataStreams = new GraphDataStream[]{ graphDataStream };
        clientPair.appClient.createWidget(1, enhancedHistoryGraph);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.reset();
        clientPair.appClient.getEnhancedGraphData(1, 432, DAY);
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new cc.blynk.server.core.protocol.model.messages.ResponseMessage(1, NO_DATA)));
    }

    @Test
    public void makeSureNoReportingWhenNotAGraphPin() throws Exception {
        Superchart enhancedHistoryGraph = new Superchart();
        enhancedHistoryGraph.id = 432;
        enhancedHistoryGraph.width = 8;
        enhancedHistoryGraph.height = 4;
        DataStream dataStream = new DataStream(((short) (88)), PinType.VIRTUAL);
        GraphDataStream graphDataStream = new GraphDataStream(null, GraphType.LINE, 0, 0, dataStream, null, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        enhancedHistoryGraph.dataStreams = new GraphDataStream[]{ graphDataStream };
        clientPair.appClient.createWidget(1, enhancedHistoryGraph);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.reset();
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getMinute().size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getHourly().size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getDaily().size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.rawDataProcessor.rawStorage.size());
        clientPair.hardwareClient.send("hardware vw 89 111");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new HardwareMessage(1, TestUtil.b("1-0 vw 89 111"))));
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getMinute().size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getHourly().size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getDaily().size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.rawDataProcessor.rawStorage.size());
    }

    @Test
    public void makeSureReportingIsPresentWhenGraphAssignedToDevice() throws Exception {
        Superchart enhancedHistoryGraph = new Superchart();
        enhancedHistoryGraph.id = 432;
        enhancedHistoryGraph.width = 8;
        enhancedHistoryGraph.height = 4;
        DataStream dataStream = new DataStream(((short) (88)), PinType.VIRTUAL);
        GraphDataStream graphDataStream = new GraphDataStream(null, GraphType.LINE, 0, 0, dataStream, null, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        enhancedHistoryGraph.dataStreams = new GraphDataStream[]{ graphDataStream };
        clientPair.appClient.createWidget(1, enhancedHistoryGraph);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.reset();
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getMinute().size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getHourly().size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getDaily().size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.rawDataCacheForGraphProcessor.rawStorage.size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.rawDataProcessor.rawStorage.size());
        clientPair.hardwareClient.send("hardware vw 88 111");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new HardwareMessage(1, TestUtil.b("1-0 vw 88 111"))));
        Assert.assertEquals(1, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getMinute().size());
        Assert.assertEquals(1, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getHourly().size());
        Assert.assertEquals(1, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getDaily().size());
        Assert.assertEquals(1, HistoryGraphTest.holder.reportingDiskDao.rawDataCacheForGraphProcessor.rawStorage.size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.rawDataProcessor.rawStorage.size());
    }

    @Test
    public void makeSureReportingIsPresentWhenGraphAssignedToDevice2() throws Exception {
        Superchart enhancedHistoryGraph = new Superchart();
        enhancedHistoryGraph.id = 432;
        enhancedHistoryGraph.width = 8;
        enhancedHistoryGraph.height = 4;
        // no live
        enhancedHistoryGraph.selectedPeriods = new GraphPeriod[]{ ONE_HOUR, SIX_HOURS };
        DataStream dataStream = new DataStream(((short) (88)), PinType.VIRTUAL);
        GraphDataStream graphDataStream = new GraphDataStream(null, GraphType.LINE, 0, 0, dataStream, null, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        enhancedHistoryGraph.dataStreams = new GraphDataStream[]{ graphDataStream };
        clientPair.appClient.createWidget(1, enhancedHistoryGraph);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.reset();
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getMinute().size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getHourly().size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getDaily().size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.rawDataCacheForGraphProcessor.rawStorage.size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.rawDataProcessor.rawStorage.size());
        clientPair.hardwareClient.send("hardware vw 88 111");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new HardwareMessage(1, TestUtil.b("1-0 vw 88 111"))));
        Assert.assertEquals(1, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getMinute().size());
        Assert.assertEquals(1, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getHourly().size());
        Assert.assertEquals(1, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getDaily().size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.rawDataCacheForGraphProcessor.rawStorage.size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.rawDataProcessor.rawStorage.size());
    }

    @Test
    public void makeSureReportingIsPresentWhenGraphAssignedToDeviceTiles() throws Exception {
        long widgetId = 21321;
        DeviceTiles deviceTiles = new DeviceTiles();
        deviceTiles.id = widgetId;
        deviceTiles.x = 8;
        deviceTiles.y = 8;
        deviceTiles.width = 50;
        deviceTiles.height = 100;
        clientPair.appClient.createWidget(1, deviceTiles);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        int[] deviceIds = new int[]{ 0 };
        Superchart enhancedHistoryGraph = new Superchart();
        enhancedHistoryGraph.id = 432;
        enhancedHistoryGraph.width = 8;
        enhancedHistoryGraph.height = 4;
        GraphDataStream graphDataStream = new GraphDataStream(null, GraphType.LINE, 0, (-1), new DataStream(((short) (88)), PinType.VIRTUAL), AggregationFunctionType.MAX, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        enhancedHistoryGraph.dataStreams = new GraphDataStream[]{ graphDataStream };
        TileTemplate tileTemplate = new PageTileTemplate(1, new Widget[]{ enhancedHistoryGraph }, deviceIds, "name", "name", "iconName", BoardType.ESP8266, new DataStream(((short) (1)), PinType.VIRTUAL), false, null, null, null, 0, 0, FontSize.LARGE, false, 2);
        clientPair.appClient.createTemplate(1, widgetId, tileTemplate);
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getMinute().size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getHourly().size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getDaily().size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.rawDataCacheForGraphProcessor.rawStorage.size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.rawDataProcessor.rawStorage.size());
        clientPair.hardwareClient.send("hardware vw 88 111");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new HardwareMessage(1, TestUtil.b("1-0 vw 88 111"))));
        Assert.assertEquals(1, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getMinute().size());
        Assert.assertEquals(1, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getHourly().size());
        Assert.assertEquals(1, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getDaily().size());
        Assert.assertEquals(1, HistoryGraphTest.holder.reportingDiskDao.rawDataCacheForGraphProcessor.rawStorage.size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.rawDataProcessor.rawStorage.size());
    }

    @Test
    public void makeSureReportingIsPresentWhenGraphAssignedToDeviceTilesWith2Pins() throws Exception {
        long widgetId = 21321;
        DeviceTiles deviceTiles = new DeviceTiles();
        deviceTiles.id = widgetId;
        deviceTiles.x = 8;
        deviceTiles.y = 8;
        deviceTiles.width = 50;
        deviceTiles.height = 100;
        clientPair.appClient.createWidget(1, deviceTiles);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        int[] deviceIds = new int[]{ 0 };
        Superchart enhancedHistoryGraph = new Superchart();
        enhancedHistoryGraph.id = 432;
        enhancedHistoryGraph.width = 8;
        enhancedHistoryGraph.height = 4;
        GraphDataStream graphDataStream = new GraphDataStream(null, GraphType.LINE, 0, (-1), new DataStream(((short) (88)), PinType.VIRTUAL), AggregationFunctionType.MAX, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        GraphDataStream graphDataStream2 = new GraphDataStream(null, GraphType.LINE, 0, (-1), new DataStream(((short) (89)), PinType.VIRTUAL), AggregationFunctionType.MAX, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        enhancedHistoryGraph.dataStreams = new GraphDataStream[]{ graphDataStream, graphDataStream2 };
        TileTemplate tileTemplate = new PageTileTemplate(1, new Widget[]{ enhancedHistoryGraph }, deviceIds, "name", "name", "iconName", BoardType.ESP8266, new DataStream(((short) (1)), PinType.VIRTUAL), false, null, null, null, 0, 0, FontSize.LARGE, false, 2);
        clientPair.appClient.createTemplate(1, widgetId, tileTemplate);
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getMinute().size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getHourly().size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getDaily().size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.rawDataCacheForGraphProcessor.rawStorage.size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.rawDataProcessor.rawStorage.size());
        clientPair.hardwareClient.send("hardware vw 88 111");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new HardwareMessage(1, TestUtil.b("1-0 vw 88 111"))));
        clientPair.hardwareClient.send("hardware vw 89 112");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new HardwareMessage(2, TestUtil.b("1-0 vw 89 112"))));
        Assert.assertEquals(2, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getMinute().size());
        Assert.assertEquals(2, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getHourly().size());
        Assert.assertEquals(2, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getDaily().size());
        Assert.assertEquals(2, HistoryGraphTest.holder.reportingDiskDao.rawDataCacheForGraphProcessor.rawStorage.size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.rawDataProcessor.rawStorage.size());
    }

    @Test
    public void makeSureReportingIsPresentWhenGraphAssignedToDeviceSelector() throws Exception {
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        device1.status = Status.OFFLINE;
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        DeviceSelector deviceSelector = new DeviceSelector();
        deviceSelector.id = 200000;
        deviceSelector.x = 0;
        deviceSelector.y = 0;
        deviceSelector.width = 1;
        deviceSelector.height = 1;
        deviceSelector.deviceIds = new int[]{ 0, 1 };
        clientPair.appClient.createWidget(1, deviceSelector);
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        Superchart superchart = new Superchart();
        superchart.id = 432;
        superchart.width = 8;
        superchart.height = 4;
        GraphDataStream graphDataStream = new GraphDataStream(null, GraphType.LINE, 0, ((int) (deviceSelector.id)), new DataStream(((short) (88)), PinType.VIRTUAL), AggregationFunctionType.MAX, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        superchart.dataStreams = new GraphDataStream[]{ graphDataStream };
        clientPair.appClient.createWidget(1, superchart);
        clientPair.appClient.verifyResult(TestUtil.ok(3));
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getMinute().size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getHourly().size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getDaily().size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.rawDataCacheForGraphProcessor.rawStorage.size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.rawDataProcessor.rawStorage.size());
        clientPair.hardwareClient.send("hardware vw 88 111");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new HardwareMessage(1, TestUtil.b("1-0 vw 88 111"))));
        Assert.assertEquals(1, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getMinute().size());
        Assert.assertEquals(1, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getHourly().size());
        Assert.assertEquals(1, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getDaily().size());
        Assert.assertEquals(1, HistoryGraphTest.holder.reportingDiskDao.rawDataCacheForGraphProcessor.rawStorage.size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.rawDataProcessor.rawStorage.size());
    }

    @Test
    public void makeSureReportingIsPresentWhenPinAssignedToReporting() throws Exception {
        ReportingWidget reportingWidget = new ReportingWidget();
        reportingWidget.id = 432;
        reportingWidget.width = 8;
        reportingWidget.height = 4;
        reportingWidget.reportSources = new ReportSource[]{ new cc.blynk.server.core.model.widgets.ui.reporting.source.DeviceReportSource(new ReportDataStream[]{ new ReportDataStream(((short) (88)), PinType.VIRTUAL, null, false) }, new int[]{ 0 }) };
        clientPair.appClient.createWidget(1, reportingWidget);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.reset();
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getMinute().size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getHourly().size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getDaily().size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.rawDataCacheForGraphProcessor.rawStorage.size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.rawDataProcessor.rawStorage.size());
        clientPair.hardwareClient.send("hardware vw 88 111");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new HardwareMessage(1, TestUtil.b("1-0 vw 88 111"))));
        Assert.assertEquals(1, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getMinute().size());
        Assert.assertEquals(1, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getHourly().size());
        Assert.assertEquals(1, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getDaily().size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.rawDataCacheForGraphProcessor.rawStorage.size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.rawDataProcessor.rawStorage.size());
    }

    @Test
    public void makeSureReportingIsPresentWhenPinAssignedToReporting2() throws Exception {
        ReportingWidget reportingWidget = new ReportingWidget();
        reportingWidget.id = 432;
        reportingWidget.width = 8;
        reportingWidget.height = 4;
        reportingWidget.reportSources = new ReportSource[]{ new cc.blynk.server.core.model.widgets.ui.reporting.source.TileTemplateReportSource(new ReportDataStream[]{ new ReportDataStream(((short) (88)), PinType.VIRTUAL, null, false) }, 0, new int[]{ 0 }) };
        clientPair.appClient.createWidget(1, reportingWidget);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.reset();
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getMinute().size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getHourly().size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getDaily().size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.rawDataCacheForGraphProcessor.rawStorage.size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.rawDataProcessor.rawStorage.size());
        clientPair.hardwareClient.send("hardware vw 88 111");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new HardwareMessage(1, TestUtil.b("1-0 vw 88 111"))));
        Assert.assertEquals(1, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getMinute().size());
        Assert.assertEquals(1, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getHourly().size());
        Assert.assertEquals(1, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getDaily().size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.rawDataCacheForGraphProcessor.rawStorage.size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.rawDataProcessor.rawStorage.size());
    }

    @Test
    public void makeSureReportingIsPresentWhenPinAssignedToReporting3() throws Exception {
        ReportingWidget reportingWidget = new ReportingWidget();
        reportingWidget.id = 432;
        reportingWidget.width = 8;
        reportingWidget.height = 4;
        reportingWidget.reportSources = new ReportSource[]{ new cc.blynk.server.core.model.widgets.ui.reporting.source.TileTemplateReportSource(new ReportDataStream[]{ new ReportDataStream(((short) (88)), PinType.VIRTUAL, null, false), new ReportDataStream(((short) (89)), PinType.VIRTUAL, null, false) }, 0, new int[]{ 0 }) };
        clientPair.appClient.createWidget(1, reportingWidget);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.reset();
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getMinute().size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getHourly().size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getDaily().size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.rawDataCacheForGraphProcessor.rawStorage.size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.rawDataProcessor.rawStorage.size());
        clientPair.hardwareClient.send("hardware vw 89 111");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new HardwareMessage(1, TestUtil.b("1-0 vw 89 111"))));
        Assert.assertEquals(1, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getMinute().size());
        Assert.assertEquals(1, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getHourly().size());
        Assert.assertEquals(1, HistoryGraphTest.holder.reportingDiskDao.averageAggregator.getDaily().size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.rawDataCacheForGraphProcessor.rawStorage.size());
        Assert.assertEquals(0, HistoryGraphTest.holder.reportingDiskDao.rawDataProcessor.rawStorage.size());
    }

    @Test
    public void testGetLIVEGraphDataForEnhancedGraph() throws Exception {
        String tempDir = HistoryGraphTest.holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        Superchart enhancedHistoryGraph = new Superchart();
        enhancedHistoryGraph.id = 432;
        enhancedHistoryGraph.width = 8;
        enhancedHistoryGraph.height = 4;
        DataStream dataStream = new DataStream(((short) (88)), PinType.VIRTUAL);
        GraphDataStream graphDataStream = new GraphDataStream(null, GraphType.LINE, 0, 0, dataStream, null, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        enhancedHistoryGraph.dataStreams = new GraphDataStream[]{ graphDataStream };
        clientPair.appClient.createWidget(1, enhancedHistoryGraph);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.reset();
        clientPair.appClient.getEnhancedGraphData(1, 432, LIVE);
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new cc.blynk.server.core.protocol.model.messages.ResponseMessage(1, NO_DATA)));
        clientPair.hardwareClient.send("hardware vw 88 111");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new HardwareMessage(1, TestUtil.b("1-0 vw 88 111"))));
        clientPair.appClient.reset();
        clientPair.appClient.getEnhancedGraphData(1, 432, LIVE);
        BinaryMessage graphDataResponse = clientPair.appClient.getBinaryBody();
        Assert.assertNotNull(graphDataResponse);
        byte[] decompressedGraphData = BaseTest.decompress(graphDataResponse.getBytes());
        ByteBuffer bb = ByteBuffer.wrap(decompressedGraphData);
        Assert.assertEquals(1, bb.getInt());
        Assert.assertEquals(1, bb.getInt());
        Assert.assertEquals(111.0, bb.getDouble(), 0.1);
        Assert.assertEquals(System.currentTimeMillis(), bb.getLong(), 2000);
        for (int i = 1; i <= 60; i++) {
            clientPair.hardwareClient.send(("hardware vw 88 " + i));
        }
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(10000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new HardwareMessage(61, TestUtil.b("1-0 vw 88 60"))));
        clientPair.appClient.reset();
        clientPair.appClient.getEnhancedGraphData(1, 432, LIVE);
        graphDataResponse = clientPair.appClient.getBinaryBody();
        Assert.assertNotNull(graphDataResponse);
        decompressedGraphData = BaseTest.decompress(graphDataResponse.getBytes());
        bb = ByteBuffer.wrap(decompressedGraphData);
        Assert.assertEquals(1, bb.getInt());
        Assert.assertEquals(60, bb.getInt());
        for (int i = 1; i <= 60; i++) {
            Assert.assertEquals(i, bb.getDouble(), 0.1);
            Assert.assertEquals(System.currentTimeMillis(), bb.getLong(), 10000);
        }
    }

    @Test
    public void testNoLiveDataWhenNoGraph() throws Exception {
        String tempDir = HistoryGraphTest.holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        clientPair.hardwareClient.send("hardware vw 88 111");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new HardwareMessage(1, TestUtil.b("1-0 vw 88 111"))));
        Superchart enhancedHistoryGraph = new Superchart();
        enhancedHistoryGraph.id = 432;
        enhancedHistoryGraph.width = 8;
        enhancedHistoryGraph.height = 4;
        DataStream dataStream = new DataStream(((short) (88)), PinType.VIRTUAL);
        GraphDataStream graphDataStream = new GraphDataStream(null, GraphType.LINE, 0, 0, dataStream, null, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        enhancedHistoryGraph.dataStreams = new GraphDataStream[]{ graphDataStream };
        clientPair.appClient.createWidget(1, enhancedHistoryGraph);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.getEnhancedGraphData(1, 432, LIVE);
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new cc.blynk.server.core.protocol.model.messages.ResponseMessage(2, NO_DATA)));
    }

    @Test
    public void testNoLiveDataWhenNoGraph2() throws Exception {
        String tempDir = HistoryGraphTest.holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        clientPair.hardwareClient.send("hardware vw 88 111");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new HardwareMessage(1, TestUtil.b("1-0 vw 88 111"))));
        Superchart enhancedHistoryGraph = new Superchart();
        enhancedHistoryGraph.id = 432;
        enhancedHistoryGraph.width = 8;
        enhancedHistoryGraph.height = 4;
        DataStream dataStream = new DataStream(((short) (88)), PinType.VIRTUAL);
        GraphDataStream graphDataStream = new GraphDataStream(null, GraphType.LINE, 0, 0, dataStream, null, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        enhancedHistoryGraph.dataStreams = new GraphDataStream[]{ graphDataStream };
        clientPair.appClient.createWidget(1, enhancedHistoryGraph);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.getEnhancedGraphData(1, 432, LIVE);
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new cc.blynk.server.core.protocol.model.messages.ResponseMessage(2, NO_DATA)));
        clientPair.hardwareClient.send("hardware vw 88 111");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new HardwareMessage(2, TestUtil.b("1-0 vw 88 111"))));
        clientPair.appClient.reset();
        clientPair.appClient.getEnhancedGraphData(1, 432, LIVE);
        BinaryMessage graphDataResponse = clientPair.appClient.getBinaryBody();
        Assert.assertNotNull(graphDataResponse);
        byte[] decompressedGraphData = BaseTest.decompress(graphDataResponse.getBytes());
        ByteBuffer bb = ByteBuffer.wrap(decompressedGraphData);
        Assert.assertEquals(1, bb.getInt());
        Assert.assertEquals(1, bb.getInt());
        Assert.assertEquals(111.0, bb.getDouble(), 0.1);
        Assert.assertEquals(System.currentTimeMillis(), bb.getLong(), 2000);
        clientPair.appClient.deleteWidget(1, 432);
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        clientPair.hardwareClient.send("hardware vw 88 111");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new HardwareMessage(3, TestUtil.b("1-0 vw 88 111"))));
        clientPair.appClient.createWidget(1, enhancedHistoryGraph);
        clientPair.appClient.verifyResult(TestUtil.ok(3));
        clientPair.appClient.send(("getenhanceddata 1" + (TestUtil.b(" 432 LIVE"))));
        clientPair.appClient.reset();
        graphDataResponse = clientPair.appClient.getBinaryBody();
        Assert.assertNotNull(graphDataResponse);
        decompressedGraphData = BaseTest.decompress(graphDataResponse.getBytes());
        bb = ByteBuffer.wrap(decompressedGraphData);
        Assert.assertEquals(1, bb.getInt());
        Assert.assertEquals(1, bb.getInt());
        Assert.assertEquals(111.0, bb.getDouble(), 0.1);
        Assert.assertEquals(System.currentTimeMillis(), bb.getLong(), 2000);
    }

    @Test
    public void testGetLIVEGraphDataForEnhancedGraphWithPaging() throws Exception {
        String tempDir = HistoryGraphTest.holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        Superchart enhancedHistoryGraph = new Superchart();
        enhancedHistoryGraph.id = 432;
        enhancedHistoryGraph.width = 8;
        enhancedHistoryGraph.height = 4;
        DataStream dataStream = new DataStream(((short) (88)), PinType.VIRTUAL);
        GraphDataStream graphDataStream = new GraphDataStream(null, GraphType.LINE, 0, 0, dataStream, null, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        enhancedHistoryGraph.dataStreams = new GraphDataStream[]{ graphDataStream };
        clientPair.appClient.createWidget(1, enhancedHistoryGraph);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.reset();
        clientPair.appClient.getEnhancedGraphData(1, 432, LIVE);
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new cc.blynk.server.core.protocol.model.messages.ResponseMessage(1, NO_DATA)));
        clientPair.hardwareClient.send("hardware vw 88 111");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new HardwareMessage(1, TestUtil.b("1-0 vw 88 111"))));
        clientPair.appClient.reset();
        clientPair.appClient.getEnhancedGraphData(1, 432, LIVE);
        BinaryMessage graphDataResponse = clientPair.appClient.getBinaryBody();
        Assert.assertNotNull(graphDataResponse);
        byte[] decompressedGraphData = BaseTest.decompress(graphDataResponse.getBytes());
        ByteBuffer bb = ByteBuffer.wrap(decompressedGraphData);
        Assert.assertEquals(1, bb.getInt());
        Assert.assertEquals(1, bb.getInt());
        Assert.assertEquals(111.0, bb.getDouble(), 0.1);
        Assert.assertEquals(System.currentTimeMillis(), bb.getLong(), 2000);
        for (int i = 1; i <= 60; i++) {
            clientPair.hardwareClient.send(("hardware vw 88 " + i));
        }
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(10000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new HardwareMessage(61, TestUtil.b("1-0 vw 88 60"))));
        clientPair.appClient.reset();
        clientPair.appClient.getEnhancedGraphData(1, 432, LIVE);
        graphDataResponse = clientPair.appClient.getBinaryBody();
        Assert.assertNotNull(graphDataResponse);
        decompressedGraphData = BaseTest.decompress(graphDataResponse.getBytes());
        bb = ByteBuffer.wrap(decompressedGraphData);
        Assert.assertEquals(1, bb.getInt());
        Assert.assertEquals(60, bb.getInt());
        for (int i = 1; i <= 60; i++) {
            Assert.assertEquals(i, bb.getDouble(), 0.1);
            Assert.assertEquals(System.currentTimeMillis(), bb.getLong(), 10000);
        }
    }

    @Test
    public void testPagingWorksForGetEnhancedHistoryDataPartialData() throws Exception {
        String tempDir = HistoryGraphTest.holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        Path pinReportingDataPath = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, DIGITAL, ((short) (8)), MINUTE));
        try (DataOutputStream dos = new DataOutputStream(Files.newOutputStream(pinReportingDataPath, StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
            for (int i = 1; i <= 61; i++) {
                dos.writeDouble(i);
                dos.writeLong((i * 1000));
            }
            dos.flush();
        }
        Superchart enhancedHistoryGraph = new Superchart();
        enhancedHistoryGraph.id = 432;
        enhancedHistoryGraph.width = 8;
        enhancedHistoryGraph.height = 4;
        DataStream dataStream = new DataStream(((short) (8)), PinType.DIGITAL);
        GraphDataStream graphDataStream = new GraphDataStream(null, GraphType.LINE, 0, 0, dataStream, null, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        enhancedHistoryGraph.dataStreams = new GraphDataStream[]{ graphDataStream };
        clientPair.appClient.createWidget(1, enhancedHistoryGraph);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.reset();
        clientPair.appClient.getEnhancedGraphData(1, 432, ONE_HOUR, 1);
        BinaryMessage graphDataResponse = clientPair.appClient.getBinaryBody();
        Assert.assertNotNull(graphDataResponse);
        byte[] decompressedGraphData = BaseTest.decompress(graphDataResponse.getBytes());
        ByteBuffer bb = ByteBuffer.wrap(decompressedGraphData);
        Assert.assertEquals(1, bb.getInt());
        Assert.assertEquals(1, bb.getInt());
        Assert.assertEquals(1.0, bb.getDouble(), 0.1);
        Assert.assertEquals(1000, bb.getLong());
    }

    @Test
    public void testPagingWorksForGetEnhancedHistoryDataFullData() throws Exception {
        String tempDir = HistoryGraphTest.holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        Path pinReportingDataPath = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, DIGITAL, ((short) (8)), MINUTE));
        try (DataOutputStream dos = new DataOutputStream(Files.newOutputStream(pinReportingDataPath, StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
            for (int i = 1; i <= 120; i++) {
                dos.writeDouble(i);
                dos.writeLong((i * 1000));
            }
            dos.flush();
        }
        Superchart enhancedHistoryGraph = new Superchart();
        enhancedHistoryGraph.id = 432;
        enhancedHistoryGraph.width = 8;
        enhancedHistoryGraph.height = 4;
        DataStream dataStream = new DataStream(((short) (8)), PinType.DIGITAL);
        GraphDataStream graphDataStream = new GraphDataStream(null, GraphType.LINE, 0, 0, dataStream, null, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        enhancedHistoryGraph.dataStreams = new GraphDataStream[]{ graphDataStream };
        clientPair.appClient.createWidget(1, enhancedHistoryGraph);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.reset();
        clientPair.appClient.getEnhancedGraphData(1, 432, ONE_HOUR, 1);
        BinaryMessage graphDataResponse = clientPair.appClient.getBinaryBody();
        Assert.assertNotNull(graphDataResponse);
        byte[] decompressedGraphData = BaseTest.decompress(graphDataResponse.getBytes());
        ByteBuffer bb = ByteBuffer.wrap(decompressedGraphData);
        Assert.assertEquals(1, bb.getInt());
        Assert.assertEquals(60, bb.getInt());
        for (int i = 1; i <= 60; i++) {
            Assert.assertEquals(i, bb.getDouble(), 0.1);
            Assert.assertEquals((i * 1000), bb.getLong());
        }
    }

    @Test
    public void testPagingWorksForGetEnhancedHistoryDataFullDataAndSecondPage() throws Exception {
        String tempDir = HistoryGraphTest.holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        Path pinReportingDataPath = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, DIGITAL, ((short) (8)), MINUTE));
        try (DataOutputStream dos = new DataOutputStream(Files.newOutputStream(pinReportingDataPath, StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
            for (int i = 1; i <= 120; i++) {
                dos.writeDouble(i);
                dos.writeLong((i * 1000));
            }
            dos.flush();
        }
        Superchart enhancedHistoryGraph = new Superchart();
        enhancedHistoryGraph.id = 432;
        enhancedHistoryGraph.width = 8;
        enhancedHistoryGraph.height = 4;
        DataStream dataStream = new DataStream(((short) (8)), PinType.DIGITAL);
        GraphDataStream graphDataStream = new GraphDataStream(null, GraphType.LINE, 0, 0, dataStream, null, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        enhancedHistoryGraph.dataStreams = new GraphDataStream[]{ graphDataStream };
        clientPair.appClient.createWidget(1, enhancedHistoryGraph);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.reset();
        clientPair.appClient.getEnhancedGraphData(1, 432, ONE_HOUR, 5);
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new cc.blynk.server.core.protocol.model.messages.ResponseMessage(1, NO_DATA)));
    }

    @Test
    public void testPagingWorksForGetEnhancedHistoryDataWhenNoData() throws Exception {
        String tempDir = HistoryGraphTest.holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        Path pinReportingDataPath = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, DIGITAL, ((short) (8)), MINUTE));
        try (DataOutputStream dos = new DataOutputStream(Files.newOutputStream(pinReportingDataPath, StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
            for (int i = 1; i <= 60; i++) {
                dos.writeDouble(i);
                dos.writeLong((i * 1000));
            }
            dos.flush();
        }
        Superchart enhancedHistoryGraph = new Superchart();
        enhancedHistoryGraph.id = 432;
        enhancedHistoryGraph.width = 8;
        enhancedHistoryGraph.height = 4;
        DataStream dataStream = new DataStream(((short) (8)), PinType.DIGITAL);
        GraphDataStream graphDataStream = new GraphDataStream(null, GraphType.LINE, 0, 0, dataStream, null, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        enhancedHistoryGraph.dataStreams = new GraphDataStream[]{ graphDataStream };
        clientPair.appClient.createWidget(1, enhancedHistoryGraph);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.reset();
        clientPair.appClient.getEnhancedGraphData(1, 432, ONE_HOUR, 1);
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new cc.blynk.server.core.protocol.model.messages.ResponseMessage(1, NO_DATA)));
    }

    @Test
    public void testDeleteWorksForEnhancedGraph() throws Exception {
        Superchart enhancedHistoryGraph = new Superchart();
        enhancedHistoryGraph.id = 432;
        enhancedHistoryGraph.width = 8;
        enhancedHistoryGraph.height = 4;
        DataStream dataStream = new DataStream(((short) (8)), PinType.DIGITAL);
        GraphDataStream graphDataStream = new GraphDataStream(null, GraphType.LINE, 0, 0, dataStream, null, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        enhancedHistoryGraph.dataStreams = new GraphDataStream[]{ graphDataStream };
        clientPair.appClient.createWidget(1, enhancedHistoryGraph);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.send(("deleteEnhancedData 1\u0000" + "432"));
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(1000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(2)));
        clientPair.appClient.reset();
        clientPair.appClient.getEnhancedGraphData(1, 432, DAY);
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new cc.blynk.server.core.protocol.model.messages.ResponseMessage(1, NO_DATA)));
    }

    @Test
    public void testExportDataFromHistoryGraph() throws Exception {
        clientPair.appClient.send("export 1");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.illegalCommand(1)));
        clientPair.appClient.send("export 1 666");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.illegalCommand(2)));
        clientPair.appClient.send("export 1 1");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.illegalCommand(3)));
        clientPair.appClient.send("export 1 191600");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new cc.blynk.server.core.protocol.model.messages.ResponseMessage(4, NO_DATA)));
        // generate fake reporting data
        Path userReportDirectory = Paths.get(HistoryGraphTest.holder.props.getProperty("data.folder"), "data", CounterBase.getUserName());
        Files.createDirectories(userReportDirectory);
        Path userReportFile = Paths.get(userReportDirectory.toString(), ReportingDiskDao.generateFilename(1, 0, ANALOG, ((short) (7)), MINUTE));
        FileUtils.write(userReportFile, 1.1, 1L);
        FileUtils.write(userReportFile, 2.2, 2L);
        clientPair.appClient.send("export 1 191600");
        Mockito.verify(HistoryGraphTest.holder.mailWrapper, Mockito.timeout(1000)).sendHtml(ArgumentMatchers.eq(CounterBase.getUserName()), ArgumentMatchers.eq("History graph data for project My Dashboard"), ArgumentMatchers.contains((("/" + (CounterBase.getUserName())) + "_1_0_a7_")));
    }

    @Test
    public void testGeneratedCSVIsCorrect() throws Exception {
        // generate fake reporting data
        Path userReportDirectory = Paths.get(HistoryGraphTest.holder.props.getProperty("data.folder"), "data", CounterBase.getUserName());
        Files.createDirectories(userReportDirectory);
        String filename = ReportingDiskDao.generateFilename(1, 0, ANALOG, ((short) (7)), MINUTE);
        Path userReportFile = Paths.get(userReportDirectory.toString(), filename);
        FileUtils.write(userReportFile, 1.1, 1L);
        FileUtils.write(userReportFile, 2.2, 2L);
        clientPair.appClient.send("export 1 191600");
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        String csvFileName = HistoryGraphTest.getFileNameByMask(((CounterBase.getUserName()) + "_1_0_a7_"));
        Mockito.verify(HistoryGraphTest.holder.mailWrapper, Mockito.timeout(1000)).sendHtml(ArgumentMatchers.eq(CounterBase.getUserName()), ArgumentMatchers.eq("History graph data for project My Dashboard"), ArgumentMatchers.contains(csvFileName));
        try (InputStream fileStream = new FileInputStream(Paths.get(HistoryGraphTest.blynkTempDir, csvFileName).toString());InputStream gzipStream = new GZIPInputStream(fileStream);BufferedReader buffered = new BufferedReader(new InputStreamReader(gzipStream))) {
            String[] lineSplit = buffered.readLine().split(",");
            Assert.assertEquals(1.1, Double.parseDouble(lineSplit[0]), 0.001);
            Assert.assertEquals(1, Long.parseLong(lineSplit[1]));
            Assert.assertEquals(0, Long.parseLong(lineSplit[2]));
            lineSplit = buffered.readLine().split(",");
            Assert.assertEquals(2.2, Double.parseDouble(lineSplit[0]), 0.001);
            Assert.assertEquals(2, Long.parseLong(lineSplit[1]));
            Assert.assertEquals(0, Long.parseLong(lineSplit[2]));
        }
    }

    @Test
    public void testGeneratedCSVIsCorrectForMultiDevicesNoData() throws Exception {
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        device1.status = Status.OFFLINE;
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        clientPair.appClient.createWidget(1, "{\"id\":200000, \"deviceIds\":[0,1], \"width\":1, \"height\":1, \"x\":0, \"y\":0, \"label\":\"Some Text\", \"type\":\"DEVICE_SELECTOR\"}");
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        clientPair.appClient.reset();
        clientPair.appClient.send("export 1-200000 191600");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new cc.blynk.server.core.protocol.model.messages.ResponseMessage(1, NO_DATA)));
    }

    @Test
    public void cleanNotUsedPinDataWorksAsExpected() throws Exception {
        HistoryGraphUnusedPinDataCleanerWorker cleaner = new HistoryGraphUnusedPinDataCleanerWorker(HistoryGraphTest.holder.userDao, HistoryGraphTest.holder.reportingDiskDao);
        String tempDir = HistoryGraphTest.holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        // this file has corresponding history graph
        Path pinReportingDataPath1 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, ANALOG, ((short) (7)), HOURLY));
        FileUtils.write(pinReportingDataPath1, 1.11, 1111111);
        // those are not
        Path pinReportingDataPath2 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, DIGITAL, ((short) (100)), HOURLY));
        FileUtils.write(pinReportingDataPath2, 1.11, 1111111);
        Path pinReportingDataPath3 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, DIGITAL, ((short) (101)), HOURLY));
        FileUtils.write(pinReportingDataPath3, 1.11, 1111111);
        Path pinReportingDataPath4 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, DIGITAL, ((short) (102)), HOURLY));
        FileUtils.write(pinReportingDataPath4, 1.11, 1111111);
        Assert.assertTrue(Files.exists(pinReportingDataPath1));
        Assert.assertTrue(Files.exists(pinReportingDataPath2));
        Assert.assertTrue(Files.exists(pinReportingDataPath3));
        Assert.assertTrue(Files.exists(pinReportingDataPath4));
        // creating widget just to make user profile "updated"
        clientPair.appClient.createWidget(1, "{\"id\":200000, \"deviceIds\":[0,1], \"width\":1, \"height\":1, \"x\":0, \"y\":0, \"label\":\"Some Text\", \"type\":\"DEVICE_SELECTOR\"}");
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        cleaner.run();
        Assert.assertTrue(Files.exists(pinReportingDataPath1));
        Assert.assertTrue(Files.notExists(pinReportingDataPath2));
        Assert.assertTrue(Files.notExists(pinReportingDataPath3));
        Assert.assertTrue(Files.notExists(pinReportingDataPath4));
    }

    @Test
    public void cleanNotUsedPinDataWorksAsExpectedForSuperChart() throws Exception {
        HistoryGraphUnusedPinDataCleanerWorker cleaner = new HistoryGraphUnusedPinDataCleanerWorker(HistoryGraphTest.holder.userDao, HistoryGraphTest.holder.reportingDiskDao);
        Superchart enhancedHistoryGraph = new Superchart();
        enhancedHistoryGraph.id = 432;
        enhancedHistoryGraph.width = 8;
        enhancedHistoryGraph.height = 4;
        DataStream dataStream1 = new DataStream(((short) (8)), PinType.DIGITAL);
        DataStream dataStream2 = new DataStream(((short) (9)), PinType.DIGITAL);
        DataStream dataStream3 = new DataStream(((short) (10)), PinType.DIGITAL);
        GraphDataStream graphDataStream1 = new GraphDataStream(null, GraphType.LINE, 0, 0, dataStream1, AggregationFunctionType.MAX, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        GraphDataStream graphDataStream2 = new GraphDataStream(null, GraphType.LINE, 0, 0, dataStream2, AggregationFunctionType.MAX, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        GraphDataStream graphDataStream3 = new GraphDataStream(null, GraphType.LINE, 0, 0, dataStream3, AggregationFunctionType.MAX, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        enhancedHistoryGraph.dataStreams = new GraphDataStream[]{ graphDataStream1, graphDataStream2, graphDataStream3 };
        clientPair.appClient.createWidget(1, enhancedHistoryGraph);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        String tempDir = HistoryGraphTest.holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        // this file has corresponding history graph
        Path pinReportingDataPath1 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, DIGITAL, ((short) (8)), HOURLY));
        FileUtils.write(pinReportingDataPath1, 1.11, 1111111);
        Path pinReportingDataPath2 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, DIGITAL, ((short) (9)), HOURLY));
        FileUtils.write(pinReportingDataPath2, 1.11, 1111111);
        Path pinReportingDataPath3 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, DIGITAL, ((short) (10)), HOURLY));
        FileUtils.write(pinReportingDataPath3, 1.11, 1111111);
        // those are not
        Path pinReportingDataPath4 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, DIGITAL, ((short) (11)), HOURLY));
        FileUtils.write(pinReportingDataPath4, 1.11, 1111111);
        Assert.assertTrue(Files.exists(pinReportingDataPath1));
        Assert.assertTrue(Files.exists(pinReportingDataPath2));
        Assert.assertTrue(Files.exists(pinReportingDataPath3));
        Assert.assertTrue(Files.exists(pinReportingDataPath4));
        cleaner.run();
        Assert.assertTrue(Files.exists(pinReportingDataPath1));
        Assert.assertTrue(Files.exists(pinReportingDataPath2));
        Assert.assertTrue(Files.exists(pinReportingDataPath3));
        Assert.assertTrue(Files.notExists(pinReportingDataPath4));
    }

    @Test
    public void cleanNotUsedPinDataWorksAsExpectedForReportsWidget() throws Exception {
        HistoryGraphUnusedPinDataCleanerWorker cleaner = new HistoryGraphUnusedPinDataCleanerWorker(HistoryGraphTest.holder.userDao, HistoryGraphTest.holder.reportingDiskDao);
        ReportingWidget reportingWidget = new ReportingWidget();
        reportingWidget.id = 432;
        reportingWidget.width = 8;
        reportingWidget.height = 4;
        reportingWidget.reports = new Report[]{ new Report(1, "My One Time Report", new ReportSource[]{ new cc.blynk.server.core.model.widgets.ui.reporting.source.TileTemplateReportSource(new ReportDataStream[]{ new ReportDataStream(((short) (88)), PinType.VIRTUAL, null, false), new ReportDataStream(((short) (89)), PinType.VIRTUAL, null, false) }, 0, new int[]{ 0, 1 }) }, new OneTimeReport(86400), "test@gmail.com", GraphGranularityType.MINUTE, true, CSV_FILE_PER_DEVICE_PER_PIN, null, ZoneId.of("UTC"), 0, 0, null) };
        clientPair.appClient.createWidget(1, reportingWidget);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.reset();
        String tempDir = HistoryGraphTest.holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        // this file has corresponding history graph
        Path pinReportingDataPath1 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, VIRTUAL, ((short) (88)), MINUTE));
        FileUtils.write(pinReportingDataPath1, 1.11, 1111111);
        Path pinReportingDataPath2 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, VIRTUAL, ((short) (89)), MINUTE));
        FileUtils.write(pinReportingDataPath2, 1.11, 1111111);
        Path pinReportingDataPath12 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 1, VIRTUAL, ((short) (88)), MINUTE));
        FileUtils.write(pinReportingDataPath12, 1.11, 1111111);
        Path pinReportingDataPath22 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 1, VIRTUAL, ((short) (89)), MINUTE));
        FileUtils.write(pinReportingDataPath22, 1.11, 1111111);
        Assert.assertTrue(Files.exists(pinReportingDataPath1));
        Assert.assertTrue(Files.exists(pinReportingDataPath2));
        Assert.assertTrue(Files.exists(pinReportingDataPath12));
        Assert.assertTrue(Files.exists(pinReportingDataPath22));
        cleaner.run();
        Assert.assertTrue(Files.exists(pinReportingDataPath1));
        Assert.assertTrue(Files.exists(pinReportingDataPath2));
        Assert.assertTrue(Files.exists(pinReportingDataPath12));
        Assert.assertTrue(Files.exists(pinReportingDataPath22));
    }

    @Test
    public void cleanNotUsedPinDataWorksAsExpectedForSuperChartInDeviceTiles() throws Exception {
        HistoryGraphUnusedPinDataCleanerWorker cleaner = new HistoryGraphUnusedPinDataCleanerWorker(HistoryGraphTest.holder.userDao, HistoryGraphTest.holder.reportingDiskDao);
        DeviceTiles deviceTiles = new DeviceTiles();
        deviceTiles.id = 21321;
        deviceTiles.x = 8;
        deviceTiles.y = 8;
        deviceTiles.width = 50;
        deviceTiles.height = 100;
        clientPair.appClient.createWidget(1, deviceTiles);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        int[] deviceIds = new int[]{ 1 };
        Superchart enhancedHistoryGraph = new Superchart();
        enhancedHistoryGraph.id = 432;
        enhancedHistoryGraph.width = 8;
        enhancedHistoryGraph.height = 4;
        DataStream dataStream1 = new DataStream(((short) (8)), PinType.DIGITAL);
        DataStream dataStream2 = new DataStream(((short) (9)), PinType.DIGITAL);
        GraphDataStream graphDataStream1 = new GraphDataStream(null, GraphType.LINE, 0, (-1), dataStream1, AggregationFunctionType.MAX, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        GraphDataStream graphDataStream2 = new GraphDataStream(null, GraphType.LINE, 0, (-1), dataStream2, AggregationFunctionType.MAX, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        enhancedHistoryGraph.dataStreams = new GraphDataStream[]{ graphDataStream1, graphDataStream2 };
        PageTileTemplate tileTemplate = new PageTileTemplate(1, new Widget[]{ enhancedHistoryGraph }, deviceIds, "123", "name", "iconName", BoardType.ESP8266, new DataStream(((short) (1)), PinType.VIRTUAL), false, null, null, null, 0, 0, FontSize.LARGE, false, 2);
        clientPair.appClient.send((("createTemplate " + (TestUtil.b((("1 " + (deviceTiles.id)) + " ")))) + (MAPPER.writeValueAsString(tileTemplate))));
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        String tempDir = HistoryGraphTest.holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        // this file has corresponding history graph
        Path pinReportingDataPath1 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 1, DIGITAL, ((short) (8)), HOURLY));
        FileUtils.write(pinReportingDataPath1, 1.11, 1111111);
        Path pinReportingDataPath2 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 1, DIGITAL, ((short) (9)), HOURLY));
        FileUtils.write(pinReportingDataPath2, 1.11, 1111111);
        // those are not
        Path pinReportingDataPath3 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 1, DIGITAL, ((short) (10)), HOURLY));
        FileUtils.write(pinReportingDataPath3, 1.11, 1111111);
        Path pinReportingDataPath4 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 1, DIGITAL, ((short) (11)), HOURLY));
        FileUtils.write(pinReportingDataPath4, 1.11, 1111111);
        Assert.assertTrue(Files.exists(pinReportingDataPath1));
        Assert.assertTrue(Files.exists(pinReportingDataPath2));
        Assert.assertTrue(Files.exists(pinReportingDataPath3));
        Assert.assertTrue(Files.exists(pinReportingDataPath4));
        cleaner.run();
        Assert.assertTrue(Files.exists(pinReportingDataPath1));
        Assert.assertTrue(Files.exists(pinReportingDataPath2));
        Assert.assertTrue(Files.notExists(pinReportingDataPath3));
        Assert.assertTrue(Files.notExists(pinReportingDataPath4));
    }

    @Test
    public void cleanNotUsedPinDataWorksAsExpectedForSuperChartWithDeviceSelector() throws Exception {
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        device1.status = Status.OFFLINE;
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        clientPair.appClient.createWidget(1, "{\"id\":200000, \"deviceIds\":[0,1], \"width\":1, \"height\":1, \"value\":0, \"x\":0, \"y\":0, \"label\":\"Some Text\", \"type\":\"DEVICE_SELECTOR\"}");
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        HistoryGraphUnusedPinDataCleanerWorker cleaner = new HistoryGraphUnusedPinDataCleanerWorker(HistoryGraphTest.holder.userDao, HistoryGraphTest.holder.reportingDiskDao);
        Superchart enhancedHistoryGraph = new Superchart();
        enhancedHistoryGraph.id = 432;
        enhancedHistoryGraph.width = 8;
        enhancedHistoryGraph.height = 4;
        DataStream dataStream1 = new DataStream(((short) (8)), PinType.DIGITAL);
        DataStream dataStream2 = new DataStream(((short) (9)), PinType.DIGITAL);
        DataStream dataStream3 = new DataStream(((short) (10)), PinType.DIGITAL);
        GraphDataStream graphDataStream1 = new GraphDataStream(null, GraphType.LINE, 0, 200000, dataStream1, AggregationFunctionType.MAX, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        GraphDataStream graphDataStream2 = new GraphDataStream(null, GraphType.LINE, 0, 200000, dataStream2, AggregationFunctionType.MAX, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        GraphDataStream graphDataStream3 = new GraphDataStream(null, GraphType.LINE, 0, 200000, dataStream3, AggregationFunctionType.MAX, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        enhancedHistoryGraph.dataStreams = new GraphDataStream[]{ graphDataStream1, graphDataStream2, graphDataStream3 };
        clientPair.appClient.createWidget(1, enhancedHistoryGraph);
        clientPair.appClient.verifyResult(TestUtil.ok(3));
        String tempDir = HistoryGraphTest.holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        // this file has corresponding history graph
        Path pinReportingDataPath10 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, DIGITAL, ((short) (8)), HOURLY));
        FileUtils.write(pinReportingDataPath10, 1.11, 1111111);
        Path pinReportingDataPath20 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, DIGITAL, ((short) (9)), HOURLY));
        FileUtils.write(pinReportingDataPath20, 1.11, 1111111);
        Path pinReportingDataPath30 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, DIGITAL, ((short) (10)), HOURLY));
        FileUtils.write(pinReportingDataPath30, 1.11, 1111111);
        Path pinReportingDataPath11 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 1, DIGITAL, ((short) (8)), HOURLY));
        FileUtils.write(pinReportingDataPath11, 1.11, 1111111);
        Path pinReportingDataPath21 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 1, DIGITAL, ((short) (9)), HOURLY));
        FileUtils.write(pinReportingDataPath21, 1.11, 1111111);
        Path pinReportingDataPath31 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 1, DIGITAL, ((short) (10)), HOURLY));
        FileUtils.write(pinReportingDataPath31, 1.11, 1111111);
        // those are not
        Path pinReportingDataPath40 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, DIGITAL, ((short) (11)), HOURLY));
        FileUtils.write(pinReportingDataPath40, 1.11, 1111111);
        Path pinReportingDataPath41 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 1, DIGITAL, ((short) (11)), HOURLY));
        FileUtils.write(pinReportingDataPath41, 1.11, 1111111);
        // 3 files for device 0
        Assert.assertTrue(Files.exists(pinReportingDataPath10));
        Assert.assertTrue(Files.exists(pinReportingDataPath20));
        Assert.assertTrue(Files.exists(pinReportingDataPath30));
        // 3 files for device 1
        Assert.assertTrue(Files.exists(pinReportingDataPath11));
        Assert.assertTrue(Files.exists(pinReportingDataPath21));
        Assert.assertTrue(Files.exists(pinReportingDataPath31));
        Assert.assertTrue(Files.exists(pinReportingDataPath40));
        Assert.assertTrue(Files.exists(pinReportingDataPath41));
        cleaner.run();
        // 3 files for device 0
        Assert.assertTrue(Files.exists(pinReportingDataPath10));
        Assert.assertTrue(Files.exists(pinReportingDataPath20));
        Assert.assertTrue(Files.exists(pinReportingDataPath30));
        // 3 files for device 1
        Assert.assertTrue(Files.exists(pinReportingDataPath11));
        Assert.assertTrue(Files.exists(pinReportingDataPath21));
        Assert.assertTrue(Files.exists(pinReportingDataPath31));
        Assert.assertTrue(Files.notExists(pinReportingDataPath40));
        Assert.assertTrue(Files.notExists(pinReportingDataPath41));
    }

    @Test
    public void truncateReportingDataWorks() throws Exception {
        ReportingTruncateWorker truncateWorker = new ReportingTruncateWorker(HistoryGraphTest.holder.reportingDiskDao, 10);
        String tempDir = HistoryGraphTest.holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        // this file has corresponding history graph
        Path pinReportingDataPath1 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, ANALOG, ((short) (7)), MINUTE));
        Path pinReportingDataPath2 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, VIRTUAL, ((short) (7)), MINUTE));
        FileUtils.write(pinReportingDataPath2, 1.11, 1);
        Path pinReportingDataPath3 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, VIRTUAL, ((short) (7)), HOURLY));
        int STORAGE_PERIOD = 10;
        // write max amount of data for 1 week + 1 point
        for (int i = 0; i < (((STORAGE_PERIOD * 24) * 60) + 1); i++) {
            FileUtils.write(pinReportingDataPath1, 1.11, i);
            FileUtils.write(pinReportingDataPath3, 1.11, i);
        }
        Assert.assertEquals(((((STORAGE_PERIOD * 24) * 60) + 1) * (ReportingUtil.REPORTING_RECORD_SIZE)), Files.size(pinReportingDataPath1));
        Assert.assertEquals(16, Files.size(pinReportingDataPath2));
        Assert.assertEquals(((((STORAGE_PERIOD * 24) * 60) + 1) * (ReportingUtil.REPORTING_RECORD_SIZE)), Files.size(pinReportingDataPath3));
        truncateWorker.run();
        // expecting truncated file here
        Assert.assertEquals((((STORAGE_PERIOD * 24) * 60) * (ReportingUtil.REPORTING_RECORD_SIZE)), Files.size(pinReportingDataPath1));
        Assert.assertEquals(16, Files.size(pinReportingDataPath2));
        Assert.assertEquals(((((STORAGE_PERIOD * 24) * 60) + 1) * (ReportingUtil.REPORTING_RECORD_SIZE)), Files.size(pinReportingDataPath3));
        // check truncate is correct
        ByteBuffer bb = FileUtils.read(pinReportingDataPath1, ((STORAGE_PERIOD * 24) * 60));
        for (int i = 1; i < (((STORAGE_PERIOD * 24) * 60) + 1); i++) {
            Assert.assertEquals(1.11, bb.getDouble(), 0.001);
            Assert.assertEquals(i, bb.getLong());
        }
        bb = FileUtils.read(pinReportingDataPath3, (((STORAGE_PERIOD * 24) * 60) + 1));
        for (int i = 0; i < (((STORAGE_PERIOD * 24) * 60) + 1); i++) {
            Assert.assertEquals(1.11, bb.getDouble(), 0.001);
            Assert.assertEquals(i, bb.getLong());
        }
    }

    @Test
    public void deleteOldExportFiles() throws Exception {
        Path csvDir = Paths.get(CSV_DIR);
        if (Files.notExists(csvDir)) {
            Files.createDirectories(csvDir);
        }
        // this file has corresponding history graph
        Path csvFile = Paths.get(csvDir.toString(), "123.csv.gz");
        FileUtils.write(csvFile, 1.11, 1);
        Assert.assertTrue(Files.exists(csvFile));
        ReportingTruncateWorker truncateWorker = new ReportingTruncateWorker(HistoryGraphTest.holder.reportingDiskDao, 0, 0);
        truncateWorker.run();
        Assert.assertTrue(Files.notExists(csvFile));
    }

    @Test
    public void doNotTruncateFileWithCorrectSize() throws Exception {
        ReportingTruncateWorker truncateWorker = new ReportingTruncateWorker(HistoryGraphTest.holder.reportingDiskDao, 10);
        String tempDir = HistoryGraphTest.holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        // this file has corresponding history graph
        Path pinReportingDataPath1 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 0, ANALOG, ((short) (7)), MINUTE));
        // write max amount of data for 1 week + 1 point
        for (int i = 0; i < ((7 * 24) * 60); i++) {
            FileUtils.write(pinReportingDataPath1, 1.11, i);
        }
        Assert.assertEquals((((7 * 24) * 60) * (ReportingUtil.REPORTING_RECORD_SIZE)), Files.size(pinReportingDataPath1));
        truncateWorker.run();
        // expecting truncated file here
        Assert.assertEquals((((7 * 24) * 60) * (ReportingUtil.REPORTING_RECORD_SIZE)), Files.size(pinReportingDataPath1));
        // check no truncate
        ByteBuffer bb = FileUtils.read(pinReportingDataPath1, ((7 * 24) * 60));
        for (int i = 0; i < ((7 * 24) * 60); i++) {
            Assert.assertEquals(1.11, bb.getDouble(), 0.001);
            Assert.assertEquals(i, bb.getLong());
        }
        Assert.assertTrue(Files.exists(userReportFolder));
    }

    @Test
    public void truncateReportingDataDontFailsInEmptyFolder() throws Exception {
        ReportingTruncateWorker truncateWorker = new ReportingTruncateWorker(HistoryGraphTest.holder.reportingDiskDao, 10);
        String tempDir = HistoryGraphTest.holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        truncateWorker.run();
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        truncateWorker.run();
    }

    @Test
    public void truncateReportingDataDeletesEmptyFolder() throws Exception {
        ReportingTruncateWorker truncateWorker = new ReportingTruncateWorker(HistoryGraphTest.holder.reportingDiskDao, 10);
        String tempDir = HistoryGraphTest.holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        truncateWorker.run();
        Assert.assertTrue(Files.notExists(userReportFolder));
    }

    @Test
    public void testGeneratedCSVIsCorrectForMultiDevicesAndEnhancedGraph() throws Exception {
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        device1.status = Status.OFFLINE;
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        clientPair.appClient.createWidget(1, "{\"id\":200000, \"deviceIds\":[0,1], \"width\":1, \"height\":1, \"x\":0, \"y\":0, \"label\":\"Some Text\", \"type\":\"DEVICE_SELECTOR\"}");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(2)));
        Superchart enhancedHistoryGraph = new Superchart();
        enhancedHistoryGraph.id = 432;
        enhancedHistoryGraph.width = 8;
        enhancedHistoryGraph.height = 4;
        DataStream dataStream = new DataStream(((short) (8)), PinType.DIGITAL);
        GraphDataStream graphDataStream = new GraphDataStream(null, GraphType.LINE, 0, 200000, dataStream, null, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        enhancedHistoryGraph.dataStreams = new GraphDataStream[]{ graphDataStream };
        clientPair.appClient.createWidget(1, enhancedHistoryGraph);
        clientPair.appClient.verifyResult(TestUtil.ok(3));
        clientPair.appClient.reset();
        // generate fake reporting data
        Path userReportDirectory = Paths.get(HistoryGraphTest.holder.props.getProperty("data.folder"), "data", CounterBase.getUserName());
        Files.createDirectories(userReportDirectory);
        String filename = ReportingDiskDao.generateFilename(1, 0, DIGITAL, ((short) (8)), MINUTE);
        Path userReportFile = Paths.get(userReportDirectory.toString(), filename);
        FileUtils.write(userReportFile, 1.1, 1L);
        FileUtils.write(userReportFile, 2.2, 2L);
        filename = ReportingDiskDao.generateFilename(1, 1, DIGITAL, ((short) (8)), MINUTE);
        userReportFile = Paths.get(userReportDirectory.toString(), filename);
        FileUtils.write(userReportFile, 11.1, 11L);
        FileUtils.write(userReportFile, 12.2, 12L);
        clientPair.appClient.send("export 1 432");
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        String csvFileName = HistoryGraphTest.getFileNameByMask(((CounterBase.getUserName()) + "_1_200000_d8_"));
        Mockito.verify(HistoryGraphTest.holder.mailWrapper, Mockito.timeout(1000)).sendHtml(ArgumentMatchers.eq(CounterBase.getUserName()), ArgumentMatchers.eq("History graph data for project My Dashboard"), ArgumentMatchers.contains(csvFileName));
        try (InputStream fileStream = new FileInputStream(Paths.get(HistoryGraphTest.blynkTempDir, csvFileName).toString());InputStream gzipStream = new GZIPInputStream(fileStream);BufferedReader buffered = new BufferedReader(new InputStreamReader(gzipStream))) {
            // first device
            String[] lineSplit = buffered.readLine().split(",");
            Assert.assertEquals(1.1, Double.parseDouble(lineSplit[0]), 0.001);
            Assert.assertEquals(1, Long.parseLong(lineSplit[1]));
            Assert.assertEquals(0, Long.parseLong(lineSplit[2]));
            lineSplit = buffered.readLine().split(",");
            Assert.assertEquals(2.2, Double.parseDouble(lineSplit[0]), 0.001);
            Assert.assertEquals(2, Long.parseLong(lineSplit[1]));
            Assert.assertEquals(0, Long.parseLong(lineSplit[2]));
            // second device
            lineSplit = buffered.readLine().split(",");
            Assert.assertEquals(11.1, Double.parseDouble(lineSplit[0]), 0.001);
            Assert.assertEquals(11, Long.parseLong(lineSplit[1]));
            Assert.assertEquals(1, Long.parseLong(lineSplit[2]));
            lineSplit = buffered.readLine().split(",");
            Assert.assertEquals(12.2, Double.parseDouble(lineSplit[0]), 0.001);
            Assert.assertEquals(12, Long.parseLong(lineSplit[1]));
            Assert.assertEquals(1, Long.parseLong(lineSplit[2]));
        }
    }

    @Test
    public void testGeneratedCSVIsCorrectForMultiDevices() throws Exception {
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        device1.status = Status.OFFLINE;
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        clientPair.appClient.createWidget(1, "{\"id\":200000, \"deviceIds\":[0,1], \"width\":1, \"height\":1, \"x\":0, \"y\":0, \"label\":\"Some Text\", \"type\":\"DEVICE_SELECTOR\"}");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(2)));
        Superchart superchart = new Superchart();
        superchart.id = 191600;
        superchart.width = 8;
        superchart.height = 4;
        DataStream dataStream = new DataStream(((short) (7)), PinType.ANALOG);
        GraphDataStream graphDataStream = new GraphDataStream(null, GraphType.LINE, 0, 200000, dataStream, null, 0, null, null, null, 0, 0, false, null, false, false, false, null, 0, false, 0);
        superchart.dataStreams = new GraphDataStream[]{ graphDataStream };
        clientPair.appClient.updateWidget(1, superchart);
        clientPair.appClient.verifyResult(TestUtil.ok(3));
        clientPair.appClient.reset();
        // generate fake reporting data
        Path userReportDirectory = Paths.get(HistoryGraphTest.holder.props.getProperty("data.folder"), "data", CounterBase.getUserName());
        Files.createDirectories(userReportDirectory);
        String filename = ReportingDiskDao.generateFilename(1, 0, ANALOG, ((short) (7)), MINUTE);
        Path userReportFile = Paths.get(userReportDirectory.toString(), filename);
        FileUtils.write(userReportFile, 1.1, 1L);
        FileUtils.write(userReportFile, 2.2, 2L);
        filename = ReportingDiskDao.generateFilename(1, 1, ANALOG, ((short) (7)), MINUTE);
        userReportFile = Paths.get(userReportDirectory.toString(), filename);
        FileUtils.write(userReportFile, 11.1, 11L);
        FileUtils.write(userReportFile, 12.2, 12L);
        clientPair.appClient.send("export 1 191600");
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        String csvFileName = HistoryGraphTest.getFileNameByMask(((CounterBase.getUserName()) + "_1_200000_a7_"));
        Mockito.verify(HistoryGraphTest.holder.mailWrapper, Mockito.timeout(1000)).sendHtml(ArgumentMatchers.eq(CounterBase.getUserName()), ArgumentMatchers.eq("History graph data for project My Dashboard"), ArgumentMatchers.contains(csvFileName));
        try (InputStream fileStream = new FileInputStream(Paths.get(HistoryGraphTest.blynkTempDir, csvFileName).toString());InputStream gzipStream = new GZIPInputStream(fileStream);BufferedReader buffered = new BufferedReader(new InputStreamReader(gzipStream))) {
            // first device
            String[] lineSplit = buffered.readLine().split(",");
            Assert.assertEquals(1.1, Double.parseDouble(lineSplit[0]), 0.001);
            Assert.assertEquals(1, Long.parseLong(lineSplit[1]));
            Assert.assertEquals(0, Long.parseLong(lineSplit[2]));
            lineSplit = buffered.readLine().split(",");
            Assert.assertEquals(2.2, Double.parseDouble(lineSplit[0]), 0.001);
            Assert.assertEquals(2, Long.parseLong(lineSplit[1]));
            Assert.assertEquals(0, Long.parseLong(lineSplit[2]));
            // second device
            lineSplit = buffered.readLine().split(",");
            Assert.assertEquals(11.1, Double.parseDouble(lineSplit[0]), 0.001);
            Assert.assertEquals(11, Long.parseLong(lineSplit[1]));
            Assert.assertEquals(1, Long.parseLong(lineSplit[2]));
            lineSplit = buffered.readLine().split(",");
            Assert.assertEquals(12.2, Double.parseDouble(lineSplit[0]), 0.001);
            Assert.assertEquals(12, Long.parseLong(lineSplit[1]));
            Assert.assertEquals(1, Long.parseLong(lineSplit[2]));
        }
    }
}

