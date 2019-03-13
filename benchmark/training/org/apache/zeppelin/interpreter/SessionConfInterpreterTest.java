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
package org.apache.zeppelin.interpreter;


import InterpreterResult.Code.ERROR;
import InterpreterResult.Code.SUCCESS;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.zeppelin.interpreter.remote.RemoteInterpreter;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class SessionConfInterpreterTest {
    @Test
    public void testUserSessionConfInterpreter() throws InterpreterException {
        InterpreterSetting mockInterpreterSetting = Mockito.mock(InterpreterSetting.class);
        ManagedInterpreterGroup mockInterpreterGroup = Mockito.mock(ManagedInterpreterGroup.class);
        Mockito.when(mockInterpreterSetting.getInterpreterGroup("group_1")).thenReturn(mockInterpreterGroup);
        Properties properties = new Properties();
        properties.setProperty("property_1", "value_1");
        properties.setProperty("property_2", "value_2");
        SessionConfInterpreter confInterpreter = new SessionConfInterpreter(properties, "session_1", "group_1", mockInterpreterSetting);
        RemoteInterpreter remoteInterpreter = new RemoteInterpreter(properties, "session_1", "clasName", "user1", null);
        List<Interpreter> interpreters = new ArrayList<>();
        interpreters.add(confInterpreter);
        interpreters.add(remoteInterpreter);
        Mockito.when(mockInterpreterGroup.get("session_1")).thenReturn(interpreters);
        InterpreterResult result = confInterpreter.interpret("property_1\tupdated_value_1\nproperty_3\tvalue_3", Mockito.mock(InterpreterContext.class));
        Assert.assertEquals(SUCCESS, result.code);
        Assert.assertEquals(3, remoteInterpreter.getProperties().size());
        Assert.assertEquals("updated_value_1", remoteInterpreter.getProperty("property_1"));
        Assert.assertEquals("value_2", remoteInterpreter.getProperty("property_2"));
        Assert.assertEquals("value_3", remoteInterpreter.getProperty("property_3"));
        remoteInterpreter.setOpened(true);
        result = confInterpreter.interpret("property_1\tupdated_value_1\nproperty_3\tvalue_3", Mockito.mock(InterpreterContext.class));
        Assert.assertEquals(ERROR, result.code);
    }
}

