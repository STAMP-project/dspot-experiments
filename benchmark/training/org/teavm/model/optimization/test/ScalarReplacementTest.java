/**
 * Copyright 2017 Alexey Andreev.
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
package org.teavm.model.optimization.test;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


public class ScalarReplacementTest {
    private static final String PREFIX = "model/optimization/scalar-replacement/";

    @Rule
    public TestName name = new TestName();

    @Test
    public void simple() {
        doTest();
    }

    @Test
    public void phi() {
        doTest();
    }

    @Test
    public void escapingPhi() {
        doTest();
    }

    @Test
    public void escapingPhiSource() {
        doTest();
    }

    @Test
    public void escapingPhiReceiver() {
        doTest();
    }

    @Test
    public void escapingSharedPhiSource() {
        doTest();
    }

    @Test
    public void escapingLivingAssignmentSource() {
        doTest();
    }

    @Test
    public void copy() {
        doTest();
    }

    @Test
    public void reachabilityByClasses() {
        doTest();
    }
}

