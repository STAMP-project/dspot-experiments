/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.jdbc;


import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.concurrent.Semaphore;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ITTestShadedJar {
    private static final Logger logger = LoggerFactory.getLogger(ITTestShadedJar.class);

    private static DrillbitClassLoader drillbitLoader;

    private static ClassLoader rootClassLoader;

    private static int userPort;

    @ClassRule
    public static final TestWatcher testWatcher = new TestWatcher() {
        @Override
        protected void starting(Description description) {
            super.starting(description);
            try {
                ITTestShadedJar.drillbitLoader = new DrillbitClassLoader();
                ITTestShadedJar.drillbitLoader.loadClass("org.apache.commons.io.FileUtils");
                ITTestShadedJar.rootClassLoader = Thread.currentThread().getContextClassLoader();
                Class<?> clazz = ITTestShadedJar.drillbitLoader.loadClass("org.apache.drill.test.BaseTestQuery");
                Class<?> watcherClazz = ITTestShadedJar.drillbitLoader.loadClass("org.apache.drill.test.BaseDirTestWatcher");
                // Setup tmp dirs
                runMethod("starting", description);
                // Configure tmp dir
                Object watcher = clazz.getField("dirTestWatcher").get(null);
                Method method = watcherClazz.getDeclaredMethod("getTmpDir");
                File tmpDir = ((File) (method.invoke(watcher)));
                System.setProperty("DRILL_CONF_DIR", tmpDir.getAbsolutePath());
                // Start the drill cluster
                try {
                    ITTestShadedJar.runWithLoader("DrillbitStartThread", ITTestShadedJar.drillbitLoader);
                } catch (Exception e) {
                    ITTestShadedJar.printClassesLoaded("root", ITTestShadedJar.rootClassLoader);
                    throw e;
                }
                ITTestShadedJar.DrillbitStartThread.SEM.acquire();
                // After starting the drill cluster get the client port
                ITTestShadedJar.userPort = ((Integer) (clazz.getMethod("getUserPort").invoke(null)));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void finished(Description description) {
            super.finished(description);
            done();
            runMethod("finished", description);
        }

        @Override
        protected void failed(Throwable e, Description description) {
            super.failed(e, description);
            done();
            runMethod("failed", description);
        }

        private void done() {
            try {
                ITTestShadedJar.runWithLoader("DrillbitStopThread", ITTestShadedJar.drillbitLoader);
                ITTestShadedJar.DrillbitStopThread.SEM.acquire();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private void runMethod(String name, Description description) {
            try {
                Class<?> clazz = ITTestShadedJar.drillbitLoader.loadClass("org.apache.drill.test.BaseTestQuery");
                Class<?> watcherClazz = ITTestShadedJar.drillbitLoader.loadClass("org.junit.rules.TestWatcher");
                Object watcher = clazz.getField("dirTestWatcher").get(null);
                Method method = watcherClazz.getDeclaredMethod(name, Description.class);
                method.setAccessible(true);
                method.invoke(watcher, description);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };

    @Test
    public void testDatabaseVersion() throws Exception {
        final URLClassLoader loader = URLClassLoader.newInstance(new URL[]{ ITTestShadedJar.getJdbcUrl() });
        Class<?> clazz = loader.loadClass("org.apache.drill.jdbc.Driver");
        try {
            Driver driver = ((Driver) (clazz.newInstance()));
            try (Connection c = driver.connect(("jdbc:drill:drillbit=localhost:" + (ITTestShadedJar.userPort)), null)) {
                DatabaseMetaData metadata = c.getMetaData();
                Assert.assertEquals("Apache Drill JDBC Driver", metadata.getDriverName());
                Assert.assertEquals("Apache Drill Server", metadata.getDatabaseProductName());
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    @Test
    public void executeJdbcAllQuery() throws Exception {
        final URLClassLoader loader = URLClassLoader.newInstance(new URL[]{ ITTestShadedJar.getJdbcUrl() });
        Class<?> clazz = loader.loadClass("org.apache.drill.jdbc.Driver");
        try {
            Driver driver = ((Driver) (clazz.newInstance()));
            try (Connection c = driver.connect(("jdbc:drill:drillbit=localhost:" + (ITTestShadedJar.userPort)), null)) {
                ITTestShadedJar.printQuery(c, "select * from cp.`types.json`");
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public abstract static class AbstractLoaderThread extends Thread {
        private Exception ex;

        protected final ClassLoader loader;

        public AbstractLoaderThread(ClassLoader loader) {
            this.setContextClassLoader(loader);
            this.loader = loader;
        }

        @Override
        public final void run() {
            try {
                internalRun();
            } catch (Exception e) {
                this.ex = e;
            }
        }

        protected abstract void internalRun() throws Exception;

        public void go() throws Exception {
            start();
            join();
            if ((ex) != null) {
                throw ex;
            }
        }
    }

    public static class DrillbitStartThread extends ITTestShadedJar.AbstractLoaderThread {
        public static final Semaphore SEM = new Semaphore(0);

        public DrillbitStartThread(ClassLoader loader) {
            super(loader);
        }

        @Override
        protected void internalRun() throws Exception {
            Class<?> clazz = loader.loadClass("org.apache.drill.test.BaseTestQuery");
            clazz.getMethod("setupDefaultTestCluster").invoke(null);
            // loader.loadClass("org.apache.drill.exec.exception.SchemaChangeException");
            // execute a single query to make sure the drillbit is fully up
            clazz.getMethod("testNoResult", String.class, new Object[]{  }.getClass()).invoke(null, "select * from (VALUES 1)", new Object[]{  });
            ITTestShadedJar.DrillbitStartThread.SEM.release();
        }
    }

    public static class DrillbitStopThread extends ITTestShadedJar.AbstractLoaderThread {
        public static final Semaphore SEM = new Semaphore(0);

        public DrillbitStopThread(ClassLoader loader) {
            super(loader);
        }

        @Override
        protected void internalRun() throws Exception {
            Class<?> clazz = loader.loadClass("org.apache.drill.test.BaseTestQuery");
            clazz.getMethod("closeClient").invoke(null);
            ITTestShadedJar.DrillbitStopThread.SEM.release();
        }
    }
}

