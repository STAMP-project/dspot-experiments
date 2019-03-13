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
package org.apache.camel.component.jms;


import org.junit.Assert;
import org.junit.Test;


public class DefaultJmsHeaderKeyFormatStrategyTest extends Assert {
    private JmsKeyFormatStrategy strategy = new DefaultJmsKeyFormatStrategy();

    @Test
    public void testEncodeValidKeys() {
        Assert.assertEquals("foo", strategy.encodeKey("foo"));
        Assert.assertEquals("foo123bar", strategy.encodeKey("foo123bar"));
        Assert.assertEquals("CamelFileName", strategy.encodeKey("CamelFileName"));
        Assert.assertEquals("org_DOT_apache_DOT_camel_DOT_MyBean", strategy.encodeKey("org.apache.camel.MyBean"));
        Assert.assertEquals("Content_HYPHEN_Type", strategy.encodeKey("Content-Type"));
        Assert.assertEquals("My_HYPHEN_Header_DOT_You", strategy.encodeKey("My-Header.You"));
    }

    @Test
    public void testDecodeValidKeys() {
        Assert.assertEquals("foo", strategy.decodeKey("foo"));
        Assert.assertEquals("foo123bar", strategy.decodeKey("foo123bar"));
        Assert.assertEquals("CamelFileName", strategy.decodeKey("CamelFileName"));
        Assert.assertEquals("Content-Type", strategy.decodeKey("Content_HYPHEN_Type"));
        Assert.assertEquals("My-Header.You", strategy.decodeKey("My_HYPHEN_Header_DOT_You"));
        Assert.assertEquals("org.apache.camel.MyBean", strategy.decodeKey("org_DOT_apache_DOT_camel_DOT_MyBean"));
    }
}

