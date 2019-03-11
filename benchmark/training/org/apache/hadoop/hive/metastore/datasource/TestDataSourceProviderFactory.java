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
package org.apache.hadoop.hive.metastore.datasource;


import BoneCPDataSourceProvider.BONECP;
import ConfVars.CONNECTION_POOLING_TYPE;
import DbCPDataSourceProvider.DBCP;
import HikariCPDataSourceProvider.HIKARI;
import com.jolbox.bonecp.BoneCPDataSource;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.metastore.annotation.MetastoreUnitTest;
import org.apache.hadoop.hive.metastore.conf.MetastoreConf;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static BoneCPDataSourceProvider.BONECP;
import static HikariCPDataSourceProvider.HIKARI;


@Category(MetastoreUnitTest.class)
public class TestDataSourceProviderFactory {
    private Configuration conf;

    @Test
    public void testNoDataSourceCreatedWithoutProps() throws SQLException {
        MetastoreConf.setVar(conf, CONNECTION_POOLING_TYPE, "dummy");
        DataSourceProvider dsp = DataSourceProviderFactory.tryGetDataSourceProviderOrNull(conf);
        Assert.assertNull(dsp);
    }

    @Test
    public void testCreateBoneCpDataSource() throws SQLException {
        MetastoreConf.setVar(conf, CONNECTION_POOLING_TYPE, BONECP);
        DataSourceProvider dsp = DataSourceProviderFactory.tryGetDataSourceProviderOrNull(conf);
        Assert.assertNotNull(dsp);
        DataSource ds = dsp.create(conf);
        Assert.assertTrue((ds instanceof BoneCPDataSource));
    }

    @Test
    public void testSetBoneCpStringProperty() throws SQLException {
        MetastoreConf.setVar(conf, CONNECTION_POOLING_TYPE, BONECP);
        conf.set(((BONECP) + ".initSQL"), "select 1 from dual");
        DataSourceProvider dsp = DataSourceProviderFactory.tryGetDataSourceProviderOrNull(conf);
        Assert.assertNotNull(dsp);
        DataSource ds = dsp.create(conf);
        Assert.assertTrue((ds instanceof BoneCPDataSource));
        Assert.assertEquals("select 1 from dual", getInitSQL());
    }

    @Test
    public void testSetBoneCpNumberProperty() throws SQLException {
        MetastoreConf.setVar(conf, CONNECTION_POOLING_TYPE, BONECP);
        conf.set(((BONECP) + ".acquireRetryDelayInMs"), "599");
        DataSourceProvider dsp = DataSourceProviderFactory.tryGetDataSourceProviderOrNull(conf);
        Assert.assertNotNull(dsp);
        DataSource ds = dsp.create(conf);
        Assert.assertTrue((ds instanceof BoneCPDataSource));
        Assert.assertEquals(599L, getAcquireRetryDelayInMs());
    }

    @Test
    public void testSetBoneCpBooleanProperty() throws SQLException {
        MetastoreConf.setVar(conf, CONNECTION_POOLING_TYPE, BONECP);
        conf.set(((BONECP) + ".disableJMX"), "true");
        DataSourceProvider dsp = DataSourceProviderFactory.tryGetDataSourceProviderOrNull(conf);
        Assert.assertNotNull(dsp);
        DataSource ds = dsp.create(conf);
        Assert.assertTrue((ds instanceof BoneCPDataSource));
        Assert.assertEquals(true, isDisableJMX());
    }

    @Test
    public void testCreateHikariCpDataSource() throws SQLException {
        MetastoreConf.setVar(conf, CONNECTION_POOLING_TYPE, HIKARI);
        // This is needed to prevent the HikariDataSource from trying to connect to the DB
        conf.set(((HIKARI) + ".initializationFailTimeout"), "-1");
        DataSourceProvider dsp = DataSourceProviderFactory.tryGetDataSourceProviderOrNull(conf);
        Assert.assertNotNull(dsp);
        DataSource ds = dsp.create(conf);
        Assert.assertTrue((ds instanceof HikariDataSource));
    }

    @Test
    public void testSetHikariCpStringProperty() throws SQLException {
        MetastoreConf.setVar(conf, CONNECTION_POOLING_TYPE, HIKARI);
        conf.set(((HIKARI) + ".connectionInitSql"), "select 1 from dual");
        conf.set(((HIKARI) + ".initializationFailTimeout"), "-1");
        DataSourceProvider dsp = DataSourceProviderFactory.tryGetDataSourceProviderOrNull(conf);
        Assert.assertNotNull(dsp);
        DataSource ds = dsp.create(conf);
        Assert.assertTrue((ds instanceof HikariDataSource));
        Assert.assertEquals("select 1 from dual", getConnectionInitSql());
    }

    @Test
    public void testSetHikariCpNumberProperty() throws SQLException {
        MetastoreConf.setVar(conf, CONNECTION_POOLING_TYPE, HIKARI);
        conf.set(((HIKARI) + ".idleTimeout"), "59999");
        conf.set(((HIKARI) + ".initializationFailTimeout"), "-1");
        DataSourceProvider dsp = DataSourceProviderFactory.tryGetDataSourceProviderOrNull(conf);
        Assert.assertNotNull(dsp);
        DataSource ds = dsp.create(conf);
        Assert.assertTrue((ds instanceof HikariDataSource));
        Assert.assertEquals(59999L, getIdleTimeout());
    }

    @Test
    public void testSetHikariCpBooleanProperty() throws SQLException {
        MetastoreConf.setVar(conf, CONNECTION_POOLING_TYPE, HIKARI);
        conf.set(((HIKARI) + ".allowPoolSuspension"), "false");
        conf.set(((HIKARI) + ".initializationFailTimeout"), "-1");
        DataSourceProvider dsp = DataSourceProviderFactory.tryGetDataSourceProviderOrNull(conf);
        Assert.assertNotNull(dsp);
        DataSource ds = dsp.create(conf);
        Assert.assertTrue((ds instanceof HikariDataSource));
        Assert.assertEquals(false, isAllowPoolSuspension());
    }

    @Test
    public void testCreateDbCpDataSource() throws SQLException {
        MetastoreConf.setVar(conf, CONNECTION_POOLING_TYPE, DBCP);
        DataSourceProvider dsp = DataSourceProviderFactory.tryGetDataSourceProviderOrNull(conf);
        Assert.assertNotNull(dsp);
        DataSource ds = dsp.create(conf);
        Assert.assertTrue((ds instanceof PoolingDataSource));
    }
}

