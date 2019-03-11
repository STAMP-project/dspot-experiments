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
package org.apache.hadoop.yarn.client.cli;


import ContainerState.RUNNING;
import Status.OK;
import YarnApplicationState.FINISHED;
import YarnConfiguration.APPLICATION_HISTORY_ENABLED;
import YarnConfiguration.LOG_AGGREGATION_ENABLED;
import YarnConfiguration.NM_REMOTE_APP_LOG_DIR;
import YarnConfiguration.TIMELINE_SERVICE_ENABLED;
import YarnConfiguration.TIMELINE_SERVICE_VERSIONS;
import YarnConfiguration.YARN_ACL_ENABLE;
import YarnConfiguration.YARN_ADMIN_ACL;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptReport;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerReport;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.logaggregation.ContainerLogsRequest;
import org.apache.hadoop.yarn.logaggregation.LogAggregationUtils;
import org.apache.hadoop.yarn.logaggregation.LogCLIHelpers;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestLogsCLI {
    ByteArrayOutputStream sysOutStream;

    private PrintStream sysOut;

    ByteArrayOutputStream sysErrStream;

    private PrintStream sysErr;

    @Test(timeout = 5000L)
    public void testFailResultCodes() throws Exception {
        Configuration conf = new YarnConfiguration();
        conf.setClass("fs.file.impl", LocalFileSystem.class, FileSystem.class);
        LogCLIHelpers cliHelper = new LogCLIHelpers();
        cliHelper.setConf(conf);
        YarnClient mockYarnClient = createMockYarnClient(FINISHED, UserGroupInformation.getCurrentUser().getShortUserName());
        LogsCLI dumper = new TestLogsCLI.LogsCLIForTest(mockYarnClient);
        dumper.setConf(conf);
        // verify dumping a non-existent application's logs returns a failure code
        int exitCode = dumper.run(new String[]{ "-applicationId", "application_0_0" });
        Assert.assertTrue("Should return an error code", (exitCode != 0));
        // verify dumping a non-existent container log is a failure code
        exitCode = cliHelper.dumpAContainersLogs("application_0_0", "container_0_0", "nonexistentnode:1234", "nobody");
        Assert.assertTrue("Should return an error code", (exitCode != 0));
    }

    @Test(timeout = 10000L)
    public void testInvalidOpts() throws Exception {
        Configuration conf = new YarnConfiguration();
        YarnClient mockYarnClient = createMockYarnClient(FINISHED, UserGroupInformation.getCurrentUser().getShortUserName());
        LogsCLI cli = new TestLogsCLI.LogsCLIForTest(mockYarnClient);
        cli.setConf(conf);
        int exitCode = cli.run(new String[]{ "-InvalidOpts" });
        Assert.assertTrue((exitCode == (-1)));
        Assert.assertTrue(sysErrStream.toString().contains("options parsing failed: Unrecognized option: -InvalidOpts"));
    }

    @Test(timeout = 5000L)
    public void testInvalidApplicationId() throws Exception {
        Configuration conf = new YarnConfiguration();
        YarnClient mockYarnClient = createMockYarnClient(FINISHED, UserGroupInformation.getCurrentUser().getShortUserName());
        LogsCLI cli = new TestLogsCLI.LogsCLIForTest(mockYarnClient);
        cli.setConf(conf);
        int exitCode = cli.run(new String[]{ "-applicationId", "not_an_app_id" });
        Assert.assertTrue((exitCode == (-1)));
        Assert.assertTrue(sysErrStream.toString().startsWith("Invalid ApplicationId specified"));
    }

    @Test(timeout = 5000L)
    public void testInvalidAMContainerId() throws Exception {
        Configuration conf = new YarnConfiguration();
        conf.setBoolean(APPLICATION_HISTORY_ENABLED, true);
        YarnClient mockYarnClient = createMockYarnClient(FINISHED, UserGroupInformation.getCurrentUser().getShortUserName());
        LogsCLI cli = Mockito.spy(new TestLogsCLI.LogsCLIForTest(mockYarnClient));
        List<JSONObject> list = Arrays.asList(new JSONObject());
        Mockito.doReturn(list).when(cli).getAMContainerInfoForRMWebService(ArgumentMatchers.any(Configuration.class), ArgumentMatchers.any(String.class));
        cli.setConf(conf);
        int exitCode = cli.run(new String[]{ "-applicationId", "application_1465862913885_0027", "-am", "1000" });
        Assert.assertTrue((exitCode == (-1)));
        Assert.assertTrue(sysErrStream.toString().contains("exceeds the number of AM containers"));
    }

    @Test
    public void testAMContainerInfoFetchFromTimelineReader() throws Exception {
        Configuration conf = new YarnConfiguration();
        conf.setBoolean(TIMELINE_SERVICE_ENABLED, true);
        conf.set(TIMELINE_SERVICE_VERSIONS, "2.0f");
        YarnClient mockYarnClient = createMockYarnClient(FINISHED, UserGroupInformation.getCurrentUser().getShortUserName());
        LogsCLI cli = Mockito.spy(new TestLogsCLI.LogsCLIForTest(mockYarnClient));
        String appInfoEntity = "[{\"metrics\":[],\"events\":[],\"createdtime\":1542273848613,\"idpref" + (((("ix\":9223372036854775806,\"id\":\"appattempt_1542271570060_0002_" + "000001\",\"type\":\"YARN_APPLICATION_ATTEMPT\",\"info\":{\"YARN_") + "APPLICATION_ATTEMPT_MASTER_CONTAINER\":\"container_e01_154227157") + "0060_0002_01_000001\"},\"configs\":{},\"isrelatedto\":{},\"relat") + "esto\":{}}]");
        JSONArray obj = new JSONArray(appInfoEntity);
        ClientResponse response = Mockito.mock(ClientResponse.class);
        Mockito.doReturn(obj).when(response).getEntity(JSONArray.class);
        Mockito.doReturn(response).when(cli).getClientResponseFromTimelineReader(ArgumentMatchers.any(Configuration.class), ArgumentMatchers.any(String.class));
        Mockito.doThrow(new RuntimeException()).when(cli).getAMContainerInfoForRMWebService(ArgumentMatchers.any(Configuration.class), ArgumentMatchers.any(String.class));
        cli.setConf(conf);
        int exitCode = cli.run(new String[]{ "-applicationId", "application_1542271570060_0002", "-am", "1" });
        Assert.assertTrue((exitCode == 0));
    }

    @Test(timeout = 5000L)
    public void testUnknownApplicationId() throws Exception {
        Configuration conf = new YarnConfiguration();
        YarnClient mockYarnClient = createMockYarnClientUnknownApp();
        LogsCLI cli = new TestLogsCLI.LogsCLIForTest(mockYarnClient);
        cli.setConf(conf);
        int exitCode = cli.run(new String[]{ "-applicationId", ApplicationId.newInstance(1, 1).toString() });
        // Error since no logs present for the app.
        Assert.assertTrue((exitCode != 0));
        Assert.assertTrue(sysErrStream.toString().startsWith("Unable to get ApplicationState"));
    }

    @Test(timeout = 10000)
    public void testHelpMessage() throws Exception {
        Configuration conf = new YarnConfiguration();
        YarnClient mockYarnClient = createMockYarnClient(FINISHED, UserGroupInformation.getCurrentUser().getShortUserName());
        LogsCLI dumper = new TestLogsCLI.LogsCLIForTest(mockYarnClient);
        dumper.setConf(conf);
        int exitCode = dumper.run(new String[]{  });
        Assert.assertTrue((exitCode == (-1)));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos);
        pw.println("Retrieve logs for YARN applications.");
        pw.println("usage: yarn logs -applicationId <application ID> [OPTIONS]");
        pw.println();
        pw.println("general options are:");
        pw.println(" -am <AM Containers>                          Prints the AM Container logs");
        pw.println("                                              for this application.");
        pw.println("                                              Specify comma-separated");
        pw.println("                                              value to get logs for");
        pw.println("                                              related AM Container. For");
        pw.println("                                              example, If we specify -am");
        pw.println("                                              1,2, we will get the logs");
        pw.println("                                              for the first AM Container");
        pw.println("                                              as well as the second AM");
        pw.println("                                              Container. To get logs for");
        pw.println("                                              all AM Containers, use -am");
        pw.println("                                              ALL. To get logs for the");
        pw.println("                                              latest AM Container, use -am");
        pw.println("                                              -1. By default, it will");
        pw.println("                                              print all available logs.");
        pw.println("                                              Work with -log_files to get");
        pw.println("                                              only specific logs.");
        pw.println(" -appOwner <Application Owner>                AppOwner (assumed to be");
        pw.println("                                              current user if not");
        pw.println("                                              specified)");
        pw.println(" -client_max_retries <Max Retries>            Set max retry number for a");
        pw.println("                                              retry client to get the");
        pw.println("                                              container logs for the");
        pw.println("                                              running applications. Use a");
        pw.println("                                              negative value to make retry");
        pw.println("                                              forever. The default value");
        pw.println("                                              is 30.");
        pw.println(" -client_retry_interval_ms <Retry Interval>   Work with");
        pw.println("                                              --client_max_retries to");
        pw.println("                                              create a retry client. The");
        pw.println("                                              default value is 1000.");
        pw.println(" -clusterId <Cluster ID>                      ClusterId. By default, it");
        pw.println("                                              will take default cluster id");
        pw.println("                                              from the RM");
        pw.println(" -containerId <Container ID>                  ContainerId. By default, it");
        pw.println("                                              will print all available");
        pw.println("                                              logs. Work with -log_files");
        pw.println("                                              to get only specific logs.");
        pw.println("                                              If specified, the");
        pw.println("                                              applicationId can be omitted");
        pw.println(" -help                                        Displays help for all");
        pw.println("                                              commands.");
        pw.println(" -list_nodes                                  Show the list of nodes that");
        pw.println("                                              successfully aggregated");
        pw.println("                                              logs. This option can only");
        pw.println("                                              be used with finished");
        pw.println("                                              applications.");
        pw.println(" -log_files <Log File Name>                   Specify comma-separated");
        pw.println("                                              value to get exact matched");
        pw.println("                                              log files. Use \"ALL\" or \"*\"");
        pw.println("                                              to fetch all the log files");
        pw.println("                                              for the container.");
        pw.println(" -log_files_pattern <Log File Pattern>        Specify comma-separated");
        pw.println("                                              value to get matched log");
        pw.println("                                              files by using java regex.");
        pw.println("                                              Use \".*\" to fetch all the");
        pw.println("                                              log files for the container.");
        pw.println(" -nodeAddress <Node Address>                  NodeAddress in the format");
        pw.println("                                              nodename:port");
        pw.println(" -out <Local Directory>                       Local directory for storing");
        pw.println("                                              individual container logs.");
        pw.println("                                              The container logs will be");
        pw.println("                                              stored based on the node the");
        pw.println("                                              container ran on.");
        pw.println(" -show_application_log_info                   Show the containerIds which");
        pw.println("                                              belong to the specific");
        pw.println("                                              Application. You can combine");
        pw.println("                                              this with --nodeAddress to");
        pw.println("                                              get containerIds for all the");
        pw.println("                                              containers on the specific");
        pw.println("                                              NodeManager.");
        pw.println(" -show_container_log_info                     Show the container log");
        pw.println("                                              metadata, including log-file");
        pw.println("                                              names, the size of the log");
        pw.println("                                              files. You can combine this");
        pw.println("                                              with --containerId to get");
        pw.println("                                              log metadata for the");
        pw.println("                                              specific container, or with");
        pw.println("                                              --nodeAddress to get log");
        pw.println("                                              metadata for all the");
        pw.println("                                              containers on the specific");
        pw.println("                                              NodeManager.");
        pw.println(" -size <size>                                 Prints the log file's first");
        pw.println("                                              'n' bytes or the last 'n'");
        pw.println("                                              bytes. Use negative values");
        pw.println("                                              as bytes to read from the");
        pw.println("                                              end and positive values as");
        pw.println("                                              bytes to read from the");
        pw.println("                                              beginning.");
        pw.println(" -size_limit_mb <Size Limit>                  Use this option to limit the");
        pw.println("                                              size of the total logs which");
        pw.println("                                              could be fetched. By");
        pw.println("                                              default, we only allow to");
        pw.println("                                              fetch at most 10240 MB logs.");
        pw.println("                                              If the total log size is");
        pw.println("                                              larger than the specified");
        pw.println("                                              number, the CLI would fail.");
        pw.println("                                              The user could specify -1 to");
        pw.println("                                              ignore the size limit and");
        pw.println("                                              fetch all logs.");
        pw.close();
        String appReportStr = baos.toString("UTF-8");
        Assert.assertTrue(sysOutStream.toString().contains(appReportStr));
    }

    @Test(timeout = 15000)
    public void testFetchFinishedApplictionLogs() throws Exception {
        String remoteLogRootDir = "target/logs/";
        Configuration configuration = new YarnConfiguration();
        configuration.setBoolean(LOG_AGGREGATION_ENABLED, true);
        configuration.set(NM_REMOTE_APP_LOG_DIR, remoteLogRootDir);
        configuration.setBoolean(YARN_ACL_ENABLE, true);
        configuration.set(YARN_ADMIN_ACL, "admin");
        FileSystem fs = FileSystem.get(configuration);
        UserGroupInformation ugi = UserGroupInformation.getCurrentUser();
        ApplicationId appId = ApplicationId.newInstance(0, 1);
        ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(appId, 1);
        ContainerId containerId0 = ContainerId.newContainerId(appAttemptId, 0);
        ContainerId containerId1 = ContainerId.newContainerId(appAttemptId, 1);
        ContainerId containerId2 = ContainerId.newContainerId(appAttemptId, 2);
        ContainerId containerId3 = ContainerId.newContainerId(appAttemptId, 3);
        final NodeId nodeId = NodeId.newInstance("localhost", 1234);
        // create local logs
        String rootLogDir = "target/LocalLogs";
        Path rootLogDirPath = new Path(rootLogDir);
        if (fs.exists(rootLogDirPath)) {
            fs.delete(rootLogDirPath, true);
        }
        Assert.assertTrue(fs.mkdirs(rootLogDirPath));
        Path appLogsDir = new Path(rootLogDirPath, appId.toString());
        if (fs.exists(appLogsDir)) {
            fs.delete(appLogsDir, true);
        }
        Assert.assertTrue(fs.mkdirs(appLogsDir));
        List<String> rootLogDirs = Arrays.asList(rootLogDir);
        List<String> logTypes = new ArrayList<String>();
        logTypes.add("syslog");
        // create container logs in localLogDir
        TestLogsCLI.createContainerLogInLocalDir(appLogsDir, containerId1, fs, logTypes);
        TestLogsCLI.createContainerLogInLocalDir(appLogsDir, containerId2, fs, logTypes);
        // create two logs for container3 in localLogDir
        logTypes.add("stdout");
        logTypes.add("stdout1234");
        TestLogsCLI.createContainerLogInLocalDir(appLogsDir, containerId3, fs, logTypes);
        Path path = new Path(((remoteLogRootDir + (ugi.getShortUserName())) + "/logs/application_0_0001"));
        if (fs.exists(path)) {
            fs.delete(path, true);
        }
        Assert.assertTrue(fs.mkdirs(path));
        // upload container logs into remote directory
        // the first two logs is empty. When we try to read first two logs,
        // we will meet EOF exception, but it will not impact other logs.
        // Other logs should be read successfully.
        TestLogsCLI.uploadEmptyContainerLogIntoRemoteDir(ugi, configuration, rootLogDirs, nodeId, containerId0, path, fs);
        TestLogsCLI.uploadEmptyContainerLogIntoRemoteDir(ugi, configuration, rootLogDirs, nodeId, containerId1, path, fs);
        TestLogsCLI.uploadContainerLogIntoRemoteDir(ugi, configuration, rootLogDirs, nodeId, containerId1, path, fs);
        TestLogsCLI.uploadContainerLogIntoRemoteDir(ugi, configuration, rootLogDirs, nodeId, containerId2, path, fs);
        TestLogsCLI.uploadContainerLogIntoRemoteDir(ugi, configuration, rootLogDirs, nodeId, containerId3, path, fs);
        YarnClient mockYarnClient = createMockYarnClient(FINISHED, ugi.getShortUserName());
        LogsCLI cli = new TestLogsCLI.LogsCLIForTest(mockYarnClient) {
            @Override
            public ContainerReport getContainerReport(String containerIdStr) throws IOException, YarnException {
                ContainerReport mockReport = Mockito.mock(ContainerReport.class);
                Mockito.doReturn(nodeId).when(mockReport).getAssignedNode();
                Mockito.doReturn("http://localhost:2345").when(mockReport).getNodeHttpAddress();
                return mockReport;
            }
        };
        cli.setConf(configuration);
        int exitCode = cli.run(new String[]{ "-applicationId", appId.toString() });
        Assert.assertTrue((exitCode == 0));
        Assert.assertTrue(sysOutStream.toString().contains(TestLogsCLI.logMessage(containerId1, "syslog")));
        Assert.assertTrue(sysOutStream.toString().contains(TestLogsCLI.logMessage(containerId2, "syslog")));
        Assert.assertTrue(sysOutStream.toString().contains(TestLogsCLI.logMessage(containerId3, "syslog")));
        Assert.assertTrue(sysOutStream.toString().contains(TestLogsCLI.logMessage(containerId3, "stdout")));
        Assert.assertTrue(sysOutStream.toString().contains(TestLogsCLI.logMessage(containerId3, "stdout1234")));
        sysOutStream.reset();
        exitCode = cli.run(new String[]{ "-applicationId", appId.toString(), "-log_files_pattern", ".*" });
        Assert.assertTrue((exitCode == 0));
        Assert.assertTrue(sysOutStream.toString().contains(TestLogsCLI.logMessage(containerId1, "syslog")));
        Assert.assertTrue(sysOutStream.toString().contains(TestLogsCLI.logMessage(containerId2, "syslog")));
        Assert.assertTrue(sysOutStream.toString().contains(TestLogsCLI.logMessage(containerId3, "syslog")));
        Assert.assertTrue(sysOutStream.toString().contains(TestLogsCLI.logMessage(containerId3, "stdout")));
        Assert.assertTrue(sysOutStream.toString().contains(TestLogsCLI.logMessage(containerId3, "stdout1234")));
        sysOutStream.reset();
        exitCode = cli.run(new String[]{ "-applicationId", appId.toString(), "-log_files", "*" });
        Assert.assertTrue((exitCode == 0));
        Assert.assertTrue(sysOutStream.toString().contains(TestLogsCLI.logMessage(containerId1, "syslog")));
        Assert.assertTrue(sysOutStream.toString().contains(TestLogsCLI.logMessage(containerId2, "syslog")));
        Assert.assertTrue(sysOutStream.toString().contains(TestLogsCLI.logMessage(containerId3, "syslog")));
        Assert.assertTrue(sysOutStream.toString().contains(TestLogsCLI.logMessage(containerId3, "stdout")));
        Assert.assertTrue(sysOutStream.toString().contains(TestLogsCLI.logMessage(containerId3, "stdout1234")));
        int fullSize = sysOutStream.toByteArray().length;
        sysOutStream.reset();
        exitCode = cli.run(new String[]{ "-applicationId", appId.toString(), "-log_files", "stdout" });
        Assert.assertTrue((exitCode == 0));
        Assert.assertFalse(sysOutStream.toString().contains(TestLogsCLI.logMessage(containerId1, "syslog")));
        Assert.assertFalse(sysOutStream.toString().contains(TestLogsCLI.logMessage(containerId2, "syslog")));
        Assert.assertFalse(sysOutStream.toString().contains(TestLogsCLI.logMessage(containerId3, "syslog")));
        Assert.assertTrue(sysOutStream.toString().contains(TestLogsCLI.logMessage(containerId3, "stdout")));
        Assert.assertFalse(sysOutStream.toString().contains(TestLogsCLI.logMessage(containerId3, "stdout1234")));
        sysOutStream.reset();
        exitCode = cli.run(new String[]{ "-applicationId", appId.toString(), "-log_files_pattern", "std*" });
        Assert.assertTrue((exitCode == 0));
        Assert.assertFalse(sysOutStream.toString().contains(TestLogsCLI.logMessage(containerId1, "syslog")));
        Assert.assertFalse(sysOutStream.toString().contains(TestLogsCLI.logMessage(containerId2, "syslog")));
        Assert.assertFalse(sysOutStream.toString().contains(TestLogsCLI.logMessage(containerId3, "syslog")));
        Assert.assertTrue(sysOutStream.toString().contains(TestLogsCLI.logMessage(containerId3, "stdout")));
        Assert.assertTrue(sysOutStream.toString().contains(TestLogsCLI.logMessage(containerId3, "stdout1234")));
        sysOutStream.reset();
        exitCode = cli.run(new String[]{ "-applicationId", appId.toString(), "-log_files", "123" });
        Assert.assertTrue((exitCode == (-1)));
        Assert.assertTrue(sysErrStream.toString().contains((("Can not find any log file matching the pattern: [123] " + "for the application: ") + (appId.toString()))));
        sysErrStream.reset();
        // specify the bytes which is larger than the actual file size,
        // we would get the full logs
        exitCode = cli.run(new String[]{ "-applicationId", appId.toString(), "-log_files", "*", "-size", "10000" });
        Assert.assertTrue((exitCode == 0));
        Assert.assertTrue(((sysOutStream.toByteArray().length) == fullSize));
        sysOutStream.reset();
        // uploaded two logs for container1. The first log is empty.
        // The second one is not empty.
        // We can still successfully read logs for container1.
        exitCode = cli.run(new String[]{ "-applicationId", appId.toString(), "-nodeAddress", nodeId.toString(), "-containerId", containerId1.toString() });
        Assert.assertTrue((exitCode == 0));
        Assert.assertTrue(sysOutStream.toString().contains(TestLogsCLI.logMessage(containerId1, "syslog")));
        Assert.assertTrue(sysOutStream.toString().contains("LogLastModifiedTime"));
        Assert.assertTrue((!(sysOutStream.toString().contains((("Logs for container " + (containerId1.toString())) + " are not present in this log-file.")))));
        sysOutStream.reset();
        exitCode = cli.run(new String[]{ "-applicationId", appId.toString(), "-containerId", containerId3.toString(), "-log_files", "123" });
        Assert.assertTrue((exitCode == (-1)));
        Assert.assertTrue(sysErrStream.toString().contains((((("Can not find any log file matching the pattern: [123] " + "for the container: ") + containerId3) + " within the application: ") + (appId.toString()))));
        sysErrStream.reset();
        exitCode = cli.run(new String[]{ "-applicationId", appId.toString(), "-containerId", containerId3.toString(), "-log_files", "stdout" });
        Assert.assertTrue((exitCode == 0));
        int fullContextSize = sysOutStream.toByteArray().length;
        String fullContext = sysOutStream.toString();
        sysOutStream.reset();
        String logMessage = TestLogsCLI.logMessage(containerId3, "stdout");
        int fileContentSize = logMessage.getBytes().length;
        StringBuilder sb = new StringBuilder();
        String endOfFile = "End of LogType:stdout";
        sb.append((("\n" + endOfFile) + "\n"));
        sb.append(((StringUtils.repeat("*", ((endOfFile.length()) + 50))) + "\n\n"));
        int tailContentSize = sb.toString().length();
        // specify how many bytes we should get from logs
        // specify a position number, it would get the first n bytes from
        // container log
        exitCode = cli.run(new String[]{ "-applicationId", appId.toString(), "-containerId", containerId3.toString(), "-log_files", "stdout", "-size", "5" });
        Assert.assertTrue((exitCode == 0));
        Assert.assertEquals(new String(logMessage.getBytes(), 0, 5), new String(sysOutStream.toByteArray(), ((fullContextSize - fileContentSize) - tailContentSize), 5));
        sysOutStream.reset();
        // specify a negative number, it would get the last n bytes from
        // container log
        exitCode = cli.run(new String[]{ "-applicationId", appId.toString(), "-containerId", containerId3.toString(), "-log_files", "stdout", "-size", "-5" });
        Assert.assertTrue((exitCode == 0));
        Assert.assertEquals(new String(logMessage.getBytes(), ((logMessage.getBytes().length) - 5), 5), new String(sysOutStream.toByteArray(), ((fullContextSize - fileContentSize) - tailContentSize), 5));
        sysOutStream.reset();
        long negative = (fullContextSize + 1000) * (-1);
        exitCode = cli.run(new String[]{ "-applicationId", appId.toString(), "-containerId", containerId3.toString(), "-log_files", "stdout", "-size", Long.toString(negative) });
        Assert.assertTrue((exitCode == 0));
        Assert.assertEquals(fullContext, sysOutStream.toString());
        sysOutStream.reset();
        // Uploaded the empty log for container0.
        // We should see the message showing the log for container0
        // are not present.
        exitCode = cli.run(new String[]{ "-applicationId", appId.toString(), "-nodeAddress", nodeId.toString(), "-containerId", containerId0.toString() });
        Assert.assertTrue((exitCode == (-1)));
        Assert.assertTrue(sysErrStream.toString().contains("Can not find any log file matching the pattern"));
        sysErrStream.reset();
        // uploaded two logs for container3. The first log is named as syslog.
        // The second one is named as stdout.
        exitCode = cli.run(new String[]{ "-applicationId", appId.toString(), "-nodeAddress", nodeId.toString(), "-containerId", containerId3.toString() });
        Assert.assertTrue((exitCode == 0));
        Assert.assertTrue(sysOutStream.toString().contains(TestLogsCLI.logMessage(containerId3, "syslog")));
        Assert.assertTrue(sysOutStream.toString().contains(TestLogsCLI.logMessage(containerId3, "stdout")));
        sysOutStream.reset();
        // set -log_files option as stdout
        // should only print log with the name as stdout
        exitCode = cli.run(new String[]{ "-applicationId", appId.toString(), "-nodeAddress", nodeId.toString(), "-containerId", containerId3.toString(), "-log_files", "stdout" });
        Assert.assertTrue((exitCode == 0));
        Assert.assertTrue(sysOutStream.toString().contains(TestLogsCLI.logMessage(containerId3, "stdout")));
        Assert.assertTrue((!(sysOutStream.toString().contains(TestLogsCLI.logMessage(containerId3, "syslog")))));
        sysOutStream.reset();
        YarnClient mockYarnClientWithException = createMockYarnClientWithException();
        cli = new TestLogsCLI.LogsCLIForTest(mockYarnClientWithException);
        cli.setConf(configuration);
        exitCode = cli.run(new String[]{ "-applicationId", appId.toString(), "-containerId", containerId3.toString() });
        Assert.assertTrue((exitCode == 0));
        Assert.assertTrue(sysOutStream.toString().contains(TestLogsCLI.logMessage(containerId3, "syslog")));
        Assert.assertTrue(sysOutStream.toString().contains(TestLogsCLI.logMessage(containerId3, "stdout")));
        Assert.assertTrue(sysOutStream.toString().contains(((containerId3 + " on ") + (LogAggregationUtils.getNodeString(nodeId)))));
        sysOutStream.reset();
        // The same should also work without the applicationId
        exitCode = cli.run(new String[]{ "-containerId", containerId3.toString() });
        Assert.assertTrue((exitCode == 0));
        Assert.assertTrue(sysOutStream.toString().contains(TestLogsCLI.logMessage(containerId3, "syslog")));
        Assert.assertTrue(sysOutStream.toString().contains(TestLogsCLI.logMessage(containerId3, "stdout")));
        Assert.assertTrue(sysOutStream.toString().contains(((containerId3 + " on ") + (LogAggregationUtils.getNodeString(nodeId)))));
        sysOutStream.reset();
        exitCode = cli.run(new String[]{ "-containerId", "invalid_container" });
        Assert.assertTrue((exitCode == (-1)));
        Assert.assertTrue(sysErrStream.toString().contains("Invalid ContainerId specified"));
        sysErrStream.reset();
        fs.delete(new Path(remoteLogRootDir), true);
        fs.delete(new Path(rootLogDir), true);
    }

    @Test
    public void testCheckRetryCount() throws Exception {
        UserGroupInformation ugi = UserGroupInformation.getCurrentUser();
        NodeId nodeId = NodeId.newInstance("localhost", 1234);
        ApplicationId appId = ApplicationId.newInstance(0, 1);
        ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(appId, 1);
        // Create a mock ApplicationAttempt Report
        ApplicationAttemptReport mockAttemptReport = Mockito.mock(ApplicationAttemptReport.class);
        Mockito.doReturn(appAttemptId).when(mockAttemptReport).getApplicationAttemptId();
        List<ApplicationAttemptReport> attemptReports = Arrays.asList(mockAttemptReport);
        // Create one mock containerReport
        ContainerId containerId1 = ContainerId.newContainerId(appAttemptId, 1);
        ContainerReport mockContainerReport1 = Mockito.mock(ContainerReport.class);
        Mockito.doReturn(containerId1).when(mockContainerReport1).getContainerId();
        Mockito.doReturn(nodeId).when(mockContainerReport1).getAssignedNode();
        Mockito.doReturn("http://localhost:2345").when(mockContainerReport1).getNodeHttpAddress();
        Mockito.doReturn(RUNNING).when(mockContainerReport1).getContainerState();
        List<ContainerReport> containerReports = Arrays.asList(mockContainerReport1);
        // Mock the YarnClient, and it would report the previous created
        // mockAttemptReport and previous two created mockContainerReports
        YarnClient mockYarnClient = createMockYarnClient(YarnApplicationState.RUNNING, ugi.getShortUserName(), true, attemptReports, containerReports);
        Mockito.doReturn(mockContainerReport1).when(mockYarnClient).getContainerReport(ArgumentMatchers.any(ContainerId.class));
        LogsCLI cli = new TestLogsCLI.LogsCLIForTest(mockYarnClient);
        cli.setConf(new YarnConfiguration());
        try {
            cli.run(new String[]{ "-containerId", containerId1.toString(), "-client_max_retries", "5" });
            Assert.fail(("Exception expected! " + "NodeManager should be off to run this test. "));
        } catch (RuntimeException ce) {
            Assert.assertTrue(("Handler exception for reason other than retry: " + (ce.getMessage())), ce.getMessage().contains("Connection retries limit exceeded"));
            Assert.assertTrue("Retry filter didn't perform any retries! ", cli.connectionRetry.getRetired());
        }
    }

    @Test(timeout = 5000)
    public void testGetRunningContainerLogs() throws Exception {
        UserGroupInformation ugi = UserGroupInformation.getCurrentUser();
        NodeId nodeId = NodeId.newInstance("localhost", 1234);
        ApplicationId appId = ApplicationId.newInstance(0, 1);
        ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(appId, 1);
        // Create a mock ApplicationAttempt Report
        ApplicationAttemptReport mockAttemptReport = Mockito.mock(ApplicationAttemptReport.class);
        Mockito.doReturn(appAttemptId).when(mockAttemptReport).getApplicationAttemptId();
        List<ApplicationAttemptReport> attemptReports = Arrays.asList(mockAttemptReport);
        // Create one mock containerReport
        ContainerId containerId1 = ContainerId.newContainerId(appAttemptId, 1);
        ContainerReport mockContainerReport1 = Mockito.mock(ContainerReport.class);
        Mockito.doReturn(containerId1).when(mockContainerReport1).getContainerId();
        Mockito.doReturn(nodeId).when(mockContainerReport1).getAssignedNode();
        Mockito.doReturn("http://localhost:2345").when(mockContainerReport1).getNodeHttpAddress();
        Mockito.doReturn(RUNNING).when(mockContainerReport1).getContainerState();
        List<ContainerReport> containerReports = Arrays.asList(mockContainerReport1);
        // Mock the YarnClient, and it would report the previous created
        // mockAttemptReport and previous two created mockContainerReports
        YarnClient mockYarnClient = createMockYarnClient(YarnApplicationState.RUNNING, ugi.getShortUserName(), true, attemptReports, containerReports);
        Mockito.doReturn(mockContainerReport1).when(mockYarnClient).getContainerReport(ArgumentMatchers.any(ContainerId.class));
        // create local logs
        Configuration configuration = new YarnConfiguration();
        FileSystem fs = FileSystem.get(configuration);
        String rootLogDir = "target/LocalLogs";
        Path rootLogDirPath = new Path(rootLogDir);
        if (fs.exists(rootLogDirPath)) {
            fs.delete(rootLogDirPath, true);
        }
        Assert.assertTrue(fs.mkdirs(rootLogDirPath));
        Path appLogsDir = new Path(rootLogDirPath, appId.toString());
        if (fs.exists(appLogsDir)) {
            fs.delete(appLogsDir, true);
        }
        Assert.assertTrue(fs.mkdirs(appLogsDir));
        String fileName = "syslog";
        List<String> logTypes = new ArrayList<String>();
        logTypes.add(fileName);
        // create container logs in localLogDir
        TestLogsCLI.createContainerLogInLocalDir(appLogsDir, containerId1, fs, logTypes);
        Path containerDirPath = new Path(appLogsDir, containerId1.toString());
        Path logPath = new Path(containerDirPath, fileName);
        File logFile = new File(logPath.toString());
        final FileInputStream fis = new FileInputStream(logFile);
        try {
            LogsCLI cli = Mockito.spy(new TestLogsCLI.LogsCLIForTest(mockYarnClient));
            Set<String> logsSet = new HashSet<String>();
            logsSet.add(fileName);
            Mockito.doReturn(logsSet).when(cli).getMatchedContainerLogFiles(ArgumentMatchers.any(ContainerLogsRequest.class), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean());
            ClientResponse mockReponse = Mockito.mock(ClientResponse.class);
            Mockito.doReturn(OK).when(mockReponse).getStatusInfo();
            Mockito.doReturn(fis).when(mockReponse).getEntityInputStream();
            Mockito.doReturn(mockReponse).when(cli).getResponeFromNMWebService(ArgumentMatchers.any(Configuration.class), ArgumentMatchers.any(Client.class), ArgumentMatchers.any(ContainerLogsRequest.class), ArgumentMatchers.anyString());
            cli.setConf(new YarnConfiguration());
            int exitCode = cli.run(new String[]{ "-containerId", containerId1.toString() });
            Assert.assertTrue((exitCode == 0));
            Assert.assertTrue(sysOutStream.toString().contains(TestLogsCLI.logMessage(containerId1, "syslog")));
            sysOutStream.reset();
        } finally {
            IOUtils.closeQuietly(fis);
            fs.delete(new Path(rootLogDir), true);
        }
    }

    @Test(timeout = 5000)
    public void testFetchRunningApplicationLogs() throws Exception {
        UserGroupInformation ugi = UserGroupInformation.getCurrentUser();
        NodeId nodeId = NodeId.newInstance("localhost", 1234);
        ApplicationId appId = ApplicationId.newInstance(0, 1);
        ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(appId, 1);
        // Create a mock ApplicationAttempt Report
        ApplicationAttemptReport mockAttemptReport = Mockito.mock(ApplicationAttemptReport.class);
        Mockito.doReturn(appAttemptId).when(mockAttemptReport).getApplicationAttemptId();
        List<ApplicationAttemptReport> attemptReports = Arrays.asList(mockAttemptReport);
        // Create two mock containerReports
        ContainerId containerId1 = ContainerId.newContainerId(appAttemptId, 1);
        ContainerReport mockContainerReport1 = Mockito.mock(ContainerReport.class);
        Mockito.doReturn(containerId1).when(mockContainerReport1).getContainerId();
        Mockito.doReturn(nodeId).when(mockContainerReport1).getAssignedNode();
        Mockito.doReturn("http://localhost:2345").when(mockContainerReport1).getNodeHttpAddress();
        ContainerId containerId2 = ContainerId.newContainerId(appAttemptId, 2);
        ContainerReport mockContainerReport2 = Mockito.mock(ContainerReport.class);
        Mockito.doReturn(containerId2).when(mockContainerReport2).getContainerId();
        Mockito.doReturn(nodeId).when(mockContainerReport2).getAssignedNode();
        Mockito.doReturn("http://localhost:2345").when(mockContainerReport2).getNodeHttpAddress();
        List<ContainerReport> containerReports = Arrays.asList(mockContainerReport1, mockContainerReport2);
        // Mock the YarnClient, and it would report the previous created
        // mockAttemptReport and previous two created mockContainerReports
        YarnClient mockYarnClient = createMockYarnClient(YarnApplicationState.RUNNING, ugi.getShortUserName(), true, attemptReports, containerReports);
        LogsCLI cli = Mockito.spy(new TestLogsCLI.LogsCLIForTest(mockYarnClient));
        Mockito.doReturn(0).when(cli).printContainerLogsFromRunningApplication(ArgumentMatchers.any(Configuration.class), ArgumentMatchers.any(ContainerLogsRequest.class), ArgumentMatchers.any(LogCLIHelpers.class), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean());
        Set<String> logTypes = new HashSet<>();
        logTypes.add("ALL");
        ContainerLogsRequest mockContainer1 = Mockito.mock(ContainerLogsRequest.class);
        Mockito.doReturn(logTypes).when(mockContainer1).getLogTypes();
        ContainerLogsRequest mockContainer2 = Mockito.mock(ContainerLogsRequest.class);
        Mockito.doReturn(logTypes).when(mockContainer2).getLogTypes();
        Map<String, ContainerLogsRequest> matchedLogTypes = new HashMap<>();
        matchedLogTypes.put(containerId1.toString(), mockContainer1);
        matchedLogTypes.put(containerId2.toString(), mockContainer2);
        Mockito.doReturn(matchedLogTypes).when(cli).getMatchedLogTypesForRunningApp(ArgumentMatchers.anyList(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean());
        cli.setConf(new YarnConfiguration());
        int exitCode = cli.run(new String[]{ "-applicationId", appId.toString() });
        Assert.assertTrue((exitCode == 0));
        ArgumentCaptor<ContainerLogsRequest> logsRequestCaptor = ArgumentCaptor.forClass(ContainerLogsRequest.class);
        // we have two container reports, so make sure we have called
        // printContainerLogsFromRunningApplication twice
        Mockito.verify(cli, Mockito.times(2)).printContainerLogsFromRunningApplication(ArgumentMatchers.any(Configuration.class), logsRequestCaptor.capture(), ArgumentMatchers.any(LogCLIHelpers.class), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean());
        // Verify that the log-type is "ALL"
        List<ContainerLogsRequest> capturedRequests = logsRequestCaptor.getAllValues();
        Assert.assertEquals(2, capturedRequests.size());
        Set<String> logTypes0 = capturedRequests.get(0).getLogTypes();
        Set<String> logTypes1 = capturedRequests.get(1).getLogTypes();
        Assert.assertTrue(((logTypes0.contains("ALL")) && ((logTypes0.size()) == 1)));
        Assert.assertTrue(((logTypes1.contains("ALL")) && ((logTypes1.size()) == 1)));
        mockYarnClient = createMockYarnClientWithException(YarnApplicationState.RUNNING, ugi.getShortUserName());
        LogsCLI cli2 = Mockito.spy(new TestLogsCLI.LogsCLIForTest(mockYarnClient));
        ContainerLogsRequest newOption = Mockito.mock(ContainerLogsRequest.class);
        Mockito.doReturn(newOption).when(cli2).getMatchedOptionForRunningApp(ArgumentMatchers.any(ContainerLogsRequest.class), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean());
        Mockito.doReturn(0).when(cli2).printContainerLogsFromRunningApplication(ArgumentMatchers.any(Configuration.class), ArgumentMatchers.any(ContainerLogsRequest.class), ArgumentMatchers.any(LogCLIHelpers.class), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean());
        Mockito.doReturn("123").when(cli2).getNodeHttpAddressFromRMWebString(ArgumentMatchers.any(ContainerLogsRequest.class));
        cli2.setConf(new YarnConfiguration());
        ContainerId containerId100 = ContainerId.newContainerId(appAttemptId, 100);
        exitCode = cli2.run(new String[]{ "-applicationId", appId.toString(), "-containerId", containerId100.toString(), "-nodeAddress", "NM:1234" });
        Assert.assertTrue((exitCode == 0));
        Mockito.verify(cli2, Mockito.times(1)).printContainerLogsFromRunningApplication(ArgumentMatchers.any(Configuration.class), logsRequestCaptor.capture(), ArgumentMatchers.any(LogCLIHelpers.class), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean());
    }

    @Test(timeout = 15000)
    public void testFetchApplictionLogsAsAnotherUser() throws Exception {
        String remoteLogRootDir = "target/logs/";
        String rootLogDir = "target/LocalLogs";
        String testUser = "test";
        UserGroupInformation testUgi = UserGroupInformation.createRemoteUser(testUser);
        Configuration configuration = new YarnConfiguration();
        configuration.setBoolean(LOG_AGGREGATION_ENABLED, true);
        configuration.set(NM_REMOTE_APP_LOG_DIR, remoteLogRootDir);
        configuration.setBoolean(YARN_ACL_ENABLE, true);
        configuration.set(YARN_ADMIN_ACL, "admin");
        FileSystem fs = FileSystem.get(configuration);
        ApplicationId appId = ApplicationId.newInstance(0, 1);
        ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(appId, 1);
        ContainerId containerId = ContainerId.newContainerId(appAttemptId, 1);
        NodeId nodeId = NodeId.newInstance("localhost", 1234);
        try {
            Path rootLogDirPath = new Path(rootLogDir);
            if (fs.exists(rootLogDirPath)) {
                fs.delete(rootLogDirPath, true);
            }
            Assert.assertTrue(fs.mkdirs(rootLogDirPath));
            // create local app dir for app
            final Path appLogsDir = new Path(rootLogDirPath, appId.toString());
            if (fs.exists(appLogsDir)) {
                fs.delete(appLogsDir, true);
            }
            Assert.assertTrue(fs.mkdirs(appLogsDir));
            List<String> rootLogDirs = Arrays.asList(rootLogDir);
            List<String> logTypes = new ArrayList<String>();
            logTypes.add("syslog");
            // create container logs in localLogDir for app
            TestLogsCLI.createContainerLogInLocalDir(appLogsDir, containerId, fs, logTypes);
            // create the remote app dir for app
            // but for a different user testUser"
            Path path = new Path((((remoteLogRootDir + testUser) + "/logs/") + appId));
            if (fs.exists(path)) {
                fs.delete(path, true);
            }
            Assert.assertTrue(fs.mkdirs(path));
            // upload container logs for app into remote dir
            TestLogsCLI.uploadContainerLogIntoRemoteDir(testUgi, configuration, rootLogDirs, nodeId, containerId, path, fs);
            YarnClient mockYarnClient = createMockYarnClient(FINISHED, testUgi.getShortUserName());
            LogsCLI cli = new TestLogsCLI.LogsCLIForTest(mockYarnClient);
            cli.setConf(configuration);
            // Verify that we can get the application logs by specifying
            // a correct appOwner
            int exitCode = cli.run(new String[]{ "-applicationId", appId.toString(), "-appOwner", testUser });
            Assert.assertTrue((exitCode == 0));
            Assert.assertTrue(sysOutStream.toString().contains(TestLogsCLI.logMessage(containerId, "syslog")));
            sysOutStream.reset();
            // Verify that we can not get the application logs
            // if an invalid user is specified
            exitCode = cli.run(new String[]{ "-applicationId", appId.toString(), "-appOwner", "invalid" });
            Assert.assertTrue((exitCode == (-1)));
            Assert.assertTrue(sysErrStream.toString().contains((("Can not find the logs " + "for the application: ") + (appId.toString()))));
            sysErrStream.reset();
            // Verify that we do not specify appOwner, and can not
            // get appReport from RM, we still can figure out the appOwner
            // and can get app logs successfully.
            YarnClient mockYarnClient2 = createMockYarnClientUnknownApp();
            cli = new TestLogsCLI.LogsCLIForTest(mockYarnClient2);
            cli.setConf(configuration);
            exitCode = cli.run(new String[]{ "-applicationId", appId.toString() });
            Assert.assertTrue((exitCode == 0));
            Assert.assertTrue(sysOutStream.toString().contains(TestLogsCLI.logMessage(containerId, "syslog")));
            sysOutStream.reset();
            // Verify that we could get the err message "Can not find the appOwner"
            // if we do not specify the appOwner, can not get appReport, and
            // the app does not exist in remote dir.
            ApplicationId appId2 = ApplicationId.newInstance(System.currentTimeMillis(), 2);
            exitCode = cli.run(new String[]{ "-applicationId", appId2.toString() });
            Assert.assertTrue((exitCode == (-1)));
            Assert.assertTrue(sysErrStream.toString().contains("Can not find the appOwner"));
            sysErrStream.reset();
            // Verify that we could not get appOwner
            // because we don't have file-system permissions
            ApplicationId appTest = ApplicationId.newInstance(System.currentTimeMillis(), 1000);
            String priorityUser = "priority";
            Path pathWithoutPerm = new Path((((remoteLogRootDir + priorityUser) + "/logs/") + appTest));
            if (fs.exists(pathWithoutPerm)) {
                fs.delete(pathWithoutPerm, true);
            }
            // The user will not have read permission for this directory.
            // To mimic the scenario that the user can not get file status
            FsPermission permission = FsPermission.createImmutable(((short) (704)));
            Assert.assertTrue(fs.mkdirs(pathWithoutPerm, permission));
            exitCode = cli.run(new String[]{ "-applicationId", appTest.toString() });
            Assert.assertTrue((exitCode == (-1)));
            Assert.assertTrue(sysErrStream.toString().contains(("Can not find the logs for the application: " + (appTest.toString()))));
            sysErrStream.reset();
        } finally {
            fs.delete(new Path(remoteLogRootDir), true);
            fs.delete(new Path(rootLogDir), true);
        }
    }

    @Test(timeout = 5000)
    public void testLogsCLIWithInvalidArgs() throws Exception {
        String localDir = "target/SaveLogs";
        Path localPath = new Path(localDir);
        Configuration configuration = new YarnConfiguration();
        FileSystem fs = FileSystem.get(configuration);
        ApplicationId appId = ApplicationId.newInstance(0, 1);
        YarnClient mockYarnClient = createMockYarnClient(FINISHED, UserGroupInformation.getCurrentUser().getShortUserName());
        LogsCLI cli = new TestLogsCLI.LogsCLIForTest(mockYarnClient);
        cli.setConf(configuration);
        // Specify an invalid applicationId
        int exitCode = cli.run(new String[]{ "-applicationId", "123" });
        Assert.assertTrue((exitCode == (-1)));
        Assert.assertTrue(sysErrStream.toString().contains("Invalid ApplicationId specified"));
        sysErrStream.reset();
        // Specify an invalid containerId
        exitCode = cli.run(new String[]{ "-containerId", "123" });
        Assert.assertTrue((exitCode == (-1)));
        Assert.assertTrue(sysErrStream.toString().contains("Invalid ContainerId specified"));
        sysErrStream.reset();
        // Specify show_container_log_info and show_application_log_info
        // at the same time
        exitCode = cli.run(new String[]{ "-applicationId", appId.toString(), "-show_container_log_info", "-show_application_log_info" });
        Assert.assertTrue((exitCode == (-1)));
        Assert.assertTrue(sysErrStream.toString().contains(("Invalid options. " + ("Can only accept one of show_application_log_info/" + "show_container_log_info."))));
        sysErrStream.reset();
        // Specify log_files and log_files_pattern
        // at the same time
        exitCode = cli.run(new String[]{ "-applicationId", appId.toString(), "-log_files", "*", "-log_files_pattern", ".*" });
        Assert.assertTrue((exitCode == (-1)));
        Assert.assertTrue(sysErrStream.toString().contains(("Invalid options. " + ("Can only accept one of log_files/" + "log_files_pattern."))));
        sysErrStream.reset();
        // Specify a file name to the option -out
        try {
            fs.mkdirs(localPath);
            Path tmpFilePath = new Path(localPath, "tmpFile");
            if (!(fs.exists(tmpFilePath))) {
                fs.createNewFile(tmpFilePath);
            }
            exitCode = cli.run(new String[]{ "-applicationId", appId.toString(), "-out", tmpFilePath.toString() });
            Assert.assertTrue((exitCode == (-1)));
            Assert.assertTrue(sysErrStream.toString().contains("Invalid value for -out option. Please provide a directory."));
        } finally {
            fs.delete(localPath, true);
        }
    }

    @Test(timeout = 15000)
    public void testSaveContainerLogsLocally() throws Exception {
        String remoteLogRootDir = "target/logs/";
        String rootLogDir = "target/LocalLogs";
        String localDir = "target/SaveLogs";
        Path localPath = new Path(localDir);
        Configuration configuration = new YarnConfiguration();
        configuration.setBoolean(LOG_AGGREGATION_ENABLED, true);
        configuration.set(NM_REMOTE_APP_LOG_DIR, remoteLogRootDir);
        configuration.setBoolean(YARN_ACL_ENABLE, true);
        configuration.set(YARN_ADMIN_ACL, "admin");
        FileSystem fs = FileSystem.get(configuration);
        ApplicationId appId = ApplicationId.newInstance(0, 1);
        ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(appId, 1);
        List<ContainerId> containerIds = new ArrayList<ContainerId>();
        ContainerId containerId1 = ContainerId.newContainerId(appAttemptId, 1);
        ContainerId containerId2 = ContainerId.newContainerId(appAttemptId, 2);
        containerIds.add(containerId1);
        containerIds.add(containerId2);
        List<NodeId> nodeIds = new ArrayList<NodeId>();
        NodeId nodeId = NodeId.newInstance("localhost", 1234);
        NodeId nodeId2 = NodeId.newInstance("test", 4567);
        nodeIds.add(nodeId);
        nodeIds.add(nodeId2);
        try {
            createContainerLogs(configuration, remoteLogRootDir, rootLogDir, fs, appId, containerIds, nodeIds);
            YarnClient mockYarnClient = createMockYarnClient(FINISHED, UserGroupInformation.getCurrentUser().getShortUserName());
            LogsCLI cli = new TestLogsCLI.LogsCLIForTest(mockYarnClient);
            cli.setConf(configuration);
            int exitCode = cli.run(new String[]{ "-applicationId", appId.toString(), "-out", localPath.toString() });
            Assert.assertTrue((exitCode == 0));
            // make sure we created a dir named as node id
            FileStatus[] nodeDir = fs.listStatus(localPath);
            Arrays.sort(nodeDir);
            Assert.assertTrue(((nodeDir.length) == 2));
            Assert.assertTrue(nodeDir[0].getPath().getName().contains(LogAggregationUtils.getNodeString(nodeId)));
            Assert.assertTrue(nodeDir[1].getPath().getName().contains(LogAggregationUtils.getNodeString(nodeId2)));
            FileStatus[] container1Dir = fs.listStatus(nodeDir[0].getPath());
            Assert.assertTrue(((container1Dir.length) == 1));
            Assert.assertTrue(container1Dir[0].getPath().getName().equals(containerId1.toString()));
            String container1 = readContainerContent(container1Dir[0].getPath(), fs);
            Assert.assertTrue(container1.contains(TestLogsCLI.logMessage(containerId1, "syslog")));
            FileStatus[] container2Dir = fs.listStatus(nodeDir[1].getPath());
            Assert.assertTrue(((container2Dir.length) == 1));
            Assert.assertTrue(container2Dir[0].getPath().getName().equals(containerId2.toString()));
            String container2 = readContainerContent(container2Dir[0].getPath(), fs);
            Assert.assertTrue(container2.contains(TestLogsCLI.logMessage(containerId2, "syslog")));
        } finally {
            fs.delete(new Path(remoteLogRootDir), true);
            fs.delete(new Path(rootLogDir), true);
            fs.delete(localPath, true);
        }
    }

    @Test(timeout = 15000)
    public void testPrintContainerLogMetadata() throws Exception {
        String remoteLogRootDir = "target/logs/";
        Configuration configuration = new YarnConfiguration();
        configuration.setBoolean(LOG_AGGREGATION_ENABLED, true);
        configuration.set(NM_REMOTE_APP_LOG_DIR, remoteLogRootDir);
        configuration.setBoolean(YARN_ACL_ENABLE, true);
        configuration.set(YARN_ADMIN_ACL, "admin");
        FileSystem fs = FileSystem.get(configuration);
        String rootLogDir = "target/LocalLogs";
        ApplicationId appId = ApplicationId.newInstance(0, 1);
        ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(appId, 1);
        List<ContainerId> containerIds = new ArrayList<ContainerId>();
        ContainerId containerId1 = ContainerId.newContainerId(appAttemptId, 1);
        ContainerId containerId2 = ContainerId.newContainerId(appAttemptId, 2);
        containerIds.add(containerId1);
        containerIds.add(containerId2);
        List<NodeId> nodeIds = new ArrayList<NodeId>();
        NodeId nodeId = NodeId.newInstance("localhost", 1234);
        nodeIds.add(nodeId);
        nodeIds.add(nodeId);
        createContainerLogs(configuration, remoteLogRootDir, rootLogDir, fs, appId, containerIds, nodeIds);
        YarnClient mockYarnClient = createMockYarnClient(FINISHED, UserGroupInformation.getCurrentUser().getShortUserName());
        LogsCLI cli = new TestLogsCLI.LogsCLIForTest(mockYarnClient);
        cli.setConf(configuration);
        cli.run(new String[]{ "-applicationId", appId.toString(), "-show_container_log_info" });
        Assert.assertTrue(sysOutStream.toString().contains("Container: container_0_0001_01_000001 on localhost_"));
        Assert.assertTrue(sysOutStream.toString().contains("Container: container_0_0001_01_000002 on localhost_"));
        Assert.assertTrue(sysOutStream.toString().contains("syslog"));
        Assert.assertTrue(sysOutStream.toString().contains("43"));
        sysOutStream.reset();
        cli.run(new String[]{ "-applicationId", appId.toString(), "-show_container_log_info", "-containerId", "container_0_0001_01_000001" });
        Assert.assertTrue(sysOutStream.toString().contains("Container: container_0_0001_01_000001 on localhost_"));
        Assert.assertFalse(sysOutStream.toString().contains("Container: container_0_0001_01_000002 on localhost_"));
        Assert.assertTrue(sysOutStream.toString().contains("syslog"));
        Assert.assertTrue(sysOutStream.toString().contains("43"));
        sysOutStream.reset();
        cli.run(new String[]{ "-applicationId", appId.toString(), "-show_container_log_info", "-nodeAddress", "localhost" });
        Assert.assertTrue(sysOutStream.toString().contains("Container: container_0_0001_01_000001 on localhost_"));
        Assert.assertTrue(sysOutStream.toString().contains("Container: container_0_0001_01_000002 on localhost_"));
        Assert.assertTrue(sysOutStream.toString().contains("syslog"));
        Assert.assertTrue(sysOutStream.toString().contains("43"));
        sysOutStream.reset();
        cli.run(new String[]{ "-applicationId", appId.toString(), "-show_container_log_info", "-nodeAddress", "localhost", "-containerId", "container_1234" });
        Assert.assertTrue(sysErrStream.toString().contains("Invalid ContainerId specified"));
        sysErrStream.reset();
        cli.run(new String[]{ "-applicationId", appId.toString(), "-show_application_log_info" });
        Assert.assertTrue(sysOutStream.toString().contains("Application State: Completed."));
        Assert.assertTrue(sysOutStream.toString().contains("container_0_0001_01_000001 on localhost"));
        Assert.assertTrue(sysOutStream.toString().contains("container_0_0001_01_000002 on localhost"));
        sysOutStream.reset();
        cli.run(new String[]{ "-applicationId", appId.toString(), "-show_application_log_info", "-nodeAddress", "localhost" });
        Assert.assertTrue(sysOutStream.toString().contains("Application State: Completed."));
        Assert.assertTrue(sysOutStream.toString().contains("container_0_0001_01_000001 on localhost"));
        Assert.assertTrue(sysOutStream.toString().contains("container_0_0001_01_000002 on localhost"));
        sysOutStream.reset();
        fs.delete(new Path(remoteLogRootDir), true);
        fs.delete(new Path(rootLogDir), true);
    }

    @Test(timeout = 15000)
    public void testListNodeInfo() throws Exception {
        String remoteLogRootDir = "target/logs/";
        Configuration configuration = new YarnConfiguration();
        configuration.setBoolean(LOG_AGGREGATION_ENABLED, true);
        configuration.set(NM_REMOTE_APP_LOG_DIR, remoteLogRootDir);
        configuration.setBoolean(YARN_ACL_ENABLE, true);
        configuration.set(YARN_ADMIN_ACL, "admin");
        ApplicationId appId = ApplicationId.newInstance(0, 1);
        ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(appId, 1);
        List<ContainerId> containerIds = new ArrayList<ContainerId>();
        ContainerId containerId1 = ContainerId.newContainerId(appAttemptId, 1);
        ContainerId containerId2 = ContainerId.newContainerId(appAttemptId, 2);
        containerIds.add(containerId1);
        containerIds.add(containerId2);
        List<NodeId> nodeIds = new ArrayList<NodeId>();
        NodeId nodeId1 = NodeId.newInstance("localhost1", 1234);
        NodeId nodeId2 = NodeId.newInstance("localhost2", 2345);
        nodeIds.add(nodeId1);
        nodeIds.add(nodeId2);
        String rootLogDir = "target/LocalLogs";
        FileSystem fs = FileSystem.get(configuration);
        createContainerLogs(configuration, remoteLogRootDir, rootLogDir, fs, appId, containerIds, nodeIds);
        YarnClient mockYarnClient = createMockYarnClient(FINISHED, UserGroupInformation.getCurrentUser().getShortUserName());
        LogsCLI cli = new TestLogsCLI.LogsCLIForTest(mockYarnClient);
        cli.setConf(configuration);
        cli.run(new String[]{ "-applicationId", appId.toString(), "-list_nodes" });
        Assert.assertTrue(sysOutStream.toString().contains(LogAggregationUtils.getNodeString(nodeId1)));
        Assert.assertTrue(sysOutStream.toString().contains(LogAggregationUtils.getNodeString(nodeId2)));
        sysOutStream.reset();
        fs.delete(new Path(remoteLogRootDir), true);
        fs.delete(new Path(rootLogDir), true);
    }

    @Test(timeout = 15000)
    public void testFetchApplictionLogsHar() throws Exception {
        String remoteLogRootDir = "target/logs/";
        Configuration configuration = new YarnConfiguration();
        configuration.setBoolean(LOG_AGGREGATION_ENABLED, true);
        configuration.set(NM_REMOTE_APP_LOG_DIR, remoteLogRootDir);
        configuration.setBoolean(YARN_ACL_ENABLE, true);
        configuration.set(YARN_ADMIN_ACL, "admin");
        FileSystem fs = FileSystem.get(configuration);
        UserGroupInformation ugi = UserGroupInformation.getCurrentUser();
        URL harUrl = ClassLoader.getSystemClassLoader().getResource("application_1440536969523_0001.har");
        Assert.assertNotNull(harUrl);
        Path path = new Path(((remoteLogRootDir + (ugi.getShortUserName())) + "/logs/application_1440536969523_0001"));
        if (fs.exists(path)) {
            fs.delete(path, true);
        }
        Assert.assertTrue(fs.mkdirs(path));
        Path harPath = new Path(path, "application_1440536969523_0001.har");
        fs.copyFromLocalFile(false, new Path(harUrl.toURI()), harPath);
        Assert.assertTrue(fs.exists(harPath));
        YarnClient mockYarnClient = createMockYarnClient(FINISHED, ugi.getShortUserName());
        LogsCLI cli = new TestLogsCLI.LogsCLIForTest(mockYarnClient);
        cli.setConf(configuration);
        int exitCode = cli.run(new String[]{ "-applicationId", "application_1440536969523_0001" });
        Assert.assertTrue((exitCode == 0));
        String out = sysOutStream.toString();
        Assert.assertTrue(out.contains("container_1440536969523_0001_01_000001 on host1_1111"));
        Assert.assertTrue(out.contains("Hello stderr"));
        Assert.assertTrue(out.contains("Hello stdout"));
        Assert.assertTrue(out.contains("Hello syslog"));
        Assert.assertTrue(out.contains("container_1440536969523_0001_01_000002 on host2_2222"));
        Assert.assertTrue(out.contains("Goodbye stderr"));
        Assert.assertTrue(out.contains("Goodbye stdout"));
        Assert.assertTrue(out.contains("Goodbye syslog"));
        sysOutStream.reset();
        fs.delete(new Path(remoteLogRootDir), true);
    }

    private static class LogsCLIForTest extends LogsCLI {
        private YarnClient yarnClient;

        public LogsCLIForTest(YarnClient yarnClient) {
            super();
            this.yarnClient = yarnClient;
        }

        protected YarnClient createYarnClient() {
            return yarnClient;
        }
    }
}

