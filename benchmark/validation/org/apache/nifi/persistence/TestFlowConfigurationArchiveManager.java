/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.persistence;


import java.io.File;
import java.nio.file.NoSuchFileException;
import org.apache.nifi.util.NiFiProperties;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestFlowConfigurationArchiveManager {
    private final File flowFile = new File("./target/flow-archive/flow.xml.gz");

    private final File archiveDir = new File("./target/flow-archive");

    @Test
    public void testNiFiPropertiesDefault() throws Exception {
        final NiFiProperties defaultProperties = Mockito.mock(NiFiProperties.class);
        Mockito.when(defaultProperties.getFlowConfigurationArchiveMaxCount()).thenReturn(null);
        Mockito.when(defaultProperties.getFlowConfigurationArchiveMaxTime()).thenReturn(null);
        Mockito.when(defaultProperties.getFlowConfigurationArchiveMaxStorage()).thenReturn(null);
        final FlowConfigurationArchiveManager archiveManager = new FlowConfigurationArchiveManager(flowFile.toPath(), defaultProperties);
        assertNull(getPrivateFieldValue(archiveManager, "maxCount"));
        Assert.assertEquals(((((60L * 60L) * 24L) * 30L) * 1000L), getPrivateFieldValue(archiveManager, "maxTimeMillis"));
        Assert.assertEquals(((500L * 1024L) * 1024L), getPrivateFieldValue(archiveManager, "maxStorageBytes"));
    }

    @Test
    public void testNiFiPropertiesMaxTime() throws Exception {
        final NiFiProperties withMaxTime = Mockito.mock(NiFiProperties.class);
        Mockito.when(withMaxTime.getFlowConfigurationArchiveMaxCount()).thenReturn(null);
        Mockito.when(withMaxTime.getFlowConfigurationArchiveMaxTime()).thenReturn("10 days");
        Mockito.when(withMaxTime.getFlowConfigurationArchiveMaxStorage()).thenReturn(null);
        final FlowConfigurationArchiveManager archiveManager = new FlowConfigurationArchiveManager(flowFile.toPath(), withMaxTime);
        assertNull(getPrivateFieldValue(archiveManager, "maxCount"));
        Assert.assertEquals(((((60L * 60L) * 24L) * 10L) * 1000L), getPrivateFieldValue(archiveManager, "maxTimeMillis"));
        assertNull(getPrivateFieldValue(archiveManager, "maxStorageBytes"));
    }

    @Test
    public void testNiFiPropertiesMaxStorage() throws Exception {
        final NiFiProperties withMaxTime = Mockito.mock(NiFiProperties.class);
        Mockito.when(withMaxTime.getFlowConfigurationArchiveMaxCount()).thenReturn(null);
        Mockito.when(withMaxTime.getFlowConfigurationArchiveMaxTime()).thenReturn(null);
        Mockito.when(withMaxTime.getFlowConfigurationArchiveMaxStorage()).thenReturn("10 MB");
        final FlowConfigurationArchiveManager archiveManager = new FlowConfigurationArchiveManager(flowFile.toPath(), withMaxTime);
        assertNull(getPrivateFieldValue(archiveManager, "maxCount"));
        assertNull(getPrivateFieldValue(archiveManager, "maxTimeMillis"));
        Assert.assertEquals(((10L * 1024L) * 1024L), getPrivateFieldValue(archiveManager, "maxStorageBytes"));
    }

    @Test
    public void testNiFiPropertiesCount() throws Exception {
        final NiFiProperties onlyCount = Mockito.mock(NiFiProperties.class);
        Mockito.when(onlyCount.getFlowConfigurationArchiveMaxCount()).thenReturn(10);
        Mockito.when(onlyCount.getFlowConfigurationArchiveMaxTime()).thenReturn(null);
        Mockito.when(onlyCount.getFlowConfigurationArchiveMaxStorage()).thenReturn(null);
        final FlowConfigurationArchiveManager archiveManager = new FlowConfigurationArchiveManager(flowFile.toPath(), onlyCount);
        Assert.assertEquals(10, getPrivateFieldValue(archiveManager, "maxCount"));
        assertNull(getPrivateFieldValue(archiveManager, "maxTimeMillis"));
        assertNull(getPrivateFieldValue(archiveManager, "maxStorageBytes"));
    }

    @Test(expected = NoSuchFileException.class)
    public void testArchiveWithoutOriginalFile() throws Exception {
        final NiFiProperties properties = Mockito.mock(NiFiProperties.class);
        Mockito.when(properties.getFlowConfigurationArchiveDir()).thenReturn(archiveDir.getPath());
        final File flowFile = new File("does-not-exist");
        final FlowConfigurationArchiveManager archiveManager = new FlowConfigurationArchiveManager(flowFile.toPath(), properties);
        archiveManager.archive();
    }

    @Test
    public void testArchiveExpiration() throws Exception {
        final long intervalMillis = 60000;
        File[] oldArchives = new File[5];
        createSimulatedOldArchives(oldArchives, intervalMillis);
        // Now, we will test expiration. There should be following old archives created above:
        // -5 min, -4 min, -3min, -2min, -1min
        final long maxTimeForExpirationTest = (intervalMillis * 3) + (intervalMillis / 2);
        final FlowConfigurationArchiveManager archiveManager = createArchiveManager(null, (maxTimeForExpirationTest + "ms"), null);
        final File archive = archiveManager.archive();
        Assert.assertTrue((!(oldArchives[0].exists())));// -5 min

        Assert.assertTrue((!(oldArchives[1].exists())));// -4 min

        Assert.assertTrue(oldArchives[2].isFile());// -3 min

        Assert.assertTrue(oldArchives[3].isFile());// -2 min

        Assert.assertTrue(oldArchives[4].isFile());// -1 min

        Assert.assertTrue(archive.exists());// new archive

        Assert.assertTrue("Original file should remain intact", flowFile.isFile());
    }

    @Test
    public void testArchiveStorageSizeLimit() throws Exception {
        final long intervalMillis = 60000;
        File[] oldArchives = new File[5];
        createSimulatedOldArchives(oldArchives, intervalMillis);
        // Now, we will test storage size limit. There should be following old archives created above:
        // -5 min, -4 min, -3min, -2min, -1min, each of those have 10 bytes.
        final FlowConfigurationArchiveManager archiveManager = createArchiveManager(null, null, "20b");
        final File archive = archiveManager.archive();
        Assert.assertTrue((!(oldArchives[0].exists())));// -5 min

        Assert.assertTrue((!(oldArchives[1].exists())));// -4 min

        Assert.assertTrue((!(oldArchives[2].exists())));// -3 min

        Assert.assertTrue((!(oldArchives[3].exists())));// -2 min

        Assert.assertTrue(oldArchives[4].exists());// -1 min

        Assert.assertTrue(archive.exists());// new archive

        Assert.assertTrue("Original file should remain intact", flowFile.isFile());
    }

    @Test
    public void testArchiveStorageCountLimit() throws Exception {
        final long intervalMillis = 60000;
        File[] oldArchives = new File[5];
        createSimulatedOldArchives(oldArchives, intervalMillis);
        // Now, we will test count limit. There should be following old archives created above:
        // -5 min, -4 min, -3min, -2min, -1min, each of those have 10 bytes.
        final FlowConfigurationArchiveManager archiveManager = createArchiveManager(2, null, null);
        final File archive = archiveManager.archive();
        Assert.assertTrue((!(oldArchives[0].exists())));// -5 min

        Assert.assertTrue((!(oldArchives[1].exists())));// -4 min

        Assert.assertTrue((!(oldArchives[2].exists())));// -3 min

        Assert.assertTrue((!(oldArchives[3].exists())));// -2 min

        Assert.assertTrue(oldArchives[4].exists());// -1 min

        Assert.assertTrue(archive.exists());// new archive

        Assert.assertTrue("Original file should remain intact", flowFile.isFile());
    }

    @Test
    public void testLargeConfigFile() throws Exception {
        final long intervalMillis = 60000;
        File[] oldArchives = new File[5];
        createSimulatedOldArchives(oldArchives, intervalMillis);
        // Now, we will test storage size limit. There should be following old archives created above:
        // -5 min, -4 min, -3min, -2min, -1min, each of those have 10 bytes.
        final FlowConfigurationArchiveManager archiveManager = createArchiveManager(null, null, "3b");
        final File archive = archiveManager.archive();
        Assert.assertTrue((!(oldArchives[0].exists())));// -5 min

        Assert.assertTrue((!(oldArchives[1].exists())));// -4 min

        Assert.assertTrue((!(oldArchives[2].exists())));// -3 min

        Assert.assertTrue((!(oldArchives[3].exists())));// -2 min

        Assert.assertTrue((!(oldArchives[4].exists())));// -1 min

        Assert.assertTrue("Even if flow config file is larger than maxStorage file, it can be archived", archive.exists());// new archive

        Assert.assertTrue("Original file should remain intact", flowFile.isFile());
    }
}

