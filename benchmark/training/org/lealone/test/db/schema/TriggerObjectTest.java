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
package org.lealone.test.db.schema;


import java.sql.Connection;
import java.sql.SQLException;
import org.junit.Test;
import org.lealone.db.api.Trigger;
import org.lealone.test.db.DbObjectTestBase;


public class TriggerObjectTest extends DbObjectTestBase {
    public static class MyTrigger implements Trigger {
        @Override
        public void init(Connection conn, String schemaName, String triggerName, String tableName, boolean before, int type) throws SQLException {
            System.out.println(((("schemaName=" + schemaName) + " tableName=") + tableName));
        }

        @Override
        public void fire(Connection conn, Object[] oldRow, Object[] newRow) throws SQLException {
            System.out.println(((("oldRow=" + oldRow) + " newRow=") + newRow));
        }

        @Override
        public void close() throws SQLException {
            System.out.println("org.lealone.test.db.schema.TriggerObjectTest.MyInsertTrigger.close()");
        }

        @Override
        public void remove() throws SQLException {
            System.out.println("org.lealone.test.db.schema.TriggerObjectTest.MyInsertTrigger.remove()");
        }
    }

    @Test
    public void run() throws Exception {
        create();
        drop();
    }
}

