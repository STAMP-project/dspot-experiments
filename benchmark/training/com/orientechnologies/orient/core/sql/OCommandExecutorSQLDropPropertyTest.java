/**
 * *  Copyright 2015 OrientDB LTD (info(at)orientdb.com)
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *       http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *  *
 *  * For more information: http://orientdb.com
 */
package com.orientechnologies.orient.core.sql;


import OType.STRING;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import org.junit.Assert;
import org.junit.Test;


public class OCommandExecutorSQLDropPropertyTest {
    static ODatabaseDocumentTx db;

    private static String DB_STORAGE = "memory";

    private static String DB_NAME = "OCommandExecutorSQLDropPropertyTest";

    private int ORDER_SKIP_LIMIT_ITEMS = 100 * 1000;

    @Test
    public void test() {
        OSchema schema = OCommandExecutorSQLDropPropertyTest.db.getMetadata().getSchema();
        OClass foo = schema.createClass("Foo");
        foo.createProperty("name", STRING);
        Assert.assertTrue(schema.getClass("Foo").existsProperty("name"));
        OCommandExecutorSQLDropPropertyTest.db.command(new OCommandSQL("DROP PROPERTY Foo.name")).execute();
        schema.reload();
        Assert.assertFalse(schema.getClass("Foo").existsProperty("name"));
        foo.createProperty("name", STRING);
        Assert.assertTrue(schema.getClass("Foo").existsProperty("name"));
        OCommandExecutorSQLDropPropertyTest.db.command(new OCommandSQL("DROP PROPERTY `Foo`.name")).execute();
        schema.reload();
        Assert.assertFalse(schema.getClass("Foo").existsProperty("name"));
        foo.createProperty("name", STRING);
        Assert.assertTrue(schema.getClass("Foo").existsProperty("name"));
        OCommandExecutorSQLDropPropertyTest.db.command(new OCommandSQL("DROP PROPERTY Foo.`name`")).execute();
        schema.reload();
        Assert.assertFalse(schema.getClass("Foo").existsProperty("name"));
        foo.createProperty("name", STRING);
        Assert.assertTrue(schema.getClass("Foo").existsProperty("name"));
        OCommandExecutorSQLDropPropertyTest.db.command(new OCommandSQL("DROP PROPERTY `Foo`.`name`")).execute();
        schema.reload();
        Assert.assertFalse(schema.getClass("Foo").existsProperty("name"));
    }

    @Test
    public void testIfExists() {
        OSchema schema = OCommandExecutorSQLDropPropertyTest.db.getMetadata().getSchema();
        OClass testIfExistsClass = schema.createClass("testIfExists");
        testIfExistsClass.createProperty("name", STRING);
        Assert.assertTrue(schema.getClass("testIfExists").existsProperty("name"));
        OCommandExecutorSQLDropPropertyTest.db.command(new OCommandSQL("DROP PROPERTY testIfExists.name if exists")).execute();
        schema.reload();
        Assert.assertFalse(schema.getClass("testIfExists").existsProperty("name"));
        OCommandExecutorSQLDropPropertyTest.db.command(new OCommandSQL("DROP PROPERTY testIfExists.name if exists")).execute();
        schema.reload();
        Assert.assertFalse(schema.getClass("testIfExists").existsProperty("name"));
    }
}

