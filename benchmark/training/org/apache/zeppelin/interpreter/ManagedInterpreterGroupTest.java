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


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ManagedInterpreterGroupTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ManagedInterpreterGroupTest.class);

    private InterpreterSetting interpreterSetting;

    @Test
    public void testInterpreterGroup() {
        ManagedInterpreterGroup interpreterGroup = new ManagedInterpreterGroup("group_1", interpreterSetting);
        Assert.assertEquals(0, interpreterGroup.getSessionNum());
        // create session_1
        List<Interpreter> interpreters = interpreterGroup.getOrCreateSession("user1", "session_1");
        Assert.assertEquals(3, interpreters.size());
        Assert.assertEquals(EchoInterpreter.class.getName(), interpreters.get(0).getClassName());
        Assert.assertEquals(DoubleEchoInterpreter.class.getName(), interpreters.get(1).getClassName());
        Assert.assertEquals(1, interpreterGroup.getSessionNum());
        // get the same interpreters when interpreterGroup.getOrCreateSession is invoked again
        Assert.assertEquals(interpreters, interpreterGroup.getOrCreateSession("user1", "session_1"));
        Assert.assertEquals(1, interpreterGroup.getSessionNum());
        // create session_2
        List<Interpreter> interpreters2 = interpreterGroup.getOrCreateSession("user1", "session_2");
        Assert.assertEquals(3, interpreters2.size());
        Assert.assertEquals(EchoInterpreter.class.getName(), interpreters2.get(0).getClassName());
        Assert.assertEquals(DoubleEchoInterpreter.class.getName(), interpreters2.get(1).getClassName());
        Assert.assertEquals(2, interpreterGroup.getSessionNum());
        // close session_1
        interpreterGroup.close("session_1");
        Assert.assertEquals(1, interpreterGroup.getSessionNum());
        // close InterpreterGroup
        interpreterGroup.close();
        Assert.assertEquals(0, interpreterGroup.getSessionNum());
    }
}

