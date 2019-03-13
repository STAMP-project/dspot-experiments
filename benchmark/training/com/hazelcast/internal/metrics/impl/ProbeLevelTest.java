/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.internal.metrics.impl;


import com.hazelcast.internal.metrics.ProbeLevel;
import com.hazelcast.logging.ILogger;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class ProbeLevelTest extends HazelcastTestSupport {
    private ILogger logger;

    @Test
    public void test() {
        assertProbeExist(ProbeLevel.MANDATORY, ProbeLevel.MANDATORY);
        assertNotProbeExist(ProbeLevel.INFO, ProbeLevel.MANDATORY);
        assertNotProbeExist(ProbeLevel.DEBUG, ProbeLevel.MANDATORY);
        assertProbeExist(ProbeLevel.MANDATORY, ProbeLevel.INFO);
        assertProbeExist(ProbeLevel.INFO, ProbeLevel.INFO);
        assertNotProbeExist(ProbeLevel.DEBUG, ProbeLevel.INFO);
        assertProbeExist(ProbeLevel.MANDATORY, ProbeLevel.DEBUG);
        assertProbeExist(ProbeLevel.INFO, ProbeLevel.DEBUG);
        assertProbeExist(ProbeLevel.DEBUG, ProbeLevel.DEBUG);
    }
}

