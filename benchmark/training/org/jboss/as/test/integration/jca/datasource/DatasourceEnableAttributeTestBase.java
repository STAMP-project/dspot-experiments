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
package org.jboss.as.test.integration.jca.datasource;


import org.jboss.as.test.integration.management.jca.DsMgmtTestBase;
import org.jboss.as.test.integration.management.util.MgmtOperationException;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;


/**
 * A basic testing of setting/unsetting "enable" attribute of datasource.
 *
 * @author <a href="mailto:ochaloup@redhat.com>Ondra Chaloupka</a>
 */
public abstract class DatasourceEnableAttributeTestBase extends DsMgmtTestBase {
    private static final Logger log = Logger.getLogger(DatasourceEnableAttributeTestBase.class);

    private static final String DS_ENABLED_SYSTEM_PROPERTY_NAME = "ds.enabled";

    @Test
    public void addDatasourceEnabled() throws Exception {
        Datasource ds = Datasource.Builder("testDatasourceEnabled").enabled(true).build();
        try {
            createDataSource(ds);
            testConnection(ds);
        } finally {
            removeDataSourceSilently(ds);
        }
    }

    @Test(expected = MgmtOperationException.class)
    public void addDatasourceDisabled() throws Exception {
        Datasource ds = Datasource.Builder("testDatasourceDisabled").enabled(false).build();
        try {
            createDataSource(ds);
            testConnection(ds);
        } finally {
            removeDataSourceSilently(ds);
        }
    }

    @Test
    public void enableLater() throws Exception {
        Datasource ds = Datasource.Builder("testDatasourceLater").enabled(false).build();
        try {
            createDataSource(ds);
            try {
                testConnection(ds);
                Assert.fail((("Datasource " + ds) + " is disabled. Test connection can't succeed."));
            } catch (MgmtOperationException moe) {
                // expecting that datasource won't be available 'online'
            }
            enableDatasource(ds);
            testConnection(ds);
        } finally {
            removeDataSourceSilently(ds);
        }
    }

    @Test
    public void enableBySystemProperty() throws Exception {
        Datasource ds = Datasource.Builder("testDatasourceEnableBySystem").enabled(wrapProp(DatasourceEnableAttributeTestBase.DS_ENABLED_SYSTEM_PROPERTY_NAME)).build();
        try {
            addSystemProperty(DatasourceEnableAttributeTestBase.DS_ENABLED_SYSTEM_PROPERTY_NAME, "true");
            createDataSource(ds);
            testConnection(ds);
        } finally {
            removeSystemPropertySilently(DatasourceEnableAttributeTestBase.DS_ENABLED_SYSTEM_PROPERTY_NAME);
            removeDataSourceSilently(ds);
        }
    }

    @Test(expected = MgmtOperationException.class)
    public void disableBySystemProperty() throws Exception {
        Datasource ds = Datasource.Builder("testDatasourceDisableBySystem").enabled(wrapProp(DatasourceEnableAttributeTestBase.DS_ENABLED_SYSTEM_PROPERTY_NAME)).build();
        try {
            addSystemProperty(DatasourceEnableAttributeTestBase.DS_ENABLED_SYSTEM_PROPERTY_NAME, "false");
            createDataSource(ds);
            testConnection(ds);
        } finally {
            removeSystemPropertySilently(DatasourceEnableAttributeTestBase.DS_ENABLED_SYSTEM_PROPERTY_NAME);
            removeDataSourceSilently(ds);
        }
    }

    @Test
    public void allBySystemProperty() throws Exception {
        String url = "ds.url";
        String username = "ds.username";
        String password = "ds.password";
        String jndiName = "ds.jndi";
        String driverName = "ds.drivername";
        Datasource ds = Datasource.Builder("testAllBySystem").connectionUrl(wrapProp(url)).userName(wrapProp(username)).password(wrapProp(password)).jndiName(wrapProp(jndiName)).driverName(wrapProp(driverName)).enabled(wrapProp(DatasourceEnableAttributeTestBase.DS_ENABLED_SYSTEM_PROPERTY_NAME)).build();
        try {
            Datasource defaultPropertyDs = Datasource.Builder("temporary").build();
            addSystemProperty(url, defaultPropertyDs.getConnectionUrl());
            addSystemProperty(username, defaultPropertyDs.getUserName());
            addSystemProperty(password, defaultPropertyDs.getPassword());
            addSystemProperty(jndiName, defaultPropertyDs.getJndiName());
            addSystemProperty(driverName, defaultPropertyDs.getDriverName());
            addSystemProperty(DatasourceEnableAttributeTestBase.DS_ENABLED_SYSTEM_PROPERTY_NAME, "true");
            createDataSource(ds);
            testConnection(ds);
        } finally {
            removeDataSourceSilently(ds);
            removeSystemPropertySilently(url);
            removeSystemPropertySilently(username);
            removeSystemPropertySilently(password);
            removeSystemPropertySilently(jndiName);
            removeSystemPropertySilently(driverName);
            removeSystemPropertySilently(DatasourceEnableAttributeTestBase.DS_ENABLED_SYSTEM_PROPERTY_NAME);
        }
    }
}

