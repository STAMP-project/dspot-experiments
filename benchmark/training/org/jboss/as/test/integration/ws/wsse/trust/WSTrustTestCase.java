/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.test.integration.ws.wsse.trust;


import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.StringTokenizer;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.test.integration.ws.WrapThreadContextClassLoader;
import org.jboss.as.test.integration.ws.wsse.trust.actas.ActAsServiceIface;
import org.jboss.as.test.integration.ws.wsse.trust.bearer.BearerIface;
import org.jboss.as.test.integration.ws.wsse.trust.holderofkey.HolderOfKeyIface;
import org.jboss.as.test.integration.ws.wsse.trust.onbehalfof.OnBehalfOfServiceIface;
import org.jboss.as.test.integration.ws.wsse.trust.service.ServiceIface;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;


/**
 * WS-Trust test case
 * This is basically the Apache CXF STS demo (from distribution samples)
 * ported to jbossws-cxf for running over JBoss Application Server.
 *
 * @author alessio.soldano@jboss.com
 * @author rsearls@redhat.com
 * @since 08-Feb-2012
 */
@RunWith(Arquillian.class)
@ServerSetup(WSTrustTestCaseSecuritySetupTask.class)
public class WSTrustTestCase {
    private static final String STS_DEP = "jaxws-samples-wsse-policy-trust-sts";

    private static final String SERVER_DEP = "jaxws-samples-wsse-policy-trust";

    private static final String ACT_AS_SERVER_DEP = "jaxws-samples-wsse-policy-trust-actas";

    private static final String ON_BEHALF_OF_SERVER_DEP = "jaxws-samples-wsse-policy-trust-onbehalfof";

    private static final String HOLDER_OF_KEY_STS_DEP = "jaxws-samples-wsse-policy-trust-sts-holderofkey";

    private static final String HOLDER_OF_KEY_SERVER_DEP = "jaxws-samples-wsse-policy-trust-holderofkey";

    private static final String PL_STS_DEP = "jaxws-samples-wsse-policy-trustPicketLink-sts";

    private static final String BEARER_STS_DEP = "jaxws-samples-wsse-policy-trust-sts-bearer";

    private static final String BEARER_SERVER_DEP = "jaxws-samples-wsse-policy-trust-bearer";

    @Rule
    public TestRule watcher = new WSTrustTestCase.WrapThreadContextClassLoaderWatcher();

    @ArquillianResource
    private URL serviceURL;

    /**
     * WS-Trust test with the STS information programmatically provided
     *
     * @throws Exception
     * 		
     */
    @Test
    @RunAsClient
    @OperateOnDeployment(WSTrustTestCase.SERVER_DEP)
    @WrapThreadContextClassLoader
    public void test() throws Exception {
        Bus bus = BusFactory.newInstance().createBus();
        try {
            BusFactory.setThreadDefaultBus(bus);
            final QName serviceName = new QName("http://www.jboss.org/jbossws/ws-extensions/wssecuritypolicy", "SecurityService");
            final URL wsdlURL = new URL(((serviceURL) + "SecurityService?wsdl"));
            Service service = Service.create(wsdlURL, serviceName);
            ServiceIface proxy = ((ServiceIface) (service.getPort(ServiceIface.class)));
            final QName stsServiceName = new QName("http://docs.oasis-open.org/ws-sx/ws-trust/200512/", "SecurityTokenService");
            final QName stsPortName = new QName("http://docs.oasis-open.org/ws-sx/ws-trust/200512/", "UT_Port");
            URL stsURL = new URL(serviceURL.getProtocol(), serviceURL.getHost(), serviceURL.getPort(), "/jaxws-samples-wsse-policy-trust-sts/SecurityTokenService?wsdl");
            WSTrustTestUtils.setupWsseAndSTSClient(proxy, bus, stsURL.toString(), stsServiceName, stsPortName);
            try {
                Assert.assertEquals("WS-Trust Hello World!", proxy.sayHello());
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        } finally {
            bus.shutdown(true);
        }
    }

    /**
     * WS-Trust test with the STS information coming from EPR specified in service endpoint contract policy
     *
     * @throws Exception
     * 		
     */
    @Test
    @RunAsClient
    @OperateOnDeployment(WSTrustTestCase.SERVER_DEP)
    @WrapThreadContextClassLoader
    public void testUsingEPR() throws Exception {
        Bus bus = BusFactory.newInstance().createBus();
        try {
            BusFactory.setThreadDefaultBus(bus);
            final QName serviceName = new QName("http://www.jboss.org/jbossws/ws-extensions/wssecuritypolicy", "SecurityService");
            final URL wsdlURL = new URL(((serviceURL) + "SecurityService?wsdl"));
            Service service = Service.create(wsdlURL, serviceName);
            ServiceIface proxy = ((ServiceIface) (service.getPort(ServiceIface.class)));
            WSTrustTestUtils.setupWsse(proxy, bus);
            try {
                Assert.assertEquals("WS-Trust Hello World!", proxy.sayHello());
            } catch (Exception e) {
                throw e;
            }
        } finally {
            bus.shutdown(true);
        }
    }

    /**
     * No CallbackHandler is provided in STSCLient.  Username and password provided instead.
     *
     * @throws Exception
     * 		
     */
    @Test
    @RunAsClient
    @OperateOnDeployment(WSTrustTestCase.SERVER_DEP)
    @WrapThreadContextClassLoader
    public void testNoClientCallback() throws Exception {
        Bus bus = BusFactory.newInstance().createBus();
        try {
            BusFactory.setThreadDefaultBus(bus);
            final QName serviceName = new QName("http://www.jboss.org/jbossws/ws-extensions/wssecuritypolicy", "SecurityService");
            final URL wsdlURL = new URL(((serviceURL) + "SecurityService?wsdl"));
            Service service = Service.create(wsdlURL, serviceName);
            ServiceIface proxy = ((ServiceIface) (service.getPort(ServiceIface.class)));
            final QName stsServiceName = new QName("http://docs.oasis-open.org/ws-sx/ws-trust/200512/", "SecurityTokenService");
            final QName stsPortName = new QName("http://docs.oasis-open.org/ws-sx/ws-trust/200512/", "UT_Port");
            URL stsURL = new URL(serviceURL.getProtocol(), serviceURL.getHost(), serviceURL.getPort(), "/jaxws-samples-wsse-policy-trust-sts/SecurityTokenService?wsdl");
            WSTrustTestUtils.setupWsseAndSTSClientNoCallbackHandler(proxy, bus, stsURL.toString(), stsServiceName, stsPortName);
            Assert.assertEquals("WS-Trust Hello World!", proxy.sayHello());
        } finally {
            bus.shutdown(true);
        }
    }

    /**
     * No SIGNATURE_USERNAME is provided to the service.  Service will use the
     * client's keystore alias in its place.
     *
     * @throws Exception
     * 		
     */
    @Test
    @RunAsClient
    @OperateOnDeployment(WSTrustTestCase.SERVER_DEP)
    @WrapThreadContextClassLoader
    public void testNoSignatureUsername() throws Exception {
        Bus bus = BusFactory.newInstance().createBus();
        try {
            BusFactory.setThreadDefaultBus(bus);
            final QName serviceName = new QName("http://www.jboss.org/jbossws/ws-extensions/wssecuritypolicy", "SecurityService");
            final URL wsdlURL = new URL(((serviceURL) + "SecurityService?wsdl"));
            Service service = Service.create(wsdlURL, serviceName);
            ServiceIface proxy = ((ServiceIface) (service.getPort(ServiceIface.class)));
            final QName stsServiceName = new QName("http://docs.oasis-open.org/ws-sx/ws-trust/200512/", "SecurityTokenService");
            final QName stsPortName = new QName("http://docs.oasis-open.org/ws-sx/ws-trust/200512/", "UT_Port");
            URL stsURL = new URL(serviceURL.getProtocol(), serviceURL.getHost(), serviceURL.getPort(), "/jaxws-samples-wsse-policy-trust-sts/SecurityTokenService?wsdl");
            WSTrustTestUtils.setupWsseAndSTSClientNoSignatureUsername(proxy, bus, stsURL.toString(), stsServiceName, stsPortName);
            Assert.assertEquals("WS-Trust Hello World!", proxy.sayHello());
        } finally {
            bus.shutdown(true);
        }
    }

    /**
     * Request a security token that allows it to act as if it were somebody else.
     *
     * @throws Exception
     * 		
     */
    @Test
    @RunAsClient
    @OperateOnDeployment(WSTrustTestCase.ACT_AS_SERVER_DEP)
    @WrapThreadContextClassLoader
    public void testActAs() throws Exception {
        Bus bus = BusFactory.newInstance().createBus();
        try {
            BusFactory.setThreadDefaultBus(bus);
            final QName serviceName = new QName("http://www.jboss.org/jbossws/ws-extensions/actaswssecuritypolicy", "ActAsService");
            final URL wsdlURL = new URL(((serviceURL) + "ActAsService?wsdl"));
            Service service = Service.create(wsdlURL, serviceName);
            ActAsServiceIface proxy = ((ActAsServiceIface) (service.getPort(ActAsServiceIface.class)));
            WSTrustTestUtils.setupWsseAndSTSClientActAs(((BindingProvider) (proxy)), bus);
            Assert.assertEquals("ActAs WS-Trust Hello World!", proxy.sayHello(serviceURL.getHost(), String.valueOf(serviceURL.getPort())));
        } finally {
            bus.shutdown(true);
        }
    }

    /**
     * Request a security token that allows it to act on behalf of somebody else.
     *
     * @throws Exception
     * 		
     */
    @Test
    @RunAsClient
    @OperateOnDeployment(WSTrustTestCase.ON_BEHALF_OF_SERVER_DEP)
    @WrapThreadContextClassLoader
    public void testOnBehalfOf() throws Exception {
        Bus bus = BusFactory.newInstance().createBus();
        try {
            BusFactory.setThreadDefaultBus(bus);
            final QName serviceName = new QName("http://www.jboss.org/jbossws/ws-extensions/onbehalfofwssecuritypolicy", "OnBehalfOfService");
            final URL wsdlURL = new URL(((serviceURL) + "OnBehalfOfService?wsdl"));
            Service service = Service.create(wsdlURL, serviceName);
            OnBehalfOfServiceIface proxy = ((OnBehalfOfServiceIface) (service.getPort(OnBehalfOfServiceIface.class)));
            WSTrustTestUtils.setupWsseAndSTSClientOnBehalfOf(((BindingProvider) (proxy)), bus);
            Assert.assertEquals("OnBehalfOf WS-Trust Hello World!", proxy.sayHello(serviceURL.getHost(), String.valueOf(serviceURL.getPort())));
        } finally {
            bus.shutdown(true);
        }
    }

    @Test
    @RunAsClient
    @OperateOnDeployment(WSTrustTestCase.HOLDER_OF_KEY_SERVER_DEP)
    @WrapThreadContextClassLoader
    public void testHolderOfKey() throws Exception {
        // TLSv1.2 seems buggy on JDK-11 (Invalid ECDH ServerKeyExchange signature)
        String originalProtocols = System.getProperty("https.protocols");
        System.setProperty("https.protocols", "TLSv1.1");
        Bus bus = BusFactory.newInstance().createBus();
        try {
            BusFactory.setThreadDefaultBus(bus);
            final QName serviceName = new QName("http://www.jboss.org/jbossws/ws-extensions/holderofkeywssecuritypolicy", "HolderOfKeyService");
            final URL wsdlURL = new URL("https", serviceURL.getHost(), (((serviceURL.getPort()) - 8080) + 8444), "/jaxws-samples-wsse-policy-trust-holderofkey/HolderOfKeyService?wsdl");
            Service service = Service.create(wsdlURL, serviceName);
            HolderOfKeyIface proxy = ((HolderOfKeyIface) (service.getPort(HolderOfKeyIface.class)));
            WSTrustTestUtils.setupWsseAndSTSClientHolderOfKey(((BindingProvider) (proxy)), bus);
            Assert.assertEquals("Holder-Of-Key WS-Trust Hello World!", proxy.sayHello());
        } finally {
            bus.shutdown(true);
            if (originalProtocols == null) {
                System.clearProperty("https.protocols");
            } else {
                System.setProperty("https.protocols", originalProtocols);
            }
        }
    }

    @Test
    @RunAsClient
    @OperateOnDeployment(WSTrustTestCase.SERVER_DEP)
    @WrapThreadContextClassLoader
    public void testPicketLink() throws Exception {
        Bus bus = BusFactory.newInstance().createBus();
        try {
            BusFactory.setThreadDefaultBus(bus);
            final QName serviceName = new QName("http://www.jboss.org/jbossws/ws-extensions/wssecuritypolicy", "SecurityService");
            final URL wsdlURL = new URL(((serviceURL) + "SecurityService?wsdl"));
            Service service = Service.create(wsdlURL, serviceName);
            ServiceIface proxy = ((ServiceIface) (service.getPort(ServiceIface.class)));
            final QName stsServiceName = new QName("urn:picketlink:identity-federation:sts", "PicketLinkSTS");
            final QName stsPortName = new QName("urn:picketlink:identity-federation:sts", "PicketLinkSTSPort");
            final URL stsURL = new URL(serviceURL.getProtocol(), serviceURL.getHost(), serviceURL.getPort(), "/jaxws-samples-wsse-policy-trustPicketLink-sts/PicketLinkSTS?wsdl");
            WSTrustTestUtils.setupWsseAndSTSClient(proxy, bus, stsURL.toString(), stsServiceName, stsPortName);
            try {
                Assert.assertEquals("WS-Trust Hello World!", proxy.sayHello());
            } catch (Exception e) {
                throw e;
            }
        } finally {
            bus.shutdown(true);
        }
    }

    @Test
    @RunAsClient
    @OperateOnDeployment(WSTrustTestCase.BEARER_SERVER_DEP)
    @WrapThreadContextClassLoader
    public void testBearer() throws Exception {
        // TLSv1.2 seems buggy on JDK-11 (Invalid ECDH ServerKeyExchange signature)
        String originalProtocols = System.getProperty("https.protocols");
        System.setProperty("https.protocols", "TLSv1.1");
        Bus bus = BusFactory.newInstance().createBus();
        try {
            BusFactory.setThreadDefaultBus(bus);
            final QName serviceName = new QName("http://www.jboss.org/jbossws/ws-extensions/bearerwssecuritypolicy", "BearerService");
            Service service = Service.create(new URL(((serviceURL) + "BearerService?wsdl")), serviceName);
            BearerIface proxy = ((BearerIface) (service.getPort(BearerIface.class)));
            WSTrustTestUtils.setupWsseAndSTSClientBearer(((BindingProvider) (proxy)), bus);
            Assert.assertEquals("Bearer WS-Trust Hello World!", proxy.sayHello());
        } finally {
            bus.shutdown(true);
            if (originalProtocols == null) {
                System.clearProperty("https.protocols");
            } else {
                System.setProperty("https.protocols", originalProtocols);
            }
        }
    }

    class WrapThreadContextClassLoaderWatcher extends TestWatcher {
        private ClassLoader classLoader = null;

        protected void starting(Description description) {
            try {
                final String cjp = getClientJarPaths();
                if ((cjp == null) || (cjp.trim().isEmpty())) {
                    return;
                }
                if ((description.getAnnotation(WrapThreadContextClassLoader.class)) != null) {
                    classLoader = Thread.currentThread().getContextClassLoader();
                    StringTokenizer st = new StringTokenizer(cjp, ", ");
                    URL[] archives = new URL[st.countTokens()];
                    for (int i = 0; i < (archives.length); i++) {
                        archives[i] = new File(st.nextToken()).toURI().toURL();
                    }
                    URLClassLoader cl = new URLClassLoader(archives, classLoader);
                    Thread.currentThread().setContextClassLoader(cl);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        protected void finished(Description description) {
            if (((classLoader) != null) && ((description.getAnnotation(WrapThreadContextClassLoader.class)) != null)) {
                Thread.currentThread().setContextClassLoader(classLoader);
            }
        }
    }
}

