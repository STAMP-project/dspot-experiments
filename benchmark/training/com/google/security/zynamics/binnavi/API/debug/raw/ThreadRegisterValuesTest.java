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
package com.google.security.zynamics.binnavi.API.debug.raw;


import com.google.common.collect.Lists;
import com.google.security.zynamics.binnavi.API.debug.Register;
import com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterValue;
import java.math.BigInteger;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class ThreadRegisterValuesTest {
    @Test
    public void test() {
        final ThreadRegisterValues values = new ThreadRegisterValues(new com.google.security.zynamics.binnavi.debug.models.targetinformation.ThreadRegisters(55, Lists.newArrayList(new RegisterValue("eax", BigInteger.valueOf(291), new byte[10], false, true))));
        Assert.assertEquals(55, values.getThreadId());
        final List<Register> registers = values.getValues();
        Assert.assertEquals(1, registers.size());
        Assert.assertEquals("eax", registers.get(0).getName());
        Assert.assertEquals(291, registers.get(0).getValue().longValue());
    }
}

