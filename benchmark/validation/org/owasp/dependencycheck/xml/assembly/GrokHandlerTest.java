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
 * Copyright (c) 2013 Jeremy Long. All Rights Reserved.
 */
package org.owasp.dependencycheck.xml.assembly;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import javax.xml.parsers.SAXParser;
import org.junit.Assert;
import org.junit.Test;
import org.owasp.dependencycheck.BaseTest;
import org.owasp.dependencycheck.utils.XmlUtils;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;


/**
 *
 *
 * @author Jeremy Long
 */
public class GrokHandlerTest extends BaseTest {
    /**
     * Test of getSuppressionRules method, of class SuppressionHandler.
     *
     * @throws Exception
     * 		thrown if there is an exception....
     */
    @Test
    public void testHandler() throws Exception {
        File file = BaseTest.getResourceAsFile(this, "assembly/sample-grok.xml");
        InputStream schemaStream = BaseTest.getResourceAsStream(this, "schema/grok-assembly.1.0.xsd");
        GrokHandler handler = new GrokHandler();
        SAXParser saxParser = XmlUtils.buildSecureSaxParser(schemaStream);
        XMLReader xmlReader = saxParser.getXMLReader();
        xmlReader.setErrorHandler(new GrokErrorHandler());
        xmlReader.setContentHandler(handler);
        InputStream inputStream = new FileInputStream(file);
        Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        InputSource in = new InputSource(reader);
        xmlReader.parse(in);
        AssemblyData result = handler.getAssemblyData();
        Assert.assertEquals("OWASP Contributors", result.getCompanyName());
        Assert.assertEquals("GrokAssembly", result.getProductName());
        Assert.assertEquals("Inspects a .NET Assembly to determine Company, Product, and Version information", result.getComments());
        Assert.assertEquals(1, result.getNamespaces().size());
    }
}

