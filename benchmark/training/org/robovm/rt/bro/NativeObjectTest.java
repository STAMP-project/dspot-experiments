/**
 * Copyright (C) 2014 RoboVM AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.robovm.rt.bro;


import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link NativeObject}.
 */
public class NativeObjectTest {
    static class A extends NativeObject {
        A(long h) {
            setHandle(h);
        }
    }

    static class B extends NativeObject {
        B(long h) {
            setHandle(h);
        }
    }

    static class C extends NativeObjectTest.B {
        C(long h) {
            super(h);
        }
    }

    @Test
    public void testAs() {
        NativeObjectTest.A a = new NativeObjectTest.A(123);
        NativeObjectTest.B b = as(NativeObjectTest.B.class);
        Assert.assertEquals(NativeObjectTest.B.class, b.getClass());
        Assert.assertEquals(getHandle(), getHandle());
        NativeObjectTest.C c = b.as(NativeObjectTest.C.class);
        Assert.assertEquals(NativeObjectTest.C.class, c.getClass());
        Assert.assertEquals(getHandle(), getHandle());
        Assert.assertTrue((c == (c.as(NativeObjectTest.C.class))));
        Assert.assertTrue((c == (c.as(NativeObjectTest.B.class))));
    }
}

