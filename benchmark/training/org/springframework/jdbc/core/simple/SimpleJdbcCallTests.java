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
package org.springframework.jdbc.core.simple;


import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Types;
import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;


/**
 * Tests for {@link SimpleJdbcCall}.
 *
 * @author Thomas Risberg
 * @author Kiril Nugmanov
 */
public class SimpleJdbcCallTests {
    private Connection connection;

    private DatabaseMetaData databaseMetaData;

    private DataSource dataSource;

    private CallableStatement callableStatement;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testNoSuchStoredProcedure() throws Exception {
        final String NO_SUCH_PROC = "x";
        SQLException sqlException = new SQLException("Syntax error or access violation exception", "42000");
        BDDMockito.given(databaseMetaData.getDatabaseProductName()).willReturn("MyDB");
        BDDMockito.given(databaseMetaData.getDatabaseProductName()).willReturn("MyDB");
        BDDMockito.given(databaseMetaData.getUserName()).willReturn("me");
        BDDMockito.given(databaseMetaData.storesLowerCaseIdentifiers()).willReturn(true);
        BDDMockito.given(callableStatement.execute()).willThrow(sqlException);
        BDDMockito.given(connection.prepareCall((("{call " + NO_SUCH_PROC) + "()}"))).willReturn(callableStatement);
        SimpleJdbcCall sproc = new SimpleJdbcCall(dataSource).withProcedureName(NO_SUCH_PROC);
        thrown.expect(BadSqlGrammarException.class);
        thrown.expect(exceptionCause(sameInstance(sqlException)));
        try {
            sproc.execute();
        } finally {
            Mockito.verify(callableStatement).close();
            Mockito.verify(connection, Mockito.atLeastOnce()).close();
        }
    }

    @Test
    public void testUnnamedParameterHandling() throws Exception {
        final String MY_PROC = "my_proc";
        SimpleJdbcCall sproc = new SimpleJdbcCall(dataSource).withProcedureName(MY_PROC);
        // Shouldn't succeed in adding unnamed parameter
        thrown.expect(InvalidDataAccessApiUsageException.class);
        sproc.addDeclaredParameter(new SqlParameter(1));
    }

    @Test
    public void testAddInvoiceProcWithoutMetaDataUsingMapParamSource() throws Exception {
        initializeAddInvoiceWithoutMetaData(false);
        SimpleJdbcCall adder = new SimpleJdbcCall(dataSource).withProcedureName("add_invoice");
        adder.declareParameters(new SqlParameter("amount", Types.INTEGER), new SqlParameter("custid", Types.INTEGER), new SqlOutParameter("newid", Types.INTEGER));
        Number newId = adder.executeObject(Number.class, new MapSqlParameterSource().addValue("amount", 1103).addValue("custid", 3));
        Assert.assertEquals(4, newId.intValue());
        verifyAddInvoiceWithoutMetaData(false);
        Mockito.verify(connection, Mockito.atLeastOnce()).close();
    }

    @Test
    public void testAddInvoiceProcWithoutMetaDataUsingArrayParams() throws Exception {
        initializeAddInvoiceWithoutMetaData(false);
        SimpleJdbcCall adder = new SimpleJdbcCall(dataSource).withProcedureName("add_invoice");
        adder.declareParameters(new SqlParameter("amount", Types.INTEGER), new SqlParameter("custid", Types.INTEGER), new SqlOutParameter("newid", Types.INTEGER));
        Number newId = adder.executeObject(Number.class, 1103, 3);
        Assert.assertEquals(4, newId.intValue());
        verifyAddInvoiceWithoutMetaData(false);
        Mockito.verify(connection, Mockito.atLeastOnce()).close();
    }

    @Test
    public void testAddInvoiceProcWithMetaDataUsingMapParamSource() throws Exception {
        initializeAddInvoiceWithMetaData(false);
        SimpleJdbcCall adder = new SimpleJdbcCall(dataSource).withProcedureName("add_invoice");
        Number newId = adder.executeObject(Number.class, new MapSqlParameterSource().addValue("amount", 1103).addValue("custid", 3));
        Assert.assertEquals(4, newId.intValue());
        verifyAddInvoiceWithMetaData(false);
        Mockito.verify(connection, Mockito.atLeastOnce()).close();
    }

    @Test
    public void testAddInvoiceProcWithMetaDataUsingArrayParams() throws Exception {
        initializeAddInvoiceWithMetaData(false);
        SimpleJdbcCall adder = new SimpleJdbcCall(dataSource).withProcedureName("add_invoice");
        Number newId = adder.executeObject(Number.class, 1103, 3);
        Assert.assertEquals(4, newId.intValue());
        verifyAddInvoiceWithMetaData(false);
        Mockito.verify(connection, Mockito.atLeastOnce()).close();
    }

    @Test
    public void testAddInvoiceFuncWithoutMetaDataUsingMapParamSource() throws Exception {
        initializeAddInvoiceWithoutMetaData(true);
        SimpleJdbcCall adder = new SimpleJdbcCall(dataSource).withFunctionName("add_invoice");
        adder.declareParameters(new SqlOutParameter("return", Types.INTEGER), new SqlParameter("amount", Types.INTEGER), new SqlParameter("custid", Types.INTEGER));
        Number newId = adder.executeFunction(Number.class, new MapSqlParameterSource().addValue("amount", 1103).addValue("custid", 3));
        Assert.assertEquals(4, newId.intValue());
        verifyAddInvoiceWithoutMetaData(true);
        Mockito.verify(connection, Mockito.atLeastOnce()).close();
    }

    @Test
    public void testAddInvoiceFuncWithoutMetaDataUsingArrayParams() throws Exception {
        initializeAddInvoiceWithoutMetaData(true);
        SimpleJdbcCall adder = new SimpleJdbcCall(dataSource).withFunctionName("add_invoice");
        adder.declareParameters(new SqlOutParameter("return", Types.INTEGER), new SqlParameter("amount", Types.INTEGER), new SqlParameter("custid", Types.INTEGER));
        Number newId = adder.executeFunction(Number.class, 1103, 3);
        Assert.assertEquals(4, newId.intValue());
        verifyAddInvoiceWithoutMetaData(true);
        Mockito.verify(connection, Mockito.atLeastOnce()).close();
    }

    @Test
    public void testAddInvoiceFuncWithMetaDataUsingMapParamSource() throws Exception {
        initializeAddInvoiceWithMetaData(true);
        SimpleJdbcCall adder = new SimpleJdbcCall(dataSource).withFunctionName("add_invoice");
        Number newId = adder.executeFunction(Number.class, new MapSqlParameterSource().addValue("amount", 1103).addValue("custid", 3));
        Assert.assertEquals(4, newId.intValue());
        verifyAddInvoiceWithMetaData(true);
        Mockito.verify(connection, Mockito.atLeastOnce()).close();
    }

    @Test
    public void testAddInvoiceFuncWithMetaDataUsingArrayParams() throws Exception {
        initializeAddInvoiceWithMetaData(true);
        SimpleJdbcCall adder = new SimpleJdbcCall(dataSource).withFunctionName("add_invoice");
        Number newId = adder.executeFunction(Number.class, 1103, 3);
        Assert.assertEquals(4, newId.intValue());
        verifyAddInvoiceWithMetaData(true);
        Mockito.verify(connection, Mockito.atLeastOnce()).close();
    }

    @Test
    public void testCorrectFunctionStatement() throws Exception {
        initializeAddInvoiceWithMetaData(true);
        SimpleJdbcCall adder = new SimpleJdbcCall(dataSource).withFunctionName("add_invoice");
        adder.compile();
        verifyStatement(adder, "{? = call ADD_INVOICE(?, ?)}");
    }

    @Test
    public void testCorrectFunctionStatementNamed() throws Exception {
        initializeAddInvoiceWithMetaData(true);
        SimpleJdbcCall adder = new SimpleJdbcCall(dataSource).withNamedBinding().withFunctionName("add_invoice");
        adder.compile();
        verifyStatement(adder, "{? = call ADD_INVOICE(AMOUNT => ?, CUSTID => ?)}");
    }

    @Test
    public void testCorrectProcedureStatementNamed() throws Exception {
        initializeAddInvoiceWithMetaData(false);
        SimpleJdbcCall adder = new SimpleJdbcCall(dataSource).withNamedBinding().withProcedureName("add_invoice");
        adder.compile();
        verifyStatement(adder, "{call ADD_INVOICE(AMOUNT => ?, CUSTID => ?, NEWID => ?)}");
    }
}

