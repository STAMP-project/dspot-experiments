/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.as.messaging.test;


import ExtensionRegistryType.SERVER;
import JGroupsDefaultRequirement.CHANNEL_FACTORY;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.OperationStepHandler;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.ProcessType;
import org.jboss.as.controller.RunningMode;
import org.jboss.as.controller.capability.registry.RuntimeCapabilityRegistry;
import org.jboss.as.controller.descriptions.common.ControllerResolver;
import org.jboss.as.controller.extension.ExtensionRegistry;
import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.jboss.as.controller.registry.Resource;
import org.jboss.as.messaging.MessagingExtension;
import org.jboss.as.subsystem.test.AbstractSubsystemTest;
import org.jboss.as.subsystem.test.AdditionalInitialization;
import org.jboss.as.subsystem.test.KernelServices;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author <a href="http://jmesnil.net/">Jeff Mesnil</a> (c) 2015 Red Hat inc.
 */
public class MigrateTestCase extends AbstractSubsystemTest {
    public static final String MESSAGING_ACTIVEMQ_SUBSYSTEM_NAME = "messaging-activemq";

    public MigrateTestCase() {
        super(MessagingExtension.SUBSYSTEM_NAME, new MessagingExtension());
    }

    @Test
    public void testMigrateHA() throws Exception {
        String subsystemXml = readResource("subsystem_migration_ha.xml");
        MigrateTestCase.newSubsystemAdditionalInitialization additionalInitialization = new MigrateTestCase.newSubsystemAdditionalInitialization();
        KernelServices services = createKernelServicesBuilder(additionalInitialization).setSubsystemXml(subsystemXml).build();
        ModelNode model = services.readWholeModel();
        Assert.assertFalse(additionalInitialization.extensionAdded);
        Assert.assertTrue(model.get(SUBSYSTEM, MessagingExtension.SUBSYSTEM_NAME).isDefined());
        Assert.assertFalse(model.get(SUBSYSTEM, MigrateTestCase.MESSAGING_ACTIVEMQ_SUBSYSTEM_NAME).isDefined());
        ModelNode migrateOp = new ModelNode();
        migrateOp.get(OP).set("migrate");
        migrateOp.get(OP_ADDR).add(SUBSYSTEM, MessagingExtension.SUBSYSTEM_NAME);
        ModelNode response = services.executeOperation(migrateOp);
        // System.out.println("response = " + response);
        checkOutcome(response);
        ModelNode warnings = response.get(RESULT, "migration-warnings");
        // 1 warning about unmigrated-backup
        // 1 warning about shared-store
        // 3 warnings aboud discarded failback-delay attribute
        Assert.assertEquals(warnings.toString(), ((1 + 1) + 3), warnings.asList().size());
        model = services.readWholeModel();
        // System.out.println("model = " + model);
        Assert.assertFalse(model.get(SUBSYSTEM, MigrateTestCase.MESSAGING_ACTIVEMQ_SUBSYSTEM_NAME, "server", "unmigrated-backup", "ha-policy").isDefined());
        Assert.assertFalse(model.get(SUBSYSTEM, MigrateTestCase.MESSAGING_ACTIVEMQ_SUBSYSTEM_NAME, "server", "unmigrated-shared-store", "ha-policy").isDefined());
        ModelNode haPolicyForDefaultServer = model.get(SUBSYSTEM, MigrateTestCase.MESSAGING_ACTIVEMQ_SUBSYSTEM_NAME, "server", "default", "ha-policy", "shared-store-master");
        Assert.assertTrue(haPolicyForDefaultServer.isDefined());
        Assert.assertFalse(haPolicyForDefaultServer.get("failback-delay").isDefined());
        Assert.assertEquals(false, haPolicyForDefaultServer.get("failover-on-server-shutdown").asBoolean());
        ModelNode haPolicyForSharedStoreMasterServer = model.get(SUBSYSTEM, MigrateTestCase.MESSAGING_ACTIVEMQ_SUBSYSTEM_NAME, "server", "shared-store-master", "ha-policy", "shared-store-master");
        Assert.assertTrue(haPolicyForSharedStoreMasterServer.isDefined());
        Assert.assertFalse(haPolicyForSharedStoreMasterServer.get("failback-delay").isDefined());
        Assert.assertEquals("${failover.on.shutdown:true}", haPolicyForSharedStoreMasterServer.get("failover-on-server-shutdown").asString());
        ModelNode haPolicyForSharedStoreSlaveServer = model.get(SUBSYSTEM, MigrateTestCase.MESSAGING_ACTIVEMQ_SUBSYSTEM_NAME, "server", "shared-store-slave", "ha-policy", "shared-store-slave");
        Assert.assertTrue(haPolicyForSharedStoreSlaveServer.isDefined());
        Assert.assertEquals("${allow.failback.1:false}", haPolicyForSharedStoreSlaveServer.get("allow-failback").asString());
        Assert.assertFalse(haPolicyForSharedStoreSlaveServer.get("failback-delay").isDefined());
        Assert.assertEquals("${failover.on.shutdown.1:true}", haPolicyForSharedStoreSlaveServer.get("failover-on-server-shutdown").asString());
        ModelNode haPolicyForReplicationMasterServer = model.get(SUBSYSTEM, MigrateTestCase.MESSAGING_ACTIVEMQ_SUBSYSTEM_NAME, "server", "replication-master", "ha-policy", "replication-master");
        Assert.assertTrue(haPolicyForReplicationMasterServer.isDefined());
        Assert.assertEquals("${check.for.live.server:true}", haPolicyForReplicationMasterServer.get("check-for-live-server").asString());
        Assert.assertEquals("${replication.master.group.name:mygroup}", haPolicyForReplicationMasterServer.get("group-name").asString());
        ModelNode haPolicyForReplicationSlaveServer = model.get(SUBSYSTEM, MigrateTestCase.MESSAGING_ACTIVEMQ_SUBSYSTEM_NAME, "server", "replication-slave", "ha-policy", "replication-slave");
        Assert.assertTrue(haPolicyForReplicationSlaveServer.isDefined());
        Assert.assertEquals("${allow.failback.2:false}", haPolicyForReplicationSlaveServer.get("allow-failback").asString());
        Assert.assertFalse(haPolicyForReplicationSlaveServer.get("failback-delay").isDefined());
        Assert.assertEquals("${max.saved.replicated.journal.size:2}", haPolicyForReplicationSlaveServer.get("max-saved-replicated-journal-size").asString());
        Assert.assertEquals("${replication.master.group.name:mygroup2}", haPolicyForReplicationSlaveServer.get("group-name").asString());
    }

    @Test
    public void testMigrateOperationWithoutLegacyEntries() throws Exception {
        testMigrateOperation(false);
    }

    @Test
    public void testMigrateOperationWithLegacyEntries() throws Exception {
        testMigrateOperation(true);
    }

    private static class newSubsystemAdditionalInitialization extends AdditionalInitialization {
        MessagingExtension newSubsystem = new org.wildfly.extension.messaging.activemq.MessagingExtension();

        boolean extensionAdded = false;

        @Override
        protected void initializeExtraSubystemsAndModel(ExtensionRegistry extensionRegistry, Resource rootResource, ManagementResourceRegistration rootRegistration, RuntimeCapabilityRegistry capabilityRegistry) {
            rootRegistration.registerSubModel(new org.jboss.as.controller.SimpleResourceDefinition(PathElement.pathElement(EXTENSION), ControllerResolver.getResolver(EXTENSION), new OperationStepHandler() {
                @Override
                public void execute(OperationContext context, ModelNode operation) throws OperationFailedException {
                    extensionAdded = true;
                    newSubsystem.initialize(extensionRegistry.getExtensionContext("org.wildfly.extension.messaging-activemq", rootRegistration, SERVER));
                }
            }, null));
            registerCapabilities(capabilityRegistry, CHANNEL_FACTORY.getName(), "org.wildfly.remoting.http-listener-registry");
        }

        @Override
        protected ProcessType getProcessType() {
            return ProcessType.HOST_CONTROLLER;
        }

        @Override
        protected RunningMode getRunningMode() {
            return RunningMode.ADMIN_ONLY;
        }
    }
}

