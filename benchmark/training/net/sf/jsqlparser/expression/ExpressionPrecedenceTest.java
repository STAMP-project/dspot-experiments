/**
 * -
 * #%L
 * JSQLParser library
 * %%
 * Copyright (C) 2004 - 2019 JSQLParser
 * %%
 * Dual licensed under GNU LGPL 2.1 or Apache License 2.0
 * #L%
 */
package net.sf.jsqlparser.expression;


import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseAnd;
import net.sf.jsqlparser.expression.operators.arithmetic.Concat;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author tw
 */
public class ExpressionPrecedenceTest {
    @Test
    public void testGetSign() throws JSQLParserException {
        Expression expr = CCJSqlParserUtil.parseExpression("1&2||3");
        Assert.assertTrue((expr instanceof Concat));
        Assert.assertTrue(((getLeftExpression()) instanceof BitwiseAnd));
        Assert.assertTrue(((getRightExpression()) instanceof LongValue));
    }
}

