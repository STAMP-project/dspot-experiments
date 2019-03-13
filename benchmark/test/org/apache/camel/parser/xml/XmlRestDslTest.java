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
package org.apache.camel.parser.xml;


import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import org.apache.camel.parser.XmlRestDslParser;
import org.apache.camel.parser.model.RestConfigurationDetails;
import org.apache.camel.parser.model.RestServiceDetails;
import org.junit.Assert;
import org.junit.Test;


public class XmlRestDslTest {
    @Test
    public void testXmlTree() throws Exception {
        InputStream is = new FileInputStream("src/test/resources/org/apache/camel/parser/xml/myrest.xml");
        String fqn = "src/test/resources/org/apache/camel/camel/parser/xml/myrest.xml";
        String baseDir = "src/test/resources";
        List<RestConfigurationDetails> list = XmlRestDslParser.parseRestConfiguration(is, baseDir, fqn);
        Assert.assertEquals(1, list.size());
        RestConfigurationDetails details = list.get(0);
        Assert.assertEquals("src/test/resources/org/apache/camel/camel/parser/xml/myrest.xml", details.getFileName());
        Assert.assertNull(details.getMethodName());
        Assert.assertNull(details.getClassName());
        Assert.assertEquals("29", details.getLineNumber());
        Assert.assertEquals("35", details.getLineNumberEnd());
        Assert.assertEquals("1234", details.getPort());
        Assert.assertEquals("myapi", details.getContextPath());
        Assert.assertEquals("jetty", details.getComponent());
        Assert.assertEquals("json", details.getBindingMode());
        Assert.assertEquals("swagger", details.getApiComponent());
        Assert.assertEquals("myapi/swagger", details.getApiContextPath());
        Assert.assertEquals("localhost", details.getApiHost());
        Assert.assertEquals("true", details.getSkipBindingOnErrorCode());
        Assert.assertEquals("https", details.getScheme());
        Assert.assertEquals("allLocalIp", details.getHostNameResolver());
        Assert.assertEquals(1, details.getComponentProperties().size());
        Assert.assertEquals("123", details.getComponentProperties().get("foo"));
        Assert.assertEquals(1, details.getEndpointProperties().size());
        Assert.assertEquals("false", details.getEndpointProperties().get("pretty"));
        Assert.assertEquals(1, details.getEndpointProperties().size());
        Assert.assertEquals("456", details.getConsumerProperties().get("bar"));
        Assert.assertEquals(2, details.getCorsHeaders().size());
        Assert.assertEquals("value1", details.getCorsHeaders().get("key1"));
        Assert.assertEquals("value2", details.getCorsHeaders().get("key2"));
    }

    @Test
    public void parseRestService() throws Exception {
        InputStream is = new FileInputStream("src/test/resources/org/apache/camel/parser/xml/myrest.xml");
        String fqn = "src/test/resources/org/apache/camel/camel/parser/xml/myrest.xml";
        String baseDir = "src/test/resources";
        List<RestServiceDetails> list = XmlRestDslParser.parseRestService(is, baseDir, fqn);
        Assert.assertEquals(1, list.size());
        RestServiceDetails details = list.get(0);
        Assert.assertEquals("src/test/resources/org/apache/camel/camel/parser/xml/myrest.xml", details.getFileName());
        Assert.assertNull(details.getMethodName());
        Assert.assertNull(details.getClassName());
        Assert.assertEquals("37", details.getLineNumber());
        Assert.assertEquals("47", details.getLineNumberEnd());
        Assert.assertEquals("src/test/resources/org/apache/camel/camel/parser/xml/myrest.xml", details.getFileName());
        Assert.assertNull(details.getMethodName());
        Assert.assertNull(details.getClassName());
        Assert.assertEquals("/foo", details.getPath());
        Assert.assertEquals("my foo service", details.getDescription());
        Assert.assertEquals("json", details.getProduces());
        Assert.assertEquals("json", details.getProduces());
        Assert.assertEquals(2, details.getVerbs().size());
        Assert.assertEquals("get", details.getVerbs().get(0).getMethod());
        Assert.assertEquals("{id}", details.getVerbs().get(0).getUri());
        Assert.assertEquals("get by id", details.getVerbs().get(0).getDescription());
        Assert.assertEquals("log:id", details.getVerbs().get(0).getTo());
        Assert.assertEquals("false", details.getVerbs().get(0).getApiDocs());
        Assert.assertEquals("post", details.getVerbs().get(1).getMethod());
        Assert.assertEquals("post something", details.getVerbs().get(1).getDescription());
        Assert.assertEquals("xml", details.getVerbs().get(1).getBindingMode());
        Assert.assertEquals("log:post", details.getVerbs().get(1).getToD());
        Assert.assertNull(details.getVerbs().get(1).getUri());
    }
}

