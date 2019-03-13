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


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.lucene.LuceneResultStruct;
import org.apache.geode.cache.lucene.internal.distributed.EntryScore;
import org.apache.geode.test.junit.categories.LuceneTest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;


@Category({ LuceneTest.class })
public class PageableLuceneQueryResultsImplJUnitTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private List<EntryScore<String>> hits;

    private List<LuceneResultStruct> expected = new ArrayList<LuceneResultStruct>();

    private Region<String, String> userRegion;

    private Execution execution;

    @Test
    public void testMaxStore() {
        hits.set(5, new EntryScore<String>("key_5", 502));
        PageableLuceneQueryResultsImpl<String, String> results = new PageableLuceneQueryResultsImpl<String, String>(hits, null, 5);
        Assert.assertEquals(502, results.getMaxScore(), 0.1F);
    }

    @Test
    public void testPagination() {
        PageableLuceneQueryResultsImpl<String, String> results = new PageableLuceneQueryResultsImpl<String, String>(hits, userRegion, 10) {
            @Override
            protected Execution onRegion() {
                return execution;
            }
        };
        Assert.assertEquals(23, results.size());
        Assert.assertTrue(results.hasNext());
        List<LuceneResultStruct<String, String>> next = results.next();
        Assert.assertEquals(expected.subList(0, 10), next);
        Assert.assertTrue(results.hasNext());
        next = results.next();
        Assert.assertEquals(expected.subList(10, 20), next);
        Assert.assertTrue(results.hasNext());
        next = results.next();
        Assert.assertEquals(expected.subList(20, 23), next);
        Assert.assertFalse(results.hasNext());
        thrown.expect(NoSuchElementException.class);
        results.next();
    }

    @Test
    public void testNoPagination() {
        PageableLuceneQueryResultsImpl<String, String> results = new PageableLuceneQueryResultsImpl<String, String>(hits, userRegion, 0) {
            @Override
            protected Execution onRegion() {
                return execution;
            }
        };
        Assert.assertEquals(23, results.size());
        Assert.assertTrue(results.hasNext());
        List<LuceneResultStruct<String, String>> next = results.next();
        Assert.assertEquals(expected, next);
        Assert.assertFalse(results.hasNext());
        thrown.expect(NoSuchElementException.class);
        results.next();
    }

    @Test
    public void shouldThrowNoSuchElementExceptionFromNextWithNoMorePages() {
        PageableLuceneQueryResultsImpl<String, String> results = new PageableLuceneQueryResultsImpl(Collections.emptyList(), userRegion, 0);
        Assert.assertFalse(results.hasNext());
        thrown.expect(NoSuchElementException.class);
        results.next();
    }
}

