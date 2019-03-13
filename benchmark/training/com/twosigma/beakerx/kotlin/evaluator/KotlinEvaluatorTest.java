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
package com.twosigma.beakerx.kotlin.evaluator;


import com.twosigma.beakerx.DefaultJVMVariables;
import com.twosigma.beakerx.MessageFactorTest;
import com.twosigma.beakerx.TryResult;
import com.twosigma.beakerx.jvm.object.SimpleEvaluationObject;
import com.twosigma.beakerx.kernel.EvaluatorParameters;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;


public class KotlinEvaluatorTest {
    private static KotlinEvaluator evaluator;

    @Test
    public void javaImports_shouldBeAdjustedForKotlin() throws Exception {
        // given
        Map<String, Object> paramMap = new HashMap<>();
        // This import tests both "static" removal and "object" escaping.
        List<String> imports = Arrays.asList("import static com.twosigma.beakerx.kotlin.evaluator.object.ImportTestHelper.staticMethod");
        paramMap.put(DefaultJVMVariables.IMPORTS, imports);
        EvaluatorParameters kernelParameters = new EvaluatorParameters(paramMap);
        // when
        KotlinEvaluatorTest.evaluator.updateEvaluatorParameters(kernelParameters);
        String code = "val x = staticMethod()";
        SimpleEvaluationObject seo = new SimpleEvaluationObject(code);
        TryResult evaluate = KotlinEvaluatorTest.evaluator.evaluate(seo, code);
        // then
        assertThat(evaluate.result()).isNull();
    }

    @Test
    public void evaluatePlot_shouldCreatePlotObject() throws Exception {
        // given
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(DefaultJVMVariables.IMPORTS, Arrays.asList("import com.twosigma.beakerx.chart.xychart.*"));
        KotlinEvaluatorTest.evaluator.updateEvaluatorParameters(new EvaluatorParameters(paramMap));
        String code = "val plot = Plot()\n" + ("plot.setTitle(\"test title\");\n" + "plot.display();");
        SimpleEvaluationObject seo = new SimpleEvaluationObject(code);
        seo.setJupyterMessage(MessageFactorTest.commMsg());
        // when
        TryResult evaluate = KotlinEvaluatorTest.evaluator.evaluate(seo, code);
        // then
        assertThat(evaluate.result()).isNull();
    }

    @Test
    public void executePlot() throws Exception {
        // given
        String code = "" + ("import com.twosigma.beakerx.chart.xychart.*\n" + "val plot = Plot()");
        SimpleEvaluationObject seo = new SimpleEvaluationObject(code);
        seo.setJupyterMessage(MessageFactorTest.commMsg());
        // when
        TryResult evaluate = KotlinEvaluatorTest.evaluator.evaluate(seo, code);
        // then
        assertThat(evaluate.result()).isNull();
    }

    @Test
    public void handleErrors() throws Exception {
        // given
        String code = "val plot = UndefinedPlot()";
        SimpleEvaluationObject seo = new SimpleEvaluationObject(code);
        seo.setJupyterMessage(MessageFactorTest.commMsg());
        // when
        TryResult evaluate = KotlinEvaluatorTest.evaluator.evaluate(seo, code);
        // then
        assertThat(evaluate.error()).contains("unresolved reference: UndefinedPlot");
    }

    @Test
    public void returnFromFunction() throws Exception {
        // given
        String code = "" + (((((("val a = 2.2\n" + "val b = 14\n") + "\n") + "val f = {x: Double -> a*x + b}\n") + "\n") + "println(f(2.0))\n") + "f(2.0)");
        SimpleEvaluationObject seo = new SimpleEvaluationObject(code);
        // when
        TryResult evaluate = KotlinEvaluatorTest.evaluator.evaluate(seo, code);
        // then
        assertThat(((Double) (evaluate.result()))).isEqualTo(18.4);
    }
}

