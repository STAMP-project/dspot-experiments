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
package org.apache.hadoop.tools;


import java.io.ByteArrayOutputStream;
import org.apache.hadoop.conf.Configuration;
import org.junit.Assert;
import org.junit.Test;


public class TestCommandShell {
    public class Example extends CommandShell {
        public static final String EXAMPLE = "example";

        public static final String HELLO = "hello";

        public static final String HELLO_MSG = "hello is running";

        public static final String GOODBYE = "goodbye";

        public static final String GOODBYE_MSG = "goodbye is running";

        public String[] savedArgs = null;

        @Override
        protected int init(String[] args) throws Exception {
            String command = args[0];
            if (command.equals(TestCommandShell.Example.HELLO)) {
                setSubCommand(new TestCommandShell.Example.Hello());
            } else
                if (command.equals(TestCommandShell.Example.GOODBYE)) {
                    setSubCommand(new TestCommandShell.Example.Goodbye());
                } else {
                    return 1;
                }

            savedArgs = args;
            return 0;
        }

        public String getCommandUsage() {
            return TestCommandShell.Example.EXAMPLE;
        }

        public class Hello extends SubCommand {
            public static final String HELLO_USAGE = (TestCommandShell.Example.EXAMPLE) + " hello";

            @Override
            public boolean validate() {
                return (savedArgs.length) == 1;
            }

            @Override
            public void execute() throws Exception {
                System.out.println(TestCommandShell.Example.HELLO_MSG);
            }

            @Override
            public String getUsage() {
                return TestCommandShell.Example.Hello.HELLO_USAGE;
            }
        }

        public class Goodbye extends SubCommand {
            public static final String GOODBYE_USAGE = (TestCommandShell.Example.EXAMPLE) + " goodbye";

            @Override
            public void execute() throws Exception {
                System.out.println(TestCommandShell.Example.GOODBYE_MSG);
            }

            @Override
            public String getUsage() {
                return TestCommandShell.Example.Goodbye.GOODBYE_USAGE;
            }
        }
    }

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @Test
    public void testCommandShellExample() throws Exception {
        TestCommandShell.Example ex = new TestCommandShell.Example();
        ex.setConf(new Configuration());
        int rc = 0;
        outContent.reset();
        String[] args1 = new String[]{ "hello" };
        rc = run(args1);
        Assert.assertEquals(outMsg("test exit code - normal hello"), 0, rc);
        Assert.assertTrue(outMsg("test normal hello message"), outContent.toString().contains(TestCommandShell.Example.HELLO_MSG));
        outContent.reset();
        String[] args2 = new String[]{ "hello", "x" };
        rc = run(args2);
        Assert.assertEquals(outMsg("test exit code - bad hello"), 1, rc);
        Assert.assertTrue(outMsg("test bad hello message"), outContent.toString().contains(TestCommandShell.Example.Hello.HELLO_USAGE));
        outContent.reset();
        String[] args3 = new String[]{ "goodbye" };
        rc = run(args3);
        Assert.assertEquals(outMsg("test exit code - normal goodbye"), 0, rc);
        Assert.assertTrue(outMsg("test normal goodbye message"), outContent.toString().contains(TestCommandShell.Example.GOODBYE_MSG));
    }
}

