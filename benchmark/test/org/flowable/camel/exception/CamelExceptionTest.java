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
package org.flowable.camel.exception;


import org.apache.camel.CamelContext;
import org.flowable.camel.exception.tools.ExceptionServiceMock;
import org.flowable.camel.exception.tools.NoExceptionServiceMock;
import org.flowable.camel.exception.tools.ThrowBpmnExceptionBean;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.engine.ManagementService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.impl.test.JobTestHelper;
import org.flowable.engine.test.Deployment;
import org.flowable.job.api.Job;
import org.flowable.spring.impl.test.SpringFlowableTestCase;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static org.flowable.camel.exception.tools.ThrowBpmnExceptionBean.ExceptionType.BPMN_EXCEPTION;
import static org.flowable.camel.exception.tools.ThrowBpmnExceptionBean.ExceptionType.NON_BPMN_EXCEPTION;
import static org.flowable.camel.exception.tools.ThrowBpmnExceptionBean.ExceptionType.NO_EXCEPTION;


/**
 *
 *
 * @author Saeid Mirzaei
 */
@Tag("camel")
@ContextConfiguration("classpath:generic-camel-flowable-context.xml")
public class CamelExceptionTest extends SpringFlowableTestCase {
    @Autowired
    protected CamelContext camelContext;

    @Autowired
    protected RuntimeService runtimeService;

    @Autowired
    protected ManagementService managementService;

    // check happy path in synchronouse camel call
    @Test
    @Deployment(resources = { "org/flowable/camel/exception/bpmnExceptionInRouteSynchronous.bpmn20.xml" })
    public void testHappyPathSynchronous() {
        // Signal ThrowBpmnExceptionBean to throw no exception
        ThrowBpmnExceptionBean.setExceptionType(NO_EXCEPTION);
        runtimeService.startProcessInstanceByKey("exceptionInRouteSynchron");
        assertFalse(ExceptionServiceMock.isCalled());
        assertTrue(NoExceptionServiceMock.isCalled());
    }

    // Check Non BPMN error in synchronouse camel call
    @Test
    @Deployment(resources = { "org/flowable/camel/exception/bpmnExceptionInRouteSynchronous.bpmn20.xml" })
    public void testNonBpmnExceptionInCamel() {
        // Signal ThrowBpmnExceptionBean to throw a non BPMN Exception
        ThrowBpmnExceptionBean.setExceptionType(NON_BPMN_EXCEPTION);
        try {
            runtimeService.startProcessInstanceByKey("exceptionInRouteSynchron");
        } catch (FlowableException e) {
            assertEquals(Exception.class, e.getCause().getClass());
            assertEquals("arbitrary non bpmn exception", e.getCause().getMessage());
            assertFalse(ExceptionServiceMock.isCalled());
            assertFalse(NoExceptionServiceMock.isCalled());
            return;
        }
        fail("Activiti exception expected");
    }

    // check Bpmn Exception in synchronous camel call
    @Test
    @Deployment(resources = { "org/flowable/camel/exception/bpmnExceptionInRouteSynchronous.bpmn20.xml" })
    public void testBpmnExceptionInCamel() {
        // Signal ThrowBpmnExceptionBean to throw a BPMN Exception
        ThrowBpmnExceptionBean.setExceptionType(BPMN_EXCEPTION);
        try {
            runtimeService.startProcessInstanceByKey("exceptionInRouteSynchron");
        } catch (FlowableException e) {
            fail("The exception should be handled by camel. No exception expected.");
        }
        assertTrue(ExceptionServiceMock.isCalled());
        assertFalse(NoExceptionServiceMock.isCalled());
    }

    // check happy path in asynchronous camel call
    @Test
    @Deployment(resources = { "org/flowable/camel/exception/bpmnExceptionInRouteAsynchronous.bpmn20.xml" })
    public void testHappyPathAsynchronous() {
        // Signal ThrowBpmnExceptionBean to throw no exception
        ThrowBpmnExceptionBean.setExceptionType(NO_EXCEPTION);
        runtimeService.startProcessInstanceByKey("exceptionInRouteSynchron");
        Job job = managementService.createJobQuery().singleResult();
        managementService.executeJob(job.getId());
        assertFalse(JobTestHelper.areJobsAvailable(managementService));
        assertFalse(ExceptionServiceMock.isCalled());
        assertTrue(NoExceptionServiceMock.isCalled());
    }

    // check non bpmn exception in asynchronouse camel call
    @Test
    @Deployment(resources = { "org/flowable/camel/exception/bpmnExceptionInRouteAsynchronous.bpmn20.xml" })
    public void testNonBpmnPathAsynchronous() {
        // Signal ThrowBpmnExceptionBean to throw non bpmn exception
        ThrowBpmnExceptionBean.setExceptionType(NON_BPMN_EXCEPTION);
        runtimeService.startProcessInstanceByKey("exceptionInRouteSynchron");
        assertTrue(JobTestHelper.areJobsAvailable(managementService));
        Job job = managementService.createJobQuery().singleResult();
        try {
            managementService.executeJob(job.getId());
            fail();
        } catch (Exception e) {
            // expected
        }
        // The job is now a timer job, to be retried later
        job = managementService.createTimerJobQuery().singleResult();
        assertEquals("Unhandled exception on camel route", job.getExceptionMessage());
        assertFalse(ExceptionServiceMock.isCalled());
        assertFalse(NoExceptionServiceMock.isCalled());
    }
}

