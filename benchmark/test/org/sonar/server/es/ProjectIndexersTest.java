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


import ProjectIndexer.Cause.PROJECT_CREATION;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.db.DbSession;
import org.sonar.db.es.EsQueueDto;


public class ProjectIndexersTest {
    @Test
    public void commitAndIndexByProjectUuids_calls_indexer_with_only_its_supported_items() {
        EsQueueDto item1a = EsQueueDto.create("fake/fake1", "P1");
        EsQueueDto item1b = EsQueueDto.create("fake/fake1", "P1");
        EsQueueDto item2 = EsQueueDto.create("fake/fake2", "P1");
        ProjectIndexersTest.FakeIndexer indexer1 = new ProjectIndexersTest.FakeIndexer(Arrays.asList(item1a, item1b));
        ProjectIndexersTest.FakeIndexer indexer2 = new ProjectIndexersTest.FakeIndexer(Collections.singletonList(item2));
        DbSession dbSession = Mockito.mock(DbSession.class);
        ProjectIndexersImpl underTest = new ProjectIndexersImpl(indexer1, indexer2);
        underTest.commitAndIndexByProjectUuids(dbSession, Collections.singletonList("P1"), PROJECT_CREATION);
        assertThat(indexer1.calledItems).containsExactlyInAnyOrder(item1a, item1b);
        assertThat(indexer2.calledItems).containsExactlyInAnyOrder(item2);
    }

    @Test
    public void commitAndIndex_restricts_indexing_to_projects() {
        // TODO
    }

    private static class FakeIndexer implements ProjectIndexer {
        private final List<EsQueueDto> items;

        private Collection<EsQueueDto> calledItems;

        private FakeIndexer(List<EsQueueDto> items) {
            this.items = items;
        }

        @Override
        public void indexOnStartup(Set<IndexType> uninitializedIndexTypes) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<IndexType> getIndexTypes() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Collection<EsQueueDto> prepareForRecovery(DbSession dbSession, Collection<String> projectUuids, Cause cause) {
            return items;
        }

        @Override
        public IndexingResult index(DbSession dbSession, Collection<EsQueueDto> items) {
            this.calledItems = items;
            return new IndexingResult();
        }

        @Override
        public void indexOnAnalysis(String branchUuid) {
            throw new UnsupportedOperationException();
        }
    }
}

