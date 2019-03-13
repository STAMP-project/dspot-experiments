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
package org.springframework.ejb.access;


import java.lang.reflect.Proxy;
import java.rmi.RemoteException;
import javax.ejb.CreateException;
import javax.ejb.EJBHome;
import javax.ejb.EJBObject;
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
 * @since 21.05.2003
 */
public class SimpleRemoteStatelessSessionProxyFactoryBeanTests extends SimpleRemoteSlsbInvokerInterceptorTests {
    @Test
    public void testInvokesMethod() throws Exception {
        final int value = 11;
        final String jndiName = "foo";
        SimpleRemoteStatelessSessionProxyFactoryBeanTests.MyEjb myEjb = Mockito.mock(SimpleRemoteStatelessSessionProxyFactoryBeanTests.MyEjb.class);
        BDDMockito.given(myEjb.getValue()).willReturn(value);
        final SimpleRemoteStatelessSessionProxyFactoryBeanTests.MyHome home = Mockito.mock(SimpleRemoteStatelessSessionProxyFactoryBeanTests.MyHome.class);
        BDDMockito.given(home.create()).willReturn(myEjb);
        JndiTemplate jt = new JndiTemplate() {
            @Override
            public Object lookup(String name) {
                // parameterize
                Assert.assertTrue(name.equals(("java:comp/env/" + jndiName)));
                return home;
            }
        };
        SimpleRemoteStatelessSessionProxyFactoryBean fb = new SimpleRemoteStatelessSessionProxyFactoryBean();
        fb.setJndiName(jndiName);
        fb.setResourceRef(true);
        fb.setBusinessInterface(SimpleRemoteStatelessSessionProxyFactoryBeanTests.MyBusinessMethods.class);
        fb.setJndiTemplate(jt);
        // Need lifecycle methods
        fb.afterPropertiesSet();
        SimpleRemoteStatelessSessionProxyFactoryBeanTests.MyBusinessMethods mbm = ((SimpleRemoteStatelessSessionProxyFactoryBeanTests.MyBusinessMethods) (fb.getObject()));
        Assert.assertTrue(Proxy.isProxyClass(mbm.getClass()));
        Assert.assertEquals("Returns expected value", value, mbm.getValue());
        remove();
    }

    @Test
    public void testInvokesMethodOnEjb3StyleBean() throws Exception {
        final int value = 11;
        final String jndiName = "foo";
        final SimpleRemoteStatelessSessionProxyFactoryBeanTests.MyEjb myEjb = Mockito.mock(SimpleRemoteStatelessSessionProxyFactoryBeanTests.MyEjb.class);
        BDDMockito.given(myEjb.getValue()).willReturn(value);
        JndiTemplate jt = new JndiTemplate() {
            @Override
            public Object lookup(String name) {
                // parameterize
                Assert.assertTrue(name.equals(("java:comp/env/" + jndiName)));
                return myEjb;
            }
        };
        SimpleRemoteStatelessSessionProxyFactoryBean fb = new SimpleRemoteStatelessSessionProxyFactoryBean();
        fb.setJndiName(jndiName);
        fb.setResourceRef(true);
        fb.setBusinessInterface(SimpleRemoteStatelessSessionProxyFactoryBeanTests.MyBusinessMethods.class);
        fb.setJndiTemplate(jt);
        // Need lifecycle methods
        fb.afterPropertiesSet();
        SimpleRemoteStatelessSessionProxyFactoryBeanTests.MyBusinessMethods mbm = ((SimpleRemoteStatelessSessionProxyFactoryBeanTests.MyBusinessMethods) (fb.getObject()));
        Assert.assertTrue(Proxy.isProxyClass(mbm.getClass()));
        Assert.assertEquals("Returns expected value", value, mbm.getValue());
    }

    @Override
    @Test
    public void testRemoteException() throws Exception {
        final RemoteException rex = new RemoteException();
        final String jndiName = "foo";
        SimpleRemoteStatelessSessionProxyFactoryBeanTests.MyEjb myEjb = Mockito.mock(SimpleRemoteStatelessSessionProxyFactoryBeanTests.MyEjb.class);
        BDDMockito.given(myEjb.getValue()).willThrow(rex);
        // TODO might want to control this behaviour...
        // Do we really want to call remove after a remote exception?
        final SimpleRemoteStatelessSessionProxyFactoryBeanTests.MyHome home = Mockito.mock(SimpleRemoteStatelessSessionProxyFactoryBeanTests.MyHome.class);
        BDDMockito.given(home.create()).willReturn(myEjb);
        JndiTemplate jt = new JndiTemplate() {
            @Override
            public Object lookup(String name) {
                // parameterize
                Assert.assertTrue(name.equals(("java:comp/env/" + jndiName)));
                return home;
            }
        };
        SimpleRemoteStatelessSessionProxyFactoryBean fb = new SimpleRemoteStatelessSessionProxyFactoryBean();
        fb.setJndiName(jndiName);
        fb.setResourceRef(true);
        fb.setBusinessInterface(SimpleRemoteStatelessSessionProxyFactoryBeanTests.MyBusinessMethods.class);
        fb.setJndiTemplate(jt);
        // Need lifecycle methods
        fb.afterPropertiesSet();
        SimpleRemoteStatelessSessionProxyFactoryBeanTests.MyBusinessMethods mbm = ((SimpleRemoteStatelessSessionProxyFactoryBeanTests.MyBusinessMethods) (fb.getObject()));
        Assert.assertTrue(Proxy.isProxyClass(mbm.getClass()));
        try {
            mbm.getValue();
            Assert.fail("Should've thrown remote exception");
        } catch (RemoteException ex) {
            Assert.assertSame("Threw expected RemoteException", rex, ex);
        }
        remove();
    }

    @Test
    public void testCreateException() throws Exception {
        final String jndiName = "foo";
        final CreateException cex = new CreateException();
        final SimpleRemoteStatelessSessionProxyFactoryBeanTests.MyHome home = Mockito.mock(SimpleRemoteStatelessSessionProxyFactoryBeanTests.MyHome.class);
        BDDMockito.given(home.create()).willThrow(cex);
        JndiTemplate jt = new JndiTemplate() {
            @Override
            public Object lookup(String name) {
                // parameterize
                Assert.assertTrue(name.equals(jndiName));
                return home;
            }
        };
        SimpleRemoteStatelessSessionProxyFactoryBean fb = new SimpleRemoteStatelessSessionProxyFactoryBean();
        fb.setJndiName(jndiName);
        // rely on default setting of resourceRef=false, no auto addition of java:/comp/env prefix
        fb.setBusinessInterface(SimpleRemoteStatelessSessionProxyFactoryBeanTests.MyBusinessMethods.class);
        Assert.assertEquals(fb.getBusinessInterface(), SimpleRemoteStatelessSessionProxyFactoryBeanTests.MyBusinessMethods.class);
        fb.setJndiTemplate(jt);
        // Need lifecycle methods
        fb.afterPropertiesSet();
        SimpleRemoteStatelessSessionProxyFactoryBeanTests.MyBusinessMethods mbm = ((SimpleRemoteStatelessSessionProxyFactoryBeanTests.MyBusinessMethods) (fb.getObject()));
        Assert.assertTrue(Proxy.isProxyClass(mbm.getClass()));
        try {
            mbm.getValue();
            Assert.fail("Should have failed to create EJB");
        } catch (RemoteException ex) {
            // expected
        }
    }

    @Test
    public void testCreateExceptionWithLocalBusinessInterface() throws Exception {
        final String jndiName = "foo";
        final CreateException cex = new CreateException();
        final SimpleRemoteStatelessSessionProxyFactoryBeanTests.MyHome home = Mockito.mock(SimpleRemoteStatelessSessionProxyFactoryBeanTests.MyHome.class);
        BDDMockito.given(home.create()).willThrow(cex);
        JndiTemplate jt = new JndiTemplate() {
            @Override
            public Object lookup(String name) {
                // parameterize
                Assert.assertTrue(name.equals(jndiName));
                return home;
            }
        };
        SimpleRemoteStatelessSessionProxyFactoryBean fb = new SimpleRemoteStatelessSessionProxyFactoryBean();
        fb.setJndiName(jndiName);
        // rely on default setting of resourceRef=false, no auto addition of java:/comp/env prefix
        fb.setBusinessInterface(SimpleRemoteStatelessSessionProxyFactoryBeanTests.MyLocalBusinessMethods.class);
        Assert.assertEquals(fb.getBusinessInterface(), SimpleRemoteStatelessSessionProxyFactoryBeanTests.MyLocalBusinessMethods.class);
        fb.setJndiTemplate(jt);
        // Need lifecycle methods
        fb.afterPropertiesSet();
        SimpleRemoteStatelessSessionProxyFactoryBeanTests.MyLocalBusinessMethods mbm = ((SimpleRemoteStatelessSessionProxyFactoryBeanTests.MyLocalBusinessMethods) (fb.getObject()));
        Assert.assertTrue(Proxy.isProxyClass(mbm.getClass()));
        try {
            mbm.getValue();
            Assert.fail("Should have failed to create EJB");
        } catch (RemoteAccessException ex) {
            Assert.assertTrue(((ex.getCause()) == cex));
        }
    }

    @Test
    public void testNoBusinessInterfaceSpecified() throws Exception {
        // Will do JNDI lookup to get home but won't call create
        // Could actually try to figure out interface from create?
        final String jndiName = "foo";
        final SimpleRemoteStatelessSessionProxyFactoryBeanTests.MyHome home = Mockito.mock(SimpleRemoteStatelessSessionProxyFactoryBeanTests.MyHome.class);
        JndiTemplate jt = new JndiTemplate() {
            @Override
            public Object lookup(String name) throws NamingException {
                // parameterize
                Assert.assertTrue(name.equals(jndiName));
                return home;
            }
        };
        SimpleRemoteStatelessSessionProxyFactoryBean fb = new SimpleRemoteStatelessSessionProxyFactoryBean();
        fb.setJndiName(jndiName);
        // rely on default setting of resourceRef=false, no auto addition of java:/comp/env prefix
        // Don't set business interface
        fb.setJndiTemplate(jt);
        // Check it's a singleton
        Assert.assertTrue(fb.isSingleton());
        try {
            fb.afterPropertiesSet();
            Assert.fail("Should have failed to create EJB");
        } catch (IllegalArgumentException ex) {
            // TODO more appropriate exception?
            Assert.assertTrue(((ex.getMessage().indexOf("businessInterface")) != 1));
        }
        // Expect no methods on home
        Mockito.verifyZeroInteractions(home);
    }

    protected interface MyHome extends EJBHome {
        SimpleRemoteStatelessSessionProxyFactoryBeanTests.MyBusinessMethods create() throws RemoteException, CreateException;
    }

    protected interface MyBusinessMethods {
        int getValue() throws RemoteException;
    }

    protected interface MyLocalBusinessMethods {
        int getValue();
    }

    protected interface MyEjb extends EJBObject , SimpleRemoteStatelessSessionProxyFactoryBeanTests.MyBusinessMethods {}
}

