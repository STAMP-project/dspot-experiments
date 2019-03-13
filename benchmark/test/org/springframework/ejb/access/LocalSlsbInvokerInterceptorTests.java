/**
 * Copyright 2002-2013 the original author or authors.
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


import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;
import javax.ejb.EJBLocalObject;
import javax.naming.Context;
import javax.naming.NamingException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.jndi.JndiTemplate;


/**
 *
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Chris Beams
 */
public class LocalSlsbInvokerInterceptorTests {
    /**
     * Test that it performs the correct lookup.
     */
    @Test
    public void testPerformsLookup() throws Exception {
        LocalSlsbInvokerInterceptorTests.LocalInterfaceWithBusinessMethods ejb = Mockito.mock(LocalSlsbInvokerInterceptorTests.LocalInterfaceWithBusinessMethods.class);
        String jndiName = "foobar";
        Context mockContext = mockContext(jndiName, ejb);
        configuredInterceptor(mockContext, jndiName);
        Mockito.verify(mockContext).close();
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
        LocalSlsbInvokerInterceptor si = new LocalSlsbInvokerInterceptor();
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
        Object retVal = new Object();
        LocalSlsbInvokerInterceptorTests.LocalInterfaceWithBusinessMethods ejb = Mockito.mock(LocalSlsbInvokerInterceptorTests.LocalInterfaceWithBusinessMethods.class);
        BDDMockito.given(ejb.targetMethod()).willReturn(retVal);
        String jndiName = "foobar";
        Context mockContext = mockContext(jndiName, ejb);
        LocalSlsbInvokerInterceptor si = configuredInterceptor(mockContext, jndiName);
        ProxyFactory pf = new ProxyFactory(new Class<?>[]{ LocalSlsbInvokerInterceptorTests.BusinessMethods.class });
        pf.addAdvice(si);
        LocalSlsbInvokerInterceptorTests.BusinessMethods target = ((LocalSlsbInvokerInterceptorTests.BusinessMethods) (pf.getProxy()));
        Assert.assertTrue(((target.targetMethod()) == retVal));
        Mockito.verify(mockContext).close();
        remove();
    }

    @Test
    public void testInvokesMethodOnEjbInstanceWithSeparateBusinessMethods() throws Exception {
        Object retVal = new Object();
        LocalSlsbInvokerInterceptorTests.LocalInterface ejb = Mockito.mock(LocalSlsbInvokerInterceptorTests.LocalInterface.class);
        BDDMockito.given(ejb.targetMethod()).willReturn(retVal);
        String jndiName = "foobar";
        Context mockContext = mockContext(jndiName, ejb);
        LocalSlsbInvokerInterceptor si = configuredInterceptor(mockContext, jndiName);
        ProxyFactory pf = new ProxyFactory(new Class<?>[]{ LocalSlsbInvokerInterceptorTests.BusinessMethods.class });
        pf.addAdvice(si);
        LocalSlsbInvokerInterceptorTests.BusinessMethods target = ((LocalSlsbInvokerInterceptorTests.BusinessMethods) (pf.getProxy()));
        Assert.assertTrue(((target.targetMethod()) == retVal));
        Mockito.verify(mockContext).close();
        remove();
    }

    @Test
    public void testApplicationException() throws Exception {
        testException(new LocalSlsbInvokerInterceptorTests.ApplicationException());
    }

    /**
     * Needed so that we can mock the create() method.
     */
    private interface SlsbHome extends EJBLocalHome {
        LocalSlsbInvokerInterceptorTests.LocalInterface create() throws CreateException;
    }

    private interface BusinessMethods {
        Object targetMethod() throws LocalSlsbInvokerInterceptorTests.ApplicationException;
    }

    private interface LocalInterface extends EJBLocalObject {
        Object targetMethod() throws LocalSlsbInvokerInterceptorTests.ApplicationException;
    }

    private interface LocalInterfaceWithBusinessMethods extends LocalSlsbInvokerInterceptorTests.BusinessMethods , LocalSlsbInvokerInterceptorTests.LocalInterface {}

    @SuppressWarnings("serial")
    private class ApplicationException extends Exception {
        public ApplicationException() {
            super("appException");
        }
    }
}

