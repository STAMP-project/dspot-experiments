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
package alluxio.underfs.hdfs;


import PropertyKey.UNDERFS_HDFS_IMPL;
import alluxio.ConfigurationTestUtils;
import alluxio.conf.AlluxioConfiguration;
import alluxio.underfs.UnderFileSystemConfiguration;
import org.apache.hadoop.conf.Configuration;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link HdfsUnderFileSystem}.
 */
public final class HdfsUnderFileSystemTest {
    private HdfsUnderFileSystem mHdfsUnderFileSystem;

    private final AlluxioConfiguration mAlluxioConf = ConfigurationTestUtils.defaults();

    /**
     * Tests the {@link HdfsUnderFileSystem#getUnderFSType()} method. Confirm the UnderFSType for
     * HdfsUnderFileSystem.
     */
    @Test
    public void getUnderFSType() throws Exception {
        Assert.assertEquals("hdfs", mHdfsUnderFileSystem.getUnderFSType());
    }

    /**
     * Tests the {@link HdfsUnderFileSystem#createConfiguration} method.
     *
     * Checks the hdfs implements class and alluxio underfs config setting
     */
    @Test
    public void prepareConfiguration() throws Exception {
        UnderFileSystemConfiguration ufsConf = UnderFileSystemConfiguration.defaults();
        Configuration conf = HdfsUnderFileSystem.createConfiguration(ufsConf);
        Assert.assertEquals(ufsConf.get(UNDERFS_HDFS_IMPL), conf.get("fs.hdfs.impl"));
        Assert.assertTrue(conf.getBoolean("fs.hdfs.impl.disable.cache", false));
    }
}

