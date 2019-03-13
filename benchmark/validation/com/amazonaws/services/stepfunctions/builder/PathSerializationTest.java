/**
 * Copyright 2011-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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


import org.junit.Test;


public class PathSerializationTest {
    @Test
    public void taskStateWithNoPathsProvided_DoesNotHavePathFieldsInJson() {
        final StateMachine stateMachine = StepFunctionBuilder.stateMachine().startAt("InitialState").state("InitialState", StepFunctionBuilder.taskState().resource("resource-arn").transition(StepFunctionBuilder.end())).build();
        PathSerializationTest.assertStateMachineMatches("TaskStateWithNoExplicitPaths.json", stateMachine);
    }

    @Test
    public void taskStateWithExplicitNullPaths_HasExplicitJsonNullInJson() {
        final StateMachine stateMachine = StepFunctionBuilder.stateMachine().startAt("InitialState").state("InitialState", StepFunctionBuilder.taskState().resource("resource-arn").inputPath(null).outputPath(null).resultPath(null).parameters(null).transition(StepFunctionBuilder.end())).build();
        PathSerializationTest.assertStateMachineMatches("TaskStateWithExplicitNullPaths.json", stateMachine);
    }

    @Test
    public void taskStateWithNonNullPaths_HasCorrectPathsInJson() {
        final StateMachine stateMachine = StepFunctionBuilder.stateMachine().startAt("InitialState").state("InitialState", StepFunctionBuilder.taskState().resource("resource-arn").inputPath("$.input").outputPath("$.output").resultPath("$.result").parameters("{\"foo\": 42}").transition(StepFunctionBuilder.end())).build();
        PathSerializationTest.assertStateMachineMatches("TaskStateWithNonNullPaths.json", stateMachine);
    }

    @Test
    public void parallelStateWithNoPathsProvided_DoesNotHavePathFieldsInJson() {
        final StateMachine stateMachine = StepFunctionBuilder.stateMachine().startAt("InitialState").state("InitialState", StepFunctionBuilder.parallelState().branch(StepFunctionBuilder.branch().startAt("BranchState").state("BranchState", StepFunctionBuilder.succeedState())).transition(StepFunctionBuilder.end())).build();
        PathSerializationTest.assertStateMachineMatches("ParallelStateWithNoExplicitPaths.json", stateMachine);
    }

    @Test
    public void parallelStateWithExplicitNullPaths_HasExplicitJsonNullInJson() {
        final StateMachine stateMachine = StepFunctionBuilder.stateMachine().startAt("InitialState").state("InitialState", StepFunctionBuilder.parallelState().branch(StepFunctionBuilder.branch().startAt("BranchState").state("BranchState", StepFunctionBuilder.succeedState())).inputPath(null).outputPath(null).resultPath(null).parameters(null).transition(StepFunctionBuilder.end())).build();
        PathSerializationTest.assertStateMachineMatches("ParallelStateWithExplicitNullPaths.json", stateMachine);
    }

    @Test
    public void parallelStateWithNonNullPaths_HasCorrectPathsInJson() {
        final StateMachine stateMachine = StepFunctionBuilder.stateMachine().startAt("InitialState").state("InitialState", StepFunctionBuilder.parallelState().branch(StepFunctionBuilder.branch().startAt("BranchState").state("BranchState", StepFunctionBuilder.succeedState())).inputPath("$.input").outputPath("$.output").resultPath("$.result").parameters("[42, \"foo\", {}]").transition(StepFunctionBuilder.end())).build();
        PathSerializationTest.assertStateMachineMatches("ParallelStateWithNonNullPaths.json", stateMachine);
    }

    @Test
    public void passStateWithNoPathsProvided_DoesNotHavePathFieldsInJson() {
        final StateMachine stateMachine = StepFunctionBuilder.stateMachine().startAt("InitialState").state("InitialState", StepFunctionBuilder.passState().transition(StepFunctionBuilder.end())).build();
        PathSerializationTest.assertStateMachineMatches("PassStateWithNoExplicitPaths.json", stateMachine);
    }

    @Test
    public void passStateWithExplicitNullPaths_HasExplicitJsonNullInJson() {
        final StateMachine stateMachine = StepFunctionBuilder.stateMachine().startAt("InitialState").state("InitialState", StepFunctionBuilder.passState().inputPath(null).outputPath(null).resultPath(null).parameters(null).transition(StepFunctionBuilder.end())).build();
        PathSerializationTest.assertStateMachineMatches("PassStateWithExplicitNullPaths.json", stateMachine);
    }

    @Test
    public void passStateWithNonNullPaths_HasCorrectPathsInJson() {
        final StateMachine stateMachine = StepFunctionBuilder.stateMachine().startAt("InitialState").state("InitialState", StepFunctionBuilder.passState().inputPath("$.input").outputPath("$.output").resultPath("$.result").parameters("\"foo\"").transition(StepFunctionBuilder.end())).build();
        PathSerializationTest.assertStateMachineMatches("PassStateWithNonNullPaths.json", stateMachine);
    }

    @Test
    public void waitStateWithNoPathsProvided_DoesNotHavePathFieldsInJson() {
        final StateMachine stateMachine = StepFunctionBuilder.stateMachine().startAt("InitialState").state("InitialState", StepFunctionBuilder.waitState().waitFor(StepFunctionBuilder.seconds(4)).transition(StepFunctionBuilder.end())).build();
        PathSerializationTest.assertStateMachineMatches("WaitStateWithNoExplicitPaths.json", stateMachine);
    }

    @Test
    public void waitStateWithExplicitNullPaths_HasExplicitJsonNullInJson() {
        final StateMachine stateMachine = StepFunctionBuilder.stateMachine().startAt("InitialState").state("InitialState", StepFunctionBuilder.waitState().waitFor(StepFunctionBuilder.seconds(4)).inputPath(null).outputPath(null).transition(StepFunctionBuilder.end())).build();
        PathSerializationTest.assertStateMachineMatches("WaitStateWithExplicitNullPaths.json", stateMachine);
    }

    @Test
    public void waitStateWithNonNullPaths_HasCorrectPathsInJson() {
        final StateMachine stateMachine = StepFunctionBuilder.stateMachine().startAt("InitialState").state("InitialState", StepFunctionBuilder.waitState().waitFor(StepFunctionBuilder.seconds(4)).inputPath("$.input").outputPath("$.output").transition(StepFunctionBuilder.end())).build();
        PathSerializationTest.assertStateMachineMatches("WaitStateWithNonNullPaths.json", stateMachine);
    }

    @Test
    public void succeedStateWithNoPathsProvided_DoesNotHavePathFieldsInJson() {
        final StateMachine stateMachine = StepFunctionBuilder.stateMachine().startAt("InitialState").state("InitialState", StepFunctionBuilder.succeedState()).build();
        PathSerializationTest.assertStateMachineMatches("SucceedStateWithNoExplicitPaths.json", stateMachine);
    }

    @Test
    public void succeedStateWithExplicitNullPaths_HasExplicitJsonNullInJson() {
        final StateMachine stateMachine = StepFunctionBuilder.stateMachine().startAt("InitialState").state("InitialState", StepFunctionBuilder.succeedState().inputPath(null).outputPath(null)).build();
        PathSerializationTest.assertStateMachineMatches("SucceedStateWithExplicitNullPaths.json", stateMachine);
    }

    @Test
    public void succeedStateWithNonNullPaths_HasCorrectPathsInJson() {
        final StateMachine stateMachine = StepFunctionBuilder.stateMachine().startAt("InitialState").state("InitialState", StepFunctionBuilder.succeedState().inputPath("$.input").outputPath("$.output")).build();
        PathSerializationTest.assertStateMachineMatches("SucceedStateWithNonNullPaths.json", stateMachine);
    }

    @Test
    public void choiceStateWithNoPathsProvided_DoesNotHavePathFieldsInJson() {
        final StateMachine stateMachine = StepFunctionBuilder.stateMachine().startAt("InitialState").state("InitialState", StepFunctionBuilder.choiceState().choice(StepFunctionBuilder.choice().condition(StepFunctionBuilder.eq("$.foo", "val")).transition(StepFunctionBuilder.next("EndState")))).state("EndState", StepFunctionBuilder.succeedState()).build();
        PathSerializationTest.assertStateMachineMatches("ChoiceStateWithNoExplicitPaths.json", stateMachine);
    }

    @Test
    public void choiceStateWithExplicitNullPaths_HasExplicitJsonNullInJson() {
        final StateMachine stateMachine = StepFunctionBuilder.stateMachine().startAt("InitialState").state("InitialState", StepFunctionBuilder.choiceState().inputPath(null).outputPath(null).choice(StepFunctionBuilder.choice().condition(StepFunctionBuilder.eq("$.foo", "val")).transition(StepFunctionBuilder.next("EndState")))).state("EndState", StepFunctionBuilder.succeedState()).build();
        PathSerializationTest.assertStateMachineMatches("ChoiceStateWithExplicitNullPaths.json", stateMachine);
    }

    @Test
    public void choiceStateWithNonNullPaths_HasCorrectPathsInJson() {
        final StateMachine stateMachine = StepFunctionBuilder.stateMachine().startAt("InitialState").state("InitialState", StepFunctionBuilder.choiceState().inputPath("$.input").outputPath("$.output").choice(StepFunctionBuilder.choice().condition(StepFunctionBuilder.eq("$.foo", "val")).transition(StepFunctionBuilder.next("EndState")))).state("EndState", StepFunctionBuilder.succeedState()).build();
        PathSerializationTest.assertStateMachineMatches("ChoiceStateWithNonNullPaths.json", stateMachine);
    }

    @Test
    public void catcherWithNoPathsProvided_DoesNotHavePathFieldsInJson() {
        final StateMachine stateMachine = StepFunctionBuilder.stateMachine().startAt("InitialState").state("InitialState", StepFunctionBuilder.taskState().resource("resource-arn").catcher(StepFunctionBuilder.catcher().catchAll().transition(StepFunctionBuilder.next("EndState"))).transition(StepFunctionBuilder.end())).state("EndState", StepFunctionBuilder.succeedState()).build();
        PathSerializationTest.assertStateMachineMatches("CatcherWithNoExplicitPaths.json", stateMachine);
    }

    @Test
    public void catcherWithExplicitNullPaths_HasExplicitJsonNullInJson() {
        final StateMachine stateMachine = StepFunctionBuilder.stateMachine().startAt("InitialState").state("InitialState", StepFunctionBuilder.taskState().resource("resource-arn").catcher(StepFunctionBuilder.catcher().catchAll().resultPath(null).transition(StepFunctionBuilder.next("EndState"))).transition(StepFunctionBuilder.end())).state("EndState", StepFunctionBuilder.succeedState()).build();
        PathSerializationTest.assertStateMachineMatches("CatcherWithExplicitNullPaths.json", stateMachine);
    }

    @Test
    public void catcherWithNonNullPaths_HasCorrectPathsInJson() {
        final StateMachine stateMachine = StepFunctionBuilder.stateMachine().startAt("InitialState").state("InitialState", StepFunctionBuilder.taskState().resource("resource-arn").catcher(StepFunctionBuilder.catcher().catchAll().resultPath("$.result").transition(StepFunctionBuilder.next("EndState"))).transition(StepFunctionBuilder.end())).state("EndState", StepFunctionBuilder.succeedState()).build();
        PathSerializationTest.assertStateMachineMatches("CatcherWithNonNullPaths.json", stateMachine);
    }
}

