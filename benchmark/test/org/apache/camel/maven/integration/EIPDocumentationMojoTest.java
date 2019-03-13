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
package org.apache.camel.maven.integration;


import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import org.apache.camel.maven.EipDocumentationEnricherMojo;
import org.junit.Test;
import org.w3c.dom.Document;


public class EIPDocumentationMojoTest {
    EipDocumentationEnricherMojo eipDocumentationEnricherMojo = new EipDocumentationEnricherMojo();

    XPath xPath = XPathFactory.newInstance().newXPath();

    File tempFile;

    @Test
    public void testExecuteMojo() throws Exception {
        eipDocumentationEnricherMojo.execute();
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document doc = documentBuilder.parse(tempFile);
        validateElement(doc);
        validateAttributes(doc);
        validateParentAttribute(doc);
    }
}

