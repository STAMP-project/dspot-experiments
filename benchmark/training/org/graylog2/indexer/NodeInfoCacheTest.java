/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.indexer;


import java.util.Optional;
import org.graylog2.indexer.cluster.Cluster;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class NodeInfoCacheTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private Cluster cluster;

    private NodeInfoCache nodeInfoCache;

    @Test
    public void getNodeNameReturnsNodeNameIfNodeIdIsValid() {
        Mockito.when(cluster.nodeIdToName("node_id")).thenReturn(Optional.of("Node Name"));
        assertThat(nodeInfoCache.getNodeName("node_id")).contains("Node Name");
    }

    @Test
    public void getNodeNameUsesCache() {
        Mockito.when(cluster.nodeIdToName("node_id")).thenReturn(Optional.of("Node Name"));
        nodeInfoCache.getNodeName("node_id");
        nodeInfoCache.getNodeName("node_id");
        Mockito.verify(cluster, Mockito.times(1)).nodeIdToName("node_id");
    }

    @Test
    public void getNodeNameReturnsEmptyOptionalIfNodeIdIsInvalid() {
        Mockito.when(cluster.nodeIdToName("node_id")).thenReturn(Optional.empty());
        assertThat(nodeInfoCache.getNodeName("node_id")).isEmpty();
    }

    @Test
    public void getHostNameReturnsNodeNameIfNodeIdIsValid() {
        Mockito.when(cluster.nodeIdToHostName("node_id")).thenReturn(Optional.of("node-hostname"));
        assertThat(nodeInfoCache.getHostName("node_id")).contains("node-hostname");
    }

    @Test
    public void getHostNameUsesCache() {
        Mockito.when(cluster.nodeIdToHostName("node_id")).thenReturn(Optional.of("node-hostname"));
        nodeInfoCache.getHostName("node_id");
        nodeInfoCache.getHostName("node_id");
        Mockito.verify(cluster, Mockito.times(1)).nodeIdToHostName("node_id");
    }

    @Test
    public void getHostNameReturnsEmptyOptionalIfNodeIdIsInvalid() {
        Mockito.when(cluster.nodeIdToHostName("node_id")).thenReturn(Optional.empty());
        assertThat(nodeInfoCache.getHostName("node_id")).isEmpty();
    }
}

