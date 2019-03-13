/**
 * This file is part of dependency-check-core.
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
 *
 * Copyright (c) 2016 Jeremy Long. All Rights Reserved.
 */
package org.owasp.dependencycheck.xml.hints;


import HintParser.JAXP_SCHEMA_LANGUAGE;
import HintParser.JAXP_SCHEMA_SOURCE;
import HintParser.W3C_XML_SCHEMA;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.junit.Assert;
import org.junit.Test;
import org.owasp.dependencycheck.BaseTest;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;


/**
 *
 *
 * @author Jeremy Long
 */
public class HintHandlerTest extends BaseTest {
    @Test
    public void testHandler() throws FileNotFoundException, IOException, UnsupportedEncodingException, ParserConfigurationException, SAXException, SAXNotRecognizedException, SAXNotSupportedException {
        File file = BaseTest.getResourceAsFile(this, "hints.xml");
        File schema = BaseTest.getResourceAsFile(this, "schema/dependency-hint.1.1.xsd");
        HintHandler handler = new HintHandler();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(true);
        SAXParser saxParser = factory.newSAXParser();
        saxParser.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
        saxParser.setProperty(JAXP_SCHEMA_SOURCE, schema);
        XMLReader xmlReader = saxParser.getXMLReader();
        xmlReader.setErrorHandler(new HintErrorHandler());
        xmlReader.setContentHandler(handler);
        InputStream inputStream = new FileInputStream(file);
        Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        InputSource in = new InputSource(reader);
        xmlReader.parse(in);
        List<HintRule> result = handler.getHintRules();
        Assert.assertEquals("two hint rules should have been loaded", 2, result.size());
    }
}

