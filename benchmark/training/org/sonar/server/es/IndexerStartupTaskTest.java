/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.es;


import com.google.common.collect.ImmutableSet;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.server.es.metadata.MetadataIndex;


public class IndexerStartupTaskTest {
    @Rule
    public EsTester es = EsTester.createCustom(new FakeIndexDefinition());

    private final MapSettings settings = new MapSettings();

    private final MetadataIndex metadataIndex = Mockito.mock(MetadataIndex.class);

    private final StartupIndexer indexer = Mockito.mock(StartupIndexer.class);

    private final IndexerStartupTask underTest = new IndexerStartupTask(es.client(), settings.asConfig(), metadataIndex, indexer);

    @Test
    public void index_if_not_initialized() {
        Mockito.doReturn(false).when(metadataIndex).getInitialized(FakeIndexDefinition.INDEX_TYPE_FAKE);
        underTest.execute();
        Mockito.verify(indexer).getIndexTypes();
        Mockito.verify(indexer).indexOnStartup(Mockito.eq(ImmutableSet.of(FakeIndexDefinition.INDEX_TYPE_FAKE)));
    }

    @Test
    public void set_initialized_after_indexation() {
        Mockito.doReturn(false).when(metadataIndex).getInitialized(FakeIndexDefinition.INDEX_TYPE_FAKE);
        underTest.execute();
        Mockito.verify(metadataIndex).setInitialized(ArgumentMatchers.eq(FakeIndexDefinition.INDEX_TYPE_FAKE), ArgumentMatchers.eq(true));
    }

    @Test
    public void do_not_index_if_already_initialized() {
        Mockito.doReturn(true).when(metadataIndex).getInitialized(FakeIndexDefinition.INDEX_TYPE_FAKE);
        underTest.execute();
        Mockito.verify(indexer).getIndexTypes();
        Mockito.verifyNoMoreInteractions(indexer);
    }

    @Test
    public void do_not_index_if_indexes_are_disabled() {
        settings.setProperty("sonar.internal.es.disableIndexes", "true");
        es.putDocuments(FakeIndexDefinition.INDEX_TYPE_FAKE, new FakeDoc());
        underTest.execute();
        // do not index
        Mockito.verifyNoMoreInteractions(indexer);
    }
}

