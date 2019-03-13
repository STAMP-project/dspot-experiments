/**
 * Copyright 2002-2014 the original author or authors.
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


import SQLErrorCodesFactory.SQL_ERROR_CODE_DEFAULT_PATH;
import SQLErrorCodesFactory.SQL_ERROR_CODE_OVERRIDE_PATH;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;


/**
 * Tests for SQLErrorCodes loading.
 *
 * @author Rod Johnson
 * @author Thomas Risberg
 * @author Stephane Nicoll
 */
public class SQLErrorCodesFactoryTests {
    /**
     * Check that a default instance returns empty error codes for an unknown database.
     */
    @Test
    public void testDefaultInstanceWithNoSuchDatabase() {
        SQLErrorCodes sec = SQLErrorCodesFactory.getInstance().getErrorCodes("xx");
        Assert.assertTrue(((sec.getBadSqlGrammarCodes().length) == 0));
        Assert.assertTrue(((sec.getDataIntegrityViolationCodes().length) == 0));
    }

    /**
     * Check that a known database produces recognizable codes.
     */
    @Test
    public void testDefaultInstanceWithOracle() {
        SQLErrorCodes sec = SQLErrorCodesFactory.getInstance().getErrorCodes("Oracle");
        assertIsOracle(sec);
    }

    @Test
    public void testLookupOrder() {
        class TestSQLErrorCodesFactory extends SQLErrorCodesFactory {
            private int lookups = 0;

            @Override
            protected Resource loadResource(String path) {
                ++(lookups);
                if ((lookups) == 1) {
                    Assert.assertEquals(SQL_ERROR_CODE_DEFAULT_PATH, path);
                    return null;
                } else {
                    // Should have only one more lookup
                    Assert.assertEquals(2, lookups);
                    Assert.assertEquals(SQL_ERROR_CODE_OVERRIDE_PATH, path);
                    return null;
                }
            }
        }
        // Should have failed to load without error
        TestSQLErrorCodesFactory sf = new TestSQLErrorCodesFactory();
        Assert.assertTrue(((getErrorCodes("XX").getBadSqlGrammarCodes().length) == 0));
        Assert.assertTrue(((getErrorCodes("Oracle").getDataIntegrityViolationCodes().length) == 0));
    }

    /**
     * Check that user defined error codes take precedence.
     */
    @Test
    public void testFindUserDefinedCodes() {
        class TestSQLErrorCodesFactory extends SQLErrorCodesFactory {
            @Override
            protected Resource loadResource(String path) {
                if (SQL_ERROR_CODE_OVERRIDE_PATH.equals(path)) {
                    return new ClassPathResource("test-error-codes.xml", SQLErrorCodesFactoryTests.class);
                }
                return null;
            }
        }
        // Should have loaded without error
        TestSQLErrorCodesFactory sf = new TestSQLErrorCodesFactory();
        Assert.assertTrue(((getErrorCodes("XX").getBadSqlGrammarCodes().length) == 0));
        Assert.assertEquals(2, getErrorCodes("Oracle").getBadSqlGrammarCodes().length);
        Assert.assertEquals("1", getErrorCodes("Oracle").getBadSqlGrammarCodes()[0]);
        Assert.assertEquals("2", getErrorCodes("Oracle").getBadSqlGrammarCodes()[1]);
    }

    @Test
    public void testInvalidUserDefinedCodeFormat() {
        class TestSQLErrorCodesFactory extends SQLErrorCodesFactory {
            @Override
            protected Resource loadResource(String path) {
                if (SQL_ERROR_CODE_OVERRIDE_PATH.equals(path)) {
                    // Guaranteed to be on the classpath, but most certainly NOT XML
                    return new ClassPathResource("SQLExceptionTranslator.class", SQLErrorCodesFactoryTests.class);
                }
                return null;
            }
        }
        // Should have failed to load without error
        TestSQLErrorCodesFactory sf = new TestSQLErrorCodesFactory();
        Assert.assertTrue(((getErrorCodes("XX").getBadSqlGrammarCodes().length) == 0));
        Assert.assertEquals(0, getErrorCodes("Oracle").getBadSqlGrammarCodes().length);
    }

    /**
     * Check that custom error codes take precedence.
     */
    @Test
    public void testFindCustomCodes() {
        class TestSQLErrorCodesFactory extends SQLErrorCodesFactory {
            @Override
            protected Resource loadResource(String path) {
                if (SQL_ERROR_CODE_OVERRIDE_PATH.equals(path)) {
                    return new ClassPathResource("custom-error-codes.xml", SQLErrorCodesFactoryTests.class);
                }
                return null;
            }
        }
        // Should have loaded without error
        TestSQLErrorCodesFactory sf = new TestSQLErrorCodesFactory();
        Assert.assertEquals(1, getErrorCodes("Oracle").getCustomTranslations().length);
        CustomSQLErrorCodesTranslation translation = getErrorCodes("Oracle").getCustomTranslations()[0];
        Assert.assertEquals(CustomErrorCodeException.class, translation.getExceptionClass());
        Assert.assertEquals(1, translation.getErrorCodes().length);
    }

    @Test
    public void testDataSourceWithNullMetadata() throws Exception {
        Connection connection = Mockito.mock(Connection.class);
        DataSource dataSource = Mockito.mock(DataSource.class);
        BDDMockito.given(dataSource.getConnection()).willReturn(connection);
        SQLErrorCodes sec = SQLErrorCodesFactory.getInstance().getErrorCodes(dataSource);
        assertIsEmpty(sec);
        Mockito.verify(connection).close();
    }

    @Test
    public void testGetFromDataSourceWithSQLException() throws Exception {
        SQLException expectedSQLException = new SQLException();
        DataSource dataSource = Mockito.mock(DataSource.class);
        BDDMockito.given(dataSource.getConnection()).willThrow(expectedSQLException);
        SQLErrorCodes sec = SQLErrorCodesFactory.getInstance().getErrorCodes(dataSource);
        assertIsEmpty(sec);
    }

    @Test
    public void testSQLServerRecognizedFromMetadata() throws Exception {
        SQLErrorCodes sec = getErrorCodesFromDataSource("MS-SQL", null);
        assertIsSQLServer(sec);
    }

    @Test
    public void testOracleRecognizedFromMetadata() throws Exception {
        SQLErrorCodes sec = getErrorCodesFromDataSource("Oracle", null);
        assertIsOracle(sec);
    }

    @Test
    public void testHsqlRecognizedFromMetadata() throws Exception {
        SQLErrorCodes sec = getErrorCodesFromDataSource("HSQL Database Engine", null);
        assertIsHsql(sec);
    }

    @Test
    public void testDB2RecognizedFromMetadata() throws Exception {
        SQLErrorCodes sec = getErrorCodesFromDataSource("DB2", null);
        assertIsDB2(sec);
        sec = getErrorCodesFromDataSource("DB2/", null);
        assertIsDB2(sec);
        sec = getErrorCodesFromDataSource("DB-2", null);
        assertIsEmpty(sec);
    }

    @Test
    public void testHanaIsRecognizedFromMetadata() throws Exception {
        SQLErrorCodes sec = getErrorCodesFromDataSource("SAP DB", null);
        assertIsHana(sec);
    }

    /**
     * Check that wild card database name works.
     */
    @Test
    public void testWildCardNameRecognized() throws Exception {
        class WildcardSQLErrorCodesFactory extends SQLErrorCodesFactory {
            @Override
            protected Resource loadResource(String path) {
                if (SQL_ERROR_CODE_OVERRIDE_PATH.equals(path)) {
                    return new ClassPathResource("wildcard-error-codes.xml", SQLErrorCodesFactoryTests.class);
                }
                return null;
            }
        }
        WildcardSQLErrorCodesFactory factory = new WildcardSQLErrorCodesFactory();
        SQLErrorCodes sec = getErrorCodesFromDataSource("DB2", factory);
        assertIsDB2(sec);
        sec = getErrorCodesFromDataSource("DB2 UDB for Xxxxx", factory);
        assertIsDB2(sec);
        sec = getErrorCodesFromDataSource("DB3", factory);
        assertIsDB2(sec);
        sec = getErrorCodesFromDataSource("DB3/", factory);
        assertIsDB2(sec);
        sec = getErrorCodesFromDataSource("/DB3", factory);
        assertIsDB2(sec);
        sec = getErrorCodesFromDataSource("/DB3", factory);
        assertIsDB2(sec);
        sec = getErrorCodesFromDataSource("/DB3/", factory);
        assertIsDB2(sec);
        sec = getErrorCodesFromDataSource("DB-3", factory);
        assertIsEmpty(sec);
        sec = getErrorCodesFromDataSource("DB1", factory);
        assertIsDB2(sec);
        sec = getErrorCodesFromDataSource("DB1/", factory);
        assertIsDB2(sec);
        sec = getErrorCodesFromDataSource("/DB1", factory);
        assertIsEmpty(sec);
        sec = getErrorCodesFromDataSource("/DB1/", factory);
        assertIsEmpty(sec);
        sec = getErrorCodesFromDataSource("DB0", factory);
        assertIsDB2(sec);
        sec = getErrorCodesFromDataSource("/DB0", factory);
        assertIsDB2(sec);
        sec = getErrorCodesFromDataSource("DB0/", factory);
        assertIsEmpty(sec);
        sec = getErrorCodesFromDataSource("/DB0/", factory);
        assertIsEmpty(sec);
    }
}

