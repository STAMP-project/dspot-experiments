/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.engine.test.cfg.taskcount;


import org.flowable.engine.impl.test.ResourceFlowableTestCase;
import org.flowable.engine.test.Deployment;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ChangeTaskCountConfigAndRebootEngineTest extends ResourceFlowableTestCase {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeTaskCountConfigAndRebootEngineTest.class);

    protected boolean newTaskRelationshipCountValue;

    public ChangeTaskCountConfigAndRebootEngineTest() {
        // Simply boot up the same engine with the usual config file
        // This way, database tests work. the only thing we have to make
        // sure is to give the process engine a name so it is
        // registered and unregistered separately.
        super("flowable.cfg.xml", ChangeTaskCountConfigAndRebootEngineTest.class.getName());
    }

    @Test
    @Deployment
    public void testChangeTaskCountSettingAndRebootengine() {
        rebootFlagNotChanged(true);
        rebootFlagNotChanged(false);
        checkEnableFlagBetweenTasks();
        checkDisableFlagBetweenTasks();
    }
}

