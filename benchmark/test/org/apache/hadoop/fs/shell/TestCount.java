/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.fs.shell;


import StorageType.SSD;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.ContentSummary;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FilterFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.QuotaUsage;
import org.apache.hadoop.fs.StorageType;
import org.apache.hadoop.fs.shell.CommandFormat.NotEnoughArgumentsException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * JUnit test class for {@link org.apache.hadoop.fs.shell.Count}
 */
public class TestCount {
    private static final String WITH_QUOTAS = "Content summary with quotas";

    private static final String NO_QUOTAS = "Content summary without quotas";

    private static final String HUMAN = "human: ";

    private static final String BYTES = "bytes: ";

    private static final String QUOTAS_AND_USAGE = "quotas and usage";

    private static Configuration conf;

    private static FileSystem mockFs;

    private static FileStatus fileStat;

    @Test
    public void processOptionsHumanReadable() {
        LinkedList<String> options = new LinkedList<String>();
        options.add("-h");
        options.add("dummy");
        Count count = new Count();
        count.processOptions(options);
        Assert.assertFalse(count.isShowQuotas());
        Assert.assertTrue(count.isHumanReadable());
    }

    @Test
    public void processOptionsAll() {
        LinkedList<String> options = new LinkedList<String>();
        options.add("-q");
        options.add("-h");
        options.add("-t");
        options.add("SSD");
        options.add("dummy");
        Count count = new Count();
        count.processOptions(options);
        Assert.assertTrue(count.isShowQuotas());
        Assert.assertTrue(count.isHumanReadable());
        Assert.assertTrue(count.isShowQuotabyType());
        Assert.assertEquals(1, count.getStorageTypes().size());
        Assert.assertEquals(SSD, count.getStorageTypes().get(0));
    }

    // check no options is handled correctly
    @Test
    public void processOptionsNoOptions() {
        LinkedList<String> options = new LinkedList<String>();
        options.add("dummy");
        Count count = new Count();
        count.processOptions(options);
        Assert.assertFalse(count.isShowQuotas());
    }

    // check -q is handled correctly
    @Test
    public void processOptionsShowQuotas() {
        LinkedList<String> options = new LinkedList<String>();
        options.add("-q");
        options.add("dummy");
        Count count = new Count();
        count.processOptions(options);
        Assert.assertTrue(count.isShowQuotas());
    }

    // check missing arguments is handled correctly
    @Test
    public void processOptionsMissingArgs() {
        LinkedList<String> options = new LinkedList<String>();
        Count count = new Count();
        try {
            count.processOptions(options);
            Assert.fail("Count.processOptions - NotEnoughArgumentsException not thrown");
        } catch (NotEnoughArgumentsException e) {
        }
        Assert.assertFalse(count.isShowQuotas());
    }

    // check the correct header is produced with no quotas (-v)
    @Test
    public void processOptionsHeaderNoQuotas() {
        LinkedList<String> options = new LinkedList<String>();
        options.add("-v");
        options.add("dummy");
        PrintStream out = Mockito.mock(PrintStream.class);
        Count count = new Count();
        count.out = out;
        count.processOptions(options);
        // <----12----> <----12----> <-------18------->
        String noQuotasHeader = "   DIR_COUNT   FILE_COUNT       CONTENT_SIZE PATHNAME";
        Mockito.verify(out).println(noQuotasHeader);
        Mockito.verifyNoMoreInteractions(out);
    }

    // check the correct header is produced with quotas (-q -v)
    @Test
    public void processOptionsHeaderWithQuotas() {
        LinkedList<String> options = new LinkedList<String>();
        options.add("-q");
        options.add("-v");
        options.add("dummy");
        PrintStream out = Mockito.mock(PrintStream.class);
        Count count = new Count();
        count.out = out;
        count.processOptions(options);
        // <----12----> <-----15------> <-----15------> <-----15------>
        String withQuotasHeader = "       QUOTA       REM_QUOTA     SPACE_QUOTA REM_SPACE_QUOTA " + // <----12----> <----12----> <-------18------->
        "   DIR_COUNT   FILE_COUNT       CONTENT_SIZE PATHNAME";
        Mockito.verify(out).println(withQuotasHeader);
        Mockito.verifyNoMoreInteractions(out);
    }

    // check quotas are reported correctly
    @Test
    public void processPathShowQuotas() throws Exception {
        Path path = new Path("mockfs:/test");
        Mockito.when(TestCount.mockFs.getFileStatus(ArgumentMatchers.eq(path))).thenReturn(TestCount.fileStat);
        PathData pathData = new PathData(path.toString(), TestCount.conf);
        PrintStream out = Mockito.mock(PrintStream.class);
        Count count = new Count();
        count.out = out;
        LinkedList<String> options = new LinkedList<String>();
        options.add("-q");
        options.add("dummy");
        count.processOptions(options);
        count.processPath(pathData);
        Mockito.verify(out).println((((TestCount.BYTES) + (TestCount.WITH_QUOTAS)) + (path.toString())));
        Mockito.verifyNoMoreInteractions(out);
    }

    // check counts without quotas are reported correctly
    @Test
    public void processPathNoQuotas() throws Exception {
        Path path = new Path("mockfs:/test");
        Mockito.when(TestCount.mockFs.getFileStatus(ArgumentMatchers.eq(path))).thenReturn(TestCount.fileStat);
        PathData pathData = new PathData(path.toString(), TestCount.conf);
        PrintStream out = Mockito.mock(PrintStream.class);
        Count count = new Count();
        count.out = out;
        LinkedList<String> options = new LinkedList<String>();
        options.add("dummy");
        count.processOptions(options);
        count.processPath(pathData);
        Mockito.verify(out).println((((TestCount.BYTES) + (TestCount.NO_QUOTAS)) + (path.toString())));
        Mockito.verifyNoMoreInteractions(out);
    }

    @Test
    public void processPathShowQuotasHuman() throws Exception {
        Path path = new Path("mockfs:/test");
        Mockito.when(TestCount.mockFs.getFileStatus(ArgumentMatchers.eq(path))).thenReturn(TestCount.fileStat);
        PathData pathData = new PathData(path.toString(), TestCount.conf);
        PrintStream out = Mockito.mock(PrintStream.class);
        Count count = new Count();
        count.out = out;
        LinkedList<String> options = new LinkedList<String>();
        options.add("-q");
        options.add("-h");
        options.add("dummy");
        count.processOptions(options);
        count.processPath(pathData);
        Mockito.verify(out).println((((TestCount.HUMAN) + (TestCount.WITH_QUOTAS)) + (path.toString())));
    }

    @Test
    public void processPathNoQuotasHuman() throws Exception {
        Path path = new Path("mockfs:/test");
        Mockito.when(TestCount.mockFs.getFileStatus(ArgumentMatchers.eq(path))).thenReturn(TestCount.fileStat);
        PathData pathData = new PathData(path.toString(), TestCount.conf);
        PrintStream out = Mockito.mock(PrintStream.class);
        Count count = new Count();
        count.out = out;
        LinkedList<String> options = new LinkedList<String>();
        options.add("-h");
        options.add("dummy");
        count.processOptions(options);
        count.processPath(pathData);
        Mockito.verify(out).println((((TestCount.HUMAN) + (TestCount.NO_QUOTAS)) + (path.toString())));
    }

    @Test
    public void processPathWithQuotasByStorageTypesHeader() throws Exception {
        Path path = new Path("mockfs:/test");
        Mockito.when(TestCount.mockFs.getFileStatus(ArgumentMatchers.eq(path))).thenReturn(TestCount.fileStat);
        PrintStream out = Mockito.mock(PrintStream.class);
        Count count = new Count();
        count.out = out;
        LinkedList<String> options = new LinkedList<String>();
        options.add("-q");
        options.add("-v");
        options.add("-t");
        options.add("all");
        options.add("dummy");
        count.processOptions(options);
        // <----13---> <-------17------> <----13-----> <------17------->
        String withStorageTypeHeader = "    SSD_QUOTA     REM_SSD_QUOTA    DISK_QUOTA    REM_DISK_QUOTA " + // <----13---> <-------17------>
        ("ARCHIVE_QUOTA REM_ARCHIVE_QUOTA PROVIDED_QUOTA REM_PROVIDED_QUOTA " + "PATHNAME");
        Mockito.verify(out).println(withStorageTypeHeader);
        Mockito.verifyNoMoreInteractions(out);
    }

    @Test
    public void processPathWithQuotasBySSDStorageTypesHeader() throws Exception {
        Path path = new Path("mockfs:/test");
        Mockito.when(TestCount.mockFs.getFileStatus(ArgumentMatchers.eq(path))).thenReturn(TestCount.fileStat);
        PrintStream out = Mockito.mock(PrintStream.class);
        Count count = new Count();
        count.out = out;
        LinkedList<String> options = new LinkedList<String>();
        options.add("-q");
        options.add("-v");
        options.add("-t");
        options.add("SSD");
        options.add("dummy");
        count.processOptions(options);
        // <----13---> <-------17------>
        String withStorageTypeHeader = "    SSD_QUOTA     REM_SSD_QUOTA " + "PATHNAME";
        Mockito.verify(out).println(withStorageTypeHeader);
        Mockito.verifyNoMoreInteractions(out);
    }

    @Test
    public void processPathWithQuotasByQTVH() throws Exception {
        Path path = new Path("mockfs:/test");
        Mockito.when(TestCount.mockFs.getFileStatus(ArgumentMatchers.eq(path))).thenReturn(TestCount.fileStat);
        PrintStream out = Mockito.mock(PrintStream.class);
        Count count = new Count();
        count.out = out;
        LinkedList<String> options = new LinkedList<String>();
        options.add("-q");
        options.add("-t");
        options.add("-v");
        options.add("-h");
        options.add("dummy");
        count.processOptions(options);
        // <----13---> <-------17------>
        String withStorageTypeHeader = "    SSD_QUOTA     REM_SSD_QUOTA " + ((("   DISK_QUOTA    REM_DISK_QUOTA " + "ARCHIVE_QUOTA REM_ARCHIVE_QUOTA ") + "PROVIDED_QUOTA REM_PROVIDED_QUOTA ") + "PATHNAME");
        Mockito.verify(out).println(withStorageTypeHeader);
        Mockito.verifyNoMoreInteractions(out);
    }

    @Test
    public void processPathWithQuotasByMultipleStorageTypesContent() throws Exception {
        processMultipleStorageTypesContent(false);
    }

    @Test
    public void processPathWithQuotaUsageByMultipleStorageTypesContent() throws Exception {
        processMultipleStorageTypesContent(true);
    }

    @Test
    public void processPathWithQuotasByMultipleStorageTypes() throws Exception {
        Path path = new Path("mockfs:/test");
        Mockito.when(TestCount.mockFs.getFileStatus(ArgumentMatchers.eq(path))).thenReturn(TestCount.fileStat);
        PrintStream out = Mockito.mock(PrintStream.class);
        Count count = new Count();
        count.out = out;
        LinkedList<String> options = new LinkedList<String>();
        options.add("-q");
        options.add("-v");
        options.add("-t");
        options.add("SSD,DISK");
        options.add("dummy");
        count.processOptions(options);
        // <----13---> <------17------->
        String withStorageTypeHeader = "    SSD_QUOTA     REM_SSD_QUOTA " + ("   DISK_QUOTA    REM_DISK_QUOTA " + "PATHNAME");
        Mockito.verify(out).println(withStorageTypeHeader);
        Mockito.verifyNoMoreInteractions(out);
    }

    @Test
    public void getCommandName() {
        Count count = new Count();
        String actual = count.getCommandName();
        String expected = "count";
        Assert.assertEquals("Count.getCommandName", expected, actual);
    }

    @Test
    public void isDeprecated() {
        Count count = new Count();
        boolean actual = count.isDeprecated();
        boolean expected = false;
        Assert.assertEquals("Count.isDeprecated", expected, actual);
    }

    @Test
    public void getReplacementCommand() {
        Count count = new Count();
        String actual = count.getReplacementCommand();
        String expected = null;
        Assert.assertEquals("Count.getReplacementCommand", expected, actual);
    }

    @Test
    public void getName() {
        Count count = new Count();
        String actual = count.getName();
        String expected = "count";
        Assert.assertEquals("Count.getName", expected, actual);
    }

    @Test
    public void getUsage() {
        Count count = new Count();
        String actual = count.getUsage();
        String expected = "-count [-q] [-h] [-v] [-t [<storage type>]] [-u] [-x] [-e] <path> ...";
        Assert.assertEquals("Count.getUsage", expected, actual);
    }

    // check the correct description is returned
    @Test
    public void getDescription() {
        Count count = new Count();
        String actual = count.getDescription();
        String expected = "Count the number of directories, files and bytes under the paths\n" + ((((((((((((((((((((("that match the specified file pattern.  The output columns are:\n" + "DIR_COUNT FILE_COUNT CONTENT_SIZE PATHNAME\n") + "or, with the -q option:\n") + "QUOTA REM_QUOTA SPACE_QUOTA REM_SPACE_QUOTA\n") + "      DIR_COUNT FILE_COUNT CONTENT_SIZE PATHNAME\n") + "The -h option shows file sizes in human readable format.\n") + "The -v option displays a header line.\n") + "The -x option excludes snapshots from being calculated. \n") + "The -t option displays quota by storage types.\n") + "It should be used with -q or -u option, ") + "otherwise it will be ignored.\n") + "If a comma-separated list of storage types is given after the -t option, \n") + "it displays the quota and usage for the specified types. \n") + "Otherwise, it displays the quota and usage for all the storage \n") + "types that support quota. The list of possible storage ") + "types(case insensitive):\n") + "ram_disk, ssd, disk and archive.\n") + "It can also pass the value '', 'all' or 'ALL' to specify all the ") + "storage types.\n") + "The -u option shows the quota and \n") + "the usage against the quota without the detailed content summary.") + "The -e option shows the erasure coding policy.");
        Assert.assertEquals("Count.getDescription", expected, actual);
    }

    @Test
    public void processPathWithQuotaUsageHuman() throws Exception {
        processPathWithQuotaUsage(false);
    }

    @Test
    public void processPathWithQuotaUsageRawBytes() throws Exception {
        processPathWithQuotaUsage(true);
    }

    // mock content system
    static class MockContentSummary extends ContentSummary {
        // suppress warning on the usage of deprecated ContentSummary constructor
        @SuppressWarnings("deprecation")
        public MockContentSummary() {
        }

        @Override
        public String toString(boolean qOption, boolean hOption, boolean xOption) {
            if (qOption) {
                if (hOption) {
                    return (TestCount.HUMAN) + (TestCount.WITH_QUOTAS);
                } else {
                    return (TestCount.BYTES) + (TestCount.WITH_QUOTAS);
                }
            } else {
                if (hOption) {
                    return (TestCount.HUMAN) + (TestCount.NO_QUOTAS);
                } else {
                    return (TestCount.BYTES) + (TestCount.NO_QUOTAS);
                }
            }
        }
    }

    // mock content system
    static class MockQuotaUsage extends QuotaUsage {
        // suppress warning on the usage of deprecated ContentSummary constructor
        @SuppressWarnings("deprecation")
        public MockQuotaUsage() {
        }

        @Override
        public String toString(boolean hOption, boolean tOption, List<StorageType> types) {
            if (tOption) {
                StringBuffer result = new StringBuffer();
                result.append((hOption ? TestCount.HUMAN : TestCount.BYTES));
                for (StorageType type : types) {
                    result.append(type.toString());
                    result.append(" ");
                }
                return result.toString();
            }
            if (hOption) {
                return (TestCount.HUMAN) + (TestCount.QUOTAS_AND_USAGE);
            } else {
                return (TestCount.BYTES) + (TestCount.QUOTAS_AND_USAGE);
            }
        }
    }

    // mock file system for use in testing
    static class MockFileSystem extends FilterFileSystem {
        Configuration conf;

        MockFileSystem() {
            super(TestCount.mockFs);
        }

        @Override
        public void initialize(URI uri, Configuration conf) {
            this.conf = conf;
        }

        @Override
        public Path makeQualified(Path path) {
            return path;
        }

        @Override
        public ContentSummary getContentSummary(Path f) throws IOException {
            return new TestCount.MockContentSummary();
        }

        @Override
        public Configuration getConf() {
            return conf;
        }

        @Override
        public QuotaUsage getQuotaUsage(Path f) throws IOException {
            return new TestCount.MockQuotaUsage();
        }
    }
}

