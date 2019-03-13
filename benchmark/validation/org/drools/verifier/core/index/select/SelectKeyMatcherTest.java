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
import org.junit.Assert;
import org.junit.Test;


public class SelectKeyMatcherTest {
    private Select<String> select;

    @Test
    public void testAll() throws Exception {
        final Collection<String> all = select.all();
        Assert.assertEquals(2, all.size());
    }

    @Test
    public void testFirst() throws Exception {
        Assert.assertEquals("value1", select.first());
    }

    @Test
    public void testLast() throws Exception {
        Assert.assertEquals("value2", select.last());
    }
}

