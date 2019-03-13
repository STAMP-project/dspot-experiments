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
package net.sf.jsqlparser.statement.execute;


import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.test.TestUtils;
import org.junit.Test;


/**
 *
 *
 * @author toben
 */
public class ExecuteTest {
    public ExecuteTest() {
    }

    /**
     * Test of accept method, of class Execute.
     *
     * @throws net.sf.jsqlparser.JSQLParserException
     * 		
     */
    @Test
    public void testAcceptExecute() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("EXECUTE myproc 'a', 2, 'b'");
    }

    @Test
    public void testAcceptExec() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("EXEC myproc 'a', 2, 'b'");
    }

    @Test
    public void testAcceptCall() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("CALL myproc 'a', 2, 'b'");
    }

    @Test
    public void testCallWithMultiname() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("CALL BAR.FOO");
    }

    @Test
    public void testAcceptCallWithParenthesis() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("CALL myproc ('a', 2, 'b')");
    }

    @Test
    public void testAcceptExecNamesParameters() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("EXEC procedure @param");
    }

    @Test
    public void testAcceptExecNamesParameters2() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("EXEC procedure @param = 1");
    }

    @Test
    public void testAcceptExecNamesParameters3() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("EXEC procedure @param = 'foo'");
    }

    @Test
    public void testAcceptExecNamesParameters4() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("EXEC procedure @param = 'foo', @param2 = 'foo2'");
    }
}

