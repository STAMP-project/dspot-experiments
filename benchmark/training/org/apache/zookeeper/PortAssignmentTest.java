/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zookeeper;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(ZKParameterized.RunnerFactory.class)
public class PortAssignmentTest {
    private final String strProcessCount;

    private final String cmdLine;

    private final int expectedMinimumPort;

    private final int expectedMaximumPort;

    public PortAssignmentTest(String strProcessCount, String cmdLine, int expectedMinimumPort, int expectedMaximumPort) {
        this.strProcessCount = strProcessCount;
        this.cmdLine = cmdLine;
        this.expectedMinimumPort = expectedMinimumPort;
        this.expectedMaximumPort = expectedMaximumPort;
    }

    @Test
    public void testSetupPortRange() {
        PortAssignment.PortRange portRange = PortAssignment.setupPortRange(strProcessCount, cmdLine);
        Assert.assertEquals(buildAssertionMessage("minimum"), expectedMinimumPort, portRange.getMinimum());
        Assert.assertEquals(buildAssertionMessage("maximum"), expectedMaximumPort, portRange.getMaximum());
    }
}

