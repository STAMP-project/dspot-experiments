package cc.blynk.server.core.dao;


import GraphGranularityType.DAILY;
import GraphGranularityType.HOURLY;
import GraphGranularityType.MINUTE;
import cc.blynk.server.core.model.enums.PinType;
import org.junit.Assert;
import org.junit.Test;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 07.09.16.
 */
public class ReportingDaoTest {
    final String REPORTING_MINUTE_FILE_NAME = "history_%s-0_%c%d_minute.bin";

    final String REPORTING_HOURLY_FILE_NAME = "history_%s-0_%c%d_hourly.bin";

    final String REPORTING_DAILY_FILE_NAME = "history_%s-0_%c%d_daily.bin";

    @Test
    public void testFileName() {
        int dashId = 1;
        PinType pinType = PinType.VIRTUAL;
        short pin = 2;
        Assert.assertEquals(String.format(REPORTING_MINUTE_FILE_NAME, dashId, pinType.pintTypeChar, pin), ReportingDiskDao.generateFilename(dashId, 0, pinType, pin, MINUTE));
        Assert.assertEquals(String.format(REPORTING_HOURLY_FILE_NAME, dashId, pinType.pintTypeChar, pin), ReportingDiskDao.generateFilename(dashId, 0, pinType, pin, HOURLY));
        Assert.assertEquals(String.format(REPORTING_DAILY_FILE_NAME, dashId, pinType.pintTypeChar, pin), ReportingDiskDao.generateFilename(dashId, 0, pinType, pin, DAILY));
    }
}

