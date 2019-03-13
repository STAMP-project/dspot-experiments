/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.dmn.feel.codegen.feel11;


import java.util.Arrays;
import java.util.Collections;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DirectCompilerUnaryTestsTest {
    public static final Logger LOG = LoggerFactory.getLogger(DirectCompilerUnaryTestsTest.class);

    @Test
    public void test_Dash() {
        Assert.assertThat(parseCompileEvaluate("-", 1), CoreMatchers.is(Arrays.asList(true)));
        Assert.assertThat(parseCompileEvaluate("-, -", 1), CoreMatchers.is(Collections.emptyList()));
    }

    @Test
    public void test_positiveUnaryTestIneq() {
        Assert.assertThat(parseCompileEvaluate("<47", 1), CoreMatchers.is(Arrays.asList(true)));
        Assert.assertThat(parseCompileEvaluate("<47, <100", 1), CoreMatchers.is(Arrays.asList(true, true)));
        Assert.assertThat(parseCompileEvaluate("<47, <100, <-47", 1), CoreMatchers.is(Arrays.asList(true, true, false)));
        Assert.assertThat(parseCompileEvaluate(">=1, >2, <3, <=4", 0), CoreMatchers.is(Arrays.asList(false, false, true, true)));
        Assert.assertThat(parseCompileEvaluate(">=1, >2, <3, <=4", 1), CoreMatchers.is(Arrays.asList(true, false, true, true)));
        Assert.assertThat(parseCompileEvaluate(">=1, >2, <3, <=4", 2), CoreMatchers.is(Arrays.asList(true, false, true, true)));
        Assert.assertThat(parseCompileEvaluate(">=1, >2, <3, <=4", 3), CoreMatchers.is(Arrays.asList(true, true, false, true)));
        Assert.assertThat(parseCompileEvaluate(">=1, >2, <3, <=4", 4), CoreMatchers.is(Arrays.asList(true, true, false, true)));
        Assert.assertThat(parseCompileEvaluate(">=1, >2, <3, <=4", 5), CoreMatchers.is(Arrays.asList(true, true, false, false)));
        Assert.assertThat(parseCompileEvaluate("!=1, !=42", 1), CoreMatchers.is(Arrays.asList(false, true)));
    }

    @Test
    public void test_positiveUnaryTestIneq_forEQ() {
        Assert.assertThat(parseCompileEvaluate("<47, =1", 1), CoreMatchers.is(Arrays.asList(true, true)));
        Assert.assertThat(parseCompileEvaluate("<47, =47", 1), CoreMatchers.is(Arrays.asList(true, false)));
        Assert.assertThat(parseCompileEvaluate("<47, 1", 1), CoreMatchers.is(Arrays.asList(true, true)));
        Assert.assertThat(parseCompileEvaluate("<47, 47", 1), CoreMatchers.is(Arrays.asList(true, false)));
    }

    @Test
    public void test_not() {
        Assert.assertThat(parseCompileEvaluate("not(=47), not(<1), not(!=1)", 1), CoreMatchers.is(Collections.emptyList()));
    }

    @Test
    public void test_simpleUnaryTest_forRANGE() {
        Assert.assertThat(parseCompileEvaluate("[1..2]", 1), CoreMatchers.is(Collections.singletonList(true)));
        Assert.assertThat(parseCompileEvaluate("[1..2], [2..3]", 1), CoreMatchers.is(Arrays.asList(true, false)));
        Assert.assertThat(parseCompileEvaluate("(1..2], [2..3]", 1), CoreMatchers.is(Arrays.asList(false, false)));
        Assert.assertThat(parseCompileEvaluate("(1..2], [2..3]", 2), CoreMatchers.is(Arrays.asList(true, true)));
    }
}

