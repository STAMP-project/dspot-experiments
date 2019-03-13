/**
 * Licensed to Crate.IO GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial agreement.
 */
package io.crate.integrationtests;


import ESIntegTestCase.ClusterScope;
import ESIntegTestCase.Scope;
import org.junit.Test;


@ClusterScope(scope = Scope.TEST, maxNumDataNodes = 2)
public class ThreadPoolsExhaustedIntegrationTest extends SQLTransportIntegrationTest {
    @Test
    public void testRegularSelectWithFewAvailableThreadsShouldNeverGetStuck() throws Exception {
        execute("create table t (x int) with (number_of_replicas = 0)");
        ensureYellow();
        bulkInsert(10);
        assertRejectedExecutionFailure("select * from t limit ?", new Object[]{ 1000 });
    }

    @Test
    public void testDistributedPushSelectWithFewAvailableThreadsShouldNeverGetStuck() throws Exception {
        execute("create table t (x int) with (number_of_replicas = 0)");
        ensureYellow();
        bulkInsert(10);
        // by setting a very high limit we force a push based collection instead of a direct response
        assertRejectedExecutionFailure("select * from t limit ?", new Object[]{ 1000000 });
    }
}

