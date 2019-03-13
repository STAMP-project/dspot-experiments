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
package org.lealone.test.sql.ddl;


import LealoneDatabase.NAME;
import java.sql.Connection;
import org.junit.Assert;
import org.junit.Test;
import org.lealone.test.sql.SqlTestBase;


// ????bug: ?????????????hash??????????????????
// ??????????????bug
public class AlterUserTest extends SqlTestBase {
    public AlterUserTest() {
        super(NAME);
    }

    @Test
    public void run() throws Exception {
        stmt.executeUpdate("DROP USER IF EXISTS test1");
        stmt.executeUpdate("DROP USER IF EXISTS test2");
        stmt.executeUpdate("CREATE USER IF NOT EXISTS test1 PASSWORD 'test'");
        try (Connection conn = getConnection("test1", "test")) {
        } catch (Exception e) {
            Assert.fail();
        }
        stmt.executeUpdate("ALTER USER test1 RENAME TO test2");
        try (Connection conn = getConnection("test2", "test")) {
        } catch (Exception e) {
            Assert.fail();
        }
    }
}

