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
package alluxio.hadoop;


import PropertyKey.S3A_ACCESS_KEY;
import PropertyKey.S3A_SECRET_KEY;
import PropertyKey.ZOOKEEPER_ADDRESS;
import PropertyKey.ZOOKEEPER_ENABLED;
import Source.RUNTIME;
import alluxio.ConfigurationTestUtils;
import alluxio.conf.InstancedConfiguration;
import org.apache.hadoop.conf.Configuration;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link HadoopConfigurationUtils} class.
 */
public final class HadoopConfigurationUtilsTest {
    private static final String TEST_S3_ACCCES_KEY = "TEST ACCESS KEY";

    private static final String TEST_S3_SECRET_KEY = "TEST SECRET KEY";

    private static final String TEST_ALLUXIO_PROPERTY = "alluxio.unsupported.parameter";

    private static final String TEST_ALLUXIO_VALUE = "alluxio.unsupported.value";

    private InstancedConfiguration mConf = ConfigurationTestUtils.defaults();

    /**
     * Test for the {@link HadoopConfigurationUtils#mergeHadoopConfiguration} method for an empty
     * configuration.
     */
    @Test
    public void mergeEmptyHadoopConfiguration() {
        Configuration hadoopConfig = new Configuration();
        long beforeSize = mConf.toMap().size();
        mConf = HadoopConfigurationUtils.mergeHadoopConfiguration(hadoopConfig, mConf.copyProperties());
        long afterSize = mConf.toMap().size();
        Assert.assertEquals(beforeSize, afterSize);
        Assert.assertFalse(mConf.getBoolean(ZOOKEEPER_ENABLED));
    }

    /**
     * Test for the {@link HadoopConfigurationUtils#mergeHadoopConfiguration} method.
     */
    @Test
    public void mergeHadoopConfiguration() {
        Configuration hadoopConfig = new Configuration();
        hadoopConfig.set(S3A_ACCESS_KEY.toString(), HadoopConfigurationUtilsTest.TEST_S3_ACCCES_KEY);
        hadoopConfig.set(S3A_SECRET_KEY.toString(), HadoopConfigurationUtilsTest.TEST_S3_SECRET_KEY);
        hadoopConfig.set(HadoopConfigurationUtilsTest.TEST_ALLUXIO_PROPERTY, HadoopConfigurationUtilsTest.TEST_ALLUXIO_VALUE);
        hadoopConfig.setBoolean(ZOOKEEPER_ENABLED.getName(), true);
        hadoopConfig.set(ZOOKEEPER_ADDRESS.getName(), "host1:port1,host2:port2;host3:port3");
        // This hadoop config will not be loaded into Alluxio configuration.
        hadoopConfig.set("hadoop.config.parameter", "hadoop config value");
        mConf = HadoopConfigurationUtils.mergeHadoopConfiguration(hadoopConfig, mConf.copyProperties());
        Assert.assertEquals(HadoopConfigurationUtilsTest.TEST_S3_ACCCES_KEY, mConf.get(S3A_ACCESS_KEY));
        Assert.assertEquals(HadoopConfigurationUtilsTest.TEST_S3_SECRET_KEY, mConf.get(S3A_SECRET_KEY));
        Assert.assertEquals(RUNTIME, mConf.getSource(S3A_ACCESS_KEY));
        Assert.assertEquals(RUNTIME, mConf.getSource(S3A_SECRET_KEY));
        Assert.assertTrue(mConf.getBoolean(ZOOKEEPER_ENABLED));
        Assert.assertEquals("host1:port1,host2:port2;host3:port3", mConf.get(ZOOKEEPER_ADDRESS));
    }
}

