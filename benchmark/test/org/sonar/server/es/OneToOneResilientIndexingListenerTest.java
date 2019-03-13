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


import java.util.Arrays;
import java.util.Collections;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.db.DbTester;
import org.sonar.db.es.EsQueueDto;
import org.sonar.server.issue.index.IssueIndexDefinition;


public class OneToOneResilientIndexingListenerTest {
    @Rule
    public EsTester es = EsTester.create();

    @Rule
    public DbTester db = DbTester.create();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void onSuccess_deletes_rows_from_ES_QUEUE_table() {
        EsQueueDto item1 = insertInQueue(IssueIndexDefinition.INDEX_TYPE_ISSUE, "foo");
        EsQueueDto item2 = insertInQueue(IssueIndexDefinition.INDEX_TYPE_ISSUE, "bar");
        EsQueueDto item3 = insertInQueue(IssueIndexDefinition.INDEX_TYPE_ISSUE, "baz");
        db.commit();
        IndexingListener underTest = newListener(Arrays.asList(item1, item2, item3));
        underTest.onSuccess(Collections.emptyList());
        assertThatEsTableContainsOnly(item1, item2, item3);
        underTest.onSuccess(Arrays.asList(OneToOneResilientIndexingListenerTest.toDocId(item1), OneToOneResilientIndexingListenerTest.toDocId(item3)));
        assertThatEsTableContainsOnly(item2);
        // onFinish does nothing
        underTest.onFinish(new IndexingResult());
        assertThatEsTableContainsOnly(item2);
    }

    /**
     * ES_QUEUE can contain multiple times the same document, for instance
     * when an issue has been updated multiple times in a row without
     * being successfully indexed.
     * Elasticsearch response does not make difference between the different
     * occurrences (and nevertheless it would be useless). So all the
     * occurrences are marked as successfully indexed if a single request
     * passes.
     */
    @Test
    public void onSuccess_deletes_all_the_rows_with_same_doc_id() {
        EsQueueDto item1 = insertInQueue(IssueIndexDefinition.INDEX_TYPE_ISSUE, "foo");
        // same id as item1
        EsQueueDto item2 = insertInQueue(IssueIndexDefinition.INDEX_TYPE_ISSUE, item1.getDocId());
        EsQueueDto item3 = insertInQueue(IssueIndexDefinition.INDEX_TYPE_ISSUE, "bar");
        db.commit();
        IndexingListener underTest = newListener(Arrays.asList(item1, item2, item3));
        underTest.onSuccess(Arrays.asList(OneToOneResilientIndexingListenerTest.toDocId(item1)));
        assertThatEsTableContainsOnly(item3);
    }
}

