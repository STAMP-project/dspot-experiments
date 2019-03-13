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


import ComponentIndexDefinition.INDEX_TYPE_COMPONENT;
import java.util.Arrays;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.db.DbTester;
import org.sonar.db.es.EsQueueDto;
import org.sonar.server.issue.index.IssueIndexDefinition;


public class OneToManyResilientIndexingListenerTest {
    @Rule
    public EsTester es = EsTester.create();

    @Rule
    public DbTester db = DbTester.create();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void ES_QUEUE_rows_are_deleted_when_all_docs_are_successfully_indexed() {
        EsQueueDto item1 = insertInQueue(IssueIndexDefinition.INDEX_TYPE_ISSUE, "P1");
        EsQueueDto item2 = insertInQueue(IssueIndexDefinition.INDEX_TYPE_ISSUE, "P2");
        EsQueueDto outOfScopeItem = insertInQueue(INDEX_TYPE_COMPONENT, "P1");
        db.commit();
        // does not contain outOfScopeItem
        IndexingListener underTest = newListener(Arrays.asList(item1, item2));
        DocId issue1 = OneToManyResilientIndexingListenerTest.newDocId(IssueIndexDefinition.INDEX_TYPE_ISSUE, "I1");
        DocId issue2 = OneToManyResilientIndexingListenerTest.newDocId(IssueIndexDefinition.INDEX_TYPE_ISSUE, "I2");
        underTest.onSuccess(Arrays.asList(issue1, issue2));
        assertThatEsTableContainsOnly(item1, item2, outOfScopeItem);
        // onFinish deletes all items
        IndexingResult result = new IndexingResult();
        result.incrementSuccess().incrementRequests();
        result.incrementSuccess().incrementRequests();
        underTest.onFinish(result);
        assertThatEsTableContainsOnly(outOfScopeItem);
    }

    @Test
    public void ES_QUEUE_rows_are_not_deleted_on_partial_error() {
        EsQueueDto item1 = insertInQueue(IssueIndexDefinition.INDEX_TYPE_ISSUE, "P1");
        EsQueueDto item2 = insertInQueue(IssueIndexDefinition.INDEX_TYPE_ISSUE, "P2");
        EsQueueDto outOfScopeItem = insertInQueue(INDEX_TYPE_COMPONENT, "P1");
        db.commit();
        // does not contain outOfScopeItem
        IndexingListener underTest = newListener(Arrays.asList(item1, item2));
        DocId issue1 = OneToManyResilientIndexingListenerTest.newDocId(IssueIndexDefinition.INDEX_TYPE_ISSUE, "I1");
        DocId issue2 = OneToManyResilientIndexingListenerTest.newDocId(IssueIndexDefinition.INDEX_TYPE_ISSUE, "I2");
        underTest.onSuccess(Arrays.asList(issue1, issue2));
        assertThatEsTableContainsOnly(item1, item2, outOfScopeItem);
        // one failure among the 2 indexing requests of issues
        IndexingResult result = new IndexingResult();
        result.incrementSuccess().incrementRequests();
        result.incrementRequests();
        underTest.onFinish(result);
        assertThatEsTableContainsOnly(item1, item2, outOfScopeItem);
    }
}

