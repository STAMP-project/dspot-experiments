package com.alibaba.druid.bvt.log;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.proxy.DruidDriver;
import com.alibaba.druid.util.Utils;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.sql.Connection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import junit.framework.TestCase;


public class LoggerTest extends TestCase {
    private static ProtectionDomain DOMAIN;

    private ClassLoader contextClassLoader;

    private DruidDataSource dataSource;

    static {
        LoggerTest.DOMAIN = ((ProtectionDomain) (AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
                return LoggerTest.TestLoader.class.getProtectionDomain();
            }
        })));
    }

    public static class TestLoader extends ClassLoader {
        private ClassLoader loader;

        private Set<String> definedSet = new HashSet<String>();

        public TestLoader() {
            super(null);
            loader = DruidDriver.class.getClassLoader();
        }

        public URL getResource(String name) {
            return loader.getResource(name);
        }

        public Enumeration<URL> getResources(String name) throws IOException {
            return loader.getResources(name);
        }

        public Class<?> loadClass(String name) throws ClassNotFoundException {
            if (name.startsWith("java")) {
                return loader.loadClass(name);
            }
            if (definedSet.contains(name)) {
                return super.loadClass(name);
            }
            String resourceName = (name.replace('.', '/')) + ".class";
            InputStream is = loader.getResourceAsStream(resourceName);
            if (is == null) {
                throw new ClassNotFoundException();
            }
            try {
                byte[] bytes = Utils.readByteArray(is);
                this.defineClass(name, bytes, 0, bytes.length, LoggerTest.DOMAIN);
                definedSet.add(name);
            } catch (IOException e) {
                throw new ClassNotFoundException(e.getMessage(), e);
            }
            try {
                is.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Class<?> clazz = super.loadClass(name);
            return clazz;
        }
    }

    public void test_log() throws Exception {
        LoggerTest.TestLoader classLoader = new LoggerTest.TestLoader();
        Thread.currentThread().setContextClassLoader(classLoader);
        dataSource = new DruidDataSource();
        dataSource.setFilters("log");
        dataSource.setUrl("jdbc:mock:xx");
        Connection conn = dataSource.getConnection();
        conn.close();
    }
}

