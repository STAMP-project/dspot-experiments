/**
 * *  Copyright 2015 OrientDB LTD (info(at)orientdb.com)
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *       http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *  *
 *  * For more information: http://www.orientdb.com
 */
package com.orientechnologies.orient.core.sql.parser;


import OMathExpression.Operator;
import OMathExpression.Operator.BIT_AND;
import OMathExpression.Operator.BIT_OR;
import OMathExpression.Operator.LSHIFT;
import OMathExpression.Operator.MINUS;
import OMathExpression.Operator.PLUS;
import OMathExpression.Operator.RSHIFT;
import OMathExpression.Operator.STAR;
import com.orientechnologies.orient.core.sql.executor.OResult;
import java.math.BigDecimal;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by luigidellaquila on 02/07/15.
 */
public class OMathExpressionTest {
    @Test
    public void testTypes() {
        OMathExpression expr = new OMathExpression((-1));
        OMathExpression[] basicOps = new OMathExpression.Operator[]{ Operator.PLUS, Operator.MINUS, Operator.STAR, Operator.SLASH, Operator.REM };
        for (OMathExpression.Operator op : basicOps) {
            Assert.assertEquals(op.apply(1, 1).getClass(), Integer.class);
            Assert.assertEquals(op.apply(((short) (1)), ((short) (1))).getClass(), Integer.class);
            Assert.assertEquals(op.apply(1L, 1L).getClass(), Long.class);
            Assert.assertEquals(op.apply(1.0F, 1.0F).getClass(), Float.class);
            Assert.assertEquals(op.apply(1.0, 1.0).getClass(), Double.class);
            Assert.assertEquals(op.apply(BigDecimal.ONE, BigDecimal.ONE).getClass(), BigDecimal.class);
            Assert.assertEquals(op.apply(1L, 1).getClass(), Long.class);
            Assert.assertEquals(op.apply(1.0F, 1).getClass(), Float.class);
            Assert.assertEquals(op.apply(1.0, 1).getClass(), Double.class);
            Assert.assertEquals(op.apply(BigDecimal.ONE, 1).getClass(), BigDecimal.class);
            Assert.assertEquals(op.apply(1, 1L).getClass(), Long.class);
            Assert.assertEquals(op.apply(1, 1.0F).getClass(), Float.class);
            Assert.assertEquals(op.apply(1, 1.0).getClass(), Double.class);
            Assert.assertEquals(op.apply(1, BigDecimal.ONE).getClass(), BigDecimal.class);
        }
        Assert.assertEquals(PLUS.apply(Integer.MAX_VALUE, 1).getClass(), Long.class);
        Assert.assertEquals(MINUS.apply(Integer.MIN_VALUE, 1).getClass(), Long.class);
    }

    @Test
    public void testPriority() {
        OMathExpression exp = new OMathExpression((-1));
        exp.childExpressions.add(integer(10));
        exp.operators.add(PLUS);
        exp.childExpressions.add(integer(5));
        exp.operators.add(STAR);
        exp.childExpressions.add(integer(8));
        exp.operators.add(PLUS);
        exp.childExpressions.add(integer(2));
        exp.operators.add(LSHIFT);
        exp.childExpressions.add(integer(1));
        exp.operators.add(PLUS);
        exp.childExpressions.add(integer(1));
        Object result = exp.execute(((OResult) (null)), null);
        Assert.assertTrue((result instanceof Integer));
        Assert.assertEquals(208, result);
    }

    @Test
    public void testPriority2() {
        OMathExpression exp = new OMathExpression((-1));
        exp.childExpressions.add(integer(1));
        exp.operators.add(PLUS);
        exp.childExpressions.add(integer(2));
        exp.operators.add(STAR);
        exp.childExpressions.add(integer(3));
        exp.operators.add(STAR);
        exp.childExpressions.add(integer(4));
        exp.operators.add(PLUS);
        exp.childExpressions.add(integer(8));
        exp.operators.add(RSHIFT);
        exp.childExpressions.add(integer(2));
        exp.operators.add(PLUS);
        exp.childExpressions.add(integer(1));
        exp.operators.add(MINUS);
        exp.childExpressions.add(integer(3));
        exp.operators.add(PLUS);
        exp.childExpressions.add(integer(1));
        Object result = exp.execute(((OResult) (null)), null);
        Assert.assertTrue((result instanceof Integer));
        Assert.assertEquals(16, result);
    }

    @Test
    public void testPriority3() {
        OMathExpression exp = new OMathExpression((-1));
        exp.childExpressions.add(integer(3));
        exp.operators.add(RSHIFT);
        exp.childExpressions.add(integer(1));
        exp.operators.add(LSHIFT);
        exp.childExpressions.add(integer(1));
        Object result = exp.execute(((OResult) (null)), null);
        Assert.assertTrue((result instanceof Integer));
        Assert.assertEquals(2, result);
    }

    @Test
    public void testPriority4() {
        OMathExpression exp = new OMathExpression((-1));
        exp.childExpressions.add(integer(3));
        exp.operators.add(LSHIFT);
        exp.childExpressions.add(integer(1));
        exp.operators.add(RSHIFT);
        exp.childExpressions.add(integer(1));
        Object result = exp.execute(((OResult) (null)), null);
        Assert.assertTrue((result instanceof Integer));
        Assert.assertEquals(3, result);
    }

    @Test
    public void testAnd() {
        OMathExpression exp = new OMathExpression((-1));
        exp.childExpressions.add(integer(5));
        exp.operators.add(BIT_AND);
        exp.childExpressions.add(integer(1));
        Object result = exp.execute(((OResult) (null)), null);
        Assert.assertTrue((result instanceof Integer));
        Assert.assertEquals(1, result);
    }

    @Test
    public void testAnd2() {
        OMathExpression exp = new OMathExpression((-1));
        exp.childExpressions.add(integer(5));
        exp.operators.add(BIT_AND);
        exp.childExpressions.add(integer(4));
        Object result = exp.execute(((OResult) (null)), null);
        Assert.assertTrue((result instanceof Integer));
        Assert.assertEquals(4, result);
    }

    @Test
    public void testOr() {
        OMathExpression exp = new OMathExpression((-1));
        exp.childExpressions.add(integer(4));
        exp.operators.add(BIT_OR);
        exp.childExpressions.add(integer(1));
        Object result = exp.execute(((OResult) (null)), null);
        Assert.assertTrue((result instanceof Integer));
        Assert.assertEquals(5, result);
    }
}

