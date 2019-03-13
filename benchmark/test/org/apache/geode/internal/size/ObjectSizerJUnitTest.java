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
package org.apache.geode.internal.size;


import org.junit.Assert;
import org.junit.Test;


public class ObjectSizerJUnitTest {
    @Test
    public void test() throws Exception {
        Assert.assertEquals(SizeTestUtil.roundup(SizeTestUtil.OBJECT_SIZE), ObjectGraphSizer.size(new Object()));
        Assert.assertEquals(SizeTestUtil.roundup(((SizeTestUtil.OBJECT_SIZE) + 4)), ObjectGraphSizer.size(new ObjectSizerJUnitTest.TestObject1()));
        Assert.assertEquals(SizeTestUtil.roundup(((SizeTestUtil.OBJECT_SIZE) + 4)), ObjectGraphSizer.size(new ObjectSizerJUnitTest.TestObject2()));
        Assert.assertEquals(SizeTestUtil.roundup(SizeTestUtil.OBJECT_SIZE), ObjectGraphSizer.size(new ObjectSizerJUnitTest.TestObject3()));
        Assert.assertEquals(SizeTestUtil.roundup((((SizeTestUtil.OBJECT_SIZE) * 2) + (SizeTestUtil.REFERENCE_SIZE))), ObjectGraphSizer.size(new ObjectSizerJUnitTest.TestObject3(), true));
        Assert.assertEquals(SizeTestUtil.roundup(((SizeTestUtil.OBJECT_SIZE) + (SizeTestUtil.REFERENCE_SIZE))), ObjectGraphSizer.size(new ObjectSizerJUnitTest.TestObject4()));
        Assert.assertEquals(((SizeTestUtil.roundup(((SizeTestUtil.OBJECT_SIZE) + (SizeTestUtil.REFERENCE_SIZE)))) + (SizeTestUtil.roundup(((SizeTestUtil.OBJECT_SIZE) + 4)))), ObjectGraphSizer.size(new ObjectSizerJUnitTest.TestObject5()));
        Assert.assertEquals((((SizeTestUtil.roundup(((SizeTestUtil.OBJECT_SIZE) + (SizeTestUtil.REFERENCE_SIZE)))) + (SizeTestUtil.roundup((((SizeTestUtil.OBJECT_SIZE) + ((SizeTestUtil.REFERENCE_SIZE) * 4)) + 4)))) + (SizeTestUtil.roundup(((SizeTestUtil.OBJECT_SIZE) + 4)))), ObjectGraphSizer.size(new ObjectSizerJUnitTest.TestObject6()));
        Assert.assertEquals(SizeTestUtil.roundup(((SizeTestUtil.OBJECT_SIZE) + 7)), ObjectGraphSizer.size(new ObjectSizerJUnitTest.TestObject7()));
    }

    private static class TestObject1 {
        int a;
    }

    private static class TestObject2 {
        int a;
    }

    private static class TestObject3 {
        static ObjectSizerJUnitTest.TestObject1 a = new ObjectSizerJUnitTest.TestObject1();
    }

    private static class TestObject4 {
        ObjectSizerJUnitTest.TestObject1 object = null;
    }

    private static class TestObject5 {
        ObjectSizerJUnitTest.TestObject1 object = new ObjectSizerJUnitTest.TestObject1();
    }

    private static class TestObject6 {
        ObjectSizerJUnitTest.TestObject1[] array = new ObjectSizerJUnitTest.TestObject1[4];

        public TestObject6() {
            array[3] = new ObjectSizerJUnitTest.TestObject1();
            array[2] = array[3];
        }
    }

    private static class TestObject7 {
        byte b1;

        byte b2;

        byte b3;

        byte b4;

        byte b5;

        byte b6;

        byte b7;
    }
}

