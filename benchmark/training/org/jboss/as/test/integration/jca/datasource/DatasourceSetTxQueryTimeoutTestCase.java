/**
 * Copyright 2018 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.test.integration.jca.datasource;


import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.annotation.Resource;
import javax.sql.DataSource;
import javax.transaction.UserTransaction;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.test.integration.jca.JcaMgmtBase;
import org.jboss.as.test.integration.jca.JcaMgmtServerSetupTask;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Verifies that query timeout is set to the remaining transaction timeout, when the set-tx-query-timeout attribute
 * is enabled in the data source.
 *
 * https://issues.jboss.org/browse/JBEAP-13301
 *
 * @author <a href="mailto:thofman@redhat.com>Tomas Hofman</a>
 */
@RunWith(Arquillian.class)
@ServerSetup(DatasourceSetTxQueryTimeoutTestCase.DatasourceServerSetupTask.class)
public class DatasourceSetTxQueryTimeoutTestCase extends JcaMgmtBase {
    private static final String TX_DS_NAME = "JTADS";

    static class DatasourceServerSetupTask extends JcaMgmtServerSetupTask {
        @Override
        protected void doSetup(ManagementClient managementClient) throws Exception {
            setupDs(managementClient, DatasourceSetTxQueryTimeoutTestCase.TX_DS_NAME, true);
            reload();
        }

        private void setupDs(ManagementClient managementClient, String dsName, boolean jta) throws Exception {
            Datasource ds = Datasource.Builder(dsName).build();
            ModelNode address = new ModelNode();
            address.add("subsystem", "datasources");
            address.add("data-source", dsName);
            ModelNode operation = new ModelNode();
            operation.get(OP).set(ADD);
            operation.get(OP_ADDR).set(address);
            operation.get("jndi-name").set(ds.getJndiName());
            operation.get("use-java-context").set("true");
            operation.get("driver-name").set(ds.getDriverName());
            operation.get("enabled").set("true");
            operation.get("user-name").set(ds.getUserName());
            operation.get("password").set(ds.getPassword());
            operation.get("jta").set(jta);
            operation.get("use-ccm").set("true");
            operation.get("connection-url").set(ds.getConnectionUrl());
            operation.get("set-tx-query-timeout").set("true");
            managementClient.getControllerClient().execute(operation);
        }
    }

    @Resource(mappedName = "java:jboss/datasources/" + (DatasourceSetTxQueryTimeoutTestCase.TX_DS_NAME))
    private DataSource txDS;

    @Resource(mappedName = "java:jboss/UserTransaction")
    private UserTransaction transaction;

    @Test
    public void testJTADS() throws Exception {
        transaction.begin();
        try (Connection connection = txDS.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select 1")) {
                Assert.assertEquals(0, statement.getQueryTimeout());
                statement.execute();
                int queryTimeout = statement.getQueryTimeout();
                // during statement execution the query timeout was supposed to be set to default transaction timeout,
                // which is set to 300 seconds, but lets give it some allowance
                Assert.assertTrue(((queryTimeout > 290) && (queryTimeout <= 300)));
            }
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }
}

