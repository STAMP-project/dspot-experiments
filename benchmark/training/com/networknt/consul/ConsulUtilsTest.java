/**
 * Copyright (c) 2016 Network New Technologies Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.networknt.consul;


import com.networknt.registry.URL;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author zhanglei28
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

