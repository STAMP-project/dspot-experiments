/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.manualmode.web.ssl;


import SecurityTestConstants.KEYSTORE_PASSWORD;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.h2.tools.Server;
import org.jboss.arquillian.container.test.api.ContainerController;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.test.integration.security.common.AbstractDataSourceServerSetupTask;
import org.jboss.as.test.integration.security.common.AbstractSecurityDomainsServerSetupTask;
import org.jboss.as.test.integration.security.common.Utils;
import org.jboss.as.test.integration.security.common.config.DataSource;
import org.jboss.as.test.integration.security.common.config.JSSE;
import org.jboss.as.test.integration.security.common.config.SecureStore;
import org.jboss.as.test.integration.security.common.config.SecurityDomain;
import org.jboss.as.test.integration.security.common.config.SecurityModule;
import org.jboss.as.test.integration.security.common.servlets.SimpleSecuredServlet;
import org.jboss.as.test.shared.ServerSnapshot;
import org.jboss.as.test.shared.TestSuiteEnvironment;
import org.jboss.logging.Logger;
import org.jboss.security.auth.spi.DatabaseCertLoginModule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.jboss.as.test.manualmode.web.ssl.AbstractCertificateLoginModuleTestCase.HTTPSConnectorSetup.INSTANCE;


/**
 * Tests for {@link DatabaseCertLoginModule} which uses truststore with trusted
 * certificates for authentication of users and database with users roles for
 * authorization.
 *
 * @author Filip Bogyai
 */
@RunWith(Arquillian.class)
@RunAsClient
public class DatabaseCertLoginModuleTestCase extends AbstractCertificateLoginModuleTestCase {
    private static Logger LOGGER = Logger.getLogger(DatabaseCertLoginModuleTestCase.class);

    private static final String APP_NAME = "database_cert";

    private static final String SECURITY_DOMAIN_CERT = "database_cert_domain";

    private static final String SECURITY_DOMAIN_JSSE = "jsse_truststore_domain";

    private static final String DATASOURCE_NAME = "UsersRolesDB";

    @ArquillianResource
    private static ContainerController containerController;

    @ArquillianResource
    private Deployer deployer;

    private static ManagementClient managementClient;

    private static AutoCloseable snapshot;

    @Test
    @InSequence(-1)
    public void startAndSetupContainer() throws Exception {
        DatabaseCertLoginModuleTestCase.LOGGER.trace("*** starting server");
        DatabaseCertLoginModuleTestCase.containerController.start(AbstractCertificateLoginModuleTestCase.CONTAINER);
        ModelControllerClient client = TestSuiteEnvironment.getModelControllerClient();
        DatabaseCertLoginModuleTestCase.managementClient = new ManagementClient(client, TestSuiteEnvironment.getServerAddress(), TestSuiteEnvironment.getServerPort(), "remote+http");
        DatabaseCertLoginModuleTestCase.snapshot = ServerSnapshot.takeSnapshot(DatabaseCertLoginModuleTestCase.managementClient);
        DatabaseCertLoginModuleTestCase.LOGGER.trace("*** will configure server now");
        INSTANCE.setup(DatabaseCertLoginModuleTestCase.managementClient, AbstractCertificateLoginModuleTestCase.CONTAINER);
        DatabaseCertLoginModuleTestCase.DataSourcesSetup.INSTANCE.setup(DatabaseCertLoginModuleTestCase.managementClient, AbstractCertificateLoginModuleTestCase.CONTAINER);
        DatabaseCertLoginModuleTestCase.DBSetup.INSTANCE.setup(DatabaseCertLoginModuleTestCase.managementClient, AbstractCertificateLoginModuleTestCase.CONTAINER);
        DatabaseCertLoginModuleTestCase.SecurityDomainsSetup.INSTANCE.setup(DatabaseCertLoginModuleTestCase.managementClient, AbstractCertificateLoginModuleTestCase.CONTAINER);
        DatabaseCertLoginModuleTestCase.LOGGER.trace("*** reloading server");
        executeReloadAndWaitForCompletion(client, 100000);
        deployer.deploy(DatabaseCertLoginModuleTestCase.APP_NAME);
    }

    /**
     * Test authentication against application which uses security domain with
     * configured {@link DatabaseCertLoginModule}.
     */
    @Test
    @InSequence(1)
    public void testDatabaseCertLoginModule() throws Exception {
        testLoginWithCertificate(DatabaseCertLoginModuleTestCase.APP_NAME);
    }

    @Test
    @InSequence(3)
    public void stopContainer() throws Exception {
        deployer.undeploy(DatabaseCertLoginModuleTestCase.APP_NAME);
        DatabaseCertLoginModuleTestCase.LOGGER.trace("*** reseting test configuration");
        AbstractCertificateLoginModuleTestCase.deleteWorkDir();
        DatabaseCertLoginModuleTestCase.snapshot.close();
        DatabaseCertLoginModuleTestCase.managementClient.close();
        DatabaseCertLoginModuleTestCase.LOGGER.trace("*** stopping container");
        DatabaseCertLoginModuleTestCase.containerController.stop(AbstractCertificateLoginModuleTestCase.CONTAINER);
    }

    // Embedded classes ------------------------------------------------------
    /**
     * A {@link ServerSetupTask} instance which creates security domains for
     * this test case.
     *
     * @author Filip Bogyai
     */
    static class SecurityDomainsSetup extends AbstractSecurityDomainsServerSetupTask {
        private static final DatabaseCertLoginModuleTestCase.SecurityDomainsSetup INSTANCE = new DatabaseCertLoginModuleTestCase.SecurityDomainsSetup();

        /**
         * Returns SecurityDomains configuration for this testcase.
         *
         * @see org.jboss.as.test.integration.security.common.AbstractSecurityDomainsServerSetupTask#getSecurityDomains()
         */
        @Override
        protected SecurityDomain[] getSecurityDomains() throws Exception {
            final SecurityDomain sd = new SecurityDomain.Builder().name(DatabaseCertLoginModuleTestCase.SECURITY_DOMAIN_CERT).loginModules(new SecurityModule.Builder().name(DatabaseCertLoginModule.class.getName()).putOption("securityDomain", DatabaseCertLoginModuleTestCase.SECURITY_DOMAIN_JSSE).putOption("password-stacking", "useFirstPass").putOption("dsJndiName", ("java:jboss/datasources/" + (DatabaseCertLoginModuleTestCase.DATASOURCE_NAME))).putOption("rolesQuery", "select Role, RoleGroup from Roles where PrincipalID=?").build()).build();
            final SecurityDomain sdJsse = // 
            new SecurityDomain.Builder().name(DatabaseCertLoginModuleTestCase.SECURITY_DOMAIN_JSSE).jsse(// 
            new JSSE.Builder().trustStore(new SecureStore.Builder().type("JKS").url(AbstractCertificateLoginModuleTestCase.SERVER_TRUSTSTORE_FILE.toURI().toURL()).password(KEYSTORE_PASSWORD).build()).build()).build();
            return new SecurityDomain[]{ sdJsse, sd };
        }
    }

    /**
     * Datasource setup task for H2 DB.
     */
    static class DataSourcesSetup extends AbstractDataSourceServerSetupTask {
        private static final DatabaseCertLoginModuleTestCase.DataSourcesSetup INSTANCE = new DatabaseCertLoginModuleTestCase.DataSourcesSetup();

        @Override
        protected DataSource[] getDataSourceConfigurations(ManagementClient managementClient, String containerId) {
            return new DataSource[]{ new DataSource.Builder().name(DatabaseCertLoginModuleTestCase.DATASOURCE_NAME).connectionUrl(((("jdbc:h2:tcp://" + (Utils.getSecondaryTestAddress(managementClient))) + "/mem:") + (DatabaseCertLoginModuleTestCase.DATASOURCE_NAME))).driver("h2").username("sa").password("sa").build() };
        }
    }

    /**
     * H2 DB configuration setup task.
     */
    static class DBSetup implements ServerSetupTask {
        private static final DatabaseCertLoginModuleTestCase.DBSetup INSTANCE = new DatabaseCertLoginModuleTestCase.DBSetup();

        private Server server;

        public void setup(ManagementClient managementClient, String containerId) throws Exception {
            server = Server.createTcpServer("-tcpAllowOthers").start();
            final String dbUrl = ("jdbc:h2:mem:" + (DatabaseCertLoginModuleTestCase.DATASOURCE_NAME)) + ";DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
            DatabaseCertLoginModuleTestCase.LOGGER.trace(("Creating database " + dbUrl));
            final Connection conn = DriverManager.getConnection(dbUrl, "sa", "sa");
            executeUpdate(conn, "CREATE TABLE Roles(PrincipalID Varchar(50), Role Varchar(50), RoleGroup Varchar(50))");
            executeUpdate(conn, (("INSERT INTO Roles VALUES ('CN=client','" + (SimpleSecuredServlet.ALLOWED_ROLE)) + "','Roles')"));
            executeUpdate(conn, "INSERT INTO Roles VALUES ('CN=untrusted','testRole','Roles')");
            conn.close();
        }

        public void tearDown(ManagementClient managementClient, String containerId) throws Exception {
            server.shutdown();
            server = null;
        }

        private void executeUpdate(Connection connection, String query) throws SQLException {
            final Statement statement = connection.createStatement();
            final int updateResult = statement.executeUpdate(query);
            DatabaseCertLoginModuleTestCase.LOGGER.trace(((("Result: " + updateResult) + ".  SQL statement: ") + query));
            statement.close();
        }
    }
}

