/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zeppelin.integration;


import AuthenticationInfo.ANONYMOUS;
import InterpreterResult.Code.SUCCESS;
import com.google.common.collect.Lists;
import org.apache.zeppelin.dep.Dependency;
import org.apache.zeppelin.interpreter.Interpreter;
import org.apache.zeppelin.interpreter.InterpreterContext;
import org.apache.zeppelin.interpreter.InterpreterException;
import org.apache.zeppelin.interpreter.InterpreterFactory;
import org.apache.zeppelin.interpreter.InterpreterResult;
import org.apache.zeppelin.interpreter.InterpreterSetting;
import org.apache.zeppelin.interpreter.InterpreterSettingManager;
import org.junit.Assert;
import org.junit.Test;


public class JdbcIntegrationTest {
    private static MiniZeppelin zeppelin;

    private static InterpreterFactory interpreterFactory;

    private static InterpreterSettingManager interpreterSettingManager;

    @Test
    public void testMySql() throws InterruptedException, InterpreterException {
        InterpreterSetting interpreterSetting = JdbcIntegrationTest.interpreterSettingManager.getInterpreterSettingByName("jdbc");
        interpreterSetting.setProperty("default.driver", "com.mysql.jdbc.Driver");
        interpreterSetting.setProperty("default.url", "jdbc:mysql://localhost:3306/");
        interpreterSetting.setProperty("default.user", "root");
        Dependency dependency = new Dependency("mysql:mysql-connector-java:5.1.46");
        interpreterSetting.setDependencies(Lists.newArrayList(dependency));
        JdbcIntegrationTest.interpreterSettingManager.restart(interpreterSetting.getId());
        interpreterSetting.waitForReady((60 * 1000));
        Interpreter jdbcInterpreter = JdbcIntegrationTest.interpreterFactory.getInterpreter("user1", "note1", "jdbc", "test");
        Assert.assertNotNull("JdbcInterpreter is null", jdbcInterpreter);
        InterpreterContext context = new InterpreterContext.Builder().setNoteId("note1").setParagraphId("paragraph_1").setAuthenticationInfo(ANONYMOUS).build();
        InterpreterResult interpreterResult = jdbcInterpreter.interpret("show databases;", context);
        Assert.assertEquals(SUCCESS, interpreterResult.code());
    }
}

