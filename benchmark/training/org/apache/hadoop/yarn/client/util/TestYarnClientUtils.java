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
package org.apache.hadoop.yarn.client.util;


import YarnConfiguration.RM_ADDRESS;
import YarnConfiguration.RM_HA_ENABLED;
import YarnConfiguration.RM_HA_ID;
import YarnConfiguration.RM_HA_IDS;
import YarnConfiguration.RM_PRINCIPAL;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the YarnClientUtils class
 */
public class TestYarnClientUtils {
    /**
     * Test of getRMPrincipal(Configuration) method, of class YarnClientUtils
     * when HA is not enabled.
     *
     * @throws java.io.IOException
     * 		thrown if stuff breaks
     */
    @Test
    public void testGetRMPrincipalStandAlone_Configuration() throws IOException {
        Configuration conf = new Configuration();
        conf.set(RM_ADDRESS, "myhost");
        conf.setBoolean(RM_HA_ENABLED, false);
        String result = YarnClientUtils.getRmPrincipal(conf);
        Assert.assertNull((("The hostname translation did return null when the principal is " + "missing from the conf: ") + result), result);
        conf = new Configuration();
        conf.set(RM_ADDRESS, "myhost");
        conf.set(RM_PRINCIPAL, "test/_HOST@REALM");
        conf.setBoolean(RM_HA_ENABLED, false);
        result = YarnClientUtils.getRmPrincipal(conf);
        Assert.assertEquals((("The hostname translation did not produce the expected " + "results: ") + result), "test/myhost@REALM", result);
        conf.set(RM_PRINCIPAL, "test/yourhost@REALM");
        result = YarnClientUtils.getRmPrincipal(conf);
        Assert.assertEquals((("The hostname translation did not produce the expected " + "results: ") + result), "test/yourhost@REALM", result);
    }

    /**
     * Test of getRMPrincipal(Configuration) method, of class YarnClientUtils
     * when HA is enabled.
     *
     * @throws java.io.IOException
     * 		thrown if stuff breaks
     */
    @Test
    public void testGetRMPrincipalHA_Configuration() throws IOException {
        Configuration conf = new Configuration();
        conf.set(RM_ADDRESS, "myhost");
        conf.setBoolean(RM_HA_ENABLED, true);
        String result = YarnClientUtils.getRmPrincipal(conf);
        Assert.assertNull((("The hostname translation did return null when the principal is " + "missing from the conf: ") + result), result);
        conf = new Configuration();
        conf.set(((YarnConfiguration.RM_ADDRESS) + ".rm0"), "myhost");
        conf.set(RM_PRINCIPAL, "test/_HOST@REALM");
        conf.setBoolean(RM_HA_ENABLED, true);
        conf.set(RM_HA_IDS, "rm0");
        result = YarnClientUtils.getRmPrincipal(conf);
        Assert.assertEquals((("The hostname translation did not produce the expected " + "results: ") + result), "test/myhost@REALM", result);
        conf = new Configuration();
        conf.set(((YarnConfiguration.RM_ADDRESS) + ".rm0"), "myhost");
        conf.set(RM_PRINCIPAL, "test/_HOST@REALM");
        conf.setBoolean(RM_HA_ENABLED, true);
        try {
            result = YarnClientUtils.getRmPrincipal(conf);
            Assert.fail((("The hostname translation succeeded even though no RM ids were " + "set: ") + result));
        } catch (IOException ex) {
            // Expected
        }
        conf = new Configuration();
        conf.set(((YarnConfiguration.RM_ADDRESS) + ".rm0"), "myhost");
        conf.set(RM_PRINCIPAL, "test/_HOST@REALM");
        conf.setBoolean(RM_HA_ENABLED, true);
        conf.set(RM_HA_ID, "rm0");
        result = YarnClientUtils.getRmPrincipal(conf);
        Assert.assertEquals((("The hostname translation did not produce the expected " + "results: ") + result), "test/myhost@REALM", result);
        conf.set(RM_PRINCIPAL, "test/yourhost@REALM");
        result = YarnClientUtils.getRmPrincipal(conf);
        Assert.assertEquals((("The hostname translation did not produce the expected " + "results: ") + result), "test/yourhost@REALM", result);
    }

    /**
     * Test of getRMPrincipal(Configuration) method, of class YarnClientUtils
     * when HA is not enabled.
     *
     * @throws java.io.IOException
     * 		thrown if stuff breaks
     */
    @Test
    public void testGetRMPrincipalStandAlone_String() throws IOException {
        Configuration conf = new Configuration();
        conf.set(RM_ADDRESS, "myhost");
        conf.setBoolean(RM_HA_ENABLED, false);
        String result = YarnClientUtils.getRmPrincipal("test/_HOST@REALM", conf);
        Assert.assertEquals((("The hostname translation did not produce the expected " + "results: ") + result), "test/myhost@REALM", result);
        result = YarnClientUtils.getRmPrincipal("test/yourhost@REALM", conf);
        Assert.assertEquals((("The hostname translation did not produce the expected " + "results: ") + result), "test/yourhost@REALM", result);
        try {
            result = YarnClientUtils.getRmPrincipal(null, conf);
            Assert.fail((("The hostname translation succeeded even though the RM principal " + "was null: ") + result));
        } catch (IllegalArgumentException ex) {
            // Expected
        }
    }

    /**
     * Test of getRMPrincipal(Configuration) method, of class YarnClientUtils
     * when HA is enabled.
     *
     * @throws java.io.IOException
     * 		thrown if stuff breaks
     */
    @Test
    public void testGetRMPrincipalHA_String() throws IOException {
        Configuration conf = new Configuration();
        conf.set(((YarnConfiguration.RM_ADDRESS) + ".rm0"), "myhost");
        conf.setBoolean(RM_HA_ENABLED, true);
        conf.set(RM_HA_IDS, "rm0");
        String result = YarnClientUtils.getRmPrincipal("test/_HOST@REALM", conf);
        Assert.assertEquals((("The hostname translation did not produce the expected " + "results: ") + result), "test/myhost@REALM", result);
        try {
            result = YarnClientUtils.getRmPrincipal(null, conf);
            Assert.fail((("The hostname translation succeeded even though the RM principal " + "was null: ") + result));
        } catch (IllegalArgumentException ex) {
            // Expected
        }
        conf = new Configuration();
        conf.set(((YarnConfiguration.RM_ADDRESS) + ".rm0"), "myhost");
        conf.setBoolean(RM_HA_ENABLED, true);
        try {
            YarnClientUtils.getRmPrincipal("test/_HOST@REALM", conf);
            Assert.fail("The hostname translation succeeded even though no RM ids were set");
        } catch (IOException ex) {
            // Expected
        }
        conf = new Configuration();
        conf.set(((YarnConfiguration.RM_ADDRESS) + ".rm0"), "myhost");
        conf.setBoolean(RM_HA_ENABLED, true);
        conf.set(RM_HA_ID, "rm0");
        result = YarnClientUtils.getRmPrincipal("test/_HOST@REALM", conf);
        Assert.assertEquals((("The hostname translation did not produce the expected " + "results: ") + result), "test/myhost@REALM", result);
        result = YarnClientUtils.getRmPrincipal("test/yourhost@REALM", conf);
        Assert.assertEquals((("The hostname translation did not produce the expected " + "results: ") + result), "test/yourhost@REALM", result);
    }

    /**
     * Test of getRMPrincipal method of class YarnClientUtils.
     *
     * @throws IOException
     * 		thrown when something breaks
     */
    @Test
    public void testGetYarnConfWithRmHaId() throws IOException {
        Configuration conf = new Configuration();
        conf.set(RM_HA_ID, "rm0");
        conf.setBoolean(RM_HA_ENABLED, false);
        YarnConfiguration result = YarnClientUtils.getYarnConfWithRmHaId(conf);
        assertSameConf(conf, result);
        Assert.assertEquals(("RM_HA_ID was changed when it shouldn't have been: " + (result.get(RM_HA_ID))), "rm0", result.get(RM_HA_ID));
        conf = new Configuration();
        conf.set(RM_HA_ID, "rm0");
        conf.setBoolean(RM_HA_ENABLED, true);
        result = YarnClientUtils.getYarnConfWithRmHaId(conf);
        assertSameConf(conf, result);
        Assert.assertEquals(("RM_HA_ID was changed when it shouldn't have been: " + (result.get(RM_HA_ID))), "rm0", result.get(RM_HA_ID));
        conf = new Configuration();
        conf.set(RM_HA_IDS, "rm0,rm1");
        conf.setBoolean(RM_HA_ENABLED, true);
        result = YarnClientUtils.getYarnConfWithRmHaId(conf);
        assertSameConf(conf, result);
        Assert.assertEquals(("RM_HA_ID was not set correctly: " + (result.get(RM_HA_ID))), "rm0", result.get(RM_HA_ID));
        conf = new Configuration();
        conf.setBoolean(RM_HA_ENABLED, true);
        try {
            YarnClientUtils.getYarnConfWithRmHaId(conf);
            Assert.fail(("Allowed invalid HA configuration: HA is enabled, but no RM ID " + "is set"));
        } catch (IOException ex) {
            // Expected
        }
    }
}

