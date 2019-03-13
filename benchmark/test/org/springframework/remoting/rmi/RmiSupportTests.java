/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.remoting.rmi;


import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.rmi.ConnectException;
import java.rmi.ConnectIOException;
import java.rmi.MarshalException;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.StubNotFoundException;
import java.rmi.UnknownHostException;
import java.rmi.UnmarshalException;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.RemoteConnectFailureException;
import org.springframework.remoting.RemoteProxyFailureException;
import org.springframework.remoting.support.RemoteInvocation;


/**
 *
 *
 * @author Juergen Hoeller
 * @since 16.05.2003
 */
public class RmiSupportTests {
    @Test
    public void rmiProxyFactoryBean() throws Exception {
        RmiSupportTests.CountingRmiProxyFactoryBean factory = new RmiSupportTests.CountingRmiProxyFactoryBean();
        setServiceInterface(RmiSupportTests.IRemoteBean.class);
        setServiceUrl("rmi://localhost:1090/test");
        afterPropertiesSet();
        Assert.assertTrue("Correct singleton value", isSingleton());
        Assert.assertTrue(((getObject()) instanceof RmiSupportTests.IRemoteBean));
        RmiSupportTests.IRemoteBean proxy = ((RmiSupportTests.IRemoteBean) (getObject()));
        proxy.setName("myName");
        Assert.assertEquals("myName", RmiSupportTests.RemoteBean.name);
        Assert.assertEquals(1, factory.counter);
    }

    @Test
    public void rmiProxyFactoryBeanWithRemoteException() throws Exception {
        doTestRmiProxyFactoryBeanWithException(RemoteException.class);
    }

    @Test
    public void rmiProxyFactoryBeanWithConnectException() throws Exception {
        doTestRmiProxyFactoryBeanWithException(ConnectException.class);
    }

    @Test
    public void rmiProxyFactoryBeanWithConnectIOException() throws Exception {
        doTestRmiProxyFactoryBeanWithException(ConnectIOException.class);
    }

    @Test
    public void rmiProxyFactoryBeanWithUnknownHostException() throws Exception {
        doTestRmiProxyFactoryBeanWithException(UnknownHostException.class);
    }

    @Test
    public void rmiProxyFactoryBeanWithNoSuchObjectException() throws Exception {
        doTestRmiProxyFactoryBeanWithException(NoSuchObjectException.class);
    }

    @Test
    public void rmiProxyFactoryBeanWithStubNotFoundException() throws Exception {
        doTestRmiProxyFactoryBeanWithException(StubNotFoundException.class);
    }

    @Test
    public void rmiProxyFactoryBeanWithMarshalException() throws Exception {
        doTestRmiProxyFactoryBeanWithException(MarshalException.class);
    }

    @Test
    public void rmiProxyFactoryBeanWithUnmarshalException() throws Exception {
        doTestRmiProxyFactoryBeanWithException(UnmarshalException.class);
    }

    @Test
    public void rmiProxyFactoryBeanWithConnectExceptionAndRefresh() throws Exception {
        doTestRmiProxyFactoryBeanWithExceptionAndRefresh(ConnectException.class);
    }

    @Test
    public void rmiProxyFactoryBeanWithConnectIOExceptionAndRefresh() throws Exception {
        doTestRmiProxyFactoryBeanWithExceptionAndRefresh(ConnectIOException.class);
    }

    @Test
    public void rmiProxyFactoryBeanWithUnknownHostExceptionAndRefresh() throws Exception {
        doTestRmiProxyFactoryBeanWithExceptionAndRefresh(UnknownHostException.class);
    }

    @Test
    public void rmiProxyFactoryBeanWithNoSuchObjectExceptionAndRefresh() throws Exception {
        doTestRmiProxyFactoryBeanWithExceptionAndRefresh(NoSuchObjectException.class);
    }

    @Test
    public void rmiProxyFactoryBeanWithStubNotFoundExceptionAndRefresh() throws Exception {
        doTestRmiProxyFactoryBeanWithExceptionAndRefresh(StubNotFoundException.class);
    }

    @Test
    public void rmiProxyFactoryBeanWithBusinessInterface() throws Exception {
        RmiSupportTests.CountingRmiProxyFactoryBean factory = new RmiSupportTests.CountingRmiProxyFactoryBean();
        setServiceInterface(RmiSupportTests.IBusinessBean.class);
        setServiceUrl("rmi://localhost:1090/test");
        afterPropertiesSet();
        Assert.assertTrue(((getObject()) instanceof RmiSupportTests.IBusinessBean));
        RmiSupportTests.IBusinessBean proxy = ((RmiSupportTests.IBusinessBean) (getObject()));
        Assert.assertFalse((proxy instanceof RmiSupportTests.IRemoteBean));
        proxy.setName("myName");
        Assert.assertEquals("myName", RmiSupportTests.RemoteBean.name);
        Assert.assertEquals(1, factory.counter);
    }

    @Test
    public void rmiProxyFactoryBeanWithWrongBusinessInterface() throws Exception {
        RmiSupportTests.CountingRmiProxyFactoryBean factory = new RmiSupportTests.CountingRmiProxyFactoryBean();
        setServiceInterface(RmiSupportTests.IWrongBusinessBean.class);
        setServiceUrl("rmi://localhost:1090/test");
        afterPropertiesSet();
        Assert.assertTrue(((getObject()) instanceof RmiSupportTests.IWrongBusinessBean));
        RmiSupportTests.IWrongBusinessBean proxy = ((RmiSupportTests.IWrongBusinessBean) (getObject()));
        Assert.assertFalse((proxy instanceof RmiSupportTests.IRemoteBean));
        try {
            proxy.setOtherName("name");
            Assert.fail("Should have thrown RemoteProxyFailureException");
        } catch (RemoteProxyFailureException ex) {
            Assert.assertTrue(((ex.getCause()) instanceof NoSuchMethodException));
            Assert.assertTrue(ex.getMessage().contains("setOtherName"));
            Assert.assertTrue(ex.getMessage().contains("IWrongBusinessBean"));
        }
        Assert.assertEquals(1, factory.counter);
    }

    @Test
    public void rmiProxyFactoryBeanWithBusinessInterfaceAndRemoteException() throws Exception {
        doTestRmiProxyFactoryBeanWithBusinessInterfaceAndException(RemoteException.class, RemoteAccessException.class);
    }

    @Test
    public void rmiProxyFactoryBeanWithBusinessInterfaceAndConnectException() throws Exception {
        doTestRmiProxyFactoryBeanWithBusinessInterfaceAndException(ConnectException.class, RemoteConnectFailureException.class);
    }

    @Test
    public void rmiProxyFactoryBeanWithBusinessInterfaceAndConnectIOException() throws Exception {
        doTestRmiProxyFactoryBeanWithBusinessInterfaceAndException(ConnectIOException.class, RemoteConnectFailureException.class);
    }

    @Test
    public void rmiProxyFactoryBeanWithBusinessInterfaceAndUnknownHostException() throws Exception {
        doTestRmiProxyFactoryBeanWithBusinessInterfaceAndException(UnknownHostException.class, RemoteConnectFailureException.class);
    }

    @Test
    public void rmiProxyFactoryBeanWithBusinessInterfaceAndNoSuchObjectExceptionException() throws Exception {
        doTestRmiProxyFactoryBeanWithBusinessInterfaceAndException(NoSuchObjectException.class, RemoteConnectFailureException.class);
    }

    @Test
    public void rmiProxyFactoryBeanWithBusinessInterfaceAndStubNotFoundException() throws Exception {
        doTestRmiProxyFactoryBeanWithBusinessInterfaceAndException(StubNotFoundException.class, RemoteConnectFailureException.class);
    }

    @Test
    public void rmiProxyFactoryBeanWithBusinessInterfaceAndRemoteExceptionAndRefresh() throws Exception {
        doTestRmiProxyFactoryBeanWithBusinessInterfaceAndExceptionAndRefresh(RemoteException.class, RemoteAccessException.class);
    }

    @Test
    public void rmiProxyFactoryBeanWithBusinessInterfaceAndConnectExceptionAndRefresh() throws Exception {
        doTestRmiProxyFactoryBeanWithBusinessInterfaceAndExceptionAndRefresh(ConnectException.class, RemoteConnectFailureException.class);
    }

    @Test
    public void rmiProxyFactoryBeanWithBusinessInterfaceAndConnectIOExceptionAndRefresh() throws Exception {
        doTestRmiProxyFactoryBeanWithBusinessInterfaceAndExceptionAndRefresh(ConnectIOException.class, RemoteConnectFailureException.class);
    }

    @Test
    public void rmiProxyFactoryBeanWithBusinessInterfaceAndUnknownHostExceptionAndRefresh() throws Exception {
        doTestRmiProxyFactoryBeanWithBusinessInterfaceAndExceptionAndRefresh(UnknownHostException.class, RemoteConnectFailureException.class);
    }

    @Test
    public void rmiProxyFactoryBeanWithBusinessInterfaceAndNoSuchObjectExceptionAndRefresh() throws Exception {
        doTestRmiProxyFactoryBeanWithBusinessInterfaceAndExceptionAndRefresh(NoSuchObjectException.class, RemoteConnectFailureException.class);
    }

    @Test
    public void rmiProxyFactoryBeanWithBusinessInterfaceAndStubNotFoundExceptionAndRefresh() throws Exception {
        doTestRmiProxyFactoryBeanWithBusinessInterfaceAndExceptionAndRefresh(StubNotFoundException.class, RemoteConnectFailureException.class);
    }

    @Test
    public void rmiClientInterceptorRequiresUrl() throws Exception {
        RmiClientInterceptor client = new RmiClientInterceptor();
        client.setServiceInterface(RmiSupportTests.IRemoteBean.class);
        try {
            client.afterPropertiesSet();
            Assert.fail("url isn't set, expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    @Test
    public void remoteInvocation() throws NoSuchMethodException {
        // let's see if the remote invocation object works
        final RmiSupportTests.RemoteBean rb = new RmiSupportTests.RemoteBean();
        final Method setNameMethod = rb.getClass().getDeclaredMethod("setName", String.class);
        MethodInvocation mi = new MethodInvocation() {
            @Override
            public Method getMethod() {
                return setNameMethod;
            }

            @Override
            public Object[] getArguments() {
                return new Object[]{ "bla" };
            }

            @Override
            public Object proceed() throws Throwable {
                throw new UnsupportedOperationException();
            }

            @Override
            public Object getThis() {
                return rb;
            }

            @Override
            public AccessibleObject getStaticPart() {
                return setNameMethod;
            }
        };
        RemoteInvocation inv = new RemoteInvocation(mi);
        Assert.assertEquals("setName", inv.getMethodName());
        Assert.assertEquals("bla", inv.getArguments()[0]);
        Assert.assertEquals(String.class, inv.getParameterTypes()[0]);
        // this is a bit BS, but we need to test it
        inv = new RemoteInvocation();
        inv.setArguments(new Object[]{ "bla" });
        Assert.assertEquals("bla", inv.getArguments()[0]);
        inv.setMethodName("setName");
        Assert.assertEquals("setName", inv.getMethodName());
        inv.setParameterTypes(new Class<?>[]{ String.class });
        Assert.assertEquals(String.class, inv.getParameterTypes()[0]);
        inv = new RemoteInvocation("setName", new Class<?>[]{ String.class }, new Object[]{ "bla" });
        Assert.assertEquals("bla", inv.getArguments()[0]);
        Assert.assertEquals("setName", inv.getMethodName());
        Assert.assertEquals(String.class, inv.getParameterTypes()[0]);
    }

    @Test
    public void rmiInvokerWithSpecialLocalMethods() throws Exception {
        String serviceUrl = "rmi://localhost:1090/test";
        RmiProxyFactoryBean factory = new RmiProxyFactoryBean() {
            @Override
            protected Remote lookupStub() {
                return new RmiInvocationHandler() {
                    @Override
                    public String getTargetInterfaceName() {
                        return null;
                    }

                    @Override
                    public Object invoke(RemoteInvocation invocation) throws RemoteException {
                        throw new RemoteException();
                    }
                };
            }
        };
        factory.setServiceInterface(RmiSupportTests.IBusinessBean.class);
        factory.setServiceUrl(serviceUrl);
        factory.afterPropertiesSet();
        RmiSupportTests.IBusinessBean proxy = ((RmiSupportTests.IBusinessBean) (factory.getObject()));
        // shouldn't go through to remote service
        Assert.assertTrue(proxy.toString().contains("RMI invoker"));
        Assert.assertTrue(proxy.toString().contains(serviceUrl));
        Assert.assertEquals(proxy.hashCode(), proxy.hashCode());
        Assert.assertTrue(proxy.equals(proxy));
        // should go through
        try {
            proxy.setName("test");
            Assert.fail("Should have thrown RemoteAccessException");
        } catch (RemoteAccessException ex) {
            // expected
        }
    }

    private static class CountingRmiProxyFactoryBean extends RmiProxyFactoryBean {
        private int counter = 0;

        @Override
        protected Remote lookupStub() {
            (counter)++;
            return new RmiSupportTests.RemoteBean();
        }
    }

    public interface IBusinessBean {
        void setName(String name);
    }

    public interface IWrongBusinessBean {
        void setOtherName(String name);
    }

    public interface IRemoteBean extends Remote {
        void setName(String name) throws RemoteException;
    }

    public static class RemoteBean implements RmiSupportTests.IRemoteBean {
        private static String name;

        @Override
        public void setName(String nam) throws RemoteException {
            if ((nam != null) && (nam.endsWith("Exception"))) {
                RemoteException rex;
                try {
                    Class<?> exClass = Class.forName(nam);
                    Constructor<?> ctor = exClass.getConstructor(String.class);
                    rex = ((RemoteException) (ctor.newInstance("myMessage")));
                } catch (Exception ex) {
                    throw new RemoteException(("Illegal exception class name: " + nam), ex);
                }
                throw rex;
            }
            RmiSupportTests.RemoteBean.name = nam;
        }
    }
}

