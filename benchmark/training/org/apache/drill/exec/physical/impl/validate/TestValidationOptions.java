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
package org.apache.drill.exec.physical.impl.validate;


import ExecConstants.ENABLE_ITERATOR_VALIDATION;
import ExecConstants.ENABLE_ITERATOR_VALIDATION_OPTION;
import ExecConstants.ENABLE_VECTOR_VALIDATION;
import ExecConstants.ENABLE_VECTOR_VALIDATION_OPTION;
import org.apache.drill.test.BaseDirTestWatcher;
import org.apache.drill.test.ClientFixture;
import org.apache.drill.test.ClusterFixture;
import org.apache.drill.test.ClusterFixtureBuilder;
import org.apache.drill.test.DrillTest;
import org.apache.drill.test.LogFixture;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;


@Ignore("requires manual verification")
public class TestValidationOptions extends DrillTest {
    protected static LogFixture logFixture;

    @Rule
    public final BaseDirTestWatcher dirTestWatcher = new BaseDirTestWatcher();

    // To validate these tests, set breakpoints in ImplCreator
    // and IteratorValidatorBatchIterator to see if the options
    // work as expected.
    @Test
    public void testOptions() throws Exception {
        ClusterFixtureBuilder builder = ClusterFixture.builder(dirTestWatcher).maxParallelization(1).configProperty(ENABLE_ITERATOR_VALIDATION, false).configProperty(ENABLE_VECTOR_VALIDATION, false).sessionOption(ENABLE_ITERATOR_VALIDATION_OPTION, true).sessionOption(ENABLE_VECTOR_VALIDATION_OPTION, true);
        try (ClusterFixture cluster = builder.build();ClientFixture client = cluster.clientFixture()) {
            boolean hasAssertions = false;
            assert hasAssertions = true;
            Assert.assertFalse(hasAssertions);
            String sql = "SELECT id_i, name_s10 FROM `mock`.`customers_10`";
            client.queryBuilder().sql(sql).run();
            client.alterSession(ENABLE_VECTOR_VALIDATION, false);
            client.queryBuilder().sql(sql).run();
            client.alterSession(ENABLE_ITERATOR_VALIDATION, false);
            client.queryBuilder().sql(sql).run();
        }
    }

    /**
     * Config options override session options. Config options allow passing in
     * the setting at run time on the command line. This is a work-around for the
     * fact that the config system has no generic solution at present.
     *
     * @throws Exception
     * 		if anything goes wrong
     */
    @Test
    public void testConfig() throws Exception {
        ClusterFixtureBuilder builder = ClusterFixture.builder(dirTestWatcher).maxParallelization(1).configProperty(ENABLE_ITERATOR_VALIDATION, true).configProperty(ENABLE_VECTOR_VALIDATION, true).sessionOption(ENABLE_ITERATOR_VALIDATION_OPTION, false).sessionOption(ENABLE_VECTOR_VALIDATION_OPTION, false);
        try (ClusterFixture cluster = builder.build();ClientFixture client = cluster.clientFixture()) {
            boolean hasAssertions = false;
            assert hasAssertions = true;
            Assert.assertFalse(hasAssertions);
            String sql = "SELECT id_i, name_s10 FROM `mock`.`customers_10`";
            client.queryBuilder().sql(sql).run();
        }
    }

    /**
     * Should do no validation with all-default options.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDefaults() throws Exception {
        ClusterFixtureBuilder builder = ClusterFixture.builder(dirTestWatcher).maxParallelization(1);
        try (ClusterFixture cluster = builder.build();ClientFixture client = cluster.clientFixture()) {
            boolean hasAssertions = false;
            assert hasAssertions = true;
            Assert.assertFalse(hasAssertions);
            String sql = "SELECT id_i, name_s10 FROM `mock`.`customers_10`";
            client.queryBuilder().sql(sql).run();
        }
    }
}

