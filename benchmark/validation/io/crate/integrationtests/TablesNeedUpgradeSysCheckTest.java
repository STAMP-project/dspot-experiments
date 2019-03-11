/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.integrationtests;


import ESIntegTestCase.ClusterScope;
import org.hamcrest.core.Is;
import org.junit.Test;


@ClusterScope(numDataNodes = 0, numClientNodes = 0)
public class TablesNeedUpgradeSysCheckTest extends SQLTransportIntegrationTest {
    @Test
    public void testNoUpgradeRequired() throws Exception {
        startUpNodeWithDataDir("/indices/data_home/cratedata-3.2.3.zip");
        execute("select * from sys.shards where min_lucene_version = '7.5.0';");
        assertThat(response.rowCount(), Is.is(4L));
        execute("select * from sys.checks where id = 3");
        assertThat(response.rowCount(), Is.is(1L));
        assertThat(response.rows()[0][0], Is.is("The following tables need to be upgraded for compatibility with future versions of CrateDB: [] https://cr8.is/d-cluster-check-3"));
    }
}

