/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.domain.suites;


import java.io.File;
import org.apache.commons.io.FileUtils;
import org.jboss.as.controller.client.helpers.domain.DomainClient;
import org.jboss.as.test.integration.domain.management.util.DomainLifecycleUtil;
import org.jboss.as.test.integration.domain.management.util.DomainTestSupport;
import org.jboss.as.test.integration.management.util.ModelUtil;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests both automated and manual configuration model persistence snapshot generation.
 *
 * @author Dominik Pospisil <dpospisi@redhat.com>
 */
public class ModelPersistenceTestCase {
    enum Host {

        MASTER,
        SLAVE;}

    private class CfgFileDescription {
        CfgFileDescription(int version, File file, long hash) {
            this.version = version;
            this.file = file;
            this.hash = hash;
        }

        public int version;

        public File file;

        public long hash;
    }

    private static DomainTestSupport domainSupport;

    private static DomainLifecycleUtil domainMasterLifecycleUtil;

    private static DomainLifecycleUtil domainSlaveLifecycleUtil;

    private static final String DOMAIN_HISTORY_DIR = "domain_xml_history";

    private static final String HOST_HISTORY_DIR = "host_xml_history";

    private static final String CONFIG_DIR = "configuration";

    private static final String CURRENT_DIR = "current";

    private static final String MASTER_DIR = "master";

    private static final String SLAVE_DIR = "slave";

    private static final String DOMAIN_NAME = "testing-domain-standard";

    private static final String MASTER_NAME = "testing-host-master";

    private static final String SLAVE_NAME = "testing-host-slave";

    private static File domainCurrentCfgDir;

    private static File masterCurrentCfgDir;

    private static File slaveCurrentCfgDir;

    private static File domainLastCfgFile;

    private static File masterLastCfgFile;

    private static File slaveLastCfgFile;

    @Test
    public void testSimpleDomainOperation() throws Exception {
        ModelNode op = ModelUtil.createOpNode("profile=default/subsystem=ee", WRITE_ATTRIBUTE_OPERATION);
        op.get(NAME).set("ear-subdeployments-isolated");
        op.get(VALUE).set(true);
        testDomainOperation(op);
        op.get(VALUE).set(false);
        testDomainOperation(op);
    }

    @Test
    public void testCompositeDomainOperation() throws Exception {
        ModelNode[] steps = new ModelNode[2];
        steps[0] = ModelUtil.createOpNode("profile=default/subsystem=ee", WRITE_ATTRIBUTE_OPERATION);
        steps[0].get(NAME).set("ear-subdeployments-isolated");
        steps[0].get(VALUE).set(true);
        steps[1] = ModelUtil.createOpNode("system-property=model-persistence-test", ADD);
        steps[1].get(VALUE).set("test");
        testDomainOperation(ModelUtil.createCompositeNode(steps));
        steps[0].get(VALUE).set(false);
        steps[1] = ModelUtil.createOpNode("system-property=model-persistence-test", REMOVE);
        testDomainOperation(ModelUtil.createCompositeNode(steps));
    }

    @Test
    public void testDomainOperationRollback() throws Exception {
        DomainClient client = ModelPersistenceTestCase.domainMasterLifecycleUtil.getDomainClient();
        ModelPersistenceTestCase.CfgFileDescription lastDomainBackupDesc = getLatestBackup(ModelPersistenceTestCase.domainCurrentCfgDir);
        ModelPersistenceTestCase.CfgFileDescription lastMasterBackupDesc = getLatestBackup(ModelPersistenceTestCase.masterCurrentCfgDir);
        ModelPersistenceTestCase.CfgFileDescription lastSlaveBackupDesc = getLatestBackup(ModelPersistenceTestCase.slaveCurrentCfgDir);
        // execute operation so the model gets updated
        ModelNode op = ModelUtil.createOpNode("system-property=model-persistence-test", "add");
        op.get(VALUE).set("test");
        executeAndRollbackOperation(client, op);
        // check that the model has not been updated
        ModelPersistenceTestCase.CfgFileDescription newDomainBackupDesc = getLatestBackup(ModelPersistenceTestCase.domainCurrentCfgDir);
        ModelPersistenceTestCase.CfgFileDescription newMasterBackupDesc = getLatestBackup(ModelPersistenceTestCase.masterCurrentCfgDir);
        ModelPersistenceTestCase.CfgFileDescription newSlaveBackupDesc = getLatestBackup(ModelPersistenceTestCase.slaveCurrentCfgDir);
        // check that the configs did not change
        Assert.assertTrue(((lastDomainBackupDesc.version) == (newDomainBackupDesc.version)));
        Assert.assertTrue(((lastMasterBackupDesc.version) == (newMasterBackupDesc.version)));
        Assert.assertTrue(((lastSlaveBackupDesc.version) == (newSlaveBackupDesc.version)));
    }

    @Test
    public void testSimpleHostOperation() throws Exception {
        // using master DC
        ModelNode op = ModelUtil.createOpNode("host=master/system-property=model-persistence-test", ADD);
        op.get(VALUE).set("test");
        testHostOperation(op, ModelPersistenceTestCase.Host.MASTER, ModelPersistenceTestCase.Host.MASTER);
        op = ModelUtil.createOpNode("host=master/system-property=model-persistence-test", REMOVE);
        testHostOperation(op, ModelPersistenceTestCase.Host.MASTER, ModelPersistenceTestCase.Host.MASTER);
        op = ModelUtil.createOpNode("host=slave/system-property=model-persistence-test", ADD);
        op.get(VALUE).set("test");
        testHostOperation(op, ModelPersistenceTestCase.Host.MASTER, ModelPersistenceTestCase.Host.SLAVE);
        op = ModelUtil.createOpNode("host=slave/system-property=model-persistence-test", REMOVE);
        testHostOperation(op, ModelPersistenceTestCase.Host.MASTER, ModelPersistenceTestCase.Host.SLAVE);
        // using slave HC
        op = ModelUtil.createOpNode("host=slave/system-property=model-persistence-test", ADD);
        op.get(VALUE).set("test");
        testHostOperation(op, ModelPersistenceTestCase.Host.SLAVE, ModelPersistenceTestCase.Host.SLAVE);
        op = ModelUtil.createOpNode("host=slave/system-property=model-persistence-test", REMOVE);
        testHostOperation(op, ModelPersistenceTestCase.Host.SLAVE, ModelPersistenceTestCase.Host.SLAVE);
    }

    @Test
    public void testCompositeHostOperation() throws Exception {
        // test op on master using master controller
        ModelNode[] steps = new ModelNode[2];
        steps[0] = ModelUtil.createOpNode("host=master/system-property=model-persistence-test", ADD);
        steps[0].get(VALUE).set("test");
        steps[1] = ModelUtil.createOpNode("host=master/system-property=model-persistence-test", "write-attribute");
        steps[1].get(NAME).set("value");
        steps[1].get(VALUE).set("test2");
        testHostOperation(ModelUtil.createCompositeNode(steps), ModelPersistenceTestCase.Host.MASTER, ModelPersistenceTestCase.Host.MASTER);
        ModelNode op = ModelUtil.createOpNode("host=master/system-property=model-persistence-test", REMOVE);
        testHostOperation(op, ModelPersistenceTestCase.Host.MASTER, ModelPersistenceTestCase.Host.MASTER);
        // test op on slave using master controller
        steps[0] = ModelUtil.createOpNode("host=slave/system-property=model-persistence-test", ADD);
        steps[0].get(VALUE).set("test");
        steps[1] = ModelUtil.createOpNode("host=slave/system-property=model-persistence-test", "write-attribute");
        steps[1].get(NAME).set("value");
        steps[1].get(VALUE).set("test2");
        testHostOperation(ModelUtil.createCompositeNode(steps), ModelPersistenceTestCase.Host.MASTER, ModelPersistenceTestCase.Host.SLAVE);
        op = ModelUtil.createOpNode("host=slave/system-property=model-persistence-test", REMOVE);
        testHostOperation(op, ModelPersistenceTestCase.Host.MASTER, ModelPersistenceTestCase.Host.SLAVE);
        // test op on slave using slave controller
        steps[0] = ModelUtil.createOpNode("host=slave/system-property=model-persistence-test", ADD);
        steps[0].get(VALUE).set("test");
        steps[1] = ModelUtil.createOpNode("host=slave/system-property=model-persistence-test", "write-attribute");
        steps[1].get(NAME).set("value");
        steps[1].get(VALUE).set("test2");
        testHostOperation(ModelUtil.createCompositeNode(steps), ModelPersistenceTestCase.Host.SLAVE, ModelPersistenceTestCase.Host.SLAVE);
        op = ModelUtil.createOpNode("host=slave/system-property=model-persistence-test", REMOVE);
        testHostOperation(op, ModelPersistenceTestCase.Host.SLAVE, ModelPersistenceTestCase.Host.SLAVE);
    }

    @Test
    public void testHostOperationRollback() throws Exception {
        DomainClient client = ModelPersistenceTestCase.domainMasterLifecycleUtil.getDomainClient();
        for (ModelPersistenceTestCase.Host host : ModelPersistenceTestCase.Host.values()) {
            ModelPersistenceTestCase.CfgFileDescription lastDomainBackupDesc = getLatestBackup(ModelPersistenceTestCase.domainCurrentCfgDir);
            ModelPersistenceTestCase.CfgFileDescription lastMasterBackupDesc = getLatestBackup(ModelPersistenceTestCase.masterCurrentCfgDir);
            ModelPersistenceTestCase.CfgFileDescription lastSlaveBackupDesc = getLatestBackup(ModelPersistenceTestCase.slaveCurrentCfgDir);
            // execute operation so the model gets updated
            ModelNode op = (host.equals(ModelPersistenceTestCase.Host.MASTER)) ? ModelUtil.createOpNode("host=master/system-property=model-persistence-test", "add") : ModelUtil.createOpNode("host=slave/system-property=model-persistence-test", "add");
            op.get(VALUE).set("test");
            executeAndRollbackOperation(client, op);
            // check that the model has not been updated
            ModelPersistenceTestCase.CfgFileDescription newDomainBackupDesc = getLatestBackup(ModelPersistenceTestCase.domainCurrentCfgDir);
            ModelPersistenceTestCase.CfgFileDescription newMasterBackupDesc = getLatestBackup(ModelPersistenceTestCase.masterCurrentCfgDir);
            ModelPersistenceTestCase.CfgFileDescription newSlaveBackupDesc = getLatestBackup(ModelPersistenceTestCase.slaveCurrentCfgDir);
            // check that the configs did not change
            Assert.assertTrue(((lastDomainBackupDesc.version) == (newDomainBackupDesc.version)));
            Assert.assertTrue(((lastMasterBackupDesc.version) == (newMasterBackupDesc.version)));
            Assert.assertTrue(((lastSlaveBackupDesc.version) == (newSlaveBackupDesc.version)));
        }
    }

    @Test
    public void testTakeAndDeleteSnapshot() throws Exception {
        DomainClient client = ModelPersistenceTestCase.domainMasterLifecycleUtil.getDomainClient();
        // take snapshot
        ModelNode op = ModelUtil.createOpNode(null, "take-snapshot");
        ModelNode result = executeOperation(client, op);
        // check that the snapshot file exists
        String snapshotFileName = result.asString();
        File snapshotFile = new File(snapshotFileName);
        Assert.assertTrue(snapshotFile.exists());
        // compare with current cfg
        long snapshotHash = FileUtils.checksumCRC32(snapshotFile);
        long lastHash = FileUtils.checksumCRC32(ModelPersistenceTestCase.domainLastCfgFile);
        Assert.assertTrue((snapshotHash == lastHash));
        // delete snapshot
        op = ModelUtil.createOpNode(null, "delete-snapshot");
        op.get("name").set(snapshotFile.getName());
        executeOperation(client, op);
        // check that the file is deleted
        Assert.assertFalse("Snapshot file still exists.", snapshotFile.exists());
    }
}

