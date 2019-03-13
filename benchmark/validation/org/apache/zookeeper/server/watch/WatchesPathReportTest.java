/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zookeeper.server.watch;


import java.util.Map;
import java.util.Set;
import org.apache.zookeeper.ZKTestCase;
import org.junit.Assert;
import org.junit.Test;


public class WatchesPathReportTest extends ZKTestCase {
    private Map<String, Set<Long>> m;

    private WatchesPathReport r;

    @Test
    public void testHasSessions() {
        Assert.assertTrue(r.hasSessions("path1"));
        Assert.assertTrue(r.hasSessions("path2"));
        Assert.assertFalse(r.hasSessions("path3"));
    }

    @Test
    public void testGetSessions() {
        Set<Long> s = r.getSessions("path1");
        Assert.assertEquals(2, s.size());
        Assert.assertTrue(s.contains(101L));
        Assert.assertTrue(s.contains(102L));
        s = r.getSessions("path2");
        Assert.assertEquals(1, s.size());
        Assert.assertTrue(s.contains(201L));
        Assert.assertNull(r.getSessions("path3"));
    }

    @Test
    public void testToMap() {
        Assert.assertEquals(m, r.toMap());
    }
}

