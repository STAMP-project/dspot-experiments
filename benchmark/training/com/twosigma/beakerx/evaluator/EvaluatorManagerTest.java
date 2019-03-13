/**
 * Copyright 2017 TWO SIGMA OPEN SOURCE, LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.twosigma.beakerx.evaluator;


import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.MessageFactorTest;
import com.twosigma.beakerx.jvm.object.SimpleEvaluationObject;
import com.twosigma.beakerx.kernel.GroupName;
import com.twosigma.beakerx.kernel.PlainCode;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class EvaluatorManagerTest {
    private EvaluatorTest evaluator;

    private static KernelTest kernel;

    @Test
    public void killAllThreads_callEvaluatorToKillAllThreads() throws Exception {
        // when
        evaluator.killAllThreads();
        // then
        Assertions.assertThat(evaluator.isCallKillAllThreads()).isTrue();
    }

    @Test
    public void cancelExecution() throws Exception {
        // when
        evaluator.cancelExecution(GroupName.generate());
        // then
        Assertions.assertThat(evaluator.isCallCancelExecution()).isTrue();
    }

    @Test
    public void exit_callEvaluatorToExit() throws Exception {
        // when
        evaluator.exit();
        // then
        Assertions.assertThat(evaluator.isCallExit()).isTrue();
    }

    @Test
    public void executeCode_callEvaluatorToEvaluate() {
        String code = "test code";
        SimpleEvaluationObject seo = PlainCode.createSimpleEvaluationObject(code, EvaluatorManagerTest.kernel, MessageFactorTest.commMsg(), 5);
        // when
        evaluator.evaluate(seo, code);
        // then
        Assertions.assertThat(evaluator.getCode()).isEqualTo(code);
    }
}

