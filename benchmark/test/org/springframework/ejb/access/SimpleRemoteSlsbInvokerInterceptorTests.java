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
package org.springframework.ejb.access;


import java.rmi.RemoteException;
import javax.ejb.CreateException;
import javax.ejb.EJBHome;
import javax.ejb.EJBObject;
import javax.naming.Context;
import javax.naming.NamingException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.jndi.JndiTemplate;
import org.springframework.remoting.RemoteAccessException;


/**
 *
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Chris Beams
 */
public class SimpleRemoteSlsbInvokerInterceptorTests {
    @Test
    public void testPerformsLookup() throws Exception {
        SimpleRemoteSlsbInvokerInterceptorTests.RemoteInterface ejb = Mockito.mock(SimpleRemoteSlsbInvokerInterceptorTests.RemoteInterface.class);
        String jndiName = "foobar";
        Context mockContext = mockContext(jndiName, ejb);
        SimpleRemoteSlsbInvokerInterceptor si = configuredInterceptor(mockContext, jndiName);
        configuredProxy(si, SimpleRemoteSlsbInvokerInterceptorTests.RemoteInterface.class);
        Mockito.verify(mockContext).close();
    }

    @Test
    public void testPerformsLookupWithAccessContext() throws Exception {
        SimpleRemoteSlsbInvokerInterceptorTests.RemoteInterface ejb = Mockito.mock(SimpleRemoteSlsbInvokerInterceptorTests.RemoteInterface.class);
        String jndiName = "foobar";
        Context mockContext = mockContext(jndiName, ejb);
        SimpleRemoteSlsbInvokerInterceptor si = configuredInterceptor(mockContext, jndiName);
        si.setExposeAccessContext(true);
        SimpleRemoteSlsbInvokerInterceptorTests.RemoteInterface target = ((SimpleRemoteSlsbInvokerInterceptorTests.RemoteInterface) (configuredProxy(si, SimpleRemoteSlsbInvokerInterceptorTests.RemoteInterface.class)));
        Assert.assertNull(target.targetMethod());
        Mockito.verify(mockContext, Mockito.times(2)).close();
        Mockito.verify(ejb).targetMethod();
    }

    @Test
    public void testLookupFailure() throws Exception {
        final NamingException nex = new NamingException();
        final String jndiName = "foobar";
        JndiTemplate jt = new JndiTemplate() {
            @Override
            public Object lookup(String name) throws NamingException {
                Assert.assertTrue(jndiName.equals(name));
                throw nex;
            }
        };
        SimpleRemoteSlsbInvokerInterceptor si = new SimpleRemoteSlsbInvokerInterceptor();
        si.setJndiName("foobar");
        // default resourceRef=false should cause this to fail, as java:/comp/env will not
        // automatically be added
        si.setJndiTemplate(jt);
        try {
            si.afterPropertiesSet();
            Assert.fail("Should have failed with naming exception");
        } catch (NamingException ex) {
            Assert.assertTrue((ex == nex));
        }
    }

    @Test
    public void testInvokesMethodOnEjbInstance() throws Exception {
        doTestInvokesMethodOnEjbInstance(true, true);
    }

    @Test
    public void testInvokesMethodOnEjbInstanceWithLazyLookup() throws Exception {
        doTestInvokesMethodOnEjbInstance(false, true);
    }

    @Test
    public void testInvokesMethodOnEjbInstanceWithLazyLookupAndNoCache() throws Exception {
        doTestInvokesMethodOnEjbInstance(false, false);
    }

    @Test
    public void testInvokesMethodOnEjbInstanceWithNoCache() throws Exception {
        doTestInvokesMethodOnEjbInstance(true, false);
    }

    @Test
    public void testInvokesMethodOnEjbInstanceWithRemoteException() throws Exception {
        final SimpleRemoteSlsbInvokerInterceptorTests.RemoteInterface ejb = Mockito.mock(SimpleRemoteSlsbInvokerInterceptorTests.RemoteInterface.class);
        BDDMockito.given(ejb.targetMethod()).willThrow(new RemoteException());
        remove();
        final String jndiName = "foobar";
        Context mockContext = mockContext(jndiName, ejb);
        SimpleRemoteSlsbInvokerInterceptor si = configuredInterceptor(mockContext, jndiName);
        SimpleRemoteSlsbInvokerInterceptorTests.RemoteInterface target = ((SimpleRemoteSlsbInvokerInterceptorTests.RemoteInterface) (configuredProxy(si, SimpleRemoteSlsbInvokerInterceptorTests.RemoteInterface.class)));
        try {
            target.targetMethod();
            Assert.fail("Should have thrown RemoteException");
        } catch (RemoteException ex) {
            // expected
        }
        Mockito.verify(mockContext).close();
        remove();
    }

    @Test
    public void testInvokesMethodOnEjbInstanceWithConnectExceptionWithRefresh() throws Exception {
        doTestInvokesMethodOnEjbInstanceWithConnectExceptionWithRefresh(true, true);
    }

    @Test
    public void testInvokesMethodOnEjbInstanceWithConnectExceptionWithRefreshAndLazyLookup() throws Exception {
        doTestInvokesMethodOnEjbInstanceWithConnectExceptionWithRefresh(false, true);
    }

    @Test
    public void testInvokesMethodOnEjbInstanceWithConnectExceptionWithRefreshAndLazyLookupAndNoCache() throws Exception {
        doTestInvokesMethodOnEjbInstanceWithConnectExceptionWithRefresh(false, false);
    }

    @Test
    public void testInvokesMethodOnEjbInstanceWithConnectExceptionWithRefreshAndNoCache() throws Exception {
        doTestInvokesMethodOnEjbInstanceWithConnectExceptionWithRefresh(true, false);
    }

    @Test
    public void testInvokesMethodOnEjbInstanceWithBusinessInterface() throws Exception {
        Object retVal = new Object();
        final SimpleRemoteSlsbInvokerInterceptorTests.RemoteInterface ejb = Mockito.mock(SimpleRemoteSlsbInvokerInterceptorTests.RemoteInterface.class);
        BDDMockito.given(ejb.targetMethod()).willReturn(retVal);
        final String jndiName = "foobar";
        Context mockContext = mockContext(jndiName, ejb);
        SimpleRemoteSlsbInvokerInterceptor si = configuredInterceptor(mockContext, jndiName);
        SimpleRemoteSlsbInvokerInterceptorTests.BusinessInterface target = ((SimpleRemoteSlsbInvokerInterceptorTests.BusinessInterface) (configuredProxy(si, SimpleRemoteSlsbInvokerInterceptorTests.BusinessInterface.class)));
        Assert.assertTrue(((target.targetMethod()) == retVal));
        Mockito.verify(mockContext).close();
        remove();
    }

    @Test
    public void testInvokesMethodOnEjbInstanceWithBusinessInterfaceWithRemoteException() throws Exception {
        final SimpleRemoteSlsbInvokerInterceptorTests.RemoteInterface ejb = Mockito.mock(SimpleRemoteSlsbInvokerInterceptorTests.RemoteInterface.class);
        BDDMockito.given(ejb.targetMethod()).willThrow(new RemoteException());
        final String jndiName = "foobar";
        Context mockContext = mockContext(jndiName, ejb);
        SimpleRemoteSlsbInvokerInterceptor si = configuredInterceptor(mockContext, jndiName);
        SimpleRemoteSlsbInvokerInterceptorTests.BusinessInterface target = ((SimpleRemoteSlsbInvokerInterceptorTests.BusinessInterface) (configuredProxy(si, SimpleRemoteSlsbInvokerInterceptorTests.BusinessInterface.class)));
        try {
            target.targetMethod();
            Assert.fail("Should have thrown RemoteAccessException");
        } catch (RemoteAccessException ex) {
            // expected
        }
        Mockito.verify(mockContext).close();
        remove();
    }

    @Test
    public void testApplicationException() throws Exception {
        doTestException(new SimpleRemoteSlsbInvokerInterceptorTests.ApplicationException());
    }

    @Test
    public void testRemoteException() throws Exception {
        doTestException(new RemoteException());
    }

    /**
     * Needed so that we can mock create() method.
     */
    protected interface SlsbHome extends EJBHome {
        EJBObject create() throws RemoteException, CreateException;
    }

    protected interface RemoteInterface extends EJBObject {
        // Also business exception!?
        Object targetMethod() throws RemoteException, SimpleRemoteSlsbInvokerInterceptorTests.ApplicationException;
    }

    protected interface BusinessInterface {
        Object targetMethod() throws SimpleRemoteSlsbInvokerInterceptorTests.ApplicationException;
    }

    @SuppressWarnings("serial")
    protected class ApplicationException extends Exception {
        public ApplicationException() {
            super("appException");
        }
    }
}

