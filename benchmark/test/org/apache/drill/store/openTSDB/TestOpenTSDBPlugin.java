/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.store.openTSDB;


import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.drill.PlanTestBase;
import org.apache.drill.common.exceptions.UserRemoteException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class TestOpenTSDBPlugin extends PlanTestBase {
    private static int portNumber;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(TestOpenTSDBPlugin.portNumber);

    @Test
    public void testBasicQueryFromWithRequiredParams() throws Exception {
        String query = "select * from openTSDB.`(metric=warp.speed.test, start=47y-ago, aggregator=sum)`";
        Assert.assertEquals(18, testSql(query));
    }

    @Test
    public void testBasicQueryGroupBy() throws Exception {
        String query = "select `timestamp`, sum(`aggregated value`) from openTSDB.`(metric=warp.speed.test, aggregator=sum, start=47y-ago)` group by `timestamp`";
        Assert.assertEquals(15, testSql(query));
    }

    @Test
    public void testBasicQueryFromWithInterpolationParam() throws Exception {
        String query = "select * from openTSDB.`(metric=warp.speed.test, aggregator=sum, start=47y-ago, downsample=5y-avg)`";
        Assert.assertEquals(4, testSql(query));
    }

    @Test
    public void testBasicQueryFromWithEndParam() throws Exception {
        String query = "select * from openTSDB.`(metric=warp.speed.test, aggregator=sum, start=47y-ago, end=1407165403000))`";
        Assert.assertEquals(5, testSql(query));
    }

    @Test(expected = UserRemoteException.class)
    public void testBasicQueryWithoutTableName() throws Exception {
        test("select * from openTSDB.``;");
    }

    @Test(expected = UserRemoteException.class)
    public void testBasicQueryWithNonExistentTableName() throws Exception {
        test("select * from openTSDB.`warp.spee`");
    }

    @Test
    public void testPhysicalPlanSubmission() throws Exception {
        String query = "select * from openTSDB.`(metric=warp.speed.test, start=47y-ago, aggregator=sum)`";
        testPhysicalPlanExecutionBasedOnQuery(query);
    }

    @Test
    public void testDescribe() throws Exception {
        test("use openTSDB");
        test("describe `warp.speed.test`");
        Assert.assertEquals(1, testSql("show tables"));
    }
}

