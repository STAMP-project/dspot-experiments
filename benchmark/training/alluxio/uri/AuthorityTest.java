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
package alluxio.uri;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link Authority}.
 */
public class AuthorityTest {
    @Test
    public void authorityFromStringTest() {
        Assert.assertTrue(((Authority.fromString("localhost:19998")) instanceof SingleMasterAuthority));
        Assert.assertTrue(((Authority.fromString("127.0.0.1:19998")) instanceof SingleMasterAuthority));
        Assert.assertTrue(((Authority.fromString("zk@host:2181")) instanceof ZookeeperAuthority));
        Assert.assertTrue(((Authority.fromString("zk@host1:2181,127.0.0.2:2181,12.43.214.53:2181")) instanceof ZookeeperAuthority));
        Assert.assertTrue(((Authority.fromString("zk@host1:2181;host2:2181;host3:2181")) instanceof ZookeeperAuthority));
        Assert.assertTrue(((Authority.fromString("")) instanceof NoAuthority));
        Assert.assertTrue(((Authority.fromString(null)) instanceof NoAuthority));
        Assert.assertTrue(((Authority.fromString("localhost")) instanceof UnknownAuthority));
        Assert.assertTrue(((Authority.fromString("f3,321:sad")) instanceof UnknownAuthority));
        Assert.assertTrue(((Authority.fromString("localhost:")) instanceof UnknownAuthority));
        Assert.assertTrue(((Authority.fromString("127.0.0.1:19998,")) instanceof UnknownAuthority));
        Assert.assertTrue(((Authority.fromString("localhost:19998:8080")) instanceof UnknownAuthority));
        Assert.assertTrue(((Authority.fromString("localhost:asdsad")) instanceof UnknownAuthority));
        Assert.assertTrue(((Authority.fromString("zk@")) instanceof UnknownAuthority));
        Assert.assertTrue(((Authority.fromString("zk@;")) instanceof UnknownAuthority));
        Assert.assertTrue(((Authority.fromString("zk@localhost")) instanceof UnknownAuthority));
        Assert.assertTrue(((Authority.fromString("zk@127.0.0.1:port")) instanceof UnknownAuthority));
        Assert.assertTrue(((Authority.fromString("zk@127.0.0.1:2181,")) instanceof UnknownAuthority));
        Assert.assertTrue(((Authority.fromString("zk@127.0.0.1:2181,localhost")) instanceof UnknownAuthority));
        Assert.assertTrue(((Authority.fromString(",,,")) instanceof UnknownAuthority));
        Assert.assertTrue(((Authority.fromString(";;;")) instanceof UnknownAuthority));
    }

    @Test
    public void singleMasterAuthorityTest() {
        SingleMasterAuthority authority = ((SingleMasterAuthority) (Authority.fromString("localhost:19998")));
        Assert.assertEquals("localhost:19998", authority.toString());
        Assert.assertEquals("localhost", authority.getHost());
        Assert.assertEquals(19998, authority.getPort());
    }

    @Test
    public void multiMasterAuthorityTest() {
        MultiMasterAuthority authority = ((MultiMasterAuthority) (Authority.fromString("host1:19998,host2:19998,host3:19998")));
        Assert.assertEquals("host1:19998,host2:19998,host3:19998", authority.toString());
        Assert.assertEquals("host1:19998,host2:19998,host3:19998", authority.getMasterAddresses());
        authority = ((MultiMasterAuthority) (Authority.fromString("127.0.0.1:213,127.0.0.2:532423,127.0.0.3:3213")));
        Assert.assertEquals("127.0.0.1:213,127.0.0.2:532423,127.0.0.3:3213", authority.toString());
        Assert.assertEquals("127.0.0.1:213,127.0.0.2:532423,127.0.0.3:3213", authority.getMasterAddresses());
        authority = ((MultiMasterAuthority) (Authority.fromString("host1:19998;host2:19998;host3:19998")));
        Assert.assertEquals("host1:19998,host2:19998,host3:19998", authority.getMasterAddresses());
        authority = ((MultiMasterAuthority) (Authority.fromString("host1:19998+host2:19998+host3:19998")));
        Assert.assertEquals("host1:19998,host2:19998,host3:19998", authority.getMasterAddresses());
        Assert.assertFalse(((Authority.fromString("localhost:19998")) instanceof MultiMasterAuthority));
        Assert.assertFalse(((Authority.fromString("localhost:abc,127.0.0.1:dsa")) instanceof MultiMasterAuthority));
        Assert.assertFalse(((Authority.fromString(",,,")) instanceof MultiMasterAuthority));
        Assert.assertFalse(((Authority.fromString(";;;")) instanceof MultiMasterAuthority));
        Assert.assertFalse(((Authority.fromString("+++")) instanceof MultiMasterAuthority));
    }

    @Test
    public void zookeeperAuthorityTest() {
        ZookeeperAuthority authority = ((ZookeeperAuthority) (Authority.fromString("zk@host:2181")));
        Assert.assertEquals("zk@host:2181", authority.toString());
        Assert.assertEquals("host:2181", authority.getZookeeperAddress());
        authority = ((ZookeeperAuthority) (Authority.fromString("zk@127.0.0.1:2181,127.0.0.2:2181,127.0.0.3:2181")));
        Assert.assertEquals("zk@127.0.0.1:2181,127.0.0.2:2181,127.0.0.3:2181", authority.toString());
        Assert.assertEquals("127.0.0.1:2181,127.0.0.2:2181,127.0.0.3:2181", authority.getZookeeperAddress());
        authority = ((ZookeeperAuthority) (Authority.fromString("zk@host1:2181;host2:2181;host3:2181")));
        Assert.assertEquals("zk@host1:2181,host2:2181,host3:2181", authority.toString());
        Assert.assertEquals("host1:2181,host2:2181,host3:2181", authority.getZookeeperAddress());
        authority = ((ZookeeperAuthority) (Authority.fromString("zk@host1:2181+host2:2181+host3:2181")));
        Assert.assertEquals("zk@host1:2181,host2:2181,host3:2181", authority.toString());
        Assert.assertEquals("host1:2181,host2:2181,host3:2181", authority.getZookeeperAddress());
    }

    @Test
    public void mixedDelimiters() {
        String normalized = "a:0,b:0,c:0";
        for (String test : Arrays.asList("zk@a:0;b:0+c:0", "zk@a:0,b:0;c:0", "zk@a:0+b:0,c:0")) {
            Assert.assertEquals(normalized, getZookeeperAddress());
        }
    }
}

