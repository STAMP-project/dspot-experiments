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
import com.twosigma.beakerx.evaluator.Evaluator;
import com.twosigma.beakerx.jvm.object.SimpleEvaluationObject;
import com.twosigma.beakerx.kernel.PlainCode;
import com.twosigma.beakerx.message.Message;
import org.junit.Test;


public class GroovyOutputContainerTest {
    public static final Message HEADER_MESSAGE = MessageFactorTest.commMsg();

    private Evaluator evaluator;

    private KernelTest groovyKernel;

    @Test
    public void shouldAddPlotToOutputContainerTest() throws Exception {
        // given
        String code = "import com.twosigma.beakerx.groovy.evaluator.ResourceLoaderTest;\n" + (((("import com.twosigma.beakerx.jvm.object.OutputContainer;\n" + "import com.twosigma.beakerx.chart.xychart.SimpleTimePlot;\n") + "List<Map<?, ?>> rates = ResourceLoaderTest.readAsList(\"tableRowsTest.csv\");\n") + "plot2 = new SimpleTimePlot(rates, [\"m3\", \"y1\"], showLegend:false, initWidth: 300, initHeight: 400)\n") + "new OutputContainer() << plot2");
        // when
        SimpleEvaluationObject evaluationObject = PlainCode.createSimpleEvaluationObject(code, groovyKernel, GroovyOutputContainerTest.HEADER_MESSAGE, 1);
        TryResult seo = evaluator.evaluate(evaluationObject, code);
        // then
        assertThat(seo.result()).isNotNull();
        verifyPlot(groovyKernel.getPublishedMessages());
    }
}

