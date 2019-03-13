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
import org.drools.verifier.core.index.matchers.Matcher;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class SelectExactMatcherNegateTest {
    private int amount;

    private Select<SelectExactMatcherNegateTest.Item> select;

    private Object firstValue;

    private Object lastValue;

    public SelectExactMatcherNegateTest(final int amount, final Object firstValue, final Object lastValue, final Matcher matcher) throws Exception {
        this.firstValue = firstValue;
        this.lastValue = lastValue;
        this.amount = amount;
        this.select = new Select(makeMap(), matcher);
    }

    @Test
    public void testAll() throws Exception {
        final Collection<SelectExactMatcherNegateTest.Item> all = select.all();
        Assert.assertEquals(amount, all.size());
    }

    @Test
    public void testFirst() throws Exception {
        Assert.assertEquals(firstValue, select.first().cost);
    }

    @Test
    public void testLast() throws Exception {
        Assert.assertEquals(lastValue, select.last().cost);
    }

    private class Item {
        private Integer cost;

        public Item(final Integer cost) {
            this.cost = cost;
        }
    }
}

