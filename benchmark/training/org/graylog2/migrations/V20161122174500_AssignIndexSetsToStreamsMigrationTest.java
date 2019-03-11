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
package org.graylog2.migrations;


import V20161122174500_AssignIndexSetsToStreamsMigration.MigrationCompleted;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.time.ZonedDateTime;
import java.util.Collections;
import org.graylog2.indexer.indexset.IndexSetConfig;
import org.graylog2.indexer.indexset.IndexSetService;
import org.graylog2.plugin.cluster.ClusterConfigService;
import org.graylog2.plugin.database.ValidationException;
import org.graylog2.plugin.streams.Stream;
import org.graylog2.streams.StreamService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class V20161122174500_AssignIndexSetsToStreamsMigrationTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Mock
    private IndexSetService indexSetService;

    @Mock
    private StreamService streamService;

    @Mock
    private ClusterConfigService clusterConfigService;

    private Migration migration;

    @Test
    public void createdAt() throws Exception {
        // Test the date to detect accidental changes to it.
        assertThat(migration.createdAt()).isEqualTo(ZonedDateTime.parse("2016-11-22T17:45:00Z"));
    }

    @Test
    public void upgrade() throws Exception {
        final Stream stream1 = Mockito.mock(Stream.class);
        final Stream stream2 = Mockito.mock(Stream.class);
        final IndexSetConfig indexSetConfig = Mockito.mock(IndexSetConfig.class);
        Mockito.when(indexSetService.findAll()).thenReturn(Collections.singletonList(indexSetConfig));
        Mockito.when(indexSetConfig.id()).thenReturn("abc123");
        Mockito.when(stream1.getId()).thenReturn("stream1");
        Mockito.when(stream2.getId()).thenReturn("stream2");
        Mockito.when(streamService.loadAll()).thenReturn(Lists.newArrayList(stream1, stream2));
        migration.upgrade();
        Mockito.verify(stream1).setIndexSetId(indexSetConfig.id());
        Mockito.verify(stream2).setIndexSetId(indexSetConfig.id());
        Mockito.verify(streamService, Mockito.times(1)).save(stream1);
        Mockito.verify(streamService, Mockito.times(1)).save(stream2);
        Mockito.verify(clusterConfigService, Mockito.times(1)).write(MigrationCompleted.create(indexSetConfig.id(), Sets.newHashSet("stream1", "stream2"), Collections.emptySet()));
    }

    @Test
    public void upgradeWithAlreadyAssignedIndexSet() throws Exception {
        final Stream stream1 = Mockito.mock(Stream.class);
        final Stream stream2 = Mockito.mock(Stream.class);
        final IndexSetConfig indexSetConfig = Mockito.mock(IndexSetConfig.class);
        Mockito.when(indexSetService.findAll()).thenReturn(Collections.singletonList(indexSetConfig));
        Mockito.when(indexSetConfig.id()).thenReturn("abc123");
        Mockito.when(stream1.getId()).thenReturn("stream1");
        Mockito.when(stream2.getId()).thenReturn("stream2");
        Mockito.when(streamService.loadAll()).thenReturn(Lists.newArrayList(stream1, stream2));
        Mockito.when(stream2.getIndexSetId()).thenReturn("abc123");
        migration.upgrade();
        Mockito.verify(stream1).setIndexSetId(indexSetConfig.id());
        Mockito.verify(stream2, Mockito.never()).setIndexSetId(indexSetConfig.id());
        Mockito.verify(streamService, Mockito.times(1)).save(stream1);
        Mockito.verify(streamService, Mockito.never()).save(stream2);
        Mockito.verify(clusterConfigService, Mockito.times(1)).write(MigrationCompleted.create(indexSetConfig.id(), Sets.newHashSet("stream1"), Collections.emptySet()));
    }

    @Test
    public void upgradeWithFailedStreamUpdate() throws Exception {
        final Stream stream1 = Mockito.mock(Stream.class);
        final Stream stream2 = Mockito.mock(Stream.class);
        final IndexSetConfig indexSetConfig = Mockito.mock(IndexSetConfig.class);
        Mockito.when(indexSetService.findAll()).thenReturn(Collections.singletonList(indexSetConfig));
        Mockito.when(indexSetConfig.id()).thenReturn("abc123");
        Mockito.when(stream1.getId()).thenReturn("stream1");
        Mockito.when(stream2.getId()).thenReturn("stream2");
        Mockito.when(streamService.loadAll()).thenReturn(Lists.newArrayList(stream1, stream2));
        // Updating stream1 should fail!
        Mockito.when(streamService.save(stream1)).thenThrow(ValidationException.class);
        migration.upgrade();
        Mockito.verify(stream1).setIndexSetId(indexSetConfig.id());
        Mockito.verify(stream2).setIndexSetId(indexSetConfig.id());
        Mockito.verify(streamService, Mockito.times(1)).save(stream1);
        Mockito.verify(streamService, Mockito.times(1)).save(stream2);
        // Check that the failed stream1 will be recorded as failed!
        Mockito.verify(clusterConfigService, Mockito.times(1)).write(MigrationCompleted.create(indexSetConfig.id(), Sets.newHashSet("stream2"), Sets.newHashSet("stream1")));
    }

    @Test
    public void upgradeWithoutAnyIndexSetConfig() throws Exception {
        Mockito.when(indexSetService.findAll()).thenReturn(Collections.emptyList());
        expectedException.expect(IllegalStateException.class);
        migration.upgrade();
    }

    @Test
    public void upgradeWithMoreThanOneIndexSetConfig() throws Exception {
        Mockito.when(indexSetService.findAll()).thenReturn(Lists.newArrayList(Mockito.mock(IndexSetConfig.class), Mockito.mock(IndexSetConfig.class)));
        expectedException.expect(IllegalStateException.class);
        migration.upgrade();
    }

    @Test
    public void upgradeWhenAlreadyCompleted() throws Exception {
        final IndexSetConfig indexSetConfig = Mockito.mock(IndexSetConfig.class);
        Mockito.when(indexSetService.findAll()).thenReturn(Collections.singletonList(indexSetConfig));
        Mockito.when(indexSetConfig.id()).thenReturn("abc123");
        Mockito.when(clusterConfigService.get(MigrationCompleted.class)).thenReturn(MigrationCompleted.create("1", Collections.emptySet(), Collections.emptySet()));
        migration.upgrade();
        Mockito.verify(streamService, Mockito.never()).save(ArgumentMatchers.any(Stream.class));
        Mockito.verify(clusterConfigService, Mockito.never()).write(ArgumentMatchers.any(MigrationCompleted.class));
    }
}

