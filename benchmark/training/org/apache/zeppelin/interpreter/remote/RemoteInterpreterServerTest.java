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
package org.apache.zeppelin.interpreter.remote;


import InterpreterResult.Code;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.thrift.TException;
import org.apache.zeppelin.interpreter.Interpreter;
import org.apache.zeppelin.interpreter.InterpreterContext;
import org.apache.zeppelin.interpreter.InterpreterException;
import org.apache.zeppelin.interpreter.InterpreterResult;
import org.apache.zeppelin.interpreter.thrift.RemoteInterpreterContext;
import org.apache.zeppelin.interpreter.thrift.RemoteInterpreterResult;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static FormType.NATIVE;


public class RemoteInterpreterServerTest {
    @Test
    public void testStartStop() throws IOException, InterruptedException, TException {
        RemoteInterpreterServer server = new RemoteInterpreterServer("localhost", RemoteInterpreterUtils.findRandomAvailablePortOnAllLocalInterfaces(), ":", "groupId", true);
        startRemoteInterpreterServer(server, (10 * 1000));
        stopRemoteInterpreterServer(server, (10 * 10000));
    }

    @Test
    public void testStartStopWithQueuedEvents() throws IOException, InterruptedException, TException {
        RemoteInterpreterServer server = new RemoteInterpreterServer("localhost", RemoteInterpreterUtils.findRandomAvailablePortOnAllLocalInterfaces(), ":", "groupId", true);
        server.intpEventClient = Mockito.mock(RemoteInterpreterEventClient.class);
        startRemoteInterpreterServer(server, (10 * 1000));
        server.intpEventClient.onAppStatusUpdate("", "", "", "");
        stopRemoteInterpreterServer(server, (10 * 10000));
    }

    @Test
    public void testInterpreter() throws IOException, InterruptedException, TException {
        final RemoteInterpreterServer server = new RemoteInterpreterServer("localhost", RemoteInterpreterUtils.findRandomAvailablePortOnAllLocalInterfaces(), ":", "groupId", true);
        server.intpEventClient = Mockito.mock(RemoteInterpreterEventClient.class);
        Map<String, String> intpProperties = new HashMap<>();
        intpProperties.put("property_1", "value_1");
        intpProperties.put("zeppelin.interpreter.localRepo", "/tmp");
        // create Test1Interpreter in session_1
        server.createInterpreter("group_1", "session_1", RemoteInterpreterServerTest.Test1Interpreter.class.getName(), intpProperties, "user_1");
        RemoteInterpreterServerTest.Test1Interpreter interpreter1 = ((RemoteInterpreterServerTest.Test1Interpreter) (getInnerInterpreter()));
        Assert.assertEquals(1, server.getInterpreterGroup().getSessionNum());
        Assert.assertEquals(1, server.getInterpreterGroup().get("session_1").size());
        Assert.assertEquals(2, getProperties().size());
        Assert.assertEquals("value_1", getProperty("property_1"));
        // create Test2Interpreter in session_1
        server.createInterpreter("group_1", "session_1", RemoteInterpreterServerTest.Test1Interpreter.class.getName(), intpProperties, "user_1");
        Assert.assertEquals(2, server.getInterpreterGroup().get("session_1").size());
        // create Test1Interpreter in session_2
        server.createInterpreter("group_1", "session_2", RemoteInterpreterServerTest.Test1Interpreter.class.getName(), intpProperties, "user_1");
        Assert.assertEquals(2, server.getInterpreterGroup().getSessionNum());
        Assert.assertEquals(2, server.getInterpreterGroup().get("session_1").size());
        Assert.assertEquals(1, server.getInterpreterGroup().get("session_2").size());
        final RemoteInterpreterContext intpContext = new RemoteInterpreterContext();
        intpContext.setNoteId("note_1");
        intpContext.setParagraphId("paragraph_1");
        intpContext.setGui("{}");
        intpContext.setNoteGui("{}");
        intpContext.setLocalProperties(new HashMap());
        // single output of SUCCESS
        RemoteInterpreterResult result = server.interpret("session_1", RemoteInterpreterServerTest.Test1Interpreter.class.getName(), "SINGLE_OUTPUT_SUCCESS", intpContext);
        Assert.assertEquals("SUCCESS", result.code);
        Assert.assertEquals(1, result.getMsg().size());
        Assert.assertEquals("SINGLE_OUTPUT_SUCCESS", result.getMsg().get(0).getData());
        // combo output of SUCCESS
        result = server.interpret("session_1", RemoteInterpreterServerTest.Test1Interpreter.class.getName(), "COMBO_OUTPUT_SUCCESS", intpContext);
        Assert.assertEquals("SUCCESS", result.code);
        Assert.assertEquals(2, result.getMsg().size());
        Assert.assertEquals("INTERPRETER_OUT", result.getMsg().get(0).getData());
        Assert.assertEquals("SINGLE_OUTPUT_SUCCESS", result.getMsg().get(1).getData());
        // single output of ERROR
        result = server.interpret("session_1", RemoteInterpreterServerTest.Test1Interpreter.class.getName(), "SINGLE_OUTPUT_ERROR", intpContext);
        Assert.assertEquals("ERROR", result.code);
        Assert.assertEquals(1, result.getMsg().size());
        Assert.assertEquals("SINGLE_OUTPUT_ERROR", result.getMsg().get(0).getData());
        // getFormType
        String formType = server.getFormType("session_1", RemoteInterpreterServerTest.Test1Interpreter.class.getName());
        Assert.assertEquals("NATIVE", formType);
        // cancel
        Thread sleepThread = new Thread() {
            @Override
            public void run() {
                try {
                    server.interpret("session_1", RemoteInterpreterServerTest.Test1Interpreter.class.getName(), "SLEEP", intpContext);
                } catch (TException e) {
                    e.printStackTrace();
                }
            }
        };
        sleepThread.start();
        Thread.sleep(1000);
        Assert.assertFalse(interpreter1.cancelled.get());
        server.cancel("session_1", RemoteInterpreterServerTest.Test1Interpreter.class.getName(), intpContext);
        Assert.assertTrue(interpreter1.cancelled.get());
        // getProgress
        Assert.assertEquals(10, server.getProgress("session_1", RemoteInterpreterServerTest.Test1Interpreter.class.getName(), intpContext));
        // close
        server.close("session_1", RemoteInterpreterServerTest.Test1Interpreter.class.getName());
        Assert.assertTrue(interpreter1.closed.get());
    }

    public static class Test1Interpreter extends Interpreter {
        AtomicBoolean cancelled = new AtomicBoolean();

        AtomicBoolean closed = new AtomicBoolean();

        public Test1Interpreter(Properties properties) {
            super(properties);
        }

        @Override
        public void open() {
        }

        @Override
        public InterpreterResult interpret(String st, InterpreterContext context) {
            if (st.equals("SINGLE_OUTPUT_SUCCESS")) {
                return new InterpreterResult(Code.SUCCESS, "SINGLE_OUTPUT_SUCCESS");
            } else
                if (st.equals("SINGLE_OUTPUT_ERROR")) {
                    return new InterpreterResult(Code.ERROR, "SINGLE_OUTPUT_ERROR");
                } else
                    if (st.equals("COMBO_OUTPUT_SUCCESS")) {
                        try {
                            context.out.write("INTERPRETER_OUT");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return new InterpreterResult(Code.SUCCESS, "SINGLE_OUTPUT_SUCCESS");
                    } else
                        if (st.equals("SLEEP")) {
                            try {
                                Thread.sleep((3 * 1000));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            return new InterpreterResult(Code.SUCCESS, "SLEEP_SUCCESS");
                        }



            return null;
        }

        @Override
        public void cancel(InterpreterContext context) throws InterpreterException {
            cancelled.set(true);
        }

        @Override
        public FormType getFormType() throws InterpreterException {
            return NATIVE;
        }

        @Override
        public int getProgress(InterpreterContext context) throws InterpreterException {
            return 10;
        }

        @Override
        public void close() {
            closed.set(true);
        }
    }

    public static class Test2Interpreter extends Interpreter {
        public Test2Interpreter(Properties properties) {
            super(properties);
        }

        @Override
        public void open() {
        }

        @Override
        public InterpreterResult interpret(String st, InterpreterContext context) {
            return null;
        }

        @Override
        public void cancel(InterpreterContext context) throws InterpreterException {
        }

        @Override
        public FormType getFormType() throws InterpreterException {
            return FormType.NATIVE;
        }

        @Override
        public int getProgress(InterpreterContext context) throws InterpreterException {
            return 0;
        }

        @Override
        public void close() {
        }
    }
}

