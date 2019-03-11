/**
 * Copyright 2016 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.compute.deprecated;


import SchedulingOptions.Maintenance.MIGRATE;
import SchedulingOptions.Maintenance.TERMINATE;
import org.junit.Assert;
import org.junit.Test;


public class SchedulingOptionsTest {
    private static final SchedulingOptions SCHEDULING_OPTIONS = SchedulingOptions.preemptible();

    @Test
    public void testFactoryMethods() {
        Assert.assertTrue(SchedulingOptionsTest.SCHEDULING_OPTIONS.isPreemptible());
        Assert.assertFalse(SchedulingOptionsTest.SCHEDULING_OPTIONS.automaticRestart());
        Assert.assertEquals(TERMINATE, SchedulingOptionsTest.SCHEDULING_OPTIONS.getMaintenance());
        SchedulingOptions schedulingOptions = SchedulingOptions.standard(true, MIGRATE);
        Assert.assertFalse(schedulingOptions.isPreemptible());
        Assert.assertTrue(schedulingOptions.automaticRestart());
        Assert.assertEquals(MIGRATE, schedulingOptions.getMaintenance());
    }

    @Test
    public void testToAndFromPb() {
        compareSchedulingOptions(SchedulingOptionsTest.SCHEDULING_OPTIONS, SchedulingOptions.fromPb(SchedulingOptionsTest.SCHEDULING_OPTIONS.toPb()));
        SchedulingOptions schedulingOptions = SchedulingOptions.standard(true, MIGRATE);
        compareSchedulingOptions(schedulingOptions, SchedulingOptions.fromPb(schedulingOptions.toPb()));
    }
}

