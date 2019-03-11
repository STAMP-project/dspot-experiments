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
package alluxio.wire;


import PropertyKey.LOCALITY_COMPARE_NODE_IP;
import alluxio.ConfigurationTestUtils;
import alluxio.conf.InstancedConfiguration;
import alluxio.grpc.GrpcUtils;
import alluxio.network.TieredIdentityFactory;
import alluxio.util.TieredIdentityUtils;
import alluxio.wire.TieredIdentity.LocalityTier;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


/**
 * Unit tests for {@link TieredIdentity}.
 */
public class TieredIdentityTest {
    private InstancedConfiguration mConfiguration = ConfigurationTestUtils.defaults();

    @Test
    public void nearest() throws Exception {
        TieredIdentity id1 = TieredIdentityFactory.fromString("node=A,rack=rack1", mConfiguration);
        TieredIdentity id2 = TieredIdentityFactory.fromString("node=B,rack=rack2", mConfiguration);
        TieredIdentity id3 = TieredIdentityFactory.fromString("node=C,rack=rack2", mConfiguration);
        List<TieredIdentity> identities = Arrays.asList(id1, id2, id3);
        boolean resolveIp = mConfiguration.getBoolean(LOCALITY_COMPARE_NODE_IP);
        Assert.assertSame(id1, TieredIdentityUtils.nearest(TieredIdentityFactory.fromString("node=D,rack=rack1", mConfiguration), identities, mConfiguration).get());
        Assert.assertSame(id2, TieredIdentityUtils.nearest(TieredIdentityFactory.fromString("node=B,rack=rack2", mConfiguration), identities, mConfiguration).get());
        Assert.assertSame(id3, TieredIdentityUtils.nearest(TieredIdentityFactory.fromString("node=C,rack=rack2", mConfiguration), identities, mConfiguration).get());
        Assert.assertSame(id1, TieredIdentityUtils.nearest(TieredIdentityFactory.fromString("node=D,rack=rack3", mConfiguration), identities, mConfiguration).get());
    }

    @Test
    public void json() throws Exception {
        TieredIdentity tieredIdentity = TieredIdentityTest.createRandomTieredIdentity();
        ObjectMapper mapper = new ObjectMapper();
        TieredIdentity other = mapper.readValue(mapper.writeValueAsBytes(tieredIdentity), TieredIdentity.class);
        checkEquality(tieredIdentity, other);
    }

    @Test
    public void proto() {
        TieredIdentity tieredIdentity = TieredIdentityTest.createRandomTieredIdentity();
        TieredIdentity other = GrpcUtils.fromProto(GrpcUtils.toProto(tieredIdentity));
        checkEquality(tieredIdentity, other);
    }

    @Test
    public void matchByStringEquality() {
        LocalityTier lt1 = new LocalityTier("node", "NonResolvableHostname-A");
        LocalityTier lt2 = new LocalityTier("node", "NonResolvableHostname-A");
        LocalityTier lt3 = new LocalityTier("node", "NonResolvableHostname-B");
        LocalityTier lt4 = new LocalityTier("rack", "NonResolvableHostname-A");
        LocalityTier lt5 = new LocalityTier("rack", "NonResolvableHostname-B");
        LocalityTier lt6 = new LocalityTier("rack", "NonResolvableHostname-B");
        LocalityTier lt7 = new LocalityTier("rack", "");
        LocalityTier lt8 = new LocalityTier("node", "NonResolvableHostname-A");
        LocalityTier lt9 = new LocalityTier("node", "");
        Assert.assertTrue(TieredIdentityUtils.matches(lt1, lt1, true));
        Assert.assertTrue(TieredIdentityUtils.matches(lt1, lt2, true));
        Assert.assertFalse(TieredIdentityUtils.matches(lt2, lt3, true));
        Assert.assertTrue(TieredIdentityUtils.matches(lt5, lt6, true));
        Assert.assertFalse(TieredIdentityUtils.matches(lt4, lt5, true));
        Assert.assertFalse(TieredIdentityUtils.matches(lt6, lt7, true));
        Assert.assertFalse(TieredIdentityUtils.matches(lt8, lt9, true));
    }

    @Test
    public void matchByIpResolution() throws Exception {
        Assume.assumeTrue(InetAddress.getByName("localhost").getHostAddress().equals("127.0.0.1"));
        LocalityTier lt1 = new LocalityTier("node", "localhost");
        LocalityTier lt2 = new LocalityTier("node", "127.0.0.1");
        Assert.assertTrue(TieredIdentityUtils.matches(lt1, lt2, true));
        Assert.assertFalse(TieredIdentityUtils.matches(lt1, lt2, false));
    }
}

