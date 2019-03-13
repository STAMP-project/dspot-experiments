/**
 * Copyright 2009-2012 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.jdbc;


import java.io.Reader;
import java.sql.Connection;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.io.Resources;
import org.junit.Assert;
import org.junit.Test;


public class ScriptRunnerTest extends BaseDataTest {
    @Test
    public void shouldRunScriptsUsingConnection() throws Exception {
        DataSource ds = BaseDataTest.createUnpooledDataSource(BaseDataTest.JPETSTORE_PROPERTIES);
        Connection conn = ds.getConnection();
        ScriptRunner runner = new ScriptRunner(conn);
        runner.setAutoCommit(true);
        runner.setStopOnError(false);
        runner.setErrorLogWriter(null);
        runner.setLogWriter(null);
        runJPetStoreScripts(runner);
        assertProductsTableExistsAndLoaded();
    }

    @Test
    public void shouldRunScriptsUsingProperties() throws Exception {
        Properties props = Resources.getResourceAsProperties(BaseDataTest.JPETSTORE_PROPERTIES);
        DataSource dataSource = new UnpooledDataSource(props.getProperty("driver"), props.getProperty("url"), props.getProperty("username"), props.getProperty("password"));
        ScriptRunner runner = new ScriptRunner(dataSource.getConnection());
        runner.setAutoCommit(true);
        runner.setStopOnError(false);
        runner.setErrorLogWriter(null);
        runner.setLogWriter(null);
        runJPetStoreScripts(runner);
        assertProductsTableExistsAndLoaded();
    }

    @Test
    public void shouldReturnWarningIfEndOfLineTerminatorNotFound() throws Exception {
        DataSource ds = BaseDataTest.createUnpooledDataSource(BaseDataTest.JPETSTORE_PROPERTIES);
        Connection conn = ds.getConnection();
        ScriptRunner runner = new ScriptRunner(conn);
        runner.setAutoCommit(true);
        runner.setStopOnError(false);
        runner.setErrorLogWriter(null);
        runner.setLogWriter(null);
        String resource = "org/apache/ibatis/jdbc/ScriptMissingEOLTerminator.sql";
        Reader reader = Resources.getResourceAsReader(resource);
        try {
            runner.runScript(reader);
            Assert.fail("Expected script runner to fail due to missing end of line terminator.");
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("end-of-line terminator"));
        }
    }

    @Test
    public void commentAferStatementDelimiterShouldNotCauseRunnerFail() throws Exception {
        DataSource ds = BaseDataTest.createUnpooledDataSource(BaseDataTest.JPETSTORE_PROPERTIES);
        Connection conn = ds.getConnection();
        ScriptRunner runner = new ScriptRunner(conn);
        runner.setAutoCommit(true);
        runner.setStopOnError(true);
        runner.setErrorLogWriter(null);
        runner.setLogWriter(null);
        runJPetStoreScripts(runner);
        String resource = "org/apache/ibatis/jdbc/ScriptCommentAfterEOLTerminator.sql";
        Reader reader = Resources.getResourceAsReader(resource);
        try {
            runner.runScript(reader);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
}

