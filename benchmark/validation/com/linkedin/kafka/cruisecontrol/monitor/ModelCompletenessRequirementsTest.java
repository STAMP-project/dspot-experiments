/**
 * Copyright 2017 LinkedIn Corp. Licensed under the BSD 2-Clause License (the "License"). See License in the project root for license information.
 */
package com.linkedin.kafka.cruisecontrol.monitor;


import org.junit.Assert;
import org.junit.Test;


public class ModelCompletenessRequirementsTest {
    @Test
    public void testCombine() {
        ModelCompletenessRequirements r1 = new ModelCompletenessRequirements(1, 0.5, true);
        ModelCompletenessRequirements r2 = new ModelCompletenessRequirements(5, 0.2, false);
        ModelCompletenessRequirements combined = r1.stronger(r2);
        Assert.assertEquals(5, combined.minRequiredNumWindows());
        Assert.assertEquals(0.5, combined.minMonitoredPartitionsPercentage(), 0.0);
        Assert.assertEquals(true, combined.includeAllTopics());
    }
}

