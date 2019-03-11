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


import StreamImpl.MatchingType.DEFAULT;
import org.graylog2.database.NotFoundException;
import org.graylog2.indexer.IndexSet;
import org.graylog2.indexer.IndexSetRegistry;
import org.graylog2.indexer.indexset.IndexSetConfig;
import org.graylog2.plugin.streams.Stream;
import org.graylog2.streams.StreamService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class V20161116172200_CreateDefaultStreamMigrationTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private Migration migration;

    @Mock
    private StreamService streamService;

    @Mock
    private IndexSetRegistry indexSetRegistry;

    @Mock
    private IndexSet indexSet;

    @Mock
    private IndexSetConfig indexSetConfig;

    @Test
    public void upgrade() throws Exception {
        final ArgumentCaptor<Stream> streamArgumentCaptor = ArgumentCaptor.forClass(Stream.class);
        Mockito.when(streamService.load("000000000000000000000001")).thenThrow(NotFoundException.class);
        Mockito.when(indexSetRegistry.getDefault()).thenReturn(indexSet);
        migration.upgrade();
        Mockito.verify(streamService).save(streamArgumentCaptor.capture());
        final Stream stream = streamArgumentCaptor.getValue();
        assertThat(stream.getTitle()).isEqualTo("All messages");
        assertThat(stream.getDisabled()).isFalse();
        assertThat(stream.getMatchingType()).isEqualTo(DEFAULT);
    }

    @Test
    public void upgradeWithoutDefaultIndexSet() throws Exception {
        Mockito.when(streamService.load("000000000000000000000001")).thenThrow(NotFoundException.class);
        Mockito.when(indexSetRegistry.getDefault()).thenThrow(IllegalStateException.class);
        expectedException.expect(IllegalStateException.class);
        migration.upgrade();
    }

    @Test
    public void upgradeDoesNotRunIfDefaultStreamExists() throws Exception {
        Mockito.when(streamService.load("000000000000000000000001")).thenReturn(Mockito.mock(Stream.class));
        migration.upgrade();
        Mockito.verify(streamService, Mockito.never()).save(ArgumentMatchers.any(Stream.class));
    }
}

