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
package com.google.security.zynamics.binnavi.API.debug;


import com.google.security.zynamics.binnavi.debug.models.targetinformation.DebuggerOptions;
import com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterDescription;
import com.google.security.zynamics.binnavi.debug.models.targetinformation.TargetInformation;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class TargetInformationTest {
    @Test
    public void testConstructor() {
        final List<RegisterDescription> registers = new ArrayList<>();
        final DebuggerOptions debuggerOptions = new DebuggerOptions(false, false, true, false, true, false, true, false, true, false, 1, 0, new ArrayList<com.google.security.zynamics.binnavi.debug.models.targetinformation.DebuggerException>(), true, true, true);
        final TargetInformation internalTargetInformation = new TargetInformation(4, registers, debuggerOptions);
        final TargetInformation targetInformation = new TargetInformation(internalTargetInformation);
        Assert.assertFalse(targetInformation.canDetach());
        Assert.assertFalse(targetInformation.canAttach());
        Assert.assertTrue(targetInformation.canTerminate());
        Assert.assertFalse(targetInformation.canMapMemory());
        Assert.assertFalse(targetInformation.canValidateMemory());
        Assert.assertTrue(targetInformation.canHalt());
        Assert.assertTrue(targetInformation.canMultithread());
        Assert.assertFalse(targetInformation.canSoftwareBreakpoint());
        Assert.assertEquals(4, targetInformation.getAddressSize());
        Assert.assertTrue(targetInformation.canTracecount());
    }
}

