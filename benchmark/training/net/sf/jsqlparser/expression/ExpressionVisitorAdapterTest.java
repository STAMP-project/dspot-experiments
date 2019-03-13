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


import java.util.ArrayList;
import java.util.List;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectVisitorAdapter;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author tw
 */
public class ExpressionVisitorAdapterTest {
    public ExpressionVisitorAdapterTest() {
    }

    @Test
    public void testInExpressionProblem() throws JSQLParserException {
        final List exprList = new ArrayList();
        Select select = ((Select) (CCJSqlParserUtil.parse("select * from foo where x in (?,?,?)")));
        PlainSelect plainSelect = ((PlainSelect) (select.getSelectBody()));
        Expression where = plainSelect.getWhere();
        where.accept(new ExpressionVisitorAdapter() {
            @Override
            public void visit(InExpression expr) {
                super.visit(expr);
                exprList.add(expr.getLeftExpression());
                exprList.add(expr.getLeftItemsList());
                exprList.add(expr.getRightItemsList());
            }
        });
        Assert.assertTrue(((exprList.get(0)) instanceof Expression));
        Assert.assertNull(exprList.get(1));
        Assert.assertTrue(((exprList.get(2)) instanceof ItemsList));
    }

    @Test
    public void testInExpression() throws JSQLParserException {
        final List exprList = new ArrayList();
        Select select = ((Select) (CCJSqlParserUtil.parse("select * from foo where (a,b) in (select a,b from foo2)")));
        PlainSelect plainSelect = ((PlainSelect) (select.getSelectBody()));
        Expression where = plainSelect.getWhere();
        where.accept(new ExpressionVisitorAdapter() {
            @Override
            public void visit(InExpression expr) {
                super.visit(expr);
                exprList.add(expr.getLeftExpression());
                exprList.add(expr.getLeftItemsList());
                exprList.add(expr.getRightItemsList());
            }
        });
        Assert.assertNull(exprList.get(0));
        Assert.assertTrue(((exprList.get(1)) instanceof ItemsList));
        Assert.assertTrue(((exprList.get(2)) instanceof ItemsList));
    }

    @Test
    public void testOracleHintExpressions() throws JSQLParserException {
        ExpressionVisitorAdapterTest.testOracleHintExpression("select --+ MYHINT \n * from foo", "MYHINT", true);
        ExpressionVisitorAdapterTest.testOracleHintExpression("select /*+ MYHINT */ * from foo", "MYHINT", false);
    }

    @Test
    public void testCurrentTimestampExpression() throws JSQLParserException {
        final List<String> columnList = new ArrayList<String>();
        Select select = ((Select) (CCJSqlParserUtil.parse("select * from foo where bar < CURRENT_TIMESTAMP")));
        PlainSelect plainSelect = ((PlainSelect) (select.getSelectBody()));
        Expression where = plainSelect.getWhere();
        where.accept(new ExpressionVisitorAdapter() {
            @Override
            public void visit(Column column) {
                super.visit(column);
                columnList.add(column.getColumnName());
            }
        });
        Assert.assertEquals(1, columnList.size());
        Assert.assertEquals("bar", columnList.get(0));
    }

    @Test
    public void testCurrentDateExpression() throws JSQLParserException {
        final List<String> columnList = new ArrayList<String>();
        Select select = ((Select) (CCJSqlParserUtil.parse("select * from foo where bar < CURRENT_DATE")));
        PlainSelect plainSelect = ((PlainSelect) (select.getSelectBody()));
        Expression where = plainSelect.getWhere();
        where.accept(new ExpressionVisitorAdapter() {
            @Override
            public void visit(Column column) {
                super.visit(column);
                columnList.add(column.getColumnName());
            }
        });
        Assert.assertEquals(1, columnList.size());
        Assert.assertEquals("bar", columnList.get(0));
    }

    @Test
    public void testSubSelectExpressionProblem() throws JSQLParserException {
        Select select = ((Select) (CCJSqlParserUtil.parse("SELECT * FROM t1 WHERE EXISTS (SELECT * FROM t2 WHERE t2.col2 = t1.col1)")));
        PlainSelect plainSelect = ((PlainSelect) (select.getSelectBody()));
        Expression where = plainSelect.getWhere();
        ExpressionVisitorAdapter adapter = new ExpressionVisitorAdapter();
        adapter.setSelectVisitor(new SelectVisitorAdapter());
        try {
            where.accept(adapter);
        } catch (NullPointerException npe) {
            Assert.fail();
        }
    }

    @Test
    public void testCaseWithoutElse() throws JSQLParserException {
        Expression expr = CCJSqlParserUtil.parseExpression("CASE WHEN 1 then 0 END");
        ExpressionVisitorAdapter adapter = new ExpressionVisitorAdapter();
        expr.accept(adapter);
    }

    @Test
    public void testCaseWithoutElse2() throws JSQLParserException {
        Expression expr = CCJSqlParserUtil.parseExpression("CASE WHEN 1 then 0 ELSE -1 END");
        ExpressionVisitorAdapter adapter = new ExpressionVisitorAdapter();
        expr.accept(adapter);
    }

    @Test
    public void testCaseWithoutElse3() throws JSQLParserException {
        Expression expr = CCJSqlParserUtil.parseExpression("CASE 3+4 WHEN 1 then 0 END");
        ExpressionVisitorAdapter adapter = new ExpressionVisitorAdapter();
        expr.accept(adapter);
    }

    @Test
    public void testAnalyticFunctionWithoutExpression502() throws JSQLParserException {
        Expression expr = CCJSqlParserUtil.parseExpression("row_number() over (order by c)");
        ExpressionVisitorAdapter adapter = new ExpressionVisitorAdapter();
        expr.accept(adapter);
    }
}

