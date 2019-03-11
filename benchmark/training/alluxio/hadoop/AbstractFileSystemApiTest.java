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


import PropertyKey.ZOOKEEPER_ADDRESS;
import PropertyKey.ZOOKEEPER_ENABLED;
import alluxio.ConfigurationTestUtils;
import alluxio.TestLoggerRule;
import alluxio.conf.InstancedConfiguration;
import java.io.IOException;
import java.net.URI;
import org.apache.hadoop.conf.Configuration;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Tests for {@link AbstractFileSystem}. Unlike {@link AbstractFileSystemTest}, these tests only
 * exercise the public API of {@link AbstractFileSystem}.
 */
public final class AbstractFileSystemApiTest {
    @Rule
    public TestLoggerRule mTestLogger = new TestLoggerRule();

    @Rule
    public ExpectedException mThrown = ExpectedException.none();

    private InstancedConfiguration mConf = ConfigurationTestUtils.defaults();

    @Test
    public void unknownAuthorityTriggersWarning() throws IOException {
        URI unknown = URI.create("alluxio://test/");
        mThrown.expectMessage((("Authority \"test\" is unknown. The client can not be configured with " + "the authority from ") + unknown));
        FileSystem.get(unknown, new Configuration());
    }

    @Test
    public void noAuthorityNoWarning() throws IOException {
        URI unknown = URI.create("alluxio:///");
        FileSystem.get(unknown, new Configuration());
        Assert.assertFalse(loggedAuthorityWarning());
    }

    @Test
    public void validAuthorityNoWarning() throws IOException {
        URI unknown = URI.create("alluxio://localhost:12345/");
        FileSystem.get(unknown, new Configuration());
        Assert.assertFalse(loggedAuthorityWarning());
    }

    @Test
    public void parseZkUriWithPlusDelimiters() throws Exception {
        org.apache.hadoop.fs.FileSystem fs = FileSystem.get(URI.create("alluxio://zk@a:0+b:1+c:2/"), new Configuration());
        Assert.assertTrue((fs instanceof AbstractFileSystem));
        AbstractFileSystem afs = ((AbstractFileSystem) (fs));
        Assert.assertTrue(afs.mFileSystem.getConf().getBoolean(ZOOKEEPER_ENABLED));
        Assert.assertEquals("a:0,b:1,c:2", afs.mFileSystem.getConf().get(ZOOKEEPER_ADDRESS));
    }
}

