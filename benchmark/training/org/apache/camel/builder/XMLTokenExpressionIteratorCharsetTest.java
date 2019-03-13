/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.builder;


import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class XMLTokenExpressionIteratorCharsetTest extends Assert {
    private static final String DATA_TEMPLATE = "<?xml version=\"1.0\" encoding=\"{0}\"?>" + ((("<Statements xmlns=\"http://www.apache.org/xml/test\">" + "    <statement>we l\u00f3ve iso-latin</statement>") + "    <statement>we h\u00e4te unicode</statement>") + "</Statements>");

    private static final String[] RESULTS = new String[]{ "<statement xmlns=\"http://www.apache.org/xml/test\">we l\u00f3ve iso-latin</statement>", "<statement xmlns=\"http://www.apache.org/xml/test\">we h\u00e4te unicode</statement>" };

    private static final String DATA_STRING = MessageFormat.format(XMLTokenExpressionIteratorCharsetTest.DATA_TEMPLATE, "utf-8");

    private static final byte[] DATA_UTF8 = XMLTokenExpressionIteratorCharsetTest.getBytes(XMLTokenExpressionIteratorCharsetTest.DATA_TEMPLATE, "utf-8");

    private static final byte[] DATA_ISOLATIN = XMLTokenExpressionIteratorCharsetTest.getBytes(XMLTokenExpressionIteratorCharsetTest.DATA_TEMPLATE, "iso-8859-1");

    private static final Map<String, String> NSMAP = Collections.singletonMap("", "http://www.apache.org/xml/test");

    @Test
    public void testTokenzeWithUTF8() throws Exception {
        XMLTokenExpressionIterator xtei = new XMLTokenExpressionIterator("//statement", 'i');
        xtei.setNamespaces(XMLTokenExpressionIteratorCharsetTest.NSMAP);
        invokeAndVerify(xtei.createIterator(new ByteArrayInputStream(XMLTokenExpressionIteratorCharsetTest.DATA_UTF8), "utf-8"));
    }

    @Test
    public void testTokenizeWithISOLatin() throws Exception {
        XMLTokenExpressionIterator xtei = new XMLTokenExpressionIterator("//statement", 'i');
        xtei.setNamespaces(XMLTokenExpressionIteratorCharsetTest.NSMAP);
        invokeAndVerify(xtei.createIterator(new ByteArrayInputStream(XMLTokenExpressionIteratorCharsetTest.DATA_ISOLATIN), "iso-8859-1"));
    }

    @Test
    public void testTokenizeWithReader() throws Exception {
        XMLTokenExpressionIterator xtei = new XMLTokenExpressionIterator("//statement", 'i');
        xtei.setNamespaces(XMLTokenExpressionIteratorCharsetTest.NSMAP);
        invokeAndVerify(xtei.createIterator(new StringReader(XMLTokenExpressionIteratorCharsetTest.DATA_STRING)));
    }
}

