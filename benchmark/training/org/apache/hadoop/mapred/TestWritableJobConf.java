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
import org.junit.Test;


public class TestWritableJobConf {
    private static final Configuration CONF = new Configuration();

    @Test
    public void testEmptyConfiguration() throws Exception {
        JobConf conf = new JobConf();
        Configuration deser = serDeser(conf);
        assertEquals(conf, deser);
    }

    @Test
    public void testNonEmptyConfiguration() throws Exception {
        JobConf conf = new JobConf();
        conf.set("a", "A");
        conf.set("b", "B");
        Configuration deser = serDeser(conf);
        assertEquals(conf, deser);
    }

    @Test
    public void testConfigurationWithDefaults() throws Exception {
        JobConf conf = new JobConf(false);
        conf.set("a", "A");
        conf.set("b", "B");
        Configuration deser = serDeser(conf);
        assertEquals(conf, deser);
    }
}

