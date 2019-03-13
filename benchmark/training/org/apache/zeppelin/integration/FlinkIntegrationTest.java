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
package org.apache.zeppelin.integration;


import YarnApplicationState.RUNNING;
import java.io.IOException;
import java.util.EnumSet;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationsRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationsResponse;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.zeppelin.interpreter.InterpreterException;
import org.apache.zeppelin.interpreter.InterpreterFactory;
import org.apache.zeppelin.interpreter.InterpreterSetting;
import org.apache.zeppelin.interpreter.InterpreterSettingManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(Parameterized.class)
public class FlinkIntegrationTest {
    private static Logger LOGGER = LoggerFactory.getLogger(SparkIntegrationTest.class);

    private static MiniHadoopCluster hadoopCluster;

    private static MiniZeppelin zeppelin;

    private static InterpreterFactory interpreterFactory;

    private static InterpreterSettingManager interpreterSettingManager;

    private String flinkVersion;

    private String flinkHome;

    public FlinkIntegrationTest(String flinkVersion) {
        FlinkIntegrationTest.LOGGER.info(("Testing FlinkVersion: " + flinkVersion));
        this.flinkVersion = flinkVersion;
        this.flinkHome = DownloadUtils.downloadFlink(flinkVersion);
    }

    @Test
    public void testLocalMode() throws IOException, YarnException, InterpreterException {
        InterpreterSetting flinkInterpreterSetting = FlinkIntegrationTest.interpreterSettingManager.getInterpreterSettingByName("flink");
        flinkInterpreterSetting.setProperty("FLINK_HOME", flinkHome);
        flinkInterpreterSetting.setProperty("ZEPPELIN_CONF_DIR", FlinkIntegrationTest.zeppelin.getZeppelinConfDir().getAbsolutePath());
        testInterpreterBasics();
        // no yarn application launched
        GetApplicationsRequest request = GetApplicationsRequest.newInstance(EnumSet.of(RUNNING));
        GetApplicationsResponse response = FlinkIntegrationTest.hadoopCluster.getYarnCluster().getResourceManager().getClientRMService().getApplications(request);
        Assert.assertEquals(0, response.getApplicationList().size());
        FlinkIntegrationTest.interpreterSettingManager.close();
    }
}

