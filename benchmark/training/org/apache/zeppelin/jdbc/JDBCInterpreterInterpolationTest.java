/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.zeppelin.jdbc;


import InterpreterResult.Code.SUCCESS;
import InterpreterResult.Type.TABLE;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import java.io.IOException;
import java.util.Properties;
import org.apache.zeppelin.interpreter.InterpreterContext;
import org.apache.zeppelin.interpreter.InterpreterException;
import org.apache.zeppelin.interpreter.InterpreterResult;
import org.apache.zeppelin.resource.ResourcePool;
import org.junit.Assert;
import org.junit.Test;


/**
 * JDBC interpreter Z-variable interpolation unit tests.
 */
public class JDBCInterpreterInterpolationTest extends BasicJDBCTestCaseAdapter {
    private static String jdbcConnection;

    private InterpreterContext interpreterContext;

    private ResourcePool resourcePool;

    @Test
    public void testEnableDisableProperty() throws IOException, InterpreterException {
        Properties properties = new Properties();
        properties.setProperty("common.max_count", "1000");
        properties.setProperty("common.max_retry", "3");
        properties.setProperty("default.driver", "org.h2.Driver");
        properties.setProperty("default.url", getJdbcConnection());
        properties.setProperty("default.user", "");
        properties.setProperty("default.password", "");
        resourcePool.put("zid", "mem");
        String sqlQuery = "select * from test_table where id = '{zid}'";
        // 
        // Empty result expected because "zeppelin.jdbc.interpolation" is false by default ...
        // 
        JDBCInterpreter t = new JDBCInterpreter(properties);
        t.open();
        InterpreterResult interpreterResult = t.interpret(sqlQuery, interpreterContext);
        Assert.assertEquals(SUCCESS, interpreterResult.code());
        Assert.assertEquals(TABLE, interpreterResult.message().get(0).getType());
        Assert.assertEquals(1, interpreterResult.message().size());
        Assert.assertEquals("ID\tNAME\n", interpreterResult.message().get(0).getData());
        // 
        // 1 result expected because "zeppelin.jdbc.interpolation" set to "true" ...
        // 
        properties.setProperty("zeppelin.jdbc.interpolation", "true");
        t = new JDBCInterpreter(properties);
        t.open();
        interpreterResult = t.interpret(sqlQuery, interpreterContext);
        Assert.assertEquals(SUCCESS, interpreterResult.code());
        Assert.assertEquals(TABLE, interpreterResult.message().get(0).getType());
        Assert.assertEquals(1, interpreterResult.message().size());
        Assert.assertEquals("ID\tNAME\nmem\tmemory\n", interpreterResult.message().get(0).getData());
    }

    @Test
    public void testNormalQueryInterpolation() throws IOException, InterpreterException {
        Properties properties = new Properties();
        properties.setProperty("common.max_count", "1000");
        properties.setProperty("common.max_retry", "3");
        properties.setProperty("default.driver", "org.h2.Driver");
        properties.setProperty("default.url", getJdbcConnection());
        properties.setProperty("default.user", "");
        properties.setProperty("default.password", "");
        properties.setProperty("zeppelin.jdbc.interpolation", "true");
        JDBCInterpreter t = new JDBCInterpreter(properties);
        t.open();
        // 
        // Empty result expected because "kbd" is not defined ...
        // 
        String sqlQuery = "select * from test_table where id = '{kbd}'";
        InterpreterResult interpreterResult = t.interpret(sqlQuery, interpreterContext);
        Assert.assertEquals(SUCCESS, interpreterResult.code());
        Assert.assertEquals(TABLE, interpreterResult.message().get(0).getType());
        Assert.assertEquals(1, interpreterResult.message().size());
        Assert.assertEquals("ID\tNAME\n", interpreterResult.message().get(0).getData());
        resourcePool.put("itemId", "key");
        // 
        // 1 result expected because z-variable 'item' is 'key' ...
        // 
        sqlQuery = "select * from test_table where id = '{itemId}'";
        interpreterResult = t.interpret(sqlQuery, interpreterContext);
        Assert.assertEquals(SUCCESS, interpreterResult.code());
        Assert.assertEquals(TABLE, interpreterResult.message().get(0).getType());
        Assert.assertEquals(1, interpreterResult.message().size());
        Assert.assertEquals("ID\tNAME\nkey\tkeyboard\n", interpreterResult.message().get(0).getData());
    }

    @Test
    public void testEscapedInterpolationPattern() throws IOException, InterpreterException {
        Properties properties = new Properties();
        properties.setProperty("common.max_count", "1000");
        properties.setProperty("common.max_retry", "3");
        properties.setProperty("default.driver", "org.h2.Driver");
        properties.setProperty("default.url", getJdbcConnection());
        properties.setProperty("default.user", "");
        properties.setProperty("default.password", "");
        properties.setProperty("zeppelin.jdbc.interpolation", "true");
        JDBCInterpreter t = new JDBCInterpreter(properties);
        t.open();
        // 
        // 2 rows (keyboard and mouse) expected when searching names with 2 consecutive vowels ...
        // The 'regexp' keyword is specific to H2 database
        // 
        String sqlQuery = "select * from test_table where name regexp '[aeiou]{{2}}'";
        InterpreterResult interpreterResult = t.interpret(sqlQuery, interpreterContext);
        Assert.assertEquals(SUCCESS, interpreterResult.code());
        Assert.assertEquals(TABLE, interpreterResult.message().get(0).getType());
        Assert.assertEquals(1, interpreterResult.message().size());
        Assert.assertEquals("ID\tNAME\nkey\tkeyboard\nmou\tmouse\n", interpreterResult.message().get(0).getData());
    }
}

