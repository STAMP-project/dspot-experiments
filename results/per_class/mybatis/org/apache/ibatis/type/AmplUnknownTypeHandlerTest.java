/**
 * Copyright 2009-2016 the original author or authors.
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


package org.apache.ibatis.type;


public class AmplUnknownTypeHandlerTest extends org.apache.ibatis.type.BaseTypeHandlerTest {
    private static final org.apache.ibatis.type.TypeHandler<java.lang.Object> TYPE_HANDLER = org.mockito.Mockito.spy(new org.apache.ibatis.type.UnknownTypeHandler(new org.apache.ibatis.type.TypeHandlerRegistry()));

    @java.lang.Override
    @org.junit.Test
    public void shouldSetParameter() throws java.lang.Exception {
        org.apache.ibatis.type.AmplUnknownTypeHandlerTest.TYPE_HANDLER.setParameter(ps, 1, "Hello", null);
        org.mockito.Mockito.verify(ps).setString(1, "Hello");
    }

    @java.lang.Override
    @org.junit.Test
    public void shouldGetResultFromResultSetByName() throws java.lang.Exception {
        org.mockito.Mockito.when(rs.getMetaData()).thenReturn(rsmd);
        org.mockito.Mockito.when(rsmd.getColumnCount()).thenReturn(1);
        org.mockito.Mockito.when(rsmd.getColumnName(1)).thenReturn("column");
        org.mockito.Mockito.when(rsmd.getColumnClassName(1)).thenReturn(java.lang.String.class.getName());
        org.mockito.Mockito.when(rsmd.getColumnType(1)).thenReturn(org.apache.ibatis.type.JdbcType.VARCHAR.TYPE_CODE);
        org.mockito.Mockito.when(rs.getString("column")).thenReturn("Hello");
        org.mockito.Mockito.when(rs.wasNull()).thenReturn(false);
        org.junit.Assert.assertEquals("Hello", org.apache.ibatis.type.AmplUnknownTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
    }

    @java.lang.Override
    public void shouldGetResultNullFromResultSetByName() throws java.lang.Exception {
        // Unnecessary
    }

    @java.lang.Override
    @org.junit.Test
    public void shouldGetResultFromResultSetByPosition() throws java.lang.Exception {
        org.mockito.Mockito.when(rs.getMetaData()).thenReturn(rsmd);
        org.mockito.Mockito.when(rsmd.getColumnClassName(1)).thenReturn(java.lang.String.class.getName());
        org.mockito.Mockito.when(rsmd.getColumnType(1)).thenReturn(org.apache.ibatis.type.JdbcType.VARCHAR.TYPE_CODE);
        org.mockito.Mockito.when(rs.getString(1)).thenReturn("Hello");
        org.mockito.Mockito.when(rs.wasNull()).thenReturn(false);
        org.junit.Assert.assertEquals("Hello", org.apache.ibatis.type.AmplUnknownTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1));
    }

    @java.lang.Override
    public void shouldGetResultNullFromResultSetByPosition() throws java.lang.Exception {
        // Unnecessary
    }

    @java.lang.Override
    @org.junit.Test
    public void shouldGetResultFromCallableStatement() throws java.lang.Exception {
        org.mockito.Mockito.when(cs.getObject(1)).thenReturn("Hello");
        org.mockito.Mockito.when(cs.wasNull()).thenReturn(false);
        org.junit.Assert.assertEquals("Hello", org.apache.ibatis.type.AmplUnknownTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
    }

    @java.lang.Override
    public void shouldGetResultNullFromCallableStatement() throws java.lang.Exception {
        // Unnecessary
    }

    @org.junit.Test
    public void setParameterWithNullParameter() throws java.lang.Exception {
        org.apache.ibatis.type.AmplUnknownTypeHandlerTest.TYPE_HANDLER.setParameter(ps, 0, null, org.apache.ibatis.type.JdbcType.INTEGER);
        org.mockito.Mockito.verify(ps).setNull(0, org.apache.ibatis.type.JdbcType.INTEGER.TYPE_CODE);
    }

    @org.junit.Test
    public void setParameterWithNullParameterThrowsException() throws java.sql.SQLException {
        org.mockito.Mockito.doThrow(new java.sql.SQLException("invalid column")).when(ps).setNull(1, org.apache.ibatis.type.JdbcType.INTEGER.TYPE_CODE);
        try {
            org.apache.ibatis.type.AmplUnknownTypeHandlerTest.TYPE_HANDLER.setParameter(ps, 1, null, org.apache.ibatis.type.JdbcType.INTEGER);
            org.junit.Assert.fail("Should have thrown a TypeException");
        } catch (java.lang.Exception e) {
            org.junit.Assert.assertTrue("Expected TypedException", (e instanceof org.apache.ibatis.type.TypeException));
            org.junit.Assert.assertTrue("Parameter index is in exception", e.getMessage().contains("parameter #1"));
        }
    }

    @org.junit.Test
    public void setParameterWithNonNullParameterThrowsException() throws java.sql.SQLException {
        org.mockito.Mockito.doThrow(new java.sql.SQLException("invalid column")).when(((org.apache.ibatis.type.UnknownTypeHandler) (org.apache.ibatis.type.AmplUnknownTypeHandlerTest.TYPE_HANDLER))).setNonNullParameter(ps, 1, 99, org.apache.ibatis.type.JdbcType.INTEGER);
        try {
            org.apache.ibatis.type.AmplUnknownTypeHandlerTest.TYPE_HANDLER.setParameter(ps, 1, 99, org.apache.ibatis.type.JdbcType.INTEGER);
            org.junit.Assert.fail("Should have thrown a TypeException");
        } catch (java.lang.Exception e) {
            org.junit.Assert.assertTrue("Expected TypedException", (e instanceof org.apache.ibatis.type.TypeException));
            org.junit.Assert.assertTrue("Parameter index is in exception", e.getMessage().contains("parameter #1"));
        }
    }

    @org.junit.Test
    public void getResultWithResultSetAndColumnNameThrowsException() throws java.sql.SQLException {
        org.mockito.Mockito.doThrow(new java.sql.SQLException("invalid column")).when(((org.apache.ibatis.type.UnknownTypeHandler) (org.apache.ibatis.type.AmplUnknownTypeHandlerTest.TYPE_HANDLER))).getNullableResult(rs, "foo");
        try {
            org.apache.ibatis.type.AmplUnknownTypeHandlerTest.TYPE_HANDLER.getResult(rs, "foo");
            org.junit.Assert.fail("Should have thrown a ResultMapException");
        } catch (java.lang.Exception e) {
            org.junit.Assert.assertTrue("Expected ResultMapException", (e instanceof org.apache.ibatis.executor.result.ResultMapException));
            org.junit.Assert.assertTrue("column name is not in exception", e.getMessage().contains("column 'foo'"));
        }
    }

    @org.junit.Test
    public void getResultWithResultSetAndColumnIndexThrowsException() throws java.sql.SQLException {
        org.mockito.Mockito.doThrow(new java.sql.SQLException("invalid column")).when(((org.apache.ibatis.type.UnknownTypeHandler) (org.apache.ibatis.type.AmplUnknownTypeHandlerTest.TYPE_HANDLER))).getNullableResult(rs, 1);
        try {
            org.apache.ibatis.type.AmplUnknownTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1);
            org.junit.Assert.fail("Should have thrown a ResultMapException");
        } catch (java.lang.Exception e) {
            org.junit.Assert.assertTrue("Expected ResultMapException", (e instanceof org.apache.ibatis.executor.result.ResultMapException));
            org.junit.Assert.assertTrue("column index is not in exception", e.getMessage().contains("column #1"));
        }
    }

    @org.junit.Test
    public void getResultWithCallableStatementAndColumnIndexThrowsException() throws java.sql.SQLException {
        org.mockito.Mockito.doThrow(new java.sql.SQLException("invalid column")).when(((org.apache.ibatis.type.UnknownTypeHandler) (org.apache.ibatis.type.AmplUnknownTypeHandlerTest.TYPE_HANDLER))).getNullableResult(cs, 1);
        try {
            org.apache.ibatis.type.AmplUnknownTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1);
            org.junit.Assert.fail("Should have thrown a ResultMapException");
        } catch (java.lang.Exception e) {
            org.junit.Assert.assertTrue("Expected ResultMapException", (e instanceof org.apache.ibatis.executor.result.ResultMapException));
            org.junit.Assert.assertTrue("column index is not in exception", e.getMessage().contains("column #1"));
        }
    }
}

