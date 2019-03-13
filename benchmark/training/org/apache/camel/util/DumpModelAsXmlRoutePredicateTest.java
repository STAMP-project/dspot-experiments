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
import org.apache.camel.TestSupport;
import org.apache.camel.model.ModelHelper;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class DumpModelAsXmlRoutePredicateTest extends ContextTestSupport {
    @Test
    public void testDumpModelAsXml() throws Exception {
        String xml = ModelHelper.dumpModelAsXml(context, context.getRouteDefinition("myRoute"));
        Assert.assertNotNull(xml);
        log.info(xml);
        Assert.assertTrue(xml.contains("<simple>${body} &gt; 10</simple>"));
    }

    @Test
    public void testDumpModelAsXmlXPath() throws Exception {
        String xml = ModelHelper.dumpModelAsXml(context, context.getRouteDefinition("myOtherRoute"));
        Assert.assertNotNull(xml);
        log.info(xml);
        Assert.assertTrue(xml.contains("<xpath>/foo</xpath>"));
    }

    @Test
    public void testDumpModelAsXmlHeader() throws Exception {
        String xml = ModelHelper.dumpModelAsXml(context, context.getRouteDefinition("myFooRoute"));
        Assert.assertNotNull(xml);
        log.info(xml);
        Assert.assertTrue(xml.contains("<header>bar</header>"));
    }

    @Test
    public void testDumpModelAsXmlBean() throws Exception {
        String xml = ModelHelper.dumpModelAsXml(context, context.getRouteDefinition("myBeanRoute"));
        Assert.assertNotNull(xml);
        log.info(xml);
        Assert.assertTrue(xml.contains("<method>myCoolBean</method>"));
    }
}

