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
package com.twosigma.beakerx.scala.evaluator;


import com.twosigma.beakerx.DefaultJVMVariables;
import com.twosigma.beakerx.KernelExecutionTest;
import com.twosigma.beakerx.MessageFactorTest;
import com.twosigma.beakerx.TryResult;
import com.twosigma.beakerx.chart.xychart.Plot;
import com.twosigma.beakerx.jvm.object.SimpleEvaluationObject;
import com.twosigma.beakerx.kernel.EvaluatorParameters;
import com.twosigma.beakerx.widget.DisplayableWidget;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;


public class ScalaEvaluatorTest {
    private static ScalaEvaluator scalaEvaluator;

    @Test
    public void evaluatePlot_shouldCreatePlotObject() throws Exception {
        // given
        String code = "import com.twosigma.beakerx.chart.xychart.Plot;\n" + ("val plot = new Plot();\n" + "plot.setTitle(\"test title\");");
        SimpleEvaluationObject seo = new SimpleEvaluationObject(code);
        seo.setJupyterMessage(MessageFactorTest.commMsg());
        // when
        TryResult evaluate = ScalaEvaluatorTest.scalaEvaluator.evaluate(seo, code);
        // then
        assertThat(((evaluate.result()) instanceof Plot)).isTrue();
        assertThat(getTitle()).isEqualTo("test title");
    }

    @Test
    public void javaImports_shouldBeAdjustedForScala() throws Exception {
        // given
        Map<String, Object> paramMap = new HashMap<>();
        // This import tests both "static" removal and "object" escaping.
        List<String> imports = Arrays.asList("import static com.twosigma.beakerx.scala.evaluator.object.ImportTestHelper.staticMethod");
        paramMap.put(DefaultJVMVariables.IMPORTS, imports);
        EvaluatorParameters kernelParameters = new EvaluatorParameters(paramMap);
        // when
        ScalaEvaluatorTest.scalaEvaluator.updateEvaluatorParameters(kernelParameters);
        String code = "val x = staticMethod()";
        SimpleEvaluationObject seo = new SimpleEvaluationObject(code);
        TryResult evaluate = ScalaEvaluatorTest.scalaEvaluator.evaluate(seo, code);
        // then
        assertThat(evaluate.result()).isNull();
    }

    @Test
    public void incompleteInput_shouldBeDetected() throws Exception {
        // given
        String code = "1 to 10 map { i => i * 2";
        SimpleEvaluationObject seo = new SimpleEvaluationObject(code);
        // when
        TryResult evaluate = ScalaEvaluatorTest.scalaEvaluator.evaluate(seo, code);
        // then
        assertThat(evaluate.error()).contains("incomplete");
    }

    @Test
    public void displayTable() throws Exception {
        // given
        String code = "val table = new TableDisplay(new CSV().readFile(\"src/test/resources/tableRowsTest.csv\"))\n" + "table";
        SimpleEvaluationObject seo = new SimpleEvaluationObject(code);
        seo.setJupyterMessage(MessageFactorTest.commMsg());
        // when
        TryResult evaluate = ScalaEvaluatorTest.scalaEvaluator.evaluate(seo, code);
        // then
        assertThat(((evaluate.result()) instanceof DisplayableWidget)).isTrue();
    }

    @Test
    public void newShellWhenAddJars() {
        // given
        ScalaEvaluatorGlue shell = ScalaEvaluatorTest.scalaEvaluator.getShell();
        ClassLoader classLoader = ScalaEvaluatorTest.scalaEvaluator.getClassLoader();
        // when
        ScalaEvaluatorTest.scalaEvaluator.addJarsToClasspath(Collections.singletonList(new com.twosigma.beakerx.kernel.PathToJar(KernelExecutionTest.DEMO_JAR)));
        // then
        assertThat(ScalaEvaluatorTest.scalaEvaluator.getShell()).isNotEqualTo(shell);
        assertThat(ScalaEvaluatorTest.scalaEvaluator.getClassLoader()).isEqualTo(classLoader);
        assertThat(shell.interpreter().lastRequest().lineRep().lineId()).isEqualTo(ScalaEvaluatorTest.scalaEvaluator.getShell().interpreter().lastRequest().lineRep().lineId());
    }

    @Test
    public void allowOnlyComment() {
        // given
        String code = "/*\n" + "*/";
        SimpleEvaluationObject seo = new SimpleEvaluationObject(code);
        seo.setJupyterMessage(MessageFactorTest.commMsg());
        // when
        TryResult evaluate = ScalaEvaluatorTest.scalaEvaluator.evaluate(seo, code);
        // then
        assertThat(evaluate.result()).isNull();
    }

    @Test
    public void inputIncomplete() {
        // given
        String code = "/*\n" + "*";
        SimpleEvaluationObject seo = new SimpleEvaluationObject(code);
        seo.setJupyterMessage(MessageFactorTest.commMsg());
        // when
        TryResult evaluate = ScalaEvaluatorTest.scalaEvaluator.evaluate(seo, code);
        // then
        assertThat(evaluate.error()).isEqualTo(ScalaEvaluatorGlue.INPUT_IS_INCOMPLETE());
    }
}

