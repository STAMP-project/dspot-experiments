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
package org.apache.geode.cache.lucene.internal.repository.serializer;


import org.apache.geode.pdx.PdxInstance;
import org.apache.geode.test.junit.categories.LuceneTest;
import org.apache.lucene.document.Document;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;


/**
 * Unit test of the PdxFieldMapperJUnitTest. Tests that all field types are mapped correctly.
 */
@Category({ LuceneTest.class })
public class PdxFieldMapperJUnitTest {
    @Test
    public void testWriteFields() {
        String[] fields = new String[]{ "s", "i" };
        PdxLuceneSerializer mapper = new PdxLuceneSerializer();
        PdxInstance pdxInstance = Mockito.mock(PdxInstance.class);
        Mockito.when(pdxInstance.hasField("s")).thenReturn(true);
        Mockito.when(pdxInstance.hasField("i")).thenReturn(true);
        Mockito.when(pdxInstance.getField("s")).thenReturn("a");
        Mockito.when(pdxInstance.getField("i")).thenReturn(5);
        Document doc = SerializerTestHelper.invokeSerializer(mapper, pdxInstance, fields);
        Assert.assertEquals(2, doc.getFields().size());
        Assert.assertEquals("a", doc.getField("s").stringValue());
        Assert.assertEquals(5, doc.getField("i").numericValue());
    }

    @Test
    public void testIgnoreMissing() {
        String[] fields = new String[]{ "s", "i", "s2", "o" };
        PdxLuceneSerializer mapper = new PdxLuceneSerializer();
        PdxInstance pdxInstance = Mockito.mock(PdxInstance.class);
        Mockito.when(pdxInstance.hasField("s")).thenReturn(true);
        Mockito.when(pdxInstance.hasField("i")).thenReturn(true);
        Mockito.when(pdxInstance.hasField("o")).thenReturn(true);
        Mockito.when(pdxInstance.hasField("o2")).thenReturn(true);
        Mockito.when(pdxInstance.getField("s")).thenReturn("a");
        Mockito.when(pdxInstance.getField("i")).thenReturn(5);
        Mockito.when(pdxInstance.getField("o")).thenReturn(new Object());
        Mockito.when(pdxInstance.getField("o2")).thenReturn(new Object());
        Document doc = SerializerTestHelper.invokeSerializer(mapper, pdxInstance, fields);
        Assert.assertEquals(2, doc.getFields().size());
        Assert.assertEquals("a", doc.getField("s").stringValue());
        Assert.assertEquals(5, doc.getField("i").numericValue());
    }

    @Test
    public void testNullField() {
        String[] fields = new String[]{ "s", "i" };
        PdxLuceneSerializer mapper = new PdxLuceneSerializer();
        PdxInstance pdxInstance = Mockito.mock(PdxInstance.class);
        Mockito.when(pdxInstance.hasField("s")).thenReturn(true);
        Mockito.when(pdxInstance.hasField("i")).thenReturn(true);
        Mockito.when(pdxInstance.getField("s")).thenReturn("a");
        Mockito.when(pdxInstance.getField("i")).thenReturn(null);
        Document doc = SerializerTestHelper.invokeSerializer(mapper, pdxInstance, fields);
        Assert.assertEquals(1, doc.getFields().size());
        Assert.assertEquals("a", doc.getField("s").stringValue());
        Assert.assertNull(doc.getField("i"));
    }
}

