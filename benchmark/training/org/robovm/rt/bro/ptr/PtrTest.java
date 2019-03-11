/**
 * Copyright (C) 2012 RoboVM AB
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
package org.robovm.rt.bro.ptr;


import org.junit.Assert;
import org.junit.Test;
import org.robovm.rt.bro.Struct;
import org.robovm.rt.bro.annotation.StructMember;


/**
 * Tests {@link Ptr}.
 */
public class PtrTest {
    public static final class Point extends Struct<PtrTest.Point> {
        @StructMember(0)
        public native int x();

        @StructMember(0)
        public native PtrTest.Point x(int x);

        @StructMember(1)
        public native int y();

        @StructMember(1)
        public native PtrTest.Point y(int y);
    }

    public static final class PointPtr extends Ptr<PtrTest.Point, PtrTest.PointPtr> {}

    public static final class PointPtrPtr extends Ptr<PtrTest.PointPtr, PtrTest.PointPtrPtr> {}

    @Test
    public void testNext() throws Exception {
        PtrTest.Point p1 = new PtrTest.Point().x(1).y(2);
        PtrTest.Point p2 = new PtrTest.Point().x(3).y(4);
        PtrTest.Point p3 = new PtrTest.Point().x(5).y(6);
        PtrTest.PointPtr ptr = Struct.allocate(PtrTest.PointPtr.class, 3);
        Assert.assertNull(get());
        Assert.assertNull(get());
        Assert.assertNull(get());
        set(p1);
        set(p2);
        set(p3);
        Assert.assertEquals(p1, get());
        Assert.assertEquals(p2, get());
        Assert.assertEquals(p3, get());
        PtrTest.PointPtr[] ptrs = toArray(3);
        Assert.assertEquals(p1, get());
        Assert.assertEquals(p2, get());
        Assert.assertEquals(p3, get());
    }

    @Test
    public void testCopy() throws Exception {
        PtrTest.PointPtr ptr1 = Struct.allocate(PtrTest.PointPtr.class).set(new PtrTest.Point().x(1).y(2));
        PtrTest.PointPtr ptr2 = copy();
        Assert.assertTrue(((getHandle()) != (getHandle())));
        Assert.assertEquals(get(), get());
    }

    @Test
    public void testCopyWithMalloc() throws Exception {
        PtrTest.PointPtr ptr1 = Struct.allocate(PtrTest.PointPtr.class).set(new PtrTest.Point().x(1).y(2));
        PtrTest.PointPtr ptr2 = copyWithMalloc();
        try {
            Assert.assertTrue(((getHandle()) != (getHandle())));
            Assert.assertEquals(get(), get());
        } finally {
            free();
        }
    }
}

