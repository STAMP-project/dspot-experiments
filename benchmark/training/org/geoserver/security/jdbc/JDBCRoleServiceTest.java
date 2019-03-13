/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.jdbc;


import java.io.IOException;
import java.util.logging.Logger;
import org.geoserver.security.impl.AbstractRoleServiceTest;
import org.geotools.util.logging.Logging;
import org.junit.Assert;
import org.junit.Test;


public abstract class JDBCRoleServiceTest extends AbstractRoleServiceTest {
    static Logger LOGGER = Logging.getLogger("org.geoserver.security.jdbc");

    @Test
    public void testRoleDatabaseSetup() {
        try {
            JDBCRoleStore jdbcStore = ((JDBCRoleStore) (store));
            Assert.assertTrue(jdbcStore.tablesAlreadyCreated());
            jdbcStore.checkDDLStatements();
            jdbcStore.checkDMLStatements();
            jdbcStore.clear();
            jdbcStore.dropTables();
            jdbcStore.store();
            Assert.assertFalse(jdbcStore.tablesAlreadyCreated());
            jdbcStore.load();
        } catch (IOException ex) {
            Assert.fail(ex.getMessage());
        }
    }
}

