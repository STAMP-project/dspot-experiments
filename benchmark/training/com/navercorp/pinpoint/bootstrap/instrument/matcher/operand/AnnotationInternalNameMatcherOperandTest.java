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
public class AnnotationInternalNameMatcherOperandTest {
    @Test
    public void base() throws Exception {
        AnnotationInternalNameMatcherOperand operand = new AnnotationInternalNameMatcherOperand("javax/annotation/Resource", true);
        Assert.assertTrue(operand.isConsiderMetaAnnotation());
        Assert.assertTrue(operand.match("javax/annotation/Resource"));
        Assert.assertFalse(operand.match("java.lang.Override"));
        Assert.assertEquals("javax/annotation/Resource", operand.getAnnotationInternalName());
        Assert.assertEquals(5, operand.getExecutionCost());
        Assert.assertFalse(operand.isIndex());
        Assert.assertFalse(operand.isOperator());
    }
}

