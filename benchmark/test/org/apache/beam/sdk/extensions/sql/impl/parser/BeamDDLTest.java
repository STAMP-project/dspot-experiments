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
package org.apache.beam.sdk.extensions.sql.impl.parser;


import CalciteUtils.INTEGER;
import Schema.Field;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.util.stream.Stream;
import org.apache.beam.sdk.extensions.sql.impl.BeamSqlEnv;
import org.apache.beam.sdk.extensions.sql.impl.ParseException;
import org.apache.beam.sdk.extensions.sql.meta.Table;
import org.apache.beam.sdk.extensions.sql.meta.provider.test.TestTableProvider;
import org.junit.Assert;
import org.junit.Test;


/**
 * UnitTest for {@link BeamSqlParserImpl}.
 */
public class BeamDDLTest {
    @Test
    public void testParseCreateExternalTable_full() throws Exception {
        TestTableProvider tableProvider = new TestTableProvider();
        BeamSqlEnv env = BeamSqlEnv.withTableProvider(tableProvider);
        JSONObject properties = new JSONObject();
        JSONArray hello = new JSONArray();
        hello.add("james");
        hello.add("bond");
        properties.put("hello", hello);
        env.executeDdl(("CREATE EXTERNAL TABLE person (\n" + ((((("id int COMMENT \'id\', \n" + "name varchar COMMENT \'name\') \n") + "TYPE \'text\' \n") + "COMMENT \'person table\' \n") + "LOCATION \'/home/admin/person\'\n") + "TBLPROPERTIES \'{\"hello\": [\"james\", \"bond\"]}\'")));
        Assert.assertEquals(BeamDDLTest.mockTable("person", "text", "person table", properties), tableProvider.getTables().get("person"));
    }

    @Test(expected = ParseException.class)
    public void testParseCreateExternalTable_withoutType() throws Exception {
        BeamSqlEnv env = BeamSqlEnv.withTableProvider(new TestTableProvider());
        env.executeDdl(("CREATE EXTERNAL TABLE person (\n" + (((("id int COMMENT \'id\', \n" + "name varchar COMMENT \'name\') \n") + "COMMENT \'person table\' \n") + "LOCATION \'/home/admin/person\'\n") + "TBLPROPERTIES \'{\"hello\": [\"james\", \"bond\"]}\'")));
    }

    @Test(expected = ParseException.class)
    public void testParseCreateTable() throws Exception {
        BeamSqlEnv env = BeamSqlEnv.withTableProvider(new TestTableProvider());
        env.executeDdl(("CREATE TABLE person (\n" + ((((("id int COMMENT \'id\', \n" + "name varchar COMMENT \'name\') \n") + "TYPE \'text\' \n") + "COMMENT \'person table\' \n") + "LOCATION \'/home/admin/person\'\n") + "TBLPROPERTIES \'{\"hello\": [\"james\", \"bond\"]}\'")));
    }

    @Test
    public void testParseCreateExternalTable_withoutTableComment() throws Exception {
        TestTableProvider tableProvider = new TestTableProvider();
        BeamSqlEnv env = BeamSqlEnv.withTableProvider(tableProvider);
        JSONObject properties = new JSONObject();
        JSONArray hello = new JSONArray();
        hello.add("james");
        hello.add("bond");
        properties.put("hello", hello);
        env.executeDdl(("CREATE EXTERNAL TABLE person (\n" + (((("id int COMMENT \'id\', \n" + "name varchar COMMENT \'name\') \n") + "TYPE \'text\' \n") + "LOCATION \'/home/admin/person\'\n") + "TBLPROPERTIES \'{\"hello\": [\"james\", \"bond\"]}\'")));
        Assert.assertEquals(BeamDDLTest.mockTable("person", "text", null, properties), tableProvider.getTables().get("person"));
    }

    @Test
    public void testParseCreateExternalTable_withoutTblProperties() throws Exception {
        TestTableProvider tableProvider = new TestTableProvider();
        BeamSqlEnv env = BeamSqlEnv.withTableProvider(tableProvider);
        env.executeDdl(("CREATE EXTERNAL TABLE person (\n" + (((("id int COMMENT \'id\', \n" + "name varchar COMMENT \'name\') \n") + "TYPE \'text\' \n") + "COMMENT \'person table\' \n") + "LOCATION \'/home/admin/person\'\n")));
        Assert.assertEquals(BeamDDLTest.mockTable("person", "text", "person table", new JSONObject()), tableProvider.getTables().get("person"));
    }

    @Test
    public void testParseCreateExternalTable_withoutLocation() throws Exception {
        TestTableProvider tableProvider = new TestTableProvider();
        BeamSqlEnv env = BeamSqlEnv.withTableProvider(tableProvider);
        env.executeDdl(("CREATE EXTERNAL TABLE person (\n" + ((("id int COMMENT \'id\', \n" + "name varchar COMMENT \'name\') \n") + "TYPE \'text\' \n") + "COMMENT \'person table\' \n")));
        Assert.assertEquals(BeamDDLTest.mockTable("person", "text", "person table", new JSONObject(), null), tableProvider.getTables().get("person"));
    }

    @Test
    public void testParseCreateExternalTable_minimal() throws Exception {
        TestTableProvider tableProvider = new TestTableProvider();
        BeamSqlEnv env = BeamSqlEnv.withTableProvider(tableProvider);
        env.executeDdl("CREATE EXTERNAL TABLE person (id INT) TYPE text");
        Assert.assertEquals(Table.builder().name("person").type("text").schema(Stream.of(Field.of("id", INTEGER).withNullable(true)).collect(toSchema())).properties(new JSONObject()).build(), tableProvider.getTables().get("person"));
    }

    @Test
    public void testParseCreateExternalTable_withDatabase() throws Exception {
        TestTableProvider rootProvider = new TestTableProvider();
        TestTableProvider testProvider = new TestTableProvider();
        BeamSqlEnv env = BeamSqlEnv.withTableProvider(rootProvider);
        env.addSchema("test", testProvider);
        Assert.assertNull(testProvider.getTables().get("person"));
        env.executeDdl("CREATE EXTERNAL TABLE test.person (id INT) TYPE text");
        Assert.assertNotNull(testProvider.getTables().get("person"));
    }

    @Test
    public void testParseDropTable() throws Exception {
        TestTableProvider tableProvider = new TestTableProvider();
        BeamSqlEnv env = BeamSqlEnv.withTableProvider(tableProvider);
        Assert.assertNull(tableProvider.getTables().get("person"));
        env.executeDdl(("CREATE EXTERNAL TABLE person (\n" + ((("id int COMMENT \'id\', \n" + "name varchar COMMENT \'name\') \n") + "TYPE \'text\' \n") + "COMMENT \'person table\' \n")));
        Assert.assertNotNull(tableProvider.getTables().get("person"));
        env.executeDdl("drop table person");
        Assert.assertNull(tableProvider.getTables().get("person"));
    }
}

