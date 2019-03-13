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
package com.twosigma.beakerx.groovy.evaluator;


import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.MessageFactorTest;
import com.twosigma.beakerx.TryResult;
import com.twosigma.beakerx.evaluator.BaseEvaluator;
import com.twosigma.beakerx.jvm.object.SimpleEvaluationObject;
import org.junit.Test;


public class GroovyEvaluatorProgressReportingTest {
    public static final String GROOVY_EVALUATOR_PROGRESS_REPORTING_TEST = "groovyEvaluatorProgressReportingTest";

    private BaseEvaluator groovyEvaluator;

    private KernelTest groovyKernel;

    @Test
    public void progressReporting() throws Exception {
        // given
        String code = (((("for ( int i = 0 ; i<5; i++) {\n" + "  ") + (BEAKER_VARIABLE_NAME)) + ".showProgressUpdate(\"msg\"+i, i)\n") + "}\n") + "\"finished\"";
        SimpleEvaluationObject seo = new SimpleEvaluationObject(code);
        seo.setJupyterMessage(MessageFactorTest.commMsg());
        // when
        TryResult evaluate = groovyEvaluator.evaluate(seo, code);
        // then
        assertThat(evaluate.result()).isEqualTo("finished");
        verifyProgressReporting(groovyKernel.getPublishedMessages());
    }
}

