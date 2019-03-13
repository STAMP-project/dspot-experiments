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


import java.util.List;
import org.drools.verifier.core.index.keys.Value;
import org.drools.verifier.core.maps.KeyDefinition;
import org.drools.verifier.core.maps.MultiMap;
import org.drools.verifier.core.maps.MultiMapFactory;
import org.junit.Assert;
import org.junit.Test;


public class ExactMatcherSearchTest {
    private ExactMatcherSearch<Object> search;

    private MultiMap<Value, Object, List<Object>> map = MultiMapFactory.make();

    @Test
    public void testNullSearch() throws Exception {
        search = new ExactMatcherSearch(new org.drools.verifier.core.index.matchers.ExactMatcher(KeyDefinition.newKeyDefinition().withId("value").build(), null), map);
        MultiMap<Value, Object, List<Object>> search1 = search.search();
        Assert.assertEquals("I am null", search1.get(new Value(null)).get(0));
    }

    @Test
    public void testNegatedNullSearch() throws Exception {
        search = new ExactMatcherSearch(new org.drools.verifier.core.index.matchers.ExactMatcher(KeyDefinition.newKeyDefinition().withId("value").build(), null, true), map);
        MultiMap<Value, Object, List<Object>> search1 = search.search();
        Assert.assertEquals("hello", search1.get(new Value("helloKey")).get(0));
    }
}

