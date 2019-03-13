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
package org.apache.hadoop.fs;


import java.util.List;
import java.util.Set;
import org.apache.hadoop.fs.shell.CommandFormat;
import org.apache.hadoop.fs.shell.CommandFormat.NotEnoughArgumentsException;
import org.apache.hadoop.fs.shell.CommandFormat.TooManyArgumentsException;
import org.apache.hadoop.fs.shell.CommandFormat.UnknownOptionException;
import org.junit.Assert;
import org.junit.Test;


/**
 * This class tests the command line parsing
 */
public class TestCommandFormat {
    private static List<String> args;

    private static List<String> expectedArgs;

    private static Set<String> expectedOpts;

    @Test
    public void testNoArgs() {
        TestCommandFormat.checkArgLimits(null, 0, 0);
        TestCommandFormat.checkArgLimits(null, 0, 1);
        TestCommandFormat.checkArgLimits(NotEnoughArgumentsException.class, 1, 1);
        TestCommandFormat.checkArgLimits(NotEnoughArgumentsException.class, 1, 2);
    }

    @Test
    public void testOneArg() {
        TestCommandFormat.args = TestCommandFormat.listOf("a");
        TestCommandFormat.expectedArgs = TestCommandFormat.listOf("a");
        TestCommandFormat.checkArgLimits(TooManyArgumentsException.class, 0, 0);
        TestCommandFormat.checkArgLimits(null, 0, 1);
        TestCommandFormat.checkArgLimits(null, 1, 1);
        TestCommandFormat.checkArgLimits(null, 1, 2);
        TestCommandFormat.checkArgLimits(NotEnoughArgumentsException.class, 2, 3);
    }

    @Test
    public void testTwoArgs() {
        TestCommandFormat.args = TestCommandFormat.listOf("a", "b");
        TestCommandFormat.expectedArgs = TestCommandFormat.listOf("a", "b");
        TestCommandFormat.checkArgLimits(TooManyArgumentsException.class, 0, 0);
        TestCommandFormat.checkArgLimits(TooManyArgumentsException.class, 1, 1);
        TestCommandFormat.checkArgLimits(null, 1, 2);
        TestCommandFormat.checkArgLimits(null, 2, 2);
        TestCommandFormat.checkArgLimits(null, 2, 3);
        TestCommandFormat.checkArgLimits(NotEnoughArgumentsException.class, 3, 3);
    }

    @Test
    public void testOneOpt() {
        TestCommandFormat.args = TestCommandFormat.listOf("-a");
        TestCommandFormat.expectedOpts = TestCommandFormat.setOf("a");
        TestCommandFormat.checkArgLimits(UnknownOptionException.class, 0, 0);
        TestCommandFormat.checkArgLimits(null, 0, 0, "a", "b");
        TestCommandFormat.checkArgLimits(NotEnoughArgumentsException.class, 1, 1, "a", "b");
    }

    @Test
    public void testTwoOpts() {
        TestCommandFormat.args = TestCommandFormat.listOf("-a", "-b");
        TestCommandFormat.expectedOpts = TestCommandFormat.setOf("a", "b");
        TestCommandFormat.checkArgLimits(UnknownOptionException.class, 0, 0);
        TestCommandFormat.checkArgLimits(null, 0, 0, "a", "b");
        TestCommandFormat.checkArgLimits(null, 0, 1, "a", "b");
        TestCommandFormat.checkArgLimits(NotEnoughArgumentsException.class, 1, 1, "a", "b");
    }

    @Test
    public void testOptArg() {
        TestCommandFormat.args = TestCommandFormat.listOf("-a", "b");
        TestCommandFormat.expectedOpts = TestCommandFormat.setOf("a");
        TestCommandFormat.expectedArgs = TestCommandFormat.listOf("b");
        TestCommandFormat.checkArgLimits(UnknownOptionException.class, 0, 0);
        TestCommandFormat.checkArgLimits(TooManyArgumentsException.class, 0, 0, "a", "b");
        TestCommandFormat.checkArgLimits(null, 0, 1, "a", "b");
        TestCommandFormat.checkArgLimits(null, 1, 1, "a", "b");
        TestCommandFormat.checkArgLimits(null, 1, 2, "a", "b");
        TestCommandFormat.checkArgLimits(NotEnoughArgumentsException.class, 2, 2, "a", "b");
    }

    @Test
    public void testArgOpt() {
        TestCommandFormat.args = TestCommandFormat.listOf("b", "-a");
        TestCommandFormat.expectedArgs = TestCommandFormat.listOf("b", "-a");
        TestCommandFormat.checkArgLimits(TooManyArgumentsException.class, 0, 0, "a", "b");
        TestCommandFormat.checkArgLimits(null, 1, 2, "a", "b");
        TestCommandFormat.checkArgLimits(null, 2, 2, "a", "b");
        TestCommandFormat.checkArgLimits(NotEnoughArgumentsException.class, 3, 4, "a", "b");
    }

    @Test
    public void testOptStopOptArg() {
        TestCommandFormat.args = TestCommandFormat.listOf("-a", "--", "-b", "c");
        TestCommandFormat.expectedOpts = TestCommandFormat.setOf("a");
        TestCommandFormat.expectedArgs = TestCommandFormat.listOf("-b", "c");
        TestCommandFormat.checkArgLimits(UnknownOptionException.class, 0, 0);
        TestCommandFormat.checkArgLimits(TooManyArgumentsException.class, 0, 1, "a", "b");
        TestCommandFormat.checkArgLimits(null, 2, 2, "a", "b");
        TestCommandFormat.checkArgLimits(NotEnoughArgumentsException.class, 3, 4, "a", "b");
    }

    @Test
    public void testOptDashArg() {
        TestCommandFormat.args = TestCommandFormat.listOf("-b", "-", "-c");
        TestCommandFormat.expectedOpts = TestCommandFormat.setOf("b");
        TestCommandFormat.expectedArgs = TestCommandFormat.listOf("-", "-c");
        TestCommandFormat.checkArgLimits(UnknownOptionException.class, 0, 0);
        TestCommandFormat.checkArgLimits(TooManyArgumentsException.class, 0, 0, "b", "c");
        TestCommandFormat.checkArgLimits(TooManyArgumentsException.class, 1, 1, "b", "c");
        TestCommandFormat.checkArgLimits(null, 2, 2, "b", "c");
        TestCommandFormat.checkArgLimits(NotEnoughArgumentsException.class, 3, 4, "b", "c");
    }

    @Test
    public void testOldArgsWithIndex() {
        String[] arrayArgs = new String[]{ "ignore", "-a", "b", "-c" };
        {
            CommandFormat cf = new CommandFormat(0, 9, "a", "c");
            List<String> parsedArgs = cf.parse(arrayArgs, 0);
            Assert.assertEquals(TestCommandFormat.setOf(), cf.getOpts());
            Assert.assertEquals(TestCommandFormat.listOf("ignore", "-a", "b", "-c"), parsedArgs);
        }
        {
            CommandFormat cf = new CommandFormat(0, 9, "a", "c");
            List<String> parsedArgs = cf.parse(arrayArgs, 1);
            Assert.assertEquals(TestCommandFormat.setOf("a"), cf.getOpts());
            Assert.assertEquals(TestCommandFormat.listOf("b", "-c"), parsedArgs);
        }
        {
            CommandFormat cf = new CommandFormat(0, 9, "a", "c");
            List<String> parsedArgs = cf.parse(arrayArgs, 2);
            Assert.assertEquals(TestCommandFormat.setOf(), cf.getOpts());
            Assert.assertEquals(TestCommandFormat.listOf("b", "-c"), parsedArgs);
        }
    }
}

