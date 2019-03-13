/**
 * Copyright 2017 LinkedIn Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package azkaban.flowtrigger;


import azkaban.flowtrigger.database.FlowTriggerInstanceLoader;
import org.junit.Test;
import org.mockito.Mockito;

import static CancellationCause.NONE;
import static Status.RUNNING;


public class DependencyInstanceProcessorTest {
    private static FlowTriggerInstanceLoader triggerInstLoader;

    private static DependencyInstanceProcessor processor;

    @Test
    public void testStatusUpdate() {
        final DependencyInstance depInst = new DependencyInstance("dep1", System.currentTimeMillis(), 0, null, RUNNING, NONE);
        DependencyInstanceProcessorTest.processor.processStatusUpdate(depInst);
        Mockito.verify(DependencyInstanceProcessorTest.triggerInstLoader).updateDependencyExecutionStatus(depInst);
    }
}

