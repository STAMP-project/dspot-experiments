/**
 * The MIT License
 * Copyright ? 2010 JmxTrans team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.googlecode.jmxtrans.model;


import com.googlecode.jmxtrans.connections.JMXConnection;
import com.googlecode.jmxtrans.connections.JmxConnectionProvider;
import com.googlecode.jmxtrans.test.RequiresIO;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import org.apache.commons.pool.KeyedObjectPool;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.InOrder;
import org.mockito.Mockito;


/**
 *
 *
 * @author lanyonm
 */
@Category(RequiresIO.class)
public class ServerTests {
    @Test
    public void testGetUrl() {
        // test with host and port
        Server server = Server.builder().setHost("mysys.mydomain").setPort("8004").setPool(ServerFixtures.createPool()).build();
        Assert.assertEquals("should be 'service:jmx:rmi:///jndi/rmi://mysys.mydomain:8004/jmxrmi'", "service:jmx:rmi:///jndi/rmi://mysys.mydomain:8004/jmxrmi", server.getUrl());
        // test with url
        server = Server.builder().setUrl("service:jmx:remoting-jmx://mysys.mydomain:8004").setPool(ServerFixtures.createPool()).build();
        Assert.assertEquals("should be 'service:jmx:remoting-jmx://mysys.mydomain:8004'", "service:jmx:remoting-jmx://mysys.mydomain:8004", server.getUrl());
        server = Server.builder().setUrl("service:jmx:rmi:///jndi/rmi://mysys.mydomain:8004/jmxrmi").setPool(ServerFixtures.createPool()).build();
        Assert.assertEquals("shold be 'service:jmx:rmi:///jndi/rmi://mysys.mydomain:8004/jmxrmi'", "service:jmx:rmi:///jndi/rmi://mysys.mydomain:8004/jmxrmi", server.getUrl());
    }

    @Test
    public void testGetHostAndPortFromUrl() {
        Server server = Server.builder().setUrl("service:jmx:remoting-jmx://mysys.mydomain:8004").setPool(ServerFixtures.createPool()).build();
        Assert.assertEquals("server host should be 'mysys.mydomain'", "mysys.mydomain", server.getHost());
        Assert.assertEquals("server port should be '8004'", "8004", server.getPort());
        // test with a different url
        server = Server.builder().setUrl("service:jmx:rmi:///jndi/rmi://mysys.mydomain:8004/jmxrmi").setPool(ServerFixtures.createPool()).build();
        Assert.assertEquals("server host should be 'mysys.mydomain'", "mysys.mydomain", server.getHost());
        Assert.assertEquals("server port should be '8004'", "8004", server.getPort());
    }

    @Test
    public void testGetLabel() {
        Server server = Server.builder().setAlias("alias").setHost("host").setPool(ServerFixtures.createPool()).build();
        Assert.assertEquals("server label should be 'alias'", "alias", server.getLabel());
        server = Server.builder().setHost("host").setPool(ServerFixtures.createPool()).build();
        Assert.assertEquals("server label should be 'host'", "host", server.getLabel());
    }

    @Test
    public void testEquals() {
        Server s1 = Server.builder().setAlias("alias").setHost("host").setPort("8008").setPool(ServerFixtures.createPool()).setCronExpression("cron").setNumQueryThreads(123).setPassword("pass").setUsername("user").build();
        Server s2 = Server.builder().setAlias("alias").setHost("host").setPort("8008").setPool(ServerFixtures.createPool()).setCronExpression("cron").setNumQueryThreads(123).setPassword("pass").setUsername("user").build();
        Server s3 = Server.builder().setAlias("alias").setHost("host3").setPort("8008").setPool(ServerFixtures.createPool()).setCronExpression("cron").setNumQueryThreads(123).setPassword("pass").setUsername("user").build();
        Assert.assertFalse(s1.equals(null));
        Assert.assertEquals(s1, s1);
        Assert.assertFalse(s1.equals("hi"));
        Assert.assertEquals(s1, s2);
        Assert.assertTrue(s1.equals(s2));
        Assert.assertNotEquals(s1, s3);
    }

    @Test
    public void testEquals_forPid() {
        Server s1 = Server.builder().setPid("1").setPool(ServerFixtures.createPool()).build();
        Server s2 = Server.builder().setPid("2").setPool(ServerFixtures.createPool()).build();
        Server s3 = Server.builder(s1).build();
        Assert.assertEquals(s1, s3);
        Assert.assertNotEquals(s1, s2);
    }

    @Test
    public void testHashCode() {
        Server s1 = Server.builder().setUrl("service:jmx:remoting-jmx://mysys.mydomain:8004").setPool(ServerFixtures.createPool()).build();
        Server s2 = Server.builder().setUrl("service:jmx:remoting-jmx://mysys.mydomain:8004").setPool(ServerFixtures.createPool()).build();
        Server s3 = Server.builder().setUrl("service:jmx:remoting-jmx://mysys3.mydomain:8004").setPool(ServerFixtures.createPool()).build();
        Assert.assertEquals(s1.hashCode(), s2.hashCode());
        Assert.assertFalse(((s1.hashCode()) == (s3.hashCode())));
    }

    @Test
    public void testToString() {
        Server s1 = Server.builder().setPool(ServerFixtures.createPool()).setPid("123").setCronExpression("cron").setNumQueryThreads(2).build();
        Server s2 = Server.builder().setHost("mydomain").setPort("1234").setPool(ServerFixtures.createPool()).setUrl("service:jmx:remoting-jmx://mysys.mydomain:8004").setCronExpression("cron").setNumQueryThreads(2).build();
        assertThat(s1.toString()).contains("pid=123").contains("cronExpression=cron").contains("numQueryThreads=2");
        assertThat(s2.toString()).contains("host=mydomain").contains("port=1234").contains("url=service:jmx:remoting-jmx://mysys.mydomain:8004").contains("cronExpression=cron").contains("numQueryThreads=2");
    }

    @Test
    public void testIntegrity() {
        try {
            Server.builder().setPid("123").setPool(ServerFixtures.createPool()).setUrl("aaa").build();
            Assert.fail("Pid and Url should not be allowed at the same time");
        } catch (IllegalArgumentException e) {
        }
        try {
            Server.builder().setPool(ServerFixtures.createPool()).build();
            Assert.fail("No Pid or Url can't work");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testConnectionRepoolingOk() throws Exception {
        @SuppressWarnings("unchecked")
        GenericKeyedObjectPool<JmxConnectionProvider, JMXConnection> pool = Mockito.mock(GenericKeyedObjectPool.class);
        Server server = Server.builder().setHost("host.example.net").setPort("4321").setLocal(true).setPool(pool).build();
        MBeanServerConnection mBeanConn = Mockito.mock(MBeanServerConnection.class);
        JMXConnection conn = Mockito.mock(JMXConnection.class);
        Mockito.when(conn.getMBeanServerConnection()).thenReturn(mBeanConn);
        Mockito.when(pool.borrowObject(server)).thenReturn(conn);
        Query query = Mockito.mock(Query.class);
        Iterable<ObjectName> objectNames = Lists.emptyList();
        Mockito.when(query.queryNames(mBeanConn)).thenReturn(objectNames);
        server.execute(query);
        Mockito.verify(pool, Mockito.never()).invalidateObject(server, conn);
        InOrder orderVerifier = Mockito.inOrder(pool);
        orderVerifier.verify(pool).borrowObject(server);
        orderVerifier.verify(pool).returnObject(server, conn);
    }

    @Test
    public void testConnectionRepoolingSkippedOnError_andConnectionIsClosed() throws Exception {
        @SuppressWarnings("unchecked")
        GenericKeyedObjectPool<JmxConnectionProvider, JMXConnection> pool = Mockito.mock(GenericKeyedObjectPool.class);
        Server server = Server.builder().setHost("host.example.net").setPort("4321").setLocal(true).setPool(pool).build();
        MBeanServerConnection mBeanConn = Mockito.mock(MBeanServerConnection.class);
        JMXConnection conn = Mockito.mock(JMXConnection.class);
        Mockito.when(conn.getMBeanServerConnection()).thenReturn(mBeanConn);
        Mockito.when(pool.borrowObject(server)).thenReturn(conn);
        Query query = Mockito.mock(Query.class);
        IOException e = Mockito.mock(IOException.class);
        Mockito.when(query.queryNames(mBeanConn)).thenThrow(e);
        try {
            server.execute(query);
            Assert.fail("No exception got throws");
        } catch (IOException e2) {
            if (e != e2) {
                Assert.fail((("Wrong exception thrown (" + e) + " instead of mock"));
            }
        }
        Mockito.verify(pool, Mockito.never()).returnObject(server, conn);
        InOrder orderVerifier = Mockito.inOrder(pool);
        orderVerifier.verify(pool).borrowObject(server);
        orderVerifier.verify(pool).invalidateObject(server, conn);
    }

    @Test
    public void testConnectionRepoolingSkippedOnError_andErrorClosingConnectionIsIgnored() throws Exception {
        @SuppressWarnings("unchecked")
        GenericKeyedObjectPool<JmxConnectionProvider, JMXConnection> pool = Mockito.mock(GenericKeyedObjectPool.class);
        Server server = Server.builder().setHost("host.example.net").setPort("4321").setLocal(true).setPool(pool).build();
        MBeanServerConnection mBeanConn = Mockito.mock(MBeanServerConnection.class);
        JMXConnection conn = Mockito.mock(JMXConnection.class);
        Mockito.when(conn.getMBeanServerConnection()).thenReturn(mBeanConn);
        Mockito.doThrow(new RuntimeException()).when(conn).close();
        Mockito.when(pool.borrowObject(server)).thenReturn(conn);
        Query query = Mockito.mock(Query.class);
        RuntimeException e = Mockito.mock(RuntimeException.class);
        Mockito.when(query.queryNames(mBeanConn)).thenThrow(e);
        try {
            server.execute(query);
            Assert.fail("No exception got throws");
        } catch (RuntimeException e2) {
            if (e != e2) {
                Assert.fail((("Wrong exception thrown (" + e) + " instead of mock"));
            }
        }
        Mockito.verify(pool, Mockito.never()).returnObject(server, conn);
        InOrder orderVerifier = Mockito.inOrder(pool);
        orderVerifier.verify(pool).borrowObject(server);
        orderVerifier.verify(pool).invalidateObject(server, conn);
    }

    /**
     * Test for issue #642
     *
     * @see https://github.com/jmxtrans/jmxtrans/issues/642
     */
    @Test
    public void testConnectionWithPid() throws IOException {
        String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        // jvmName is usually of the form 12345@host where 12345 is the pid
        Pattern jvmNamePattern = Pattern.compile("^(\\d+)@(.*)$");
        Matcher jvmNameMatcher = jvmNamePattern.matcher(jvmName);
        Assume.assumeTrue(jvmNameMatcher.matches());// Skip this test if we don't know how to get PID

        Integer pid = Integer.valueOf(jvmNameMatcher.group(1));
        String host = jvmNameMatcher.group(2);
        Server server = Server.builder().setPid(pid.toString()).setPool(Mockito.mock(KeyedObjectPool.class)).build();
        assertThat(server.getHost()).isEqualToIgnoringCase(host);
        try (JMXConnector serverConnection = server.getServerConnection()) {
            assertThat(serverConnection).isNotNull();
        }
    }
}

