/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.client.hadoop;


import PropertyKey.USER_BLOCK_SIZE_BYTES_DEFAULT;
import alluxio.hadoop.FileSystem;
import alluxio.testutils.BaseIntegrationTest;
import alluxio.testutils.LocalAlluxioClusterResource;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem.Statistics;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;


/**
 * Integration tests for statistics in TFS.
 */
public class FileSystemStatisticsTest extends BaseIntegrationTest {
    private static final int BLOCK_SIZE = 128;

    private static final int FILE_LEN = ((FileSystemStatisticsTest.BLOCK_SIZE) * 2) + 1;

    @ClassRule
    public static LocalAlluxioClusterResource sLocalAlluxioClusterResource = new LocalAlluxioClusterResource.Builder().setProperty(USER_BLOCK_SIZE_BYTES_DEFAULT, FileSystemStatisticsTest.BLOCK_SIZE).build();

    private static Statistics sStatistics;

    private static FileSystem sTFS;

    /**
     * Test the number of bytes read.
     */
    @Test
    public void bytesReadStatistics() throws Exception {
        long originStat = FileSystemStatisticsTest.sStatistics.getBytesRead();
        InputStream is = FileSystemStatisticsTest.sTFS.open(new Path("/testFile-read"));
        while ((is.read()) != (-1)) {
        } 
        is.close();
        Assert.assertEquals((originStat + (FileSystemStatisticsTest.FILE_LEN)), FileSystemStatisticsTest.sStatistics.getBytesRead());
    }

    /**
     * Test the number of bytes written.
     */
    @Test
    public void bytesWrittenStatistics() throws Exception {
        long originStat = FileSystemStatisticsTest.sStatistics.getBytesWritten();
        OutputStream os = FileSystemStatisticsTest.sTFS.create(new Path("/testFile-write"));
        for (int i = 0; i < (FileSystemStatisticsTest.FILE_LEN); i++) {
            os.write(1);
        }
        os.close();
        Assert.assertEquals((originStat + (FileSystemStatisticsTest.FILE_LEN)), FileSystemStatisticsTest.sStatistics.getBytesWritten());
    }

    /**
     * Test the number of read/write operations.
     */
    @Test
    public void readWriteOperationsStatistics() throws Exception {
        int exceptedReadOps = FileSystemStatisticsTest.sStatistics.getReadOps();
        int exceptedWriteOps = FileSystemStatisticsTest.sStatistics.getWriteOps();
        // Call all the overridden methods and check the statistics.
        FileSystemStatisticsTest.sTFS.create(new Path("/testFile-create")).close();
        exceptedWriteOps++;
        Assert.assertEquals(exceptedReadOps, FileSystemStatisticsTest.sStatistics.getReadOps());
        Assert.assertEquals(exceptedWriteOps, FileSystemStatisticsTest.sStatistics.getWriteOps());
        FileSystemStatisticsTest.sTFS.delete(new Path("/testFile-create"), true);
        exceptedWriteOps++;
        Assert.assertEquals(exceptedReadOps, FileSystemStatisticsTest.sStatistics.getReadOps());
        Assert.assertEquals(exceptedWriteOps, FileSystemStatisticsTest.sStatistics.getWriteOps());
        // Due to Hadoop 1 support we stick with the deprecated version. If we drop support for it
        // FileSystem.getDefaultBlockSize(new Path("/testFile-create")) will be the new one.
        FileSystemStatisticsTest.sTFS.getDefaultBlockSize();
        Assert.assertEquals(exceptedReadOps, FileSystemStatisticsTest.sStatistics.getReadOps());
        Assert.assertEquals(exceptedWriteOps, FileSystemStatisticsTest.sStatistics.getWriteOps());
        // Due to Hadoop 1 support we stick with the deprecated version. If we drop support for it
        // FileSystem.geDefaultReplication(new Path("/testFile-create")) will be the new one.
        FileSystemStatisticsTest.sTFS.getDefaultReplication();
        Assert.assertEquals(exceptedReadOps, FileSystemStatisticsTest.sStatistics.getReadOps());
        Assert.assertEquals(exceptedWriteOps, FileSystemStatisticsTest.sStatistics.getWriteOps());
        FileStatus fStatus = FileSystemStatisticsTest.sTFS.getFileStatus(new Path("/testFile-read"));
        exceptedReadOps++;
        Assert.assertEquals(exceptedReadOps, FileSystemStatisticsTest.sStatistics.getReadOps());
        Assert.assertEquals(exceptedWriteOps, FileSystemStatisticsTest.sStatistics.getWriteOps());
        FileSystemStatisticsTest.sTFS.getFileBlockLocations(fStatus, 0, FileSystemStatisticsTest.FILE_LEN);
        exceptedReadOps++;
        Assert.assertEquals(exceptedReadOps, FileSystemStatisticsTest.sStatistics.getReadOps());
        Assert.assertEquals(exceptedWriteOps, FileSystemStatisticsTest.sStatistics.getWriteOps());
        FileSystemStatisticsTest.sTFS.getUri();
        Assert.assertEquals(exceptedReadOps, FileSystemStatisticsTest.sStatistics.getReadOps());
        Assert.assertEquals(exceptedWriteOps, FileSystemStatisticsTest.sStatistics.getWriteOps());
        FileSystemStatisticsTest.sTFS.getWorkingDirectory();
        Assert.assertEquals(exceptedReadOps, FileSystemStatisticsTest.sStatistics.getReadOps());
        Assert.assertEquals(exceptedWriteOps, FileSystemStatisticsTest.sStatistics.getWriteOps());
        FileSystemStatisticsTest.sTFS.listStatus(new Path("/"));
        exceptedReadOps++;
        Assert.assertEquals(exceptedReadOps, FileSystemStatisticsTest.sStatistics.getReadOps());
        Assert.assertEquals(exceptedWriteOps, FileSystemStatisticsTest.sStatistics.getWriteOps());
        FileSystemStatisticsTest.sTFS.mkdirs(new Path("/testDir"));
        exceptedWriteOps++;
        Assert.assertEquals(exceptedReadOps, FileSystemStatisticsTest.sStatistics.getReadOps());
        Assert.assertEquals(exceptedWriteOps, FileSystemStatisticsTest.sStatistics.getWriteOps());
        FileSystemStatisticsTest.sTFS.open(new Path("/testFile-read")).close();
        exceptedReadOps++;
        Assert.assertEquals(exceptedReadOps, FileSystemStatisticsTest.sStatistics.getReadOps());
        Assert.assertEquals(exceptedWriteOps, FileSystemStatisticsTest.sStatistics.getWriteOps());
        FileSystemStatisticsTest.sTFS.rename(new Path("/testDir"), new Path("/testDir-rename"));
        exceptedWriteOps++;
        Assert.assertEquals(exceptedReadOps, FileSystemStatisticsTest.sStatistics.getReadOps());
        Assert.assertEquals(exceptedWriteOps, FileSystemStatisticsTest.sStatistics.getWriteOps());
        FileSystemStatisticsTest.sTFS.setWorkingDirectory(new Path("/testDir-rename"));
        Assert.assertEquals(exceptedReadOps, FileSystemStatisticsTest.sStatistics.getReadOps());
        Assert.assertEquals(exceptedWriteOps, FileSystemStatisticsTest.sStatistics.getWriteOps());
    }
}

