/**
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.bvt.proxy;


import com.alibaba.druid.stat.JdbcConnectionStat;
import junit.framework.TestCase;
import org.junit.Assert;


public class StatisticTest extends TestCase {
    public void test_stat() throws Exception {
        JdbcConnectionStat stat = new JdbcConnectionStat();
        Assert.assertEquals(null, stat.getConnectLastTime());
        stat.setActiveCount(1);
        Assert.assertEquals(1, stat.getActiveMax());
        stat.setActiveCount(2);
        Assert.assertEquals(2, stat.getActiveMax());
    }
}

