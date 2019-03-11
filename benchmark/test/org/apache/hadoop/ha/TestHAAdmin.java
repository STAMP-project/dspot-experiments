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
package org.apache.hadoop.ha;


import java.io.ByteArrayOutputStream;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestHAAdmin {
    private static final Logger LOG = LoggerFactory.getLogger(TestHAAdmin.class);

    private HAAdmin tool;

    private ByteArrayOutputStream errOutBytes = new ByteArrayOutputStream();

    private ByteArrayOutputStream outBytes = new ByteArrayOutputStream();

    private String errOutput;

    private String output;

    @Test
    public void testAdminUsage() throws Exception {
        Assert.assertEquals((-1), runTool());
        assertOutputContains("Usage:");
        assertOutputContains("-transitionToActive");
        Assert.assertEquals((-1), runTool("badCommand"));
        assertOutputContains("Bad command 'badCommand'");
        Assert.assertEquals((-1), runTool("-badCommand"));
        assertOutputContains("badCommand: Unknown");
        // valid command but not enough arguments
        Assert.assertEquals((-1), runTool("-transitionToActive"));
        assertOutputContains("transitionToActive: incorrect number of arguments");
        Assert.assertEquals((-1), runTool("-transitionToActive", "x", "y"));
        assertOutputContains("transitionToActive: incorrect number of arguments");
        Assert.assertEquals((-1), runTool("-failover"));
        assertOutputContains("failover: incorrect arguments");
        assertOutputContains("failover: incorrect arguments");
        Assert.assertEquals((-1), runTool("-failover", "foo:1234"));
        assertOutputContains("failover: incorrect arguments");
    }

    @Test
    public void testHelp() throws Exception {
        Assert.assertEquals(0, runTool("-help"));
        Assert.assertEquals(0, runTool("-help", "transitionToActive"));
        assertOutputContains("Transitions the service into Active");
    }
}

