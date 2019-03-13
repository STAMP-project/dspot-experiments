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
package com.mapr.drill.maprdb.tests.json;


import com.mapr.tests.annotations.ClusterTest;
import org.apache.drill.exec.proto.UserBitShared;
import org.apache.drill.exec.proto.UserBitShared.QueryProfile;
import org.apache.drill.exec.proto.helper.QueryIdHelper;
import org.apache.drill.exec.rpc.user.AwaitableUserResultsListener;
import org.apache.drill.exec.store.sys.PersistentStore;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category(ClusterTest.class)
public class TestScanRanges extends BaseJsonTest {
    private static final Logger logger = LoggerFactory.getLogger(TestScanRanges.class);

    private static final int TOTAL_ROW_COUNT = 1000000;

    private static final String TABLE_NAME = "large_table_TestScanRanges";

    private static final String JSON_FILE_URL = "/com/mapr/drill/json/business.json";

    private static boolean tableCreated = false;

    private static String tablePath;

    @Test
    public void test_scan_ranges() throws Exception {
        final PersistentStore<UserBitShared.QueryProfile> completed = getDrillbitContext().getProfileStoreContext().getCompletedProfileStore();
        setColumnWidths(new int[]{ 25, 40, 25, 45 });
        final String sql = format(("SELECT\n" + (("  *\n" + "FROM\n") + "  %s.`%s` business")));
        final SilentListener resultListener = new SilentListener();
        final AwaitableUserResultsListener listener = new AwaitableUserResultsListener(resultListener);
        testWithListener(QueryType.SQL, sql, listener);
        listener.await();
        Assert.assertEquals(TestScanRanges.TOTAL_ROW_COUNT, resultListener.getRowCount());
        String queryId = QueryIdHelper.getQueryId(resultListener.getQueryId());
        QueryProfile profile = completed.get(queryId);
        String profileString = String.valueOf(profile);
        TestScanRanges.logger.debug(profileString);
        Assert.assertNotNull(profile);
        Assert.assertTrue(((profile.getTotalFragments()) >= 5));// should at least as many as

    }

    @Test
    public void test_scan_ranges_with_filter_on_id() throws Exception {
        setColumnWidths(new int[]{ 25, 25, 25 });
        final String sql = format(("SELECT\n" + (((("  _id, business_id, city\n" + "FROM\n") + "  %s.`%s` business\n") + "WHERE\n") + " _id > 'M' AND _id < 'Q'")));
        final SilentListener resultListener = new SilentListener();
        final AwaitableUserResultsListener listener = new AwaitableUserResultsListener(resultListener);
        testWithListener(QueryType.SQL, sql, listener);
        listener.await();
        Assert.assertEquals(200000, resultListener.getRowCount());
    }

    @Test
    public void test_scan_ranges_with_filter_on_non_id_field() throws Exception {
        setColumnWidths(new int[]{ 25, 25, 25 });
        final String sql = format(("SELECT\n" + (((("  _id, business_id, documentId\n" + "FROM\n") + "  %s.`%s` business\n") + "WHERE\n") + " documentId >= 100 AND documentId < 150")));
        final SilentListener resultListener = new SilentListener();
        final AwaitableUserResultsListener listener = new AwaitableUserResultsListener(resultListener);
        testWithListener(QueryType.SQL, sql, listener);
        listener.await();
        Assert.assertEquals(10000, resultListener.getRowCount());
    }
}

