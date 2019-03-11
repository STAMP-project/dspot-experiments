/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.utils;


import HostComponentAdminState.DECOMMISSIONED;
import HostComponentAdminState.INSERVICE;
import StageUtils.AMBARI_SERVER_HOST;
import StageUtils.DEFAULT_PING_PORT;
import StageUtils.HOSTS_LIST;
import StageUtils.PORTS;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.inject.Injector;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.JAXBException;
import org.apache.ambari.server.actionmanager.ExecutionCommandWrapper;
import org.apache.ambari.server.actionmanager.Stage;
import org.apache.ambari.server.actionmanager.StageFactory;
import org.apache.ambari.server.agent.ExecutionCommand;
import org.apache.ambari.server.api.services.AmbariMetaInfo;
import org.apache.ambari.server.configuration.Configuration;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.Host;
import org.apache.ambari.server.state.Service;
import org.apache.ambari.server.state.ServiceComponent;
import org.apache.ambari.server.state.ServiceComponentHost;
import org.apache.ambari.server.topology.TopologyManager;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.easymock.EasyMockSupport;
import org.junit.Assert;
import org.junit.Test;


public class StageUtilsTest extends EasyMockSupport {
    private static final String STACK_ID = "HDP-1.3.1";

    private Injector injector;

    @Test
    public void testGetATestStage() {
        StageUtils stageUtils = new StageUtils(injector.getInstance(StageFactory.class));
        Stage s = StageUtils.getATestStage(1, 2, "host2", "", "hostParamsStage");
        String hostname = s.getHosts().get(0);
        List<ExecutionCommandWrapper> wrappers = s.getExecutionCommands(hostname);
        for (ExecutionCommandWrapper wrapper : wrappers) {
            Assert.assertEquals("cluster1", wrapper.getExecutionCommand().getClusterName());
            Assert.assertEquals(StageUtils.getActionId(1, 2), wrapper.getExecutionCommand().getCommandId());
            Assert.assertEquals(hostname, wrapper.getExecutionCommand().getHostname());
        }
    }

    @Test
    public void testJaxbToString() throws Exception {
        StageUtils stageUtils = new StageUtils(injector.getInstance(StageFactory.class));
        Stage s = StageUtils.getATestStage(1, 2, "host1", "", "hostParamsStage");
        String hostname = s.getHosts().get(0);
        List<ExecutionCommandWrapper> wrappers = s.getExecutionCommands(hostname);
        for (ExecutionCommandWrapper wrapper : wrappers) {
            // Why are we logging in test case?
            // LOG.info("Command is " + StageUtils.jaxbToString(wrapper.getExecutionCommand()));
        }
        Assert.assertEquals(StageUtils.getActionId(1, 2), s.getActionId());
    }

    @Test
    public void testJasonToExecutionCommand() throws IOException, JAXBException, JsonGenerationException, JsonMappingException {
        StageUtils stageUtils = new StageUtils(injector.getInstance(StageFactory.class));
        Stage s = StageUtils.getATestStage(1, 2, "host1", "clusterHostInfo", "hostParamsStage");
        ExecutionCommand cmd = s.getExecutionCommands("host1").get(0).getExecutionCommand();
        String json = StageUtils.jaxbToString(cmd);
        InputStream is = new ByteArrayInputStream(json.getBytes(Charset.forName("UTF8")));
        ExecutionCommand cmdDes = new Gson().fromJson(new InputStreamReader(is), ExecutionCommand.class);
        Assert.assertEquals(cmd.toString(), cmdDes.toString());
        Assert.assertEquals(cmd, cmdDes);
    }

    @Test
    public void testGetClusterHostInfo() throws Exception {
        final HashMap<String, String> hostAttributes = new HashMap<String, String>() {
            {
                put("os_family", "redhat");
                put("os_release_version", "5.9");
            }
        };
        final Clusters clusters = createNiceMock(Clusters.class);
        List<Host> hosts = new ArrayList<>();
        List<String> hostNames = new ArrayList<>();
        List<Integer> pingPorts = Arrays.asList(DEFAULT_PING_PORT, DEFAULT_PING_PORT, DEFAULT_PING_PORT, 8671, 8671, null, 8672, 8672, null, 8673);
        for (int i = 0; i < 10; i++) {
            String hostname = String.format("h%d", i);
            Host host = createNiceMock(Host.class);
            expect(host.getHostName()).andReturn(hostname).anyTimes();
            expect(host.getHostAttributes()).andReturn(hostAttributes).anyTimes();
            expect(host.getCurrentPingPort()).andReturn(pingPorts.get(i)).anyTimes();
            hosts.add(host);
            hostNames.add(hostname);
            expect(clusters.getHost(hostname)).andReturn(host).anyTimes();
        }
        final ServiceComponentHost nnh0ServiceComponentHost = createMock(ServiceComponentHost.class);
        expect(nnh0ServiceComponentHost.getComponentAdminState()).andReturn(INSERVICE).anyTimes();
        final ServiceComponentHost snnh1ServiceComponentHost = createMock(ServiceComponentHost.class);
        expect(snnh1ServiceComponentHost.getComponentAdminState()).andReturn(INSERVICE).anyTimes();
        final ServiceComponentHost dnh0ServiceComponentHost = createMock(ServiceComponentHost.class);
        expect(dnh0ServiceComponentHost.getComponentAdminState()).andReturn(INSERVICE).anyTimes();
        final ServiceComponentHost dnh1ServiceComponentHost = createMock(ServiceComponentHost.class);
        expect(dnh1ServiceComponentHost.getComponentAdminState()).andReturn(INSERVICE).anyTimes();
        final ServiceComponentHost dnh2ServiceComponentHost = createMock(ServiceComponentHost.class);
        expect(dnh2ServiceComponentHost.getComponentAdminState()).andReturn(INSERVICE).anyTimes();
        final ServiceComponentHost dnh3ServiceComponentHost = createMock(ServiceComponentHost.class);
        expect(dnh3ServiceComponentHost.getComponentAdminState()).andReturn(INSERVICE).anyTimes();
        final ServiceComponentHost dnh5ServiceComponentHost = createMock(ServiceComponentHost.class);
        expect(dnh5ServiceComponentHost.getComponentAdminState()).andReturn(INSERVICE).anyTimes();
        final ServiceComponentHost dnh7ServiceComponentHost = createMock(ServiceComponentHost.class);
        expect(dnh7ServiceComponentHost.getComponentAdminState()).andReturn(INSERVICE).anyTimes();
        final ServiceComponentHost dnh8ServiceComponentHost = createMock(ServiceComponentHost.class);
        expect(dnh8ServiceComponentHost.getComponentAdminState()).andReturn(INSERVICE).anyTimes();
        final ServiceComponentHost dnh9ServiceComponentHost = createMock(ServiceComponentHost.class);
        expect(dnh9ServiceComponentHost.getComponentAdminState()).andReturn(INSERVICE).anyTimes();
        final ServiceComponentHost hbm5ServiceComponentHost = createMock(ServiceComponentHost.class);
        expect(hbm5ServiceComponentHost.getComponentAdminState()).andReturn(INSERVICE).anyTimes();
        final ServiceComponentHost hbrs1ServiceComponentHost = createMock(ServiceComponentHost.class);
        expect(hbrs1ServiceComponentHost.getComponentAdminState()).andReturn(INSERVICE).anyTimes();
        final ServiceComponentHost hbrs3ServiceComponentHost = createMock(ServiceComponentHost.class);
        expect(hbrs3ServiceComponentHost.getComponentAdminState()).andReturn(INSERVICE).anyTimes();
        final ServiceComponentHost hbrs5ServiceComponentHost = createMock(ServiceComponentHost.class);
        expect(hbrs5ServiceComponentHost.getComponentAdminState()).andReturn(INSERVICE).anyTimes();
        final ServiceComponentHost hbrs8ServiceComponentHost = createMock(ServiceComponentHost.class);
        expect(hbrs8ServiceComponentHost.getComponentAdminState()).andReturn(INSERVICE).anyTimes();
        final ServiceComponentHost hbrs9ServiceComponentHost = createMock(ServiceComponentHost.class);
        expect(hbrs9ServiceComponentHost.getComponentAdminState()).andReturn(INSERVICE).anyTimes();
        final ServiceComponentHost mrjt5ServiceComponentHost = createMock(ServiceComponentHost.class);
        expect(mrjt5ServiceComponentHost.getComponentAdminState()).andReturn(INSERVICE).anyTimes();
        final ServiceComponentHost mrtt1ServiceComponentHost = createMock(ServiceComponentHost.class);
        expect(mrtt1ServiceComponentHost.getComponentAdminState()).andReturn(INSERVICE).anyTimes();
        final ServiceComponentHost mrtt2ServiceComponentHost = createMock(ServiceComponentHost.class);
        expect(mrtt2ServiceComponentHost.getComponentAdminState()).andReturn(DECOMMISSIONED).anyTimes();
        final ServiceComponentHost mrtt3ServiceComponentHost = createMock(ServiceComponentHost.class);
        expect(mrtt3ServiceComponentHost.getComponentAdminState()).andReturn(DECOMMISSIONED).anyTimes();
        final ServiceComponentHost mrtt4ServiceComponentHost = createMock(ServiceComponentHost.class);
        expect(mrtt4ServiceComponentHost.getComponentAdminState()).andReturn(INSERVICE).anyTimes();
        final ServiceComponentHost mrtt5ServiceComponentHost = createMock(ServiceComponentHost.class);
        expect(mrtt5ServiceComponentHost.getComponentAdminState()).andReturn(INSERVICE).anyTimes();
        final ServiceComponentHost mrtt7ServiceComponentHost = createMock(ServiceComponentHost.class);
        expect(mrtt7ServiceComponentHost.getComponentAdminState()).andReturn(INSERVICE).anyTimes();
        final ServiceComponentHost mrtt9ServiceComponentHost = createMock(ServiceComponentHost.class);
        expect(mrtt9ServiceComponentHost.getComponentAdminState()).andReturn(INSERVICE).anyTimes();
        final ServiceComponentHost nns7ServiceComponentHost = createMock(ServiceComponentHost.class);
        expect(nns7ServiceComponentHost.getComponentAdminState()).andReturn(INSERVICE).anyTimes();
        Map<String, Collection<String>> projectedTopology = new HashMap<>();
        final HashMap<String, ServiceComponentHost> nnServiceComponentHosts = new HashMap<String, ServiceComponentHost>() {
            {
                put("h0", nnh0ServiceComponentHost);
            }
        };
        insertTopology(projectedTopology, "NAMENODE", nnServiceComponentHosts.keySet());
        final HashMap<String, ServiceComponentHost> snnServiceComponentHosts = new HashMap<String, ServiceComponentHost>() {
            {
                put("h1", snnh1ServiceComponentHost);
            }
        };
        insertTopology(projectedTopology, "SECONDARY_NAMENODE", snnServiceComponentHosts.keySet());
        final Map<String, ServiceComponentHost> dnServiceComponentHosts = new HashMap<String, ServiceComponentHost>() {
            {
                put("h0", dnh0ServiceComponentHost);
                put("h1", dnh1ServiceComponentHost);
                put("h2", dnh2ServiceComponentHost);
                put("h3", dnh3ServiceComponentHost);
                put("h5", dnh5ServiceComponentHost);
                put("h7", dnh7ServiceComponentHost);
                put("h8", dnh8ServiceComponentHost);
                put("h9", dnh9ServiceComponentHost);
            }
        };
        insertTopology(projectedTopology, "DATANODE", dnServiceComponentHosts.keySet());
        final Map<String, ServiceComponentHost> hbmServiceComponentHosts = new HashMap<String, ServiceComponentHost>() {
            {
                put("h5", hbm5ServiceComponentHost);
            }
        };
        insertTopology(projectedTopology, "HBASE_MASTER", hbmServiceComponentHosts.keySet());
        final Map<String, ServiceComponentHost> hbrsServiceComponentHosts = new HashMap<String, ServiceComponentHost>() {
            {
                put("h1", hbrs1ServiceComponentHost);
                put("h3", hbrs3ServiceComponentHost);
                put("h5", hbrs5ServiceComponentHost);
                put("h8", hbrs8ServiceComponentHost);
                put("h9", hbrs9ServiceComponentHost);
            }
        };
        insertTopology(projectedTopology, "HBASE_REGIONSERVER", hbrsServiceComponentHosts.keySet());
        final Map<String, ServiceComponentHost> mrjtServiceComponentHosts = new HashMap<String, ServiceComponentHost>() {
            {
                put("h5", mrjt5ServiceComponentHost);
            }
        };
        insertTopology(projectedTopology, "JOBTRACKER", mrjtServiceComponentHosts.keySet());
        final Map<String, ServiceComponentHost> mrttServiceComponentHosts = new HashMap<String, ServiceComponentHost>() {
            {
                put("h1", mrtt1ServiceComponentHost);
                put("h2", mrtt2ServiceComponentHost);
                put("h3", mrtt3ServiceComponentHost);
                put("h4", mrtt4ServiceComponentHost);
                put("h5", mrtt5ServiceComponentHost);
                put("h7", mrtt7ServiceComponentHost);
                put("h9", mrtt9ServiceComponentHost);
            }
        };
        insertTopology(projectedTopology, "TASKTRACKER", mrttServiceComponentHosts.keySet());
        final Map<String, ServiceComponentHost> nnsServiceComponentHosts = new HashMap<String, ServiceComponentHost>() {
            {
                put("h7", nns7ServiceComponentHost);
            }
        };
        insertTopology(projectedTopology, "NONAME_SERVER", nnsServiceComponentHosts.keySet());
        final ServiceComponent nnComponent = createMock(ServiceComponent.class);
        expect(nnComponent.getName()).andReturn("NAMENODE").anyTimes();
        expect(nnComponent.getServiceComponentHost(anyObject(String.class))).andAnswer(new org.easymock.IAnswer<ServiceComponentHost>() {
            @Override
            public ServiceComponentHost answer() throws Throwable {
                Object[] args = getCurrentArguments();
                return nnServiceComponentHosts.get(args[0]);
            }
        }).anyTimes();
        expect(nnComponent.getServiceComponentHosts()).andReturn(nnServiceComponentHosts).anyTimes();
        expect(nnComponent.isClientComponent()).andReturn(false).anyTimes();
        final ServiceComponent snnComponent = createMock(ServiceComponent.class);
        expect(snnComponent.getName()).andReturn("SECONDARY_NAMENODE").anyTimes();
        expect(snnComponent.getServiceComponentHost(anyObject(String.class))).andAnswer(new org.easymock.IAnswer<ServiceComponentHost>() {
            @Override
            public ServiceComponentHost answer() throws Throwable {
                Object[] args = getCurrentArguments();
                return snnServiceComponentHosts.get(args[0]);
            }
        }).anyTimes();
        expect(snnComponent.getServiceComponentHosts()).andReturn(snnServiceComponentHosts).anyTimes();
        expect(snnComponent.isClientComponent()).andReturn(false).anyTimes();
        final ServiceComponent dnComponent = createMock(ServiceComponent.class);
        expect(dnComponent.getName()).andReturn("DATANODE").anyTimes();
        expect(dnComponent.getServiceComponentHost(anyObject(String.class))).andAnswer(new org.easymock.IAnswer<ServiceComponentHost>() {
            @Override
            public ServiceComponentHost answer() throws Throwable {
                Object[] args = getCurrentArguments();
                return dnServiceComponentHosts.get(args[0]);
            }
        }).anyTimes();
        expect(dnComponent.getServiceComponentHosts()).andReturn(dnServiceComponentHosts).anyTimes();
        expect(dnComponent.isClientComponent()).andReturn(false).anyTimes();
        final ServiceComponent hbmComponent = createMock(ServiceComponent.class);
        expect(hbmComponent.getName()).andReturn("HBASE_MASTER").anyTimes();
        expect(hbmComponent.getServiceComponentHost(anyObject(String.class))).andAnswer(new org.easymock.IAnswer<ServiceComponentHost>() {
            @Override
            public ServiceComponentHost answer() throws Throwable {
                Object[] args = getCurrentArguments();
                return hbmServiceComponentHosts.get(args[0]);
            }
        }).anyTimes();
        expect(hbmComponent.getServiceComponentHosts()).andReturn(hbmServiceComponentHosts).anyTimes();
        expect(hbmComponent.isClientComponent()).andReturn(false).anyTimes();
        final ServiceComponent hbrsComponent = createMock(ServiceComponent.class);
        expect(hbrsComponent.getName()).andReturn("HBASE_REGIONSERVER").anyTimes();
        expect(hbrsComponent.getServiceComponentHost(anyObject(String.class))).andAnswer(new org.easymock.IAnswer<ServiceComponentHost>() {
            @Override
            public ServiceComponentHost answer() throws Throwable {
                Object[] args = getCurrentArguments();
                return hbrsServiceComponentHosts.get(args[0]);
            }
        }).anyTimes();
        Map<String, ServiceComponentHost> hbrsHosts = Maps.filterKeys(hbrsServiceComponentHosts, new Predicate<String>() {
            @Override
            public boolean apply(String s) {
                return s.equals("h1");
            }
        });
        expect(hbrsComponent.getServiceComponentHosts()).andReturn(hbrsServiceComponentHosts).anyTimes();
        expect(hbrsComponent.isClientComponent()).andReturn(false).anyTimes();
        final ServiceComponent mrjtComponent = createMock(ServiceComponent.class);
        expect(mrjtComponent.getName()).andReturn("JOBTRACKER").anyTimes();
        expect(mrjtComponent.getServiceComponentHost(anyObject(String.class))).andAnswer(new org.easymock.IAnswer<ServiceComponentHost>() {
            @Override
            public ServiceComponentHost answer() throws Throwable {
                Object[] args = getCurrentArguments();
                return mrjtServiceComponentHosts.get(args[0]);
            }
        }).anyTimes();
        expect(mrjtComponent.getServiceComponentHosts()).andReturn(mrjtServiceComponentHosts).anyTimes();
        expect(mrjtComponent.isClientComponent()).andReturn(false).anyTimes();
        final ServiceComponent mrttCompomnent = createMock(ServiceComponent.class);
        expect(mrttCompomnent.getName()).andReturn("TASKTRACKER").anyTimes();
        expect(mrttCompomnent.getServiceComponentHost(anyObject(String.class))).andAnswer(new org.easymock.IAnswer<ServiceComponentHost>() {
            @Override
            public ServiceComponentHost answer() throws Throwable {
                Object[] args = getCurrentArguments();
                return mrttServiceComponentHosts.get(args[0]);
            }
        }).anyTimes();
        expect(mrttCompomnent.getServiceComponentHosts()).andReturn(mrttServiceComponentHosts).anyTimes();
        expect(mrttCompomnent.isClientComponent()).andReturn(false).anyTimes();
        final ServiceComponent nnsComponent = createMock(ServiceComponent.class);
        expect(nnsComponent.getName()).andReturn("NONAME_SERVER").anyTimes();
        expect(nnsComponent.getServiceComponentHost(anyObject(String.class))).andAnswer(new org.easymock.IAnswer<ServiceComponentHost>() {
            @Override
            public ServiceComponentHost answer() throws Throwable {
                Object[] args = getCurrentArguments();
                return nnsServiceComponentHosts.get(args[0]);
            }
        }).anyTimes();
        expect(nnsComponent.getServiceComponentHosts()).andReturn(nnsServiceComponentHosts).anyTimes();
        expect(nnsComponent.isClientComponent()).andReturn(false).anyTimes();
        final Service hdfsService = createMock(Service.class);
        expect(hdfsService.getServiceComponents()).andReturn(new HashMap<String, ServiceComponent>() {
            {
                put("NAMENODE", nnComponent);
                put("SECONDARY_NAMENODE", snnComponent);
                put("DATANODE", dnComponent);
            }
        }).anyTimes();
        final Service hbaseService = createMock(Service.class);
        expect(hbaseService.getServiceComponents()).andReturn(new HashMap<String, ServiceComponent>() {
            {
                put("HBASE_MASTER", hbmComponent);
                put("HBASE_REGIONSERVER", hbrsComponent);
            }
        }).anyTimes();
        final Service mrService = createMock(Service.class);
        expect(mrService.getServiceComponents()).andReturn(new HashMap<String, ServiceComponent>() {
            {
                put("JOBTRACKER", mrjtComponent);
                put("TASKTRACKER", mrttCompomnent);
            }
        }).anyTimes();
        final Service nnService = createMock(Service.class);
        expect(nnService.getServiceComponents()).andReturn(new HashMap<String, ServiceComponent>() {
            {
                put("NONAME_SERVER", nnsComponent);
            }
        }).anyTimes();
        Cluster cluster = createMock(Cluster.class);
        expect(cluster.getHosts()).andReturn(hosts).anyTimes();
        expect(cluster.getServices()).andReturn(new HashMap<String, Service>() {
            {
                put("HDFS", hdfsService);
                put("HBASE", hbaseService);
                put("MAPREDUCE", mrService);
                put("NONAME", nnService);
            }
        }).anyTimes();
        final TopologyManager topologyManager = injector.getInstance(TopologyManager.class);
        topologyManager.getPendingHostComponents();
        expectLastCall().andReturn(projectedTopology).once();
        replayAll();
        // This is required by the infrastructure
        injector.getInstance(AmbariMetaInfo.class).init();
        // Get cluster host info
        Map<String, Set<String>> info = StageUtils.getClusterHostInfo(cluster);
        verifyAll();
        // All hosts present in cluster host info
        Set<String> allHosts = info.get(HOSTS_LIST);
        Assert.assertEquals(hosts.size(), allHosts.size());
        for (Host host : hosts) {
            Assert.assertTrue(allHosts.contains(host.getHostName()));
        }
        checkServiceHostIndexes(info, "DATANODE", "slave_hosts", projectedTopology, hostNames);
        checkServiceHostIndexes(info, "NAMENODE", "namenode_host", projectedTopology, hostNames);
        checkServiceHostIndexes(info, "SECONDARY_NAMENODE", "snamenode_host", projectedTopology, hostNames);
        checkServiceHostIndexes(info, "HBASE_MASTER", "hbase_master_hosts", projectedTopology, hostNames);
        checkServiceHostIndexes(info, "HBASE_REGIONSERVER", "hbase_rs_hosts", projectedTopology, hostNames);
        checkServiceHostIndexes(info, "JOBTRACKER", "jtnode_host", projectedTopology, hostNames);
        checkServiceHostIndexes(info, "TASKTRACKER", "mapred_tt_hosts", projectedTopology, hostNames);
        checkServiceHostIndexes(info, "NONAME_SERVER", "noname_server_hosts", projectedTopology, hostNames);
        Set<String> actualPingPorts = info.get(PORTS);
        if (pingPorts.contains(null)) {
            Assert.assertEquals(new HashSet<>(pingPorts).size(), ((actualPingPorts.size()) + 1));
        } else {
            Assert.assertEquals(new HashSet<>(pingPorts).size(), actualPingPorts.size());
        }
        List<Integer> pingPortsActual = getRangeMappedDecompressedSet(actualPingPorts);
        List<Integer> reindexedPorts = getReindexedList(pingPortsActual, new ArrayList<>(allHosts), hostNames);
        // Treat null values
        List<Integer> expectedPingPorts = new ArrayList<>(pingPorts);
        for (int i = 0; i < (expectedPingPorts.size()); i++) {
            if ((expectedPingPorts.get(i)) == null) {
                expectedPingPorts.set(i, DEFAULT_PING_PORT);
            }
        }
        Assert.assertEquals(expectedPingPorts, reindexedPorts);
        // check server hostname field
        Assert.assertTrue(info.containsKey(AMBARI_SERVER_HOST));
        Set<String> serverHost = info.get(AMBARI_SERVER_HOST);
        Assert.assertEquals(1, serverHost.size());
        Assert.assertEquals(StageUtils.getHostName(), serverHost.iterator().next());
        // check host role replacing by the projected topology
        Assert.assertTrue(getDecompressedSet(info.get("hbase_regionserver_hosts")).contains(9));
        // Validate substitutions...
        info = StageUtils.substituteHostIndexes(info);
        checkServiceHostNames(info, "DATANODE", projectedTopology);
        checkServiceHostNames(info, "NAMENODE", projectedTopology);
        checkServiceHostNames(info, "SECONDARY_NAMENODE", projectedTopology);
        checkServiceHostNames(info, "HBASE_MASTER", projectedTopology);
        checkServiceHostNames(info, "HBASE_REGIONSERVER", projectedTopology);
        checkServiceHostNames(info, "JOBTRACKER", projectedTopology);
        checkServiceHostNames(info, "TASKTRACKER", projectedTopology);
        checkServiceHostNames(info, "NONAME_SERVER", projectedTopology);
    }

    @Test
    public void testUseAmbariJdkWithoutavaHome() {
        // GIVEN
        Map<String, String> commandParams = new HashMap<>();
        Configuration configuration = new Configuration();
        // WHEN
        StageUtils.useAmbariJdkInCommandParams(commandParams, configuration);
        // THEN
        Assert.assertTrue(commandParams.isEmpty());
    }

    @Test
    public void testUseAmbariJdkWithCustomJavaHome() {
        // GIVEN
        Map<String, String> commandParams = new HashMap<>();
        Configuration configuration = new Configuration();
        configuration.setProperty("java.home", "myJavaHome");
        // WHEN
        StageUtils.useAmbariJdkInCommandParams(commandParams, configuration);
        // THEN
        Assert.assertEquals("myJavaHome", commandParams.get("ambari_java_home"));
        Assert.assertEquals(2, commandParams.size());
    }

    @Test
    public void testUseAmbariJdk() {
        // GIVEN
        Map<String, String> commandParams = new HashMap<>();
        Configuration configuration = new Configuration();
        configuration.setProperty("java.home", "myJavaHome");
        configuration.setProperty("jdk.name", "myJdkName");
        configuration.setProperty("jce.name", "myJceName");
        // WHEN
        StageUtils.useAmbariJdkInCommandParams(commandParams, configuration);
        // THEN
        Assert.assertEquals("myJavaHome", commandParams.get("ambari_java_home"));
        Assert.assertEquals("myJdkName", commandParams.get("ambari_jdk_name"));
        Assert.assertEquals("myJceName", commandParams.get("ambari_jce_name"));
        Assert.assertEquals(4, commandParams.size());
    }

    @Test
    public void testUseStackJdkIfExistsWithCustomStackJdk() {
        // GIVEN
        Map<String, String> hostLevelParams = new HashMap<>();
        Configuration configuration = new Configuration();
        configuration.setProperty("java.home", "myJavaHome");
        configuration.setProperty("jdk.name", "myJdkName");
        configuration.setProperty("jce.name", "myJceName");
        configuration.setProperty("stack.java.home", "myStackJavaHome");
        // WHEN
        StageUtils.useStackJdkIfExists(hostLevelParams, configuration);
        // THEN
        Assert.assertEquals("myStackJavaHome", hostLevelParams.get("java_home"));
        Assert.assertNull(hostLevelParams.get("jdk_name"));
        Assert.assertNull(hostLevelParams.get("jce_name"));
        Assert.assertEquals(4, hostLevelParams.size());
    }

    @Test
    public void testUseStackJdkIfExists() {
        // GIVEN
        Map<String, String> hostLevelParams = new HashMap<>();
        Configuration configuration = new Configuration();
        configuration.setProperty("java.home", "myJavaHome");
        configuration.setProperty("jdk.name", "myJdkName");
        configuration.setProperty("jce.name", "myJceName");
        configuration.setProperty("stack.java.home", "myStackJavaHome");
        configuration.setProperty("stack.jdk.name", "myStackJdkName");
        configuration.setProperty("stack.jce.name", "myStackJceName");
        configuration.setProperty("stack.java.version", "7");
        // WHEN
        StageUtils.useStackJdkIfExists(hostLevelParams, configuration);
        // THEN
        Assert.assertEquals("myStackJavaHome", hostLevelParams.get("java_home"));
        Assert.assertEquals("myStackJdkName", hostLevelParams.get("jdk_name"));
        Assert.assertEquals("myStackJceName", hostLevelParams.get("jce_name"));
        Assert.assertEquals("7", hostLevelParams.get("java_version"));
        Assert.assertEquals(4, hostLevelParams.size());
    }

    @Test
    public void testUseStackJdkIfExistsWithoutStackJdk() {
        // GIVEN
        Map<String, String> hostLevelParams = new HashMap<>();
        Configuration configuration = new Configuration();
        configuration.setProperty("java.home", "myJavaHome");
        configuration.setProperty("jdk.name", "myJdkName");
        configuration.setProperty("jce.name", "myJceName");
        // WHEN
        StageUtils.useStackJdkIfExists(hostLevelParams, configuration);
        // THEN
        Assert.assertEquals("myJavaHome", hostLevelParams.get("java_home"));
        Assert.assertEquals("myJdkName", hostLevelParams.get("jdk_name"));
        Assert.assertEquals("myJceName", hostLevelParams.get("jce_name"));
        Assert.assertEquals(4, hostLevelParams.size());
    }
}

