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
package org.apache.hadoop.hdfs.server.namenode;


import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.fs.XAttr;
import org.apache.hadoop.hdfs.XAttrHelper;
import org.junit.Assert;
import org.junit.Test;


public class TestXAttrFeature {
    static final String name1 = "system.a1";

    static final byte[] value1 = new byte[]{ 49, 50, 51 };

    static final String name2 = "security.a2";

    static final byte[] value2 = new byte[]{ 55, 56, 57 };

    static final String name3 = "trusted.a3";

    static final String name4 = "user.a4";

    static final byte[] value4 = new byte[]{ 1, 2, 3 };

    static final String name5 = "user.a5";

    static final byte[] value5 = TestXAttrFeature.randomBytes(2000);

    static final String name6 = "user.a6";

    static final byte[] value6 = TestXAttrFeature.randomBytes(1800);

    static final String name7 = "raw.a7";

    static final byte[] value7 = new byte[]{ 17, 18, 19 };

    static final String name8 = "user.a8";

    static final String bigXattrKey = "user.big.xattr.key";

    static final byte[] bigXattrValue = new byte[128];

    static {
        for (int i = 0; i < (TestXAttrFeature.bigXattrValue.length); i++) {
            TestXAttrFeature.bigXattrValue[i] = ((byte) (i & 255));
        }
    }

    @Test
    public void testXAttrFeature() throws Exception {
        List<XAttr> xAttrs = new ArrayList<>();
        XAttrFeature feature = new XAttrFeature(xAttrs);
        // no XAttrs in the feature
        Assert.assertTrue(feature.getXAttrs().isEmpty());
        // one XAttr in the feature
        XAttr a1 = XAttrHelper.buildXAttr(TestXAttrFeature.name1, TestXAttrFeature.value1);
        xAttrs.add(a1);
        feature = new XAttrFeature(xAttrs);
        XAttr r1 = feature.getXAttr(TestXAttrFeature.name1);
        Assert.assertTrue(a1.equals(r1));
        Assert.assertEquals(feature.getXAttrs().size(), 1);
        // more XAttrs in the feature
        XAttr a2 = XAttrHelper.buildXAttr(TestXAttrFeature.name2, TestXAttrFeature.value2);
        XAttr a3 = XAttrHelper.buildXAttr(TestXAttrFeature.name3);
        XAttr a4 = XAttrHelper.buildXAttr(TestXAttrFeature.name4, TestXAttrFeature.value4);
        XAttr a5 = XAttrHelper.buildXAttr(TestXAttrFeature.name5, TestXAttrFeature.value5);
        XAttr a6 = XAttrHelper.buildXAttr(TestXAttrFeature.name6, TestXAttrFeature.value6);
        XAttr a7 = XAttrHelper.buildXAttr(TestXAttrFeature.name7, TestXAttrFeature.value7);
        XAttr bigXattr = XAttrHelper.buildXAttr(TestXAttrFeature.bigXattrKey, TestXAttrFeature.bigXattrValue);
        xAttrs.add(a2);
        xAttrs.add(a3);
        xAttrs.add(a4);
        xAttrs.add(a5);
        xAttrs.add(a6);
        xAttrs.add(a7);
        xAttrs.add(bigXattr);
        feature = new XAttrFeature(xAttrs);
        XAttr r2 = feature.getXAttr(TestXAttrFeature.name2);
        Assert.assertTrue(a2.equals(r2));
        XAttr r3 = feature.getXAttr(TestXAttrFeature.name3);
        Assert.assertTrue(a3.equals(r3));
        XAttr r4 = feature.getXAttr(TestXAttrFeature.name4);
        Assert.assertTrue(a4.equals(r4));
        XAttr r5 = feature.getXAttr(TestXAttrFeature.name5);
        Assert.assertTrue(a5.equals(r5));
        XAttr r6 = feature.getXAttr(TestXAttrFeature.name6);
        Assert.assertTrue(a6.equals(r6));
        XAttr r7 = feature.getXAttr(TestXAttrFeature.name7);
        Assert.assertTrue(a7.equals(r7));
        XAttr rBigXattr = feature.getXAttr(TestXAttrFeature.bigXattrKey);
        Assert.assertTrue(bigXattr.equals(rBigXattr));
        List<XAttr> rs = feature.getXAttrs();
        Assert.assertEquals(rs.size(), xAttrs.size());
        for (int i = 0; i < (rs.size()); i++) {
            Assert.assertTrue(xAttrs.contains(rs.get(i)));
        }
        // get non-exist XAttr in the feature
        XAttr r8 = feature.getXAttr(TestXAttrFeature.name8);
        Assert.assertTrue((r8 == null));
    }
}

