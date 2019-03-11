package cc.blynk.server.core.reporting.average;


import AverageAggregatorProcessor.DAILY_TEMP_FILENAME;
import AverageAggregatorProcessor.HOURLY_TEMP_FILENAME;
import PinType.VIRTUAL;
import cc.blynk.server.core.dao.ReportingDiskDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.utils.AppNameUtil;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Assert;
import org.junit.Test;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 29.08.15.
 */
public class AverageAggregatorTest {
    private final String reportingFolder = Paths.get(System.getProperty("java.io.tmpdir"), "data").toString();

    @Test
    public void testAverageWorksOkForOnePin() {
        AverageAggregatorProcessor averageAggregator = new AverageAggregatorProcessor("");
        User user = new User();
        user.email = "test@test.com";
        user.appName = AppNameUtil.BLYNK;
        PinType pinType = PinType.VIRTUAL;
        int dashId = 1;
        short pin = 1;
        long ts = AverageAggregatorTest.getMillis(2015, 8, 1, 0, 0);
        int COUNT = 100;
        double expectedAverage = 0;
        for (int i = 0; i < COUNT; i++) {
            expectedAverage += i;
            averageAggregator.collect(new cc.blynk.server.core.reporting.raw.BaseReportingKey(user.email, user.appName, dashId, 0, pinType, pin), ts, i);
        }
        expectedAverage /= COUNT;
        Assert.assertEquals(1, averageAggregator.getHourly().size());
        Assert.assertEquals(1, averageAggregator.getDaily().size());
        Assert.assertEquals(expectedAverage, averageAggregator.getHourly().get(new AggregationKey(user.email, user.appName, dashId, 0, pinType, pin, (ts / (AverageAggregatorProcessor.HOUR)))).calcAverage(), 0);
        Assert.assertEquals(expectedAverage, averageAggregator.getDaily().get(new AggregationKey(user.email, user.appName, dashId, 0, pinType, pin, (ts / (AverageAggregatorProcessor.DAY)))).calcAverage(), 0);
    }

    @Test
    public void testAverageWorksForOneDay() {
        AverageAggregatorProcessor averageAggregator = new AverageAggregatorProcessor("");
        User user = new User();
        user.email = "test@test.com";
        user.appName = AppNameUtil.BLYNK;
        PinType pinType = PinType.VIRTUAL;
        int dashId = 1;
        short pin = 1;
        double expectedDailyAverage = 0;
        int COUNT = 100;
        for (int hour = 0; hour < 24; hour++) {
            long ts = AverageAggregatorTest.getMillis(2015, 8, 1, hour, 0);
            double expectedAverage = 0;
            for (int i = 0; i < COUNT; i++) {
                expectedAverage += i;
                averageAggregator.collect(new cc.blynk.server.core.reporting.raw.BaseReportingKey(user.email, user.appName, dashId, 0, pinType, pin), ts, i);
            }
            expectedDailyAverage += expectedAverage;
            expectedAverage /= COUNT;
            Assert.assertEquals((hour + 1), averageAggregator.getHourly().size());
            Assert.assertEquals(expectedAverage, averageAggregator.getHourly().get(new AggregationKey(user.email, user.appName, dashId, 0, pinType, pin, (ts / (AverageAggregatorProcessor.HOUR)))).calcAverage(), 0);
        }
        expectedDailyAverage /= COUNT * 24;
        Assert.assertEquals(24, averageAggregator.getHourly().size());
        Assert.assertEquals(1, averageAggregator.getDaily().size());
        Assert.assertEquals(expectedDailyAverage, averageAggregator.getDaily().get(new AggregationKey(user.email, user.appName, dashId, 0, pinType, pin, ((AverageAggregatorTest.getMillis(2015, 8, 1, 0, 0)) / (AverageAggregatorProcessor.DAY)))).calcAverage(), 0);
    }

    @Test
    public void testTempFilesCreated() throws IOException {
        Path dir = Paths.get(reportingFolder, "");
        if (Files.notExists(dir)) {
            Files.createDirectories(dir);
        }
        AverageAggregatorProcessor averageAggregator = new AverageAggregatorProcessor(reportingFolder);
        User user = new User();
        user.email = "test@test.com";
        user.appName = AppNameUtil.BLYNK;
        PinType pinType = PinType.VIRTUAL;
        int dashId = 1;
        short pin = 1;
        double expectedDailyAverage = 0;
        int COUNT = 100;
        for (int hour = 0; hour < 24; hour++) {
            long ts = AverageAggregatorTest.getMillis(2015, 8, 1, hour, 0);
            double expectedAverage = 0;
            for (int i = 0; i < COUNT; i++) {
                expectedAverage += i;
                averageAggregator.collect(new cc.blynk.server.core.reporting.raw.BaseReportingKey(user.email, user.appName, dashId, 0, pinType, pin), ts, i);
            }
            expectedDailyAverage += expectedAverage;
            expectedAverage /= COUNT;
            Assert.assertEquals((hour + 1), averageAggregator.getHourly().size());
            Assert.assertEquals(expectedAverage, averageAggregator.getHourly().get(new AggregationKey(user.email, user.appName, dashId, 0, pinType, pin, (ts / (AverageAggregatorProcessor.HOUR)))).calcAverage(), 0);
        }
        expectedDailyAverage /= COUNT * 24;
        Assert.assertEquals(24, averageAggregator.getHourly().size());
        Assert.assertEquals(1, averageAggregator.getDaily().size());
        Assert.assertEquals(expectedDailyAverage, averageAggregator.getDaily().get(new AggregationKey(new cc.blynk.server.core.reporting.raw.BaseReportingKey(user.email, user.appName, dashId, 0, pinType, pin), ((AverageAggregatorTest.getMillis(2015, 8, 1, 0, 0)) / (AverageAggregatorProcessor.DAY)))).calcAverage(), 0);
        averageAggregator.close();
        Assert.assertTrue(Files.exists(Paths.get(reportingFolder, HOURLY_TEMP_FILENAME)));
        Assert.assertTrue(Files.exists(Paths.get(reportingFolder, DAILY_TEMP_FILENAME)));
        averageAggregator = new AverageAggregatorProcessor(reportingFolder);
        Assert.assertEquals(24, averageAggregator.getHourly().size());
        Assert.assertEquals(1, averageAggregator.getDaily().size());
        Assert.assertEquals(expectedDailyAverage, averageAggregator.getDaily().get(new AggregationKey(new cc.blynk.server.core.reporting.raw.BaseReportingKey(user.email, user.appName, dashId, 0, pinType, pin), ((AverageAggregatorTest.getMillis(2015, 8, 1, 0, 0)) / (AverageAggregatorProcessor.DAY)))).calcAverage(), 0);
        Assert.assertTrue(Files.notExists(Paths.get(reportingFolder, HOURLY_TEMP_FILENAME)));
        Assert.assertTrue(Files.notExists(Paths.get(reportingFolder, DAILY_TEMP_FILENAME)));
        ReportingDiskDao reportingDao = new ReportingDiskDao(reportingFolder, true);
        reportingDao.delete(user, dashId, 0, VIRTUAL, pin);
        Assert.assertTrue(Files.notExists(Paths.get(reportingFolder, HOURLY_TEMP_FILENAME)));
        Assert.assertTrue(Files.notExists(Paths.get(reportingFolder, DAILY_TEMP_FILENAME)));
    }
}

