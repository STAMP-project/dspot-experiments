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
package org.kie.dmn.feel.runtime;


import java.util.Collections;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.parser.feel11.profiles.DoCompileFEELProfile;

import static org.kie.dmn.feel.runtime.BaseFEELTest.FEEL_TARGET.JAVA_TRANSLATED;


@RunWith(Parameterized.class)
public abstract class BaseFEELCompilerTest {
    @Parameterized.Parameter(4)
    public BaseFEELTest.FEEL_TARGET testFEELTarget;

    private FEEL feel = null;// due to @Parameter injection by JUnit framework, need to defer FEEL init to actual instance method, to have the opportunity for the JUNit framework to initialize all the @Parameters


    @Parameterized.Parameter(0)
    public String expression;

    @Parameterized.Parameter(1)
    public Map<String, Type> inputTypes;

    @Parameterized.Parameter(2)
    public Map<String, Object> inputValues;

    @Parameterized.Parameter(3)
    public Object result;

    @Test
    public void testExpression() {
        feel = ((testFEELTarget) == (JAVA_TRANSLATED)) ? FEEL.newInstance(Collections.singletonList(new DoCompileFEELProfile())) : FEEL.newInstance();
        assertResult(expression, inputTypes, inputValues, result);
    }
}

