package com.weibo.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.weibo.service.RegistryService;
import java.util.ArrayList;
import java.util.List;
import org.I0Itec.zkclient.ZkClient;
import org.junit.Assert;
import org.junit.Test;


public class ZkRegistryServiceTest {
    private RegistryService registryService;

    private EmbeddedZookeeper embeddedZookeeper;

    private ZkClient zkClient;

    private String group = "default_rpc";

    private String service1 = "com.weibo.motan.demoService";

    private String service2 = "com.weibo.motan.demoService2";

    @Test
    public void getGroups() throws Exception {
        List<String> groups = registryService.getGroups();
        Assert.assertTrue(((groups.size()) == 1));
        Assert.assertTrue(groups.contains(group));
    }

    @Test
    public void getServicesByGroup() throws Exception {
        List<String> services = registryService.getServicesByGroup(group);
        Assert.assertTrue(((services.size()) == 2));
        Assert.assertTrue(services.contains(service1));
        Assert.assertTrue(services.contains(service2));
    }

    @Test
    public void getNodes() throws Exception {
        List<JSONObject> nodes = registryService.getNodes(group, service1, "unavailableServer");
        Assert.assertTrue(((nodes.size()) == 2));
        List<String> addresses = new ArrayList<String>();
        for (JSONObject node : nodes) {
            addresses.add(((String) (node.get("host"))));
        }
        Assert.assertTrue(addresses.contains("127.0.0.1:8001"));
        Assert.assertTrue(addresses.contains("127.0.0.1:8002"));
    }

    @Test
    public void getAllNodes() throws Exception {
        List<JSONObject> nodes = registryService.getAllNodes(group);
        Assert.assertTrue(((nodes.size()) == 2));
    }
}

