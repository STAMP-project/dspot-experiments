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
package org.graylog2.indexer.rotation.strategies;


import java.util.Optional;
import org.graylog2.audit.AuditEventSender;
import org.graylog2.indexer.IndexSet;
import org.graylog2.indexer.indexset.IndexSetConfig;
import org.graylog2.indexer.indices.Indices;
import org.graylog2.plugin.system.NodeId;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class SizeBasedRotationStrategyTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private IndexSet indexSet;

    @Mock
    private IndexSetConfig indexSetConfig;

    @Mock
    private Indices indices;

    @Mock
    private NodeId nodeId;

    @Mock
    private AuditEventSender auditEventSender;

    @Test
    public void testRotate() throws Exception {
        Mockito.when(indices.getStoreSizeInBytes("name")).thenReturn(Optional.of(1000L));
        Mockito.when(indexSet.getNewestIndex()).thenReturn("name");
        Mockito.when(indexSet.getConfig()).thenReturn(indexSetConfig);
        Mockito.when(indexSetConfig.rotationStrategy()).thenReturn(SizeBasedRotationStrategyConfig.create(100L));
        final SizeBasedRotationStrategy strategy = new SizeBasedRotationStrategy(indices, nodeId, auditEventSender);
        strategy.rotate(indexSet);
        Mockito.verify(indexSet, Mockito.times(1)).cycle();
        Mockito.reset(indexSet);
    }

    @Test
    public void testDontRotate() throws Exception {
        Mockito.when(indices.getStoreSizeInBytes("name")).thenReturn(Optional.of(1000L));
        Mockito.when(indexSet.getNewestIndex()).thenReturn("name");
        Mockito.when(indexSet.getConfig()).thenReturn(indexSetConfig);
        Mockito.when(indexSetConfig.rotationStrategy()).thenReturn(SizeBasedRotationStrategyConfig.create(100000L));
        final SizeBasedRotationStrategy strategy = new SizeBasedRotationStrategy(indices, nodeId, auditEventSender);
        strategy.rotate(indexSet);
        Mockito.verify(indexSet, Mockito.never()).cycle();
        Mockito.reset(indexSet);
    }

    @Test
    public void testRotateFailed() throws Exception {
        Mockito.when(indices.getStoreSizeInBytes("name")).thenReturn(Optional.empty());
        Mockito.when(indexSet.getNewestIndex()).thenReturn("name");
        Mockito.when(indexSet.getConfig()).thenReturn(indexSetConfig);
        Mockito.when(indexSetConfig.rotationStrategy()).thenReturn(SizeBasedRotationStrategyConfig.create(100L));
        final SizeBasedRotationStrategy strategy = new SizeBasedRotationStrategy(indices, nodeId, auditEventSender);
        strategy.rotate(indexSet);
        Mockito.verify(indexSet, Mockito.never()).cycle();
        Mockito.reset(indexSet);
    }
}

