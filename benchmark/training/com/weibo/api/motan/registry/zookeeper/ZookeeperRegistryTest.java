package com.weibo.api.motan.registry.zookeeper;


import ZkNodeType.AVAILABLE_SERVER;
import ZkNodeType.UNAVAILABLE_SERVER;
import com.weibo.api.motan.registry.support.command.CommandListener;
import com.weibo.api.motan.registry.support.command.ServiceListener;
import com.weibo.api.motan.rpc.URL;
import java.util.List;
import junit.framework.Assert;
import org.I0Itec.zkclient.ZkClient;
import org.junit.Test;


public class ZookeeperRegistryTest {
    private static ZookeeperRegistry registry;

    private static URL serviceUrl;

    private static URL clientUrl;

    private static EmbeddedZookeeper zookeeper;

    private static ZkClient zkClient;

    private static String service = "com.weibo.motan.demoService";

    @Test
    public void subAndUnsubService() throws Exception {
        ServiceListener serviceListener = new ServiceListener() {
            @Override
            public void notifyService(URL refUrl, URL registryUrl, List<URL> urls) {
                if (!(urls.isEmpty())) {
                    Assert.assertTrue(urls.contains(ZookeeperRegistryTest.serviceUrl));
                }
            }
        };
        ZookeeperRegistryTest.registry.subscribeService(ZookeeperRegistryTest.clientUrl, serviceListener);
        Assert.assertTrue(containsServiceListener(ZookeeperRegistryTest.clientUrl, serviceListener));
        ZookeeperRegistryTest.registry.doRegister(ZookeeperRegistryTest.serviceUrl);
        ZookeeperRegistryTest.registry.doAvailable(ZookeeperRegistryTest.serviceUrl);
        Thread.sleep(2000);
        ZookeeperRegistryTest.registry.unsubscribeService(ZookeeperRegistryTest.clientUrl, serviceListener);
        Assert.assertFalse(containsServiceListener(ZookeeperRegistryTest.clientUrl, serviceListener));
    }

    @Test
    public void subAndUnsubCommand() throws Exception {
        final String command = "{\"index\":0,\"mergeGroups\":[\"aaa:1\",\"bbb:1\"],\"pattern\":\"*\",\"routeRules\":[]}\n";
        CommandListener commandListener = new CommandListener() {
            @Override
            public void notifyCommand(URL refUrl, String commandString) {
                if (commandString != null) {
                    Assert.assertTrue(commandString.equals(command));
                }
            }
        };
        ZookeeperRegistryTest.registry.subscribeCommand(ZookeeperRegistryTest.clientUrl, commandListener);
        Assert.assertTrue(containsCommandListener(ZookeeperRegistryTest.clientUrl, commandListener));
        String commandPath = ZkUtils.toCommandPath(ZookeeperRegistryTest.clientUrl);
        if (!(ZookeeperRegistryTest.zkClient.exists(commandPath))) {
            ZookeeperRegistryTest.zkClient.createPersistent(commandPath, true);
        }
        ZookeeperRegistryTest.zkClient.writeData(commandPath, command);
        Thread.sleep(2000);
        ZookeeperRegistryTest.zkClient.delete(commandPath);
        ZookeeperRegistryTest.registry.unsubscribeCommand(ZookeeperRegistryTest.clientUrl, commandListener);
        Assert.assertFalse(containsCommandListener(ZookeeperRegistryTest.clientUrl, commandListener));
    }

    @Test
    public void discoverService() throws Exception {
        ZookeeperRegistryTest.registry.doRegister(ZookeeperRegistryTest.serviceUrl);
        List<URL> results = ZookeeperRegistryTest.registry.discoverService(ZookeeperRegistryTest.clientUrl);
        Assert.assertTrue(results.isEmpty());
        ZookeeperRegistryTest.registry.doAvailable(ZookeeperRegistryTest.serviceUrl);
        results = ZookeeperRegistryTest.registry.discoverService(ZookeeperRegistryTest.clientUrl);
        Assert.assertTrue(results.contains(ZookeeperRegistryTest.serviceUrl));
    }

    @Test
    public void discoverCommand() throws Exception {
        String result = ZookeeperRegistryTest.registry.discoverCommand(ZookeeperRegistryTest.clientUrl);
        Assert.assertTrue(result.equals(""));
        String command = "{\"index\":0,\"mergeGroups\":[\"aaa:1\",\"bbb:1\"],\"pattern\":\"*\",\"routeRules\":[]}\n";
        String commandPath = ZkUtils.toCommandPath(ZookeeperRegistryTest.clientUrl);
        if (!(ZookeeperRegistryTest.zkClient.exists(commandPath))) {
            ZookeeperRegistryTest.zkClient.createPersistent(commandPath, true);
        }
        ZookeeperRegistryTest.zkClient.writeData(commandPath, command);
        result = ZookeeperRegistryTest.registry.discoverCommand(ZookeeperRegistryTest.clientUrl);
        Assert.assertTrue(result.equals(command));
    }

    @Test
    public void doRegisterAndAvailable() throws Exception {
        String node = ZookeeperRegistryTest.serviceUrl.getServerPortStr();
        List<String> available;
        List<String> unavailable;
        String unavailablePath = ZkUtils.toNodeTypePath(ZookeeperRegistryTest.serviceUrl, UNAVAILABLE_SERVER);
        String availablePath = ZkUtils.toNodeTypePath(ZookeeperRegistryTest.serviceUrl, AVAILABLE_SERVER);
        ZookeeperRegistryTest.registry.doRegister(ZookeeperRegistryTest.serviceUrl);
        unavailable = ZookeeperRegistryTest.zkClient.getChildren(unavailablePath);
        Assert.assertTrue(unavailable.contains(node));
        ZookeeperRegistryTest.registry.doAvailable(ZookeeperRegistryTest.serviceUrl);
        unavailable = ZookeeperRegistryTest.zkClient.getChildren(unavailablePath);
        Assert.assertFalse(unavailable.contains(node));
        available = ZookeeperRegistryTest.zkClient.getChildren(availablePath);
        Assert.assertTrue(available.contains(node));
        ZookeeperRegistryTest.registry.doUnavailable(ZookeeperRegistryTest.serviceUrl);
        unavailable = ZookeeperRegistryTest.zkClient.getChildren(unavailablePath);
        Assert.assertTrue(unavailable.contains(node));
        available = ZookeeperRegistryTest.zkClient.getChildren(availablePath);
        Assert.assertFalse(available.contains(node));
        ZookeeperRegistryTest.registry.doUnregister(ZookeeperRegistryTest.serviceUrl);
        unavailable = ZookeeperRegistryTest.zkClient.getChildren(unavailablePath);
        Assert.assertFalse(unavailable.contains(node));
        available = ZookeeperRegistryTest.zkClient.getChildren(availablePath);
        Assert.assertFalse(available.contains(node));
    }
}

