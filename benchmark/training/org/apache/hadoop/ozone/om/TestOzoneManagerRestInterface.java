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
package org.apache.hadoop.ozone.om;


import DatanodeDetails.Port.Name.REST;
import HddsProtos.NodeType;
import HddsProtos.NodeType.DATANODE;
import HddsProtos.NodeType.OM;
import HddsProtos.NodeType.SCM;
import ServicePort.Type;
import ServicePort.Type.HTTP;
import ServicePort.Type.RPC;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.hdds.HddsUtils;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdds.protocol.DatanodeDetails;
import org.apache.hadoop.hdds.protocol.proto.HddsProtos;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.ozone.MiniOzoneCluster;
import org.apache.hadoop.ozone.OmUtils;
import org.apache.hadoop.ozone.om.helpers.ServiceInfo;
import org.apache.hadoop.ozone.protocol.proto.OzoneManagerProtocolProtos.ServicePort;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * This class is to test the REST interface exposed by OzoneManager.
 */
public class TestOzoneManagerRestInterface {
    private static MiniOzoneCluster cluster;

    private static OzoneConfiguration conf;

    @Test
    public void testGetServiceList() throws Exception {
        OzoneManagerHttpServer server = TestOzoneManagerRestInterface.cluster.getOzoneManager().getHttpServer();
        HttpClient client = HttpClients.createDefault();
        String connectionUri = "http://" + (NetUtils.getHostPortString(server.getHttpAddress()));
        HttpGet httpGet = new HttpGet((connectionUri + "/serviceList"));
        HttpResponse response = client.execute(httpGet);
        String serviceListJson = EntityUtils.toString(response.getEntity());
        ObjectMapper objectMapper = new ObjectMapper();
        TypeReference<List<ServiceInfo>> serviceInfoReference = new TypeReference<List<ServiceInfo>>() {};
        List<ServiceInfo> serviceInfos = objectMapper.readValue(serviceListJson, serviceInfoReference);
        Map<HddsProtos.NodeType, ServiceInfo> serviceMap = new HashMap<>();
        for (ServiceInfo serviceInfo : serviceInfos) {
            serviceMap.put(serviceInfo.getNodeType(), serviceInfo);
        }
        InetSocketAddress omAddress = OmUtils.getOmAddressForClients(TestOzoneManagerRestInterface.conf);
        ServiceInfo omInfo = serviceMap.get(OM);
        Assert.assertEquals(omAddress.getHostName(), omInfo.getHostname());
        Assert.assertEquals(omAddress.getPort(), omInfo.getPort(RPC));
        Assert.assertEquals(server.getHttpAddress().getPort(), omInfo.getPort(HTTP));
        InetSocketAddress scmAddress = HddsUtils.getScmAddressForClients(TestOzoneManagerRestInterface.conf);
        ServiceInfo scmInfo = serviceMap.get(SCM);
        Assert.assertEquals(scmAddress.getHostName(), scmInfo.getHostname());
        Assert.assertEquals(scmAddress.getPort(), scmInfo.getPort(RPC));
        ServiceInfo datanodeInfo = serviceMap.get(DATANODE);
        DatanodeDetails datanodeDetails = TestOzoneManagerRestInterface.cluster.getHddsDatanodes().get(0).getDatanodeDetails();
        Assert.assertEquals(datanodeDetails.getHostName(), datanodeInfo.getHostname());
        Map<ServicePort.Type, Integer> ports = datanodeInfo.getPorts();
        for (ServicePort.Type type : ports.keySet()) {
            switch (type) {
                case HTTP :
                case HTTPS :
                    Assert.assertEquals(datanodeDetails.getPort(REST).getValue(), ports.get(type));
                    break;
                default :
                    // OM only sends Datanode's info port details
                    // i.e. HTTP or HTTPS
                    // Other ports are not expected as of now.
                    Assert.fail();
                    break;
            }
        }
    }
}

