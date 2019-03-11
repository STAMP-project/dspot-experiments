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
package org.apache.hadoop.hdfs.server.namenode.top.window;


import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.junit.Assert;
import org.junit.Test;


public class TestRollingWindowManager {
    Configuration conf;

    RollingWindowManager manager;

    String[] users;

    static final int MIN_2_MS = 60000;

    final int WINDOW_LEN_MS = 1 * (TestRollingWindowManager.MIN_2_MS);

    final int BUCKET_CNT = 10;

    final int N_TOP_USERS = 10;

    final int BUCKET_LEN = (WINDOW_LEN_MS) / (BUCKET_CNT);

    @Test
    public void testTops() {
        long time = (WINDOW_LEN_MS) + (((BUCKET_LEN) * 3) / 2);
        for (int i = 0; i < (users.length); i++)
            manager.recordMetric(time, "open", users[i], ((i + 1) * 2));

        time++;
        for (int i = 0; i < (users.length); i++)
            manager.recordMetric(time, "close", users[i], (i + 1));

        time++;
        RollingWindowManager.TopWindow tops = manager.snapshot(time);
        Assert.assertEquals("Unexpected number of ops", 2, tops.getOps().size());
        for (RollingWindowManager.Op op : tops.getOps()) {
            final List<RollingWindowManager.User> topUsers = op.getTopUsers();
            Assert.assertEquals("Unexpected number of users", N_TOP_USERS, topUsers.size());
            if ((op.getOpType()) == "open") {
                for (int i = 0; i < (topUsers.size()); i++) {
                    RollingWindowManager.User user = topUsers.get(i);
                    Assert.assertEquals(("Unexpected count for user " + (user.getUser())), (((users.length) - i) * 2), user.getCount());
                }
                // Closed form of sum(range(2,42,2))
                Assert.assertEquals("Unexpected total count for op", ((2 + ((users.length) * 2)) * ((users.length) / 2)), op.getTotalCount());
            }
        }
        // move the window forward not to see the "open" results
        time += (WINDOW_LEN_MS) - 2;
        tops = manager.snapshot(time);
        Assert.assertEquals("Unexpected number of ops", 1, tops.getOps().size());
        final RollingWindowManager.Op op = tops.getOps().get(0);
        Assert.assertEquals("Should only see close ops", "close", op.getOpType());
        final List<RollingWindowManager.User> topUsers = op.getTopUsers();
        for (int i = 0; i < (topUsers.size()); i++) {
            RollingWindowManager.User user = topUsers.get(i);
            Assert.assertEquals(("Unexpected count for user " + (user.getUser())), ((users.length) - i), user.getCount());
        }
        // Closed form of sum(range(1,21))
        Assert.assertEquals("Unexpected total count for op", ((1 + (users.length)) * ((users.length) / 2)), op.getTotalCount());
    }
}

