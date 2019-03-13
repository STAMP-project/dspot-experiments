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
package org.apache.hadoop.hive.cli;


import java.io.File;
import java.util.Comparator;
import org.apache.hadoop.hive.cli.control.CliAdapter;
import org.apache.hadoop.hive.cli.control.CliConfigs;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class TestTezPerfCliDriver {
    static CliAdapter adapter = new CliConfigs.TezPerfCliConfig(false).getCliAdapter();

    static class C1 implements Comparator<Object[]> {
        @Override
        public int compare(Object[] o1, Object[] o2) {
            return o1[0].toString().compareTo(o2[0].toString());
        }
    }

    @ClassRule
    public static TestRule cliClassRule = TestTezPerfCliDriver.adapter.buildClassRule();

    @Rule
    public TestRule cliTestRule = TestTezPerfCliDriver.adapter.buildTestRule();

    private String name;

    private File qfile;

    public TestTezPerfCliDriver(String name, File qfile) {
        this.name = name;
        this.qfile = qfile;
    }

    @Test
    public void testCliDriver() throws Exception {
        TestTezPerfCliDriver.adapter.runTest(name, qfile);
    }
}

