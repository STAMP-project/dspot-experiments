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
package org.jboss.as.test.integration.ws.authentication.policy;


import SAML2Constants.SAML2_ASSERTION_PROPERTY;
import SAMLUtil.SAML2_TOKEN_TYPE;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.cli.CommandContext;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.test.integration.ws.authentication.policy.resources.EchoServiceRemote;
import org.jboss.as.test.shared.TestSuiteEnvironment;
import org.jboss.logging.Logger;
import org.jboss.ws.api.configuration.ClientConfigUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.picketlink.common.exceptions.fed.WSTrustException;
import org.picketlink.identity.federation.api.wstrust.WSTrustClient;
import org.picketlink.identity.federation.core.saml.v2.util.DocumentUtil;
import org.w3c.dom.Element;


/**
 * Before the fix web service with STS (Picketlink) returned wrong subject with PolicyContext("javax.security.auth.subject.container").
 * This test calls SimpleSecurityManager and checks if no exception is thrown and PolicyContext.getContext("javax.security.auth.Subject.container") should not be null.
 * Test for [ WFLY-8946 ].
 *
 * @author Daniel Cihak
 */
@RunWith(Arquillian.class)
public class AuthenticationPolicyContextTestCase {
    private static Logger LOGGER = Logger.getLogger(AuthenticationPolicyContextTestCase.class);

    private static final String PICKETLINK_STS = "picketlink-sts";

    private static final String PICKETLINK_STS_WS = "picketlink-sts-ws";

    private static final String RESOURCE_DIR = "org/jboss/as/test/integration/ws/authentication/policy/resources/";

    private static final String HOST = TestSuiteEnvironment.getServerAddress();

    private static final int PORT_OFFSET = 0;

    private static final String USERNAME = "UserA";

    private static final String PASSWORD = "PassA";

    private static final String DEFAULT_HOST = AuthenticationPolicyContextTestCase.getHostname();

    private static final int DEFAULT_PORT = 8080;

    private volatile ModelControllerClient modelControllerClient;

    private static WSTrustClient wsClient;

    private volatile CommandContext commandCtx;

    private volatile ByteArrayOutputStream consoleOut = new ByteArrayOutputStream();

    @ArquillianResource
    private static volatile Deployer deployer;

    /**
     * Test gets SAML assertion by token using the web service in deployment picketlink-sts.war.
     * Afterwards web service EchoService from the deployment picketlink-sts-ws.war is called using role testRole and
     * security domain sp created during test initialization.
     *
     * @throws Exception
     * 		
     */
    @Test
    @RunAsClient
    public void test() throws Exception {
        Element assertion = null;
        try {
            AuthenticationPolicyContextTestCase.LOGGER.debug(("Invoking token service to get SAML assertion for " + (AuthenticationPolicyContextTestCase.USERNAME)));
            assertion = AuthenticationPolicyContextTestCase.wsClient.issueToken(SAML2_TOKEN_TYPE);
            String domElementAsString = DocumentUtil.getDOMElementAsString(assertion);
            AuthenticationPolicyContextTestCase.LOGGER.debug(("assertion: " + domElementAsString));
            AuthenticationPolicyContextTestCase.LOGGER.debug((("SAML assertion for " + (AuthenticationPolicyContextTestCase.USERNAME)) + " successfully obtained!"));
        } catch (WSTrustException wse) {
            AuthenticationPolicyContextTestCase.LOGGER.error(("Unable to issue assertion: " + (wse.getMessage())));
            wse.printStackTrace();
            System.exit(1);
        }
        try {
            URL wsdl = new URL((((("http://" + (TestSuiteEnvironment.getServerAddress())) + ":") + (TestSuiteEnvironment.getHttpPort())) + "/picketlink-sts-ws/EchoService?wsdl"));
            QName serviceName = new QName("http://ws.picketlink.sts.jboss.org/", "EchoServiceService");
            Service service = Service.create(wsdl, serviceName);
            EchoServiceRemote port = service.getPort(new QName("http://ws.picketlink.sts.jboss.org/", "EchoServicePort"), EchoServiceRemote.class);
            BindingProvider bp = ((BindingProvider) (port));
            ClientConfigUtil.setConfigHandlers(bp, "standard-jaxws-client-config.xml", "SAML WSSecurity Client");
            bp.getRequestContext().put(SAML2_ASSERTION_PROPERTY, assertion);
            port.echo("Test");
        } finally {
            if ((AuthenticationPolicyContextTestCase.wsClient) != null) {
                AuthenticationPolicyContextTestCase.wsClient.close();
            }
        }
    }
}

