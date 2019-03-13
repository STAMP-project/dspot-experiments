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


import org.apache.geode.cache.Cache;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.lucene.LuceneQuery;
import org.apache.geode.test.junit.categories.LuceneTest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@Category({ LuceneTest.class })
public class LuceneQueryFactoryImplJUnitTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldCreateQueryWithCorrectAttributes() {
        Cache cache = Mockito.mock(Cache.class);
        Region region = Mockito.mock(Region.class);
        Mockito.when(cache.getRegion(ArgumentMatchers.any())).thenReturn(region);
        LuceneQueryFactoryImpl f = new LuceneQueryFactoryImpl(cache);
        f.setPageSize(5);
        f.setLimit(25);
        LuceneQuery<Object, Object> query = f.create("index", "region", new StringQueryProvider("test", DEFAULT_FIELD));
        Assert.assertEquals(25, query.getLimit());
        Assert.assertEquals(5, query.getPageSize());
        Mockito.verify(cache).getRegion(Mockito.eq("region"));
    }

    @Test
    public void shouldThrowIllegalArgumentWhenRegionIsMissing() {
        Cache cache = Mockito.mock(Cache.class);
        LuceneQueryFactoryImpl f = new LuceneQueryFactoryImpl(cache);
        thrown.expect(IllegalArgumentException.class);
        LuceneQuery<Object, Object> query = f.create("index", "region", new StringQueryProvider("test", DEFAULT_FIELD));
    }
}

