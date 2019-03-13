/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.streams.processor.internals;


import org.junit.Assert;
import org.junit.Test;


public class QuickUnionTest {
    @Test
    public void testUnite() {
        final QuickUnion<Long> qu = new QuickUnion();
        final long[] ids = new long[]{ 1L, 2L, 3L, 4L, 5L };
        for (final long id : ids) {
            qu.add(id);
        }
        Assert.assertEquals(5, roots(qu, ids).size());
        qu.unite(1L, 2L);
        Assert.assertEquals(4, roots(qu, ids).size());
        Assert.assertEquals(qu.root(1L), qu.root(2L));
        qu.unite(3L, 4L);
        Assert.assertEquals(3, roots(qu, ids).size());
        Assert.assertEquals(qu.root(1L), qu.root(2L));
        Assert.assertEquals(qu.root(3L), qu.root(4L));
        qu.unite(1L, 5L);
        Assert.assertEquals(2, roots(qu, ids).size());
        Assert.assertEquals(qu.root(1L), qu.root(2L));
        Assert.assertEquals(qu.root(2L), qu.root(5L));
        Assert.assertEquals(qu.root(3L), qu.root(4L));
        qu.unite(3L, 5L);
        Assert.assertEquals(1, roots(qu, ids).size());
        Assert.assertEquals(qu.root(1L), qu.root(2L));
        Assert.assertEquals(qu.root(2L), qu.root(3L));
        Assert.assertEquals(qu.root(3L), qu.root(4L));
        Assert.assertEquals(qu.root(4L), qu.root(5L));
    }

    @Test
    public void testUniteMany() {
        final QuickUnion<Long> qu = new QuickUnion();
        final long[] ids = new long[]{ 1L, 2L, 3L, 4L, 5L };
        for (final long id : ids) {
            qu.add(id);
        }
        Assert.assertEquals(5, roots(qu, ids).size());
        qu.unite(1L, 2L, 3L, 4L);
        Assert.assertEquals(2, roots(qu, ids).size());
        Assert.assertEquals(qu.root(1L), qu.root(2L));
        Assert.assertEquals(qu.root(2L), qu.root(3L));
        Assert.assertEquals(qu.root(3L), qu.root(4L));
        Assert.assertNotEquals(qu.root(1L), qu.root(5L));
    }
}

