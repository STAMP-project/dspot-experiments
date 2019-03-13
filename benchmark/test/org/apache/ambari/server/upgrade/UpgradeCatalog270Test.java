/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.upgrade;


import AmbariServerConfigurationCategory.LDAP_CONFIGURATION;
import AmbariServerConfigurationKey.AMBARI_MANAGES_LDAP_CONFIGURATION;
import AmbariServerConfigurationKey.LDAP_ENABLED;
import AmbariServerConfigurationKey.LDAP_ENABLED_SERVICES;
import CaptureType.ALL;
import DBAccessor.DBColumnInfo;
import SecurityType.KERBEROS;
import SecurityType.NONE;
import State.UNKNOWN;
import StringUtils.EMPTY;
import UpgradeCatalog270.HRC_OPS_DISPLAY_NAME_COLUMN;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provider;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityManager;
import org.apache.ambari.server.AmbariException;
import org.apache.ambari.server.actionmanager.ActionManager;
import org.apache.ambari.server.actionmanager.HostRoleStatus;
import org.apache.ambari.server.agent.stomp.AgentConfigsHolder;
import org.apache.ambari.server.agent.stomp.MetadataHolder;
import org.apache.ambari.server.configuration.Configuration;
import org.apache.ambari.server.controller.AmbariManagementController;
import org.apache.ambari.server.controller.AmbariManagementControllerImpl;
import org.apache.ambari.server.controller.AmbariServer;
import org.apache.ambari.server.controller.KerberosHelper;
import org.apache.ambari.server.controller.MaintenanceStateHelper;
import org.apache.ambari.server.controller.ServiceConfigVersionResponse;
import org.apache.ambari.server.orm.DBAccessor;
import org.apache.ambari.server.orm.dao.AmbariConfigurationDAO;
import org.apache.ambari.server.orm.dao.ArtifactDAO;
import org.apache.ambari.server.orm.entities.ArtifactEntity;
import org.apache.ambari.server.serveraction.kerberos.PrepareKerberosIdentitiesServerAction;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.Config;
import org.apache.ambari.server.state.ConfigHelper;
import org.apache.ambari.server.state.StackId;
import org.apache.ambari.server.state.stack.OsFamily;
import org.apache.commons.io.IOUtils;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.easymock.MockType;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(EasyMockRunner.class)
public class UpgradeCatalog270Test {
    public static final Gson GSON = new Gson();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock(type = MockType.STRICT)
    private Provider<EntityManager> entityManagerProvider;

    @Mock(type = MockType.NICE)
    private Injector injector;

    @Mock(type = MockType.NICE)
    private EntityManager entityManager;

    @Mock(type = MockType.DEFAULT)
    private DBAccessor dbAccessor;

    @Mock(type = MockType.NICE)
    private OsFamily osFamily;

    @Mock(type = MockType.NICE)
    private Config config;

    @Mock(type = MockType.NICE)
    private ActionManager actionManager;

    @Mock(type = MockType.NICE)
    private Clusters clusters;

    @Mock(type = MockType.NICE)
    private Cluster cluster;

    @Mock(type = MockType.NICE)
    AmbariConfigurationDAO ambariConfigurationDao;

    @Mock(type = MockType.NICE)
    ArtifactDAO artifactDAO;

    @Mock(type = MockType.NICE)
    private AmbariManagementController ambariManagementController;

    @Test
    public void testExecuteDMLUpdates() throws Exception {
        Method addNewConfigurationsFromXml = AbstractUpgradeCatalog.class.getDeclaredMethod("addNewConfigurationsFromXml");
        Method showHcatDeletedUserMessage = UpgradeCatalog270.class.getDeclaredMethod("showHcatDeletedUserMessage");
        Method setStatusOfStagesAndRequests = UpgradeCatalog270.class.getDeclaredMethod("setStatusOfStagesAndRequests");
        Method updateLogSearchConfigs = UpgradeCatalog270.class.getDeclaredMethod("updateLogSearchConfigs");
        Method updateKerberosConfigurations = UpgradeCatalog270.class.getDeclaredMethod("updateKerberosConfigurations");
        Method upgradeLdapConfiguration = UpgradeCatalog270.class.getDeclaredMethod("moveAmbariPropertiesToAmbariConfiguration");
        Method createRoleAuthorizations = UpgradeCatalog270.class.getDeclaredMethod("createRoleAuthorizations");
        Method addUserAuthenticationSequence = UpgradeCatalog270.class.getDeclaredMethod("addUserAuthenticationSequence");
        Method renameAmbariInfra = UpgradeCatalog270.class.getDeclaredMethod("renameAmbariInfra");
        Method updateKerberosDescriptorArtifacts = UpgradeCatalog270.class.getSuperclass().getDeclaredMethod("updateKerberosDescriptorArtifacts");
        Method updateSolrConfigurations = UpgradeCatalog270.class.getDeclaredMethod("updateSolrConfigurations");
        Method updateAmsConfigurations = UpgradeCatalog270.class.getDeclaredMethod("updateAmsConfigs");
        Method updateStormConfigurations = UpgradeCatalog270.class.getDeclaredMethod("updateStormConfigs");
        Method clearHadoopMetrics2Content = UpgradeCatalog270.class.getDeclaredMethod("clearHadoopMetrics2Content");
        UpgradeCatalog270 upgradeCatalog270 = createMockBuilder(UpgradeCatalog270.class).addMockedMethod(showHcatDeletedUserMessage).addMockedMethod(addNewConfigurationsFromXml).addMockedMethod(setStatusOfStagesAndRequests).addMockedMethod(updateLogSearchConfigs).addMockedMethod(updateKerberosConfigurations).addMockedMethod(upgradeLdapConfiguration).addMockedMethod(createRoleAuthorizations).addMockedMethod(addUserAuthenticationSequence).addMockedMethod(renameAmbariInfra).addMockedMethod(updateKerberosDescriptorArtifacts).addMockedMethod(updateSolrConfigurations).addMockedMethod(updateAmsConfigurations).addMockedMethod(updateStormConfigurations).addMockedMethod(clearHadoopMetrics2Content).createMock();
        upgradeCatalog270.addNewConfigurationsFromXml();
        expectLastCall().once();
        upgradeCatalog270.showHcatDeletedUserMessage();
        expectLastCall().once();
        upgradeCatalog270.createRoleAuthorizations();
        expectLastCall().once();
        upgradeCatalog270.setStatusOfStagesAndRequests();
        expectLastCall().once();
        upgradeCatalog270.updateLogSearchConfigs();
        upgradeCatalog270.updateKerberosConfigurations();
        expectLastCall().once();
        upgradeCatalog270.moveAmbariPropertiesToAmbariConfiguration();
        expectLastCall().once();
        upgradeCatalog270.addUserAuthenticationSequence();
        expectLastCall().once();
        upgradeCatalog270.renameAmbariInfra();
        expectLastCall().once();
        upgradeCatalog270.updateKerberosDescriptorArtifacts();
        expectLastCall().once();
        upgradeCatalog270.updateSolrConfigurations();
        expectLastCall().once();
        upgradeCatalog270.updateAmsConfigs();
        expectLastCall().once();
        upgradeCatalog270.updateStormConfigs();
        expectLastCall().once();
        upgradeCatalog270.clearHadoopMetrics2Content();
        expectLastCall().once();
        replay(upgradeCatalog270);
        upgradeCatalog270.executeDMLUpdates();
        verify(upgradeCatalog270);
    }

    @Test
    public void testExecuteDDLUpdates() throws Exception {
        // dropBrokenFKs
        dbAccessor.dropFKConstraint(UpgradeCatalog270.COMPONENT_DESIRED_STATE_TABLE, UpgradeCatalog270.FK_HOSTCOMPONENTDESIREDSTATE_COMPONENT_NAME);
        expectLastCall().once();
        dbAccessor.dropFKConstraint(UpgradeCatalog270.COMPONENT_STATE_TABLE, UpgradeCatalog270.FK_HOSTCOMPONENTSTATE_COMPONENT_NAME);
        expectLastCall().once();
        dbAccessor.dropFKConstraint(UpgradeCatalog270.SERVICE_COMPONENT_DESIRED_STATE_TABLE, UpgradeCatalog270.FK_SERVICECOMPONENTDESIREDSTATE_SERVICE_NAME);
        expectLastCall().once();
        // updateStageTable
        Capture<DBAccessor.DBColumnInfo> updateStageTableCaptures = newCapture(ALL);
        dbAccessor.addColumn(eq(UpgradeCatalog270.STAGE_TABLE), capture(updateStageTableCaptures));
        expectLastCall().once();
        dbAccessor.addColumn(eq(UpgradeCatalog270.STAGE_TABLE), capture(updateStageTableCaptures));
        expectLastCall().once();
        dbAccessor.addColumn(eq(UpgradeCatalog270.REQUEST_TABLE), capture(updateStageTableCaptures));
        expectLastCall().once();
        // updateRequestTable
        Capture<DBAccessor.DBColumnInfo> updateRequestTableCapture = newCapture(ALL);
        dbAccessor.addColumn(eq(UpgradeCatalog270.REQUEST_TABLE), capture(updateRequestTableCapture));
        expectLastCall().once();
        // updateWidgetTable
        Capture<DBAccessor.DBColumnInfo> updateWidgetTableCapture = newCapture(ALL);
        dbAccessor.addColumn(eq(UpgradeCatalog270.WIDGET_TABLE), capture(updateWidgetTableCapture));
        expectLastCall().once();
        // addOpsDisplayNameColumnToHostRoleCommand
        Capture<DBAccessor.DBColumnInfo> hrcOpsDisplayNameColumn = newCapture();
        dbAccessor.addColumn(eq(UpgradeCatalog270.HOST_ROLE_COMMAND_TABLE), capture(hrcOpsDisplayNameColumn));
        expectLastCall().once();
        Capture<DBAccessor.DBColumnInfo> lastValidColumn = newCapture();
        dbAccessor.addColumn(eq(UpgradeCatalog270.COMPONENT_STATE_TABLE), capture(lastValidColumn));
        // removeSecurityState
        dbAccessor.dropColumn(UpgradeCatalog270.COMPONENT_DESIRED_STATE_TABLE, UpgradeCatalog270.SECURITY_STATE_COLUMN);
        expectLastCall().once();
        dbAccessor.dropColumn(UpgradeCatalog270.COMPONENT_STATE_TABLE, UpgradeCatalog270.SECURITY_STATE_COLUMN);
        expectLastCall().once();
        dbAccessor.dropColumn(UpgradeCatalog270.SERVICE_DESIRED_STATE_TABLE, UpgradeCatalog270.SECURITY_STATE_COLUMN);
        expectLastCall().once();
        // addAmbariConfigurationTable
        Capture<List<DBAccessor.DBColumnInfo>> ambariConfigurationTableColumns = newCapture();
        dbAccessor.createTable(eq(UpgradeCatalog270.AMBARI_CONFIGURATION_TABLE), capture(ambariConfigurationTableColumns));
        expectLastCall().once();
        dbAccessor.addPKConstraint(UpgradeCatalog270.AMBARI_CONFIGURATION_TABLE, "PK_ambari_configuration", UpgradeCatalog270.AMBARI_CONFIGURATION_CATEGORY_NAME_COLUMN, UpgradeCatalog270.AMBARI_CONFIGURATION_PROPERTY_NAME_COLUMN);
        expectLastCall().once();
        // upgradeUserTable - converting users.create_time to long
        Capture<DBAccessor.DBColumnInfo> temporaryColumnCreationCapture = newCapture(ALL);
        Capture<DBAccessor.DBColumnInfo> temporaryColumnRenameCapture = newCapture(ALL);
        // upgradeUserTable - create user_authentication table
        Capture<List<DBAccessor.DBColumnInfo>> createUserAuthenticationTableCaptures = newCapture(ALL);
        Capture<List<DBAccessor.DBColumnInfo>> createMembersTableCaptures = newCapture(ALL);
        Capture<List<DBAccessor.DBColumnInfo>> createAdminPrincipalTableCaptures = newCapture(ALL);
        Capture<DBAccessor.DBColumnInfo> updateUserTableCaptures = newCapture(ALL);
        Capture<DBAccessor.DBColumnInfo> alterUserTableCaptures = newCapture(ALL);
        Capture<List<DBAccessor.DBColumnInfo>> addRepoOsTableCapturedColumns = newCapture(ALL);
        Capture<List<DBAccessor.DBColumnInfo>> addRepoDefinitionTableCapturedColumns = newCapture(ALL);
        Capture<List<DBAccessor.DBColumnInfo>> addRepoTagsTableCapturedColumns = newCapture(ALL);
        Capture<List<DBAccessor.DBColumnInfo>> addRepoApplicableServicesTableCapturedColumns = newCapture(ALL);
        Capture<String[]> insertRepoOsTableRowColumns = newCapture(ALL);
        Capture<String[]> insertRepoOsTableRowValues = newCapture(ALL);
        Capture<String[]> insertAmbariSequencesRowColumns = newCapture(ALL);
        Capture<String[]> insertAmbariSequencesRowValues = newCapture(ALL);
        // Any return value will work here as long as a SQLException is not thrown.
        expect(dbAccessor.getColumnType(UpgradeCatalog270.USERS_TABLE, UpgradeCatalog270.USERS_USER_TYPE_COLUMN)).andReturn(0).anyTimes();
        prepareConvertingUsersCreationTime(dbAccessor, temporaryColumnCreationCapture, temporaryColumnRenameCapture);
        prepareCreateUserAuthenticationTable(dbAccessor, createUserAuthenticationTableCaptures);
        prepareUpdateGroupMembershipRecords(dbAccessor, createMembersTableCaptures);
        prepareUpdateAdminPrivilegeRecords(dbAccessor, createAdminPrincipalTableCaptures);
        prepareUpdateUsersTable(dbAccessor, updateUserTableCaptures, alterUserTableCaptures);
        prepareUpdateRepoTables(dbAccessor, addRepoOsTableCapturedColumns, addRepoDefinitionTableCapturedColumns, addRepoTagsTableCapturedColumns, addRepoApplicableServicesTableCapturedColumns, insertRepoOsTableRowColumns, insertRepoOsTableRowValues, insertAmbariSequencesRowColumns, insertAmbariSequencesRowValues);
        // upgradeKerberosTables
        Capture<List<DBAccessor.DBColumnInfo>> kerberosKeytabColumnsCapture = newCapture();
        dbAccessor.createTable(eq(UpgradeCatalog270.KERBEROS_KEYTAB_TABLE), capture(kerberosKeytabColumnsCapture));
        expectLastCall().once();
        dbAccessor.addPKConstraint(UpgradeCatalog270.KERBEROS_KEYTAB_TABLE, UpgradeCatalog270.PK_KERBEROS_KEYTAB, UpgradeCatalog270.KEYTAB_PATH_FIELD);
        expectLastCall().once();
        Capture<List<DBAccessor.DBColumnInfo>> kerberosKeytabPrincipalColumnsCapture = newCapture();
        dbAccessor.createTable(eq(UpgradeCatalog270.KERBEROS_KEYTAB_PRINCIPAL_TABLE), capture(kerberosKeytabPrincipalColumnsCapture));
        expectLastCall().once();
        dbAccessor.addPKConstraint(UpgradeCatalog270.KERBEROS_KEYTAB_PRINCIPAL_TABLE, UpgradeCatalog270.PK_KKP, UpgradeCatalog270.KKP_ID_COLUMN);
        expectLastCall().once();
        dbAccessor.addUniqueConstraint(UpgradeCatalog270.KERBEROS_KEYTAB_PRINCIPAL_TABLE, UpgradeCatalog270.UNI_KKP, UpgradeCatalog270.KEYTAB_PATH_FIELD, UpgradeCatalog270.PRINCIPAL_NAME_COLUMN, UpgradeCatalog270.HOST_ID_COLUMN);
        expectLastCall().once();
        Capture<List<DBAccessor.DBColumnInfo>> mappingColumnsCapture = newCapture();
        dbAccessor.createTable(eq(UpgradeCatalog270.KKP_MAPPING_SERVICE_TABLE), capture(mappingColumnsCapture));
        expectLastCall().once();
        dbAccessor.addPKConstraint(UpgradeCatalog270.KKP_MAPPING_SERVICE_TABLE, UpgradeCatalog270.PK_KKP_MAPPING_SERVICE, UpgradeCatalog270.KKP_ID_COLUMN, UpgradeCatalog270.SERVICE_NAME_COLUMN, UpgradeCatalog270.COMPONENT_NAME_COLUMN);
        expectLastCall().once();
        dbAccessor.addFKConstraint(UpgradeCatalog270.KERBEROS_KEYTAB_PRINCIPAL_TABLE, UpgradeCatalog270.FK_KKP_KEYTAB_PATH, UpgradeCatalog270.KEYTAB_PATH_FIELD, UpgradeCatalog270.KERBEROS_KEYTAB_TABLE, UpgradeCatalog270.KEYTAB_PATH_FIELD, false);
        expectLastCall().once();
        dbAccessor.addFKConstraint(UpgradeCatalog270.KERBEROS_KEYTAB_PRINCIPAL_TABLE, UpgradeCatalog270.FK_KKP_HOST_ID, UpgradeCatalog270.HOST_ID_COLUMN, UpgradeCatalog270.HOSTS_TABLE, UpgradeCatalog270.HOST_ID_COLUMN, false);
        expectLastCall().once();
        dbAccessor.addFKConstraint(UpgradeCatalog270.KERBEROS_KEYTAB_PRINCIPAL_TABLE, UpgradeCatalog270.FK_KKP_PRINCIPAL_NAME, UpgradeCatalog270.PRINCIPAL_NAME_COLUMN, UpgradeCatalog270.KERBEROS_PRINCIPAL_TABLE, UpgradeCatalog270.PRINCIPAL_NAME_COLUMN, false);
        expectLastCall().once();
        dbAccessor.addFKConstraint(UpgradeCatalog270.KKP_MAPPING_SERVICE_TABLE, UpgradeCatalog270.FK_KKP_SERVICE_PRINCIPAL, UpgradeCatalog270.KKP_ID_COLUMN, UpgradeCatalog270.KERBEROS_KEYTAB_PRINCIPAL_TABLE, UpgradeCatalog270.KKP_ID_COLUMN, false);
        expectLastCall().once();
        Connection c = niceMock(Connection.class);
        Statement s = niceMock(Statement.class);
        expect(s.executeQuery(anyString())).andReturn(null).once();
        expect(c.createStatement()).andReturn(s).once();
        expect(dbAccessor.getConnection()).andReturn(c).once();
        dbAccessor.dropTable(UpgradeCatalog270.KERBEROS_PRINCIPAL_HOST_TABLE);
        replay(dbAccessor);
        Injector injector = Guice.createInjector(getTestGuiceModule());
        UpgradeCatalog270 upgradeCatalog270 = injector.getInstance(UpgradeCatalog270.class);
        upgradeCatalog270.executeDDLUpdates();
        // Validate updateStageTableCaptures
        Assert.assertTrue(updateStageTableCaptures.hasCaptured());
        validateColumns(updateStageTableCaptures.getValues(), Arrays.asList(new DBAccessor.DBColumnInfo(UpgradeCatalog270.STAGE_STATUS_COLUMN, String.class, 255, HostRoleStatus.PENDING, false), new DBAccessor.DBColumnInfo(UpgradeCatalog270.STAGE_DISPLAY_STATUS_COLUMN, String.class, 255, HostRoleStatus.PENDING, false), new DBAccessor.DBColumnInfo(UpgradeCatalog270.REQUEST_DISPLAY_STATUS_COLUMN, String.class, 255, HostRoleStatus.PENDING, false)));
        // Validate updateRequestTableCapture
        Assert.assertTrue(updateRequestTableCapture.hasCaptured());
        validateColumns(updateRequestTableCapture.getValues(), Arrays.asList(new DBAccessor.DBColumnInfo(UpgradeCatalog270.REQUEST_USER_NAME_COLUMN, String.class, 255)));
        DBAccessor.DBColumnInfo capturedOpsDisplayNameColumn = hrcOpsDisplayNameColumn.getValue();
        Assert.assertEquals(HRC_OPS_DISPLAY_NAME_COLUMN, capturedOpsDisplayNameColumn.getName());
        Assert.assertEquals(null, capturedOpsDisplayNameColumn.getDefaultValue());
        Assert.assertEquals(String.class, capturedOpsDisplayNameColumn.getType());
        // Ambari configuration table addition...
        Assert.assertTrue(ambariConfigurationTableColumns.hasCaptured());
        validateColumns(ambariConfigurationTableColumns.getValue(), Arrays.asList(new DBAccessor.DBColumnInfo(UpgradeCatalog270.AMBARI_CONFIGURATION_CATEGORY_NAME_COLUMN, String.class, 100, null, false), new DBAccessor.DBColumnInfo(UpgradeCatalog270.AMBARI_CONFIGURATION_PROPERTY_NAME_COLUMN, String.class, 100, null, false), new DBAccessor.DBColumnInfo(UpgradeCatalog270.AMBARI_CONFIGURATION_PROPERTY_VALUE_COLUMN, String.class, 2048, null, true)));
        List<DBAccessor.DBColumnInfo> columns = ambariConfigurationTableColumns.getValue();
        Assert.assertEquals(3, columns.size());
        for (DBAccessor.DBColumnInfo column : columns) {
            String columnName = column.getName();
            if (UpgradeCatalog270.AMBARI_CONFIGURATION_CATEGORY_NAME_COLUMN.equals(columnName)) {
                Assert.assertEquals(String.class, column.getType());
                Assert.assertEquals(Integer.valueOf(100), column.getLength());
                Assert.assertEquals(null, column.getDefaultValue());
                Assert.assertFalse(column.isNullable());
            } else
                if (UpgradeCatalog270.AMBARI_CONFIGURATION_PROPERTY_NAME_COLUMN.equals(columnName)) {
                    Assert.assertEquals(String.class, column.getType());
                    Assert.assertEquals(Integer.valueOf(100), column.getLength());
                    Assert.assertEquals(null, column.getDefaultValue());
                    Assert.assertFalse(column.isNullable());
                } else
                    if (UpgradeCatalog270.AMBARI_CONFIGURATION_PROPERTY_VALUE_COLUMN.equals(columnName)) {
                        Assert.assertEquals(String.class, column.getType());
                        Assert.assertEquals(Integer.valueOf(2048), column.getLength());
                        Assert.assertEquals(null, column.getDefaultValue());
                        Assert.assertTrue(column.isNullable());
                    } else {
                        Assert.fail(("Unexpected column name: " + columnName));
                    }


        }
        // Ambari configuration table addition...
        DBAccessor.DBColumnInfo capturedLastValidColumn = lastValidColumn.getValue();
        Assert.assertEquals(upgradeCatalog270.COMPONENT_LAST_STATE_COLUMN, capturedLastValidColumn.getName());
        Assert.assertEquals(UNKNOWN, capturedLastValidColumn.getDefaultValue());
        Assert.assertEquals(String.class, capturedLastValidColumn.getType());
        validateConvertingUserCreationTime(temporaryColumnCreationCapture, temporaryColumnRenameCapture);
        validateCreateUserAuthenticationTable(createUserAuthenticationTableCaptures);
        validateUpdateGroupMembershipRecords(createMembersTableCaptures);
        validateUpdateAdminPrivilegeRecords(createAdminPrincipalTableCaptures);
        validateUpdateUsersTable(updateUserTableCaptures, alterUserTableCaptures);
        validateCreateRepoOsTable(addRepoOsTableCapturedColumns, addRepoDefinitionTableCapturedColumns, addRepoTagsTableCapturedColumns, insertRepoOsTableRowColumns, insertRepoOsTableRowValues, insertAmbariSequencesRowColumns, insertAmbariSequencesRowValues);
        verify(dbAccessor);
    }

    @Test
    public void testLogSearchUpdateConfigs() throws Exception {
        reset(clusters, cluster);
        expect(clusters.getClusters()).andReturn(ImmutableMap.of("normal", cluster)).once();
        EasyMockSupport easyMockSupport = new EasyMockSupport();
        Injector injector2 = easyMockSupport.createNiceMock(Injector.class);
        AmbariManagementControllerImpl controller = createMockBuilder(AmbariManagementControllerImpl.class).addMockedMethod("createConfiguration").addMockedMethod("getClusters", new Class[]{  }).addMockedMethod("createConfig").createNiceMock();
        ConfigHelper configHelper = createMockBuilder(ConfigHelper.class).addMockedMethod("createConfigType", Cluster.class, StackId.class, AmbariManagementController.class, String.class, Map.class, String.class, String.class).createMock();
        expect(injector2.getInstance(AmbariManagementController.class)).andReturn(controller).anyTimes();
        expect(injector2.getInstance(ConfigHelper.class)).andReturn(configHelper).anyTimes();
        expect(injector2.getInstance(DBAccessor.class)).andReturn(dbAccessor).anyTimes();
        expect(controller.getClusters()).andReturn(clusters).anyTimes();
        configHelper.createConfigType(anyObject(Cluster.class), anyObject(StackId.class), eq(controller), eq("logsearch-common-properties"), eq(Collections.emptyMap()), eq("ambari-upgrade"), eq("Updated logsearch-common-properties during Ambari Upgrade from 2.6.0 to 3.0.0"));
        expectLastCall().once();
        Map<String, String> oldLogSearchProperties = ImmutableMap.of("logsearch.logfeeder.include.default.level", "FATAL,ERROR,WARN");
        Map<String, String> expectedLogFeederProperties = ImmutableMap.of("logfeeder.include.default.level", "FATAL,ERROR,WARN");
        Config logFeederPropertiesConf = easyMockSupport.createNiceMock(Config.class);
        expect(cluster.getDesiredConfigByType("logfeeder-properties")).andReturn(logFeederPropertiesConf).times(2);
        expect(logFeederPropertiesConf.getProperties()).andReturn(Collections.emptyMap()).once();
        Capture<Map<String, String>> logFeederPropertiesCapture = EasyMock.newCapture();
        expect(controller.createConfig(anyObject(Cluster.class), anyObject(StackId.class), eq("logfeeder-properties"), capture(logFeederPropertiesCapture), anyString(), EasyMock.anyObject())).andReturn(config).once();
        Config logSearchPropertiesConf = easyMockSupport.createNiceMock(Config.class);
        expect(cluster.getDesiredConfigByType("logsearch-properties")).andReturn(logSearchPropertiesConf).times(2);
        expect(logSearchPropertiesConf.getProperties()).andReturn(oldLogSearchProperties).times(2);
        Capture<Map<String, String>> logSearchPropertiesCapture = EasyMock.newCapture();
        expect(controller.createConfig(anyObject(Cluster.class), anyObject(StackId.class), eq("logsearch-properties"), capture(logSearchPropertiesCapture), anyString(), EasyMock.anyObject())).andReturn(config).once();
        Map<String, String> oldLogFeederLog4j = ImmutableMap.of("content", "<!DOCTYPE log4j:configuration SYSTEM \"log4j.dtd\">");
        Map<String, String> expectedLogFeederLog4j = ImmutableMap.of("content", "<!DOCTYPE log4j:configuration SYSTEM \"http://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/xml/doc-files/log4j.dtd\">");
        Config logFeederLog4jConf = easyMockSupport.createNiceMock(Config.class);
        expect(cluster.getDesiredConfigByType("logfeeder-log4j")).andReturn(logFeederLog4jConf).atLeastOnce();
        expect(logFeederLog4jConf.getProperties()).andReturn(oldLogFeederLog4j).anyTimes();
        Capture<Map<String, String>> logFeederLog4jCapture = EasyMock.newCapture();
        expect(controller.createConfig(anyObject(Cluster.class), anyObject(StackId.class), anyString(), capture(logFeederLog4jCapture), anyString(), EasyMock.anyObject())).andReturn(config).once();
        Map<String, String> oldLogSearchLog4j = ImmutableMap.of("content", "<!DOCTYPE log4j:configuration SYSTEM \"log4j.dtd\">");
        Map<String, String> expectedLogSearchLog4j = ImmutableMap.of("content", "<!DOCTYPE log4j:configuration SYSTEM \"http://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/xml/doc-files/log4j.dtd\">");
        Config logSearchLog4jConf = easyMockSupport.createNiceMock(Config.class);
        expect(cluster.getDesiredConfigByType("logsearch-log4j")).andReturn(logSearchLog4jConf).atLeastOnce();
        expect(logSearchLog4jConf.getProperties()).andReturn(oldLogSearchLog4j).anyTimes();
        Capture<Map<String, String>> logSearchLog4jCapture = EasyMock.newCapture();
        expect(controller.createConfig(anyObject(Cluster.class), anyObject(StackId.class), anyString(), capture(logSearchLog4jCapture), anyString(), EasyMock.anyObject())).andReturn(config).once();
        Map<String, String> oldLogSearchServiceLogsConf = ImmutableMap.of("content", "<before/><requestHandler name=\"/admin/\"   class=\"solr.admin.AdminHandlers\" /><after/>");
        Map<String, String> expectedLogSearchServiceLogsConf = ImmutableMap.of("content", "<before/><after/>");
        Config logSearchServiceLogsConf = easyMockSupport.createNiceMock(Config.class);
        expect(cluster.getDesiredConfigByType("logsearch-service_logs-solrconfig")).andReturn(logSearchServiceLogsConf).atLeastOnce();
        expect(logSearchServiceLogsConf.getProperties()).andReturn(oldLogSearchServiceLogsConf).anyTimes();
        Capture<Map<String, String>> logSearchServiceLogsConfCapture = EasyMock.newCapture();
        expect(controller.createConfig(anyObject(Cluster.class), anyObject(StackId.class), anyString(), capture(logSearchServiceLogsConfCapture), anyString(), EasyMock.anyObject())).andReturn(config).once();
        Map<String, String> oldLogSearchAuditLogsConf = ImmutableMap.of("content", "<before/><requestHandler name=\"/admin/\"   class=\"solr.admin.AdminHandlers\" /><after/>");
        Map<String, String> expectedLogSearchAuditLogsConf = ImmutableMap.of("content", "<before/><after/>");
        Config logSearchAuditLogsConf = easyMockSupport.createNiceMock(Config.class);
        expect(cluster.getDesiredConfigByType("logsearch-audit_logs-solrconfig")).andReturn(logSearchAuditLogsConf).atLeastOnce();
        expect(logSearchAuditLogsConf.getProperties()).andReturn(oldLogSearchAuditLogsConf).anyTimes();
        Capture<Map<String, String>> logSearchAuditLogsConfCapture = EasyMock.newCapture();
        expect(controller.createConfig(anyObject(Cluster.class), anyObject(StackId.class), anyString(), capture(logSearchAuditLogsConfCapture), anyString(), EasyMock.anyObject())).andReturn(config).once();
        Map<String, String> oldLogFeederOutputConf = ImmutableMap.of("content", ("      \"zk_connect_string\":\"{{logsearch_solr_zk_quorum}}{{logsearch_solr_zk_znode}}\",\n" + ((((((("      \"collection\":\"{{logsearch_solr_collection_service_logs}}\",\n" + "      \"number_of_shards\": \"{{logsearch_collection_service_logs_numshards}}\",\n") + "      \"splits_interval_mins\": \"{{logsearch_service_logs_split_interval_mins}}\",\n") + "\n") + "      \"zk_connect_string\":\"{{logsearch_solr_zk_quorum}}{{logsearch_solr_zk_znode}}\",\n") + "      \"collection\":\"{{logsearch_solr_collection_audit_logs}}\",\n") + "      \"number_of_shards\": \"{{logsearch_collection_audit_logs_numshards}}\",\n") + "      \"splits_interval_mins\": \"{{logsearch_audit_logs_split_interval_mins}}\",\n")));
        Map<String, String> expectedLogFeederOutputConf = ImmutableMap.of("content", ("      \"zk_connect_string\":\"{{logsearch_solr_zk_quorum}}{{logsearch_solr_zk_znode}}\",\n" + ((("      \"type\": \"service\",\n" + "\n") + "      \"zk_connect_string\":\"{{logsearch_solr_zk_quorum}}{{logsearch_solr_zk_znode}}\",\n") + "      \"type\": \"audit\",\n")));
        Config logFeederOutputConf = easyMockSupport.createNiceMock(Config.class);
        expect(cluster.getDesiredConfigByType("logfeeder-output-config")).andReturn(logFeederOutputConf).atLeastOnce();
        expect(logFeederOutputConf.getProperties()).andReturn(oldLogFeederOutputConf).anyTimes();
        Capture<Map<String, String>> logFeederOutputConfCapture = EasyMock.newCapture();
        expect(controller.createConfig(anyObject(Cluster.class), anyObject(StackId.class), anyString(), capture(logFeederOutputConfCapture), anyString(), EasyMock.anyObject())).andReturn(config).once();
        String serviceConfigMapping = "serviceconfigmapping";
        String clusterConfig = "clusterconfig";
        dbAccessor.executeQuery(startsWith(("DELETE FROM " + serviceConfigMapping)));
        expectLastCall().once();
        dbAccessor.executeQuery(startsWith(("DELETE FROM " + clusterConfig)));
        expectLastCall().once();
        replay(clusters, cluster, dbAccessor);
        replay(controller, injector2);
        replay(logSearchPropertiesConf, logFeederPropertiesConf);
        replay(logFeederLog4jConf, logSearchLog4jConf);
        replay(logSearchServiceLogsConf, logSearchAuditLogsConf);
        replay(logFeederOutputConf);
        updateLogSearchConfigs();
        easyMockSupport.verifyAll();
        Map<String, String> newLogFeederProperties = logFeederPropertiesCapture.getValue();
        Assert.assertTrue(Maps.difference(expectedLogFeederProperties, newLogFeederProperties).areEqual());
        Map<String, String> newLogSearchProperties = logSearchPropertiesCapture.getValue();
        Assert.assertTrue(Maps.difference(Collections.emptyMap(), newLogSearchProperties).areEqual());
        Map<String, String> updatedLogFeederLog4j = logFeederLog4jCapture.getValue();
        Assert.assertTrue(Maps.difference(expectedLogFeederLog4j, updatedLogFeederLog4j).areEqual());
        Map<String, String> updatedLogSearchLog4j = logSearchLog4jCapture.getValue();
        Assert.assertTrue(Maps.difference(expectedLogSearchLog4j, updatedLogSearchLog4j).areEqual());
        Map<String, String> updatedServiceLogsConf = logSearchServiceLogsConfCapture.getValue();
        Assert.assertTrue(Maps.difference(expectedLogSearchServiceLogsConf, updatedServiceLogsConf).areEqual());
        Map<String, String> updatedAuditLogsConf = logSearchAuditLogsConfCapture.getValue();
        Assert.assertTrue(Maps.difference(expectedLogSearchAuditLogsConf, updatedAuditLogsConf).areEqual());
        Map<String, String> updatedLogFeederOutputConf = logFeederOutputConfCapture.getValue();
        Assert.assertTrue(Maps.difference(expectedLogFeederOutputConf, updatedLogFeederOutputConf).areEqual());
    }

    @Test
    public void testUpdateKerberosConfigurations() throws IllegalAccessException, NoSuchFieldException, AmbariException {
        StackId stackId = new StackId("HDP", "2.6.0.0");
        Map<String, Cluster> clusterMap = new HashMap<>();
        Map<String, String> propertiesWithGroup = new HashMap<>();
        propertiesWithGroup.put("group", "ambari_managed_identities");
        propertiesWithGroup.put("kdc_host", "host1.example.com");
        propertiesWithGroup.put("realm", "example.com");
        Config newConfig = createMock(Config.class);
        expect(newConfig.getTag()).andReturn("version2").atLeastOnce();
        expect(newConfig.getType()).andReturn("kerberos-env").atLeastOnce();
        ServiceConfigVersionResponse response = createMock(ServiceConfigVersionResponse.class);
        Config configWithGroup = createMock(Config.class);
        expect(configWithGroup.getProperties()).andReturn(propertiesWithGroup).atLeastOnce();
        expect(configWithGroup.getPropertiesAttributes()).andReturn(Collections.emptyMap()).atLeastOnce();
        expect(configWithGroup.getTag()).andReturn("version1").atLeastOnce();
        Cluster cluster1 = createMock(Cluster.class);
        expect(cluster1.getDesiredConfigByType("kerberos-env")).andReturn(configWithGroup).atLeastOnce();
        expect(cluster1.getConfigsByType("kerberos-env")).andReturn(Collections.singletonMap("v1", configWithGroup)).atLeastOnce();
        expect(cluster1.getServiceByConfigType("kerberos-env")).andReturn("KERBEROS").atLeastOnce();
        expect(cluster1.getClusterName()).andReturn("c1").atLeastOnce();
        expect(cluster1.getDesiredStackVersion()).andReturn(stackId).atLeastOnce();
        expect(cluster1.getConfig(eq("kerberos-env"), anyString())).andReturn(newConfig).atLeastOnce();
        expect(cluster1.addDesiredConfig("ambari-upgrade", Collections.singleton(newConfig), "Updated kerberos-env during Ambari Upgrade from 2.6.2 to 2.7.0.")).andReturn(response).once();
        Map<String, String> propertiesWithoutGroup = new HashMap<>();
        propertiesWithoutGroup.put("kdc_host", "host2.example.com");
        propertiesWithoutGroup.put("realm", "example.com");
        Config configWithoutGroup = createMock(Config.class);
        expect(configWithoutGroup.getProperties()).andReturn(propertiesWithoutGroup).atLeastOnce();
        Cluster cluster2 = createMock(Cluster.class);
        expect(cluster2.getDesiredConfigByType("kerberos-env")).andReturn(configWithoutGroup).atLeastOnce();
        Cluster cluster3 = createMock(Cluster.class);
        expect(cluster3.getDesiredConfigByType("kerberos-env")).andReturn(null).atLeastOnce();
        clusterMap.put("c1", cluster1);
        clusterMap.put("c2", cluster2);
        clusterMap.put("c3", cluster3);
        Clusters clusters = createMock(Clusters.class);
        expect(clusters.getClusters()).andReturn(clusterMap).anyTimes();
        Capture<Map<String, String>> capturedProperties = newCapture();
        AmbariManagementControllerImpl controller = createMockBuilder(AmbariManagementControllerImpl.class).addMockedMethod("createConfiguration").addMockedMethod("getClusters", new Class[]{  }).addMockedMethod("createConfig").addMockedMethod("getClusterMetadataOnConfigsUpdate", Cluster.class).createMock();
        expect(controller.getClusters()).andReturn(clusters).anyTimes();
        expect(controller.createConfig(eq(cluster1), eq(stackId), eq("kerberos-env"), capture(capturedProperties), anyString(), anyObject(Map.class))).andReturn(newConfig).once();
        Injector injector = createNiceMock(Injector.class);
        ConfigHelper configHelper = createStrictMock(ConfigHelper.class);
        expect(injector.getInstance(AmbariManagementController.class)).andReturn(controller).anyTimes();
        expect(injector.getInstance(MetadataHolder.class)).andReturn(createNiceMock(MetadataHolder.class)).anyTimes();
        expect(injector.getInstance(AgentConfigsHolder.class)).andReturn(createNiceMock(AgentConfigsHolder.class)).anyTimes();
        expect(injector.getInstance(AmbariServer.class)).andReturn(createNiceMock(AmbariServer.class)).anyTimes();
        expect(injector.getInstance(ConfigHelper.class)).andReturn(configHelper).anyTimes();
        KerberosHelper kerberosHelperMock = createNiceMock(KerberosHelper.class);
        expect(kerberosHelperMock.createTemporaryDirectory()).andReturn(new File("/invalid/file/path")).times(2);
        expect(injector.getInstance(KerberosHelper.class)).andReturn(kerberosHelperMock).anyTimes();
        configHelper.updateAgentConfigs(anyObject(Set.class));
        expectLastCall();
        replay(controller, clusters, cluster1, cluster2, configWithGroup, configWithoutGroup, newConfig, response, injector, kerberosHelperMock, configHelper);
        Field field = AbstractUpgradeCatalog.class.getDeclaredField("configuration");
        UpgradeCatalog270 upgradeCatalog270 = createMockBuilder(UpgradeCatalog270.class).addMockedMethod("getPrepareIdentityServerAction").addMockedMethod("executeInTransaction").createMock();
        PrepareKerberosIdentitiesServerAction mockAction = createNiceMock(PrepareKerberosIdentitiesServerAction.class);
        expect(upgradeCatalog270.getPrepareIdentityServerAction()).andReturn(mockAction).times(2);
        upgradeCatalog270.executeInTransaction(anyObject());
        expectLastCall().times(2);
        upgradeCatalog270.injector = injector;
        replay(upgradeCatalog270);
        field.set(upgradeCatalog270, createNiceMock(Configuration.class));
        upgradeCatalog270.updateKerberosConfigurations();
        verify(controller, clusters, cluster1, cluster2, configWithGroup, configWithoutGroup, newConfig, response, injector, upgradeCatalog270, configHelper);
        Assert.assertEquals(1, capturedProperties.getValues().size());
        Map<String, String> properties = capturedProperties.getValue();
        Assert.assertEquals(3, properties.size());
        Assert.assertEquals("ambari_managed_identities", properties.get("ipa_user_group"));
        Assert.assertEquals("host1.example.com", properties.get("kdc_host"));
        Assert.assertEquals("example.com", properties.get("realm"));
        Assert.assertEquals(3, propertiesWithGroup.size());
        Assert.assertEquals("ambari_managed_identities", propertiesWithGroup.get("group"));
        Assert.assertEquals("host1.example.com", propertiesWithGroup.get("kdc_host"));
        Assert.assertEquals("example.com", propertiesWithGroup.get("realm"));
    }

    @Test
    public void shouldSaveLdapConfigurationIfPropertyIsSetInAmbariProperties() throws Exception {
        final Module module = getTestGuiceModule();
        expect(entityManager.find(anyObject(), anyObject())).andReturn(null).anyTimes();
        final Map<String, String> properties = new HashMap<>();
        properties.put(LDAP_ENABLED.key(), "true");
        properties.put(AMBARI_MANAGES_LDAP_CONFIGURATION.key(), "true");
        properties.put(LDAP_ENABLED_SERVICES.key(), "AMBARI");
        expect(ambariConfigurationDao.reconcileCategory(LDAP_CONFIGURATION.getCategoryName(), properties, false)).andReturn(true).once();
        replay(entityManager, ambariConfigurationDao);
        final Injector injector = Guice.createInjector(module);
        injector.getInstance(Configuration.class).setProperty("ambari.ldap.isConfigured", "true");
        final UpgradeCatalog270 upgradeCatalog270 = new UpgradeCatalog270(injector);
        upgradeCatalog270.moveAmbariPropertiesToAmbariConfiguration();
        verify(entityManager, ambariConfigurationDao);
    }

    @Test
    public void shouldNotSaveLdapConfigurationIfPropertyIsNotSetInAmbariProperties() throws Exception {
        final Module module = getTestGuiceModule();
        expect(entityManager.find(anyObject(), anyObject())).andReturn(null).anyTimes();
        final Map<String, String> properties = new HashMap<>();
        properties.put(LDAP_ENABLED.key(), "true");
        expect(ambariConfigurationDao.reconcileCategory(LDAP_CONFIGURATION.getCategoryName(), properties, false)).andReturn(true).once();
        replay(entityManager, ambariConfigurationDao);
        final Injector injector = Guice.createInjector(module);
        final UpgradeCatalog270 upgradeCatalog270 = new UpgradeCatalog270(injector);
        upgradeCatalog270.moveAmbariPropertiesToAmbariConfiguration();
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("Expectation failure on verify");
        verify(entityManager, ambariConfigurationDao);
    }

    @Test
    public void testupdateKerberosDescriptorArtifact() throws Exception {
        String kerberosDescriptorJson = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("org/apache/ambari/server/upgrade/kerberos_descriptor.json"), "UTF-8");
        // there is HIVE -> WEBHCAT_SERVER -> configurations -> core-site -> hadoop.proxyuser.HTTP.hosts
        Assert.assertTrue(kerberosDescriptorJson.contains("${clusterHostInfo/webhcat_server_host|append(core-site/hadoop.proxyuser.HTTP.hosts, \\\\\\\\,, true)}"));
        Assert.assertTrue(kerberosDescriptorJson.contains("${clusterHostInfo/rm_host}"));
        ArtifactEntity artifactEntity = new ArtifactEntity();
        artifactEntity.setArtifactName("kerberos_descriptor");
        artifactEntity.setArtifactData(UpgradeCatalog270Test.GSON.<Map<String, Object>>fromJson(kerberosDescriptorJson, Map.class));
        UpgradeCatalog270 upgradeCatalog270 = createMockBuilder(UpgradeCatalog270.class).createMock();
        expect(artifactDAO.merge(artifactEntity)).andReturn(artifactEntity);
        replay(upgradeCatalog270);
        upgradeCatalog270.updateKerberosDescriptorArtifact(artifactDAO, artifactEntity);
        final String newKerberosDescriptorJson = UpgradeCatalog270Test.GSON.toJson(artifactEntity.getArtifactData());
        int oldCount = substringCount(kerberosDescriptorJson, UpgradeCatalog270.AMBARI_INFRA_OLD_NAME);
        int newCount = substringCount(newKerberosDescriptorJson, UpgradeCatalog270.AMBARI_INFRA_NEW_NAME);
        Assert.assertThat(newCount, Is.is(oldCount));
        Assert.assertTrue(newKerberosDescriptorJson.contains("${clusterHostInfo/webhcat_server_hosts|append(core-site/hadoop.proxyuser.HTTP.hosts, \\\\,, true)}"));
        Assert.assertTrue(newKerberosDescriptorJson.contains("${clusterHostInfo/resourcemanager_hosts}"));
        verify(upgradeCatalog270);
    }

    @Test
    public void testupdateLuceneMatchVersion() throws Exception {
        String solrConfigXml = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("org/apache/ambari/server/upgrade/solrconfig-v500.xml.j2"), "UTF-8");
        UpgradeCatalog270 upgradeCatalog270 = createMockBuilder(UpgradeCatalog270.class).createMock();
        replay(upgradeCatalog270);
        String updated = upgradeCatalog270.updateLuceneMatchVersion(solrConfigXml, "7.3.1");
        Assert.assertThat(updated.contains("<luceneMatchVersion>7.3.1</luceneMatchVersion>"), Is.is(true));
        Assert.assertThat(updated.contains("<luceneMatchVersion>5.0.0</luceneMatchVersion>"), Is.is(false));
        verify(upgradeCatalog270);
    }

    @Test
    public void testupdateMergeFactor() throws Exception {
        String solrConfigXml = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("org/apache/ambari/server/upgrade/solrconfig-v500.xml.j2"), "UTF-8");
        UpgradeCatalog270 upgradeCatalog270 = createMockBuilder(UpgradeCatalog270.class).createMock();
        replay(upgradeCatalog270);
        String updated = upgradeCatalog270.updateMergeFactor(solrConfigXml, "logsearch_service_logs_merge_factor");
        Assert.assertThat(updated.contains("<int name=\"maxMergeAtOnce\">{{logsearch_service_logs_merge_factor}}</int>"), Is.is(true));
        Assert.assertThat(updated.contains("<int name=\"segmentsPerTier\">{{logsearch_service_logs_merge_factor}}</int>"), Is.is(true));
        Assert.assertThat(updated.contains("<mergeFactor>{{logsearch_service_logs_merge_factor}}</mergeFactor>"), Is.is(false));
        verify(upgradeCatalog270);
    }

    @Test
    public void testupdateInfraSolrEnv() {
        String solrConfigXml = "#SOLR_HOST=\"192.168.1.1\"\n" + (("SOLR_HOST=\"192.168.1.1\"\n" + "SOLR_KERB_NAME_RULES=\"{{infra_solr_kerberos_name_rules}}\"\n") + "SOLR_AUTHENTICATION_CLIENT_CONFIGURER=\"org.apache.solr.client.solrj.impl.Krb5HttpClientConfigurer\"");
        UpgradeCatalog270 upgradeCatalog270 = createMockBuilder(UpgradeCatalog270.class).createMock();
        replay(upgradeCatalog270);
        String updated = upgradeCatalog270.updateInfraSolrEnv(solrConfigXml);
        Assert.assertThat(updated, Is.is("SOLR_HOST=`hostname -f`\nSOLR_HOST=`hostname -f`\n\nSOLR_AUTH_TYPE=\"kerberos\""));
        verify(upgradeCatalog270);
    }

    @Test
    public void testRemoveAdminHandlers() {
        UpgradeCatalog270 upgradeCatalog270 = createMockBuilder(UpgradeCatalog270.class).createMock();
        replay(upgradeCatalog270);
        String updated = upgradeCatalog270.removeAdminHandlers(("<requestHandler name=\"/admin/\"\n" + "                  class=\"solr.admin.AdminHandlers\"/>"));
        Assert.assertThat(updated, Is.is(""));
        verify(upgradeCatalog270);
    }

    @Test
    public void testUpdateAmsConfigs() throws Exception {
        Map<String, String> oldProperties = new HashMap<String, String>() {
            {
                put("timeline.metrics.service.default.result.limit", "15840");
                put("timeline.container-metrics.ttl", "2592000");
                put("timeline.metrics.cluster.aggregate.splitpoints", "cpu_user,mem_free");
                put("timeline.metrics.host.aggregate.splitpoints", "kafka.metric,nimbus.metric");
                put("timeline.metrics.downsampler.topn.metric.patterns", ("dfs.NNTopUserOpCounts.windowMs=60000.op=__%.user=%," + "dfs.NNTopUserOpCounts.windowMs=300000.op=__%.user=%,dfs.NNTopUserOpCounts.windowMs=1500000.op=__%.user=%"));
            }
        };
        Map<String, String> newProperties = new HashMap<String, String>() {
            {
                put("timeline.metrics.service.default.result.limit", "5760");
                put("timeline.container-metrics.ttl", "1209600");
                put("timeline.metrics.downsampler.topn.metric.patterns", EMPTY);
            }
        };
        Map<String, String> oldAmsHBaseSiteProperties = new HashMap<String, String>() {
            {
                put("hbase.snapshot.enabled", "false");
            }
        };
        Map<String, String> newAmsHBaseSiteProperties = new HashMap<String, String>() {
            {
                put("hbase.snapshot.enabled", "true");
            }
        };
        EasyMockSupport easyMockSupport = new EasyMockSupport();
        Clusters clusters = easyMockSupport.createNiceMock(Clusters.class);
        final Cluster cluster = easyMockSupport.createNiceMock(Cluster.class);
        Config mockAmsSite = easyMockSupport.createNiceMock(Config.class);
        expect(clusters.getClusters()).andReturn(new HashMap<String, Cluster>() {
            {
                put("normal", cluster);
            }
        }).once();
        expect(cluster.getDesiredConfigByType("ams-site")).andReturn(mockAmsSite).atLeastOnce();
        expect(mockAmsSite.getProperties()).andReturn(oldProperties).anyTimes();
        Config mockAmsHbaseSite = easyMockSupport.createNiceMock(Config.class);
        expect(cluster.getDesiredConfigByType("ams-hbase-site")).andReturn(mockAmsHbaseSite).atLeastOnce();
        expect(mockAmsHbaseSite.getProperties()).andReturn(oldAmsHBaseSiteProperties).anyTimes();
        Injector injector = easyMockSupport.createNiceMock(Injector.class);
        expect(injector.getInstance(Gson.class)).andReturn(null).anyTimes();
        expect(injector.getInstance(MaintenanceStateHelper.class)).andReturn(null).anyTimes();
        replay(injector, clusters, mockAmsSite, mockAmsHbaseSite, cluster);
        AmbariManagementControllerImpl controller = createMockBuilder(AmbariManagementControllerImpl.class).addMockedMethod("createConfiguration").addMockedMethod("getClusters", new Class[]{  }).addMockedMethod("createConfig").withConstructor(createNiceMock(ActionManager.class), clusters, injector).createNiceMock();
        Injector injector2 = easyMockSupport.createNiceMock(Injector.class);
        Capture<Map> propertiesCapture = EasyMock.newCapture(ALL);
        expect(injector2.getInstance(AmbariManagementController.class)).andReturn(controller).anyTimes();
        expect(controller.getClusters()).andReturn(clusters).anyTimes();
        expect(controller.createConfig(anyObject(Cluster.class), anyObject(StackId.class), anyString(), capture(propertiesCapture), anyString(), anyObject(Map.class))).andReturn(createNiceMock(Config.class)).times(2);
        replay(controller, injector2);
        updateAmsConfigs();
        easyMockSupport.verifyAll();
        Assert.assertEquals(propertiesCapture.getValues().size(), 2);
        Map<String, String> updatedProperties = propertiesCapture.getValues().get(0);
        Assert.assertTrue(Maps.difference(newProperties, updatedProperties).areEqual());
        updatedProperties = propertiesCapture.getValues().get(1);
        Assert.assertTrue(Maps.difference(newAmsHBaseSiteProperties, updatedProperties).areEqual());
    }

    @Test
    public void testUpdateAmsConfigsWithNoContainerMetrics() throws Exception {
        Map<String, String> oldProperties = new HashMap<String, String>() {
            {
                put("timeline.metrics.service.default.result.limit", "15840");
                put("timeline.metrics.host.aggregate.splitpoints", "kafka.metric,nimbus.metric");
            }
        };
        Map<String, String> newProperties = new HashMap<String, String>() {
            {
                put("timeline.metrics.service.default.result.limit", "5760");
            }
        };
        EasyMockSupport easyMockSupport = new EasyMockSupport();
        Clusters clusters = easyMockSupport.createNiceMock(Clusters.class);
        final Cluster cluster = easyMockSupport.createNiceMock(Cluster.class);
        Config mockAmsSite = easyMockSupport.createNiceMock(Config.class);
        expect(clusters.getClusters()).andReturn(new HashMap<String, Cluster>() {
            {
                put("normal", cluster);
            }
        }).once();
        expect(cluster.getDesiredConfigByType("ams-site")).andReturn(mockAmsSite).atLeastOnce();
        expect(mockAmsSite.getProperties()).andReturn(oldProperties).anyTimes();
        Injector injector = easyMockSupport.createNiceMock(Injector.class);
        expect(injector.getInstance(Gson.class)).andReturn(null).anyTimes();
        expect(injector.getInstance(MaintenanceStateHelper.class)).andReturn(null).anyTimes();
        replay(injector, clusters, mockAmsSite, cluster);
        AmbariManagementControllerImpl controller = createMockBuilder(AmbariManagementControllerImpl.class).addMockedMethod("createConfiguration").addMockedMethod("getClusters", new Class[]{  }).addMockedMethod("createConfig").withConstructor(createNiceMock(ActionManager.class), clusters, injector).createNiceMock();
        Injector injector2 = easyMockSupport.createNiceMock(Injector.class);
        Capture<Map> propertiesCapture = EasyMock.newCapture();
        expect(injector2.getInstance(AmbariManagementController.class)).andReturn(controller).anyTimes();
        expect(controller.getClusters()).andReturn(clusters).anyTimes();
        expect(controller.createConfig(anyObject(Cluster.class), anyObject(StackId.class), anyString(), capture(propertiesCapture), anyString(), anyObject(Map.class))).andReturn(createNiceMock(Config.class)).once();
        replay(controller, injector2);
        updateAmsConfigs();
        easyMockSupport.verifyAll();
        Map<String, String> updatedProperties = propertiesCapture.getValue();
        Assert.assertTrue(Maps.difference(newProperties, updatedProperties).areEqual());
    }

    @Test
    public void testStormConfigs() throws Exception {
        Map<String, String> stormProperties = new HashMap<String, String>() {
            {
                put("_storm.thrift.nonsecure.transport", "org.apache.storm.security.auth.SimpleTransportPlugin");
                put("_storm.thrift.secure.transport", "org.apache.storm.security.auth.kerberos.KerberosSaslTransportPlugin");
                put("storm.thrift.transport", "{{storm_thrift_transport}}");
                put("storm.zookeeper.port", "2181");
            }
        };
        Map<String, String> newStormProperties = new HashMap<String, String>() {
            {
                put("storm.thrift.transport", "org.apache.storm.security.auth.SimpleTransportPlugin");
                put("storm.zookeeper.port", "2181");
            }
        };
        EasyMockSupport easyMockSupport = new EasyMockSupport();
        Clusters clusters = easyMockSupport.createNiceMock(Clusters.class);
        final Cluster cluster = easyMockSupport.createNiceMock(Cluster.class);
        Config mockStormSite = easyMockSupport.createNiceMock(Config.class);
        expect(clusters.getClusters()).andReturn(new HashMap<String, Cluster>() {
            {
                put("normal", cluster);
            }
        }).once();
        expect(cluster.getDesiredConfigByType("storm-site")).andReturn(mockStormSite).atLeastOnce();
        expect(cluster.getSecurityType()).andReturn(NONE).anyTimes();
        expect(mockStormSite.getProperties()).andReturn(stormProperties).anyTimes();
        Injector injector = easyMockSupport.createNiceMock(Injector.class);
        replay(injector, clusters, mockStormSite, cluster);
        AmbariManagementControllerImpl controller = createMockBuilder(AmbariManagementControllerImpl.class).addMockedMethod("getClusters", new Class[]{  }).addMockedMethod("createConfig").withConstructor(createNiceMock(ActionManager.class), clusters, injector).createNiceMock();
        Injector injector2 = easyMockSupport.createNiceMock(Injector.class);
        Capture<Map> propertiesCapture = EasyMock.newCapture();
        expect(injector2.getInstance(AmbariManagementController.class)).andReturn(controller).anyTimes();
        expect(controller.getClusters()).andReturn(clusters).anyTimes();
        expect(controller.createConfig(anyObject(Cluster.class), anyObject(StackId.class), anyString(), capture(propertiesCapture), anyString(), anyObject(Map.class))).andReturn(createNiceMock(Config.class)).once();
        replay(controller, injector2);
        updateStormConfigs();
        easyMockSupport.verifyAll();
        Map<String, String> updatedProperties = propertiesCapture.getValue();
        Assert.assertTrue(Maps.difference(newStormProperties, updatedProperties).areEqual());
    }

    @Test
    public void testStormConfigsWithKerberos() throws Exception {
        Map<String, String> stormProperties = new HashMap<String, String>() {
            {
                put("_storm.thrift.nonsecure.transport", "org.apache.storm.security.auth.SimpleTransportPlugin");
                put("_storm.thrift.secure.transport", "org.apache.storm.security.auth.kerberos.KerberosSaslTransportPlugin");
                put("storm.thrift.transport", "{{storm_thrift_transport}}");
                put("storm.zookeeper.port", "2181");
            }
        };
        Map<String, String> newStormProperties = new HashMap<String, String>() {
            {
                put("storm.thrift.transport", "org.apache.storm.security.auth.kerberos.KerberosSaslTransportPlugin");
                put("storm.zookeeper.port", "2181");
            }
        };
        EasyMockSupport easyMockSupport = new EasyMockSupport();
        Clusters clusters = easyMockSupport.createNiceMock(Clusters.class);
        final Cluster cluster = easyMockSupport.createNiceMock(Cluster.class);
        Config mockStormSite = easyMockSupport.createNiceMock(Config.class);
        expect(clusters.getClusters()).andReturn(new HashMap<String, Cluster>() {
            {
                put("normal", cluster);
            }
        }).once();
        expect(cluster.getDesiredConfigByType("storm-site")).andReturn(mockStormSite).atLeastOnce();
        expect(cluster.getSecurityType()).andReturn(KERBEROS).anyTimes();
        expect(mockStormSite.getProperties()).andReturn(stormProperties).anyTimes();
        Injector injector = easyMockSupport.createNiceMock(Injector.class);
        replay(injector, clusters, mockStormSite, cluster);
        AmbariManagementControllerImpl controller = createMockBuilder(AmbariManagementControllerImpl.class).addMockedMethod("getClusters", new Class[]{  }).addMockedMethod("createConfig").withConstructor(createNiceMock(ActionManager.class), clusters, injector).createNiceMock();
        Injector injector2 = easyMockSupport.createNiceMock(Injector.class);
        Capture<Map> propertiesCapture = EasyMock.newCapture();
        expect(injector2.getInstance(AmbariManagementController.class)).andReturn(controller).anyTimes();
        expect(controller.getClusters()).andReturn(clusters).anyTimes();
        expect(controller.createConfig(anyObject(Cluster.class), anyObject(StackId.class), anyString(), capture(propertiesCapture), anyString(), anyObject(Map.class))).andReturn(createNiceMock(Config.class)).once();
        replay(controller, injector2);
        updateStormConfigs();
        easyMockSupport.verifyAll();
        Map<String, String> updatedProperties = propertiesCapture.getValue();
        Assert.assertTrue(Maps.difference(newStormProperties, updatedProperties).areEqual());
    }

    @Test
    public void testClearHadoopMetrics2Content() throws Exception {
        Map<String, String> oldContentProperty = new HashMap<String, String>() {
            {
                put("content", "# Licensed to the Apache Software Foundation (ASF) under one or more...");
            }
        };
        Map<String, String> newContentProperty = new HashMap<String, String>() {
            {
                put("content", "");
            }
        };
        EasyMockSupport easyMockSupport = new EasyMockSupport();
        Clusters clusters = easyMockSupport.createNiceMock(Clusters.class);
        final Cluster cluster = easyMockSupport.createNiceMock(Cluster.class);
        Config mockHadoopMetrics2Properties = easyMockSupport.createNiceMock(Config.class);
        expect(clusters.getClusters()).andReturn(new HashMap<String, Cluster>() {
            {
                put("normal", cluster);
            }
        }).once();
        expect(cluster.getDesiredConfigByType("hadoop-metrics2.properties")).andReturn(mockHadoopMetrics2Properties).atLeastOnce();
        expect(mockHadoopMetrics2Properties.getProperties()).andReturn(oldContentProperty).anyTimes();
        Injector injector = easyMockSupport.createNiceMock(Injector.class);
        replay(injector, clusters, mockHadoopMetrics2Properties, cluster);
        AmbariManagementControllerImpl controller = createMockBuilder(AmbariManagementControllerImpl.class).addMockedMethod("getClusters", new Class[]{  }).addMockedMethod("createConfig").withConstructor(createNiceMock(ActionManager.class), clusters, injector).createNiceMock();
        Injector injector2 = easyMockSupport.createNiceMock(Injector.class);
        Capture<Map> propertiesCapture = EasyMock.newCapture();
        expect(injector2.getInstance(AmbariManagementController.class)).andReturn(controller).anyTimes();
        expect(controller.getClusters()).andReturn(clusters).anyTimes();
        expect(controller.createConfig(anyObject(Cluster.class), anyObject(StackId.class), anyString(), capture(propertiesCapture), anyString(), anyObject(Map.class))).andReturn(createNiceMock(Config.class)).once();
        replay(controller, injector2);
        clearHadoopMetrics2Content();
        easyMockSupport.verifyAll();
        Map<String, String> updatedProperties = propertiesCapture.getValue();
        Assert.assertTrue(Maps.difference(newContentProperty, updatedProperties).areEqual());
    }
}

