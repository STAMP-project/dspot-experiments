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
package org.apache.camel.util;


import org.apache.camel.ContextTestSupport;
import org.apache.camel.model.ModelHelper;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class DumpModelAsXmlNamespaceTest extends ContextTestSupport {
    private static final String URL_FOO = "http://foo.com";

    private static final String URL_BAR = "http://bar.com";

    @Test
    public void testDumpModelAsXml() throws Exception {
        String xml = ModelHelper.dumpModelAsXml(context, context.getRouteDefinition("myRoute"));
        Assert.assertNotNull(xml);
        Document dom = context.getTypeConverter().convertTo(Document.class, xml);
        Element rootNode = dom.getDocumentElement();
        Assert.assertNotNull(rootNode);
        String attributeFoo = rootNode.getAttribute("xmlns:foo");
        Assert.assertNotNull(attributeFoo);
        Assert.assertEquals(DumpModelAsXmlNamespaceTest.URL_FOO, attributeFoo);
        String attributeBar = rootNode.getAttribute("xmlns:bar");
        Assert.assertNotNull(attributeBar);
        Assert.assertEquals(DumpModelAsXmlNamespaceTest.URL_BAR, attributeBar);
    }
}

