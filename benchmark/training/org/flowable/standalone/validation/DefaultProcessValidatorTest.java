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
package org.flowable.standalone.validation;


import Problems.ALL_PROCESS_DEFINITIONS_NOT_EXECUTABLE;
import Problems.ASSOCIATION_INVALID_SOURCE_REFERENCE;
import Problems.ASSOCIATION_INVALID_TARGET_REFERENCE;
import Problems.BOUNDARY_EVENT_CANCEL_ONLY_ON_TRANSACTION;
import Problems.BOUNDARY_EVENT_MULTIPLE_CANCEL_ON_TRANSACTION;
import Problems.BOUNDARY_EVENT_NO_EVENT_DEFINITION;
import Problems.COMPENSATE_EVENT_INVALID_ACTIVITY_REF;
import Problems.DATA_ASSOCIATION_MISSING_TARGETREF;
import Problems.DATA_OBJECT_MISSING_NAME;
import Problems.END_EVENT_CANCEL_ONLY_INSIDE_TRANSACTION;
import Problems.EVENT_GATEWAY_ONLY_CONNECTED_TO_INTERMEDIATE_EVENTS;
import Problems.EVENT_LISTENER_IMPLEMENTATION_MISSING;
import Problems.EVENT_LISTENER_INVALID_THROW_EVENT_TYPE;
import Problems.EVENT_SUBPROCESS_INVALID_START_EVENT_DEFINITION;
import Problems.EVENT_TIMER_MISSING_CONFIGURATION;
import Problems.EXCLUSIVE_GATEWAY_CONDITION_NOT_ALLOWED_ON_SINGLE_SEQ_FLOW;
import Problems.EXCLUSIVE_GATEWAY_CONDITION_ON_DEFAULT_SEQ_FLOW;
import Problems.EXCLUSIVE_GATEWAY_NO_OUTGOING_SEQ_FLOW;
import Problems.EXCLUSIVE_GATEWAY_SEQ_FLOW_WITHOUT_CONDITIONS;
import Problems.EXECUTION_LISTENER_IMPLEMENTATION_MISSING;
import Problems.HTTP_TASK_NO_REQUEST_METHOD;
import Problems.HTTP_TASK_NO_REQUEST_URL;
import Problems.INTERMEDIATE_CATCH_EVENT_INVALID_EVENTDEFINITION;
import Problems.INTERMEDIATE_CATCH_EVENT_NO_EVENTDEFINITION;
import Problems.MAIL_TASK_NO_CONTENT;
import Problems.MAIL_TASK_NO_RECIPIENT;
import Problems.MESSAGE_EVENT_MISSING_MESSAGE_REF;
import Problems.MESSAGE_INVALID_ITEM_REF;
import Problems.MULTI_INSTANCE_MISSING_COLLECTION;
import Problems.SCRIPT_TASK_MISSING_SCRIPT;
import Problems.SEND_TASK_INVALID_IMPLEMENTATION;
import Problems.SEND_TASK_INVALID_TYPE;
import Problems.SEND_TASK_WEBSERVICE_INVALID_OPERATION_REF;
import Problems.SEQ_FLOW_INVALID_SRC;
import Problems.SEQ_FLOW_INVALID_TARGET;
import Problems.SERVICE_TASK_INVALID_TYPE;
import Problems.SERVICE_TASK_MISSING_IMPLEMENTATION;
import Problems.SERVICE_TASK_RESULT_VAR_NAME_WITH_DELEGATE;
import Problems.SERVICE_TASK_USE_LOCAL_SCOPE_FOR_RESULT_VAR_WITHOUT_RESULT_VARIABLE_NAME;
import Problems.SERVICE_TASK_WEBSERVICE_INVALID_OPERATION_REF;
import Problems.SHELL_TASK_NO_COMMAND;
import Problems.SIGNAL_DUPLICATE_NAME;
import Problems.SIGNAL_EVENT_INVALID_SIGNAL_REF;
import Problems.SIGNAL_EVENT_MISSING_SIGNAL_REF;
import Problems.SIGNAL_INVALID_SCOPE;
import Problems.SIGNAL_MISSING_ID;
import Problems.SIGNAL_MISSING_NAME;
import Problems.START_EVENT_INVALID_EVENT_DEFINITION;
import Problems.START_EVENT_MULTIPLE_FOUND;
import Problems.SUBPROCESS_MULTIPLE_START_EVENTS;
import Problems.SUBPROCESS_START_EVENT_EVENT_DEFINITION_NOT_ALLOWED;
import Problems.THROW_EVENT_INVALID_EVENTDEFINITION;
import Problems.USER_TASK_LISTENER_IMPLEMENTATION_MISSING;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.Process;
import org.flowable.engine.test.util.TestProcessUtil;
import org.flowable.validation.ProcessValidator;
import org.flowable.validation.ValidationError;
import org.flowable.validation.validator.ValidatorSetNames;
import org.junit.Assert;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author jbarrez
 */
public class DefaultProcessValidatorTest {
    protected ProcessValidator processValidator;

    @Test
    public void verifyValidation() throws Exception {
        InputStream xmlStream = this.getClass().getClassLoader().getResourceAsStream("org/flowable/engine/test/validation/invalidProcess.bpmn20.xml");
        XMLInputFactory xif = XMLInputFactory.newInstance();
        InputStreamReader in = new InputStreamReader(xmlStream, StandardCharsets.UTF_8);
        XMLStreamReader xtr = xif.createXMLStreamReader(in);
        BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(xtr);
        Assert.assertNotNull(bpmnModel);
        List<ValidationError> allErrors = processValidator.validate(bpmnModel);
        Assert.assertEquals(70, allErrors.size());
        String setName = ValidatorSetNames.FLOWABLE_EXECUTABLE_PROCESS;// shortening it a bit

        // isExecutable should be true
        List<ValidationError> problems = findErrors(allErrors, setName, ALL_PROCESS_DEFINITIONS_NOT_EXECUTABLE, 1);
        Assert.assertNotNull(problems.get(0).getValidatorSetName());
        Assert.assertNotNull(problems.get(0).getProblem());
        Assert.assertNotNull(problems.get(0).getDefaultDescription());
        // Event listeners
        problems = findErrors(allErrors, setName, EVENT_LISTENER_IMPLEMENTATION_MISSING, 1);
        assertProcessElementError(problems.get(0));
        problems = findErrors(allErrors, setName, EVENT_LISTENER_INVALID_THROW_EVENT_TYPE, 1);
        assertProcessElementError(problems.get(0));
        // Execution listeners
        problems = findErrors(allErrors, setName, EXECUTION_LISTENER_IMPLEMENTATION_MISSING, 2);
        assertProcessElementError(problems.get(0));
        assertCommonProblemFieldForActivity(problems.get(1));
        // Association
        problems = findErrors(allErrors, setName, ASSOCIATION_INVALID_SOURCE_REFERENCE, 1);
        assertProcessElementError(problems.get(0));
        problems = findErrors(allErrors, setName, ASSOCIATION_INVALID_TARGET_REFERENCE, 1);
        assertProcessElementError(problems.get(0));
        // Signals
        problems = findErrors(allErrors, setName, SIGNAL_MISSING_ID, 1);
        assertCommonErrorFields(problems.get(0));
        problems = findErrors(allErrors, setName, SIGNAL_MISSING_NAME, 2);
        assertCommonErrorFields(problems.get(0));
        assertCommonErrorFields(problems.get(1));
        problems = findErrors(allErrors, setName, SIGNAL_DUPLICATE_NAME, 2);
        assertCommonErrorFields(problems.get(0));
        assertCommonErrorFields(problems.get(1));
        problems = findErrors(allErrors, setName, SIGNAL_INVALID_SCOPE, 1);
        assertCommonErrorFields(problems.get(0));
        // Start event
        problems = findErrors(allErrors, setName, START_EVENT_MULTIPLE_FOUND, 2);
        assertCommonProblemFieldForActivity(problems.get(0));
        assertCommonProblemFieldForActivity(problems.get(1));
        problems = findErrors(allErrors, setName, START_EVENT_INVALID_EVENT_DEFINITION, 1);
        assertCommonProblemFieldForActivity(problems.get(0));
        // Sequence flow
        problems = findErrors(allErrors, setName, SEQ_FLOW_INVALID_SRC, 1);
        assertCommonProblemFieldForActivity(problems.get(0));
        problems = findErrors(allErrors, setName, SEQ_FLOW_INVALID_TARGET, 2);
        assertCommonProblemFieldForActivity(problems.get(0));
        // User task
        problems = findErrors(allErrors, setName, USER_TASK_LISTENER_IMPLEMENTATION_MISSING, 1);
        assertCommonProblemFieldForActivity(problems.get(0));
        // Service task
        problems = findErrors(allErrors, setName, SERVICE_TASK_RESULT_VAR_NAME_WITH_DELEGATE, 1);
        assertCommonProblemFieldForActivity(problems.get(0));
        problems = findErrors(allErrors, setName, SERVICE_TASK_INVALID_TYPE, 1);
        assertCommonProblemFieldForActivity(problems.get(0));
        problems = findErrors(allErrors, setName, SERVICE_TASK_MISSING_IMPLEMENTATION, 1);
        assertCommonProblemFieldForActivity(problems.get(0));
        problems = findErrors(allErrors, setName, SERVICE_TASK_WEBSERVICE_INVALID_OPERATION_REF, 1);
        assertCommonProblemFieldForActivity(problems.get(0));
        problems = findErrors(allErrors, setName, SERVICE_TASK_USE_LOCAL_SCOPE_FOR_RESULT_VAR_WITHOUT_RESULT_VARIABLE_NAME, 1);
        assertCommonProblemFieldForActivity(problems.get(0));
        // Send task
        problems = findErrors(allErrors, setName, SEND_TASK_INVALID_IMPLEMENTATION, 1);
        assertCommonProblemFieldForActivity(problems.get(0));
        problems = findErrors(allErrors, setName, SEND_TASK_INVALID_TYPE, 1);
        assertCommonProblemFieldForActivity(problems.get(0));
        problems = findErrors(allErrors, setName, SEND_TASK_WEBSERVICE_INVALID_OPERATION_REF, 1);
        assertCommonProblemFieldForActivity(problems.get(0));
        // Mail task
        problems = findErrors(allErrors, setName, MAIL_TASK_NO_RECIPIENT, 2);
        assertCommonProblemFieldForActivity(problems.get(0));
        assertCommonProblemFieldForActivity(problems.get(1));
        problems = findErrors(allErrors, setName, MAIL_TASK_NO_CONTENT, 4);
        assertCommonProblemFieldForActivity(problems.get(0));
        assertCommonProblemFieldForActivity(problems.get(1));
        // Http task
        problems = findErrors(allErrors, setName, HTTP_TASK_NO_REQUEST_METHOD, 2);
        assertCommonProblemFieldForActivity(problems.get(0));
        assertCommonProblemFieldForActivity(problems.get(1));
        problems = findErrors(allErrors, setName, HTTP_TASK_NO_REQUEST_URL, 2);
        assertCommonProblemFieldForActivity(problems.get(0));
        assertCommonProblemFieldForActivity(problems.get(1));
        // Shell task
        problems = findErrors(allErrors, setName, SHELL_TASK_NO_COMMAND, 1);
        assertCommonProblemFieldForActivity(problems.get(0));
        // Script task
        problems = findErrors(allErrors, setName, SCRIPT_TASK_MISSING_SCRIPT, 2);
        assertCommonProblemFieldForActivity(problems.get(0));
        assertCommonProblemFieldForActivity(problems.get(1));
        // Exclusive gateway
        problems = findErrors(allErrors, setName, EXCLUSIVE_GATEWAY_CONDITION_NOT_ALLOWED_ON_SINGLE_SEQ_FLOW, 1);
        assertCommonProblemFieldForActivity(problems.get(0));
        problems = findErrors(allErrors, setName, EXCLUSIVE_GATEWAY_CONDITION_ON_DEFAULT_SEQ_FLOW, 1);
        assertCommonProblemFieldForActivity(problems.get(0));
        problems = findErrors(allErrors, setName, EXCLUSIVE_GATEWAY_NO_OUTGOING_SEQ_FLOW, 1);
        assertCommonProblemFieldForActivity(problems.get(0));
        problems = findErrors(allErrors, setName, EXCLUSIVE_GATEWAY_SEQ_FLOW_WITHOUT_CONDITIONS, 1);
        assertCommonProblemFieldForActivity(problems.get(0));
        // Event gateway
        problems = findErrors(allErrors, setName, EVENT_GATEWAY_ONLY_CONNECTED_TO_INTERMEDIATE_EVENTS, 1);
        assertCommonProblemFieldForActivity(problems.get(0));
        // Subprocesses
        problems = findErrors(allErrors, setName, SUBPROCESS_MULTIPLE_START_EVENTS, 1);
        assertCommonProblemFieldForActivity(problems.get(0));
        problems = findErrors(allErrors, setName, SUBPROCESS_START_EVENT_EVENT_DEFINITION_NOT_ALLOWED, 1);
        assertCommonProblemFieldForActivity(problems.get(0));
        // Event subprocesses
        problems = findErrors(allErrors, setName, EVENT_SUBPROCESS_INVALID_START_EVENT_DEFINITION, 1);
        assertCommonProblemFieldForActivity(problems.get(0));
        // Boundary events
        problems = findErrors(allErrors, setName, BOUNDARY_EVENT_NO_EVENT_DEFINITION, 1);
        assertCommonProblemFieldForActivity(problems.get(0));
        problems = findErrors(allErrors, setName, BOUNDARY_EVENT_CANCEL_ONLY_ON_TRANSACTION, 1);
        assertCommonProblemFieldForActivity(problems.get(0));
        problems = findErrors(allErrors, setName, BOUNDARY_EVENT_MULTIPLE_CANCEL_ON_TRANSACTION, 1);
        assertCommonProblemFieldForActivity(problems.get(0));
        // Intermediate catch event
        problems = findErrors(allErrors, setName, INTERMEDIATE_CATCH_EVENT_NO_EVENTDEFINITION, 1);
        assertCommonProblemFieldForActivity(problems.get(0));
        problems = findErrors(allErrors, setName, INTERMEDIATE_CATCH_EVENT_INVALID_EVENTDEFINITION, 1);
        assertCommonProblemFieldForActivity(problems.get(0));
        // Intermediate throw event
        problems = findErrors(allErrors, setName, THROW_EVENT_INVALID_EVENTDEFINITION, 1);
        assertCommonProblemFieldForActivity(problems.get(0));
        // Multi instance
        problems = findErrors(allErrors, setName, MULTI_INSTANCE_MISSING_COLLECTION, 1);
        assertCommonProblemFieldForActivity(problems.get(0));
        // Message events
        problems = findErrors(allErrors, setName, MESSAGE_EVENT_MISSING_MESSAGE_REF, 2);
        assertCommonProblemFieldForActivity(problems.get(0));
        assertCommonProblemFieldForActivity(problems.get(1));
        // Signal events
        problems = findErrors(allErrors, setName, SIGNAL_EVENT_MISSING_SIGNAL_REF, 1);
        assertCommonProblemFieldForActivity(problems.get(0));
        problems = findErrors(allErrors, setName, SIGNAL_EVENT_INVALID_SIGNAL_REF, 1);
        assertCommonProblemFieldForActivity(problems.get(0));
        // Compensate event
        problems = findErrors(allErrors, setName, COMPENSATE_EVENT_INVALID_ACTIVITY_REF, 1);
        assertCommonProblemFieldForActivity(problems.get(0));
        // Timer event
        problems = findErrors(allErrors, setName, EVENT_TIMER_MISSING_CONFIGURATION, 2);
        assertCommonProblemFieldForActivity(problems.get(0));
        // Data association
        problems = findErrors(allErrors, setName, DATA_ASSOCIATION_MISSING_TARGETREF, 1);
        assertCommonProblemFieldForActivity(problems.get(0));
        // Data object
        problems = findErrors(allErrors, setName, DATA_OBJECT_MISSING_NAME, 2);
        assertCommonErrorFields(problems.get(0));
        assertCommonErrorFields(problems.get(1));
        // End event
        problems = findErrors(allErrors, setName, END_EVENT_CANCEL_ONLY_INSIDE_TRANSACTION, 1);
        assertCommonProblemFieldForActivity(problems.get(0));
        // Messages
        problems = findErrors(allErrors, setName, MESSAGE_INVALID_ITEM_REF, 1);
        assertCommonErrorFields(problems.get(0));
    }

    @Test
    public void testWarningError() throws UnsupportedEncodingException, XMLStreamException {
        String flowWithoutConditionNoDefaultFlow = "<?xml version='1.0' encoding='UTF-8'?>" + ((((((((((((("<definitions id='definitions' xmlns='http://www.omg.org/spec/BPMN/20100524/MODEL' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:activiti='http://activiti.org/bpmn' targetNamespace='Examples'>" + "  <process id='exclusiveGwDefaultSequenceFlow'> ") + "    <startEvent id='theStart' /> ") + "    <sequenceFlow id='flow1' sourceRef='theStart' targetRef='exclusiveGw' /> ") + "    <exclusiveGateway id='exclusiveGw' name='Exclusive Gateway' /> ") + // no default = "flow3" !!
        "    <sequenceFlow id='flow2' sourceRef='exclusiveGw' targetRef='theTask1'> ") + "      <conditionExpression xsi:type='tFormalExpression'>${input == 1}</conditionExpression> ") + "    </sequenceFlow> ") + "    <sequenceFlow id='flow3' sourceRef='exclusiveGw' targetRef='theTask2'/> ")// one
         + // would
        // be
        // OK
        "    <sequenceFlow id='flow4' sourceRef='exclusiveGw' targetRef='theTask2'/> ")// but
         + // two
        // unconditional
        // not!
        "    <userTask id='theTask1' name='Input is one' /> ") + "    <userTask id='theTask2' name='Default input' /> ") + "  </process>") + "</definitions>");
        XMLInputFactory xif = XMLInputFactory.newInstance();
        InputStreamReader in = new InputStreamReader(new ByteArrayInputStream(flowWithoutConditionNoDefaultFlow.getBytes()), StandardCharsets.UTF_8);
        XMLStreamReader xtr = xif.createXMLStreamReader(in);
        BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(xtr);
        Assert.assertNotNull(bpmnModel);
        List<ValidationError> allErrors = processValidator.validate(bpmnModel);
        Assert.assertEquals(1, allErrors.size());
        Assert.assertTrue(allErrors.get(0).isWarning());
    }

    /* Test for https://jira.codehaus.org/browse/ACT-2071:

    If all processes in a deployment are not executable, throw an exception as this doesn't make sense to do.
     */
    @Test
    public void testAllNonExecutableProcesses() {
        BpmnModel bpmnModel = new BpmnModel();
        for (int i = 0; i < 5; i++) {
            Process process = TestProcessUtil.createOneTaskProcess();
            process.setExecutable(false);
            bpmnModel.addProcess(process);
        }
        List<ValidationError> errors = processValidator.validate(bpmnModel);
        Assert.assertEquals(1, errors.size());
    }

    /* Test for https://jira.codehaus.org/browse/ACT-2071:

    If there is at least one process definition which is executable, and the deployment contains other process definitions which are not executable, then add a warning for those non executable
    process definitions
     */
    @Test
    public void testNonExecutableProcessDefinitionWarning() {
        BpmnModel bpmnModel = new BpmnModel();
        // 3 non-executables
        for (int i = 0; i < 3; i++) {
            Process process = TestProcessUtil.createOneTaskProcess();
            process.setExecutable(false);
            bpmnModel.addProcess(process);
        }
        // 1 executables
        Process process = TestProcessUtil.createOneTaskProcess();
        process.setExecutable(true);
        bpmnModel.addProcess(process);
        List<ValidationError> errors = processValidator.validate(bpmnModel);
        Assert.assertEquals(3, errors.size());
        for (ValidationError error : errors) {
            Assert.assertTrue(error.isWarning());
            Assert.assertNotNull(error.getValidatorSetName());
            Assert.assertNotNull(error.getProblem());
            Assert.assertNotNull(error.getDefaultDescription());
        }
    }
}

