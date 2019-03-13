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


import com.twosigma.beakerx.TryResult;
import com.twosigma.beakerx.jvm.object.SimpleEvaluationObject;
import com.twosigma.beakerx.kernel.GroupName;
import java.nio.file.Files;
import org.junit.Test;


public abstract class EvaluatorBaseTest {
    public static final String TEMP_DIR_NAME = "beakerxTest";

    @Test
    public void shouldDivide16By2() throws Exception {
        // given
        String code = codeForDivide16By2();
        SimpleEvaluationObject seo = new SimpleEvaluationObject(code);
        // when
        TryResult result = evaluator().evaluate(seo, code);
        // then
        assertThat(result.result().toString()).isEqualTo("8");
    }

    @Test
    public void shouldCreateErrorResultWithArithmeticExceptionWhenDivisionByZero() throws Exception {
        // given
        String code = codeForDivisionByZero();
        SimpleEvaluationObject seo = new SimpleEvaluationObject(code);
        // when
        TryResult either = evaluator().evaluate(seo, code);
        // then
        assertThat(either.error()).contains(textAssertionForDivisionByZero());
    }

    @Test
    public void returnHelloString() throws Exception {
        // given
        String code = codeForHello();
        SimpleEvaluationObject seo = new SimpleEvaluationObject(code);
        // when
        TryResult result = evaluator().evaluate(seo, code);
        // then
        assertThat(((String) (result.result()))).contains("Hello");
    }

    @Test
    public void returnPrintln() throws Exception {
        // given
        String code = codeForPrintln();
        SimpleEvaluationObject seo = new SimpleEvaluationObject(code);
        // when
        TryResult result = evaluator().evaluate(seo, code);
        // then
        verifyReturnPrintlnStatement(result);
    }

    @Test
    public void shouldCreateAndRemoveTempFolder() throws Exception {
        // given
        BaseEvaluator groovyEvaluator = createNewEvaluator(EvaluatorBaseTest.getTestTempFolderFactoryWithoutDeleteOnExit());
        // when
        assertThat(Files.exists(groovyEvaluator.getTempFolder())).isTrue();
        groovyEvaluator.exit();
        // then
        assertThat(Files.exists(groovyEvaluator.getTempFolder())).isFalse();
    }

    @Test
    public void shouldRunCancelHooks() {
        // given
        final StringBuilder cancelDone = new StringBuilder();
        BaseEvaluator evaluator = evaluator();
        evaluator.registerCancelHook(() -> cancelDone.append("Done"));
        // when
        evaluator.cancelExecution(GroupName.generate());
        // then
        assertThat(cancelDone.toString()).isEqualTo("Done");
    }
}

