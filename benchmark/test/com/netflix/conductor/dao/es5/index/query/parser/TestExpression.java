/**
 * Copyright 2016 Netflix, Inc.
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
/**
 *
 */
package com.netflix.conductor.dao.es5.index.query.parser;


import ConstValue.SystemConsts.NOT_NULL;
import ConstValue.SystemConsts.NULL;
import com.netflix.conductor.elasticsearch.query.parser.AbstractParserTest;
import com.netflix.conductor.elasticsearch.query.parser.ConstValue;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Viren
 */
public class TestExpression extends AbstractParserTest {
    @Test
    public void test() throws Exception {
        String test = "type='IMAGE' AND subType	='sdp' AND (metadata.width > 50 OR metadata.height > 50)";
        // test = "type='IMAGE' AND subType	='sdp'";
        // test = "(metadata.type = 'IMAGE')";
        InputStream is = new BufferedInputStream(new ByteArrayInputStream(test.getBytes()));
        Expression expr = new Expression(is);
        System.out.println(expr);
        Assert.assertTrue(expr.isBinaryExpr());
        Assert.assertNull(expr.getGroupedExpression());
        Assert.assertNotNull(expr.getNameValue());
        NameValue nv = expr.getNameValue();
        Assert.assertEquals("type", nv.getName().getName());
        Assert.assertEquals("=", nv.getOp().getOperator());
        Assert.assertEquals("\"IMAGE\"", nv.getValue().getValue());
        Expression rhs = expr.getRightHandSide();
        Assert.assertNotNull(rhs);
        Assert.assertTrue(rhs.isBinaryExpr());
        nv = rhs.getNameValue();
        Assert.assertNotNull(nv);// subType = sdp

        Assert.assertNull(rhs.getGroupedExpression());
        Assert.assertEquals("subType", nv.getName().getName());
        Assert.assertEquals("=", nv.getOp().getOperator());
        Assert.assertEquals("\"sdp\"", nv.getValue().getValue());
        Assert.assertEquals("AND", rhs.getOperator().getOperator());
        rhs = rhs.getRightHandSide();
        Assert.assertNotNull(rhs);
        Assert.assertFalse(rhs.isBinaryExpr());
        GroupedExpression ge = rhs.getGroupedExpression();
        Assert.assertNotNull(ge);
        expr = ge.getExpression();
        Assert.assertNotNull(expr);
        Assert.assertTrue(expr.isBinaryExpr());
        nv = expr.getNameValue();
        Assert.assertNotNull(nv);
        Assert.assertEquals("metadata.width", nv.getName().getName());
        Assert.assertEquals(">", nv.getOp().getOperator());
        Assert.assertEquals("50", nv.getValue().getValue());
        Assert.assertEquals("OR", expr.getOperator().getOperator());
        rhs = expr.getRightHandSide();
        Assert.assertNotNull(rhs);
        Assert.assertFalse(rhs.isBinaryExpr());
        nv = rhs.getNameValue();
        Assert.assertNotNull(nv);
        Assert.assertEquals("metadata.height", nv.getName().getName());
        Assert.assertEquals(">", nv.getOp().getOperator());
        Assert.assertEquals("50", nv.getValue().getValue());
    }

    @Test
    public void testWithSysConstants() throws Exception {
        String test = "type='IMAGE' AND subType	='sdp' AND description IS null";
        InputStream is = new BufferedInputStream(new ByteArrayInputStream(test.getBytes()));
        Expression expr = new Expression(is);
        System.out.println(expr);
        Assert.assertTrue(expr.isBinaryExpr());
        Assert.assertNull(expr.getGroupedExpression());
        Assert.assertNotNull(expr.getNameValue());
        NameValue nv = expr.getNameValue();
        Assert.assertEquals("type", nv.getName().getName());
        Assert.assertEquals("=", nv.getOp().getOperator());
        Assert.assertEquals("\"IMAGE\"", nv.getValue().getValue());
        Expression rhs = expr.getRightHandSide();
        Assert.assertNotNull(rhs);
        Assert.assertTrue(rhs.isBinaryExpr());
        nv = rhs.getNameValue();
        Assert.assertNotNull(nv);// subType = sdp

        Assert.assertNull(rhs.getGroupedExpression());
        Assert.assertEquals("subType", nv.getName().getName());
        Assert.assertEquals("=", nv.getOp().getOperator());
        Assert.assertEquals("\"sdp\"", nv.getValue().getValue());
        Assert.assertEquals("AND", rhs.getOperator().getOperator());
        rhs = rhs.getRightHandSide();
        Assert.assertNotNull(rhs);
        Assert.assertFalse(rhs.isBinaryExpr());
        GroupedExpression ge = rhs.getGroupedExpression();
        Assert.assertNull(ge);
        nv = rhs.getNameValue();
        Assert.assertNotNull(nv);
        Assert.assertEquals("description", nv.getName().getName());
        Assert.assertEquals("IS", nv.getOp().getOperator());
        ConstValue cv = nv.getValue();
        Assert.assertNotNull(cv);
        Assert.assertEquals(cv.getSysConstant(), NULL);
        test = "description IS not null";
        is = new BufferedInputStream(new ByteArrayInputStream(test.getBytes()));
        expr = new Expression(is);
        System.out.println(expr);
        nv = expr.getNameValue();
        Assert.assertNotNull(nv);
        Assert.assertEquals("description", nv.getName().getName());
        Assert.assertEquals("IS", nv.getOp().getOperator());
        cv = nv.getValue();
        Assert.assertNotNull(cv);
        Assert.assertEquals(cv.getSysConstant(), NOT_NULL);
    }
}

