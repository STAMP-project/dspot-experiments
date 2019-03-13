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


import com.twosigma.beakerx.TryResult;
import com.twosigma.beakerx.evaluator.BaseEvaluator;
import com.twosigma.beakerx.kernel.PathToJar;
import java.util.Collections;
import org.junit.Test;


public class GroovyEvaluatorMagicCommandsTest {
    public static final String SRC_TEST_RESOURCES = "../../doc/resources/jar/";

    private static BaseEvaluator groovyEvaluator;

    @Test
    public void addJarToClasspath() throws Exception {
        // given
        String code = "" + (("import com.example.Demo;\n" + "Demo demo = new Demo();\n") + "demo.getObjectTest()\n");
        TryResult seo = runCode(code);
        assertThat(seo.error()).contains("unable to resolve class");
        // when
        PathToJar path = new PathToJar(((GroovyEvaluatorMagicCommandsTest.SRC_TEST_RESOURCES) + "demo.jar"));
        GroovyEvaluatorMagicCommandsTest.groovyEvaluator.addJarsToClasspath(Collections.singletonList(path));
        // then
        TryResult seo2 = runCode(code);
        assertThat(seo2.result()).isEqualTo("Demo_test_123");
    }
}

