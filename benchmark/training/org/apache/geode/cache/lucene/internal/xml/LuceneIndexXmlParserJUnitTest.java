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


import LuceneXmlConstants.INDEX;
import LuceneXmlConstants.NAME;
import LuceneXmlConstants.NAMESPACE;
import java.util.Map;
import java.util.Stack;
import org.apache.geode.internal.cache.xmlcache.RegionCreation;
import org.apache.geode.internal.cache.xmlcache.XmlGeneratorUtils;
import org.apache.geode.test.junit.categories.LuceneTest;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


@Category({ LuceneTest.class })
public class LuceneIndexXmlParserJUnitTest {
    private LuceneXmlParser parser;

    private RegionCreation rc;

    private Stack<Object> stack;

    @Test
    public void generateWithFields() throws SAXException {
        AttributesImpl attrs = new AttributesImpl();
        XmlGeneratorUtils.addAttribute(attrs, NAME, "index");
        this.parser.startElement(NAMESPACE, INDEX, null, attrs);
        addField("field1");
        addField("field2");
        addField("field3", KeywordAnalyzer.class.getName());
        this.parser.endElement(NAMESPACE, INDEX, null);
        Assert.assertEquals(this.rc, this.stack.peek());
        LuceneIndexCreation index = ((LuceneIndexCreation) (this.rc.getExtensionPoint().getExtensions().iterator().next()));
        Assert.assertEquals("index", index.getName());
        Assert.assertArrayEquals(new String[]{ "field1", "field2", "field3" }, index.getFieldNames());
        // Assert analyzers
        Map<String, Analyzer> fieldAnalyzers = index.getFieldAnalyzers();
        Assert.assertEquals(1, fieldAnalyzers.size());
        Assert.assertTrue(fieldAnalyzers.containsKey("field3"));
        Assert.assertTrue(((fieldAnalyzers.get("field3")) instanceof KeywordAnalyzer));
    }

    @Test
    public void attemptInvalidAnalyzerClass() throws SAXException {
        AttributesImpl attrs = new AttributesImpl();
        XmlGeneratorUtils.addAttribute(attrs, NAME, "index");
        this.parser.startElement(NAMESPACE, INDEX, null, attrs);
        try {
            addField("field", "some.invalid.class");
            Assert.fail("Should not have been able to add a field with an invalid analyzer class name");
        } catch (Exception e) {
        }
    }
}

