package com.weibo.api.motan.registry.consul;


import com.weibo.api.motan.rpc.URL;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author zhanglei28
 * @unknown ConsulUtilsTest
 * @unknown 2016?3?22?
 */
public class ConsulUtilsTest {
    String testGroup;

    String testPath;

    String testHost;

    String testProtocol;

    int testPort;

    URL url;

    String testServiceName;

    String testServiceId;

    String testServiceTag;

    @Test
    public void testConvertGroupToServiceName() {
        String tempServiceName = ConsulUtils.convertGroupToServiceName(testGroup);
        Assert.assertTrue(testServiceName.equals(tempServiceName));
    }

    @Test
    public void testGetGroupFromServiceName() {
        String tempGroup = ConsulUtils.getGroupFromServiceName(testServiceName);
        Assert.assertEquals(testGroup, tempGroup);
    }

    @Test
    public void testConvertConsulSerivceId() {
        String tempServiceId = ConsulUtils.convertConsulSerivceId(url);
        Assert.assertEquals(testServiceId, tempServiceId);
    }

    @Test
    public void testGetPathFromServiceId() {
        String tempPath = ConsulUtils.getPathFromServiceId(testServiceId);
        Assert.assertEquals(testPath, tempPath);
    }
}

