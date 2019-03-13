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
package org.wildfly.iiop.openjdk;


import IIOPExtension.SUBSYSTEM_NAME;
import ModelTestControllerVersion.EAP_7_0_0;
import RuntimeCapability.Builder;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.net.ssl.SSLContext;
import org.jboss.as.controller.ModelVersion;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.RunningMode;
import org.jboss.as.controller.capability.registry.RuntimeCapabilityRegistry;
import org.jboss.as.controller.extension.ExtensionRegistry;
import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.jboss.as.controller.registry.Resource;
import org.jboss.as.network.OutboundSocketBinding;
import org.jboss.as.subsystem.test.AbstractSubsystemBaseTest;
import org.jboss.as.subsystem.test.AdditionalInitialization;
import org.jboss.as.subsystem.test.ControllerInitializer;
import org.jboss.dmr.ModelNode;
import org.jboss.security.SecurityDomain;
import org.junit.Test;
import org.wildfly.security.credential.store.CredentialStore;


/**
 * <?>
 * IIOP subsystem tests.
 * </?>
 *
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 * @author <a href="sguilhen@jboss.com">Stefan Guilhen</a>
 * @author <a href="mailto:tadamski@redhat.com">Tomasz Adamski</a>
 */
public class IIOPSubsystemTransformersTestCase extends AbstractSubsystemBaseTest {
    private static ModelVersion EAP7_0_0 = ModelVersion.create(1, 0, 0);

    public IIOPSubsystemTransformersTestCase() {
        super(SUBSYSTEM_NAME, new IIOPExtension());
    }

    @Test
    public void testTransformersEAP_7_0_0() throws Exception {
        testTransformers(EAP_7_0_0, IIOPSubsystemTransformersTestCase.EAP7_0_0);
    }

    @Test
    public void testRejectTransformersEAP_7_0_0() throws Exception {
        doRejectTest(EAP_7_0_0, IIOPSubsystemTransformersTestCase.EAP7_0_0);
    }

    private static class DefaultInitialization extends AdditionalInitialization {
        protected final Map<String, Integer> sockets = new HashMap<>();

        {
            sockets.put("ajp", 8009);
            sockets.put("http", 8080);
        }

        @Override
        protected ControllerInitializer createControllerInitializer() {
            return new ControllerInitializer() {
                @Override
                protected void initializeSocketBindingsOperations(List<ModelNode> ops) {
                    super.initializeSocketBindingsOperations(ops);
                    ModelNode op = new ModelNode();
                    op.get(OP).set(ADD);
                    op.get(OP_ADDR).set(PathAddress.pathAddress(PathElement.pathElement(SOCKET_BINDING_GROUP, SOCKET_BINDING_GROUP_NAME), PathElement.pathElement(SOCKET_BINDING, "advertise-socket-binding")).toModelNode());
                    op.get(PORT).set(8011);
                    op.get(MULTICAST_ADDRESS).set("224.0.1.105");
                    op.get(MULTICAST_PORT).set("23364");
                    ops.add(op);
                }
            };
        }

        @Override
        protected RunningMode getRunningMode() {
            return RunningMode.ADMIN_ONLY;
        }

        @Override
        protected void setupController(ControllerInitializer controllerInitializer) {
            super.setupController(controllerInitializer);
            for (Map.Entry<String, Integer> entry : sockets.entrySet()) {
                controllerInitializer.addSocketBinding(entry.getKey(), entry.getValue());
            }
            controllerInitializer.addRemoteOutboundSocketBinding("iiop", "localhost", 7777);
            controllerInitializer.addRemoteOutboundSocketBinding("iiop-ssl", "localhost", 7778);
        }

        @Override
        protected void initializeExtraSubystemsAndModel(ExtensionRegistry extensionRegistry, Resource rootResource, ManagementResourceRegistration rootRegistration, RuntimeCapabilityRegistry capabilityRegistry) {
            super.initializeExtraSubystemsAndModel(extensionRegistry, rootResource, rootRegistration, capabilityRegistry);
            Map<String, Class> capabilities = new HashMap<>();
            capabilities.put(buildDynamicCapabilityName("org.wildfly.security.ssl-context", "TestContext"), SSLContext.class);
            capabilities.put(buildDynamicCapabilityName("org.wildfly.security.ssl-context", "my-ssl-context"), SSLContext.class);
            capabilities.put(buildDynamicCapabilityName("org.wildfly.security.key-store", "my-key-store"), KeyStore.class);
            capabilities.put(buildDynamicCapabilityName("org.wildfly.security.credential-store", "my-credential-store"), CredentialStore.class);
            capabilities.put(buildDynamicCapabilityName("org.wildfly.security.ssl-context", "foo"), SSLContext.class);
            capabilities.put(buildDynamicCapabilityName("org.wildfly.security.authentication-context", "iiop"), SSLContext.class);
            capabilities.put(buildDynamicCapabilityName("org.wildfly.security.legacy-security-domain", "domain"), SecurityDomain.class);
            registerServiceCapabilities(capabilityRegistry, capabilities);
            registerCapabilities(capabilityRegistry, Builder.of("org.wildfly.network.outbound-socket-binding", true, OutboundSocketBinding.class).build(), Builder.of("org.wildfly.security.ssl-context", true, SSLContext.class).build());
        }
    }
}

