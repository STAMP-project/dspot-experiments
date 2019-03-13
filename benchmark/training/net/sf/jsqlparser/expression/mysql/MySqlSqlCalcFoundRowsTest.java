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
package net.sf.jsqlparser.expression.mysql;


import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.test.TestUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author sam
 */
public class MySqlSqlCalcFoundRowsTest {
    @Test
    public void testPossibleParsingWithSqlCalcFoundRowsHint() throws JSQLParserException {
        MySqlSqlCalcFoundRowRef ref = new MySqlSqlCalcFoundRowRef(false);
        String sqlCalcFoundRowsContainingSql = "SELECT SQL_CALC_FOUND_ROWS * FROM TABLE";
        String generalSql = "SELECT * FROM TABLE";
        accept(CCJSqlParserUtil.parse(sqlCalcFoundRowsContainingSql), ref);
        Assert.assertTrue(ref.sqlCalcFoundRows);
        accept(CCJSqlParserUtil.parse(generalSql), ref);
        Assert.assertFalse(ref.sqlCalcFoundRows);
        TestUtils.assertSqlCanBeParsedAndDeparsed(sqlCalcFoundRowsContainingSql);
        TestUtils.assertSqlCanBeParsedAndDeparsed(generalSql);
    }
}

