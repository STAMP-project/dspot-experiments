/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.  The ASF licenses this file to you under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.apache.storm;


import Config.TOPOLOGY_MESSAGE_TIMEOUT_SECS;
import DaemonConfig.DRPC_CHILDOPTS;
import DaemonConfig.LOGVIEWER_CHILDOPTS;
import DaemonConfig.LOGVIEWER_HTTPS_KEY_PASSWORD;
import DaemonConfig.NIMBUS_CHILDOPTS;
import DaemonConfig.PACEMAKER_CHILDOPTS;
import DaemonConfig.SUPERVISOR_CHILDOPTS;
import DaemonConfig.UI_CHILDOPTS;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import org.apache.storm.utils.ConfigUtils;
import org.junit.Assert;
import org.junit.Test;


public class DaemonConfigTest {
    @Test
    public void testNimbusChildoptsIsStringOrStringList() throws IllegalAccessException, InstantiationException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException {
        stringOrStringListTest(NIMBUS_CHILDOPTS);
    }

    @Test
    public void testLogviewerChildoptsIsStringOrStringList() throws IllegalAccessException, InstantiationException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException {
        stringOrStringListTest(LOGVIEWER_CHILDOPTS);
    }

    @Test
    public void testUiChildoptsIsStringOrStringList() throws IllegalAccessException, InstantiationException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException {
        stringOrStringListTest(UI_CHILDOPTS);
    }

    @Test
    public void testPacemakerChildoptsIsStringOrStringList() throws IllegalAccessException, InstantiationException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException {
        stringOrStringListTest(PACEMAKER_CHILDOPTS);
    }

    @Test
    public void testDrpcChildoptsIsStringOrStringList() throws IllegalAccessException, InstantiationException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException {
        stringOrStringListTest(DRPC_CHILDOPTS);
    }

    @Test
    public void testSupervisorChildoptsIsStringOrStringList() throws IllegalAccessException, InstantiationException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException {
        stringOrStringListTest(SUPERVISOR_CHILDOPTS);
    }

    @Test
    public void testMaskPasswords() {
        Map<String, Object> conf = new HashMap<>();
        conf.put(LOGVIEWER_HTTPS_KEY_PASSWORD, "pass1");
        conf.put(TOPOLOGY_MESSAGE_TIMEOUT_SECS, 100);
        Map result = ConfigUtils.maskPasswords(conf);
        Assert.assertEquals("*****", result.get(LOGVIEWER_HTTPS_KEY_PASSWORD));
        Assert.assertEquals(100, result.get(TOPOLOGY_MESSAGE_TIMEOUT_SECS));
    }
}

