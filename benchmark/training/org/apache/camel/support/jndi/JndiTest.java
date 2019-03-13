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
package org.apache.camel.support.jndi;


import javax.naming.Context;
import org.apache.camel.TestSupport;
import org.junit.Assert;
import org.junit.Test;


public class JndiTest extends TestSupport {
    protected Context context;

    @Test
    public void testLookupOfSimpleName() throws Exception {
        Object value = assertLookup("foo");
        Assert.assertEquals("foo", "bar", value);
    }

    @Test
    public void testLookupOfTypedObject() throws Exception {
        Object value = assertLookup("example");
        ExampleBean bean = TestSupport.assertIsInstanceOf(ExampleBean.class, value);
        Assert.assertEquals("Bean.name", "James", bean.getName());
        Assert.assertEquals("Bean.price", 2.34, bean.getPrice(), 1.0E-5);
        log.info(("Found bean: " + bean));
    }
}

