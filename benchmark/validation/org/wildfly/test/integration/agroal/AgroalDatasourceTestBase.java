/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
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
package org.wildfly.test.integration.agroal;


import org.jboss.as.test.integration.management.base.ContainerResourceMgmtTestBase;
import org.jboss.logging.Logger;
import org.junit.Test;


/**
 * A basic testing of getting a connection from an agroal datasource.
 *
 * @author <a href="mailto:lbarreiro@redhat.com">Luis Barreiro</a>
 */
public abstract class AgroalDatasourceTestBase extends ContainerResourceMgmtTestBase {
    private static final Logger log = Logger.getLogger(AgroalDatasourceTestBase.class);

    protected static final String AGROAL_EXTENTION = "org.wildfly.extension.datasources-agroal";

    protected static final String DATASOURCES_SUBSYSTEM = "datasources-agroal";

    // --- //
    @Test
    public void addDatasource() throws Exception {
        Datasource ds = Datasource.Builder("testDatasourceEnabled").build();
        try {
            createDriver(ds);
            createDataSource(ds);
            testConnection(ds);
        } finally {
            removeDataSourceSilently(ds);
            removeDriverSilently(ds);
        }
    }

    @Test
    public void allBySystemProperty() throws Exception {
        String url = "myds.url";
        String username = "myds.username";
        String password = "myds.password";
        String jndiName = "myds.jndi";
        Datasource ds = Datasource.Builder("testAllBySystem").connectionUrl(AgroalDatasourceTestBase.wrapProp(url)).userName(AgroalDatasourceTestBase.wrapProp(username)).password(AgroalDatasourceTestBase.wrapProp(password)).jndiName(AgroalDatasourceTestBase.wrapProp(jndiName)).driverName("h2_ref").build();
        try {
            Datasource defaultPropertyDs = Datasource.Builder("temporary").build();
            addSystemProperty(url, defaultPropertyDs.getConnectionUrl());
            addSystemProperty(username, defaultPropertyDs.getUserName());
            addSystemProperty(password, defaultPropertyDs.getPassword());
            addSystemProperty(jndiName, defaultPropertyDs.getJndiName());
            createDriver(ds);
            createDataSource(ds);
            testConnection(ds);
        } finally {
            removeDataSourceSilently(ds);
            removeDriverSilently(ds);
            removeSystemPropertySilently(url);
            removeSystemPropertySilently(username);
            removeSystemPropertySilently(password);
            removeSystemPropertySilently(jndiName);
        }
    }
}

