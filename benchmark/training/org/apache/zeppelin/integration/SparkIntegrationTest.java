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
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class SparkIntegrationTest {
    private static Logger LOGGER = LoggerFactory.getLogger(SparkIntegrationTest.class);

    private static MiniHadoopCluster hadoopCluster;

    private static MiniZeppelin zeppelin;

    private static InterpreterFactory interpreterFactory;

    private static InterpreterSettingManager interpreterSettingManager;

    private String sparkVersion;

    private String sparkHome;

    public SparkIntegrationTest(String sparkVersion) {
        SparkIntegrationTest.LOGGER.info(("Testing SparkVersion: " + sparkVersion));
        this.sparkVersion = sparkVersion;
        this.sparkHome = DownloadUtils.downloadSpark(sparkVersion);
    }

    @Test
    public void testLocalMode() throws IOException, YarnException, InterpreterException, XmlPullParserException {
        InterpreterSetting sparkInterpreterSetting = SparkIntegrationTest.interpreterSettingManager.getInterpreterSettingByName("spark");
        sparkInterpreterSetting.setProperty("master", "local[*]");
        sparkInterpreterSetting.setProperty("SPARK_HOME", sparkHome);
        sparkInterpreterSetting.setProperty("ZEPPELIN_CONF_DIR", SparkIntegrationTest.zeppelin.getZeppelinConfDir().getAbsolutePath());
        sparkInterpreterSetting.setProperty("zeppelin.spark.useHiveContext", "false");
        sparkInterpreterSetting.setProperty("zeppelin.pyspark.useIPython", "false");
        sparkInterpreterSetting.setProperty("zeppelin.spark.scala.color", "false");
        testInterpreterBasics();
        // no yarn application launched
        GetApplicationsRequest request = GetApplicationsRequest.newInstance(EnumSet.of(RUNNING));
        GetApplicationsResponse response = SparkIntegrationTest.hadoopCluster.getYarnCluster().getResourceManager().getClientRMService().getApplications(request);
        Assert.assertEquals(0, response.getApplicationList().size());
        SparkIntegrationTest.interpreterSettingManager.close();
    }

    @Test
    public void testYarnClientMode() throws IOException, InterruptedException, YarnException, InterpreterException, XmlPullParserException {
        InterpreterSetting sparkInterpreterSetting = SparkIntegrationTest.interpreterSettingManager.getInterpreterSettingByName("spark");
        sparkInterpreterSetting.setProperty("master", "yarn-client");
        sparkInterpreterSetting.setProperty("HADOOP_CONF_DIR", SparkIntegrationTest.hadoopCluster.getConfigPath());
        sparkInterpreterSetting.setProperty("SPARK_HOME", sparkHome);
        sparkInterpreterSetting.setProperty("ZEPPELIN_CONF_DIR", SparkIntegrationTest.zeppelin.getZeppelinConfDir().getAbsolutePath());
        sparkInterpreterSetting.setProperty("zeppelin.spark.useHiveContext", "false");
        sparkInterpreterSetting.setProperty("zeppelin.pyspark.useIPython", "false");
        sparkInterpreterSetting.setProperty("PYSPARK_PYTHON", getPythonExec());
        sparkInterpreterSetting.setProperty("spark.driver.memory", "512m");
        sparkInterpreterSetting.setProperty("zeppelin.spark.scala.color", "false");
        testInterpreterBasics();
        // 1 yarn application launched
        GetApplicationsRequest request = GetApplicationsRequest.newInstance(EnumSet.of(RUNNING));
        GetApplicationsResponse response = SparkIntegrationTest.hadoopCluster.getYarnCluster().getResourceManager().getClientRMService().getApplications(request);
        Assert.assertEquals(1, response.getApplicationList().size());
        SparkIntegrationTest.interpreterSettingManager.close();
    }

    @Test
    public void testYarnClusterMode() throws IOException, InterruptedException, YarnException, InterpreterException, XmlPullParserException {
        InterpreterSetting sparkInterpreterSetting = SparkIntegrationTest.interpreterSettingManager.getInterpreterSettingByName("spark");
        sparkInterpreterSetting.setProperty("master", "yarn-cluster");
        sparkInterpreterSetting.setProperty("HADOOP_CONF_DIR", SparkIntegrationTest.hadoopCluster.getConfigPath());
        sparkInterpreterSetting.setProperty("SPARK_HOME", sparkHome);
        sparkInterpreterSetting.setProperty("ZEPPELIN_CONF_DIR", SparkIntegrationTest.zeppelin.getZeppelinConfDir().getAbsolutePath());
        sparkInterpreterSetting.setProperty("zeppelin.spark.useHiveContext", "false");
        sparkInterpreterSetting.setProperty("zeppelin.pyspark.useIPython", "false");
        sparkInterpreterSetting.setProperty("PYSPARK_PYTHON", getPythonExec());
        sparkInterpreterSetting.setProperty("spark.driver.memory", "512m");
        sparkInterpreterSetting.setProperty("zeppelin.spark.scala.color", "false");
        testInterpreterBasics();
        // 1 yarn application launched
        GetApplicationsRequest request = GetApplicationsRequest.newInstance(EnumSet.of(RUNNING));
        GetApplicationsResponse response = SparkIntegrationTest.hadoopCluster.getYarnCluster().getResourceManager().getClientRMService().getApplications(request);
        Assert.assertEquals(1, response.getApplicationList().size());
        SparkIntegrationTest.interpreterSettingManager.close();
    }
}

