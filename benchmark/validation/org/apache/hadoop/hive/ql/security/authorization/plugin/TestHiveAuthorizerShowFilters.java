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
package org.apache.hadoop.hive.ql.security.authorization.plugin;


import HivePrivilegeObjectType.DATABASE;
import HivePrivilegeObjectType.TABLE_OR_VIEW;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.ql.IDriver;
import org.apache.hadoop.hive.ql.security.HiveAuthenticationProvider;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Test HiveAuthorizer api invocation for filtering objects
 */
public class TestHiveAuthorizerShowFilters {
    protected static HiveConf conf;

    protected static IDriver driver;

    private static final String tableName1 = ((TestHiveAuthorizerShowFilters.class.getSimpleName()) + "table1").toLowerCase();

    private static final String tableName2 = ((TestHiveAuthorizerShowFilters.class.getSimpleName()) + "table2").toLowerCase();

    private static final String dbName1 = ((TestHiveAuthorizerShowFilters.class.getSimpleName()) + "db1").toLowerCase();

    private static final String dbName2 = ((TestHiveAuthorizerShowFilters.class.getSimpleName()) + "db2").toLowerCase();

    protected static HiveAuthorizer mockedAuthorizer;

    static final List<String> AllTables = TestHiveAuthorizerShowFilters.getSortedList(TestHiveAuthorizerShowFilters.tableName1, TestHiveAuthorizerShowFilters.tableName2);

    static final List<String> AllDbs = TestHiveAuthorizerShowFilters.getSortedList("default", TestHiveAuthorizerShowFilters.dbName1, TestHiveAuthorizerShowFilters.dbName2);

    private static List<HivePrivilegeObject> filterArguments = new ArrayList<>();

    private static List<HivePrivilegeObject> filteredResults = new ArrayList<>();

    /**
     * This factory creates a mocked HiveAuthorizer class. The mocked class is
     * used to capture the argument passed to HiveAuthorizer.filterListCmdObjects.
     * It returns fileredResults object for call to
     * HiveAuthorizer.filterListCmdObjects, and stores the list argument in
     * filterArguments
     */
    protected static class MockedHiveAuthorizerFactory implements HiveAuthorizerFactory {
        protected abstract class AuthorizerWithFilterCmdImpl implements HiveAuthorizer {
            @Override
            public List<HivePrivilegeObject> filterListCmdObjects(List<HivePrivilegeObject> listObjs, HiveAuthzContext context) throws HiveAccessControlException, HiveAuthzPluginException {
                // capture arguments in static
                TestHiveAuthorizerShowFilters.filterArguments.addAll(listObjs);
                // return static variable with results, if it is set to some set of
                // values
                // otherwise return the arguments
                if ((TestHiveAuthorizerShowFilters.filteredResults.size()) == 0) {
                    return TestHiveAuthorizerShowFilters.filterArguments;
                }
                return TestHiveAuthorizerShowFilters.filteredResults;
            }
        }

        @Override
        public HiveAuthorizer createHiveAuthorizer(HiveMetastoreClientFactory metastoreClientFactory, HiveConf conf, HiveAuthenticationProvider authenticator, HiveAuthzSessionContext ctx) {
            Mockito.validateMockitoUsage();
            TestHiveAuthorizerShowFilters.mockedAuthorizer = Mockito.mock(TestHiveAuthorizerShowFilters.MockedHiveAuthorizerFactory.AuthorizerWithFilterCmdImpl.class, Mockito.withSettings().verboseLogging());
            try {
                Mockito.when(TestHiveAuthorizerShowFilters.mockedAuthorizer.filterListCmdObjects(((List<HivePrivilegeObject>) (ArgumentMatchers.any())), ((HiveAuthzContext) (ArgumentMatchers.any())))).thenCallRealMethod();
            } catch (Exception e) {
                Assert.fail(("Caught exception " + e));
            }
            return TestHiveAuthorizerShowFilters.mockedAuthorizer;
        }
    }

    @Test
    public void testShowDatabasesAll() throws Exception {
        runShowDbTest(TestHiveAuthorizerShowFilters.AllDbs);
    }

    @Test
    public void testShowDatabasesSelected() throws Exception {
        TestHiveAuthorizerShowFilters.setFilteredResults(DATABASE, TestHiveAuthorizerShowFilters.dbName2);
        runShowDbTest(Arrays.asList(TestHiveAuthorizerShowFilters.dbName2));
    }

    @Test
    public void testShowTablesAll() throws Exception {
        runShowTablesTest(TestHiveAuthorizerShowFilters.AllTables);
    }

    @Test
    public void testShowTablesSelected() throws Exception {
        TestHiveAuthorizerShowFilters.setFilteredResults(TABLE_OR_VIEW, TestHiveAuthorizerShowFilters.tableName2);
        runShowTablesTest(Arrays.asList(TestHiveAuthorizerShowFilters.tableName2));
    }
}

