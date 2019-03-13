/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.jdbc.env;


import DefaultSchemaNameResolver.INSTANCE;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.hibernate.dialect.Dialect;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
public class DefaultSchemaNameResolverTest {
    private static final String SCHEMA_NAME = "theSchemaName";

    private static final String GET_CURRENT_SCHEMA_NAME_COMMAND = "get the schema name";

    @Test
    public void testSecondConnectionDoesNotSupportGetSchemaName() throws SQLException {
        final Connection connectionSupportsGetSchemaName = DefaultSchemaNameResolverTest.ConnectionProxy.generateProxy(new DefaultSchemaNameResolverTest.ConnectionProxy(DefaultSchemaNameResolverTest.SCHEMA_NAME));
        String schemaName = INSTANCE.resolveSchemaName(connectionSupportsGetSchemaName, new Dialect() {});
        Assert.assertEquals(DefaultSchemaNameResolverTest.SCHEMA_NAME, schemaName);
        final Connection connectionNotSupportGetSchemaName = DefaultSchemaNameResolverTest.ConnectionProxy.generateProxy(new DefaultSchemaNameResolverTest.ConnectionProxy(null));
        schemaName = INSTANCE.resolveSchemaName(connectionNotSupportGetSchemaName, new Dialect() {
            @Override
            public String getCurrentSchemaCommand() {
                return DefaultSchemaNameResolverTest.GET_CURRENT_SCHEMA_NAME_COMMAND;
            }
        });
        Assert.assertEquals(DefaultSchemaNameResolverTest.SCHEMA_NAME, schemaName);
    }

    public static class ConnectionProxy implements InvocationHandler {
        private String schemaName;

        ConnectionProxy(String schemaName) {
            this.schemaName = schemaName;
        }

        public static Connection generateProxy(DefaultSchemaNameResolverTest.ConnectionProxy handler) {
            return ((Connection) (Proxy.newProxyInstance(DefaultSchemaNameResolverTest.ConnectionProxy.getProxyClassLoader(), new Class[]{ Connection.class }, handler)));
        }

        private static ClassLoader getProxyClassLoader() {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl == null) {
                cl = Connection.class.getClassLoader();
            }
            return cl;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ((method.getName().equals("getSchema")) && (args == null)) {
                if ((schemaName) != null) {
                    return schemaName;
                }
                throw new AbstractMethodError("getSchema is not implemented");
            } else
                if ((method.getName().equals("createStatement")) && (args == null)) {
                    return DefaultSchemaNameResolverTest.StatementProxy.generateProxy(new DefaultSchemaNameResolverTest.StatementProxy());
                }

            throw new UnsupportedOperationException(("Unexpected call ResultSet." + (method.getName())));
        }
    }

    public static class StatementProxy implements InvocationHandler {
        public static Statement generateProxy(DefaultSchemaNameResolverTest.StatementProxy handler) {
            return ((Statement) (Proxy.newProxyInstance(DefaultSchemaNameResolverTest.StatementProxy.getProxyClassLoader(), new Class[]{ Statement.class }, handler)));
        }

        private static ClassLoader getProxyClassLoader() {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl == null) {
                cl = Statement.class.getClassLoader();
            }
            return cl;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (((method.getName().equals("executeQuery")) && ((args.length) == 1)) && (DefaultSchemaNameResolverTest.GET_CURRENT_SCHEMA_NAME_COMMAND.equals(args[0]))) {
                return DefaultSchemaNameResolverTest.ResultSetProxy.generateProxy(new DefaultSchemaNameResolverTest.ResultSetProxy());
            }
            if ((method.getName().equals("close")) && (args == null)) {
                // nothing to do
                return null;
            }
            throw new UnsupportedOperationException(("Unexpected call Statement." + (method.getName())));
        }
    }

    public static class ResultSetProxy implements InvocationHandler {
        public static ResultSet generateProxy(DefaultSchemaNameResolverTest.ResultSetProxy handler) {
            return ((ResultSet) (Proxy.newProxyInstance(DefaultSchemaNameResolverTest.ResultSetProxy.getProxyClassLoader(), new Class[]{ ResultSet.class }, handler)));
        }

        private static ClassLoader getProxyClassLoader() {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl == null) {
                cl = ResultSet.class.getClassLoader();
            }
            return cl;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ((method.getName().equals("next")) && (args == null)) {
                return true;
            }
            if (((method.getName().equals("getString")) && ((args.length) == 1)) && (Integer.valueOf(1).equals(args[0]))) {
                return DefaultSchemaNameResolverTest.SCHEMA_NAME;
            }
            if ((method.getName().equals("close")) && (args == null)) {
                // nothing to do
                return null;
            }
            throw new UnsupportedOperationException(("Unexpected call ResultSet." + (method.getName())));
        }
    }
}

