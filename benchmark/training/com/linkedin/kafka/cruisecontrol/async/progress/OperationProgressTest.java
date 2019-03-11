/**
 * Copyright 2017 LinkedIn Corp. Licensed under the BSD 2-Clause License (the "License"). See License in the project root for license information.
 */
package com.linkedin.kafka.cruisecontrol.async.progress;


import org.junit.Assert;
import org.junit.Test;


public class OperationProgressTest {
    @Test
    public void testRefer() {
        OperationProgress progress1 = new OperationProgress();
        progress1.addStep(new Pending());
        OperationProgress progress2 = new OperationProgress();
        progress2.addStep(new WaitingForClusterModel());
        Assert.assertTrue(((progress1.progress().get(0)) instanceof Pending));
        progress1.refer(progress2);
        Assert.assertTrue(((progress1.progress().get(0)) instanceof WaitingForClusterModel));
        Assert.assertEquals(progress1.progress(), progress2.progress());
    }

    @Test
    public void testImmutableAfterRefer() {
        OperationProgress progress1 = new OperationProgress();
        OperationProgress progress2 = new OperationProgress();
        progress1.refer(progress2);
        try {
            progress1.addStep(new Pending());
            Assert.fail("Should have thrown IllegalStateException.");
        } catch (IllegalStateException ise) {
            // let it go.
        }
        try {
            progress1.refer(progress2);
            Assert.fail("Should have thrown IllegalStateException.");
        } catch (IllegalStateException ise) {
            // let it go.
        }
        try {
            progress1.clear();
            // After calling clear(), OperationProgress should be able to refer to another OperationProgress.
            progress1.refer(progress2);
        } catch (Exception e) {
            Assert.fail("Should not throw any Exception.");
        }
    }
}

