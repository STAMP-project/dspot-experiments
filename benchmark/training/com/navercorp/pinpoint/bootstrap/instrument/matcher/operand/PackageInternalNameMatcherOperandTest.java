/**
 * Copyright 2017 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.bootstrap.instrument.matcher.operand;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author jaehong.kim
 */
public class PackageInternalNameMatcherOperandTest {
    @Test
    public void base() {
        PackageInternalNameMatcherOperand operand = new PackageInternalNameMatcherOperand("com");
        Assert.assertEquals("com", operand.getPackageInternalName());
        Assert.assertEquals(3, operand.getExecutionCost());
        Assert.assertTrue(operand.isIndex());
        Assert.assertTrue(operand.match("com"));
    }

    @Test(expected = NullPointerException.class)
    public void packageNameisNull() {
        PackageInternalNameMatcherOperand operand = new PackageInternalNameMatcherOperand(null);
    }
}

