/**
 * Copyright 2015-2019 The OpenZipkin Authors
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
package zipkin2.storage.cassandra;


import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.KeyspaceMetadata;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.VersionNumber;
import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import java.util.UUID;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;


public class SchemaTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void getKeyspaceMetadata_failsWhenVersionLessThan3_11_3() {
        Session session = Mockito.mock(Session.class);
        Cluster cluster = Mockito.mock(Cluster.class);
        Metadata metadata = Mockito.mock(Metadata.class);
        Host host = Mockito.mock(Host.class);
        Mockito.when(session.getCluster()).thenReturn(cluster);
        Mockito.when(cluster.getMetadata()).thenReturn(metadata);
        Mockito.when(metadata.getAllHosts()).thenReturn(Collections.singleton(host));
        Mockito.when(host.getHostId()).thenReturn(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        Mockito.when(host.getCassandraVersion()).thenReturn(VersionNumber.parse("3.11.2"));
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Host 11111111-1111-1111-1111-111111111111 is running Cassandra 3.11.2, but minimum version is 3.11.3");
        Schema.getKeyspaceMetadata(session, "zipkin2");
    }

    @Test
    public void getKeyspaceMetadata_failsWhenOneVersionLessThan3_11_3() {
        Session session = Mockito.mock(Session.class);
        Cluster cluster = Mockito.mock(Cluster.class);
        Metadata metadata = Mockito.mock(Metadata.class);
        Host host1 = Mockito.mock(Host.class);
        Host host2 = Mockito.mock(Host.class);
        Mockito.when(session.getCluster()).thenReturn(cluster);
        Mockito.when(cluster.getMetadata()).thenReturn(metadata);
        Mockito.when(metadata.getAllHosts()).thenReturn(ImmutableSet.of(host1, host2));
        Mockito.when(host1.getHostId()).thenReturn(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        Mockito.when(host2.getHostId()).thenReturn(UUID.fromString("22222222-2222-2222-2222-222222222222"));
        Mockito.when(host1.getCassandraVersion()).thenReturn(VersionNumber.parse("3.11.3"));
        Mockito.when(host2.getCassandraVersion()).thenReturn(VersionNumber.parse("3.11.2"));
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Host 22222222-2222-2222-2222-222222222222 is running Cassandra 3.11.2, but minimum version is 3.11.3");
        Schema.getKeyspaceMetadata(session, "zipkin2");
    }

    @Test
    public void getKeyspaceMetadata_passesWhenVersion3_11_3AndKeyspaceMetadataIsNotNull() {
        Session session = Mockito.mock(Session.class);
        Cluster cluster = Mockito.mock(Cluster.class);
        Metadata metadata = Mockito.mock(Metadata.class);
        Host host = Mockito.mock(Host.class);
        KeyspaceMetadata keyspaceMetadata = Mockito.mock(KeyspaceMetadata.class);
        Mockito.when(session.getCluster()).thenReturn(cluster);
        Mockito.when(cluster.getMetadata()).thenReturn(metadata);
        Mockito.when(metadata.getAllHosts()).thenReturn(Collections.singleton(host));
        Mockito.when(host.getCassandraVersion()).thenReturn(VersionNumber.parse("3.11.3"));
        Mockito.when(metadata.getKeyspace("zipkin2")).thenReturn(keyspaceMetadata);
        assertThat(Schema.getKeyspaceMetadata(session, "zipkin2")).isSameAs(keyspaceMetadata);
    }

    @Test
    public void getKeyspaceMetadata_passesWhenVersion3_11_4AndKeyspaceMetadataIsNotNull() {
        Session session = Mockito.mock(Session.class);
        Cluster cluster = Mockito.mock(Cluster.class);
        Metadata metadata = Mockito.mock(Metadata.class);
        Host host = Mockito.mock(Host.class);
        KeyspaceMetadata keyspaceMetadata = Mockito.mock(KeyspaceMetadata.class);
        Mockito.when(session.getCluster()).thenReturn(cluster);
        Mockito.when(cluster.getMetadata()).thenReturn(metadata);
        Mockito.when(metadata.getAllHosts()).thenReturn(Collections.singleton(host));
        Mockito.when(host.getCassandraVersion()).thenReturn(VersionNumber.parse("3.11.4"));
        Mockito.when(metadata.getKeyspace("zipkin2")).thenReturn(keyspaceMetadata);
        assertThat(Schema.getKeyspaceMetadata(session, "zipkin2")).isSameAs(keyspaceMetadata);
    }

    @Test
    public void ensureKeyspaceMetadata_failsWhenKeyspaceMetadataIsNotNull() {
        Session session = Mockito.mock(Session.class);
        Cluster cluster = Mockito.mock(Cluster.class);
        Metadata metadata = Mockito.mock(Metadata.class);
        Host host = Mockito.mock(Host.class);
        Mockito.when(session.getCluster()).thenReturn(cluster);
        Mockito.when(cluster.getMetadata()).thenReturn(metadata);
        Mockito.when(metadata.getAllHosts()).thenReturn(Collections.singleton(host));
        Mockito.when(host.getCassandraVersion()).thenReturn(VersionNumber.parse("3.11.3"));
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Cannot read keyspace metadata for keyspace");
        Schema.ensureKeyspaceMetadata(session, "zipkin2");
    }
}

