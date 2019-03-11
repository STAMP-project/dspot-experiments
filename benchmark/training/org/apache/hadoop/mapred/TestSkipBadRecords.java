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
package org.apache.hadoop.mapred;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Test;


/**
 * test SkipBadRecords
 */
public class TestSkipBadRecords {
    @Test(timeout = 5000)
    public void testSkipBadRecords() {
        // test default values
        Configuration conf = new Configuration();
        Assert.assertEquals(2, SkipBadRecords.getAttemptsToStartSkipping(conf));
        Assert.assertTrue(SkipBadRecords.getAutoIncrMapperProcCount(conf));
        Assert.assertTrue(SkipBadRecords.getAutoIncrReducerProcCount(conf));
        Assert.assertEquals(0, SkipBadRecords.getMapperMaxSkipRecords(conf));
        Assert.assertEquals(0, SkipBadRecords.getReducerMaxSkipGroups(conf), 0);
        Assert.assertNull(SkipBadRecords.getSkipOutputPath(conf));
        // test setters
        SkipBadRecords.setAttemptsToStartSkipping(conf, 5);
        SkipBadRecords.setAutoIncrMapperProcCount(conf, false);
        SkipBadRecords.setAutoIncrReducerProcCount(conf, false);
        SkipBadRecords.setMapperMaxSkipRecords(conf, 6L);
        SkipBadRecords.setReducerMaxSkipGroups(conf, 7L);
        JobConf jc = new JobConf();
        SkipBadRecords.setSkipOutputPath(jc, new Path("test"));
        // test getters
        Assert.assertEquals(5, SkipBadRecords.getAttemptsToStartSkipping(conf));
        Assert.assertFalse(SkipBadRecords.getAutoIncrMapperProcCount(conf));
        Assert.assertFalse(SkipBadRecords.getAutoIncrReducerProcCount(conf));
        Assert.assertEquals(6L, SkipBadRecords.getMapperMaxSkipRecords(conf));
        Assert.assertEquals(7L, SkipBadRecords.getReducerMaxSkipGroups(conf), 0);
        Assert.assertEquals("test", SkipBadRecords.getSkipOutputPath(jc).toString());
    }
}

