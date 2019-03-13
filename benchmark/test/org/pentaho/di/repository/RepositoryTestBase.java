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
package org.pentaho.di.repository;


import RepositoryObjectType.CLUSTER_SCHEMA;
import RepositoryObjectType.DATABASE;
import RepositoryObjectType.PARTITION_SCHEMA;
import RepositoryObjectType.SLAVE_SERVER;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Stack;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.pentaho.di.cluster.ClusterSchema;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Condition;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.annotations.JobEntry;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.job.entry.JobEntryBase;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.pentaho.di.partition.PartitionSchema;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.ui.repository.pur.services.IRevisionService;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;


public abstract class RepositoryTestBase extends RepositoryTestLazySupport {
    // ~ Static fields/initializers ======================================================================================
    protected static final String EXP_USERNAME = "Apache Tomcat";

    protected static final String EXP_LOGIN = "admin";

    protected static final String EXP_TENANT = "acme";

    protected static final String EXP_LOGIN_PLUS_TENANT = ((RepositoryTestBase.EXP_LOGIN) + "-/pentaho/") + (RepositoryTestBase.EXP_TENANT);

    protected static final String VERSION_COMMENT_V1 = "hello";

    protected static final String VERSION_LABEL_V1 = "1.0";

    protected static final Log logger = LogFactory.getLog(RepositoryTestBase.class);

    protected static final String DIR_CONNECTIONS = "connections";

    protected static final String DIR_SCHEMAS = "schemas";

    protected static final String DIR_SLAVES = "slaves";

    protected static final String DIR_CLUSTERS = "clusters";

    protected static final String DIR_TRANSFORMATIONS = "transformations";

    protected static final String DIR_JOBS = "jobs";

    protected static final String DIR_TMP = "tmp";

    protected static final String EXP_JOB_NAME = "job1";

    protected static final String EXP_JOB_DESC = "jobDesc";

    protected static final String EXP_JOB_EXTENDED_DESC = "jobExtDesc";

    protected static final String EXP_JOB_VERSION = "anything";

    protected static final int EXP_JOB_STATUS = 12;

    protected static final String EXP_JOB_CREATED_USER = "jerry";

    protected static final Date EXP_JOB_CREATED_DATE = new Date();

    protected static final String EXP_JOB_MOD_USER = "george";

    protected static final Date EXP_JOB_MOD_DATE = new Date();

    protected static final String EXP_JOB_PARAM_1_DESC = "param1desc";

    protected static final String EXP_JOB_PARAM_1_NAME = "param1";

    protected static final String EXP_JOB_PARAM_1_DEF = "param1default";

    protected static final String EXP_JOB_LOG_TABLE_INTERVAL = "15";

    protected static final String EXP_JOB_LOG_TABLE_CONN_NAME = "connName";

    protected static final String EXP_JOB_LOG_TABLE_SCHEMA_NAME = "schemaName";

    protected static final String EXP_JOB_LOG_TABLE_TABLE_NAME = "tableName";

    protected static final String EXP_JOB_LOG_TABLE_TIMEOUT_IN_DAYS = "2";

    protected static final String EXP_JOB_LOG_TABLE_SIZE_LIMIT = "250";

    protected static final boolean EXP_JOB_BATCH_ID_PASSED = true;

    protected static final String EXP_JOB_SHARED_OBJECTS_FILE = ".kettle/whatever";

    protected static final String EXP_JOB_ENTRY_1_NAME = "createFile";

    protected static final String EXP_JOB_ENTRY_1_FILENAME = "/tmp/whatever";

    protected static final String EXP_JOB_ENTRY_2_NAME = "deleteFile";

    protected static final String EXP_JOB_ENTRY_2_FILENAME = "/tmp/whatever";

    protected static final int EXP_JOB_ENTRY_1_COPY_X_LOC = 10;

    protected static final int EXP_JOB_ENTRY_1_COPY_Y_LOC = 10;

    protected static final int EXP_JOB_ENTRY_2_COPY_X_LOC = 75;

    protected static final int EXP_JOB_ENTRY_2_COPY_Y_LOC = 10;

    protected static final int EXP_NOTEPAD_X = 10;

    protected static final String EXP_NOTEPAD_NOTE = "blah";

    protected static final int EXP_NOTEPAD_Y = 200;

    protected static final int EXP_NOTEPAD_WIDTH = 50;

    protected static final int EXP_NOTEPAD_HEIGHT = 25;

    protected static final String EXP_DBMETA_NAME = "haha";

    protected static final String EXP_DBMETA_HOSTNAME = "acme";

    protected static final String EXP_DBMETA_TYPE = "ORACLE";

    protected static final int EXP_DBMETA_ACCESS = DatabaseMeta.TYPE_ACCESS_NATIVE;

    protected static final String EXP_DBMETA_DBNAME = "lksjdf";

    protected static final String EXP_DBMETA_PORT = "10521";

    protected static final String EXP_DBMETA_USERNAME = "elaine";

    protected static final String EXP_DBMETA_PASSWORD = "password";

    protected static final String EXP_DBMETA_SERVERNAME = "serverName";

    protected static final String EXP_DBMETA_DATA_TABLESPACE = "dataTablespace";

    protected static final String EXP_DBMETA_INDEX_TABLESPACE = "indexTablespace";

    protected static final String EXP_SLAVE_NAME = "slave54545";

    protected static final String EXP_SLAVE_HOSTNAME = "slave98745";

    protected static final String EXP_SLAVE_PORT = "11111";

    protected static final String EXP_SLAVE_USERNAME = "cosmo";

    protected static final String EXP_SLAVE_PASSWORD = "password";

    protected static final String EXP_SLAVE_PROXY_HOSTNAME = "proxySlave542254";

    protected static final String EXP_SLAVE_PROXY_PORT = "11112";

    protected static final String EXP_SLAVE_NON_PROXY_HOSTS = "ljksdflsdf";

    protected static final boolean EXP_SLAVE_MASTER = true;

    protected static final String EXP_SLAVE_HOSTNAME_V2 = "slave98561111";

    protected static final String EXP_DBMETA_HOSTNAME_V2 = "acme98734";

    protected static final String VERSION_COMMENT_V2 = "v2 blah blah blah";

    protected static final String EXP_JOB_DESC_V2 = "jobDesc0368";

    protected static final String EXP_TRANS_NAME = "transMeta";

    protected static final String EXP_TRANS_DESC = "transMetaDesc";

    protected static final String EXP_TRANS_EXTENDED_DESC = "transMetaExtDesc";

    protected static final String EXP_TRANS_VERSION = "2.0";

    protected static final int EXP_TRANS_STATUS = 2;

    protected static final String EXP_TRANS_PARAM_1_DESC = "transParam1Desc";

    protected static final String EXP_TRANS_PARAM_1_DEF = "transParam1Def";

    protected static final String EXP_TRANS_PARAM_1_NAME = "transParamName";

    protected static final String EXP_TRANS_CREATED_USER = "newman";

    protected static final Date EXP_TRANS_CREATED_DATE = new Date();

    protected static final String EXP_TRANS_MOD_USER = "banya";

    protected static final Date EXP_TRANS_MOD_DATE = new Date();

    protected static final String EXP_TRANS_LOG_TABLE_CONN_NAME = "transLogTableConnName";

    protected static final String EXP_TRANS_LOG_TABLE_INTERVAL = "34";

    protected static final String EXP_TRANS_LOG_TABLE_SCHEMA_NAME = "transLogTableSchemaName";

    protected static final String EXP_TRANS_LOG_TABLE_SIZE_LIMIT = "600";

    protected static final String EXP_TRANS_LOG_TABLE_TABLE_NAME = "transLogTableTableName";

    protected static final String EXP_TRANS_LOG_TABLE_TIMEOUT_IN_DAYS = "5";

    protected static final String EXP_TRANS_MAX_DATE_TABLE = "transMaxDateTable";

    protected static final String EXP_TRANS_MAX_DATE_FIELD = "transMaxDateField";

    protected static final double EXP_TRANS_MAX_DATE_OFFSET = 55;

    protected static final double EXP_TRANS_MAX_DATE_DIFF = 70;

    protected static final int EXP_TRANS_SIZE_ROWSET = 833;

    protected static final int EXP_TRANS_SLEEP_TIME_EMPTY = 4;

    protected static final int EXP_TRANS_SLEEP_TIME_FULL = 9;

    protected static final boolean EXP_TRANS_USING_UNIQUE_CONN = true;

    protected static final boolean EXP_TRANS_FEEDBACK_SHOWN = true;

    protected static final int EXP_TRANS_FEEDBACK_SIZE = 222;

    protected static final boolean EXP_TRANS_USING_THREAD_PRIORITY_MGMT = true;

    protected static final String EXP_TRANS_SHARED_OBJECTS_FILE = "transSharedObjectsFile";

    protected static final boolean EXP_TRANS_CAPTURE_STEP_PERF_SNAPSHOTS = true;

    protected static final long EXP_TRANS_STEP_PERF_CAP_DELAY = 81;

    protected static final String EXP_TRANS_DEP_TABLE_NAME = "KLKJSDF";

    protected static final String EXP_TRANS_DEP_FIELD_NAME = "lkjsdfflll11";

    protected static final String EXP_PART_SCHEMA_NAME = "partitionSchemaName";

    protected static final String EXP_PART_SCHEMA_PARTID_2 = "partitionSchemaId2";

    protected static final boolean EXP_PART_SCHEMA_DYN_DEF = true;

    protected static final String EXP_PART_SCHEMA_PART_PER_SLAVE_COUNT = "562";

    protected static final String EXP_PART_SCHEMA_PARTID_1 = "partitionSchemaId1";

    protected static final String EXP_PART_SCHEMA_DESC = "partitionSchemaDesc";

    protected static final String EXP_PART_SCHEMA_PART_PER_SLAVE_COUNT_V2 = "563";

    protected static final String EXP_CLUSTER_SCHEMA_NAME = "clusterSchemaName";

    protected static final String EXP_CLUSTER_SCHEMA_SOCKETS_BUFFER_SIZE = "2048";

    protected static final String EXP_CLUSTER_SCHEMA_BASE_PORT = "12456";

    protected static final String EXP_CLUSTER_SCHEMA_SOCKETS_FLUSH_INTERVAL = "1500";

    protected static final boolean EXP_CLUSTER_SCHEMA_SOCKETS_COMPRESSED = true;

    protected static final boolean EXP_CLUSTER_SCHEMA_DYN = true;

    protected static final String EXP_CLUSTER_SCHEMA_BASE_PORT_V2 = "12457";

    protected static final String EXP_TRANS_STEP_1_NAME = "transStep1";

    protected static final String EXP_TRANS_STEP_2_NAME = "transStep2";

    protected static final boolean EXP_TRANS_STEP_ERROR_META_1_ENABLED = true;

    protected static final String EXP_TRANS_STEP_ERROR_META_1_NR_ERRORS_VALUE_NAME = "ihwefmcd";

    protected static final String EXP_TRANS_STEP_ERROR_META_1_DESC_VALUE_NAME = "lxeslsdff";

    protected static final String EXP_TRANS_STEP_ERROR_META_1_FIELDS_VALUE_NAME = "uiwcm";

    protected static final String EXP_TRANS_STEP_ERROR_META_1_CODES_VALUE_NAME = "wedsse";

    protected static final String EXP_TRANS_STEP_ERROR_META_1_MAX_ERRORS = "2000";

    protected static final String EXP_TRANS_STEP_ERROR_META_1_MAX_PERCENT_ERRORS = "29";

    protected static final String EXP_TRANS_STEP_ERROR_META_1_MIN_PERCENT_ROWS = "12";

    protected static final boolean EXP_TRANS_SLAVE_TRANSFORMATION = true;

    protected static final String EXP_TRANS_DESC_V2 = "transMetaDesc2";

    protected static final String EXP_TRANS_LOCK_MSG = "98u344jerfnsdmklfe";

    protected static final String EXP_JOB_LOCK_MSG = "ihesfdnmsdm348iesdm";

    protected static final String DIR_TMP2_NEW_NAME = "tmp2_new";

    protected static final String DIR_TMP2 = "tmp2";

    protected static final String EXP_JOB_NAME_NEW = "job98u34u5";

    protected static final String EXP_TRANS_NAME_NEW = "trans98jksdf32";

    protected static final String EXP_DBMETA_NAME_NEW = "database983kdaerer";

    private static final String EXP_DBMETA_ATTR1_VALUE = "LKJSDFKDSJKF";

    private static final String EXP_DBMETA_ATTR1_KEY = "IOWUEIOUEWR";

    private static final String EXP_DBMETA_ATTR2_KEY = "XDKDSDF";

    private static final String EXP_DBMETA_ATTR2_VALUE = "POYIUPOUI";

    private static final String EXP_DBMETA2_NAME = "abc_db2";

    private static final String EXP_DBMETA_NAME_STEP = "khdfsghk438";

    private static final String EXP_DBMETA_NAME_JOB = "KLJSDFJKL2";

    // ~ Instance fields =================================================================================================
    protected RepositoryMeta repositoryMeta;

    protected Repository repository;

    protected UserInfo userInfo;

    // necessary to delete in the correct order to avoid referential integrity problems
    protected Stack<RepositoryElementInterface> deleteStack;

    // ~ Constructors ====================================================================================================
    public RepositoryTestBase(Boolean lazyRepo) {
        super(lazyRepo);
    }

    /**
     * getUserInfo() getVersion() getName() isConnected() getRepositoryMeta() getLog()
     */
    @Test
    public void testVarious() throws Exception {
        // unfortunately UserInfo doesn't override equals()
        // for now, disable user checks, as a connection isn't made so no
        // user info is available
        // UserInfo userInfo = repository.getUserInfo();
        // assertEquals(EXP_LOGIN, userInfo.getName());
        // assertEquals("password", userInfo.getPassword());
        // assertEquals(EXP_USERNAME, userInfo.getUsername());
        // assertEquals("Apache Tomcat user", userInfo.getDescription());
        // assertTrue(userInfo.isEnabled());
        Assert.assertEquals(RepositoryTestBase.VERSION_LABEL_V1, repository.getVersion());
        Assert.assertEquals("JackRabbit", repository.getName());
        Assert.assertTrue(repository.isConnected());
        RepositoryMeta repoMeta = repository.getRepositoryMeta();
        Assert.assertEquals("JackRabbit", repoMeta.getName());
        Assert.assertEquals("JackRabbit test repository", repoMeta.getDescription());
        RepositoryCapabilities caps = repoMeta.getRepositoryCapabilities();
        Assert.assertTrue(caps.supportsUsers());
        Assert.assertTrue(caps.managesUsers());
        Assert.assertFalse(caps.isReadOnly());
        Assert.assertTrue(caps.supportsRevisions());
        Assert.assertTrue(caps.supportsMetadata());
        Assert.assertTrue(caps.supportsLocking());
        Assert.assertTrue(caps.hasVersionRegistry());
        Assert.assertNotNull(repository.getLog());
    }

    /**
     * save(partitionSchema) exists() loadPartitionSchema() deletePartitionSchema() getPartitionSchemaID()
     * getPartitionSchemaIDs() getPartitionSchemaNames()
     */
    @Test
    public void testPartitionSchemas() throws Exception {
        // RepositoryDirectoryInterface rootDir =
        initRepo();
        PartitionSchema partSchema = createPartitionSchema("");
        repository.save(partSchema, RepositoryTestBase.VERSION_COMMENT_V1, null);
        Assert.assertNotNull(partSchema.getObjectId());
        ObjectRevision version = partSchema.getObjectRevision();
        Assert.assertNotNull(version);
        Assert.assertTrue(hasVersionWithComment(partSchema, RepositoryTestBase.VERSION_COMMENT_V1));
        Assert.assertTrue(repository.exists(RepositoryTestBase.EXP_PART_SCHEMA_NAME, null, PARTITION_SCHEMA));
        PartitionSchema fetchedPartSchema = repository.loadPartitionSchema(partSchema.getObjectId(), null);
        Assert.assertEquals(RepositoryTestBase.EXP_PART_SCHEMA_NAME, fetchedPartSchema.getName());
        // TODO mlowery partitionSchema.getXML doesn't output desc either; should it?
        // assertEquals(EXP_PART_SCHEMA_DESC, fetchedPartSchema.getDescription());
        Assert.assertEquals(Arrays.asList(new String[]{ RepositoryTestBase.EXP_PART_SCHEMA_PARTID_1, RepositoryTestBase.EXP_PART_SCHEMA_PARTID_2 }), fetchedPartSchema.getPartitionIDs());
        Assert.assertEquals(RepositoryTestBase.EXP_PART_SCHEMA_DYN_DEF, fetchedPartSchema.isDynamicallyDefined());
        Assert.assertEquals(RepositoryTestBase.EXP_PART_SCHEMA_PART_PER_SLAVE_COUNT, fetchedPartSchema.getNumberOfPartitionsPerSlave());
        partSchema.setNumberOfPartitionsPerSlave(RepositoryTestBase.EXP_PART_SCHEMA_PART_PER_SLAVE_COUNT_V2);
        repository.save(partSchema, RepositoryTestBase.VERSION_COMMENT_V2, null);
        Assert.assertEquals(RepositoryTestBase.VERSION_COMMENT_V2, partSchema.getObjectRevision().getComment());
        fetchedPartSchema = repository.loadPartitionSchema(partSchema.getObjectId(), null);
        Assert.assertEquals(RepositoryTestBase.EXP_PART_SCHEMA_PART_PER_SLAVE_COUNT_V2, fetchedPartSchema.getNumberOfPartitionsPerSlave());
        fetchedPartSchema = repository.loadPartitionSchema(partSchema.getObjectId(), RepositoryTestBase.VERSION_LABEL_V1);
        Assert.assertEquals(RepositoryTestBase.EXP_PART_SCHEMA_PART_PER_SLAVE_COUNT, fetchedPartSchema.getNumberOfPartitionsPerSlave());
        Assert.assertEquals(partSchema.getObjectId(), repository.getPartitionSchemaID(RepositoryTestBase.EXP_PART_SCHEMA_NAME));
        Assert.assertEquals(1, repository.getPartitionSchemaIDs(false).length);
        Assert.assertEquals(1, repository.getPartitionSchemaIDs(true).length);
        Assert.assertEquals(partSchema.getObjectId(), repository.getPartitionSchemaIDs(false)[0]);
        Assert.assertEquals(1, repository.getPartitionSchemaNames(false).length);
        Assert.assertEquals(1, repository.getPartitionSchemaNames(true).length);
        Assert.assertEquals(RepositoryTestBase.EXP_PART_SCHEMA_NAME, repository.getPartitionSchemaNames(false)[0]);
        repository.deletePartitionSchema(partSchema.getObjectId());
        Assert.assertFalse(repository.exists(RepositoryTestBase.EXP_PART_SCHEMA_NAME, null, PARTITION_SCHEMA));
        Assert.assertEquals(0, repository.getPartitionSchemaIDs(false).length);
        // shared object deletion is permanent by default
        Assert.assertEquals(0, repository.getPartitionSchemaIDs(true).length);
        Assert.assertEquals(0, repository.getPartitionSchemaNames(false).length);
        // shared object deletion is permanent by default
        Assert.assertEquals(0, repository.getPartitionSchemaNames(true).length);
    }

    /**
     * save(clusterSchema) exists() loadClusterSchema() deleteClusterSchema() getClusterID() getClusterIDs()
     * getClusterNames()
     */
    @Test
    public void testClusterSchemas() throws Exception {
        // RepositoryDirectoryInterface rootDir =
        initRepo();
        ClusterSchema clusterSchema = createClusterSchema(RepositoryTestBase.EXP_CLUSTER_SCHEMA_NAME);
        repository.save(clusterSchema, RepositoryTestBase.VERSION_COMMENT_V1, null);
        Assert.assertNotNull(clusterSchema.getObjectId());
        ObjectRevision version = clusterSchema.getObjectRevision();
        Assert.assertNotNull(version);
        Assert.assertTrue(hasVersionWithComment(clusterSchema, RepositoryTestBase.VERSION_COMMENT_V1));
        Assert.assertTrue(repository.exists(RepositoryTestBase.EXP_CLUSTER_SCHEMA_NAME, null, CLUSTER_SCHEMA));
        ClusterSchema fetchedClusterSchema = repository.loadClusterSchema(clusterSchema.getObjectId(), repository.getSlaveServers(), null);
        Assert.assertEquals(RepositoryTestBase.EXP_CLUSTER_SCHEMA_NAME, fetchedClusterSchema.getName());
        // TODO mlowery clusterSchema.getXML doesn't output desc either; should it?
        // assertEquals(EXP_CLUSTER_SCHEMA_DESC, fetchedClusterSchema.getDescription());
        Assert.assertEquals(RepositoryTestBase.EXP_CLUSTER_SCHEMA_BASE_PORT, fetchedClusterSchema.getBasePort());
        Assert.assertEquals(RepositoryTestBase.EXP_CLUSTER_SCHEMA_SOCKETS_BUFFER_SIZE, fetchedClusterSchema.getSocketsBufferSize());
        Assert.assertEquals(RepositoryTestBase.EXP_CLUSTER_SCHEMA_SOCKETS_FLUSH_INTERVAL, fetchedClusterSchema.getSocketsFlushInterval());
        Assert.assertEquals(RepositoryTestBase.EXP_CLUSTER_SCHEMA_SOCKETS_COMPRESSED, fetchedClusterSchema.isSocketsCompressed());
        Assert.assertEquals(RepositoryTestBase.EXP_CLUSTER_SCHEMA_DYN, fetchedClusterSchema.isDynamic());
        Assert.assertEquals(1, fetchedClusterSchema.getSlaveServers().size());
        Assert.assertTrue(fetchedClusterSchema.getSlaveServers().get(0).getName().startsWith(RepositoryTestBase.EXP_SLAVE_NAME));
        // versioning test
        clusterSchema.setBasePort(RepositoryTestBase.EXP_CLUSTER_SCHEMA_BASE_PORT_V2);
        repository.save(clusterSchema, RepositoryTestBase.VERSION_COMMENT_V2, null);
        Assert.assertEquals(RepositoryTestBase.VERSION_COMMENT_V2, clusterSchema.getObjectRevision().getComment());
        fetchedClusterSchema = repository.loadClusterSchema(clusterSchema.getObjectId(), repository.getSlaveServers(), null);
        Assert.assertEquals(RepositoryTestBase.EXP_CLUSTER_SCHEMA_BASE_PORT_V2, fetchedClusterSchema.getBasePort());
        fetchedClusterSchema = repository.loadClusterSchema(clusterSchema.getObjectId(), repository.getSlaveServers(), RepositoryTestBase.VERSION_LABEL_V1);
        Assert.assertEquals(RepositoryTestBase.EXP_CLUSTER_SCHEMA_BASE_PORT, fetchedClusterSchema.getBasePort());
        Assert.assertEquals(clusterSchema.getObjectId(), repository.getClusterID(RepositoryTestBase.EXP_CLUSTER_SCHEMA_NAME));
        Assert.assertEquals(1, repository.getClusterIDs(false).length);
        Assert.assertEquals(1, repository.getClusterIDs(true).length);
        Assert.assertEquals(clusterSchema.getObjectId(), repository.getClusterIDs(false)[0]);
        Assert.assertEquals(1, repository.getClusterNames(false).length);
        Assert.assertEquals(1, repository.getClusterNames(true).length);
        Assert.assertEquals(RepositoryTestBase.EXP_CLUSTER_SCHEMA_NAME, repository.getClusterNames(false)[0]);
        repository.deleteClusterSchema(clusterSchema.getObjectId());
        Assert.assertFalse(repository.exists(RepositoryTestBase.EXP_CLUSTER_SCHEMA_NAME, null, CLUSTER_SCHEMA));
        Assert.assertEquals(0, repository.getClusterIDs(false).length);
        // shared object deletion is permanent by default
        Assert.assertEquals(0, repository.getClusterIDs(true).length);
        Assert.assertEquals(0, repository.getClusterNames(false).length);
        // shared object deletion is permanent by default
        Assert.assertEquals(0, repository.getClusterNames(true).length);
    }

    /**
     * save(databaseMeta) loadDatabaseMeta() exists() deleteDatabaseMeta() getDatabaseID() getDatabaseIDs()
     * getDatabaseNames() readDatabases()
     */
    @Test
    public void testDatabases() throws Exception {
        DatabaseMeta dbMeta = createDatabaseMeta(RepositoryTestBase.EXP_DBMETA_NAME);
        repository.save(dbMeta, RepositoryTestBase.VERSION_COMMENT_V1, null);
        Assert.assertNotNull(dbMeta.getObjectId());
        ObjectRevision v1 = dbMeta.getObjectRevision();
        Assert.assertNotNull(v1);
        Assert.assertTrue(hasVersionWithComment(dbMeta, RepositoryTestBase.VERSION_COMMENT_V1));
        // setting repository directory on dbMeta is not supported; use null parent directory
        Assert.assertTrue(repository.exists(RepositoryTestBase.EXP_DBMETA_NAME, null, DATABASE));
        DatabaseMeta fetchedDatabase = repository.loadDatabaseMeta(dbMeta.getObjectId(), null);
        Assert.assertEquals(RepositoryTestBase.EXP_DBMETA_NAME, fetchedDatabase.getName());
        Assert.assertEquals(RepositoryTestBase.EXP_DBMETA_HOSTNAME, fetchedDatabase.getHostname());
        Assert.assertEquals(RepositoryTestBase.EXP_DBMETA_TYPE, fetchedDatabase.getPluginId());
        Assert.assertEquals(RepositoryTestBase.EXP_DBMETA_ACCESS, fetchedDatabase.getAccessType());
        Assert.assertEquals(RepositoryTestBase.EXP_DBMETA_DBNAME, fetchedDatabase.getDatabaseName());
        Assert.assertEquals(RepositoryTestBase.EXP_DBMETA_PORT, fetchedDatabase.getDatabasePortNumberString());
        Assert.assertEquals(RepositoryTestBase.EXP_DBMETA_USERNAME, fetchedDatabase.getUsername());
        Assert.assertEquals(RepositoryTestBase.EXP_DBMETA_PASSWORD, fetchedDatabase.getPassword());
        Assert.assertEquals(RepositoryTestBase.EXP_DBMETA_SERVERNAME, fetchedDatabase.getServername());
        Assert.assertEquals(RepositoryTestBase.EXP_DBMETA_DATA_TABLESPACE, fetchedDatabase.getDataTablespace());
        Assert.assertEquals(RepositoryTestBase.EXP_DBMETA_INDEX_TABLESPACE, fetchedDatabase.getIndexTablespace());
        // 2 for the ones explicitly set and 1 for port (set behind the scenes)
        Assert.assertEquals((2 + 1), fetchedDatabase.getAttributes().size());
        Assert.assertEquals(RepositoryTestBase.EXP_DBMETA_ATTR1_VALUE, fetchedDatabase.getAttributes().getProperty(RepositoryTestBase.EXP_DBMETA_ATTR1_KEY));
        Assert.assertEquals(RepositoryTestBase.EXP_DBMETA_ATTR2_VALUE, fetchedDatabase.getAttributes().getProperty(RepositoryTestBase.EXP_DBMETA_ATTR2_KEY));
        dbMeta.setHostname(RepositoryTestBase.EXP_DBMETA_HOSTNAME_V2);
        repository.save(dbMeta, RepositoryTestBase.VERSION_COMMENT_V2, null);
        Assert.assertTrue(hasVersionWithComment(dbMeta, RepositoryTestBase.VERSION_COMMENT_V2));
        fetchedDatabase = repository.loadDatabaseMeta(dbMeta.getObjectId(), null);
        Assert.assertEquals(RepositoryTestBase.EXP_DBMETA_HOSTNAME_V2, fetchedDatabase.getHostname());
        fetchedDatabase = repository.loadDatabaseMeta(dbMeta.getObjectId(), v1.getName());
        Assert.assertEquals(RepositoryTestBase.EXP_DBMETA_HOSTNAME, fetchedDatabase.getHostname());
        Assert.assertEquals(dbMeta.getObjectId(), repository.getDatabaseID(RepositoryTestBase.EXP_DBMETA_NAME));
        Assert.assertEquals(1, repository.getDatabaseIDs(false).length);
        Assert.assertEquals(1, repository.getDatabaseIDs(true).length);
        Assert.assertEquals(dbMeta.getObjectId(), repository.getDatabaseIDs(false)[0]);
        Assert.assertEquals(1, repository.getDatabaseNames(false).length);
        Assert.assertEquals(1, repository.getDatabaseNames(true).length);
        Assert.assertEquals(RepositoryTestBase.EXP_DBMETA_NAME, repository.getDatabaseNames(false)[0]);
        Assert.assertEquals(1, repository.readDatabases().size());
        repository.deleteDatabaseMeta(RepositoryTestBase.EXP_DBMETA_NAME);
        Assert.assertFalse(repository.exists(RepositoryTestBase.EXP_DBMETA_NAME, null, DATABASE));
        Assert.assertEquals(0, repository.getDatabaseIDs(false).length);
        // shared object deletion is permanent by default
        Assert.assertEquals(0, repository.getDatabaseIDs(true).length);
        Assert.assertEquals(0, repository.getDatabaseNames(false).length);
        // shared object deletion is permanent by default
        Assert.assertEquals(0, repository.getDatabaseNames(true).length);
        Assert.assertEquals(0, repository.readDatabases().size());
    }

    /**
     * save(slave) loadSlaveServer() exists() deleteSlave() getSlaveID() getSlaveIDs() getSlaveNames() getSlaveServers()
     */
    @Test
    public void testSlaves() throws Exception {
        SlaveServer slave = createSlaveServer("");
        repository.save(slave, RepositoryTestBase.VERSION_COMMENT_V1, null);
        Assert.assertNotNull(slave.getObjectId());
        ObjectRevision version = slave.getObjectRevision();
        Assert.assertNotNull(version);
        Assert.assertTrue(hasVersionWithComment(slave, RepositoryTestBase.VERSION_COMMENT_V1));
        // setting repository directory on slave is not supported; use null parent directory
        Assert.assertTrue(repository.exists(RepositoryTestBase.EXP_SLAVE_NAME, null, SLAVE_SERVER));
        SlaveServer fetchedSlave = repository.loadSlaveServer(slave.getObjectId(), null);
        Assert.assertEquals(RepositoryTestBase.EXP_SLAVE_NAME, fetchedSlave.getName());
        Assert.assertEquals(RepositoryTestBase.EXP_SLAVE_HOSTNAME, fetchedSlave.getHostname());
        Assert.assertEquals(RepositoryTestBase.EXP_SLAVE_PORT, fetchedSlave.getPort());
        Assert.assertEquals(RepositoryTestBase.EXP_SLAVE_USERNAME, fetchedSlave.getUsername());
        Assert.assertEquals(RepositoryTestBase.EXP_SLAVE_PASSWORD, fetchedSlave.getPassword());
        Assert.assertEquals(RepositoryTestBase.EXP_SLAVE_PROXY_HOSTNAME, fetchedSlave.getProxyHostname());
        Assert.assertEquals(RepositoryTestBase.EXP_SLAVE_PROXY_PORT, fetchedSlave.getProxyPort());
        Assert.assertEquals(RepositoryTestBase.EXP_SLAVE_NON_PROXY_HOSTS, fetchedSlave.getNonProxyHosts());
        Assert.assertEquals(RepositoryTestBase.EXP_SLAVE_MASTER, fetchedSlave.isMaster());
        slave.setHostname(RepositoryTestBase.EXP_SLAVE_HOSTNAME_V2);
        repository.save(slave, RepositoryTestBase.VERSION_COMMENT_V2, null);
        Assert.assertEquals(RepositoryTestBase.VERSION_COMMENT_V2, slave.getObjectRevision().getComment());
        fetchedSlave = repository.loadSlaveServer(slave.getObjectId(), null);
        Assert.assertEquals(RepositoryTestBase.EXP_SLAVE_HOSTNAME_V2, fetchedSlave.getHostname());
        fetchedSlave = repository.loadSlaveServer(slave.getObjectId(), RepositoryTestBase.VERSION_LABEL_V1);
        Assert.assertEquals(RepositoryTestBase.EXP_SLAVE_HOSTNAME, fetchedSlave.getHostname());
        Assert.assertEquals(slave.getObjectId(), repository.getSlaveID(RepositoryTestBase.EXP_SLAVE_NAME));
        Assert.assertEquals(1, repository.getSlaveIDs(false).length);
        Assert.assertEquals(1, repository.getSlaveIDs(true).length);
        Assert.assertEquals(slave.getObjectId(), repository.getSlaveIDs(false)[0]);
        Assert.assertEquals(1, repository.getSlaveNames(false).length);
        Assert.assertEquals(1, repository.getSlaveNames(true).length);
        Assert.assertEquals(RepositoryTestBase.EXP_SLAVE_NAME, repository.getSlaveNames(false)[0]);
        Assert.assertEquals(1, repository.getSlaveServers().size());
        Assert.assertEquals(RepositoryTestBase.EXP_SLAVE_NAME, repository.getSlaveServers().get(0).getName());
        repository.deleteSlave(slave.getObjectId());
        Assert.assertFalse(repository.exists(RepositoryTestBase.EXP_SLAVE_NAME, null, SLAVE_SERVER));
        Assert.assertEquals(0, repository.getSlaveIDs(false).length);
        // shared object deletion is permanent by default
        Assert.assertEquals(0, repository.getSlaveIDs(true).length);
        Assert.assertEquals(0, repository.getSlaveNames(false).length);
        // shared object deletion is permanent by default
        Assert.assertEquals(0, repository.getSlaveNames(true).length);
        Assert.assertEquals(0, repository.getSlaveServers().size());
    }

    @Test
    public void testVersions() throws Exception {
        IRevisionService service = ((IRevisionService) (repository.getService(IRevisionService.class)));
        DatabaseMeta dbMeta = createDatabaseMeta(RepositoryTestBase.EXP_DBMETA_NAME);
        repository.save(dbMeta, RepositoryTestBase.VERSION_COMMENT_V1, null);
        deleteStack.push(dbMeta);
        List<ObjectRevision> revs = service.getRevisions(dbMeta);
        Assert.assertTrue(((revs.size()) >= 1));
        dbMeta.setHostname(RepositoryTestBase.EXP_DBMETA_HOSTNAME_V2);
        repository.save(dbMeta, RepositoryTestBase.VERSION_COMMENT_V2, null);
        revs = service.getRevisions(dbMeta);
        Assert.assertTrue(((revs.size()) >= 2));
        // RepositoryVersionRegistry vReg = repository.getVersionRegistry();
        // assertEquals(0, vReg.getVersions().size());
        // vReg.addVersion(new SimpleObjectVersion(EXP_OBJECT_VERSION_LABEL, null, null, null));
        // assertEquals(2, versions.size());
        // assertEquals("1.0", versions.get(0).getLabel());
        // assertEquals("1.1", versions.get(1).getLabel());
        // TODO mlowery finish me
    }

    public static interface EntryAndStepConstants {
        String ATTR_BOOL = "KDF";

        boolean VALUE_BOOL = true;

        String ATTR_BOOL_MULTI = "DFS";

        boolean VALUE_BOOL_MULTI_0 = true;

        boolean VALUE_BOOL_MULTI_1 = false;

        String ATTR_BOOL_NOEXIST = "IXS";

        boolean VALUE_BOOL_NOEXIST_DEF = true;

        String ATTR_INT = "TAS";

        int VALUE_INT = 4;

        String ATTR_INT_MULTI = "EZA";

        int VALUE_INT_MULTI_0 = 13;

        int VALUE_INT_MULTI_1 = 42;

        String ATTR_STRING = "YAZ";

        String VALUE_STRING = "sdfsdfsdfswe2222";

        String ATTR_STRING_MULTI = "LKS";

        String VALUE_STRING_MULTI_0 = "LKS";

        String VALUE_STRING_MULTI_1 = "LKS";

        String ATTR_DB = "dbMeta1";

        String ATTR_COND = "cond1";
    }

    /**
     * Does assertions on all repository.getJobEntryAttribute* and repository.saveJobEntryAttribute* methods.
     */
    @JobEntry(id = "JobEntryAttributeTester", image = "")
    public static class JobEntryAttributeTesterJobEntry extends JobEntryBase implements Cloneable , JobEntryInterface , RepositoryTestBase.EntryAndStepConstants {
        private DatabaseMeta databaseMeta;

        public JobEntryAttributeTesterJobEntry() {
            this("");
        }

        public JobEntryAttributeTesterJobEntry(final String name) {
            super(name, "");
        }

        @Override
        public void loadRep(final Repository rep, final IMetaStore metaStore, final ObjectId idJobentry, final List<DatabaseMeta> databases, final List<SlaveServer> slaveServers) throws KettleException {
            Assert.assertEquals(2, rep.countNrJobEntryAttributes(idJobentry, RepositoryTestBase.EntryAndStepConstants.ATTR_BOOL_MULTI));
            Assert.assertEquals(RepositoryTestBase.EntryAndStepConstants.VALUE_BOOL, rep.getJobEntryAttributeBoolean(idJobentry, RepositoryTestBase.EntryAndStepConstants.ATTR_BOOL));
            Assert.assertEquals(RepositoryTestBase.EntryAndStepConstants.VALUE_BOOL_MULTI_0, rep.getJobEntryAttributeBoolean(idJobentry, 0, RepositoryTestBase.EntryAndStepConstants.ATTR_BOOL_MULTI));
            Assert.assertEquals(RepositoryTestBase.EntryAndStepConstants.VALUE_BOOL_MULTI_1, rep.getJobEntryAttributeBoolean(idJobentry, 1, RepositoryTestBase.EntryAndStepConstants.ATTR_BOOL_MULTI));
            Assert.assertEquals(RepositoryTestBase.EntryAndStepConstants.VALUE_BOOL_NOEXIST_DEF, rep.getJobEntryAttributeBoolean(idJobentry, RepositoryTestBase.EntryAndStepConstants.ATTR_BOOL_NOEXIST, RepositoryTestBase.EntryAndStepConstants.VALUE_BOOL_NOEXIST_DEF));
            Assert.assertEquals(RepositoryTestBase.EntryAndStepConstants.VALUE_INT, rep.getJobEntryAttributeInteger(idJobentry, RepositoryTestBase.EntryAndStepConstants.ATTR_INT));
            Assert.assertEquals(RepositoryTestBase.EntryAndStepConstants.VALUE_INT_MULTI_0, rep.getJobEntryAttributeInteger(idJobentry, 0, RepositoryTestBase.EntryAndStepConstants.ATTR_INT_MULTI));
            Assert.assertEquals(RepositoryTestBase.EntryAndStepConstants.VALUE_INT_MULTI_1, rep.getJobEntryAttributeInteger(idJobentry, 1, RepositoryTestBase.EntryAndStepConstants.ATTR_INT_MULTI));
            Assert.assertEquals(RepositoryTestBase.EntryAndStepConstants.VALUE_STRING, rep.getJobEntryAttributeString(idJobentry, RepositoryTestBase.EntryAndStepConstants.ATTR_STRING));
            Assert.assertEquals(RepositoryTestBase.EntryAndStepConstants.VALUE_STRING_MULTI_0, rep.getJobEntryAttributeString(idJobentry, 0, RepositoryTestBase.EntryAndStepConstants.ATTR_STRING_MULTI));
            Assert.assertEquals(RepositoryTestBase.EntryAndStepConstants.VALUE_STRING_MULTI_1, rep.getJobEntryAttributeString(idJobentry, 1, RepositoryTestBase.EntryAndStepConstants.ATTR_STRING_MULTI));
            Assert.assertNotNull(rep.loadDatabaseMetaFromJobEntryAttribute(idJobentry, null, RepositoryTestBase.EntryAndStepConstants.ATTR_DB, databases));
        }

        @Override
        public void saveRep(final Repository rep, final IMetaStore metaStore, final ObjectId idJob) throws KettleException {
            rep.saveJobEntryAttribute(idJob, getObjectId(), RepositoryTestBase.EntryAndStepConstants.ATTR_BOOL, RepositoryTestBase.EntryAndStepConstants.VALUE_BOOL);
            rep.saveJobEntryAttribute(idJob, getObjectId(), 0, RepositoryTestBase.EntryAndStepConstants.ATTR_BOOL_MULTI, RepositoryTestBase.EntryAndStepConstants.VALUE_BOOL_MULTI_0);
            rep.saveJobEntryAttribute(idJob, getObjectId(), 1, RepositoryTestBase.EntryAndStepConstants.ATTR_BOOL_MULTI, RepositoryTestBase.EntryAndStepConstants.VALUE_BOOL_MULTI_1);
            rep.saveJobEntryAttribute(idJob, getObjectId(), RepositoryTestBase.EntryAndStepConstants.ATTR_INT, RepositoryTestBase.EntryAndStepConstants.VALUE_INT);
            rep.saveJobEntryAttribute(idJob, getObjectId(), 0, RepositoryTestBase.EntryAndStepConstants.ATTR_INT_MULTI, RepositoryTestBase.EntryAndStepConstants.VALUE_INT_MULTI_0);
            rep.saveJobEntryAttribute(idJob, getObjectId(), 1, RepositoryTestBase.EntryAndStepConstants.ATTR_INT_MULTI, RepositoryTestBase.EntryAndStepConstants.VALUE_INT_MULTI_1);
            rep.saveJobEntryAttribute(idJob, getObjectId(), RepositoryTestBase.EntryAndStepConstants.ATTR_STRING, RepositoryTestBase.EntryAndStepConstants.VALUE_STRING);
            rep.saveJobEntryAttribute(idJob, getObjectId(), 0, RepositoryTestBase.EntryAndStepConstants.ATTR_STRING_MULTI, RepositoryTestBase.EntryAndStepConstants.VALUE_STRING_MULTI_0);
            rep.saveJobEntryAttribute(idJob, getObjectId(), 1, RepositoryTestBase.EntryAndStepConstants.ATTR_STRING_MULTI, RepositoryTestBase.EntryAndStepConstants.VALUE_STRING_MULTI_1);
            rep.saveDatabaseMetaJobEntryAttribute(idJob, getObjectId(), null, RepositoryTestBase.EntryAndStepConstants.ATTR_DB, databaseMeta);
            rep.insertJobEntryDatabase(idJob, getObjectId(), databaseMeta.getObjectId());
        }

        public Result execute(final Result prevResult, final int nr) throws KettleException {
            throw new UnsupportedOperationException();
        }

        public void loadXML(final Node entrynode, final List<DatabaseMeta> databases, final List<SlaveServer> slaveServers, final Repository rep, final IMetaStore metaStore) throws KettleXMLException {
            throw new UnsupportedOperationException();
        }

        public void setDatabaseMeta(DatabaseMeta databaseMeta) {
            this.databaseMeta = databaseMeta;
        }
    }

    /**
     * Does assertions on all repository.getStepAttribute* and repository.saveStepAttribute* methods.
     */
    @Step(id = "StepAttributeTester", name = "StepAttributeTester", image = "")
    public static class TransStepAttributeTesterTransStep extends BaseStepMeta implements RepositoryTestBase.EntryAndStepConstants , StepMetaInterface {
        private DatabaseMeta databaseMeta;

        private Condition condition;

        public void check(List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepMeta, RowMetaInterface prev, String[] input, String[] output, RowMetaInterface info, VariableSpace space, Repository repository, IMetaStore metaStore) {
        }

        public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans) {
            return null;
        }

        public StepDataInterface getStepData() {
            return null;
        }

        public void loadXML(Node stepnode, List<DatabaseMeta> databases, final IMetaStore metaStore) throws KettleXMLException {
        }

        public void readRep(Repository rep, final IMetaStore metaStore, ObjectId idStep, List<DatabaseMeta> databases) throws KettleException {
            Assert.assertEquals(2, rep.countNrStepAttributes(idStep, RepositoryTestBase.EntryAndStepConstants.ATTR_BOOL_MULTI));
            Assert.assertEquals(RepositoryTestBase.EntryAndStepConstants.VALUE_BOOL, rep.getStepAttributeBoolean(idStep, RepositoryTestBase.EntryAndStepConstants.ATTR_BOOL));
            Assert.assertEquals(RepositoryTestBase.EntryAndStepConstants.VALUE_BOOL_MULTI_0, rep.getStepAttributeBoolean(idStep, 0, RepositoryTestBase.EntryAndStepConstants.ATTR_BOOL_MULTI));
            Assert.assertEquals(RepositoryTestBase.EntryAndStepConstants.VALUE_BOOL_MULTI_1, rep.getStepAttributeBoolean(idStep, 1, RepositoryTestBase.EntryAndStepConstants.ATTR_BOOL_MULTI));
            Assert.assertEquals(RepositoryTestBase.EntryAndStepConstants.VALUE_BOOL_NOEXIST_DEF, rep.getStepAttributeBoolean(idStep, 0, RepositoryTestBase.EntryAndStepConstants.ATTR_BOOL_NOEXIST, RepositoryTestBase.EntryAndStepConstants.VALUE_BOOL_NOEXIST_DEF));
            Assert.assertEquals(RepositoryTestBase.EntryAndStepConstants.VALUE_INT, rep.getStepAttributeInteger(idStep, RepositoryTestBase.EntryAndStepConstants.ATTR_INT));
            Assert.assertEquals(RepositoryTestBase.EntryAndStepConstants.VALUE_INT_MULTI_0, rep.getStepAttributeInteger(idStep, 0, RepositoryTestBase.EntryAndStepConstants.ATTR_INT_MULTI));
            Assert.assertEquals(RepositoryTestBase.EntryAndStepConstants.VALUE_INT_MULTI_1, rep.getStepAttributeInteger(idStep, 1, RepositoryTestBase.EntryAndStepConstants.ATTR_INT_MULTI));
            Assert.assertEquals(RepositoryTestBase.EntryAndStepConstants.VALUE_STRING, rep.getStepAttributeString(idStep, RepositoryTestBase.EntryAndStepConstants.ATTR_STRING));
            Assert.assertEquals(RepositoryTestBase.EntryAndStepConstants.VALUE_STRING_MULTI_0, rep.getStepAttributeString(idStep, 0, RepositoryTestBase.EntryAndStepConstants.ATTR_STRING_MULTI));
            Assert.assertEquals(RepositoryTestBase.EntryAndStepConstants.VALUE_STRING_MULTI_1, rep.getStepAttributeString(idStep, 1, RepositoryTestBase.EntryAndStepConstants.ATTR_STRING_MULTI));
            Assert.assertNotNull(rep.loadDatabaseMetaFromStepAttribute(idStep, RepositoryTestBase.EntryAndStepConstants.ATTR_DB, databases));
            Assert.assertNotNull(rep.loadConditionFromStepAttribute(idStep, RepositoryTestBase.EntryAndStepConstants.ATTR_COND));
        }

        public void saveRep(Repository rep, final IMetaStore metaStore, ObjectId idTransformation, ObjectId idStep) throws KettleException {
            rep.saveStepAttribute(idTransformation, idStep, RepositoryTestBase.EntryAndStepConstants.ATTR_BOOL, RepositoryTestBase.EntryAndStepConstants.VALUE_BOOL);
            rep.saveStepAttribute(idTransformation, idStep, 0, RepositoryTestBase.EntryAndStepConstants.ATTR_BOOL_MULTI, RepositoryTestBase.EntryAndStepConstants.VALUE_BOOL_MULTI_0);
            rep.saveStepAttribute(idTransformation, idStep, 1, RepositoryTestBase.EntryAndStepConstants.ATTR_BOOL_MULTI, RepositoryTestBase.EntryAndStepConstants.VALUE_BOOL_MULTI_1);
            rep.saveStepAttribute(idTransformation, idStep, RepositoryTestBase.EntryAndStepConstants.ATTR_INT, RepositoryTestBase.EntryAndStepConstants.VALUE_INT);
            rep.saveStepAttribute(idTransformation, idStep, 0, RepositoryTestBase.EntryAndStepConstants.ATTR_INT_MULTI, RepositoryTestBase.EntryAndStepConstants.VALUE_INT_MULTI_0);
            rep.saveStepAttribute(idTransformation, idStep, 1, RepositoryTestBase.EntryAndStepConstants.ATTR_INT_MULTI, RepositoryTestBase.EntryAndStepConstants.VALUE_INT_MULTI_1);
            rep.saveStepAttribute(idTransformation, idStep, RepositoryTestBase.EntryAndStepConstants.ATTR_STRING, RepositoryTestBase.EntryAndStepConstants.VALUE_STRING);
            rep.saveStepAttribute(idTransformation, idStep, 0, RepositoryTestBase.EntryAndStepConstants.ATTR_STRING_MULTI, RepositoryTestBase.EntryAndStepConstants.VALUE_STRING_MULTI_0);
            rep.saveStepAttribute(idTransformation, idStep, 1, RepositoryTestBase.EntryAndStepConstants.ATTR_STRING_MULTI, RepositoryTestBase.EntryAndStepConstants.VALUE_STRING_MULTI_1);
            rep.saveDatabaseMetaStepAttribute(idTransformation, idStep, RepositoryTestBase.EntryAndStepConstants.ATTR_DB, databaseMeta);
            rep.insertStepDatabase(idTransformation, idStep, databaseMeta.getObjectId());
            rep.saveConditionStepAttribute(idTransformation, idStep, RepositoryTestBase.EntryAndStepConstants.ATTR_COND, condition);
        }

        public void setDefault() {
        }

        public void setDatabaseMeta(final DatabaseMeta databaseMeta) {
            this.databaseMeta = databaseMeta;
        }

        public void setCondition(final Condition condition) {
            this.condition = condition;
        }
    }
}

