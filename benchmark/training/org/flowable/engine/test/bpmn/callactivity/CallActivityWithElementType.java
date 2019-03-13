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
package org.flowable.engine.test.bpmn.callactivity;


import java.io.IOException;
import java.util.Collections;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.engine.impl.test.PluggableFlowableTestCase;
import org.flowable.engine.test.Deployment;
import org.junit.jupiter.api.Test;


/**
 * Tests {@link org.flowable.engine.impl.bpmn.behavior.CallActivityBehavior} with calledElementType id
 */
public class CallActivityWithElementType extends PluggableFlowableTestCase {
    @Test
    @Deployment(resources = "org/flowable/engine/test/bpmn/callactivity/simpleSubProcess.bpmn20.xml")
    public void testCallSimpleSubProcessByKey() throws IOException {
        assertThatSubProcessIsCalled(createCallProcess("key", "simpleSubProcess"), Collections.emptyMap());
    }

    @Test
    @Deployment(resources = "org/flowable/engine/test/bpmn/callactivity/simpleSubProcess.bpmn20.xml")
    public void testCallSimpleSubProcessById() throws IOException {
        String subProcessDefinitionId = repositoryService.createProcessDefinitionQuery().processDefinitionKey("simpleSubProcess").singleResult().getId();
        assertThatSubProcessIsCalled(createCallProcess("id", subProcessDefinitionId), Collections.emptyMap());
    }

    @Test
    @Deployment(resources = "org/flowable/engine/test/bpmn/callactivity/simpleSubProcess.bpmn20.xml")
    public void testCallSimpleSubProcessByIdExpression() throws IOException {
        String subProcessDefinitionId = repositoryService.createProcessDefinitionQuery().processDefinitionKey("simpleSubProcess").singleResult().getId();
        assertThatSubProcessIsCalled(createCallProcess("id", "${subProcessDefinitionId}"), Collections.singletonMap("subProcessDefinitionId", subProcessDefinitionId));
    }

    @Test
    @Deployment(resources = "org/flowable/engine/test/bpmn/callactivity/simpleSubProcess.bpmn20.xml")
    public void testCallSimpleSubProcessByKeyExpression() throws IOException {
        repositoryService.createProcessDefinitionQuery().processDefinitionKey("simpleSubProcess").singleResult().getId();
        assertThatSubProcessIsCalled(createCallProcess("key", "${subProcessDefinitionKey}"), Collections.singletonMap("subProcessDefinitionKey", "simpleSubProcess"));
    }

    @Test
    @Deployment(resources = "org/flowable/engine/test/bpmn/callactivity/simpleSubProcess.bpmn20.xml")
    public void testCallSimpleSubProcessWithUnrecognizedElementType() throws IOException {
        try {
            assertThatSubProcessIsCalled(createCallProcess("unrecognizedElementType", "simpleSubProcess"), Collections.singletonMap("subProcessDefinitionKey", "simpleSubProcess"));
            fail("Flowable exception expected");
        } catch (FlowableException e) {
            assertThat(e).hasMessage("Unrecognized calledElementType [unrecognizedElementType]");
        }
    }
}

