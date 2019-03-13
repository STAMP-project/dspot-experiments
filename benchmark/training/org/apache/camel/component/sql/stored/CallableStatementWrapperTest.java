/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.sql.stored;


import java.sql.SQLException;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.component.sql.stored.template.TemplateParser;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;


public class CallableStatementWrapperTest extends CamelTestSupport {
    private TemplateParser templateParser;

    private EmbeddedDatabase db;

    private JdbcTemplate jdbcTemplate;

    private CallableStatementWrapperFactory factory;

    @Test
    public void shouldExecuteStoredProcedure() throws Exception {
        CallableStatementWrapper wrapper = new CallableStatementWrapper(("SUBNUMBERS" + "(INTEGER ${header.v1},INTEGER ${header.v2},OUT INTEGER resultofsub)"), factory);
        final Exchange exchange = createExchangeWithBody(null);
        exchange.getIn().setHeader("v1", 1);
        exchange.getIn().setHeader("v2", 2);
        wrapper.call(new WrapperExecuteCallback() {
            @Override
            public void execute(StatementWrapper statementWrapper) throws SQLException, DataAccessException {
                statementWrapper.populateStatement(null, exchange);
                Map resultOfQuery = ((Map) (statementWrapper.executeStatement()));
                Assert.assertEquals((-1), resultOfQuery.get("resultofsub"));
            }
        });
    }

    @Test
    public void shouldExecuteStoredFunction() throws Exception {
        CallableStatementWrapperFactory factory = new CallableStatementWrapperFactory(jdbcTemplate, templateParser, true);
        CallableStatementWrapper wrapper = new CallableStatementWrapper(("SUBNUMBERS_FUNCTION" + "(OUT INTEGER resultofsub, INTEGER ${header.v1},INTEGER ${header.v2})"), factory);
        final Exchange exchange = createExchangeWithBody(null);
        exchange.getIn().setHeader("v1", 1);
        exchange.getIn().setHeader("v2", 2);
        wrapper.call(new WrapperExecuteCallback() {
            @Override
            public void execute(StatementWrapper statementWrapper) throws SQLException, DataAccessException {
                statementWrapper.populateStatement(null, exchange);
                Map resultOfQuery = ((Map) (statementWrapper.executeStatement()));
                Assert.assertEquals((-1), resultOfQuery.get("resultofsub"));
            }
        });
    }

    @Test
    public void shouldExecuteNilacidProcedure() throws Exception {
        CallableStatementWrapper wrapper = new CallableStatementWrapper("NILADIC()", factory);
        wrapper.call(new WrapperExecuteCallback() {
            @Override
            public void execute(StatementWrapper statementWrapper) throws SQLException, DataAccessException {
                statementWrapper.populateStatement(null, null);
                Map result = ((Map) (statementWrapper.executeStatement()));
                // no output parameter in stored procedure NILADIC()
                // Spring sets #update-count-1
                assertNotNull(result.get("#update-count-1"));
            }
        });
    }
}

