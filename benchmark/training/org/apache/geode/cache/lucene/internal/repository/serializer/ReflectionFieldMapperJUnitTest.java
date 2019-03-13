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


import org.apache.geode.test.junit.categories.LuceneTest;
import org.apache.lucene.document.Document;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Unit test of the ReflectionFieldMapperClass. Tests that all field types are mapped correctly.
 */
@Category({ LuceneTest.class })
public class ReflectionFieldMapperJUnitTest {
    @Test
    public void testAllFields() {
        String[] allFields = new String[]{ "s", "i", "l", "d", "f", "s2" };
        ReflectionLuceneSerializer mapper1 = new ReflectionLuceneSerializer(Type1.class, allFields);
        ReflectionLuceneSerializer mapper2 = new ReflectionLuceneSerializer(Type2.class, allFields);
        Type1 type1 = new Type1("a", 1, 2L, 3.0, 4.0F);
        Type2 type2 = new Type2("a", 1, 2L, 3.0, 4.0F, "b");
        Document doc1 = SerializerTestHelper.invokeSerializer(mapper1, type1, allFields);
        Assert.assertEquals(5, doc1.getFields().size());
        Assert.assertEquals("a", doc1.getField("s").stringValue());
        Assert.assertEquals(1, doc1.getField("i").numericValue());
        Assert.assertEquals(2L, doc1.getField("l").numericValue());
        Assert.assertEquals(3.0, doc1.getField("d").numericValue());
        Assert.assertEquals(4.0F, doc1.getField("f").numericValue());
        Document doc2 = SerializerTestHelper.invokeSerializer(mapper2, type2, allFields);
        Assert.assertEquals(6, doc2.getFields().size());
        Assert.assertEquals("a", doc2.getField("s").stringValue());
        Assert.assertEquals("b", doc2.getField("s2").stringValue());
        Assert.assertEquals(1, doc2.getField("i").numericValue());
        Assert.assertEquals(2L, doc2.getField("l").numericValue());
        Assert.assertEquals(3.0, doc2.getField("d").numericValue());
        Assert.assertEquals(4.0F, doc2.getField("f").numericValue());
    }

    @Test
    public void testIgnoreInvalid() {
        String[] fields = new String[]{ "s", "o", "s2" };
        ReflectionLuceneSerializer mapper = new ReflectionLuceneSerializer(Type2.class, fields);
        Type2 type2 = new Type2("a", 1, 2L, 3.0, 4.0F, "b");
        Document doc = SerializerTestHelper.invokeSerializer(mapper, type2, fields);
        Assert.assertEquals(2, doc.getFields().size());
        Assert.assertEquals("a", doc.getField("s").stringValue());
        Assert.assertEquals("b", doc.getField("s2").stringValue());
    }

    @Test
    public void testNullField() {
        String[] fields = new String[]{ "s", "o", "s2" };
        ReflectionLuceneSerializer mapper = new ReflectionLuceneSerializer(Type2.class, fields);
        Type2 type2 = new Type2("a", 1, 2L, 3.0, 4.0F, null);
        Document doc = SerializerTestHelper.invokeSerializer(mapper, type2, fields);
        Assert.assertEquals(1, doc.getFields().size());
        Assert.assertEquals("a", doc.getField("s").stringValue());
        Assert.assertNull(doc.getField("s2"));
    }
}

