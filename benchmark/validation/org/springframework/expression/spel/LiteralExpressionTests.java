/**
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.expression.spel;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.expression.spel.support.StandardEvaluationContext;


/**
 *
 *
 * @author Andy Clement
 */
public class LiteralExpressionTests {
    @Test
    public void testGetValue() throws Exception {
        LiteralExpression lEx = new LiteralExpression("somevalue");
        checkString("somevalue", lEx.getValue());
        checkString("somevalue", lEx.getValue(String.class));
        EvaluationContext ctx = new StandardEvaluationContext();
        checkString("somevalue", lEx.getValue(ctx));
        checkString("somevalue", lEx.getValue(ctx, String.class));
        checkString("somevalue", lEx.getValue(new LiteralExpressionTests.Rooty()));
        checkString("somevalue", lEx.getValue(new LiteralExpressionTests.Rooty(), String.class));
        checkString("somevalue", lEx.getValue(ctx, new LiteralExpressionTests.Rooty()));
        checkString("somevalue", lEx.getValue(ctx, new LiteralExpressionTests.Rooty(), String.class));
        Assert.assertEquals("somevalue", lEx.getExpressionString());
        Assert.assertFalse(lEx.isWritable(new StandardEvaluationContext()));
        Assert.assertFalse(lEx.isWritable(new LiteralExpressionTests.Rooty()));
        Assert.assertFalse(lEx.isWritable(new StandardEvaluationContext(), new LiteralExpressionTests.Rooty()));
    }

    static class Rooty {}

    @Test
    public void testSetValue() {
        try {
            LiteralExpression lEx = new LiteralExpression("somevalue");
            lEx.setValue(new StandardEvaluationContext(), "flibble");
            Assert.fail("Should have got an exception that the value cannot be set");
        } catch (EvaluationException ee) {
            // success, not allowed - whilst here, check the expression value in the exception
            Assert.assertEquals("somevalue", ee.getExpressionString());
        }
        try {
            LiteralExpression lEx = new LiteralExpression("somevalue");
            lEx.setValue(new LiteralExpressionTests.Rooty(), "flibble");
            Assert.fail("Should have got an exception that the value cannot be set");
        } catch (EvaluationException ee) {
            // success, not allowed - whilst here, check the expression value in the exception
            Assert.assertEquals("somevalue", ee.getExpressionString());
        }
        try {
            LiteralExpression lEx = new LiteralExpression("somevalue");
            lEx.setValue(new StandardEvaluationContext(), new LiteralExpressionTests.Rooty(), "flibble");
            Assert.fail("Should have got an exception that the value cannot be set");
        } catch (EvaluationException ee) {
            // success, not allowed - whilst here, check the expression value in the exception
            Assert.assertEquals("somevalue", ee.getExpressionString());
        }
    }

    @Test
    public void testGetValueType() throws Exception {
        LiteralExpression lEx = new LiteralExpression("somevalue");
        Assert.assertEquals(String.class, lEx.getValueType());
        Assert.assertEquals(String.class, lEx.getValueType(new StandardEvaluationContext()));
        Assert.assertEquals(String.class, lEx.getValueType(new LiteralExpressionTests.Rooty()));
        Assert.assertEquals(String.class, lEx.getValueType(new StandardEvaluationContext(), new LiteralExpressionTests.Rooty()));
        Assert.assertEquals(String.class, lEx.getValueTypeDescriptor().getType());
        Assert.assertEquals(String.class, lEx.getValueTypeDescriptor(new StandardEvaluationContext()).getType());
        Assert.assertEquals(String.class, lEx.getValueTypeDescriptor(new LiteralExpressionTests.Rooty()).getType());
        Assert.assertEquals(String.class, lEx.getValueTypeDescriptor(new StandardEvaluationContext(), new LiteralExpressionTests.Rooty()).getType());
    }
}

