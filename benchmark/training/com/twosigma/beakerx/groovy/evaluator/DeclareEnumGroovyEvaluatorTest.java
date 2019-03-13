/**
 * Copyright 2018 TWO SIGMA OPEN SOURCE, LLC
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
import org.junit.Test;


public class DeclareEnumGroovyEvaluatorTest {
    private static BaseEvaluator groovyEvaluator;

    @Test
    public void declareEnum() {
        // given
        // when
        String enumCode = "" + (("enum MyColors{\n" + "  BLUE, RED, WHITE\n") + "}\n");
        runCode(enumCode);
        // when
        TryResult result = runCode("MyColors.values()");
        // then
        Object[] array = ((Object[]) (result.result()));
        assertThat(array[1].toString()).isEqualTo("RED");
    }
}

