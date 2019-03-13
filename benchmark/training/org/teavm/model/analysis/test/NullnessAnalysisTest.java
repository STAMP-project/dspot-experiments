/**
 * Copyright 2016 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.model.analysis.test;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


public class NullnessAnalysisTest {
    @Rule
    public TestName name = new TestName();

    private static final String NOT_NULL_DIRECTIVE = "// NOT_NULL ";

    private static final String NULLABLE_DIRECTIVE = "// NULLABLE ";

    private static final String NULL_DIRECTIVE = "// NULL ";

    @Test
    public void simple() {
        test();
    }

    @Test
    public void phiJoin() {
        test();
    }

    @Test
    public void branch() {
        test();
    }

    @Test
    public void phiPropagation() {
        test();
    }

    @Test
    public void tryCatchJoint() {
        test();
    }

    @Test
    public void loop() {
        test();
    }

    @Test
    public void nonDominatedBranch() {
        test();
    }

    @Test
    public void exception() {
        test();
    }

    @Test
    public void nullAndNull() {
        test();
    }

    @Test
    public void irreduciblePhiLoop() {
        test();
    }
}

