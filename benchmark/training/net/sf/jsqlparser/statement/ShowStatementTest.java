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
package net.sf.jsqlparser.statement;


import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.test.TestUtils;
import org.junit.Test;


/**
 *
 *
 * @author oshai
 */
public class ShowStatementTest {
    @Test
    public void testSimpleUse() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SHOW COLUMNS FROM mydatabase");
    }
}

