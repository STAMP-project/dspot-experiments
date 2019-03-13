/**
 * Copyright 2015 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.datastore;


import org.junit.Assert;
import org.junit.Test;


public class IncompleteKeyTest {
    private static IncompleteKey pk1;

    private static IncompleteKey pk2;

    private static IncompleteKey deprecatedPk1;

    private static IncompleteKey deprecatedPk2;

    private static Key parent1;

    @Test
    public void testBuilders() throws Exception {
        Assert.assertEquals("ds", IncompleteKeyTest.pk1.getProjectId());
        Assert.assertEquals("kind1", IncompleteKeyTest.pk1.getKind());
        Assert.assertTrue(IncompleteKeyTest.pk1.getAncestors().isEmpty());
        Assert.assertEquals("ds", IncompleteKeyTest.pk2.getProjectId());
        Assert.assertEquals("kind3", IncompleteKeyTest.pk2.getKind());
        Assert.assertEquals(IncompleteKeyTest.parent1.getPath(), IncompleteKeyTest.pk2.getAncestors());
        Assert.assertEquals(IncompleteKeyTest.pk2, IncompleteKey.newBuilder(IncompleteKeyTest.pk2).build());
        IncompleteKey pk3 = IncompleteKey.newBuilder(IncompleteKeyTest.pk2).setKind("kind4").build();
        Assert.assertEquals("ds", pk3.getProjectId());
        Assert.assertEquals("kind4", pk3.getKind());
        Assert.assertEquals(IncompleteKeyTest.parent1.getPath(), pk3.getAncestors());
    }

    @Test
    public void testParent() {
        Assert.assertNull(IncompleteKeyTest.pk1.getParent());
        Assert.assertEquals(IncompleteKeyTest.parent1, IncompleteKeyTest.pk2.getParent());
        Key parent2 = Key.newBuilder("ds", "kind3", "name").setNamespace("ns").build();
        IncompleteKey pk3 = IncompleteKey.newBuilder(parent2, "kind3").build();
        Assert.assertEquals(parent2, pk3.getParent());
    }
}

