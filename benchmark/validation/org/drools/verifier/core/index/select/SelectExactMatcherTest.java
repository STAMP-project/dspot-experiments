/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.verifier.core.index.select;


import java.util.Collection;
import java.util.List;
import org.drools.verifier.core.index.keys.Value;
import org.drools.verifier.core.maps.MultiMap;
import org.junit.Assert;
import org.junit.Test;


public class SelectExactMatcherTest {
    private Select<SelectExactMatcherTest.Item> select;

    private MultiMap<Value, SelectExactMatcherTest.Item, List<SelectExactMatcherTest.Item>> map;

    private SelectExactMatcherTest.Item thirteen;

    @Test
    public void testAll() throws Exception {
        final Collection<SelectExactMatcherTest.Item> all = select.all();
        Assert.assertEquals(1, all.size());
    }

    @Test
    public void testFirst() throws Exception {
        Assert.assertEquals(thirteen, select.first());
    }

    @Test
    public void testLast() throws Exception {
        Assert.assertEquals(thirteen, select.last());
    }

    private class Item {
        private Integer cost;

        public Item(final Integer cost) {
            this.cost = cost;
        }
    }
}

