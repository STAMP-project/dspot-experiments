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
import org.drools.verifier.core.index.keys.Key;
import org.drools.verifier.core.index.keys.UUIDKey;
import org.drools.verifier.core.maps.KeyDefinition;
import org.drools.verifier.core.maps.util.HasKeys;
import org.junit.Assert;
import org.junit.Test;


public class SelectWithNegativeExactMatcherWhenTheValueIsNotInTheMapTest {
    private Select<SelectWithNegativeExactMatcherWhenTheValueIsNotInTheMapTest.Item> select;

    @Test
    public void testAll() throws Exception {
        final Collection<SelectWithNegativeExactMatcherWhenTheValueIsNotInTheMapTest.Item> all = select.all();
        Assert.assertEquals(4, all.size());
    }

    @Test
    public void testFirst() throws Exception {
        Assert.assertEquals(0, select.first().cost);
    }

    @Test
    public void testLast() throws Exception {
        Assert.assertEquals(1200, select.last().cost);
    }

    private class Item implements HasKeys {
        private int cost;

        private UUIDKey uuidKey = getUUID(this);

        public Item(final int cost) {
            this.cost = cost;
        }

        @Override
        public Key[] keys() {
            return new Key[]{ uuidKey, new Key(KeyDefinition.newKeyDefinition().withId("cost").build(), cost) };
        }

        @Override
        public UUIDKey getUuidKey() {
            return uuidKey;
        }
    }
}

