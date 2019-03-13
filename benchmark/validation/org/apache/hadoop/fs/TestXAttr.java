/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.fs;


import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for <code>XAttr</code> objects.
 */
public class TestXAttr {
    private static XAttr XATTR;

    private static XAttr XATTR1;

    private static XAttr XATTR2;

    private static XAttr XATTR3;

    private static XAttr XATTR4;

    private static XAttr XATTR5;

    @Test
    public void testXAttrEquals() {
        Assert.assertNotSame(TestXAttr.XATTR1, TestXAttr.XATTR2);
        Assert.assertNotSame(TestXAttr.XATTR2, TestXAttr.XATTR3);
        Assert.assertNotSame(TestXAttr.XATTR3, TestXAttr.XATTR4);
        Assert.assertNotSame(TestXAttr.XATTR4, TestXAttr.XATTR5);
        Assert.assertEquals(TestXAttr.XATTR, TestXAttr.XATTR1);
        Assert.assertEquals(TestXAttr.XATTR1, TestXAttr.XATTR1);
        Assert.assertEquals(TestXAttr.XATTR2, TestXAttr.XATTR2);
        Assert.assertEquals(TestXAttr.XATTR3, TestXAttr.XATTR3);
        Assert.assertEquals(TestXAttr.XATTR4, TestXAttr.XATTR4);
        Assert.assertEquals(TestXAttr.XATTR5, TestXAttr.XATTR5);
        Assert.assertFalse(TestXAttr.XATTR1.equals(TestXAttr.XATTR2));
        Assert.assertFalse(TestXAttr.XATTR2.equals(TestXAttr.XATTR3));
        Assert.assertFalse(TestXAttr.XATTR3.equals(TestXAttr.XATTR4));
        Assert.assertFalse(TestXAttr.XATTR4.equals(TestXAttr.XATTR5));
    }

    @Test
    public void testXAttrHashCode() {
        Assert.assertEquals(TestXAttr.XATTR.hashCode(), TestXAttr.XATTR1.hashCode());
        Assert.assertFalse(((TestXAttr.XATTR1.hashCode()) == (TestXAttr.XATTR2.hashCode())));
        Assert.assertFalse(((TestXAttr.XATTR2.hashCode()) == (TestXAttr.XATTR3.hashCode())));
        Assert.assertFalse(((TestXAttr.XATTR3.hashCode()) == (TestXAttr.XATTR4.hashCode())));
        Assert.assertFalse(((TestXAttr.XATTR4.hashCode()) == (TestXAttr.XATTR5.hashCode())));
    }
}

