/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.security.zynamics.reil.algorithms.mono.valuetracking.transformers;


import OperandSize.DWORD;
import OperandSize.QWORD;
import com.google.security.zynamics.reil.ReilHelpers;
import com.google.security.zynamics.reil.ReilInstruction;
import com.google.security.zynamics.reil.algorithms.mono.valuetracking.ValueTrackerElement;
import com.google.security.zynamics.reil.algorithms.mono.valuetracking.elements.Addition;
import com.google.security.zynamics.reil.algorithms.mono.valuetracking.elements.Literal;
import com.google.security.zynamics.reil.algorithms.mono.valuetracking.elements.Symbol;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class AddTransformerTest {
    @Test
    public void testAddConstants() {
        final ReilInstruction instruction = ReilHelpers.createAdd(256, DWORD, "2", DWORD, "4", QWORD, "t0");
        final ValueTrackerElement state = new ValueTrackerElement();
        final ValueTrackerElement result = AddTransformer.transform(instruction, state);
        Assert.assertTrue(((result.getState("t0")) instanceof Literal));
        Assert.assertEquals(6, getValue().longValue());
    }

    @Test
    public void testAddRegisterConstant() {
        final ReilInstruction instruction = ReilHelpers.createAdd(256, DWORD, "t0", DWORD, "4", QWORD, "t1");
        final ValueTrackerElement state = new ValueTrackerElement();
        final ValueTrackerElement result = AddTransformer.transform(instruction, state);
        Assert.assertTrue(((result.getState("t1")) instanceof Addition));
        Assert.assertTrue(((getLhs()) instanceof Symbol));
        Assert.assertTrue(((getRhs()) instanceof Literal));
    }
}

