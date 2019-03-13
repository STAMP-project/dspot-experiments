/**
 * Copyright 2016 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.buffer;


import io.netty.util.IllegalReferenceCountException;
import org.junit.Assert;
import org.junit.Test;


public class AbstractReferenceCountedByteBufTest {
    @Test(expected = IllegalReferenceCountException.class)
    public void testRetainOverflow() {
        AbstractReferenceCountedByteBuf referenceCounted = AbstractReferenceCountedByteBufTest.newReferenceCounted();
        referenceCounted.setRefCnt(Integer.MAX_VALUE);
        Assert.assertEquals(Integer.MAX_VALUE, referenceCounted.refCnt());
        referenceCounted.retain();
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testRetainOverflow2() {
        AbstractReferenceCountedByteBuf referenceCounted = AbstractReferenceCountedByteBufTest.newReferenceCounted();
        Assert.assertEquals(1, referenceCounted.refCnt());
        referenceCounted.retain(Integer.MAX_VALUE);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testReleaseOverflow() {
        AbstractReferenceCountedByteBuf referenceCounted = AbstractReferenceCountedByteBufTest.newReferenceCounted();
        referenceCounted.setRefCnt(0);
        Assert.assertEquals(0, referenceCounted.refCnt());
        referenceCounted.release(Integer.MAX_VALUE);
    }

    @Test
    public void testReleaseErrorMessage() {
        AbstractReferenceCountedByteBuf referenceCounted = AbstractReferenceCountedByteBufTest.newReferenceCounted();
        Assert.assertTrue(referenceCounted.release());
        try {
            referenceCounted.release(1);
            Assert.fail("IllegalReferenceCountException didn't occur");
        } catch (IllegalReferenceCountException e) {
            Assert.assertEquals("refCnt: 0, decrement: 1", e.getMessage());
        }
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testRetainResurrect() {
        AbstractReferenceCountedByteBuf referenceCounted = AbstractReferenceCountedByteBufTest.newReferenceCounted();
        Assert.assertTrue(referenceCounted.release());
        Assert.assertEquals(0, referenceCounted.refCnt());
        referenceCounted.retain();
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testRetainResurrect2() {
        AbstractReferenceCountedByteBuf referenceCounted = AbstractReferenceCountedByteBufTest.newReferenceCounted();
        Assert.assertTrue(referenceCounted.release());
        Assert.assertEquals(0, referenceCounted.refCnt());
        referenceCounted.retain(2);
    }
}

