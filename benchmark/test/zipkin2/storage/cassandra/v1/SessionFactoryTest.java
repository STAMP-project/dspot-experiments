/**
 * Copyright 2015-2018 The OpenZipkin Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package zipkin2.storage.cassandra.v1;


import AuthProvider.NONE;
import HostDistance.IGNORED;
import HostDistance.LOCAL;
import com.datastax.driver.core.Authenticator;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import com.datastax.driver.core.policies.RoundRobinPolicy;
import java.net.InetSocketAddress;
import java.util.Arrays;
import org.junit.Test;
import org.mockito.Mockito;


public class SessionFactoryTest {
    @Test
    public void contactPoints_defaultsToLocalhost() {
        assertThat(parseContactPoints(CassandraStorage.newBuilder().build())).containsExactly(new InetSocketAddress("127.0.0.1", 9042));
    }

    @Test
    public void contactPoints_defaultsToPort9042() {
        assertThat(parseContactPoints(CassandraStorage.newBuilder().contactPoints("1.1.1.1").build())).containsExactly(new InetSocketAddress("1.1.1.1", 9042));
    }

    @Test
    public void contactPoints_defaultsToPort9042_multi() {
        assertThat(parseContactPoints(CassandraStorage.newBuilder().contactPoints("1.1.1.1:9143,2.2.2.2").build())).containsExactly(new InetSocketAddress("1.1.1.1", 9143), new InetSocketAddress("2.2.2.2", 9042));
    }

    @Test
    public void contactPoints_hostAndPort() {
        assertThat(parseContactPoints(CassandraStorage.newBuilder().contactPoints("1.1.1.1:9142").build())).containsExactly(new InetSocketAddress("1.1.1.1", 9142));
    }

    @Test
    public void connectPort_singleContactPoint() {
        assertThat(buildCluster(CassandraStorage.newBuilder().contactPoints("1.1.1.1:9142").build()).getConfiguration().getProtocolOptions().getPort()).isEqualTo(9142);
    }

    @Test
    public void connectPort_whenContactPointsHaveSamePort() {
        assertThat(buildCluster(CassandraStorage.newBuilder().contactPoints("1.1.1.1:9143,2.2.2.2:9143").build()).getConfiguration().getProtocolOptions().getPort()).isEqualTo(9143);
    }

    @Test
    public void connectPort_whenContactPointsHaveMixedPorts_coercesToDefault() {
        assertThat(buildCluster(CassandraStorage.newBuilder().contactPoints("1.1.1.1:9143,2.2.2.2").build()).getConfiguration().getProtocolOptions().getPort()).isEqualTo(9042);
    }

    @Test
    public void usernamePassword_impliesNullDelimitedUtf8Bytes() {
        Authenticator authenticator = buildCluster(CassandraStorage.newBuilder().username("bob").password("secret").build()).getConfiguration().getProtocolOptions().getAuthProvider().newAuthenticator(new InetSocketAddress("localhost", 8080), null);
        byte[] SASLhandshake = new byte[]{ 0, 'b', 'o', 'b', 0, 's', 'e', 'c', 'r', 'e', 't' };
        assertThat(authenticator.initialResponse()).isEqualTo(SASLhandshake);
    }

    @Test
    public void authProvider_defaultsToNone() {
        assertThat(buildCluster(CassandraStorage.newBuilder().build()).getConfiguration().getProtocolOptions().getAuthProvider()).isEqualTo(NONE);
    }

    @Test
    public void loadBalancing_defaultsToRoundRobin() {
        RoundRobinPolicy policy = toRoundRobinPolicy(CassandraStorage.newBuilder().build());
        Host foo = Mockito.mock(Host.class);
        Mockito.when(foo.getDatacenter()).thenReturn("foo");
        Host bar = Mockito.mock(Host.class);
        Mockito.when(bar.getDatacenter()).thenReturn("bar");
        policy.init(Mockito.mock(Cluster.class), Arrays.asList(foo, bar));
        assertThat(policy.distance(foo)).isEqualTo(LOCAL);
        assertThat(policy.distance(bar)).isEqualTo(LOCAL);
    }

    @Test
    public void loadBalancing_settingLocalDcIgnoresOtherDatacenters() {
        DCAwareRoundRobinPolicy policy = toDCAwareRoundRobinPolicy(CassandraStorage.newBuilder().localDc("bar").build());
        Host foo = Mockito.mock(Host.class);
        Mockito.when(foo.getDatacenter()).thenReturn("foo");
        Host bar = Mockito.mock(Host.class);
        Mockito.when(bar.getDatacenter()).thenReturn("bar");
        policy.init(Mockito.mock(Cluster.class), Arrays.asList(foo, bar));
        assertThat(policy.distance(foo)).isEqualTo(IGNORED);
        assertThat(policy.distance(bar)).isEqualTo(LOCAL);
    }

    @Test
    public void maxConnections_defaultsTo8() {
        PoolingOptions poolingOptions = buildCluster(CassandraStorage.newBuilder().build()).getConfiguration().getPoolingOptions();
        assertThat(poolingOptions.getMaxConnectionsPerHost(LOCAL)).isEqualTo(8);
    }

    @Test
    public void maxConnections_setsMaxConnectionsPerDatacenterLocalHost() {
        PoolingOptions poolingOptions = buildCluster(CassandraStorage.newBuilder().maxConnections(16).build()).getConfiguration().getPoolingOptions();
        assertThat(poolingOptions.getMaxConnectionsPerHost(LOCAL)).isEqualTo(16);
    }
}

