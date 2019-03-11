/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.shardingsphere.dbtest.engine.dml;


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import javax.xml.bind.JAXBException;
import org.apache.shardingsphere.dbtest.cases.assertion.root.IntegrateTestCase;
import org.apache.shardingsphere.dbtest.cases.assertion.root.IntegrateTestCaseAssertion;
import org.apache.shardingsphere.dbtest.engine.BatchIntegrateTest;
import org.apache.shardingsphere.dbtest.env.DatabaseTypeEnvironment;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class BatchDMLIntegrateTest extends BatchIntegrateTest {
    private final IntegrateTestCase integrateTestCase;

    public BatchDMLIntegrateTest(final String sqlCaseId, final IntegrateTestCase integrateTestCase, final String shardingRuleType, final DatabaseTypeEnvironment databaseTypeEnvironment) throws IOException, SQLException, JAXBException {
        super(sqlCaseId, integrateTestCase, shardingRuleType, databaseTypeEnvironment);
        this.integrateTestCase = integrateTestCase;
    }

    @Test
    public void assertExecuteBatch() throws IOException, SQLException, ParseException, JAXBException {
        // TODO fix masterslave
        if ((!(getDatabaseTypeEnvironment().isEnabled())) || ("masterslave".equals(getShardingRuleType()))) {
            return;
        }
        int[] actualUpdateCounts;
        try (Connection connection = getDataSource().getConnection()) {
            actualUpdateCounts = executeBatchForPreparedStatement(connection);
        }
        assertDataSet(actualUpdateCounts);
    }

    @Test
    public void assertClearBatch() throws SQLException, ParseException {
        // TODO fix masterslave
        if ((!(getDatabaseTypeEnvironment().isEnabled())) || ("masterslave".equals(getShardingRuleType()))) {
            return;
        }
        try (Connection connection = getDataSource().getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(getSql())) {
                for (IntegrateTestCaseAssertion each : integrateTestCase.getIntegrateTestCaseAssertions()) {
                    addBatch(preparedStatement, each);
                }
                preparedStatement.clearBatch();
                Assert.assertThat(preparedStatement.executeBatch().length, CoreMatchers.is(0));
            }
        }
    }
}

