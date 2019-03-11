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
package org.apache.hadoop.hive.metastore;


import MetricsConstants.CREATE_TOTAL_DATABASES;
import MetricsConstants.CREATE_TOTAL_PARTITIONS;
import MetricsConstants.CREATE_TOTAL_TABLES;
import MetricsConstants.DELETE_TOTAL_DATABASES;
import MetricsConstants.DELETE_TOTAL_PARTITIONS;
import MetricsConstants.DELETE_TOTAL_TABLES;
import MetricsConstants.OPEN_CONNECTIONS;
import MetricsConstants.TOTAL_DATABASES;
import MetricsConstants.TOTAL_PARTITIONS;
import MetricsConstants.TOTAL_TABLES;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.metrics.Metrics;
import org.apache.hadoop.hive.ql.IDriver;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests Hive Metastore Metrics.
 */
public class TestMetaStoreMetrics {
    private static HiveConf hiveConf;

    private static IDriver driver;

    @Test
    public void testMethodCounts() throws Exception {
        TestMetaStoreMetrics.driver.run("show databases");
        // one call by init, one called here.
        Assert.assertEquals(2, Metrics.getRegistry().getTimers().get("api_get_databases").getCount());
    }

    @Test
    public void testMetaDataCounts() throws Exception {
        int initDbCount = ((Integer) (Metrics.getRegistry().getGauges().get(TOTAL_DATABASES).getValue()));
        int initTblCount = ((Integer) (Metrics.getRegistry().getGauges().get(TOTAL_TABLES).getValue()));
        int initPartCount = ((Integer) (Metrics.getRegistry().getGauges().get(TOTAL_PARTITIONS).getValue()));
        // 1 databases created
        TestMetaStoreMetrics.driver.run("create database testdb1");
        // 4 tables
        TestMetaStoreMetrics.driver.run("create table testtbl1 (key string)");
        TestMetaStoreMetrics.driver.run("create table testtblpart (key string) partitioned by (partkey string)");
        TestMetaStoreMetrics.driver.run("use testdb1");
        TestMetaStoreMetrics.driver.run("create table testtbl2 (key string)");
        TestMetaStoreMetrics.driver.run("create table testtblpart2 (key string) partitioned by (partkey string)");
        // 6 partitions
        TestMetaStoreMetrics.driver.run("alter table default.testtblpart add partition (partkey='a')");
        TestMetaStoreMetrics.driver.run("alter table default.testtblpart add partition (partkey='b')");
        TestMetaStoreMetrics.driver.run("alter table default.testtblpart add partition (partkey='c')");
        TestMetaStoreMetrics.driver.run("alter table testdb1.testtblpart2 add partition (partkey='a')");
        TestMetaStoreMetrics.driver.run("alter table testdb1.testtblpart2 add partition (partkey='b')");
        TestMetaStoreMetrics.driver.run("alter table testdb1.testtblpart2 add partition (partkey='c')");
        // create and drop some additional metadata, to test drop counts.
        TestMetaStoreMetrics.driver.run("create database tempdb");
        TestMetaStoreMetrics.driver.run("use tempdb");
        TestMetaStoreMetrics.driver.run("create table delete_by_table (key string) partitioned by (partkey string)");
        TestMetaStoreMetrics.driver.run("alter table delete_by_table add partition (partkey='temp')");
        TestMetaStoreMetrics.driver.run("drop table delete_by_table");
        TestMetaStoreMetrics.driver.run("create table delete_by_part (key string) partitioned by (partkey string)");
        TestMetaStoreMetrics.driver.run("alter table delete_by_part add partition (partkey='temp')");
        TestMetaStoreMetrics.driver.run("alter table delete_by_part drop partition (partkey='temp')");
        TestMetaStoreMetrics.driver.run("create table delete_by_db (key string) partitioned by (partkey string)");
        TestMetaStoreMetrics.driver.run("alter table delete_by_db add partition (partkey='temp')");
        TestMetaStoreMetrics.driver.run("use default");
        TestMetaStoreMetrics.driver.run("drop database tempdb cascade");
        Assert.assertEquals(2, Metrics.getRegistry().getCounters().get(CREATE_TOTAL_DATABASES).getCount());
        Assert.assertEquals(7, Metrics.getRegistry().getCounters().get(CREATE_TOTAL_TABLES).getCount());
        Assert.assertEquals(9, Metrics.getRegistry().getCounters().get(CREATE_TOTAL_PARTITIONS).getCount());
        Assert.assertEquals(1, Metrics.getRegistry().getCounters().get(DELETE_TOTAL_DATABASES).getCount());
        Assert.assertEquals(3, Metrics.getRegistry().getCounters().get(DELETE_TOTAL_TABLES).getCount());
        Assert.assertEquals(3, Metrics.getRegistry().getCounters().get(DELETE_TOTAL_PARTITIONS).getCount());
        // to test initial metadata count metrics.
        Assert.assertEquals((initDbCount + 1), Metrics.getRegistry().getGauges().get(TOTAL_DATABASES).getValue());
        Assert.assertEquals((initTblCount + 4), Metrics.getRegistry().getGauges().get(TOTAL_TABLES).getValue());
        Assert.assertEquals((initPartCount + 6), Metrics.getRegistry().getGauges().get(TOTAL_PARTITIONS).getValue());
    }

    @Test
    public void testConnections() throws Exception {
        Thread.sleep(2000);// TODO Evil!  Need to figure out a way to remove this sleep.

        // initial state is one connection
        int initialCount = ((Integer) (Metrics.getRegistry().getGauges().get(OPEN_CONNECTIONS).getValue()));
        // create two connections
        HiveMetaStoreClient msc = new HiveMetaStoreClient(TestMetaStoreMetrics.hiveConf);
        HiveMetaStoreClient msc2 = new HiveMetaStoreClient(TestMetaStoreMetrics.hiveConf);
        Assert.assertEquals((initialCount + 2), Metrics.getRegistry().getGauges().get(OPEN_CONNECTIONS).getValue());
        // close one connection, verify still two left
        msc.close();
        Thread.sleep(2000);// TODO Evil!  Need to figure out a way to remove this sleep.

        Assert.assertEquals((initialCount + 1), Metrics.getRegistry().getGauges().get(OPEN_CONNECTIONS).getValue());
        // close one connection, verify still one left
        msc2.close();
        Thread.sleep(2000);// TODO Evil!  Need to figure out a way to remove this sleep.

        Assert.assertEquals(initialCount, Metrics.getRegistry().getGauges().get(OPEN_CONNECTIONS).getValue());
    }
}

