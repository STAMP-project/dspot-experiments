/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
package org.nd4j.linalg.schedule;


import DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import MapperFeature.SORT_PROPERTIES_ALPHABETICALLY;
import SerializationFeature.FAIL_ON_EMPTY_BEANS;
import SerializationFeature.INDENT_OUTPUT;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.shade.jackson.databind.ObjectMapper;

import static ScheduleType.ITERATION;


public class TestSchedules {
    @Test
    public void testJson() throws Exception {
        ObjectMapper om = new ObjectMapper();
        om.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
        om.configure(FAIL_ON_EMPTY_BEANS, false);
        om.configure(SORT_PROPERTIES_ALPHABETICALLY, true);
        om.enable(INDENT_OUTPUT);
        ISchedule[] schedules = new ISchedule[]{ new ExponentialSchedule(ITERATION, 1.0, 0.5), new InverseSchedule(ITERATION, 1.0, 0.5, 2), add(10, 0.5).build(), new PolySchedule(ITERATION, 1.0, 2, 100), new SigmoidSchedule(ITERATION, 1.0, 0.5, 10), new StepSchedule(ITERATION, 1.0, 0.9, 100), new CycleSchedule(ITERATION, 1.5, 100) };
        for (ISchedule s : schedules) {
            String json = om.writeValueAsString(s);
            ISchedule fromJson = om.readValue(json, ISchedule.class);
            Assert.assertEquals(s, fromJson);
        }
    }

    @Test
    public void testScheduleValues() {
        double lr = 0.8;
        double decay = 0.9;
        double power = 2;
        double gamma = 0.5;
        int step = 20;
        for (ScheduleType st : ScheduleType.values()) {
            ISchedule[] schedules = new ISchedule[]{ new ExponentialSchedule(st, lr, gamma), new InverseSchedule(st, lr, gamma, power), new PolySchedule(st, lr, power, step), new SigmoidSchedule(st, lr, gamma, step), new StepSchedule(st, lr, decay, step) };
            for (ISchedule s : schedules) {
                for (int i = 0; i < 9; i++) {
                    int epoch = i / 3;
                    int x;
                    if (st == (ITERATION)) {
                        x = i;
                    } else {
                        x = epoch;
                    }
                    double now = s.valueAt(i, epoch);
                    double e;
                    if (s instanceof ExponentialSchedule) {
                        e = TestSchedules.calcExponentialDecay(lr, gamma, x);
                    } else
                        if (s instanceof InverseSchedule) {
                            e = TestSchedules.calcInverseDecay(lr, gamma, x, power);
                        } else
                            if (s instanceof PolySchedule) {
                                e = TestSchedules.calcPolyDecay(lr, x, power, step);
                            } else
                                if (s instanceof SigmoidSchedule) {
                                    e = TestSchedules.calcSigmoidDecay(lr, gamma, x, step);
                                } else
                                    if (s instanceof StepSchedule) {
                                        e = TestSchedules.calcStepDecay(lr, decay, x, step);
                                    } else {
                                        throw new RuntimeException();
                                    }




                    Assert.assertEquals((((s.toString()) + ", ") + st), e, now, 1.0E-6);
                }
            }
        }
    }

    @Test
    public void testMapSchedule() {
        ISchedule schedule = add(5, 0.1).build();
        for (int i = 0; i < 10; i++) {
            if (i < 5) {
                Assert.assertEquals(0.5, schedule.valueAt(i, 0), 1.0E-6);
            } else {
                Assert.assertEquals(0.1, schedule.valueAt(i, 0), 1.0E-6);
            }
        }
    }

    @Test
    public void testCycleSchedule() {
        ISchedule schedule = new CycleSchedule(ITERATION, 1.5, 100);
        Assert.assertEquals(0.15, schedule.valueAt(0, 0), 1.0E-6);
        Assert.assertEquals(1.5, schedule.valueAt(45, 0), 1.0E-6);
        Assert.assertEquals(0.15, schedule.valueAt(90, 0), 1.0E-6);
        Assert.assertEquals(0.015, schedule.valueAt(91, 0), 1.0E-6);
        schedule = new CycleSchedule(ITERATION, 0.95, 0.85, 100, 10, 1);
        Assert.assertEquals(0.95, schedule.valueAt(0, 0), 1.0E-6);
        Assert.assertEquals(0.85, schedule.valueAt(45, 0), 1.0E-6);
        Assert.assertEquals(0.95, schedule.valueAt(90, 0), 1.0E-6);
        Assert.assertEquals(0.95, schedule.valueAt(91, 0), 1.0E-6);
    }
}

