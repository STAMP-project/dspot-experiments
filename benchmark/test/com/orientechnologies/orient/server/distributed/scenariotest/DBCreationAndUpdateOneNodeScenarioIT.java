/**
 * Copyright 2015 OrientDB LTD (info--at--orientdb.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.orientechnologies.orient.server.distributed.scenariotest;


import org.junit.Test;


/**
 * It checks the consistency in the cluster with the following scenario:
 * - 3 server
 * - db creation on server1
 * - check the new db is present both on server2 and server3
 * - 5 threads write 100 records on server1
 * - check consistency: db with all the records are consistent
 *
 * @author Gabriele Ponzi
 * @unknown <gabriele.ponzi--at--gmail.com>
 */
public class DBCreationAndUpdateOneNodeScenarioIT extends AbstractScenarioTest {
    @Test
    public void test() throws Exception {
        init(AbstractScenarioTest.SERVERS);
        prepare(false);
        execute();
    }
}

