/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2018, Red Hat Middleware LLC, and individual contributors
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
import org.jboss.as.test.integration.ws.wsse.trust.bearer.BearerIface;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;


/**
 * Test for WFLY-10480 with Elytron security domain
 */
@RunWith(Arquillian.class)
@ServerSetup(WSTrustTestCaseElytronSecuritySetupTask.class)
public class WSBearerElytronSecurityPropagationTestCase {
    private static final String BEARER_STS_DEP = "jaxws-samples-wsse-policy-trust-sts-bearer";

    private static final String BEARER_SERVER_DEP = "jaxws-samples-wsse-policy-trust-bearer";

    @Rule
    public TestRule watcher = new WSBearerElytronSecurityPropagationTestCase.WrapThreadContextClassLoaderWatcher();

    @ArquillianResource
    private URL serviceURL;

    @Test
    @RunAsClient
    @OperateOnDeployment(WSBearerElytronSecurityPropagationTestCase.BEARER_SERVER_DEP)
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
            Assert.assertEquals("alice&alice", proxy.sayHello());
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

