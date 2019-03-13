/**
 * Copyright 2017 Red Hat, Inc.
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
package org.jboss.as.test.integration.security.credentialreference;


import ModelDescriptionConstants.SUBSYSTEM;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Objects;
import org.h2.tools.Server;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.test.integration.management.util.MgmtOperationException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * tests for credential-reference in datasource subsystem
 */
@RunWith(Arquillian.class)
@ServerSetup({ CredentialStoreServerSetupTask.class, CredentialReferenceDatasourceTestCase.DatasourceServerSetupTask.class })
@RunAsClient
public class CredentialReferenceDatasourceTestCase {
    private static final String DATABASE_PASSWORD = "chucknorris";

    private static final PathAddress DATASOURCES_SUBSYSTEM_ADDRESS = PathAddress.pathAddress(SUBSYSTEM, "datasources");

    private static final String CLEAR_TEXT_CREDENTIAL_REF_DS_NAME = "ClearTextCredentialReferenceDatasource";

    private static final String STORE_ALIAS_CREDENTIAL_REF_DS_NAME = "StoreAliasCredentialReferenceDatasource";

    private static final String PASSWORD_AND_CREDENTIAL_REF_DS_NAME = "PasswordAndClearTextCredentialReferenceDatasource";

    enum Scenario {

        // <credential-reference clear-text="chucknorris"/>
        CREDENTIAL_REFERENCE_CLEAR_TEXT(CredentialReferenceDatasourceTestCase.CLEAR_TEXT_CREDENTIAL_REF_DS_NAME),
        // <credential-reference store="store001" alias="alias001"/>
        CREDENTIAL_REFERENCE_STORE_ALIAS(CredentialReferenceDatasourceTestCase.STORE_ALIAS_CREDENTIAL_REF_DS_NAME),
        // password and credential-reference should be mutually exclusive
        // test that it is not possible to create datasource with both defined
        PASSWORD_AND_CREDENTIAL_REFERENCE_PREFERENCE(CredentialReferenceDatasourceTestCase.PASSWORD_AND_CREDENTIAL_REF_DS_NAME);
        private final String datasourceName;

        Scenario(String datasourceName) {
            Objects.requireNonNull(datasourceName);
            this.datasourceName = datasourceName;
        }

        private PathAddress getDatasourceAddress() {
            return CredentialReferenceDatasourceTestCase.DATASOURCES_SUBSYSTEM_ADDRESS.append("data-source", datasourceName);
        }

        private PathAddress getXADatasourceAddress() {
            return CredentialReferenceDatasourceTestCase.DATASOURCES_SUBSYSTEM_ADDRESS.append("xa-data-source", datasourceName);
        }

        public String getDatasourceJndiName() {
            return "java:jboss/datasources/" + (datasourceName);
        }
    }

    @ArquillianResource
    private ManagementClient client;

    @Test
    public void testDatasourceClearTextCredentialReference() throws IOException, MgmtOperationException {
        final CredentialReferenceDatasourceTestCase.Scenario scenario = CredentialReferenceDatasourceTestCase.Scenario.CREDENTIAL_REFERENCE_CLEAR_TEXT;
        addDatasource(scenario);
        try {
            testConnectionInPool(scenario.getDatasourceAddress());
        } finally {
            removeDatasourceSilently(scenario);
        }
    }

    @Test
    public void testDatasourceStoreAliasCredentialReference() throws IOException, MgmtOperationException {
        final CredentialReferenceDatasourceTestCase.Scenario scenario = CredentialReferenceDatasourceTestCase.Scenario.CREDENTIAL_REFERENCE_STORE_ALIAS;
        addDatasource(scenario);
        try {
            testConnectionInPool(scenario.getDatasourceAddress());
        } finally {
            removeDatasourceSilently(scenario);
        }
    }

    @Test
    public void testDatasourceCredentialReferenceOverPasswordPreference() throws IOException {
        final CredentialReferenceDatasourceTestCase.Scenario scenario = CredentialReferenceDatasourceTestCase.Scenario.PASSWORD_AND_CREDENTIAL_REFERENCE_PREFERENCE;
        try {
            addDatasource(scenario);
            Assert.fail("It shouldn't be possible to add datasource with both, credential-reference and password, defined");
        } catch (MgmtOperationException moe) {
            // expected
        } finally {
            removeDatasourceSilently(scenario);
        }
    }

    @Test
    public void testXADatasourceClearTextCredentialReference() throws IOException, MgmtOperationException {
        final CredentialReferenceDatasourceTestCase.Scenario scenario = CredentialReferenceDatasourceTestCase.Scenario.CREDENTIAL_REFERENCE_CLEAR_TEXT;
        addXADatasource(scenario);
        try {
            testConnectionInPool(scenario.getXADatasourceAddress());
        } finally {
            removeXADatasourceSilently(scenario);
        }
    }

    @Test
    public void testXADatasourceStoreAliasCredentialReference() throws IOException, MgmtOperationException {
        final CredentialReferenceDatasourceTestCase.Scenario scenario = CredentialReferenceDatasourceTestCase.Scenario.CREDENTIAL_REFERENCE_STORE_ALIAS;
        addXADatasource(scenario);
        try {
            testConnectionInPool(scenario.getXADatasourceAddress());
        } finally {
            removeXADatasourceSilently(scenario);
        }
    }

    @Test
    public void testXADatasourceCredentialReferenceOverPasswordPreference() throws IOException {
        final CredentialReferenceDatasourceTestCase.Scenario scenario = CredentialReferenceDatasourceTestCase.Scenario.PASSWORD_AND_CREDENTIAL_REFERENCE_PREFERENCE;
        try {
            addXADatasource(scenario);
            Assert.fail("It shouldn't be possible to add datasource with both, credential-reference and password, defined");
        } catch (MgmtOperationException moe) {
            // expected
        } finally {
            removeXADatasourceSilently(scenario);
        }
    }

    public static class DatasourceServerSetupTask implements ServerSetupTask {
        private Server h2Server;

        private Connection connection;

        @Override
        public void setup(ManagementClient managementClient, String s) throws Exception {
            h2Server = Server.createTcpServer("-tcpAllowOthers").start();
            // open connection to database, because that's only (easy) way to set password for user sa
            connection = DriverManager.getConnection((("jdbc:h2:mem:" + (CredentialReferenceDatasourceTestCase.class.getName())) + ";DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE"), "sa", CredentialReferenceDatasourceTestCase.DATABASE_PASSWORD);
        }

        @Override
        public void tearDown(ManagementClient managementClient, String s) throws Exception {
            connection.close();
            h2Server.shutdown();
        }
    }
}

