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
package org.apache.hadoop.ozone.container.common.volume;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.ozone.container.common.utils.HddsVolumeUtil;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.GenericTestUtils.LogCapturer;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


/**
 * Tests {@link VolumeSet} operations.
 */
public class TestVolumeSet {
    private OzoneConfiguration conf;

    private VolumeSet volumeSet;

    private final String baseDir = MiniDFSCluster.getBaseDirectory();

    private final String volume1 = (baseDir) + "disk1";

    private final String volume2 = (baseDir) + "disk2";

    private final List<String> volumes = new ArrayList<>();

    private static final String DUMMY_IP_ADDR = "0.0.0.0";

    @Rule
    public Timeout testTimeout = new Timeout(300000);

    @Test
    public void testVolumeSetInitialization() throws Exception {
        List<HddsVolume> volumesList = volumeSet.getVolumesList();
        // VolumeSet initialization should add volume1 and volume2 to VolumeSet
        Assert.assertEquals("VolumeSet intialization is incorrect", volumesList.size(), volumes.size());
        Assert.assertTrue("VolumeSet not initailized correctly", checkVolumeExistsInVolumeSet(volume1));
        Assert.assertTrue("VolumeSet not initailized correctly", checkVolumeExistsInVolumeSet(volume2));
    }

    @Test
    public void testAddVolume() {
        Assert.assertEquals(2, volumeSet.getVolumesList().size());
        // Add a volume to VolumeSet
        String volume3 = (baseDir) + "disk3";
        boolean success = volumeSet.addVolume(volume3);
        Assert.assertTrue(success);
        Assert.assertEquals(3, volumeSet.getVolumesList().size());
        Assert.assertTrue("AddVolume did not add requested volume to VolumeSet", checkVolumeExistsInVolumeSet(volume3));
    }

    @Test
    public void testFailVolume() throws Exception {
        // Fail a volume
        volumeSet.failVolume(volume1);
        // Failed volume should not show up in the volumeList
        Assert.assertEquals(1, volumeSet.getVolumesList().size());
        // Failed volume should be added to FailedVolumeList
        Assert.assertEquals("Failed volume not present in FailedVolumeMap", 1, volumeSet.getFailedVolumesList().size());
        Assert.assertEquals("Failed Volume list did not match", HddsVolumeUtil.getHddsRoot(volume1), volumeSet.getFailedVolumesList().get(0).getHddsRootDir().getPath());
        Assert.assertTrue(volumeSet.getFailedVolumesList().get(0).isFailed());
        // Failed volume should not exist in VolumeMap
        Assert.assertFalse(volumeSet.getVolumeMap().containsKey(volume1));
    }

    @Test
    public void testRemoveVolume() throws Exception {
        Assert.assertEquals(2, volumeSet.getVolumesList().size());
        // Remove a volume from VolumeSet
        volumeSet.removeVolume(volume1);
        Assert.assertEquals(1, volumeSet.getVolumesList().size());
        // Attempting to remove a volume which does not exist in VolumeSet should
        // log a warning.
        LogCapturer logs = LogCapturer.captureLogs(LogFactory.getLog(VolumeSet.class));
        volumeSet.removeVolume(volume1);
        Assert.assertEquals(1, volumeSet.getVolumesList().size());
        String expectedLogMessage = ("Volume : " + (HddsVolumeUtil.getHddsRoot(volume1))) + " does not exist in VolumeSet";
        Assert.assertTrue(("Log output does not contain expected log message: " + expectedLogMessage), logs.getOutput().contains(expectedLogMessage));
    }

    @Test
    public void testVolumeInInconsistentState() throws Exception {
        Assert.assertEquals(2, volumeSet.getVolumesList().size());
        // Add a volume to VolumeSet
        String volume3 = (baseDir) + "disk3";
        // Create the root volume dir and create a sub-directory within it.
        File newVolume = new File(volume3, HddsVolume.HDDS_VOLUME_DIR);
        System.out.println(("new volume root: " + newVolume));
        newVolume.mkdirs();
        Assert.assertTrue("Failed to create new volume root", newVolume.exists());
        File dataDir = new File(newVolume, "chunks");
        dataDir.mkdirs();
        Assert.assertTrue(dataDir.exists());
        // The new volume is in an inconsistent state as the root dir is
        // non-empty but the version file does not exist. Add Volume should
        // return false.
        boolean success = volumeSet.addVolume(volume3);
        Assert.assertFalse(success);
        Assert.assertEquals(2, volumeSet.getVolumesList().size());
        Assert.assertTrue("AddVolume should fail for an inconsistent volume", (!(checkVolumeExistsInVolumeSet(volume3))));
        // Delete volume3
        File volume = new File(volume3);
        FileUtils.deleteDirectory(volume);
    }

    @Test
    public void testShutdown() throws Exception {
        List<HddsVolume> volumesList = volumeSet.getVolumesList();
        volumeSet.shutdown();
        // Verify that the volumes are shutdown and the volumeUsage is set to null.
        for (HddsVolume volume : volumesList) {
            Assert.assertNull(volume.getVolumeInfo().getUsageForTesting());
            try {
                // getAvailable() should throw null pointer exception as usage is null.
                volume.getAvailable();
                Assert.fail("Volume shutdown failed.");
            } catch (IOException ex) {
                // Do Nothing. Exception is expected.
                Assert.assertTrue(ex.getMessage().contains("Volume Usage thread is not running."));
            }
        }
    }

    @Test
    public void testFailVolumes() throws Exception {
        VolumeSet volSet = null;
        File readOnlyVolumePath = new File(baseDir);
        // Set to readonly, so that this volume will be failed
        readOnlyVolumePath.setReadOnly();
        File volumePath = GenericTestUtils.getRandomizedTestDir();
        OzoneConfiguration ozoneConfig = new OzoneConfiguration();
        ozoneConfig.set(HDDS_DATANODE_DIR_KEY, (((readOnlyVolumePath.getAbsolutePath()) + ",") + (volumePath.getAbsolutePath())));
        volSet = new VolumeSet(UUID.randomUUID().toString(), ozoneConfig);
        Assert.assertTrue(((volSet.getFailedVolumesList().size()) == 1));
        Assert.assertEquals(readOnlyVolumePath, volSet.getFailedVolumesList().get(0).getHddsRootDir());
        // Set back to writable
        try {
            readOnlyVolumePath.setWritable(true);
        } finally {
            FileUtil.fullyDelete(volumePath);
        }
    }
}

