/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.jmx.access;


import TestGroup.JMXMP;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.BindException;
import javax.management.Descriptor;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.springframework.jmx.AbstractMBeanServerTests;
import org.springframework.jmx.IJmxTestBean;
import org.springframework.jmx.JmxException;
import org.springframework.jmx.JmxTestBean;
import org.springframework.jmx.export.assembler.AbstractReflectiveMBeanInfoAssembler;
import org.springframework.util.SocketUtils;


/**
 * To run the tests in the class, set the following Java system property:
 * {@code -DtestGroups=jmxmp}.
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Chris Beams
 */
public class MBeanClientInterceptorTests extends AbstractMBeanServerTests {
    protected static final String OBJECT_NAME = "spring:test=proxy";

    protected JmxTestBean target;

    protected boolean runTests = true;

    @Test
    public void testProxyClassIsDifferent() throws Exception {
        Assume.assumeTrue(runTests);
        IJmxTestBean proxy = getProxy();
        Assert.assertTrue("The proxy class should be different than the base class", ((proxy.getClass()) != (IJmxTestBean.class)));
    }

    @Test
    public void testDifferentProxiesSameClass() throws Exception {
        Assume.assumeTrue(runTests);
        IJmxTestBean proxy1 = getProxy();
        IJmxTestBean proxy2 = getProxy();
        Assert.assertNotSame("The proxies should NOT be the same", proxy1, proxy2);
        Assert.assertSame("The proxy classes should be the same", proxy1.getClass(), proxy2.getClass());
    }

    @Test
    public void testGetAttributeValue() throws Exception {
        Assume.assumeTrue(runTests);
        IJmxTestBean proxy1 = getProxy();
        int age = proxy1.getAge();
        Assert.assertEquals("The age should be 100", 100, age);
    }

    @Test
    public void testSetAttributeValue() throws Exception {
        Assume.assumeTrue(runTests);
        IJmxTestBean proxy = getProxy();
        proxy.setName("Rob Harrop");
        Assert.assertEquals("The name of the bean should have been updated", "Rob Harrop", target.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetAttributeValueWithRuntimeException() throws Exception {
        Assume.assumeTrue(runTests);
        IJmxTestBean proxy = getProxy();
        proxy.setName("Juergen");
    }

    @Test(expected = ClassNotFoundException.class)
    public void testSetAttributeValueWithCheckedException() throws Exception {
        Assume.assumeTrue(runTests);
        IJmxTestBean proxy = getProxy();
        proxy.setName("Juergen Class");
    }

    @Test(expected = IOException.class)
    public void testSetAttributeValueWithIOException() throws Exception {
        Assume.assumeTrue(runTests);
        IJmxTestBean proxy = getProxy();
        proxy.setName("Juergen IO");
    }

    @Test(expected = InvalidInvocationException.class)
    public void testSetReadOnlyAttribute() throws Exception {
        Assume.assumeTrue(runTests);
        IJmxTestBean proxy = getProxy();
        proxy.setAge(900);
    }

    @Test
    public void testInvokeNoArgs() throws Exception {
        Assume.assumeTrue(runTests);
        IJmxTestBean proxy = getProxy();
        long result = proxy.myOperation();
        Assert.assertEquals("The operation should return 1", 1, result);
    }

    @Test
    public void testInvokeArgs() throws Exception {
        Assume.assumeTrue(runTests);
        IJmxTestBean proxy = getProxy();
        int result = proxy.add(1, 2);
        Assert.assertEquals("The operation should return 3", 3, result);
    }

    @Test(expected = InvalidInvocationException.class)
    public void testInvokeUnexposedMethodWithException() throws Exception {
        Assume.assumeTrue(runTests);
        IJmxTestBean bean = getProxy();
        bean.dontExposeMe();
    }

    @Test
    public void testTestLazyConnectionToRemote() throws Exception {
        Assume.assumeTrue(runTests);
        org.springframework.tests.Assume.group(JMXMP);
        final int port = SocketUtils.findAvailableTcpPort();
        JMXServiceURL url = new JMXServiceURL(("service:jmx:jmxmp://localhost:" + port));
        JMXConnectorServer connector = JMXConnectorServerFactory.newJMXConnectorServer(url, null, getServer());
        MBeanProxyFactoryBean factory = new MBeanProxyFactoryBean();
        factory.setServiceUrl(url.toString());
        factory.setProxyInterface(IJmxTestBean.class);
        factory.setObjectName(MBeanClientInterceptorTests.OBJECT_NAME);
        factory.setConnectOnStartup(false);
        factory.setRefreshOnConnectFailure(true);
        // should skip connection to the server
        factory.afterPropertiesSet();
        IJmxTestBean bean = ((IJmxTestBean) (factory.getObject()));
        // now start the connector
        try {
            connector.start();
        } catch (BindException ex) {
            System.out.println(((("Skipping remainder of JMX LazyConnectionToRemote test because binding to local port [" + port) + "] failed: ") + (ex.getMessage())));
            return;
        }
        // should now be able to access data via the lazy proxy
        try {
            Assert.assertEquals("Rob Harrop", bean.getName());
            Assert.assertEquals(100, bean.getAge());
        } finally {
            connector.stop();
        }
        try {
            bean.getName();
        } catch (JmxException ex) {
            // expected
        }
        connector = JMXConnectorServerFactory.newJMXConnectorServer(url, null, getServer());
        connector.start();
        // should now be able to access data via the lazy proxy
        try {
            Assert.assertEquals("Rob Harrop", bean.getName());
            Assert.assertEquals(100, bean.getAge());
        } finally {
            connector.stop();
        }
    }

    /* public void testMXBeanAttributeAccess() throws Exception {
    MBeanClientInterceptor interceptor = new MBeanClientInterceptor();
    interceptor.setServer(ManagementFactory.getPlatformMBeanServer());
    interceptor.setObjectName("java.lang:type=Memory");
    interceptor.setManagementInterface(MemoryMXBean.class);
    MemoryMXBean proxy = ProxyFactory.getProxy(MemoryMXBean.class, interceptor);
    assertTrue(proxy.getHeapMemoryUsage().getMax() > 0);
    }

    public void testMXBeanOperationAccess() throws Exception {
    MBeanClientInterceptor interceptor = new MBeanClientInterceptor();
    interceptor.setServer(ManagementFactory.getPlatformMBeanServer());
    interceptor.setObjectName("java.lang:type=Threading");
    ThreadMXBean proxy = ProxyFactory.getProxy(ThreadMXBean.class, interceptor);
    assertTrue(proxy.getThreadInfo(Thread.currentThread().getId()).getStackTrace() != null);
    }

    public void testMXBeanAttributeListAccess() throws Exception {
    MBeanClientInterceptor interceptor = new MBeanClientInterceptor();
    interceptor.setServer(ManagementFactory.getPlatformMBeanServer());
    interceptor.setObjectName("com.sun.management:type=HotSpotDiagnostic");
    HotSpotDiagnosticMXBean proxy = ProxyFactory.getProxy(HotSpotDiagnosticMXBean.class, interceptor);
    assertFalse(proxy.getDiagnosticOptions().isEmpty());
    }
     */
    private static class ProxyTestAssembler extends AbstractReflectiveMBeanInfoAssembler {
        @Override
        protected boolean includeReadAttribute(Method method, String beanKey) {
            return true;
        }

        @Override
        protected boolean includeWriteAttribute(Method method, String beanKey) {
            if ("setAge".equals(method.getName())) {
                return false;
            }
            return true;
        }

        @Override
        protected boolean includeOperation(Method method, String beanKey) {
            if ("dontExposeMe".equals(method.getName())) {
                return false;
            }
            return true;
        }

        @SuppressWarnings("unused")
        protected String getOperationDescription(Method method) {
            return method.getName();
        }

        @SuppressWarnings("unused")
        protected String getAttributeDescription(PropertyDescriptor propertyDescriptor) {
            return propertyDescriptor.getDisplayName();
        }

        @SuppressWarnings("unused")
        protected void populateAttributeDescriptor(Descriptor descriptor, Method getter, Method setter) {
        }

        @SuppressWarnings("unused")
        protected void populateOperationDescriptor(Descriptor descriptor, Method method) {
        }

        @SuppressWarnings({ "unused", "rawtypes" })
        protected String getDescription(String beanKey, Class beanClass) {
            return "";
        }

        @SuppressWarnings({ "unused", "rawtypes" })
        protected void populateMBeanDescriptor(Descriptor mbeanDescriptor, String beanKey, Class beanClass) {
        }
    }
}

