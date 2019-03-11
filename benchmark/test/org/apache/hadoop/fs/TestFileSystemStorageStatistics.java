/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.fs;


import FileSystem.Statistics;
import java.util.Iterator;
import org.apache.hadoop.fs.StorageStatistics.LongStatistic;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This tests basic operations of {@link FileSystemStorageStatistics} class.
 */
public class TestFileSystemStorageStatistics {
    private static final Logger LOG = LoggerFactory.getLogger(TestFileSystemStorageStatistics.class);

    private static final String FS_STORAGE_STATISTICS_NAME = "test-fs-statistics";

    private static final String[] STATISTICS_KEYS = new String[]{ "bytesRead", "bytesWritten", "readOps", "largeReadOps", "writeOps", "bytesReadLocalHost", "bytesReadDistanceOfOneOrTwo", "bytesReadDistanceOfThreeOrFour", "bytesReadDistanceOfFiveOrLarger", "bytesReadErasureCoded" };

    private Statistics statistics = new FileSystem.Statistics("test-scheme");

    private FileSystemStorageStatistics storageStatistics = new FileSystemStorageStatistics(TestFileSystemStorageStatistics.FS_STORAGE_STATISTICS_NAME, statistics);

    @Rule
    public final Timeout globalTimeout = new Timeout((10 * 1000));

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testGetLongStatistics() {
        Iterator<LongStatistic> iter = storageStatistics.getLongStatistics();
        while (iter.hasNext()) {
            final LongStatistic longStat = iter.next();
            Assert.assertNotNull(longStat);
            final long expectedStat = getStatisticsValue(longStat.getName());
            TestFileSystemStorageStatistics.LOG.info("{}: FileSystem.Statistics={}, FileSystemStorageStatistics={}", longStat.getName(), expectedStat, longStat.getValue());
            Assert.assertEquals(expectedStat, longStat.getValue());
        } 
    }

    @Test
    public void testGetLong() {
        for (String key : TestFileSystemStorageStatistics.STATISTICS_KEYS) {
            final long expectedStat = getStatisticsValue(key);
            final long storageStat = storageStatistics.getLong(key);
            TestFileSystemStorageStatistics.LOG.info("{}: FileSystem.Statistics={}, FileSystemStorageStatistics={}", key, expectedStat, storageStat);
            Assert.assertEquals(expectedStat, storageStat);
        }
    }
}

