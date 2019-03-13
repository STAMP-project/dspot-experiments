/**
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2011, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.ejb.security;


import ModelDescriptionConstants.CORE_SERVICE;
import ModelDescriptionConstants.STEPS;
import ModelDescriptionConstants.SUBSYSTEM;
import java.io.File;
import java.io.IOException;
import javax.ejb.EJBException;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ContainerResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.as.test.integration.management.base.AbstractCliTestBase;
import org.jboss.as.test.integration.management.util.ServerReload;
import org.jboss.as.test.shared.SnapshotRestoreSetupTask;
import org.jboss.dmr.ModelNode;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Security is configured via legacy security options in remoting subsystem. Test asserts, that anonymous authentication/plain authentication is used.
 *
 * @author Jiri Ondrusek (jondruse@redhat.com)
 */
@RunWith(Arquillian.class)
@RunAsClient
@ServerSetup(SaslLegacyMechanismConfigurationTestCase.LegacyMechanismConfigurationSetupTask.class)
public class SaslLegacyMechanismConfigurationTestCase extends AbstractCliTestBase {
    private static final Logger log = Logger.getLogger(SaslLegacyMechanismConfigurationTestCase.class);

    private static final String MODULE = "SaslLegacyMechanismConfigurationTestCase";

    public static final String ANONYMOUS_PLAIN_SASL_MECHANISMS = "PLAIN,ANONYMOUS";

    public static final String ANONYMOUS_PLAIN_SASL_MECHANISMS_REVERSED = "ANONYMOUS,PLAIN";

    @ContainerResource
    private ManagementClient managementClient;

    // if this is true, tests are ignored as legacy security is not allowed
    private static boolean ignoreTest = false;

    @Test
    public void testAnonymous() throws Exception {
        String echoValue = getBean(SaslLegacyMechanismConfigurationTestCase.log, null, null).getPrincipal();
        Assert.assertEquals("anonymous", echoValue);
    }

    @Test
    public void testAuthorized() throws Exception {
        String echoValue = getBean(SaslLegacyMechanismConfigurationTestCase.log, "user1", "password1").getPrincipal();
        Assert.assertEquals("user1", echoValue);
    }

    @Test
    public void testAuthorizedReversed() throws Exception {
        tryReloadWithSaslMechanism(SaslLegacyMechanismConfigurationTestCase.ANONYMOUS_PLAIN_SASL_MECHANISMS_REVERSED);
        try {
            String echoValue = getBean(SaslLegacyMechanismConfigurationTestCase.log, "user1", "password1").getPrincipal();
            Assert.assertEquals("user1", echoValue);
        } finally {
            tryReloadWithSaslMechanism(SaslLegacyMechanismConfigurationTestCase.ANONYMOUS_PLAIN_SASL_MECHANISMS);
        }
    }

    @Test
    public void testFakeParameter() throws Exception {
        Exception ex = null;
        try {
            tryReloadWithSaslMechanism("FAKE_MECHANISM,PLAIN,ANONYMOUS");
            getBean(SaslLegacyMechanismConfigurationTestCase.log, null, null).getPrincipal();
        } catch (EJBException e) {
            ex = e;
        } finally {
            // in case that one of previous statements fails and changes server
            tryReloadWithSaslMechanism(SaslLegacyMechanismConfigurationTestCase.ANONYMOUS_PLAIN_SASL_MECHANISMS);
        }
        Assert.assertNotNull("Call to ejb should fail, because http-connector is not valid", ex);
    }

    @Test
    public void testDisallowedParameter() throws Exception {
        Exception ex = null;
        try {
            tryReloadWithSaslMechanism("EXTERNAL,PLAIN,ANONYMOUS");
            getBean(SaslLegacyMechanismConfigurationTestCase.log, null, null).getPrincipal();
        } catch (EJBException e) {
            ex = e;
        } finally {
            // in case that one of previous statements fails and changes server
            tryReloadWithSaslMechanism(SaslLegacyMechanismConfigurationTestCase.ANONYMOUS_PLAIN_SASL_MECHANISMS);
        }
        Assert.assertNotNull("Call to ejb should fail, because http-connector is not valid", ex);
    }

    /**
     * Setup task which adds legacy remoting properties and restores it afterwards.
     */
    public static class LegacyMechanismConfigurationSetupTask extends SnapshotRestoreSetupTask {
        private ModelNode localAuthentication;

        private ModelNode propsAuthentication;

        private String saslAuthenticationFactory;

        private static final PathAddress AUTHENTICATION_PROPS = PathAddress.pathAddress(CORE_SERVICE, "management").append("security-realm", "ApplicationRealm").append("authentication", "properties");

        private static final PathAddress LOCAL_AUTHENTICATION = PathAddress.pathAddress(CORE_SERVICE, "management").append("security-realm", "ApplicationRealm").append("authentication", "local");

        private static final PathAddress HTTP_REMOTING_CONNECTOR = PathAddress.pathAddress(SUBSYSTEM, "remoting").append("http-connector", "http-remoting-connector");

        private static final PathAddress SASL_MECHANISMS = PathAddress.pathAddress(SUBSYSTEM, "remoting").append("http-connector", "http-remoting-connector").append("property", "SASL_MECHANISMS");

        private static final PathAddress SASL_POLICY_NOANONYMOUS = PathAddress.pathAddress(SUBSYSTEM, "remoting").append("http-connector", "http-remoting-connector").append("property", "SASL_POLICY_NOANONYMOUS");

        @Override
        protected void doSetup(ManagementClient managementClient, String containerId) throws Exception {
            ModelControllerClient mcc = managementClient.getControllerClient();
            ModelNode authentication;
            String securityRealm = null;
            authentication = execute(Operations.createReadAttributeOperation(SaslLegacyMechanismConfigurationTestCase.LegacyMechanismConfigurationSetupTask.HTTP_REMOTING_CONNECTOR.append().toModelNode(), "security-realm"), mcc);
            if (SUCCESS.equals(authentication.get(OUTCOME).asString())) {
                securityRealm = authentication.get(RESULT).asString();
            }
            if ("undefined".equals(securityRealm)) {
                securityRealm = null;
            }
            SaslLegacyMechanismConfigurationTestCase.ignoreTest = securityRealm == null;
            // if security-realm on htt-remoting-connector is emoty,it means that legacy security is not allowed and this test should be ignored
            Assume.assumeTrue((securityRealm != null));
            authentication = execute(Operations.createReadResourceOperation(SaslLegacyMechanismConfigurationTestCase.LegacyMechanismConfigurationSetupTask.LOCAL_AUTHENTICATION.append().toModelNode()), mcc);
            if (SUCCESS.equals(authentication.get(OUTCOME).asString())) {
                localAuthentication = authentication.get(RESULT);
            }
            authentication = execute(Operations.createReadResourceOperation(SaslLegacyMechanismConfigurationTestCase.LegacyMechanismConfigurationSetupTask.AUTHENTICATION_PROPS.append().toModelNode()), mcc);
            if (SUCCESS.equals(authentication.get(OUTCOME).asString())) {
                propsAuthentication = authentication.get(RESULT);
            }
            authentication = execute(Operations.createReadAttributeOperation(SaslLegacyMechanismConfigurationTestCase.LegacyMechanismConfigurationSetupTask.HTTP_REMOTING_CONNECTOR.append().toModelNode(), "sasl-authentication-factory"), mcc);
            if (SUCCESS.equals(authentication.get(OUTCOME).asString())) {
                saslAuthenticationFactory = authentication.get(RESULT).asString();
            }
            if ("undefined".equals(saslAuthenticationFactory)) {
                saslAuthenticationFactory = null;
            }
            ModelNode compositeOperation = Operations.createCompositeOperation();
            if ((localAuthentication) != null) {
                compositeOperation.get(STEPS).add(Operations.createRemoveOperation(SaslLegacyMechanismConfigurationTestCase.LegacyMechanismConfigurationSetupTask.LOCAL_AUTHENTICATION.toModelNode()));
            }
            compositeOperation.get(STEPS).add(addSaslMechanisms());
            compositeOperation.get(STEPS).add(addPolicyNoanonymous());
            String users = new File(SaslLegacyMechanismConfigurationTestCase.class.getResource("users.properties").getFile()).getAbsolutePath();
            compositeOperation.get(STEPS).add(Operations.createWriteAttributeOperation(SaslLegacyMechanismConfigurationTestCase.LegacyMechanismConfigurationSetupTask.AUTHENTICATION_PROPS.toModelNode(), "path", users));
            compositeOperation.get(STEPS).add(Operations.createWriteAttributeOperation(SaslLegacyMechanismConfigurationTestCase.LegacyMechanismConfigurationSetupTask.AUTHENTICATION_PROPS.toModelNode(), "plain-text", "true"));
            compositeOperation.get(STEPS).add(Operations.createUndefineAttributeOperation(SaslLegacyMechanismConfigurationTestCase.LegacyMechanismConfigurationSetupTask.AUTHENTICATION_PROPS.toModelNode(), "relative-to"));
            if ((saslAuthenticationFactory) != null) {
                compositeOperation.get(STEPS).add(Operations.createUndefineAttributeOperation(SaslLegacyMechanismConfigurationTestCase.LegacyMechanismConfigurationSetupTask.HTTP_REMOTING_CONNECTOR.append().toModelNode(), "sasl-authentication-factory"));
            }
            ModelNode response = execute(compositeOperation, mcc);
            Assert.assertEquals(response.toString(), SUCCESS, response.get(OUTCOME).asString());
            ServerReload.reloadIfRequired(managementClient.getControllerClient());
        }

        private ModelNode addSaslMechanisms() throws IOException {
            ModelNode addRaOperation = Operations.createAddOperation(SaslLegacyMechanismConfigurationTestCase.LegacyMechanismConfigurationSetupTask.SASL_MECHANISMS.toModelNode());
            addRaOperation.get("name").set("value");
            addRaOperation.get("value").set(SaslLegacyMechanismConfigurationTestCase.ANONYMOUS_PLAIN_SASL_MECHANISMS);
            return addRaOperation;
        }

        private ModelNode addPolicyNoanonymous() throws IOException {
            ModelNode addRaOperation = Operations.createAddOperation(SaslLegacyMechanismConfigurationTestCase.LegacyMechanismConfigurationSetupTask.SASL_POLICY_NOANONYMOUS.toModelNode());
            addRaOperation.get("name").set("value");
            addRaOperation.get("value").set("false");
            return addRaOperation;
        }

        private ModelNode execute(ModelNode operation, ModelControllerClient client) throws IOException {
            return client.execute(operation);
        }
    }
}

