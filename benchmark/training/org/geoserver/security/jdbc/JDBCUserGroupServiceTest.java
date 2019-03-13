/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.jdbc;


import java.io.IOException;
import java.util.logging.Logger;
import org.geoserver.security.impl.AbstractUserGroupServiceTest;
import org.geotools.util.logging.Logging;
import org.junit.Assert;
import org.junit.Test;


public abstract class JDBCUserGroupServiceTest extends AbstractUserGroupServiceTest {
    static Logger LOGGER = Logging.getLogger("org.geoserver.security.jdbc");

    @Test
    public void testUserGroupDatabaseSetup() throws IOException {
        JDBCUserGroupStore jdbcStore = ((JDBCUserGroupStore) (store));
        Assert.assertTrue(jdbcStore.tablesAlreadyCreated());
        jdbcStore.checkDDLStatements();
        jdbcStore.checkDMLStatements();
        jdbcStore.clear();
        jdbcStore.dropTables();
        jdbcStore.store();
        Assert.assertFalse(jdbcStore.tablesAlreadyCreated());
        jdbcStore.load();
    }
}

