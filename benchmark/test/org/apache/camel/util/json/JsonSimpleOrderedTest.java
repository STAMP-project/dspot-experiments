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
package org.apache.camel.util.json;


import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class JsonSimpleOrderedTest extends Assert {
    @Test
    public void testOrdered() throws Exception {
        InputStream is = new FileInputStream("src/test/resources/bean.json");
        String json = JsonSimpleOrderedTest.loadText(is);
        JsonObject output = Jsoner.deserialize(json, new JsonObject());
        Assert.assertNotNull(output);
        // should preserve order
        Map map = output.getMap("component");
        Assert.assertTrue((map instanceof LinkedHashMap));
        Iterator it = map.keySet().iterator();
        Assert.assertEquals("kind", it.next());
        Assert.assertEquals("scheme", it.next());
        Assert.assertEquals("syntax", it.next());
        Assert.assertEquals("title", it.next());
        Assert.assertEquals("description", it.next());
        Assert.assertEquals("label", it.next());
        Assert.assertEquals("deprecated", it.next());
        Assert.assertEquals("deprecationNote", it.next());
        Assert.assertEquals("async", it.next());
        Assert.assertEquals("consumerOnly", it.next());
        Assert.assertEquals("producerOnly", it.next());
        Assert.assertEquals("lenientProperties", it.next());
        Assert.assertEquals("javaType", it.next());
        Assert.assertEquals("firstVersion", it.next());
        Assert.assertEquals("groupId", it.next());
        Assert.assertEquals("artifactId", it.next());
        Assert.assertEquals("version", it.next());
        Assert.assertFalse(it.hasNext());
    }
}

