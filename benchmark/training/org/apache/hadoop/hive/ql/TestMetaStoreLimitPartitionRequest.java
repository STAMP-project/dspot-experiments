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
package org.apache.hadoop.hive.ql;


import java.sql.Connection;
import java.sql.Statement;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hive.jdbc.miniHS2.MiniHS2;
import org.junit.Test;


/**
 * This unit test is for testing HIVE-13884 with more complex queries and
 * hive.metastore.limit.partition.request enabled.
 * It covers cases when the query predicates can be pushed down and the
 * number of partitions can be retrieved via directSQL.
 * It also covers cases when the number of partitions cannot be retrieved
 * via directSQL, so it falls back to ORM.
 */
public class TestMetaStoreLimitPartitionRequest {
    private static final String DB_NAME = "max_partition_test_db";

    private static final String TABLE_NAME = "max_partition_test_table";

    private static int PARTITION_REQUEST_LIMIT = 4;

    private static MiniHS2 miniHS2 = null;

    private static HiveConf conf;

    private Connection hs2Conn = null;

    private Statement stmt;

    /* Tests with queries which can be pushed down and executed with directSQL */
    @Test
    public void testSimpleQueryWithDirectSql() throws Exception {
        String queryString = "select value from %s where num='25' and ds='2008-04-09'";
        executeQuery(queryString, "value1");
    }

    @Test
    public void testMoreComplexQueryWithDirectSql() throws Exception {
        String queryString = "select value from %s where (ds between '2009-01-01' and '2009-12-31' and num='25') or (ds between '2008-01-01' and '2008-12-31' and num='30')";
        executeQuery(queryString, "value2", "value6", "value9");
    }

    /* Tests with queries which can be pushed down and executed with directSQL, but the number of
    partitions which should be fetched is bigger than the maximum set by the
    hive.metastore.limit.partition.request parameter.
     */
    @Test
    public void testSimpleQueryWithDirectSqlTooManyPartitions() throws Exception {
        String queryString = "select value from %s where ds>'2008-04-20'";
        executeQueryExceedPartitionLimit(queryString, 8);
    }

    @Test
    public void testMoreComplexQueryWithDirectSqlTooManyPartitions() throws Exception {
        String queryString = "select value from %s where num='25' or (num='30' and ds between '2008-01-01' and '2008-12-31')";
        executeQueryExceedPartitionLimit(queryString, 5);
    }

    /* Tests with queries which cannot be executed with directSQL, because of type mismatch. The type
    of the num column is string, but the parameters used in the where clause are numbers. After
    falling back to ORM, the number of partitions can be fetched by the
    ObjectStore.getNumPartitionsViaOrmFilter method.
     */
    @Test
    public void testQueryWithFallbackToORM1() throws Exception {
        String queryString = "select value from %s where num!=25 and num!=35 and num!=40";
        executeQuery(queryString, "value2", "value6", "value10");
    }

    @Test
    public void testQueryWithFallbackToORMTooManyPartitions1() throws Exception {
        String queryString = "select value from %s where num=30 or num=25";
        executeQueryExceedPartitionLimit(queryString, 6);
    }

    /* Tests with queries which cannot be executed with directSQL, because of type mismatch. The type
    of the num column is string, but the parameters used in the where clause are numbers. After
    falling back to ORM the number of partitions cannot be fetched by the
    ObjectStore.getNumPartitionsViaOrmFilter method. They are fetched by the
    ObjectStore.getPartitionNamesPrunedByExprNoTxn method.
     */
    @Test
    public void testQueryWithFallbackToORM2() throws Exception {
        String queryString = "select value from %s where num!=25 and ds='2008-04-09'";
        executeQuery(queryString, "value2", "value3", "value4");
    }

    @Test
    public void testQueryWithFallbackToORM3() throws Exception {
        String queryString = "select value from %s where num between 26 and 31";
        executeQuery(queryString, "value2", "value6", "value10");
    }

    @Test
    public void testQueryWithFallbackToORMTooManyPartitions2() throws Exception {
        String queryString = "select value from %s where num!=25 and (ds='2008-04-09' or ds='2008-05-09')";
        executeQueryExceedPartitionLimit(queryString, 6);
    }

    @Test
    public void testQueryWithFallbackToORMTooManyPartitions3() throws Exception {
        String queryString = "select value from %s where num>=30";
        executeQueryExceedPartitionLimit(queryString, 9);
    }

    @Test
    public void testQueryWithFallbackToORMTooManyPartitions4() throws Exception {
        String queryString = "select value from %s where num between 20 and 50";
        executeQueryExceedPartitionLimit(queryString, 12);
    }

    /* Tests with queries which cannot be executed with directSQL, because the contain like or in.
    After falling back to ORM the number of partitions cannot be fetched by the
    ObjectStore.getNumPartitionsViaOrmFilter method. They are fetched by the
    ObjectStore.getPartitionNamesPrunedByExprNoTxn method.
     */
    @Test
    public void testQueryWithInWithFallbackToORM() throws Exception {
        setupNumTmpTable();
        String queryString = ("select value from %s a where ds='2008-04-09' and a.num in (select value from " + (TestMetaStoreLimitPartitionRequest.TABLE_NAME)) + "_num_tmp)";
        executeQuery(queryString, "value1", "value2");
    }

    @Test
    public void testQueryWithInWithFallbackToORMTooManyPartitions() throws Exception {
        setupNumTmpTable();
        String queryString = ("select value from %s a where a.num in (select value from " + (TestMetaStoreLimitPartitionRequest.TABLE_NAME)) + "_num_tmp)";
        executeQueryExceedPartitionLimit(queryString, 12);
    }

    @Test
    public void testQueryWithInWithFallbackToORMTooManyPartitions2() throws Exception {
        setupNumTmpTable();
        String queryString = ("select value from %s a where a.num in (select value from " + (TestMetaStoreLimitPartitionRequest.TABLE_NAME)) + "_num_tmp where value='25')";
        executeQueryExceedPartitionLimit(queryString, 12);
    }

    @Test
    public void testQueryWithLikeWithFallbackToORMTooManyPartitions() throws Exception {
        String queryString = "select value from %s where num like '3%%'";
        executeQueryExceedPartitionLimit(queryString, 6);
    }
}

