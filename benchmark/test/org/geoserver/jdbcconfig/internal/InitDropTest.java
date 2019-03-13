/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.jdbcconfig.internal;


import org.geoserver.jdbcconfig.JDBCConfigTestSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 *
 *
 * @author Ian Schneider <ischneider@boundlessgeo.com>
 */
@RunWith(Parameterized.class)
public class InitDropTest {
    JDBCConfigTestSupport.DBConfig dbConfig;

    JDBCConfigTestSupport testSupport;

    public InitDropTest(JDBCConfigTestSupport.DBConfig dbConfig) {
        this.dbConfig = dbConfig;
        testSupport = new JDBCConfigTestSupport(dbConfig);
    }

    @Test
    public void testInitDrop() throws Exception {
        testSupport.setUp();// clear state

        assertScript(dbConfig.getDropScript());
        assertScript(dbConfig.getInitScript());
    }
}

