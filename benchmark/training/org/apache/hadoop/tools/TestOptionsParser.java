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
package org.apache.hadoop.tools;


import DistCpConstants.DEFAULT_MAPS;
import DistCpConstants.UNIFORMSIZE;
import DistCpOptionSwitch.APPEND;
import DistCpOptionSwitch.ATOMIC_COMMIT;
import DistCpOptionSwitch.BANDWIDTH;
import DistCpOptionSwitch.DELETE_MISSING;
import DistCpOptionSwitch.IGNORE_FAILURES;
import DistCpOptionSwitch.PRESERVE_STATUS;
import DistCpOptionSwitch.PRESERVE_STATUS_DEFAULT;
import DistCpOptionSwitch.SYNC_FOLDERS;
import DistCpOptions.MAX_NUM_LISTSTATUS_THREADS;
import FileAttribute.ACL;
import FileAttribute.BLOCKSIZE;
import FileAttribute.CHECKSUMTYPE;
import FileAttribute.GROUP;
import FileAttribute.PERMISSION;
import FileAttribute.REPLICATION;
import FileAttribute.TIMES;
import FileAttribute.USER;
import FileAttribute.XATTR;
import java.util.NoSuchElementException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;


public class TestOptionsParser {
    private static final float DELTA = 0.001F;

    @Test
    public void testParseIgnoreFailure() {
        DistCpOptions options = OptionsParser.parse(new String[]{ "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        Assert.assertFalse(options.shouldIgnoreFailures());
        options = OptionsParser.parse(new String[]{ "-i", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        Assert.assertTrue(options.shouldIgnoreFailures());
    }

    @Test
    public void testParseOverwrite() {
        DistCpOptions options = OptionsParser.parse(new String[]{ "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        Assert.assertFalse(options.shouldOverwrite());
        options = OptionsParser.parse(new String[]{ "-overwrite", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        Assert.assertTrue(options.shouldOverwrite());
        try {
            OptionsParser.parse(new String[]{ "-update", "-overwrite", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
            Assert.fail("Update and overwrite aren't allowed together");
        } catch (IllegalArgumentException ignore) {
        }
    }

    @Test
    public void testLogPath() {
        DistCpOptions options = OptionsParser.parse(new String[]{ "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        Assert.assertNull(options.getLogPath());
        options = OptionsParser.parse(new String[]{ "-log", "hdfs://localhost:8020/logs", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        Assert.assertEquals(options.getLogPath(), new Path("hdfs://localhost:8020/logs"));
    }

    @Test
    public void testParseBlokcing() {
        DistCpOptions options = OptionsParser.parse(new String[]{ "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        Assert.assertTrue(options.shouldBlock());
        options = OptionsParser.parse(new String[]{ "-async", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        Assert.assertFalse(options.shouldBlock());
    }

    @Test
    public void testParsebandwidth() {
        DistCpOptions options = OptionsParser.parse(new String[]{ "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        Assert.assertEquals(options.getMapBandwidth(), 0, TestOptionsParser.DELTA);
        options = OptionsParser.parse(new String[]{ "-bandwidth", "11.2", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        Assert.assertEquals(options.getMapBandwidth(), 11.2, TestOptionsParser.DELTA);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseNonPositiveBandwidth() {
        OptionsParser.parse(new String[]{ "-bandwidth", "-11", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseZeroBandwidth() {
        OptionsParser.parse(new String[]{ "-bandwidth", "0", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
    }

    @Test
    public void testParseSkipCRC() {
        DistCpOptions options = OptionsParser.parse(new String[]{ "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        Assert.assertFalse(options.shouldSkipCRC());
        options = OptionsParser.parse(new String[]{ "-update", "-skipcrccheck", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        Assert.assertTrue(options.shouldSyncFolder());
        Assert.assertTrue(options.shouldSkipCRC());
    }

    @Test
    public void testParseAtomicCommit() {
        DistCpOptions options = OptionsParser.parse(new String[]{ "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        Assert.assertFalse(options.shouldAtomicCommit());
        options = OptionsParser.parse(new String[]{ "-atomic", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        Assert.assertTrue(options.shouldAtomicCommit());
        try {
            OptionsParser.parse(new String[]{ "-atomic", "-update", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
            Assert.fail("Atomic and sync folders were allowed");
        } catch (IllegalArgumentException ignore) {
        }
    }

    @Test
    public void testParseWorkPath() {
        DistCpOptions options = OptionsParser.parse(new String[]{ "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        Assert.assertNull(options.getAtomicWorkPath());
        options = OptionsParser.parse(new String[]{ "-atomic", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        Assert.assertNull(options.getAtomicWorkPath());
        options = OptionsParser.parse(new String[]{ "-atomic", "-tmp", "hdfs://localhost:8020/work", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        Assert.assertEquals(options.getAtomicWorkPath(), new Path("hdfs://localhost:8020/work"));
        try {
            OptionsParser.parse(new String[]{ "-tmp", "hdfs://localhost:8020/work", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
            Assert.fail("work path was allowed without -atomic switch");
        } catch (IllegalArgumentException ignore) {
        }
    }

    @Test
    public void testParseSyncFolders() {
        DistCpOptions options = OptionsParser.parse(new String[]{ "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        Assert.assertFalse(options.shouldSyncFolder());
        options = OptionsParser.parse(new String[]{ "-update", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        Assert.assertTrue(options.shouldSyncFolder());
    }

    @Test
    public void testParseDeleteMissing() {
        DistCpOptions options = OptionsParser.parse(new String[]{ "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        Assert.assertFalse(options.shouldDeleteMissing());
        options = OptionsParser.parse(new String[]{ "-update", "-delete", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        Assert.assertTrue(options.shouldSyncFolder());
        Assert.assertTrue(options.shouldDeleteMissing());
        options = OptionsParser.parse(new String[]{ "-overwrite", "-delete", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        Assert.assertTrue(options.shouldOverwrite());
        Assert.assertTrue(options.shouldDeleteMissing());
        try {
            OptionsParser.parse(new String[]{ "-atomic", "-delete", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
            Assert.fail("Atomic and delete folders were allowed");
        } catch (IllegalArgumentException ignore) {
        }
    }

    @Test
    public void testParseMaps() {
        DistCpOptions options = OptionsParser.parse(new String[]{ "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        Assert.assertEquals(options.getMaxMaps(), DEFAULT_MAPS);
        options = OptionsParser.parse(new String[]{ "-m", "1", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        Assert.assertEquals(options.getMaxMaps(), 1);
        options = OptionsParser.parse(new String[]{ "-m", "0", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        Assert.assertEquals(options.getMaxMaps(), 1);
        try {
            OptionsParser.parse(new String[]{ "-m", "hello", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
            Assert.fail("Non numberic map parsed");
        } catch (IllegalArgumentException ignore) {
        }
        try {
            OptionsParser.parse(new String[]{ "-mapredXslConf", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
            Assert.fail("Non numberic map parsed");
        } catch (IllegalArgumentException ignore) {
        }
    }

    @Test
    public void testParseNumListstatusThreads() {
        DistCpOptions options = OptionsParser.parse(new String[]{ "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        // If command line argument isn't set, we expect .getNumListstatusThreads
        // option to be zero (so that we know when to override conf properties).
        Assert.assertEquals(0, options.getNumListstatusThreads());
        options = OptionsParser.parse(new String[]{ "--numListstatusThreads", "12", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        Assert.assertEquals(12, options.getNumListstatusThreads());
        options = OptionsParser.parse(new String[]{ "--numListstatusThreads", "0", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        Assert.assertEquals(0, options.getNumListstatusThreads());
        try {
            OptionsParser.parse(new String[]{ "--numListstatusThreads", "hello", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
            Assert.fail("Non numberic numListstatusThreads parsed");
        } catch (IllegalArgumentException ignore) {
        }
        // Ignore large number of threads.
        options = OptionsParser.parse(new String[]{ "--numListstatusThreads", "100", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        Assert.assertEquals(MAX_NUM_LISTSTATUS_THREADS, options.getNumListstatusThreads());
    }

    @Test
    public void testSourceListing() {
        DistCpOptions options = OptionsParser.parse(new String[]{ "-f", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        Assert.assertEquals(options.getSourceFileListing(), new Path("hdfs://localhost:8020/source/first"));
    }

    @Test
    public void testSourceListingAndSourcePath() {
        try {
            OptionsParser.parse(new String[]{ "-f", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
            Assert.fail("Both source listing & source paths allowed");
        } catch (IllegalArgumentException ignore) {
        }
    }

    @Test
    public void testMissingSourceInfo() {
        try {
            OptionsParser.parse(new String[]{ "hdfs://localhost:8020/target/" });
            Assert.fail("Neither source listing not source paths present");
        } catch (IllegalArgumentException ignore) {
        }
    }

    @Test
    public void testMissingTarget() {
        try {
            OptionsParser.parse(new String[]{ "-f", "hdfs://localhost:8020/source" });
            Assert.fail("Missing target allowed");
        } catch (IllegalArgumentException ignore) {
        }
    }

    @Test
    public void testInvalidArgs() {
        try {
            OptionsParser.parse(new String[]{ "-m", "-f", "hdfs://localhost:8020/source" });
            Assert.fail("Missing map value");
        } catch (IllegalArgumentException ignore) {
        }
    }

    @Test
    public void testCopyStrategy() {
        DistCpOptions options = OptionsParser.parse(new String[]{ "-strategy", "dynamic", "-f", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        Assert.assertEquals(options.getCopyStrategy(), "dynamic");
        options = OptionsParser.parse(new String[]{ "-f", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        Assert.assertEquals(options.getCopyStrategy(), UNIFORMSIZE);
    }

    @Test
    public void testTargetPath() {
        DistCpOptions options = OptionsParser.parse(new String[]{ "-f", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        Assert.assertEquals(options.getTargetPath(), new Path("hdfs://localhost:8020/target/"));
    }

    @Test
    public void testPreserve() {
        DistCpOptions options = OptionsParser.parse(new String[]{ "-f", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        Assert.assertTrue(options.shouldPreserve(BLOCKSIZE));
        Assert.assertFalse(options.shouldPreserve(REPLICATION));
        Assert.assertFalse(options.shouldPreserve(PERMISSION));
        Assert.assertFalse(options.shouldPreserve(USER));
        Assert.assertFalse(options.shouldPreserve(GROUP));
        Assert.assertFalse(options.shouldPreserve(CHECKSUMTYPE));
        options = OptionsParser.parse(new String[]{ "-p", "-f", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        Assert.assertTrue(options.shouldPreserve(BLOCKSIZE));
        Assert.assertTrue(options.shouldPreserve(REPLICATION));
        Assert.assertTrue(options.shouldPreserve(PERMISSION));
        Assert.assertTrue(options.shouldPreserve(USER));
        Assert.assertTrue(options.shouldPreserve(GROUP));
        Assert.assertTrue(options.shouldPreserve(CHECKSUMTYPE));
        Assert.assertFalse(options.shouldPreserve(ACL));
        Assert.assertFalse(options.shouldPreserve(XATTR));
        options = OptionsParser.parse(new String[]{ "-p", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        Assert.assertTrue(options.shouldPreserve(BLOCKSIZE));
        Assert.assertTrue(options.shouldPreserve(REPLICATION));
        Assert.assertTrue(options.shouldPreserve(PERMISSION));
        Assert.assertTrue(options.shouldPreserve(USER));
        Assert.assertTrue(options.shouldPreserve(GROUP));
        Assert.assertTrue(options.shouldPreserve(CHECKSUMTYPE));
        Assert.assertFalse(options.shouldPreserve(ACL));
        Assert.assertFalse(options.shouldPreserve(XATTR));
        options = OptionsParser.parse(new String[]{ "-pbr", "-f", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        Assert.assertTrue(options.shouldPreserve(BLOCKSIZE));
        Assert.assertTrue(options.shouldPreserve(REPLICATION));
        Assert.assertFalse(options.shouldPreserve(PERMISSION));
        Assert.assertFalse(options.shouldPreserve(USER));
        Assert.assertFalse(options.shouldPreserve(GROUP));
        Assert.assertFalse(options.shouldPreserve(CHECKSUMTYPE));
        Assert.assertFalse(options.shouldPreserve(ACL));
        Assert.assertFalse(options.shouldPreserve(XATTR));
        options = OptionsParser.parse(new String[]{ "-pbrgup", "-f", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        Assert.assertTrue(options.shouldPreserve(BLOCKSIZE));
        Assert.assertTrue(options.shouldPreserve(REPLICATION));
        Assert.assertTrue(options.shouldPreserve(PERMISSION));
        Assert.assertTrue(options.shouldPreserve(USER));
        Assert.assertTrue(options.shouldPreserve(GROUP));
        Assert.assertFalse(options.shouldPreserve(CHECKSUMTYPE));
        Assert.assertFalse(options.shouldPreserve(ACL));
        Assert.assertFalse(options.shouldPreserve(XATTR));
        options = OptionsParser.parse(new String[]{ "-pbrgupcaxt", "-f", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        Assert.assertTrue(options.shouldPreserve(BLOCKSIZE));
        Assert.assertTrue(options.shouldPreserve(REPLICATION));
        Assert.assertTrue(options.shouldPreserve(PERMISSION));
        Assert.assertTrue(options.shouldPreserve(USER));
        Assert.assertTrue(options.shouldPreserve(GROUP));
        Assert.assertTrue(options.shouldPreserve(CHECKSUMTYPE));
        Assert.assertTrue(options.shouldPreserve(ACL));
        Assert.assertTrue(options.shouldPreserve(XATTR));
        Assert.assertTrue(options.shouldPreserve(TIMES));
        options = OptionsParser.parse(new String[]{ "-pc", "-f", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        Assert.assertFalse(options.shouldPreserve(BLOCKSIZE));
        Assert.assertFalse(options.shouldPreserve(REPLICATION));
        Assert.assertFalse(options.shouldPreserve(PERMISSION));
        Assert.assertFalse(options.shouldPreserve(USER));
        Assert.assertFalse(options.shouldPreserve(GROUP));
        Assert.assertTrue(options.shouldPreserve(CHECKSUMTYPE));
        Assert.assertFalse(options.shouldPreserve(ACL));
        Assert.assertFalse(options.shouldPreserve(XATTR));
        options = OptionsParser.parse(new String[]{ "-p", "-f", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        Assert.assertEquals(((PRESERVE_STATUS_DEFAULT.length()) - 2), options.getPreserveAttributes().size());
        try {
            OptionsParser.parse(new String[]{ "-pabcd", "-f", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target" });
            Assert.fail("Invalid preserve attribute");
        } catch (NoSuchElementException ignore) {
        }
        Builder builder = new DistCpOptions.Builder(new Path("hdfs://localhost:8020/source/first"), new Path("hdfs://localhost:8020/target/"));
        Assert.assertFalse(builder.build().shouldPreserve(PERMISSION));
        builder.preserve(PERMISSION);
        Assert.assertTrue(builder.build().shouldPreserve(PERMISSION));
        builder.preserve(PERMISSION);
        Assert.assertTrue(builder.build().shouldPreserve(PERMISSION));
    }

    @Test
    public void testOptionsSwitchAddToConf() {
        Configuration conf = new Configuration();
        Assert.assertNull(conf.get(ATOMIC_COMMIT.getConfigLabel()));
        DistCpOptionSwitch.addToConf(conf, ATOMIC_COMMIT);
        Assert.assertTrue(conf.getBoolean(ATOMIC_COMMIT.getConfigLabel(), false));
    }

    @Test
    public void testOptionsAppendToConf() {
        Configuration conf = new Configuration();
        Assert.assertFalse(conf.getBoolean(IGNORE_FAILURES.getConfigLabel(), false));
        Assert.assertFalse(conf.getBoolean(ATOMIC_COMMIT.getConfigLabel(), false));
        Assert.assertEquals(conf.getRaw(BANDWIDTH.getConfigLabel()), null);
        DistCpOptions options = OptionsParser.parse(new String[]{ "-atomic", "-i", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        options.appendToConf(conf);
        Assert.assertTrue(conf.getBoolean(IGNORE_FAILURES.getConfigLabel(), false));
        Assert.assertTrue(conf.getBoolean(ATOMIC_COMMIT.getConfigLabel(), false));
        Assert.assertEquals(conf.getFloat(BANDWIDTH.getConfigLabel(), (-1)), (-1.0), TestOptionsParser.DELTA);
        conf = new Configuration();
        Assert.assertFalse(conf.getBoolean(SYNC_FOLDERS.getConfigLabel(), false));
        Assert.assertFalse(conf.getBoolean(DELETE_MISSING.getConfigLabel(), false));
        Assert.assertEquals(conf.get(PRESERVE_STATUS.getConfigLabel()), null);
        options = OptionsParser.parse(new String[]{ "-update", "-delete", "-pu", "-bandwidth", "11.2", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        options.appendToConf(conf);
        Assert.assertTrue(conf.getBoolean(SYNC_FOLDERS.getConfigLabel(), false));
        Assert.assertTrue(conf.getBoolean(DELETE_MISSING.getConfigLabel(), false));
        Assert.assertEquals(conf.get(PRESERVE_STATUS.getConfigLabel()), "U");
        Assert.assertEquals(conf.getFloat(BANDWIDTH.getConfigLabel(), (-1)), 11.2, TestOptionsParser.DELTA);
    }

    @Test
    public void testOptionsAppendToConfDoesntOverwriteBandwidth() {
        Configuration conf = new Configuration();
        Assert.assertEquals(conf.getRaw(BANDWIDTH.getConfigLabel()), null);
        DistCpOptions options = OptionsParser.parse(new String[]{ "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        options.appendToConf(conf);
        Assert.assertEquals(conf.getFloat(BANDWIDTH.getConfigLabel(), (-1)), (-1.0), TestOptionsParser.DELTA);
        conf = new Configuration();
        Assert.assertEquals(conf.getRaw(BANDWIDTH.getConfigLabel()), null);
        options = OptionsParser.parse(new String[]{ "-update", "-delete", "-pu", "-bandwidth", "77", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        options.appendToConf(conf);
        Assert.assertEquals(conf.getFloat(BANDWIDTH.getConfigLabel(), (-1)), 77.0, TestOptionsParser.DELTA);
        conf = new Configuration();
        conf.set(BANDWIDTH.getConfigLabel(), "88");
        Assert.assertEquals(conf.getRaw(BANDWIDTH.getConfigLabel()), "88");
        options = OptionsParser.parse(new String[]{ "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        options.appendToConf(conf);
        Assert.assertEquals(conf.getFloat(BANDWIDTH.getConfigLabel(), (-1)), 88.0, TestOptionsParser.DELTA);
        conf = new Configuration();
        conf.set(BANDWIDTH.getConfigLabel(), "88.0");
        Assert.assertEquals(conf.getRaw(BANDWIDTH.getConfigLabel()), "88.0");
        options = OptionsParser.parse(new String[]{ "-bandwidth", "99", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        options.appendToConf(conf);
        Assert.assertEquals(conf.getFloat(BANDWIDTH.getConfigLabel(), (-1)), 99.0, TestOptionsParser.DELTA);
    }

    @Test
    public void testAppendOption() {
        Configuration conf = new Configuration();
        Assert.assertFalse(conf.getBoolean(APPEND.getConfigLabel(), false));
        Assert.assertFalse(conf.getBoolean(SYNC_FOLDERS.getConfigLabel(), false));
        DistCpOptions options = OptionsParser.parse(new String[]{ "-update", "-append", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        options.appendToConf(conf);
        Assert.assertTrue(conf.getBoolean(APPEND.getConfigLabel(), false));
        Assert.assertTrue(conf.getBoolean(SYNC_FOLDERS.getConfigLabel(), false));
        // make sure -append is only valid when -update is specified
        try {
            OptionsParser.parse(new String[]{ "-append", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
            Assert.fail("Append should fail if update option is not specified");
        } catch (IllegalArgumentException e) {
            GenericTestUtils.assertExceptionContains("Append is valid only with update options", e);
        }
        // make sure -append is invalid when skipCrc is specified
        try {
            OptionsParser.parse(new String[]{ "-append", "-update", "-skipcrccheck", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
            Assert.fail("Append should fail if skipCrc option is specified");
        } catch (IllegalArgumentException e) {
            GenericTestUtils.assertExceptionContains("Append is disallowed when skipping CRC", e);
        }
    }

    @Test
    public void testDiffOption() {
        testSnapshotDiffOption(true);
    }

    @Test
    public void testRdiffOption() {
        testSnapshotDiffOption(false);
    }

    @Test
    public void testExclusionsOption() {
        DistCpOptions options = OptionsParser.parse(new String[]{ "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        Assert.assertNull(options.getFiltersFile());
        options = OptionsParser.parse(new String[]{ "-filters", "/tmp/filters.txt", "hdfs://localhost:8020/source/first", "hdfs://localhost:8020/target/" });
        Assert.assertEquals(options.getFiltersFile(), "/tmp/filters.txt");
    }
}

