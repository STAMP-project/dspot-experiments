/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.internal.jdbc2;


import GridResourceIoc.AnnotationSet.GENERIC;
import IgniteComponentType.SPRING;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.Callable;
import javax.sql.DataSource;
import org.apache.ignite.IgniteJdbcDriver;
import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.internal.IgniteKernal;
import org.apache.ignite.internal.util.spring.IgniteSpringHelper;
import org.apache.ignite.resources.SpringApplicationContextResource;
import org.apache.ignite.testframework.GridTestUtils;
import org.junit.Test;


/**
 * Test of cluster and JDBC driver with config that contains cache with POJO store and datasource bean.
 */
public class JdbcSpringSelfTest extends JdbcConnectionSelfTest {
    /**
     * Grid count.
     */
    private static final int GRID_CNT = 2;

    /**
     * {@inheritDoc }
     */
    @Test
    @Override
    public void testClientNodeId() throws Exception {
        IgniteEx client = ((IgniteEx) (startGridWithSpringCtx(getTestIgniteInstanceName(), true, configURL())));
        UUID clientId = client.localNode().id();
        final String url = ((((IgniteJdbcDriver.CFG_URL_PREFIX) + "nodeId=") + clientId) + '@') + (configURL());
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                try (Connection conn = DriverManager.getConnection(url)) {
                    return conn;
                }
            }
        }, SQLException.class, ("Failed to establish connection with node (is it a server node?): " + clientId));
    }

    /**
     * Special class to test Spring context injection.
     */
    private static class TestInjectTarget {
        /**
         *
         */
        @SpringApplicationContextResource
        private Object appCtx;
    }

    /**
     * Test that we have valid Spring context and also could create beans from it.
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testSpringBean() throws Exception {
        String url = (IgniteJdbcDriver.CFG_URL_PREFIX) + (configURL());
        // Create connection.
        try (Connection conn = DriverManager.getConnection(url)) {
            assertNotNull(conn);
            JdbcSpringSelfTest.TestInjectTarget target = new JdbcSpringSelfTest.TestInjectTarget();
            IgniteKernal kernal = ((IgniteKernal) (ignite()));
            // Inject Spring context to test object.
            kernal.context().resource().inject(target, GENERIC);
            assertNotNull(target.appCtx);
            IgniteSpringHelper spring = SPRING.create(false);
            // Load bean by name.
            DataSource ds = spring.loadBeanFromAppContext(target.appCtx, "dsTest");
            assertNotNull(ds);
        }
    }
}

