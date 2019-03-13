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
package org.lealone.test.db;


import org.junit.Assert;
import org.junit.Test;
import org.lealone.db.UserDataType;
import org.lealone.db.table.Column;


public class UserDataTypeTest extends DbObjectTestBase {
    @Test
    public void run() {
        int id = db.allocateObjectId();
        String udtName = "EMAIL";
        UserDataType udt = new UserDataType(db, id, udtName);
        Assert.assertEquals(id, udt.getId());
        Column column = new Column("c", 0);
        udt.setColumn(column);
        db.addDatabaseObject(session, udt);
        Assert.assertNotNull(db.findUserDataType(udtName));
        udt.removeChildrenAndResources(session);
        Assert.assertNotNull(db.findUserDataType(udtName));// ?????UserDataType

        db.removeDatabaseObject(session, udt);
        Assert.assertNull(db.findUserDataType(udtName));
        // ??SQL
        // CREATE DOMAIN/TYPE/DATATYPE?????
        // DROP DOMAIN/TYPE/DATATYPE?????
        // -----------------------------------------------
        // VALUE?CREATE DOMAIN?????????
        String sql = ("CREATE DOMAIN IF NOT EXISTS " + udtName) + " AS VARCHAR(255) CHECK (POSITION('@', VALUE) > 1)";
        executeUpdate(sql);
        Assert.assertNotNull(db.findUserDataType(udtName));
        sql = "DROP DOMAIN " + udtName;
        executeUpdate(sql);
        Assert.assertNull(db.findUserDataType(udtName));
        sql = ("CREATE TYPE IF NOT EXISTS " + udtName) + " AS VARCHAR(255) CHECK (POSITION('@', VALUE) > 1)";
        executeUpdate(sql);
        Assert.assertNotNull(db.findUserDataType(udtName));
        sql = "DROP TYPE " + udtName;
        executeUpdate(sql);
        Assert.assertNull(db.findUserDataType(udtName));
        sql = ("CREATE DATATYPE IF NOT EXISTS " + udtName) + " AS VARCHAR(255) CHECK (POSITION('@', VALUE) > 1)";
        executeUpdate(sql);
        Assert.assertNotNull(db.findUserDataType(udtName));
        sql = "DROP DATATYPE " + udtName;
        executeUpdate(sql);
        Assert.assertNull(db.findUserDataType(udtName));
        // ?????????????????????int
        // new String[]{"INTEGER", "INT", "MEDIUMINT", "INT4", "SIGNED"}
        // ?????????????????????
        // ?CREATE DATATYPE IF NOT EXISTS int AS VARCHAR(255)
        // ????????????
        // ?CREATE DATATYPE IF NOT EXISTS integer AS VARCHAR(255)
        sql = "CREATE DATATYPE IF NOT EXISTS int AS VARCHAR(255) CHECK (POSITION('@', VALUE) > 1)";
        executeUpdate(sql);
        udtName = "int";
        if (db.getSettings().databaseToUpper)
            udtName = udtName.toUpperCase();

        Assert.assertNotNull(db.findUserDataType(udtName));
        sql = "DROP DATATYPE int";
        executeUpdate(sql);
        Assert.assertNull(db.findUserDataType(udtName));
        try {
            udtName = "integer";
            // ??DATABASE_TO_UPPER?false????INTEGER
            if (!(db.getSettings().databaseToUpper))
                udtName = udtName.toUpperCase();

            sql = ("CREATE DATATYPE IF NOT EXISTS " + udtName) + " AS VARCHAR(255) CHECK (POSITION('@', VALUE) > 1)";
            executeUpdate(sql);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().toLowerCase().contains("user data type"));
        }
    }
}

