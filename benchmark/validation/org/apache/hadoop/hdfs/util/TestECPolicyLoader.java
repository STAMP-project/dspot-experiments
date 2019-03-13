/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs.util;


import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;
import org.apache.hadoop.hdfs.protocol.ErasureCodingPolicy;
import org.apache.hadoop.io.erasurecode.ECSchema;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test load EC policy file.
 */
public class TestECPolicyLoader {
    private static final String TEST_DIR = new File(System.getProperty("test.build.data", "/tmp")).getAbsolutePath();

    private static final String POLICY_FILE = new File(TestECPolicyLoader.TEST_DIR, "test-ecpolicy").getAbsolutePath();

    /**
     * Test load EC policy.
     */
    @Test
    public void testLoadECPolicy() throws Exception {
        PrintWriter out = new PrintWriter(new FileWriter(TestECPolicyLoader.POLICY_FILE));
        out.println("<?xml version=\"1.0\"?>");
        out.println("<configuration>");
        out.println("<layoutversion>1</layoutversion>");
        out.println("<schemas>");
        out.println("  <schema id=\"RSk12m4\">");
        out.println("    <codec>RS</codec>");
        out.println("    <k>12</k>");
        out.println("    <m>4</m>");
        out.println("  </schema>");
        out.println("  <schema id=\"RS-legacyk12m4\">");
        out.println("    <codec>RS-legacy</codec>");
        out.println("    <k>12</k>");
        out.println("    <m>4</m>");
        out.println("  </schema>");
        out.println("</schemas>");
        out.println("<policies>");
        out.println("  <policy>");
        out.println("    <schema>RSk12m4</schema>");
        out.println("    <cellsize>131072</cellsize>");
        out.println("  </policy>");
        out.println("  <policy>");
        out.println("    <schema>RS-legacyk12m4</schema>");
        out.println("    <cellsize>262144</cellsize>");
        out.println("  </policy>");
        out.println("</policies>");
        out.println("</configuration>");
        out.close();
        ECPolicyLoader ecPolicyLoader = new ECPolicyLoader();
        List<ErasureCodingPolicy> policies = ecPolicyLoader.loadPolicy(TestECPolicyLoader.POLICY_FILE);
        Assert.assertEquals(2, policies.size());
        ErasureCodingPolicy policy1 = policies.get(0);
        ECSchema schema1 = policy1.getSchema();
        Assert.assertEquals(131072, policy1.getCellSize());
        Assert.assertEquals(0, schema1.getExtraOptions().size());
        Assert.assertEquals(12, schema1.getNumDataUnits());
        Assert.assertEquals(4, schema1.getNumParityUnits());
        Assert.assertEquals("RS", schema1.getCodecName());
        ErasureCodingPolicy policy2 = policies.get(1);
        ECSchema schema2 = policy2.getSchema();
        Assert.assertEquals(262144, policy2.getCellSize());
        Assert.assertEquals(0, schema2.getExtraOptions().size());
        Assert.assertEquals(12, schema2.getNumDataUnits());
        Assert.assertEquals(4, schema2.getNumParityUnits());
        Assert.assertEquals("RS-legacy", schema2.getCodecName());
    }

    /**
     * Test load null EC schema option.
     */
    @Test
    public void testNullECSchemaOptionValue() throws Exception {
        PrintWriter out = new PrintWriter(new FileWriter(TestECPolicyLoader.POLICY_FILE));
        out.println("<?xml version=\"1.0\"?>");
        out.println("<configuration>");
        out.println("<layoutversion>1</layoutversion>");
        out.println("<schemas>");
        out.println("  <schema id=\"RSk12m4\">");
        out.println("    <codec>RS</codec>");
        out.println("    <k>12</k>");
        out.println("    <m>4</m>");
        out.println("  </schema>");
        out.println("  <schema id=\"RS-legacyk12m4\">");
        out.println("    <codec>RS-legacy</codec>");
        out.println("    <k>12</k>");
        out.println("    <m>4</m>");
        out.println("    <option></option>");
        out.println("  </schema>");
        out.println("</schemas>");
        out.println("<policies>");
        out.println("  <policy>");
        out.println("    <schema>RS-legacyk12m4</schema>");
        out.println("    <cellsize>1024</cellsize>");
        out.println("  </policy>");
        out.println("  <policy>");
        out.println("    <schema>RSk12m4</schema>");
        out.println("    <cellsize>20480</cellsize>");
        out.println("  </policy>");
        out.println("</policies>");
        out.println("</configuration>");
        out.close();
        ECPolicyLoader ecPolicyLoader = new ECPolicyLoader();
        try {
            ecPolicyLoader.loadPolicy(TestECPolicyLoader.POLICY_FILE);
            Assert.fail("IllegalArgumentException should be thrown for null value");
        } catch (IllegalArgumentException e) {
            assertExceptionContains("Value of <option> is null", e);
        }
    }

    /**
     * Test load repetitive EC schema.
     */
    @Test
    public void testRepeatECSchema() throws Exception {
        PrintWriter out = new PrintWriter(new FileWriter(TestECPolicyLoader.POLICY_FILE));
        out.println("<?xml version=\"1.0\"?>");
        out.println("<configuration>");
        out.println("<layoutversion>1</layoutversion>");
        out.println("<schemas>");
        out.println("  <schema id=\"RSk12m4\">");
        out.println("    <codec>RS-legacy</codec>");
        out.println("    <k>12</k>");
        out.println("    <m>4</m>");
        out.println("  </schema>");
        out.println("  <schema id=\"RS-legacyk12m4\">");
        out.println("    <codec>RS-legacy</codec>");
        out.println("    <k>12</k>");
        out.println("    <m>4</m>");
        out.println("  </schema>");
        out.println("</schemas>");
        out.println("<policies>");
        out.println("  <policy>");
        out.println("    <schema>RS-legacyk12m4</schema>");
        out.println("    <cellsize>1024</cellsize>");
        out.println("  </policy>");
        out.println("  <policy>");
        out.println("    <schema>RSk12m4</schema>");
        out.println("    <cellsize>20480</cellsize>");
        out.println("  </policy>");
        out.println("</policies>");
        out.println("</configuration>");
        out.close();
        ECPolicyLoader ecPolicyLoader = new ECPolicyLoader();
        try {
            ecPolicyLoader.loadPolicy(TestECPolicyLoader.POLICY_FILE);
            Assert.fail("RuntimeException should be thrown for repetitive elements");
        } catch (RuntimeException e) {
            assertExceptionContains(("Repetitive schemas in EC policy" + " configuration file: RS-legacyk12m4"), e);
        }
    }

    /**
     * Test load bad EC policy layoutversion.
     */
    @Test
    public void testBadECLayoutVersion() throws Exception {
        PrintWriter out = new PrintWriter(new FileWriter(TestECPolicyLoader.POLICY_FILE));
        out.println("<?xml version=\"1.0\"?>");
        out.println("<configuration>");
        out.println("<layoutversion>3</layoutversion>");
        out.println("<schemas>");
        out.println("  <schema id=\"RSk12m4\">");
        out.println("    <codec>RS</codec>");
        out.println("    <k>12</k>");
        out.println("    <m>4</m>");
        out.println("  </schema>");
        out.println("  <schema id=\"RS-legacyk12m4\">");
        out.println("    <codec>RS-legacy</codec>");
        out.println("    <k>12</k>");
        out.println("    <m>4</m>");
        out.println("  </schema>");
        out.println("</schemas>");
        out.println("<policies>");
        out.println("  <policy>");
        out.println("    <schema>RSk12m4</schema>");
        out.println("    <cellsize>1024</cellsize>");
        out.println("  </policy>");
        out.println("</policies>");
        out.println("</configuration>");
        out.close();
        ECPolicyLoader ecPolicyLoader = new ECPolicyLoader();
        try {
            ecPolicyLoader.loadPolicy(TestECPolicyLoader.POLICY_FILE);
            Assert.fail("RuntimeException should be thrown for bad layoutversion");
        } catch (RuntimeException e) {
            assertExceptionContains(("The parse failed because of " + "bad layoutversion value"), e);
        }
    }

    /**
     * Test load bad EC policy cellsize.
     */
    @Test
    public void testBadECCellsize() throws Exception {
        PrintWriter out = new PrintWriter(new FileWriter(TestECPolicyLoader.POLICY_FILE));
        out.println("<?xml version=\"1.0\"?>");
        out.println("<configuration>");
        out.println("<layoutversion>1</layoutversion>");
        out.println("<schemas>");
        out.println("  <schema id=\"RSk12m4\">");
        out.println("    <codec>RS</codec>");
        out.println("    <k>12</k>");
        out.println("    <m>4</m>");
        out.println("  </schema>");
        out.println("  <schema id=\"RS-legacyk12m4\">");
        out.println("    <codec>RS-legacy</codec>");
        out.println("    <k>12</k>");
        out.println("    <m>4</m>");
        out.println("  </schema>");
        out.println("</schemas>");
        out.println("<policies>");
        out.println("  <policy>");
        out.println("    <schema>RSk12m4</schema>");
        out.println("    <cellsize>free</cellsize>");
        out.println("  </policy>");
        out.println("</policies>");
        out.println("</configuration>");
        out.close();
        ECPolicyLoader ecPolicyLoader = new ECPolicyLoader();
        try {
            ecPolicyLoader.loadPolicy(TestECPolicyLoader.POLICY_FILE);
            Assert.fail("IllegalArgumentException should be thrown for bad policy");
        } catch (IllegalArgumentException e) {
            assertExceptionContains(("Bad EC policy cellsize value free is found." + " It should be an integer"), e);
        }
    }

    /**
     * Test load bad EC policy.
     */
    @Test
    public void testBadECPolicy() throws Exception {
        PrintWriter out = new PrintWriter(new FileWriter(TestECPolicyLoader.POLICY_FILE));
        out.println("<?xml version=\"1.0\"?>");
        out.println("<configuration>");
        out.println("<layoutversion>1</layoutversion>");
        out.println("<schemas>");
        out.println("  <schema id=\"RSk12m4\">");
        out.println("    <codec>RS</codec>");
        out.println("    <k>12</k>");
        out.println("    <m>4</m>");
        out.println("  </schema>");
        out.println("  <schema id=\"RS-legacyk12m4\">");
        out.println("    <codec>RS-legacy</codec>");
        out.println("    <k>12</k>");
        out.println("    <m>4</m>");
        out.println("  </schema>");
        out.println("</schemas>");
        out.println("<policies>");
        out.println("  <policy>");
        out.println("    <schema>RSk12m4</schema>");
        out.println("    <cellsize>-1025</cellsize>");
        out.println("  </policy>");
        out.println("</policies>");
        out.println("</configuration>");
        out.close();
        ECPolicyLoader ecPolicyLoader = new ECPolicyLoader();
        try {
            ecPolicyLoader.loadPolicy(TestECPolicyLoader.POLICY_FILE);
            Assert.fail("RuntimeException should be thrown for bad policy");
        } catch (RuntimeException e) {
            assertExceptionContains(("Bad policy is found in EC policy" + " configuration file"), e);
        }
    }
}

