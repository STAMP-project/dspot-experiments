/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.facebook.imagepipeline.memory;


import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/* Copyright (c) Facebook, Inc. and its affiliates.

This source code is licensed under the MIT license found in the
LICENSE file in the root directory of this source tree.
 */
@RunWith(RobolectricTestRunner.class)
public class BucketMapTest {
    @Test
    public void testLRU() {
        BucketMap<Object> map = new BucketMap();
        Object one = new Object();
        Object two = new Object();
        Object three = new Object();
        Object four = new Object();
        map.release(1, one);
        map.release(2, two);
        map.release(3, three);
        BucketMapTest.assertLinkedList(map.mHead, map.mTail, 3, 2, 1);
        map.removeFromEnd();
        map.removeFromEnd();
        map.removeFromEnd();
        map.release(1, one);
        map.release(3, three);
        map.release(2, two);
        BucketMapTest.assertLinkedList(map.mHead, map.mTail, 2, 3, 1);
        map.acquire(3);
        BucketMapTest.assertLinkedList(map.mHead, map.mTail, 3, 2, 1);
        map.release(4, four);
        BucketMapTest.assertLinkedList(map.mHead, map.mTail, 4, 3, 2, 1);
    }

    @Test
    public void testLRU2() {
        BucketMap<Object> map = new BucketMap();
        Object one = new Object();
        Object two = new Object();
        map.release(1, one);
        map.release(1, two);
        BucketMapTest.assertLinkedList(map.mHead, map.mTail, 1);
        Assert.assertEquals(2, map.valueCount());
        map.removeFromEnd();
        Assert.assertEquals(1, map.valueCount());
    }
}

