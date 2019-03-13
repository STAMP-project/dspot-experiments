/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.lang;


import org.junit.Assert;
import org.junit.Test;


/**
 * The InOutParameterJUnitTest class is a test suite with test cases to test the contract and
 * functionality of the InOutParameter class.
 * <p/>
 *
 * @see org.apache.geode.internal.lang.InOutParameter
 * @see org.junit.Test
 * @since GemFire 6.8
 */
public class InOutParameterJUnitTest {
    @Test
    public void testEquals() {
        Assert.assertEquals(new InOutParameter<Object>(null), new InOutParameter<Object>(null));
        Assert.assertEquals(new InOutParameter<Object>(null), null);
        Assert.assertEquals(new InOutParameter<Object>("test"), new InOutParameter<Object>("test"));
        Assert.assertEquals(new InOutParameter<Object>("test"), "test");
        Assert.assertEquals(new InOutParameter<Object>(Math.PI), new InOutParameter<Object>(Math.PI));
        Assert.assertEquals(new InOutParameter<Object>(Math.PI), Math.PI);
        Assert.assertEquals(new InOutParameter<Object>(true), new InOutParameter<Object>(true));
        Assert.assertEquals(new InOutParameter<Object>(true), true);
    }

    @Test
    public void testNotEqual() {
        Assert.assertFalse(new InOutParameter<Object>("null").equals(new InOutParameter<Object>(null)));
        Assert.assertFalse(new InOutParameter<Object>(Math.PI).equals(3.14159));
        Assert.assertFalse(new InOutParameter<Object>("test").equals("TEST"));
    }

    @Test
    public void testHashCode() {
        Assert.assertEquals(0, new InOutParameter<Object>(null).hashCode());
        Assert.assertEquals("test".hashCode(), new InOutParameter<Object>("test").hashCode());
        Assert.assertEquals(new Double(Math.PI).hashCode(), new InOutParameter<Object>(Math.PI).hashCode());
        Assert.assertEquals(Boolean.TRUE.hashCode(), new InOutParameter<Object>(true).hashCode());
    }

    @Test
    public void testToString() {
        Assert.assertEquals("null", new InOutParameter<Object>(null).toString());
        Assert.assertEquals("null", new InOutParameter<Object>("null").toString());
        Assert.assertEquals("test", new InOutParameter<Object>("test").toString());
        Assert.assertEquals(String.valueOf(Math.PI), new InOutParameter<Object>(Math.PI).toString());
        Assert.assertEquals("true", new InOutParameter<Object>(Boolean.TRUE).toString());
    }
}

