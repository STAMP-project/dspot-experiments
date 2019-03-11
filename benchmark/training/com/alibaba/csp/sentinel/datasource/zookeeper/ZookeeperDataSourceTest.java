package com.alibaba.csp.sentinel.datasource.zookeeper;


import CreateMode.PERSISTENT;
import ZooDefs.Perms;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.fastjson.JSON;
import java.util.Collections;
import java.util.List;
import org.apache.curator.framework.AuthInfo;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;
import org.junit.Test;


/**
 *
 *
 * @author Eric Zhao
 */
public class ZookeeperDataSourceTest {
    @Test
    public void testZooKeeperDataSource() throws Exception {
        TestingServer server = new TestingServer(21812);
        server.start();
        final String remoteAddress = server.getConnectString();
        final String path = "/sentinel-zk-ds-demo/flow-HK";
        ReadableDataSource<String, List<FlowRule>> flowRuleDataSource = new ZookeeperDataSource<List<FlowRule>>(remoteAddress, path, new com.alibaba.csp.sentinel.datasource.Converter<String, List<FlowRule>>() {
            @Override
            public List<FlowRule> convert(String source) {
                return JSON.parseObject(source, new com.alibaba.fastjson.TypeReference<List<FlowRule>>() {});
            }
        });
        FlowRuleManager.register2Property(flowRuleDataSource.getProperty());
        CuratorFramework zkClient = CuratorFrameworkFactory.newClient(remoteAddress, new ExponentialBackoffRetry(3, 1000));
        zkClient.start();
        Stat stat = zkClient.checkExists().forPath(path);
        if (stat == null) {
            zkClient.create().creatingParentContainersIfNeeded().withMode(PERSISTENT).forPath(path, null);
        }
        final String resourceName = "HK";
        publishThenTestFor(zkClient, path, resourceName, 10);
        publishThenTestFor(zkClient, path, resourceName, 15);
        zkClient.close();
        server.stop();
    }

    @Test
    public void testZooKeeperDataSourceAuthorization() throws Exception {
        TestingServer server = new TestingServer(21812);
        server.start();
        final String remoteAddress = server.getConnectString();
        final String groupId = "sentinel-zk-ds-demo";
        final String dataId = "flow-HK";
        final String path = (("/" + groupId) + "/") + dataId;
        final String scheme = "digest";
        final String auth = "root:123456";
        AuthInfo authInfo = new AuthInfo(scheme, auth.getBytes());
        List<AuthInfo> authInfoList = Collections.singletonList(authInfo);
        CuratorFramework zkClient = CuratorFrameworkFactory.builder().connectString(remoteAddress).retryPolicy(new ExponentialBackoffRetry(3, 100)).authorization(authInfoList).build();
        zkClient.start();
        Stat stat = zkClient.checkExists().forPath(path);
        if (stat == null) {
            ACL acl = new ACL(Perms.ALL, new org.apache.zookeeper.data.Id(scheme, DigestAuthenticationProvider.generateDigest(auth)));
            zkClient.create().creatingParentContainersIfNeeded().withACL(Collections.singletonList(acl)).forPath(path, null);
        }
        ReadableDataSource<String, List<FlowRule>> flowRuleDataSource = new ZookeeperDataSource<List<FlowRule>>(remoteAddress, authInfoList, groupId, dataId, new com.alibaba.csp.sentinel.datasource.Converter<String, List<FlowRule>>() {
            @Override
            public List<FlowRule> convert(String source) {
                return JSON.parseObject(source, new com.alibaba.fastjson.TypeReference<List<FlowRule>>() {});
            }
        });
        FlowRuleManager.register2Property(flowRuleDataSource.getProperty());
        final String resourceName = "HK";
        publishThenTestFor(zkClient, path, resourceName, 10);
        publishThenTestFor(zkClient, path, resourceName, 15);
        zkClient.close();
        server.stop();
    }
}

