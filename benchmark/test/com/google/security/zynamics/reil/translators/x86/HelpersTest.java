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
package com.google.security.zynamics.reil.translators.x86;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class HelpersTest {
    @Test
    public void testIsHigher8BitRegister() {
        Assert.assertFalse(Helpers.isHigher8BitRegister("al"));
        Assert.assertFalse(Helpers.isHigher8BitRegister("bl"));
        Assert.assertFalse(Helpers.isHigher8BitRegister("cl"));
        Assert.assertFalse(Helpers.isHigher8BitRegister("dl"));
        Assert.assertTrue(Helpers.isHigher8BitRegister("ah"));
        Assert.assertTrue(Helpers.isHigher8BitRegister("bh"));
        Assert.assertTrue(Helpers.isHigher8BitRegister("ch"));
        Assert.assertTrue(Helpers.isHigher8BitRegister("dh"));
        Assert.assertFalse(Helpers.isHigher8BitRegister("ax"));
        Assert.assertFalse(Helpers.isHigher8BitRegister("bx"));
        Assert.assertFalse(Helpers.isHigher8BitRegister("cx"));
        Assert.assertFalse(Helpers.isHigher8BitRegister("dx"));
        Assert.assertFalse(Helpers.isHigher8BitRegister("di"));
        Assert.assertFalse(Helpers.isHigher8BitRegister("si"));
        Assert.assertFalse(Helpers.isHigher8BitRegister("sp"));
        Assert.assertFalse(Helpers.isHigher8BitRegister("bp"));
        Assert.assertFalse(Helpers.isHigher8BitRegister("ip"));
        Assert.assertFalse(Helpers.isHigher8BitRegister("eax"));
        Assert.assertFalse(Helpers.isHigher8BitRegister("ebx"));
        Assert.assertFalse(Helpers.isHigher8BitRegister("ecx"));
        Assert.assertFalse(Helpers.isHigher8BitRegister("edx"));
        Assert.assertFalse(Helpers.isHigher8BitRegister("edi"));
        Assert.assertFalse(Helpers.isHigher8BitRegister("esi"));
        Assert.assertFalse(Helpers.isHigher8BitRegister("esp"));
        Assert.assertFalse(Helpers.isHigher8BitRegister("ebp"));
        Assert.assertFalse(Helpers.isHigher8BitRegister("eip"));
        Assert.assertFalse(Helpers.isHigher8BitRegister("cs"));
        Assert.assertFalse(Helpers.isHigher8BitRegister("ds"));
        Assert.assertFalse(Helpers.isHigher8BitRegister("es"));
        Assert.assertFalse(Helpers.isHigher8BitRegister("fs"));
        Assert.assertFalse(Helpers.isHigher8BitRegister("gs"));
    }
}

