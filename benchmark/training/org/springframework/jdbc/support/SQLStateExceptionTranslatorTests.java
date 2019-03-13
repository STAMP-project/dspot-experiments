/**
 * Copyright 2002-2015 the original author or authors.
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
package org.springframework.jdbc.support;


import java.sql.SQLException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.UncategorizedSQLException;


/**
 *
 *
 * @author Rod Johnson
 * @since 13-Jan-03
 */
public class SQLStateExceptionTranslatorTests {
    private static final String sql = "SELECT FOO FROM BAR";

    private final SQLStateSQLExceptionTranslator trans = new SQLStateSQLExceptionTranslator();

    // ALSO CHECK CHAIN of SQLExceptions!?
    // also allow chain of translators? default if can't do specific?
    @Test
    public void badSqlGrammar() {
        SQLException sex = new SQLException("Message", "42001", 1);
        try {
            throw this.trans.translate("task", SQLStateExceptionTranslatorTests.sql, sex);
        } catch (BadSqlGrammarException ex) {
            // OK
            Assert.assertTrue("SQL is correct", SQLStateExceptionTranslatorTests.sql.equals(ex.getSql()));
            Assert.assertTrue("Exception matches", sex.equals(ex.getSQLException()));
        }
    }

    @Test
    public void invalidSqlStateCode() {
        SQLException sex = new SQLException("Message", "NO SUCH CODE", 1);
        try {
            throw this.trans.translate("task", SQLStateExceptionTranslatorTests.sql, sex);
        } catch (UncategorizedSQLException ex) {
            // OK
            Assert.assertTrue("SQL is correct", SQLStateExceptionTranslatorTests.sql.equals(ex.getSql()));
            Assert.assertTrue("Exception matches", sex.equals(ex.getSQLException()));
        }
    }

    /**
     * PostgreSQL can return null.
     * SAP DB can apparently return empty SQL code.
     * Bug 729170
     */
    @Test
    public void malformedSqlStateCodes() {
        SQLException sex = new SQLException("Message", null, 1);
        testMalformedSqlStateCode(sex);
        sex = new SQLException("Message", "", 1);
        testMalformedSqlStateCode(sex);
        // One char's not allowed
        sex = new SQLException("Message", "I", 1);
        testMalformedSqlStateCode(sex);
    }
}

