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
package org.apache.geode.cache.lucene;


import java.util.Collection;
import org.apache.geode.cache.lucene.internal.repository.serializer.SerializerTestHelper;
import org.apache.geode.pdx.PdxInstance;
import org.apache.geode.test.junit.categories.LuceneTest;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ LuceneTest.class })
public class FlatFormatPdxSerializerJunitTest {
    @Rule
    public LocalCacheRule localCacheRule = new LocalCacheRule();

    @Test
    public void shouldParseTopLevelPdxIntArray() {
        String[] fields = new String[]{ "description", "status", "names", "intArr", "position1.country", "position1.sharesOutstanding", "position1.secId", "positions.country", "positions.sharesOutstanding", "positions.secId" };
        FlatFormatSerializer serializer = new FlatFormatSerializer();
        PdxInstance pdx = createPdxInstance();
        Document doc1 = SerializerTestHelper.invokeSerializer(serializer, pdx, fields);
        Assert.assertEquals(17, doc1.getFields().size());
        IndexableField[] fieldsInDoc = doc1.getFields("intArr");
        Collection<Object> results = getResultCollection(fieldsInDoc, true);
        Assert.assertEquals(2, results.size());
        Assert.assertTrue(results.contains(2001));
        Assert.assertTrue(results.contains(2017));
    }

    @Test
    public void shouldParseTopLevelPdxStringField() {
        String[] fields = new String[]{ "status" };
        FlatFormatSerializer serializer = new FlatFormatSerializer();
        PdxInstance pdx = createPdxInstance();
        Document doc1 = SerializerTestHelper.invokeSerializer(serializer, pdx, fields);
        IndexableField[] fieldsInDoc = doc1.getFields("status");
        Collection<Object> results = getResultCollection(fieldsInDoc, false);
        Assert.assertEquals(1, results.size());
        Assert.assertTrue(results.contains("active"));
    }

    @Test
    public void shouldParseSecondTopLevelPdxStringField() {
        String[] fields = new String[]{ "positions.secId" };
        FlatFormatSerializer serializer = new FlatFormatSerializer();
        PdxInstance pdx = createPdxInstance();
        Document doc1 = SerializerTestHelper.invokeSerializer(serializer, pdx, fields);
        IndexableField[] fieldsInDoc = doc1.getFields("positions.secId");
        Collection<Object> results = getResultCollection(fieldsInDoc, false);
        Assert.assertEquals(2, results.size());
        Assert.assertTrue(results.contains("IBM"));
        Assert.assertTrue(results.contains("AAPL"));
    }

    @Test
    public void shouldParseSecondTopLevelPdxDoubleField() {
        String[] fields = new String[]{ "positions.sharesOutstanding" };
        FlatFormatSerializer serializer = new FlatFormatSerializer();
        PdxInstance pdx = createPdxInstance();
        Document doc1 = SerializerTestHelper.invokeSerializer(serializer, pdx, fields);
        IndexableField[] fieldsInDoc = doc1.getFields("positions.sharesOutstanding");
        Collection<Object> results = getResultCollection(fieldsInDoc, true);
        Assert.assertEquals(2, results.size());
        Assert.assertTrue(results.contains(5000.0));
        Assert.assertTrue(results.contains(4000.0));
    }
}

