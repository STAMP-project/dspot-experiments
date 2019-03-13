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
package com.navercorp.pinpoint.bootstrap.instrument.matcher;


import com.navercorp.pinpoint.bootstrap.instrument.matcher.operand.ClassInternalNameMatcherOperand;
import com.navercorp.pinpoint.bootstrap.instrument.matcher.operand.InterfaceInternalNameMatcherOperand;
import com.navercorp.pinpoint.bootstrap.instrument.matcher.operand.MatcherOperand;
import com.navercorp.pinpoint.bootstrap.instrument.matcher.operator.AndMatcherOperator;
import com.navercorp.pinpoint.bootstrap.instrument.matcher.operator.OrMatcherOperator;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author jaehong.kim
 */
public class DefaultMultiClassBasedMatcherTest {
    @Test
    public void getMatcherOperandWithMultiClassName() throws Exception {
        // (class OR class)
        DefaultMultiClassBasedMatcher matcher = new DefaultMultiClassBasedMatcher(Arrays.asList("java.lang.String", "java.lang.Thread"));
        Assert.assertTrue(matcher.getBaseClassNames().contains("java.lang.String"));
        Assert.assertTrue(matcher.getBaseClassNames().contains("java.lang.Thread"));
        MatcherOperand operand = matcher.getMatcherOperand();
        Assert.assertTrue((operand instanceof OrMatcherOperator));
        OrMatcherOperator operator = ((OrMatcherOperator) (operand));
        Assert.assertTrue(((operator.getLeftOperand()) instanceof ClassInternalNameMatcherOperand));
        ClassInternalNameMatcherOperand leftOperand = ((ClassInternalNameMatcherOperand) (operator.getLeftOperand()));
        Assert.assertEquals("java/lang/String", leftOperand.getClassInternalName());
        Assert.assertTrue(((operator.getRightOperand()) instanceof ClassInternalNameMatcherOperand));
        ClassInternalNameMatcherOperand rightOperand = ((ClassInternalNameMatcherOperand) (operator.getRightOperand()));
        Assert.assertEquals("java/lang/Thread", rightOperand.getClassInternalName());
    }

    @Test
    public void getMatcherOperandWithMultiClassNameAndAdditional() throws Exception {
        // (class OR class) AND interface
        InterfaceInternalNameMatcherOperand additional = new InterfaceInternalNameMatcherOperand("java/lang/Runnable", false);
        DefaultMultiClassBasedMatcher matcher = new DefaultMultiClassBasedMatcher(Arrays.asList("java.lang.String", "java.lang.Thread"), additional);
        Assert.assertTrue(matcher.getBaseClassNames().contains("java.lang.String"));
        Assert.assertTrue(matcher.getBaseClassNames().contains("java.lang.Thread"));
        MatcherOperand operand = matcher.getMatcherOperand();
        Assert.assertTrue((operand instanceof AndMatcherOperator));
        AndMatcherOperator operator = ((AndMatcherOperator) (operand));
        // (class OR class)
        Assert.assertTrue(((operator.getLeftOperand()) instanceof OrMatcherOperator));
        // ... AND interface
        Assert.assertTrue(((operator.getRightOperand()) instanceof InterfaceInternalNameMatcherOperand));
        InterfaceInternalNameMatcherOperand rightOperand = ((InterfaceInternalNameMatcherOperand) (operator.getRightOperand()));
        Assert.assertEquals("java/lang/Runnable", rightOperand.getInterfaceInternalName());
    }
}

