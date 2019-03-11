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
package org.flowable.crystalball.simulator.impl.playback;


import SimpleSimulationRun.Builder;
import org.flowable.common.engine.impl.runtime.Clock;
import org.flowable.crystalball.simulator.SimpleSimulationRun;
import org.flowable.crystalball.simulator.delegate.event.impl.InMemoryRecordFlowableEventListener;
import org.flowable.crystalball.simulator.impl.SimulationProcessEngineFactory;
import org.flowable.crystalball.simulator.impl.clock.DefaultClockFactory;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.ProcessEngines;
import org.flowable.engine.impl.ProcessEngineImpl;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.variable.service.impl.el.NoExecutionVariableScope;
import org.junit.Test;


/**
 *
 *
 * @author martin.grofcik
 */
public class PlaybackRunTest {
    // deployment created
    private static final String DEPLOYMENT_CREATED_EVENT_TYPE = "DEPLOYMENT_CREATED_EVENT";

    private static final String DEPLOYMENT_RESOURCES_KEY = "deploymentResources";

    // Process instance start event
    private static final String PROCESS_INSTANCE_START_EVENT_TYPE = "PROCESS_INSTANCE_START";

    private static final String PROCESS_DEFINITION_ID_KEY = "processDefinitionId";

    private static final String VARIABLES_KEY = "variables";

    // User task completed event
    private static final String USER_TASK_COMPLETED_EVENT_TYPE = "USER_TASK_COMPLETED";

    private static final String SIMPLEST_PROCESS = "theSimplestProcess";

    private static final String BUSINESS_KEY = "testBusinessKey";

    private static final String TEST_VALUE = "TestValue";

    private static final String TEST_VARIABLE = "testVariable";

    protected InMemoryRecordFlowableEventListener listener = new InMemoryRecordFlowableEventListener(getTransformers());

    private static final String THE_SIMPLEST_PROCESS = "org/flowable/crystalball/simulator/impl/playback/PlaybackProcessStartTest.testDemo.bpmn20.xml";

    @Test
    public void testProcessInstanceStartEvents() throws Exception {
        recordEvents();
        final SimpleSimulationRun.Builder builder = new SimpleSimulationRun.Builder();
        // init simulation run
        Clock clock = new org.flowable.crystalball.simulator.impl.clock.ThreadLocalClock(new DefaultClockFactory());
        ProcessEngineConfigurationImpl config = ((ProcessEngineConfigurationImpl) (ProcessEngineConfiguration.createProcessEngineConfigurationFromResourceDefault()));
        config.setClock(clock);
        SimulationProcessEngineFactory simulationProcessEngineFactory = new SimulationProcessEngineFactory(config);
        final ProcessEngineImpl simProcessEngine = simulationProcessEngineFactory.getObject();
        builder.processEngine(simProcessEngine).eventCalendar(getObject()).eventHandlers(PlaybackRunTest.getHandlers());
        SimpleSimulationRun simRun = builder.build();
        simRun.execute(new NoExecutionVariableScope());
        checkStatus(simProcessEngine);
        simProcessEngine.getProcessEngineConfiguration().setDatabaseSchemaUpdate("create-drop");
        simProcessEngine.close();
        ProcessEngines.destroy();
    }
}

