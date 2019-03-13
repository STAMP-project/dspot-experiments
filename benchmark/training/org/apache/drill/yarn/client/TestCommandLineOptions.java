/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.yarn.client;


import CommandLineOptions.Command.HELP;
import CommandLineOptions.Command.RESIZE;
import CommandLineOptions.Command.START;
import CommandLineOptions.Command.STATUS;
import CommandLineOptions.Command.STOP;
import org.junit.Assert;
import org.junit.Test;


public class TestCommandLineOptions {
    @Test
    public void testOptions() {
        CommandLineOptions opts = new CommandLineOptions();
        opts.parse(new String[]{  });
        Assert.assertNull(opts.getCommand());
        opts = new CommandLineOptions();
        opts.parse(new String[]{ "-h" });
        Assert.assertEquals(HELP, opts.getCommand());
        opts = new CommandLineOptions();
        opts.parse(new String[]{ "-?" });
        Assert.assertEquals(HELP, opts.getCommand());
        opts = new CommandLineOptions();
        opts.parse(new String[]{ "help" });
        Assert.assertEquals(HELP, opts.getCommand());
        opts = new CommandLineOptions();
        opts.parse(new String[]{ "start" });
        Assert.assertEquals(START, opts.getCommand());
        opts = new CommandLineOptions();
        opts.parse(new String[]{ "stop" });
        Assert.assertEquals(STOP, opts.getCommand());
        opts = new CommandLineOptions();
        opts.parse(new String[]{ "status" });
        Assert.assertEquals(STATUS, opts.getCommand());
        opts = new CommandLineOptions();
        opts.parse(new String[]{ "resize" });
        Assert.assertNull(opts.getCommand());
        opts = new CommandLineOptions();
        opts.parse(new String[]{ "resize", "10" });
        Assert.assertEquals(RESIZE, opts.getCommand());
        Assert.assertEquals("", opts.getResizePrefix());
        Assert.assertEquals(10, opts.getResizeValue());
        opts = new CommandLineOptions();
        opts.parse(new String[]{ "resize", "+2" });
        Assert.assertEquals(RESIZE, opts.getCommand());
        Assert.assertEquals("+", opts.getResizePrefix());
        Assert.assertEquals(2, opts.getResizeValue());
        opts = new CommandLineOptions();
        opts.parse(new String[]{ "resize", "-3" });
        Assert.assertEquals(RESIZE, opts.getCommand());
        Assert.assertEquals("-", opts.getResizePrefix());
        Assert.assertEquals(3, opts.getResizeValue());
        opts = new CommandLineOptions();
        opts.parse(new String[]{ "myDrill" });
        Assert.assertNull(opts.getCommand());
    }
}

