/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.cache.lucene.internal;


import java.util.function.IntSupplier;
import org.apache.geode.Statistics;
import org.apache.geode.StatisticsType;
import org.apache.geode.test.junit.categories.LuceneTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@Category({ LuceneTest.class })
public class LuceneIndexStatsJUnitTest {
    private Statistics statistics;

    private LuceneIndexStats stats;

    private StatisticsType type;

    @Test
    public void shouldIncrementQueryStats() {
        stats.startQuery();
        verifyIncInt("queryExecutionsInProgress", 1);
        stats.endQuery(5, 2);
        stats.incNumberOfQueryExecuted();
        verifyIncInt("queryExecutionsInProgress", (-1));
        verifyIncInt("queryExecutions", 1);
        verifyIncLong("queryExecutionTotalHits", 2);
        // Because the initial stat time is 0 and the final time is 5, the delta is -5
        verifyIncLong("queryExecutionTime", (-5));
    }

    @Test
    public void shouldIncrementRepositoryQueryStats() {
        stats.startRepositoryQuery();
        verifyIncInt("repositoryQueryExecutionsInProgress", 1);
        stats.endRepositoryQuery(5, 2);
        verifyIncInt("repositoryQueryExecutionsInProgress", (-1));
        verifyIncInt("repositoryQueryExecutions", 1);
        verifyIncLong("repositoryQueryExecutionTotalHits", 2);
        // Because the initial stat time is 0 and the final time is 5, the delta is -5
        verifyIncLong("repositoryQueryExecutionTime", (-5));
    }

    @Test
    public void shouldIncrementUpdateStats() {
        stats.startUpdate();
        verifyIncInt("updatesInProgress", 1);
        stats.endUpdate(5);
        verifyIncInt("updatesInProgress", (-1));
        verifyIncInt("updates", 1);
        // Because the initial stat time is 0 and the final time is 5, the delta is -5
        verifyIncLong("updateTime", (-5));
    }

    @Test
    public void shouldIncrementFailedEntriesStats() {
        stats.startUpdate();
        verifyIncInt("updatesInProgress", 1);
        stats.incFailedEntries();
        stats.endUpdate(5);
        verifyIncInt("updatesInProgress", (-1));
        verifyIncInt("updates", 1);
        verifyIncInt("failedEntries", 1);
        // Because the initial stat time is 0 and the final time is 5, the delta is -5
        verifyIncLong("updateTime", (-5));
    }

    @Test
    public void shouldIncrementCommitStats() {
        stats.startCommit();
        verifyIncInt("commitsInProgress", 1);
        stats.endCommit(5);
        verifyIncInt("commitsInProgress", (-1));
        verifyIncInt("commits", 1);
        // Because the initial stat time is 0 and the final time is 5, the delta is -5
        verifyIncLong("commitTime", (-5));
    }

    @Test
    public void shouldPollSuppliersForDocumentStat() {
        stats.addDocumentsSupplier(() -> 5);
        stats.addDocumentsSupplier(() -> 3);
        int documentsId = type.nameToId("documents");
        ArgumentCaptor<IntSupplier> documentsSupplierCaptor = ArgumentCaptor.forClass(IntSupplier.class);
        Mockito.verify(statistics).setIntSupplier(ArgumentMatchers.eq(documentsId), documentsSupplierCaptor.capture());
        IntSupplier documentsSuppler = documentsSupplierCaptor.getValue();
        Assert.assertEquals(8, documentsSuppler.getAsInt());
    }
}

