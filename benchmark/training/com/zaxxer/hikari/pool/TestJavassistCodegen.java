package com.zaxxer.hikari.pool;


import com.zaxxer.hikari.mocks.StubConnection;
import com.zaxxer.hikari.util.JavassistProxyFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Statement;
import org.junit.Assert;
import org.junit.Test;


public class TestJavassistCodegen {
    @Test
    public void testCodegen() throws Exception {
        String tmp = System.getProperty("java.io.tmpdir");
        JavassistProxyFactory.main((tmp + (tmp.endsWith("/") ? "" : "/")));
        Path base = Paths.get(tmp, "target/classes/com/zaxxer/hikari/pool".split("/"));
        Assert.assertTrue("", Files.isRegularFile(base.resolve("HikariProxyConnection.class")));
        Assert.assertTrue("", Files.isRegularFile(base.resolve("HikariProxyStatement.class")));
        Assert.assertTrue("", Files.isRegularFile(base.resolve("HikariProxyCallableStatement.class")));
        Assert.assertTrue("", Files.isRegularFile(base.resolve("HikariProxyPreparedStatement.class")));
        Assert.assertTrue("", Files.isRegularFile(base.resolve("HikariProxyResultSet.class")));
        Assert.assertTrue("", Files.isRegularFile(base.resolve("ProxyFactory.class")));
        TestElf.FauxWebClassLoader fauxClassLoader = new TestElf.FauxWebClassLoader();
        Class<?> proxyFactoryClass = fauxClassLoader.loadClass("com.zaxxer.hikari.pool.ProxyFactory");
        Connection connection = new StubConnection();
        Class<?> fastListClass = fauxClassLoader.loadClass("com.zaxxer.hikari.util.FastList");
        Object fastList = fastListClass.getConstructor(Class.class).newInstance(Statement.class);
        Object proxyConnection = /* poolEntry */
        /* leakTask */
        /* now */
        /* isReadOnly */
        /* isAutoCommit */
        getMethod(proxyFactoryClass, "getProxyConnection").invoke(null, null, connection, fastList, null, 0L, Boolean.FALSE, Boolean.FALSE);
        Assert.assertNotNull(proxyConnection);
        Object proxyStatement = getMethod(proxyConnection.getClass(), "createStatement", 0).invoke(proxyConnection);
        Assert.assertNotNull(proxyStatement);
    }
}

