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
package org.apache.geode.cache.lucene.internal.xml;


import LuceneXmlConstants.NAME;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import org.apache.geode.cache.lucene.LuceneSerializer;
import org.apache.geode.internal.cache.xmlcache.CacheXmlGenerator;
import org.apache.geode.internal.cache.xmlcache.Declarable2;
import org.apache.geode.test.junit.categories.LuceneTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;


@Category({ LuceneTest.class })
public class LuceneIndexXmlGeneratorJUnitTest {
    /**
     * Test of generating cache configuration.
     */
    @Test
    public void generateWithFields() throws Exception {
        LuceneIndexCreation index = Mockito.mock(LuceneIndexCreation.class);
        Mockito.when(index.getName()).thenReturn("index");
        String[] fields = new String[]{ "field1", "field2" };
        Mockito.when(index.getFieldNames()).thenReturn(fields);
        LuceneIndexXmlGenerator generator = new LuceneIndexXmlGenerator(index);
        CacheXmlGenerator cacheXmlGenerator = Mockito.mock(CacheXmlGenerator.class);
        ContentHandler handler = Mockito.mock(ContentHandler.class);
        Mockito.when(cacheXmlGenerator.getContentHandler()).thenReturn(handler);
        generator.generate(cacheXmlGenerator);
        ArgumentCaptor<Attributes> captor = ArgumentCaptor.forClass(Attributes.class);
        Mockito.verify(handler).startElement(ArgumentMatchers.eq(""), ArgumentMatchers.eq("index"), ArgumentMatchers.eq("lucene:index"), captor.capture());
        Attributes value = captor.getValue();
        Assert.assertEquals("index", value.getValue(NAME));
        captor = ArgumentCaptor.forClass(Attributes.class);
        Mockito.verify(handler, Mockito.times(2)).startElement(ArgumentMatchers.eq(""), ArgumentMatchers.eq("field"), ArgumentMatchers.eq("lucene:field"), captor.capture());
        Set<String> foundFields = new HashSet<String>();
        for (Attributes fieldAttr : captor.getAllValues()) {
            foundFields.add(fieldAttr.getValue(NAME));
        }
        HashSet<String> expected = new HashSet<String>(Arrays.asList(fields));
        Assert.assertEquals(expected, foundFields);
        Mockito.verify(handler, Mockito.times(2)).endElement(ArgumentMatchers.eq(""), ArgumentMatchers.eq("field"), ArgumentMatchers.eq("lucene:field"));
        Mockito.verify(handler).endElement(ArgumentMatchers.eq(""), ArgumentMatchers.eq("index"), ArgumentMatchers.eq("lucene:index"));
    }

    /**
     * Test generating lucene xml with serializer
     */
    @Test
    public void generateWithSerializer() throws Exception {
        LuceneIndexCreation index = Mockito.mock(LuceneIndexCreation.class);
        LuceneSerializer mySerializer = Mockito.mock(LuceneSerializer.class, Mockito.withSettings().extraInterfaces(Declarable2.class));
        Properties props = new Properties();
        props.put("param", "value");
        Mockito.when(index.getName()).thenReturn("index");
        String[] fields = new String[]{ "field1", "field2" };
        Mockito.when(index.getFieldNames()).thenReturn(fields);
        Mockito.when(index.getLuceneSerializer()).thenReturn(mySerializer);
        Mockito.when(getConfig()).thenReturn(props);
        LuceneIndexXmlGenerator generator = new LuceneIndexXmlGenerator(index);
        CacheXmlGenerator cacheXmlGenerator = Mockito.mock(CacheXmlGenerator.class);
        ContentHandler handler = Mockito.mock(ContentHandler.class);
        Mockito.when(cacheXmlGenerator.getContentHandler()).thenReturn(handler);
        generator.generate(cacheXmlGenerator);
        ArgumentCaptor<Attributes> captor = ArgumentCaptor.forClass(Attributes.class);
        Mockito.verify(handler).startElement(ArgumentMatchers.eq(""), ArgumentMatchers.eq("index"), ArgumentMatchers.eq("lucene:index"), captor.capture());
        Attributes value = captor.getValue();
        Assert.assertEquals("index", value.getValue(NAME));
        // fields
        captor = ArgumentCaptor.forClass(Attributes.class);
        Mockito.verify(handler, Mockito.times(2)).startElement(ArgumentMatchers.eq(""), ArgumentMatchers.eq("field"), ArgumentMatchers.eq("lucene:field"), captor.capture());
        Set<String> foundFields = new HashSet<String>();
        for (Attributes fieldAttr : captor.getAllValues()) {
            foundFields.add(fieldAttr.getValue(NAME));
        }
        HashSet<String> expected = new HashSet<String>(Arrays.asList(fields));
        Assert.assertEquals(expected, foundFields);
        Mockito.verify(handler, Mockito.times(2)).endElement(ArgumentMatchers.eq(""), ArgumentMatchers.eq("field"), ArgumentMatchers.eq("lucene:field"));
        // serializer
        captor = ArgumentCaptor.forClass(Attributes.class);
        Mockito.verify(handler, Mockito.times(1)).startElement(ArgumentMatchers.eq(""), ArgumentMatchers.eq("serializer"), ArgumentMatchers.eq("lucene:serializer"), captor.capture());
        captor = ArgumentCaptor.forClass(Attributes.class);
        Mockito.verify(handler, Mockito.times(1)).startElement(ArgumentMatchers.eq(""), ArgumentMatchers.eq("class-name"), ArgumentMatchers.eq("class-name"), captor.capture());
        String expectedString = mySerializer.getClass().getName();
        Mockito.verify(handler).characters(ArgumentMatchers.eq(expectedString.toCharArray()), ArgumentMatchers.eq(0), ArgumentMatchers.eq(expectedString.length()));
        Mockito.verify(handler).endElement(ArgumentMatchers.eq(""), ArgumentMatchers.eq("class-name"), ArgumentMatchers.eq("class-name"));
        // properties as parameters
        captor = ArgumentCaptor.forClass(Attributes.class);
        Mockito.verify(handler, Mockito.times(1)).startElement(ArgumentMatchers.eq(""), ArgumentMatchers.eq("parameter"), ArgumentMatchers.eq("parameter"), captor.capture());
        value = captor.getValue();
        Assert.assertEquals("param", value.getValue(NAME));
        captor = ArgumentCaptor.forClass(Attributes.class);
        Mockito.verify(handler, Mockito.times(1)).startElement(ArgumentMatchers.eq(""), ArgumentMatchers.eq("string"), ArgumentMatchers.eq("string"), captor.capture());
        String expectedValue = "value";
        Mockito.verify(handler).characters(ArgumentMatchers.eq(expectedValue.toCharArray()), ArgumentMatchers.eq(0), ArgumentMatchers.eq(expectedValue.length()));
        Mockito.verify(handler).endElement(ArgumentMatchers.eq(""), ArgumentMatchers.eq("string"), ArgumentMatchers.eq("string"));
        Mockito.verify(handler).endElement(ArgumentMatchers.eq(""), ArgumentMatchers.eq("parameter"), ArgumentMatchers.eq("parameter"));
        // endElement invocations
        Mockito.verify(handler).endElement(ArgumentMatchers.eq(""), ArgumentMatchers.eq("serializer"), ArgumentMatchers.eq("lucene:serializer"));
        Mockito.verify(handler).endElement(ArgumentMatchers.eq(""), ArgumentMatchers.eq("index"), ArgumentMatchers.eq("lucene:index"));
    }
}

