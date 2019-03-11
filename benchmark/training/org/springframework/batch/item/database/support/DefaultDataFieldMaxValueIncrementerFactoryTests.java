/**
 * Copyright 2006-2018 the original author or authors.
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
package org.springframework.batch.item.database.support;


import junit.framework.TestCase;
import org.springframework.jdbc.support.incrementer.Db2LuwMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.Db2MainframeMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.DerbyMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.HsqlMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.MySQLMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.OracleSequenceMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.PostgresSequenceMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.SqlServerMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.SybaseMaxValueIncrementer;


/**
 *
 *
 * @author Lucas Ward
 * @author Will Schipp
 * @author Drummond Dawson
 */
public class DefaultDataFieldMaxValueIncrementerFactoryTests extends TestCase {
    private DefaultDataFieldMaxValueIncrementerFactory factory;

    public void testSupportedDatabaseType() {
        TestCase.assertTrue(factory.isSupportedIncrementerType("db2"));
        TestCase.assertTrue(factory.isSupportedIncrementerType("db2zos"));
        TestCase.assertTrue(factory.isSupportedIncrementerType("mysql"));
        TestCase.assertTrue(factory.isSupportedIncrementerType("derby"));
        TestCase.assertTrue(factory.isSupportedIncrementerType("oracle"));
        TestCase.assertTrue(factory.isSupportedIncrementerType("postgres"));
        TestCase.assertTrue(factory.isSupportedIncrementerType("hsql"));
        TestCase.assertTrue(factory.isSupportedIncrementerType("sqlserver"));
        TestCase.assertTrue(factory.isSupportedIncrementerType("sybase"));
        TestCase.assertTrue(factory.isSupportedIncrementerType("sqlite"));
    }

    public void testUnsupportedDatabaseType() {
        TestCase.assertFalse(factory.isSupportedIncrementerType("invalidtype"));
    }

    public void testInvalidDatabaseType() {
        try {
            factory.getIncrementer("invalidtype", "NAME");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    public void testNullIncrementerName() {
        try {
            factory.getIncrementer("db2", null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    public void testDb2() {
        TestCase.assertTrue(((factory.getIncrementer("db2", "NAME")) instanceof Db2LuwMaxValueIncrementer));
    }

    public void testDb2zos() {
        TestCase.assertTrue(((factory.getIncrementer("db2zos", "NAME")) instanceof Db2MainframeMaxValueIncrementer));
    }

    public void testMysql() {
        TestCase.assertTrue(((factory.getIncrementer("mysql", "NAME")) instanceof MySQLMaxValueIncrementer));
    }

    public void testOracle() {
        factory.setIncrementerColumnName("ID");
        TestCase.assertTrue(((factory.getIncrementer("oracle", "NAME")) instanceof OracleSequenceMaxValueIncrementer));
    }

    public void testDerby() {
        TestCase.assertTrue(((factory.getIncrementer("derby", "NAME")) instanceof DerbyMaxValueIncrementer));
    }

    public void testHsql() {
        TestCase.assertTrue(((factory.getIncrementer("hsql", "NAME")) instanceof HsqlMaxValueIncrementer));
    }

    public void testPostgres() {
        TestCase.assertTrue(((factory.getIncrementer("postgres", "NAME")) instanceof PostgresSequenceMaxValueIncrementer));
    }

    public void testMsSqlServer() {
        TestCase.assertTrue(((factory.getIncrementer("sqlserver", "NAME")) instanceof SqlServerMaxValueIncrementer));
    }

    public void testSybase() {
        TestCase.assertTrue(((factory.getIncrementer("sybase", "NAME")) instanceof SybaseMaxValueIncrementer));
    }

    public void testSqlite() {
        TestCase.assertTrue(((factory.getIncrementer("sqlite", "NAME")) instanceof SqliteMaxValueIncrementer));
    }
}

