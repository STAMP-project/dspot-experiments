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
package org.apache.camel.component.file;


import java.io.File;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;


public class XPathToFileTest extends ContextTestSupport {
    @Test
    public void testXPathToFile() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(2);
        String xml = "<foo><person id=\"1\">Claus<country>SE</country></person>" + "<person id=\"2\">Jonathan<country>CA</country></person></foo>";
        Document doc = context.getTypeConverter().convertTo(Document.class, xml);
        template.sendBody("direct:start", doc);
        assertMockEndpointsSatisfied();
        File first = new File("target/data/xpath/xpath-0.xml");
        Assert.assertTrue("File xpath-0.xml should exists", first.exists());
        Assert.assertEquals("<person id=\"1\">Claus<country>SE</country></person>", context.getTypeConverter().convertTo(String.class, first));
        File second = new File("target/data/xpath/xpath-1.xml");
        Assert.assertTrue("File xpath-1.xml should exists", second.exists());
        Assert.assertEquals("<person id=\"2\">Jonathan<country>CA</country></person>", context.getTypeConverter().convertTo(String.class, second));
    }
}

