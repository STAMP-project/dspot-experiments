/**
 * !
 * Copyright 2010 - 2017 Hitachi Vantara.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pentaho.di.repository.pur;


import PentahoDefaults.KETTLE_DATA_SERVICE_ELEMENT_TYPE_DESCRIPTION;
import PentahoDefaults.KETTLE_DATA_SERVICE_ELEMENT_TYPE_NAME;
import RepositoryObjectType.JOB;
import RepositoryObjectType.PARTITION_SCHEMA;
import RepositoryObjectType.TRANSFORMATION;
import java.io.File;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import javax.xml.parsers.SAXParserFactory;
import org.apache.commons.vfs2.FileObject;
import org.junit.Assert;
import org.junit.Test;
import org.pentaho.di.cluster.ClusterSchema;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.NotePadMeta;
import org.pentaho.di.core.ProgressMonitorListener;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.logging.KettleLoggingEvent;
import org.pentaho.di.core.logging.KettleLoggingEventListener;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.imp.ImportRules;
import org.pentaho.di.imp.rule.ImportRuleInterface;
import org.pentaho.di.imp.rules.TransformationHasANoteImportRule;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.partition.PartitionSchema;
import org.pentaho.di.repository.IRepositoryExporter;
import org.pentaho.di.repository.ObjectRevision;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.RepositoryElementInterface;
import org.pentaho.di.repository.RepositoryObjectType;
import org.pentaho.di.repository.RepositoryTestBase;
import org.pentaho.di.repository.pur.metastore.MetaStoreTestBase;
import org.pentaho.di.shared.SharedObjectInterface;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.steps.tableinput.TableInputMeta;
import org.pentaho.metastore.api.IMetaStore;
import org.pentaho.metastore.api.IMetaStoreElement;
import org.pentaho.metastore.api.IMetaStoreElementType;
import org.pentaho.metastore.api.exceptions.MetaStoreDependenciesExistsException;
import org.pentaho.metastore.api.exceptions.MetaStoreException;
import org.pentaho.metastore.api.exceptions.MetaStoreNamespaceExistsException;
import org.pentaho.metastore.util.PentahoDefaults;
import org.pentaho.platform.api.engine.IAuthorizationPolicy;
import org.pentaho.platform.api.engine.security.userroledao.IUserRoleDao;
import org.pentaho.platform.api.mt.ITenant;
import org.pentaho.platform.api.mt.ITenantManager;
import org.pentaho.platform.api.mt.ITenantedPrincipleNameResolver;
import org.pentaho.platform.api.repository2.unified.IBackingRepositoryLifecycleManager;
import org.pentaho.platform.api.repository2.unified.IUnifiedRepository;
import org.pentaho.platform.repository2.unified.IRepositoryFileDao;
import org.pentaho.platform.security.policy.rolebased.IRoleAuthorizationPolicyRoleBindingDao;
import org.pentaho.platform.security.userroledao.DefaultTenantedPrincipleNameResolver;
import org.pentaho.test.platform.engine.core.MicroPlatform;
import org.springframework.context.ApplicationContextAware;
import org.springframework.extensions.jcr.JcrTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContextManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.DefaultHandler2;


@ContextConfiguration(locations = { "classpath:/repository.spring.xml", "classpath:/repository-test-override.spring.xml" })
public class PurRepositoryIT extends RepositoryTestBase implements Serializable , ApplicationContextAware {
    static final long serialVersionUID = 2064159405078106703L;/* EESOURCE: UPDATE SERIALVERUID */


    private IUnifiedRepository repo;

    private ITenantedPrincipleNameResolver userNameUtils = new DefaultTenantedPrincipleNameResolver();

    private ITenantedPrincipleNameResolver roleNameUtils = new DefaultTenantedPrincipleNameResolver(DefaultTenantedPrincipleNameResolver.ALTERNATE_DELIMETER);

    private ITenantManager tenantManager;

    private ITenant systemTenant;

    private IRoleAuthorizationPolicyRoleBindingDao roleBindingDaoTarget;

    private String repositoryAdminUsername;

    private JcrTemplate testJcrTemplate;

    private MicroPlatform mp;

    IUserRoleDao testUserRoleDao;

    IUserRoleDao userRoleDao;

    private String singleTenantAdminRoleName;

    private String tenantAuthenticatedRoleName;

    private String sysAdminUserName;

    private String superAdminRoleName;

    private TransactionTemplate txnTemplate;

    private IRepositoryFileDao repositoryFileDao;

    private final String TENANT_ID_ACME = "acme";

    private IBackingRepositoryLifecycleManager repositoryLifecyleManager;

    private final String TENANT_ID_DUFF = "duff";

    private static IAuthorizationPolicy authorizationPolicy;

    private TestContextManager testContextManager;

    public PurRepositoryIT(Boolean lazyRepo) {
        super(lazyRepo);
    }

    /**
     * Allow PentahoSystem to create this class but it in turn delegates to the authorizationPolicy fetched from Spring's
     * ApplicationContext.
     */
    public static class DelegatingAuthorizationPolicy implements IAuthorizationPolicy {
        public List<String> getAllowedActions(final String actionNamespace) {
            return PurRepositoryIT.authorizationPolicy.getAllowedActions(actionNamespace);
        }

        public boolean isAllowed(final String actionName) {
            return PurRepositoryIT.authorizationPolicy.isAllowed(actionName);
        }
    }

    @Test
    public void testNewNonAmbiguousNaming() throws Exception {
        PurRepository repo = ((PurRepository) (repository));
        System.setProperty("KETTLE_COMPATIBILITY_PUR_OLD_NAMING_MODE", "N");
        PartitionSchema partSchema1 = createPartitionSchema("find.me");// $NON-NLS-1$

        PartitionSchema partSchema2 = createPartitionSchema("find|me");// $NON-NLS-1$

        repository.save(partSchema1, RepositoryTestBase.VERSION_COMMENT_V1, null);
        repository.save(partSchema2, RepositoryTestBase.VERSION_COMMENT_V1, null);
        Map<RepositoryObjectType, List<? extends SharedObjectInterface>> sharedObjectsByType = new HashMap<RepositoryObjectType, List<? extends SharedObjectInterface>>();
        repo.readSharedObjects(sharedObjectsByType, PARTITION_SCHEMA);
        List<PartitionSchema> partitionSchemas = ((List<PartitionSchema>) (sharedObjectsByType.get(PARTITION_SCHEMA)));
        Assert.assertEquals(2, partitionSchemas.size());
        System.setProperty("KETTLE_COMPATIBILITY_PUR_OLD_NAMING_MODE", "Y");
        PartitionSchema partSchema3 = createPartitionSchema("another.one");// $NON-NLS-1$

        PartitionSchema partSchema4 = createPartitionSchema("another|one");// $NON-NLS-1$

        repository.save(partSchema3, RepositoryTestBase.VERSION_COMMENT_V1, null);
        repository.save(partSchema4, RepositoryTestBase.VERSION_COMMENT_V1, null);
        sharedObjectsByType = new HashMap<RepositoryObjectType, List<? extends SharedObjectInterface>>();
        repo.readSharedObjects(sharedObjectsByType, PARTITION_SCHEMA);
        partitionSchemas = ((List<PartitionSchema>) (sharedObjectsByType.get(PARTITION_SCHEMA)));
        Assert.assertEquals(3, partitionSchemas.size());
    }

    private class MockProgressMonitorListener implements ProgressMonitorListener {
        public void beginTask(String arg0, int arg1) {
        }

        public void done() {
        }

        public boolean isCanceled() {
            return false;
        }

        public void setTaskName(String arg0) {
        }

        public void subTask(String arg0) {
        }

        public void worked(int arg0) {
        }
    }

    private class MockRepositoryExportParser extends DefaultHandler2 {
        private List<String> nodeNames = new ArrayList<String>();

        private SAXParseException fatalError;

        private List<String> nodesToCapture = Arrays.asList("repository", "transformations", "transformation", "jobs", "job");

        // $NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            // Only capture nodes we care about
            if (nodesToCapture.contains(qName)) {
                nodeNames.add(qName);
            }
        }

        @Override
        public void fatalError(SAXParseException e) throws SAXException {
            fatalError = e;
        }

        public List<String> getNodesWithName(String name) {
            List<String> nodes = new ArrayList<String>();
            for (String node : nodeNames) {
                if (node.equals(name)) {
                    nodes.add(name);
                }
            }
            return nodes;
        }

        public List<String> getNodeNames() {
            return nodeNames;
        }

        public SAXParseException getFatalError() {
            return fatalError;
        }
    }

    @Test
    public void testExport() throws Exception {
        final String exportFileName = new File("test.export").getAbsolutePath();// $NON-NLS-1$

        RepositoryDirectoryInterface rootDir = initRepo();
        String uniqueTransName = RepositoryTestBase.EXP_TRANS_NAME.concat(RepositoryTestBase.EXP_DBMETA_NAME);
        TransMeta transMeta = createTransMeta(RepositoryTestBase.EXP_DBMETA_NAME);
        // Create a database association
        DatabaseMeta dbMeta = createDatabaseMeta(RepositoryTestBase.EXP_DBMETA_NAME);
        repository.save(dbMeta, RepositoryTestBase.VERSION_COMMENT_V1, null);
        TableInputMeta tableInputMeta = new TableInputMeta();
        tableInputMeta.setDatabaseMeta(dbMeta);
        transMeta.addStep(new org.pentaho.di.trans.step.StepMeta(RepositoryTestBase.EXP_TRANS_STEP_1_NAME, tableInputMeta));
        RepositoryDirectoryInterface transDir = rootDir.findDirectory(RepositoryTestBase.DIR_TRANSFORMATIONS);
        repository.save(transMeta, RepositoryTestBase.VERSION_COMMENT_V1, null);
        deleteStack.push(transMeta);// So this transformation is cleaned up afterward

        Assert.assertNotNull(transMeta.getObjectId());
        ObjectRevision version = transMeta.getObjectRevision();
        Assert.assertNotNull(version);
        Assert.assertTrue(hasVersionWithComment(transMeta, RepositoryTestBase.VERSION_COMMENT_V1));
        Assert.assertTrue(repository.exists(uniqueTransName, transDir, TRANSFORMATION));
        JobMeta jobMeta = createJobMeta(RepositoryTestBase.EXP_JOB_NAME);
        RepositoryDirectoryInterface jobsDir = rootDir.findDirectory(RepositoryTestBase.DIR_JOBS);
        repository.save(jobMeta, RepositoryTestBase.VERSION_COMMENT_V1, null);
        deleteStack.push(jobMeta);
        Assert.assertNotNull(jobMeta.getObjectId());
        version = jobMeta.getObjectRevision();
        Assert.assertNotNull(version);
        Assert.assertTrue(hasVersionWithComment(jobMeta, RepositoryTestBase.VERSION_COMMENT_V1));
        Assert.assertTrue(repository.exists(RepositoryTestBase.EXP_JOB_NAME, jobsDir, JOB));
        PurRepositoryIT.LogListener errorLogListener = new PurRepositoryIT.LogListener(LogLevel.ERROR);
        KettleLogStore.getAppender().addLoggingEventListener(errorLogListener);
        try {
            repository.getExporter().exportAllObjects(new PurRepositoryIT.MockProgressMonitorListener(), exportFileName, null, "all");// $NON-NLS-1$

            FileObject exportFile = KettleVFS.getFileObject(exportFileName);
            Assert.assertFalse("file left open", exportFile.getContent().isOpen());
            Assert.assertNotNull(exportFile);
            PurRepositoryIT.MockRepositoryExportParser parser = new PurRepositoryIT.MockRepositoryExportParser();
            SAXParserFactory.newInstance().newSAXParser().parse(KettleVFS.getInputStream(exportFile), parser);
            if ((parser.getFatalError()) != null) {
                throw parser.getFatalError();
            }
            Assert.assertNotNull("No nodes found in export", parser.getNodeNames());// $NON-NLS-1$

            Assert.assertTrue("No nodes found in export", (!(parser.getNodeNames().isEmpty())));// $NON-NLS-1$

            Assert.assertEquals("Incorrect number of nodes", 5, parser.getNodeNames().size());// $NON-NLS-1$

            Assert.assertEquals("Incorrect number of transformations", 1, parser.getNodesWithName("transformation").size());// $NON-NLS-1$ //$NON-NLS-2$

            Assert.assertEquals("Incorrect number of jobs", 1, parser.getNodesWithName("job").size());// $NON-NLS-1$ //$NON-NLS-2$

            Assert.assertTrue("log error", errorLogListener.getEvents().isEmpty());
        } finally {
            KettleVFS.getFileObject(exportFileName).delete();
            KettleLogStore.getAppender().removeLoggingEventListener(errorLogListener);
        }
    }

    @Test
    public void testMetaStoreBasics() throws MetaStoreException {
        IMetaStore metaStore = repository.getMetaStore();
        Assert.assertNotNull(metaStore);
        MetaStoreTestBase base = new MetaStoreTestBase();
        base.testFunctionality(metaStore);
    }

    @Test
    public void testMetaStoreNamespaces() throws MetaStoreException {
        IMetaStore metaStore = repository.getMetaStore();
        Assert.assertNotNull(metaStore);
        // We start with a clean slate, only the pentaho namespace
        // 
        Assert.assertEquals(1, metaStore.getNamespaces().size());
        String ns = PentahoDefaults.NAMESPACE;
        Assert.assertEquals(true, metaStore.namespaceExists(ns));
        metaStore.deleteNamespace(ns);
        Assert.assertEquals(false, metaStore.namespaceExists(ns));
        Assert.assertEquals(0, metaStore.getNamespaces().size());
        metaStore.createNamespace(ns);
        Assert.assertEquals(true, metaStore.namespaceExists(ns));
        List<String> namespaces = metaStore.getNamespaces();
        Assert.assertEquals(1, namespaces.size());
        Assert.assertEquals(ns, namespaces.get(0));
        try {
            metaStore.createNamespace(ns);
            Assert.fail("Exception expected when a namespace already exists and where we try to create it again");
        } catch (MetaStoreNamespaceExistsException e) {
            // OK, we expected this.
        }
        metaStore.deleteNamespace(ns);
        Assert.assertEquals(false, metaStore.namespaceExists(ns));
        Assert.assertEquals(0, metaStore.getNamespaces().size());
    }

    @Test
    public void testMetaStoreElementTypes() throws MetaStoreException {
        IMetaStore metaStore = repository.getMetaStore();
        Assert.assertNotNull(metaStore);
        String ns = PentahoDefaults.NAMESPACE;
        // We start with a clean slate...
        // 
        Assert.assertEquals(1, metaStore.getNamespaces().size());
        Assert.assertEquals(true, metaStore.namespaceExists(ns));
        // Now create an element type
        // 
        IMetaStoreElementType elementType = metaStore.newElementType(ns);
        elementType.setName(KETTLE_DATA_SERVICE_ELEMENT_TYPE_NAME);
        elementType.setDescription(KETTLE_DATA_SERVICE_ELEMENT_TYPE_DESCRIPTION);
        metaStore.createElementType(ns, elementType);
        IMetaStoreElementType verifyElementType = metaStore.getElementType(ns, elementType.getId());
        Assert.assertEquals(KETTLE_DATA_SERVICE_ELEMENT_TYPE_NAME, verifyElementType.getName());
        Assert.assertEquals(KETTLE_DATA_SERVICE_ELEMENT_TYPE_DESCRIPTION, verifyElementType.getDescription());
        verifyElementType = metaStore.getElementTypeByName(ns, KETTLE_DATA_SERVICE_ELEMENT_TYPE_NAME);
        Assert.assertEquals(KETTLE_DATA_SERVICE_ELEMENT_TYPE_NAME, verifyElementType.getName());
        Assert.assertEquals(KETTLE_DATA_SERVICE_ELEMENT_TYPE_DESCRIPTION, verifyElementType.getDescription());
        // Get the list of element type ids.
        // 
        List<String> ids = metaStore.getElementTypeIds(ns);
        Assert.assertNotNull(ids);
        Assert.assertEquals(1, ids.size());
        Assert.assertEquals(elementType.getId(), ids.get(0));
        // Verify that we can't delete the namespace since it has content in it!
        // 
        try {
            metaStore.deleteNamespace(ns);
            Assert.fail("The namespace deletion didn't cause an exception because there are still an element type in it");
        } catch (MetaStoreDependenciesExistsException e) {
            Assert.assertNotNull(e.getDependencies());
            Assert.assertEquals(1, e.getDependencies().size());
            Assert.assertEquals(elementType.getId(), e.getDependencies().get(0));
        }
        metaStore.deleteElementType(ns, elementType);
        Assert.assertEquals(0, metaStore.getElementTypes(ns).size());
        metaStore.deleteNamespace(ns);
    }

    @Test
    public void testMetaStoreElements() throws MetaStoreException {
        // Set up a namespace
        // 
        String ns = PentahoDefaults.NAMESPACE;
        IMetaStore metaStore = repository.getMetaStore();
        if (!(metaStore.namespaceExists(ns))) {
            metaStore.createNamespace(ns);
        }
        // And an element type
        // 
        IMetaStoreElementType elementType = metaStore.newElementType(ns);
        elementType.setName(KETTLE_DATA_SERVICE_ELEMENT_TYPE_NAME);
        elementType.setDescription(KETTLE_DATA_SERVICE_ELEMENT_TYPE_DESCRIPTION);
        metaStore.createElementType(ns, elementType);
        // Now we play with elements...
        // 
        IMetaStoreElement oneElement = populateElement(metaStore, elementType, "Element One");
        metaStore.createElement(ns, elementType, oneElement);
        IMetaStoreElement verifyOneElement = metaStore.getElement(ns, elementType, oneElement.getId());
        Assert.assertNotNull(verifyOneElement);
        validateElement(verifyOneElement, "Element One");
        Assert.assertEquals(1, metaStore.getElements(ns, elementType).size());
        IMetaStoreElement twoElement = populateElement(metaStore, elementType, "Element Two");
        metaStore.createElement(ns, elementType, twoElement);
        IMetaStoreElement verifyTwoElement = metaStore.getElement(ns, elementType, twoElement.getId());
        Assert.assertNotNull(verifyTwoElement);
        Assert.assertEquals(2, metaStore.getElements(ns, elementType).size());
        try {
            metaStore.deleteElementType(ns, elementType);
            Assert.fail("Delete element type failed to properly detect element dependencies");
        } catch (MetaStoreDependenciesExistsException e) {
            List<String> ids = e.getDependencies();
            Assert.assertEquals(2, ids.size());
            Assert.assertTrue(ids.contains(oneElement.getId()));
            Assert.assertTrue(ids.contains(twoElement.getId()));
        }
        metaStore.deleteElement(ns, elementType, oneElement.getId());
        Assert.assertEquals(1, metaStore.getElements(ns, elementType).size());
        metaStore.deleteElement(ns, elementType, twoElement.getId());
        Assert.assertEquals(0, metaStore.getElements(ns, elementType).size());
    }

    @Test
    public void doesNotChangeFileWhenFailsToRename_slaves() throws Exception {
        final SlaveServer server1 = new SlaveServer();
        final SlaveServer server2 = new SlaveServer();
        try {
            testDoesNotChangeFileWhenFailsToRename(server1, server2, new Callable<RepositoryElementInterface>() {
                @Override
                public RepositoryElementInterface call() throws Exception {
                    return repository.loadSlaveServer(server2.getObjectId(), null);
                }
            });
        } finally {
            repository.deleteSlave(server1.getObjectId());
            repository.deleteSlave(server2.getObjectId());
        }
    }

    @Test
    public void doesNotChangeFileWhenFailsToRename_clusters() throws Exception {
        final ClusterSchema schema1 = new ClusterSchema();
        final ClusterSchema schema2 = new ClusterSchema();
        try {
            testDoesNotChangeFileWhenFailsToRename(schema1, schema2, new Callable<RepositoryElementInterface>() {
                @Override
                public RepositoryElementInterface call() throws Exception {
                    return repository.loadClusterSchema(schema2.getObjectId(), null, null);
                }
            });
        } finally {
            repository.deleteClusterSchema(schema1.getObjectId());
            repository.deleteClusterSchema(schema2.getObjectId());
        }
    }

    @Test
    public void doesNotChangeFileWhenFailsToRename_partitions() throws Exception {
        final PartitionSchema schema1 = new PartitionSchema();
        final PartitionSchema schema2 = new PartitionSchema();
        try {
            testDoesNotChangeFileWhenFailsToRename(schema1, schema2, new Callable<RepositoryElementInterface>() {
                @Override
                public RepositoryElementInterface call() throws Exception {
                    return repository.loadPartitionSchema(schema2.getObjectId(), null);
                }
            });
        } finally {
            repository.deletePartitionSchema(schema1.getObjectId());
            repository.deletePartitionSchema(schema2.getObjectId());
        }
    }

    @Test
    public void testExportWithRules() throws Exception {
        String fileName = "testExportWithRuled.xml";
        final String exportFileName = new File(fileName).getAbsolutePath();// $NON-NLS-1$

        RepositoryDirectoryInterface rootDir = initRepo();
        String transWithoutNoteName = "2" + (RepositoryTestBase.EXP_DBMETA_NAME);
        TransMeta transWithoutNote = createTransMeta(transWithoutNoteName);
        String transUniqueName = RepositoryTestBase.EXP_TRANS_NAME.concat(transWithoutNoteName);
        RepositoryDirectoryInterface transDir = rootDir.findDirectory(RepositoryTestBase.DIR_TRANSFORMATIONS);
        repository.save(transWithoutNote, RepositoryTestBase.VERSION_COMMENT_V1, null);
        deleteStack.push(transWithoutNote);// So this transformation is cleaned up afterward

        Assert.assertNotNull(transWithoutNote.getObjectId());
        Assert.assertTrue(hasVersionWithComment(transWithoutNote, RepositoryTestBase.VERSION_COMMENT_V1));
        Assert.assertTrue(repository.exists(transUniqueName, transDir, TRANSFORMATION));
        // Second transformation (contained note)
        String transWithNoteName = "1" + (RepositoryTestBase.EXP_DBMETA_NAME);
        TransMeta transWithNote = createTransMeta(transWithNoteName);
        transUniqueName = RepositoryTestBase.EXP_TRANS_NAME.concat(RepositoryTestBase.EXP_DBMETA_NAME);
        TransMeta transWithRules = createTransMeta(RepositoryTestBase.EXP_DBMETA_NAME);
        NotePadMeta note = new NotePadMeta("Note Message", 1, 1, 100, 5);
        transWithRules.addNote(note);
        repository.save(transWithRules, RepositoryTestBase.VERSION_COMMENT_V1, null);
        deleteStack.push(transWithRules);// So this transformation is cleaned up afterward

        Assert.assertNotNull(transWithRules.getObjectId());
        Assert.assertTrue(hasVersionWithComment(transWithRules, RepositoryTestBase.VERSION_COMMENT_V1));
        Assert.assertTrue(repository.exists(transUniqueName, transDir, TRANSFORMATION));
        // create rules for export to .xml file
        List<ImportRuleInterface> rules = new AbstractList<ImportRuleInterface>() {
            @Override
            public ImportRuleInterface get(int index) {
                TransformationHasANoteImportRule rule = new TransformationHasANoteImportRule();
                rule.setEnabled(true);
                return rule;
            }

            @Override
            public int size() {
                return 1;
            }
        };
        ImportRules importRules = new ImportRules();
        importRules.setRules(rules);
        // create exporter
        IRepositoryExporter exporter = repository.getExporter();
        exporter.setImportRulesToValidate(importRules);
        // export itself
        try {
            exporter.exportAllObjects(new PurRepositoryIT.MockProgressMonitorListener(), exportFileName, null, "all");// $NON-NLS-1$

            FileObject exportFile = KettleVFS.getFileObject(exportFileName);
            Assert.assertNotNull(exportFile);
            PurRepositoryIT.MockRepositoryExportParser parser = new PurRepositoryIT.MockRepositoryExportParser();
            SAXParserFactory.newInstance().newSAXParser().parse(KettleVFS.getInputStream(exportFile), parser);
            if ((parser.getFatalError()) != null) {
                throw parser.getFatalError();
            }
            // assumed transformation with note will be here and only it
            Assert.assertEquals("Incorrect number of transformations", 1, parser.getNodesWithName(TRANSFORMATION.getTypeDescription()).size());// $NON-NLS-1$ //$NON-NLS-2$

        } finally {
            KettleVFS.getFileObject(exportFileName).delete();
        }
    }

    @Test
    public void testCreateRepositoryDirectory() throws KettleException {
        RepositoryDirectoryInterface tree = repository.loadRepositoryDirectoryTree();
        repository.createRepositoryDirectory(tree.findDirectory("home"), "/admin1");
        repository.createRepositoryDirectory(tree, "/home/admin2");
        repository.createRepositoryDirectory(tree, "/home/admin2/new1");
        RepositoryDirectoryInterface repositoryDirectory = repository.createRepositoryDirectory(tree, "/home/admin2/new1");
        repository.getJobAndTransformationObjects(repositoryDirectory.getObjectId(), false);
    }

    @Test
    public void testLoadJob() throws Exception {
        RepositoryDirectoryInterface rootDir = initRepo();
        JobMeta jobMeta = createJobMeta(RepositoryTestBase.EXP_JOB_NAME);
        RepositoryDirectoryInterface jobsDir = rootDir.findDirectory(RepositoryTestBase.DIR_JOBS);
        repository.save(jobMeta, RepositoryTestBase.VERSION_COMMENT_V1, null);
        deleteStack.push(jobMeta);
        JobMeta fetchedJob = repository.loadJob(RepositoryTestBase.EXP_JOB_NAME, jobsDir, null, null);
        JobMeta jobMetaById = repository.loadJob(jobMeta.getObjectId(), null);
        Assert.assertEquals(fetchedJob, jobMetaById);
        Assert.assertNotNull(fetchedJob.getMetaStore());
        Assert.assertTrue(((fetchedJob.getMetaStore()) == (jobMetaById.getMetaStore())));
    }

    protected static class LogListener implements KettleLoggingEventListener {
        private List<KettleLoggingEvent> events = new ArrayList<>();

        private LogLevel logThreshold;

        public LogListener(LogLevel logThreshold) {
            this.logThreshold = logThreshold;
        }

        public List<KettleLoggingEvent> getEvents() {
            return events;
        }

        public void eventAdded(KettleLoggingEvent event) {
            if ((logThreshold.getLevel()) >= (event.getLevel().getLevel())) {
                events.add(event);
            }
        }
    }
}

