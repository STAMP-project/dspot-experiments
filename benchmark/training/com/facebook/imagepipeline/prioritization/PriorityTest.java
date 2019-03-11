/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.prioritization;


import com.facebook.imagepipeline.common.Priority;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for Priority enum
 */
public class PriorityTest {
    @Test
    public void testGetHigherPriority() throws Exception {
        Assert.assertEquals(HIGH, Priority.getHigherPriority(null, HIGH));
        Assert.assertEquals(HIGH, Priority.getHigherPriority(LOW, HIGH));
        Assert.assertEquals(HIGH, Priority.getHigherPriority(MEDIUM, HIGH));
        Assert.assertEquals(HIGH, Priority.getHigherPriority(HIGH, HIGH));
        Assert.assertEquals(HIGH, Priority.getHigherPriority(HIGH, MEDIUM));
        Assert.assertEquals(HIGH, Priority.getHigherPriority(HIGH, LOW));
        Assert.assertEquals(HIGH, Priority.getHigherPriority(HIGH, null));
        Assert.assertEquals(MEDIUM, Priority.getHigherPriority(null, MEDIUM));
        Assert.assertEquals(MEDIUM, Priority.getHigherPriority(LOW, MEDIUM));
        Assert.assertEquals(MEDIUM, Priority.getHigherPriority(MEDIUM, MEDIUM));
        Assert.assertEquals(MEDIUM, Priority.getHigherPriority(MEDIUM, LOW));
        Assert.assertEquals(MEDIUM, Priority.getHigherPriority(MEDIUM, null));
        Assert.assertEquals(LOW, Priority.getHigherPriority(null, LOW));
        Assert.assertEquals(LOW, Priority.getHigherPriority(LOW, LOW));
        Assert.assertEquals(LOW, Priority.getHigherPriority(LOW, null));
        Assert.assertEquals(null, Priority.getHigherPriority(null, null));
    }
}

