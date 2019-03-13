/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.config;


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.w3c.dom.Document;
import org.w3c.dom.Node;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class IterableNodeListTest {
    private Document document;

    @Test
    public void testIterableNodeList() {
        int count = 0;
        for (Node node : DomConfigHelper.asElementIterable(document.getFirstChild().getChildNodes())) {
            count += (node != null) ? 1 : 0;
        }
        Assert.assertEquals(3, count);
    }

    @Test
    public void testHasNext() {
        Assert.assertTrue(DomConfigHelper.asElementIterable(document.getChildNodes()).iterator().hasNext());
    }

    @Test
    public void testNext() {
        Assert.assertNotNull(DomConfigHelper.asElementIterable(document.getChildNodes()).iterator().next());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemove() {
        DomConfigHelper.asElementIterable(document.getChildNodes()).iterator().remove();
    }

    @Test
    public void testCleanNodeName() {
        Assert.assertEquals("root", DomConfigHelper.cleanNodeName(document.getDocumentElement()));
    }
}

