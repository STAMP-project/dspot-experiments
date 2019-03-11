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
package org.apache.activemq.tool.properties;


import java.util.Set;
import junit.framework.TestCase;


public class JmsProducerPropertiesTest extends TestCase {
    /**
     * Tests the correct parsing of message headers.
     *
     * @see JmsProducerProperties.setHeader(String encodedHeader)
     */
    public void testMessageHeaders() {
        // first test correct header values
        String header = "a=b";
        JmsProducerProperties props = new JmsProducerProperties();
        props.setHeader(header);
        TestCase.assertEquals(1, props.headerMap.size());
        Set<String> keys = props.getHeaderKeys();
        TestCase.assertEquals(1, keys.size());
        TestCase.assertTrue(keys.contains("a"));
        TestCase.assertEquals("b", props.getHeaderValue("a"));
        props.clearHeaders();
        header = "a=b:c=d";
        props.setHeader(header);
        TestCase.assertEquals(2, props.headerMap.size());
        keys = props.getHeaderKeys();
        TestCase.assertEquals(2, keys.size());
        TestCase.assertTrue(keys.contains("a"));
        TestCase.assertTrue(keys.contains("c"));
        TestCase.assertEquals("b", props.getHeaderValue("a"));
        TestCase.assertEquals("d", props.getHeaderValue("c"));
        props.clearHeaders();
        header = "a=b:c=d:e=f";
        props.setHeader(header);
        TestCase.assertEquals(3, props.headerMap.size());
        keys = props.getHeaderKeys();
        TestCase.assertEquals(3, keys.size());
        TestCase.assertTrue(keys.contains("a"));
        TestCase.assertTrue(keys.contains("c"));
        TestCase.assertTrue(keys.contains("e"));
        TestCase.assertEquals("b", props.getHeaderValue("a"));
        TestCase.assertEquals("d", props.getHeaderValue("c"));
        TestCase.assertEquals("f", props.getHeaderValue("e"));
        props.clearHeaders();
        header = "a=b:c=d:e=f:";
        props.setHeader(header);
        TestCase.assertEquals(3, props.headerMap.size());
        keys = props.getHeaderKeys();
        TestCase.assertEquals(3, keys.size());
        TestCase.assertTrue(keys.contains("a"));
        TestCase.assertTrue(keys.contains("c"));
        TestCase.assertTrue(keys.contains("e"));
        TestCase.assertEquals("b", props.getHeaderValue("a"));
        TestCase.assertEquals("d", props.getHeaderValue("c"));
        TestCase.assertEquals("f", props.getHeaderValue("e"));
        props.clearHeaders();
        // test incorrect header values
        header = "a:=";
        props.setHeader(header);
        TestCase.assertEquals(0, props.headerMap.size());
        props.clearHeaders();
        header = "a:=b";
        props.setHeader(header);
        TestCase.assertEquals(0, props.headerMap.size());
        props.clearHeaders();
        header = "a=:";
        props.setHeader(header);
        TestCase.assertEquals(0, props.headerMap.size());
        props.clearHeaders();
        header = "a=b::";
        props.setHeader(header);
        TestCase.assertEquals(1, props.headerMap.size());
        keys = props.getHeaderKeys();
        TestCase.assertEquals(1, keys.size());
        TestCase.assertTrue(keys.contains("a"));
        TestCase.assertEquals("b", props.getHeaderValue("a"));
        props.clearHeaders();
        header = "a=b:\":";
        props.setHeader(header);
        TestCase.assertEquals(1, props.headerMap.size());
        keys = props.getHeaderKeys();
        TestCase.assertEquals(1, keys.size());
        TestCase.assertTrue(keys.contains("a"));
        TestCase.assertEquals("b", props.getHeaderValue("a"));
        props.clearHeaders();
        header = " :  ";
        props.setHeader(header);
        TestCase.assertEquals(0, props.headerMap.size());
        props.clearHeaders();
    }
}

