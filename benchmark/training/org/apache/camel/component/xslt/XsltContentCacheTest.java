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
package org.apache.camel.component.xslt;


import Exchange.FILE_NAME;
import java.util.ArrayList;
import java.util.Set;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


/**
 *
 */
public class XsltContentCacheTest extends ContextTestSupport {
    private static final String ORIGINAL_XSL = "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">" + ("<xsl:template match=\"/\"><goodbye><xsl:value-of select=\"/hello\"/></goodbye></xsl:template>" + "</xsl:stylesheet>");

    private static final String NEW_XSL = "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">" + ("<xsl:template match=\"/\"><goodnight><xsl:value-of select=\"/hello\"/></goodnight></xsl:template>" + "</xsl:stylesheet>");

    @Test
    public void testNotCached() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("<?xml version=\"1.0\" encoding=\"UTF-8\"?><goodbye>world!</goodbye>");
        template.sendBody("direct:a", "<hello>world!</hello>");
        mock.assertIsSatisfied();
        // now replace the file with a new XSL transformation
        template.sendBodyAndHeader("file://target/test-classes/org/apache/camel/component/xslt?fileExist=Override", XsltContentCacheTest.NEW_XSL, FILE_NAME, "hello.xsl");
        mock.reset();
        // we expect the new output as the cache is not enabled, so it's "goodnight" and not "goodbye"
        mock.expectedBodiesReceived("<?xml version=\"1.0\" encoding=\"UTF-8\"?><goodnight>world!</goodnight>");
        template.sendBody("direct:a", "<hello>world!</hello>");
        mock.assertIsSatisfied();
    }

    @Test
    public void testCached() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("<?xml version=\"1.0\" encoding=\"UTF-8\"?><goodbye>world!</goodbye>");
        template.sendBody("direct:b", "<hello>world!</hello>");
        mock.assertIsSatisfied();
        // now replace the file with a new XSL transformation
        template.sendBodyAndHeader("file://target/test-classes/org/apache/camel/component/xslt?fileExist=Override", XsltContentCacheTest.NEW_XSL, FILE_NAME, "hello.xsl");
        mock.reset();
        // we expect the original output as the cache is enabled, so it's "goodbye" and not "goodnight"
        mock.expectedBodiesReceived("<?xml version=\"1.0\" encoding=\"UTF-8\"?><goodbye>world!</goodbye>");
        template.sendBody("direct:b", "<hello>world!</hello>");
        mock.assertIsSatisfied();
    }

    @Test
    public void testCachedIsDefault() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("<?xml version=\"1.0\" encoding=\"UTF-8\"?><goodbye>world!</goodbye>");
        template.sendBody("direct:c", "<hello>world!</hello>");
        mock.assertIsSatisfied();
        // now replace the file with a new XSL transformation
        template.sendBodyAndHeader("file://target/test-classes/org/apache/camel/component/xslt?fileExist=Override", XsltContentCacheTest.NEW_XSL, FILE_NAME, "hello.xsl");
        mock.reset();
        // we expect the original output as the cache is enabled, so it's "goodbye" and not "goodnight"
        mock.expectedBodiesReceived("<?xml version=\"1.0\" encoding=\"UTF-8\"?><goodbye>world!</goodbye>");
        template.sendBody("direct:c", "<hello>world!</hello>");
        mock.assertIsSatisfied();
    }

    @Test
    public void testClearCachedStylesheetViaJmx() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("<?xml version=\"1.0\" encoding=\"UTF-8\"?><goodbye>world!</goodbye>");
        template.sendBody("direct:b", "<hello>world!</hello>");
        mock.assertIsSatisfied();
        // now replace the file with a new XSL transformation
        template.sendBodyAndHeader("file://target/test-classes/org/apache/camel/component/xslt?fileExist=Override", XsltContentCacheTest.NEW_XSL, FILE_NAME, "hello.xsl");
        mock.reset();
        // we expect the original output as the cache is enabled, so it's "goodbye" and not "goodnight"
        mock.expectedBodiesReceived("<?xml version=\"1.0\" encoding=\"UTF-8\"?><goodbye>world!</goodbye>");
        template.sendBody("direct:b", "<hello>world!</hello>");
        mock.assertIsSatisfied();
        // clear the cache via the mbean server
        MBeanServer mbeanServer = context.getManagementStrategy().getManagementAgent().getMBeanServer();
        Set<ObjectName> objNameSet = mbeanServer.queryNames(new ObjectName("org.apache.camel:type=endpoints,name=\"xslt:*contentCache=true*\",*"), null);
        ObjectName managedObjName = new ArrayList<>(objNameSet).get(0);
        mbeanServer.invoke(managedObjName, "clearCachedStylesheet", null, null);
        // now replace the file with a new XSL transformation
        template.sendBodyAndHeader("file://target/test-classes/org/apache/camel/component/xslt?fileExist=Override", XsltContentCacheTest.NEW_XSL, FILE_NAME, "hello.xsl");
        mock.reset();
        // we've cleared the cache so we expect "goodnight" and not "goodbye"
        mock.expectedBodiesReceived("<?xml version=\"1.0\" encoding=\"UTF-8\"?><goodnight>world!</goodnight>");
        template.sendBody("direct:b", "<hello>world!</hello>");
        mock.assertIsSatisfied();
    }
}

