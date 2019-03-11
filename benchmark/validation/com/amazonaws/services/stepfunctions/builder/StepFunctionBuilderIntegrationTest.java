/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazonaws.services.stepfunctions.builder;


import com.amazonaws.services.stepfunctions.AWSStepFunctions;
import com.amazonaws.services.stepfunctions.model.CreateStateMachineRequest;
import com.amazonaws.services.stepfunctions.model.CreateStateMachineResult;
import com.amazonaws.services.stepfunctions.model.DescribeStateMachineRequest;
import com.amazonaws.services.stepfunctions.model.DescribeStateMachineResult;
import com.amazonaws.test.AWSIntegrationTestBase;
import java.util.Date;
import org.junit.Test;


public class StepFunctionBuilderIntegrationTest extends AWSIntegrationTestBase {
    /**
     * Dummy date so we have a fixed time for comparison.
     */
    private static final Date THE_FUTURE = new Date(4118625810000L);

    private static final String STATE_MACHINE_NAME = "java-sdk-integ-" + (System.currentTimeMillis());

    private AWSStepFunctions client;

    private String stateMachineArn;

    private String roleArn;

    @Test
    public void rountripStateMachine() {
        StateMachine stateMachine = StepFunctionBuilder.stateMachine().state("ParallelState", testParallelState()).state("WaitForTimestamp", testWaitForTimestamp()).state("WaitForTimestampPath", testWaitForTimestampPath()).state("WaitForSeconds", testWaitForSeconds()).state("WaitForSecondsPath", testWaitForSecondsPath()).state("ChoiceState", testChoiceState()).state("FailState", testFailState()).state("PassState", StepFunctionBuilder.passState().transition(StepFunctionBuilder.next("EndState"))).state("EndState", StepFunctionBuilder.succeedState()).startAt("ParallelState").build();
        CreateStateMachineResult createResult = client.createStateMachine(new CreateStateMachineRequest().withName(StepFunctionBuilderIntegrationTest.STATE_MACHINE_NAME).withRoleArn(roleArn).withDefinition(stateMachine));
        stateMachineArn = createResult.getStateMachineArn();
        DescribeStateMachineResult describeResult = client.describeStateMachine(new DescribeStateMachineRequest().withStateMachineArn(stateMachineArn));
        StatesAsserts.assertStateMachineMatches("IntegrationTestStateMachine.json", stateMachine);
        StatesAsserts.assertStateMachineMatches("IntegrationTestStateMachine.json", StateMachine.fromJson(describeResult.getDefinition()).build());
    }
}

