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
package org.flowable.engine.test.logging.mdc;


import java.util.List;
import org.apache.log4j.Appender;
import org.flowable.engine.impl.test.PluggableFlowableTestCase;
import org.flowable.engine.test.Deployment;
import org.junit.jupiter.api.Test;


public class MDCLoggingTest extends PluggableFlowableTestCase {
    MemoryLogAppender console = new MemoryLogAppender();

    List<Appender> appenders;

    @Test
    @Deployment
    public void testLogger() {
        setCustomLogger();
        try {
            runtimeService.startProcessInstanceByKey("testLoggerProcess");
            fail("Expected exception");
        } catch (Exception e) {
            // expected exception
        }
        String messages = console.toString();
        assertTrue(messages.contains(("ProcessDefinitionId=" + (TestService.processDefinitionId))));
        assertTrue(messages.contains(("executionId=" + (TestService.executionId))));
        assertTrue(messages.contains(("mdcProcessInstanceID=" + (TestService.processInstanceId))));
        assertTrue(messages.contains(("mdcBusinessKey=" + ((TestService.businessKey) == null ? "" : TestService.businessKey))));
        console.clear();
        restoreLoggers();
        try {
            runtimeService.startProcessInstanceByKey("testLoggerProcess");
            fail("Expected exception");
        } catch (Exception e) {
            // expected exception
        }
        assertFalse(console.toString().contains(("ProcessDefinitionId=" + (TestService.processDefinitionId))));
    }
}

