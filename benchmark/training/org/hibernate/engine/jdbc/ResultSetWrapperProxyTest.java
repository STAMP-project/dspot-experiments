/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.engine.jdbc;


import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.Test;
import org.mockito.Mockito;


public class ResultSetWrapperProxyTest {
    private ResultSet resultSet;

    private ResultSet resultSetProxy;

    @Test
    public void testRedirectedGetMethod() throws SQLException {
        resultSetProxy.getBigDecimal("myColumn");
        Mockito.verify(resultSet, Mockito.times(1)).getBigDecimal(1);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testRedirectedGetMethodWithAdditionalParameters() throws SQLException {
        resultSetProxy.getBigDecimal("myColumn", 8);
        Mockito.verify(resultSet, Mockito.times(1)).getBigDecimal(1, 8);
    }

    @Test
    public void testRedirectedUpdateMethod() throws SQLException {
        resultSetProxy.updateInt("myColumn", 19);
        Mockito.verify(resultSet, Mockito.times(1)).updateInt(1, 19);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testIntMethods() throws SQLException {
        resultSetProxy.getBigDecimal(3);
        Mockito.verify(resultSet, Mockito.times(1)).getBigDecimal(3);
        resultSetProxy.getBigDecimal(13, 8);
        Mockito.verify(resultSet, Mockito.times(1)).getBigDecimal(13, 8);
        resultSetProxy.updateInt(23, 19);
        Mockito.verify(resultSet, Mockito.times(1)).updateInt(23, 19);
    }

    @Test
    public void testStandardMethod() throws SQLException {
        resultSetProxy.getFetchSize();
        Mockito.verify(resultSet, Mockito.times(1)).getFetchSize();
    }
}

