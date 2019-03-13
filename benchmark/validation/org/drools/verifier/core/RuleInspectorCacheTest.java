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
package org.drools.verifier.core;


import java.util.Collection;
import org.drools.verifier.core.cache.RuleInspectorCache;
import org.drools.verifier.core.cache.inspectors.RuleInspector;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class RuleInspectorCacheTest {
    private RuleInspectorCache cache;

    @Test
    public void testInit() throws Exception {
        Assert.assertEquals(7, cache.all().size());
    }

    @Test
    public void testRemoveRow() throws Exception {
        cache.removeRow(3);
        final Collection<RuleInspector> all = cache.all();
        Assert.assertEquals(6, all.size());
        assertContainsRowNumbers(all, 0, 1, 2, 3, 4, 5);
    }
}

