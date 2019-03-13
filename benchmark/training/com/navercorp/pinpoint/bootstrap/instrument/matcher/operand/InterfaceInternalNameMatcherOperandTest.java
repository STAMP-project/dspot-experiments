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


import com.navercorp.pinpoint.common.util.ClassUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author jaehong.kim
 */
public class InterfaceInternalNameMatcherOperandTest {
    @Test
    public void base() throws Exception {
        InterfaceInternalNameMatcherOperand operand = new InterfaceInternalNameMatcherOperand("java/lang/Runnable", true);
        Assert.assertEquals(5, operand.getExecutionCost());
        Assert.assertTrue(operand.isJavaPackage());
        Assert.assertFalse(operand.isIndex());
        Assert.assertFalse(operand.isOperator());
        Assert.assertTrue(operand.isConsiderHierarchy());
        Assert.assertTrue(operand.match("java/lang/Runnable"));
        Assert.assertFalse(operand.match("java/lang/Comparable"));
        Assert.assertEquals("java/lang/Runnable", operand.getInterfaceInternalName());
        String className = InterfaceInternalNameMatcherOperandTest.Dummy.class.getName();
        operand = new InterfaceInternalNameMatcherOperand(className, false);
        Assert.assertEquals(2, operand.getExecutionCost());
        Assert.assertFalse(operand.isJavaPackage());
        Assert.assertFalse(operand.isIndex());
        Assert.assertFalse(operand.isOperator());
        Assert.assertFalse(operand.isConsiderHierarchy());
        Assert.assertTrue(operand.match(ClassUtils.toInternalName(className)));
        Assert.assertFalse(operand.match(className));
        Assert.assertEquals(ClassUtils.toInternalName(className), operand.getInterfaceInternalName());
    }

    interface Dummy {}
}

