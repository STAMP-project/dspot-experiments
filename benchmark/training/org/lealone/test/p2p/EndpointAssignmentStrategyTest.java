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
package org.lealone.test.p2p;


import org.junit.Test;
import org.lealone.test.runmode.RunModeTest;
import org.lealone.test.sql.SqlTestBase;


public class EndpointAssignmentStrategyTest extends RunModeTest {
    public EndpointAssignmentStrategyTest() {
    }

    @Test
    @Override
    public void run() throws Exception {
        String dbName = (EndpointAssignmentStrategyTest.class.getSimpleName()) + "_Random";
        sql = ("CREATE DATABASE IF NOT EXISTS " + dbName) + " RUN MODE sharding";
        sql += " WITH REPLICATION STRATEGY (class: 'SimpleStrategy', replication_factor: 1)";
        sql += " WITH ENDPOINT ASSIGNMENT STRATEGY (class: 'RandomEndpointAssignmentStrategy', assignment_factor: 3)";
        executeUpdate(sql);
        dbName = (EndpointAssignmentStrategyTest.class.getSimpleName()) + "_LoadBased";
        sql = ("CREATE DATABASE IF NOT EXISTS " + dbName) + " RUN MODE sharding";
        sql += " WITH REPLICATION STRATEGY (class: 'SimpleStrategy', replication_factor: 1)";
        sql += " WITH ENDPOINT ASSIGNMENT STRATEGY (class: 'LoadBasedEndpointAssignmentStrategy', assignment_factor: 2)";
        executeUpdate(sql);
        dbName = (EndpointAssignmentStrategyTest.class.getSimpleName()) + "_Manual";
        sql = ("CREATE DATABASE IF NOT EXISTS " + dbName) + " RUN MODE sharding";
        sql += " WITH REPLICATION STRATEGY (class: 'SimpleStrategy', replication_factor: 1)";
        sql += " WITH ENDPOINT ASSIGNMENT STRATEGY " + ("(class: 'ManualEndpointAssignmentStrategy', assignment_factor: 2, " + "host_id_list: '127.0.0.1:7210,127.0.0.3:7210')");
        executeUpdate(sql);
    }
}

