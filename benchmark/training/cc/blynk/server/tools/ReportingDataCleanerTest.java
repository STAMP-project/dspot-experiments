package cc.blynk.server.tools;


import cc.blynk.utils.FileUtils;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Assert;
import org.junit.Test;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 05.02.17.
 */
public class ReportingDataCleanerTest {
    private static Path reportingPath = Paths.get(System.getProperty("java.io.tmpdir"), "test_reporting");

    private static Path userPath = Paths.get(System.getProperty("java.io.tmpdir"), "test_reporting/test@test.gmail.com");

    @Test
    public void testDoNotOverrideSmallFile() throws Exception {
        Path userFile = Paths.get(ReportingDataCleanerTest.userPath.toString(), "123_minute.bin");
        int count = 1;
        ReportingDataCleanerTest.fillWithData(userFile, count);
        ReportingDataCleaner.main(new String[]{ ReportingDataCleanerTest.reportingPath.toString() });
        Assert.assertEquals((16 * count), Files.size(userFile));
    }

    @Test
    public void testDoNotOverrideSmallFile2() throws Exception {
        Path userFile = Paths.get(ReportingDataCleanerTest.userPath.toString(), "123_minute.bin");
        int count = 360;
        ReportingDataCleanerTest.fillWithData(userFile, count);
        ReportingDataCleaner.main(new String[]{ ReportingDataCleanerTest.reportingPath.toString() });
        Assert.assertEquals((16 * count), Files.size(userFile));
    }

    @Test
    public void testOverrideCorrectFlowAndCheckContent() throws Exception {
        Path userFile = Paths.get(ReportingDataCleanerTest.userPath.toString(), "123_minute.bin");
        int count = 360;
        ReportingDataCleanerTest.fillWithData(userFile, count);
        ReportingDataCleaner.main(new String[]{ ReportingDataCleanerTest.reportingPath.toString() });
        Assert.assertEquals((16 * 360), Files.size(userFile));
        ByteBuffer userReportingData = FileUtils.read(Paths.get(ReportingDataCleanerTest.userPath.toString(), "123_minute.bin"), 360);
        for (int i = 0; i < 360; i++) {
            double value = userReportingData.getDouble();
            long ts = userReportingData.getLong();
            Assert.assertEquals(i, ((int) (value)));
            Assert.assertEquals(ts, i);
        }
    }

    @Test
    public void testOverrideCorrectFlowAndCheckContent2() throws Exception {
        Path userFile = Paths.get(ReportingDataCleanerTest.userPath.toString(), "123_minute.bin");
        int count = 720;
        ReportingDataCleanerTest.fillWithData(userFile, count);
        ReportingDataCleaner.main(new String[]{ ReportingDataCleanerTest.reportingPath.toString() });
        Assert.assertEquals(11520, Files.size(userFile));
        ByteBuffer userReportingData = FileUtils.read(Paths.get(ReportingDataCleanerTest.userPath.toString(), "123_minute.bin"), 360);
        for (int i = 360; i < 720; i++) {
            double value = userReportingData.getDouble();
            long ts = userReportingData.getLong();
            Assert.assertEquals(i, ((int) (value)));
            Assert.assertEquals(ts, i);
        }
    }
}

