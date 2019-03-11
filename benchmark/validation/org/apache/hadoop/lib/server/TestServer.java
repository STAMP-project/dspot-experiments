/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.lib.server;


import Server.Status.ADMIN;
import Server.Status.BOOTING;
import Server.Status.NORMAL;
import Server.Status.SHUTDOWN;
import Server.Status.SHUTTING_DOWN;
import Server.Status.UNDEF;
import ServerException.ERROR.S09;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.lib.lang.XException;
import org.apache.hadoop.test.HTestCase;
import org.apache.hadoop.test.TestDir;
import org.apache.hadoop.test.TestDirHelper;
import org.apache.hadoop.test.TestException;
import org.apache.hadoop.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;


public class TestServer extends HTestCase {
    @Test
    @TestDir
    public void constructorsGetters() throws Exception {
        Server server = new Server("server", TestServer.getAbsolutePath("/a"), TestServer.getAbsolutePath("/b"), TestServer.getAbsolutePath("/c"), TestServer.getAbsolutePath("/d"), new Configuration(false));
        Assert.assertEquals(server.getHomeDir(), TestServer.getAbsolutePath("/a"));
        Assert.assertEquals(server.getConfigDir(), TestServer.getAbsolutePath("/b"));
        Assert.assertEquals(server.getLogDir(), TestServer.getAbsolutePath("/c"));
        Assert.assertEquals(server.getTempDir(), TestServer.getAbsolutePath("/d"));
        Assert.assertEquals(server.getName(), "server");
        Assert.assertEquals(server.getPrefix(), "server");
        Assert.assertEquals(server.getPrefixedName("name"), "server.name");
        Assert.assertNotNull(server.getConfig());
        server = new Server("server", TestServer.getAbsolutePath("/a"), TestServer.getAbsolutePath("/b"), TestServer.getAbsolutePath("/c"), TestServer.getAbsolutePath("/d"));
        Assert.assertEquals(server.getHomeDir(), TestServer.getAbsolutePath("/a"));
        Assert.assertEquals(server.getConfigDir(), TestServer.getAbsolutePath("/b"));
        Assert.assertEquals(server.getLogDir(), TestServer.getAbsolutePath("/c"));
        Assert.assertEquals(server.getTempDir(), TestServer.getAbsolutePath("/d"));
        Assert.assertEquals(server.getName(), "server");
        Assert.assertEquals(server.getPrefix(), "server");
        Assert.assertEquals(server.getPrefixedName("name"), "server.name");
        Assert.assertNull(server.getConfig());
        server = new Server("server", TestDirHelper.getTestDir().getAbsolutePath(), new Configuration(false));
        Assert.assertEquals(server.getHomeDir(), TestDirHelper.getTestDir().getAbsolutePath());
        Assert.assertEquals(server.getConfigDir(), ((TestDirHelper.getTestDir()) + "/conf"));
        Assert.assertEquals(server.getLogDir(), ((TestDirHelper.getTestDir()) + "/log"));
        Assert.assertEquals(server.getTempDir(), ((TestDirHelper.getTestDir()) + "/temp"));
        Assert.assertEquals(server.getName(), "server");
        Assert.assertEquals(server.getPrefix(), "server");
        Assert.assertEquals(server.getPrefixedName("name"), "server.name");
        Assert.assertNotNull(server.getConfig());
        server = new Server("server", TestDirHelper.getTestDir().getAbsolutePath());
        Assert.assertEquals(server.getHomeDir(), TestDirHelper.getTestDir().getAbsolutePath());
        Assert.assertEquals(server.getConfigDir(), ((TestDirHelper.getTestDir()) + "/conf"));
        Assert.assertEquals(server.getLogDir(), ((TestDirHelper.getTestDir()) + "/log"));
        Assert.assertEquals(server.getTempDir(), ((TestDirHelper.getTestDir()) + "/temp"));
        Assert.assertEquals(server.getName(), "server");
        Assert.assertEquals(server.getPrefix(), "server");
        Assert.assertEquals(server.getPrefixedName("name"), "server.name");
        Assert.assertNull(server.getConfig());
    }

    @Test
    @TestException(exception = ServerException.class, msgRegExp = "S01.*")
    @TestDir
    public void initNoHomeDir() throws Exception {
        File homeDir = new File(TestDirHelper.getTestDir(), "home");
        Configuration conf = new Configuration(false);
        conf.set("server.services", TestServer.TestService.class.getName());
        Server server = new Server("server", homeDir.getAbsolutePath(), conf);
        server.init();
    }

    @Test
    @TestException(exception = ServerException.class, msgRegExp = "S02.*")
    @TestDir
    public void initHomeDirNotDir() throws Exception {
        File homeDir = new File(TestDirHelper.getTestDir(), "home");
        new FileOutputStream(homeDir).close();
        Configuration conf = new Configuration(false);
        conf.set("server.services", TestServer.TestService.class.getName());
        Server server = new Server("server", homeDir.getAbsolutePath(), conf);
        server.init();
    }

    @Test
    @TestException(exception = ServerException.class, msgRegExp = "S01.*")
    @TestDir
    public void initNoConfigDir() throws Exception {
        File homeDir = new File(TestDirHelper.getTestDir(), "home");
        Assert.assertTrue(homeDir.mkdir());
        Assert.assertTrue(new File(homeDir, "log").mkdir());
        Assert.assertTrue(new File(homeDir, "temp").mkdir());
        Configuration conf = new Configuration(false);
        conf.set("server.services", TestServer.TestService.class.getName());
        Server server = new Server("server", homeDir.getAbsolutePath(), conf);
        server.init();
    }

    @Test
    @TestException(exception = ServerException.class, msgRegExp = "S02.*")
    @TestDir
    public void initConfigDirNotDir() throws Exception {
        File homeDir = new File(TestDirHelper.getTestDir(), "home");
        Assert.assertTrue(homeDir.mkdir());
        Assert.assertTrue(new File(homeDir, "log").mkdir());
        Assert.assertTrue(new File(homeDir, "temp").mkdir());
        File configDir = new File(homeDir, "conf");
        new FileOutputStream(configDir).close();
        Configuration conf = new Configuration(false);
        conf.set("server.services", TestServer.TestService.class.getName());
        Server server = new Server("server", homeDir.getAbsolutePath(), conf);
        server.init();
    }

    @Test
    @TestException(exception = ServerException.class, msgRegExp = "S01.*")
    @TestDir
    public void initNoLogDir() throws Exception {
        File homeDir = new File(TestDirHelper.getTestDir(), "home");
        Assert.assertTrue(homeDir.mkdir());
        Assert.assertTrue(new File(homeDir, "conf").mkdir());
        Assert.assertTrue(new File(homeDir, "temp").mkdir());
        Configuration conf = new Configuration(false);
        conf.set("server.services", TestServer.TestService.class.getName());
        Server server = new Server("server", homeDir.getAbsolutePath(), conf);
        server.init();
    }

    @Test
    @TestException(exception = ServerException.class, msgRegExp = "S02.*")
    @TestDir
    public void initLogDirNotDir() throws Exception {
        File homeDir = new File(TestDirHelper.getTestDir(), "home");
        Assert.assertTrue(homeDir.mkdir());
        Assert.assertTrue(new File(homeDir, "conf").mkdir());
        Assert.assertTrue(new File(homeDir, "temp").mkdir());
        File logDir = new File(homeDir, "log");
        new FileOutputStream(logDir).close();
        Configuration conf = new Configuration(false);
        conf.set("server.services", TestServer.TestService.class.getName());
        Server server = new Server("server", homeDir.getAbsolutePath(), conf);
        server.init();
    }

    @Test
    @TestException(exception = ServerException.class, msgRegExp = "S01.*")
    @TestDir
    public void initNoTempDir() throws Exception {
        File homeDir = new File(TestDirHelper.getTestDir(), "home");
        Assert.assertTrue(homeDir.mkdir());
        Assert.assertTrue(new File(homeDir, "conf").mkdir());
        Assert.assertTrue(new File(homeDir, "log").mkdir());
        Configuration conf = new Configuration(false);
        conf.set("server.services", TestServer.TestService.class.getName());
        Server server = new Server("server", homeDir.getAbsolutePath(), conf);
        server.init();
    }

    @Test
    @TestException(exception = ServerException.class, msgRegExp = "S02.*")
    @TestDir
    public void initTempDirNotDir() throws Exception {
        File homeDir = new File(TestDirHelper.getTestDir(), "home");
        Assert.assertTrue(homeDir.mkdir());
        Assert.assertTrue(new File(homeDir, "conf").mkdir());
        Assert.assertTrue(new File(homeDir, "log").mkdir());
        File tempDir = new File(homeDir, "temp");
        new FileOutputStream(tempDir).close();
        Configuration conf = new Configuration(false);
        conf.set("server.services", TestServer.TestService.class.getName());
        Server server = new Server("server", homeDir.getAbsolutePath(), conf);
        server.init();
    }

    @Test
    @TestException(exception = ServerException.class, msgRegExp = "S05.*")
    @TestDir
    public void siteFileNotAFile() throws Exception {
        String homeDir = TestDirHelper.getTestDir().getAbsolutePath();
        File siteFile = new File(homeDir, "server-site.xml");
        Assert.assertTrue(siteFile.mkdir());
        Server server = new Server("server", homeDir, homeDir, homeDir, homeDir);
        server.init();
    }

    @Test
    @TestDir
    public void log4jFile() throws Exception {
        InputStream is = Server.getResource("default-log4j.properties");
        OutputStream os = new FileOutputStream(new File(TestDirHelper.getTestDir(), "server-log4j.properties"));
        IOUtils.copyBytes(is, os, 1024, true);
        Configuration conf = new Configuration(false);
        Server server = createServer(conf);
        server.init();
    }

    public static class LifeCycleService extends BaseService {
        public LifeCycleService() {
            super("lifecycle");
        }

        @Override
        protected void init() throws ServiceException {
            Assert.assertEquals(getServer().getStatus(), BOOTING);
        }

        @Override
        public void destroy() {
            Assert.assertEquals(getServer().getStatus(), SHUTTING_DOWN);
            super.destroy();
        }

        @Override
        public Class getInterface() {
            return TestServer.LifeCycleService.class;
        }
    }

    @Test
    @TestDir
    public void lifeCycle() throws Exception {
        Configuration conf = new Configuration(false);
        conf.set("server.services", TestServer.LifeCycleService.class.getName());
        Server server = createServer(conf);
        Assert.assertEquals(server.getStatus(), UNDEF);
        server.init();
        Assert.assertNotNull(server.get(TestServer.LifeCycleService.class));
        Assert.assertEquals(server.getStatus(), NORMAL);
        server.destroy();
        Assert.assertEquals(server.getStatus(), SHUTDOWN);
    }

    @Test
    @TestDir
    public void startWithStatusNotNormal() throws Exception {
        Configuration conf = new Configuration(false);
        conf.set("server.startup.status", "ADMIN");
        Server server = createServer(conf);
        server.init();
        Assert.assertEquals(server.getStatus(), ADMIN);
        server.destroy();
    }

    @Test(expected = IllegalArgumentException.class)
    @TestDir
    public void nonSeteableStatus() throws Exception {
        Configuration conf = new Configuration(false);
        Server server = createServer(conf);
        server.init();
        server.setStatus(SHUTDOWN);
    }

    public static class TestService implements Service {
        static List<String> LIFECYCLE = new ArrayList<String>();

        @Override
        public void init(Server server) throws ServiceException {
            TestServer.TestService.LIFECYCLE.add("init");
        }

        @Override
        public void postInit() throws ServiceException {
            TestServer.TestService.LIFECYCLE.add("postInit");
        }

        @Override
        public void destroy() {
            TestServer.TestService.LIFECYCLE.add("destroy");
        }

        @Override
        public Class[] getServiceDependencies() {
            return new Class[0];
        }

        @Override
        public Class getInterface() {
            return TestServer.TestService.class;
        }

        @Override
        public void serverStatusChange(Server.Status oldStatus, Server.Status newStatus) throws ServiceException {
            TestServer.TestService.LIFECYCLE.add("serverStatusChange");
        }
    }

    public static class TestServiceExceptionOnStatusChange extends TestServer.TestService {
        @Override
        public void serverStatusChange(Server.Status oldStatus, Server.Status newStatus) throws ServiceException {
            throw new RuntimeException();
        }
    }

    @Test
    @TestDir
    public void changeStatus() throws Exception {
        TestServer.TestService.LIFECYCLE.clear();
        Configuration conf = new Configuration(false);
        conf.set("server.services", TestServer.TestService.class.getName());
        Server server = createServer(conf);
        server.init();
        server.setStatus(ADMIN);
        Assert.assertTrue(TestServer.TestService.LIFECYCLE.contains("serverStatusChange"));
    }

    @Test
    @TestException(exception = ServerException.class, msgRegExp = "S11.*")
    @TestDir
    public void changeStatusServiceException() throws Exception {
        TestServer.TestService.LIFECYCLE.clear();
        Configuration conf = new Configuration(false);
        conf.set("server.services", TestServer.TestServiceExceptionOnStatusChange.class.getName());
        Server server = createServer(conf);
        server.init();
    }

    @Test
    @TestDir
    public void setSameStatus() throws Exception {
        Configuration conf = new Configuration(false);
        conf.set("server.services", TestServer.TestService.class.getName());
        Server server = createServer(conf);
        server.init();
        TestServer.TestService.LIFECYCLE.clear();
        server.setStatus(server.getStatus());
        Assert.assertFalse(TestServer.TestService.LIFECYCLE.contains("serverStatusChange"));
    }

    @Test
    @TestDir
    public void serviceLifeCycle() throws Exception {
        TestServer.TestService.LIFECYCLE.clear();
        Configuration conf = new Configuration(false);
        conf.set("server.services", TestServer.TestService.class.getName());
        Server server = createServer(conf);
        server.init();
        Assert.assertNotNull(server.get(TestServer.TestService.class));
        server.destroy();
        Assert.assertEquals(TestServer.TestService.LIFECYCLE, Arrays.asList("init", "postInit", "serverStatusChange", "destroy"));
    }

    @Test
    @TestDir
    public void loadingDefaultConfig() throws Exception {
        String dir = TestDirHelper.getTestDir().getAbsolutePath();
        Server server = new Server("testserver", dir, dir, dir, dir);
        server.init();
        Assert.assertEquals(server.getConfig().get("testserver.a"), "default");
    }

    @Test
    @TestDir
    public void loadingSiteConfig() throws Exception {
        String dir = TestDirHelper.getTestDir().getAbsolutePath();
        File configFile = new File(dir, "testserver-site.xml");
        Writer w = new FileWriter(configFile);
        w.write("<configuration><property><name>testserver.a</name><value>site</value></property></configuration>");
        w.close();
        Server server = new Server("testserver", dir, dir, dir, dir);
        server.init();
        Assert.assertEquals(server.getConfig().get("testserver.a"), "site");
    }

    @Test
    @TestDir
    public void loadingSysPropConfig() throws Exception {
        try {
            System.setProperty("testserver.a", "sysprop");
            String dir = TestDirHelper.getTestDir().getAbsolutePath();
            File configFile = new File(dir, "testserver-site.xml");
            Writer w = new FileWriter(configFile);
            w.write("<configuration><property><name>testserver.a</name><value>site</value></property></configuration>");
            w.close();
            Server server = new Server("testserver", dir, dir, dir, dir);
            server.init();
            Assert.assertEquals(server.getConfig().get("testserver.a"), "sysprop");
        } finally {
            System.getProperties().remove("testserver.a");
        }
    }

    @Test(expected = IllegalStateException.class)
    @TestDir
    public void illegalState1() throws Exception {
        Server server = new Server("server", TestDirHelper.getTestDir().getAbsolutePath(), new Configuration(false));
        server.destroy();
    }

    @Test(expected = IllegalStateException.class)
    @TestDir
    public void illegalState2() throws Exception {
        Server server = new Server("server", TestDirHelper.getTestDir().getAbsolutePath(), new Configuration(false));
        server.get(Object.class);
    }

    @Test(expected = IllegalStateException.class)
    @TestDir
    public void illegalState3() throws Exception {
        Server server = new Server("server", TestDirHelper.getTestDir().getAbsolutePath(), new Configuration(false));
        server.setService(null);
    }

    @Test(expected = IllegalStateException.class)
    @TestDir
    public void illegalState4() throws Exception {
        String dir = TestDirHelper.getTestDir().getAbsolutePath();
        Server server = new Server("server", dir, dir, dir, dir, new Configuration(false));
        server.init();
        server.init();
    }

    private static List<String> ORDER = new ArrayList<String>();

    public abstract static class MyService implements XException.ERROR , Service {
        private String id;

        private Class serviceInterface;

        private Class[] dependencies;

        private boolean failOnInit;

        private boolean failOnDestroy;

        protected MyService(String id, Class serviceInterface, Class[] dependencies, boolean failOnInit, boolean failOnDestroy) {
            this.id = id;
            this.serviceInterface = serviceInterface;
            this.dependencies = dependencies;
            this.failOnInit = failOnInit;
            this.failOnDestroy = failOnDestroy;
        }

        @Override
        public void init(Server server) throws ServiceException {
            TestServer.ORDER.add(((id) + ".init"));
            if (failOnInit) {
                throw new ServiceException(this);
            }
        }

        @Override
        public void postInit() throws ServiceException {
            TestServer.ORDER.add(((id) + ".postInit"));
        }

        @Override
        public String getTemplate() {
            return "";
        }

        @Override
        public void destroy() {
            TestServer.ORDER.add(((id) + ".destroy"));
            if (failOnDestroy) {
                throw new RuntimeException();
            }
        }

        @Override
        public Class[] getServiceDependencies() {
            return dependencies;
        }

        @Override
        public Class getInterface() {
            return serviceInterface;
        }

        @Override
        public void serverStatusChange(Server.Status oldStatus, Server.Status newStatus) throws ServiceException {
        }
    }

    public static class MyService1 extends TestServer.MyService {
        public MyService1() {
            super("s1", TestServer.MyService1.class, null, false, false);
        }

        protected MyService1(String id, Class serviceInterface, Class[] dependencies, boolean failOnInit, boolean failOnDestroy) {
            super(id, serviceInterface, dependencies, failOnInit, failOnDestroy);
        }
    }

    public static class MyService2 extends TestServer.MyService {
        public MyService2() {
            super("s2", TestServer.MyService2.class, null, true, false);
        }
    }

    public static class MyService3 extends TestServer.MyService {
        public MyService3() {
            super("s3", TestServer.MyService3.class, null, false, false);
        }
    }

    public static class MyService1a extends TestServer.MyService1 {
        public MyService1a() {
            super("s1a", TestServer.MyService1.class, null, false, false);
        }
    }

    public static class MyService4 extends TestServer.MyService1 {
        public MyService4() {
            super("s4a", String.class, null, false, false);
        }
    }

    public static class MyService5 extends TestServer.MyService {
        public MyService5() {
            super("s5", TestServer.MyService5.class, null, false, true);
        }

        protected MyService5(String id, Class serviceInterface, Class[] dependencies, boolean failOnInit, boolean failOnDestroy) {
            super(id, serviceInterface, dependencies, failOnInit, failOnDestroy);
        }
    }

    public static class MyService5a extends TestServer.MyService5 {
        public MyService5a() {
            super("s5a", TestServer.MyService5.class, null, false, false);
        }
    }

    public static class MyService6 extends TestServer.MyService {
        public MyService6() {
            super("s6", TestServer.MyService6.class, new Class[]{ TestServer.MyService1.class }, false, false);
        }
    }

    public static class MyService7 extends TestServer.MyService {
        @SuppressWarnings({ "UnusedParameters" })
        public MyService7(String foo) {
            super("s6", TestServer.MyService7.class, new Class[]{ TestServer.MyService1.class }, false, false);
        }
    }

    @Test
    @TestException(exception = ServerException.class, msgRegExp = "S08.*")
    @TestDir
    public void invalidSservice() throws Exception {
        String dir = TestDirHelper.getTestDir().getAbsolutePath();
        Configuration conf = new Configuration(false);
        conf.set("server.services", "foo");
        Server server = new Server("server", dir, dir, dir, dir, conf);
        server.init();
    }

    @Test
    @TestException(exception = ServerException.class, msgRegExp = "S07.*")
    @TestDir
    public void serviceWithNoDefaultConstructor() throws Exception {
        String dir = TestDirHelper.getTestDir().getAbsolutePath();
        Configuration conf = new Configuration(false);
        conf.set("server.services", TestServer.MyService7.class.getName());
        Server server = new Server("server", dir, dir, dir, dir, conf);
        server.init();
    }

    @Test
    @TestException(exception = ServerException.class, msgRegExp = "S04.*")
    @TestDir
    public void serviceNotImplementingServiceInterface() throws Exception {
        String dir = TestDirHelper.getTestDir().getAbsolutePath();
        Configuration conf = new Configuration(false);
        conf.set("server.services", TestServer.MyService4.class.getName());
        Server server = new Server("server", dir, dir, dir, dir, conf);
        server.init();
    }

    @Test
    @TestException(exception = ServerException.class, msgRegExp = "S10.*")
    @TestDir
    public void serviceWithMissingDependency() throws Exception {
        String dir = TestDirHelper.getTestDir().getAbsolutePath();
        Configuration conf = new Configuration(false);
        String services = StringUtils.join(",", Arrays.asList(TestServer.MyService3.class.getName(), TestServer.MyService6.class.getName()));
        conf.set("server.services", services);
        Server server = new Server("server", dir, dir, dir, dir, conf);
        server.init();
    }

    @Test
    @TestDir
    public void services() throws Exception {
        String dir = TestDirHelper.getTestDir().getAbsolutePath();
        Configuration conf;
        Server server;
        // no services
        TestServer.ORDER.clear();
        conf = new Configuration(false);
        server = new Server("server", dir, dir, dir, dir, conf);
        server.init();
        Assert.assertEquals(TestServer.ORDER.size(), 0);
        // 2 services init/destroy
        TestServer.ORDER.clear();
        String services = StringUtils.join(",", Arrays.asList(TestServer.MyService1.class.getName(), TestServer.MyService3.class.getName()));
        conf = new Configuration(false);
        conf.set("server.services", services);
        server = new Server("server", dir, dir, dir, dir, conf);
        server.init();
        Assert.assertEquals(server.get(TestServer.MyService1.class).getInterface(), TestServer.MyService1.class);
        Assert.assertEquals(server.get(TestServer.MyService3.class).getInterface(), TestServer.MyService3.class);
        Assert.assertEquals(TestServer.ORDER.size(), 4);
        Assert.assertEquals(TestServer.ORDER.get(0), "s1.init");
        Assert.assertEquals(TestServer.ORDER.get(1), "s3.init");
        Assert.assertEquals(TestServer.ORDER.get(2), "s1.postInit");
        Assert.assertEquals(TestServer.ORDER.get(3), "s3.postInit");
        server.destroy();
        Assert.assertEquals(TestServer.ORDER.size(), 6);
        Assert.assertEquals(TestServer.ORDER.get(4), "s3.destroy");
        Assert.assertEquals(TestServer.ORDER.get(5), "s1.destroy");
        // 3 services, 2nd one fails on init
        TestServer.ORDER.clear();
        services = StringUtils.join(",", Arrays.asList(TestServer.MyService1.class.getName(), TestServer.MyService2.class.getName(), TestServer.MyService3.class.getName()));
        conf = new Configuration(false);
        conf.set("server.services", services);
        server = new Server("server", dir, dir, dir, dir, conf);
        try {
            server.init();
            Assert.fail();
        } catch (ServerException ex) {
            Assert.assertEquals(TestServer.MyService2.class, ex.getError().getClass());
        } catch (Exception ex) {
            Assert.fail();
        }
        Assert.assertEquals(TestServer.ORDER.size(), 3);
        Assert.assertEquals(TestServer.ORDER.get(0), "s1.init");
        Assert.assertEquals(TestServer.ORDER.get(1), "s2.init");
        Assert.assertEquals(TestServer.ORDER.get(2), "s1.destroy");
        // 2 services one fails on destroy
        TestServer.ORDER.clear();
        services = StringUtils.join(",", Arrays.asList(TestServer.MyService1.class.getName(), TestServer.MyService5.class.getName()));
        conf = new Configuration(false);
        conf.set("server.services", services);
        server = new Server("server", dir, dir, dir, dir, conf);
        server.init();
        Assert.assertEquals(TestServer.ORDER.size(), 4);
        Assert.assertEquals(TestServer.ORDER.get(0), "s1.init");
        Assert.assertEquals(TestServer.ORDER.get(1), "s5.init");
        Assert.assertEquals(TestServer.ORDER.get(2), "s1.postInit");
        Assert.assertEquals(TestServer.ORDER.get(3), "s5.postInit");
        server.destroy();
        Assert.assertEquals(TestServer.ORDER.size(), 6);
        Assert.assertEquals(TestServer.ORDER.get(4), "s5.destroy");
        Assert.assertEquals(TestServer.ORDER.get(5), "s1.destroy");
        // service override via ext
        TestServer.ORDER.clear();
        services = StringUtils.join(",", Arrays.asList(TestServer.MyService1.class.getName(), TestServer.MyService3.class.getName()));
        String servicesExt = StringUtils.join(",", Arrays.asList(TestServer.MyService1a.class.getName()));
        conf = new Configuration(false);
        conf.set("server.services", services);
        conf.set("server.services.ext", servicesExt);
        server = new Server("server", dir, dir, dir, dir, conf);
        server.init();
        Assert.assertEquals(server.get(TestServer.MyService1.class).getClass(), TestServer.MyService1a.class);
        Assert.assertEquals(TestServer.ORDER.size(), 4);
        Assert.assertEquals(TestServer.ORDER.get(0), "s1a.init");
        Assert.assertEquals(TestServer.ORDER.get(1), "s3.init");
        Assert.assertEquals(TestServer.ORDER.get(2), "s1a.postInit");
        Assert.assertEquals(TestServer.ORDER.get(3), "s3.postInit");
        server.destroy();
        Assert.assertEquals(TestServer.ORDER.size(), 6);
        Assert.assertEquals(TestServer.ORDER.get(4), "s3.destroy");
        Assert.assertEquals(TestServer.ORDER.get(5), "s1a.destroy");
        // service override via setService
        TestServer.ORDER.clear();
        services = StringUtils.join(",", Arrays.asList(TestServer.MyService1.class.getName(), TestServer.MyService3.class.getName()));
        conf = new Configuration(false);
        conf.set("server.services", services);
        server = new Server("server", dir, dir, dir, dir, conf);
        server.init();
        server.setService(TestServer.MyService1a.class);
        Assert.assertEquals(TestServer.ORDER.size(), 6);
        Assert.assertEquals(TestServer.ORDER.get(4), "s1.destroy");
        Assert.assertEquals(TestServer.ORDER.get(5), "s1a.init");
        Assert.assertEquals(server.get(TestServer.MyService1.class).getClass(), TestServer.MyService1a.class);
        server.destroy();
        Assert.assertEquals(TestServer.ORDER.size(), 8);
        Assert.assertEquals(TestServer.ORDER.get(6), "s3.destroy");
        Assert.assertEquals(TestServer.ORDER.get(7), "s1a.destroy");
        // service add via setService
        TestServer.ORDER.clear();
        services = StringUtils.join(",", Arrays.asList(TestServer.MyService1.class.getName(), TestServer.MyService3.class.getName()));
        conf = new Configuration(false);
        conf.set("server.services", services);
        server = new Server("server", dir, dir, dir, dir, conf);
        server.init();
        server.setService(TestServer.MyService5.class);
        Assert.assertEquals(TestServer.ORDER.size(), 5);
        Assert.assertEquals(TestServer.ORDER.get(4), "s5.init");
        Assert.assertEquals(server.get(TestServer.MyService5.class).getClass(), TestServer.MyService5.class);
        server.destroy();
        Assert.assertEquals(TestServer.ORDER.size(), 8);
        Assert.assertEquals(TestServer.ORDER.get(5), "s5.destroy");
        Assert.assertEquals(TestServer.ORDER.get(6), "s3.destroy");
        Assert.assertEquals(TestServer.ORDER.get(7), "s1.destroy");
        // service add via setService exception
        TestServer.ORDER.clear();
        services = StringUtils.join(",", Arrays.asList(TestServer.MyService1.class.getName(), TestServer.MyService3.class.getName()));
        conf = new Configuration(false);
        conf.set("server.services", services);
        server = new Server("server", dir, dir, dir, dir, conf);
        server.init();
        try {
            server.setService(TestServer.MyService7.class);
            Assert.fail();
        } catch (ServerException ex) {
            Assert.assertEquals(S09, ex.getError());
        } catch (Exception ex) {
            Assert.fail();
        }
        Assert.assertEquals(TestServer.ORDER.size(), 6);
        Assert.assertEquals(TestServer.ORDER.get(4), "s3.destroy");
        Assert.assertEquals(TestServer.ORDER.get(5), "s1.destroy");
        // service with dependency
        TestServer.ORDER.clear();
        services = StringUtils.join(",", Arrays.asList(TestServer.MyService1.class.getName(), TestServer.MyService6.class.getName()));
        conf = new Configuration(false);
        conf.set("server.services", services);
        server = new Server("server", dir, dir, dir, dir, conf);
        server.init();
        Assert.assertEquals(server.get(TestServer.MyService1.class).getInterface(), TestServer.MyService1.class);
        Assert.assertEquals(server.get(TestServer.MyService6.class).getInterface(), TestServer.MyService6.class);
        server.destroy();
    }
}

