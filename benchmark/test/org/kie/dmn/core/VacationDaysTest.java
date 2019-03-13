/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.dmn.core;


import org.junit.Test;


public class VacationDaysTest extends BaseInterpretedVsCompiledTest {
    public VacationDaysTest(final boolean useExecModelCompiler) {
        super(useExecModelCompiler);
    }

    @Test
    public void testSolutionCase1() {
        executeTest(16, 1, 27);
    }

    @Test
    public void testSolutionCase2() {
        executeTest(25, 5, 22);
    }

    @Test
    public void testSolutionCase3() {
        executeTest(44, 20, 24);
    }

    @Test
    public void testSolutionCase4() {
        executeTest(44, 30, 30);
    }

    @Test
    public void testSolutionCase5() {
        executeTest(50, 20, 24);
    }

    @Test
    public void testSolutionCase6() {
        executeTest(50, 30, 30);
    }

    @Test
    public void testSolutionCase7() {
        executeTest(60, 20, 30);
    }
}

