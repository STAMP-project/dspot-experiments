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


import LuceneService.REGION_VALUE_FIELD;
import org.apache.geode.cache.lucene.LuceneService;
import org.apache.geode.pdx.PdxInstance;
import org.apache.geode.test.junit.categories.LuceneTest;
import org.apache.lucene.document.Document;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;


/**
 * Unit test of the ObjectToDocumentMapper.
 */
@Category({ LuceneTest.class })
public class HeterogeneousLuceneSerializerJUnitTest {
    /**
     * Test that the mapper can handle a mix of different object types.
     */
    @Test
    public void testHeterogeneousObjects() {
        String[] fields = new String[]{ "s", "i", "l", "d", "f", "s2", "missing" };
        HeterogeneousLuceneSerializer mapper = new HeterogeneousLuceneSerializer();
        Type1 t1 = new Type1("a", 1, 2L, 3.0, 4.0F);
        Document doc1 = SerializerTestHelper.invokeSerializer(mapper, t1, fields);
        Assert.assertEquals(5, doc1.getFields().size());
        Assert.assertEquals("a", doc1.getField("s").stringValue());
        Assert.assertEquals(1, doc1.getField("i").numericValue());
        Assert.assertEquals(2L, doc1.getField("l").numericValue());
        Assert.assertEquals(3.0, doc1.getField("d").numericValue());
        Assert.assertEquals(4.0F, doc1.getField("f").numericValue());
        Type2 t2 = new Type2("a", 1, 2L, 3.0, 4.0F, "b");
        Document doc2 = SerializerTestHelper.invokeSerializer(mapper, t2, fields);
        Assert.assertEquals(6, doc2.getFields().size());
        Assert.assertEquals("a", doc2.getField("s").stringValue());
        Assert.assertEquals("b", doc2.getField("s2").stringValue());
        Assert.assertEquals(1, doc2.getField("i").numericValue());
        Assert.assertEquals(2L, doc2.getField("l").numericValue());
        Assert.assertEquals(3.0, doc2.getField("d").numericValue());
        Assert.assertEquals(4.0F, doc2.getField("f").numericValue());
        PdxInstance pdxInstance = Mockito.mock(PdxInstance.class);
        Mockito.when(pdxInstance.hasField("s")).thenReturn(true);
        Mockito.when(pdxInstance.hasField("i")).thenReturn(true);
        Mockito.when(pdxInstance.getField("s")).thenReturn("a");
        Mockito.when(pdxInstance.getField("i")).thenReturn(5);
        Document doc3 = SerializerTestHelper.invokeSerializer(mapper, pdxInstance, fields);
        Assert.assertEquals(2, doc3.getFields().size());
        Assert.assertEquals("a", doc3.getField("s").stringValue());
        Assert.assertEquals(5, doc3.getField("i").numericValue());
    }

    @Test
    public void shouldIndexPrimitiveStringIfRequested() {
        HeterogeneousLuceneSerializer mapper = new HeterogeneousLuceneSerializer();
        Document doc = SerializerTestHelper.invokeSerializer(mapper, "sample value", new String[]{ LuceneService.REGION_VALUE_FIELD });
        Assert.assertEquals(1, doc.getFields().size());
        Assert.assertEquals("sample value", doc.getField(REGION_VALUE_FIELD).stringValue());
    }

    @Test
    public void shouldIndexPrimitiveNumberIfRequested() {
        HeterogeneousLuceneSerializer mapper = new HeterogeneousLuceneSerializer();
        Document doc = SerializerTestHelper.invokeSerializer(mapper, 53, new String[]{ LuceneService.REGION_VALUE_FIELD });
        Assert.assertEquals(1, doc.getFields().size());
        Assert.assertEquals(53, doc.getField(REGION_VALUE_FIELD).numericValue());
    }
}

