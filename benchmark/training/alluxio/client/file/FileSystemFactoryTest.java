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
package alluxio.client.file;


import FileSystem.Factory;
import MasterInquireClient.ConnectDetails;
import PropertyKey.USER_WORKER_LIST_REFRESH_INTERVAL;
import PropertyKey.ZOOKEEPER_ADDRESS;
import PropertyKey.ZOOKEEPER_ELECTION_PATH;
import PropertyKey.ZOOKEEPER_ENABLED;
import alluxio.SystemPropertyRule;
import alluxio.conf.InstancedConfiguration;
import alluxio.master.MasterInquireClient;
import alluxio.uri.MultiMasterAuthority;
import alluxio.uri.ZookeeperAuthority;
import alluxio.util.ConfigurationUtils;
import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class FileSystemFactoryTest {
    @Rule
    public ExpectedException mThrown = ExpectedException.none();

    @Test
    public void testCloseRemovesFromCache() throws Exception {
        FileSystem fs1 = Factory.get();
        fs1.close();
        Assert.assertTrue("FileSystem should be marked as closed", fs1.isClosed());
        FileSystem fs2 = Factory.get();
        Assert.assertFalse("FileSystem shouldn't be closed", fs2.isClosed());
        Assert.assertNotSame("Should have different references", fs1, fs2);
    }

    @Test
    public void singleMasterFileSystemCacheTest() {
        fileSystemCacheTest();
    }

    @Test
    public void multiMasterFileSystemCacheTest() {
        try (Closeable p = toResource()) {
            ConfigurationUtils.reloadProperties();
            InstancedConfiguration conf = new InstancedConfiguration(ConfigurationUtils.defaults());
            MasterInquireClient.ConnectDetails connectDetails = MasterInquireClient.Factory.getConnectDetails(conf);
            // Make sure we have a MultiMaster authority
            Assert.assertTrue(((connectDetails.toAuthority()) instanceof MultiMasterAuthority));
            fileSystemCacheTest();
        } catch (IOException e) {
            Assert.fail("Unable to set system properties");
        }
    }

    @Test
    public void zkFileSystemCacheTest() {
        Map<String, String> sysProps = new HashMap<>();
        sysProps.put(ZOOKEEPER_ENABLED.getName(), Boolean.toString(true));
        sysProps.put(ZOOKEEPER_ADDRESS.getName(), "zk@192.168.0.5");
        sysProps.put(ZOOKEEPER_ELECTION_PATH.getName(), "/leader");
        try (Closeable p = new SystemPropertyRule(sysProps).toResource()) {
            ConfigurationUtils.reloadProperties();
            InstancedConfiguration conf = new InstancedConfiguration(ConfigurationUtils.defaults());
            MasterInquireClient.ConnectDetails connectDetails = MasterInquireClient.Factory.getConnectDetails(conf);
            // Make sure we have a Zookeeper authority
            Assert.assertTrue(((connectDetails.toAuthority()) instanceof ZookeeperAuthority));
            fileSystemCacheTest();
        } catch (IOException e) {
            Assert.fail("Unable to set system properties");
        }
    }

    @Test
    public void nullSubjectTest() {
        mThrown.expect(NullPointerException.class);
        Factory.get(null);
    }

    @Test
    public void uncachedFileSystemDoesntAffectCache() throws Exception {
        FileSystem fs1 = Factory.get();
        InstancedConfiguration conf = new InstancedConfiguration(ConfigurationUtils.defaults());
        conf.set(USER_WORKER_LIST_REFRESH_INTERVAL, "1sec");
        FileSystem fs2 = Factory.create(conf);
        fs2.close();
        FileSystem fs3 = Factory.get();
        Assert.assertSame("closing custom config should result in same FileSystem", fs1, fs3);
        Assert.assertFalse("FileSystem should not be closed", fs1.isClosed());
    }
}

