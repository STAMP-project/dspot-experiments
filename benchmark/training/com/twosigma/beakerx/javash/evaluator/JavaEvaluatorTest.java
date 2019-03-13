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
package com.twosigma.beakerx.javash.evaluator;


import com.twosigma.beakerx.MessageFactorTest;
import com.twosigma.beakerx.TryResult;
import com.twosigma.beakerx.chart.xychart.Plot;
import com.twosigma.beakerx.jvm.object.SimpleEvaluationObject;
import org.junit.Test;


public class JavaEvaluatorTest {
    private static JavaEvaluator javaEvaluator;

    @Test
    public void evaluatePlot_shouldCreatePlotObject() throws Exception {
        // given
        String code = "import com.twosigma.beakerx.chart.xychart.*;\n" + ("Plot plot = new Plot(); plot.setTitle(\"test title\");\n" + "return plot;");
        SimpleEvaluationObject seo = new SimpleEvaluationObject(code);
        seo.setJupyterMessage(MessageFactorTest.commMsg());
        // when
        TryResult evaluate = JavaEvaluatorTest.javaEvaluator.evaluate(seo, code);
        // then
        assertThat(((evaluate.result()) instanceof Plot)).isTrue();
        assertThat(getTitle()).isEqualTo("test title");
    }

    @Test
    public void evaluateDivisionByZero_shouldReturnArithmeticException() throws Exception {
        // given
        String code = "return 16/0;";
        SimpleEvaluationObject seo = new SimpleEvaluationObject(code);
        // when
        TryResult evaluate = JavaEvaluatorTest.javaEvaluator.evaluate(seo, code);
        // then
        assertThat(evaluate.error()).contains("java.lang.ArithmeticException");
    }

    @Test
    public void singleImport() throws Exception {
        // given
        String code = "import java.util.Date;";
        SimpleEvaluationObject seo = new SimpleEvaluationObject(code);
        // when
        TryResult evaluate = JavaEvaluatorTest.javaEvaluator.evaluate(seo, code);
        // then
        assertThat(evaluate.result()).isNull();
    }

    @Test
    public void onlyPackage() throws Exception {
        // given
        String code = "package beaker.test;";
        SimpleEvaluationObject seo = new SimpleEvaluationObject(code);
        // when
        TryResult evaluate = JavaEvaluatorTest.javaEvaluator.evaluate(seo, code);
        // then
        assertThat(evaluate.result()).isNull();
    }

    @Test
    public void noCode() throws Exception {
        // given
        String code = "";
        SimpleEvaluationObject seo = new SimpleEvaluationObject(code);
        // when
        TryResult evaluate = JavaEvaluatorTest.javaEvaluator.evaluate(seo, code);
        // then
        assertThat(evaluate).isNotNull();
    }

    @Test
    public void evaluateStreamInMultipleLines() throws Exception {
        // given
        String code = "import java.util.stream.Stream;\n" + (("return Stream.of(1, 2, 3, 4).map(i -> { \n" + "    return i * 10;\n") + "});");
        SimpleEvaluationObject seo = new SimpleEvaluationObject(code);
        // when
        TryResult evaluate = JavaEvaluatorTest.javaEvaluator.evaluate(seo, code);
        // then
        assertThat(evaluate.result()).isNotNull();
    }

    @Test
    public void evaluateStreamInOneLine() throws Exception {
        // given
        String code = "import java.util.stream.Stream;\n" + "return Stream.of(1, 2, 3, 4).map(i -> { return i * 10;});";
        SimpleEvaluationObject seo = new SimpleEvaluationObject(code);
        // when
        TryResult evaluate = JavaEvaluatorTest.javaEvaluator.evaluate(seo, code);
        // then
        assertThat(evaluate.result()).isNotNull();
    }

    @Test
    public void evaluateVoid() throws Exception {
        // given
        String code = "System.out.println(\"Hello\");";
        SimpleEvaluationObject seo = new SimpleEvaluationObject(code);
        // when
        TryResult evaluate = JavaEvaluatorTest.javaEvaluator.evaluate(seo, code);
        // then
        assertThat(evaluate.result()).isNull();
    }

    @Test
    public void evaluateIfStatement() throws Exception {
        // given
        String code = "" + (((("if (true){\n" + "    return \"AAA\";\n") + "}else {\n") + "    return \"BBB\";\n") + "}");
        SimpleEvaluationObject seo = new SimpleEvaluationObject(code);
        // when
        TryResult evaluate = JavaEvaluatorTest.javaEvaluator.evaluate(seo, code);
        // then
        assertThat(((String) (evaluate.result()))).isEqualTo("AAA");
    }

    @Test
    public void overwriteClass() throws Exception {
        // given
        runCode(("" + ((((("package hello;\n" + "public class Main {\n") + "    public static String doSth (){\n") + "        return \"hello\";\n") + "    }\n") + "}")));
        runCode(("" + ("package hello;\n" + "return Main.doSth();")));
        // when
        runCode(("" + ((((("package hello;\n" + "public class Main {\n") + "    public static String doSth (){\n") + "        return \"hello2\";\n") + "    }\n") + "}")));
        TryResult result = runCode(("" + ("package hello;\n" + "return Main.doSth();")));
        // then
        assertThat(((String) (result.result()))).contains("hello2");
    }
}

