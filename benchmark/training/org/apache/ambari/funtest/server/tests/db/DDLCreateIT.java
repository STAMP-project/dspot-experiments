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
package org.apache.ambari.funtest.server.tests.db;


import java.util.function.Supplier;
import org.junit.Test;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;


public class DDLCreateIT {
    @Test
    public void mysql() {
        DDLCreateIT.testSchemaCreate(() -> new MariaDBContainer("mariadb:10.2").withConfigurationOverride(null).withInitScript("Ambari-DDL-MySQL-CREATE.sql"));
        DDLCreateIT.testSchemaCreate(() -> new MySQLContainer("mysql:5.7").withConfigurationOverride(null).withInitScript("Ambari-DDL-MySQL-CREATE.sql"));
    }

    @Test
    public void postgres() {
        DDLCreateIT.testSchemaCreate(() -> new PostgreSQLContainer("postgres:9.6").withInitScript("Ambari-DDL-Postgres-CREATE.sql"));
        DDLCreateIT.testSchemaCreate(() -> new PostgreSQLContainer("postgres:10").withInitScript("Ambari-DDL-Postgres-CREATE.sql"));
    }
}

