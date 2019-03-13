package com.vip.saturn.job.console.springboot.test;


import MediaType.APPLICATION_FORM_URLENCODED;
import com.alibaba.fastjson.JSONObject;
import com.vip.saturn.job.console.AbstractSaturnConsoleTest;
import com.vip.saturn.job.console.controller.rest.AlarmRestApiController;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


@RunWith(SpringRunner.class)
@WebMvcTest(AlarmRestApiController.class)
public class RegistryCenterControllerTest extends AbstractSaturnConsoleTest {
    @Autowired
    private MockMvc mvc;

    @Test
    public void testCreateAndUpdateZkClusterInfo() throws Exception {
        String clusterName = "/clusterx";
        // craete a new zkCluster
        RegistryCenterControllerTest.ZkClusterInfoForTest zkClusterInfo = new RegistryCenterControllerTest.ZkClusterInfoForTest(clusterName, "alias1", "127.0.0.1:12345", "A??");
        MvcResult result = mvc.perform(post("/console/zkClusters").contentType(APPLICATION_FORM_URLENCODED).content(zkClusterInfo.toContent())).andExpect(status().isOk()).andReturn();
        String responseBody = result.getResponse().getContentAsString();
        Map<String, Object> resultMap = JSONObject.parseObject(responseBody, Map.class);
        Assert.assertEquals(0, resultMap.get("status"));
        // refresh
        mvc.perform(get("/console/registryCenter/refresh")).andExpect(status().isOk()).andReturn();
        // get and compare
        int size;
        int count = 0;
        List<Map<String, String>> objValue;
        do {
            Thread.sleep(3000L);
            result = mvc.perform(get("/console/zkClusters")).andExpect(status().isOk()).andReturn();
            responseBody = result.getResponse().getContentAsString();
            resultMap = JSONObject.parseObject(responseBody, Map.class);
            objValue = ((List<Map<String, String>>) (resultMap.get("obj")));
            size = objValue.size();
        } while ((size == 1) && ((count++) < 10) );
        Assert.assertEquals(2, size);
        String connectionString = "";
        String description = "";
        for (Map<String, String> clusterInfo : objValue) {
            String clusterKey = clusterInfo.get("zkClusterKey");
            if (clusterKey.equals(clusterName)) {
                connectionString = clusterInfo.get("zkAddr");
                description = clusterInfo.get("description");
                break;
            }
        }
        Assert.assertEquals("127.0.0.1:12345", connectionString);
        Assert.assertEquals("A??", description);
        // get ??zkcluster
        result = mvc.perform(get(("/console/zkClusters?zkClusterKey=" + clusterName))).andExpect(status().isOk()).andReturn();
        responseBody = result.getResponse().getContentAsString();
        resultMap = JSONObject.parseObject(responseBody, Map.class);
        Map<String, Object> zkClusterMap = ((Map<String, Object>) (resultMap.get("obj")));
        Assert.assertEquals(clusterName, zkClusterMap.get("zkClusterKey"));
        Assert.assertEquals("127.0.0.1:12345", zkClusterMap.get("zkAddr"));
        Assert.assertEquals("A??", zkClusterMap.get("description"));
        Assert.assertTrue(((Boolean) (zkClusterMap.get("offline"))));
    }

    private static class ZkClusterInfoForTest {
        private String zkClusterKey;

        private String alias;

        private String connectString;

        private String description;

        public ZkClusterInfoForTest(String zkClusterKey, String alias, String connectString, String description) {
            this.zkClusterKey = zkClusterKey;
            this.alias = alias;
            this.connectString = connectString;
            this.description = description;
        }

        public String toContent() {
            return String.format("zkClusterKey=%s&alias=%s&connectString=%s&description=%s", zkClusterKey, alias, connectString, description);
        }

        public String getZkClusterKey() {
            return zkClusterKey;
        }

        public void setZkClusterKey(String zkClusterKey) {
            this.zkClusterKey = zkClusterKey;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public String getConnectString() {
            return connectString;
        }

        public void setConnectString(String connectString) {
            this.connectString = connectString;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}

