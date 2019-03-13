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
package org.apache.camel.component.properties;


import org.apache.camel.ContextTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class PropertiesComponentLoadPropertiesFromFileTrimValuesTest extends ContextTestSupport {
    @Test
    public void testMustTrimValues() throws Exception {
        Assert.assertEquals("Leading space", context.resolvePropertyPlaceholders("{{cool.leading}}"));
        Assert.assertEquals("Trailing space", context.resolvePropertyPlaceholders("{{cool.trailing}}"));
        Assert.assertEquals("Both leading and trailing space", context.resolvePropertyPlaceholders("{{cool.both}}"));
        Assert.assertEquals("\r\n", context.resolvePropertyPlaceholders("{{space.leading}}"));
        Assert.assertEquals("\t", context.resolvePropertyPlaceholders("{{space.trailing}}"));
        Assert.assertEquals("\r   \t  \n", context.resolvePropertyPlaceholders("{{space.both}}"));
        Assert.assertEquals("Leading space\r\n", context.resolvePropertyPlaceholders("{{mixed.leading}}"));
        Assert.assertEquals("Trailing space\t", context.resolvePropertyPlaceholders("{{mixed.trailing}}"));
        Assert.assertEquals("Both leading and trailing space\r   \t  \n", context.resolvePropertyPlaceholders("{{mixed.both}}"));
        Assert.assertEquals("", context.resolvePropertyPlaceholders("{{empty.line}}"));
    }
}

