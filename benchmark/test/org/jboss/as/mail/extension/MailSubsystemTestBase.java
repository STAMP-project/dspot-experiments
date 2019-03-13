/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2017, Red Hat, Inc., and individual contributors
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
package org.jboss.as.mail.extension;


import ContextNames.JAVA_CONTEXT_SERVICE_NAME;
import ContextNames.JBOSS_CONTEXT_SERVICE_NAME;
import MailExtension.MAIL_SESSION_PATH;
import MailExtension.SUBSYSTEM_PATH;
import MailSessionDefinition.SESSION_CAPABILITY;
import RuntimeCapability.Builder;
import ServiceController.Mode.ACTIVE;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.mail.Session;
import javax.net.ssl.SSLContext;
import org.jboss.as.controller.Extension;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.RunningMode;
import org.jboss.as.controller.capability.registry.RuntimeCapabilityRegistry;
import org.jboss.as.controller.extension.ExtensionRegistry;
import org.jboss.as.controller.operations.common.Util;
import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.jboss.as.controller.registry.Resource;
import org.jboss.as.naming.service.NamingStoreService;
import org.jboss.as.network.OutboundSocketBinding;
import org.jboss.as.subsystem.test.AbstractSubsystemBaseTest;
import org.jboss.as.subsystem.test.AdditionalInitialization;
import org.jboss.as.subsystem.test.ControllerInitializer;
import org.jboss.as.subsystem.test.KernelServices;
import org.jboss.as.subsystem.test.KernelServicesBuilder;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceTarget;
import org.junit.Assert;
import org.junit.Test;
import org.wildfly.security.credential.store.CredentialStore;


/**
 *
 *
 * @author Tomaz Cerar (c) 2017 Red Hat Inc.
 */
abstract class MailSubsystemTestBase extends AbstractSubsystemBaseTest {
    MailSubsystemTestBase(String mainSubsystemName, Extension mainExtension) {
        super(mainSubsystemName, mainExtension);
    }

    @Test
    public void testOperations() throws Exception {
        KernelServicesBuilder builder = createKernelServicesBuilder(new MailSubsystemTestBase.DefaultInitializer()).setSubsystemXml(getSubsystemXml());
        KernelServices mainServices = builder.build();
        if (!(mainServices.isSuccessfulBoot())) {
            Assert.fail(mainServices.getBootError().toString());
        }
        PathAddress sessionAddress = PathAddress.pathAddress(SUBSYSTEM_PATH, PathElement.pathElement(MAIL_SESSION_PATH.getKey(), "defaultMail"));
        ModelNode result;
        ModelNode removeServerOp = Util.createRemoveOperation(sessionAddress.append("server", "imap"));
        removeServerOp.get(OPERATION_HEADERS).get(ALLOW_RESOURCE_SERVICE_RESTART).set(true);
        result = mainServices.executeOperation(removeServerOp);
        checkResult(result);
        ModelNode addServerOp = Util.createAddOperation(sessionAddress.append("server", "imap"));
        addServerOp.get(OPERATION_HEADERS).get(ALLOW_RESOURCE_SERVICE_RESTART).set(true);
        addServerOp.get("outbound-socket-binding-ref").set("mail-imap");
        addServerOp.get("username").set("user");
        addServerOp.get("password").set("pswd");
        result = mainServices.executeOperation(addServerOp);
        checkResult(result);
        checkResult(mainServices.executeOperation(removeServerOp));// to make sure noting is left behind

        checkResult(mainServices.executeOperation(addServerOp));
        ModelNode writeOp = Util.createEmptyOperation(WRITE_ATTRIBUTE_OPERATION, sessionAddress);
        writeOp.get(OPERATION_HEADERS).get(ALLOW_RESOURCE_SERVICE_RESTART).set(true);
        writeOp.get("name").set("debug");
        writeOp.get("value").set(false);
        result = mainServices.executeOperation(writeOp);
        checkResult(result);
        ServiceController<?> javaMailService = mainServices.getContainer().getService(SESSION_CAPABILITY.getCapabilityServiceName("defaultMail"));
        javaMailService.setMode(ACTIVE);
        Session session = ((Session) (javaMailService.getValue()));
        Assert.assertNotNull("session should not be null", session);
        Properties properties = session.getProperties();
        Assert.assertNotNull("smtp host should be set", properties.getProperty("mail.smtp.host"));
        Assert.assertNotNull("imap host should be set", properties.getProperty("mail.imap.host"));
        PathAddress nonExisting = PathAddress.pathAddress(SUBSYSTEM_PATH, PathElement.pathElement(MAIL_SESSION_PATH.getKey(), "non-existing-session"));
        ModelNode addSession = Util.createAddOperation(nonExisting);
        addSession.get("jndi-name").set("java:/bah");
        checkResult(mainServices.executeOperation(addSession));
        removeServerOp = Util.createRemoveOperation(nonExisting.append("server", "imap"));
        // removeServerOp.get(OPERATION_HEADERS).get(ALLOW_RESOURCE_SERVICE_RESTART).set(true);
        result = mainServices.executeOperation(removeServerOp);
        checkForFailure(result);
    }

    public static class DefaultInitializer extends AdditionalInitialization {
        protected final Map<String, Integer> sockets = new HashMap<>();

        public DefaultInitializer() {
            sockets.put("mail-imap", 432);
            sockets.put("mail-pop3", 1234);
            sockets.put("mail-smtp", 25);
        }

        @Override
        protected void setupController(ControllerInitializer controllerInitializer) {
            super.setupController(controllerInitializer);
            for (Map.Entry<String, Integer> entry : sockets.entrySet()) {
                controllerInitializer.addRemoteOutboundSocketBinding(entry.getKey(), "localhost", entry.getValue());
            }
            // bug in framework, it doesn't work if only outbound socket bindings are present
            controllerInitializer.addSocketBinding("useless", 9999);
        }

        @Override
        protected void addExtraServices(ServiceTarget target) {
            super.addExtraServices(target);
            target.addService(JAVA_CONTEXT_SERVICE_NAME, new NamingStoreService()).setInitialMode(ACTIVE).install();
            target.addService(JBOSS_CONTEXT_SERVICE_NAME, new NamingStoreService()).setInitialMode(ACTIVE).install();
        }

        @Override
        protected void initializeExtraSubystemsAndModel(ExtensionRegistry extensionRegistry, Resource rootResource, ManagementResourceRegistration rootRegistration, RuntimeCapabilityRegistry capabilityRegistry) {
            super.initializeExtraSubystemsAndModel(extensionRegistry, rootResource, rootRegistration, capabilityRegistry);
            Map<String, Class> capabilities = new HashMap<>();
            capabilities.put(buildDynamicCapabilityName("org.wildfly.security.credential-store", "my-credential-store"), CredentialStore.class);
            capabilities.put(buildDynamicCapabilityName("org.wildfly.security.ssl-context", "foo"), SSLContext.class);
            // capabilities.put(buildDynamicCapabilityName("org.wildfly.network.outbound-socket-binding","ajp-remote"), OutboundSocketBinding.class);
            registerServiceCapabilities(capabilityRegistry, capabilities);
            registerCapabilities(capabilityRegistry, Builder.of("org.wildfly.network.outbound-socket-binding", true, OutboundSocketBinding.class).build(), Builder.of("org.wildfly.security.ssl-context", true, SSLContext.class).build());
        }

        @Override
        protected RunningMode getRunningMode() {
            return RunningMode.NORMAL;
        }
    }

    public static class TransformersInitializer extends MailSubsystemTestBase.DefaultInitializer implements Serializable {
        @Override
        protected RunningMode getRunningMode() {
            return RunningMode.ADMIN_ONLY;
        }
    }
}

