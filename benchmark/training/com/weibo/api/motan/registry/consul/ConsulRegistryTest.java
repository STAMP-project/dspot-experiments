package com.weibo.api.motan.registry.consul;


import com.weibo.api.motan.registry.support.command.CommandListener;
import com.weibo.api.motan.registry.support.command.ServiceListener;
import com.weibo.api.motan.rpc.URL;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;


public class ConsulRegistryTest {
    private MockConsulClient client;

    private ConsulRegistry registry;

    private URL registerUrl;

    private URL serviceUrl;

    private URL serviceUrl2;

    private URL clientUrl;

    private URL clientUrl2;

    private String serviceid;

    private String serviceid2;

    private long interval = 1000;

    private long sleepTime;

    @Test
    public void doRegisterAndAvailable() throws Exception {
        // register
        registry.doRegister(serviceUrl);
        registry.doRegister(serviceUrl2);
        Assert.assertTrue(client.isRegistered(serviceid));
        Assert.assertFalse(client.isWorking(serviceid));
        Assert.assertTrue(client.isRegistered(serviceid2));
        Assert.assertFalse(client.isWorking(serviceid2));
        // available
        registry.doAvailable(null);
        Thread.sleep(sleepTime);
        Assert.assertTrue(client.isWorking(serviceid));
        Assert.assertTrue(client.isWorking(serviceid2));
        // unavailable
        registry.doUnavailable(null);
        Thread.sleep(sleepTime);
        Assert.assertFalse(client.isWorking(serviceid));
        Assert.assertFalse(client.isWorking(serviceid2));
        // unregister
        registry.doUnregister(serviceUrl);
        Assert.assertFalse(client.isRegistered(serviceid));
        Assert.assertTrue(client.isRegistered(serviceid2));
        registry.doUnregister(serviceUrl2);
        Assert.assertFalse(client.isRegistered(serviceid2));
    }

    @Test
    public void subAndUnsubService() throws Exception {
        ServiceListener serviceListener = createNewServiceListener(serviceUrl);
        ServiceListener serviceListener2 = createNewServiceListener(serviceUrl);
        registry.subscribeService(clientUrl, serviceListener);
        registry.subscribeService(clientUrl2, serviceListener2);
        Assert.assertTrue(containsServiceListener(serviceUrl, clientUrl, serviceListener));
        Assert.assertTrue(containsServiceListener(serviceUrl, clientUrl2, serviceListener2));
        registry.doRegister(serviceUrl);
        registry.doRegister(serviceUrl2);
        registry.doAvailable(null);
        Thread.sleep(sleepTime);
        registry.unsubscribeService(clientUrl, serviceListener);
        Assert.assertFalse(containsServiceListener(serviceUrl, clientUrl, serviceListener));
        Assert.assertTrue(containsServiceListener(serviceUrl, clientUrl2, serviceListener2));
        registry.unsubscribeService(clientUrl2, serviceListener2);
        Assert.assertFalse(containsServiceListener(serviceUrl, clientUrl2, serviceListener2));
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
        registry.subscribeCommand(clientUrl, commandListener);
        Assert.assertTrue(containsCommandListener(serviceUrl, clientUrl, commandListener));
        client.setKVValue(clientUrl.getGroup(), command);
        Thread.sleep(2000);
        client.removeKVValue(clientUrl.getGroup());
        registry.unsubscribeCommand(clientUrl, commandListener);
        Assert.assertFalse(containsCommandListener(serviceUrl, clientUrl, commandListener));
    }

    @Test
    public void discoverService() throws Exception {
        registry.doRegister(serviceUrl);
        List<URL> urls = registry.discoverService(serviceUrl);
        Assert.assertFalse(urls.contains(serviceUrl));
        registry.doAvailable(null);
        Thread.sleep(sleepTime);
        urls = registry.discoverService(serviceUrl);
        Assert.assertTrue(urls.contains(serviceUrl));
    }

    @Test
    public void discoverCommand() throws Exception {
        String result = registry.discoverCommand(clientUrl);
        Assert.assertTrue(result.equals(""));
        String command = "{\"index\":0,\"mergeGroups\":[\"aaa:1\",\"bbb:1\"],\"pattern\":\"*\",\"routeRules\":[]}\n";
        client.setKVValue(clientUrl.getGroup(), command);
        result = registry.discoverCommand(clientUrl);
        Assert.assertTrue(result.equals(command));
    }
}

